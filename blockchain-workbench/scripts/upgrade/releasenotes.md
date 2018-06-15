# Azure Blockchain Workbench Release Notes - Version 1.1.0

Multi Workflow and Contract Support 
=================
You may have noticed that our [Blockchain Workbench configuration documentation](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-configuration-overview) and [Workbench API](https://docs.microsoft.com/en-us/rest/api/azure-blockchain-workbench/) reference the ability to have multiple workflows within one application. Our initial release didn’t provide the UI to showcase more than one workflow per application, but we've now added this feature. With 1.1.0, you can now have multiple workflows for a single application show in the Workbench UI.  

In addition to this UI update, we have published a new Bazaar Marketplace sample application on our [Workbench Github](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-workbench/application-and-smart-contract-samples) showcasing the use of multiple workflows and smart contracts. Try it out and let us know what you think.  

Monitoring Improvements 
=================
Reference the [Blockchain Workbench architecture document](https://docs.microsoft.com/en-us/azure/blockchain-workbench/blockchain-workbench-architecture).  

The Workbench DLT watcher monitors events occurring on the attached blockchain network. If something goes wrong with this service, Workbench will no longer be able to process and understand transactions going through the blockchain. In that state, the UI looks like this: 

![](media/releasenotes110-1.png)

With 1.1.0, we’ve improved the reliability of the watcher, which means if there is a disruption with the service, Workbench can recover and process all transactions missed during the disruption.  

Usability and Polish 
=================

We made several improvements to the overall usability of Workbench. New items will now show a “new” icon, which will make it easier for you to see new contracts, actions, members, etc.  

![](media/releasenotes110-2.png)
 
Another improvement relates to how you find people when assigning roles or assignments within contracts. We now have a richer search algorithm, which will make it easier to find people when you only have a partial name.  

Improvements and Bug Fixes  
=================
We also made several other improvements to Workbench. Some of the top bugs we addressed are: 
 * Workbench is now able to support Azure Active Directories of any size.  
 * All users who have the right to create contracts can create a contract, even if the user is not an administrator.  
 * Deployment of Workbench is more reliable as we’ve addressed the top failure cases. 
 * Database views have been fixed to return the right set of data to address a bug where a few columns did not display the correct information in a couple of views.
   
Please use our [Blockchain User Voice](https://aka.ms/blockchainuservoice) to provide feedback and suggest features/ideas for Workbench. Your input is helping make this a great service.  We look forward to hearing from you.  