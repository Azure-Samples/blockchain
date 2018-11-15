using UIKit;
using Foundation;

using Xamarin.Forms.Platform.iOS;

using Workbench.Forms;
using Workbench.Forms.Helpers;
using Workbench.Forms.Interfaces;
using Workbench.Forms.iOS.ServiceImplementations;

using Lottie.Forms.iOS.Renderers;

namespace Workbench.Forms.iOS
{
    [Register("AppDelegate")]
    public partial class AppDelegate : global::Xamarin.Forms.Platform.iOS.FormsApplicationDelegate
    {
        public override bool FinishedLaunching(UIApplication app, NSDictionary options)
        {
            Constants.NavBarBackgroundColor = UIColor.FromRGB(0, 120, 215).ToColor();

            global::Xamarin.Forms.Forms.Init();

            ServiceContainer.Register<IAuthentication>(new Authentication_iOS());
            ServiceContainer.Register<IDatabase>(new Database_iOS());

            App.ScreenWidth = (double)UIScreen.MainScreen.Bounds.Width;
            App.ScreenHeight = (double)UIScreen.MainScreen.Bounds.Height;

            setupColors();

            AnimationViewRenderer.Init();

            LoadApplication(new App());

            return base.FinishedLaunching(app, options);
        }

        void setupColors()
        {
            //bar background color
            UINavigationBar.Appearance.BarTintColor = Constants.NavBarBackgroundColor.ToUIColor();

            // NAVIGATION BAR TITLE COLOR 
            UINavigationBar.Appearance.SetTitleTextAttributes(new UITextAttributes
            {
                TextColor = Constants.NavBarTextColor.ToUIColor()
            });

            // SETS THE COLOR OF THE UIBAR BUTTON ITEMS
            //UIBarButtonItem.Appearance.SetTitleTextAttributes(new UITextAttributes { TextColor = UIColor.White }, UIControlState.Normal);
            //OR
            UINavigationBar.Appearance.TintColor = Constants.NavBarTextColor.ToUIColor();
        }
    }
}