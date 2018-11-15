using Android.Graphics;
using Android.Widget;

using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;

using Workbench.Forms.Droid.Effects;

[assembly: ExportEffect(typeof(FontAwesomeEffect), "FontAwesomeEffect")]
namespace Workbench.Forms.Droid.Effects
{
	public class FontAwesomeEffect : PlatformEffect
	{
		protected override void OnAttached()
		{
			var label = (TextView)Control;

			var font = Typeface.CreateFromAsset(Xamarin.Forms.Forms.Context.ApplicationContext.Assets, "iconize-fontawesome.ttf");
			label.Typeface = font;
		}

		protected override void OnDetached()
		{
		}
	}
}