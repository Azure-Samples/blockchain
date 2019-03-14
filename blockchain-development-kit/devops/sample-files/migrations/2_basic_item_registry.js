var BasicItemRegistry = artifacts.require("BasicItemRegistry");

module.exports = async function(deployer) {
  await deployer.deploy(BasicItemRegistry, "BasicItemRegistry", "A registry for basic items :-)");
  const registry = await BasicItemRegistry.deployed();
  await registry.OpenRegistry();
};
