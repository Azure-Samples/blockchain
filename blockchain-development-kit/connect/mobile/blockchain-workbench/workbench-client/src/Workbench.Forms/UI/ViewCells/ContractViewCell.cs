using Xamarin.Forms;

using Workbench.Client.Models;

namespace Workbench.Forms.UI.ViewCells
{
    public class ContractViewCell : ViewCell
    {
        Label contractName;

        public ContractViewCell()
        {
            var grid = new Grid
            {
                Padding = 10,
                BackgroundColor = Color.FromHex("#E3E3E3"),
                HorizontalOptions = LayoutOptions.FillAndExpand,
                RowDefinitions = new RowDefinitionCollection { new RowDefinition { Height = GridLength.Star } }
            };

            contractName = new Label
            {
                AutomationId = "ContractViewCell",
                Margin = new Thickness(20, 0, 0, 0),
                VerticalOptions = LayoutOptions.Center,
				HorizontalOptions = LayoutOptions.CenterAndExpand,
                FontAttributes = FontAttributes.Bold
            };

            grid.Children.Add(contractName, 0, 0);

            View = new ContentView
            {
                Content = grid,
                Padding = 10,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
                BackgroundColor = Color.White
            };
        }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();

			var item = BindingContext as Client.Models.Application;
            
			contractName.Text = item?.DisplayName?.ToString() ?? "Description not available";
        }
    }
}