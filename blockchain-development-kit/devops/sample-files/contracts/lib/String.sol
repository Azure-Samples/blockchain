pragma solidity ^0.4.20;

library String {

  //-----------------------------------------------------
  // Supporting Functions
  //-----------------------------------------------------
  function stringToBytes32(string memory source) internal pure returns (bytes32 result) {
    bytes memory tempEmptyStringTest = bytes(source);
    if (tempEmptyStringTest.length == 0) {
      return 0x0;
    }

    assembly {
      result := mload(add(source, 32))
    }
  }

  function bytes32ToString (bytes32 x) internal pure returns (string) {
    bytes memory bytesString = new bytes(32);
    uint charCount = 0;
    for (uint j  = 0; j < 32; j++) {
      byte char = byte(bytes32(uint(x) * 2 ** (8 * j)));
      if (char != 0) {
        bytesString[charCount] = char;
        charCount++;
      }
    }
    bytes memory bytesStringTrimmed = new bytes(charCount);
    for (j = 0; j < charCount; j++) {
      bytesStringTrimmed[j] = bytesString[j];
    }
    return string(bytesStringTrimmed);

  }

  function compareStrings (string a, string b) internal pure returns (bool){
    return keccak256(a) == keccak256(b);
  }

  function stringToAddress(string _a) internal pure returns (address){
    bytes memory tmp = bytes(_a);
    uint160 iaddr = 0;
    uint160 b1;
    uint160 b2;
    for (uint i=2; i<2+2*20; i+=2){
      iaddr *= 256;
      b1 = uint160(tmp[i]);
      b2 = uint160(tmp[i+1]);
      if ((b1 >= 97)&&(b1 <= 102)) b1 -= 87;
      else if ((b1 >= 48)&&(b1 <= 57)) b1 -= 48;
      if ((b2 >= 97)&&(b2 <= 102)) b2 -= 87;
      else if ((b2 >= 48)&&(b2 <= 57)) b2 -= 48;
      iaddr += (b1*16+b2);
    }
    return address(iaddr);
  }
}
