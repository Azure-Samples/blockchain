// Deploys a contract onto the specified network and generates 
// transactions in batches to this contract 
// Each transaction is submitted from a new account
// usage: node mockTransactions.js rpcEndpoint requestsPerBatch


var ethereumjs = require('ethereumjs-tx')
var Web3 = require('web3')
var wallet = require('ethereumjs-wallet')
var Stopwatch = require("node-stopwatch").Stopwatch;
var solc = require('solc')
var fs  = require('fs'); 

var rpc = process.argv[2];
var requestsPerBatch = process.argv[3];

// Load contract into memory and compile
var input = {
    'postBox.sol': fs.readFileSync('postBox.sol', 'utf8')
    };
    
var web3 = new Web3(new Web3.providers.HttpProvider(rpc));
var compiledCode = solc.compile({sources: input}, 1);
var bytecode = compiledCode.contracts['postBox.sol:postBox'].bytecode;
var abi = compiledCode.contracts['postBox.sol:postBox'].interface;
console.log(abi);
// Contract object
const contract = web3.eth.contract(JSON.parse(abi));

// Get contract data
const contractData = '0x' + bytecode;
const gasLimitHex = web3.toHex(300000);


var account = wallet.generate();
var rawTx = {
    nonce: 0,
    gasPrice: '0x00', 
    gasLimit: gasLimitHex,
    data: contractData,
    from: account.getAddress()
};

var tx = new ethereumjs(rawTx);
tx.sign(account.getPrivateKey());

// Submit batch transactions to the chain
function batchRequests(){
  if (!running){
	  running = true;
	  for (i = 0; i < requestsPerBatch; i++) {
		var privateKey = wallet.generate().getPrivateKey();
		ethRequests.push(ethRequest(privateKey));
	  }
	  
	  Promise.all(ethRequests).then(function() {
		  console.log("Submitted batch");
		  running = false
	  });
  }
}

function ethRequest(privateKey){
return new Promise((resolve, reject) => {

  var tx = new ethereumjs(rawTx);
  tx.sign(privateKey);

  var raw = '0x' + tx.serialize().toString('hex');
  web3.eth.sendRawTransaction(raw, function (txErr, transactionHash) {
	if(!txErr) {
	  requests++;
	  console.log(transactionHash);
	  resolve(transactionHash);
	}
	else {
		console.log("Error: " + txErr);
		return reject("Error: " + txErr);
	}
  });
});
}
  
function runPerfTests(address) {
  var data = contract.at(address).postMsg.getData("Hello PoA");
  rawTx = {
      nonce: 0,
      gasPrice: '0x00', 
      gasLimit: '0xA8D6',
      to: address, 
      value: '0x00', 
      data: data
  }

  requests = 0;  
  ethRequests = [];  
  running = false;
  
  setInterval(batchRequests,100);
}


function waitForTransactionReceipt(hash) {
    console.log('waiting for contract to be mined');
    const receipt = web3.eth.getTransactionReceipt(hash);
    // If no receipt, try again in 1s
    if (receipt == null) {
        setTimeout(() => {
            waitForTransactionReceipt(hash);
        }, 1000);
    } else {
        // The transaction was mined, we can retrieve the contract address
        console.log('deployed contract at address: ' + receipt.contractAddress);
        runPerfTests(receipt.contractAddress.toString());
    }
}

web3.eth.sendRawTransaction('0x' + tx.serialize().toString('hex'), (err, hash) => {
    if (err) { console.log(err); return; }

    // Log the tx, you can explore status manually with eth.getTransaction()
    console.log('contract creation tx: ' + hash);

    // Wait for the transaction to be mined
    waitForTransactionReceipt(hash);
});