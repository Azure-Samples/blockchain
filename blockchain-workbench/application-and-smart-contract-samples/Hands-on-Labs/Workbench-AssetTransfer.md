![](media/d7dee407c9585010579bb48b396b94ef.png)

Terms of Use

© 2017 Microsoft Corporation. All rights reserved.

Information in this document, including URL and other Internet Web site
references, is subject to change without notice. Unless otherwise noted, the
companies, organizations, products, domain names, e-mail addresses, logos,
people, places, and events depicted herein are fictitious, and no association
with any real company, organization, product, domain name, e-mail address, logo,
person, place, or event is intended or should be inferred. Complying with all
applicable copyright laws is the responsibility of the user. Without limiting
the rights under copyright, no part of this document may be reproduced, stored
in or introduced into a retrieval system, or transmitted in any form or by any
means (electronic, mechanical, photocopying, recording, or otherwise), or for
any purpose, without the express written permission of Microsoft Corporation.

For more information, see **Microsoft Copyright Permissions** at
<http://www.microsoft.com/permission>

Microsoft may have patents, patent applications, trademarks, copyrights, or
other intellectual property rights covering subject matter in this document.
Except as expressly provided in any written license agreement from Microsoft,
the furnishing of this document does not give you any license to these patents,
trademarks, copyrights, or other intellectual property.

The Microsoft company name and Microsoft products mentioned herein may be either
registered trademarks or trademarks of Microsoft Corporation in the United
States and/or other countries. The names of actual companies and products
mentioned herein may be the trademarks of their respective owners.

**This document reflects current views and assumptions as of the date of
development and is subject to change. Actual and future results and trends may
differ materially from any forward-looking statements. Microsoft assumes no
responsibility for errors or omissions in the materials.**

**THIS DOCUMENT IS FOR INFORMATIONAL AND TRAINING PURPOSES ONLY AND IS PROVIDED
"AS IS" WITHOUT WARRANTY OF ANY KIND, WHETHER EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, AND NON-INFRINGEMENT.**

Lab Overview
============

###### Abstract

![](media/1321818af6ddd397455f4247bd02833b.png)

This exercise walks you through the process of writing a smart contract that
facilitates transferring an asset from an owner to a buyer. An owner places an
asset up for sale. A buyer places an offer and specifies an inspector for the
asset and an appraiser of the asset. Once the owner and the buyer negotiate and
agree upon an offer, the inspector and the appraiser proceed to perform their
duties. After their involvement, the buyer and the owner can choose to move
forward and ultimately complete the transaction.

###### Learning Objectives

After completing the exercises in this lab, you will be able to:

-   Create a metadata file that captures the flow of the business logic, which
    will also be used for the UI configuration when the application is deployed
    on Workbench.

-   Create the Asset Transfer Smart Contract based on the business logic flow
    specified in the metadata file.

-   Deploy both the contract and metadata file.

-   Configure users in AAD and assign them to personas in Blockchain Workbench.

**Estimated time to complete this lab: 60 minutes**

**Total time to complete this lab: 2 hours (If Workbench is not already
deployed, the deployment automation takes 60 minutes.)**

Prerequisite: Deploy Azure Blockchain Workbench 
================================================

The successful completion of this lab requires a working instance of Azure
Blockchain Workbench.

If you have not yet completed Azure Blockchain Workbench deployment, please
refer to [Azure Blockchain Workbench
deployment.](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-deploy)
Please note that the deployment can take up to 60 minutes.

Things created in this lab
==========================

We will create three things in this lab:

1.  A state transition diagram that visualizes the entire flow of the Asset
    Transfer application.

2.  A configuration file that translates the state transition diagram that can
    be ingested by Workbench.

3.  A smart contract code file that implements the workflow specified in the
    configuration file.

After completing this exercise, you will be able to

1.  Design solutions to your application based on a workflow that captures the
    business logic

2.  Determine the state transitions required for your application’s workflow

3.  Determine the roles your users can take on in your application

4.  Understand that Workbench enforces which user can and cannot perform certain
    actions based on the roles assigned to the users.

Exercise 1: Designing a solution
================================

The first step in designing a solution for an application is determining what
roles users of that application can take on. A user can assume one of the
following four roles in the Asset Transfer application:

| **Role**   | **Description**                                                                                     |
|------------|-----------------------------------------------------------------------------------------------------|
| Owner      | A person who owns an asset and wants to sell the asset.                                             |
| Buyer      | A person who intends to buy the asset being sold by the seller.                                     |
| Inspector  | A person who is chosen by the buyer to be the inspector of the asset being considered for buying.   |
| Appraiser  | A person who is chosen by the buyer to be the appraiser for the asset being considered for buying.  |

