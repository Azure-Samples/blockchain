# Accessing Workbench's API in Python

## Overview
This is a sample code written in Python that demonstrates how to obtain a bearer token and call Workbench's API programmatically. 

This sample makes a `GET` call to `/api/v1/users` and prints the list of users in a Workbench instance.

## Prerequisite for Workbench Authentication
To access Workbench's API programmatically you need to first create a service principal with certain configuration. Here is a guide on how to [create a service principal for Workbench](../../scripts/workbench-serviceprincipal).

## Execution Instructions

### Getting the code
You need to clone this repo

```
git clone https://github.com/Azure-Samples/blockchain.git
cd blockchain/blockchain-workbench/auth-samples/python
```


### Replacing the parameters
Before running the code you need to replace some variables in `app.py` file.

```
AUTHORITY = 'https://login.microsoftonline.com/<tenant_name>'
WORKBENCH_API_URL = '<Workbench API URL>'
RESOURCE = '<Workbench AppId>'
CLIENT_APP_Id = '<service principal AppId>'
CLIENT_SECRET = '<service principal secret>'
```


### Running using Docker
To run the sample using Docker you need to have [Docker](https://www.docker.com/products/docker-desktop) installed.

```
# Building the image
docker build -t pythonSample .

# Running the image
docker run pythonSample
```

You should see a list of Workbench users if the code exits successfully. 


### Running natively
To run this sample natively you need to have [Python](https://www.python.org/downloads/) (works with both 2.x and 3.x versions) installed with [pip](https://pip.pypa.io/en/stable/installing/).

```
# Installing dependencies
pip install -r requirements.txt

# Running the sample
python app.py
```

You should see a list of Workbench users if the code exits successfully. 
