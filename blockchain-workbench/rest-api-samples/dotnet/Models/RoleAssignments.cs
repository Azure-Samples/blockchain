using System;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Workbench.Client.Models
{
    public class RoleAssignmentReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

        [JsonProperty("roleAssignments")]
        public List<RoleAssignment> RoleAssignments = new List<RoleAssignment>();
    }

    public class RoleAssignment
    {
        [JsonProperty("id")]
        public string Id { get; set; }

        [JsonProperty("applicationRoleId")]
        public long? ApplicationRoleId { get; set; }

        [JsonProperty("user")]
        public User User { get; set; }
    }
}
