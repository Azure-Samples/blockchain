# Components 
[Index](Index.md)

![Components](images/Workbench%20Integration%20-%20Components.png)

This lists the components available. Note that the current design assumes 
deployment **is limited to dev / test / demo style environments.**

## Service Bus Listener 

Listens for WB messages on the "ingress" queue on Azure Service Bus, extract 
message content and call Corda integrations services. Currently
these are blocking calls using REST over HTTP. Replies with results on the "egress"
queue. 

The basic integration pattern is shown below 
![CordaAndWorkbench](images/CordaIntegrationtoMicrosoftAzureServices%20.pdf)

 

## Corda Hub 

[Docs](../corda-hub/docs/Index.md)

_This component is still in development_

Provides a thin abstraction layer that isolates other components from 
the actual topology and management of a Corda network. A full implementation
will include:
* creating new standalone test networks on Azure
* deploying cordapps
* connecting to existing networks
* storing of RPC credentials (TBC) 


## Corda Local Network 

[Docs](../corda-local-network/docs/Index.md)

Essentially a prebuilt image that
* has the latest stable open source release of Corda (currently 3.2) 
* a network map service 
* an Agent that can be called from Workbench (probably using REST style protocols) to:
     * upload new networks definitions (basically the list of nodes) and restart all nodes
     * deploy new CorDaApps across the nodes
     * basic network wide admin (stop/start/status)
     
We plan to use the [Network Bootstrapper Tool](https://github.com/corda/corda/tree/master/tools/network-bootstrapper) for the initial implementation. 

Node state will be stored in local databases (i.e. will be lost after full restarts). As WB observers 
state and keeps it own store this is unlikely to be a problem:

Native cloud storage could be added later but does include additional complexity:
* currently only Postgres is supported on Open Source version 
* additional steps in Azure templates to create the databases
* the potential need to run migrations when upgrading workflows 
* proving access to these stores to developers

## Corda DLT Watcher 

_To discuss with Marc - the more I think about this, the more 
I think its better to make this part of the flow logic_ 

An observer node that will forward the committed transaction stream (i.e. 
those that have been Notarised) to the WB service bus.

I propose that we simply build a custom Notary for this purpose (it would be part of the "Azure Corda Network" 
config). This means that any flow will automatically be forwarded on completion.

_presumably there is a standard WB message format to use here?_ 

## Corda Transaction Builder 

[Docs](../corda-transaction-builder/docs/Index.md)

This component will integrate with WB to build a standard Corda RPC call to start the flow and wait for its completion 
or rejection. Internally this will use the Corda Java API though integration with WB will presumably 
use a REST style protocol (_to confirm with )

This component will:
* Use the combination of the JSON configuration file & the Java classes in the CorDapp jar to build the
RPC call to the flow. This wil require some agreed upon conventions for representation of common concepts (date,
money ...)
* Resolve the org details for the user invoking the request to the appropriate Corda node
* Start the call and observe any progress / status information the flow may be streaming back (presumably this is 
useful in the UI). 
* Optionally store the result. At a minimum we probably want to store the transaction identified to 
correlate it back to the stream of data from the DLT watcher.

## Workbench Config Builder (Future)

An optional tool for developers that would inspect a CorDapp jar and build a matching workbench 
config file, something like: 

```bash
java -jar workbenchtools.jar buildConfig --cordapp=/path/to/a/cordapp.jar

```

