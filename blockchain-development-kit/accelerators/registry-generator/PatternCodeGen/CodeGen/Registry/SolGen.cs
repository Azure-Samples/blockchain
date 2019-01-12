using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace PatternCodeGen.CodeGen
{
    public partial class RegistryContractGen : CodeGen
    {
        string _ItemStruct;
        string _propertyCommaList;
        string RegisterItemFuncCallArgList = string.Empty;
        private void SolGenInit()
        {
            _ItemStruct = _ItemName + "Struct";
            _propertyCommaList = getPropertyCommaList(true, false, string.Empty);
        }

        private string getPropertyCommaList(bool IncludesContractAddress, bool IsIndexOnly, string s, bool EmitType = true, string underscore = "_")
        {
            var paramList = new List<string>();
            if (!s.Equals(string.Empty, StringComparison.Ordinal))
                paramList.Add(s);

            var whereResult =
                ((RegistryTemplate)inputJSON).Properties
                .Where(prop =>
                    !(!IncludesContractAddress && prop.PropertyName.Equals("ContractAddress", StringComparison.Ordinal)) &&
                    !(IsIndexOnly && prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index)
                    );
            if (EmitType)
            {
                paramList.AddRange(whereResult.Select(prop => $"{prop.PropertyDataType} {underscore}{prop.PropertyName}"));
            }
            else
            {
                paramList.AddRange(whereResult.Select(prop => $"{underscore}{prop.PropertyName}"));
            }
            var paramString = string.Join(", ", paramList);
            return paramString;
        }

        private string PropertyDepenentDeclarations()
        {
            string codeString =
$@"struct {_ItemStruct} {{
";
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index) continue;
                codeString +=
$@"    {prop.PropertyDataType} {prop.PropertyName};
";
            }

            codeString +=
$@"    uint Index;
}}
";
            codeString += "\n";
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index) continue;
                codeString +=
$@"{prop.PropertyDataType}[] private {_ItemName}{prop.PropertyName}Index;
";
            }

            codeString += "\n";
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index) continue;
                codeString +=
$@"mapping({prop.PropertyDataType} => {_ItemStruct}) private {_ItemName}{prop.PropertyName}Lookup;
";
            }

            codeString += "\n";
            codeString +=
$@"event LogNew{_ItemName} ({getPropertyCommaList(true, true, string.Empty)}, uint index);
event LogUpdate{_ItemName} ({getPropertyCommaList(true, true, string.Empty)}, uint index);
";

            return addIndentation(codeString, 1);
        }

        private string potentiallyConvertToMemoryType(string type)
        {
            if (type == "string")
            {
                return type + " memory";
            }
            else
            {
                return type;
            }
        }
        private string IsRegisteredPropertyFunctions()
        {
            string codeString = string.Empty;
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index) continue;
                codeString +=
$@"//Lookup to see if a contract address for a {_ItemName} contract is already registered
function isRegistered{_ItemName}{prop.PropertyName}({prop.PropertyDataType} {_ItemName}{prop.PropertyName})
public view
returns(bool isRegistered)
{{
    if({_ItemName}{prop.PropertyName}Index.length == 0) return false;
    {potentiallyConvertToMemoryType(prop.PropertyDataType)} var1 = {_ItemName}{prop.PropertyName}Index[{_ItemName}{prop.PropertyName}Lookup[{_ItemName}{prop.PropertyName}].Index];
    {potentiallyConvertToMemoryType(prop.PropertyDataType)} var2 = {_ItemName}{prop.PropertyName};
    return (keccak256(abi.encodePacked(var1)) == keccak256(abi.encodePacked(var2)));
}}
";
                codeString += "\n";
            }

            return addIndentation(codeString, 1);
        }

        private string RegisterFunction()
        {
            string codeString =
$@"function Register{_ItemName}({getPropertyCommaList(true, true, string.Empty)}) 
public
{{";
            codeString +=
$@"    if (isRegistered{_ItemName}{contractAddressPropertyName}(_{contractAddressPropertyName})) revert();
";

            foreach (var prop1 in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop1.IndexType != IndexType_t.PrimaryIndex && prop1.IndexType != IndexType_t.Index) continue;
                codeString +=
$@"
    //Add lookup by {prop1.PropertyName}
";
                foreach (var prop2 in ((RegistryTemplate)inputJSON).Properties)
                {
                    if (prop2.IndexType != IndexType_t.PrimaryIndex && prop2.IndexType != IndexType_t.Index) continue;
                    codeString +=
$@"    {_ItemName}{prop1.PropertyName}Lookup[_{prop1.PropertyName}].{prop2.PropertyName} = _{prop2.PropertyName};
";
                }
                codeString +=
$@"    {_ItemName}{prop1.PropertyName}Lookup[_{prop1.PropertyName}].Index = {_ItemName}{prop1.PropertyName}Index.push(_{prop1.PropertyName}) - 1;
";
            }

            string propertyCommaListWithoutType = getPropertyCommaList(true, true, string.Empty, false);
            propertyCommaListWithoutType +=
$@",
{_ItemName}{contractAddressPropertyName}Lookup[_{contractAddressPropertyName}].Index";
            codeString +=
$@"    emit LogNew{_ItemName}(
{addIndentation(propertyCommaListWithoutType, 3)}
    );
    ContractUpdated(""Register{_ItemName}"");
}}
";
            string Func32ParamList = string.Empty;
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index) continue;
                if (!Func32ParamList.Equals(string.Empty, StringComparison.Ordinal))
                {
                    Func32ParamList += ", ";
                    RegisterItemFuncCallArgList += ",\n        ";
                }
                if (prop.PropertyDataType.Equals("string", StringComparison.Ordinal))
                {
                    Func32ParamList += "bytes32" + " _" + prop.PropertyName;
                    RegisterItemFuncCallArgList += "bytes32ToString(_" + prop.PropertyName + ")";
                }
                else
                {
                    Func32ParamList += prop.PropertyDataType + " _" + prop.PropertyName;
                    RegisterItemFuncCallArgList += "_" + prop.PropertyName;
                }
            }
            codeString +=
