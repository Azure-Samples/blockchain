using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;

using Xamarin.Forms;

using Rg.Plugins.Popup.Pages;
using Rg.Plugins.Popup.Animations;
using Rg.Plugins.Popup.Extensions;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.UI.Views;
using Workbench.Forms.Helpers;

namespace Workbench.Forms.UI.Pages
{
    public class ActionPopupPage : PopupPage
    {
        string QRData = string.Empty;
        bool isSubmitting = false;
        Button submitButton, cancelButton;
        Frame frameLayout;
        StackLayout parameterViewStack;
		WorkflowFunction contractAction;

        ActivityIndicator loadingIdicator;
        List<User> allUsers;
        int NumberOfActions;
        /* CONTRACT ID AND CONTRACT INSTANCE ID */
		string workflowId, contractId, connectionId;
        public EventHandler ContractActionSucceded;

		public ActionPopupPage(WorkflowFunction action, IEnumerable<User> _allUsers, Contract _contractInstance)
        {
            CloseWhenBackgroundIsClicked = false;
            NumberOfActions = action.Parameters.Count;
            contractAction = action;

			workflowId = _contractInstance.WorkflowId?.ToString();
			contractId = _contractInstance.Id.ToString();
			connectionId = _contractInstance.ConnectionId?.ToString();

            if (_allUsers != null)
                allUsers = new List<User>(_allUsers);
            else
                allUsers = new List<User>();

            setupViews();
            Content = new ScrollView { Content = frameLayout };
        }
        
		public ActionPopupPage(WorkflowFunction action, IEnumerable<User> _allUsers, string qrData, string _contractID, string _contractInstanceID)
        {
            CloseWhenBackgroundIsClicked = false;
            NumberOfActions = action.Parameters.Count;
			contractAction = action;

            workflowId = _contractID;
            contractId = _contractInstanceID;

            if (_allUsers != null)
                allUsers = new List<User>(_allUsers);
            else
                allUsers = new List<User>();

            QRData = qrData;
            setupViews();
            Content = new ScrollView { Content = frameLayout };
        }

        void setupViews()
        {
            var animation = new ScaleAnimation(Rg.Plugins.Popup.Enums.MoveAnimationOptions.Center, Rg.Plugins.Popup.Enums.MoveAnimationOptions.Center);
            animation.ScaleIn = 1.2;
            animation.ScaleOut = 0.8;
            animation.DurationIn = 400;
            animation.DurationOut = 300;
            animation.EasingIn = Easing.SinOut;
            animation.EasingOut = Easing.SinIn;
            animation.HasBackgroundAnimation = true;
            Animation = animation;

            frameLayout = new Frame
            {
                BackgroundColor = Color.White,
                HorizontalOptions = LayoutOptions.Center,
                VerticalOptions = LayoutOptions.Center
            };
            parameterViewStack = new StackLayout
            {
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
                WidthRequest = 300
            };
            var actionLabel = new Label
            {
                Text = contractAction?.DisplayName,
                FontAttributes = FontAttributes.Bold,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                HorizontalTextAlignment = TextAlignment.Center
            };
            loadingIdicator = new ActivityIndicator
            {
                IsRunning = false,
                IsVisible = false
            };
            var gridLayout = new Grid
            {
                Margin = new Thickness(0, 5),
                BackgroundColor = Color.White,
                ColumnSpacing = 10,
                VerticalOptions = LayoutOptions.Fill,
                RowDefinitions = {
                    new RowDefinition { Height = new GridLength(1, GridUnitType.Star)},
                },
                ColumnDefinitions = {
                    new ColumnDefinition{Width = new GridLength(1, GridUnitType.Star)},
                    new ColumnDefinition{Width = new GridLength(1, GridUnitType.Star)},
                }
            };
            submitButton = new Button
            {
                Text = "Submit",
                FontAttributes = FontAttributes.Bold,
				BackgroundColor = Color.FromHex("#2e72c0"),
                TextColor = Color.White
            };
			cancelButton = new Button
			{
				Text = "Cancel",
				FontAttributes = FontAttributes.Bold,
				BackgroundColor = Color.Transparent,
				BorderWidth = 1,
				BorderColor = Color.DimGray,
                TextColor = Color.DimGray
            };

            gridLayout.Children.Add(submitButton, 0, 0);
            gridLayout.Children.Add(cancelButton, 1, 0);

            frameLayout.Content = parameterViewStack;
            parameterViewStack.Children.Add(actionLabel);
            parameterViewStack.Children.Add(loadingIdicator);
            setupActionView();
            parameterViewStack.Children.Add(gridLayout);
        }

