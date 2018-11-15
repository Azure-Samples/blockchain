using Android.OS;
using Android.App;
using Android.Views;
using Android.Content.PM;

using Plugin.Permissions;

using Acr.UserDialogs;

using Xamarin.Forms.Platform.Android;

using Workbench.Forms.Helpers;
using Workbench.Forms.Interfaces;

using Workbench.Forms.Droid.ServiceImplementations;
using Android.Content;
using Microsoft.IdentityModel.Clients.ActiveDirectory;

namespace Workbench.Forms.Droid
{
	[Activity(Label = "Workbench", Icon = "@drawable/icon", Theme = "@style/MainTheme", MainLauncher = false, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation, ScreenOrientation = ScreenOrientation.Portrait)]
	public class MainActivity : global::Xamarin.Forms.Platform.Android.FormsAppCompatActivity
	{
		internal static MainActivity Instance { get; private set; }

		protected override void OnCreate(Bundle bundle)
		{
			TabLayoutResource = Resource.Layout.Tabbar;
			ToolbarResource = Resource.Layout.Toolbar;

			base.OnCreate(bundle);

			global::Xamarin.Forms.Forms.Init(this, bundle);

			Lottie.Forms.Droid.AnimationViewRenderer.Init();

			UserDialogs.Init(() => this);

			ServiceContainer.Register<IAuthentication>(new Authentication_Droid());
			ServiceContainer.Register<IDatabase>(new Database_Droid());

			var pixels = Resources.DisplayMetrics.WidthPixels;
			var scale = Resources.DisplayMetrics.Density;

			double dps = (double)((pixels - 0.5f) / scale);

			App.ScreenWidth = dps;

			pixels = Resources.DisplayMetrics.HeightPixels;
			dps = (double)((pixels - 0.5f) / scale);

			App.ScreenHeight = dps;

			// Change NavBar background color
			Window.ClearFlags(WindowManagerFlags.TranslucentStatus);
			Window.AddFlags(WindowManagerFlags.DrawsSystemBarBackgrounds);
			Window.SetStatusBarColor(Workbench.Forms.Helpers.Constants.NavBarBackgroundColor.ToAndroid());

			Instance = this;

			LoadApplication(new App());
		}

		public override void OnRequestPermissionsResult(int requestCode, string[] permissions, Permission[] grantResults)
		{
			PermissionsImplementation.Current.OnRequestPermissionsResult(requestCode, permissions, grantResults);
		}

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);
            AuthenticationAgentContinuationHelper.SetAuthenticationAgentContinuationEventArgs(requestCode, resultCode, data);
        }

	}
}