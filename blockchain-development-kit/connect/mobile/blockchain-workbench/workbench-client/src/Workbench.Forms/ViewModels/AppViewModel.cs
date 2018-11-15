using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.ObjectModel;

using Xamarin.Forms;

using Workbench.Client;
using Workbench.Client.Models;

using Workbench.Forms.Helpers;
using Workbench.Forms.Models;
using System.Collections.Generic;
using System.Threading;
using Workbench.Forms.Interfaces;
using System.Windows.Input;

namespace Workbench.Forms.ViewModels
{
    public class AppViewModel : BaseViewModel
    {
        public AppViewModel()
        {
            Animation = "loading.json";
        }

        public bool NEW_APPLICATION_SELECTED;
        string nickname;
        bool nicknameVisible;
        bool menuPresented;

        Workflow workflow;
        public ObservableCollection<Workflow> WorkflowListCollection { get; set; } = new ObservableCollection<Workflow>();

        Client.Models.Application currentApplication;
       
        BlockchainEnvironment selectedEnvironment;
        List<BlockchainEnvironment> workbenchEnvironments = new List<BlockchainEnvironment>();

        public event EventHandler NewContractSelectedHandler;

        public List<User> AllUsersList { get; set; } = new List<User>();
        public List<RoleAssignment> ApplicationRoleMappingsList { get; set; } = new List<RoleAssignment>();
        public ObservableCollection<Client.Models.Application> ApplicationListCollection { get; set; } = new ObservableCollection<Client.Models.Application>();
       
        public string UserName { get => Settings.LoggedInADUser?.UserDetails.DisplayName; }
        public User CurrentUser { get => Settings.LoggedInADUser?.UserDetails; }

        public bool DebugModeEnabled { 
            get => Settings.DebugModeEnabled;
            set => OnPropertyChanged(nameof(DebugModeEnabled));
        }

        public bool ShowNotificationButton { get => !NotificationCount?.Equals("0") ?? false; }

        //EVENT HANDLERS
        public event EventHandler<int> RetreievedAllWorkflows;
        public event EventHandler<int> CurrentWorkflowSet;

        public List<BlockchainEnvironment> WorkbenchEnvironments
        {
            get
            {
                OnPropertyChanged(nameof(EnvironmentString));
                return workbenchEnvironments;
            }
            set
            {
                SetProperty(ref workbenchEnvironments, value);
                OnPropertyChanged(nameof(EnvironmentString));
            }
        }
        public bool NicknameVisible
        {
            get => nicknameVisible;
            set => SetProperty(ref nicknameVisible, value);
        }
        public bool MenuPresented
        {
            get { return menuPresented; }
            set
            {
                IsBusy = false;
                SetProperty(ref menuPresented, value);
            }
        }
        public string Nickname
        {
            get => nickname;
            set
            {
                SetProperty(ref nickname, value);

                if (string.IsNullOrEmpty(Nickname))
                    NicknameVisible = false;
                else
                    NicknameVisible = true;
            }
        }

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
                IsBusy = true;
                var _app = await GatewayApi.Instance.GetApplicationByIdAsync(appId);
                if (_app != null)
                {
                    NEW_APPLICATION_SELECTED = true;
                    CurrentApplication = _app;

                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
            finally
            {
                IsBusy = false;
            }
        }

