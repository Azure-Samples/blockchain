# Hash file contents and metadata and then store on chain when a file is uploaded to OneDrive for Business

## Overview

This logic app hashes file metadata and contents and then stores it on chain when a file is uploaded to OneDrive for Business.

Specifically –

- It hashes the File contents and metadata
- Creates a new contract containing the hashed data and then adds it to the File Registry application

## Pre-Requisites

This sample takes a dependency on a third-party service provided by OneDrive for Business.

The pre-requisites for this sample include -

- A OneDrive for Business account
- An Azure Blockchain Workbench deployment with a File Registry application deployed and the Registry opened

## Of Note

This sample is designed to work with the File Registry application and File contract.

## Create a Function App

Navigate to the Azure portal at <http://portal.azure.com>

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Function App and then click Create.

![000f1dfb113749838ab1ddb9f152576a](./media/000f1dfb113749838ab1ddb9f152576a.png)

Choose a name for the Function App, make sure .NET is chosen for the Runtime Stack and then click Create.

![7c8b389f6aba40ecb4c77c6bce46bbe6](./media/7c8b389f6aba40ecb4c77c6bce46bbe6.png)

Once your Function app is deployed, navigate to the resource.  In the Function App, click the + button next to the Functions entry to create a new Function.

![4fd0e3eee7ec4784a3fd07f172750204](./media/4fd0e3eee7ec4784a3fd07f172750204.png)

Select "In Portal" when asked to choose your development environment and then click Continue.

![3204085649a64677b6a8a1ccf9ed29f9](./media/3204085649a64677b6a8a1ccf9ed29f9.png)

Select "More Templates" and click Finish and view templates.

![5639787939cc44bd8420a3f0626890bf](./media/5639787939cc44bd8420a3f0626890bf.png)

Select "HTTP Trigger" or search for it if necessary.

![d4fd330c1fdd498a998221345c2cec9c](./media/d4fd330c1fdd498a998221345c2cec9c.png)

Name your function "GenericHashFunction" and then click Create.

![7784372871e44f31bb3db25ce6d29a08](./media/7784372871e44f31bb3db25ce6d29a08.png)

In the Function editor, replace the default code with the code below and then click Save.

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

![a1d9ea5e7f6d418bb7ce93f26033f09e](./media/a1d9ea5e7f6d418bb7ce93f26033f09e.png)

## Create the Logic App

Click the + symbol in the upper left corner of the screen to add a new resource.

Search for and select Logic App and then click Create.

![](./media/82ed233953daa1bf6971180cfd1c3379.png)

Name the logic app “OneDriveForBusinessHashFileAndCreateContractOnUpload”.

Specify the same resource group as your Azure Blockchain Workbench deployment.

Click the Create button.

![](./media/7f9bfaaebcf5a38fa305e958b5bbb538.png)

A logic app is initiated by a trigger.

In this scenario, the trigger will be when a file is uploaded to OneDrive for Business.

Within the Logic App Designer, select "When a new file is created on OneDrive".

![97fbe8a46b5242edb2339440d52de3e3](./media/97fbe8a46b5242edb2339440d52de3e3.png)

Within the Logic App Designer, click the Sign In button for the trigger that was added and sign in to your OneDrive for Business account.

![e957e11ec3044c68908c13baaa8ce2f1](./media/e957e11ec3044c68908c13baaa8ce2f1.png)

Once connected successfully, the trigger will prompt you for some details such as which folder to watch and how often to check for changes.  When you are done configuring your settings, click + New Step.

![2534af099bff4d698fb29198a9bd724b](./media/2534af099bff4d698fb29198a9bd724b.png)

Search for Azure Function and select "Choose an Azure function".  Then, select the "GenericHashFunction" that you created above.

![chooseAzureFunction](./media/chooseAzureFunction.png)

We will call this function twice, once for the content and again for the metadata.  File content is returned as an object containing the properties "\$content-type" and "\$content" so we'll need to pull out just the content value. For the first call, click the Request Body field and enter "@triggerBody().\$content". Click the three dots at the top, select "Rename" and name this function "Hash File Content".

![hashContent](./media/hashContent.png)

