---
topic: sample
products:
  - azure
  - azure-blockchain	
---

![Azure Blockchain Devkit MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)
# Azure Blockchain Development Kit

This repository contains content and samples in number of areas, including:

-   [Connect](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/connect) - Connect various data producers and consumers to or from the blockchain
-   [Integrate](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/integrate) - Integrate legacy tools, systems and protocols
-   [Accelerators](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators) - Deep dive into End-to-End examples, or solutions to common patterns.
-   [DevOps for smart contracts](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/devops) - Bring traditional DevOps practices into a distributed application environment

To learn more about Azure Blockchain Workbench, please visit our [product page](https://aka.ms/workbenchdocs) and [documentation](http://azure.microsoft.com/en-us/features/blockchain-workbench).


## Contents

| File/folder       | Description                                                  |
| ----------------- | ------------------------------------------------------------ |
| `accelerators`    | Samples showing common end-to-end application patterns and business scenarios |
| `connect`         | Samples showing how to produce or consume information, sending to and reading from, the blockchain through outside sources |
| `devops`          | A series of patterns and whitepapers of how to integrate traditional software devops into a multi-party, distributed, application environments |
| integrate         | Samples showing how to connect to traditional enterprise systems such as SQL databases, FTP, storage, or cloud file and email services |
| `.gitignore`      | Define what to ignore at commit time                         |
| `CHANGELOG.md`    | List of changes to the sample                                |
| `CONTRIBUTING.md` | Guidelines for contributing to the Azure Blockchain Devkit   |
| `README.md`       | This README file                                             |
| `LICENSE`         | The license for the Azure Development Kit samples            |
## Prerequisites
- Where noted, some of the samples in this development kit may need the following

  - Azure Blockchain Workbench
- Samples using the Ethereum Logic App connector available on the Azure Marketplace require an Ethereum network with a public RPC endpoint

  - If you wish to use the Azure Blockchain Workbench with the Ethereum Logic App connectors you will need a public RPC endpoint. You may use an existing one, or create a new one. 

    - You may create a new endpoint in Azure [here](https://portal.azure.com/?pub_source=email&pub_status=success#create/microsoft-azure-blockchain.azure-blockchain-ethereumethereum-poa-consortium)

    - Once your endpoint is ready, copy the RPC address from the deployment output and deploy Azure Blockchain Workbench to your subscription. In the Azure deployment blade, enter the RPC endpoint in the blade as shown below

      ![](C:\blockchain\blockchain\blockchain-development-kit\media\wbdeployment.PNG.jpg)

## Feedback
For general product feedback, please visit our [forum](https://techcommunity.microsoft.com/t5/Blockchain/bd-p/AzureBlockchain&data=02).

To request additional features or samples, visit our [UserVoice site](https://feedback.azure.com/forums/586780-blockchain&data=02).