        public Workflow Contract
        {
            get { return workflow; }
            set
            {
                SetProperty(ref workflow, value);
                OnPropertyChanged(nameof(RolesString));
                OnPropertyChanged(nameof(UserName));
                OnPropertyChanged(nameof(CurrentUser));

                if (workflow != null)
                    Task.Run(async () =>
                    {
                        GatewayApi.Instance.CONTRACTS_TOP_QUERY_PARAM = GatewayApi.Instance.CONTRACTS_TOP_QUERY_PARAM_DEFAULT;
                        
                        string selectedPropertyIdString = string.Empty;
                        foreach (var property in value.Properties)
                        {
                            selectedPropertyIdString += $"{property.Id}|";
                        }

                        var contractPreferences = new ContractPropertyPreferences
                        {
                            ContractId = Contract.Id.ToString(),
                            DisplayedPropertyIds = selectedPropertyIdString
                        };

                        UI.ViewCells.WorkflowInstanceViewCell.Preferences = contractPreferences;
                        await LocalDbHelper.Instance.SaveContractPropertyPreferencesAsync(contractPreferences);

                        NewContractSelectedHandler?.Invoke(this, EventArgs.Empty);
                        MenuPresented = false;

                    });
            }
        }

        bool isUserInitiator;
        public bool IsUserInitiator
        {
            get
            {
                if (currentApplication is null) return false;
                if (workflow is null) return false;

                return isUserInitiator;
            }
            set
            {
                SetProperty(ref isUserInitiator, value);
            }
        }

