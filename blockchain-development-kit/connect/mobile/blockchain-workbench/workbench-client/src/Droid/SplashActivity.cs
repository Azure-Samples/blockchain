
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Android;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Views.Animations;
using Android.Widget;
using Workbench.Forms.Helpers;
using Android.Support.V4.Content.Res;
using Android.Support.V4.Content;
using Xamarin.Forms.Platform.Android;

namespace Workbench.Forms.Droid
{
	[Activity(Theme = "@style/SplashTheme", Label = "Workbench", MainLauncher = true, NoHistory = true)]
    public class SplashActivity : Activity
    {
        ImageView backgroundImage;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.SplashPage);
            backgroundImage = FindViewById<ImageView>(Resource.Id.splashImage);

            var relativeLayout = FindViewById<RelativeLayout>(Resource.Id.splashBackground);
            relativeLayout.SetBackgroundColor(Constants.NavBarBackgroundColor.ToAndroid());

            //var colorID = Resource.Color.splash_background;

            //var color = new Android.Graphics.Color(ContextCompat.GetColor(this, Resource.Color.splash_background)).ToColor();

            //Settings.NATIVE_BACKGROUND_COLOR = color;

        }

        protected override void OnResume()
        {
            base.OnResume();

            Animation myFadeInAnimation = AnimationUtils.LoadAnimation(this, Resource.Animation.fadein);
            backgroundImage.StartAnimation(myFadeInAnimation);
            backgroundImage.Animation.AnimationEnd += (sender, e) =>
            {
                StartActivity(typeof(Workbench.Forms.Droid.MainActivity));
            };
        }
    }
}