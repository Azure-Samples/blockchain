using System;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Workbench.Client.Models
{
	public class WorkflowReturnType
    {

        [JsonProperty("nextLink")]
        public string NextLink { get; set; }

        [JsonProperty("workflows")]
        public List<Workflow> Workflows = new List<Workflow>();
    }

    public class Workflow
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        [JsonProperty("applicationId")]
        public long ApplicationId { get; set; }

        [JsonProperty("constructorId")]
        public long ConstructorId { get; set; }

        [JsonProperty("startStateId")]
        public long StartStateId { get; set; }

        [JsonProperty("initiators")]
        public List<string> Initiators = new List<string>();

        [JsonProperty("properties")]
        public List<Property> Properties = new List<Property>();

        [JsonProperty("constructor")]
		public WorkflowFunction Constructor { get; set; }

        [JsonProperty("functions")]
		public List<WorkflowFunction> Functions = new List<WorkflowFunction>();

        [JsonProperty("startState")]
		public State StartState { get; set; }

        [JsonProperty("states")]
        public List<State> States = new List<State>();
    }

	public class WorkflowFunction
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        [JsonProperty("parameters")]
        public List<Parameter> Parameters = new List<Parameter>();

        [JsonProperty("workflowId")]
        public long WorkflowId { get; set; }
             
        [JsonProperty("preconditions")]
        public List<Condition> Preconditions = new List<Condition>();

        [JsonProperty("postconditions")]
        public List<Condition> Postconditions = new List<Condition>();
    }

    public class Parameter
    {
        [JsonProperty("id")]
        public string Id { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

		[JsonProperty("name")]
        public string Name { get; set; }
      
        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        [JsonProperty("type")]
        public TypeClass Type { get; set; }
    }


    public class Condition
    {
        [JsonProperty("expression")]
        public string Expression { get; set; }
    }

    public class Property
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        [JsonProperty("type")]
        public TypeClass Type { get; set; }
    }

	public class TypeClass
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("elementType")]
        public object ElementType { get; set; }

        [JsonProperty("elementTypeId")]
        public long ElementTypeId { get; set; }
    }

	public static class ContractParameterType
    {
        public const string QrCode = "qrcode";
        public const string Gps = "gps";
        public const string Image = "image";
        public const string Document = "document";
        public const string Barcode = "barcode";
        public const string String = "string";
        public const string Money = "money";
        public const string Uint = "uint";
		public const string Int = "int";
        public const string User = "user";
		public const string Device = "device";
    }

    public class State
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }

        [JsonProperty("percentComplete")]
        public long PercentComplete { get; set; }

        [JsonProperty("value")]
        public long Value { get; set; }

        [JsonProperty("style")]
        public string Style { get; set; }

        [JsonProperty("workflowStateTransitions")]
		public List<WorkflowStateTransition> WorkflowStateTransitions = new List<WorkflowStateTransition>();
    }

	public class WorkflowStateTransition
	{
		[JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("workflowFunctionId")]
        public long WorkflowFunctionId { get; set; }

        [JsonProperty("currStateId")]
        public long CurrStateId { get; set; }

		[JsonProperty("allowedRoles")]
		public List<string> AllowedRoles = new List<string>();

		[JsonProperty("allowedInstanceRoles")]
		public List<string> AllowedInstanceRoles = new List<string>();

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("function")]
        public string Function { get; set; }

        [JsonProperty("currentState")]
        public string CurrentState { get; set; }

        [JsonProperty("displayName")]
        public string DisplayName { get; set; }
    }
    
}
