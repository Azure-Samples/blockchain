Store Blockchain Workbench messages in Cosmos DB 
=================================================

Overview
--------

This sample shows how you can store blockchain workbench messages in Cosmos DB
using an Azure function with an Event Grid trigger and a Cosmos DB output
binding.

Provision a new Cosmos DB account
---------------------------------

If not present already, provision a new Cosmos DB account.

![](media/29eb123bb3d6c527127a82597ad9569b.png)

Create and configure a new Function App
=======================================

1.  If not already present, create a new function app :

    ![](media/3f09e843716df586fa78f923da898cf7.png)

2.  Use the template to create a function with the EventGrid Trigger.

    ![](media/d20878cde659502b57581214f73c52d3.png)

3.  Choose C\# as the programming language and give an appropriate name. Once
    the function is created, go to the integrate tab to add an output trigger to
    CosmosDB.

    For the Cosmos DB account connection, choose the account created in the
    above section. Make sure you check the flag to create a Cosmos DB database
    and collection.

    ![](media/84c43d8d0f72beb3d81d62f0e6bbf81e.png)

4.  In the function body, paste the following snippet :

    ``` csharp
    public static void Run(JObject eventGridEvent, TraceWriter log, out object document)
    {
        document = null;
        String operationName = eventGridEvent["data"]["OperationName"].ToString();
        switch(operationName)
        {
            case "AccountCreated":
            case "ContractInsertedOrUpdated":
            case "UpdateUserBalance":
            case "InsertBlock":
            case "InsertTransaction":
                document = eventGridEvent["data"];
                log.Info("Storing " + operationName + " transaction in Cosmos DB")
                break;
            }
        }
    ```

5.  Add the Event Grid subscription by clicking on the link next to the Run
    button. Choose the topic type as “Event Grid Topics” and select the event
    grid from your workbench deployment to listen to the messages.

    ![](media/8a48916f27bcffc5af742e115266e863.png)

6.  Once the event grid subscription is added successfully, you can run the
    Azure function.

7.  Validate that you are seeing the messages in Cosmos DB . You can run queries
    on top of this to filter or project what you need.

    ![](media/228e454c01c11507fca9adcb7f69ba22.png)
