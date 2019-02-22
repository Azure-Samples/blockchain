namespace PatternCodeGen.CodeGen
{
    public partial class RegistryContractGen : CodeGen
    {
        public string UtilsGen()
        {
            return
$@"    /******************* Utils functions *******************************************/
    function stringToBytes32(string memory source) public pure returns (bytes32 result) {{
        bytes memory tempEmptyStringTest = bytes(source);
        if (tempEmptyStringTest.length == 0) {{
            return 0x0;
        }}

        assembly {{
            result := mload(add(source, 32))
        }}
    }}
    
    function bytes32ToString (bytes32 x) public pure returns (string memory result) {{
        bytes memory bytesString = new bytes(32);
        uint charCount = 0;
        for (uint j  = 0; j < 32; j++) {{
            byte char = byte(bytes32(uint(x) * 2 ** (8 * j)));
            if (char != 0) {{
                bytesString[charCount] = char;
                charCount++;
            }}
        }}
        bytes memory bytesStringTrimmed = new bytes(charCount);
        for (uint j = 0; j < charCount; j++) {{
            bytesStringTrimmed[j] = bytesString[j];
        }}
        return string(bytesStringTrimmed);
    }}

    function stringToAddress(string memory _a) public pure returns (address _parsedAddress) {{
        bytes memory tmp = bytes(_a);
        uint160 iaddr = 0;
        uint160 b1;
        uint160 b2;
        for (uint i = 2; i < 2 + 2 * 20; i += 2) {{
            iaddr *= 256;
            b1 = uint160(uint8(tmp[i]));
            b2 = uint160(uint8(tmp[i + 1]));
        
            if ((b1 >= 97) && (b1 <= 102)) {{
                b1 -= 87;
            }} else if ((b1 >= 65) && (b1 <= 70)) {{
                b1 -= 55;
            }} else if ((b1 >= 48) && (b1 <= 57)) {{
                b1 -= 48;
            }}

            if ((b2 >= 97) && (b2 <= 102)) {{
                b2 -= 87;
            }} else if ((b2 >= 65) && (b2 <= 70)) {{
                b2 -= 55;
            }} else if ((b2 >= 48) && (b2 <= 57)) {{
                b2 -= 48;
            }}
            iaddr += (b1 * 16 + b2);
        }}
        return address(iaddr);
    }}
";
        }
    }
}
