Using Azure Search in blockchain scenarios
==================================================

Overview
--------
A common pattern we've seen from customers is an interest to utilize search with blockchain data. Because a blockchain is immutable, data such as PII is not appropriate to place in it. The pattern has search being used on a mix of data from a blockchain and "off chain" data that augments it. 

In these samples, we show two different patterns of how to integrate Azure Search capabilities into a blockchain scenario.

Scenario 1: Using Azure Search with [Azure Blockchain Workbench](./WorkbenchAzureSearch.md). In this scenario we show how to integrate Azure search into the existing SQL database tables used in workbench

Scenario 2: Creating an Azure Search index, and placing information into the Search index from a blockchain. In this scenario we show how to use the [Ethereum Logic App](./EthereumLogicAppAzureSearch.md) to place searchable smart contract information into the Azure Search service.