Repeat the steps above to add a second call to the Azure Function.  This time we will hash the metadata about the file.  The Request Body can be either a singular value or any custom JSON object.  For this second call, we will be using a custom JSON object.  Design the Request Body using the dynamic content popup so that it looks like the  following image. Click the three dots at the top, select "Rename" and name this function "Hash File Metadata".

![hashMetadata](./media/hashMetadata.png)

Click "+ New Step" and then search for "Variables" and select the "Initialize Variable" action.

![initializeVariable](./media/initializeVariable.png)

Name the variable "RequestId" and select String for the Type.  Click in the Value field and select "Expression" in the dynamic content popup.  Then type "guid()" in the function box and click OK.

![expression](./media/expression.png)

Repeat the process and this time enter "ProcessDateTime" as the name. The expression for this variable is "utcNow()".

![Process Date Time variable](./media/setvariable3.png)

Click "+ New Step" and then search for and select Service Bus.  Then choose the "Send Message" action.

![serviceBus](./media/serviceBus.png)

In the Send Message action, search for and select the "activityhub" queue that is in you Azure Blockchain Workbench resource group. Set the SessionId field to be the "RequestId" variable created above. Select the Content field and build the following structure using the dynamic content fields from the steps above.  If the outputs of a step aren't shown, click the "See more" link under that section.

![seeMore](./media/seeMore.png)

Paste this message structure in the Content field and then fill in the blank fields using the values output from the steps above, the address of the File Registry application, and your user chain Id from the Azure Blockchain Workbench.  Make sure to put the dynamic values between the quotes.  Remember, the content hash is the result for the first Azure Function call (the Body object) and the metadata hash is the result of the second Azure Function call.

```json
{
    "requestId": "",
    "userChainIdentifier": "<User chain address with BlockchainAgent role>",
    "applicationName": "FileRegistry",
    "workflowName": "File",
    "parameters": [
        {
            "name": "registryAddress",
            "value": "<File Registry contract address from Azure Blockchain Workbench>"
        },
        {
            "name": "fileId",
            "value": ""
        },
        {
            "name": "location",
            "value": ""
        },
        {
            "name": "fileHash",
            "value": ""
        },
        {
            "name": "fileMetadataHash",
            "value": ""
        },
        {
            "name": "contentType",
            "value": ""
        },
        {
            "name": "etag",
            "value": 
        },
        {
            "name": "processedDateTime",
            "value": ""
        }           
    ],
    "connectionId": 1,
    "messageSchemaVersion": "1.0.0",
    "messageName": "CreateContractRequest"
}
```

It should look similar to this when completed

![serviceBusMessage](./media/serviceBusMessage.png)

Save your Logic App

## Testing

You can test this functionality by taking the following steps –

1. Navigate to the overview page of for the logic app in the portal and confirm
   that it is enabled (if it is not, click on the “disabled” link at the top of
   the screen to transition the logic app to an enabled state).
2. Deploy the File Registry and File sample applications in Azure Blockchain
   Workbench.
3. Add members to the new application for the roles of Registrar, Registrant,
   and BlockchainAgent.
4. Create a new contract instance in the File Registry application. Once deployed, select the new Registry and perform the Open Registry action.
5. Upload an image file to your One Drive for Business account used in the logic app.
6. The logic app should now be triggered and the code will be executed.
   Upon successful execution, you should see a File entry in your File workbench application.
   If you’d like to look at the execution of the logic app,
   navigate to the logic app in the portal. At the bottom of the screen, you
   will detail for Runs history

![a3ae47edda794d93a60f5e6235df0282](./media/a3ae47edda794d93a60f5e6235df0282.png)

7. Click on the most recent execution of your logic app in the list.  
   This will show details on the trigger and actions executing within the logic
   app and allow you to validate success or troubleshoot reasons for failure.

![3385af2235e54ab6a14d4bfa5a85d0fb](./media/3385af2235e54ab6a14d4bfa5a85d0fb.png)

8. Once making changes in your logic app, you can navigate back to this same
   screen and click “Resubmit” and it will call the current version of your
   logic app with the values provided by the previous run.

### In Review

This sample deployed and configured a logic app that will hash file content and metadata and deploy a smart contract in an Azure Blockchain Workbench application.

This sample is designed to work with the File Registry sample application and contracts but can be easily adapted to other contracts by making changes to Service Bus – Send a Message action at the end of the sample to reflect the specifics of the new contract.
