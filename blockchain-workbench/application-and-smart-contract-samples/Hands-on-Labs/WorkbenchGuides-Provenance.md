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

![](media/53bde949f5f72071001dee6a9588f27c.png)

This exercise walks you through the process of writing a smart contract that
facilitates tracking the ownership or responsibility of an asset across a supply
chain. The owner of an asset can track transfers of responsibility among
different parties. This tracking information, if required, can then be used to
determine the party responsible for any damages to the asset.

###### Learning Objectives

After completing the exercises in this lab, you will be able to:

-   Create a metadata file that captures the flow of the business logic, which
    will also be used for the UI configuration when the application is deployed
    on Workbench.

-   Create the Basic Provenance Smart Contract based on the business logic flow
    specified in the metadata file.

-   Deploy both the contract and metadata file.

-   Configure users in AAD and assign them to personas in Blockchain Workbench.

**Estimated time to complete this lab: 60 minutes**

**Total time to complete this lab: 2 hours (If Workbench is not already
deployed, the deployment automation takes 60 minutes.)**

Prerequisite: Deploy Azure Blockchain Workbench
===============================================

The successful completion of this lab requires a working instance of Azure
Blockchain Workbench.

If you have not yet completed Azure Blockchain Workbench deployment, please
refer to [Azure Blockchain Workbench
deployment.](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-deploy)
Please note that the deployment can take up to 60 minutes.

Things created in this lab
==========================

We will create three things in this lab:

1.  A state transition diagram that visualizes the entire flow of the Basic
    Provenance application.

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
following four roles in the Basic Provenance application:

| **Role**                | **Description**                                                                      |
|-------------------------|--------------------------------------------------------------------------------------|
| InitiatingCounterParty  | The first participant in the supply chain.                                           |
| Counterparty            | A party to whom responsibility for a product has been assigned, e.g. a shipper       |
| Owner                   | The organization that owns the product being transported, e.g. a manufacturer        |
| Observer                | The individual or organization monitoring the supply chain, e.g. a government agency |

The next step is determining the state an application can be in as the business
logic is materialized by users in different roles. The different states that the
Basic Provenance application can be in are:

| **State**  | **Description**                                                                                           |
|------------|-----------------------------------------------------------------------------------------------------------|
| Created    | Indicates that the contract has been initiated and tracking is in progress.                               |
| InTransit  | Indicates that a Counterparty currently is in possession and responsible for the asset being transported. |
| Completed  | Indicates the product has reached its intended destination.                                               |

In the final step, one determines the functions that capture the business logic
to transition among these different states. The different transition functions
used in Basic Provenance are listed below:

| **Transition Function** | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| TransferResponsibility  | This function can be called by the owner or a counterparty depending on the current state of the application’s workflow. The owner, who is also the InitialCounterParty, can call this function when the current state is Created. Upon calling this function, the responsibility is transferred to a counterparty. At this point, this function causes a transition to the InTransit state from the Created state. The counterparty assigned by the owner or by another counterparty can call this function when the current state is InTransit. A counterparty calls this function to transfer responsibility to another counterparty. At this point, this function causes no transitions and the state remains unchanged. |
| Complete                | The owner of the asset can call this function to indicate the completion of transfer of responsibilities. This function causes a transition from the InTransit state to the Completed state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |

Here is the state transition diagram that captures the roles, states, and the
transition functions:

![Machine generated alternative text: BASIC fRDVENRNCe APB-ICRTIDN ROLES • CWNT€R?ftRTY OBJNER ( ON) • (0B) LEGEND • -TF TQP,NSITION ROLE RANSITIDNS -W. TRANSFER AIG. C? TRANSIT Compl€T€D ](media/5b0d2567def63885f0aca050f5b85b3e.png)

Exercise 2: Creating the Contract Configuration
===============================================

A configuration file translates the solution design into a tangible object that
can be ingested by Workbench. We capture the roles, states, and the transition
functions in json file. Additionally, we can specify access to the transition
functions to users in specific roles, as result achieve a level of access
control on the workflow execution. We embed all this information in a json file.
Follow the five steps below to put your json file together.

##### Create the json file

>   Create a new file in Visual Studio named BasicProvenance.json and copy the
>   following meta data in it.

"ApplicationName": "BasicProvenance",

"DisplayName": "Basic Provenance",

"Description": "Allows traceability of ownership and responsibility"

1.  **Add the Application Roles**

>   Add the roles determined during the design phase. Copy the following into
>   the json file.

