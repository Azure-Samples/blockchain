using Xamarin.Forms;

namespace Workbench.Forms.UI.Views
{
    public class BarcodeButton : Button
    {
        public BarcodeButton()
        {
            Text = "Scan QR Code";
            HorizontalOptions = LayoutOptions.FillAndExpand;
        }

        public byte[] BarcodeData { get; set; }
    }
}