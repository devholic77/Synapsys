using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Synapsys_ADB
{
	public class ConnEvent : EventArgs
	{
		public int Device { get; set; }
		public string Message { get; set; }
	}
}
