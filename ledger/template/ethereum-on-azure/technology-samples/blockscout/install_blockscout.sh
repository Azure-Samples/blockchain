#!/bin/bash

if [ $# -lt 3 ]; then
	echo "Insufficient # of parameters supplied."
	exit 1
else
	rpcRegex='(https?)://[-A-Za-z0-9\+&@#/%?=~_|!:,.;]*[-A-Za-z0-9\+&@#/%=~_|]'
	if [[ "$1" =~ $rpcRegex ]]; then
		echo "Valid Consortium IP Address"
	else
		echo "$(tput setaf 1)Invalid Consortium IP Address supplied."
		exit 1
	fi

	wsRegex='(wss|ws?)://[-A-Za-z0-9\+&@#/%?=~_|!:,.;]*[-A-Za-z0-9\+&@#/%=~_|]'
	if [[ "$2" =~ $wsRegex ]]; then
		echo "Valid WebSocket IP Address"
	else
		echo "$(tput setaf 1)Invalid WebSocket IP Address supplied."
		exit 1
	fi
fi

RPC_ENDPOINT=$1
WEBSOCKET_ENDPOINT=$2
DATABASE_PW=$3

# Erlang VM & Elixir Install
wget https://packages.erlang-solutions.com/erlang-solutions_1.0_all.deb && sudo dpkg -i erlang-solutions_1.0_all.deb
sudo apt-get update
sudo apt-get -y install esl-erlang=1:21.1.1-1
sudo apt-get -y install elixir=1.8.1-2

# gcc, make and build-essential Install
sudo apt-get install build-essential make automake libtool inotify-tools autoconf libgmpv4-dev gcc


# nginx Install
sudo apt-get -y install nginx && sudo ufw allow 'Nginx HTTP'

# Node.js & NPM Install
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
sudo apt-get -y install nodejs
sudo ln -s /usr/bin/nodejs /usr/bin/node

# PostgreSQL Install
echo 'deb http://apt.postgresql.org/pub/repos/apt/ xenial-pgdg main' sudo tee -a /etc/apt/sources.list.d/pgdg.list
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update && sudo apt-get -y install postgresql
sudo apt-get update && sudo apt-get -y install postgresql-client

# PostgreSQL Configuration
sudo -u postgres psql -U postgres -d postgres -c "alter user postgres with password '$DATABASE_PW';"

# Git Install & Clone
sudo apt-get install -y git
sudo git clone https://github.com/poanetwork/blockscout.git && echo "cloned"
cd blockscout
sudo git checkout v1.3.9-beta && echo "checked out"; cd -
sudo chmod -R a+x blockscout && echo "permissions granted"


# Install Mix Dependencies
cd blockscout
sudo MIX_ENV=prod mix local.hex --force && echo "hex installed"
sudo MIX_ENV=prod mix deps.update libsecp256k1
sudo MIX_ENV=prod mix do deps.get, local.rebar --force, deps.compile, compile && echo "mix deps installed"

# Update Explorer Configuration Files
cd apps/explorer/config/prod
echo "
use Mix.Config

config :explorer,
  json_rpc_named_arguments: [
    transport: EthereumJSONRPC.HTTP,
    transport_options: [
      http: EthereumJSONRPC.HTTP.HTTPoison,
      url: \"$RPC_ENDPOINT\",
      method_to_url: [
        eth_call: \"$RPC_ENDPOINT\",
        eth_getBalance: \"$RPC_ENDPOINT\",
        trace_replayTransaction: \"$RPC_ENDPOINT\"
      ],
      http_options: [recv_timeout: :timer.minutes(1), timeout: :timer.minutes(1), hackney: [pool: :ethereum_jsonrpc]]
    ],
    variant: EthereumJSONRPC.Parity
  ],
  subscribe_named_arguments: [
    transport: EthereumJSONRPC.WebSocket,
    transport_options: [
      web_socket: EthereumJSONRPC.WebSocket.WebSocketClient,
      url: \"$WEBSOCKET_ENDPOINT\"
    ],
    variant: EthereumJSONRPC.Parity
 ]" | sudo tee parity.exs
cd -

cd apps/explorer/config
echo "
use Mix.Config

config :explorer, Explorer.Repo,
  username: \"postgres\",
  password: \"$DATABASE_PW\",
  database: \"explorer_test\",
  hostname: \"localhost\",
  port: \"5432\",
  #url: \"postgres:localhost:5432/explorer_test\",
  pool_size: String.to_integer(System.get_env(\"POOL_SIZE\") || \"10\"),
  #ssl: String.equivalent?(System.get_env(\"ECTO_USE_SSL\") || \"true\", \"true\"),
  prepare: :unnamed,
  timeout: :timer.seconds(60)

variant =
  if is_nil(System.get_env(\"ETHEREUM_JSONRPC_VARIANT\")) do
    \"parity\"
  else
    System.get_env(\"ETHEREUM_JSONRPC_VARIANT\")
    |> String.split(\".\")
    |> List.last()
    |> String.downcase()
  end" | sudo tee prod.exs
cd -

# Update Indexer Configuration Files
cd apps/indexer/config/prod

echo "
use Mix.Config

config :indexer,
  block_interval: :timer.seconds(5),
  json_rpc_named_arguments: [
    transport: EthereumJSONRPC.HTTP,
    transport_options: [
      http: EthereumJSONRPC.HTTP.HTTPoison,
      url: \"$RPC_ENDPOINT\",
      method_to_url: [
        eth_getBalance: \"$RPC_ENDPOINT\",
        trace_block: \"$RPC_ENDPOINT\",
        trace_replayTransaction: \"$RPC_ENDPOINT\"
      ],
      http_options: [recv_timeout: :timer.minutes(1), timeout: :timer.minutes(1), hackney: [pool: :ethereum_jsonrpc]]
    ],
    variant: EthereumJSONRPC.Parity
  ],
  subscribe_named_arguments: [
    transport: EthereumJSONRPC.WebSocket,
    transport_options: [
      web_socket: EthereumJSONRPC.WebSocket.WebSocketClient,
      url: \"$WEBSOCKET_ENDPOINT\"
    ]
  ]
" | sudo tee parity.exs
cd -

# Update Blockscout Web Configuration Files
cd apps/block_scout_web/config
echo "
use Mix.Config

config :block_scout_web, BlockScoutWeb.Endpoint,
  force_ssl: false,
  check_origin: false,
  http: [port: 4000],
  url: [
    scheme: \"http\",
    port: \"4000\",
	host: \"*.azure.com\"
  ]" | sudo tee prod.exs
cd -

# Drop Old DB (If Exists) && Create + Migrate DB
sudo MIX_ENV=prod mix do ecto.drop --no-compile --force, ecto.create --no-compile, ecto.migrate --no-compile && echo "migrated DB"

# Install NPM Dependencies
cd apps/block_scout_web/assets && sudo npm install --unsafe-perm; cd -
cd apps/explorer && sudo npm install --unsafe-perm; cd -

# NPM Deploy
cd apps/block_scout_web/assets && sudo npm run-script deploy; cd -

# Create systemd Service File
cd ../../../etc/systemd/system
echo "
	[Unit]
	Description=Blockscout Web App

	[Service]
	Type=simple
	User=$USER
	Group=$USER
	Restart=on-failure
	Environment=MIX_ENV=prod
	Environment=LANG=en_US.UTF-8
	WorkingDirectory=/home/$USER/blockscout
	ExecStart=/usr/bin/mix phx.server

	[Install]
	WantedBy=multi-user.target
" | sudo tee blockscout.service
cd -

# Edit nginx Configuration File
cd ../../../etc/nginx
echo "
	events {
		worker_connections  1024;
	}

	http {
		server {
		    listen 80;
			server_name \"\";

			location / {
				proxy_pass http://localhost:4000;
				proxy_http_version 1.1;
				proxy_redirect off;
				proxy_set_header X-Real-IP \$remote_addr;
				proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
				proxy_set_header Host \$host;
				proxy_set_header Upgrade \$http_upgrade;
				proxy_set_header Connection \"upgrade\";
			}
		}
	}
" | sudo tee nginx.conf
cd -
sudo service nginx reload

# Start systemd Service
sudo systemctl daemon-reload
sudo systemctl start blockscout.service