The next step is determining the state an application can be in as the business
logic is materialized by users in different roles. The different states that the
Asset Transfer application can be in are:

| **State**            | **Description**                                                                                             |
|----------------------|-------------------------------------------------------------------------------------------------------------|
| Active               | Indicates that an asset is available for being bought.                                                      |
| Offer Placed         | Indicates a buyer's intention to buy.                                                                       |
| Pending Inspection   | Indicates a buyer's request to the Inspector to inspect the asset under consideration.                      |
| Inspected            | Indicates the Inspector's approval to buy the asset under consideration.                                    |
| Appraised            | Indicates the Appraiser's approval to buy the asset under consideration.                                    |
| Notional Acceptance  | Indicates that both the Inspector and the Appraiser have approved buying the asset under consideration.     |
| Seller Accepted      | Indicates the owner's approval to accept the offer made by the buyer.                                       |
| Buyer Accepted       | Indicates the buyer's approval for the owner's approval.                                                    |
| Accepted             | Indicates that both the buyer and the seller have agreed to the transfer of the asset under consideration.  |
| Terminated           | Indicates owner's disapproval to continue with selling the asset under consideration.                       |

In the final step, one determines the functions that capture the business logic
to transition among these different states. The different transition functions
used in Asset Transfer are listed below:

| **Transition Function** | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| MakeOffer               | A buyer calls to make an offer for the asset. This function causes a transition from the Active state to the OfferPlaced state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Reject                  | An owner calls this to reject the offer made by the buyer. This function causes a transition from the OfferPlaced state to the Active state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| Modify                  | An owner calls this function to modify the values assigned to the asset. This function does not cause a transition and the workflow stays in the Active state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| ModifyOffer             | A buyer calls this to modify the offer they made earlier. This function does not cause a transition and the workflow stays in the OfferPlaced state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| RescindOffer            | A buyer calls this to rescind the offer they made. This function causes a transition from the OfferPlaced state to the Active state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| AcceptOffer             | An owner calls this to accept the offer made by the buyer. This function causes a transition from the OfferPlaced state to the PendingInspection state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| MarkInspected           | The inspector assigned by the buyer calls this function to indicate that the inspector has inspected the asset. This function causes a transition from the PendingInspection state to the Inpected state when called from the PendingInspection state.  This function can also be used to transition to the NotionalAcceptance state when called from the Appraised state.                                                                                                                                                                                                                                                                                                                |
| MarkAppraised           | The appraiser assigned by the buyer calls this function to indicate that the appraiser has appraised the asset. This function causes a transition from the PendingInspection state to the Appraised state when called from the PendingInpection state. This function is also used for transitioning to the NotionalAcceptance state when called from the Inspected state.                                                                                                                                                                                                                                                                                                                 |
| Accept                  | This function can be called either by the owner or the buyer. The owner calls this function to accept the buyer’s offer after the inspector and the appraiser have approved the buyer’s decision. This function causes a transition from the NotionalAcceptance state to the SellerAccepted state. The owner can also call this function after the buyer calls function and transitions from the NotionalAcceptance state to the BuyerAccepted state. When the owner calls this function when the workflow is in the BuyerAccepted state, then the workflow transitions to the SellerAccepted state. Finally, the owner can call this function to accept and conclude the asset transfer. |
| Terminate               | The owner calls this function to terminate the entire asset transfer workflow instance. This function causes the workflow to the Terminated state from whichever state it is called by the owner.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |

Here is the state transition diagram that captures the roles, states, and the
transition functions:

![](media/f6677a1f5dc284738a6afb84a4dfee81.png)

Exercise 2: Creating the Contract Configuration
===============================================

A configuration file translates the solution design into a tangible object that
can be ingested by Workbench. We capture the roles, states, and the transition
functions in json file. Additionally, we can specify access to the transition
functions to users in specific roles, as result achieve a level of access
control on the workflow execution. We embed all this information in a json file.
Follow the five steps below to put your json file together.

##### Create the json file

Create a new file in Visual Studio named AssetTransfer.json and copy the
following meta

data in it.

"ApplicationName": "AssetTransfer",

"DisplayName": "Asset Transfer",

"Description": "Allows transfer of assets between a buyer and a seller, with
appraisal/inspection functionality",

##### Add the Application Roles

>   Add the roles determined during the design phase. Copy the following into
>   the json file.