"ApplicationRoles": [

{

"Name": "InitiatingCounterparty",

"Description": "..."

},

{

"Name": "Counterparty",

"Description": "..."

},

{

"Name": "Owner",

"Description": "..."

},

{

"Name": "Observer",

"Description": "..."

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

"Name": "BasicProvenance",

"DisplayName": "Basic Provenance",

"Description": "...",

"Initiators": [ "Owner" ],

"StartState": "Created",

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

"Name": "InitiatingCounterparty",

"DisplayName": "InitiatingCounterparty",

"Description": "...",

"Type": {

"Name": "InitiatingCounterparty"

}

},

{

"Name": "Counterparty",

"DisplayName": "Counterparty",

"Description": "...",

"Type": {

"Name": "Counterparty"

}

},

{

"Name": "PreviousCounterparty",

"DisplayName": "PreviousCounterparty",

"Description": "...",

"Type": {

"Name": "Counterparty"

}

},

{

"Name": "SupplyChainOwner",

"DisplayName": "SupplyChainOwner",

"Description": "...",

"Type": {

"Name": "Owner"

}

},

{

"Name": "SupplyChainObserver",

"DisplayName": "SupplyChainObserver",

"Description": "...",

"Type": {

"Name": "Observer"

}

}

],

##### Add a constructor to the Workflow

>   The constructor defines the function that causes an instance of the workflow
>   to start in the Active state. Add the following to the json file:

"Constructor": {

"Parameters": [

{

"Name": "supplyChainOwner",

"Description": "...",

"DisplayName": "supplyChainOwner",

"Type": {

"Name": "Owner"

}

},

{

"Name": "supplyChainObserver",

"Description": "...",

"DisplayName": "supplyChainObserver",

"Type": {

"Name": "Observer"

}

}

]

},

##### Add the transition functions and the states

