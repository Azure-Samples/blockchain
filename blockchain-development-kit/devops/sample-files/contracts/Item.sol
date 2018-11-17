pragma solidity ^0.4.20;

import "./lib/WorkbenchBase.sol";

// contains string support functions
import "./lib/String.sol";

import "./BasicItemRegistry.sol";

contract Item is WorkbenchBase("BasicItemRegistry", "Item") {

  // Registry
  BasicItemRegistry MyItemRegistry;
  address public RegistryAddress;

  //Item Proeprties
  //Set of States
  enum StateType {Active, Retired}
  StateType public  State;
  address public Manufacturer; // manufacturer of the Item
  string public ItemId; //identifier for the Item, stored off chain
  address public Owner; //identifier for the Owner of the Item, stored off chain
  string public OwnerDetailHash; //hash of the owner details for the Item, stored off chain
  string public Make; // text here, but could be an Id
  string public Model; // text here, but coudl be an Id
  string public Color; // text, represents the color of the Item
  string public ManufactureDate; // epoch time
  string public FactoryId; // Reflects an off chain identifier for a Factory;

  function Item (address manufacturer, string itemId, string make, string model, string color, string manufactureDate, string factoryId) public {
    Manufacturer = manufacturer;   
    ItemId = itemId;
    Make = make;
    Model = model;
    ManufactureDate = manufactureDate;
    FactoryId = factoryId;
    Color = color;

    //NOTE - Don't hardcode registry address, even if registry previously deployed to the chain, as this would break tests
    State = StateType.Active;
    ContractCreated();
  }

  function RegisterItem(address registryAddress) public {

    // only assign if there isn't one assigned already
    require(RegistryAddress == 0x0);
    require(State == StateType.Active);

    MyItemRegistry = BasicItemRegistry(registryAddress);

    //Check to see if the item is already registered and 
    //also check to see if this item can have its ownership transferred
    require(!MyItemRegistry.IsRegisteredItemContractAddress(address(this)));

    MyItemRegistry.RegisterItem(address(this), ItemId);
    ContractUpdated("RegisterItem");
  }

  function Retire() public {
    State = StateType.Retired;
    ContractUpdated("Retire");
  }
}
