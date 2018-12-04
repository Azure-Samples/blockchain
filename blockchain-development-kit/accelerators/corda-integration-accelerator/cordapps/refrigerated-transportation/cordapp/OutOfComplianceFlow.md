# Happy Path 


## Out of Compliance flow via REST API

The following scripts a basic `OutOfCompliance` path using REST APIs. Note that for simplicity parties 
are referred to by their organisation name. Copy and paste the commands below to a unix bash console.

_TODO - these need updating, the latest JSON serializers are returning very verbose 
and confusing output_

```bash 

# Aliases for servers to make scripts more readable
ContosoLtd=localhost:10007
WorldWideImporters=localhost:10010
NorthwindTraders=localhost:10013
WoodgroveBank=localhost:10016
Device01=localhost:10019
Device02=localhost:10022
TasmanianTraders=localhost:10025

# Create a random ID
id=$(uuidgen | cut -c 1-6)
echo $id


# Create a shipment for ContosoLtd. Note the minHumidity etc params can be skipped, in which case defaults will be applied 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$id --data \
 '{ "device" : "Device01", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank", "minHumidity": 20, "maxHumidity":90, "minTemperature":5, "maxTemperature":20 }'


# Record a good telemtery reading 
curl -X PUT -H "Content-Type: application/json" $Device01/api/transportation/shipments/$id/telemetry --data \
 '{ "humidity" : 50, "temperature" : 10 }'


# Transfer ownership 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$id/transfer --data \
 '{ "newCounterParty" : "NorthwindTraders"  }'
 

# All good still and InTransit
curl $ContosoLtd/api/transportation/shipments/$id/state

# Record a bad telemtery reading  
curl -X PUT -H "Content-Type: application/json" $Device01/api/transportation/shipments/$id/telemetry --data \
  '{ "humidity" : 80, "temperature" : 25 }'
  
  
# Out of Compliance
curl $ContosoLtd/api/transportation/shipments/$id/state

```