"ApplicationRoles": [

{

"Name": "Appraiser",

"Description": "User that signs off on the asset price"

},

{

"Name": "Buyer",

"Description": "User that places an offer on an asset"

},

{

"Name": "Inspector",

"Description": "User that inspects the asset and signs off on inspection"

},

{

"Name": "Owner",

"Description": "User that signs off on the asset price"

}

],

##### Define a Workflow 

A workflow captures an implementation of the application’s business logic. Each
defined

>   workflow specifies the following configuration:

-   Name and description of the workflow.

-   States that define the status within the workflow.

-   Actions to transition to the next state.

-   User roles permitted to initiate each action

-   Smart contracts that represent business logic in code files.

>   A workflow is defined by

1.  ‘Name’ indicates the name of the workflow.

2.  ‘DisplayName’ indicates an end-user friendly name for the workflow

3.  ‘Description’ describes the workflow

4.  ‘Initiators’ indicates which roles may initiate the workflow

5.  ‘StartState’ indicates the name of the initial state for the workflow

6.  ‘Properties’ indicates a collection of workflow identifiers shown in UI

>   Add the following JSON to the file to configure the persona definitions to
>   be associated with the Asset Transfer contract:

"Workflows": [

{

"Name": "AssetTransfer",

"DisplayName": "Asset Transfer",

"Description": "Handles the business logic for the asset transfer scenario",

"Initiators": [ "Owner" ],

"StartState": "Active",

"Properties": [

{

"Name": "State",

"DisplayName": "State",

"Description": "Holds the state of the contract",

"Type": {

"Name": "state"

}

},

{

"Name": "Description",

"DisplayName": "Description",

"Description": "Describes the asset being sold",

"Type": {

"Name": "string"

}

},

{

"Name": "AskingPrice",

"DisplayName": "Asking Price",

"Description": "The asking price for the asset",

"Type": {

"Name": "money"

}

},

{

"Name": "OfferPrice",

"DisplayName": "Offer Price",

"Description": "The price being offered for the asset",

"Type": {

"Name": "money"

}

},

{

"Name": "InstanceAppraiser",

"DisplayName": "Instance Appraiser",

"Description": "The user that appraises the asset",

"Type": {

"Name": "Appraiser"

}

},

{

"Name": "InstanceBuyer",

"DisplayName": "Instance Buyer",

"Description": "The user that places an offer for this asset",

"Type": {

"Name": "Buyer"

}

},

{

"Name": "InstanceInspector",

"DisplayName": "Instance Inspector",

"Description": "The user that inspects this asset",

"Type": {

"Name": "Inspector"

}

},

{

"Name": "InstanceOwner",

"DisplayName": "Instance Owner",

"Description": "The seller of this particular asset",

"Type": {

"Name": "Owner"

}

}

],

##### Add a constructor to the Workflow

>   The constructor defines the function that causes an instance of the workflow
>   to start in the Active state. Add the following to the json file:

"Constructor": {

"Parameters": [

{

"Name": "description",

"Description": "The description of this asset",

"DisplayName": "Description",

"Type": {

"Name": "string"

}

},

{

"Name": "price",

"Description": "The price of this asset",

"DisplayName": "Price",

"Type": {

"Name": "money"

}

}

]

##### Add the transition functions and the states

>   Let us add all the transition functions and the states that we determined
>   during the design phase. Add the following to your json file:

"Functions": [

{

"Name": "Modify",

"DisplayName": "Modify",

"Description": "Modify the description/price attributes of this asset transfer
instance",

"Parameters": [

{

"Name": "description",

"Description": "The new description of the asset",

"DisplayName": "Description",

"Type": {

"Name": "string"

}

},

{

"Name": "price",

"Description": "The new price of the asset",

"DisplayName": "Price",

"Type": {

"Name": "money"

}

}

]

},

{

"Name": "Terminate",

"DisplayName": "Terminate",

"Description": "Used to cancel this particular instance of asset transfer",

"Parameters": []

},

{

"Name": "MakeOffer",

"DisplayName": "Make Offer",

"Description": "Place an offer for this asset",

"Parameters": [

{

"Name": "inspector",

"Description": "Specify a user to inspect this asset",

"DisplayName": "Inspector",

"Type": {

"Name": "Inspector"

}

},

{

"Name": "appraiser",

"Description": "Specify a user to appraise this asset",

"DisplayName": "Appraiser",

"Type": {

"Name": "Appraiser"

}

},

{

"Name": "offerPrice",

"Description": "Specify your offer price for this asset",

"DisplayName": "Offer Price",

"Type": {

"Name": "money"

}

}

],

},

{

"Name": "Reject",

"DisplayName": "Reject",

"Description": "Reject the user's offer",

"Parameters": []

},

{

"Name": "AcceptOffer",

"DisplayName": "Accept Offer",

"Description": "Accept the user's offer",

"Parameters": []

},

{

"Name": "RescindOffer",

"DisplayName": "Rescind Offer",

"Description": "Rescind your placed offer",

"Parameters": []

},

{

"Name": "ModifyOffer",

"DisplayName": "Modify Offer",

"Description": "Modify the price of your placed offer",

"Parameters": [

{

"Name": "offerPrice",

"DisplayName": "Price",

"Type": {

"Name": "money"

}

}

],

}

{

"Name": "MarkInspected",

"DisplayName": "Mark Inspected",

"Description": "Mark the asset as inspected",

"Parameters": []

},

{

"Name": "MarkAppraised",

"DisplayName": "Mark Appraised",

"Description": "Mark the asset as appraised",

"Parameters": []

}

],

"States": [

{

"Name": "Active",

"DisplayName": "Active",

"Description": "The initial state of the asset transfer workflow",

"PercentComplete": 20,

"Value": 0,

"Style": "Success",

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancels this instance of asset transfer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate Offer"

},

{

"AllowedRoles": [ "Buyer" ],

"AllowedInstanceRoles": [],

"Description": "Make an offer for this asset",

"Function": "MakeOffer",

"NextStates": ["OfferPlaced"],

"DisplayName": "Make Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Modify attributes of this asset transfer instance",

"Function": "Modify",

"NextStates": ["Active"],

"DisplayName": "Modify"

}

]

},

{

"Name": "OfferPlaced",

"DisplayName": "Offer Placed",

"Description": "Offer has been placed for the asset",

"PercentComplete": 30,

"Style": "Success",

"Value": 1,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Accept the proposed offer for the asset",

"Function": "AcceptOffer",

"NextStates": ["PendingInspection"],

"DisplayName": "Accept Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the proposed offer for the asset",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel this instance of asset transfer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you previously placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Modify the price that you specified for your offer",

"Function": "ModifyOffer",

"NextStates": ["OfferPlaced"],

"DisplayName": "Modify Offer"

}

]

},

