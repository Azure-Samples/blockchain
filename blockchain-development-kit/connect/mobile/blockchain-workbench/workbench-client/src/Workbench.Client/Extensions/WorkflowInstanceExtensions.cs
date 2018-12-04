using System;
using Workbench.Client.Models;
using System.Linq;
namespace Workbench.Client.Extensions
{
    public static class WorkflowInstanceExtensions
    {
		public static bool EqualsWorkflowInstance(this Contract wi, Contract instanceToCompare)
        {
			if (wi.ConnectionId != instanceToCompare.ConnectionId) return false;
            if (wi.LedgerIdentifier != instanceToCompare.LedgerIdentifier) return false;
            if (wi.ProvisioningStatus != instanceToCompare.ProvisioningStatus) return false;
			if (wi.Id != instanceToCompare.Id) return false;
                    
			foreach (var wiProperty in wi.ContractProperties)
            {
				var instanceProperty = instanceToCompare.ContractProperties.FirstOrDefault(c => c.WorkflowPropertyId == wiProperty.WorkflowPropertyId);
                if (instanceProperty is null) return false;
                if (wiProperty.Value != instanceProperty.Value) return false;
            }

			//TODO: SEE IF THIS IS NECESSARY
			//foreach (var wiActionHistory in wi.WorkflowInstanceActionHistory)
    //        {
				//var actionHistory = instanceToCompare.WorkflowInstanceActionHistory.FirstOrDefault(c => c. == wiProperty.WorkflowPropertyId);
            //    if (instanceProperty is null) return false;
            //    if (wiProperty.Value != instanceProperty.Value) return false;
            //}

            return true;
        }
    }
}
