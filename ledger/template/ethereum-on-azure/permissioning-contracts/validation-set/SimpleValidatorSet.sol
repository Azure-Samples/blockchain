pragma solidity 0.4.21;

import "./SafeMath.sol";
import "./Utils.sol";

contract ValidatorSet {
    /// Issue this log event to signal a desired change in validator set.
    /// This will not lead to a change in active validator set until
    /// finalizeChange is called.
    ///
    /// Only the last log event of any block can take effect.
    /// If a signal is issued while another is being finalized it may never
    /// take effect.
    ///
    /// _parent_hash here should be the parent block hash, or the
    /// signal will not be recognized.
    event InitiateChange(bytes32 indexed _parent_hash, address[] _new_set);

    /// Get current validator set (last enacted or initial if no changes ever made)
    function getValidators() public constant returns (address[] _validators);

    /// Called when an initiated change reaches finality and is activated.
    /// Only valid when msg.sender == SUPER_USER (EIP96, 2**160 - 2)
    ///
    /// Also called when the contract is first enabled for consensus. In this case,
    /// the "change" finalized is the activation of the initial set.
    function finalizeChange() public;
}

// Simple Validator Membership Management Contract
contract SimpleValidatorSet is ValidatorSet {

    event FinalizeCalled(address caller);
    event AddValidatorCalled(address caller);

    enum Operation { AddValidators, RemoveValidators }

    struct ChangeRequest {
        Operation op;
        address[] identities;
        address adminOwner;
    }

	// System address, used by the block sealer.
    address constant SYSTEM_ADDRESS = 0xffffFFFfFFffffffffffffffFfFFFfffFFFfFFfE;
    
    // To be replaced prior to compilation
    address[] bootstrapValidatorAddresses = [0x1111111111111111111111111111111111111111];
    address[] testValidatorAddresses = [address(0x00933b6FF79899F3B5B56E28725bbEB5be8f43e1), address(0x007a5dc2a434dF5e7f3F40af424F7Ba521b294b7), address(0x627306090abaB3A6e1400e9345bC60c78a8BEf57)];
    // Not used for the simple validator
    address bootstrapAdminAddress = address(0x0000000000000000000000000000000000000000);

    address[] validatorAddresses;
    mapping (address => address[]) AdminToValidators;
    address[] pendingValidatorAddresses;
    mapping (address => bool) isValidator;
    ChangeRequest latestChange;
    bool appliedLastChange;
    bool testHooksEnabled = false;
    address internal TRUFFLEADDRESS = 0x627306090abaB3A6e1400e9345bC60c78a8BEf57;
    
    function SimpleValidatorSet () public {
        // Truffle dev account
	    // testHooksEnabled allows us to call finalize from non-system account
        if (msg.sender == TRUFFLEADDRESS) {
            testHooksEnabled = true;
            bootstrapValidatorAddresses = testValidatorAddresses;
            bootstrapAdminAddress = TRUFFLEADDRESS;
        }
        // Add bootstrap addresses to the validator list
        addValidatorsInternal(bootstrapValidatorAddresses, bootstrapAdminAddress, true);
	
        appliedLastChange = true;
    }

    // Returns list of active validators
    function getValidators() public constant returns (address[]) {
        return validatorAddresses;
    }

    /// Get the validators that belong to a specific admin
    function getAdminValidators(address adminAddress) public view returns (address[]) {
        return AdminToValidators[adminAddress];
    }

    // Some change has made its way to finality
    // Update our state to reflect the change
    function finalizeChange() public onlySystem {
        emit FinalizeCalled(msg.sender);
        if (latestChange.op == Operation.AddValidators) {
            for (uint i = 0; i < latestChange.identities.length; i++) {
                if (!isValidator[latestChange.identities[i]]) {
                    isValidator[latestChange.identities[i]] = true;
                    AdminToValidators[latestChange.adminOwner].push(latestChange.identities[i]);
                }
            }
        } else if (latestChange.op == Operation.RemoveValidators) {
            for (uint j = 0; j < latestChange.identities.length; j++) {
                isValidator[latestChange.identities[j]] = false;
                AdminToValidators[latestChange.adminOwner] = Utils.deleteArrayElement(AdminToValidators[latestChange.adminOwner], latestChange.identities[j]);
            }
        } else {
            // Not supported 
            revert();
        }
        appliedLastChange = true;
        validatorAddresses = pendingValidatorAddresses;
    }

    // Add validator to pending list
    // If the validator list is updated as part of the contract constructor, we do not need to call initiateChange
    function addValidatorsInternal(address[] validatorAddressesToAdd, address adminAddress, bool fromContractConstructor) internal {        
        emit AddValidatorCalled(msg.sender);
        for (uint i = 0; i < validatorAddressesToAdd.length; i++) {
            assert(!isValidator[validatorAddressesToAdd[i]]);
            require(validatorAddressesToAdd[i] != address(0));
            // change needs to reach finality before being added
            pendingValidatorAddresses.push(validatorAddressesToAdd[i]);
            // Add directly if it's called from first member
            if (fromContractConstructor) {
                isValidator[validatorAddressesToAdd[i]] = true;
                validatorAddresses.push(validatorAddressesToAdd[i]);
                AdminToValidators[adminAddress].push(validatorAddressesToAdd[i]);
            }
        }
        if (!fromContractConstructor) {
            latestChange = ChangeRequest(Operation.AddValidators, validatorAddressesToAdd, adminAddress);
            initiateChange();
        }
    }

    // Remove validator from pending list and push to change queue
    function removeValidatorsInternal(address[] validatorAddressesToRemove, address adminAddress) internal {
        // Blockchain network halts with less than two validators
        assert(validatorAddresses.length - validatorAddressesToRemove.length >= 2);
        for (uint i = 0; i < validatorAddressesToRemove.length; i++) {
            bool found = false;
            for (uint j = 0; j < AdminToValidators[adminAddress].length; j++) {
                if (AdminToValidators[adminAddress][j] == validatorAddressesToRemove[i]) {
                    found = true;
                    break;
                }
            }
            // Ensure validator belongs to admin
            assert(found);
            pendingValidatorAddresses = Utils.deleteArrayElement(pendingValidatorAddresses, validatorAddressesToRemove[i]);
        }
        latestChange = ChangeRequest(Operation.RemoveValidators, validatorAddressesToRemove, adminAddress);
        initiateChange();
    }

    // Announce change
    // Parity's consensus is heavily tied to this event
    // The list that is announced here is just as important as the getValidators call
    function initiateChange() private {
        emit InitiateChange(block.blockhash(block.number-1), pendingValidatorAddresses);
        appliedLastChange = false;
    }

    modifier onlySystem() {
        require(testHooksEnabled || msg.sender == SYSTEM_ADDRESS);
        _;
    }
    
    modifier lastChangeFinalized() {
        require(appliedLastChange);
        _;
    }
}