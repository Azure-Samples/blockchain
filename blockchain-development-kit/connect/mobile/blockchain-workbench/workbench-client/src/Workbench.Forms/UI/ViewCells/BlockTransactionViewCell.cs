using System;

using Xamarin.Forms;

using Workbench.Forms.Models;
using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Views;
using System.Linq;

namespace Workbench.Forms.UI.ViewCells
{
    public class BlockTransactionViewCell : ViewCell
    {
        BoxView barColor;
        ContactView contactView;
        Label contractActionLabel, completedOnLabel, contractCreatedByLabel;
        
        public BlockTransactionViewCell()
        {
            var grid = new Grid
            {
                Padding = 5,
                BackgroundColor = Color.FromHex("#E3E3E3"),
                HorizontalOptions = LayoutOptions.FillAndExpand,
                ColumnDefinitions = new ColumnDefinitionCollection
                {
					new ColumnDefinition { Width = new GridLength(5, GridUnitType.Star) }
                },
                RowDefinitions = new RowDefinitionCollection
                {
                    new RowDefinition { Height = 40 },
                    new RowDefinition { Height = GridLength.Auto },
                    new RowDefinition { Height = GridLength.Auto },
                    new RowDefinition { Height = GridLength.Auto },
                }
            };

            contractActionLabel = new Label
            {
                FontSize = 18,
                FontAttributes = FontAttributes.Bold,
                LineBreakMode = LineBreakMode.WordWrap
            };
            completedOnLabel = new Label
            {
                FontSize = 14
            };
			contractCreatedByLabel = new Label
            {
                FontSize = 14,
            };

			contactView = new ContactView(hideImage: true)
			{
				HorizontalOptions = LayoutOptions.CenterAndExpand,
				VerticalOptions = LayoutOptions.CenterAndExpand,
                MinimumWidthRequest = 250
            };

            grid.Children.Add(contractActionLabel, 0, 0);
			grid.Children.Add(contractCreatedByLabel, 0, 1);
            grid.Children.Add(completedOnLabel, 0, 2);
            

            barColor = new BoxView
            {
                WidthRequest = 10,
                VerticalOptions = LayoutOptions.FillAndExpand,
                BackgroundColor = Color.FromHex("#F7D21E"),
            };
            var layoutStack = new StackLayout
            {
                Orientation = StackOrientation.Horizontal,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                Spacing = 0,
                Children = { barColor, grid }
            };

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

            var blockDetails = BindingContext as BlockModel;
            if (blockDetails != null)
            {
				contractActionLabel.Text = blockDetails.Function.Name;

				if (string.IsNullOrEmpty(blockDetails.Action.Timestamp.Value.ToString()))
                    completedOnLabel.FormattedText = createFormmattedString("Completed on", "Transaction Pending", completedOnLabel.FontSize);
                else
                    completedOnLabel.FormattedText = createFormmattedString("Completed on", blockDetails.Action.Timestamp.Value.ToLocalTime().ToString("g"), completedOnLabel.FontSize);
                
				var userDetails = App.ViewModel.ApplicationRoleMappingsList.FirstOrDefault(x => x.User.UserId.Equals(blockDetails.Action.UserId));
				if (userDetails != null)
				{
					var role = App.ViewModel.CurrentApplication.ApplicationRoles.FirstOrDefault(x => x.Id.Equals(userDetails.ApplicationRoleId)).Name;
               
					var userModel = new UserModel
					{
						Name = userDetails.User.DisplayName,
						EmailAddress = userDetails.User.EmailAddress,
						UserID = userDetails.User.UserId.ToString(),
						Role = role
					};


					contractCreatedByLabel.Text = $"{userModel.Name} recorded action {blockDetails.Function.Name}";
				}



				if (string.IsNullOrEmpty(blockDetails.Action.Timestamp.Value.ToString()))
                    barColor.BackgroundColor = Constants.NotCompleteAccentColor;
                else
                    barColor.BackgroundColor = Constants.CompleteAccentColor;
            }
        }

        FormattedString createFormmattedString(string propertyName, string displayText, double fontSize)
        {
            var span = new FormattedString();
            span.Spans.Add(new Span { Text = $"{propertyName}: ", FontAttributes = FontAttributes.Bold, FontSize = fontSize });
            span.Spans.Add(new Span { Text = $"{displayText ?? "Posted transaction not complete"}", FontSize = fontSize });

            return span;
        }
    }
}