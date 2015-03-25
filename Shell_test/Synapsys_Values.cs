using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Shell_test
{
    public class Synapsys_Values
    {
        public static String First_Device_Name = "";  //  명령어 바꾸기
        public static String First_Device_State = "";
        public static String First_Device_Use = "Disuse";
        public static String Second_Device_Name = "";
        public static String Second_Device_State = "";
        public static String Second_Device_Use = "Disuse";
        public static String Data_Socket_Port = "";
        public static String Display_Socket_Port = "";
        public static int Current_Device_Num = 0;
        public static String Synapsys_App_name = "com.example.synapsys_socket/com.example.synapsys_socket.MainActivity";
        public static String[] port = {"1234","1235","1236","1237"};
        public static String adb_install_path = @"C:\Users\Jomin\android-sdks\platform-tools";

        public static Synapsys_ADB_Instruction ADB_Instruction =  new Synapsys_ADB_Instruction();
        public static Synapsys_Buttons_Function Buttons_Function = new Synapsys_Buttons_Function();

    }
}
