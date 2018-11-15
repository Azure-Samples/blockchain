using System;
using System.Linq;
using System.Collections.Generic;

using Xamarin.Forms;

using Workbench.Client;

using Workbench.Forms.Helpers;
using Workbench.Forms.UI.Pages;
using Workbench.Forms.Interfaces;
using Workbench.Forms.ViewModels;

using System.Threading.Tasks;
using Workbench.Forms.Models;
using DLToolkit.Forms.Controls;
using Xamarin.Forms.PlatformConfiguration;
using Xamarin.Forms.PlatformConfiguration.iOSSpecific;

namespace Workbench.Forms
{
    public class App : Application
    {
        public static double ScreenWidth;
        public static double ScreenHeight;
        public static AppViewModel ViewModel;
        public static MasterDetailPage Master;
        public static bool CONTRACTS_FILTER_CHANGED = false;

        public static bool LOGGED_OUT = true;

        static BackgroundPage backgroundPage;
        public static WorklfowInstanceListPage ContractsPage;

        public App()
        {
            FlowListView.Init();

            GatewayApi.Instance.ExpiredAccessToken += async (object sender, EventArgs e) =>
            {
                var localEnvironments = await LocalDbHelper.Instance.GetAllSavedEnvironmentsAsync();

                BlockchainEnvironment environment = localEnvironments[0];

                var authService = ServiceContainer.Resolve<IAuthentication>();
                var loginResponse = await authService.LoginAsync(
                    $"https://login.windows.net/{environment.TenantId}",
                    environment.ResourceId,
                    environment.ClientId,
                    environment.ReturnUrl,
                    isRefresh: true);

                if (loginResponse != null)
                {
                    AppCenterHelper.TrackEvent("Refreshed Token", new Dictionary<string, string>() { { "username", loginResponse.Profile.DisplayableId } });

                    Settings.AccessToken = loginResponse.AccessToken;
                    GatewayApi.Instance.SetAuthToken(loginResponse.AccessToken);
#if DEBUG
                    Settings.AccessTokenExpiration = new DateTimeOffset(DateTime.Now.AddMinutes(2));
                    GatewayApi.Instance.AccessTokenExpiration = new DateTimeOffset(DateTime.Now.AddMinutes(2));
#else
                    Settings.AccessTokenExpiration = loginResponse.ExpiresOn;
                    GatewayApi.Instance.AccessTokenExpiration = loginResponse.ExpiresOn;
#endif
                }
                else
                {
                    App.Logout(false);
                }
            };

            GatewayApi.Instance.ExceptionThrown += async (object sender, Exception e) =>
            {
                AppCenterHelper.Report(e, new Dictionary<string, string>() { { "baseurl", GatewayApi.SiteUrl } });
            };

            ViewModel = new AppViewModel();

            backgroundPage = new BackgroundPage();

            var navPage = new Xamarin.Forms.NavigationPage(backgroundPage);
            var detailNavPage = new Xamarin.Forms.NavigationPage(new ApplicationsPage());

            ContractsPage = new WorklfowInstanceListPage();

            navPage.BarBackgroundColor = Constants.NavBarBackgroundColor;
            navPage.BarTextColor = Constants.NavBarTextColor;
            detailNavPage.BarBackgroundColor = Constants.NavBarBackgroundColor;
            detailNavPage.BarTextColor = Constants.NavBarTextColor;

            Master = new MasterDetailPage { BindingContext = ViewModel };
            Master.IsPresentedChanged += (object sender, EventArgs e) =>
            {
                var mdp = sender as MasterDetailPage;
                if (mdp.IsPresented)
                    ((Xamarin.Forms.NavigationPage)mdp.Detail)
                      .On<iOS>()
                      .SetStatusBarTextColorMode(StatusBarTextColorMode.DoNotAdjust);
                else
                    ((Xamarin.Forms.NavigationPage)mdp.Detail)
                      .On<iOS>()
                      .SetStatusBarTextColorMode(StatusBarTextColorMode.MatchNavigationBarTextLuminosity);

            };

            Master.Master = new MasterPage { BindingContext = ViewModel };
            Master.Detail = detailNavPage;
            Master.SetBinding(MasterDetailPage.IsPresentedProperty, nameof(AppViewModel.MenuPresented), BindingMode.TwoWay);

            Xamarin.Forms.NavigationPage.SetHasNavigationBar(detailNavPage, false);
           
            try
            {
                MainPage = navPage;
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
            }
        }

        public static async Task Logout(bool shouldDeleteEnvironments = false)
        {
            ServiceContainer.Resolve<IAuthentication>().clearTokenFromCache(App.ViewModel?.SelectedEnvironment?.TenantId);

            App.ViewModel.WorkbenchEnvironments = new List<Models.BlockchainEnvironment>();

            if (shouldDeleteEnvironments)
            {
                await LocalDbHelper.Instance.PurgeEnvironments();
                App.ViewModel.SelectedEnvironment = null;
            }

            LOGGED_OUT = true;

            if (App.Current.MainPage.Navigation.ModalStack.Count > 0)
                await App.Current.MainPage.Navigation.PopModalAsync();
            else if (App.Current.MainPage.Navigation.NavigationStack.Count > 0)
                await App.Current.MainPage.Navigation.PopAsync();

        }

        protected override async void OnStart()
        {
            AppCenterHelper.Start();
            AppCenterHelper.TrackEvent("App Started");

#if DEBUG
            var res = await LocalDbHelper.Instance.GetAllSavedEnvironmentsAsync();
            if (res.Count == 0)
            {
                /* The following can be used when debugging to input a sample environment 

                var environment = new BlockchainEnvironment
                {
                    ResourceId = "YOUR RESOURCE ID",
                    ClientId = "YOUR CLIENT ID",
                    ReturnUrl = "YOUR RETURN URI",
                    SiteUrl = "YOUR API ENDPOINT",
                    TenantId = "YOUR AD TENANT INFORMATION",
                    NickName = "A NICKNAME FOR THE ENV"
                };

                await LocalDbHelper.Instance.SaveEnvironmentAsync(environment);
                */
            }
#endif
        }
    }
}