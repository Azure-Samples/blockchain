# Hash file contents and metadata and then store on chain when a path is shared from a Google Drive account

## Overview

This logic app hashes file metadata and contents and then stores it on chain when a file is shared from a Google Drive account. The app will be triggered by a HTTP call containing the path of the file to process.

Specifically –

- It will retrieve a file path via an HTTP call
- It obtains the file and metadata from Google Drive
- Hashes the file contents and some metadata about the file
- Creates a new contract containing the hashed data and then adds it to the Registry

## Pre-Requisites

The pre-requisites for this sample include -

- A Google Drive account
- An Azure Blockchain Workbench deployment with a File Registry application deployed and the Registry opened
- [Postman](http://getpostman.com) for testing the logic app once completed

## Of Note

This sample is designed to work with the File Registry application and File contract.

## Create a Function App

This sample uses a Function App to hash the file contents. If you already have a Function App created, then you can skip to the next section.

Navigate to the Azure portal at <http://portal.azure.com>

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Function App and then click Create.

![Create Function App](.\media\functionapp1.png)

Choose a name for the Function App, make sure .NET is chosen for the Runtime Stack and then click Create. This Function App will be used during the setup of the Logic App.

![Configure Function App](.\media\functionapp2.png)

Once the app has been created, open it in the portal and click the + symbol next to Functions to add a new function.

![Add new function](.\media\functionapp3.png)

Select "HTTP trigger" as the type of function to add.

![Choose HTTP trigger](.\media\functionapp4.png)

Name the new function "GenericHashFunction".

![Name new function](.\media\functionapp5.png)

Add the following code to the function. This function will return a SHA256 hash of the content that is passed to it.

```c#
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

![Add Code](.\media\functionapp6.png)

Save the function. This will be used in our logic app.

## Create the Logic App

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Logic App and then click Create.

![Create Logic App](./media/logicapp1.png)

Name the logic app "GoogleFileHashLogicApp".

Click the Create button.

![Configure Logic App](./media/logicapp2.png)

A logic app is initiated by a trigger.

In this scenario, the trigger will be when a HTTP request is made containing the file path in Google Drive to add.

Within the Logic App Designer, select "Blank Logic App".

![Blank Logic App](.\media\logicapp3.png)

Search for Request and then select the "When a HTTP request is received" trigger.

![Add HTTP received trigger](.\media\logicapp4.png)

The HTTP Post URL will be available after the logic app is saved and will be used later for testing. In the "Request Body JSON Schema" section add the following JSON. This will allow the logic app to parse out the required data.

```javascript
{
    "properties": {
        "filepath": {
            "type": "string"
        }
    },
    "type": "object"
}
```
![Configure HTTP receiver](.\media\logicapp5.png)

## Add Google Drive Connectors

This sample will connect to Google Drive to obtain the content and metadata of the file to process. In the first step we will get the file content.

Add a new step and search for Google Drive. Then select "Get file content using path".

![Add Google Drive connector](.\media\logicapp6.png)

When prompted sign into the Google Drive account to be used in this exampe.

![Sign in to Google Drive](.\media\logicapp7.png)

The File path will be a dynamic property returned from the previous step. Clicking the field will open the dynamic property window. Select "filepath".

![Configure path](.\media\logicapp8a.png)

Next, add a new step and once again search Google Drive. This time select "Get file metadata using path". 

![Select folder and frequency](.\media\logicapp9.png)

The File path property for this step will be the same as the previous one.

![Configure path](.\media\logicapp10.png)

## Add Hashing Functions

This next step will create hash values for the shared file's metadata and content. The first step is to create the hash for the metadata.

Search for Azure Function and then click "Choose an Azure function".

![Add Azure Function](.\media\hashmetadata1.png)

Pick the Azure Function App you created at the start of this sample and then select "GenericHashFunction" that you created earlier.

![Create Azure Function](.\media\hashmetadata2.png)

Once you select the function you will be prompted to add the request body that will be passed into the function. This will be a JSON object and should look like the following image. Click the three dots at the top, select "Rename" and name this function "Hash File Metadata".

![Function Request Body](.\media\hashmetadata3.png)

Like in the previous steps, this data contains properties that are dynamic content that Azure determines based on the apps and connectors used upstream.  You can see an example of the dynamic content available from the Google Drive connector below.

![Dynamic Google Drive Connector Values](.\media\hashmetadata4.png)

Repeat the steps above to add a second call to the Azure Function.  This time we will hash the content of the file. The Google Drive connector that retrieves content returns an object that includes extra metadata so we'll need to strip out the content. In the Request Body type "@body('Get_file_content_using_path')['$content']". Click the three dots at the top, select "Rename" and name this function "Hash File Content".

![hashContent](.\media\hashContent.png)

## Initializing the Variables

The contract that we will be using requires two pieces of generated data: a request Id and a process time. This will be created using a variable action. Search for "Variable" and select "Initialize variable"

![Set Variable](.\media\variable1.png)

The name of the first variable is "RequestId" and the type will be "String". When you click on the Value field, the expression editor will appear. Select "Expression" and enter "guid()". This will generate a unique guid for our call to use.

![Request Id variable](.\media\variable2.png)

Add a second variable whose name is "ProcessDateTime" and the type is "String". The expression for this variable is "utcNow()".

![Process Date Time variable](.\media\variable3.png)

## Sending the Service Bus Message

The last portion of the logic app will be to create and send a message to the Service Bus Queue for the Azure Blockchain Workbench. Search "Service Bus" and then select "Send Message".

![Select Send Message](.\media\servicebus1.png)

Select the service bus you wish to connect to, either by selecting one from your current subscription or adding the connection string if it exists in a different location.

![Configure Service Bus](.\media\servicebus2.png)

The Send Message dialog will allow you to configure the message. The Content will contain dynamic data that we have created along the process of the logic app. Enter the following Json into the field:

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
        { "name": "etag", "value":  },
        { "name": "processedDateTime", "value": "" }           
    ],
    "connectionId": 1,
    "messageSchemaVersion": "1.0.0",
    "messageName": "CreateContractRequest"
}
```

In the Send Message action, search for and select the queue that is in you Azure Blockchain Workbench resource group. Set the SessionId field to be the "RequestId" variable created above. Select the Content field and build the following structure using the dynamic content fields from the steps above. It should look similar to this when completed.  Remember, the metadata hash is the result for the first Azure Function call (the Body object) and the content hash is the result of the second Azure Function call.

![Configure the message](.\media\servicebus3.png)

Once this is complete. Click Save and your logic app is ready to be tested. After saving, make sure to return to the receive HTTP request step and copy the "HTTP Post Url". 

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
5. Upload an image file to your Google Drive account used in the logic app. Make sure to record the path and name of file.
6. Open Postman and create a new request. The request should be set to POST and the url is the HTPP Post Url obtained from the logic app. For the Body of the request, select Raw and JSON as the type. Add the following as the Body content using the path to the file you want to process.

```
{
    "filepath" : "/filepath/to/use.txt"
}
```

![Configure Postman](./media/test3.png)

7. Click Send to submit the request to the logic app.

8. The logic app should now be triggered and the code will be executed.
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