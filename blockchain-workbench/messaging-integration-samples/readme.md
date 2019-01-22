Messaging Integration Samples
=============================

This section includes integration samples that showcase -

Integration with Logic Apps to send messages to the blockchain
--------------------------------------------------------------
[Creating a new contract using messaging](CreateContract.md)

[Create a smart contract action in Azure Blockchain Workbench via messaging](CreateContractAction.md)

Integration with Event Grid and Logic Apps to process messages published by Azure Blockchain Workbench
--------------------------------------------------------------------------------------------------------
[After a contract update, executing business logic based on the contract
    state after the execution of a specific contract action.](
    ExecuteLogicBasedOnContractStateAfterASpecificContractAction.md)

For example, after the execution of “IngestTelemetry”, if the state is
“OutOfCompliance” then execute relevant business logic.

[After a contract update, executing business logic based on the value of
    specific contract function invoked.](
    ExecuteLogicBasedOnPropertyValueAfterASpecificContractAction.md)

For example, after the execution of the “IngestTelemetry”

Integration with Azure Functions to process messages published by Azure Blockchain Workbench
----------------------------------------------------------------------------------------------
[Capture Azure Blockchain Workbench events in Azure Functions and take action based on message type](ProcessEventsWithAzureFunctions.md)
