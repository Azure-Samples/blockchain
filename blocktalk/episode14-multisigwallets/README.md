# Multi Signature Wallet
Hands-on lab on creating, deploying and working with Ethereum Multi Signature Wallet.

## Deploy MultiSigWallet Contract

Deploy [MultiSigWallet.sol](/multi-sig-wallet/contracts/MultiSigWallet.sol) either by using Remix (https://remix.ethereum.org) or Truffle. In terms of Ethereum enviroment, you may want to use Remix JavaScript VM or truffle(develop). Please note that [Ganache](https://truffleframework.com/ganache) has a known [bug](https://github.com/trufflesuite/ganache-cli/issues/497) that may impact transactions and revert them when they are send from Remix. 

You need to pass owner accounts to the constructor, along with the number of signatures to execute the transaction (transfer of ether). 

For example, you can select 2 accounts to be the owners and required signature count is 2. You can copy the account address by expanding the account dropdown and use the copy option (available next to the dropdown).    
```["0x4b0897b0513fdc7c541b6d9d7e929c4e5364d2db","0xca35b7d915458ef540ade6068dfe2f44e8fa733c","0x14723a09acff6d2a60dcdf7aa4aff308fddc160c"]```

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-deploy.png">

* Note: If you are using truffle make sure to update the account addresses inside [2_multi_signature_wallet_migration.js](/multi-sig-wallet/migrations/2_multi_signature_wallet_migration.js) file, before running the migrations. Also, with truffle either use truffle(develop) or Geth/Parity client.

## Sending Ether to the Wallet
Lets send some ether to the wallet (contract). Any account can send ether to the contract. Select one of the accounts from the dropdown and put 10 as the value. Finally press the ```(fallback)``` button.   

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-fallback.png">

You can check the balance of this wallet by pressing ```getCurrentBalance``` button. Remix should show 10 as the balance amount ```0: uint256: 10``` .
 
 
 ## Submit Transaction
With 10 ethers in the wallet, lets try to send 1 ether to some other destination account (EOA) e.g ```0x583031d1113ad414f02576bd6afabfb302140225```. The destination account will receive 1 ether, after required number of confirmations (signatures) from the owners are recieved. At the moment the require number of signature count is 2, so we will need two owners to confirm this transaction. 

First submit the new transaction using one of the owner's account (e.g. ```0xca35b7d915458ef540ade6068dfe2f44e8fa733c```).
Expand the ```submitTransaction``` method and fill in the details. Destination and value fields corresponds to destination address and ether value respectively. Data field is not used in this case but you can't leave it blank so its value is set to ```0x00```. This is a value in hex and you will see the role of data later in the excercise. 
* destination: 0x583031d1113ad414f02576bd6afabfb302140225
* value: 1
* data: 0x00
<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-submitTransaction.png">

Finally, press the ```transact``` button. In the Remix console, expand the transaction and look at the details. Notice that their is a ```transactionId``` associated with to this particular transaction. Note ```transactionId``` as it is the unqiue number that will be used to confirm this particular transaction in the next step. Also note down the from address ```0xca35b7d915458ef540ade6068dfe2f44e8fa733c``` and the contract address (its address available in the to field ```0x692a70d2e424a56d2c6c27aa97d1a86395877b3a```).  

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-transaction.png">


 ## Confirm Transaction
In order for the above transaction (sending 1 ether from contract ```0x692a70d2e424a56d2c6c27aa97d1a86395877b3a``` to ```0x583031d1113ad414f02576bd6afabfb302140225```) to get executed, two owners must confirm it. In real world, this essentially means until required number of signatures are gathered transfer will be delayed. 

To check the number of conformations on this transaction, press ```getConfirmations``` button. Notice, the a single account is listed in the output ```0: address[]: _confirmations 0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c```. This means that we have 1 confirmation. If you are wondering when/how this conformation took place? remember that the account  ```0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c``` is one of the owner's account, and it is also the account which submitted the transaction. Because its among the designated owners, automatic confirmation took place (You can however change the contract code if you don't want this to happen) when transaction was submitted. If you look at the ```logs``` section (image above), notice the event ```Confirmation``` was fired at the same time when transaction was submitted. 

