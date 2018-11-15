using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.Models;
using Workbench.Forms.UI.Views;
using Xamarin.Forms.PlatformConfiguration.AndroidSpecific;
using Xamarin.Forms;
using Microsoft.AppCenter.Crashes;
using System.Runtime.CompilerServices;
using Workbench.Forms.Helpers;

namespace Workbench.Forms.ViewModels
{
	public class ContractInstanceViewModel : BaseViewModel
	{
		bool displayActions = false;
		Contract contractInstance;
		List<BlockModel> blocks = new List<BlockModel>();
		List<UserModel> userList = new List<UserModel>();
		public List<UserModel> UserList
		{
			get { return userList; }
			set { SetProperty(ref userList, value); }
		}
		public bool DisplayActions
		{
			get { return displayActions; }
			set { SetProperty(ref displayActions, value); }
		}
		public Contract ContractInstance
		{
			get { return contractInstance; }
			set { SetProperty(ref contractInstance, value); }
		}
		public List<BlockModel> Blocks
		{
			get { return blocks; }
			set { SetProperty(ref blocks, value); }
		}
        
		public List<WorkflowFunction> Actions = new List<WorkflowFunction>();
        
		public async Task LoadContractInstanceAsync()
		{
			IsBusy = true;

			try
			{    
				Blocks = new List<BlockModel>();
				DisplayActions = false;

				var res = await GatewayApi.Instance.GetContractByIdAsync(ContractInstance.Id.ToString());

				if (res != default(Contract) || res != null)
				{
					ContractInstance = res;
                    assembleBlocks();
				}
                            
                assembleUsers();            
                            
				var actions = await GatewayApi.Instance.GetWorkflowActionsAsync(ContractInstance.Id.ToString());
                if (actions != null)
                {
                    Actions = new List<WorkflowFunction>(actions);

                    if (Actions?.Count > 0)
                        DisplayActions = true;
                    else
                        DisplayActions = false;
                }
                else
                {
                    DisplayActions = false;
                }
			}
			catch (Exception e)
			{
				AppCenterHelper.Report(e);
			}
			finally
			{
				IsBusy = false;
			}
		}

		public void assembleUsers()
		{
			List<UserModel> tempList = new List<UserModel>();
			foreach (var user in ContractInstance.ContractActions)
			{
				
				var userId = user.UserId;
				var userDetails = App.ViewModel.ApplicationRoleMappingsList.FirstOrDefault(x => x.User.UserId.Equals(userId));
				var role = App.ViewModel.CurrentApplication.ApplicationRoles.FirstOrDefault(x => x.Id.Equals(userDetails.ApplicationRoleId)).Name;

				var userModel = new UserModel
				{
					Name = userDetails.User.DisplayName,
					EmailAddress = userDetails.User.EmailAddress,
					UserID = userDetails.User.UserId.ToString(),
					Role = role,
					UserChainMappings = new List<UserChainMapping>(userDetails.User.UserChainMappings)
				};

				var _userInList = tempList.FirstOrDefault(x => x.UserID.Equals(user.UserId.ToString()) && x.Role.Equals(role));
				if (_userInList is null)
				    tempList.Add(userModel);            
			}

			UserList = new List<UserModel>(tempList);
			tempList = null;
		}


		void assembleBlocks()
		{         
			var _blocks = new List<BlockModel>();
			ContractInstance.ContractActions.Reverse();
			foreach (var action in ContractInstance.ContractActions)
			{
				var transaction = ContractInstance.Transactions.FirstOrDefault(t => t.Id == action.TransactionId);
				WorkflowFunction function;
				if (action.WorkflowFunctionId == App.ViewModel.Contract.ConstructorId)
				{
					function = App.ViewModel.Contract.Constructor;
					function.DisplayName = "Contract Created";
			    }else
				{
					function = App.ViewModel.Contract.Functions.FirstOrDefault(f => f.Id == action.WorkflowFunctionId);
				}

                _blocks.Add(new BlockModel(action, transaction,new BlockFunction {Name = function.DisplayName, Description = function.Description, Parameters = function.Parameters }));
			}

			_blocks.OrderByDescending(b => b.Action.Timestamp);

			Blocks = _blocks;
		}
	}
}