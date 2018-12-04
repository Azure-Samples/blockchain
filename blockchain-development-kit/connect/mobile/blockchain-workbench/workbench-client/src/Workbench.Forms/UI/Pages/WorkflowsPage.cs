using System;
using Workbench.Forms.ViewModels;
using Xamarin.Forms;
using Workbench.Forms.UI.ViewCells;
using Workbench.Forms.Helpers;
using DLToolkit.Forms.Controls;

namespace Workbench.Forms.UI.Pages
{
    public class WorkflowsPage : BasePage
    {
        AppViewModel ViewModel;
        bool isBusy;
        FlowListView flowListView;

        public WorkflowsPage()
        {
            NavigationPage.SetBackButtonTitle(this, "Back");
            Title = "Workflows";
            ViewModel = App.ViewModel;
            BindingContext = ViewModel;

            flowListView = new FlowListView
            {
                FlowColumnCount = 2,
                VerticalOptions = LayoutOptions.FillAndExpand,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                SeparatorVisibility = SeparatorVisibility.None,
                HasUnevenRows = true,
                FlowColumnTemplate = new DataTemplate(typeof(WorkflowViewCell))
            };

            flowListView.FlowTappedBackgroundColor = Color.LightGray;
            flowListView.SetBinding(FlowListView.FlowItemsSourceProperty, nameof(ViewModel.WorkflowListCollection));

            RootContent = flowListView;
         
            animationView.Loop = true;
            animationView.AutoPlay = true;

            ViewModel.CurrentWorkflowSet += CurrentWorkflowSet;

        }

        async void CurrentWorkflowSet(object sender, int e)
        {
            await Navigation.PushAsync(App.ContractsPage);
        }


        private async void RefreshContracts()
        {
            if (!App.ViewModel.IsBusy)
                await App.ViewModel.EnsureSelectedEnvironmentLoads();
        }

        async void handleWorkflowTapped(object sender, ItemTappedEventArgs e)
        {
            try
            {
                if (!isBusy)
                {
                    isBusy = true;
                    if (e.Item == null) return;

                    var selectedWorkflow = e.Item as Workbench.Client.Models.Workflow;

                    //Reset the IsInitiator property
                    App.ViewModel.IsUserInitiator = false;

                    await App.ViewModel.SetCurrentWorkflow(selectedWorkflow.Id.ToString());

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
            flowListView.FlowItemTapped += handleWorkflowTapped;

            //if (App.ViewModel.RolesString.Equals("No Roles") || string.IsNullOrEmpty(App.ViewModel.RolesString.ToString()))
            //{
            //    var toastConfig = new ToastConfig("You are currently not assigned a role in this workflow. If you believe this is an error, please contact your administrator.");
            //    toastConfig.SetDuration(3000);
            //    UserDialogs.Instance.Toast(toastConfig);
            //}
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
            flowListView.FlowItemTapped -= handleWorkflowTapped;
        }
    }
}
