# Quick Start 
[Index](Index.md)

This sets a network to run the [refrigerated transport](../../../cordapps/refrigerated-transportation/README.md)
example. A prebuilt CorDapp is included here, so there is no need to build this project. 

## Building 

These assume a working Java dev environment with at a minimum Java 8. 

```bash
./gradlew clean jar -x test
```

This will result in Java JAR file with the app. To run this on the default port of 1114.

```bash
java -jar build/libs/corda-local-network.jar 
```

## Local DNS 

All services are configured to communicate using known host names (which match 
the service name). Under unix, set the following in <code>/etc/hosts</code>:

<pre>
127.0.0.1       corda-local-network
</pre>


## Deploying a CorDapp

### 1. Create a network 

The command below creates a new network for all the parties. It might take a minute or two to run. Note that 
the "1" is the network name - multiple networks with different logical names can be created, though the 
current implementation only one can running at a time. The "1" is taken in order to match the "connectionId"
value in Azure Workbench messages, which currently defaults to 1.

```bash
curl -X POST -H "Content-Type: application/json" http://corda-local-network:1114/1/nodes/create \
--data '["O=Notary,L=London,C=GB","O=ContosoLtd,L=Seatle,C=US","O=WorldWideImporters,L=Memphhsis,C=US","O=NorthwindTraders,L=Copenhagen,C=DK","O=WoodgroveBank,L=london,C=GB","O=Device01,L=london,C=GB","O=Device02,L=Shanghai,C=CN","O=TasmanianTraders,L=Bentonville,C=US"]'
```

You can list the nodes with:

```bash
curl http://corda-local-network:1114/1/nodes
```

Or get more info on a particular node with 

```bash
curl http://corda-local-network:1114/1/nodes/ContosoLtd/config
```


### 2. Deploy the CorDapp

The command below uploads the CorDapp to all nodes. 

```bash
curl -X POST  http://corda-local-network:1114/1/apps/refrigerated-transportation/deploy \
 --data-binary  @../../cordapps/refrigerated-transportation/lib/refrigerated-transportation.jar 
```

### 3. Run the network

Simply call the start command. 

```bash
curl -X POST  http://corda-local-network:1114/1/start 
```

The nodes will actually startup in the background and may take a few minutes. A simple way of testing 
is see if the port is accepting requests (the port is obtained by 'config' endpoint above)

```bash
telnet corda-local-network 10014 
```

You can also check the status endpoint, e.g. 

```bash
curl http://corda-local-network:1114/1/nodes/ContosoLtd/status
```

### 4. Call your workflow 

As by default this process doesn't deploy a webapp, the only communication is via RPC. Annoyingly this makes 
it hard to do or see anything without some kind of client app running such as Azure Workbench or
 the [corda-transaction-builder](../../corda-transaction-builder/docs/Index.md).  However it is 
possible to view and run flows via the ssh console. 

To connect to the ContosoLtd node (password is 'test')

```bash
ssh -p 10014 corda-local-network -l user1 
```

As certs are regenerated, ssh might detect a key mismatch and abort the login, 
in which case one option is below (_don't try this on prod_) 

```bash
ssh -p 10014 localhost -l user1 -o UserKnownHostsFile=/dev/null 
```

Once in the console 'flow list' will show all available flows. Output will be similar to below

```bash
Wed Oct 10 16:19:08 BST 2018>>> flow list
net.corda.core.flows.ContractUpgradeFlow$Authorise
net.corda.core.flows.ContractUpgradeFlow$Deauthorise
net.corda.core.flows.ContractUpgradeFlow$Initiate
net.corda.workbench.refrigeratedTransportation.Initiator
net.corda.workbench.refrigeratedTransportation.flow.CompleteFlow
net.corda.workbench.refrigeratedTransportation.flow.CreateFlow
net.corda.workbench.refrigeratedTransportation.flow.CreateFlowViaWorkbench
net.corda.workbench.refrigeratedTransportation.flow.IngestTelemetryFlow
net.corda.workbench.refrigeratedTransportation.flow.PingFlow
net.corda.workbench.refrigeratedTransportation.flow.TransferResponsibilityFlow
```

And 'flow start' runs a flow.  

```bash
Wed Oct 10 16:43:57 BST 2018>>> flow start PingFlow

✓ Running
✓ Done

```

### 5. Shutdown 

Stop the network with 

```bash
curl -X POST  http://corda-local-network:1114/1/stop 
``` 
    
Delete it with 

```bash
curl -X POST  http://corda-local-network:1114/1/delete 
```     

### 6. Troubleshooting 

It's a bit limited, but to see what Java processes are running 

```bash
curl http://corda-local-network:1114/processes
```

To see what task have been run (_each command will result in one or more internal tasks_)

```bash
curl http://corda-local-network:1114/1/tasks/history
```

By default data will be at `~/.corda-local-network/1/`

There is a directory for each node with logs in the standard locations for a Corda Node 
