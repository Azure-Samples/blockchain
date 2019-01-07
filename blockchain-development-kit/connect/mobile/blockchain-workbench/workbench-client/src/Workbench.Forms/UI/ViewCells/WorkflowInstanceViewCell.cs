using Xamarin.Forms;

using Workbench.Client.Models;
using Workbench.Forms.Models;
using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Controls;

namespace Workbench.Forms.UI.ViewCells
{
    public class WorkflowInstanceViewCell : ViewCell
    {
        Label dynamicContent;
		Label contractTitle;
        public WorkflowInstanceViewCell()
        {
			contractTitle = new Label
			{
				Text = " ", 
                FontAttributes = FontAttributes.Bold,
				HorizontalOptions = LayoutOptions.CenterAndExpand
			};
			
            dynamicContent = new Label
            {
                Text = " ",
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.CenterAndExpand
            };
			if (Device.RuntimePlatform == Device.Android)
			{
				contractTitle.FontSize = 16;
				dynamicContent.FontSize = 14;
			}

			var grid = new Grid
			{
                BackgroundColor = Color.Transparent,
                ColumnSpacing = 10,
                VerticalOptions = LayoutOptions.Fill,
                RowDefinitions = {
					new RowDefinition { Height = 20},
                    new RowDefinition { Height = GridLength.Auto},
                }         
			};

			grid.Children.Add(contractTitle, 0, 0);
			grid.Children.Add(dynamicContent, 0, 1);


			var frame = new MaterialFrame
			{
				Content = grid,
				Padding = 10,
				BackgroundColor = Color.White
            };
         
            View = new ContentView
            {
                Content = frame,
                Padding = 10,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
				BackgroundColor = Color.FromHex("E3E3E3")
            };
        }

        public static ContractPropertyPreferences Preferences { get; set; }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();

            if (Preferences is null) return;

            var contract = App.ViewModel.Contract ?? new Workflow();
			var item = BindingContext as Contract;
			contractTitle.Text = $"{contract.DisplayName} Contract {item?.Id}";

			if (item != null)
			{
				var dynamicText = DynamicFormatter.GetString(contract, item, false, Preferences);

				dynamicContent.FormattedText = dynamicText;
			}
        }
    }
}