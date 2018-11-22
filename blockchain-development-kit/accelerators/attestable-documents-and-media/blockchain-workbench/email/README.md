# Hash attachments and metadata and then store on chain when an email is received that contains attachments. 

## Overview

This logic app hashes attachment metadata and contents and then stores it on chain when an email is received that contains attachments. The app will use a connector to be notified when new emails are received.

Specifically –

- It will look for attachments in incoming emails to an Office 365 account
- Hashes the contents and metadata for any attachments
- Creates a new contract for each attachment containing the hashed data and then adds it to the Registry

## Pre-Requisites

The pre-requisites for this sample include -

- An Office 365 account
- An Azure Blockchain Workbench deployment

## Of Note

This sample is designed to work with the File Registry application and File contract.

## Create a Function App

This sample uses a Function App to hash the file contents. If you already have a Function App created, then you can skip to the next section.

Navigate to the Azure portal at <http://portal.azure.com>

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Function App and then click Create.

![Create Function App](./media/functionapp1.png)

Choose a name for the Function App, make sure .NET is chosen for the Runtime Stack and then click Create. This Function App will be used during the setup of the Logic App.

![Configure Function App](./media/functionapp2.png)

Once the app has been created, open it in the portal and click the + symbol next to Functions to add a new function.

![Add new function](./media/functionapp3.png)

Select "HTTP trigger" as the type of function to add.

![Choose HTTP trigger](./media/functionapp4.png)

Name the new function "GenericHashFunction".

![Name new function](./media/functionapp5.png)

Add the following code to the function. This function will return a SHA256 hash of the content that is passed to it.

```c
#r "Newtonsoft.Json"

using System.Net;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json;
using System.Security.Cryptography;
using System.Text;

public static async Task<IActionResult> Run(HttpRequest req, ILogger log)
{
    log.LogInformation("C# HTTP trigger function processed a request.");

    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();

    if (string.IsNullOrWhiteSpace(requestBody))
        return new BadRequestObjectResult("File content or metadata failed to be hashed.");

    string contentHash = ComputeSha256Hash(requestBody);
        
    return (ActionResult)new OkObjectResult(contentHash);
}

public static string ComputeSha256Hash(string rawData)  
{  
    // Create a SHA256   
    using (SHA256 sha256Hash = SHA256.Create())  
    {  
        // ComputeHash - returns byte array  
        byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(rawData));  

        // Convert byte array to a string   
        StringBuilder builder = new StringBuilder();  
        for (int i = 0; i < bytes.Length; i++)  
        {  
            builder.Append(bytes[i].ToString("x2"));  
        }  
        return builder.ToString();  
    }  
} 
```

![Add Code](./media/functionapp6.png)

Save the function. This will be used in our logic app.

## Create the Logic App

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Logic App and then click Create.

![Create Logic App](./media/logicapp1.png)

Name the logic app "EmailFileHashLogicApp".

Click the Create button.

![Configure Logic App](./media/logicapp2.png)

A logic app is initiated by a trigger.

In this scenario, the trigger will be when an email is received by the Office 365 account.

Within the Logic App Designer, select "Blank Logic App".

![Blank Logic App](./media/logicapp3.png)

Search for "Office 365 Outlook" and then select the "When a new email arrives" trigger.

![Add Email trigger](./media/logicapp4.png)

When prompted, log in with the Office 365 account to be used. Once completed,
click the Create button.

![Configure Email account](./media/logicapp5.png)

Once you have authenticated the user, select the folder that you want to check and set the frequency to check for updates. Next, click Show advanced options and set "Has Attachments" and "Include Attachments" to true. Once completed click + New Step.

![Select folder and frequency](./media/logicapp6.png)

## Initialzing the Variables

The contract that we will be using requires two pieces of generated data: a request Id and a process time. This will be created using a variable action. Search for "Variable" and select "Initialize variable"

![Set Variable](./media/variable1.png)

The name of the first variable is "RequestId" and the type will be "String". Leave the Value blank for now. This will be set later in our app.

![Request Id variable](./media/variable2.png)

Add a second variable whose name is "ProcessDateTime" and the type is "String". This variable will also remain blank for the time being.

![Process Date Time variable](./media/variable3.png)

## Process Each Attachment

Since an email can have multiple attachments, when need to use a Control action to allow us to process each attachment. Search "Control" and select "For each".
![Select for each control](./media/control1.png)

In the dynamic content window there will be an Attachments property. Add that to the "Select an output from previous steps" field. The rest of our workflow will be added as an action inside of the for each. Click the "Add an action" button to begin processing the attachments.

![Set attachments](./media/control2.png)

## Add Hashing Function

This next step will create hash values for the uploaded file's metadata and content. The first step is to create the hash for the metadata.

Search for Azure Function and then click "Choose an Azure function".

![Add Azure Function](./media/hashmetadata1.png)

