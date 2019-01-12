#!/bin/bash

if [ $# -lt 3 ]; then 
	echo "Insufficient # of parameters supplied."
	exit 1
else
	if [[ "$1" =~ ^(([1-9]?[0-9]|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))\.){3}([1-9]?[0-9]|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))$ ]]; then
		echo "Valid Consortium IP Address"
	else 
		echo "$(tput setaf 1)Invalid Consortium IP Address supplied."
		exit 1
	fi
	
	if [[ "$2" =~ ^(([1-9]?[0-9]|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))\.){3}([1-9]?[0-9]|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))$ ]]; then
		echo "Valid WebSocket IP Address"
	else 
		echo "$(tput setaf 1)Invalid WebSocket IP Address supplied."
		exit 1
	fi
fi

CONSORTIUM_IP=$1
WEBSOCKET_IP=$2
DATABASE_PW=$3
RPC_PORT=${4:-8540}
WEBSOCKET_PORT=${5:-8547}

# Erlang VM & Elixir Install
wget https://packages.erlang-solutions.com/erlang-solutions_1.0_all.deb && sudo dpkg -i erlang-solutions_1.0_all.deb
sudo apt-get update
sudo apt-get -y install esl-erlang
sudo apt-get -y install elixir

# gcc & make Install
sudo apt-get install make
sudo apt-get -y install gcc

# nginx Install
sudo apt-get -y install nginx && sudo ufw allow 'Nginx HTTP'

# Node.js & NPM Install
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
sudo apt-get -y install nodejs
sudo ln -s /usr/bin/nodejs /usr/bin/node 
sudo apt-get -y install npm

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
sudo git checkout 869425df && echo "checked out"; cd -
sudo chmod -R a+x blockscout && echo "permissions granted"

# Setup WebSocket Client
cd blockscout/apps/ethereum_jsonrpc/lib/ethereum_jsonrpc/web_socket
sudo sed -i 's/wss:/ws:/g' web_socket_client.ex
sudo sed -i 's/ssl_verify: :verify_peer,/#ssl_verify: :verify_peer,/g' web_socket_client.ex
sudo sed -i 's/cacerts: :certifi.cacerts(),/#cacerts: :certifi.cacerts(),/g' web_socket_client.ex
sudo sed -i 's/depth: 99,/#depth: 99,/g' web_socket_client.ex
sudo sed -i 's/server_name_indication: host_charlist,/#server_name_indication: host_charlist,/g' web_socket_client.ex
sudo sed -i 's/verify_fun/#verify_fun/g' web_socket_client.ex
sudo sed -i 's/%URI/#%URI/g' web_socket_client.ex
sudo sed -i 's/host_charlist =/#host_charlist =/g' web_socket_client.ex
cd -

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
      url: \"http://$CONSORTIUM_IP:$RPC_PORT\",
      method_to_url: [
        eth_getBalance: \"http://$CONSORTIUM_IP:$RPC_PORT\",
        trace_replayTransaction: \"http://$CONSORTIUM_IP:$RPC_PORT\"
      ],
      http_options: [recv_timeout: 60_000, timeout: 60_000, hackney: [pool: :ethereum_jsonrpc]]
    ],
    variant: EthereumJSONRPC.Parity
  ],
  subscribe_named_arguments: [
    transport: EthereumJSONRPC.WebSocket,
    transport_options: [
      web_socket: EthereumJSONRPC.WebSocket.WebSocketClient,
      url: \"wss://$WEBSOCKET_IP:$WEBSOCKET_PORT\"
    ],
    variant: EthereumJSONRPC.Parity
 ]" | sudo tee parity.exs 
cd -

cd apps/explorer/config
echo "
use Mix.Config

config :explorer, Explorer.Repo,
  adapter: Ecto.Adapters.Postgres,
  username: \"postgres\",
  password: \"$DATABASE_PW\",
  database: \"explorer_test\",
  hostname: \"localhost\",
  port: \"5432\",
  #url: \"postgres:localhost:5432/explorer_test\",
  pool_size: String.to_integer(System.get_env(\"POOL_SIZE\") || \"10\"),
  #ssl: String.equivalent?(System.get_env(\"ECTO_USE_SSL\") || \"true\", \"true\"),
  prepare: :unnamed,
  timeout: 60_000

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
  block_interval: 2_000,
  json_rpc_named_arguments: [
    transport: EthereumJSONRPC.HTTP,
    transport_options: [
      http: EthereumJSONRPC.HTTP.HTTPoison,
      url: \"http://$CONSORTIUM_IP:$RPC_PORT\",
      method_to_url: [
        eth_getBalance: \"http://$CONSORTIUM_IP:$RPC_PORT\",
        trace_replayTransaction: \"http://$CONSORTIUM_IP:$RPC_PORT\"
      ],
      http_options: [recv_timeout: 60_000, timeout: 60_000, hackney: [pool: :ethereum_jsonrpc]]
    ],
    variant: EthereumJSONRPC.Parity
  ],
  subscribe_named_arguments: [
    transport: EthereumJSONRPC.WebSocket,
    transport_options: [
      web_socket: EthereumJSONRPC.WebSocket.WebSocketClient,
      url: \"ws://$WEBSOCKET_IP:$WEBSOCKET_PORT\"
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
sudo MIX_ENV=prod mix do ecto.drop --no-compile --force
sudo MIX_ENV=prod mix ecto.create && sudo MIX_ENV=prod mix ecto.migrate && echo "migrated DB"

# Install NPM Dependencies
cd apps/block_scout_web/assets && sudo npm install --unsafe-perm; cd -
cd apps/explorer && sudo npm install; cd -

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
	ExecStart=/usr/local/bin/mix phx.server

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