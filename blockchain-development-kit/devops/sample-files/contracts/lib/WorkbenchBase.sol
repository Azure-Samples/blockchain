pragma solidity ^0.4.20;

contract WorkbenchBase {
  event WorkbenchContractCreated(string applicationName, string workflowName, address originatingAddress);

  event WorkbenchContractUpdated(string applicationName, string workflowName, string action, address originatingAddress);

  string internal ApplicationName;

  string internal WorkflowName;

  function WorkbenchBase(string applicationName, string workflowName) internal {
    ApplicationName = applicationName;
    WorkflowName = workflowName;
  }

  function ContractCreated() internal {
    WorkbenchContractCreated(ApplicationName, WorkflowName, msg.sender);
  }

  function ContractUpdated(string action) internal {
    WorkbenchContractUpdated(ApplicationName, WorkflowName, action, msg.sender);
  }
}

