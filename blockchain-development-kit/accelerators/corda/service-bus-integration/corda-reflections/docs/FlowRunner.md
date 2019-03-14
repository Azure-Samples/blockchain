# Flow Runner
[Index](Index.md)

The Flow Runner package encapulates the logic necessary to invoke a Corda Flow via reflections. 

## RpcCaller

This interface exposes the underlying Corda RPC operations. An implementation is injected into the 
FlowRunner. 

## FlowRunner 

Encapsulates the logic for making a call to a Corda RPC flow via reflections. To use
this class it is important that all classes to be loaded via reflections are **in the 
default classpath** The Corda RPC libraries will not load correctly if invoked
 from a custom class loader (this problem may have been fixed in Corda4). 
