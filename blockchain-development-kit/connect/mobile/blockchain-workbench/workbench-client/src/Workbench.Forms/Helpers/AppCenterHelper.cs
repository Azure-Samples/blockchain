using System;

using Microsoft.AppCenter;
using Microsoft.AppCenter.Crashes;
using Microsoft.AppCenter.Analytics;

using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AppCenter.Distribute;

namespace Workbench.Forms.Helpers
{
	public static class AppCenterHelper
    {
          
		public static void Report(Exception ex, IDictionary<string, string> table = null) =>
           Crashes.TrackError(ex, table);

        public static void TrackEvent(string trackIdentifier, IDictionary<string, string> table = null) =>
            Analytics.TrackEvent(trackIdentifier, table);
              
        public static void TrackEvent(string trackIdentifier, string key, string value)
        {
            IDictionary<string, string> table = new Dictionary<string, string> { { key, value } };

            if (string.IsNullOrWhiteSpace(key) && string.IsNullOrWhiteSpace(value))
                table = null;

            TrackEvent(trackIdentifier, table);
        }

             
        public static void Start()
        {
            AppCenter.LogLevel = LogLevel.Verbose;
            Distribute.ReleaseAvailable = OnReleaseAvailable;
            Distribute.SetEnabledAsync(true);
            Analytics.SetEnabledAsync(true);
#if DEBUG
			Distribute.SetEnabledAsync(false);
#endif
			AppCenter.Start(
                $"ios={Constants.APPCENTER_IOS_KEY};" +
                "uwp={Your UWP App secret here};" +
                $"android={Constants.APPCENTER_DROID_KEY}",
                typeof(Crashes),
                typeof(Distribute),
				typeof(Analytics));  
        }
      
        static bool OnReleaseAvailable(ReleaseDetails releaseDetails)
        {
            // Look at releaseDetails public properties to get version information, release notes text or release notes URL
            string versionName = releaseDetails.ShortVersion;
            string versionCodeOrBuildNumber = releaseDetails.Version;
            string releaseNotes = releaseDetails.ReleaseNotes;
            Uri releaseNotesUrl = releaseDetails.ReleaseNotesUrl;

            // custom dialog
            var title = "Version " + versionName + " available!";
            Task answer;

			if (releaseDetails.MandatoryUpdate)
            {
                answer = App.Current.MainPage.DisplayAlert(title, releaseNotes, "Download and Install");
            }
            else
            {
				answer = App.Current.MainPage.DisplayAlert(title, releaseNotes, "Download and Install", "Maybe tomorrow...");
            }

            answer.ContinueWith((task) =>
            {
                // If mandatory or if answer was positive
                if (releaseDetails.MandatoryUpdate || (task as Task<bool>).Result)
                {
                    // Notify SDK that user selected update
                    Distribute.NotifyUpdateAction(UpdateAction.Update);
                }
                else
                {
                    // Notify SDK that user selected postpone (for 1 day)
                    // Note that this method call is ignored by the SDK if the update is mandatory
                    Distribute.NotifyUpdateAction(UpdateAction.Postpone);
                }
            });

            // Return true if you are using your own dialog, false otherwise
            return true;
        }
      
    }
}