{

"Name": "PendingInspection",

"DisplayName": "Pending Inspection",

"Description": "Asset is pending inspection",

"PercentComplete": 40,

"Style": "Success",

"Value": 2,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the offer",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel the offer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceInspector" ],

"Description": "Mark this asset as inspected",

"Function": "MarkInspected",

"NextStates": ["Inspected"],

"DisplayName": "Mark Inspected"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceAppraiser" ],

"Description": "Mark this asset as appraised",

"Function": "MarkAppraised",

"NextStates": ["Appraised"],

"DisplayName": "Mark Appraised"

}

]

},

{

"Name": "Inspected",

"DisplayName": "Inspected",

"PercentComplete": 45,

"Style": "Success",

"Value": 3,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the offer",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel the offer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceAppraiser" ],

"Description": "Mark this asset as appraised",

"Function": "MarkAppraised",

"NextStates": ["NotionalAcceptance"],

"DisplayName": "Mark Appraised"

}

]

},

{

"Name": "Appraised",

"DisplayName": "Appraised",

"Description": "Asset has been appraised, now awaiting inspection",

"PercentComplete": 45,

"Style": "Success",

"Value": 4,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the offer",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel the offer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceInspector" ],

"Description": "Mark the asset as inspected",

"Function": "MarkInspected",

"NextStates": ["NotionalAcceptance"],

"DisplayName": "Mark Inspected"

}

]

},

{

"Name": "NotionalAcceptance",

"DisplayName": "Notional Acceptance",

"Description": "Asset has been inspected and appraised, awaiting final sign-off
from buyer and seller",

"PercentComplete": 50,

"Style": "Success",

"Value": 5,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Sign-off on inspection and appraisal",

"Function": "Accept",

"NextStates": ["SellerAccepted"],

"DisplayName": "SellerAccept"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the proposed offer for the asset",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel this instance of asset transfer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Sign-off on inspection and appraisal",

"Function": "Accept",

"NextStates": ["BuyerAccepted"],

"DisplayName": "BuyerAccept"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

}

]

},

