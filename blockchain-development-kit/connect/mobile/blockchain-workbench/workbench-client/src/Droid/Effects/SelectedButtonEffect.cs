using System.ComponentModel;

using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;

using Workbench.Forms.Droid.Effects;
using Workbench.Forms.UI.Views;
using Android.Graphics;
using Android.Graphics.Drawables;


[assembly: ResolutionGroupName("Xamarin")]
[assembly: ExportEffect(typeof(SelectedButtonEffect), nameof(SelectedButtonEffect))]
namespace Workbench.Forms.Droid.Effects
{
	public class SelectedButtonEffect : PlatformEffect
	{
		public SelectedButtonEffect()
		{
		}

		SelectedButton formsElement;
		Android.Widget.Button nativeControl;

		protected override void OnAttached()
		{
			formsElement = Element as SelectedButton;
			nativeControl = Control as Android.Widget.Button;

			nativeControl.SetBackgroundResource(Resource.Layout.button_border);
			GradientDrawable drawable = (GradientDrawable)nativeControl.Background;
			drawable.SetColor(formsElement.BackgroundColor.ToAndroid());

			nativeControl.LongClick += (s, args) =>
			{
				formsElement.LongPressCommand.Execute(new object());
			};
		}

		protected override void OnDetached()
		{
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
						nativeControl?.SetBackgroundResource(Resource.Layout.button_border);
						GradientDrawable drawable = (GradientDrawable)nativeControl?.Background;
						drawable.SetColor(Xamarin.Forms.Color.FromHex("#2e72c0").ToAndroid());
					}
					else
					{
						nativeControl.SetBackgroundResource(Resource.Layout.unselected_button);
						GradientDrawable drawable = (GradientDrawable)nativeControl.Background;
						drawable.SetColor(formsElement.BackgroundColor.ToAndroid());
					}
					break;
				case nameof(SelectedButton.Height):
					if (formsElement.Height == -1) return;

					if (formsElement.IsSelected)
					{
						nativeControl?.SetBackgroundResource(Resource.Layout.button_border);
						GradientDrawable drawable = (GradientDrawable)nativeControl?.Background;
						drawable.SetColor(Xamarin.Forms.Color.FromHex("#2e72c0").ToAndroid());
					}
					else
					{
						nativeControl.SetBackgroundResource(Resource.Layout.unselected_button);
						GradientDrawable drawable = (GradientDrawable)nativeControl.Background;
						drawable.SetColor(formsElement.BackgroundColor.ToAndroid());
					}
					break;
			}
		}
	}
}