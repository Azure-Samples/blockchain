param (
  [Parameter(Position=0)]
  [string]$url
)

$env:APP_SVC_URL="$url"

echo "migrating to $env:APP_SVC_URL"

npm install

node_modules/.bin/truffle compile
node_modules/.bin/truffle migrate --network=consortiumTest
