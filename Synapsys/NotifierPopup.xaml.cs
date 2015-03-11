using System;
using System.Windows;
using System.Windows.Documents;

using System.Collections.ObjectModel;

namespace Synapsys
{

	/// <summary>
	/// NotifierPopup.xaml에 대한 상호 작용 논리
	/// </summary>
	public class NotifierPopup : TaskbarNotifier
	{
		public NotifierPopup()
		{
			InitializeComponent();
		}

		private ObservableCollection<NotifyObject> notifyContent;
		/// <summary>
		/// A collection of NotifyObjects that the main window can add to.
		/// </summary>
		public ObservableCollection<NotifyObject> NotifyContent
		{
			get
			{
				if (this.notifyContent == null)
				{
					// Not yet created.
					// Create it.
					this.NotifyContent = new ObservableCollection<NotifyObject>();
				}

				return this.notifyContent;
			}
			set
			{
				this.notifyContent = value;
			}
		}

		private void Item_Click(object sender, EventArgs e)
		{
			Hyperlink hyperlink = sender as Hyperlink;

			if (hyperlink == null)
				return;

			NotifyObject notifyObject = hyperlink.Tag as NotifyObject;
			if (notifyObject != null)
			{
				MessageBox.Show("\"" + notifyObject.Message + "\"" + " clicked!");
			}
		}

		private void HideButton_Click(object sender, EventArgs e)
		{
			this.ForceHidden();
		}
	}
}
