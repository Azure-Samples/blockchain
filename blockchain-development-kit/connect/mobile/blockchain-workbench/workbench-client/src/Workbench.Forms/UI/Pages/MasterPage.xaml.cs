using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using Acr.UserDialogs;

using Workbench.Forms.Models;
using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Views;
using System.Linq;

using Workbench.Client;
using Workbench.Forms.ViewModels;

namespace Workbench.Forms.UI.Pages
{
	public partial class MasterPage : BasePage
	{

		public MasterPage()
		{
			InitializeComponent();

			if (Xamarin.Forms.Device.RuntimePlatform == Xamarin.Forms.Device.iOS)
			{
				Icon = "menu.png";
			}

			animationView.Loop = true;
			animationView.AutoPlay = true;

            settingsLabel.GestureRecognizers.Add(new TapGestureRecognizer()
            {
                Command = new Command(() => {
                    SettingsClicked(this, null);
                })
            });

            removeEnvLabel.GestureRecognizers.Add(new TapGestureRecognizer()
            {
                Command = new Command(() => {
                    ChangeURLEndpoint(this, null); 
                })
            });

            logoutLabel.GestureRecognizers.Add(new TapGestureRecognizer()
            {
                Command = new Command(() => {
                    LogoutClicked(this, null);
                })
            });

            var contactCircleView = new ContactCircleView(80,20);
            contactCircleView.HorizontalOptions = LayoutOptions.Start;
            contactCircleView.Margin = new Thickness(15, 30, 0, 0);
            contactCircleView.SetBinding(ContactCircleView.ContactDetailProperty, nameof(App.ViewModel.CurrentUser));

            mainGrid.Children.Add(contactCircleView, 0, 0);

            Grid.SetColumnSpan(contactCircleView, 2);

		}

        
		private async void RefreshContracts(object sender, EventArgs e)
		{
			if (!App.ViewModel.IsBusy)
    			await App.ViewModel.EnsureSelectedEnvironmentLoads();
		}

        protected override void OnAppearing()
        {
            base.OnAppearing();
        }

        protected override void OnSizeAllocated(double width, double height)
		{
			base.OnSizeAllocated(width, height);

			if (width < 0 || animationView is null) return;

			if (100 == animationView.WidthRequest) return;

			animationView.WidthRequest = 100;
			animationView.HeightRequest = 100;

			RelativeLayout.SetXConstraint(animationView, Constraint.RelativeToParent(p => p.Bounds.Center.X - 50));
			RelativeLayout.SetYConstraint(animationView, Constraint.RelativeToParent(p => p.Bounds.Center.X - 50));
		}

		async Task handleContractInstanceTapped(ItemTappedEventArgs e)
		{
			
			if (e.Item == null) return;

			var selectedApplication = e.Item as Workbench.Client.Models.Application;
    
			App.ViewModel.CurrentApplication = selectedApplication;

			//Reset the IsInitiator property
			App.ViewModel.IsUserInitiator = false;
			await App.ViewModel.SetCurrentApplication(selectedApplication.Id.ToString());
			await App.ViewModel.RefreshWorkflowsForSelectedApplication(selectedApplication.Id.ToString());
		      
		}

        async void SettingsClicked(object sender, System.EventArgs eventArgs)
        {
            App.ViewModel.MenuPresented = false;
            await App.Master.Detail.Navigation.PushAsync(new SettingsPage());
        }

        async void ChangeURLEndpoint(object sender, System.EventArgs e)
		{
			if (App.ViewModel.WorkbenchEnvironments.Count > 1) return;
			var prompt = new PromptConfig
            {
                IsCancellable = true,
				Message = $"This lets you change to another Workbench instance with the same Active Directory. Please enter the environment name in the format 'xxx-xxxxxx'",
                OkText = "Save",
                Title = "Change API Endpoint?"
            };

            var apiURLResults = await UserDialogs.Instance.PromptAsync(prompt);
			if (!string.IsNullOrEmpty(apiURLResults?.Value)&&apiURLResults.Ok)
			{
				var newSiteUrl = $"https://{apiURLResults?.Value}-api.azurewebsites.net/";

				var duplicateEnvironment = App.ViewModel.WorkbenchEnvironments.FirstOrDefault(env => env.SiteUrl == newSiteUrl);

				if (duplicateEnvironment is null)
				{
					var prevEnv = App.ViewModel.WorkbenchEnvironments[0];
					App.ViewModel.WorkbenchEnvironments[0].SiteUrl = newSiteUrl;
                    App.ViewModel.AllUsersList.Clear();
					await LocalDbHelper.Instance.PurgeEnvironments();
					var environment = new BlockchainEnvironment
					{
						ResourceId = prevEnv.ResourceId,
						ClientId = prevEnv.ClientId,
						ReturnUrl = prevEnv.ReturnUrl,
						SiteUrl = newSiteUrl,
						TenantId = prevEnv.TenantId,
						NickName = prevEnv.NickName
					};
					await LocalDbHelper.Instance.SaveEnvironmentAsync(environment);
					GatewayApi.SiteUrl = newSiteUrl;

				}
				else
				{
					await DisplayAlert("Error", "This endpoint is already configured", "Ok");
				}
			}
		}

		async void LogoutClicked(object sender, System.EventArgs e)
		{
			var res = await DisplayAlert("Log Out? ", "Would you like to log out?", "Yes", "No");
			if (res)
			{
				await App.Logout();
			}
		}


	}
}