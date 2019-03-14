using System.Linq;

using Xamarin.Forms;

using Workbench.Forms.Helpers;


namespace Workbench.Forms.UI.Views
{
    public class ContractDetailsView : Grid
    {
        public Label DynamicContent { get; set; }

        public ContractDetailsView()
        {
			if (Device.RuntimePlatform.Equals(Device.iOS)){
				Margin = new Thickness(0, 10, 0, 0);
			}
			    
            Padding = 10;
            RowSpacing = 10;
            BackgroundColor = Color.White;
            RowDefinitions = new RowDefinitionCollection
            {
                new RowDefinition { Height = GridLength.Auto },
                new RowDefinition { Height = GridLength.Auto },
            };
            ColumnDefinitions = new ColumnDefinitionCollection
            {
                new ColumnDefinition { Width = GridLength.Auto }
            };

            DynamicContent = new Label
            {
                Text = " ",
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.CenterAndExpand
            };

			var detailsLabel = new Label
            {
                FontAttributes = FontAttributes.Bold,
                FontSize = 18,
                HorizontalTextAlignment = TextAlignment.Start,
                Text = "DETAILS"
            };

			Children.Add(detailsLabel, 0, 0);
            Children.Add(DynamicContent, 0, 1);

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
            var contractDetail = bindable as ContractDetailsView;
			var contractInstance = newValue as Client.Models.Contract;
			var contractPropertiesToDisplay = contractDetail.Contract.ContractProperties.ToList();
			
			var stateProperty = App.ViewModel.Contract.Properties.FirstOrDefault(x => x.Type.Name.Equals("state"));
			var stateValue = contractInstance?.ContractProperties.FirstOrDefault(x => x.WorkflowPropertyId.Equals(stateProperty.Id.ToString()))?.Value;

			var dynamicText = DynamicFormatter.GetString(App.ViewModel.Contract, contractInstance, true);

            contractDetail.DynamicContent.FormattedText = dynamicText;
        }
    }
}