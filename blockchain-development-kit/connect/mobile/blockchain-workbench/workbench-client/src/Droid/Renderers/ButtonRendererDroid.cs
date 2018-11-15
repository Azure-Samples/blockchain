using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;

using Workbench.Forms.UI.Views;
using Workbench.Forms.Droid.Renderers;
using Android.Content;

[assembly: ExportRenderer(typeof(FabButton), typeof(ButtonRendererDroid))]

namespace Workbench.Forms.Droid.Renderers
{
    public class ButtonRendererDroid : ButtonRenderer
    {
        public ButtonRendererDroid(Context context) : base(context)
        {
        }

        protected override void OnDraw(Android.Graphics.Canvas canvas)
        {
            base.OnDraw(canvas);
        }

        protected override void OnElementChanged(ElementChangedEventArgs<Button> e)
        {
            base.OnElementChanged(e);
        }
    }
}