
using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

using Workbench.Forms.iOS.Effects;
using UIKit;

[assembly: ExportEffect(typeof(FontAwesomeEffect), "FontAwesomeEffect")]
namespace Workbench.Forms.iOS.Effects
{
    public class FontAwesomeEffect : PlatformEffect
    {
        protected override void OnAttached()
        {
            var label = (UIButton)Control;
            
			label.TitleLabel.Font = UIFont.FromName("FontAwesome", 20f);
        }

        protected override void OnDetached()
        {
        }
    }
}