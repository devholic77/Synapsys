using System;
using System.Text;
using System.Diagnostics;
using System.Threading;

namespace Synapsys_ADB
{

    public class Synapsys_ADB_Instruction
    {

        private Process process;
        private ProcessStartInfo startInfo;

        public Synapsys_ADB_Instruction()
        {
            //Check_Device();

            //Start_Application(Synapsys_Values.First_Device_Name);
            //new Thread(new ThreadStart(Check_Device)).Start();

        }

        String cmd_string;
        String ret;
        public void Init_Values()
        {
            Synapsys_Values.First_Device_Name = "";
            Synapsys_Values.First_Device_State = "";
            Synapsys_Values.Second_Device_Name = "";
            Synapsys_Values.Second_Device_State = "";
            Synapsys_Values.Current_Device_Num = 0;
        }
        public bool Check_Device() // 존나수정 Search Button이랑 합쳐서 전부 수정하기. 
        // win 함수로 usb 연결여부를 계속해서 확인 후 변화가 있을때마다 함수 호출. 내일마무리하기.
        {
            Console.WriteLine("");
            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            process.EnableRaisingEvents = false;
            process.StartInfo = startInfo;
            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
            Console.Write(startInfo.WorkingDirectory + ">");

            cmd_string = "adb devices";
            int index;
            bool flag = true;

            if (cmd_type(cmd_string))
            {
                try
                {
                    process.Start();
                    process.StandardInput.Write(cmd_string + Environment.NewLine);
                    process.StandardInput.Close();
                    ret = process.StandardOutput.ReadToEnd();
                    String msg = ret.Substring(ret.IndexOf(cmd_string) + cmd_string.Length);
                    index = msg.IndexOf("\r\n\r\n\r\n");
                    String sub_msg = msg.Substring(0, index);
                    sub_msg = sub_msg.Replace("\r\n", " ");
                    sub_msg = sub_msg.Replace("\t", " ");
                    sub_msg = sub_msg.Replace(" List of devices attached ", "");
                    String[] result_msg = sub_msg.Split(' ');


                    if (Synapsys_Values.Current_Connect_Device_Num < result_msg.Length / 2) // Android Device가 연결되었을때
                    {
                        Console.WriteLine("Android Device가 연결되었을때");

                        if (result_msg.Length == 3) // device가 한대 연결되었을때
                        {
                            Console.WriteLine("device가 한대 연결되었을때");
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                            Synapsys_Values.First_Device_Connect = true;

                            Synapsys_Values.Monitor_Num = 1;

                            Synapsys_Values.Add_device[0] = result_msg[1];
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 1;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                            Synapsys_Values.Current_Connect_Device_Num = 1;

                            Synapsys_Values.Check_Deivce_Msg = "Add";
                            Synapsys_Values.Check_Device_Flag = 1;

                            //Console.WriteLine("");
                            //Console.WriteLine("1 device");
                        }
                        else if (result_msg.Length == 5 && Synapsys_Values.First_Device_Connect == true) //1개가 연결되있는 상태에서 추가적으로 1개가 연결된경우
                        {
                            Console.WriteLine("1개가 연결되있는 상태에서 추가적으로 1개가 연결된경우");
                            Synapsys_Values.Second_Device_Name = result_msg[3];
                            Synapsys_Values.Second_Device_State = result_msg[4];
                            Synapsys_Values.Second_Deivce_Connect = true;
                            Synapsys_Values.Monitor_Num = 2;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);
                            Synapsys_Values.Add_device[0] = result_msg[3];
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 1;

                            Synapsys_Values.Check_Deivce_Msg = "Add";
                            Synapsys_Values.Check_Device_Flag = 2;


                            Synapsys_Values.Current_Connect_Device_Num = 2;
                            //Console.WriteLine("");
                            //Console.WriteLine("2 device");
           
                        }
                        else if (result_msg.Length == 5 && Synapsys_Values.First_Device_Connect == false) //동시에 2개가 연결된경우
                        {
                            Console.WriteLine("동시에 2개가 연결된경우");
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                            Synapsys_Values.First_Device_Connect = true;

                            Synapsys_Values.Second_Device_Name = result_msg[3];
                            Synapsys_Values.Second_Device_State = result_msg[4];
                            Synapsys_Values.Second_Deivce_Connect = true;

                            Synapsys_Values.Monitor_Num = 3;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);

                            Synapsys_Values.Add_device[0] = result_msg[1];
                            Synapsys_Values.Add_device[1] = result_msg[3];
                            Synapsys_Values.Add_Device_Num = 2;

                            Synapsys_Values.Current_Connect_Device_Num = 2;

                            Synapsys_Values.Check_Deivce_Msg = "Add";
                            Synapsys_Values.Check_Device_Flag = 3;

                           // Console.WriteLine("2 device");
                            flag = true;
                        }
                    }

                    else if (Synapsys_Values.Current_Connect_Device_Num > result_msg.Length / 2) // Android Device가 제거되었을때
                    {
                        //Console.WriteLine("Android Device가 제거되었을때");

                        int num = Synapsys_Values.Current_Connect_Device_Num - result_msg.Length / 2;

                        if (num == 1) // device가 한대 제거됬을때
                        {
                            if (Synapsys_Values.Current_Connect_Device_Num == 1)  //현재 연결된 device가 1대 
                            {
                                Synapsys_Values.First_Device_Name = "";
                                Synapsys_Values.First_Device_State = "";
                                Synapsys_Values.First_Device_Connect = false;

                                Synapsys_Values.Check_Deivce_Msg = "Remove";
                                Synapsys_Values.Check_Device_Flag = 1;

                                Synapsys_Values.Current_Connect_Device_Num = 0;

                                //Console.WriteLine("현재 연결된 device가 1대");

                            }
                            else //현재 연결된 device가 2대 
                            {
                                //Console.WriteLine("현재 연결된 device가 2대");
                                if (Synapsys_Values.First_Device_Name.Equals(result_msg[1])) // 2번 모니터 제거된 경우
                                {
                                    Synapsys_Values.Second_Device_Name = "";
                                    Synapsys_Values.Second_Device_State = "";
                                    Synapsys_Values.Second_Deivce_Connect = false;

                                    Synapsys_Values.Check_Deivce_Msg = "Remove";
                                    Synapsys_Values.Check_Device_Flag = 2;

                                    //Console.WriteLine("2번 모니터가 제거된 경우");
                                }
                                else // 1번 모니터 제거된 경우
                                {
                                    // 초기화 후 다시시작 2번이 1번이 되야됨.

                                    //Console.WriteLine("1번 모니터가 제거된 경우");

                                    //2번 소켓을 지우고 //연호쪽에서 포
                                    
                                    
                                    
                                    //1번 소켓을 실행

                                    Synapsys_Values.First_Device_Name = Synapsys_Values.Second_Device_Name;
                                    Synapsys_Values.First_Device_State = Synapsys_Values.Second_Device_State;
                                    Synapsys_Values.First_Device_Connect = true;

                                    Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                                    Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                                    Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                                    Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);


                                    Synapsys_Values.Second_Device_Name = "";
                                    Synapsys_Values.Second_Device_State = "";
                                    Synapsys_Values.Second_Deivce_Connect = false;

                                    Synapsys_Values.Current_Connect_Device_Num = 0;

                                    // 특수케이스
                                    Synapsys_Values.Check_Deivce_Msg = "Remove";
                                    Synapsys_Values.Check_Device_Flag = 4;

                                }
                                Synapsys_Values.Current_Connect_Device_Num = 1;
                            }
                        }
                        else if (num == 2) // 두대 모두 제거됬을때 
                        {
                            Synapsys_Values.First_Device_Name = "";
                            Synapsys_Values.First_Device_State = "";
                            Synapsys_Values.First_Device_Connect = false;

                            Synapsys_Values.Second_Device_Name = "";
                            Synapsys_Values.Second_Device_State = "";
                            Synapsys_Values.Second_Deivce_Connect = false;

                            Synapsys_Values.Add_device[0] = "";
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 0;

                            Synapsys_Values.Current_Connect_Device_Num = 0;

                            Synapsys_Values.Check_Deivce_Msg = "Remove";
                            Synapsys_Values.Check_Device_Flag = 3;

                        }
                    }
                    else // 아무것도 아닐때
                    {
                        flag = false;

                    }



