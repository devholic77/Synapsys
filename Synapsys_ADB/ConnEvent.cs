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

        public  String Check_Deivce_Msg { get; set; } //Add, Remove
        public  int Check_Device_Flag { get; set; }
        //(Check_Deivce_Msg 에 따라서)
        // 1 - First Monitor가 추가 또는 제거됨 
        // 2 - Second Monitor가 추가 또는 제거됨 
        // 3 - 두개의 Monitor가 동시에 추가 또는 제거됨


	}
}
