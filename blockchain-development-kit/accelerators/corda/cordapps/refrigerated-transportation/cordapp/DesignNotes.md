# Design Notes

This is a basic porting of the Ethereum Solidity contract into a CorDapp. By design, it 
is as similar to the original as possible. But as many Blockchain
concepts are implemented differently in Corda, there is some re-architecting needed for a
production ready implementation.

## Organisation vs. Individual Identity

The example uses `Party` to identify users, but `Party` this is really tied to organisation. This will 
probably need to be refactored to use an identity tied to an Active Directory user, with the 
higher level Party reserved for Organisation to Organisation rules (e.g _Northwind_ only see _Contoso_ 
shipments when responsibility is transferred).

## Single State

All state is contained within a single `State` object, "Shipment", which matches the design of the Solidity 
contract. It would be more natural in Corda to break this down into more discrete concepts,
for example:

* the contract parameters (which never change)
* telemetry 
* contract state 

https://docs.corda.net/head/design/reference-states/design.html

## Over signing of transactions 

Making a single state available to all nodes involved in the transactions also means that they need to sign it,
which has some implications that probably are not acceptable in the real world. For instance:
* the Device must sign when there is a Transfer of Responsibility.
* the PreviousCounterParty must continue to sign (as they are participant even though are 
are no longer interested). 


   