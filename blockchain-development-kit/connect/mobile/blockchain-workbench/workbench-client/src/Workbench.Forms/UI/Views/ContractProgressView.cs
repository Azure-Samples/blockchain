using System.Linq;

using Xamarin.Forms;

namespace Workbench.Forms.UI.Views
{
	public class ContractProgressView : Grid
    {
		public ProgressView Progress { get; set; }
        public Label StateLabel { get; set; }

        public ContractProgressView()
        {
			Margin = new Thickness(0, 0, 0, 10);
			Padding = 10;
            RowSpacing = 10;
            BackgroundColor = Color.White;
            RowDefinitions = new RowDefinitionCollection
            {
                new RowDefinition { Height = GridLength.Auto },
                new RowDefinition { Height = GridLength.Auto },
                new RowDefinition { Height = GridLength.Auto },
            };
            ColumnDefinitions = new ColumnDefinitionCollection
            {
                new ColumnDefinition { Width = GridLength.Star },
                new ColumnDefinition { Width = GridLength.Auto },
				new ColumnDefinition { Width = GridLength.Star }
            };

			Progress = new ProgressView
			{
				VerticalOptions = LayoutOptions.StartAndExpand,
				HorizontalOptions = LayoutOptions.CenterAndExpand,
			};
            

			if (Device.RuntimePlatform.Equals(Device.Android))
			{
				Progress.WidthRequest = App.ScreenWidth / 2.3;
				Progress.HeightRequest = App.ScreenWidth / 2.3;
			}

			var titleLabel = new Label
            {
                FontAttributes = FontAttributes.Bold,
                FontSize = 18,
                HorizontalTextAlignment = TextAlignment.Start,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                Text = "STATUS"
            };

            StateLabel = new Label
            {
                FontAttributes = FontAttributes.Bold,
                FontSize = 18,
                HorizontalTextAlignment = TextAlignment.Center,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                LineBreakMode = LineBreakMode.WordWrap
            };
                  
			Children.Add(titleLabel, 0, 0);
			Grid.SetColumnSpan(titleLabel, 2);
            Children.Add(Progress, 1, 1);
            Children.Add(StateLabel, 1, 2);
        }

		public static readonly BindableProperty ContractProperty = BindableProperty.Create(
            propertyName: "Contract",
            returnType: typeof(Workbench.Client.Models.Contract),
            declaringType: typeof(Workbench.Client.Models.Contract),
            defaultValue: null,
            propertyChanged: handleContractChanged);

        public Workbench.Client.Models.Contract Contract
        {
            get { return (Client.Models.Contract)GetValue(ContractProperty); }
            set { SetValue(ContractProperty, value); }
        }

		static void handleContractChanged(BindableObject bindable, object oldValue, object newValue)
		{
			var contractProgress = bindable as ContractProgressView;

            if (contractProgress != null)
            {
                contractProgress.Progress.HorizontalOptions = LayoutOptions.FillAndExpand;

                var contractInstance = newValue as Client.Models.Contract;

                var stateProperty = App.ViewModel.Contract.Properties.FirstOrDefault(x => x.Type.Name.Equals("state"));
                var stateValue = contractInstance?.ContractProperties.FirstOrDefault(x => x.WorkflowPropertyId.Equals(stateProperty.Id.ToString()))?.Value;

                if (stateValue != null)
                {
                    var _contractState = App.ViewModel.Contract.States.FirstOrDefault(x => x.Value.ToString().Equals(stateValue));

                    contractProgress.StateLabel.Text = _contractState.DisplayName;

                    //contractProgress.Progress.LabelText = _contractState.PercentComplete.ToString() + "%";

                    contractProgress.Progress.PercentComplete = (int)_contractState.PercentComplete;

                    if ((int)_contractState.PercentComplete == 100)
                    {
                        if (_contractState.Style.ToLower().Equals("failure"))
                            contractProgress.Progress.LabelStyle = _contractState.Style;
                        else
                            contractProgress.Progress.LabelStyle = "complete";
                    }
                    else
                    {
                        contractProgress.Progress.LabelStyle = _contractState.Style;
                    }
                }
                else
                {
                    contractProgress.StateLabel.Text = "Verifying";
                    contractProgress.Progress.LabelText = "";
                    contractProgress.Progress.PercentComplete = 0;
                    contractProgress.Progress.LabelStyle = "success";
                }
            }
		}
    }
}
