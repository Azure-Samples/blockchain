using System;
using System.Collections.Generic;
using Newtonsoft.Json;
namespace Workbench.Client.Models
{
	public class ChainTypeReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("chainTypes")]
		public List<ChainType> ChainTypes = new List<ChainType>();
    }

    public class ChainType
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }
    }
}
