using System;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Workbench.Client.Models
{
    public class ApplicationReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

        [JsonProperty("applications")]
        public List<Application> Applications = new List<Application>();
    }

    public class Application
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        //[JsonProperty("applicationType")]
        //public string ApplicationType { get; set; }

        [JsonProperty("createdByUserId")]
        public long? CreatedByUserId { get; set; }

        [JsonProperty("createdDtTm")]
        public System.DateTimeOffset CreatedDtTm { get; set; }

        [JsonProperty("enabled")]
        public bool Enabled { get; set; }

        [JsonProperty("applicationRoles")]
        public List<ApplicationRole> ApplicationRoles = new List<ApplicationRole>();

        //[JsonProperty("workflows")]
        //public List<Workflow> Workflows = new List<Workflow>();
    }

    public class ApplicationRole
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("applicationId")]
        public long? ApplicationId { get; set; }
    }

}