$@"function Register{_ItemName}32({Func32ParamList}) 
public
{{
    return Register{_ItemName} (
        {RegisterItemFuncCallArgList});
}}
";
            return addIndentation(codeString, 1);
        }

        private string GetItemByPropertyFunctions()
        {
            string codeString = string.Empty;
            string pType, convertion1, convertion2;
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index
                    || prop.PropertyName == contractAddressPropertyName
                ) continue;
                if (prop.PropertyDataType == "string")
                {
                    pType = "string";
                    convertion1 = string.Empty;
                    convertion2 = string.Empty;
                }
                else if (prop.PropertyDataType == "bytes32")
                {
                    pType = "string";
                    convertion1 = "bytes32ToString";
                    convertion2 = "stringToBytes32";
                }
                else
                {
                    pType = prop.PropertyDataType;
                    convertion1 = string.Empty;
                    convertion2 = string.Empty;
                }
                codeString +=
$@"
function get{prop.PropertyName}ByAddress(address {_ItemName}{contractAddressPropertyName})
public view
returns({pType} {_ItemName}{prop.PropertyName})
{{
    if(!isRegistered{_ItemName}{contractAddressPropertyName}({_ItemName}{contractAddressPropertyName})) revert(); 
    return {convertion1}({_ItemName}{contractAddressPropertyName}Lookup[{_ItemName}{contractAddressPropertyName}].{prop.PropertyName});
}}

