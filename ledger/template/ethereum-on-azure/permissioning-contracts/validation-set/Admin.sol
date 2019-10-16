pragma solidity 0.4.21;
import "./Utils.sol";

// Consortium member that can vote to add/remove new members
contract Admin {    
    uint votesForAdding;
    address[] votesFor;
    mapping(address=>bool) alreadyVotedToAdd;
    uint votesForRemoving;
    address[] votesAgainst;
    mapping(address=>bool) alreadyVotedToRemove;
    address identity;
    // Ensures that only the AdminValidatorSet or AdminSet can make changes
    mapping(address=>bool) owners;

    function Admin(address id, address owner) public {
        owners[msg.sender] = true;
        owners[owner] = true;
        identity = id;
    }

    function getIdentity() public view callerIsOwner returns (address) {
        return identity;
    }

    function voteFor(address existingAdmin) public callerIsOwner returns (uint)  {
        assert(!alreadyVotedToAdd[existingAdmin]);
        alreadyVotedToAdd[existingAdmin] = true;
        votesFor.push(existingAdmin);
        return ++votesForAdding;
    }

    function countOfVotesFor() public callerIsOwner returns (uint) {
        return votesForAdding;
    }

    // rescindVoteFor will ignore in the case that this admin never voted for
    function rescindVoteFor(address existingAdmin) public callerIsOwner {
        if(!alreadyVotedToAdd[existingAdmin]) {
            return;
        }
        alreadyVotedToAdd[existingAdmin] = false;
        votesFor = Utils.deleteArrayElement(votesFor, existingAdmin);
        --votesForAdding;
    } 

    function voteAgainst(address existingAdmin) public callerIsOwner returns (uint) {
        assert(!alreadyVotedToRemove[existingAdmin]);
        alreadyVotedToRemove[existingAdmin] = true;
        votesAgainst.push(existingAdmin);
        return ++votesForRemoving;
    }

    function countOfVotesAgainst() public callerIsOwner returns (uint) {
        return votesForRemoving;
    }

    // rescindVoteAgainst will ignore in the case that this admin never voted against
    function rescindVoteAgainst(address existingAdmin) public callerIsOwner {
        if(!alreadyVotedToRemove[existingAdmin]) {
            return;
        }
        alreadyVotedToRemove[existingAdmin] = false;
        votesAgainst = Utils.deleteArrayElement(votesAgainst, existingAdmin);
        --votesForRemoving;
    }

    function clearVotes() public callerIsOwner {
        for (uint i = 0; i < votesFor.length; i++) {
            alreadyVotedToAdd[votesFor[i]] = false;
        }
        votesForAdding = 0;
        votesFor = new address[](0);

        for (i = 0; i < votesAgainst.length; i++) {
            alreadyVotedToRemove[votesAgainst[i]] = false;
        }
        votesForAdding = 0;
        votesAgainst = new address[](0);
    }

    modifier callerIsOwner() {
        require(owners[msg.sender]);
        _;
    }
}

// Object to keep track of AdminSet and PendingAdminSet
// This contract provides O(1) operations for:
// Retrieving list of addresses
// Determining if admin is in set
// Retrieving an admin from the set
contract AdminSet {
    address[] addressList;
    mapping (address => Admin) adminMap;
    mapping (address => bool) inSet;
    uint count = 0;
    address owner;
    uint MAXADMINS = 200;
    
    function AdminSet() public {
        owner = msg.sender;
    }

    // Solidity doesn't support return of dynamic sized arrays
    function getAddressList() public view callerIsOwner returns (address[200] staticList) {
        for (uint i = 0; i < addressList.length; i++) {
            staticList[i] = addressList[i];
        }
    }
    
    function isInSet(address adminId) public view callerIsOwner returns (bool) {
        return inSet[adminId];
    }

    function getAdmin(address adminId) public view callerIsOwner returns (Admin) {
        assert(inSet[adminId]);
        return adminMap[adminId];
    }

    function addAdmin(address adminId) public callerIsOwner {
        assert(count < MAXADMINS);
        assert(!inSet[adminId]);
        adminMap[adminId] = new Admin(adminId, owner);
        inSet[adminId] = true;
        addressList.push(adminId);
        count++;
    }

    function removeAdmin(address id) public callerIsOwner {
        assert(inSet[id]);
        adminMap[id].clearVotes();
        inSet[id] = false;
        addressList = Utils.deleteArrayElement(addressList, id);
        count--;
    }

    function getCount() public view callerIsOwner returns (uint) {
        return count;
    }

    modifier callerIsOwner() {
        require(owner == msg.sender);
        _;
    }
}
