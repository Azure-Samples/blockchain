using System;
using System.Collections.Generic;
using Newtonsoft.Json;
namespace Workbench.Client.Models
{
	public partial class ConnectionsReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("connection")]
		public List<Connection> Connections = new List<Connection>();
    }

    public partial class Connection
    {
        [JsonProperty("connectionID")]
        public long ChainInstanceId { get; set; }

        [JsonProperty("ledgerID")]
        public long? ChainTypeId { get; set; }

        [JsonProperty("endpointURL")]
        public string EndpointUrl { get; set; }

        [JsonProperty("fundingAccount")]
        public string FundingAccount { get; set; }
    }

	public class BlockReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("blocks")]
		public List<Block> Blocks = new List<Block>();
    }

    public class Block
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("connectionId")]
        public long? ConnectionID { get; set; }

        [JsonProperty("timestamp")]
        public System.DateTimeOffset Timestamp { get; set; }

        [JsonProperty("blockNumber")]
        public long? BlockNumber { get; set; }

        [JsonProperty("blockHash")]
        public string BlockHash { get; set; }
    }

    public class TransactionReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

        [JsonProperty("transactions")]
        public List<Transaction> Transactions = new List<Transaction>();
    }

	public class Transaction
	{
		[JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("connectionId")]
        public long? ConnectionId { get; set; }

        [JsonProperty("transactionHash")]
        public string TransactionHash { get; set; }

        [JsonProperty("blockID")]
        public long? BlockId { get; set; }

        [JsonProperty("from")]
        public string From { get; set; }

        [JsonProperty("to")]
        public string To { get; set; }

        [JsonProperty("value")]
        public long? Value { get; set; }

        [JsonProperty("isAppBuilderTx")]
        public bool IsAppBuilderTx { get; set; }
	}
}
