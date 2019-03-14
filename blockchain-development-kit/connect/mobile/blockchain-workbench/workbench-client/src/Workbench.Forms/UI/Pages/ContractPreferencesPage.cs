using System.Linq;
using System.ComponentModel;
using System.Threading.Tasks;
using System.Collections.Generic;

using Xamarin.Forms;

using Workbench.Client.Models;

using Workbench.Forms.Models;
using Workbench.Forms.Helpers;
using Workbench.Forms.UI.ViewCells;

namespace Workbench.Forms.UI.Pages
{
	public class ContractPreferencesPage : ContentPage
	{
		public class WrappedSelection<T> : INotifyPropertyChanged
		{
			public T Item { get; set; }
			bool isSelected = false;
			public bool IsSelected
			{
				get
				{
					return isSelected;
				}
				set
				{
					if (isSelected != value)
					{
						isSelected = value;
						PropertyChanged(this, new PropertyChangedEventArgs(nameof(IsSelected)));
					}
				}
			}
			public event PropertyChangedEventHandler PropertyChanged = delegate { };
		}

		public class WrappedItemSelectionTemplate : ViewCell
		{
			public WrappedItemSelectionTemplate() : base()
			{
				Label name = new Label();
				Switch mainSwitch = new Switch();

				name.SetBinding(Label.TextProperty, new Binding("Item.Name"));
				mainSwitch.SetBinding(Switch.IsToggledProperty, new Binding("IsSelected"));

				RelativeLayout layout = new RelativeLayout();
				layout.Children.Add(name,
					Constraint.Constant(5),
					Constraint.Constant(5),
					Constraint.RelativeToParent(p => p.Width - 60),
					Constraint.RelativeToParent(p => p.Height - 10)
				);
				layout.Children.Add(mainSwitch,
					Constraint.RelativeToParent(p => p.Width - 55),
					Constraint.Constant(5),
					Constraint.Constant(50),
					Constraint.RelativeToParent(p => p.Height - 10)
				);

				View = layout;
			}
		}

		public List<WrappedSelection<Property>> WrappedItems = new List<WrappedSelection<Property>>();

		Workflow workflowDefinition;

		public ContractPreferencesPage(Workflow workflow)
		{
			Title = "Properties to Display";

			workflowDefinition = workflow;
			WrappedItems = workflow.Properties.Select(item => new WrappedSelection<Property> { Item = item, IsSelected = false }).ToList();

			var mainList = new ListView()
			{
				ItemsSource = WrappedItems,
				ItemTemplate = new DataTemplate(typeof(WrappedItemSelectionTemplate)),
			};

			var saveButton = new Button
			{
				AutomationId = "SaveButton",
				Text = "Save Selected Properties",
				HorizontalOptions = LayoutOptions.FillAndExpand,
				BackgroundColor = Constants.ButtonBackgroundColor,
				TextColor = Constants.ButtonTextColor,
                Margin = new Thickness(-10, 0)
			};

			saveButton.Clicked += async (sender, e) => await SaveSelectionAsync();

			mainList.ItemSelected += (sender, e) =>
			{
				if (e.SelectedItem == null) return;
				var o = (WrappedSelection<Property>)e.SelectedItem;
				o.IsSelected = !o.IsSelected;
				((ListView)sender).SelectedItem = null; //de-select
			};


			Content = new StackLayout
			{
				Spacing = 0,
				Children =
				{
					mainList,
					saveButton
				}
			};

			ToolbarItems.Add(new ToolbarItem("All", null, SelectAll, ToolbarItemOrder.Primary));
			ToolbarItems.Add(new ToolbarItem("None", null, SelectNone, ToolbarItemOrder.Primary));
		}

		protected async override void OnAppearing()
		{
			await SelectSaved();
		}
        
		async Task SelectSaved()
		{
			var savedPreferences = await LocalDbHelper.Instance.GetContractPropertyPreferencesAsync(workflowDefinition.Id.ToString());

			if (savedPreferences != null)
			{
				foreach (var wrappedItem in WrappedItems)
				{
					if (savedPreferences.PropertyIds.Contains(wrappedItem.Item.Id.ToString()))
					{
						wrappedItem.IsSelected = true;
					}
				}
			}
		}

		void SelectAll()
		{
			foreach (var wrappedItem in WrappedItems)
			{
				wrappedItem.IsSelected = true;
			}
		}
		void SelectNone()
		{
			foreach (var wrappedItem in WrappedItems)
			{
				wrappedItem.IsSelected = false;
			}
		}

		public async Task SaveSelectionAsync()
		{
			var selectedPropertyIds = string.Empty;
			var selectedItems = WrappedItems.Where(item => item.IsSelected).Select(wrappedItem => wrappedItem.Item).ToList();

			foreach (var item in selectedItems)
			{
				selectedPropertyIds += $"{item.Id}|";
			}

			if (!string.IsNullOrEmpty(selectedPropertyIds))
			{
				selectedPropertyIds = selectedPropertyIds.Remove(selectedPropertyIds.Length - 1, 1);

				var contractPreferences = new ContractPropertyPreferences
				{
					ContractId = workflowDefinition.Id.ToString(),
					DisplayedPropertyIds = selectedPropertyIds
				};

				await LocalDbHelper.Instance.SaveContractPropertyPreferencesAsync(contractPreferences);

				WorkflowInstanceViewCell.Preferences = contractPreferences;
                
				App.CONTRACTS_FILTER_CHANGED = true;

				await Navigation.PopAsync();
			}
		}
	}
}