using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;

using Workbench.Forms.UI.Views;
using Workbench.Forms.Droid.Renderers;
using Android.Content;
using Workbench.Forms.UI.Controls;

[assembly: ExportRenderer(typeof(NestedListView), typeof(NestedListViewRendererDroid))]

namespace Workbench.Forms.Droid.Renderers
{
	public class NestedListViewRendererDroid : ListViewRenderer
    {
		public NestedListViewRendererDroid(Context context) : base(context)
        {
        }
      
		protected override void OnElementChanged(ElementChangedEventArgs<Xamarin.Forms.ListView> e)
        {
            base.OnElementChanged(e);

            if (e.NewElement != null)
            {
                var listView = this.Control as Android.Widget.ListView;
                listView.NestedScrollingEnabled = true;
            }
        }
    }
}