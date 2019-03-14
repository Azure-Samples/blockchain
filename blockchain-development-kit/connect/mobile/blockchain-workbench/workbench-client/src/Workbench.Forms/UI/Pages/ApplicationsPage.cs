using System;
using Workbench.Forms.ViewModels;
using Xamarin.Forms;
using Workbench.Forms.UI.ViewCells;
using System.Threading.Tasks;
using Workbench.Forms.Helpers;
using DLToolkit.Forms.Controls;

namespace Workbench.Forms.UI.Pages
{
    public class ApplicationsPage : BasePage
    {
        AppViewModel ViewModel;
        bool isBusy;
        FlowListView flowListView;

        public ApplicationsPage()
        {
            NavigationPage.SetBackButtonTitle(this, "Back");
            Title = "Applications";
            ViewModel = App.ViewModel;
            BindingContext = ViewModel;


            flowListView = new FlowListView
            {
                FlowColumnCount = 2,
                VerticalOptions = LayoutOptions.FillAndExpand,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                SeparatorVisibility = SeparatorVisibility.None,
                HasUnevenRows = Device.RuntimePlatform.Equals(Device.iOS),
                IsPullToRefreshEnabled = true,
                FlowColumnTemplate = new DataTemplate(typeof(ApplicationViewCell))
            };
            if (Device.RuntimePlatform.Equals(Device.Android))
            {
                flowListView.RowHeight = (int)(App.ScreenHeight / 2.5);
            }

            flowListView.FlowTappedBackgroundColor = Color.LightGray;
            flowListView.SetBinding(FlowListView.FlowItemsSourceProperty, nameof(ViewModel.ApplicationListCollection));
            flowListView.SetBinding(FlowListView.RefreshCommandProperty, nameof(ViewModel.FlowListViewRefreshCommand));
            flowListView.SetBinding(FlowListView.IsRefreshingProperty, nameof(ViewModel.IsBusy));


            RootContent = flowListView;


            animationView.Loop = true;
            animationView.AutoPlay = true;
            animationView.Scale = 0.5;

            ViewModel.RetreievedAllWorkflows += RetreievedAllWorkflows;

        }

        async void RetreievedAllWorkflows(object sender, int e)
        {
            App.ContractsPage.ViewModel.ContractInstances.Clear();
            if (e == 1)
            {
                await Navigation.PushAsync(App.ContractsPage);
            }
            else
            {
                await Navigation.PushAsync(new WorkflowsPage());
            }

        }


        private async void RefreshContracts()
        {
            if (!App.ViewModel.IsBusy)
                await App.ViewModel.EnsureSelectedEnvironmentLoads();
        }

        async void handleApplicationTapped(object sender, ItemTappedEventArgs e)
        {
            try
            {
                if (!isBusy)
                {
                    isBusy = true;
                    if (e.Item == null) return;

                    var selectedApplication = e.Item as Workbench.Client.Models.Application;

                    //Reset the IsInitiator property
                    App.ViewModel.IsUserInitiator = false;


                    await App.ViewModel.SetCurrentApplication(selectedApplication.Id.ToString());

                    await App.ViewModel.RefreshWorkflowsForSelectedApplication(selectedApplication.Id.ToString());

                }
            }
            catch (Exception ex)
            {
                AppCenterHelper.Report(ex);
            }
            finally
            {
                isBusy = false;
            }
        }


        protected override void OnAppearing()
        {
            base.OnAppearing();      
            flowListView.FlowItemTapped += handleApplicationTapped;
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
            flowListView.FlowItemTapped -= handleApplicationTapped;
        }
    }
}
