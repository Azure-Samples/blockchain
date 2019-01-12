using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using Workbench.Forms.Helpers;
using Workbench.Forms.ViewModels;

namespace Workbench.Forms.UI.Pages
{
	public class BackgroundPage : BaseContentPage<BackgroundPageViewModel>
	{
		public BackgroundPage()
		{
            Title = "Blockchain Workbench";

            NavigationPage.SetBackButtonTitle(this, "Back");

            ToolbarItems.Add(new ToolbarItem("Settings", "ic_settings_white.png", async () => await Navigation.PushAsync(new SettingsPage())));

            BackgroundColor = Constants.NavBarBackgroundColor;

            var backgroundImage = new Image
			{
                Source = Constants.BackgroundPageImage,
				WidthRequest = 150,
				HeightRequest = 150,
				HorizontalOptions = LayoutOptions.Center,
                VerticalOptions = LayoutOptions.Center
			};

            var loginButton = new Button
            {
                AutomationId = "LoginButton",
                Text = "Login",
                TextColor = Constants.NavBarBackgroundColor,
                BackgroundColor = Color.White,
                FontAttributes = FontAttributes.Bold,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                Margin = new Thickness(-10, 0)
			};

			if (Device.RuntimePlatform == Device.iOS)
			{
				loginButton.HeightRequest = 65;
			}

			var loadingIndicator = new ActivityIndicator
			{
				VerticalOptions = LayoutOptions.Center,
				HorizontalOptions = LayoutOptions.Center
			};

			loginButton.Clicked += Login;

			var gridLayout = new Grid
			{
				RowDefinitions = new RowDefinitionCollection
				{
					new RowDefinition { Height = 20},
					new RowDefinition { Height = GridLength.Star },
					new RowDefinition { Height = GridLength.Auto },
					new RowDefinition { Height = GridLength.Star },
					new RowDefinition { Height = GridLength.Auto },
				},
				RowSpacing = 0
			};

			gridLayout.Children.Add(backgroundImage, 0, 2);
			gridLayout.Children.Add(loginButton, 0, 4);
			gridLayout.Children.Add(loadingIndicator, 0, 1, 0, 4);

			Content = gridLayout;

			loadingIndicator.SetBinding(ActivityIndicator.IsVisibleProperty, nameof(BackgroundPageViewModel.IsBusy));
			loadingIndicator.SetBinding(ActivityIndicator.IsRunningProperty, nameof(BackgroundPageViewModel.IsBusy));

			if (Device.RuntimePlatform == Device.Android)
			{
				loadingIndicator.HeightRequest = 50;
				loadingIndicator.WidthRequest = 50;
			}
		}

        bool shouldShowLogin = true;

		protected override async void OnAppearing()
		{
			if (shouldShowLogin)
			{
				await Task.Delay(250);
				shouldShowLogin = await ViewModel.LoginAsync();
			}
		}

		async void Login(object sender, EventArgs e) => await ViewModel.LoginAsync();
	}
}