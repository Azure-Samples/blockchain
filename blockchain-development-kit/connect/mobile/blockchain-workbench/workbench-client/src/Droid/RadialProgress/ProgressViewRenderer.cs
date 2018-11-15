using RadialProgress;

using Xamarin.Forms.Platform.Android;

using Workbench.Forms.UI.Views;
using Workbench.Forms.Droid;
using Android.Content;

[assembly: Xamarin.Forms.ExportRenderer(typeof(ProgressView), typeof(ProgressViewRenderer))]
namespace Workbench.Forms.Droid
{
    public class ProgressViewRenderer : ViewRenderer
    {
        public ProgressViewRenderer(Context context) : base(context)
        {
        }

        ProgressView formsControl;
        RadialProgressView progressView;

        protected override void OnElementChanged(ElementChangedEventArgs<Xamarin.Forms.View> e)
        {
            base.OnElementChanged(e);

            if (Control == null)
            {
                progressView = new RadialProgressView(Context, labelTextFunc: getLabelText, progressType: RadialProgressViewStyle.Small);
                progressView.SetPadding(20, 20, 20, 20);

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

				if (formsControl.LabelStyle.ToLower().Equals("failure"))
				{
                    progressView.ProgressColor = formsControl.FailureProgressColor.ToAndroid();
                    RadialProgressView.CircleBackgroundColor = formsControl.FailureBackgroundColor.ToAndroid();
                    progressView.InitPaints();
				}   
				else if (formsControl.LabelStyle.ToLower().Equals("complete"))
                {
                    progressView.ProgressColor = formsControl.SuccessProgressColor.ToAndroid();
                    RadialProgressView.CircleBackgroundColor = formsControl.SuccessBackgroundColor.ToAndroid();
                    progressView.InitPaints();
                } 
				else
				{
					progressView.ProgressColor = RadialProgressView.DefaultColor;
                    RadialProgressView.CircleBackgroundColor = RadialProgressView.DefaultCircleBackgroundColor;
                    progressView.InitPaints();
				}
					            
                progressView.Value = percentValue;
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
				progressView.LabelTextDelegate = (arg) => formsControl.LabelText;
			else if (e.PropertyName == nameof(ProgressView.LabelStyle))
			{
                if (formsControl.LabelStyle.ToLower().Equals("failure"))
                {
                    progressView.ProgressColor = formsControl.FailureProgressColor.ToAndroid();
                    RadialProgressView.CircleBackgroundColor = formsControl.FailureBackgroundColor.ToAndroid();
                    progressView.InitPaints();
                }
                else if (formsControl.LabelStyle.ToLower().Equals("complete"))
                {
                    progressView.ProgressColor = formsControl.SuccessProgressColor.ToAndroid();
                    RadialProgressView.CircleBackgroundColor = formsControl.SuccessBackgroundColor.ToAndroid();
                    progressView.InitPaints();
                }
                else
                {
                    progressView.ProgressColor = RadialProgressView.DefaultColor;
                    RadialProgressView.CircleBackgroundColor = RadialProgressView.DefaultCircleBackgroundColor;
                    progressView.InitPaints();
                }
			}
        }

        protected override void OnDraw(Android.Graphics.Canvas canvas)
        {
            base.OnDraw(canvas);

            var bounds = formsControl.Bounds;
            progressView.Layout(System.Convert.ToInt32(formsControl.X), System.Convert.ToInt32(bounds.Y), System.Convert.ToInt32(bounds.Width), System.Convert.ToInt32(bounds.Height));
        }

        string getLabelText(float percent)
        {
            return "";//formsControl?.LabelText ?? "-";
        }
    }
}