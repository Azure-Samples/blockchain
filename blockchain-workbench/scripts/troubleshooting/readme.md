
Azure Blockchain Workbench Troubleshooting Script
=================================================

Overview
---------

This PowerShell script collects logs and metrics from an Azure Blockchain Workbench instance for
troubleshooting.

Description
------------

Collects logs and metrics from an Azure Blockchain Workbench instance for
troubleshooting. This script will prompt for Azure authentication if you
are not already logged in.

> NOTE: If you don't have the latest Azure Powershell installed on your machine, we recommend that you use the MSI installer to get the latest at https://github.com/Azure/azure-powershell/releases. This script currently doest not work on Azure Cloud shell.

PARAMETER SubscriptionID
------------------------
SubscriptionID to create or locate all resources.

PARAMETER ResourceGroupName
---------------------------
Name of the Azure Resource Group where Blockchain Workbench has been deployed to.

PARAMETER OutputDirectory
-------------------------
Path to create the output .ZIP file. If not specified, defaults to the current
directory

PARAMETER LookbackHours
-----------------------
Number of hours to use when pulling telemetry. Default to 24 hours. Maximum
value is 90 hours.

PARAMETER OmsSubscriptionId
---------------------------
The subscription id where OMS is deployed. Only pass this parameter if the OMS
for the blockchain network is deployed outside of Blockchain Workbench's resource
group.

PARAMETER OmsResourceGroup
--------------------------
The resource group where OMS is deployed. Only pass this parameter if the OMS
for the blockchain network is deployed outside of Blockchain Workbench's resource
group.

PARAMETER OmsWorkspaceName
--------------------------
The OMS workspace name. Only pass this parameter if the OMS for the blockchain
network is deployed outside of Blockchain Workbench's resource group.

INPUTS
=======
None. You cannot pipe objects to this script.

OUTPUTS
=======
None. This script does not generate any output. It creates a .ZIP file which 
contains a summary file with a "top errors" report, last timestamp 
information for each Workbench microservice, and recommended actions.
Following that, it creates two subfolders, "details" and "metrics" for futher
troubleshooting.

EXAMPLE
--------
``` powershell
C:\tmp> .\collectBlockchainWorkbenchTroubleshooting.ps1 -SubscriptionID "<subscription_id>" -ResourceGroupName "<workbench-resource-group-name>"
```