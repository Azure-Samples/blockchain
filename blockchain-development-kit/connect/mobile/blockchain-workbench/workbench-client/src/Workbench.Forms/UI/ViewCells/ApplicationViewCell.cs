using Xamarin.Forms;

using Workbench.Client.Models;
using Workbench.Forms.Helpers;
using System.Linq;
using DLToolkit.Forms.Controls;
using Workbench.Forms.UI.Controls;
using System;

namespace Workbench.Forms.UI.ViewCells
{
    public class ApplicationViewCell : FlowViewCell
    {
        Label applicationName;
        BoxView applicationBox;
        Label applicationShortName;
        Label applicationDeployedDate;
        Label applicationEnabledState;

        public ApplicationViewCell()
        {
            VerticalOptions = LayoutOptions.Fill;

            var MainRelativeLayout = new RelativeLayout
            {
                Margin = 5,
                BackgroundColor = Color.Transparent,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
                MinimumWidthRequest = 0
            };

            applicationShortName = new Label
            {
                TextColor = Color.White,
                FontSize = 30,
                FontAttributes = FontAttributes.Bold,
                Text = "AT",
                HorizontalOptions = LayoutOptions.Center,
                VerticalOptions = LayoutOptions.CenterAndExpand,
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center
            };
            Func<RelativeLayout, double> applicationShortNameWidth = (parent) => applicationShortName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> applicationShortNameHeight = (parent) => applicationShortName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            applicationBox = new BoxView
            {
                HorizontalOptions = LayoutOptions.FillAndExpand,
                MinimumWidthRequest = App.ScreenWidth / 2
            };
            Func<RelativeLayout, double> applicationBoxWidth = (parent) => applicationBox.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> applicationBoxHeight = (parent) => applicationBox.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;

            applicationBox.BindingContext = applicationBox;
            applicationBox.SetBinding(HeightRequestProperty, "Width");

            applicationName = new Label
            {
                Text = "Asset Transfer",
                TextColor = Color.Black,
                FontSize = 14,
                AutomationId = "ApplicationViewCell",
                VerticalOptions = LayoutOptions.Center,
                HorizontalOptions = LayoutOptions.Center,
                LineBreakMode = LineBreakMode.WordWrap,
                FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center,
            };
            Func<RelativeLayout, double> applicationNameWidth = (parent) => applicationName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> applicationNameHeight = (parent) => applicationName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            applicationDeployedDate = new Label
            {
                Text = "Deployed 07/11/18",
                TextColor = Color.Black,
                FontSize = 11,
                AutomationId = "ApplicationDeployedDate",
                VerticalOptions = LayoutOptions.Center,
                HorizontalOptions = LayoutOptions.Center,
                LineBreakMode = LineBreakMode.WordWrap,
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center,
                Margin = new Thickness(0, 0, 0, 5)
            };

            Func<RelativeLayout, double> applicationDeployedDateWidth = (parent) => applicationDeployedDate.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> applicationDeployedDateHeight = (parent) => applicationDeployedDate.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            applicationEnabledState = new Label
            {
                Text = "Enabled",
                TextColor = Color.Black,
                FontSize = 11,
                AutomationId = "ApplicationEnabledState",
                VerticalOptions = LayoutOptions.Center,
                HorizontalOptions = LayoutOptions.Center,
                LineBreakMode = LineBreakMode.WordWrap,
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center,
                Margin = new Thickness(0, 0, 0, 5)
            };
            Func<RelativeLayout, double> applicationEnabledStateWidth = (parent) => applicationEnabledState.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> applicationEnabledStateHeight = (parent) => applicationEnabledState.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            //ADD CHILDREN TO RELATIVELAYOUT
            MainRelativeLayout.Children.Add(applicationBox,
                                            Constraint.RelativeToParent((parent) =>
                                            {
                                                return parent.X;
                                            }),
                                            Constraint.RelativeToParent((parent) =>
                                            {
                                                return parent.Y;
                                            }),
                                            Constraint.RelativeToParent((parent) =>
                                            {
                                                return parent.Width - 20;
                                            }),
                                            Constraint.RelativeToParent((parent) =>
                                            {
                                                return parent.Width - 20;
                                            }));

            MainRelativeLayout.Children.Add(applicationShortName,
                                            Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - applicationShortNameWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.Y - applicationShortNameHeight(parent) / 2;
                                            }));

            MainRelativeLayout.Children.Add(applicationName,
                                            Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - applicationNameWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Bottom + 10;
                                            }));

            MainRelativeLayout.Children.Add(applicationDeployedDate,
                                            Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - applicationDeployedDateWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(applicationName, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Bottom + 5;
                                            }));

            MainRelativeLayout.Children.Add(applicationEnabledState,
                                           Constraint.RelativeToView(applicationBox, (parent, sibling) =>
                                           {
                                               return sibling.Bounds.Center.X - applicationEnabledStateWidth(parent) / 2;
                                           }),
                                           Constraint.RelativeToView(applicationDeployedDate, (parent, sibling) =>
                                           {
                                               return sibling.Bounds.Bottom + 7;
                                           }));

            var frame = new MaterialFrame
            {
                Content = MainRelativeLayout,
                Padding = 5,
                BackgroundColor = Color.White,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
            };


            Content = new ContentView
            {
                Content = frame,
                Padding = 10,
                BackgroundColor = Color.Transparent
            };
        }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();

            var item = BindingContext as Client.Models.Application;

            applicationName.Text = item?.DisplayName?.ToString() ?? "Description not available";

            applicationBox.BackgroundColor = Color.FromHex(Constants.ColorSwatch[ (item?.Id ?? 0) % Constants.ColorSwatch.Length]);

            applicationDeployedDate.Text = $"Deployed {item?.CreatedDtTm.ToString("d")}";

            applicationEnabledState.Text = item.Enabled ? "Enabled" : "Disabled";

            var array = item?.DisplayName.Split(' ');

            if (array.Length > 2)
                applicationShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1) + array[1]?.ToUpper().Substring(0, 1) ?? "" + array[2]?.ToUpper().Substring(0, 1) ?? "";
            else if (array.Length == 2)
                applicationShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1) + array[1]?.ToUpper().Substring(0, 1) ?? "";
            else
                applicationShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1);


        }
    }
}