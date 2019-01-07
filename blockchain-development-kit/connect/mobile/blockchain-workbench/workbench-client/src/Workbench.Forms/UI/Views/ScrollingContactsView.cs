using System.Linq;
using System.Collections.Generic;

using Xamarin.Forms;

using Workbench.Client.Models;

namespace Workbench.Forms.UI.Views
{
    public class ScrollingContactsView : ScrollView
    {
        public static readonly BindableProperty ContactsProperty = BindableProperty.Create(
            propertyName: "Contacts",
			returnType: typeof(IEnumerable<UserModel>),
			declaringType: typeof(IEnumerable<UserModel>),
            defaultValue: null,
            propertyChanged: handleContactsChanged);

		public IEnumerable<UserModel> Contacts
        {
			get { return (IEnumerable<UserModel>)GetValue(ContactsProperty); }
            set { SetValue(ContactsProperty, value); }
        }

        StackLayout contactsStack;

        public ScrollingContactsView()
        {
			Margin = new Thickness(0, 10, 0, 10);

			var grid = new Grid
			{
                BackgroundColor = Color.White,
				RowDefinitions = new RowDefinitionCollection
				{
					new RowDefinition { Height = GridLength.Auto },
				},
				ColumnDefinitions = new ColumnDefinitionCollection
				{
					new ColumnDefinition { Width = GridLength.Auto },
				}

			};

			contactsStack = new StackLayout
			{
				Padding = 5,
				VerticalOptions = LayoutOptions.Center,
                Children = { new ContactView() }
            };
            
			grid.Children.Add(contactsStack, 0, 0);

            BackgroundColor = Color.White;
            Content = contactsStack;
        }

        static void handleContactsChanged(BindableObject bindable, object oldValue, object newValue)
        {
            var scrollingContactsView = bindable as ScrollingContactsView;

            if (newValue == null || oldValue == null)
                scrollingContactsView.clearContactList();

			var oldList = oldValue as IEnumerable<UserModel>;
			var newList = newValue as IEnumerable<UserModel>;

            var oldNumerOfContacts = oldList?.Count() ?? 0;
            var newNumberOfContacts = newList.Count();

            if (oldNumerOfContacts == newNumberOfContacts)
                scrollingContactsView.reloadNewContacts(newList);
            else if (oldNumerOfContacts > newNumberOfContacts)
            {
                //Remove contact views
                scrollingContactsView.removeContactViews(oldNumerOfContacts - newNumberOfContacts);
            }
            else if (oldNumerOfContacts < newNumberOfContacts)
            {
                //Add contactViews
                scrollingContactsView.addContactViews(newNumberOfContacts - oldNumerOfContacts);
            }

            scrollingContactsView.reloadNewContacts(newList);
        }

		void reloadNewContacts(IEnumerable<UserModel> newContacts)
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                for (var x = 0; x < newContacts.Count(); x++)
                {
                    var contactView = contactsStack.Children[x] as ContactView;
                    contactView.Contact = newContacts.ElementAt(x);
                }
            });
        }

        void addContactViews(int numberOfViewsToAdd)
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                for (var x = 0; x < numberOfViewsToAdd; x++)
                    contactsStack.Children.Add(new ContactView());
            });
        }

        void removeContactViews(int numberOfViewsToRemove)
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                for (var x = 0; x < numberOfViewsToRemove; x++)
                    contactsStack.Children.RemoveAt(0);
            });
        }

        void clearContactList()
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                contactsStack.Children.Clear();
            });
        }
    }
}