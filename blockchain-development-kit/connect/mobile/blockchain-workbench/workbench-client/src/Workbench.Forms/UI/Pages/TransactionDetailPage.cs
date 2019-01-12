using System;
using Xamarin.Forms;
using Workbench.Forms.Models;
using System.Data.SqlTypes;
using System.Linq;
using Workbench.Client.Models;
namespace Workbench.Forms.UI.Pages
{
	public class TransactionDetailPage : ContentPage
    {
		Label ContractStateLabel, ActionTakenLabel, DateTimeLabel, BlockLabel, FromAddressLabel, TXHashLabel;
        
		Label ContractStateValue, ActionTakenValue, DateTimeValue, BlockValue, FromAddressValue, TXHashValue;

		public TransactionDetailPage(BlockModel Block)
        {
			Title = "Transaction Details";
            var scrollView = new ScrollView
            {
                HorizontalOptions = LayoutOptions.FillAndExpand,
                BackgroundColor = Color.White,
            };

			var stacklayout = new StackLayout
			{
                HorizontalOptions = LayoutOptions.FillAndExpand,
                Padding = 20,
                BackgroundColor = Color.White
			};

			ContractStateLabel = new Label
			{
                Text = "Contract State",
                FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
			};

			ActionTakenLabel = new Label
            {
                Text = "Action Taken",
                FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
            };
                     

			DateTimeLabel = new Label
            {
                Text = "Date",
                FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
            };
                    
			BlockLabel = new Label
            {
                Text = "Block",
                FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
            }; 

			FromAddressLabel = new Label
            {
                Text = "From Address",
                FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
            }; 

			TXHashLabel = new Label
            {
                Text = "TX Hash",
				FontSize = 16,
				FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Start
            };


			ContractStateValue = new Label
            {
                Text = App.ViewModel.Contract.States.FirstOrDefault(X=>X.Id == Block.Action.WorkflowStateId)?.DisplayName ?? "Verifying",
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            };  

			ActionTakenValue = new Label
            {
				Text = Block.Function.Name,
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            }; 


			DateTimeValue = new Label
            {
                Text = Block.Action.Timestamp.Value.ToLocalTime().ToString("g"),
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            };

			BlockValue = new Label
            {
                Text = Block.Transaction?.BlockId.ToString() ?? "Verifying",
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            }; 

			FromAddressValue = new Label
            {
                Text = Block.Transaction?.From ?? "Verifying",
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            }; 

			TXHashValue = new Label
            {
                Text = Block.Transaction?.TransactionHash ?? "Verifying",
                FontSize = 14,
                HorizontalTextAlignment = TextAlignment.Start
            };


			stacklayout.Children.Add(ContractStateLabel);
            stacklayout.Children.Add(ContractStateValue);
			stacklayout.Children.Add(ActionTakenLabel);         
            stacklayout.Children.Add(ActionTakenValue);
            
			var param = Block.Function.Parameters;
            var allUsers = App.ViewModel.AllUsersList;

			foreach (var item in Block.Action.Parameters)
			{
                var ParameterDetails = Block.Function.Parameters.FirstOrDefault(X => X.DisplayName.ToLower().Equals(item.Name.ToLower()));

                if (ParameterDetails != null)
                {
                    // ADD THE FUNCTION PARAMETER NAME
                    stacklayout.Children.Add(new Label()
                    {
                        Text = ParameterDetails.DisplayName.ToUpper(),
                        FontSize = 16,
                        FontAttributes = FontAttributes.Bold,
                        HorizontalTextAlignment = TextAlignment.Start
                    });

                    var isParameterApplicationRole = App.ViewModel.CurrentApplication.ApplicationRoles?.FirstOrDefault(_p => _p.Name.Equals(ParameterDetails?.Type.Name));

                    if ((isParameterApplicationRole is null) && !ParameterDetails.Type.Name.Equals(ContractParameterType.User))
                    {
                        if (ParameterDetails.Type.Name.Equals(ContractParameterType.Enum))
                        {
                            var itemIndex = Convert.ToInt32(item.Value);
                            stacklayout.Children.Add(new Label()
                            {
                                Text = ParameterDetails.Type.EnumValues[itemIndex],
                                FontSize = 14,
                                HorizontalTextAlignment = TextAlignment.Start
                            });
                        }
                        else
                        {
                            stacklayout.Children.Add(new Label()
                            {
                                Text = item.Value,
                                FontSize = 14,
                                HorizontalTextAlignment = TextAlignment.Start
                            });
                        }
                    }
                    else //Parameter is a USER Type
                    {
                        var userID = allUsers.SelectMany(y => y.UserChainMappings).FirstOrDefault(chainId => chainId.ChainIdentifier.Equals(item?.Value));
                        if (userID != null)
                        {
                            var displayUser = allUsers?.FirstOrDefault(_id => _id.UserId.Equals(userID?.UserId));

                            stacklayout.Children.Add(new Label()
                            {
                                Text = displayUser?.DisplayName ?? "None",
                                FontSize = 14,
                                HorizontalTextAlignment = TextAlignment.Start
                            });
                        }
                        else
                        {
                            stacklayout.Children.Add(new Label()
                            {
                                Text = "None",
                                FontSize = 14,
                                HorizontalTextAlignment = TextAlignment.Start
                            });
                        }

                    }
                }
			}
         
			stacklayout.Children.Add(DateTimeLabel);
			stacklayout.Children.Add(DateTimeValue);
			stacklayout.Children.Add(BlockLabel);
			stacklayout.Children.Add(BlockValue);
			stacklayout.Children.Add(FromAddressLabel);
			stacklayout.Children.Add(FromAddressValue);
			stacklayout.Children.Add(TXHashLabel);
            stacklayout.Children.Add(TXHashValue);
         
			scrollView.Content = stacklayout;
			Content = scrollView;
        }
    }
}
