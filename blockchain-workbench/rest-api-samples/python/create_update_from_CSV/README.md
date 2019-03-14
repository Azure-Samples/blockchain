# Batch processing of contract creation & action with Python

## Overview

There are many blockchain use-cases where importing and acting on a large set of data into the blockchain ledger is required.  This code performs those activities with Python & the Python Requests package.  

To create and act on contracts, the code reads the CSV input, formats it into the correct JSON payload, then programmatically performs the API POST call.  


## Prerequisites

1. [Deployment of Azure Blockchain Workbench](https://docs.microsoft.com/en-us/azure/blockchain/workbench/deploy)
2. [HelloBlockchain](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples/hello-blockchain) app deployed in workbench
3. [Service principal / API user](https://medium.com/@malirezaie/how-to-enable-programmatic-interaction-with-azure-blockchain-workbench-apis-56c0d95c79c0)
4. API user needs permissions to perform the *Request* and *Respond* actions per the smart contract.  Use [this POSTMAN collection](https://medium.com/@malirezaie/how-to-enable-programmatic-interaction-with-azure-blockchain-workbench-apis-56c0d95c79c0) to update the role access
5. If using Jupyter, an Anaconda installation of Python & the Jupyter VS Code extension

## How to run

After deployment of workbench and the HelloBlockchain app, test the workflow through the GUI web app.  Using POSTMAN to test and prepare to adapt this code to your requirements.  

The code was writted with the Jupyter extension for VS Code.  To use Jupyter, press CMD + SHFT + P, select a python intepreter from anaconda


## Acknowledgements

David Havera, Mahdi Alirezaie & ali92hm

## Future Functionality:

1. Update contract data with specific criteria
2. Integration into Azure Function apps