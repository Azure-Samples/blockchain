using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;
using Newtonsoft.Json.Schema;
using System.ComponentModel.DataAnnotations;
using System.IO;
using System.IO.Compression;
using System.Web.Mvc;


/********************************************
 * Consider the following eight combination of input json
 *
 * RegistryKnown|OwnerKnown|OwnerType| ==> |       ItemConstructor                       |AssignRegistry|   Register or AssignOwnerOrRegister
 * ----------------------------------------------------------------------------------------------------------------------
 *   Yes        |   Yes    |  None   | ==> |    This is an invalid input
 * ----------------------------------------------------------------------------------------------------------------------
 *   Yes        |   Yes    | not none| ==> |  All args; call register32                  | no need      |   no need
 * ----------------------------------------------------------------------------------------------------------------------
 *   Yes        |   No     |  None   | ==> | All args - owner args; call register32      | no need      |   no need
 * ----------------------------------------------------------------------------------------------------------------------
 *   Yes        |   No     | not none| ==> |All args - owner args; don't call register32 | no need      |  AssignOwnerOrRegister
 * ----------------------------------------------------------------------------------------------------------------------
 *   No         |  Yes     |  None   | ==> |    This is an invalid input
 * ----------------------------------------------------------------------------------------------------------------------
 *   No         |   Yes    | not none| ==> |  All args - regi arg; don't call register32 |   need      |   Register
 * ----------------------------------------------------------------------------------------------------------------------
 *   No         |   No     |  None   | ==> | All args - owner args - regi arg;           |   need      |   Register
 *              |          |         |     |           don't call register32             |
 * ----------------------------------------------------------------------------------------------------------------------
 *   No         |   No     | not none| ==> | All args - owner args - regi arg;           |   need      |  AssignOwnerOrRegister
 *              |          |         |     |           don't call register32             |
 * ----------------------------------------------------------------------------------------------------------------------
 *******************************************/
namespace PatternCodeGen.CodeGen
{
    public enum IndexType_t {None, PrimaryIndex, Index};
    public enum OwnershipType_t { None, OnChain, OffChain };
    public enum AssociatedMedia_t { None, SingleElementAtCreation, SingleElementAfterCreation /*,MultipleElements*/ };
    public enum ApprovalProcessForRegistrationRequests_t { None, Basic, Advanced }
    public class Ownership_t
    {
        public OwnershipType_t OwnershipType;
        public bool IsOwnerKnownAtCreation;
        // Not yet supported          public bool IsOwnershipTransferrable;
    }
    public class Media_t
    {
        public AssociatedMedia_t AssociatedMedia;
        // Not yet supported           public AssociatedMedia_t ApprovalProcessMedia;
    }
    public class RegistryTemplate : Template
    {
        public bool IsRegistryAddressKnownAtCreation;
        // Not yet supported     public ApprovalProcessForRegistrationRequests_t ApprovalProcessForRegistrationRequests;
        public Ownership_t Ownership;
        public Media_t Media;
        public IList<RegProperty> Properties { get; set; }
    }

  
    public class RegProperty
    {
        [JsonProperty(Required = Required.Always)]
        [RegularExpression(@"^[_A-Za-z]([A-Za-z_0-9]*)$")]
        [MaxLength(50)]
        public string PropertyName { get; set; }
        [JsonProperty(Required = Required.Always)]
        [RegularExpression(@"^[string|address|uint|int|bool]$")]
        [MaxLength(50)]
        public string PropertyDataType { get; set; }
        [System.ComponentModel.DefaultValue(IndexType_t.None)]
        public IndexType_t IndexType { get; set; }
    }

    public partial class RegistryContractGen : CodeGen
    {
        string contractAddressPropertyName = "ContractAddress";
        bool IsRegistryKnown, IsOwnerKnown, IsOwnershipTypeNone;
        Media_t MediaSetting;
        string Media_ContractStateVars, Media_ConstructorParamList, Media_VarAssignments, Media_JSONParamList;
        string Ownership_ContractStateVars, Ownership_ConstructorParamList, Ownership_VarAssignments, Ownership_JSONParamList;
        public RegistryContractGen(string __inputJSONString): base (__inputJSONString, typeof(RegistryTemplate))
        {
        }

