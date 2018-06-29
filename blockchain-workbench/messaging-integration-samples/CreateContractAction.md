Create a Smart Contract Action in Azure Blockchain Workbench via Messaging 
========================================================================

Overview
--------

This sample provides step by step instructions for using message to create a
contract action on an existing contract on the blockchain.

Deploy the Stored Procedures
----------------------------

Download the file with the stored procedures to support messaging integration.
They are in a single file that can be downloaded from [this location](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-workbench/messaging-integration-samples/SQL/LogicAppStoredProcedures.sql)

Open a web browser and navigate to the Azure portal at
<http://portal.azure.com>

Navigate to the database for your Azure Blockchain Workbench deployment.

Select the query editor

![](media/0bea9bd62adadaef87c5e913eb72edb4.png)

Click Login and provide your database credentials. The username will be
‘dbadmin’ and the password is the one your provided during the installation of
Azure Blockchain Workbench.

![](media/7ad55e3793ecb76b2e3a55b5306feacc.png)

Click Load Query and select the stored procedure file you downloaded earlier.

![](media/09ada66b5aca0afc98253d42ba3791aa.png)

Click the run button to create the stored procedures in the database.

![](media/fe516699cba1eded2a122f4d45563d98.png)

Create the Logic App
--------------------

![](media/82ed233953daa1bf6971180cfd1c3379.png)

Click the + symbol in the upper left corner of the screen to add a new resource

Search for and select Logic App and then click Create.

When asked to select a template, select blank template.

![](media/7f9bfaaebcf5a38fa305e958b5bbb538.png)

A logic app is initiated by a trigger. Depending on your scenario this trigger
may be a message arriving via service bus, event grid, HTTP, or another
mechanism.

For this walk through, select Recurrence. Recurrence occurs at a regular
interval. It’s selected because you can easily test the trigger without writing
additional code.

![](media/e9eb985cbf4ef55ff95c1675f184cf15.png)

Click “New Step” and then select “New Action”

Select the SQL Connector and then select the “Execute Stored Procedure” action.

![](media/86d9cff5ef3e8f9a6b135777522e4dcb.png)

Select the appropriate Azure Workbench SQL DB server from the list.

![](media/964e7c061e4bfe77f60646f35ed52760.png)

Next, select the database for your Azure Blockchain Workbench deployment and
enter your database credentials.

![](media/f2d6160b0808057be9009f2dcb095d9f.png)

Select the stored procedure named “LogicAppGetContractCreationDetails”

Provide the name of the application and workflow to be created, for example
“AssetTransfer”, “AssetTransfer.” Also provide the email address for the user on
whose behalf this transaction will be sent.

Note – the name of the application and contract is the “Name” from the
configuration file. It is not the “DisplayName”

![](media/ef7bc4d025f9bfa747b75123cac778f1.png)

Click “New Step” and select “New Action”

Select the Variable connector and then select the “Initialize Variable” action.

![](media/8de7e521679a4e6e0d3009c657a97066.png)

Name the field “RequestId”

Select the type of “String”

Provide a value of “guid()”

![](media/4ca67ffb0e784381e78cbb8bda26cdc2.png)

Click “New Step” and select “New Action”

Select the Service Bus connector and then select the “Send Message” action.

![](media/420924cad452e61c78855ac8edc48102.png)

![](media/f4679f0e5391e5792fd4f790645c0f82.png)

Select “activityhub”

In the Session Id field, select RequestId from the Dynamic content dialog

In the content field, enter the below –

Using the dynamic content dialog to insert the values that were generated from
the stored procedures. The result should resemble the image below.

![](media/dee0ee075a67cda7900fbe16a7489618.png)

Click Save

Click Run

![](media/70e528c75e320b794260fa6044709795.png)

If you check Azure Blockchain Workbench you will see that a new contract action has
been executed.
