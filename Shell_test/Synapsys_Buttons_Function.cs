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
            int device_num = 0;

            device_num = Synapsys_Values.ADB_Instruction.Check_Device(); //returen 값이 Device 개수

            if (device_num == 1)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1],Synapsys_Values.First_Device_Name);                
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
            }
            else if(device_num == 2)
            {
                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.First_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);

                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[2], Synapsys_Values.port[3], Synapsys_Values.Second_Device_Name);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);
                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
            }
        }
        public void Synapsys_Start_Monitor(int num)
        {
            if (num == 1)
            {
                //Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.First_Device_Name);
                new Synapsys_Display_Socket(Synapsys_Values.port[0]);
                new Synapsys_Data_Socket(Synapsys_Values.port[1]);
            }
            else
            {
                Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.First_Device_Name);
                Synapsys_Values.ADB_Instruction.Start_Application(Synapsys_Values.Second_Device_Name);

                new Synapsys_Display_Socket(Synapsys_Values.port[0]);
                new Synapsys_Data_Socket(Synapsys_Values.port[1]);
                new Synapsys_Display_Socket(Synapsys_Values.port[2]);
                new Synapsys_Data_Socket(Synapsys_Values.port[3]);
            }
        }
    }
}