Pick the Azure Function App you created at the start of this sample and then select "GenericHashFunction" that you created earlier.

![Create Azure Function](./media/hashmetadata2.png)

Once you select the function you will be prompted to add the request body that will be passed into the function. This will be a JSON object and should look like the following image.  Click the three dots at the top, select "Rename" and name this function "Hash File Metadata".

![Function Request Body](./media/hashmetadata3.png)

The properties above are dynamic content that Azure determines based on the apps and connectors used upstream.  You can see an example of the dynamic content available from the Outlook connector below.

![Dynamic Email Connector Values](./media/hashmetadata4.png)

Repeat the steps above to add a second call to the Azure Function.  This time we will hash the content of the attachment.  In the Request Body field, add the "Attachments Content" dynamic field. Click the three dots at the top, select "Rename" and name this function "Hash File Content".

![hashContent](./media/hashContent.png)

## Set the Variables

Since we need updated values for our variables for each attachment, we will update the value for both RequestId and ProcessDateTime. Search Variable and select "Set variable".

![Set Variable](./media/setvariable1.png)

Select "RequestId" to update the first variable. When you click on the Value field, the expression editor will appear. Select "Expression" and enter "guid()". This will generate a unique guid for our call to use.

![Request Id variable](./media/setvariable2.png)

Repeat the process and this time select "ProcessDateTime". The expression for this variable is "utcNow()".

![Process Date Time variable](./media/setvariable3.png)

## Sending the Service Bus Message

The last portion of the logic app will be to create and send a message to the Service Bus Queue for the Azure Blockchain Workbench. Search "Service Bus" and then select "Send Message".

![Select Send Message](./media/servicebus1.png)

Select the service bus you wish to connect to, either by selecting it from your current subscription or adding the connection string if it exists in a different location.

![Configure Service Bus](./media/servicebus2.png)

The Send Message dialog will allow you to configure the message. For the "Queue/Topic name" select the "activityhub". The Content will contain dynamic data that we have created along the process of the logic app. Enter the following JSON into the field:


```
{
    "requestId": "",
    "userChainIdentifier": "0x13ea6fbcaa5a5606f557ee1aeb5ed99f4c571ac6",
    "applicationName": "FileRegistry",
    "workflowName": "File",
    "parameters": [
        {
            "name": "registryAddress",
            "value": "0x7cfc428eda3ead7b35134a58a7930b56f9bd5b43"
        },
        { "name": "fileId", "value": "" },
        { "name": "location", "value": "" },
        { "name": "fileHash", "value": "" },
        { "name": "fileMetadataHash", "value": "" },
        { "name": "contentType", "value": "" },
        { "name": "etag", "value": "" },
        { "name": "processedDateTime", "value": "" }           
    ],
    "connectionId": 1,
    "messageSchemaVersion": "1.0.0",
    "messageName": "CreateContractRequest"
}
```

Add the dynamic fields from the various steps above and Variable sections as show here.  Remember that we hashed the metadata first and the content second so use the respective "Body" fields from those steps accordingly.

![Configure the message](./media/servicebus3.png)

Once this is complete. Click Save and your logic app is ready to be tested.

## Testing the logic app

You can test this functionality by taking the following steps –

1. Navigate to the overview page of for the logic app in the portal and confirm
   that it is enabled (if it is not, click on the “disabled” link at the top of
   the screen to transition the logic app to an enabled state).
2. Deploy the File Registry and File sample applications in Azure Blockchain
   Workbench.
3. Add members to the new application for the roles of Registrar, Registrant,
   and BlockchainAgent.
4. Create a new contract instance in the File Registry application. Once deployed, select the new Registry and perform the Open Registry action.
5. Send an email with an attachment to the account specified in the logic app.
6. The logic app should now be triggered and the code will be executed.
   Upon successful execution, you should see a File entry in your File workbench application.
   If you’d like to look at the execution of the logic app,
   navigate to the logic app in the portal. At the bottom of the screen, you
   will detail for Runs history

![a3ae47edda794d93a60f5e6235df0282](./media/a3ae47edda794d93a60f5e6235df0282.png)

7. Click on the most recent execution of your logic app in the list.  
   This will show details on the trigger and actions executing within the logic
   app and allow you to validate success or troubleshoot reasons for failure.

![3385af2235e54ab6a14d4bfa5a85d0fb](./media/runOutput.png)

8. Once making changes in your logic app, you can navigate back to this same
   screen and click “Resubmit” and it will call the current version of your
   logic app with the values provided by the previous run.

### In Review

This sample deployed and configured a logic app that will hash file content and metadata and deploy a smart contract in an Azure Blockchain Workbench application.

This sample is designed to work with the File Registry sample application and contracts but can be easily adapted to other contracts by making changes to Service Bus – Send a Message action at the end of the sample to reflect the specifics of the new contract.
