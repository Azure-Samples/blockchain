# Corda Network
[Index](Index.md)

## What is Corda Network?

The network concept in Corda is sometimes a little hard to define. On docs.corda.net 

_"A Corda network consists of a number of machines running nodes. 
These nodes communicate using persistent protocols in order to create and validate transactions."_

Technically true, but probably better is to think about the purpose of Corda:

_Corda is a platform for enabling multiple organisations to form consensus about shared facts 
(e.g. agreements) that exist between them and maintain this consensus as those facts evolve over time, 
delivering for the enterprise the blockchain promise,
"I know that I what you see is what I see" - WYSIWIS._

And to make this work at scale the "network" effect common in widely used software platforms is needed:

_To achieve this vision, nodes need to be able to find each other, communicate securely, utilise the 
services of third-party data providers such as data 'oracles' and ensure transactions are 
correctly ordered. Our vision is of a global 'internet' of Corda nodes, able to do just that.
 The Corda network consists of nodes operated by or on behalf of any legal entity anywhere in the world(*)_ 
* _an openly-governed and transparently operated identity service known as a 'doorman'_
* _a directory service known as the network map_
* _one or more 'notary clusters' that provide transaction ordering services_
* _zero or more Corda nodes that provide 'oracle' services to other participants in the network_
  
(*) subject to sanctions/legal restrictions if any

## A standalone development network 

For developers a completely standalone network can be built, and the 
purpose of the this service is to easily build and run these.

These are a little worlds of imaginary organisations collaborating through CorDapps, 
that can be stood up and down at the whim of the developer. But as they have untrusted certificates, 
they are unable to collaborate beyond their boundaries. 

## The Corda Testnet 

_"The Corda Testnet is an open public network of Corda nodes on the internet. 
It is designed to be a complement to the Corda Network where any entity can transact real 
world value with any other counterparty in the context of any application. 
The Corda Testnet is designed for “non-production” use in a genuine global context of Corda nodes"_

See [here](https://docs.corda.net/head/corda-testnet-intro.html) for more.

The intention is that this service will also be able to drive the provisioning of nodes and CorDapps 
on the Corda Testnet via a consistent REST API.

## The Corda Network

This is an initiative sponsored by R3 to establish a global "internet" of Corda nodes and CorDapps, 
and a Foundation to provide the necessary technical and governance processes. 
It will be independent of R3, who will simply have representatives on the governing Board. 
