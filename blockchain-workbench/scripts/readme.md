.
# Scripts

## Introduction

This section includes scripts focused on deploying and managing your Azure Blockchain Workbench deployment.

The currently available scripts include

* [collectBlockchainWorkbenchTroubleshooting.ps1](https://github.com/Azure-Samples/blockchain-powershell/blob/master/collectBlockchainWorkbenchTroubleshooting.ps1) - This generates a summary and detailed logs for troubleshooting Azure Blockchain Workbench. See [Azure Blockchain Workbench troubleshooting]() for more details.

* [cloudShellPreDeploy.ps1](./deployment/cloudShellPreDeploy.ps1) - This script can be used to automate some of Azure Active Directory configuration tasks for an Azure Blockchain Workbench deployment.

## Prerequisites

If needed, install the Azure PowerShell module using the instructions found in the [Azure PowerShell guide](https://docs.microsoft.com/powershell/azureps-cmdlets-docs/), and then run `Login-AzureRmAccount` to create a connection with Azure. Also, you need to have an SSH public key named `id_rsa.pub` in the .ssh directory of your user profile.

## Contributing

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
