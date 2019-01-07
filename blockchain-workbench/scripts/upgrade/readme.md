# Azure Blockchain Workbench Upgrade



Overview
=================
An existing deployment of Azure Blockchain Workbench can be upgraded to the latest version.

This script automates the upgrade of your Azure Blockchain Workbench deployment. It can be easily invoked from latest PowerShell.

To learn what's new in this release, please check our [release notes](releasenotes.md).

Execution Instructions
=======================
1. Install PowerShell 6 for your operating system from [https://github.com/PowerShell/PowerShell](https://github.com/PowerShell/PowerShell)

![](./media/release140-1.png)


2. Run PowerShell 6 as administrator:

![](./media/release-140-2.png)


3. Enable running unsigned scripts in PowerShell:

```powershell
Set-ExecutionPolicy -ExecutionPolicy Unrestricted -Scope Process
```
(Note: command above needs to be run each time before running upgrade script.)
For more information about running scripts and setting execution policy, see about_Execution_Policies at https://go.microsoft.com/fwlink/?LinkID=135170

4. Install Azure PowerShell module:
```powershell
Install-Module -Name AzureRM.NetCore -AllowClobber
```
Select A (Yes to All) in the next prompt:

![](./media/release-140-3.png)


5. Sign in to your azure account:

```powershell
Login-AzureRmAccount
```
![](./media/release-140-4.png)

You may be asked to authenticate if you're not currently authenticated. In this case, you will be provided with a link and a code to Authenticate to Azure. Follow the instructions shows after running this command.

![](./media/upgrade-5.png)


6. Locate your Azure subscription ID, and the resource group name where you deployed Azure Blockchain Workbench.


7. Next, run the downloaded upgrade script by typing the following;

```powershell
# Download the upgrade script automatically. (Or you can download it from this repository manually)
cd; Invoke-WebRequest -Uri https://aka.ms/workbenchUpgradeScript -OutFile azureBlockchainWorkbenchUpgradeTov1_5_1.ps1


# Running the script
./azureBlockchainWorkbenchUpgradeTov1_5_1.ps1 -SubscriptionID <subscription_id> -ResourceGroupName <workbench-resource-group-name>

```

When the upgrade completes, you will see the following message:

```powershell
Azure Blockchain Workbench in Resource Group $ResourceGroupName was successfully updated to version 1.5.1.

Important: There are new AAD application registration requirements with 1.5.1 that are not performed by this upgrade process. Please visit https://aka.ms/workbenchAADUpgrade to perform the necessary updated.

```

> Note: The AAD Application Registration configuration has changed for Worbnech 1.5. This script does **not** automatically update your Application Registration. Please visit [AAD Upgrade Instructions](https://aka.ms/workbenchAADUpgrade) to update your AAD application.
