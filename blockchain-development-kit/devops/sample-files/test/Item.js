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
  0: "Active",
  1: "Retired",
  Active: 0,
  Retired: 1,
}

contract("Item", function (accounts) {
  describe("constructor", function () {
    let item;
    before("deploy fresh contract", async function () {
      item = await Item.new(
        accounts[0],
        "item0",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "redmond"
      );
    });

    it("should have a manufacturer address", async function () {
      const manufacturer = await item.Manufacturer();
      assert.strictEqual(manufacturer, accounts[0]);
    });

    it("should have an itemId", async function () {
      const itemId = await item.ItemId();
      assert.strictEqual(itemId, "item0");
    });

    it("should have a make", async function () {
      const make = await item.Make();
      assert.strictEqual(make, "microsoft");
    });

    it("should have a model", async function () {
      const model = await item.Model();
      assert.strictEqual(model, "windows");
    });

    it("should have a color", async function () {
      const color = await item.Color();
      assert.strictEqual(color, "blue");
    });

    it("should have a manufactureDate", async function () {
      const manufactureDate = await item.ManufactureDate();
      assert.strictEqual(manufactureDate, "1985-11-20");
    });

    it("should have a factoryId", async function () {
      const factoryId = await item.FactoryId();
      assert.strictEqual(factoryId, "redmond");
    });

    it("should be created in the Active state", async function () {
      const state = await item.State();
      assert.strictEqual(StateType[state], "Active");
    });

    it("should emit a WorkbenchContractCreated event", async function () {
      const receipt = await web3.eth.getTransactionReceipt(item.transactionHash);
      const logs = decodeLogs(receipt.logs);
      assert.eventIsWorkbenchContractCreated(logs[0], "BasicItemRegistry", "Item", accounts[0]);
    });
  });

  describe("RegisterItem", function () {
    let item;
    beforeEach("deploy fresh contract", async function () {
      item = await Item.new(
        accounts[0],
        "item0",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "redmond"
      );
    });

    it ("should register itself successfully", async function() {
      const basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      await basicItemRegistry.OpenRegistry();

      // assert precondition: Item address not registered
      let isRegisteredItemContractAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
      assert (!isRegisteredItemContractAddress)

      // assert precondition: Item Id not registered
      let isRegisteredItemId = await basicItemRegistry.IsRegisteredItemId("item0");
      assert (!isRegisteredItemId)

      await item.RegisterItem(basicItemRegistry.address);

      // assert postcondition: Item address is registered
      isRegisteredItemContractAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
      assert (isRegisteredItemContractAddress)

      // assert postcondition: Item Id is registered
      isRegisteredItemId = await basicItemRegistry.IsRegisteredItemId("item0");
      assert (isRegisteredItemId)
    });

    it("should emit a WorkbenchContractUpdated event", async function () {
      const basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      await basicItemRegistry.OpenRegistry();

      // assert precondition: Item address not registered
      let isRegisteredItemContractAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
      assert (!isRegisteredItemContractAddress)

      // assert precondition: Item Id not registered
      let isRegisteredItemId = await basicItemRegistry.IsRegisteredItemId("item0");
      assert (!isRegisteredItemId)

      const { logs } = await item.RegisterItem(basicItemRegistry.address);
      assert.eventIsWorkbenchContractUpdated(logs[logs.length - 1], "BasicItemRegistry", "Item", "RegisterItem", accounts[0]);
    });

    it("should not change Item state", async function () {
      const basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      await basicItemRegistry.OpenRegistry();

      // assert precondition: Item address not registered
      let isRegisteredItemContractAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
      assert (!isRegisteredItemContractAddress)

      // assert precondition: Item Id not registered
      let isRegisteredItemId = await basicItemRegistry.IsRegisteredItemId("item0");
      assert (!isRegisteredItemId)

      // assert precondition: Item state is Active
      let state = await item.State();
      assert.strictEqual(StateType[state], "Active");

      await item.RegisterItem(basicItemRegistry.address);

      // assert postcondition: Item state is still Active
      state = await item.State();
      assert.strictEqual(StateType[state], "Active");
    });

    it("should revert if item is retired", async function () {
      const basicItemRegistry = await BasicItemRegistry.new("MyRegistry", "Description goes here.");
      await basicItemRegistry.OpenRegistry();

      // assert precondition: Item address not registered
      let isRegisteredItemContractAddress = await basicItemRegistry.IsRegisteredItemContractAddress(item.address);
      assert (!isRegisteredItemContractAddress)

      // assert precondition: Item Id not registered
      let isRegisteredItemId = await basicItemRegistry.IsRegisteredItemId("item0");
      assert (!isRegisteredItemId)

      // set up precondition: retire item
      await item.Retire();

      // assert precondition: Item state is Retired
      let state = await item.State();
      assert.strictEqual(StateType[state], "Retired");

      try {
        await item.RegisterItem(basicItemRegistry.address);
        assert.fail("previous line should have thrown!");
      } catch (err) {
        assert(
          /revert/.test(err.message),
          `Expected revert error. Got '${err.message}', instead.`
        );
      }
    });
  });

  describe("Retire", function () {
    beforeEach("deploy fresh contract", async function () {
      item = await Item.new(
        accounts[0],
        "item0",
        "microsoft",
        "windows",
        "blue",
        "1985-11-20",
        "redmond"
      );
    });

    it("should change state to Retired", async function () {
      // assert precondition: Item state is Active
      state = await item.State();
      assert.strictEqual(StateType[state], "Active");

      // retire item
      await item.Retire();

      // assert postcondition: Item state is Retired
      state = await item.State();
      assert.strictEqual(StateType[state], "Retired");
    });

    it("should emit a WorkbenchContractUpdated event", async function () {
      const { logs } = await item.Retire();
      assert.eventIsWorkbenchContractUpdated(logs[0], "BasicItemRegistry", "Item", "Retire", accounts[0]);
    });
  });
});