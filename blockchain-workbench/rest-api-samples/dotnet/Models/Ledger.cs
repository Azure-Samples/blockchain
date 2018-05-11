using System;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Workbench.Client.Models
{
	public class LedgersReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("ledgers")]
		public List<Ledger> Ledgers = new List<Ledger>();
    }

    public class Ledger
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }
    }
}
