using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using Workbench.Client;

using Workbench.Forms.Models;
using Workbench.Forms.Interfaces;

using Workbench.Forms.Helpers;
using System.Collections.Generic;
using Acr.UserDialogs;
using System.Net.Http;
using Newtonsoft.Json;

namespace Workbench.Forms.ViewModels
{
    public class BackgroundPageViewModel : BaseViewModel
    {

        bool addingNewEnvironment = false;

        public BackgroundPageViewModel()
        {
            Animation = "loading.json";
        }

        public async Task<bool> LoginAsync()
        {
            if (IsBusy)
                return false;

            IsBusy = true;
            IsAnimationVisible = true;

            try
            {
                var response = await executeLoginAsync();

                IsAnimationVisible = false;

                return response;
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                await App.Current.MainPage.DisplayAlert("Error", $"executeLoginAsync: {e.Message}", "OK");
            }
            finally
            {
                IsBusy = false;
                IsAnimationVisible = false;
            }

            return false;
        }

        async Task<bool> executeLoginAsync()
        {
            var localEnvironments = await LocalDbHelper.Instance.GetAllSavedEnvironmentsAsync();
            if (localEnvironments.Count <= 0)
            {
                System.Diagnostics.Debug.WriteLine("################### NO LOCAL ENVIRONMENTS FOUND");

                await configureFirstEnvironment();

                return false;
            }

            BlockchainEnvironment environment = localEnvironments[0];

            var authService = ServiceContainer.Resolve<IAuthentication>();
            var loginResponse = await authService.LoginAsync(
                $"https://login.windows.net/{environment.TenantId}",
                environment.ResourceId,
                environment.ClientId,
                environment.ReturnUrl);

            if (loginResponse != null)
            {
                AppCenterHelper.TrackEvent("Logged in", new Dictionary<string, string>() { { "username", loginResponse.Profile.DisplayableId } });

                Settings.AccessToken = loginResponse.AccessToken;
#if DEBUG
                Settings.AccessTokenExpiration = new DateTimeOffset(DateTime.UtcNow.AddMinutes(2));
                GatewayApi.Instance.AccessTokenExpiration = new DateTimeOffset(DateTime.UtcNow.AddMinutes(2));
#else

                Settings.AccessTokenExpiration = loginResponse.ExpiresOn;
                GatewayApi.Instance.AccessTokenExpiration = loginResponse.ExpiresOn;
#endif

                GatewayApi.Instance.SetAuthToken(loginResponse.AccessToken);

                App.LOGGED_OUT = false;

				Device.BeginInvokeOnMainThread(async () =>
				{
					if (App.Current.MainPage.Navigation.ModalStack.Count == 0)
						await App.Current.MainPage.Navigation.PushModalAsync(App.Master);
               
					App.ViewModel.WorkbenchEnvironments = localEnvironments;
					App.ViewModel.SelectedEnvironment = environment;
				});

				return true;
			}
			else
			{
				await App.Logout();

				return false;
			}
		}

		async Task configureFirstEnvironment()
		{
            try
            {
                addingNewEnvironment = true;

                var prompt = new PromptConfig
                {
                    IsCancellable = true,
                    Message = $"Please enter the URL that contains your environment configuration JSON",
                    OkText = "Ok",
                    Title = "Add Environment"
                };

                var URLResults = await UserDialogs.Instance.PromptAsync(prompt);
                if (!string.IsNullOrEmpty(URLResults?.Value))
                {

                    using (var httpclient = new HttpClient())
                    {
                        var results = await httpclient.GetStringAsync(URLResults?.Value);

                        if (!string.IsNullOrEmpty(results))
                        {
                            var environment = JsonConvert.DeserializeObject<BlockchainEnvironment>(results);

                            await LocalDbHelper.Instance.SaveEnvironmentAsync(environment);

                            Device.BeginInvokeOnMainThread(async () =>
                            {
                                await LoginAsync();
                            });
                        }
                        else
                        {
                            await UserDialogs.Instance.AlertAsync("Unable to get environment data, please ensure you entered a valid URL", "Error", "OK");
                        }
                    }
                }
            }
            catch (Exception e)
            {
                AppCenterHelper.Report(e);
                await UserDialogs.Instance.AlertAsync("Unable to get environment data, please ensure you entered a valid URL", "Error", "OK");
            }
		}
	}
}