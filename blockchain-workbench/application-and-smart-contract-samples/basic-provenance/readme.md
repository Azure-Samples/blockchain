Basic Provenance Sample Application for Azure Blockchain Workbench
==============================

Overview 
---------

The Basic Provenance application expresses a workflow for a simple record of
ownership or responsibility.  The state transition diagram below shows the
interactions among the states in this workflow. 

Application Roles 
------------------

| Name                   | Description                                       |
|------------------------|---------------------------------------------------|
| InitiatingCounterParty | The first participant in the supply chain.        |
| Counterparty           | A party to whom responsibility for a product has been assigned. For example, a shipper |
| Owner                  | The organization that owns the product being transported. For example, a manufacturer |
| Observer               | The individual or organization monitoring the supply chain. For example, a government agency |

 

States 
-------

| Name                   | Description                                       |
|------------------------|---------------------------------------------------|
|Created |Indicates that the contract has initiated and tracking is in progress. |
|InTransit |Indicates that a Counterparty currently is in possession and responsible for goods being transported.|
|Completed |Indicates the product has reached it's intended destination.|

Workflow Details
----------------

![state diagram of the workflow](media/c3d3c6764f6ae1e565c0929d2f2fed48.png)

An instance of the Basic Provenance application's workflow starts in the Created
state when an owner wants to begin a process for tracking ownership or
responsibility.  An owner is also the InitiatingCounterParty since the owner
initiates the process for tracking the ownership or responsibility.  The state
changes to InTransit whenever a new counterparty that can take on the
responsibility is identified.  The owner in the InitiatingCounterParty role
calls a function to transfer responsibility by specifying a counterparty.  Upon
reaching the InTransit state, the counterparty can transfer the responsibility
to another counterparty or the owner can decide to complete the transfers of
responsibility and call the Complete function to reach the Completed state. 

The happy path shown in the transition diagram traces the owner transferring
responsibility to a counterparty once and then completing the workflow. 

Application Files
-----------------
[BasicProvenance.json](./ethereum/BasicProvenance.json)

[BasicProvenance.sol](./ethereum/BasicProvenance.sol)
