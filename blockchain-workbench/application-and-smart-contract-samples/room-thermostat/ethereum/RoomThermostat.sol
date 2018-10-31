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

contract RoomThermostat is WorkbenchBase('RoomThermostat', 'RoomThermostat')
{

    //Set of States
	enum StateType { Created, InUse}
	
	//List of properties
	StateType public State;
	address public Installer;
	address public User;
    int public TargetTemperature;
    enum ModeEnum {Off, Cool, Heat, Auto}
	ModeEnum public  Mode;
	
	constructor(address thermostatInstaller, address thermostatUser) public
	{
        Installer = thermostatInstaller;
        User = thermostatUser;
        TargetTemperature = 70;
        ContractCreated();
    }

	function StartThermostat() public
	{
        if (Installer != msg.sender || State != StateType.Created)
        {
            revert();
        }

        State = StateType.InUse;
        ContractUpdated('StartThermostat');
    }

	function SetTargetTemperature(int targetTemperature) public
	{
	    if (User != msg.sender || State != StateType.InUse)
        {
            revert();
        }
        TargetTemperature = targetTemperature;

        ContractUpdated('SetTemperature');
    }

	function SetMode(ModeEnum mode) public
	{
	    if (User != msg.sender || State != StateType.InUse)
        {
            revert();
        }
        Mode = mode;

        ContractUpdated('SetMode');
    }
}
