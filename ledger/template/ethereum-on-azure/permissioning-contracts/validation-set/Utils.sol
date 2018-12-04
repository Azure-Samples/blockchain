pragma solidity 0.4.21;

library Utils {

    function deleteArrayElement(address[] list, address validatorAddress) internal pure returns (address[]) {
        address[] memory newList = new address[](list.length-1);
        uint writeIndex = 0;
        for (uint i = 0; i < list.length; i++) {
            if (list[i] != validatorAddress) {
                newList[writeIndex++] = list[i];
            }
        }
        assert(writeIndex == list.length-1);
        return newList;
    }
}