        async void handleSubmit(object sender, EventArgs e)
        {
            if (isSubmitting) return;
            isSubmitting = true;

            Device.BeginInvokeOnMainThread(() =>
            {
                loadingIdicator.IsRunning = true;
                loadingIdicator.IsVisible = true;
                frameLayout.IsEnabled = false;
            });


            try
            {
                var result = await submitAction();
                switch (result)
                {
                    case 0:
                        //Action Submission was successful
                        await Navigation.PopPopupAsync();

                        OnContractActionSucceded();
                        break;
                    case 1:
                        //Didn't fill out all the fields
                        await DisplayAlert("Invalid Paremeters", "Please ensure you fill out all required parameters before submitting", "OK");
                        break;
                    case 2:
                        //Something went wrong, dismiss the pop up
                        await Navigation.PopPopupAsync();
                        break;
                }
            }
            finally
            {
                isSubmitting = false;

                Device.BeginInvokeOnMainThread(() =>
                {
                    loadingIdicator.IsRunning = false;
                    loadingIdicator.IsVisible = false;
                    frameLayout.IsEnabled = true;
                });
            }
        }

        async void handleCancel(object sender, EventArgs e)
        {
            await Navigation.PopPopupAsync();
        }

        void setupActionView()
        {
            foreach (var actionParam in contractAction?.Parameters)
            {
				var action = actionParam.Type.Name.ToLower();

                // Add Labels
                var label = new Label
                {
                    HorizontalOptions = LayoutOptions.FillAndExpand,
                    Text = $"{actionParam.DisplayName}:"
                };
                parameterViewStack.Children.Add(label);

                //Check if action parameter is within the application roles     
                var currentAppRole = App.ViewModel.CurrentApplication.ApplicationRoles.FirstOrDefault(x => x.Name.ToLower().Equals(action));
                if (currentAppRole != null)
                {
                    var picker = new Picker
                    {
                        HorizontalOptions = LayoutOptions.FillAndExpand,
                        Title = actionParam.DisplayName
                    };

                    var listOfRoles = App.ViewModel.ApplicationRoleMappingsList.Where(X => X.ApplicationRoleId.Equals(currentAppRole.Id));

                    picker.ItemsSource = listOfRoles.Select(x => x.User).ToList().Select(Y => Y.DisplayName).ToList();
                    parameterViewStack.Children.Add(picker);
                }
                else
                {
                    switch (action)
                    {
                        case ContractParameterType.String:
                        case ContractParameterType.Address:
                        case ContractParameterType.Contract:
                            var stringEntry = new Entry
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand
                            };
                            parameterViewStack.Children.Add(stringEntry);
                            break;
                            
                        case ContractParameterType.Money:
                        case ContractParameterType.Uint:
						case ContractParameterType.Int:
                            var moneyEntry = new Entry
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand,
                                Keyboard = Keyboard.Numeric
                            };
                            parameterViewStack.Children.Add(moneyEntry);
                            break;
						case ContractParameterType.Device:
                        case ContractParameterType.User:
                            var picker = new Picker
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand,
                                Title = actionParam.DisplayName
                            };
                            var listOfRoles = App.ViewModel.ApplicationRoleMappingsList.Where(X => X.ApplicationRoleId.Equals(currentAppRole.Id));

                            picker.ItemsSource = listOfRoles.Select(x => x.User).ToList().Select(Y => Y.DisplayName).ToList();
                            parameterViewStack.Children.Add(picker);
                            break;
                        case ContractParameterType.Enum:
                            var enumPicker = new Picker
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand,
                                Title = actionParam.DisplayName
                            };

                            enumPicker.ItemsSource = actionParam.Type.EnumValues;
                           
                            parameterViewStack.Children.Add(enumPicker);
                            break;
                        case ContractParameterType.Bool:
                            var boolPicker = new Picker
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand,
                                Title = actionParam.DisplayName
                            };

