pragma solidity ^0.4.25;

contract DefectiveComponentCounter is WorkbenchBase('DefectiveComponentCounter', 'DefectiveComponentCounter') {

     //Set of States
    enum StateType {Create, ComputeTotal}

    //List of properties
    StateType public  State;
    address public  Manufacturer;
    int[12] public DefectiveComponentsCount;
    int public Total;

    // constructor function
    constructor(int[12] defectiveComponentsCount) public
    {
        Manufacturer = msg.sender;
        DefectiveComponentsCount = defectiveComponentsCount;
        Total = 0;
        State = StateType.Create;

        // call ContractCreated() to create an instance of this workflow
        ContractCreated();
    }

    // call this function to send a request
    function ComputeTotal() public
    {
        if (Manufacturer != msg.sender)
        {
            revert();
        }

        // calculate total for only the first 12 values, in case more than 12 are entered
        for (uint i = 0; i < 12; i++)
        {
            Total += DefectiveComponentsCount[i];
        }

        State = StateType.ComputeTotal;

        ContractUpdated('ComputeTotal');
    }

    // add the required getter function for array DefectiveComponentsCount
    function GetDefectiveComponentsCount() public constant returns (int[12]) {
        return DefectiveComponentsCount;
    }
}