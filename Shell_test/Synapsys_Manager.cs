using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Shell_test
{
    class Synapsys_Manager
    {
        static void Main(string[] args)
        {            
            String str_display_socket = "adb forward tcp:1234 tcp:1234";
            String str_data_socket = "adb forward tcp:1235 tcp:1235";
            //new Synapsys_Display_Socket();
            new Synapsys_ADB_Instruction(str_display_socket, str_data_socket); // Port Forward 할 Port 2개를 입력 
            
            //  new Synapsys_Display_Socket(1234);
            //  new Synapsys_Data_Socket(1235);
        }
    }
}
