using System;
using System.Linq;
using System.Collections.Generic;

using Xamarin.Forms;

using Rg.Plugins.Popup.Extensions;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Views;
using Workbench.Forms.ViewModels;
using Workbench.Forms.UI.ViewCells;
using Workbench.Forms.Models;
using Xamarin.Forms.Internals;
using Workbench.Forms.UI.Controls;
using System.ComponentModel.Design;

namespace Workbench.Forms.UI.Pages
{
    public class ContractInstancePage : BaseContentPage<ContractInstanceViewModel>
    {
        Button actionButton;
        NestedListView transactionsList;

		public ContractInstancePage(Contract contractInstance)
        {
			//TODO: FOR NOW JUST TAKING THE NAME OF THE WORKFLOW
			Title = App.ViewModel.Contract.DisplayName + $" ({contractInstance?.Id})";
            ViewModel.ContractInstance = contractInstance;
			NavigationPage.SetBackButtonTitle(this, "Back");

            var loadingIndicator = new ActivityIndicator { AutomationId = "LoadingIndicator" };
            actionButton = new Button
            {
                Text = "TAKE ACTION",
                CornerRadius = 0,
                BackgroundColor = Color.White,
                TextColor = Constants.NavBarBackgroundColor,
                FontSize = 18,
                FontAttributes = FontAttributes.Bold,
                HorizontalOptions = LayoutOptions.FillAndExpand
            };

            var contractDetails = new ContractDetailsView();
			var contractProgress = new ContractProgressView();
            var contactsView = new ScrollingContactsView();
            
			var transactionHeader = new Label
            {
                Text = "ACTIVITY",
                FontSize = 18,
                FontAttributes = FontAttributes.Bold,
            };

            transactionsList = new NestedListView(ListViewCachingStrategy.RecycleElement)
            {
                Header = new ContentView
                {
                    Content = transactionHeader,
                    Padding = new Thickness(10, 10, 10, 0),
                },
                ItemTemplate = new DataTemplate(typeof(BlockTransactionViewCell)),
                SeparatorVisibility = SeparatorVisibility.None,
                HasUnevenRows = true,
                VerticalOptions = LayoutOptions.FillAndExpand
            };
         
            BackgroundColor = Color.FromHex("#E3E3E3");

            var gridLayout = new Grid
            {
                Padding = new Thickness(10, 10, 10, 0),
				RowDefinitions = new RowDefinitionCollection
				{
					new RowDefinition{ Height = GridLength.Auto},
					new RowDefinition{ Height = GridLength.Auto}
				},
                ColumnDefinitions = new ColumnDefinitionCollection
				{
                    new ColumnDefinition {Width = GridLength.Star}
				}
            };

			var stacklayout = new StackLayout
			{
				Padding = new Thickness(10, 10, 10, 10)
			};

			stacklayout.Children.Add(loadingIndicator);
			stacklayout.Children.Add(actionButton);
         
			stacklayout.Children.Add(new MaterialFrame { Content = contractProgress, Padding = Device.RuntimePlatform.Equals(Device.iOS) ? 0 : 5 });
			stacklayout.Children.Add(new MaterialFrame { Content = contractDetails, Padding = Device.RuntimePlatform.Equals(Device.iOS) ? 0 : 5 });
            

            var contactsLabel = new Label
            {
                FontAttributes = FontAttributes.Bold,
                FontSize = 18,
                HorizontalTextAlignment = TextAlignment.Start,
                Text = "MEMBERS"
            };

			gridLayout.Children.Add(contactsLabel, 0, 0);
			gridLayout.Children.Add(contactsView, 0, 1);

			stacklayout.Children.Add(new MaterialFrame { Content = gridLayout, Padding = Device.RuntimePlatform.Equals(Device.iOS) ? 0 : 5 });//, 0, 2);
			stacklayout.Children.Add(new MaterialFrame { Content = transactionsList, Padding = Device.RuntimePlatform.Equals(Device.iOS) ? 0 : 5 });//, 0, 3);

			if (Device.RuntimePlatform.Equals(Device.Android))
			{
				actionButton.HeightRequest = 50;
				loadingIndicator.HeightRequest = 50;
			}

			contractProgress.SetBinding(ContractProgressView.ContractProperty, nameof(ContractInstanceViewModel.ContractInstance));
            contractDetails.SetBinding(ContractDetailsView.ContractProperty, nameof(ContractInstanceViewModel.ContractInstance));
			contactsView.SetBinding(ScrollingContactsView.ContactsProperty, nameof(ContractInstanceViewModel.UserList));
            actionButton.SetBinding(Button.IsVisibleProperty, nameof(ContractInstanceViewModel.DisplayActions));
            //actionButton.SetBinding(Button.IsEnabledProperty, nameof(ContractInstanceViewModel.DisplayActions));
            loadingIndicator.SetBinding(ActivityIndicator.IsRunningProperty, nameof(BaseViewModel.IsBusy));
            loadingIndicator.SetBinding(ActivityIndicator.IsVisibleProperty, nameof(BaseViewModel.IsBusy));
            transactionsList.SetBinding(ListView.ItemsSourceProperty, nameof(ContractInstanceViewModel.Blocks));
            
			ToolbarItems.Add(new ToolbarItem("Refresh", null, async () => await ViewModel.LoadContractInstanceAsync()));

			RootContent = new ScrollView() { Content = stacklayout, Padding = 5 };
        }

