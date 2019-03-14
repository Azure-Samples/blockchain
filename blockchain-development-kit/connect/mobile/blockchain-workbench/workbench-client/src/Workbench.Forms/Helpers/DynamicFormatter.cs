using System.Linq;
using System.Collections.Generic;

using Xamarin.Forms;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.Models;
using Workbench.Forms.ViewModels;
using Newtonsoft.Json.Linq;
using System.Linq.Expressions;
using System;

namespace Workbench.Forms.Helpers
{
	public static class DynamicFormatter
	{
        public static FormattedString GetString(Workflow workflow, Contract item, bool displayAddress, ContractPropertyPreferences Preferences = null)
        {
            try
            {

                var allUsers = App.ViewModel.AllUsersList;

                var formattedString = new FormattedString();
                var createdBy = allUsers.Where(u => u.UserId == item.DeployedByUserId)?.FirstOrDefault();

                List<string> CurrentProperties;

                if (Preferences == null)
                {
                    CurrentProperties = new List<string>(item.ContractProperties.Select(i => i.WorkflowPropertyId).ToList());
                }
                else
                {
                    CurrentProperties = new List<string>(Preferences.PropertyIds);
                }

                if (displayAddress)
                {
                    formattedString.Spans.Add(new Span { Text = $"Contract Address:\n", FontAttributes = FontAttributes.Bold });
                    formattedString.Spans.Add(new Span { Text = $"{item.LedgerIdentifier}\n"});
                }

                for (var x = 0; x < CurrentProperties.Count; x++)
                {
                    var preferenceId = CurrentProperties[x];
                    var propertyToDisplay = workflow?.Properties?.Where(prop => prop.Id.ToString() == preferenceId).FirstOrDefault();

                    if (propertyToDisplay != null)
                    {
                        //AT THE TITLE OF THE PROPERTY
                        formattedString.Spans.Add(new Span { Text = $"{propertyToDisplay.DisplayName}: ", FontAttributes = FontAttributes.Bold });

                        //GET THE VALUE OF THE PROPERTY FROM THE WORKFLOW INSTANCE
                        var propertyValue = item.ContractProperties.Find(prop => prop.WorkflowPropertyId == propertyToDisplay.Id.ToString());

                        if (propertyValue == null)
                        {
                            formattedString.Spans.Add(new Span { Text = "Verifying \n" });
                        }
                        else
                        {

                            if (propertyToDisplay?.Type.Name == ContractParameterType.User)
                            {
                                var userID = allUsers.SelectMany(y => y.UserChainMappings).FirstOrDefault(chainId => chainId.ChainIdentifier.Equals(propertyValue?.Value));
                                if (userID != null)
                                {
                                    var displayUser = allUsers?.FirstOrDefault(_id => _id.UserId.Equals(userID?.UserId));

                                    if (string.IsNullOrEmpty(displayUser?.DisplayName))
                                        formattedString.Spans.Add(new Span { Text = "None" });
                                    else
                                        formattedString.Spans.Add(new Span { Text = displayUser.DisplayName });
                                }
                                else
                                {
                                    formattedString.Spans.Add(new Span { Text = "None" });
                                }
                            }
                            //COVERS CASES WHERE THE NAME COULD NOT BE OF TYPE "USER" BUT OF ONE OF THE APPLICATION ROLES
                            else if (propertyValue?.Value?.Contains("0x") ?? false)
                            {
                                var applicationRole = App.ViewModel.CurrentApplication.ApplicationRoles?.FirstOrDefault(_p => _p.Name.Equals(propertyToDisplay?.Type.Name));

                                if (applicationRole != null)
                                {
                                    var userID = allUsers.SelectMany(y => y.UserChainMappings).FirstOrDefault(chainId => chainId.ChainIdentifier.Equals(propertyValue?.Value));
                                    if (userID != null)
                                    {
                                        var displayUser = allUsers?.FirstOrDefault(_id => _id.UserId.Equals(userID?.UserId));

                                        if (string.IsNullOrEmpty(displayUser?.DisplayName))
                                            formattedString.Spans.Add(new Span { Text = "None" });
                                        else
                                            formattedString.Spans.Add(new Span { Text = displayUser.DisplayName });
                                    }
                                    else
                                    {
                                        formattedString.Spans.Add(new Span { Text = "None" });
                                    }
                                }
                                else
                                {
                                    formattedString.Spans.Add(new Span { Text = propertyValue?.Value });
                                }
                            }
                            else if (propertyToDisplay?.Type.Name == ContractParameterType.State)
                            {
                                var stateToDisplay = workflow.States.FirstOrDefault(_s => _s.Value.ToString().Equals(propertyValue?.Value));

                                if (stateToDisplay?.Style.ToLower().Equals("failure") ?? false)
                                {
                                    formattedString.Spans.Add(new Span { Text = stateToDisplay?.DisplayName, ForegroundColor = Color.Red });

                                }
                                else
                                {
                                    formattedString.Spans.Add(new Span { Text = stateToDisplay?.DisplayName });
                                }

                            }
                            else if (propertyToDisplay?.Type.Name == ContractParameterType.Money)
                            {
                                var dollarValue = System.Convert.ToDouble(propertyValue.Value);

                                formattedString.Spans.Add(new Span { Text = dollarValue.ToString("C") });
                            }
                            else if (propertyToDisplay?.Type.Name == ContractParameterType.Enum)
                            {
                                var enumIndex = Convert.ToInt32(propertyValue.Value);
                                var enumValue = propertyToDisplay?.Type.EnumValues[enumIndex];
                                formattedString.Spans.Add(new Span { Text = enumValue });
                            }
                            else
                            {
                                formattedString.Spans.Add(new Span { Text = propertyValue?.Value });
                            }

                            if (x != CurrentProperties.Count - 1)
                                formattedString.Spans.Add(new Span { Text = "\n" });
                        }
                    }
                }

                return formattedString;
            }
            catch (Exception ex)
            {
                AppCenterHelper.Report(ex);
                return "";
            }
        }
	}
}