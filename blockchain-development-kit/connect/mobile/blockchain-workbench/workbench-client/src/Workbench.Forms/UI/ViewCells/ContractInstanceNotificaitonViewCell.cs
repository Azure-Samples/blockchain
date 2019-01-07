using Xamarin.Forms;

using Workbench.Forms.Helpers;

namespace Workbench.Forms.UI.ViewCells
{
    public class ContractInstanceNotificaitonViewCell : ViewCell
    {
        Label title, message;

        public ContractInstanceNotificaitonViewCell()
        {
            var grid = new Grid
            {
                Padding = 10,
                BackgroundColor = Color.FromHex("#E3E3E3"),
                HorizontalOptions = LayoutOptions.FillAndExpand,
                ColumnDefinitions = new ColumnDefinitionCollection
                {
                    new ColumnDefinition { Width = GridLength.Star },
                    new ColumnDefinition { Width = GridLength.Auto },
                },
            };

            title = new Label
            {
                FontSize = 18,
                VerticalOptions = LayoutOptions.End,
                FontAttributes = FontAttributes.Bold,
                LineBreakMode = LineBreakMode.WordWrap
            };
            message = new Label
            {
                FontSize = 14,
                LineBreakMode = LineBreakMode.WordWrap
            };

            grid.Children.Add(title, 0, 1, 0, 1);
            grid.Children.Add(message, 0, 1, 1, 2);

            switch (Device.RuntimePlatform)
            {
                case Device.iOS:
                    var iOSViewButton = new ContentView
                    {
                        Content = new Label
                        {
                            Text = " VIEW ",
                            TextColor = Constants.ButtonTextColor,
                            VerticalTextAlignment = TextAlignment.Center,
                            FontAttributes = FontAttributes.Bold,
                            HeightRequest = 50,
                            FontSize = 20
                        },
                        Padding = new Thickness(10, 0),
                        BackgroundColor = Constants.ButtonBackgroundColor,
                        HorizontalOptions = LayoutOptions.End,
                        VerticalOptions = LayoutOptions.Center,
                    };


                    grid.Children.Add(iOSViewButton, 1, 2, 0, 2);

                    View = new ContentView
                    {
                        Content = grid,
                        Padding = 10,
                        HorizontalOptions = LayoutOptions.FillAndExpand,
                        VerticalOptions = LayoutOptions.FillAndExpand,
                        BackgroundColor = Color.FromRgb(210, 210, 210),
                    };
                    break;

                case Device.Android:
                    var androidViewButton = new Frame
                    {
                        Content = new Label
                        {
                            Text = " VIEW ",
                            TextColor = Constants.ButtonTextColor,
                            VerticalTextAlignment = TextAlignment.Center,
                            FontAttributes = FontAttributes.Bold,
                            HeightRequest = 50,
                            FontSize = 20
                        },
                        Padding = new Thickness(10, 0),
                        BackgroundColor = Constants.ButtonBackgroundColor,
                        HorizontalOptions = LayoutOptions.End,
                        VerticalOptions = LayoutOptions.Center,
                    };


                    grid.Children.Add(androidViewButton, 1, 2, 0, 2);
                    View = new ContentView
                    {
                        Content = new Frame { Content = grid, Margin = 5, Padding = 0 },
                        Padding = 10,
                        HorizontalOptions = LayoutOptions.FillAndExpand,
                        VerticalOptions = LayoutOptions.FillAndExpand,
                        BackgroundColor = Color.FromRgb(210, 210, 210),
                    };
                    break;
            }
        }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();

            //var notification = BindingContext as ContractInstanceNotification;

            //title.Text = notification?.HeaderText ?? "No Title";
            //message.Text = notification?.MessageText ?? "No Messages";
        }
    }
}