using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Synapsys_ADB
{
    public class Synapsys_Buttons_Function
    {
        public void Synapsys_Search_Button()
        {
            //device_num = Synapsys_Values.ADB_Instruction.Check_Device(); //returen 값이 Device 개수

            Synapsys_Values.ADB_Instruction.Check_Device();

            if (Synapsys_Values.Current_Device_Num == 1)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);//
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);
            }
            else if (Synapsys_Values.Current_Device_Num == 2)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);
            }
        }
        public void Synapsys_Start_Monitor(String Device_Name) // android app 실행 및 socket 연결 이쪽에 문제가있음
        {
            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                //Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.First_Device_Name);
                //new Synapsys_Display_Socket(Synapsys_Values.port[0]);
 
                //new Synapsys_Display_Socket(Synapsys_Values.port[0], Synapsys_Values.Second_Device_Name);
                //new Synapsys_Data_Socket(Synapsys_Values.port[1], Synapsys_Values.First_Device_Name);
                Synapsys_Values.First_Device_Use = "Use";
            }
            else
            {
                //Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.Second_Device_Name);
                //System.Diagnostics.Process.Start("Synapsys_Sub_Program.exe", "..\\..\\Synapsys_Sub_Program\\Synapsys_Sub_Program\\bin\\Debug");

                //new Synapsys_Display_Socket(Synapsys_Values.port[2], Synapsys_Values.Second_Device_Name);
                //new Synapsys_Data_Socket(Synapsys_Values.port[3], Synapsys_Values.Second_Device_Name);
                Synapsys_Values.Second_Device_Use = "Use";
            }

            String path = System.IO.Directory.GetCurrentDirectory(); // sub program 실행 
            path.IndexOf("Synapsys");
            path = path.Substring(0, path.IndexOf("Synapsys")) + "Synapsys\\Synapsys_Sub_Program\\Synapsys_Sub_Program\\bin\\Release\\Synapsys_Sub_Program.exe";
            System.Diagnostics.Process.Start(path);  
        }


        public void Synapsys_Remove_Monitor(String Device_Name)
		{
			;

            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                Synapsys_Values.First_Device_Use = "Disuse";
                Synapsys_Values.First_Device_Name = "";
                Synapsys_Values.First_Device_State = "";
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Disuse";
                Synapsys_Values.Second_Device_Name = "";
                Synapsys_Values.Second_Device_State = "";
            }
        }

        public void Synapsys_Stop_Monitor(String Device_Name)
        {
            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                Synapsys_Values.First_Device_Use = "Disuse";
                // 안드로이드 과제와 협의하기. 종료했을때 소켓을 닫을지 안닫을지.
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Disuse";
            }

        }
    }
}
