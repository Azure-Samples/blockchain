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

![](media/64c4c25a8869eec2bfdb2dbb04bdf734.png)

This exercise walks you through the process of writing a smart contract to
collect telemetry information and enforce contract specifics related to
conditions during transport. Specifically, receiving and evaluating temperature
and humidity data against a range specified by the owner of the goods being
transported. All participants will be able to view the state and details of the
contract at any point in time. If the IoT device identifies that the telemetry
is out of the acceptable range, the contract will indicate violation of
compliance terms and appropriate remedies can be sought.

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

1.  A state transition diagram that visualizes the entire flow of the
    Refrigerated Transportation application.

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
following five roles in the Refrigerated Transportation application:

| **Role**               | **Description**                                                                                            |
|------------------------|------------------------------------------------------------------------------------------------------------|
| InitiatingCounterParty | The first participant in the supply chain.                                                                 |
| Counterparty           | A party to whom responsibility for a product has been assigned, e.g. a shipper                             |
| Device                 | A device used to monitor the temperature and humidity of the environment the good(s) are being shipped in. |
| Owner                  | The organization that owns the product being transported, e.g. a manufacturer                              |
| Observer               | The individual or organization monitoring the supply chain, e.g. a government agency                       |

The next step is determining the state an application can be in as the business
logic is materialized by users in different roles. The different states that the
Refrigerated Transportation application can be in are:

| **State**       | **Description**                                                                                       |
|-----------------|-------------------------------------------------------------------------------------------------------|
| Created         | Indicates that the contract has initiated, and tracking is in progress.                               |
| InTransit       | Indicates that a Counterparty currently is in possession and responsible for goods being transported. |
| Completed       | Indicates the product has reached its intended destination.                                           |
| OutOfCompliance | Indicates that the agreed upon terms for temperature and humidity conditions were not met.            |

In the final step, one determines the functions that capture the business logic
to transition among these different states. The different transition functions
used in Refrigerated Transportation are listed below:

| **Transition Function** | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| TransferResponsibility  | This function can be called by the owner or a counterparty depending on the current state of the application’s workflow. The owner, who is also the InitialCounterParty, can call this function when the current state is Created. Upon calling this function, the responsibility is transferred to a counterparty. At this point, this function causes a transition to the InTransit state from the Created state. The counterparty assigned by the owner or by another counterparty can call this function when the current state is InTransit. A counterparty calls this function to transfer responsibility to another counterparty. At this point, this function causes no transitions and the state remains unchanged. |
| IngestTelemetry         | A device calls this function to pass the ingested telemetry parameters either when in the Created state or in the InTransit state. This function causes the state to change only when the ingested telemetry is not within the agreed limits. The resulting state is OutOfCompliance. No state changes are observed if the ingested telemetry readings are within the agreed limits.                                                                                                                                                                                                                                                                                                                                         |
| Complete                | The owner of the asset can call this function to indicate the completion of refrigerated transportation. This function causes a transition from the InTransit state to the Completed state.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |

Here is the state transition diagram that captures the roles, states, and the
transition functions:

![Machine generated alternative text: ELEMETRY OMPLIRNCE STmTe TRANSITIONS ATI-ICATION ROLES - XNlTlATlN6 COUNTCR?ARTH C rcp) - COONT&RPARTY CC?) - Device C') - Obe0NeR - OBSeRvee COB) F: INGEST TEL€m€T2--f ( AIRD • TRANSITION ROLE AR Al-cowe» -SrÄT€s C OR AR INT F. 1üNSFER TRANSIT • CompG7E TELEtvETRM ( ) OUT og Comp.-ETED cess ](media/ae6bb2f23baed4861753a24ba3a086c9.png)

Exercise 2: Creating the Contract Configuration
===============================================

A configuration file translates the solution design into a tangible object that
can be ingested by Workbench. We capture the roles, states, and the transition
functions in json file. Additionally, we can specify access to the transition
functions to users in specific roles, as result achieve a level of access
control on the workflow execution. We embed all this information in a json file.
Follow the five steps below to put your json file together.

##### Create the json file

>   Create a new file in Visual Studio named RefrigeratedTransportation.json and
>   copy the following meta data in it.

"ApplicationName": "RefrigeratedTransportation",

"DisplayName": "Refrigerated Transportation",

"Description": "Application to track end-to-end transportation of perishable
goods.",

##### Add the Application Roles

