using System;
using System.Linq;
using System.Threading.Tasks;

using Foundation;

using Microsoft.IdentityModel.Clients.ActiveDirectory;

using Workbench.Forms.Models;
using Workbench.Forms.Interfaces;
using Workbench.Forms.iOS.Helpers;
using Workbench.Forms.Helpers;
using UIKit;

namespace Workbench.Forms.iOS.ServiceImplementations
{
    public class Authentication_iOS : IAuthentication
	{
		static AuthenticationContext _authContext;

		public Authentication_iOS()
		{
		}

		public async Task<LoginResponse> LoginAsync(string authority, string resource, string clientId, string returnUri, bool isRefresh = false)
		{
			var authContext = new AuthenticationContext(authority);
			if (authContext.TokenCache.ReadItems().Any())
				authContext = new AuthenticationContext(authContext.TokenCache.ReadItems().First().Authority);

			try
			{
                AuthenticationResult authResult;
                if (isRefresh)
                {
                    authResult = await authContext.AcquireTokenSilentAsync(resource, clientId);
                }
                else
                {

                    var controller = ViewControllerHelpers.GetVisibleViewController();
                    var uri = new Uri(returnUri);
                    var platformParams = new PlatformParameters(controller);

                    authResult = await authContext.AcquireTokenAsync(resource, clientId, uri, platformParams);
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
				var _controller = ViewControllerHelpers.GetVisibleViewController();
				var alert = UIAlertController.Create("Error", e.Message, UIAlertControllerStyle.Alert);
				alert.AddAction(UIAlertAction.Create("Cancel", UIAlertActionStyle.Cancel, null));

                _controller.BeginInvokeOnMainThread(() =>
                {
                    _controller.PresentViewController(alert, true, null);
                });
                                                    
				AppCenterHelper.Report(e);
				clearTokenFromCache(authority);
				return null;
			}
		}

		public void clearTokenFromCache(string authority)
		{
			if (string.IsNullOrWhiteSpace(authority)) return;

			var authContext = new AuthenticationContext($"https://login.microsoftonline.com/{authority}");
			authContext.TokenCache.Clear();

			foreach (var cookie in NSHttpCookieStorage.SharedStorage.Cookies)
			{
				NSHttpCookieStorage.SharedStorage.DeleteCookie(cookie);
			}
		}
	}
}