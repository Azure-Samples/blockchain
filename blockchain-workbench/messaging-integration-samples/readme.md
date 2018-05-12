Messaging Integration Samples
=============================

This section includes integration samples that showcase -

Integration with Logic Apps to send messages to the blockchain
--------------------------------------------------------------
[Creating a new contract using messaging](CreateContract.md)

[Execution an action on a contract using
        messaging](CreateContractAction.md)

Integration with Event Grid and Logic Apps to process messages delivered from Azure Blockchain Workbench
--------------------------------------------------------------------------------------------------------
[After a contract update, executing business logic based on the value of
        the contract
        state.](ExecuteLogicBasedOnContractStateAfterAContractUpdate.md)

For example, execute this logic for every update that occurs during the
“Open” state.

[After a contract update, executing business logic based on the contract
    state after the execution of a specific contract action.](
    ExecuteLogicBasedOnContractStateAfterASpecificContractAction.md)

For example, after the execution of “IngestTelemetry”, if the state is
“OutOfCompliance” then execute relevant business logic.

[After a contract update, executing business logic based on the value of
    specific contract property value.](
    ExecuteLogicBasedOnPropertyValueAfterASpecificContractAction.md)

For example, after the execution of the “IngestTelemetry”

Integration with Azure Functions to process messages delivered from Azure Blockchain Workbench
[Capture Azure Blockchain Workbench events in Azure Functions and take action based on message type](ProcessEventsWithAzureFunctions.md)