                            boolPicker.ItemsSource = new List<string>() { "True", "False" };
                            parameterViewStack.Children.Add(boolPicker);
                            break;
                        case ContractParameterType.Array:
                            var arrayEntry = new Entry
                            {
                                HorizontalOptions = LayoutOptions.FillAndExpand
                            };
                            if (actionParam.Type.ElementType.Name.Equals("bool"))
                            {
                                arrayEntry.Keyboard = Keyboard.Default;
                                arrayEntry.Placeholder = "Input a comma separated array of booleans";
                            }
                            else
                            {
                                arrayEntry.Placeholder = "Input a comma separated array of integers";
                                arrayEntry.Keyboard = Keyboard.Numeric;
                            }
                            parameterViewStack.Children.Add(arrayEntry);
                            break;
                        default:
							AppCenterHelper.Report(new Exception($"unsupported data type encountered: {action}"));
							break;
                    }
                }
            }
        }

        void OnContractActionSucceded() =>
            ContractActionSucceded?.Invoke(this, EventArgs.Empty);

        /// <summary>
        /// Submits the action.
        /// </summary>
        /// <returns>Returns 0 if action submission is successful. Returns 1 if a parameters is missing. Returns 2 if Post fails</returns>
        async Task<int> submitAction()
        {
            string postSuccess = string.Empty;

			// CREATE A NEW CONTRACT
			if (string.IsNullOrEmpty(contractId))
			{
                var newContractAction = new ActionInformation();

                var values = getParametersForUpload();

                if (values != null)
                {
                    newContractAction.WorkflowActionParameters = values;

                    newContractAction.WorkflowFunctionId = contractAction.Id;

                    if (contractAction.Parameters == null) return 1;

                    postSuccess = await GatewayApi.Instance.CreateNewContractAsync(newContractAction, workflowId, App.ViewModel.CurrentApplication.Id.ToString(), connectionId);
                }
                else
                {
                    return 1;
                }
			}
            else
            {
				var newContractAction = new ActionInformation();
				newContractAction.WorkflowActionParameters = getParametersForUpload();
				newContractAction.WorkflowFunctionId = contractAction.Id;

				if (newContractAction.WorkflowActionParameters == null) return 1;

				postSuccess = await GatewayApi.Instance.PostWorkflowActionAsync(newContractAction, contractId);
			}

            if (!string.IsNullOrEmpty(postSuccess))
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    await DisplayAlert("Action Submit Failed", postSuccess, "OK");
                });
                return 2;
            }

            return 0;
        }

		List<WorkflowActionParameter> getParametersForUpload()
        {
			var parameters = new List<WorkflowActionParameter>();
            //Process the rest of the Entry and Picker parameters 
            var parameterViews = parameterViewStack.Children.Where(x => x.GetType().Equals(typeof(Entry))
                                                            || x.GetType().Equals(typeof(Picker)));
            for (int i = 0; i < parameterViews.Count(); i++)
            {
				var currentParameter = contractAction?.Parameters[i];
				var newParam = new WorkflowActionParameter();

                newParam.Name = currentParameter.Name;
				if (App.ViewModel.CurrentApplication.ApplicationRoles.FirstOrDefault(x => x.Name.ToLower().Equals(currentParameter.Type.Name.ToLower())) != null)
				{
					var picker = parameterViews.ElementAt(i) as Picker;
                    if (picker.SelectedItem == null)
                        return null;
                    var userName = picker.SelectedItem?.ToString().ToUpper();
                    var chainAddresses = allUsers.FirstOrDefault(x => x.DisplayName.ToUpper().Equals(userName)).UserChainMappings;
                    var chainAddress = chainAddresses.FirstOrDefault(x => x.ConnectionId.ToString().Equals(connectionId)).ChainIdentifier;
                    newParam.Value = chainAddress;               
				}
				else
				{
					//TODO Need to add data validation
					switch (currentParameter.Type.Name.ToLower())
					{
                        case ContractParameterType.Uint:
                        case ContractParameterType.Int:
                        case ContractParameterType.Money:
                            var numberEntry = parameterViews.ElementAt(i) as Entry;
                            if (string.IsNullOrEmpty(numberEntry.Text))
                            {
                                return null;
                            }

                            int paramValueAsNumber;

                            if (!Int32.TryParse(numberEntry.Text, out paramValueAsNumber))
                            {
                                return null;
                            }

                            newParam.Value = paramValueAsNumber.ToString();

                            break;
						case ContractParameterType.String:
                        case ContractParameterType.Address:
                        case ContractParameterType.Contract:
							var entry = parameterViews.ElementAt(i) as Entry;
							if (string.IsNullOrEmpty(entry.Text))
								return null;
							newParam.Value = entry.Text;
							break;
						case ContractParameterType.Device:
						case ContractParameterType.User:
							var picker = parameterViews.ElementAt(i) as Picker;
							if (picker.SelectedItem == null)
								return null;
							var userName = picker.SelectedItem?.ToString().ToUpper();
							var chainAddresses = allUsers.FirstOrDefault(x => x.DisplayName.ToUpper().Equals(userName)).UserChainMappings;
							var chainAddress = chainAddresses.FirstOrDefault(x => x.ConnectionId.ToString().Equals(connectionId)).ChainIdentifier;
							newParam.Value = chainAddress;
							break;
                        case ContractParameterType.Enum:
                            var enumPicker = parameterViews.ElementAt(i) as Picker;
                            if (enumPicker.SelectedItem == null)
                                return null;
                            newParam.Value = enumPicker.SelectedIndex.ToString();
                            break;
                        case ContractParameterType.Bool:
                            var boolPicker = parameterViews.ElementAt(i) as Picker;
                            if (boolPicker.SelectedItem == null)
                                return null;
                            newParam.Value = boolPicker.SelectedItem?.ToString();
                            break;
                        case ContractParameterType.Array:
                            var arrayEntry = parameterViews.ElementAt(i) as Entry;
                            if (string.IsNullOrEmpty(arrayEntry.Text))
                            {
                                return null;
                            }
                            if (!arrayEntry.Text.Contains(","))
                            {
                                return null;
                            }
                            var arrayLength = arrayEntry.Text.Split(',').Length;
                            var countOfCommas = arrayEntry.Text.Count(c => c == ',');
                            if (countOfCommas == (arrayLength - 1))
                            {
                                newParam.Value = arrayEntry.Text;
                            }
                            else
                            {
                                return null;
                            }
                            break;
						default:
							break;
					}
				}

                parameters.Add(newParam);
            }

			return parameters;
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
            submitButton.Clicked += handleSubmit;
            cancelButton.Clicked += handleCancel;
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
            submitButton.Clicked -= handleSubmit;
            cancelButton.Clicked -= handleCancel;
        }
    }
}