{

"Name": "BuyerAccepted",

"DisplayName": "Buyer Accepted",

"Description": "Buyer has signed-off on inspection and appraisal",

"PercentComplete": 75,

"Style": "Success",

"Value": 6,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Sign-off on inspection and appraisal",

"Function": "Accept",

"NextStates": ["SellerAccepted"],

"DisplayName": "Accept"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Reject the proposed offer for the asset",

"Function": "Reject",

"NextStates": ["Active"],

"DisplayName": "Reject"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceOwner" ],

"Description": "Cancel this instance of asset transfer",

"Function": "Terminate",

"NextStates": ["Terminated"],

"DisplayName": "Terminate"

}

]

},

{

"Name": "SellerAccepted",

"DisplayName": "Seller Accepted",

"Description": "Seller has signed-off on inspection and appraisal",

"PercentComplete": 75,

"Style": "Success",

"Value": 7,

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Sign-off on inspection and appraisal",

"Function": "Accept",

"NextStates": ["Accepted"],

"DisplayName": "Accept"

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": [ "InstanceBuyer" ],

"Description": "Rescind the offer you placed for this asset",

"Function": "RescindOffer",

"NextStates": ["Active"],

"DisplayName": "Rescind Offer"

}

]

},

{

"Name": "Accepted",

"DisplayName": "Accepted",

"Description": "Asset transfer process is complete",

"PercentComplete": 100,

"Style": "Success",

"Value": 8,

"Transitions": []

},

{

"Name": "Terminated",

"DisplayName": "Terminated",

"Description": "Asset transfer has been cancelled",

"PercentComplete": 100,

"Style": "Failure",

"Value": 9,

"Transitions": []

}

]

}

]

}

Exercise 3: Writing the Smart Contract
======================================

A smart contract code translates the configuration file into an implementation
that is ingested by Workbench. Presently, Workbench supports Ethereum. Let us
write a Solidity implementation of the json file that we created in Exercise 2.
Follow the two steps below to put your Solidity file together.

##### Add the Workbench base

In an editor, create a file called AssetTransfer.sol. Add the line of code below
to identify this is written in the Solidity language, specifically version
0.4.20.

pragma solidity \^0.4.20;

Next add the base contract for Workbench, called WorkbenchBase. For the Ethereum
blockchain, the blockchain’s built in eventing capability is used to infer
context that is utilized by the framework.  
  
When creating your contracts, this base contract will capture the contract type
and address – and will log them using events. The type and address are passed
into the base contract from your constructor and the ContractCreated function is
called from your constructor.

As state changes occur, your contract will call the ContractUpdated function on
the base contract to indicate an update has occurred, specifying the function in
your contract that was called.

contract WorkbenchBase {

event WorkbenchContractCreated(string applicationName, string workflowName,
address originatingAddress);

event WorkbenchContractUpdated(string applicationName, string workflowName,
string action, address originatingAddress);

string internal ApplicationName;

string internal WorkflowName;

function WorkbenchBase(string applicationName, string workflowName) internal {

ApplicationName = applicationName;

WorkflowName = workflowName;

}

function ContractCreated() internal {

WorkbenchContractCreated(ApplicationName, WorkflowName, msg.sender);

}

function ContractUpdated(string action) internal {

WorkbenchContractUpdated(ApplicationName, WorkflowName, action, msg.sender);

}

}

1.  Add the properties and functions

    Following that, add this at the beginning of the smart contract. The type
    for this contract will be AssetTransfer and it will inherit from the
    WorkbenchBase contract. Add:

contract AssetTransfer is WorkbenchBase('AssetTransfer', 'AssetTransfer')

Add an enum named StateType to identify the different states of the workflow.
Following that you will see a list of properties which describe the data types.
During the lifetime of the contract, the constructor and functions within the
smart contract will set the State variable to the appropriate state.

enum StateType { Active, OfferPlaced, PendingInspection, Inspected, Appraised,
NotionalAcceptance, BuyerAccepted, SellerAccepted, Accepted, Terminated }

address public InstanceOwner;

string public Description;

uint public AskingPrice;

StateType public State;

address public InstanceBuyer;

uint public OfferPrice;

address public InstanceInspector;

address public InstanceAppraiser;;

>   Let us add the constructor function that has the same name as the contract
>   and is called when the contract is created. It has two parameters:
>   “description” and “price.”

>   The contract assumes the individual creating the contract is the Owner. The
>   function begins by using msg.sender to retrieve the address on the
>   blockchain of the individual who created the contract and assigns it to the
>   Owner property.

>   The parameters of description and price are assigned to their corresponding
>   properties.

>   The function then updates the State property indicating that the contract
>   has moved to the Active state.

