# Accessing Workbench's API in Javascript (Node.js)


## Overview
This is a sample code written in Javascript (Node.js) that demonstrates how to obtain a bearer token and call Workbench's API programmatically. 

This sample makes a `GET` call to `/api/v1/users` and prints the list of users in a Workbench instance.

## Prerequisite for Workbench Authentication
To access Workbench's API programmatically you need to first create a service principal with certain configuration. Here is a guide on how to [create a service principal for Workbench](../../scripts/workbench-serviceprincipal).

## Execution Instructions

### Getting the code
You need to clone this repo

```
git clone https://github.com/Azure-Samples/blockchain.git
cd blockchain/blockchain-workbench/auth-samples/nodejs
```


### Replacing the parameters
Before running the code you need to replace some variables in `indes.js` file.

```
const AUTHORITY = 'https://login.microsoftonline.com/<tenant_name>';
const WORKBENCH_API_URL = '<Workbench API URL>';
const RESOURCE = '<Workbench AppId>';
const CLIENT_APP_Id = '<service principal AppId>';
const CLIENT_SECRET = '<service principal secret>';
```


### Running using Docker
To run the sample using Docker you need to have [Docker](https://www.docker.com/products/docker-desktop) installed.

```
# Building the image
docker build -t nodeSample .

# Running the image
docker run nodeSample
```

You should see a list of Workbench users if the code exits successfully. 


### Running natively
To run this sample natively you need to have [Node v10.15](https://nodejs.org/en/) (or above) installed 

```
# Installing dependencies
npm i

# Running the sample
npm start
```

You should see a list of Workbench users if the code exits successfully. 
