using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace Workbench.Client.Models
{
	   
	public class WorkflowInstancesReturnType
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("contracts")]
		public List<Contract> Contracts = new List<Contract>();
    }
    
    public class Contract
    {
		[JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("provisioningStatus")]
        public long? ProvisioningStatus { get; set; }

        [JsonProperty("connectionID")]
        public long? ConnectionId { get; set; }

        [JsonProperty("ledgerIdentifier")]
        public string LedgerIdentifier { get; set; }

        [JsonProperty("deployedByUserId")]
        public long? DeployedByUserId { get; set; }

        [JsonProperty("workflowId")]
        public long? WorkflowId { get; set; }

        [JsonProperty("contractCodeId")]
        public long? ContractCodeId { get; set; }

		[JsonProperty("contractProperties")]
		public List<ContractProperty> ContractProperties = new List<ContractProperty>();

		[JsonProperty("transactions")]
		public List<Transaction> Transactions = new List<Transaction>();

		[JsonProperty("contractActions")]
		public List<ContractAction> ContractActions = new List<ContractAction>();
    }

	public class ContractAction
    {
        [JsonProperty("id")]
        public long? Id { get; set; }

        [JsonProperty("userId")]
        public long? UserId { get; set; }

        [JsonProperty("provisioningStatus")]
        public long? ProvisioningStatus { get; set; }

        [JsonProperty("timestamp")]
        public DateTime? Timestamp { get; set; }

		[JsonProperty("parameters")]
        public List<WorkflowActionParameter> Parameters = new List<WorkflowActionParameter>();

        [JsonProperty("workflowFunctionId")]
        public long? WorkflowFunctionId { get; set; }

        [JsonProperty("transactionId")]
        public long? TransactionId { get; set; }

        [JsonProperty("workflowStateId")]
        public long? WorkflowStateId { get; set; }
    }

	public class ContractProperty
    {
        [JsonProperty("workflowPropertyID")]
        public string WorkflowPropertyId { get; set; }

        [JsonProperty("value")]
        public string Value { get; set; }
    }

   

	public class Actions
    {
        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

		[JsonProperty("workflowFunctions")]
		public List<WorkflowFunction> WorkflowFunctions = new List<WorkflowFunction>();
    }
   

	public class ActionInformation
    {
		[JsonProperty("workflowFunctionID")]
        public long WorkflowFunctionId { get; set; }
      
		[JsonProperty("workflowActionParameters")]
		public List<WorkflowActionParameter> WorkflowActionParameters = new List<WorkflowActionParameter>();
    }

    public class WorkflowActionParameter
    {
        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("value")]
        public string Value { get; set; }
    }

}
