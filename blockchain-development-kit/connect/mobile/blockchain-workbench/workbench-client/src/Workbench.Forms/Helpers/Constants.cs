using Xamarin.Forms;

namespace Workbench.Forms.Helpers
{
    public static class Constants
    {
        public static readonly string BackgroundPageImage = "logo.png";

        #region Application Colors 
        /* On both platforms we set the Navigation Bar's color to the same color as the 
         * Splash pages and Homescreen page.
         * On Android we can set the background color of the splash page dynamically, this is not
         * possible on iOS. In Appdelegate.cs we set the NavBarBackgroundColor to the same color
         * as the splash page. 
         */

        public static readonly string NavBarBackgroundColorDefault = "#2E72C0";
        public static Color NavBarBackgroundColor = Color.FromHex(NavBarBackgroundColorDefault);//Settings.NATIVE_BACKGROUND_COLOR;

        public static readonly string NavBarTextColorDefault = "#FFFFFF";
        public static readonly Color NavBarTextColor = Color.FromHex(NavBarTextColorDefault);

        public static readonly string ButtonTextColorDefault = "#FFFFFF";
        public static readonly Color ButtonTextColor = Color.FromHex(ButtonTextColorDefault);

        public static readonly string ButtonBackgroundColorDefault = "#2E72C0";
        public static readonly Color ButtonBackgroundColor = Color.FromHex(ButtonBackgroundColorDefault);

        public static readonly string CompleteAccentColorDefault = "#0BCD4D";
        public static readonly Color CompleteAccentColor = Color.FromHex(CompleteAccentColorDefault);

        public static readonly string NotCompleteAccentColorDefault = "#F7D21E";
        public static readonly Color NotCompleteAccentColor = Color.FromHex(NotCompleteAccentColorDefault);

        #endregion Application Colors

        #region Progressview Colors
        /* Colors used on the progressview displaying the progress of a workflow */

        public static readonly Color DefaultBackgroundColor = Color.FromRgb(193, 149, 194);
        public static readonly Color DefaultProgressColor = Color.FromRgb(116, 49, 117);
        public static readonly Color FailureBackgroundColor = Color.FromRgb(165, 0, 0); 
        public static readonly Color FailureProgressColor = Color.Red; 
        public static readonly Color SuccessBackgroundColor =  Color.FromRgb(1, 117, 1); 
        public static readonly Color SuccessProgressColor = Color.FromRgb(44, 198, 44); 

        #endregion


        #region APPCENTER CONSTANTS 
        /* Used for setting AppCenter application keys for automatic updates
         */
        public static string APPCENTER_DROID_KEY = "androidkey";
        public static string APPCENTER_IOS_KEY = "ioskey";

        #endregion


        #region ColorSwatch - same as the dynamic colors used in the web UI

        public static string[] ColorSwatch = {
            "#750B1C",
            "#A4262C",
            "#D13438",
            "#CA5010",
            "#986F0B",
            "#498205",
            "#005E50",
            "#038387",
            "#0078D7",
            "#004E8C",
            "#4F6BED",
            "#373277",
            "#8764B8",
            "#881798",
            "#C239B3",
            "#E3008C",
            "#603E30",
            "#567C73",
            "#69797E",
            "#747574",
        };

        #endregion
    }
}