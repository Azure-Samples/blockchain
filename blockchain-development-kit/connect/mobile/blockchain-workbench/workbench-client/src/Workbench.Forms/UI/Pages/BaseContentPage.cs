using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using Workbench.Forms.ViewModels;
using Lottie.Forms;
using Workbench.Forms.Interfaces;
using Workbench.Forms.Helpers;
using System.Collections.ObjectModel;

namespace Workbench.Forms.UI.Pages
{
	public abstract class BaseContentPage<T> : BasePage where T : BaseViewModel, new()
	{
		T viewModel;
		public ObservableCollection<ToolbarItem> LeftToolbarItems { get; set; }

		protected BaseContentPage()
		{
			this.ViewModel = Activator.CreateInstance<T>();
			LeftToolbarItems = new ObservableCollection<ToolbarItem>();
		}

		public T ViewModel
		{
			get { return this.viewModel; }
			set
			{
				viewModel = value;
				BindingContext = viewModel;
				this.SetBinding(TitleProperty, nameof(ViewModel.Title));

				Task.Run(async () =>
				{
					await this.Init();
				});
			}
		}

		private async Task Init()
		{
			await this.ViewModel.InitAsync();
		}

		public override async Task RunAnimation()
		{
			ViewModel.IsAnimationVisible = true;

			Device.BeginInvokeOnMainThread(() => animationView.PlayProgressSegment(0f, 1f));

			await Task.Delay(1500).ConfigureAwait(false);

			ViewModel.IsAnimationVisible = false;
		}

		protected override void OnSizeAllocated(double width, double height)
		{
			base.OnSizeAllocated(width, height);

			if (width < 0 || animationView is null) return;
			if (width == animationView.WidthRequest) return;

			animationView.WidthRequest = width;
			animationView.HeightRequest = width;

			RelativeLayout.SetXConstraint(animationView, Constraint.RelativeToParent(p => p.Bounds.Center.X - width / 2));
			RelativeLayout.SetYConstraint(animationView, Constraint.RelativeToParent(p => p.Bounds.Center.X - width / 2));
		}
	}
}