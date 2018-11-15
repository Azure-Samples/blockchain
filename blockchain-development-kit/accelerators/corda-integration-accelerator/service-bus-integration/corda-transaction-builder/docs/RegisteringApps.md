# Registering Apps 
[Index](Index.md)

_Work in Progress - this only relevant for full integration with Azure 
Blockchain Workbench._ 

The current rules are quite simple:

* the CorDapp jar file (and any dependencies) must be on the classpath. There are 
some prebuilt examples in `src/test/resources/cordapps`, anything else will for 
now need a manual modification of either `build.gradle` or the `java -jar ...` command. 
* the CorDapp must include an additional config file, see below.

## Config File

This must be at:
 
 <pre>src/main/resources/META-INF/services/net.corda.workbench.Registry.json</pre>
 
within the cordapp source folder. *Note that 'net.corda.workbench.Registry.json' is 
the file name, the rest are directories (not always so clear within an IDE).* 

An example file is:

```json
{
  "id" : "19D3B4FA-FBB1-4FB3-9435-A9B32D9C4486",
  "name" : "Refrigerated Transportation",
  "summary" : "Azure Workbench Refrigerated Transportation Example in Corda",
  "slug" : "refrigeration",
  "version" : "1.0",
  "authors" : ["ian.morgan@r3"],
  "url" : "https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation",
  "scannablePackages" : ["net.corda.workbench.refrigeratedTransportation"]
}
```

**id**, **name** and **scannablePackages** are mandatory, the rest are optional.

Flows will be available if: 

* they have the '@InitiatingFlow' and '@StartableByRPC' annotations 
* they are in one of the scannablePackages

The unqualified flow name (i.e. without package name) must be unique 
within the list of scannablePackages.




