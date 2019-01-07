Execute Logic Based on Events from Azure Blockchain Workbench
=============================================================

Overview
--------

This sample provides step by step instructions for setting up a logic app that
connects to the Event Grid exposed by Azure Blockchain workbench, identifies
which messages are being sent, and facilitates writing code to perform logic
when certain events occurs.

The sample includes responding to events exposed during public preview and
includes parsing logic with the schemas for each of the associated message
types. Using the dynamic properties dialog within logic apps, this allows you to
easily reference properties of those messages by name when adding logic.

Of Note
-------

This is a baseline sample that responds to all events delivered for all smart
contract implementations deployed for your workflows in Azure Blockchain
Workbench.

Other samples showcase how to extend this to take action based on the state of a
contract (generally), the state of the contract after a specific action on the
contract was taken, or the value of a property after a specific action was
taken.

It may also be desired to send an alert based on data found within an event.
There are samples that show how to deliver alerts using email (Outlook), sms
(Twilio), and voice (Twilio).

Create the Logic App
--------------------

Navigate to the Azure Portal at http://portal.azure.com

![](media/82ed233953daa1bf6971180cfd1c3379.png)

Click the + symbol in the upper left corner of the screen to add a new resource

Search for and select Logic App and then click Create.

![](media/7f9bfaaebcf5a38fa305e958b5bbb538.png)

A logic app is initiated by a trigger. In this scenario, the trigger will be an
event from Azure Blockchain Workbench delivered via the Event Grid.

Within the Logic App Designer select the trigger “When an Event Grid event
occurs”

![](media/1be166636ff6c58073a0d31d7bd22ea2.png)

Within the Logic App Designer, click the Sign In button.

![](media/d8dab9c287a5e74b4fa98f60be4aa846.png)

Select the Azure Active Directory Tenant that the Event Grid was deployed to in
the drop-down list and either sign in or connect using a Service Principal. For
this basic sample, you will sign in using your credentials.

Clicking Sign In will display a login dialog.

![](media/e066010a74494ade806912e9946472b9.png)

Once connected successfully, the trigger will show a check mark in a green
circle as demonstrated below.

![](media/debbcf931f5b445471dd52bcd20a0fc0.png)

Click the Continue button.

Next select the subscription for the Azure Blockchain Workbench, specify a
Resource Type of “Microsoft.EventGrid.Topics” and the resource name for the
Event Grid topic in the resource group for the Azure Blockchain Workbench
deployment.

![](media/f491275d3e072d2ca5affa55e51d0b41.png)

Click New Step

Click More and click the Add a Switch Case

For the Switch, there is an “On” configuration that identifies what value will
be reviewed.

Click the text box and then select “Subject” which contains the name of the
message type being delivered.

![](media/25c2f52eb62db1733a41a9166242dc31.png)

In the Case message on the right, enter the value of ApplicationIngestion

Click the “…” in the upper right of the case and select Rename.

Rename the case to ApplicationIngestion

For the action, select “Data Operations – Parse Json”

![](media/af962ad2163a852d6bea49f14df12c4a.png)

In the Content field select Body.

![](media/4d4ecefee414592598f583ee35844a2e.png)

In the Schema field, enter the following –

{

"properties": {

"data": {

"properties": {

"EventName": {

"type": "string"

},

"ApplicationId": {

"type": "number"

},

"ApplicationName": {

"type": "string"

},

"ApplicationDisplayName": {

"type": "string"

},

"ApplicationVersion": {

"type": "string"

},

"ApplicationDefinitionLocation": {

"type": "string"

},

"ContractCodes": {

"items": {

"properties": {

"Id": {

"type": "number"

},
"LedgerId": {

"type": "number"

},

"Location": {

"type": "string"

},

},

"required": [

"Id",

"LedgerId",

"Location"

],

"type": "object"

},

"type": "array"

},

"ApplicationRoles": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Name": {

"type": "string"

},

},

"required": [

"Id",

"Name"

],

"type": "object"

},

"type": "array"

},

"ApplicationWorkflows": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Name": {

"type": "string"

},

"DisplayName": {

"type": "string"

},

"Functions": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Name": {

"type": "string"

},

"Parameters": {

"items": {

"properties": {

"Name": {

"type": "string"

},

"Type": {

"properties": {

"Name": {

"type": "string"

}

},

"required": [

"Name"

],

"type": "object"

},

},

"required": [

"Name",

"Type"

],

"type": "object"

},

"type": "array"

},

},

"required": [

"Id",

"Name",

"Parameters"

],

"type": "object"

},

"type": "array"

},

"States": {

"items": {

"properties": {

"Name": {

"type": "string"

},

"DisplayName": {

"type": "string"

},

"Style": {

"type": "string"

}

},

"required": [

"Name",

"DisplayName",

"Style"

],

"type": "object"

},

"type": "array"

},

"Properties": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Name": {

"type": "string"

},

"Type": {

"properties": {

"Name": {

"type": "string"

},

}

"required": [

"Name"

],

"type": "object"

}

},

"required": [

"Id",

"Name",

"Type"

],

"type": "object"

},

"type": "array"

},

},

"required": [

"Id",

"Name",

"DisplayName",

"Functions",

"States",

"Properties"

],

"type": "object"

},

"type": "array"

},

"ConnectionId": {

"type": "number"

},

"MessageSchemaVersion": {

"type": "string"

},

"MessageName": {

"type": "string"

}

},

"type": "object"

},

},

"type": "object"

}

Click on the + button in the middle of the screen to create the case for the
next message.

In the Case message on the right, enter the value of
RoleAssignment

Click the “…” in the upper right of the case and select Rename.

Rename the case to RoleAssignment

