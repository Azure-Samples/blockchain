using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.ObjectModel;

using Xamarin.Forms;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.Helpers;
using System.Collections.Generic;

namespace Workbench.Forms.ViewModels
{
	public class ApplicationsPageViewModel : BaseViewModel
    {
		public ApplicationsPageViewModel()
        {
           Animation = "loading.json";

        }

		public List<User> AllUsersList { get; set; } = new List<User>();
        public List<RoleAssignment> ApplicationRoleMappingsList { get; set; } = new List<RoleAssignment>();
        
		public ObservableCollection<Client.Models.Application> ApplicationListCollection { get; set; } = new ObservableCollection<Client.Models.Application>();

		public string UserName { get => Settings.LoggedInADUser?.UserDetails.DisplayName; }
        
		Client.Models.Application currentApplication;
        
		public Client.Models.Application CurrentApplication
        {
            get { return currentApplication; }
            set
            {
                OnPropertyChanged(nameof(RolesString));
                SetProperty(ref currentApplication, value);
            }
        }

		public async Task SetCurrentApplication(string appId)
        {
            try
            {
                var _app = await GatewayApi.Instance.GetApplicationByIdAsync(appId);
                if (_app != null)
                    CurrentApplication = _app;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
        }

		public FormattedString RolesString
        {
            get
            {
                try
                {
                    if (currentApplication is null) return "No Application Selected";

                    var roleIdsForContract = ApplicationRoleMappingsList.Where(x => x.User.UserId.Equals(Settings.LoggedInADUser?.UserDetails?.UserId)).Select(y => y.ApplicationRoleId).ToList();

                    List<ApplicationRole> rolesForContract = new List<ApplicationRole>();
                    foreach (var roleid in roleIdsForContract)
                    {
                        var role = currentApplication.ApplicationRoles.FirstOrDefault(x => x.Id.Equals(roleid));
                        if (role != null)
                            rolesForContract.Add(role);
                    }

                    FormattedString rolesString = new FormattedString();

                    if (rolesForContract.Count <= 0)
                        rolesString.Spans.Add(new Span { Text = "No Roles", FontAttributes = FontAttributes.Bold });
                    else if (rolesForContract.Count == 1)
                    {
                        rolesString.Spans.Add(new Span { Text = "Role: ", FontAttributes = FontAttributes.Bold });
                        rolesString.Spans.Add(new Span { Text = rolesForContract[0].Name });
                    }
                    else
                    {
                        rolesString.Spans.Add(new Span { Text = "Role: ", FontAttributes = FontAttributes.Bold });
                        foreach (var role in rolesForContract)
                            rolesString.Spans.Add(new Span { Text = $"\n    {role.Name}" });
                    }

                    return rolesString;
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    return string.Empty;
                }
            }
        }

    }
}