function getAddressBy{prop.PropertyName}({pType} {_ItemName}{prop.PropertyName})
public view
returns(address {_ItemName}{contractAddressPropertyName})
{{
    {potentiallyConvertToMemoryType(prop.PropertyDataType)} idx = {convertion2}({_ItemName}{prop.PropertyName});
    if(!isRegistered{_ItemName}{prop.PropertyName}(idx)) revert(); 
    return {_ItemName}{prop.PropertyName}Lookup[idx].{contractAddressPropertyName};
}}
";

                if (prop.PropertyDataType == "string")
                {
                    pType = "bytes32";
                    convertion1 = "stringToBytes32";
                    convertion2 = "bytes32ToString";
                }
                else if (prop.PropertyDataType == "bytes32")
                {
                    pType = "bytes32";
                    convertion1 = string.Empty;
                    convertion2 = string.Empty;
                }
                if (pType == "bytes32")
                {
                    codeString +=
$@"
function get{prop.PropertyName}ByAddress32(address {_ItemName}{contractAddressPropertyName})
public view
returns({pType} {_ItemName}{prop.PropertyName})
{{
    if(!isRegistered{_ItemName}{contractAddressPropertyName}({_ItemName}{contractAddressPropertyName})) revert(); 
    return {convertion1}({_ItemName}{contractAddressPropertyName}Lookup[{_ItemName}{contractAddressPropertyName}].{prop.PropertyName});
}}

function getAddressBy{prop.PropertyName}32({pType} {_ItemName}{prop.PropertyName})
public view
returns(address {_ItemName}{contractAddressPropertyName})
{{
    {potentiallyConvertToMemoryType(prop.PropertyDataType)} idx = {convertion2}({_ItemName}{prop.PropertyName});
    if(!isRegistered{_ItemName}{prop.PropertyName}(idx)) revert(); 
    return {_ItemName}{prop.PropertyName}Lookup[idx].{contractAddressPropertyName};
}}
";
                }

            }
            return addIndentation(codeString, 1); ;
        }

        protected override void SolGen(string outputPath)
        {
            string filePath;
            string outputString =
$@"pragma solidity ^0.4.20;
import ""./{_ItemContractName}.sol"";
contract WorkbenchBase {{
    event WorkbenchContractCreated(string applicationName, string workflowName, address originatingAddress);
    event WorkbenchContractUpdated(string applicationName, string workflowName, string action, address originatingAddress);

    string internal ApplicationName;
    string internal WorkflowName;

    constructor(string applicationName, string workflowName) internal {{
        ApplicationName = applicationName;
        WorkflowName = workflowName;
    }}

    function ContractCreated() internal {{
        emit WorkbenchContractCreated(ApplicationName, WorkflowName, msg.sender);
    }}

    function ContractUpdated(string action) internal {{
        emit WorkbenchContractUpdated(ApplicationName, WorkflowName, action, msg.sender);
    }}
}}

contract {_RegistryContracyName} is WorkbenchBase(""{_ApplicationName}"", ""{_RegistryContracyName}"") {{
    enum StateType {{ Created, Open, Closed}}
    StateType public State;                       

    {_ItemStruct}[] public {_ItemName}s;

    string public Name;
    string public Description;

{PropertyDepenentDeclarations()}
    constructor(string _Name, string _Description) public {{
        Name = _Name;
        Description = _Description;
        State = StateType.Created;
        ContractCreated();
    }}

    function OpenRegistry() public
    {{
        State = StateType.Open;        
        ContractUpdated(""OpenRegistry"");
    }}

    function CloseRegistry() public
    {{
        State = StateType.Closed;
        ContractUpdated(""CloseRegistry"");
    }}

{IsRegisteredPropertyFunctions()}
{RegisterFunction()}
{GetItemByPropertyFunctions()}
    
    function getNumberOfRegistered{_ItemName}s() 
    public
    constant
    returns(uint count)
    {{
        return {_ItemName}{contractAddressPropertyName}Index.length;
    }}

    function get{_ItemName}AtIndex(uint index)
    public
    constant
    returns(address {_ItemName}{contractAddressPropertyName})
    {{
        return {_ItemName}{contractAddressPropertyName}Index[index];
    }}

{UtilsGen()}
}}";
            filePath = Path.Combine(outputPath, _ApplicationName + ".sol");
            File.WriteAllText(filePath, outputString);

            outputString = SolGenItem();
            filePath = Path.Combine(outputPath, _ItemContractName + ".sol");
            File.WriteAllText(filePath, outputString);

            /*
            outputString = UtilsGen();
            filePath = Path.Combine(outputPath, "Utils.sol");
            File.WriteAllText(filePath, outputString);
            */
        }

        #region Item specific code gen
        protected string SolGenItem()
        {
            string s =
$@"pragma solidity ^0.4.20;
import ""./{_ApplicationName}.sol"";

contract {_ItemContractName} is WorkbenchBase(""{_ApplicationName}"", ""{_ItemContractName}"") {{

    // Registry
    {_RegistryContracyName} My{_ItemName}Registry;
    address public RegistryAddress;

    //Common Properties
    //Set of States
    enum StateType {{ Active, Retired }}
    StateType public State;
    string public RetirementRecordedDateTime;

{PropertyDepenentDeclarationsForItem()}
{Ownership_ContractStateVars}
{Media_ContractStateVars}

{ConstructorFunctionForItem()}

{RegisterFunctionForItem()}

{AssignRegistryForItem()}

{AddMediaForItem()}

{RetireFunctionForItem()}

{UtilsGen()}
}}";
            return s;
        }

        private string RetireFunctionForItem()
        {
            var codeString =
                $@"
//Retire Function for {_ItemName}
function  Retire(string retirementRecordedDateTime) public {{
    RetirementRecordedDateTime = retirementRecordedDateTime;
    State = StateType.Retired;
    ContractUpdated(""Retire"");
}}
";
            return addIndentation(codeString, 1);
        }

        private string RegisterFunctionForItem()
        {
            if (IsRegistryKnown && (IsOwnerKnown ^ IsOwnershipTypeNone))
            {
                return string.Empty;
            }

            string funcName, argList, ownership_varAssignments;
            if (!IsRegistryKnown && (IsOwnerKnown ^ IsOwnershipTypeNone))
            {
                funcName = $"Register{_ItemName}";
                argList = string.Empty;
                ownership_varAssignments = string.Empty;
            }
            else
            {
                funcName = "AssignOwnerAndRegister";
                argList = Ownership_ConstructorParamList;
                ownership_varAssignments = Ownership_VarAssignments;
            }
            var codeString =
                $@"
//funcName Function for {_ItemName}
function {funcName}({argList}) public {{
    
    // only assign if there isn't one assigned already
    if (RegistryAddress != 0x0) revert(); 
    {ownership_varAssignments}
    if (State != StateType.Active) revert();
    
    My{_ItemName}Registry = {_RegistryContracyName}(RegistryAddress);

    {RegisterItem()}
    State = StateType.Active;
    ContractUpdated(""{funcName}"");
}}
";
            return addIndentation(codeString, 1);
        }

        private string AssignRegistryForItem()
        {
            if (((RegistryTemplate)inputJSON).IsRegistryAddressKnownAtCreation)
                return string.Empty;
            string output = $@"
function AssignRegistry(address _registryAddress) public
{{
    if (RegistryAddress != 0x0) revert(); 
    RegistryAddress = _registryAddress;
    ContractUpdated(""AssignRegistry"");
}}";
            return addIndentation(output, 1);
        }

        private string AddMediaForItem()
        {
            if (MediaSetting.AssociatedMedia == AssociatedMedia_t.None || MediaSetting.AssociatedMedia == AssociatedMedia_t.SingleElementAtCreation)
                return string.Empty;
            string output = $@"
function AddMedia({Media_ConstructorParamList}) public
{{
{Media_VarAssignments}
    ContractUpdated(""AddMedia"");
}}";
            return addIndentation(output, 1);
        }

        private object ConstructorFunctionForItem()
        {
            string codeString =
                $@"
//Constructor Function for {_ItemName}
//-------------------------------------
";
            var paramString = getPropertyCommaList(false, false, IsRegistryKnown?"string _RegistryAddress":string.Empty);
            string Media_ConstructorParamList_Or_Empty = (MediaSetting.AssociatedMedia == AssociatedMedia_t.SingleElementAtCreation) ?
                                                              $", {Media_ConstructorParamList}" : string.Empty;
            string Media_VarAssignments_Or_Empty = (MediaSetting.AssociatedMedia == AssociatedMedia_t.SingleElementAtCreation) ?
                                                              $"{Media_VarAssignments}" : string.Empty;
            codeString += $@"
constructor({paramString}{Media_ConstructorParamList_Or_Empty}";
            if (IsOwnerKnown)
                codeString += $@", {Ownership_ConstructorParamList}";
            codeString += $@") public {{
{Media_VarAssignments_Or_Empty}
";
            if (IsOwnerKnown)
                codeString += $@"
{Ownership_VarAssignments}
";

            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.PropertyName.Equals("ContractAddress", StringComparison.Ordinal))
                    continue;
                codeString +=
$@"    {prop.PropertyName} = _{prop.PropertyName};
     
";
            }
            codeString += IsRegistryKnown ? $"    RegistryAddress = stringToAddress(_RegistryAddress);\n" : string.Empty;
            if (IsRegistryKnown && (IsOwnerKnown ^ IsOwnershipTypeNone))
            {
                codeString +=
$@"
     My{_ItemName}Registry = {_RegistryContracyName}(RegistryAddress);
     {RegisterItem()}
";
            }

            codeString +=
$@"    ContractCreated();
}}";
            return addIndentation(codeString,1);
        }

        private object RegisterItem()
        {
            string argList = RegisterItemFuncCallArgList.Replace("_", string.Empty);
            argList = argList.Replace("bytes32ToString", $"stringToBytes32");
            argList = argList.Replace("ContractAddress", "address(this)");
            return
$@"
    My{_ItemName}Registry.Register{_ItemName}32(
        {argList});
";
        }

        private object PropertyDepenentDeclarationsForItem()
        {
            string codeString =
                    $@"
    //{_ItemName} specific property declarations
    //-------------------------------------------
";
            foreach (var prop in ((RegistryTemplate)inputJSON).Properties)
            {
                if (prop.PropertyName.Equals("ContractAddress", StringComparison.Ordinal))
                    continue;

                codeString +=
$@"    {prop.PropertyDataType} public {prop.PropertyName};
";
            }

            return codeString;
        }
        #endregion 
    }
}