For the action, select “Data Operations – Parse Json”

In the Content field select Body.

![](media/ab7a67ca33ed73b566a22386fb1d4284.png)

In the Schema field, enter the following –

{

"properties": {

"data": {

"properties": {

"EventName": {

"type": "string"

},

"ApplicationId": {

"type": "number"

},

"ApplicationName": {

"type": "string"

},

"ApplicationDisplayName": {

"type": "string"

},

"ApplicationVersion": {

"type": "string"

},

"ApplicationRole": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Name": {

"type": "string"

}

},

"required": [

"Id",

"Name"

],

"type": "object"

},

"Assigner": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Type": {

"type": "string"

},

"ChainIdentifier": {

"type": "string"

},

},

"required": [

"Id",

"Type",

"ChainIdentifier"

],

"type": "object"

},

"Assignee": {

"items": {

"properties": {

"Id": {

"type": "number"

},

"Type": {

"type": "string"

},

"ChainIdentifier": {

"type": "string"

},

},

"required": [

"Id",

"Type",

"ChainIdentifier"

],

"type": "object"

},

"ConnectionId": {

"type": "number"

},

"MessageSchemaVersion": {

"type": "string"

},

"MessageName": {

"type": "string"

}

},

"type": "object"

},

},

"type": "object"

}

Click on the + button in the middle of the screen to create the case for the
next message.

In the Case message on the right, enter the value of ContractMessage

Click the “…” in the upper right of the case and select Rename.

Rename the case to ContractMessage

For the action, select “Data Operations – Parse Json”

In the Content field select Body.

![](media/cba74aaab2a729b0d9bc70b1542fc124.png)

In the Schema field, enter the following –

{

"properties": {

"data": {

"properties": {

"BlockId": {

"type": "number"

},
"BlockHash": {

"type": "string"

},

"ModifyingTransactions": {

"items": {

"properties": {

"TransactionId": {

"type": "number"

},
"TransactionHash": {

"type": "string"

},
"From": {

"type": "string"

},
"To": {

"type": "string"

},

},

"required": [

"TransactionId",

"TransactionHash",

"From",

"To"

],

"type": "object"

},

"type": "array"

},

"ContractId": {

"type": "number"

},

"ContractLedgerIdentifier": {

"type": "string"

},

"ContractProperties": {

"items": {

"properties": {

"WorkflowPropertyId": {

"type": "number"

},
"Name": {

"type": "string"

},
"Value": {

"type": "string"

}

},

"required": [

"WorkflowPropertyId",

"Name",

"Value"

],

"type": "object"

},

"type": "array"

},

"IsNewContract": {

"type": "boolean"

},

"ConnectionId": {

"type": "number"

},

"MessageSchemaVersion": {

"type": "string"

},

"MessageName": {

"type": "string"

}

},

"type": "object"

},

},

"type": "object"

}

Click on the + button in the middle of the screen to create the case for the
next message.

In the Case message on the right, enter the value of BlockMessage

Click the “…” in the upper right of the case and select Rename.

Rename the case to BlockMessage

For the action, select “Data Operations – Parse Json”

In the Content field select Body from the Dynamic Content window.

![](media/7a6344b21dd293a29a352d96aa8b4834.png)

In the Schema field, enter the following –

{

"properties": {

"data": {

"properties": {

"Block": {

"items": {

"properties": {

"BlockId": {

"type": "number"

},

"BlockNumber": {

"type": "number"

},

"BlockHash": {

"type": "string"

},

"PreviousBlockHash": {

"type": "string"

},

"BlockTimestamp": {

"type": "string"

}

},

"required": [

"BlockId",

"BlockNumber",

"BlockHash"

],

"type": "object"

},

"Transactions": {

"items": {

"properties": {

"TransactionId": {

"type": "number"

},
"TransactionHash": {

"type": "string"

},
"From": {

"type": "string"

},
"To": {

"type": "string"

},

"ProvisioningStatus": {

"type": "number"

},

},

"required": [

"TransactionId",

"TransactionHash",

"From",

"To"

],

"type": "object"

},

"type": "array"

},

"ConnectionId": {

"type": "number"

},

"MessageSchemaVersion": {

"type": "string"

},

"MessageName": {

"type": "string"

}

},

"type": "object"

},

},

"type": "object"

}

Testing
-------

You can test this functionality by taking the following steps –

1.  Navigate to the overview page of for the logic app in the portal and confirm
    that it is enabled (if it is not, click on the “disabled” link at the top of
    the screen to transition the logic app to an enabled state).

2.  Deploy any application in Azure Blockchain Workbench.

3.  Create a contract for the specified application.

4.  The logic app should now be triggered and the code will be executed.
    Navigate to the logic app in the portal. At the bottom of the screen you
    will detail for Runs history.

![](media/72adb726484a524d15aefc72f914a1c7.png)

1.  Click on the most recent execution of your logic app in the list. This will
    show details on the trigger and actions executing within the logic app and
    allow you to validate success or troubleshoot reasons for failure.  
      
    Note – there will likely be multiple events in this list as certain events,
    such as InsertBlock, are happening regularly. If the most recent run does
    not reflect the creation of your contract, click on other runs and it will
    be displayed.

2.  If you find and error and want to re-test, navigate back to the logic app
    page in the portal, select a prior run, and click “Resubmit” This will call
    the current version of your logic app with the values provided by the
    previous run.

### In Review

In many real world scenarios, there is a need to execute logic based on events
emitted from the blockchain and Azure Blockchain Workbench.

The logic app created in this sample facilitates this need by –

-   Identifies the event that occurred.

-   Parses the event into a message that reflects this, making properties of the
    message available for use in your custom logic as Dynamic Content.
