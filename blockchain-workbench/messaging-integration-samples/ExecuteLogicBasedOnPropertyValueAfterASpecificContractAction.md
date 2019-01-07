Execute Logic Based on a Specific Contract Property Value After a Specific Action Was Taken
===========================================================================================

Overview
--------

This logic app performs the following analysis on an incoming message, evaluates
the state of property and allows you to take appropriate action.

Specifically –

-   It identifies if the message is of type ContractInsertedOrUpdated

-   If true, it identifies if this is an update to an existing contract or a new
    contract

-   If an update, it identifies if the action executed was named
    “IngestTelemetry”

-   If true, it will cycle through the parameters in the message to find the
    parameter named “ComplianceStatus”

-   Once found, it evaluates the value of that property so that the appropriate
    action can be taken.

Of Note
-------

This sample is designed to work with the [RefrigeratedTransportation application
and associated smart
contract](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation)
but can be easily adapted to other contracts by making changes to the name of
the action (“IngestTelemetry”) or the logic related to identifying the parameter
(“ComplianceStatus”) and its associated conditional logic.

Other samples will further extend this to send alerts via Outlook, SMS (Twilio)
and voice (Twilio).

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

Select the Azure Active Directory Tenant that the Azure Blockchain Workbench was
deployed to in the drop-down list. Sign in with user credentials or connect
using a Service Principal. For this basic sample, you will sign in using your
credentials.

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

"WorkflowPropertuId": {

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

Click the “More” link and then select “add a condition”

Click in the box at the left of the condition. It will display the Dynamic
Content window, select “IsUpdate”.

Set the condition to “is equal to”

Set the condition value to true.

This identifies that this is an update to a contract and not the creation of a
new contract.

![](media/7a0a04127d71637f8b6595f7f90b24aa.png)

Click the three dots in the upper right corner of the condition action and
select Rename. Rename this action to “Check to See If This is a New Contract or
an Action”

In the “If true” section, click “More” and select “add a switch case”.

Click inside the “On” field and select “ActionName” from the Dynamic Content
window.

Click the three dots in the upper right of the action, select Rename, and name
it “Check to See What Action Was Executed”

![](media/76e6d0ebc407cbc8b90b3d8604b0bcde.png)

In the choice on the right, set the “Equals” field to IngestTelemetry.

Click the three dots in the upper right, select Rename and name this “Check to
see if this is the IngestTelemetry action from our device”

Click More” and select “add for each”

![](media/e77fbaa0dad3081ab19cb876b4c0db38.png)

In the “For each” action, click in the “Select an output from previews steps”
field and select Parameters from the Dynamic Content window.

Click “More” button and add a switch case.

Right click the three dots in the upper corner of the action and select Rename.
Rename this to “Determine what parameter this is”

![](media/3df8d7ffada6d4b0a1a66ccdd288fa4a.png)

For the item on the left of the switch case, set the Equals property to
ComplianceStuatus

Add a condition. For the left most item, click in the field and then select
Value from the Dynamic Content window.

Set “is equal to” in the center box.

Set the right most box to “false”

![](media/952b9528ab2e0dac4be03c272b72f857.png)

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
    Navigate to the logic app in the portal. At the bottom of the screen you
    will detail for Runs history

![](media/72adb726484a524d15aefc72f914a1c7.png)

1.  Click on the most recent execution of your logic app in the list.  
    This will show details on the trigger and actions executing within the logic
    app and allow you to validate success or troubleshoot reasons for failure.

![](media/1ff80b6294ee3520bea3a959dfe914c5.png)

1.  If you made a mistake or need to make changes to your logic, you can re-test
    your  
    changes easily.

-   After making changes in your logic app, navigate back to this same screen
    and click “Resubmit” and it will call the current version of your logic app
    with the values provided by the previous run.

### In Review

In many workflows, there is a need to execute logic after an action has occurred
and when a contract property is set to a specific value.

The logic app created in this sample facilitates this need by –

-   Identifying if the message is of type ContractInsertedOrUpdated

-   Confirming if this is an update to an existing contract

-   Confirming that the action executed was IngestTelemetry

-   Finding the value of the property named “ComplianceStatus”

-   Evaluating the value of that property so that the appropriate action can be
    taken.

This sample is designed to work with the [RefrigeratedTransportation application
and associated smart
contract](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation)
but can be easily adapted to other contracts by making changes to the name of
the action (“IngestTelemetry”) or the logic related to identifying the parameter
(“ComplianceStatus”) and its associated conditional logic.
