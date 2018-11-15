#Corda Local Network 
[Index](Index.md)


## Overview 

This is a tool to fire up a number of Corda nodes quickly inside a single VM. The 
configuration is passed from Workbench and can be as simple as a list of node names, e.g. "Alice",
 "Bob", "Charlie", "Notary". 

It builds a standalone Corda network suitable for development and testing but not 
for production. See [The Corda Network](CordaNetwork.md) for a  fuller description of the differences.

Internally the service uses the 
[Network Bootstrapper Tool](https://github.com/corda/corda/tree/master/tools/network-bootstrapper) to 
build and manage nodes. 

External interaction is via REST-style API.

Future enhancements may include integration with 
[The Corda Testnet](https://docs.corda.net/head/corda-testnet-intro.html) and deployments 
to multiple VM (for bigger networks).

## Next Steps 

See [Quick Start](QuickStart.md) to get up and running and [API Docs](API.md) details 
the REST interface. [Docker](Docker.md) describes how to build a run docker container.
