using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Synapsys
{
	class NotifyObject
	{
		public NotifyObject(string message, string title)
		{
			this.message = message;
			this.title = title;
		}

		private string title;
		public string Title
		{
			get { return this.title; }
			set { this.title = value; }
		}

		private string message;
		public string Message
		{
			get { return this.message; }
			set { this.message = value; }
		}
	}
}
