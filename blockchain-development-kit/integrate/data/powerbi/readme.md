Using Azure Blockchain Workbench Data with Power BI
===================================================

Microsoft’s PowerBI provides the ability to easily generate powerful reports
from SQL DB databases using Power BI Desktop and then publish them to
<http://www.powerbi.com>.

This section contains a step by step walkthrough of how to connect to Azure
Blockchain Workbench’s SQL Database from within PowerBI desktop, create a report
and deploy that report to <http://www.powerbi.com>.


Downloading PowerBI Desktop
---------------------------

Download PowerBI desktop by clicking on the “Get” button at
<https://aka.ms/pbidesktopstore>

Configuring Firewall Rules
--------------------------
In the Azure Portal (https://portal.azure.com), navigate to the resource group for your Azure Blockchain Workbench deployment.
Follow the instructions located at https://docs.microsoft.com/en-us/azure/sql-database/sql-database-firewall-configure to configure the firewall rules to enable access the database from your current IP address.

Note - not doing this will result in an error when attempting to connect your client PC to the ABW SQL instance from PowerBI

Connecting PowerBI to data in Azure Blockchain Workbench
--------------------------------------------------------

Once installed, open the application and click on the “Get Data” button.

![](media/fb339d0e122afa2d125a1d52caa297cf.png)

Select SQL Server from the list of data source types

Provide the server and database name in the dialog shown below. Specify
whether you want to import the data or perform a DirectQuery then click the
Ok button.

![](media/2297aa08bcc4356a4e946567deb337f0.png)

You will now provide the database credentials to access Azure Blockchain
Workbench. Click on the “Database” link on the side of the dialog and enter
your credentials.

If you are using the credentials created by the Azure Blockchain Workbench
deployment process, the username will be dbadmin and the password will be
the one you provided during deployment.

![](media/70d45b123a4858a05a5a4e3aa2147833.png)

Once connected to the database, the Navigator dialog will display the tables
and views available within the database. The views are designed for
reporting and are all prefixed ‘vw’.

![](media/334fc7167e9a17205faebbd57c2a1d8a.png)

Select the views you wish to include. For demonstration purposes we will
include vwContractAction which will provide details on all of the actions
that have taken place on a contract.

![](media/3cd76fcdf68bfd84a6004c2cf81a4444.png)

You can now create and publish reports as you normally would with PowerBI.
