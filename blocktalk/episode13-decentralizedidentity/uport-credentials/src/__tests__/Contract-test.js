import {ContractFactory } from '../Contract'


const buildRequestURI = (txObject) => {
  return `me.uport:${txObject.to}?function=${txObject.function}`
}
const Contract = ContractFactory(buildRequestURI)


const address = '0x41566e3a081f5032bdcad470adb797635ddfe1f0'
const abiToken = [
  {
    "constant": true,
    "inputs": [],
    "name": "name",
    "outputs": [
      {
        "name": "",
        "type": "string"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": false,
    "inputs": [
      {
        "name": "_spender",
        "type": "address"
      },
      {
        "name": "_value",
        "type": "uint256"
      }
    ],
    "name": "approve",
    "outputs": [
      {
        "name": "success",
        "type": "bool"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [],
    "name": "totalSupply",
    "outputs": [
      {
        "name": "",
        "type": "uint256"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": false,
    "inputs": [
      {
        "name": "_from",
        "type": "address"
      },
      {
        "name": "_to",
        "type": "address"
      },
      {
        "name": "_value",
        "type": "uint256"
      }
    ],
    "name": "transferFrom",
    "outputs": [
      {
        "name": "success",
        "type": "bool"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [],
    "name": "decimals",
    "outputs": [
      {
        "name": "",
        "type": "uint8"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [],
    "name": "version",
    "outputs": [
      {
        "name": "",
        "type": "string"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [
      {
        "name": "_owner",
        "type": "address"
      }
    ],
    "name": "balanceOf",
    "outputs": [
      {
        "name": "balance",
        "type": "uint256"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [],
    "name": "symbol",
    "outputs": [
      {
        "name": "",
        "type": "string"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": false,
    "inputs": [
      {
        "name": "_to",
        "type": "address"
      },
      {
        "name": "_value",
        "type": "uint256"
      }
    ],
    "name": "transfer",
    "outputs": [
      {
        "name": "success",
        "type": "bool"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": false,
    "inputs": [
      {
        "name": "_spender",
        "type": "address"
      },
      {
        "name": "_value",
        "type": "uint256"
      },
      {
        "name": "_extraData",
        "type": "bytes"
      }
    ],
    "name": "approveAndCall",
    "outputs": [
      {
        "name": "success",
        "type": "bool"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "constant": true,
    "inputs": [
      {
        "name": "_owner",
        "type": "address"
      },
      {
        "name": "_spender",
        "type": "address"
      }
    ],
    "name": "allowance",
    "outputs": [
      {
        "name": "remaining",
        "type": "uint256"
      }
    ],
    "payable": false,
    "type": "function"
  },
  {
    "inputs": [
      {
        "name": "_initialAmount",
        "type": "uint256"
      },
      {
        "name": "_tokenName",
        "type": "string"
      },
      {
        "name": "_decimalUnits",
        "type": "uint8"
      },
      {
        "name": "_tokenSymbol",
        "type": "string"
      }
    ],
    "type": "constructor"
  },
  {
    "payable": false,
    "type": "fallback"
  },
  {
    "anonymous": false,
    "inputs": [
      {
        "indexed": true,
        "name": "_from",
        "type": "address"
      },
      {
        "indexed": true,
        "name": "_to",
        "type": "address"
      },
      {
        "indexed": false,
        "name": "_value",
        "type": "uint256"
      }
    ],
    "name": "Transfer",
    "type": "event"
  },
  {
    "anonymous": false,
    "inputs": [
      {
        "indexed": true,
        "name": "_owner",
        "type": "address"
      },
      {
        "indexed": true,
        "name": "_spender",
        "type": "address"
      },
      {
        "indexed": false,
        "name": "_value",
        "type": "uint256"
      }
    ],
    "name": "Approval",
    "type": "event"
  },
]


describe('Contract', () => {

  let tokenContract

  beforeAll(() => {
    tokenContract = Contract(abiToken).at(address)
  })

  it('initializes given a contractABI and address', () => {
    expect(tokenContract).toEqual(jasmine.any(Object))
  });

  it('returns a function given a contractABI', () => {
    expect(Contract(abiToken)).toEqual(jasmine.any(Object))
  });

  it('returns a contract object with the given contract functions available', () => {
    expect(tokenContract.transferFrom).toBeDefined()
    expect(tokenContract.transfer).toBeDefined()
    expect(tokenContract.approveAndCall).toBeDefined()
  });

  it('returns a contract object with the given contract event names available', () => {
    expect(tokenContract.Transfer).toBeDefined()
    expect(tokenContract.Approval).toBeDefined()
  });

  it('returns a contract object with the given contract constant names available', () => {
    expect(tokenContract.totalSupply).toBeDefined()
    expect(tokenContract.balanceOf).toBeDefined()
  });

  it('throws an error if an event is called', () => {
    expect(tokenContract.Transfer).toThrowError(Error)
  });

  it('throws an error if a constant is called', () => {
    expect(tokenContract.totalSupply).toThrowError(Error)
  });

  it('returns a well formed uri on contract function calls', () => {
    const uri = tokenContract.transfer('0x41566e3a081f5032bdcad470adb797635ddfe1f0', 10)
    expect(uri).toEqual("me.uport:0x41566e3a081f5032bdcad470adb797635ddfe1f0?function=transfer(address 0x41566e3a081f5032bdcad470adb797635ddfe1f0, uint256 10)")
  });
});

describe('ContractFactory', () => {

  describe('By default', () => {
    let txObject

    beforeAll(() => {
      const tokenContract = ContractFactory()(abiToken).at(address)
      txObject = tokenContract.transfer('0x41566e3a081f5032bdcad470adb797635ddfe1f0', 10)
    })

    it('returns a well formed txObject on contract function calls', () => {
      expect(txObject.function).toBeDefined()
      expect(txObject.to).toEqual(address)
    });

    it('returns a txObject with a human readable function and params', () => {
      expect(txObject.function).toEqual('transfer(address 0x41566e3a081f5032bdcad470adb797635ddfe1f0, uint256 10)')
    });
  });

  describe('With an extend function', () => {
    it('allows the Contract object functions to be extended if given a function', () => {
      const extend = (txObject) => { return 'hello'}
      const Contract = ContractFactory(extend)
      const tokenContract = Contract(abiToken).at(address)
      // expect(tokenContract.transfer('0x41566e3a081f5032bdcad470adb797635ddfe1f0', 10)).toEqual('hello')
    });

    it('passes additional args on Contract object function calls to the extend function', () => {
      const str = 'ether'
      const extend = (txObject, str) => { return str}
      const Contract = ContractFactory(extend)
      const tokenContract = Contract(abiToken).at(address)
      expect(tokenContract.transfer('0x41566e3a081f5032bdcad470adb797635ddfe1f0', 10, str)).toEqual(str)
    });

    it('passes additional args beyond a transaction object to the extend function', () => {
      const extend = (txObj, id, sendOpts) => ({txObj, id, sendOpts})
      const Contract = ContractFactory(extend)
      const tokenContract = Contract(abiToken).at(address)

      const txObj = {gas: '10000000'}
      const id = 'WOOP'
      const sendOpts = {woop: 'woop'}

      const result = tokenContract.transfer('0xdeadbeef', 10, txObj, id, sendOpts)

      expect(result.txObj).toMatchObject(txObj)
      expect(result.id).toEqual(id)
      expect(result.sendOpts).toEqual(sendOpts)
    })
  });
});
