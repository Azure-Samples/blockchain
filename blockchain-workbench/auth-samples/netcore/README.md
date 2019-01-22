# Accessing Workbench's API in C# (.NET Core)

## Overview
This is a sample code written in C# (.NET Core) that demonstrates how to obtain a bearer token and call Workbench's API programmatically. 

This sample makes a `GET` call to `/api/v1/users` and prints the list of users in a Workbench instance.

## Prerequisite for Workbench Authentication
To access Workbench's API programmatically you need to first create a service principal with certain configuration. Here is a guide on how to [create a service principal for Workbench](../../scripts/workbench-serviceprincipal).

## Execution Instructions

### Getting the code
You need to clone this repo

```
git clone https://github.com/Azure-Samples/blockchain.git
cd blockchain/blockchain-workbench/auth-samples/netcore
```


### Replacing the parameters
Before running the code you need to replace some variables in `Program.cs` file.

```
public static readonly string AUTHORITY = "https://login.microsoftonline.com/<tenant_name>";
public static readonly string WORKBENCH_API_URL = "<Workbench API URL>";
public static readonly string RESOURCE = "<Workbench AppId>";
public static readonly string CLIENT_APP_Id = "<service principal AppId>";
public static readonly string CLIENT_SECRET = "<service principal secret>";
```


### Running using Docker
To run the sample using Docker you need to have [Docker](https://www.docker.com/products/docker-desktop) installed.

```
# Building the image
docker build -t dotnetSample .

# Running the image
docker run dotnetSample
```

You should see a list of Workbench users if the code exits successfully. 


### Running natively
To run this sample natively you need to have installed [.NET Core 2.2](https://dotnet.microsoft.com/download/dotnet-core/2.2)

```
# Running the sample
dotnet run
```

You should see a list of Workbench users if the code exits successfully. 
