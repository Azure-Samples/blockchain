
## Initial Steps 

Make sure the "Initial Setup" steps in [Quick Start](../../service-bus-integration/docs/QuickStart.md) 
have been complete 

All terminal commands should be **run from this folder**. 

## Clear exist state 

This is optional, but for a clean run:

```bash
# check we have'nt got some orphaned processs - if so kill
ps -ef | grep -i Notary
ps -ef | grep -i ContosoLtd
ps -ef | grep -i WorldWideImporters
ps -ef | grep -i NorthwindTraders
ps -ef | grep -i WoodgroveBank
ps -ef | grep -i Device01
ps -ef | grep -i Device02
ps -ef | grep -i TasmanianTraders


# Clean out all file storage 
rm -r ~/.corda-local-network
rm -r ~/.corda-transaction-builder
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
curl -X POST -H "Content-Type: application/json" http://corda-local-network:1114/1/nodes/create \
--data '["O=Notary,L=London,C=GB","O=ContosoLtd,L=Seatle,C=US","O=WorldWideImporters,L=Memphhsis,C=US","O=NorthwindTraders,L=Copenhagen,C=DK","O=WoodgroveBank,L=london,C=GB","O=Device01,L=london,C=GB","O=Device02,L=Shanghai,C=CN","O=TasmanianTraders,L=Bentonville,C=US"]'

# deploy the app
curl -X POST  http://corda-local-network:1114/1/apps/refrigerated-transportation/deploy \
 --data-binary  @../../cordapps/refrigerated-transportation/lib/refrigerated-transportation.jar 

# start the network 
curl -X POST  http://corda-local-network:1114/1/start 
```

## Check the nodes (optional)

If you want to test 

```bash
# all nodes
curl http://corda-local-network:1114/1/nodes

# status for a node 
curl http://corda-local-network:1114/1/nodes/ContosoLtd/status

# connect to a node (password = test)
ssh -p 10014 localhost -l user1 -o UserKnownHostsFile=/dev/null 

```

Congratulations! You now have a running network of Corda Nodes emulating 8 real
world nodes. 

Having problems? Check the logs at ~/.corda-local-network/1/<nodename>_node/logs 
Starting 8 Corda nodes locally is quite resource intensive. You might just be running 
low on memory or file handles, or possibly the ports are clashing with an existing process.


## Setup the corda-transaction-builder

The following create a running corda-transaction-builder with the  Refrigerated Transport example
deployed. It needs access to the same app that is deployed to the network 
to build Corda RPC calls.

```bash
# deploy the app
curl -X POST  http://corda-transaction-builder:1112/1/apps/refrigerated-transportation/deploy \
 --data-binary  @../../cordapps/refrigerated-transportation/lib/refrigerated-transportation.jar 

# start an "agent" that will communicate with the network
curl -X POST  http://corda-transaction-builder:1112/1/start
```

## Check the agent (optional)

```bash
# is the agent running (note check the 'corda-transaction-builder' console as it 
# may have allocated an alternative port)
curl http://corda-transaction-builder:10200/ping
```

## run a query using the corda-transaction-builder

```bash
curl http://corda-transaction-builder:1112/1/ContosoLtd/refrigerated-transportation/query/Shipment
```

This will return an empty array as there is no data

## create some test data 

```bash
# submit txn
curl -X POST -H "Content-Type: application/json"  http://corda-transaction-builder:1112/1/ContosoLtd/refrigerated-transportation/flows/CreateFlow/run --data \
 '{"state" : {"owner" : "ContosoLtd", "device" : "Device01", "supplyChainOwner" : "WorldWideImporters","supplyChainObserver" : "WoodgroveBank", "minHumidity" : 20, "maxHumidity" : 50, "minTemperature" : -10,"maxTemperature" : 0 }}'

# see it using the query 
curl http://corda-transaction-builder:1112/1/ContosoLtd/refrigerated-transportation/query/Shipment
``` 

Congratulations! You now created a new transaction!

## Configure the 'service-bus-listener' 

Update the config with your service bus connection and queue names. For simplicity 
just edit the [reference config](../../service-bus-listener/src/main/resources/reference.conf), but
please follow the proper rules documented in the file for server deploys. Update the config with you service bus connection 
and queue names. For simplicity just edit the reference config, but please 
follow the proper rules documented in the file for server deploys.

## Start the 'service-bus-listener' 

Run the command below in new terminal windows and leave in the background.

```bash
../../service-bus-integration/service-bus-listener/runDev.sh 
```

## Generate test messages 

Run the command below in new terminal windows and leave in the background. This 
runs a simple monitor on the egress message queue

```bash
../../service-bus-integration/service-bus-listener/dummyEgressClient.sh
```

To generate some test messages, in yet another terminal window run the command below


```bash
../../service-bus-integration/service-bus-listener/generateTestData.sh
```

There console output should be similar to that below 

```bash
17LDN-MAC55-3:refrigerated-transport ianmorgan$ ../../service-bus-integration/service-bus-listener/generateTestData.sh
Running 'corda-local-network' in /Users/ianmorgan/corda/azure-workbench-2/service-bus-integration/service-bus-listener

BUILD SUCCESSFUL in 0s
4 actionable tasks: 4 up-to-date
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Sender connected to queue ingress using endpoint Endpoint=sb://iantest.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=7eXlKWeW6INnbP6WHl5iNRwj/J4c999AUcoQwzwIjr0=
Sending messages for refrigeratedTransportation/happyPath dataset with linearId of 6fcd4e25-fb0c-4ada-b40e-f0ba738b1c3f
   Sending 01-create.json....done
   Sending 02-telemetry.json....done
   Sending 03-transfer.json....done
   Sending 04-complete.json....done
   Completed dataset/n
Sending messages for refrigeratedTransportation/outOfCompliance dataset with linearId of 5d3064f5-3351-4855-80ba-2ea7ca122333
   Sending 01-create.json....done
   Sending 02-transfer.json....done
   Sending 03-telemetry1.json....done
   Sending 04-telemetry2.json....done
   Completed dataset/n
All done, closing connection to queue
```

The new transactions can be queried with the following URL

http://corda-transaction-builder:1112/1/ContosoLtd/refrigerated-transportation/query/Shipment

And the reply messages will have been read by the 'dummyEgressClient'

Congratulations!. You now have everything running 'End to End'. The messages are 
in the format expected by Azure Blockchain Workbench, so any component that can generate
or consume these message will now communicate with Corda.  


You can also generate test messages using any suitable Azure Service Bus client. Some
examples are [here](../../service-bus-integration/service-bus-listener/src/test/resources/datasets).

