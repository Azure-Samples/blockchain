using System;
using System.Linq;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Workbench.Forms.Interfaces;
using Workbench.Forms.Models;
using Microsoft.IdentityModel.Clients.ActiveDirectory;
using Workbench.Forms.Helpers;

namespace Workbench.Forms.Droid.ServiceImplementations
{
	public class Authentication_Droid : IAuthentication
	{
		private const string SharedPreferencesName = "ActiveDirectoryAuthenticationLibrary";
		private const string SharedPreferencesKey = "cache";

		private static ISharedPreferences SharedPreferences => Application.Context.GetSharedPreferences(SharedPreferencesName, FileCreationMode.Private);

        public void clearTokenFromCache(string authority)
		{
			try
			{
				Android.Webkit.CookieManager.Instance.RemoveAllCookies(null);

				var authContext = new AuthenticationContext($"https://login.microsoftonline.com/{authority}");
				authContext.TokenCache.Clear();

				var editor = SharedPreferences.Edit();
				editor.Remove(SharedPreferencesKey);
			}
			catch (Exception ex)
			{
				// log the error
				AppCenterHelper.Report(ex);
				System.Diagnostics.Debug.WriteLine(ex.Message);
			}

		}

		public async Task<LoginResponse> LoginAsync(string authority, string resource, string clientId, string returnUri, bool isRefresh = false)
		{
			var authContext = new AuthenticationContext(authority);
			if (authContext.TokenCache.ReadItems().Any())
				authContext = new AuthenticationContext(authContext.TokenCache.ReadItems().First().Authority);
            
			var uri = new Uri(returnUri);
			var platformParams = new PlatformParameters((Android.App.Activity)Xamarin.Forms.Forms.Context);
         
			try
			{
                AuthenticationResult authResult;

                if (isRefresh)
                {
                    authResult = await authContext.AcquireTokenSilentAsync(resource, clientId);
                }
                else
                {
                    authResult = await authContext.AcquireTokenAsync(resource, clientId, uri, platformParams).ConfigureAwait(false);
                }

				var result = new LoginResponse
				{
					AuthHeader = authResult.CreateAuthorizationHeader(),
					AccessToken = authResult.AccessToken,
					AccessTokenType = authResult.AccessTokenType,
					ExpiresOn = authResult.ExpiresOn,
					ExtendedLifetimeToken = authResult.ExtendedLifeTimeToken,
					IdToken = authResult.IdToken,
					TenantId = authResult.TenantId
				};

				result.Profile.DisplayableId = authResult.UserInfo.DisplayableId;
				result.Profile.FamilyName = authResult.UserInfo.FamilyName;
				result.Profile.GivenName = authResult.UserInfo.GivenName;
				result.Profile.UniqueId = authResult.UserInfo.UniqueId;

				return result;
			}
			catch (Exception e)
			{
				AppCenterHelper.Report(e);
				clearTokenFromCache(authority);
				return null;
			}
		}
	}
}