        protected void MediaInit()
        {
            Media_ContractStateVars = Media_ConstructorParamList = Media_VarAssignments = Media_JSONParamList = string.Empty;
            if (((RegistryTemplate)inputJSON).Media.AssociatedMedia == AssociatedMedia_t.None)
                return;
            Media_ContractStateVars = addIndentation($@"
string public MediaUri; // URI for an image of the item
string public MediaHash; // Hash of the image of the item so changes can be detected.
string public MediaMetadataHash; // Hash of the image of the item so changes can be detected.", 1);

            Media_ConstructorParamList = "string _MediaUri, string _MediaHash, string _MediaMetadataHash";

            Media_VarAssignments = addIndentation($@"
MediaUri = _MediaUri;
MediaHash = _MediaHash;
MediaMetadataHash = _MediaMetadataHash;", 1);

            Media_JSONParamList = addIndentation(
$@"{{
    ""Name"": ""_MediaUri"",
    ""Description"": ""Media Uri"",
    ""DisplayName"": ""MediaUri"",
    ""Type"": {{
        ""Name"": ""string""
    }}
}},
{{
    ""Name"": ""_MediaHash"",
    ""Description"": ""Media Hash"",
    ""DisplayName"": ""MediaHash"",
    ""Type"": {{
        ""Name"": ""string""
    }}
}},
{{
    ""Name"": ""_MediaMetadataHash"",
    ""Description"": ""Media Metadata Hash"",
    ""DisplayName"": ""MediaMetadataHash"",
    ""Type"": {{
        ""Name"": ""string""
    }}
}}", 3);

        }
        protected void OwnershipInit()
        {
            Ownership_ContractStateVars = Ownership_ConstructorParamList = Ownership_VarAssignments = Ownership_JSONParamList = string.Empty;
            if (IsOwnershipTypeNone)
                return;
            Ownership_ContractStateVars = addIndentation($@"
address public Owner; //identifier for the Owner of the Item, stored off chain
string public OwnerDetailHash; //hash of the owner details for the Item, stored off chain
", 1);

            Ownership_ConstructorParamList = "address _Owner, string _OwnerDetailHash";

            Ownership_VarAssignments = addIndentation($@"
Owner = _Owner;
OwnerDetailHash = _OwnerDetailHash;", 1);

            Ownership_JSONParamList = addIndentation($@"
{{
    ""Name"": ""_Owner"",
    ""Description"": ""Owner"",
    ""DisplayName"": ""Owner"",
    ""Type"": {{
        ""Name"": ""address""
    }}
}},
{{
    ""Name"": ""_OwnerDetailHash"",
    ""Description"": ""OwnerDetailHash"",
    ""DisplayName"": ""OwnerDetailHash"",
    ""Type"": {{
        ""Name"": ""string""
    }}
}}
", 3);
        }
        protected override Template Deserialize(JSchemaValidatingReader validatingReader)
        {
            var serializer = new JsonSerializer();
            var parsedJSON = serializer.Deserialize<RegistryTemplate>(validatingReader);
            if (parsedJSON.Properties.Any(x => x.PropertyName.Equals("ContractAddress", StringComparison.OrdinalIgnoreCase))
                || parsedJSON.Properties.Any(x => x.PropertyName.Equals("RetirementRecordedDateTime", StringComparison.OrdinalIgnoreCase)))
            {
                throw new Exception("Input JSON must not contain a property named ContractAddress or RetirementRecordedDateTime.");
            }
            IsRegistryKnown = parsedJSON.IsRegistryAddressKnownAtCreation;
            IsOwnerKnown = parsedJSON.Ownership.IsOwnerKnownAtCreation;
            IsOwnershipTypeNone = (parsedJSON.Ownership.OwnershipType == OwnershipType_t.None);
            MediaSetting = parsedJSON.Media;
            if (IsOwnershipTypeNone && (parsedJSON.Ownership.IsOwnerKnownAtCreation /* || parsedJSON.Ownership.IsOwnershipTransferrable*/))
            {
                throw new Exception("When the ownership type is None, other ownership properties must be false");
            }
            return parsedJSON;
        }

        protected override void init()
        {
            if (!((RegistryTemplate)inputJSON).Properties.Any(x => (x.IndexType == IndexType_t.PrimaryIndex)))
            {
                throw new Exception("At least one property needs to set IsPrimaryIndex true.");
            }

            ((RegistryTemplate)inputJSON).Properties.Add(new RegProperty{ PropertyName = "ContractAddress", PropertyDataType = "address", IndexType = IndexType_t.Index });

            _ApplicationName = _ItemName + "Registry" + _Version;
            _RegistryContracyName = _ApplicationName;
            _ItemContractName = _ItemName + _Version;
            SolGenInit();
            MediaInit();
            OwnershipInit();
        }
    }
}
