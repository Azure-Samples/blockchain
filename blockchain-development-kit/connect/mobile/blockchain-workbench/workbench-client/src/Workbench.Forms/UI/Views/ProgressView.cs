using Xamarin.Forms;
using Workbench.Forms.Helpers;

namespace Workbench.Forms.UI.Views
{
    public class ProgressView : View
    {

        public Color DefaultBackgroundColor => Constants.DefaultBackgroundColor;
        public Color DefaultProgressColor => Constants.DefaultProgressColor;
        public Color FailureBackgroundColor => Constants.FailureBackgroundColor;
        public Color FailureProgressColor => Constants.FailureProgressColor;
        public Color SuccessBackgroundColor => Constants.SuccessBackgroundColor;
        public Color SuccessProgressColor => Constants.SuccessProgressColor;

        public static readonly BindableProperty PercentCompleteProperty = BindableProperty.Create(
            propertyName: "PercentComplete",
            returnType: typeof(int),
            declaringType: typeof(int),
            defaultValue: 0);

        public static readonly BindableProperty LabelTextProperty = BindableProperty.Create(
            propertyName: "LabelText",
            returnType: typeof(string),
            declaringType: typeof(string),
            defaultValue: "0");

		public static readonly BindableProperty LabelStyleProperty = BindableProperty.Create(
            propertyName: "LabelStyle",
            returnType: typeof(string),
            declaringType: typeof(string),
            defaultValue: "0");

        public int PercentComplete
        {
            get { return (int)GetValue(PercentCompleteProperty); }
            set { SetValue(PercentCompleteProperty, value); }
        }

        public string LabelText
        {
            get { return (string)GetValue(LabelTextProperty);}
            set { SetValue(LabelTextProperty, value); }
        }

		public string LabelStyle
        {
			get { return (string)GetValue(LabelStyleProperty); }
            set { SetValue(LabelStyleProperty, value); }
        }
    }
}