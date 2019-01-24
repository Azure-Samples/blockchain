# 'refrigerated-transportation' CorDapp

## Overview

This is a CorDapp implementation of the Azure Blockchain Workbench
 [refrigerated-transportation](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation),
 example
 
## Building locally 

Other project reference this CorDapp in test cases. To update them with changes 

```bash
./buildLocal.sh
```

You will also need to check <code>../jars/refrigerated-transportation.jar</code> 
into Git  
 
## Quick start 

```bash
./gradlew clean deployNodes
build/nodes/runnodes
```

This may take a few minutes In total 17 processes will be started (each organisation 
apart from the Notary has both a Corda Node and Web Server for the demo REST API).

This will create the following nodes and a Notary. 

A demo REST API is a available at:

* ContosoLtd : localhost:10007
* WorldWideImporters : localhost:10010
* NorthwindTraders : localhost:10013
* WoodgroveBank : localhost:10016
* Device01 : localhost:10019
* Device02 : localhost:10022
* TasmanianTraders : localhost:10025


These services can be run via the REST API. See 

* [Happy](HappyFlow.md) - basic end to end flow
* [Out Of Compliance](OutOfComplianceFlow.md) - sensor records a non compliant value
* [Flow Validation](FlowValidations.md) - check rules for calling flows based on state & role

See also the [Design Notes](DesignNotes.md).

You may see messages similar to those below in the console - they can be ignored 

<pre>
java.lang.NullPointerException
	at co.paralleluniverse.fibers.instrument.MethodDatabase$ClassEntry.equals(MethodDatabase.java:557)
	at co.paralleluniverse.fibers.instrument.MethodDatabase.recordSuspendableMethods(MethodDatabase.java:265)
	at co.paralleluniverse.fibers.instrument.MethodDatabase.checkClass(MethodDatabase.java:327)
	at co.paralleluniverse.fibers.instrument.MethodDatabase.getOrLoadClassEntry(MethodDatabase.java:183)
	at co.paralleluniverse.fibers.instrument.SimpleSuspendableClassifier.isSuspendable(SimpleSuspendableClassifier.java:156)
</pre>
