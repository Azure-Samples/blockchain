pragma solidity ^0.4.23;

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

contract FrequentFlyerRewardsCalculator is WorkbenchBase('FrequentFlyerRewardsCalculator', 'FrequentFlyerRewardsCalculator') {

     //Set of States
    enum StateType {SetFlyerAndReward, MilesAdded}

    //List of properties
    StateType public  State;
    address public  AirlineRepresentative;
    address public  Flyer;
    uint public RewardsPerMile;
    uint[] public Miles;
    uint IndexCalculatedUpto;
    uint public TotalRewards;

    // constructor function
    constructor(address flyer, int rewardsPerMile) public
    {
        AirlineRepresentative = msg.sender;
        Flyer = flyer;
        RewardsPerMile = uint(rewardsPerMile);
        IndexCalculatedUpto = 0;
        TotalRewards = 0;
        State = StateType.SetFlyerAndReward;

        // call ContractCreated() to create an instance of this workflow
        ContractCreated();
    }

    // call this function to add miles
    function AddMiles(int[] miles) public
    {
        if (Flyer != msg.sender)
        {
            revert();
        }

        for (uint i = 0; i < miles.length; i++)
        {
            Miles.push(uint(miles[i]));
        }

        ComputeTotalRewards();

        State = StateType.MilesAdded;

        // call ContractUpdated() to record this action
        ContractUpdated('AddMiles');
    }

    function ComputeTotalRewards() private
    {
        // make length uint compatible
        uint milesLength = uint(Miles.length);
        for (uint i = IndexCalculatedUpto; i < milesLength; i++)
        {
            TotalRewards += (RewardsPerMile * Miles[i]);
            IndexCalculatedUpto++;
        }
    }

    function GetMiles() public constant returns (uint[]) {
        return Miles;
    }
}
