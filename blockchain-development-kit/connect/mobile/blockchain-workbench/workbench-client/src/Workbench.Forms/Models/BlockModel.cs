using Workbench.Client.Models;
using System.Collections.Generic;

namespace Workbench.Forms.Models
{
    public class BlockModel
    {
		public BlockModel(ContractAction action, Transaction transaction, BlockFunction function)
        {
            Action = action;
            Transaction = transaction;
			Function = function;
        }

		public ContractAction Action { get; set; }
		public BlockFunction Function { get; set; }
		public Transaction Transaction { get; set; }
    }

	public class BlockFunction
	{
		public string Name { get; set; }
		public string Description { get; set; }
		public List<Parameter> Parameters { get; set; } = new List<Parameter>();
	}
}