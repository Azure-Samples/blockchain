# Happy Path 

## Test flow validations via REST API

The following scripts test some of the validations necessary when invalid transitions 
are attempted, it is not an exhaustive list. Note that for simplicity parties 
are referred to by their organisation name. Copy and paste the commands below 
 to a unix bash console.

```bash 

# Aliases for servers to make scripts more readable
ContosoLtd=localhost:10007
WorldWideImporters=localhost:10010
NorthwindTraders=localhost:10013
WoodgroveBank=localhost:10016
Device01=localhost:10019
Device02=localhost:10022
TasmanianTraders=localhost:10025

# Create some random IDs

idCreated=$(uuidgen | cut -c 1-6)
idInTransit=$(uuidgen | cut -c 1-6)
idCompleted=$(uuidgen | cut -c 1-6)
idOutOfCompliance=$(uuidgen | cut -c 1-6)


# Created state  
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idCreated --data \
 '{ "device" : "Device02", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank" }'

# InTransit state
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idInTransit --data \
 '{ "device" : "Device01", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank" }'
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idInTransit/transfer --data \
 '{ "newCounterParty" : "NorthwindTraders"  }'
 
# Completed state
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idCompleted --data \
  '{ "device" : "Device02", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank" }'
curl -X PUT -H "Content-Type: application/json" $TasmanianTraders/api/transportation/shipments/$idCompleted/transfer --data \
  '{ "newCounterParty" : "NorthwindTraders"  }'
curl -X PUT  $WorldWideImporters/api/transportation/shipments/$idCompleted/complete 

# OutOfCompliance state 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idOutOfCompliance --data \
 '{ "device" : "Device02", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank" }'
curl -X PUT -H "Content-Type: application/json" $Device02/api/transportation/shipments/$idOutOfCompliance/telemetry --data \
  '{ "humidity" : 100, "temperature" : 40 }'

# Print what we have
curl $ContosoLtd/api/transportation/shipments


#############################################################################
# InTransit transition tests                                                #
#############################################################################

# Fails as CompleteState
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idCompleted/transfer --data \
 '{ "newCounterParty" : "TasmanianTraders"  }'
 
# Fails as OutOfCompliance
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idOutOfCompliance/transfer --data \
 '{ "newCounterParty" : "TasmanianTraders"  }' 
 
# Fails as same counter party 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idInTransit/transfer --data \
  '{ "newCounterParty" : "NorthwindTraders"  }' 
  
# Fails as not the initiating counter party 
curl -X PUT -H "Content-Type: application/json" $WorldWideImporters/api/transportation/shipments/$idInTransit/transfer --data \
  '{ "newCounterParty" : "NorthwindTraders"  }'   
  

#############################################################################
# IngestTelementry tests                                                #
#############################################################################

# Fails as OutOfCompliance  
curl -X PUT -H "Content-Type: application/json" $Device02/api/transportation/shipments/$idOutOfCompliance/telemetry --data \
  '{ "humidity" : 50, "temperature" : 10 }' 
  
# Fails as Completed  
curl -X PUT -H "Content-Type: application/json" $Device02/api/transportation/shipments/$idCompleted/telemetry --data \
  '{ "humidity" : 50, "temperature" : 10 }'   
 ## todo - needs fixing  
 
# Fails as only the device can run this flow
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$idCreated/telemetry --data \
  '{ "humidity" : 50, "temperature" : 10 }'  

  
#############################################################################
# Complete transition tests                                                #
#############################################################################

# Fails as in Created state  
curl -X PUT  $WorldWideImporters/api/transportation/shipments/$idCreated/complete 

# Fails as in Complete state  
curl -X PUT  $WorldWideImporters/api/transportation/shipments/$idCompleted/complete 

# Fails as in OutOfCompliance state  
curl -X PUT  $WorldWideImporters/api/transportation/shipments/$idOutOfCompliance/complete 

# Fails as not the Supply Chain Owner  
curl -X PUT  $ContosoLtd/api/transportation/shipments/$idInTransit/complete  
```
