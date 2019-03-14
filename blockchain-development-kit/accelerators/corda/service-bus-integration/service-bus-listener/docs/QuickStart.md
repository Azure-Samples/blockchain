# Quick Start 
[Index](Index.md)

Work in progress. Assume all other services are running,
 `/etc/hosts` updated (if running locally) and service bus queues created.

## Edit the config 

Update the config with your service bus connection and queue names. For simplicity 
just edit the [reference config](../src/main/resources/reference.conf), but
please follow the proper rules documented in the file for server deployments.

## Start the service 

```bash
./gradlew run
```

## Send some test message

Beforehand, make sure that both [corda-local-network](../../corda-local-network/docs/QuickStart.md)
and [corda-transaction-builder](../../corda-transaction-builder/docs/QuickStart.md) are correctly
set up and running.

```bash
# monitor egress queue
./dummyEgressClient.sh

# Run this in an other terminal to generate test data
 ./generateTestData.sh

# Test query - should now show freshly created shipments
curl http://$cordatransactionbuilder:1112/1/ContosoLtd/query/shipment
```

