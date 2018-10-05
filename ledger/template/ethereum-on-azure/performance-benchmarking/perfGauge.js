
var Web3 = require('web3')
var clui = require('clui');

var rpc = process.argv[2];
var web3 = new Web3(new Web3.providers.HttpProvider(rpc));

var Gauge = clui.Gauge;
var filter = web3.eth.filter('latest');

var BLOCK_GENERATION_FREQ_IN_SEC = 2;
var BLOCK_SAMPLE_AMOUNT = 50;
var TX_SAMPLE_RATE = 1000;
var blockCount = 0;
var totalTPS = 0;
var maxTPS = 0;
var queryTime = 0;

filter.watch(function (error, blockhash) {

    if (error) {
        console.log(" Error on getting latest mined block: " + error);
        process.exit();
    }

    var t0 = new Date().getTime();
    web3.eth.getBlock(blockhash, function(error, minedBlock){
      var t1 = new Date().getTime();
      queryTime += (t1 - t0);

      if (error) {
        console.log("Error receiving mined block: " + error);
        process.exit();
      }
      var tx = (minedBlock.transactions) ? minedBlock.transactions.length : 0;
      var successful = 0;
      var queried = 0;
      // sample the transactions to not overwhelm the nodes
      var statusChecks = [];
      for (var i = 0; i < minedBlock.transactions.length; i= i + TX_SAMPLE_RATE) {
  		    statusChecks.push(new Promise((resolve,reject) => {
    				queried++;
    				var txReceipt = web3.eth.getTransactionReceipt(minedBlock.transactions[i]);
    				if (typeof txReceipt == "undefined") {
    					console.log("undefined transaction in block");
    					reject(false);
    				}
    				if(txReceipt.status == "0x1") {
    					successful++;
    					resolve(true);
    				}
    				else {
    					console.log("TX in failed state:" + minedBlock.transactions[i]);
    				}
          }));
       }

  		Promise.all(statusChecks).then(function () {
  			blockCount++;
  			var tps = tx/BLOCK_GENERATION_FREQ_IN_SEC;
  			totalTPS += tps;
  			if (maxTPS < tps) {
  				maxTPS = tps;
  			}
  			var total = 1000;
  			console.log(Gauge(tps, total, 20, total * 0.8, tps+" TPS"));
  			if (queried != 0 && (successful/queried)!= 1) {
  				console.log("Detected transction: " + successful / queried);
  				process.exit()
  			}
  			if (blockCount >= BLOCK_SAMPLE_AMOUNT) {
  				console.log("============RESULTS============");
  				console.log("Blocks tracked: " + blockCount);
  				console.log("Avg TPS: " + totalTPS/blockCount);
  				console.log("Max TPS: " + maxTPS);
          console.log("Avg query speed: " + queryTime/blockCount + "ms");
  				process.exit()
  			}
  		});
    });
});
