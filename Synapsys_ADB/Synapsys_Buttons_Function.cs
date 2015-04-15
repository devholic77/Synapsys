using System;
using System.Diagnostics;

namespace Synapsys_ADB
{
    public class Synapsys_Buttons_Function
    {
        
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
                path = @"C:\Users\Jomin\Source\Repos\Synapsys_Sub_Program\Synapsys_Sub_Program\bin\Release\Synapsys_Sub_Program.exe";
                Synapsys_Values.First_Device_Use = "Use";
               
                Synapsys_Values.FirstSubProgram = Process.Start(path, Synapsys_Values.port[2]);
                Console.WriteLine("1번 모니터 실행");
                //asdf.Close();
            }
            else
            {
                path = @"C:\Users\Jomin\Source\Repos\Winpart_Sub\Synapsys_Sub_Program\Synapsys_Sub_Program\bin\Release\Synapsys_Sub_Program.exe";
                Console.WriteLine("2번 모니터 실행");
                Synapsys_Values.Second_Device_Use = "Use";
                Synapsys_Values.SecondSubProgram = Process.Start(path, Synapsys_Values.port[5]);
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
                Synapsys_Values.FirstSubProgram.Kill();
            }
            else
            {
                Synapsys_Values.Second_Device_Use = "Disuse";
                Synapsys_Values.Second_Device_Name = "";
                Synapsys_Values.Second_Device_State = "";
                Synapsys_Values.SecondSubProgram.Kill();
            }
        }

        public void Synapsys_Stop_Monitor(String Device_Name) // stop 버튼을 누를 경우 대찬이형 부분 소켓 닫기~!!! 
        {
            if(Device_Name.Equals(Synapsys_Values.First_Device_Name))
            {
                Synapsys_Values.First_Device_Use = "Disuse";
                Synapsys_Values.FirstSubProgram.Kill();
                // 안드로이드 과제와 협의하기. 종료했을때 소켓을 닫을지 안닫을지.
            }
            else
            {
                Synapsys_Values.SecondSubProgram.Kill();
                Synapsys_Values.Second_Device_Use = "Disuse";
            }

        }
    }
}
