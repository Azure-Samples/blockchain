# Azure Blockchain Workbench Pre-Deployment Script



Overview
=================
Azure Blockchain Workbench utilizes Azure Active Directory, which must be configured prior to deployment. 

The [documentation](http://aka.ms/workbenchdocs/) provides details on the steps required to do this configuration via the Azure Portal.

This script automates several of those steps and deliver the information necessary to complete the second tab of the Azure Blockchain Workbench template deployment.

Note - This script is deployed using the Cloud Shell avialable via the Azure Portal. It must be run in the same subscription that you will be deploying Azure Blockchain Workbench to. Cloud Shell does require access to a storage account. If you do not have access to a storage account with the subscription you've chosen, you should follow the deployment instructions in the [documentation](http://aka.ms/workbenchdocs/)

Execution Instructions
=======================
If you haven't already download the [script](.\cloudShellPreDeploy.ps1)

Log in to the Azure Portal

![](media/addomains.PNG)

Navigate to your Azure Active Directory.

Select Custom Domains and capture the domain you will use with Azure Blockchain Workbench.

Now you're ready to run the script.

![](media/142ce1c9daec7fefec1b179c59449788.png)

Click on the Cloud Shell Icon in the upper right of the screen.

This is the Cloud Shell Icon  

![](media/7bf771f6aa15cbe01ad9c8611b500af0.png)

![](media/cf60a0141d2459b59081e2e9b7c41ebb.png)



This will launch the Cloud Shell within the browser. You’ll be asked to select
Bash (Linux) or PowerShell (Windows). Click the “PowerShell (Windows)” link.

Note – Windows refers to the type of operating system and version that
PowerShell that is being used in Cloud Shell. You can use this version
regardless of the operating system you’re using the shell from, e.g. MacOs, etc.

![](media/0d74cac397b00074c0bef5c9226ae592.png)

This will launch the Cloud Shell. This can take up to 60 seconds to deploy.

![](media/7ae894a6c4022756d3339e50fb4480dd.png)

Click on the Upload button at the top of the PowerShell button

![](media/19b4b3fea6ffdd03c1d86af7e88921b4.png)

Select the file cloudShelPreDeploy.ps1 and click ok.

In the Cloud Shell, navigate to your cloud drive by typing the following -

cd \$home\\clouddrive

![](media/cfe3892d0d0f2272f76304f4522c8a19.png)

Next, run the script by typing the following –

.\\cloudShellPreDeploy.ps1

You will be prompted to enter the domain for your Azure Active Directory and confirming you wish to execute the script.

The script will then create and configure the Azure Active Direcotry application.

When the script completes, it will provide the Active Directory data needed to populate the Azure Active Direcotry tabof the Azure Blockchain Workbench Deployment.

