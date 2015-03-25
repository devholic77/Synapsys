using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Shell_test
{
    public class Synapsys_Buttons_Function
    {
        public void Synapsys_Search_Button()
        {
            //device_num = Synapsys_Values.ADB_Instruction.Check_Device(); //returen 값이 Device 개수

            Synapsys_Values.ADB_Instruction.Check_Device();

            if (Synapsys_Values.Current_Device_Num == 1)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1],Synapsys_Values.First_Device_Name);                
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
            }
            else if (Synapsys_Values.Current_Device_Num == 2)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.First_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);

                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[2], Synapsys_Values.port[3], Synapsys_Values.Second_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
            }
        }
        public void Synapsys_Start_Monitor(int num) // android app 실행 및 socket 연결
        {
            if (num == 1)
            {
                Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.First_Device_Name);
                //new Synapsys_Display_Socket(Synapsys_Values.port[0]);
                new Synapsys_Display_Socket(Synapsys_Values.port[0], Synapsys_Values.Second_Device_Name);
                new Synapsys_Data_Socket(Synapsys_Values.port[1], Synapsys_Values.First_Device_Name);
                Synapsys_Values.First_Device_Use = "Use";
            }
            else
            {
                Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.Second_Device_Name);
                new Synapsys_Display_Socket(Synapsys_Values.port[2], Synapsys_Values.Second_Device_Name);
                new Synapsys_Data_Socket(Synapsys_Values.port[3], Synapsys_Values.Second_Device_Name);
                Synapsys_Values.Second_Device_Use = "Use";
            }
        }


        public void Synapsys_Remove_Monitor(int num)
        {

            if (num == 1)
            {
                Synapsys_Values.First_Device_Use = "Disuse";
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Disuse";
            }
        }
    }
}
