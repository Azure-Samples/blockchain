pragma solidity ^0.4.20;

contract FileRegistry {
    enum StateType { Created, Open, Closed}
    StateType public State;
    FileStruct[] public Files;
    mapping(string => FileStruct) private FileIdLookup;
    mapping(address => FileStruct) private FileContractAddressLookup;
    string public Name;
    string public Description;

    struct FileStruct { 
        address FileContractAddress;
        string FileId; 
        uint Index;
    }
    address[] private FileAddressIndex;
    string[] private FileIdIndex;

    event LogNewFile   (address indexed FileContractAddress, uint index, bytes32 FileId);
    event LogUpdateFile(address indexed FileContractAddress, uint index, bytes32 FileId);

    function FileRegistry(string name, string description) public {
        Name = name;
        Description = description;
        State = StateType.Created;

    }
    function OpenRegistry() public
    {

        State = StateType.Open;        
        
    }

    function CloseRegistry() public
    {

        State = StateType.Closed;
        

    }
    //Lookup to see if a contract address for a File contract is already registered
    function IsRegisteredFileContractAddress(address FileContractAddress)
    public 
    constant
    returns(bool isRegistered) 
  {
        if(FileAddressIndex.length == 0) return false;
        
        return (FileAddressIndex[FileContractAddressLookup[FileContractAddress].Index] == FileContractAddress);
    }


    //Look up to see if this File reg is registered
    function IsRegisteredFileId(bytes32 FileId)
    public 
    constant
    returns(bool isRegistered) 
  {
        if(FileIdIndex.length == 0) return false;
        string memory FileIdString = bytes32ToString(FileId);
        string memory FileIdInternalString = FileIdIndex[FileIdLookup[FileIdString].Index] ;
        return (compareStrings(FileIdInternalString, FileIdString));
      
    }
    
    //Look up to see if this File reg is registered
    function IsRegisteredFileId(string FileId)
    public 
    constant
    returns(bool isRegistered) 
  {
        if(FileIdIndex.length == 0) return false;
    
        string memory FileIdInternalString = FileIdIndex[FileIdLookup[FileId].Index] ;
        
        return (compareStrings(FileIdInternalString, FileId));
    
      
      
    }

    function RegisterFile32(
        address FileContractAddress, 
        bytes32 FileId 
    ) 
    public
    returns(uint index)
  {
        if(State != StateType.Open) revert();
        if(IsRegisteredFileContractAddress(FileContractAddress)) revert(); 
   
        //Add lookup by address
        FileContractAddressLookup[FileContractAddress].FileContractAddress = FileContractAddress;
        string memory FileIdString = bytes32ToString(FileId);
        FileContractAddressLookup[FileContractAddress].FileId = FileIdString;
        FileContractAddressLookup[FileContractAddress].Index     = FileAddressIndex.push(FileContractAddress)-1;
   
        //Add look up by reg number

        FileIdLookup[FileIdString].FileContractAddress = FileContractAddress;
    
        FileIdLookup[FileIdString].FileId = FileIdString;
        FileIdLookup[FileIdString].Index  = FileIdIndex.push(FileIdString)-1;
   
        //LogNewFile(
          //  FileContractAddress, 
         //   FileContractAddressLookup[FileContractAddress].Index, 
         //   FileId);
    


        return FileAddressIndex.length-1;
    }

    function RegisterFile(
        address FileContractAddress, 
        string FileId 
    ) 
    public
   // returns(uint index)
  {
        if (State != StateType.Open) revert();
      
        if(IsRegisteredFileContractAddress(FileContractAddress)) revert(); 
   
        //Add lookup by address
        FileContractAddressLookup[FileContractAddress].FileContractAddress = FileContractAddress;

        FileContractAddressLookup[FileContractAddress].FileId = FileId;
        FileContractAddressLookup[FileContractAddress].Index     = FileAddressIndex.push(FileContractAddress)-1;
   
        //Add look up by reg number
        //string FileIdString = bytes32ToString(FileId);
        FileIdLookup[FileId].FileContractAddress = FileContractAddress;
    
        FileIdLookup[FileId].FileId = FileId;
        FileIdLookup[FileId].Index  = FileIdIndex.push(FileId)-1;
   
        //LogNewFile(
            //FileContractAddress, 
           // FileContractAddressLookup[FileContractAddress].Index, 
            //stringToBytes32(FileId));
    
        
     //   return FileAddressIndex.length-1;
    }
    
    function GetFileByAddress(address FileContractAddress)
    public 
    constant
    returns(bytes32 FileId)
  {
        if(!IsRegisteredFileContractAddress(FileContractAddress)) revert(); 
        return stringToBytes32(FileContractAddressLookup[FileContractAddress].FileId);
    } 
    
    function GetFileByFileId(bytes32 FileId)
    public 
    constant
    returns(address FileContractAddress)
  {
        string memory FileIdString = bytes32ToString(FileId);
        if(!IsRegisteredFileId(FileId)) revert();

        return  FileIdLookup[FileIdString].FileContractAddress;
    } 

    function GetNumberOfRegisteredFiles() 
    public
    constant
    returns(uint count)
  {
        return FileAddressIndex.length;
    }

    function GetFileAtIndex(uint index)
    public
    constant
    returns(address FileContractAddress)
  {
        return FileAddressIndex[index];
    }
    
    //-----------------------------------------------------
    // Supporting Functions
    //-----------------------------------------------------
    
    function  stringToBytes32(string memory source) private returns (bytes32 result) {
        bytes memory tempEmptyStringTest = bytes(source);
        if (tempEmptyStringTest.length == 0) {
            return 0x0;
        }

        assembly {
            result := mload(add(source, 32))
        }
    }
    
    function bytes32ToString (bytes32 x)  private returns (string) {
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
    function compareStrings (string a, string b) view private returns (bool){
        return keccak256(a) == keccak256(b);
    }

}

contract File {

// Registry
FileRegistry MyFileRegistry;
address public RegistryAddress;


//File Proeprties
//Set of States
enum StateType { Active, Deleted}
StateType public  State;
address public BlockchainAgent; // agent that processed the File
string public FileId; //identifier for the File, stored off chain
//address public Owner; //identifier for the Owner of the File, stored off chain
string public Location; //The location of the file, e.g. URI
string public FileHash; // text here, but could be an Id
string public FileMetadataHash; // text here, but coudl be an Id
string public ContentType; // text, represents the color of the File
string public Etag; // text, represents the color of the File
string public ProcessedDateTime; 
string public DeletionRecordedDateTime;


function File (string registryAddress, string fileId, string location, string fileHash, string fileMetadataHash, string contentType, string etag, string processedDateTime) public {
    FileId = fileId;
    Location = location;
    FileHash = fileHash;
    FileMetadataHash = fileMetadataHash;
    ContentType = contentType;
    Etag = etag;
    ProcessedDateTime = processedDateTime;
    RegistryAddress = stringToAddress(registryAddress);

    MyFileRegistry = FileRegistry(RegistryAddress);

   //If this file id is already registered, revert
   if (MyFileRegistry.IsRegisteredFileId(stringToBytes32(FileId))) revert();

    //NOTE - Can hardcode registry if previously registry previously deplyed to the chain to avoid having to call AssignRegistry;
    MyFileRegistry.RegisterFile32(address(this), stringToBytes32(FileId));
 
    State = StateType.Active;
    
}


function RegisterFile(address registryAddress) public {
    
    // only assign if there isn't one assigned already
    if (RegistryAddress != 0x0) revert(); 
    RegistryAddress = registryAddress;
    
    if (State != StateType.Active) revert();
    
    MyFileRegistry = FileRegistry(RegistryAddress);
    
    //Check to see if the File is already registered and 
    //also check to see if this File can have its ownership transferred

    if (MyFileRegistry.IsRegisteredFileContractAddress(address(this))) revert();
    
    

    MyFileRegistry.RegisterFile32(address(this), stringToBytes32(FileId));


}

 function  Delete(string deletionProcessedDateTime) public{
    DeletionRecordedDateTime = deletionProcessedDateTime;
    State = StateType.Deleted;
}

//-----------------------------------------------------
// Supporting Functions
//-----------------------------------------------------
    
    function  stringToBytes32(string memory source) private returns (bytes32 result) {
        bytes memory tempEmptyStringTest = bytes(source);
        if (tempEmptyStringTest.length == 0) {
            return 0x0;
        }

        assembly {
            result := mload(add(source, 32))
        }
    }
    
    function bytes32ToString (bytes32 x)  private returns (string) {
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
    function compareStrings (string a, string b) view private returns (bool){
        return keccak256(a) == keccak256(b);
    }
function stringToAddress(string _a) internal returns (address){
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