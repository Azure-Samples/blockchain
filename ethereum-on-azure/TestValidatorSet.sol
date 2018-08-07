pragma solidity 0.4.21;
import "./SafeMath.sol";
import "./SimpleValidatorSet.sol";

// Simple test implementation that restricts adding validators to existing validators
contract TestValidatorSet is SimpleValidatorSet {

    function TestValidatorSet() SimpleValidatorSet() public {  }

    // Public interface for adding validators
    function addValidators(address[] validatorAddressesToAdd, address adminAddress) public callerIsValidator lastChangeFinalized {
        addValidatorsInternal(validatorAddressesToAdd, adminAddress, false);
    }
    // Public interface for removing validators
    function removeValidators(address[] validatorAddressesToRemove, address adminAddress) public callerIsValidator lastChangeFinalized {
        removeValidatorsInternal(validatorAddressesToRemove, adminAddress);
    }
    modifier callerIsValidator() {
        require(isValidator[msg.sender]);
        _;
    }
}