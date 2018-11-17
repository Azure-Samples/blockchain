export APP_SVC_URL="$1"

echo migrating to "$APP_SVC_URL"

rm -rf node_modules
npm install

npx truffle compile
npx truffle migrate --network=consortiumTest
