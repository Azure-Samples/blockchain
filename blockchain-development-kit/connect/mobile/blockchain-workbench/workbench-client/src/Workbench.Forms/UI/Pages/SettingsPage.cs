using System;
using Xamarin.Forms;
using Workbench.Forms.Helpers;
namespace Workbench.Forms.UI.Pages
{
    public class SettingsPage : ContentPage
    {
        Button RemoveEnvironmentButton;
        static int PADDING = 10;

        public SettingsPage()
        {
            Title = "Settings";

            Padding = PADDING;

            RelativeLayout MainRelativeLayout = new RelativeLayout
            {
                Padding = PADDING
            };
        
            Label envLabel = new Label
            {
                Text = "Show Environment Data",
                VerticalTextAlignment = TextAlignment.Center
            };

            Switch envSwitch = new Switch
            {
                IsToggled = Settings.DebugModeEnabled
            };
            envSwitch.Toggled += (sender, e) => { 
                Settings.DebugModeEnabled = envSwitch.IsToggled;
                App.ViewModel.DebugModeEnabled = envSwitch.IsToggled;
            };

            Label appLabel = new Label
            {
                Text = "Show Only Enabled Apps",
                VerticalTextAlignment = TextAlignment.Center
            };

            Switch appSwitch = new Switch
            {
                IsToggled = Settings.ShowOnlyEnabledApps
            };

            appSwitch.Toggled += (sender, e) => {
                Settings.ShowOnlyEnabledApps = appSwitch.IsToggled;
            };

            RemoveEnvironmentButton = new Button
            {
                Margin = new Thickness(0, 10, 0, 0),
                TextColor = Color.Red,
                Text = "Remove Current Environment",
                HorizontalOptions = LayoutOptions.CenterAndExpand,
                BorderColor = Color.Red,
                BackgroundColor = Color.Transparent,
            };
            Func<RelativeLayout, double> RemoveEnvironmentButtonWidth = (parent) => RemoveEnvironmentButton.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;

            Label CurrentEnvLabel = new Label { Text = "Current Environment:", FontAttributes = FontAttributes.Bold };

            Label CurrentEnvDetails = new Label
            {
                FormattedText = App.ViewModel.EnvironmentString,
                LineBreakMode = LineBreakMode.WordWrap
            };

            MainRelativeLayout.Children.Add(envLabel,
                Constraint.RelativeToParent(p => p.X + 5),
                Constraint.RelativeToParent(p => p.Y),
                Constraint.RelativeToParent(p => p.Width - 60),
                Constraint.Constant(40)
            );
            MainRelativeLayout.Children.Add(envSwitch,
                Constraint.RelativeToParent(p => p.Width - 65),
                Constraint.RelativeToParent(p => p.Y + 2),
                Constraint.Constant(50),
                Constraint.Constant(40)
            );

            MainRelativeLayout.Children.Add(appLabel,
                Constraint.RelativeToParent(p => p.X + 5),
                Constraint.RelativeToView(envLabel, (parent, sibling) =>
                {
                    return sibling.Bounds.Bottom + 10;
                }), 
                Constraint.RelativeToParent(p => p.Width - 60),
                Constraint.Constant(40)
            );
            MainRelativeLayout.Children.Add(appSwitch,
                Constraint.RelativeToParent(p => p.Width - 65),
                Constraint.RelativeToView(envLabel, (parent, sibling) =>
                {
                    return sibling.Bounds.Bottom + 10;
                }), 
                Constraint.Constant(50),
                Constraint.Constant(40)
            );

            MainRelativeLayout.Children.Add(CurrentEnvLabel,
                Constraint.RelativeToParent(p => p.X + 5),
                Constraint.RelativeToView(appLabel, (parent, sibling) =>
                {
                    return sibling.Bounds.Bottom + 10;
                })
            );

            MainRelativeLayout.Children.Add(CurrentEnvDetails,
                Constraint.RelativeToParent(p => p.X + 5),
                Constraint.RelativeToView(CurrentEnvLabel, (parent, sibling) =>
                {
                    return sibling.Bounds.Bottom + 10;
                })
            );

            MainRelativeLayout.Children.Add(RemoveEnvironmentButton,
                Constraint.RelativeToParent(p => p.Bounds.Center.X - RemoveEnvironmentButtonWidth(p)/2 - PADDING),
                Constraint.RelativeToView(CurrentEnvDetails, (parent, sibling) =>
                {
                    return sibling.Bounds.Bottom + 5;
                })                 
            );


            Content = MainRelativeLayout;

        }

        async void RemoveEnvironmentButton_Clicked(object sender, EventArgs e)
        {
            var res = await App.Current.MainPage.DisplayAlert("Remove Environment?", "Would you like to remove the current environment and log out?", "Yes", "No");
            if (res)
            {
                await App.Logout(true);
            }
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
            RemoveEnvironmentButton.Clicked += RemoveEnvironmentButton_Clicked;
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
            RemoveEnvironmentButton.Clicked -= RemoveEnvironmentButton_Clicked;
        }

    }
}