"Functions": [

{

"Name": "TransferResponsibility",

"DisplayName": "TransferResponsibility",

"Description": "...",

"Parameters": [

{

"Name": "newCounterparty",

"Description": "...",

"DisplayName": "newCounterparty",

"Type": {

"Name": "user"

}

}

]

},

{

"Name": "Complete",

"DisplayName": "Complete",

"Description": "...",

"Parameters": [

],

"States": [

{

"Name": "Created",

"DisplayName": "Created",

"Description": "...",

"PercentComplete": 10,

"Value": 0,

"Style": "Success",

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": ["InitiatingCounterparty"],

"Description": "...",

"Function": "TransferResponsibility",

"NextStates": [ "InTransit" ],

"DisplayName": "TransferResponsibility"

}

]

},

{

"Name": "InTransit",

"DisplayName": "InTransit",

"Description": "...",

"PercentComplete": 50,

"Value": 1,

"Style": "Success",

"Transitions": [

{

"AllowedRoles": [],

"AllowedInstanceRoles": ["Counterparty" ],

"Description": "...",

"Function": "TransferResponsibility",

"NextStates": ["InTransit"],

"DisplayName": "TransferResponsibility"

},

{

"AllowedRoles": ["Owner"],

"AllowedInstanceRoles": [],

"Description": "...",

"Function": "Complete",

"NextStates": ["Completed"],

"DisplayName": "Complete"

}

]

},

{

"Name": "Completed",

"DisplayName": "Completed",

"Description": "...",

"PercentComplete": 100,

"Value": 2,

"Style": "Success",

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

In an editor, create a file called BasicProvenance.sol. Add the line of code
below to identify this is written in the Solidity language, specifically version
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

##### Add the properties and functions

Following that, add this at the beginning of the smart contract. The type for
this contract will be BasicProvenance and it will inherit from the WorkbenchBase
contract.

contract BasicProvenance is WorkbenchBase('BasicProvenance', 'BasicProvenance')

Add an enum named StateType to identify the different states of the workflow.
Following that you will see a list of properties which describe the data types.
During the lifetime of the contract, the constructor and functions within the
smart contract will set the State variable to the appropriate state.

//Set of States

enum StateType { Created, InTransit, Completed}

//List of properties

StateType public State;

address public InitiatingCounterparty;

address public Counterparty;

address public PreviousCounterparty;

address public SupplyChainOwner;

address public SupplyChainObserver;

Let us add the constructor function that has the same name as the contract and
is called when the contract is created. It has two parameters:
“supplyChainOwner” and “supplyChainObserver.”

The contract assumes the individual creating the contract is the
InitialCounterparty. Because this is the constructor, this individual is also
assigned to the InitiatingCounterparty.

The function assigns the SupplyChainOwner property the value of the
supplyChainOwner parameter.

The function then updates the State property indicating that the contract has
moved to the Created state.

Add the following Solidity code to your TelemetryCompliance contract:

function BasicProvenance(address supplyChainOwner, address supplyChainObserver)

{

InitiatingCounterparty = msg.sender;

Counterparty = InitiatingCounterparty;

SupplyChainOwner = supplyChainOwner;

SupplyChainObserver = supplyChainObserver;

State = StateType.Created;

ContractCreated();

}

Add the following piece of code that implements all the functions discussed in
Exercise 1.

function TransferResponsibility(address newCounterparty) public

{

if (Counterparty != msg.sender \|\| State == StateType.Completed)

{

revert();

}

if (State == StateType.Created)

{

State = StateType.InTransit;

}

PreviousCounterparty = Counterparty;

Counterparty = newCounterparty;

ContractUpdated('TransferResponsibility');

}

function Complete() public

{

if (SupplyChainOwner != msg.sender \|\| State == StateType.Completed)

{

revert();

}

State = StateType.Completed;

PreviousCounterparty = Counterparty;

Counterparty = 0x0;

ContractUpdated('Complete');

}

Exercise 4: Configuring Users
=============================

#### Scenario

In this exercise, you will be creating test users in Azure Active Directory that
can reflect each of the personas configured for the contract.

After completing this exercise, you will be able to:

-   Create users in Azure Active Directory for use with this contract.

-   Create an Administrator group in AAD.

-   Configure your account as an Administrator to gain access to the Blockchain
    Workbench Admin Portal.

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

![](media/e918b6cb0e9e6194ffc521cb3759a4b9.png)

1.  Using the following table, create the users required for this contract using
    the following steps:

2.  Select the **New User** link. This will open a User blade.

3.  In the **User** blade, provide the **Name** and the **User name**.

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

![](media/42197a4023359e63973ea2d8976f7e92.png)

Exercise 5: Deploying the Smart Contract
========================================

#### Scenario

In this exercise, you will deploy the smart contract using the Blockchain
Workbench.

After completing this exercise, you will be able to:

-   Deploy contracts and configuration files to generate a blockchain
    application.

-   Assign users to contracts and personas so that they can create or
    participate in smart contracts.

-   Create a contract instance.

Deploy the Contract and Configuration Files
-------------------------------------------

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
    **BasicProvenance.json** configuration file you created. The configuration
    file is automatically validated. Select the **Show** link to display
    validation errors. Fix validation errors before you deploy the application.

2.  Select **Upload the contract code** \> **Browse** to locate the
    **BasicProvenance.sol** smart contract code file. The code file is
    automatically validated. Select the **Show** link to display validation
    errors. Fix validation errors before you deploy the application.

3.  Select **Deploy** to create the blockchain application based on the
    configuration and smart contract files.

![](media/08c20685ae9ec79522fc5868fbd568b9.png)

Deployment of the blockchain application takes a few minutes. When deployment is
finished, the new application is displayed in **Applications**.

![](media/451c1f9cd319ce3c7fd341e8976d9ce2.png)

If you received a message that the upload was successful, move on to the next
exercise.

##### Troubleshooting

If your contract upload was not successful, it may be an issue with your smart
contract or your configuration files.

You can identify if there is an issue with your smart contract by testing in in
the remix IDE available at <http://remix.ethereum.org>.

You can also evaluate your JSON file using Visual Studio or
<http://jsoneditoronline.org/>.

Add Member Users to the Application
-----------------------------------

To add members to an application, select an application tile in the Applications
pane.

1.  Select the member tile to display a list of the current members.

2.  Select **Add members**.

3.  Search for the user's name. Only Azure AD users that exist in the Blockchain
    Workbench tenant are listed. If the user is not found, you need to [Add
    Azure AD
    users](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-manage-users#add-azure-ad-users).

4.  Select a role from the drop-down (i.e. InitiatingCounterparty, Counterparty
    etc.)

![](media/c80d93955ee5bc4567bae012ea1c7a09.png)

Create a Contract Instance
--------------------------

1.  In Blockchain Workbench application section, select “+ New Contract”. Please
    note that only users assigned to the Owner role can create a new contract.

![](media/8eaed0e4161cb00e7cd9030da7104bfe.png)

2. The **New contract** pane is displayed. Specify the initial parameters
values. Select **Create**.

![](media/13ae6a8a5848c15dee3b593e8408cbef.png)

The newly created contract is displayed in the list with the other active
contracts.

![](media/be99946002fb6913de02f1442f75095a.png)

##### Take Action on the Contract

1.  In Blockchain Workbench application section, select the application tile
    that contains the contract to take the action.

2.  Select the contract in the list.

>   Details about the contract are displayed in different sections.

![](media/b57bddc5367302610c2adc5b47d06112.png)

1.  In the **Action** section, select **Take action**.

2.  The details about the current state of the contract are displayed in a pane.
    Choose the action you want to take in the drop-down.

3.  Select **Execute** to take the action.

##### Testing the Contract

To test the contract, sign out of the contract and sign in as the users you
assigned when creating it. You should now see the contract displayed for this
persona. Note that the actions available are different for these different
users.
