using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;


namespace Synapsys_ADB
{
    public class Synapsys_Values
    {
        public static int Monitor_Driver_Num = 0; // 설치되어 있는 모니터 댓수 

        public static String First_Device_Name = "";  //  명령어 바꾸기
        public static String First_Device_State = "";
        public static String First_Device_Use = "Disuse";
        public static bool First_Device_Connect = false;

        public static String Second_Device_Name = "";
        public static String Second_Device_State = "";
        public static String Second_Device_Use = "Disuse";
        public static bool Second_Deivce_Connect = false;


        public static String Data_Socket_Port = "";
        public static String Display_Socket_Port = "";
        public static String Thumbnail_Socket_Port = "";

        public static int Current_Device_Num = 0;
        public static String Current_Device_Name = "";
        public static String[] Add_device = { "", "" };
        public static int Add_Device_Num = 0;
        public static int Monitor_Num = 0;
        public static int Current_Connect_Device_Num = 0;

        public static String[] Synapsys_Auto_Connect_List = {};

        public static String Check_Deivce_Msg = ""; //Add, Remove
        public static int Check_Device_Flag = 0;
        //(Check_Deivce_Msg 에 따라서)
        // 1 - First Monitor가 추가 또는 제거됨 
        // 2 - Second Monitor가 추가 또는 제거됨 
        // 3 - 두개의 Monitor가 동시에 추가 또는 제거됨


        public static String Synapsys_App_name = "com.example.synapsys_socket/com.example.synapsys_socket.MainActivity";
        public static String[] port = { "1234", "1235", "1236", "1237", "1238", "1239"}; //1234, 1235 : 1237,1238
        public static String adb_install_path = @"C:\Synapsys\adb";
        

        public static Synapsys_ADB_Instruction ADB_Instruction =  new Synapsys_ADB_Instruction();
        public static Synapsys_Buttons_Function Buttons_Function = new Synapsys_Buttons_Function();

        public static Synapsys_Monitor_Control Monitor_Control = new Synapsys_Monitor_Control();

    }
}
