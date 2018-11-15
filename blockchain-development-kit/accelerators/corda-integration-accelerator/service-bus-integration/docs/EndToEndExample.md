# End to End Example

## Introduction

The basic flow is best explained by working through a simple use case for the 
 [Refrigerated Transportation](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation)
 example.
 
The steps below outline the processing from the sending of message on the 'ingress' queue,
through the integration layers, calling the CorDapp and returning details on the 'egress' queue.
 
### #1 Generate Ingess Queue Message

A message is send to the Ingress Queue to create a new shipment. An example is below.

```json
{
  "requestId": "1104E723-3D75-458E-993A-26C15D5D4646",
  "userChainIdentifier": "ContosoLtd",
  "applicationName": "RefrigeratedTransportation",
  "workflowName": "WorkbenchCreateFlow",
  "parameters": [
    {
      "name": "linearId",
      "value": "4C042253-3E3D-4473-B351-AC209046C5DC"
    },
    {
      "name": "owner",
      "value": "ContosoLtd"
    },
    {
      "name": "device",
      "value": "device01"
    },
    {
      "name": "supplyChainOwner",
      "value": "WorldWideImporters"
    },
    {
      "name": "supplyChainObserver",
      "value": "WoodgroveBank"
    },
    {
      "name": "minHumidity",
      "value": 12
    },
    {
      "name": "maxHumidity",
      "value": 45
    },
    {
      "name": "minTemperature",
      "value": -20
    },
    {
      "name": "maxTemperature",
      "value": -7
    }
  ],
  "connectionId": 1,
  "messageSchemaVersion": "1.0.0",
  "messageName": "CreateContractRequest"
}
``` 

### #2 Service Bus Listener 

The [service-bus-listener](../service-bus-listener/docs/Index.md) receives this message, unpacks it 
and makes a REST call to the [corda-transaction-builder](../corda-transaction-builder/docs/Index.md)

If the structures are ok, a "Submitted" message is returned on the Egress queue 

```json
{
  "messageName": "CreateContractUpdate",
  "additionalInformation": {},
  "contractLedgerIdentifier": "4C042253-3E3D-4473-B351-AC209046C5DC",
  "requestId": "1104E723-3D75-458E-993A-26C15D5D4646",
  "messageSchemaVersion": "1.0.0",
  "contractId": 1,
  "connectionId": 1,
  "status": "Submitted"
}
```

### #3 Corda Transaction Builder 

The [corda-transaction-builder](../corda-transaction-builder/docs/Index.md) is responsible for 
calling the correct Corda Flow on the CorDapp via Corda RPC. By convention the workbench message fields are 
used as below:

* **userChainIdentifier** - identifies the node (organisation) that will issue the transaction
* **applicationName** - is the 'registered' name of the CorDapp (see below)
* **workflowName** - is the flow name (see below)
* **connectionId** - must be 1 for now (see below)

The **parameters** must match the signature of the flow. There are some restrictions here:

* they must match the rules used within the WB metadata layers
* they must be compatible with the rules in [corda-reflections](../corda-reflections/docs/Index.md), which is 
responsible for converting JSON structures into Java API calls.

The basic rules for now are:

* there must be a `linearId` field
* the parameter list must be flat, no nested structures
* restrict to simple Java types in the flow signature except for `Party` & `UniqueIdendifier`, which 
have custom resolvers

### #4 The CorDapp Flow Runs

The flow runs as usual. Note there are currently two restrictions on the flow design:

* the flow cannot be long running (as `corda-transaction-builder` is making a blocking call over HTTP)
* the flow signature must be compatible with the data expected by WB. In practice this means that 
small wrapper flows may be necessary over existing logic, see 
[WB Create Flow](../../cordapps/refrigerated-transportation/cordapp/src/main/kotlin/net/corda/workbench/refrigeratedTransportation/flow/workbench/WorkbenchCreateFlow.kt) 
for an example.

### #5 Reply on Egress Queue

On completion of the call to 'corda-transaction-builder' the 'service-bus-listener' replies with 
two messages (assuming a happy path).

```json
{
  "messageName": "CreateContractUpdate",
  "additionalInformation": {},
  "contractLedgerIdentifier": "4C042253-3E3D-4473-B351-AC209046C5DC",
  "requestId": "1104E723-3D75-458E-993A-26C15D5D4646",
  "messageSchemaVersion": "1.0.0",
  "contractId": 1,
  "connectionId": 1,
  "status": "Submitted"
}
```

and 

```json
{
  "messageName": "ContractMessage",
  "blockId": 999,
  "additionalInformation": {},
  "blockhash": "5182DFEA92D88709E9C00C3F7C8DCF8CAB4B0E11A9DC83D7019AC5F9FC7ED693",
  "contractLedgerIdentifier": "4C042253-3E3D-4473-B351-AC209046C5DC",
  "contractProperties": [
    {
      "name": "state",
      "value": "Created",
      "workflowPropertyId": 1
    },
    {
      "name": "owner",
      "value": "O=ContosoLtd, L=Seatle, C=US",
      "workflowPropertyId": 2
    },
    {
      "name": "initiatingCounterparty",
      "value": "O=ContosoLtd, L=Seatle, C=US",
      "workflowPropertyId": 3
    },
    {
      "name": "counterparty",
      "value": "O=ContosoLtd, L=Seatle, C=US",
      "workflowPropertyId": 4
    },
    {
      "name": "previousCounterparty",
      "workflowPropertyId": 5
    },
    {
      "name": "device",
      "value": "O=Device01, L=london, C=GB",
      "workflowPropertyId": 6
    },
    {
      "name": "supplyChainOwner",
      "value": "O=WorldWideImporters, L=Memphhsis, C=US",
      "workflowPropertyId": 7
    },
    {
      "name": "supplyChainObserver",
      "value": "O=WoodgroveBank, L=london, C=GB",
      "workflowPropertyId": 8
    },
    {
      "name": "minHumidity",
      "value": 12,
      "workflowPropertyId": 9
    },
    {
      "name": "maxHumidity",
      "value": 45,
      "workflowPropertyId": 10
    },
    {
      "name": "minTemperature",
      "value": -20,
      "workflowPropertyId": 11
    },
    {
      "name": "maxTemperature",
      "value": -7,
      "workflowPropertyId": 12
    },
    {
      "name": "complianceSensorType",
      "workflowPropertyId": 13
    },
    {
      "name": "complianceSensorReading",
      "workflowPropertyId": 14
    },
    {
      "name": "complianceStatus",
      "value": true,
      "workflowPropertyId": 15
    },
    {
      "name": "complianceDetail",
      "workflowPropertyId": 16
    },
    {
      "name": "lastSensorUpdateTimestamp",
      "workflowPropertyId": 17
    }
  ],
  "modifyingTransactions": [{
    "from": "O=Device01, L=london, C=GB",
    "to": [
      "O=ContosoLtd, L=Seatle, C=US",
      "O=WorldWideImporters, L=Memphhsis, C=US"
    ],
    "transactionId": 999,
    "transactionHash": "5182DFEA92D88709E9C00C3F7C8DCF8CAB4B0E11A9DC83D7019AC5F9FC7ED693"
  }],
  "messageSchemaVersion": "1.0.0",
  "contractId": 1,
  "connectionId": 1,
  "isNewContract": true
}
```
