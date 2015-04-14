using System;
using System.Diagnostics;

namespace Synapsys_ADB
{
    public class Synapsys_Buttons_Function
    {
        Process FirstSubProgram;
        Process SecondSubProgram;

        public void Synapsys_Search_Button()
        {
            //device_num = Synapsys_Values.ADB_Instruction.Check_Device(); //returen 값이 Device 개수

            Synapsys_Values.ADB_Instruction.Check_Device();

        }
        public void Synapsys_Start_Monitor(String Device_Name) // android app 실행 및 socket 연결 이쪽에 문제가있음
        {

            String path = System.IO.Directory.GetCurrentDirectory(); // sub program 실행 
            path.IndexOf("Synapsys");
            path = path.Substring(0, path.IndexOf("Synapsys\\")) + "Synapsys_Sub_Program\\Synapsys_Sub_Program\\bin\\Release\\Synapsys_Sub_Program.exe";

            if(Device_Name.Equals(Synapsys_Values.First_Device_Name)) // 1e대인경우
            {
                Synapsys_Values.First_Device_Use = "Use";
                FirstSubProgram = Process.Start(path, Synapsys_Values.port[2]);
                
                //asdf.Close();
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Use";
                SecondSubProgram = Process.Start(path, Synapsys_Values.port[5]);
                //System.Diagnostics.Process.Start(path, Synapsys_Values.port[5]);  
            }

        }
        public void Synapsys_Remove_Monitor(String Device_Name) // usb 선이 빠진 경우
		{
            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                Synapsys_Values.First_Device_Use = "Disuse";
                Synapsys_Values.First_Device_Name = "";
                Synapsys_Values.First_Device_State = "";
                FirstSubProgram.Close();
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Disuse";
                Synapsys_Values.Second_Device_Name = "";
                Synapsys_Values.Second_Device_State = "";
                SecondSubProgram.Close();
            }
        }

        public void Synapsys_Stop_Monitor(String Device_Name) // stop 버튼을 누를 경우 
        {
            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                Synapsys_Values.First_Device_Use = "Disuse";
                FirstSubProgram.Close();
                // 안드로이드 과제와 협의하기. 종료했을때 소켓을 닫을지 안닫을지.
            }
            else
            {
                SecondSubProgram.Close();
                Synapsys_Values.Second_Device_Use = "Disuse";
            }

        }
    }
}
