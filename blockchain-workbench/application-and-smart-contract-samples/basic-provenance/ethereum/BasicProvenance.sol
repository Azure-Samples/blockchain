pragma solidity ^0.4.20;
contract WorkbenchBase {
    event WorkbenchContractCreated(string applicationName, string workflowName, address originatingAddress);
    event WorkbenchContractUpdated(string applicationName, string workflowName, string action, address originatingAddress);

    string internal ApplicationName;
    string internal WorkflowName;

    constructor(string applicationName, string workflowName) internal {
        ApplicationName = applicationName;
        WorkflowName = workflowName;
    }

    function ContractCreated() internal {
        emit WorkbenchContractCreated(ApplicationName, WorkflowName, msg.sender);
    }

    function ContractUpdated(string action) internal {
        emit WorkbenchContractUpdated(ApplicationName, WorkflowName, action, msg.sender);
    }
}

contract BasicProvenance is WorkbenchBase('BasicProvenance', 'BasicProvenance')
{

    //Set of States
	enum StateType { Created, InTransit, Completed}
	
	//List of properties
	StateType public  State;
	address public  InitiatingCounterparty;
	address public  Counterparty;
	address public  PreviousCounterparty;
	address public  SupplyChainOwner;
	address public  SupplyChainObserver;
	
	constructor(address supplyChainOwner, address supplyChainObserver) public
	{
        InitiatingCounterparty = msg.sender;
        Counterparty = InitiatingCounterparty;
        SupplyChainOwner = supplyChainOwner;
        SupplyChainObserver = supplyChainObserver;
        State = StateType.Created;  
        ContractCreated();
    }

	function TransferResponsibility(address newCounterparty) public
	{
        if (Counterparty != msg.sender || State == StateType.Completed)
        {
            revert();
        }

        if (State == StateType.Created)
        {
            State = StateType.InTransit;
        }

        PreviousCounterparty = Counterparty;
        Counterparty = newCounterparty;
        ContractUpdated('TransferResponsibility');
    }

	function Complete() public
	{
	    if (SupplyChainOwner != msg.sender || State == StateType.Completed)
        {
            revert();
        }

        State = StateType.Completed;
        PreviousCounterparty = Counterparty;
        Counterparty = 0x0;
        ContractUpdated('Complete');
    }

}
