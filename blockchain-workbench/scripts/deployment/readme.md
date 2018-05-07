Login to the Azure Portal

Note – the deployment of the script will use Cloud Shell in the Azure Portal.
Cloud Shell utilizes a storage account to run.

If you have access to multiple Azure Active Directories, click your name in the
upper right and select an Azure Directory associated with an account where you
have permissions to create a storage.

Once the deployment is complete, you can delete this storage account if you do
not use Cloud Shell.

![](media/142ce1c9daec7fefec1b179c59449788.png)

Click on the Cloud Shell Icon in the upper right of the screen.

This is the Cloud Shell Icon  


![](media/7bf771f6aa15cbe01ad9c8611b500af0.png)

![](media/cf60a0141d2459b59081e2e9b7c41ebb.png)

Click on the Cloud Shell Icon in the upper right

![](media/7bf771f6aa15cbe01ad9c8611b500af0.png)

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
