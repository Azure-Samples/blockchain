const BasicItemRegistry = artifacts.require("BasicItemRegistry");
const Item = artifacts.require("Item");

// Grab truffle-contract's decodeLogs helper function. It expects to be bound
// to the contract constructor, so we do that here as well
const decodeLogs = require('truffle-contract/lib/utils').decodeLogs.bind(BasicItemRegistry);

// augment assert with workbench event validators
require("./helpers/workbenchEventValidators.js")(assert);

// helper enum description object, as enums aren't included in a contract's
// ABI, so we can't decode them from the compiler output... yet.
const StateType = {
  0: "Created",
  1: "Open",
  2: "Closed",
  Created: 0,
  Open: 1,
  Closed: 2
}

contract("BasicItemRegistry", function (accounts) {

  describe("constructor", function () {
    let basicItemRegistry;
    before("deploy fresh contract", async function () {
      basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
    });

    it("should be nameable", async function () {
      const name = await basicItemRegistry.Name();
      assert.strictEqual(name, "MyRegistry");
    });

    it("should be describable", async function () {
      const description = await basicItemRegistry.Description();
      assert.strictEqual(description, "Description goes here.");
    });

    it("should start off in the Created state", async function () {
      const state = await basicItemRegistry.State();
      assert.strictEqual(StateType[state], "Created");
    });

    it("should emit a WorkbenchContractCreated event", async function () {
      const receipt = await web3.eth.getTransactionReceipt(basicItemRegistry.transactionHash);
      const logs = decodeLogs(receipt.logs);
      assert.eventIsWorkbenchContractCreated(logs[0], "BasicItemRegistry", "ItemRegistry", accounts[0]);
    });
  });

  describe("OpenRegistry", function () {
    let basicItemRegistry;
    let logs;
    before("deploy fresh contract", async function () {
      basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      const receipt = await basicItemRegistry.OpenRegistry();
      logs = receipt.logs;
    });

    it("should set an open state", async function () {
      const state = await basicItemRegistry.State();
      assert.strictEqual(StateType[state], "Open");
    });

    it("should emit a WorkbenchContractUpdated event", async function () {
      assert.eventIsWorkbenchContractUpdated(logs[0], "BasicItemRegistry", "ItemRegistry", "OpenRegistry", accounts[0]);
    });
  })

  describe("CloseRegistry", function () {
    let basicItemRegistry;
    let logs;
    before("deploy fresh contract", async function () {
      basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      const receipt = await basicItemRegistry.CloseRegistry();
      logs = receipt.logs;
    });

    it("should set a closed state", async function () {
      const state = await basicItemRegistry.State();
      assert.strictEqual(StateType[state], "Closed");
    });

    it("should emit a WorkbenchContractUpdated event", async function () {
      assert.eventIsWorkbenchContractUpdated(logs[0], "BasicItemRegistry", "ItemRegistry", "CloseRegistry", accounts[0]);
    });
  })

  describe("RegisterItem", function () {
    beforeEach("deploy fresh contract", async function () {
    });

    describe("state: Create", function() {
      let basicItemRegistry;
      let item;
      before("Precondition: registry state & item registration status", async function() {
        basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
        item = await Item.new(
          accounts[0],
          "item0",
          "microsoft",
          "windows",
          "blue",
          "1985-11-20",
          "Redmond"
        );
        const itemId = await item.ItemId();
        const state = await basicItemRegistry.State();

        // assert precondition: registry state
        assert.strictEqual(StateType[state], "Created", "Contract was just created - state should be 'Created'");

        // assert precondition: Item address not registered
        const isRegisteredAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
        assert(!isRegisteredAddress, "Item address should not be registered, but registry contract says that it is");

        // assert precondition: Item ID not registered
        const IsRegisteredItemId = await basicItemRegistry.IsRegisteredItemId(itemId);
        assert(!IsRegisteredItemId, "Item ID should not be registered, but registry contract says that it is");
      });

      it("should not register an item when in the Created state", async function () {
        const itemId = await item.ItemId();
        const state = await basicItemRegistry.State();

        try {
          // attempt to register the item (expected to fail)
          await basicItemRegistry.RegisterItem(item.address, itemId);
          assert.fail("previous line should have thrown!");
        } catch (err) {
          assert(
            /revert/.test(err.message),
            `Expected revert error. Got '${err.message}', instead.`
          );
        }
      });
    });

    describe("state: Open", function() {
      let basicItemRegistry;
      let item;
      before("Precondition: registry state & item registration status", async function() {
        basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
        item = await Item.new(
          accounts[0],
          "item0",
          "microsoft",
          "windows",
          "blue",
          "1985-11-20",
          "Redmond"
        );
        await basicItemRegistry.OpenRegistry();

        const itemId = await item.ItemId();
        const state = await basicItemRegistry.State();

        // assert precondition: registry state
        assert.strictEqual(StateType[state], "Open", "Contract was just opened - state should be 'Opened'");

        // assert precondition: Item address not registered
        const isRegisteredAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
        assert(!isRegisteredAddress, "Item address should not be registered, but registry contract says that it is");

        // assert precondition: Item ID not registered
        const IsRegisteredItemId = await basicItemRegistry.IsRegisteredItemId(itemId);
        assert(!IsRegisteredItemId, "Item ID should not be registered, but registry contract says that it is");
      });

      it("should register an item when in the Open state", async function () {
        const itemId = await item.ItemId();

        // attempt to register the item
        await basicItemRegistry.RegisterItem(item.address, itemId);

        // assert postcondition: Item address registered
        const isRegisteredAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
        assert(isRegisteredAddress, "Registration transaction succeeded, but item address not listed as registered");

        // assert postcondition: Item ID registered
        const IsRegisteredItemId = await basicItemRegistry.IsRegisteredItemId(itemId);
        assert(IsRegisteredItemId, "Registration transaction succeeded, but item ID not listed as registered");
      });
    });

    describe("state: Closed", function() {
      let basicItemRegistry;
      let item;
      before("Precondition: registry state & item registration status", async function() {
        basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
        item = await Item.new(
          accounts[0],
          "item0",
          "microsoft",
          "windows",
          "blue",
          "1985-11-20",
          "Redmond"
        );
        await basicItemRegistry.CloseRegistry();

        const itemId = await item.ItemId();
        const state = await basicItemRegistry.State();

        // assert precondition: registry state
        assert.strictEqual(StateType[state], "Closed", "Contract was just opened - state should be 'Closed'");

        // assert precondition: Item address not registered
        const isRegisteredAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
        assert(!isRegisteredAddress, "Item address should not be registered, but registry contract says that it is");

        // assert precondition: Item ID not registered
        const IsRegisteredItemId = await basicItemRegistry.IsRegisteredItemId(itemId);
        assert(!IsRegisteredItemId, "Item ID should not be registered, but registry contract says that it is");
      });

      it("should not register an item when in the Closed state", async function () {
        const itemId = await item.ItemId();
        try {
          // attempt to register the item (expected to fail)
          await basicItemRegistry.RegisterItem(item.address, itemId);
          assert.fail("previous line should have thrown!");
        } catch (err) {
          assert(
            /revert/.test(err.message),
            `Expected revert error. Got '${err.message}', instead.`
          );
        }
      });
    });

    describe("events", function() {
      before("Precondition: registry state & item registration status", async function() {
        basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");

        await basicItemRegistry.OpenRegistry();

        item = await Item.new(
          accounts[0],
          "item0",
          "microsoft",
          "windows",
          "blue",
          "1985-11-20",
          "Redmond"
        );

        const itemId = await item.ItemId();
        const state = await basicItemRegistry.State();

        // assert precondition: registry state
        assert.strictEqual(StateType[state], "Open", "Contract was just opened - state should be 'Opened'");

        // assert precondition: Item address not registered
        const isRegisteredAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
        assert(!isRegisteredAddress, "Item address should not be registered, but registry contract says that it is");

        // assert precondition: Item ID not registered
        const IsRegisteredItemId = await basicItemRegistry.IsRegisteredItemId(itemId);
        assert(!IsRegisteredItemId, "Item ID should not be registered, but registry contract says that it is");
      });

      it("should emit a WorkbenchContractUpdated event when item is successfully registered", async function () {
        const itemId = await item.ItemId();
        let state = await basicItemRegistry.State();

        if (state.toNumber) {
          state = state.toNumber();
        }

        const { logs } = await basicItemRegistry.RegisterItem(item.address, itemId);
        assert.eventIsWorkbenchContractUpdated(logs[0], "BasicItemRegistry", "ItemRegistry", "RegisterItem", accounts[0]);
      });
    });
  });

  describe("view functions", function () {
    // All of the below tests are read-only and they can use the same initial
    // state. Let's make things a bit faster and do the precondition set up
    // outside of the function-level describe blocks.

    let basicItemRegistry;
    let items = [];
    before("deploy fresh contract", async function () {
      basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");

      await basicItemRegistry.OpenRegistry();
    });

    before("deploy three items", async function () {
      items.push(await Item.new(
        accounts[0],
        "item0",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "Redmond"
      ));
      items.push(await Item.new(
        accounts[0],
        "item1",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "Redmond"
      ));
      items.push(await Item.new(
        accounts[0],
        "item2",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "Redmond"
      ));
    });

    before("register items", async function () {
      await Promise.all(
        items.map(async (item) => {
          const itemId = await item.ItemId();
          await basicItemRegistry.RegisterItem(item.address, itemId);
        })
      );
    });

    describe("IsRegisteredItemContractAddress", function () {
      it("should return true for registered item addresses", async function () {
        await Promise.all(
          items.map(async (item) => {
            const isRegistered = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
            assert(
              isRegistered,
              `Item at address ${item.address} was registered, but IsRegisteredItemContractAddress returned 'false'`
            );
          })
        );
      });

      it("should return false if given an unknown address", async function () {
        const unknownAddress = "0xea4d02b14d3b69ac2cf31450a1591ae6b9a54b23";
        const isRegistered = await basicItemRegistry.IsRegisteredItemContractAddress(unknownAddress);
        assert(
          !isRegistered,
          `No item at address ${unknownAddress} was registered, but IsRegisteredItemContractAddress returned 'true'`
        );
      });
    });

    describe("IsRegisteredItemId", function () {
      it("should return true for registered item ids", async function () {
        await Promise.all(
          items.map(async (item) => {
            const itemId = await item.ItemId();
            const isRegistered = await basicItemRegistry.IsRegisteredItemId(itemId);
            assert(
              isRegistered,
              `Item with Id ${itemId} was registered, but IsRegisteredItemId returned 'false'`
            );
          })
        );
      });

      it("should return false if given an unknown id", async function () {
        const unknownId = "unknown";
        const isRegistered = await basicItemRegistry.IsRegisteredItemId(unknownId);
        assert(
          !isRegistered,
          `No item with id '${unknownId}' was registered, but IsRegisteredItemId returned 'true'`
        );
      });
    });

    describe("GetItemByAddress", function () {
      it("should return the correct item Id given an address", async function () {
        await Promise.all(
          items.map(async (item) => {
            const expectedItemId = await item.ItemId();
            const observedItemId = await basicItemRegistry.GetItemByAddress(item.address);
            assert.strictEqual(
              observedItemId,
              expectedItemId,
              `Item at address ${item.address} has Id ${expectedItemId}, but Id ${observedItemId} was returned instead`
            );
          })
        );
      });

      it("should revert if given an unknown address", async function () {
        const unknownAddress = "0xea4d02b14d3b69ac2cf31450a1591ae6b9a54b23";
        try {
          await basicItemRegistry.GetItemByAddress(unknownAddress);
          assert.fail("previous line should have thrown!");
        } catch (err) {
          assert(
            /revert/.test(err.message),
            `Expected revert error. Got '${err.message}', instead.`
          );
        }
      });
    });

    describe("GetItemByItemId", function () {
      it("should return the correct item address given an Id", async function () {
        await Promise.all(
          items.map(async (item) => {
            const itemId = await item.ItemId();
            const observedItemAddress = await basicItemRegistry.GetItemByItemId(itemId);
            assert.strictEqual(
              observedItemAddress,
              item.address,
              `Item with Id ${itemId} has address ${item.address}, but address ${observedItemAddress} was returned instead`
            );
          })
        );
      });

      it("should revert if given an unknown Id", async function () {
        const unknownId = "unknown";
        try {
          await basicItemRegistry.GetItemByItemId(unknownId);
          assert.fail("previous line should have thrown!");
        } catch (err) {
          assert(
            /revert/.test(err.message),
            `Expected revert error. Got '${err.message}', instead.`
          );
        }
      });
    });

    describe("GetNumberOfRegisteredItems", function () {
      it("should return the number of items in the registry", async function () {
        const count = await basicItemRegistry.GetNumberOfRegisteredItems();
        assert.strictEqual(count.toNumber(), 3)
      });
    });

    describe("GetItemAtIndex", function () {
      it("should return the correct item for a given index", async function () {
        await Promise.all(
          items.map(async (item, index) => {
            const observedItemAddress = await basicItemRegistry.GetItemAtIndex(index);
            assert.strictEqual(observedItemAddress, item.address);
          })
        );
      });
    });
  });
});
