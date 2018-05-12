Trigger Twilio SMS Alert Based on Contract State After a Specific Contract Action
=================================================================================

Overview
--------

This logic app performs the following analysis on an incoming message, evaluates
the state of the contract and triggers an SMS alert using Twilio.

Specifically –

-   It identifies if the message is of type ContractInsertedOrUpdated

-   If true, it identifies if this is an update to an existing contract or a new
    contract

-   If an update, it identifies if the action executed was named
    “IngestTelemetry”

-   If true, it will cycle through the parameters in the message to find the
    parameter named “State”

-   Once found, it evaluates the value of the current contract state.

-   It then triggers an SMS Alert using the Twilio service

Pre-Requisites
--------------

This sample takes a dependency on Sharepoint Online.

The pre-requisites for this sample include -

-   A SharePoint team site

-   A list on the team site named “Potential Food Safety Issues” that has two
    fields, Title and Date Reported.

Of Note
-------

This sample is designed to work with the [RefrigeratedTransportation
application](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation)
but can be easily adapted to other contracts by making changes to the name of
the action (“IngestTelemetry”) or the logic related to identifying the states.

Other samples will further extend this to send alerts via Outlook, SMS (Twilio)
and voice (Twilio).

Create the Logic App
--------------------

Navigate to the Azure Portal at <http://portal.azure.com>

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Logic App and then click Create.

![](media/82ed233953daa1bf6971180cfd1c3379.png)

Name the logic app “ExecuteBasedOnStateAfterSpecificAction”.

Specify the same resource group as your Azure Blockchain Workbench deployment.

Click the Create button.

![](media/7f9bfaaebcf5a38fa305e958b5bbb538.png)

A logic app is initiated by a trigger.

In this scenario, the trigger will be an event from Azure Blockchain Workbench
delivered via the Event Grid.

Within the Logic App Designer select the trigger “When an Event Grid event
occurs”.

![](media/1be166636ff6c58073a0d31d7bd22ea2.png)

Within the Logic App Designer, click the Sign In button for the action that was
just added.

![](media/d8dab9c287a5e74b4fa98f60be4aa846.png)

Select the Azure Active Directory Tenant that the Azure Blockchain Workbench was
deployed to in the drop-down list and either sign in using your credentials or
connect using a Service Principal.

For this basic sample, you will sign in using your credentials.

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

Click the “+ New Step” button.

Click More and click the Add a Switch Case

For the Switch, there is an “On” field that identifies what value will be
evaluated.

Click the text box and then select “Subject” which contains the name of the
message type being delivered.

In the Case message on the right, enter the value of ContractInsertedOrUpdated.

Click the “…” in the upper right of the case and select Rename.

Rename the case to ContractInsertedOrUpdated.

Add the action “Data Operations – Parse Json” to the case.

In the Content field select Body.

In the Schema field, enter the following –

{

"properties": {

"data": {

"properties": {

"ActionName": {

"type": "string"

},

"BlockId": {

"type": "number"

},

"ChainId": {

"type": "number"

},

"ContractAddress": {

"type": "string"

},

"ContractId": {

"type": "number"

},

"IsTopLevelUpdate": {

"type": "boolean"

},

"IsUpdate": {

"type": "boolean"

},

"OperationName": {

"type": "string"

},

"OriginatingAddress": {

"type": "string"

},

"Parameters": {

"items": {

"properties": {

"Name": {

"type": "string"

},

"Value": {

"type": "string"

}

},

"required": [

"Name",

"Value"

],

"type": "object"

},

"type": "array"

},

"TopLevelInputParams": {

"type": "array"

},

"TransactionHash": {

"type": "string"

}

},

"type": "object"

},

"dataVersion": {

"type": "string"

},

"eventTime": {

"type": "string"

},

"eventType": {

"type": "string"

},

"id": {

"type": "string"

},

"metadataVersion": {

"type": "string"

},

"subject": {

"type": "string"

},

"topic": {

"type": "string"

}

},

"type": "object"

}

![](media/3bf2658a688788436dd74b524744b07b.png)

Click the “More” link and then select “add a condition”.

Click in the box at the left of the condition. It will display the Dynamic
Content window, select “IsUpdate” from the Dynamic Content list.

Set the condition to “is equal to”.

Set the condition value to true.

This identifies that this is an update to a contract and not the creation of a
new contract.

![](media/7a0a04127d71637f8b6595f7f90b24aa.png)

Click the three dots in the upper right corner of the Condition action and
select Rename. Rename this action to “Check to See If This is a New Contract or
an Action”

In the “If true” section, click More and select “add a switch case”.

Click inside the “On” field and select “ActionName” from the dynamic properties
dialog.

Click the three dots in the upper right of the action, select Rename, and name
it “Check to See What Action Was Executed”

