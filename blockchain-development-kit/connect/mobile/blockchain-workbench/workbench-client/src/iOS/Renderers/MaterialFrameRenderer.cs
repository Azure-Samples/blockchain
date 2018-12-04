
using CoreGraphics;
using UIKit;
using Workbench.Forms.iOS;
using Workbench.Forms.UI.Controls;
using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

[assembly: ExportRenderer(typeof(MaterialFrame), typeof(MaterialFrameRenderer))]
namespace Workbench.Forms.iOS
{
    /// <summary>
    /// Renderer to update all frames with better shadows matching material design standards
    /// </summary>

    public class MaterialFrameRenderer : FrameRenderer
    {
        //public override void Draw(CGRect rect)
        //{
        //    base.Draw(rect);

        //    // Update shadow to match better material design standards of elevation
        //    Layer.ShadowRadius = 2.0f;
        //    Layer.ShadowColor = UIColor.Gray.CGColor;
        //    Layer.ShadowOffset = new CGSize(2, 2);
        //    Layer.ShadowOpacity = 0.80f;
        //    Layer.ShadowPath = UIBezierPath.FromRect(Layer.Bounds).CGPath;
        //    Layer.MasksToBounds = false;
        //}

		protected override void OnElementChanged(ElementChangedEventArgs<Frame> e)
		{
            base.OnElementChanged(e);
            // Update shadow to match better material design standards of elevation
            Layer.ShadowRadius = 3.0f;
            Layer.ShadowColor = UIColor.LightGray.CGColor;
            //Layer.ShadowOffset = new CGSize(2, 2);
            Layer.ShadowOpacity = 0.90f;
            Layer.MasksToBounds = false;
        }
	}
}