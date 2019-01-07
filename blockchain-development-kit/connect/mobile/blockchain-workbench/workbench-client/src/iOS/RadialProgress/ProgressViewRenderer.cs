using System;
using System.Drawing;

using Xamarin.Forms.Platform.iOS;

using RadialProgress;

using Workbench.Forms.UI.Views;
using Workbench.Forms.iOS;
using UIKit;

[assembly: Xamarin.Forms.ExportRenderer(typeof(ProgressView), typeof(ProgressViewRenderer))]
namespace Workbench.Forms.iOS
{
    public class ProgressViewRenderer : ViewRenderer
    {
        public ProgressViewRenderer()
        {
        }

        ProgressView formsControl;
        RadialProgressView progressView;

        protected override void OnElementChanged(ElementChangedEventArgs<Xamarin.Forms.View> e)
        {
            base.OnElementChanged(e);

            if (Control == null)
            {
                // Instantiate the native control and assign it to the Control property with
                // the SetNativeControl method

				progressView = new RadialProgressView(getLabelText, RadialProgressViewStyle.Small);

                SetNativeControl(progressView);
            }

            if (e.OldElement != null)
            {
                // Unsubscribe from event handlers and cleanup any resources
                progressView.LabelTextDelegate = null;
                progressView.Dispose();
                progressView = null;
                formsControl = null;
            }

            if (e.NewElement != null)
            {
                // Configure the control and subscribe to event handlers
                formsControl = e.NewElement as ProgressView;

                var percentValue = formsControl.PercentComplete / 100.0f;

                progressView.LabelTextDelegate = (arg) => formsControl.LabelText;
                progressView.Value = percentValue;
                progressView.Center = new PointF((float)formsControl.Bounds.Center.X, (float)formsControl.Bounds.Center.Y - 100);

                if (formsControl.LabelStyle.ToLower().Equals("failure"))
                {
                    progressView.ProgressColor = formsControl.FailureProgressColor.ToUIColor();
                    RadialProgressLayer.BackCircleBackgroundColor = formsControl.FailureBackgroundColor.ToCGColor();
                    progressView.InitSubviews();
                }
                else if (formsControl.LabelStyle.ToLower().Equals("complete"))
                {
                    progressView.ProgressColor = formsControl.SuccessProgressColor.ToUIColor();
                    RadialProgressLayer.BackCircleBackgroundColor = formsControl.SuccessBackgroundColor.ToCGColor();
                    progressView.InitSubviews();
                }
                else
                {
                    progressView.ProgressColor = RadialProgressLayer.DefaultFillColor;
                    RadialProgressLayer.BackCircleBackgroundColor = RadialProgressLayer.DefaultBackgroundColor;
                    progressView.InitSubviews();
                }
            }
        }

        protected override void OnElementPropertyChanged(object sender, System.ComponentModel.PropertyChangedEventArgs e)
        {
            base.OnElementPropertyChanged(sender, e);

            if (e.PropertyName == nameof(ProgressView.PercentComplete))
            {
                var percentValue = formsControl.PercentComplete / 100.0f;
                progressView.Value = percentValue;
            }
            else if (e.PropertyName == nameof(ProgressView.LabelText))
            {
                progressView.LabelTextDelegate = (arg) => formsControl.LabelText;
            }
			else if (e.PropertyName == nameof(ProgressView.LabelStyle))
			{
                if (formsControl.LabelStyle.ToLower().Equals("failure"))
                {
                    progressView.ProgressColor = formsControl.FailureProgressColor.ToUIColor();
                    RadialProgressLayer.BackCircleBackgroundColor = formsControl.FailureBackgroundColor.ToCGColor();
                    progressView.InitSubviews();
                }
                else if (formsControl.LabelStyle.ToLower().Equals("complete"))
                {
                    progressView.ProgressColor = formsControl.SuccessProgressColor.ToUIColor();
                    RadialProgressLayer.BackCircleBackgroundColor = formsControl.SuccessBackgroundColor.ToCGColor();
                    progressView.InitSubviews();
                }
                else
                {
                    progressView.ProgressColor = RadialProgressLayer.DefaultFillColor;
                    RadialProgressLayer.BackCircleBackgroundColor = RadialProgressLayer.DefaultBackgroundColor;
                    progressView.InitSubviews();
                }
			}
        }

        string getLabelText(nfloat percent)
        {
            return formsControl?.LabelText ?? "";
        }
    }
}