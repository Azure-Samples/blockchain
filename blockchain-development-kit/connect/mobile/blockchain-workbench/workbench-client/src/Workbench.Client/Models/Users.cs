using System;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Workbench.Client.Models
{

	public class CurrentUser
	{
		[JsonProperty("currentUser")]
        public User UserDetails { get; set; }
        
        [JsonProperty("capabilities")]
        public Capabilities Capabilities { get; set; }
	}

	public class Capabilities
    {
        [JsonProperty("canUploadApplication")]
        public bool CanUploadApplication { get; set; }

        [JsonProperty("canUploadContractCode")]
        public bool CanUploadContractCode { get; set; }

        [JsonProperty("canModifyRoleAssignments")]
        public bool CanModifyRoleAssignments { get; set; }

        [JsonProperty("canProvisionUser")]
        public bool CanProvisionUser { get; set; }
    }

	public class UsersReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("users")]
		public List<User> Users = new List<User>();
    }

    public class User
    {
        [JsonProperty("userID")]
        public long UserId { get; set; }

        [JsonProperty("provisioningStatus")]
        public long? ProvisioningStatus { get; set; }

        [JsonProperty("externalID")]
        public string ExternalId { get; set; }

        [JsonProperty("firstName")]
        public string FirstName { get; set; }

        [JsonProperty("lastName")]
        public string LastName { get; set; }

        [JsonProperty("emailAddress")]
        public string EmailAddress { get; set; }

		[JsonProperty("userChainMappings")]
		public List<UserChainMapping> UserChainMappings = new List<UserChainMapping>();

		public string DisplayName => $"{FirstName} {LastName}";          

    }

    public class UserChainMapping
    {
        [JsonProperty("userChainMappingID")]
        public long UserChainMappingId { get; set; }

        [JsonProperty("userID")]
        public long UserId { get; set; }

        [JsonProperty("connectionID")]
        public long? ConnectionId { get; set; }

        [JsonProperty("chainIdentifier")]
        public string ChainIdentifier { get; set; }

        [JsonProperty("chainBalance")]
        public long? ChainBalance { get; set; }
    }
}
