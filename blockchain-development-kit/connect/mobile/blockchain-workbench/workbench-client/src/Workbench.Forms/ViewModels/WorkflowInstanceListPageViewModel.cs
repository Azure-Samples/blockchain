using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.ObjectModel;

using Xamarin.Forms;

using Workbench.Forms.Helpers;

using Workbench.Client;
using Workbench.Client.Extensions;
using Workbench.Forms.UI.Pages;
	
namespace Workbench.Forms.ViewModels
{
	public class WorkflowInstanceListPageViewModel : BaseViewModel
	{
		static int NUMBER_OF_TIMES_REFRESHED = 0;

		Command refreshContractsCommand;

		public Command RefreshContractsCommand =>
			refreshContractsCommand ?? (refreshContractsCommand = new Command(async () => await GetAllDataAsync()));
        
		ObservableCollection<Client.Models.Contract> contractInstances = new ObservableCollection<Client.Models.Contract>();
		public ObservableCollection<Client.Models.Contract> ContractInstances
		{
			get {
				return contractInstances;
			}
			set => SetProperty(ref contractInstances, value);
		}

		bool _isUserInitiator;
        public bool IsUserInitiator
        {
            get => _isUserInitiator;
            set => SetProperty(ref _isUserInitiator, value);
        }
        
		public bool BackgroundRefreshRunning { get; set; } = false;


		public async Task GetAllDataAsync()
		{
			IsBusy = true;

			try
			{
				await getContractInstancesAsync();            
			}
			catch (Exception e)
			{
				System.Diagnostics.Debug.WriteLine($"GETALLDATA: {e.Message}");
			}
			finally
			{
				IsBusy = false;
			}
		}

		static bool isRunning;

		public async Task BeginBackgroundRefresh()
		{
			System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Try Running");
			if (isRunning)
			{
				System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Already Running");
				return;
			}
			isRunning = true;
			System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Running");

            while (isRunning)
			{
                if (App.LOGGED_OUT)
                {
                    isRunning = false;
                    System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: STOPPING");
                    break;
                }
				if (App.ViewModel.Contract != null)
				{
					try
					{
						System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Get Contracts");
						await getContractInstancesAsync();
					}
					catch (Exception e)
					{
						System.Diagnostics.Debug.WriteLine($"BackgroundRefresh Exception: {e.Message}");
					}
				}
				else if (ContractInstances.Count > 0)
				{
					System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Contract is null........Clearing List");
					ContractInstances.Clear();
				}
				else
				{
					System.Diagnostics.Debug.WriteLine($"BackgroundRefresh: Contract is null");
				}

				await Task.Delay(Settings.BackgroundRefreshDelay);
				NUMBER_OF_TIMES_REFRESHED++;
			}
		}

        async Task getContractInstancesAsync()
        {
            try
            {
                IsUserInitiator = await GatewayApi.Instance.CanCurrentUserCreateContractsForWorkflow(App.ViewModel.Contract.Id.ToString());

                var tempList = await GatewayApi.Instance.GetWorkflowInstancesAsync(App.ViewModel.Contract?.Id.ToString());

                if (tempList != null)
                {

                    if (tempList?.Count() == 0)
                    {
                        ContractInstances.Clear();
                        return;
                    }

                    var currentlyDisplayedItems = ContractInstances.ToList();

                    int iIndex = 0;
                    foreach (var item in tempList)
                    {
                        var itemCurrentlyDisplayed = ContractInstances.FirstOrDefault(ci => ci.Id == item.Id);
                        if (itemCurrentlyDisplayed is null)
                        {
                            ContractInstances.Insert(iIndex, item);
                        }
                        else if (!item.EqualsWorkflowInstance(itemCurrentlyDisplayed))
                        {
                            UpdateContractInstancePage();
                            ContractInstances.Remove(itemCurrentlyDisplayed);
                            ContractInstances.Insert(iIndex, item);
                        }

                        iIndex++;
                    }
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
        }

		public void UpdateContractInstancePage()
		{
			var contractInstancePage= App.Master.Detail.Navigation.NavigationStack.Last() as ContractInstancePage;

			if (contractInstancePage != null)
			{
				try
				{
					Device.BeginInvokeOnMainThread(async () =>
					{
						await ((ContractInstanceViewModel)contractInstancePage.ViewModel).LoadContractInstanceAsync();

					});
				}
				catch (Exception e)
				{
					AppCenterHelper.Report(e);
				}
			}         
		}

	}
}