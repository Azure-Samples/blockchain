Using Azure Search with Azure Blockchain Workbench
==================================================

Overview
--------
A common pattern we've seen from customers is an interest to utilize search with blockchain data.  We're seeing it used to power some pretty innovative user experiences that require flexible searching over fast growing data sets.  The use of facets specifically is of keen interest. If unfamilair with facets, addition detail with UX samples can be found [here](https://docs.microsoft.com/en-us/azure/search/search-faceted-navigation)

Because a blockchain is immutable, data such as PII is not appropriate to place in it. The pattern has search being used on a mix of data from a blockchain and "off chain" data that augments it. 

Azure Blockchain Workbench already provides an database that merges "on chain" data with an "off chain" model, so it's only a simple matter of getting the data from the SQL DB provided by workbench and into Azure Search.

The good news is that this capability is already built into SQL DB and in just a few minutes you can add this search capability.

Implementation
---------------
Azure Blockchain Workbench already provides a rich set of "wide table" database views that were designed for reporting. These work great with Azure Search.  

Step 1 is to identify which of the views is appropriate for your search scenario(s).  You can find the full list of database views [here](https://docs.microsoft.com/en-us/azure/blockchain/workbench/database-views)

Step 2 is to create an Azure Search Service via the Azure Portal.

Step 3 is to go to the database in your Azure Blockchain Workbench Deployment and click the "Add Azure Search" button

Simply follow the prompts where you'll select your view, the fields you want searchable and retrievable, and the index will be created.

Of Note - 
When defining your index, be thoughtful about what key is appropriate. This should provide a unique ID for the document. Because the views in Azure Blockchain Workbench are "wide", they have multiple IDs. For example, for vwContractAction the default key selected in ApplicationId where it should be ContractActionId.

Additional information on data types can be found [here](https://docs.microsoft.com/en-us/azure/search/search-what-is-an-index).

Using the REST API
------------------
If you'd prefer to set up a SQL Indexer using the REST API, the process to do this is highlighted [here](https://docs.microsoft.com/en-us/azure/search/search-howto-connecting-azure-sql-database-to-azure-search-using-indexers)

Azure Search + Power BI
-----------------------
Azure Search can also populate PowerBI.  If you'd like to utilize that connectivity, use the simple to follow instructions available [here](https://docs.microsoft.com/en-us/power-bi/service-connect-to-azure-search)

