using System;
using System.Threading.Tasks;
using Workbench.Forms.ViewModels;
using Lottie.Forms;
using Xamarin.Forms;
namespace Workbench.Forms.UI.Pages
{
    public class BasePage : ContentPage
    {
        RelativeLayout _rootLayout; //Root container for all controls on page, including HUD
        internal AnimationView animationView; //Progress animation that plays when HUD is showing

        View _contentView;
        public View RootContent
        {
            get { return _contentView; }
            set { _contentView = value; if (value != null) SetContent(); }
        }

        void SetContent()
        {
            if (_contentView == null)
                return;

            _rootLayout = new RelativeLayout();

            var animationWidth = App.ScreenWidth / 2;

            animationView = new AnimationView
            {
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
                AutoPlay = true,
                Speed = 0.5f,
                Duration = TimeSpan.FromMilliseconds(1500),
            };

            _rootLayout.Children.Add(_contentView,
                                     xConstraint: Constraint.Constant(0),
                                     yConstraint: Constraint.Constant(0),
                                     widthConstraint: Constraint.RelativeToParent(p => Width),
                                     heightConstraint: Constraint.RelativeToParent(p => Height));
            _rootLayout.Children.Add(animationView,
                                     xConstraint: Constraint.Constant(0),
                                     yConstraint: Constraint.Constant(0));

            animationView.SetBinding(AnimationView.AnimationProperty, nameof(BaseViewModel.Animation), BindingMode.TwoWay);
            animationView.SetBinding(AnimationView.IsVisibleProperty, nameof(BaseViewModel.IsAnimationVisible), BindingMode.TwoWay);

            Content = _rootLayout;
        }

        public virtual Task RunAnimation()
        {
            return Task.Run(() => { });
        }
    }
}
