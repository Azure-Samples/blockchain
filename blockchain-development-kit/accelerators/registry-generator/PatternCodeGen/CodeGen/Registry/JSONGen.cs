using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace PatternCodeGen.CodeGen
{
    public partial class RegistryContractGen : CodeGen
    {
        private string GenSpec_ParameterListOfRegisterFunction(bool IncludesContractAddress, bool IsIndexOnly, bool NeedUnderscore, string s, int indentlevel)
        {
            var paramList = new List<string>() { };
            string underscore = NeedUnderscore ? "_" : "";
            if (!s.Equals(string.Empty, StringComparison.Ordinal))
                paramList.Add(s);
            paramList.AddRange(
                ((RegistryTemplate)inputJSON).Properties
                .Where(prop =>
                    !(!IncludesContractAddress && prop.PropertyName.Equals("ContractAddress", StringComparison.Ordinal)) &&
                    !(IsIndexOnly && prop.IndexType != IndexType_t.PrimaryIndex && prop.IndexType != IndexType_t.Index)
                    )
                .Select(prop =>
$@"{{
    ""Name"": ""{underscore}{prop.PropertyName}"",
    ""Description"": ""{_ItemName} {prop.PropertyName}"",
    ""DisplayName"": ""{_ItemName}{prop.PropertyName}"",
    ""Type"": {{
        ""Name"": ""{prop.PropertyDataType}""
    }}
}}")
                );

            var paramString = string.Join(",\n", paramList);
            return addIndentation(paramString, indentlevel);
        }

        protected string GenSpec_ItemConstructor()
        {
            string output =
$@"        ""Constructor"": {{
          ""Parameters"": [
";
            if (IsRegistryKnown)
            {
                output +=
$@"            {{
              ""Name"": ""_RegistryAddress"",
              ""DisplayName"": ""Registry Address"",
              ""Description"": ""The address of the registry"",
              ""Type"": {{
                ""Name"": ""string""
              }}
            }},
";
            }

            string Media_JSONParamList_Or_Empty = (MediaSetting.AssociatedMedia == AssociatedMedia_t.SingleElementAtCreation) ?
                                                   $", {Media_JSONParamList}" : string.Empty;

            output +=
$@"{GenSpec_ParameterListOfRegisterFunction(false, false, true, string.Empty, 3)}{Media_JSONParamList_Or_Empty}";
            if (!IsOwnershipTypeNone && IsOwnerKnown)
            {
                output +=
$@",{Ownership_JSONParamList}
";
            }
            output +=
$@"          ]
        }},
";
            return output;
        }

        protected string GenSpec_RegisterFunctionForItemDeclaration()
        {
            if (IsRegistryKnown && (IsOwnerKnown ^ IsOwnershipTypeNone))
            {
                return string.Empty;
            }

            if (!IsRegistryKnown && (IsOwnerKnown ^ IsOwnershipTypeNone))
            {
                return
$@"          {{
            ""Name"": ""Register{_ItemName}"",
            ""DisplayName"": ""Register {_ItemName}"",
            ""Description"": ""Add a {_ItemName} to a registry."",
            ""Parameters"": [
            ]
          }},
";
            }
            else
            {
                return
$@"          {{
            ""Name"": ""AssignOwnerAndRegister"",
            ""DisplayName"": ""AssignOwnerAndRegister"",
            ""Description"": ""Assign Owner And Register"",
            ""Parameters"": [
            {Ownership_JSONParamList}
            ]
          }},
";
            }


        }

        protected string GenSpec_AssignRegistryFunctionForItemDeclaration()
        {
            if (IsRegistryKnown)
                return string.Empty;
            return
$@"          {{
            ""Name"": ""AssignRegistry"",
            ""DisplayName"": ""AssignRegistry"",
            ""Description"": ""Assign a registry to this {_ItemName} ."",
            ""Parameters"": [
              {{
                ""Name"": ""_registryAddress"",
                ""Description"": ""Registry Address"",
                ""DisplayName"": ""RegistryAddress"",
                ""Type"": {{
                  ""Name"": ""address""
                }}
              }}
            ]
          }},