Add the following Solidity code to your Asset Transfer contract:

function AssetTransfer(string description, int price) public

{

InstanceOwner = msg.sender;

AskingPrice = price;

Description = description;

State = StateType.Active;

ContractCreated();

}

Add the following piece of code that implements all the functions discussed in
Exercise 1.

function Terminate() public

{

if (InstanceOwner != msg.sender)

{

revert();

}

State = StateType.Terminated;

ContractUpdated('Terminate');

}

function Modify(string description, int price) public

{

if (State != StateType.Active)

{

revert();

}

if (InstanceOwner != msg.sender)

{

revert();

}

Description = description;

AskingPrice = price;

ContractUpdated('Modify');

}

function MakeOffer(address inspector, address appraiser, int offerPrice) public

{

if (inspector == 0x0 \|\| appraiser == 0x0 \|\| offerPrice == 0)

{

revert();

}

if (State != StateType.Active)

{

revert();

}

// Cannot enforce "AllowedRoles":["Buyer"] because Role information is
unavailable

if (InstanceOwner == msg.sender) // not expressible in the current specification
language

{

revert();

}

InstanceBuyer = msg.sender;

InstanceInspector = inspector;

InstanceAppraiser = appraiser;

OfferPrice = offerPrice;

State = StateType.OfferPlaced;

ContractUpdated('MakeOffer');

}

function AcceptOffer() public

{

if (State != StateType.OfferPlaced)

{

revert();

}

if (InstanceOwner != msg.sender)

{

revert();

}

State = StateType.PendingInspection;

ContractUpdated('AcceptOffer');

}

function Reject() public

{

if (State != StateType.OfferPlaced && State != StateType.PendingInspection &&
State != StateType.Inspected && State != StateType.Appraised && State !=
StateType.NotionalAcceptance && State != StateType.BuyerAccepted)

{

revert();

}

if (InstanceOwner != msg.sender)

{

revert();

}

InstanceBuyer = 0x0;

State = StateType.Active;

ContractUpdated('Reject');

}

function Accept() public

{

if (msg.sender != InstanceBuyer && msg.sender != InstanceOwner)

{

revert();

}

if (msg.sender == InstanceOwner &&

State != StateType.NotionalAcceptance &&

State != StateType.BuyerAccepted)

{

revert();

}

if (msg.sender == InstanceBuyer &&

State != StateType.NotionalAcceptance &&

State != StateType.SellerAccepted)

{

revert();

}

if (msg.sender == InstanceBuyer)

{

if (State == StateType.NotionalAcceptance)

{

State = StateType.BuyerAccepted;

}

else if (State == StateType.SellerAccepted)

{

State = StateType.Accepted;

}

}

else

{

if (State == StateType.NotionalAcceptance)

{

State = StateType.SellerAccepted;

}

else if (State == StateType.BuyerAccepted)

{

State = StateType.Accepted;

}

}

ContractUpdated('Accept');

}

function ModifyOffer(int offerPrice) public

{

if (State != StateType.OfferPlaced)

{

revert();

}

if (InstanceBuyer != msg.sender \|\| offerPrice == 0)

{

revert();

}

OfferPrice = offerPrice;

ContractUpdated('ModifyOffer');

}

function RescindOffer() public

{

if (State != StateType.OfferPlaced && State != StateType.PendingInspection &&
State != StateType.Inspected && State != StateType.Appraised && State !=
StateType.NotionalAcceptance && State != StateType.SellerAccepted)

{

revert();

}

if (InstanceBuyer != msg.sender)

{

revert();

}

InstanceBuyer = 0x0;

OfferPrice = 0;

State = StateType.Active;

ContractUpdated('RescindOffer');

}

function MarkAppraised() public

{

if (InstanceAppraiser != msg.sender)

{

revert();

}

if (State == StateType.PendingInspection)

{

State = StateType.Appraised;

}

else if (State == StateType.Inspected)

{

State = StateType.NotionalAcceptance;

}

else

{

revert();

}

ContractUpdated('MarkAppraised');

}

function MarkInspected() public

{

if (InstanceInspector != msg.sender)

{

revert();

}

if (State == StateType.PendingInspection)

{

State = StateType.Inspected;

}

else if (State == StateType.Appraised)

{

State = StateType.NotionalAcceptance;

}

else

{

revert();

}

ContractUpdated('MarkInspected');

}

}

Exercise 4: Configuring Users
=============================

#### Scenario

In this exercise, you will be creating test users in Azure Active Directory that
can reflect each of the personas configured for the contract.