        public FormattedString EnvironmentString
        {
            get
            {
                FormattedString envString = new FormattedString();
                envString.Spans.Add(new Span { Text = "API URL:\n", FontAttributes = FontAttributes.Bold  });
                envString.Spans.Add(new Span { Text = SelectedEnvironment?.SiteUrl ?? ""});
                envString.Spans.Add(new Span { Text = "\n\nAD Tenant:\n", FontAttributes = FontAttributes.Bold });
                envString.Spans.Add(new Span { Text = SelectedEnvironment?.TenantId ?? "" });
                envString.Spans.Add(new Span { Text = "\n\nWorkbench Resource ID:\n", FontAttributes = FontAttributes.Bold });
                envString.Spans.Add(new Span { Text = SelectedEnvironment?.ResourceId ?? ""});
                envString.Spans.Add(new Span { Text = "\n\nMobile Client ID:\n", FontAttributes = FontAttributes.Bold });
                envString.Spans.Add(new Span { Text = SelectedEnvironment?.ClientId ?? ""});

                return envString;
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
                        rolesString.Spans.Add(new Span { Text = "No Role Access", FontAttributes = FontAttributes.Bold });
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

        public string NotificationCount { get { return "0" ; } }

        public void SetLoggedInUser(CurrentUser userInfo)
        {
            Settings.LoggedInADUser = userInfo;

            OnPropertyChanged(nameof(RolesString));
            OnPropertyChanged(nameof(UserName));
            OnPropertyChanged(nameof(CurrentUser));
            OnPropertyChanged(nameof(IsUserInitiator));
        }

        CancellationTokenSource cts = new CancellationTokenSource();
        public BlockchainEnvironment SelectedEnvironment
        {
            get => selectedEnvironment;
            set
            {
                cts.Cancel();

                SetProperty(ref selectedEnvironment, value);
                Nickname = selectedEnvironment?.NickName;

                App.ViewModel.SetLoggedInUser(null);
                ApplicationListCollection.Clear();
                Contract = null;

                Device.BeginInvokeOnMainThread(async () =>
                {
                    var environmentConfigured = await AppViewModel.SetEnvironment(selectedEnvironment);
                    if (environmentConfigured)
                        await EnsureSelectedEnvironmentLoads();
                });

                OnPropertyChanged(nameof(EnvironmentString));
            }
        }

        public static async Task<bool> SetEnvironment(BlockchainEnvironment selectedEnvironment)
        {
            try
            {
                var authService = ServiceContainer.Resolve<IAuthentication>();
                var loginResponse = await authService.LoginAsync(
                    $"https://login.windows.net/{selectedEnvironment.TenantId}",
                    selectedEnvironment.ResourceId,
                    selectedEnvironment.ClientId,
                    selectedEnvironment.ReturnUrl);

                if (loginResponse != null)
                {
                    GatewayApi.SiteUrl = selectedEnvironment.SiteUrl;
                    GatewayApi.Instance.SetAuthToken(loginResponse.AccessToken);

                    return true;
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }

            return false;

        }
        #region Refreshing Applications
        public ICommand FlowListViewRefreshCommand => new Command(async () => await EnsureSelectedEnvironmentLoads());
        public async Task EnsureSelectedEnvironmentLoads()
        {
            cts?.Cancel();
            cts = new CancellationTokenSource();
            var ct = cts.Token;

            IsBusy = true;
            IsAnimationVisible = true;

            try
            {
                await RefreshUserInfo(ct);
                await GetAllUsers(ct);
                await RefreshAllApplications(ct);
            }
            catch (OperationCanceledException ce)
            {
                AppCenterHelper.Report(ce);
                System.Diagnostics.Debug.WriteLine("Refresh was cancelled");
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
            finally
            {
                IsBusy = false;
                IsAnimationVisible = false;
            }
        }

        async Task RefreshUserInfo(CancellationToken ct)
        {
            CurrentUser userInfo = null;
            while (userInfo is null)
            {
                try
                {
                    userInfo = await GatewayApi.Instance.GetCurrentUserDetails();
                    if (ct.IsCancellationRequested)
                    {
                        ct.ThrowIfCancellationRequested();
                    }

                }
                catch (Exception e)
                {
                    AppCenterHelper.Report(e);
                    System.Diagnostics.Debug.WriteLine($"EnsureSelectedEnvironmentLoads-refreshUserInfo Exception: {e.Message}");
                }
            }

            App.ViewModel.SetLoggedInUser(userInfo);
        }
        async Task GetAllUsers(CancellationToken ct)
        {
            while (AllUsersList.Count == 0)
            {
                try
                {
                    await getAllUsers();
                    if (ct.IsCancellationRequested)
                    {
                        ct.ThrowIfCancellationRequested();
                    }
                }
                catch (Exception e)
                {
                    AppCenterHelper.Report(e);
                    System.Diagnostics.Debug.WriteLine($"EnsureSelectedEnvironmentLoads-GetAllUsers Exception: {e.Message}");
                }
            }
        }

        async Task getAllUsers()
        {
            IsBusy = true;

            try
            {
                var list = await GatewayApi.Instance.GetAllUsersAsync();
                AllUsersList = new List<User>(list);
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
            finally
            {
                IsBusy = false;
            }
        }


        async Task RefreshAllApplications(CancellationToken ct)
        {
            try
            {
                await getAllApplications();

                if (ct.IsCancellationRequested)
                {
                    ct.ThrowIfCancellationRequested();
                }

            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine($"EnsureSelectedEnvironmentLoads-refreshAllUsers Exception: {e.Message}");
            }
        }

        async Task getAllApplications()
        {
            var tempList = await GatewayApi.Instance.GetApplicationsAsync(Enabled: Settings.ShowOnlyEnabledApps);
            tempList = tempList.Reverse();

            var currentlyDisplayedItems = ApplicationListCollection.ToList();
            var tempApplicationlist = new List<Client.Models.Application>();
            if (tempList is null) return;


            ApplicationListCollection.Clear();

            if (tempList.Count() == 0)
            {
                return;
            }


            foreach (var currentlyDisplayedItem in currentlyDisplayedItems)
            {
                //check to see if itemlist from server contains an item in our view.  
                //if not -> remove the item from the view
                var itemInBothLists = tempList.FirstOrDefault(ci => ci.Id == currentlyDisplayedItem.Id);
                if (itemInBothLists is null)
                {
                    ApplicationListCollection.Remove(currentlyDisplayedItem);
                }
            }

            foreach (var item in tempList)
            {

                var itemCurrentlyDisplayed = ApplicationListCollection.FirstOrDefault(ci => ci.Id == item.Id);
                if (itemCurrentlyDisplayed is null)
                {
                    ApplicationListCollection.Add(item);
                }
            }
        }

        #endregion
        #region Refreshing Workflows

        public async Task RefreshWorkflowsForSelectedApplication(string applicationID)
        {
            cts?.Cancel();
            cts = new CancellationTokenSource();
            var ct = cts.Token;

            IsBusy = true;
            IsAnimationVisible = true;
            try
            {
                await GetRoleMappingsForApplication(ct, applicationID);
                await RefreshAllWorkflows(ct, applicationID);
            }
            catch (OperationCanceledException ce)
            {
                AppCenterHelper.Report(ce);
                System.Diagnostics.Debug.WriteLine("Refresh was cancelled");
                ApplicationListCollection.Clear();
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
            finally
            {
                IsBusy = false;
                IsAnimationVisible = false;
            }

        }


        async Task RefreshAllWorkflows(CancellationToken ct, string applicationID)
        {
            try
            {
                await GetAllWorkflows(applicationID);

                if (ct.IsCancellationRequested)
                {
                    ct.ThrowIfCancellationRequested();
                }

            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine($"EnsureSelectedEnvironmentLoads-refreshAllUsers Exception: {e.Message}");
            }
        }

        async Task GetAllWorkflows(string applicationId)
        {
            var tempList = await GatewayApi.Instance.GetWorkflowsByApplicationIdAsync(applicationId);
            var currentlyDisplayedItems = WorkflowListCollection.ToList();

            if (tempList is null) return;


            WorkflowListCollection.Clear();
            if (tempList.Count() == 0)
            {
                return;
            }

            foreach (var currentlyDisplayedItem in currentlyDisplayedItems)
            {
                //check to see if itemlist from server contains an item in our view.  
                //if not -> remove the item from the view
                var itemInBothLists = tempList.FirstOrDefault(ci => ci.Id == currentlyDisplayedItem.Id);
                if (itemInBothLists is null)
                {
                    WorkflowListCollection.Remove(currentlyDisplayedItem);
                }
            }

            foreach (var item in tempList)
            {

                var itemCurrentlyDisplayed = WorkflowListCollection.FirstOrDefault(ci => ci.Id == item.Id);
                if (itemCurrentlyDisplayed is null)
                {
                    WorkflowListCollection.Add(item);
                }
            }

            if (WorkflowListCollection.Count == 1)
            {
                var firstWorkflowId = WorkflowListCollection.FirstOrDefault().Id;

                await SetCurrentWorkflow(firstWorkflowId.ToString());
                RetreievedAllWorkflows?.Invoke(this, WorkflowListCollection.Count);
            }
            else
            {
                RetreievedAllWorkflows?.Invoke(this, WorkflowListCollection.Count);
            }
        }

        public async Task SetCurrentWorkflow(string workflowID)
        {
            var currentWorkFlow = await GatewayApi.Instance.GetWorkflowByIdAsync(workflowID);

            Contract = currentWorkFlow;
            IsUserInitiator = await GatewayApi.Instance.CanCurrentUserCreateContractsForWorkflow(Contract.Id.ToString());

            CurrentWorkflowSet?.Invoke(this, 0);
        }

        async Task GetRoleMappingsForApplication(CancellationToken ct, string applicationId)
        {
            try
            {
                await getRoleMappingsForApplicationAsync(applicationId);
                if (ct.IsCancellationRequested)
                {
                    ct.ThrowIfCancellationRequested();
                }
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine($"EnsureSelectedEnvironmentLoads-refreshAllUsers Exception: {e.Message}");
            }

            OnPropertyChanged(nameof(RolesString));
        }

        async Task getRoleMappingsForApplicationAsync(string applicationId)
        {
            IsBusy = true;

            try
            {
                var userList = await GatewayApi.Instance.GetUserRoleAssignmentsAsync(applicationId);
                ApplicationRoleMappingsList = new List<RoleAssignment>(userList);
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
            finally
            {
                IsBusy = false;
            }
        }

        #endregion
    }
}