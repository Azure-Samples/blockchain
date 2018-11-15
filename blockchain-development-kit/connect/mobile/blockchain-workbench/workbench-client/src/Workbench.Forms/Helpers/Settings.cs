using System;

using Newtonsoft.Json;

using Plugin.Settings;
using Plugin.Settings.Abstractions;

using Workbench.Client.Models;

using Workbench.Forms.Models;
using Xamarin.Forms;

namespace Workbench.Forms.Helpers
{
    /// <summary>
    /// This is the Settings static class that can be used in your Core solution or in any
    /// of your client applications. All settings are laid out the same exact way with getters
    /// and setters. 
    /// </summary>
    public static class Settings
    {
        private static ISettings AppSettings
        {
            get
            {
                return CrossSettings.Current;
            }
        }

        #region Setting Constants


        public static Color NATIVE_BACKGROUND_COLOR;

        const string AccessTokenKey = nameof(AccessTokenKey);
        const string AccessTokenExpirationKey = nameof(AccessTokenExpirationKey);
        const string BackgroundRefreshKey = nameof(BackgroundRefreshKey);
        const string UserKey = nameof(UserKey);
        const string NavBarBackgroundColorKey = nameof(NavBarBackgroundColorKey);
        const string NavBarTextColorKey = nameof(NavBarTextColorKey);
        const string ButtonBackgroundColorKey = nameof(ButtonBackgroundColorKey);
        const string ButtonTextColorKey = nameof(ButtonTextColorKey);
        const string CompleteAccentColorKey = nameof(CompleteAccentColorKey);
        const string NotCompleteAccentColorKey = nameof(NotCompleteAccentColorKey);
        const string CurrentThemeKey = nameof(CurrentThemeKey);
        const string DebugModeEnabledKey = nameof(DebugModeEnabledKey);
        const string ShowOnlyEnabledAppsKey = nameof(ShowOnlyEnabledAppsKey);
        #endregion


        #region Properties specific to app theming, to be discontinued

        public static string NavBarBackgroundColor
        {
            get { return AppSettings.GetValueOrDefault(NavBarBackgroundColorKey, Constants.NavBarBackgroundColorDefault); }
            set { AppSettings.AddOrUpdateValue(NavBarBackgroundColorKey, value); }
        }
        public static string NavBarTextColor
        {
            get { return AppSettings.GetValueOrDefault(NavBarTextColorKey, Constants.NavBarTextColorDefault); }
            set { AppSettings.AddOrUpdateValue(NavBarTextColorKey, value); }
        }
        public static string ButtonBackgroundColor
        {
            get { return AppSettings.GetValueOrDefault(ButtonBackgroundColorKey, Constants.ButtonBackgroundColorDefault); }
            set { AppSettings.AddOrUpdateValue(ButtonBackgroundColorKey, value); }
        }
        public static string ButtonTextColor
        {
            get { return AppSettings.GetValueOrDefault(ButtonTextColorKey, Constants.ButtonTextColorDefault); }
            set { AppSettings.AddOrUpdateValue(ButtonTextColorKey, value); }
        }
        public static string CompleteAccentColor
        {
            get { return AppSettings.GetValueOrDefault(CompleteAccentColorKey, Constants.CompleteAccentColorDefault); }
            set { AppSettings.AddOrUpdateValue(CompleteAccentColorKey, value); }
        }
        public static string NotCompleteAccentColor
        {
            get { return AppSettings.GetValueOrDefault(NotCompleteAccentColorKey, Constants.NotCompleteAccentColorDefault); }
            set { AppSettings.AddOrUpdateValue(NotCompleteAccentColorKey, value); }
        }
        #endregion

        #region Active Directory Settings

        public static bool IsLoggedIn => (!string.IsNullOrWhiteSpace(AccessToken)
                                         && (!AccessTokenExpiration.HasValue || (AccessTokenExpiration.HasValue && AccessTokenExpiration.Value >= DateTimeOffset.UtcNow)));

        public static string AccessToken
        {
            get { return AppSettings.GetValueOrDefault(AccessTokenKey, ""); }
            set { AppSettings.AddOrUpdateValue(AccessTokenKey, value); }
        }

        public static DateTimeOffset? AccessTokenExpiration
        {
            get
            {
                var obj = AppSettings.GetValueOrDefault(AccessTokenExpirationKey, "");
                if (obj == "null" || obj == "")
                {
                    return null;
                }
                return DateTimeOffset.Parse(obj);

            }
            set { AppSettings.AddOrUpdateValue(AccessTokenExpirationKey, value.ToString()); }
        }

        public static int BackgroundRefreshDelay
        {
            get { return AppSettings.GetValueOrDefault(BackgroundRefreshKey, 250); }
            set { AppSettings.AddOrUpdateValue(BackgroundRefreshKey, value); }
        }

        public static CurrentUser LoggedInADUser
        {
            get
            {
                string obj = AppSettings.GetValueOrDefault(UserKey, "");
                if (obj == "null" || obj == "")
                {
                    return null;
                }

                return JsonConvert.DeserializeObject<CurrentUser>(obj);
            }
            set
            {
                AppSettings.AddOrUpdateValue(UserKey, JsonConvert.SerializeObject(value));
            }
        }


        #endregion

        public static BlockchainEnvironment CurrentTheme
        {
            get
            {
                string obj = AppSettings.GetValueOrDefault(CurrentThemeKey, "");
                if (obj == "null" || obj == "")
                {
                    return null;
                }

                return JsonConvert.DeserializeObject<BlockchainEnvironment>(obj);
            }
            set
            {
                AppSettings.AddOrUpdateValue(CurrentThemeKey, JsonConvert.SerializeObject(value));
            }
        }

        #region Other settings 

        public static bool DebugModeEnabled
        {
            get { return AppSettings.GetValueOrDefault(DebugModeEnabledKey, false); }
            set { AppSettings.AddOrUpdateValue(DebugModeEnabledKey, value); }
        }

        public static bool ShowOnlyEnabledApps
        {
            get { return AppSettings.GetValueOrDefault(ShowOnlyEnabledAppsKey, true); }
            set { AppSettings.AddOrUpdateValue(ShowOnlyEnabledAppsKey, value); }
        }
        #endregion
    }
}