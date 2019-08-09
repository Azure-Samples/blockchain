# Azure Blockchain Workbench .NET SDK

This project contains a .NET standard Client SDK for Azure Blockchain Workbench. 

You can interact with a Workbench instance via this SDK.

## Features
1. Polly.NET (https://github.com/App-vNext/Polly) a .NET resiliency library with exponential retry backing 
    - Currently, the library is used to handle TaskCancelledExceptions or timeouts from the server. 
2. Singleton Implementation - just include the library in your project and reference it from anywhere 
3. Thread-safe: only a single HttpClient is used
4. Event Handlers you can subscribe to: 
    - For expired access tokens (so you can refresh and get a new one)
    - When Exceptions are thrown 
5. Exception handling - JsonException, TaskCancelledException, HttpRequestException  
6. Generic GET and POST methods with C# Generics

## Getting Started

### 1. Setting Optional Parameters for Timeout and RetryCount

There are two parameters that can be set on the Gateway API: CLIENT_API_TIMEOUT and POLLY_RETRY_COUNT. 
The first one is for setting the default timeout interval of the HttpClient. The second is the number of times an API call will retry after at TaskCancelledException before giving up. 

### 2. Obtaining an Authentication Token via ADAL

Using the .NET ADAL library for Xamarin.iOS, Xamarin.Android, or .NET - authenticate with the Active Directory and retrieve then authentication token. 

Workbench doesn't require an API Key. It knows who the request is coming from by simply including the authentication token from ADAL in the authorization request header. 

You can take a look at the Azure Function sample included in this repository for how to get an authentication token as a service principal and use it with Workbench. 

To create a service principal, you can use the service principal script located at https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/scripts/workbench-serviceprincipal or follow the step by step guide here: https://medium.com/@malirezaie/how-to-enable-programmatic-interaction-with-azure-blockchain-workbench-apis-56c0d95c79c0

For Xamarin Apps (Xamarin.Forms, Xamarin.iOS and Xamarin.Android) have a look at the Xamarin sample in the Blockchain Development Kit: https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/connect/mobile/blockchain-workbench/workbench-client

### 3. Setting the Authentication Token on the API and using the static instance:
Once you obtain the authentication token, use the following method to set it on the Gateway API instance:

```var BASE_URL = "https://mahdi-m3yy5x-api.azurewebsites.net"```

```GatewayApi.SiteUrl =  BASE_URL;```

```GatewayApi.Instance.SetAuthToken(<yourTokenAsString>)```

### 4. Using the Gateway API
Refer to the swagger documentation of the Workbench API on getting started. Method names will be similar to the api documentation. You can also use the postman collection located here: 

https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/rest-api-samples/postman

As an example, to get a list of all applications by ID and to see if the current user can access Workbench: 

```var canUserAccessWorkbench = await GatewayApi.Instance.CanCurrentUserCreateContractsForWorkflow(<The_workflow_Id>);```

```var listOfApplications = await GatewayApi.Instance.GetApplicationsAsync();```