After completing this exercise, you will be able to:

-   Create users in Azure Active Directory for use with this contract.

-   Create an Administrator group in AAD.

-   Configure your account as an Administrator to gain access to Azure
    Blockchain Workbench Admin Portal.

Configuring Accounts for Test Users in AAD
------------------------------------------

This contract, like most smart contracts, involves multiple parties. Azure
Blockchain Workbench will generate an application for you automatically and
allow you to easily assign users to have access to contracts as a persona; for
example, Owner, Appraiser, and so on.

To perform those assignments in your contracts, there must be users created that
can be assigned.

In this section, you will create these users in Azure Active Directory.

1.  Navigate and sign in to the Azure Portal (portal.azure.com).

2.  Select the **Azure Active Directory** icon from the list of services.

##### Determining Your Domain

1.  Select **Domain names**. You should see a page similar to the following:

![](media/53869f89951e976e83b7dcc105f2a0c2.png)

1.  If there is only one domain in the list, note this here.

2.  If there is more than one in the list, find the one that has a check box in
    the primary column and enter the name value.

##### Assigning Users

1.  Select the **Users** item. You should now see the following screen:

    ![](media/183c2cf719d80dc44cb90fc28260b889.png)

2.  Using the following table, create the users required for this contract using
    the following steps:

3.  Select the **New User** link. This will open a User blade.

4.  In the **User** blade, provide the **Name** and the **User name**.

For the User name you will be creating new identities in the Azure Active
Directory that will get their own user names and passwords. The user name
specified is a mix of the name of the user and the domain identified earlier in
this exercise.

Select the **Profile** link and enter the first and last name for the user based
on the data in the following table. NOTE: This is important to complete as not
having this data entered will cause an issue at runtime in the Public Preview
version.

Select **OK** to save the first name and last name.

Select the **Show Password** check box. This will display the password and also
enable a copy icon to copy the password. Copy the password and paste it into the
following table.

Select the **Create** button.

This user has now been created. Repeat these steps for each of the following
users.

Note: When you sign in with these users to the Workbench Application, you will
be prompted to change your password.

##### AAD Values for Users to Create

| Name                   | User name                             | FirstName  | LastName     | Password |
|------------------------|---------------------------------------|------------|--------------|----------|
| Contoso Shipping       | contososhipping\@\<yourdomain\>       | Contoso    | Shipping     |          |
| Blockchain Shipping    | blockchainshipping\@\<yourdomain\>    | Blockchain | Shipping     |          |
| Government Regulator   | governmentregulator\@\<yourdomain\>   | Government | Regulator    |          |
| Simulated Device       | simulateddevice\@\<yourdomain\>       | Simulated  | Device       |          |
| Woodgrove Distribution | woodgrovedistribution\@\<yourdomain\> | Woodgrove  | Distribution |          |

Configuring Your Account as an Administrator
--------------------------------------------

To deploy the smart contract and assign access rights to users, you will need to
navigate to the Admin page of the web application.

If you see the Admin link in the upper-right corner of the web application,
you’re already a member of the Administrator group and can skip to the next
section.

If this is the first lab you’ve done, this link may not be visible and you will
need to add your account to the Administrator group. If you don’t see the Admin
link, follow the instructions to assign yourself as an Administrator in the
Workbench Web Portal.

1.  Navigate and sign in to the Azure Portal (portal.azure.com).

2.  Select the **Azure Active Directory** icon from the list of services on the
    left.

![](media/ffb1f921095baf14bc256968c3f689ad.png)

1.  Select the **Groups** square in the blade to the left. You should now see
    the following screen:

![](media/139ec3c0c7c7a00989c11cdd57710687.png)

##### If the Administrator Group Is Not Displayed

1.  If the Administrator group is not displayed, you will create it now. If it
    is already created, skip to the next step.

2.  Click **New group**. This will open up a new blade in the portal where you
    will provide the name of the group as Administrator, a description, and
    select **Assigned** from the **Membership type** drop-down menu.

![](media/9bb3bcd122030a93241ea03abf9deb0f.png)

1.  You can then assign yourself and other users to this group by clicking the
    “\>” beneath the **Members** section.

2.  After completing this, select the **Create** button to finalize your group.

##### If the Administrator Group Is Displayed

1.  If the Administrator Group is displayed but you did not see the Admin page
    link in the web application, then you will need to add yourself to the
    Administrator group.

2.  Select the **Administrator** group in the list. It should display the
    following screen:

![](media/a64141aaeb303d80da6d7d41f667954d.png)

