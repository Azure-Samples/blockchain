using System;
using Workbench.Forms.Helpers;
using Xamarin.Forms;
using Workbench.Client.Models;
namespace Workbench.Forms.UI.Views
{
    public class ContactCircleView : Button
    {

        public static readonly BindableProperty ContactDetailProperty = BindableProperty.Create(
            propertyName: "ContactDetail",
            returnType: typeof(User),
            declaringType: typeof(User),
            defaultValue: null,
            propertyChanged: handleContactChanged);
        
        public User ContactDetail
        {
            get { return (User)GetValue(ContactDetailProperty); }
            set { SetValue(ContactDetailProperty, value); }
        }

        public ContactCircleView(int width, int fontSize)
        {
            HorizontalOptions = LayoutOptions.Center;
            VerticalOptions = LayoutOptions.Center;
            HeightRequest = width;
            WidthRequest = width;
            CornerRadius = width/2;
            BorderWidth = 0;
            BorderColor = Color.Transparent;
            TextColor = Color.White;
            FontAttributes = FontAttributes.Bold;
            FontSize = fontSize;
        }

        public void SetText(string name)
        {
            var firstAndLast = name.Trim().Split(' ');
            if (firstAndLast.Length > 1)
            {
                Text = $"{firstAndLast[0][0].ToString().ToUpper()}{firstAndLast[1][0].ToString().ToUpper()}";
            }
            else
            {
                Text = firstAndLast[0][0].ToString().ToUpper();
            }
        }

        public void SetBackgroundColor(string userID)
        {
            int id;
            bool userId = Int32.TryParse(userID, out id);
            BackgroundColor = Color.FromHex(Constants.ColorSwatch[(userId ? id : 0) % Constants.ColorSwatch.Length]);   
        }
        public void SetBackgroundColor(long userID)
        {
            BackgroundColor = Color.FromHex(Constants.ColorSwatch[userID % Constants.ColorSwatch.Length]);
        }

        static void handleContactChanged(BindableObject bindable, object oldValue, object newValue)
        {
            var contactView = bindable as ContactCircleView;
            var contact = newValue as User;

            if (contact != null)
            {
                contactView.SetBackgroundColor(contact.UserId);
                contactView.SetText(contact.DisplayName);
            }
        }
    }
}
