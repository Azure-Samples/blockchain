# Troubleshooting Guide

This guide will give you a starting point for how to troubleshoot your issues. This is especially helpful if you've seen an error message on your Workbench instance that directed you to this Github repo.

## Common Questions
##### Q: I'm seeing the following message: <i>"The action failed. Please check your input or code and try again. Your administrator can use RequestId <request id> to look up details in Application Insights."</i> What could be wrong?

A: This error indicates a failure to submit the contract to the blockchain, including both the creation of the contract or taking an action on a contract. One common cause is an invalid input to the solidity function, such as supplying an int when the parameter specifies an array. Another could be that the function is hitting a revert or an assert, causing a gas estimation failure. In this case, please check the logic of your solidity contract file. 

In any case, please follow the instructions [here](#logs) to take a look at the logs to understand what's going on.

##### Q: I'm seeing the following message: <i>"This may take some time. Your administrator can use RequestId <request id> to look up details in Application Insights."</i> What could be wrong?

A: This error generally means that some component is taking a long time to process, whether that is the underlying blockchain or a processing failure. Causes can vary - please follow the instructions [here](#logs) to take a look at the logs to understand what's going on.

##### Q: I'm seeing the following message: <i>"There are no events identified from this action. Please update your code to add an event for this action and try again."</i> What could be wrong?

A: This error is shown when the transaction was submitted to the blockchain but hit an exception (such as a revert/assert). This occurs when the state of the world had changed in between the time gas was estimated and when the transaction was actually mined on the blockchain. The recommendation is to submit again, understanding the possibility that one may not be able to submit the same contract action again due to a different world state.

## <a name="logs"> Looking at logs </a>
Sometimes the error messages give you a RequestId to look at the logs. We have a new setup called [Workbooks](https://docs.microsoft.com/en-us/azure/application-insights/app-insights-usage-workbooks) that can show you the necessary logs given the RequestId, among other potential bugs. Following the instructions below to get to our Workbook.

### Instructions to get to the Workbook
1. Go to your Workbench deployment and navigate to your resource groups. Click on the 'Application Insights' resource.
![Alt text](media/Instructions_Resource_Group_List.png?raw=true "Instructions Resource Group List")

2. Once you're in the 'Application Insights' resource, navigate to 'Workbooks' on the left pane. Here you will see pre-canned Workbook templates made available for you. Find the 'Workbench Troubleshooting' Workbook under 'Azure Blockchain' and click on it.
![Alt text](media/Instructions_Application_Insights_Workbooks.png?raw=true "Instructions Workbooks list")

3. This Workbook template gives you the ability to query for logs per RequestId out of the box. In addition, like a Microsoft Word pre-canned template, you can customize it in however way you like. If you are done using the Workbook and do not care to save it, then just exit the page. If you would like to save this specific instance of the Workbook for future use, go ahead and click on the 'Save button' in the top bar.
![Alt text](media/Instructions_Application_Insights_Blockchain_Workbook_Instance.png?raw=true "Instructions Blockchain Workbook Instance")

4. If you do decide to save the Workbook, you will find in the Workbook list page that your saved 'Workbench Troubleshooting' Workbook instance is in green at the top, for easy access.
![Alt text](media/Instructions_Application_Insights_Workbooks_Saved.png?raw=true "Instructions Workbooks Instance Saved")

### How to read the logs
Logs are in chronological order, where the latest logs are at the top. Here are what the columns mean:

| Column Name   | Meaning                                                                           |
|---------------|-----------------------------------------------------------------------------------|
| LogLevel      | This refers to the level of the log. For now we display 'Information' and 'Error' |
| created       | The time when the log was created                                                 |
| Message       | The log message                                                                   |
| ServiceName   | Which service/component in the system fired the log                               |
| SourceContext | The context in which the action occurred (ex. Host refers to the Workbench code)  |
| Method        | The method that was being executed                                                |
| Exception     | Exception, if there was any                                                       |

If there are any errors, you'll see a log with the LogLevel 'Error', like below:
![Alt text](media/Instructions_Application_Insights_Error_Example.png?raw=true "Instructions Workbooks Error Log")

You can view the [list of common errors](#errors) to understand the error message better.

### <a name="errors"> Common errors seen in logs </a>
##### "Error constructing transaction"
This error is seen if there is something incorrect with the transaction. For example, the parameter input type may be mismatched (ex. array instead of an int). Please view the exception column for the specific error.

##### "Error while estimating gas. Exception Transaction execution error"
This error is usually seen when the function has hit a revert/assert given the current state and input parameters. Please check your contract logic.

##### "Error while estimating gas. Exception Invalid params: Invalid hex: Invalid character '_' at position ..."
This error comes from the usage of [libraries](https://solidity.readthedocs.io/en/v0.4.21/contracts.html#libraries). We do not support libraries in the current version of Workbench. Please move the functions from the library into your contract. 
