Ping Pong Game Application for Azure Blockchain Workbench
====================================================
	
Overview 
---------
The Ping Pong Game application is an example smart contract which has multiple workflows in Azure Blockchain Workbench.  The application showcases how one contract can create another contract as well as how a contract function in one workflow can call another contract function for another workflow.   
	
Application Roles 
------------------
| Name       | Description                                                                                         |
|------------|-----------------------------------------------------------------------------------------------------|
| Starter | Starter of the ping pong game.                                        |
	
Starter Workflow States 
-------
| Name                 | Description                                                                                                 |
|----------------------|-------------------------------------------------------------------------------------------------------------|
| Game Provisioned| The state that is reached after the contract is created.                                                    |
| PingPonging | The state that is reached when the game starter and the ping pong "player" contract are engaged in a ping pong game. |
| Game Finished| The state that is reached when the ping pong game is finished.     |
	
	
Player Workflow States 
-------
| Name                 | Description                                                                                                 |
|----------------------|-------------------------------------------------------------------------------------------------------------|
| Pingpong Player Created| The state that is reached after the contract is created and a ping pong player is created from the Starter contract.|
| PingPonging | The state that is reached when the game starter and the ping pong "player" contract are engaged in a ping pong game.  |
| Game Finished| The state that is reached when the ping pong game is finished. |
	
	
Workflow Details
---------------
![](media/FrequentFlyerRewardsCalculator.PNG)
	
An instance of Frequent Flyer Rewards Calculator is created when an airline representative creates a contract by specifying a flyer and the rewards per mile.  The flyer associated with the contract can add miles after the contract is created.  The miles can be added as an array of integers and the size of this array need not have to be specified in the contract.  The miles are all stored as a part of a monotonically growing dynamic array.  The value for the total rewards accrued is computed as and when miles are added.
	
	
Application Files
-----------------
[PingPongGame.json](./ethereum/PingPongGame.json)

[PingPongGame.sol](./ethereum/PingPongGame.sol)
