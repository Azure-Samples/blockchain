
## Initial Steps 

Make sure the "Initial Setup" steps in [Quick Start](../../service-bus-integration/docs/QuickStart.md) 
have been complete

All terminal commands should be **run from this folder**. 

## Clear exist state 

This is optional, but for a clean run:

```bash
# check we have'nt got some orphaned process - if so kill
ps -ef | grep -i Notary
ps -ef | grep -i Alice
ps -ef | grep -i Bob


# Clean out all file storage 
rm -r ~/.corda-local-network
rm -r ~/.corda-transaction-builder
```

## Set environment variables 

Update these as necessary if connecting to a remote server 

```bash
cordalocalnetwork=http://corda-local-network:1114
cordatxnbuilder=http://corda-local-network:1112
```

## Start the Services 

Run the commands below in new terminal windows and leave in the background. They 
should start quite quickly 

```bash
../../service-bus-integration/corda-local-network/runDev.sh 
../../service-bus-integration/corda-transaction-builder/runDev.sh 
```

There should now be services running, this can be checked with the "ping" endpoint

```bash
curl http://corda-local-network:1114/ping
curl http://corda-transaction-builder:1112/ping
```

## Setup a local-corda-network

The following steps create a running network with the Refrigerated Transport example
deployed. They will probably take a few minutes to complete

```bash
# create the network
curl -X POST -H "Content-Type: application/json" $cordalocalnetwork/alicebob/nodes/create \
--data '["O=Notary,L=London,C=GB","O=Alice,L=Paris,C=FR","O=Bob,L=New York,C=US"]'

# deploy the app
curl -X POST  $cordalocalnetwork/alicebob/apps/chat/deploy \
 --data-binary  @../../cordapps/jars/chat.jar 

# start the network 
curl -X POST  $cordalocalnetwork/alicebob/start 
```

## Check the nodes (optional)

If you want to test 

```bash
# all nodes
curl $cordalocalnetwork/alicebob/nodes

# status for a node 
curl $cordalocalnetwork/alicebob/nodes/Alice/status

# connect to a node (password = test)
ssh -p 10014 localhost -l user1 -o UserKnownHostsFile=/dev/null 

```

Having problems? Check the logs at ~/.corda-local-network/alicebob/<nodename>_node/logs 
Starting several Corda nodes locally is quite resource intensive. You might just be running 
low on memory or file handles, or possibly the ports are clashing with an existing process.


## Setup the corda-transaction-builder

The following create a running corda-transaction-builder with the  Chat example
deployed. It needs access to the same app that is deployed to the network 
to build Corda RPC calls.

```bash
# deploy the app
curl -X POST  $cordatxnbuilder/alicebob/apps/chat/deploy \
 --data-binary  @../../cordapps/jars/chat.jar 

# start an "agent" that will communicate with the network
curl -X POST  $cordatxnbuilder/alicebob/start
```

## Check the agent (optional)

```bash
# is the agent running (note check the 'corda-transaction-builder' console as it 
# may have allocated an alternative port). 
curl http://corda-transaction-builder:10200/ping
```

## run a query using the corda-transaction-builder

```bash
curl $cordatxnbuilder/alicebob/Alice/chat/query/Message
```

This will return an empty array as there is no data.

## create some test data 

*Make sure you give each new chat a unique linearId*

```bash
# submit txn
curl -X POST -H "Content-Type: application/json" $cordatxnbuilder/alicebob/alice/chat/flows/StartChatFlow/run --data \
 '{"otherParty" : "bob", "linearId" : "abc_99D098C6-767A-46C5-9E2C-57FF4CF61722"}'

# see it using the query 
curl $cordatxnbuilder/alicebob/Alice/chat/query/Message
curl $cordatxnbuilder/alicebob/Bob/chat/query/Message
``` 

Congratulations! You now created a new transaction. You can update with the command below (remember to update 
the linearId to match above)

curl -X POST -H "Content-Type: application/json" $cordatxnbuilder/alicebob/bob/chat/flows/ChatFlow/run --data \
 '{"message" : "whats up?", "otherParty" : "alice", "linearId" : "abc_99D098C6-767A-46C5-9E2C-57FF4CF61722"}'