>   Add the roles determined during the design phase. Copy the following into
>   the json file.

"ApplicationRoles": [

{

"Name": "InitiatingCounterparty",

"Description": "First party who stores or transports a shipment."

},

{

"Name": "Counterparty",

"Description": "A party who stores or transports a shipment."

},

{

"Name": "Device",

"Description": "A device to track humidity and temperature."

},

{

"Name": "Owner",

"Description": "The owner who owns the end-to-end shipment."

},

{

"Name": "Observer",

"Description": "An observer who has oversight on the end-to-end shipment."

}

],

##### Define a Workflow 

A workflow captures an implementation of the application’s business logic. Each
defined workflow specifies the following configuration:

-   Name and description of the workflow.

-   States that define the status within the workflow.

-   Actions to transition to the next state.

-   User roles permitted to initiate each action

-   Smart contracts that represent business logic in code files.

A workflow is defined by

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

"Name": "RefrigeratedTransportation",

"DisplayName": "Refrigerated Transportation",

"Description": "Main workflow to track end-to-end transportation of perishable
goods.",

"Initiators": [ "Owner" ],

"StartState": "Created

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

"Name": "Owner",

"DisplayName": "Owner",

"Description": "The owner of the end-to-end shipment.",

"Type": {

"Name": "Owner"

}

},

{

"Name": "InitiatingCounterparty",

"DisplayName": "Initial Party",

"Description": "First party who stored or transported the shipment.",

"Type": {

"Name": "InitiatingCounterparty"

}

},

{

"Name": "Counterparty",

"DisplayName": "Current Party",

"Description": "Current party who is storing or transporting the shipment.",

"Type": {

"Name": "Counterparty"

}

},

{

"Name": "PreviousCounterparty",

"DisplayName": "Last Party",

"Description": "Last party who stored or transported the shipment.",

"Type": {

"Name": "Counterparty"

}

},

{

"Name": "Device",

"DisplayName": "Device",

"Description": "The device used to track humidity and temperature of the
shipment.",

"Type": {

"Name": "Device"

}

},

{

"Name": "SupplyChainOwner",

"DisplayName": "Owner",

"Description": "The owner of the shipment.",

"Type": {

"Name": "Owner"

}

},

{

"Name": "SupplyChainObserver",

"DisplayName": "Observer",

"Description": "The observer overseeing the shipment.",

"Type": {

"Name": "Observer"

}

},

{

"Name": "MinHumidity",

"DisplayName": "Min Humidity",

"Description": "Minimum humidity requirement.",

"Type": {

"Name": "int"

}

},

{

"Name": "MaxHumidity",

"DisplayName": "Max Humidity",

"Description": "Max humidity requirement.",

"Type": {

"Name": "int"

}

},

{

"Name": "MinTemperature",

"DisplayName": "Min Temperature",

"Description": "Min temperature requirement.",

"Type": {

"Name": "int"

}

},

{

"Name": "MaxTemperature",

"DisplayName": "Max Temperature",

"Description": "Max temperature requirement.",

"Type": {

"Name": "int"

}

},

{

"Name": "ComplianceSensorType",

"DisplayName": "Sensor Type",

"Description": "The type of IoT sensor used to read out of compliance reading.
Either humidity or temperature.",

"Type": {

"Name": "int"

}

},

{

"Name": "ComplianceSensorReading",

"DisplayName": "Sensor Reading",

"Description": "The IoT sensor value for the out of compliance reading.",

"Type": {

"Name": "int"

}

},

{

"Name": "ComplianceStatus",

"DisplayName": "Status",

"Description": "Boolean to indicate whether the shipment is in compliance or
not.",

"Type": {

"Name": "bool"

}

},

{

"Name": "ComplianceDetail",

"DisplayName": "Detail",

"Description": "A friendly string indicating the issue and sensor reading.",

"Type": {

"Name": "string"

}

},

{

"Name": "LastSensorUpdateTimestamp",

"DisplayName": "Sensor Time",

"Description": "The time the sensor reading was taken.",

"Type": {

"Name": "int"

}

}

],

1.  Add a constructor to the Workflow

    The constructor defines the function that causes an instance of the workflow
    to start in the Active state. Add the following to the json file:

