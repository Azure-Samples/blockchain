# Quick Start 
[Index](Index.md)

Still in early stages of development, but the following should work

### 1.

Follow the [Quick Start](../corda-local-network/docs/QuickStart.md) guide 
in 'corda-local-network' to get a local dev network running with the 
'refrigerated transport' example.

Ensure that a local DNS entry is setup to resolve to this service, e.g. under unix
add the following to <code>/etc/hosts</code>:

<pre>
127.0.0.1       corda-local-network
127.0.0.1       corda-transaction-builder
</pre>

### 2.

Start with

```bash
./gradlew run
```

or with a packaged jar 

```bash
./gradlew clean jar
java -jar build/libs/corda-transaction-builder.jar
```

### 3. Deploy the CorDapp

This app needs access to the CorDapp jar files, as its needs the app's Java classes to construct 
the CordaRPC calls.

For now the process is copy of that used to deploy to the corda-local-network.
 
 
```bash
curl -X POST  http://corda-transaction-builder:1112/1/apps/refrigeration/deploy \
 --data-binary  @../../cordapps/refrigerated-transportation/lib/refrigerated-transportation.jar 
```

An "agent" must be run for this network. 

```bash
curl -X POST  http://corda-transaction-builder:1112/1/start
```

### 4. 

Query with (can use any node name)

```bash
curl http://corda-transaction-builder:1112/1/ContosoLtd/query/shipment
```

### 5.

Find the meta data, which describes the params needed

```bash
curl http://localhost:1112/1/ContosoLtd/refrigeration/flows/NewShipmentFlow/metadata
```

This will return something like that below

```json
{
  "newShipment": {
    "owner": {
      "type": "Party",
      "optional": false,
      "nullable": false
    },
    "maxTemperature": {
      "type": "Int",
      "optional": false,
      "nullable": false
    },
    "minTemperature": {
      "type": "Int",
      "optional": false,
      "nullable": false
    },
    "maxHumidity": {
      "type": "Int",
      "optional": false,
      "nullable": false
    },
    "supplyChainObserver": {
      "type": "Party",
      "optional": false,
      "nullable": false
    },
    "device": {
      "type": "Party",
      "optional": false,
      "nullable": false
    },
    "supplyChainOwner": {
      "type": "Party",
      "optional": false,
      "nullable": false
    },
    "minHumidity": {
      "type": "Int",
      "optional": false,
      "nullable": false
    },
    "linearId": {
      "type": "UniqueIdentifier",
      "optional": true,
      "nullable": false
    }
  }
}
```

See [corda-reflections](../../corda-reflections/docs/Index.md) for more details as to 
how this metadata is extracted

### 6.

Create a new shipment. 

Now we can make a call to run the flow. The data supplied must match the metadata extracted 
in the previous step.

                                                                                                                 
```bash
curl -X POST -H "Content-Type: application/json"  http://localhost:1112/1/ContosoLtd/refrigeration/flows/NewShipmentFlow/run --data \
 '{"newShipment" : {"owner" : "ContosoLtd", "device" : "Device01", "supplyChainOwner" : "WorldWideImporters","supplyChainObserver" : "WoodgroveBank", "minHumidity" : 20, "maxHumidity" : 50, "minTemperature" : -10,"maxTemperature" : 0 }}'
``` 

Or, specifying our own linearId (**make sure to change this on each run!**)

```bash
curl -X POST -H "Content-Type: application/json"  http://localhost:1112/1/ContosoLtd/refrigeration/flows/NewShipmentFlow/run --data \
 '{"state" : {"linearId":"abc_22D87099-24DE-4A85-8498-A15CC811C2B4" , "owner" : "ContosoLtd", "device" : "Device01", "supplyChainOwner" : "WorldWideImporters","supplyChainObserver" : "WoodgroveBank", "minHumidity" : 11, "maxHumidity" : 12, "minTemperature" : -10,"maxTemperature" : 0 }}'
``` 
 
 Run the query in step 3 and the new shipment should be in the list 
