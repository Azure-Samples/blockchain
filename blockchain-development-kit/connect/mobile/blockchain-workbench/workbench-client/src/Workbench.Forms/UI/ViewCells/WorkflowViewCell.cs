using Xamarin.Forms;

using Workbench.Client.Models;
using Workbench.Forms.Helpers;
using System.Linq;
using DLToolkit.Forms.Controls;
using Workbench.Forms.UI.Controls;
using System;

namespace Workbench.Forms.UI.ViewCells
{
    public class WorkflowViewCell : FlowViewCell
    {
        Label workflowName;
        BoxView workflowBox;
        Label workflowShortName;

        public WorkflowViewCell()
        {

            var MainRelativeLayout = new RelativeLayout
            {
                Margin = 5,
                BackgroundColor = Color.Transparent,
                HorizontalOptions = LayoutOptions.FillAndExpand,
                VerticalOptions = LayoutOptions.FillAndExpand,
                MinimumWidthRequest = 0
            };

            workflowShortName = new Label
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
            Func<RelativeLayout, double> workflowShortNameWidth = (parent) => workflowShortName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> workflowShortNameHeight = (parent) => workflowShortName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            workflowBox = new BoxView
            {
                HorizontalOptions = LayoutOptions.FillAndExpand,
                MinimumWidthRequest = App.ScreenWidth / 2
            };
            Func<RelativeLayout, double> workflowBoxWidth = (parent) => workflowBox.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> workflowBoxHeight = (parent) => workflowBox.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;

            workflowBox.BindingContext = workflowBox;
            workflowBox.SetBinding(HeightRequestProperty, "Width");

            workflowName = new Label
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
                Margin = new Thickness(0, 0, 0, 10)
            };
            Func<RelativeLayout, double> workflowNameWidth = (parent) => workflowName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Width;
            Func<RelativeLayout, double> workflowNameHeight = (parent) => workflowName.Measure(MainRelativeLayout.Width, MainRelativeLayout.Height).Request.Height;


            MainRelativeLayout.Children.Add(workflowBox,
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

            MainRelativeLayout.Children.Add(workflowShortName,
                                            Constraint.RelativeToView(workflowBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - workflowShortNameWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(workflowBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.Y - workflowShortNameHeight(parent) / 2;
                                            }));

            MainRelativeLayout.Children.Add(workflowName,
                                            Constraint.RelativeToView(workflowBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - workflowNameWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(workflowBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Bottom + 10;
                                            }));


            //Add extra label to fix weird padding bug
            if (Device.RuntimePlatform.Equals(Device.Android))
            {
                var blankLabel = new Label
                {
                    Text = ""
                };

                MainRelativeLayout.Children.Add(blankLabel,
                                            Constraint.RelativeToView(workflowBox, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Center.X - workflowNameWidth(parent) / 2;
                                            }),
                                            Constraint.RelativeToView(workflowName, (parent, sibling) =>
                                            {
                                                return sibling.Bounds.Bottom + 25;
                                            }));

            }



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
                BackgroundColor = Color.Transparent,
            };
        }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();

            var item = BindingContext as Client.Models.Workflow;

            workflowName.Text = item?.DisplayName?.ToString() ?? "Description not available";
            workflowBox.BackgroundColor = Color.FromHex(Constants.ColorSwatch[(item?.Id ?? 0 )% Constants.ColorSwatch.Length]);

            var array = item?.DisplayName.Split(' ');

            if (array.Length > 2)
                workflowShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1) + array.ElementAt(1)?.ToUpper().Substring(0, 1) ?? "" + array.ElementAt(2)?.ToUpper().Substring(0, 1) ?? "";
            else if (array.Length == 2)
                workflowShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1) + array.ElementAt(1)?.ToUpper().Substring(0, 1) ?? "";
            else
                workflowShortName.Text = array.FirstOrDefault().ToUpper().Substring(0, 1);
        }
    }
}