![](media/76e6d0ebc407cbc8b90b3d8604b0bcde.png)

In the choice on the right, set the “Equals” field to IngestTelemetry.

Click the three dots in the upper right, select Rename and name this “Check to
see if this is the IngestTelemetry action from our device”

Click “… More” and select “Add for each”

![](media/e77fbaa0dad3081ab19cb876b4c0db38.png)

In the “For each” action, click in the “Select an output from previous steps”
field and select Parameters from the Dynamic Properties dialog.

Next click on the “More” button and select “add a switch case”.

Right click the three dots in the upper corner of the action and select Rename.
Rename this to “Determine what parameter this is”

![](media/3df8d7ffada6d4b0a1a66ccdd288fa4a.png)

Click on the newly created item on the left, and set the “Equals” field value to
State

Click on the three dots in the upper right of the action and select Rename.
Rename this to “Confirm this is the State parameter”

![](media/b64096448afefddf736fb15e90f57f81.png)

Underneath that action, click the “More” link and then “add new switch case”.

In the “On” field, click it select Value from the dynamic properties.

For the item on the left of the switch case, set the Equals property to Created

Click on the three dots in the corner of this case and select Rename. Rename it
to “In the Created state”

Click the (+) in the center of the switch case to add a new case.

For the new case, set the Equals property to InTransit

Click on the three dots in the corner of this case and select Rename. Rename it
to “In the Intransit state”

Click the (+) in the center of the switch case to add a new case.

For the new case, set the Equals property to Completed

Click on the three dots in the corner of this case and select Rename. Rename it
to “In the Completed state”

Click the (+) in the center of the switch case to add a new case.

For the new case, set the Equals property to OutOfCompliance

Click on the three dots in the corner of this case and select Rename. Rename it
to “In the OutOfCompliance state”

![](media/624bdc6f1051fdf5291364b54e33aa71.png)

You can now add logic that takes action based on the state after a specific
action. In this sample, if after a device provides telemetry that it is now in
an out of compliance state there may be a desire to send an alert.

In the case for the “In the Accepted state”, click “Add an action”

Select SharePoint – Create Item

Click Sign in to create a connection to Sharepoint

![](media/841c6f4e56971029e344a9eb12ef2b2d.png)

This will display a login dialog for Office 365

Populate the site address and list name to add the item to.

Populate the title for the list item and use utcNow() to capture the current
time to include in the SharePoint list.

Note – You can use Dynamic Content in any of these values, allowing for dynamic
alerts specific to the context of any contract.

![](media/80ff863bcbaf1cd66fbd6429dfed0761.png)

Save your Logic App

Testing
-------

You can test this functionality by taking the following steps –

1.  Navigate to the overview page of for the logic app in the portal and confirm
    that it is enabled (if it is not, click on the “disabled” link at the top of
    the screen to transition the logic app to an enabled state).

2.  Deploy the RefrigeratedTransportation sample application in Azure Blockchain
    Workbench.

3.  Add members to the new application for the roles of Owner, Counterparty,
    Device, and Observer.

4.  Create a new contract instance. Specify values of 1 and 15 for both the
    minimum and maximum values for temperature and humidity.

5.  Transfer responsibility to a party in the Counterparty role.

6.  Log out of Workbench and log in as the account associated with the Device

7.  Navigate to the contract you just created within Workbench.

8.  Take the action of “IngestTelemetry” and enter values of 50 for both
    temperature and humidity.

9.  The logic app should now be triggered and the code will be executed.

>   Navigate to the list in SharePoint to confirm it has been successfully
>   added.

>   If you’d like to look at the execution of the logic app to send the alert,
>   navigate to the logic app in the portal. At the bottom of the screen you
>   will detail for Runs history

![](media/72adb726484a524d15aefc72f914a1c7.png)

1.  Click on the most recent execution of your logic app in the list.  
    This will show details on the trigger and actions executing within the logic
    app and allow you to validate success or troubleshoot reasons for failure.

![](media/1ff80b6294ee3520bea3a959dfe914c5.png)

1.  Once making changes in your logic app, you can navigate back to this same
    screen and click “Resubmit” and it will call the current version of your
    logic app with the values provided by the previous run.

### In Review

In many workflows, there is a need to execute logic based on a state change that
may occur after a specific action is taken on a smart contract.

The logic app created in this sample facilitates this need by –

-   Identifying if the message is of type ContractInsertedOrUpdated

-   If true, it identifies if this is an update to an existing contract or a new
    contract

-   If the message represents a contract update, it identifies if the action
    executed was named “IngestTelemetry”

-   If that is true, it will cycle through the parameters in the message to find
    the parameter named “State”

-   Once found, it evaluates the value of the current contract state and
    triggers an alert when appropriate.