We still need one more confirmation from another onwer, before ether can be send to the destination address. In Remix, select one of the owner accounts, make sure it is different from the account that already confirmed the transaction. 

Expand the ```confirmTransaction``` method and enter the ```transactionId```. Finally, press ```transact```. 

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-confirmTransaction.png">

Expand the transaction in Remix and take a look at logs section. Notice the presense of an event ```Execution```, which essentially means that all confimrations needed for the transfer to take place have been completed. At this point, the destination account ```0x583031d1113ad414f02576bd6afabfb302140225``` should have ```1 ether``` deposited from the wallet address (contract) ```0x692a70d2e424a56d2c6c27aa97d1a86395877b3a```.

You can check the balance of the wallet by pressing ```getCurrentBalance``` button. Remix should show 9 as the balance amount ```0: uint256: 9```.

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-confirmtTransLog.png">

## Adding a New Owner
Adding a new owner is a common ask, and supported through ```addOwner``` method. This method takes address to added to an owner as an input parameter. However, if we allow one of the owners to add addtional owner(s) this may lead to undesireable situations. 

For example, a rouge owner may able to add other rouge owner(s) and then able to perform transfers with their approval (confirmations). To avoid this, we need confirmations from other owners before any new owner is added. In the context of our contract, owner first send a request to add another owner through the call to ```submitTransaction``` method. The input parameter ```data``` plays an important role (as you will learn in the in next section) in the process making a call to ```addOwner``` method . If you try to call ```addOwner``` directly, the call will fail (this is due to the [```onlyWallet```](https://github.com/razi-rais/blockchain-workshop/blob/master/multi-sig-wallet/contracts/MultiSigWallet.sol#L138) requirement that is put in place on ```addOwner``` method, which only allows it to be called by contract itself.

Let's go through the step by step process of adding a new owner i-e ```0x583031d1113ad414f02576bd6afabfb302140225```.

### Prepare input 
First, we need to prepare the valid input that can be passed to ```data``` parameter of ```addOwner``` method.

* Take ```addOwner(address)``` method signature as a string, and then calculate its Keccak256 hash. After you get the hash, take first 4 bytes of that hash. The first 4 bytes of the hash of ```addOwner(address)``` are ```0x7065cb48```. You can use ```web3.sha3("removeOwner(address)")``` to get the hash and then take the first 4 bytes, but its already provided to save time.
* Take the address that you like to add and owner e.g. ```0x583031d1113ad414f02576bd6afabfb302140225``` and remove ```0x```. So ```0x583031d1113ad414f02576bd6afabfb302140225``` will become ```583031d1113ad414f02576bd6afabfb302140225```
* Replace the ```ADDRESS``` in the string below with the owner address (without 0x). 
     
     * Before: ```0x7065cb48000000000000000000000000ADDRESS```
     
     * After: ```0x7065cb48000000000000000000000000583031d1113ad414f02576bd6afabfb302140225```
     
## Making a call to submitTransaction
In Remix, expand the ```submitTransaction``` method and populate the input fields. Remember you need to an onwer to submit this tranaction. Make sure your input looks like:

* destination: ```0x692a70d2e424a56d2c6c27aa97d1a86395877b3a```
* value: ```0```
* data: ```0x7065cb48000000000000000000000000583031d1113ad414f02576bd6afabfb302140225```

Notice, the destination is set to the wallet (contract) address ```0x692a70d2e424a56d2c6c27aa97d1a86395877b3a``` (the actual value will be different for you) , value is set```0``` as their is no ether trasfer involve, and data is set to  ```0x7065cb48000000000000000000000000583031d1113ad414f02576bd6afabfb302140225``` (as prepared in previous step).

Finally, press  ```transact``` button.
<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-addowner1.png">

In Remix console pane, expand the transaction and capture its ```transactionId ``` by looking at logs section. In this case ```transactionId``` is 4.
<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-addowner2.png">


## Making a call to confirmTransaction
Since the wallet requires 2 owner confirmations on every transaction that is submitted, we still need one more owner to confirm the transaction. In the Remix, select owner account (different from the one who submitted the transaction in previous step).

To confirm the transaction, expand the ```confirmTransaction``` method and pass in ```transactionId``` that you have noted in the previous step.Finally, press the ```transact``` button.

In the Remix console pane, expand the transaction and look at the logs section. Notice, there are three events that took place:

 * Confirmation
 * OwnerAddition
 * Execution
 
If you recall, you have two of these events namely - ```Confirmation``` and ```Execution``` fired in the previous tasks then ether was send to an account. But this time, ```OwnerAddition```event is fired which make sense as you have just added another owner. 

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-addowner3.png">

You can also verify that the new owner ```0x583031d1113ad414f02576bd6afabfb302140225```is indeed added by pressing the ```getOwners``` button. 

You should see ```0x583031d1113ad414f02576bd6afabfb302140225``` listed among the owners. 

<img src="https://github.com/razi-rais/blockchain-workshop/blob/master/images/multisig-addowner4.png">

## Truffle Commands (optional)

### truffle compile

* Compile the multisigwallet.sol contract

```truffle compile --reset"```

### truffle migrate

Update the accounts in ```2_multi_signature_wallet_migration.js``` before running migrations.

```
//Accounts passed to constructor during migration:
//["0x4b0897b0513fdc7c541b6d9d7e929c4e5364d2db","0xca35b7d915458ef540ade6068dfe2f44e8fa733c","0x14723a09acff6d2a60dcdf7aa4aff3//08fddc160c"]

var MultiSigContract = artifacts.require("./MultiSigWallet.sol");

module.exports = function(deployer) {
  // deployment steps
 
  //NOTE: Make sure to update the accounts below based on your Ethereum envrioment. 
  var accounts = ["0x","0x", "0x"];
 
  //Number of owner signatures required before transaction can be executed.
  var requiredSignCount = 2;

  deployer.deploy(MultiSigContract,accounts, requiredSignCount);

};
```
```truffle migrate --reset"```

### truffle console

* Get wallet balance

```MultiSigWallet.deployed().then(function(instance) {return instance.getCurrentBalance();}).then(function(value {console.log(value);});```

* Get all owners

```MultiSigWallet.deployed().then(function(instance) {return instance.getOwners();}).then(function(value) {console.log(value);});```

* Get the number of required signatures

```MultiSigWallet.deployed().then(function(instance) {return instance.required();}).then(function(value) {console.log(value);});```

* Get all accounts available (not just owners)
```web3.eth.getAccounts(function(err,res) { accounts = res; });```

* Send Ether to contract (This will use the current default account for sending the ether)
 
```MultiSigWallet.deployed().then(function(instance) {return instance.send(web3.toWei(10, "ether"));}).then(function(value) {console.log(value);});```

* Submit transactions (There is bug in ganache-cli so try truffle-develop or regular geth client when running this command)
```
var destinationAccount = "0x821aea9a577a9b44299b9c15c88cf3087f3b5544";
var amount = 2;
var data = "0x22";
MultiSigWallet.deployed().then(function(instance) {return instance.submitTransaction(destinationAccount,amount,data);}).then(function(value) {console.log(value);});
```

* Get confirmations

```
var txId = 0; //Change transaction id if needed
MultiSigWallet.deployed().then(function(instance) {return instance.getConfirmations(txId);}).then(function(value) {console.log(value);});
```

* Confirm Transaction (This will also execute the transaction)

```
var secondAccount = "0xf17f52151ebef6c7334fad080c5704d77216b732"; //This is one of the owner's accounts
MultiSigWallet.deployed().then(function(instance) {return instance.confirmTransaction(txId,{from : secondAccount});}).then(function(value) {console.log(value);});
```

* Add a new owner

```
//data for submitTransaction to add new owner
var contractAddress = "0xf204a4ef082f5c04bb89f7d5e6568b796096735a";
var amount = 0;
var data = "0x7065cb48000000000000000000000000583031d1113ad414f02576bd6afabfb302140225";
MultiSigWallet.deployed().then(function(instance) {return instance.submitTransaction(contractAddress,amount,data);}).then(function(value) {console.log(value);});
```

