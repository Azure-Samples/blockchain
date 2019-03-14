# Corda Transaction Builder
[Index](Index.md)

_Work in Progress - this section will be expanded with more detail later_

## Overview 

This component is invoked by messages received over the service bus and uses 
Java [reflections](../../corda-reflections/docs/Index.md) over the CorDapp jars 
to build Corda RPC calls.

Very simplistically, its purpose is to convert an API triggered by an interaction within 
Workbench to an RPC call that triggers the appropriate flow on the correct node. To achieve this, 
the component needs to read meta-data held by workbench and match this to the parameters required by 
the RPC call, and also communicate with the network map service to locate the correct Corda node.
 