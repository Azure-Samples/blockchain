# BlockTalk - Truffle Ganache Example
This session covered the basics of getting started with the Truffle Ganache and how this applies to Dapp development.  In the video, the team from Truffle spoke about how Ganache works, and gave a brief demonstration.

# Prerequisites
To run this demo you will need to have Truffle installed on your machine.  If you are unable to install this or the dependencies, preconfigured virtual machines in Azure are available.  The latest stable version [here](https://portal.azure.com/#create/consensys.truffletruffle) and the latest beta version [here](https://portal.azure.com/#create/consensys.truffletruffle-beta).

- Install [nodejs](https://nodejs.org/en/)
- Install truffle by running the following:
  - From command prompt or terminal run: 
    ```
    npm install truffle -g
    ``` 
- Install ganache-cli by running the following:
  - From command promt or terminal run:
    ```
    npm install ganache-cli -g 
    ```
- Install Ganache App by downloading the image:
  - Windows [here](https://github.com/trufflesuite/ganache/releases/download/v1.2.2/Ganache-1.2.2.appx)
  - Other Os [here](https://github.com/trufflesuite/ganache/releases)

`NOTE: VS Code provided an excellent environment to develop with Truffle and is demonstrated in the video.` https://code.visualstudio.com/

# Setup the Truffle project
- From a command prompt or terminal window, navigate to an empty directory.  For our demo we created a directory: `mkdir TruffleDemo`
- In this directory, run the following command to create a empty Truffle project: `truffle init`
- Create a new smart contract in the contracts folder named SimpleStorage.sol.
- Add the following code to this contract
    ```
    pragma solidity ^0.4.24;

    contract SimpleStorage {
        uint storedData;

        constructor() public {
            storedData = 7;
        }

        function get() public view returns (uint) {
            return storedData;
        }

        function set(uint value) public {
            storedData = value;
        }
    }
    ```
- Create a migration to allow deployment of this contract by creating a new file in the migrations folder named 2_deploy_contracts.js.
- Add the following code to this new file
    ```
    const SimpleStorage = artifacts.require('SimpleStorage');

    module.exports = (deployer) => {
        deployer.deploy(SimpleStorage);
    };
    ```
- Update the truffle configuration, by overwriting the default configuration stored in the truffle.config file.  Use the following config.
    ```
    module.exports = {
        networks: {
            development: {
                host: "127.0.0.1",
                port: 7545,
                network_id: * // Match any network id
            }
        }
    };
    ```
# Working with the Truffle project
- Start the Ganache app

- From a command prompt or terminal run the following to start an instance of ganache from a prompt.
    ```
    truffle compile
    truffle migrate
    ```
- View the blocks and transactions in Ganche app