using System;

using Xamarin.Forms;

using Workbench.Client.Models;

using System.Collections.Generic;

namespace Workbench.Forms.UI.Views
{

    public class ContactView : Grid
    {
        TapGestureRecognizer tapGesture;

        internal Label name { get; set; } = new Label() { FontAttributes = FontAttributes.Bold };
        internal Label role { get; set; } = new Label();
		internal Label email { get; set; } = new Label();
        internal ContactCircleView contactCircleView = new ContactCircleView(60,16);

        public static readonly BindableProperty ContactProperty = BindableProperty.Create(
            propertyName: "Contact",
			returnType: typeof(UserModel),
			declaringType: typeof(UserModel),
            defaultValue: null,
            propertyChanged: handleContactChanged);
        
		public UserModel Contact
        {
			get { return (UserModel)GetValue(ContactProperty); }
            set { SetValue(ContactProperty, value); }
        }

		public ContactView(bool hideImage = false)
		{
			HeightRequest = 60;
			HorizontalOptions = LayoutOptions.FillAndExpand;
			RowSpacing = 0;
			RowDefinitions = new RowDefinitionCollection
			{
				new RowDefinition { Height = GridLength.Auto },
				new RowDefinition { Height = GridLength.Auto },
				new RowDefinition { Height = GridLength.Auto }
			};
			ColumnDefinitions = new ColumnDefinitionCollection
			{
				new ColumnDefinition { Width = 60 },
				new ColumnDefinition { Width = GridLength.Star }
			};

			name.FontSize = 12;
			name.LineBreakMode = LineBreakMode.WordWrap;
			name.VerticalTextAlignment = TextAlignment.Center;

			role.FontSize = 12;
			role.LineBreakMode = LineBreakMode.WordWrap;
			role.VerticalTextAlignment = TextAlignment.Center;

			email.FontSize = 12;
            email.LineBreakMode = LineBreakMode.WordWrap;
			email.VerticalTextAlignment = TextAlignment.Center;

			tapGesture = new TapGestureRecognizer();
			tapGesture.Tapped += HandleClick;
			GestureRecognizers.Add(tapGesture);

			switch (Device.RuntimePlatform)
			{
				case "Android":
					name.VerticalOptions = LayoutOptions.Center;
					role.VerticalOptions = LayoutOptions.Center;
					email.VerticalOptions = LayoutOptions.Center;
					break;
			}

			if (hideImage)
			{
				contactCircleView.IsVisible = false;
			}
            
			Children.Add(contactCircleView, 0, 1, 0, 3);
            Children.Add(name, 1, 2, 0, 1);
            Children.Add(role, 1, 2, 1, 2);
            Children.Add(email, 1, 2, 2, 3);
        }

        protected override void OnRemoved(View view)
        {
            base.OnRemoved(view);

            tapGesture.Tapped -= HandleClick;
            GestureRecognizers.RemoveAt(0);
        }

        void HandleClick(object sender, EventArgs e)
        {
            var contactView = sender as ContactView;
            Device.OpenUri(new Uri($"mailto:{contactView.Contact.EmailAddress}"));
        }

		static void handleContactChanged(BindableObject bindable, object oldValue, object newValue)
		{
			var contactView = bindable as ContactView;
			var contact = newValue as UserModel;

			if (contact != null)
			{
                contactView.contactCircleView.SetText(contact.Name);
                contactView.contactCircleView.SetBackgroundColor(contact.UserID);
				contactView.name.Text = contact.Name.ToUpper();
				contactView.role.Text = $"Role: {contact.Role}";
                contactView.email.Text = contact.EmailAddress;
			}
		}
    }

	public class UserModel
	{
		public string Name { get; set; }
		public string EmailAddress { get; set; }
		public string UserID { get; set; }
		public string ImageURL { get; set; }
		public string Role { get; set; }
		public List<UserChainMapping> UserChainMappings = new List<UserChainMapping>();
	}
}