"Constructor": {

"Parameters": [

{

"Name": "device",

"Description": "...",

"DisplayName": "Device",

"Type": {

"Name": "Device"

}

},

{

"Name": "supplyChainOwner",

"Description": "...",

"DisplayName": "Owner",

"Type": {

"Name": "Owner"

}

},

{

"Name": "supplyChainObserver",

"Description": "...",

"DisplayName": "Observer",

"Type": {

"Name": "Observer"

}

},

{

"Name": "minHumidity",

"Description": "...",

"DisplayName": "Min Humidity",

"Type": {

"Name": "int"

}

},

{

"Name": "maxHumidity",

"Description": "...",

"DisplayName": "Max Humidity",

"Type": {

"Name": "int"

}

},

{

"Name": "minTemperature",

"Description": "...",

"DisplayName": "Min Temperature",

"Type": {

"Name": "int"

}

},

{

"Name": "maxTemperature",

"Description": "...",

"DisplayName": "Max Temperature",

"Type": {

"Name": "int"

}

}

]

},

1.  Add the transition functions and the states

    Let us add all the transition functions and the states that we determined
    during the design phase. Add the following to your json file:

"Functions": [

{

"Name": "IngestTelemetry",

"DisplayName": "IngestTelemetry",

"Description": "...",

"Parameters": [

{

"Name": "humidity",

"Description": "...",

"DisplayName": "humidity",

"Type": {

"Name": "int"

}

},

{

"Name": "temperature",

"Description": "...",

"DisplayName": "temperature",

"Type": {

"Name": "int"

}

},

{

"Name": "timestamp",

"Description": "...",

"DisplayName": "timestamp",

"Type": {

"Name": "int"

}

}

],

},

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

],

},

{

"Name": "Complete",

"DisplayName": "Complete",

"Description": "...",

"Parameters": [

],

}

]

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

},

{

"AllowedRoles": [],

"AllowedInstanceRoles": ["Device" ],

"Description": "...",

"Function": "IngestTelemetry",

"NextStates": [ "OutOfCompliance", "Created"],

"DisplayName": "IngestTelemetry"

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

"AllowedRoles": [],

"AllowedInstanceRoles": [ "Device"],

"Description": "...",

"Function": "IngestTelemetry",

"NextStates": ["OutOfCompliance", "InTransit" ],

"DisplayName": "IngestTelemetry"

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

"Style": "Success"

},

