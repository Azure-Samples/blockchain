pragma solidity ^0.4.20;

import "./lib/WorkbenchBase.sol";

// contains string support functions
import "./lib/String.sol";

contract BasicItemRegistry is WorkbenchBase("BasicItemRegistry", "ItemRegistry") {
  enum StateType { Created, Open, Closed}

  StateType public State;
  ItemStruct[] public Items;

  mapping(string => ItemStruct) private ItemIdLookup;
  mapping(address => ItemStruct) private ItemContractAddressLookup;

  string public Name;
  string public Description;

  struct ItemStruct { 
    address ItemContractAddress;
    string ItemId; 
    uint Index;
  }

  address[] private itemAddressIndex;
  string[] private ItemIdIndex;

  event LogNewItem   (address indexed ItemContractAddress, uint index, bytes32 Item);
  event LogUpdateItem(address indexed ItemContractAddress, uint index, bytes32 ItemId);

  function BasicItemRegistry(string name, string description) public {
    Name = name;
    Description = description;
    State = StateType.Created;
    ContractCreated();
  }

  function OpenRegistry() public {
    State = StateType.Open;        
    ContractUpdated("OpenRegistry");
  }

  function CloseRegistry() public {
    State = StateType.Closed;
    ContractUpdated("CloseRegistry");
  }

  //Lookup to see if a contract address for a item contract is already registered
  function IsRegisteredItemContractAddress(address ItemContractAddress)
  public 
  view
  returns(bool isRegistered) 
  {
    if(itemAddressIndex.length == 0) return false;
    return (itemAddressIndex[ItemContractAddressLookup[ItemContractAddress].Index] == ItemContractAddress);
  }

  //Look up to see if this item reg is registered
  function IsRegisteredItemId(string ItemId)
  public 
  view
  returns(bool isRegistered) 
  {
    if(ItemIdIndex.length == 0) return false;

    string memory ItemIdInternalString = ItemIdIndex[ItemIdLookup[ItemId].Index] ;
    return (String.compareStrings(ItemIdInternalString, ItemId));
  }

  function RegisterItem(
    address ItemContractAddress, 
    string ItemId 
  ) 
  public
  returns(uint index)
  {
    require(State == StateType.Open);
    require(!IsRegisteredItemContractAddress(ItemContractAddress));

    //Add lookup by address
    ItemContractAddressLookup[ItemContractAddress].ItemContractAddress = ItemContractAddress;

    ItemContractAddressLookup[ItemContractAddress].ItemId = ItemId;
    ItemContractAddressLookup[ItemContractAddress].Index = itemAddressIndex.push(ItemContractAddress) - 1;

    //Add look up by reg number
    //string ItemIdString = String.bytes32ToString(ItemId);
    ItemIdLookup[ItemId].ItemContractAddress = ItemContractAddress;

    ItemIdLookup[ItemId].ItemId = ItemId;
    ItemIdLookup[ItemId].Index = ItemIdIndex.push(ItemId) - 1;

    ContractUpdated("RegisterItem");
    return itemAddressIndex.length - 1;
  }

  function GetItemByAddress(address ItemContractAddress)
  public 
  view
  returns(string ItemId)
  {
    require(IsRegisteredItemContractAddress(ItemContractAddress));
    return ItemContractAddressLookup[ItemContractAddress].ItemId;
  } 

  function GetItemByItemId(string ItemId)
  public 
  view
  returns(address ItemContractAddress)
  {
    require(IsRegisteredItemId(ItemId));
    return  ItemIdLookup[ItemId].ItemContractAddress;
  } 

  function GetNumberOfRegisteredItems() 
  public
  view
  returns(uint count)
  {
    return itemAddressIndex.length;
  }

  function GetItemAtIndex(uint index)
  public
  view
  returns(address ItemContractAddress)
  {
    return itemAddressIndex[index];
  }

}

