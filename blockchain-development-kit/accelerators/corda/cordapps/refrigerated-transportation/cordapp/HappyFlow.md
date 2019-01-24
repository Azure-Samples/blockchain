# Happy Path 

## Basic shipment flow via REST API

The following scripts a basic happy path using REST APIs. Note that for simplicity parties 
are referred to by their organisation name. Copy and paste the commands below  to a unix bash console.

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


# Create a shipment for Contoso Ltd. Note the minHumidity etc params can be skipped, in which case defaults will be applied 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$id --data \
 '{ "device" : "Device01", "supplyChainOwner" : "WorldWideImporters", "supplyChainObserver":"WoodgroveBank", "minHumidity": 10, "maxHumidity":99, "minTemperature":0, "maxTemperature":99 }'

 
# See it in their vault.
curl $ContosoLtd/api/transportation/shipments
curl $ContosoLtd/api/transportation/shipments/$id
curl $ContosoLtd/api/transportation/shipments/$id/state



# Record a telemtery reading 
curl -X PUT -H "Content-Type: application/json" $Device01/api/transportation/shipments/$id/telemetry --data \
 '{ "humidity" : 50, "temperature" : 10 }'
 

# Transfer ownership 
curl -X PUT -H "Content-Type: application/json" $ContosoLtd/api/transportation/shipments/$id/transfer --data \
 '{ "newCounterParty" : "NorthwindTraders"  }'
 

# Complete shipment  
curl -X PUT  $WorldWideImporters/api/transportation/shipments/$id/complete 


# Observer has everything 
curl $WoodgroveBank/api/transportation/shipments/$id/states
curl $WoodgroveBank/api/transportation/shipments/$id/states/summary 

# All parties have the correct count of states
for party in $ContosoLtd $WorldWideImporters $NorthwindTraders $Device01 $Device02 $TasmanianTraders $WoodgroveBank ; do curl $party/api/transportation/shipments/$id/states/count ; done

```
