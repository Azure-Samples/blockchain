using System;
using Xamarin.Forms;
namespace Workbench.Forms.UI.Controls
{
	public class MaterialFrame : Frame
    {
        public MaterialFrame()
        {
			if (Device.RuntimePlatform.Equals(Device.iOS))
				CornerRadius = 1;
        }
    }
}
