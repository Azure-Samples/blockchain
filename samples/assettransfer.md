# Asset Transfer Scenario

The asset transfer smart contract covers the scenario for buying and selling high value assets which require an inspector and appraiser. Owners can list their assets by instantiating an asset transfer smart contract. Buyers can make offers by taking an action on the smart contract, and buyer designated parties can take actions to inspect or appraise the asset. Once the asset is marked both inspected and appraised, the buyer and owner will confirm the sale again before the contract is set to complete. At each point in the process, all participants have visibility into the state of the contract as it is updated.

![Asset Transfer Scenario](https://github.com/Azure-Samples/blockchain/samples/AssetTransferScenario.jpg)

There are four personas in this demo contract – the owner as the initiator, and the other three personas (buyer, appraiser, and inspector) as participants. At different stages in the contract, there are various actions which participants can take. The logic written in the smart contract will modify the state accordingly based on which actions are taken.


## Application Roles
- Owner
- Buyer
- Inspector
- Appraiser


## States
- Active
- Offer Placed
- Pending Inspection
- Inspected
- Appraised
- Notional Acceptance
- Seller Accepted
- Buyer Accepted
- Accepted
- Terminated


## State Transition Diagram

The following state transition diagram articulates the possible flows, and the various transition functions at each state. Each user is only allowed to take certain actions depending on the application role. Instance roles indicate that only the user with the application role assigned to the specific contract is able to take actions on the contract.

The happy path highlighted shows in a given asset transfer contract, an instance owner can place an asset up for sale and a potential buyer can place an offer. The two parties can negotiate and once an offer amount is agreed upon, an inspector and an appraiser working for the instance buyer will participate. After their involvement, the buyer and the owner can choose to move forward and ultimately complete the transaction.

![Asset Transfer State Diagram](https://github.com/Azure-Samples/blockchain/samples/AssetTransferStateDiagram.png)