";
        }

        protected string GenSpec_AddMediaFunctionForItemDeclaration()
        {
            if (MediaSetting.AssociatedMedia == AssociatedMedia_t.None || MediaSetting.AssociatedMedia == AssociatedMedia_t.SingleElementAtCreation)
                return string.Empty;
            return
$@"          {{
            ""Name"": ""AddMedia"",
            ""DisplayName"": ""AddMedia"",
            ""Description"": ""Add a media to this {_ItemName} ."",
            ""Parameters"": [
              {Media_JSONParamList}
            ]
          }},
";
        }

        protected override void JSONGen(string outputPath)
        {
            string outputString = 
  $@"{{
  ""ApplicationName"": ""{_ApplicationName}"",
  ""DisplayName"": ""{_ItemName} Registry {_Version}"",
  ""Description"": ""A contract to track registered {_ItemName}s"",
  ""ApplicationRoles"": [
    {{
      ""Name"": ""Registrar"",
      ""Description"": ""Registrar""
    }},
    {{
      ""Name"": ""Registrant"",
      ""Description"": ""Registrant""
    }},
    {{
      ""Name"": ""BlockchainAgent"",
      ""Description"": ""BlockchainAgent of the {_ItemName}""
    }}
  ],
  ""Workflows"": [
    {{
      ""Name"": ""{_RegistryContracyName}"",
      ""DisplayName"": ""{_ItemName} Registry"",
      ""Description"": ""Registry to track {_ItemName}s"",
      ""Initiators"": [""Registrar""],
      ""StartState"":  ""Created"",
      ""Properties"": [
        {{
          ""Name"": ""State"",
          ""DisplayName"": ""State"",
          ""Description"": ""Holds the state of the current scope"",
          ""Type"": {{
            ""Name"": ""state""
          }}
        }}
      ],
      ""Constructor"": {{
        ""Parameters"": [
            {{
              ""Name"": ""_Name"",
              ""Description"": ""The name of the registry."",
              ""DisplayName"": ""Registry Name"",
              ""Type"": {{
                ""Name"": ""string""
              }}
            }},
            {{
              ""Name"": ""_Description"",
              ""Description"": ""Description of the registry."",
              ""DisplayName"": ""Description"",
              ""Type"": {{
                ""Name"": ""string""
              }}
            }}        
        ]
      }},
      ""Functions"": [
        {{
          ""Name"": ""OpenRegistry"",
          ""DisplayName"": ""Open Registry"",
          ""Description"": ""Opens the registry for business."",
          ""Parameters"": [
            ]
        }},
        {{
            ""Name"": ""Register{_ItemName}"",
            ""DisplayName"": ""Register {_ItemName}"",
            ""Description"": ""Add a {_ItemName} to a registry."",
            ""Parameters"": [
{GenSpec_ParameterListOfRegisterFunction(true, true, true, string.Empty, 4)}
            ]
        }},
        {{
          ""Name"": ""CloseRegistry"",
          ""DisplayName"": ""Close Registry"",
          ""Description"": ""Closes the registry."",
          ""Parameters"": []
        }}
      ],
      ""States"": [
        {{
          ""Name"": ""Created"",
          ""DisplayName"": ""Registry Created"",
          ""Description"": ""The registry is created."",
          ""PercentComplete"": 40,
          ""Style"": ""Success"",
          ""Transitions"": [
            {{
              ""AllowedRoles"": [""Registrar""],
              ""AllowedInstanceRoles"": [],
              ""Description"": ""Open the Registry"",
              ""Function"": ""OpenRegistry"",
              ""NextStates"": [ ""Open"" ],
              ""DisplayName"": ""Registry opened.""
            }}
          ]
        }},
        {{
          ""Name"": ""Open"",
          ""DisplayName"": ""Registry Opened"",
          ""Description"": ""Retired"",
          ""PercentComplete"": 100,
          ""Style"": ""Success"",
          ""Transitions"": [
            {{
                ""AllowedRoles"": [""Registrant"", ""Registrar""],
                ""AllowedInstanceRoles"": [],
                ""Description"": ""register{_ItemName}"",
                ""Function"": ""Register{_ItemName}"",
                ""NextStates"": [ ""Open"" ],
                ""DisplayName"": ""{_ItemName} registered.""
            }},
            {{
                ""AllowedRoles"": [""Registrar""],
                ""AllowedInstanceRoles"": [],
                ""Description"": ""Close the registry."",
                ""Function"": ""CloseRegistry"",
                ""NextStates"": [ ""Closed"" ],
                ""DisplayName"": ""Registry closed.""
            }}
           ]
        }},
        {{
          ""Name"": ""Closed"",
          ""DisplayName"": ""Retired"",
          ""Description"": ""Retired"",
          ""PercentComplete"": 100,
          ""Style"": ""Success"",
          ""Transitions"": []
        }}
      ]
    }},
    {{
        ""Name"": ""{_ItemContractName}"",
        ""DisplayName"": ""{_ItemName}"",
        ""Description"": ""{_ItemName} being registered"",
        ""Initiators"": [""BlockchainAgent""],
        ""StartState"":  ""Active"",
        ""Properties"": [
            {{
                ""Name"": ""State"",
                ""DisplayName"": ""State"",
                ""Description"": ""Holds the state of the current scope"",
                ""Type"": {{
                  ""Name"": ""state""
                }}
            }}, 
{GenSpec_ParameterListOfRegisterFunction(false, false, false, string.Empty, 3)},
            {{
                ""Name"": ""RetirementRecordedDateTime"",
                ""DisplayName"": ""Retired Date"",
                ""Description"": ""The UTC Date and Time the file was processed"",
                ""Type"": {{
                ""Name"": ""string""
                }}
            }}
        ],
{GenSpec_ItemConstructor()}

        ""Functions"": [
{GenSpec_RegisterFunctionForItemDeclaration()}
{GenSpec_AssignRegistryFunctionForItemDeclaration()}
{GenSpec_AddMediaFunctionForItemDeclaration()}
          {{
            ""Name"": ""Retire"",
            ""DisplayName"": ""Retire {_ItemName}"",
            ""Description"": ""Change registry status to retire."",
            ""Parameters"": [
              {{
                ""Name"": ""retirementRecordedDateTime"",
                ""Description"": ""Date and time the agent processed the retirement"",
                ""DisplayName"": ""Retirement Recorded Date Time"",
                ""Type"": {{
                  ""Name"": ""string""
                }}
              }}
            ]
          }}
        ],
        ""States"": [
          {{
            ""Name"": ""Active"",
            ""DisplayName"": ""{_ItemName} Active"",
            ""Description"": ""{_ItemName} Active"",
            ""PercentComplete"": 40,
            ""Style"": ""Success"",
            ""Transitions"": [
              {{
                ""AllowedRoles"": [""BlockchainAgent""],
                ""AllowedInstanceRoles"": [],
                ""Description"": ""Retire {_ItemName}"",
                ""Function"": ""Retire"",
                ""NextStates"": [ ""Retired"" ],
                ""DisplayName"": ""{_ItemName} retired.""
              }}
  
            ]
          }},
          {{
            ""Name"": ""Retired"",
            ""DisplayName"": ""Retired"",
            ""Description"": ""Retired"",
            ""PercentComplete"": 100,
            ""Style"": ""Success"",
            ""Transitions"": []
          }}
        ]
      }}        
  ]
}}
";
            string jsonFilePath = Path.Combine(outputPath, _ApplicationName + ".json");
            File.WriteAllText(jsonFilePath, outputString);
        }

    }
}
