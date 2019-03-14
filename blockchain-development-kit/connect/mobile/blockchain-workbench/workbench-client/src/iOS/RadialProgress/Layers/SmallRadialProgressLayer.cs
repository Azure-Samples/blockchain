//initial source: https://github.com/xamarin/XamarinComponents/blob/master/XPlat/RadialProgress/source
using System;

#if __UNIFIED__
using UIKit;
using Foundation;
using CoreGraphics;
using CoreAnimation;
#else
using System.Drawing;
using MonoTouch.UIKit;
using MonoTouch.Foundation;
using MonoTouch.CoreAnimation;
using CGRect = global::System.Drawing.RectangleF;
using CGSize = global::System.Drawing.SizeF;
using CGPoint = global::System.Drawing.PointF;
using nfloat = global::System.Single;
#endif

namespace RadialProgress
{
	internal class SmallRadialProgressLayer : RadialProgressLayer
	{
		const float BorderPadding = 2f;
		const float EndBorderRadius = 82f;
		const float StartBorderRadius = 60f;

		public SmallRadialProgressLayer()
			: base(startRadius: StartBorderRadius + BorderPadding,
					endRadius: EndBorderRadius - BorderPadding,
					backgroundWidth: 176,
					progressLayerWidth: 165f)
		{
		}

		public override UIImage GenerateBackgroundImage()
		{
			UIImage resultImage;

			UIGraphics.BeginImageContextWithOptions(BackBounds.Size, false, UIScreen.MainScreen.Scale);

			using (var context = UIGraphics.GetCurrentContext())
			using (var circlePath = UIBezierPath.FromOval(new CGRect(CGPoint.Empty, BackBounds.Size)))
			{
				context.SaveState();

				context.SetFillColor(BackCircleBackgroundColor);
				circlePath.Fill();

				context.RestoreState();

				resultImage = UIGraphics.GetImageFromCurrentImageContext();
			}

			return resultImage;
		}
	}
}