{

"Name": "OutOfCompliance",

"DisplayName": "OutOfCompliance",

"Description": "...",

"PercentComplete": 100,

"Value": 3,

"Style": "Failure",

"Transitions": [

]

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

In an editor, create a file called RefrigeratedTransportation.sol. Add the line
of code below to identify this is written in the Solidity language, specifically
version 0.4.20.

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
this contract will be RefrigeratedTransportation and it will inherit from the
WorkbenchBase contract. Add:

contract RefrigeratedTransportation is
WorkbenchBase('RefrigeratedTransportation', 'RefrigeratedTransportation')

Add an enum named StateType to identify the different states of the workflow.
Following that you will see a list of properties which describe the data types.
During the lifetime of the contract, the constructor and functions within the
smart contract will set the State variable to the appropriate state.

//Set of States

enum StateType { Created, InTransit, Completed, OutOfCompliance}

enum SensorType { None, Humidity, Temperature}

//List of properties

StateType public State;

Address public Owner;

address public InitiatingCounterparty;

address public Counterparty;

address public PreviousCounterparty;

address public Device;

address public SupplyChainOwner;

address public SupplyChainObserver;

int public MinHumidity;

int public MaxHumidity;

int public MinTemperature;

int public MaxTemperature;

SensorType public ComplianceSensorType;

int public ComplianceSensorReading;

bool public ComplianceStatus;

string public ComplianceDetail;

int public LastSensorUpdateTimestamp;

>   Let us add the constructor function that has the same name as the contract
>   and is called when the contract is created. It has seven parameters:
>   “device,” “supplyChainOwner,” “supplyChainObserver,” “minHumidity,”
>   “maxHumidity,” “minTemperature,” “maxTemperature.”

>   The constructor sets up the values for compliance to indicate that it is in
>   compliance and sets the sensor reading to -1 as none have yet occurred.

>   The contract assumes the individual creating the contract is the
>   Counterparty. Because this is the constructor, this individual is also
>   assigned to the InitiatingCounterparty.

>   The “device” parameter is assigned to the Device property.

>   The function assigns the SupplyChainOwner property the value of the
>   supplyChainOwner parameter. It does the same for SupplyChainObserver and
>   Humidity/Temperature parameters.

>   The function then updates the State property indicating that the contract
>   has moved to the Created state.

Add the following Solidity code to your TelemetryCompliance contract:

function RefrigeratedTransportation(address device, address supplyChainOwner,
address supplyChainObserver, int minHumidity, int maxHumidity, int
minTemperature, int maxTemperature) public

{

ComplianceStatus = true;

ComplianceSensorReading = -1;

InitiatingCounterparty = msg.sender;

Owner = InitiatingCounterparty;

Counterparty = InitiatingCounterparty;

Device = device;

SupplyChainOwner = supplyChainOwner;

SupplyChainObserver = supplyChainObserver;

MinHumidity = minHumidity;

MaxHumidity = maxHumidity;

MinTemperature = minTemperature;

MaxTemperature = maxTemperature;

State = StateType.Created;

ComplianceDetail = 'N/A';

ContractCreated();

}

Add the following piece of code that implements all the functions discussed in
Exercise 1.

function IngestTelemetry(int humidity, int temperature, int timestamp) public

{

// \@Dev separately check for states and sender

// to avoid not checking for state when the sender is the device

// because of the logical OR

if ( State == StateType.Completed )

{

revert();

}

if ( State == StateType.OutOfCompliance )

{

revert();

}

if (Device != msg.sender)

{

revert();

}

LastSensorUpdateTimestamp = timestamp;

if (humidity \> MaxHumidity \|\| humidity \< MinHumidity)

{

ComplianceSensorType = SensorType.Humidity;

ComplianceSensorReading = humidity;

ComplianceDetail = 'Humidity value out of range.';

ComplianceStatus = false;

}

else if (temperature \> MaxTemperature \|\| temperature \< MinTemperature)

{

ComplianceSensorType = SensorType.Temperature;

ComplianceSensorReading = temperature;

ComplianceDetail = 'Temperature value out of range.';

ComplianceStatus = false;

}

if (ComplianceStatus == false)

{

State = StateType.OutOfCompliance;

}

ContractUpdated('IngestTelemetry');

}

function TransferResponsibility(address newCounterparty) public

{

// \@Dev keep the state checking, message sender, and device checks separate

// to not get cloberred by the order of evaluation for logical OR

if ( State == StateType.Completed )

{

revert();

}

if ( State == StateType.OutOfCompliance )

{

revert();

}

if ( InitiatingCounterparty != msg.sender && Counterparty != msg.sender )

{

revert();

}

if ( newCounterparty == Device )

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

// \@Dev keep the state checking, message sender, and device checks separate

// to not get cloberred by the order of evaluation for logical OR

if ( State == StateType.Completed )

{

revert();

}

if ( State == StateType.OutOfCompliance )

{

revert();

}

if (Owner != msg.sender && SupplyChainOwner != msg.sender)

{

revert();

}

State = StateType.Completed;

PreviousCounterparty = Counterparty;

Counterparty = 0x0;

ContractUpdated('Complete');

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

-   Configure your account as an Administrator to gain access to the Blockchain
    Workbench Admin Web Portal.

Configuring Accounts for Test Users in AAD
------------------------------------------

This contract, like most smart contracts, involves multiple parties. The
Blockchain Workbench framework will generate an application for you
automatically and allow you to easily assign users to have access to contracts
as a persona; for example Counterparty, SupplyChainOwner, and so on.

To perform those assignments in your contracts, there must be users created that
can be assigned.

In this section, you will create these users in Azure Active Directory.

1.  Navigate and sign in to the Azure Portal (portal.azure.com).

2.  Select the **Azure Active Directory** icon from the list of services on the
    left.

##### Determining Your Domain

1.  Select **Domain names**. You should see a page similar to the following:

![](media/53869f89951e976e83b7dcc105f2a0c2.png)

1.  If there is only one domain in the list, note this here.

2.  If there is more than one in the list, find the one that has a check box in
    the primary column and enter the name value.

##### Assigning Users

1.  Select the **Users and groups** item. You should now see the following
    screen:

![](media/6236bf25286aeef328c5986005b1cd48.png)

1.  Select the **Users** square in the lower right and you should now see the
    following screen:

![](media/d37c21ed6066bf87f31613bd74b4ac04.png)

Using the following table, create the users required for this contract using the
following steps:

1.  Select the **New user** link. This will open a User blade.

2.  In the **User** blade, provide the **Name** and the **User name**.

For the User name you will be creating new identities in the Azure Active
Directory that will get their own user names and passwords. The user name
specified is a mix of the name of the user and the domain identified earlier in
this exercise.

1.  Select the **Profile** link and enter the first and last name for the user
    based on the data in the following table. NOTE: This is important to
    complete as not having this data entered will cause an issue at runtime in
    the pre-release version.

2.  Select **OK** to save the first name and last name.

3.  Select the **Show Password** check box. This will display the password and
    also enable a copy icon to copy the password. Copy the password and paste it
    into the following table.

4.  Select the **Create** button.

This user has now been created. Repeat these steps for each of the following
users.

**Note**: When you sign in with these users to the Workbench Application, you
will be prompted to change your password.

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
deployed Workbench application.

1.  Navigate and sign in to the Azure Portal (portal.azure.com).

2.  Select the **Azure Active Directory** icon from the list of services on the
    left.

3.  Select the **Users and groups** item. You should now see the following
    screen:

![](media/6236bf25286aeef328c5986005b1cd48.png)

1.  Select the **Groups** square in the lower right and you should now see the
    following screen:

![](media/cae55d8aca461b74d6ae5aaf395f10fe.png)

##### If the Administrator Group Is Not Displayed

If the Administrator group is not displayed, you will create it now. If it is
already created, skip to the next step.

1.  Select **New Group**. This will open up a new blade in the portal where you
    will provide the name of the group as Administrator, a description, and
    select **Assigned** from the **Membership type** drop-down menu.

![](media/41b85c0d69e66c8d56a0d037f7eb57e5.png)

1.  You can then assign yourself and other users to this group by selecting the
    “\>” beneath the **Members** section.

2.  After completing this, select the **Create** button to finalize your group.

##### If the Administrator Group Is Displayed

If the Administrator Group was displayed in Step 4 but you did not see the Admin
page link in the web application, then you will need to add yourself to the
Administrator group.

1.  Select the **Administrator** group in the list displayed in Step 4. It
    should display the following screen:

![](media/a64141aaeb303d80da6d7d41f667954d.png)

1.  Select the **Members** link and add yourself and other accounts to the group
    via the **Add Members** link.

![](media/3e28e63ab45fe358b3e0f188ee31deca.png)

Exercise 5: Deploying the Smart Contract
========================================

#### Scenario

In this exercise, you will deploy the smart contract using the Azure Blockchain
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

4.  Select **Upload the contract configuration** \> **Browse** to locate the
    **RefrigeratedTransportation.json** configuration file you created. The
    configuration file is automatically validated. Select the **Show** link to
    display validation errors. Fix validation errors before you deploy the
    application.

5.  Select **Upload the contract code** \> **Browse** to locate the
    **BasicProvenance.sol** smart contract code file. The code file is
    automatically validated. Select the **Show** link to display validation
    errors. Fix validation errors before you deploy the application. .

6.  Select **Deploy** to create the blockchain application based on the
    configuration and smart contract files.

Deployment of the blockchain application takes a few minutes. When deployment is
finished, the new application is displayed in **Applications**.

![](media/17a31d1cc8cbd5e9a5b8bbe1afe9340f.png)

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

4.  Select a role from the drop-down (i.e. InitiatingCounterparty, Device etc.)

![](media/16ccf28f51908755e33a508376a2097d.png)

Create a Contract Instance
--------------------------

1.  In Blockchain Workbench application section, select “+ New Contract”

2.  The **New contract** pane is displayed. Specify the initial parameters
    values. Select **Create**

![](media/63d5500c9714d925fe120584612e15f9.png)

1.  The newly created contract is displayed in the list with the other active
    contracts.

![](media/58f9c54b444d153af2f0cbe75ed40b50.png)

##### Take Action on the Contract

1.  In Blockchain Workbench application section, select the application tile
    that contains the contract to take the action.

2.  Select the contract in the list.

>   Details about the contract are displayed in different sections.

![](media/c0deee5ff09bf1b9d60e3797db95af86.png)

1.  In the **Action** section, select **Take action**.

2.  The details about the current state of the contract are displayed in a pane.
    Choose the action you want to take in the drop-down.

![](media/629e79e50ea3619f2c523bfd91899bfd.png)

1.  Select **Take Action** to take the action.

##### Testing the Contract

To test the contract, sign out of the contract and sign in as the users you
assigned when creating it. You should now see the contract displayed for this
persona. Note that the actions available are different for these different
users.