                    /*


                    if (result_msg.Length == 1)
                    {
                        Synapsys_Values.First_Device_Name = "";
                        Synapsys_Values.First_Device_State = "";
                        Synapsys_Values.Second_Device_Name = "";
                        Synapsys_Values.Second_Device_State = "";
                        Synapsys_Values.Current_Device_Num = 0;
                        Console.WriteLine("not device");
                    }
                    else if (result_msg.Length == 3) //device가 한개 연결되었을때
                    {
                        if (Synapsys_Values.First_Device_Connect == false && Synapsys_Values.Second_Deivce_Connect == false)
                        {
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                            Synapsys_Values.First_Device_Connect = true;

                            Synapsys_Values.Monitor_Num = 1;

                            Synapsys_Values.Add_device[0] = result_msg[1];
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 1;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                            Synapsys_Values.Current_Connect_Device_Num = 1;
                            flag = true;
                            Console.WriteLine("");
                            Console.WriteLine("1 device");
                        }
                        else if (Synapsys_Values.First_Device_Connect == false && Synapsys_Values.Second_Deivce_Connect == true)
                        {

                        }
                    }
                    else if (result_msg.Length == 5) //device가 두개 연결되었을때 
                    {

                    }

                    if (result_msg.Length == 1)
                    {
                        Synapsys_Values.First_Device_Name = "";
                        Synapsys_Values.First_Device_State = "";
                        Synapsys_Values.Second_Device_Name = "";
                        Synapsys_Values.Second_Device_State = "";
                        Synapsys_Values.Current_Device_Num = 0;
                        Console.WriteLine("not device");
                    }
                    else if (result_msg.Length == 3) //device가 한개 연결되었을때
                    {
                        if (Synapsys_Values.First_Device_Use.Equals("Disuse") && Synapsys_Values.Second_Device_Use.Equals("Disuse"))
                        {
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                            Synapsys_Values.First_Device_Connect = true;

                            Synapsys_Values.Monitor_Num = 1;


                            Synapsys_Values.Add_device[0] = result_msg[1];
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 1;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                            Synapsys_Values.Current_Connect_Device_Num = 1;
                            flag = true;
                            Console.WriteLine("");
                            Console.WriteLine("1 device");
                        }
                        else if (Synapsys_Values.First_Device_Use.Equals("Disuse") && Synapsys_Values.Second_Device_Use.Equals("Use"))
                        {
                            Synapsys_Values.First_Device_Name = "";
                            Synapsys_Values.First_Device_State = "";
                            Synapsys_Values.Add_device[0] = "";
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 0;
                        }
                        else if (Synapsys_Values.First_Device_Use.Equals("Use"))
                        {
                            Synapsys_Values.Second_Device_Name = "";
                            Synapsys_Values.Second_Device_State = "";
                            Synapsys_Values.Add_device[0] = "";
                            Synapsys_Values.Add_device[1] = "";
                            Synapsys_Values.Add_Device_Num = 0;
                        }
                        Synapsys_Values.Current_Device_Num = 1;
                    }

                    else if (result_msg.Length == 5) //device가 2개 연결되었을때
                    {
                        if (Synapsys_Values.First_Device_Use.Equals("Disuse") && Synapsys_Values.Second_Device_Use.Equals("Disuse"))
                        {
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                            Synapsys_Values.First_Device_Connect = true;

                            Synapsys_Values.Second_Device_Name = result_msg[3];
                            Synapsys_Values.Second_Device_State = result_msg[4];
                            Synapsys_Values.Second_Deivce_Connect = true;

                            Synapsys_Values.Monitor_Num = 3;

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                            Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);

                            Synapsys_Values.Add_device[0] = result_msg[1];
                            Synapsys_Values.Add_device[1] = result_msg[3];
                            Synapsys_Values.Add_Device_Num = 2;

                            Synapsys_Values.Current_Connect_Device_Num = 2;

                            Console.WriteLine("2 device");
                            flag = true;
                        }
                        else if (Synapsys_Values.First_Device_Use.Equals("Use") && Synapsys_Values.Second_Device_Use.Equals("Disuse"))
                        {
                            if (Synapsys_Values.First_Device_Name.Equals(result_msg[1]))
                            {
                                Synapsys_Values.Second_Device_Name = result_msg[3];
                                Synapsys_Values.Second_Device_State = result_msg[4];
                                Synapsys_Values.Second_Deivce_Connect = true;
                                Synapsys_Values.Monitor_Num = 2;

                                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);
                                Synapsys_Values.Add_device[0] = result_msg[3];
                                Synapsys_Values.Add_device[1] = "";
                                Synapsys_Values.Add_Device_Num = 1;
                            }
                            else
                            {
                                Synapsys_Values.Second_Device_Name = result_msg[1];
                                Synapsys_Values.Second_Device_State = result_msg[2];
                                Synapsys_Values.Second_Deivce_Connect = true;

                                Synapsys_Values.Monitor_Num = 2;

                                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[3]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[4]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[5]);
                                Synapsys_Values.Add_device[0] = result_msg[1];
                                Synapsys_Values.Add_device[1] = "";
                                Synapsys_Values.Add_Device_Num = 1;
                            }
                            Synapsys_Values.Current_Connect_Device_Num = 2;
                            Console.WriteLine("");
                            Console.WriteLine("2 device");
                            flag = true;
                        }
                        else if (Synapsys_Values.First_Device_Use.Equals("Disuse") && Synapsys_Values.Second_Device_Use.Equals("Use"))
                        {
                            if (Synapsys_Values.Second_Device_Name.Equals(result_msg[1]))
                            {
                                Synapsys_Values.First_Device_Name = result_msg[3];
                                Synapsys_Values.First_Device_State = result_msg[4];
                                Synapsys_Values.First_Device_Connect = true;

                                Synapsys_Values.Monitor_Num = 1;

                                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);

                                Synapsys_Values.Add_device[0] = result_msg[3];
                                Synapsys_Values.Add_device[1] = "";
                                Synapsys_Values.Add_Device_Num = 1;
                            }
                            else
                            {
                                Synapsys_Values.First_Device_Name = result_msg[1];
                                Synapsys_Values.First_Device_State = result_msg[2];
                                Synapsys_Values.First_Device_Connect = true;

                                Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[0]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[1]);
                                Synapsys_Values.ADB_Instruction.Port_Forward(Synapsys_Values.port[2]);
                                Synapsys_Values.Add_Device_Num = 1;
                                Synapsys_Values.Add_device[0] = result_msg[1];
                                Synapsys_Values.Add_device[1] = "";
                            }
                            flag = true;
                            Console.WriteLine("");
                            Console.WriteLine("2 device");
                        }
                        Synapsys_Values.Current_Connect_Device_Num = 2;
                        Synapsys_Values.Current_Device_Num = 2;
                    }
                    Console.Write(msg);
                     *      * */
                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }

            }
            return flag;

        }
        public void Port_Forward(object str_inst) // 실재 ADB 포워딩
        {
            Console.WriteLine("");
            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            process.EnableRaisingEvents = false;
            process.StartInfo = startInfo;

            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;

            cmd_string = "adb forward tcp:" + (String)str_inst + " tcp:" + (String)str_inst;

            if (cmd_type(cmd_string))
            {
                try
                {
                    process.Start();
                    process.StandardInput.Write(cmd_string + Environment.NewLine);
                    process.StandardInput.Write(cmd_string + Environment.NewLine);
                    process.StandardInput.Close();
                    ret = process.StandardOutput.ReadToEnd();
                    String ret_buf = ret.Substring(ret.IndexOf(cmd_string) + cmd_string.Length);
                    Console.Write(ret_buf);

                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }
            }
        }
        public bool cmd_type(String msg)
        {
            if (msg.Length > 2 && msg[0].ToString() == "c" && msg[1].ToString() == "d")
            {
                startInfo.WorkingDirectory += msg.Replace("cd ", "") + "\\";
                return false;
            }
            if (msg.Length == 2 && msg[1] == ':')
            {
                msg = msg.ToUpper();
                startInfo.WorkingDirectory = msg[0].ToString() + @":\";
                Console.Write("\r\n" + startInfo.WorkingDirectory + ">");
                return false;
            }
            return true;
        }

        public void Port_Define(String display_port, String data_port, String thumbnail_port, String device_name) //안드로이드에 파일 저장하기 
        {                                                                                  //PC에서 저장하도록 변경
            Console.WriteLine("");
            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            process.EnableRaisingEvents = true;
            process.StartInfo = startInfo;
            String msg = display_port + "\r\n" + data_port + "\r\n" + thumbnail_port + "\r\n" + device_name + "\r\nSynapsys";

            String path = System.IO.Directory.GetCurrentDirectory();

            String filename = "..\\..\\..\\..\\..\\portdefine.txt";

            String fullpath = path + filename;
            System.IO.File.WriteAllText(fullpath, msg, Encoding.Default);

            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;

            String adb_msg = "adb -s " + device_name + " push " + fullpath + " /data/synapsys/connection.dat";

            if (cmd_type(adb_msg))
            {
                try
                {
                    Get_Root_Permission(device_name);

                    String ret_buf;
                    process.Start();
                    process.StandardInput.Write(adb_msg + Environment.NewLine);
                    process.StandardInput.Close();
                    ret = process.StandardOutput.ReadToEnd();
                    ret_buf = ret.Substring(ret.IndexOf(adb_msg) + adb_msg.Length);
                    Console.WriteLine(ret_buf);
                    //Console.Write(adb_msg);

                }
                catch (Exception ex)
                {
                    //startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }

            }
        }
        public void Get_Root_Permission(String device_name)
        {
            Console.WriteLine("");
            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            process.EnableRaisingEvents = true;
            process.StartInfo = startInfo;
            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;

            String cmd_string = "adb -s " + device_name + " root ";
            //String cmd_string = "adb -s 0a1d99f6 push C:\\Users\\Jomin\\Desktop\\Synapsys\\Synapsys\\Synapsys\\bin\\Debug..\\..\\..\\..\\..\\portdefine.txt /data/synapsys/connection.dat";
            try
            {
                process.Start();
                process.StandardInput.Write(cmd_string + Environment.NewLine);
                process.StandardInput.Close();
                ret = process.StandardOutput.ReadToEnd();
                String ret_buf = ret.Substring(ret.IndexOf(cmd_string) + cmd_string.Length);


                int num = ret_buf.IndexOf("\r\n\r\n");
                ret_buf = ret_buf.Substring(0, num);

                if (!ret_buf.Equals("\r\nadbd is already running as root"))
                    Thread.Sleep(2000);
                Console.WriteLine(ret_buf);

                //Console.Write(adb_msg);

                //String msg = ret.Substring(ret.IndexOf(cmd_string) + cmd_string.Length);
            }
            catch (Exception ex)
            {
                //startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                Console.WriteLine(ex.ToString());

            }
        }
        public void Start_Application(String device_name) //PC에서 App 실행하기
        {
            /* 연호쪽에서 처리해서 구지 할 필요 없음.
            Console.WriteLine("");
            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            process.EnableRaisingEvents = false;
            process.StartInfo = startInfo;

            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;

            cmd_string = "adb -s " + device_name + " shell am start -n " + Synapsys_Values.Synapsys_App_name;

            if (cmd_type(cmd_string))
            {
                try
                {
                    process.Start();
                    process.StandardInput.Write(cmd_string + Environment.NewLine);
                    process.StandardInput.Close();
                    ret = process.StandardOutput.ReadToEnd();
                    String ret_buf = ret.Substring(ret.IndexOf(cmd_string) + cmd_string.Length);
                    Console.Write(ret_buf);

                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }

            }
            */
        }

    }
}