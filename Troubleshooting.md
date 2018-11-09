# Troubleshooting Guide

This guide will give you a starting point for how to troubleshoot your issues. This is especially helpful if you've seen an error message on your Workbench instance that directed you to this Github repo.

## Common Questions
##### Q: I'm seeing the following message: <i>"The action failed. Please check your input or code and try again. Your administrator can use RequestId <request id> to look up details in Application Insights."</i> What could be wrong?

A: <INSERT HERE>

##### Q: I'm seeing the following message: <i>"This may take some time. Your administrator can use RequestId <request id> to look up details in Application Insights."</i> What could be wrong?

A: Follow the instructions [here](#logs) to take a look at the logs to understand what could be going on.

##### Q: I'm seeing the following message: <i>"There are no events identified from this action. Please update your code to add an event for this action and try again."</i> What could be wrong?

A: <INSERT HERE>

## <a name="logs"> Looking at logs </a>
Sometimes the error messages give you a RequestId to look at the logs. We have a new setup called [Workbooks](https://docs.microsoft.com/en-us/azure/application-insights/app-insights-usage-workbooks) that can show you the necessary logs given the RequestId, among other potential bugs. Following the instructions below to get to our Workbook.

### Instructions to get to the Workbook
1. Go to your Workbench deployment and navigate to your resource groups. Click on the 'Application Insights' resource.
![Alt text](/Screenshots/Instructions_Resource_Group_List.png?raw=true "Instructions Resource Group List")

2. Once you're in the 'Application Insights' resource, navigate to 'Workbooks' on the left pane. Here you will see pre-canned Workbook templates made available for you. Find the 'Workbench Troubleshooting' Workbook under 'Azure Blockchain' and click on it.
![Alt text](/Screenshots/Instructions_Application_Insights_Workbooks.png?raw=true "Instructions Workbooks list")

3. This Workbook template gives you the ability to query for logs per RequestId out of the box. In addition, like a Microsoft Word pre-canned template, you can customize it in however way you like. If you are done using the Workbook and do not care to save it, then just exit the page. If you would like to save this specific instance of the Workbook for future use, go ahead and click on the 'Save button' in the top bar.
![Alt text](/Screenshots/Instructions_Application_Insights_Blockchain_Workbook_Instance.png?raw=true "Instructions Blockchain Workbook Instance")

4. If you do decide to save the Workbook, you will find in the Workbook list page that your saved 'Workbench Troubleshooting' Workbook instance is in green at the top, for easy access.
![Alt text](/Screenshots/Instructions_Application_Insights_Workbooks_Saved.png?raw=true "Instructions Workbooks Instance Saved")
