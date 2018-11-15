using System;
using Workbench.Forms.Models;
using Xamarin.Forms;
using SQLite;
using System.Threading.Tasks;
namespace Workbench.Forms.UI.Views
{
	public class SelectedButton : Button
	{
		public SelectedButton()
		{
			setupUi();
		}
		bool isPopupShown = false;
		Command longPressCommand;
        public Command LongPressCommand =>
    		longPressCommand ?? (longPressCommand = new Command(async () => await LongPressed()));
              
		public SelectedButton(BlockchainEnvironment environment)
		{
			Environment = environment;

			setupUi();
		}

		void setupUi()
		{
			HorizontalOptions = LayoutOptions.Center;
			VerticalOptions = LayoutOptions.Center;
			HeightRequest = 60;
			WidthRequest = 60;
			CornerRadius = 30;
			BorderWidth = 0;
			BorderColor = Color.Transparent;

            TextColor = Color.White;

			Effects.Add(Effect.Resolve("Xamarin.SelectedButtonEffect"));
		}
		BlockchainEnvironment environment = new BlockchainEnvironment();
		public BlockchainEnvironment Environment
		{
			get => environment;
			set
			{
				environment = value;

				if (string.IsNullOrEmpty(environment.NickName))
				{
					Text = environment?.SiteUrl;
				}
				else
				{
					Text = environment?.NickName?.Substring(0, 2);
				}
			}
		}

		bool isSelected = false;
		public bool IsSelected
		{
			get => isSelected;
			set
			{
				isSelected = value;

				OnPropertyChanged(nameof(IsSelected));
			}
		}

		async Task LongPressed()
		{
			if (!isPopupShown)
			{
				isPopupShown = true;
				var res = await App.Current.MainPage.DisplayAlert("Remove Environment?", "Would you like to remove the selected environment and log out?", "Yes", "No");
				if (res)
				{
					await App.Logout(true);
				}
				isPopupShown = false;
			}
				
		}

       
	}
}