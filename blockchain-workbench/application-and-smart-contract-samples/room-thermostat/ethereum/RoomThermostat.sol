pragma solidity ^0.4.20;

contract RoomThermostat
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
	
	function RoomThermostat(address thermostatInstaller, address thermostatUser) public
	{
        Installer = thermostatInstaller;
        User = thermostatUser;
        TargetTemperature = 70;
    }

	function StartThermostat() public
	{
        if (Installer != msg.sender || State != StateType.Created)
        {
            revert();
        }

        State = StateType.InUse;
    }

	function SetTargetTemperature(int targetTemperature) public
	{
	    if (User != msg.sender || State != StateType.InUse)
        {
            revert();
        }
        TargetTemperature = targetTemperature;
    }

	function SetMode(ModeEnum mode) public
	{
	    if (User != msg.sender || State != StateType.InUse)
        {
            revert();
        }
        Mode = mode;
    }
}