        protected async override void OnAppearing()
        {
            base.OnAppearing();

            if (!App.ViewModel.CurrentApplication.Enabled)
                actionButton.IsEnabled = false;
            else
                actionButton.IsEnabled = true;
            
			await ViewModel.LoadContractInstanceAsync();
            actionButton.Clicked += DisplayActions;
            transactionsList.ItemSelected += TransactionSelected;

        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();

            actionButton.Clicked -= DisplayActions;
            transactionsList.ItemSelected -= TransactionSelected;
        }

        async void TransactionSelected(object sender, SelectedItemChangedEventArgs e)
        {
            if (e.SelectedItem == null)
                return;

			var item = ((ListView)sender).SelectedItem;
         
			await Navigation.PushAsync(new TransactionDetailPage(item as BlockModel));
        }

        public async void DisplayActions(object sender, EventArgs e)
        {
            string actionSelected = string.Empty;

            if (ViewModel.Actions.Count == 1)
            {
				actionSelected = ViewModel.Actions[0].DisplayName;
            }
            else
            {
                var actions = new List<string> { };
				foreach (var action in ViewModel.Actions)
                    actions.Add(action.DisplayName);

                actionSelected = await DisplayActionSheet("Actions", "Cancel", null, actions.ToArray());
            }

            if (!string.IsNullOrWhiteSpace(actionSelected))
            {
                if (!actionSelected?.Equals("Cancel") ?? false)
                {
                    displayActionPage(actionSelected);
                }
            }
        }

        void displayActionPage(string actionSelected)
        {
			var action = App.ViewModel.Contract.Functions.Where(a => a.DisplayName == actionSelected).FirstOrDefault();

            var parameterCount = action.Parameters.Count();

            //Show a popup page if theres 3 parameters or less
            if (parameterCount < 3)
            {
                var actionPopupPage = new ActionPopupPage(action, App.ViewModel.AllUsersList, ViewModel.ContractInstance);

                actionPopupPage.ContractActionSucceded += async (_s, _e) =>
                {
                    ViewModel.DisplayActions = false;
                    await RunAnimation();
                };

                Device.BeginInvokeOnMainThread(async () =>
                {
                    await Navigation.PushPopupAsync(actionPopupPage);
                });

            } else {
                var actionPopupPage = new ContractActionPage(action, App.ViewModel.AllUsersList, ViewModel.ContractInstance);

                actionPopupPage.ContractActionSucceded += async (_s, _e) =>
                {
                    ViewModel.DisplayActions = false;
                    await RunAnimation();
                };

                Navigation.PushAsync(actionPopupPage);
            }
        }
    }
}