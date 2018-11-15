using System.ComponentModel;

using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

using Workbench.Forms.iOS.Effects;
using Workbench.Forms.UI.Views;
using UIKit;


[assembly: ResolutionGroupName("Xamarin")]
[assembly: ExportEffect(typeof(SelectedButtonEffect), nameof(SelectedButtonEffect))]
namespace Workbench.Forms.iOS.Effects
{
	public class SelectedButtonEffect : PlatformEffect
	{
		public SelectedButtonEffect()
		{
		}

		SelectedButton formsElement;
		UIKit.UIButton nativeControl;

		protected override void OnAttached()
		{
			formsElement = Element as SelectedButton;
			nativeControl = Control as UIButton;

			nativeControl.Layer.BorderWidth = 3.0f;
			nativeControl.Layer.BorderColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToCGColor();
			         
			nativeControl.BackgroundColor = formsElement.BackgroundColor.ToUIColor();
                  
			var gr = new UILongPressGestureRecognizer();
            gr.AddTarget(() => this.ButtonLongPressed(gr));
            nativeControl.AddGestureRecognizer(gr);
		}

		protected override void OnDetached()
		{
		}

		public void ButtonLongPressed(UILongPressGestureRecognizer longPressGestureRecognizer)
		{
			formsElement.LongPressCommand.Execute(new object());
		}

		protected override void OnElementPropertyChanged(PropertyChangedEventArgs args)
		{
			base.OnElementPropertyChanged(args);

			switch (args.PropertyName)
			{
				case nameof(SelectedButton.IsSelected):
					if (formsElement.Width == -1) return;

					if (formsElement.IsSelected)
					{
						nativeControl.Layer.BorderWidth = 3.0f;
						nativeControl.Layer.BorderColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToCGColor();
						nativeControl.BackgroundColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToUIColor();
					}
					else
					{
						nativeControl.Layer.BorderWidth = 0.0f;
                        nativeControl.Layer.BorderColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToCGColor();
						nativeControl.BackgroundColor = formsElement.BackgroundColor.ToUIColor();
                           
					}
					break;
				case nameof(SelectedButton.Height):
					if (formsElement.Height == -1) return;

					if (formsElement.IsSelected)
                    {
                        nativeControl.Layer.BorderWidth = 3.0f;
                        nativeControl.Layer.BorderColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToCGColor();
						nativeControl.BackgroundColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToUIColor();
                    }
                    else
                    {
                        nativeControl.Layer.BorderWidth = 0.0f;
                        nativeControl.Layer.BorderColor = Xamarin.Forms.Color.FromHex("#2e72c0").ToCGColor();
                        nativeControl.BackgroundColor = formsElement.BackgroundColor.ToUIColor();
                       
                    }
					break;
			}
		}
	}
}