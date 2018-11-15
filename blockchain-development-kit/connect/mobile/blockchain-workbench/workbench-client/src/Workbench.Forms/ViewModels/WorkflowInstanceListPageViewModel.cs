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

                    var contractsWithoutTimestamps = tempList.Where(x => x.ContractActions.Count == 0).Count();

                    //Only order by descending if every contract instance has a timestamp. For those that fail the creation process there won't be a timestamp
                    //if (contractsWithoutTimestamps == 0)
                    //{
                    //    tempList = tempList.OrderByDescending(x => x.ContractActions.LastOrDefault().Timestamp);
                    //}

                    if (tempList?.Count() == 0)
                    {
                        ContractInstances.Clear();
                        return;
                    }

                    var currentlyDisplayedItems = ContractInstances.ToList();

                    foreach (var currentlyDisplayedItem in currentlyDisplayedItems)
                    {
                        var itemInBothLists = tempList.FirstOrDefault(ci => ci.Id == currentlyDisplayedItem.Id);
                        if (itemInBothLists is null)
                        {
                            ContractInstances.Remove(currentlyDisplayedItem);
                        }
                    }

                    foreach (var item in tempList)
                    {
                        var itemCurrentlyDisplayed = ContractInstances.FirstOrDefault(ci => ci.Id == item.Id);
                        if (itemCurrentlyDisplayed is null)
                        {
                            ContractInstances.Add(item);
                        }
                        else if (!item.EqualsWorkflowInstance(itemCurrentlyDisplayed))
                        {
                            // THIS SECTION IS TO UPDATE THE WORKFLOW INSTANCE PAGE
                            UpdateContractInstancePage();

                            var index = ContractInstances.IndexOf(itemCurrentlyDisplayed);

                            ContractInstances.Remove(itemCurrentlyDisplayed);

                            if (index >= ContractInstances.Count)
                                ContractInstances.Add(item);
                            else
                                ContractInstances.Insert(index, item);
                        }
                    }
                }
                else
                {
                    ContractInstances.Clear();
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