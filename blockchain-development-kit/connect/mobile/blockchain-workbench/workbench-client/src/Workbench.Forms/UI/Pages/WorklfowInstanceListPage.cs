using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using Workbench.Client.Models;

using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Views;
using Workbench.Forms.ViewModels;
using Workbench.Forms.UI.ViewCells;

using System.Linq;
using Workbench.Client;

namespace Workbench.Forms.UI.Pages
{
    public class WorklfowInstanceListPage : BaseContentPage<WorkflowInstanceListPageViewModel>
    {
        Xamarin.Forms.ListView contractListView;
        FabButton AddContractButton;

        public WorklfowInstanceListPage()
        {
            NavigationPage.SetBackButtonTitle(this, "Back");
            Title = "Workflows";

            setup();
        }
        public WorklfowInstanceListPage(Workflow contract)
        {
            Title = contract.DisplayName;
            NavigationPage.SetBackButtonTitle(this, "Back");

            App.ViewModel.Contract = contract;

            setup();
        }

        void setup()
        {
            contractListView = new Xamarin.Forms.ListView(ListViewCachingStrategy.RecycleElement)
            {
                AutomationId = "ContractList",
                ItemTemplate = new DataTemplate(typeof(WorkflowInstanceViewCell)),
                IsPullToRefreshEnabled = true,
                SeparatorVisibility = SeparatorVisibility.None,
                HasUnevenRows = true,
                IsGroupingEnabled = false,
                BackgroundColor = Color.FromHex("E3E3E3")
            };

            contractListView.SetBinding(ListView.ItemsSourceProperty, nameof(ViewModel.ContractInstances), BindingMode.TwoWay);
            contractListView.SetBinding(ListView.RefreshCommandProperty, nameof(ViewModel.RefreshContractsCommand));
            contractListView.SetBinding(ListView.IsRefreshingProperty, nameof(ViewModel.IsBusy), BindingMode.TwoWay);

            //ToolbarItems.Add(new ToolbarItem("Filter", null, async () => await changePropertiesViewedAsync(), ToolbarItemOrder.Primary));

            var relativeLayout = new RelativeLayout();

            AddContractButton = new FabButton
            {
                Text = "",
                FontFamily = "FontAwesome",
                TextColor = Color.White,
                BackgroundColor = Constants.NavBarBackgroundColor,
                FontSize = 22,
                CornerRadius = 35,
            };

            if (Device.RuntimePlatform.Equals(Device.Android))
            {
                AddContractButton.Effects.Add(Effect.Resolve("Xamarin.FontAwesomeEffect"));
            }

            relativeLayout.Children.Add(contractListView,
                         xConstraint: Constraint.Constant(0),
                         yConstraint: Constraint.Constant(0),
                         widthConstraint: Constraint.RelativeToParent(p => p.Width),
                         heightConstraint: Constraint.RelativeToParent(p => p.Height));

            relativeLayout.Children.Add(AddContractButton,
                                xConstraint: Constraint.RelativeToParent(p => p.Width - 78), // padding is 12
                                yConstraint: Constraint.RelativeToParent(p => p.Height - 78),// padding is 12
                                widthConstraint: Constraint.Constant(70),
                                heightConstraint: Constraint.Constant(70));


            AddContractButton.SetBinding(IsVisibleProperty, "IsUserInitiator");

            RootContent = relativeLayout;

        }

        protected override bool OnBackButtonPressed()
        {
            ViewModel.ContractInstances.Clear();
            return true;
        }

        protected async override void OnAppearing()
        {
            Title = App.ViewModel.Contract.DisplayName;
            WorkflowInstanceViewCell.Preferences = await LocalDbHelper.Instance.GetContractPropertyPreferencesAsync(App.ViewModel?.Contract?.Id.ToString());

            contractListView.ItemTapped += handleContractSelectedAsync;
            contractListView.ItemAppearing+= ContractListView_ItemAppearing;
            AddContractButton.Clicked += AddContractButton_Clicked;

            if (App.ViewModel.Contract is null) return;


            if (!App.ViewModel.CurrentApplication.Enabled)
            {
                AddContractButton.IsEnabled = false;
            }
            else
            {
                AddContractButton.IsEnabled = true;
            }

            if (WorkflowInstanceViewCell.Preferences == null)
                await changePropertiesViewedAsync();

            if (App.CONTRACTS_FILTER_CHANGED)
            {
                App.CONTRACTS_FILTER_CHANGED = false;
                //await Navigation.PopAsync();
            }

            await ViewModel.GetAllDataAsync();

            await ViewModel.BeginBackgroundRefresh();

            //if (App.ViewModel.RolesString.Equals("No Roles") || string.IsNullOrEmpty(App.ViewModel.RolesString.ToString()))
            //{
            //    var toastConfig = new ToastConfig("You are currently not assigned a role in this workflow. If you believe this is an error, please contact your administrator.");
            //    toastConfig.SetDuration(3000);

            //    UserDialogs.Instance.Toast(toastConfig);
            //}

        }

        protected override void OnDisappearing()
        {
            contractListView.ItemTapped -= handleContractSelectedAsync;
            contractListView.ItemAppearing -= ContractListView_ItemAppearing;
            AddContractButton.Clicked -= AddContractButton_Clicked;

        }

        void ContractListView_ItemAppearing(object sender, ItemVisibilityEventArgs e)
        {
            var itemType = e.Item as Contract;
            if (ViewModel.ContractInstances.Last() == itemType && ViewModel.ContractInstances.Count() != 1)
            {
                if(GatewayApi.Instance.HAS_MORE_CONTRACTS)
                    GatewayApi.Instance.CONTRACTS_TOP_QUERY_PARAM += GatewayApi.Instance.CONTRACTS_TOP_QUERY_PARAM; 
            }
        }

        async void AddContractButton_Clicked(object sender, EventArgs e)
        {
            await addNewContractAsync();
        }

        async Task addNewContractAsync()
        {
            var contractDefinitionSelected = App.ViewModel.Contract;
            if (contractDefinitionSelected is null) return;

            var contractAction = contractDefinitionSelected.Constructor;

            if (ViewModel.IsUserInitiator)
            {
                //TODO: CONNECTION ID IS FORCED
                var newContractPage = new ContractActionPage(contractAction, App.ViewModel.AllUsersList, contractDefinitionSelected.Id.ToString(), "1");
                newContractPage.ContractActionSucceded += (_s, _e) => RunAnimation();

                await Navigation.PushAsync(newContractPage);
            }
        }

        async void handleRefreshButtonClickedAsync(object sender, EventArgs e)
        {
            await ViewModel.GetAllDataAsync();
        }

        async void handleContractSelectedAsync(object sender, ItemTappedEventArgs e)
        {
            if (e.Item == null)
                return;

            var listView = sender as Xamarin.Forms.ListView;
            var contractInstance = e.Item as Contract;

            //contract has issues with it and is being displayed
            if (contractInstance.Transactions.Count == 0)
                return;

            if (contractInstance != null)
            {
                await Navigation.PushAsync(new ContractInstancePage(contractInstance));
            }

            listView.SelectedItem = null;
        }

        async Task changePropertiesViewedAsync()
        {
            await Navigation.PushAsync(new ContractPreferencesPage(App.ViewModel.Contract));
        }
    }
}