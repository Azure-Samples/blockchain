# Azure Blockchain Workbench Pre-Deployment Script



Overview
=================
Azure Blockchain Workbench utilizes Azure Active Directory, which must be configured prior to deployment. 

The [documentation](http://aka.ms/workbenchdocs/) provides details on the steps required to do this configuration via the Azure Portal.

This script automates several of those steps and deliver the information necessary to complete the second tab of the Azure Blockchain Workbench template deployment.

Note - This script is deployed using the Cloud Shell available via the Azure Portal. It must be run in the same subscription that you will be deploying Azure Blockchain Workbench to. Cloud Shell does require access to a storage account. If you do not have access to a storage account with the subscription you've chosen, you should follow the deployment instructions in the [documentation](http://aka.ms/workbenchdocs/)

Execution Instructions
=======================