1.  Select the **Members** link and add yourself and other accounts to the group
    via the **Add Members** link.

![](media/3e28e63ab45fe358b3e0f188ee31deca.png)

Exercise 5: Deploying the Smart Contract
========================================

#### Scenario

In this exercise, you will deploy the smart contract using Azure Blockchain
Workbench Web View.

After completing this exercise, you will be able to d

eploy the contract and configuration files to generate a blockchain application.

-   Assign users to contracts and personas so that they can create or
    participate in smart contracts.

-   Create a blockchain application.

Deploy the Contract and Configuration File
------------------------------------------

You can upload contract and configuration files to the Azure Blockchain
Workbench Web View. The result will be an application automatically generated
within the framework.

To add a blockchain application to Blockchain Workbench, you upload the
configuration and smart contract files to define the application.

1.  In a web browser, navigate to the Blockchain Workbench web address. For
    example, https://{workbench URL}.azurewebsites.net/ The web application is
    created when you deploy Blockchain Workbench. For information on how to find
    your Blockchain Workbench web address, see [Blockchain Workbench Web
    URL](https://review.docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-deploy#blockchain-workbench-web-url)

2.  Sign in as a Blockchain Workbench administrator. For more information on
    managing users, see [Manage Users in Azure Blockchain
    Workbench](https://review.docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-manage-users).

3.  Select **Applications** \> **New**. The **New application** pane is
    displayed.

![](media/e94a9dd8a2f1ec6ee45a2e7bdc8c5c19.png)

1.  Select **Upload the contract configuration** \> **Browse** to locate the
    **AssetTransfer.json** configuration file you created. The configuration
    file is automatically validated. Select the **Show** link to display
    validation errors. Fix validation errors before you deploy the application.

2.  Select **Upload the contract code** \> **Browse** to locate the
    **AssetTransfer.sol** smart contract code file. The code file is
    automatically validated. Select the **Show** link to display validation
    errors. Fix validation errors before you deploy the application.

3.  Select **Deploy** to create the blockchain application based on the
    configuration and smart contract files.

Deployment of the blockchain application takes a few minutes. When deployment is
finished, the new application is displayed in **Applications**.

If you received a message that the upload was successful, move on to the next
exercise.

##### Troubleshooting

If your contract upload was not successful, it may be an issue with your smart
contract or your configuration files.

You can identify if there is an issue with your smart contract by testing in in
the remix IDE available at <http://remix.ethereum.org>.

You can also evaluate your JSON file using Visual Studio or
<http://jsoneditoronline.org/>.

Add Members to the Application
------------------------------

Members are added to each application. Members can have one or more application
roles to initiate contracts or take actions.

To add members to an application, select an application tile in the Applications
pane.

1.  Select the member tile to display a list of the current members.

2.  Select **Add members**.

3.  Search for the user's name. Only Azure AD users that exist in the Blockchain
    Workbench tenant are listed. If the user is not found, you need to [Add
    Azure AD
    users](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-manage-users).

4.  Select a role from the drop-down (i.e. Owner, Appraiser, etc.)

![](media/1c730634f27a8ede178bad223627ad9c.png)

Create a New Contract Instance
------------------------------

1.  In Blockchain Workbench application section, select “+ New Contract”

![](media/4f54e08eeded593cfe0b54dc15b00498.png)

2. The **New contract** pane is displayed. Specify the initial parameters
values. Select **Create**.

2.

![New contract pane](media/2b6739058e92c4c756c988ae63355cb9.png)

The newly created contract is displayed in the list with the other active
contracts.

![Active contracts list](media/7916a3da6e934cd4753cae07192decaa.png)

##### Take Action on the Contract

1.  In Blockchain Workbench application section, select the application tile
    that contains the contract to take the action.

![](media/96f1b235481c62471851ede6de711846.png)

>   Application list

1.  Select the contract in the list.

![](media/b9163e61ab25e45b958b752dbcace873.png)

>   Contract list

>   Details about the contract are displayed in different sections.

![](media/b92b9f83dd2bd08ccb393eea0d2012ca.png)

>   Contract details

1.  In the **Action** section, select **Take action**.

2.  The details about the current state of the contract are displayed in a pane.
    Choose the action you want to take in the drop-down.

![](media/afaa28184ddcf09f9c1dfc9e38be47bd.png)

>   Take action

1.  Select **Execute** to take the action.

##### Testing the Contract

To test the contract, sign out of the contract and sign in as the users you
assigned when creating it. You should now see the contract displayed for this
persona. Note that the actions available are different for these different
users.
