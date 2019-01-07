var Item = artifacts.require("Item");
var BasicItemRegistry = artifacts.require("BasicItemRegistry");

module.exports = async function(deployer) {
  const registry = await BasicItemRegistry.deployed();
  const accounts = await web3.eth.getAccounts();

  await deployer.deploy(
    Item,
    accounts[0],
    "item0",
    "Zastava",
    "Yugo",
    "mustard",
    "1977-07-02",
    "0000001"
  );

  let item = await Item.deployed();
  await item.RegisterItem(registry.address);

  await deployer.deploy(
    Item,
    accounts[0],
    "item1",
    "Zastava",
    "Yugo",
    "mustard",
    "1977-07-04",
    "0000002"
  );

  item = await Item.deployed();
  await item.RegisterItem(registry.address);

  await deployer.deploy(
    Item,
    accounts[0],
    "item2",
    "Zastava",
    "Yugo",
    "mustard",
    "1977-07-06",
    "0000003"
  );

  item = await Item.deployed();
  await item.RegisterItem(registry.address);
};
