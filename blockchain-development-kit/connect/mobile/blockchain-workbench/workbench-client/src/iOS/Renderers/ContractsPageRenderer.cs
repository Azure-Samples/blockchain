using System;
using System.Collections.Generic;

using UIKit;

using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

using Workbench.Forms.UI.Pages;
using Workbench.Forms.iOS.Renderers;

[assembly: ExportRenderer(typeof(ContractsPage), typeof(ContractsPageRenderer))]
namespace Workbench.Forms.iOS.Renderers
{
    public class ContractsPageRenderer : PageRenderer
    {
        public override void ViewWillAppear(bool animated)
        {
            base.ViewWillAppear(animated);

            var page = this.Element as ContractsPage;
            page.LeftToolbarItems.CollectionChanged += (object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e) =>
            {
                UpdateView();
            };
            UpdateView();
        }

        private void UpdateView()
        {
            var leftToolbarItems = (this.Element as ContractsPage).LeftToolbarItems;
            if (leftToolbarItems.Count != 0)
            {
                NavigationController.TopViewController.NavigationItem.LeftBarButtonItems = GetBarButtonItems(leftToolbarItems);
            }
            else
            {
                NavigationController.TopViewController.NavigationItem.LeftBarButtonItems = new UIBarButtonItem[] { };
            }
        }

        private UIBarButtonItem[] GetBarButtonItems(IEnumerable<ToolbarItem> items)
        {
            var leftBarButtonItem = new UIBarButtonItem();
            foreach (var item in items)
            {
                if (item.Priority == 1)
                {
                    leftBarButtonItem = new UIBarButtonItem(item.Text,
                                                            UIBarButtonItemStyle.Plain,
                                                            ((object sender, EventArgs e) =>
                                                            {
                                                                item.Command.Execute(null);
                                                            }));
                }
            }

            return new UIBarButtonItem[] { leftBarButtonItem };
        }
    }
}