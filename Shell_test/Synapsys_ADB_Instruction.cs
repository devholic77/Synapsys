using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.Net;
using System.Net.Sockets;
namespace Shell_test
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

        public void Check_Device() // 존나수정 Search Button이랑 합쳐서 전부 수정하기. 
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
                    if (result_msg.Length == 1)
                    {
                        Synapsys_Values.First_Device_Name = "";
                        Synapsys_Values.First_Device_State = "";
                        Synapsys_Values.Second_Device_Name = "";
                        Synapsys_Values.Second_Device_State = "";

                        Synapsys_Values.Current_Device_Num = 0;
                    }
                    else if (result_msg.Length == 3)
                    {
                        if (Synapsys_Values.First_Device_Use.Equals("Disuse") && Synapsys_Values.Second_Device_Use.Equals("Disuse"))
                        {
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                        };
                        Synapsys_Values.Current_Device_Num = 1;
                    }
                    else if (result_msg.Length == 5)
                    {
                        if (Synapsys_Values.First_Device_Use.Equals("Disuse"))
                        {
                            Synapsys_Values.First_Device_Name = result_msg[1];
                            Synapsys_Values.First_Device_State = result_msg[2];
                        }
                        if (Synapsys_Values.Second_Device_Use.Equals("Disuse"))
                        {
                            Synapsys_Values.Second_Device_Name = result_msg[3];
                            Synapsys_Values.Second_Device_State = result_msg[4];
                        }
                        Synapsys_Values.Current_Device_Num = 2;
                    }
                    Console.Write(msg);
                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }

            }

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

        public void Port_Define(String display_port, String data_port, String device_name) //안드로이드에 파일 저장하기 
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
            String msg = display_port + " " + data_port + " Synapsys";



            String path = System.IO.Directory.GetCurrentDirectory();

            String filename = "\\portdefine.txt";

            String fullpath = path + filename;
            System.IO.File.WriteAllText(fullpath, msg, Encoding.Default);

            startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;

            String adb_msg = "adb -s " + device_name + " push " + fullpath + " /sdcard/portdefine.txt";

            if (cmd_type(adb_msg))
            {
                try
                {
                    process.Start();
                    process.StandardInput.Write(adb_msg + Environment.NewLine);
                    process.StandardInput.Close();
                    ret = process.StandardOutput.ReadToEnd();
                    String ret_buf = ret.Substring(ret.IndexOf(adb_msg) + adb_msg.Length);
                    Console.Write(adb_msg);

                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = Synapsys_Values.adb_install_path;
                    Console.WriteLine(ex.ToString());
                }

            }
        }

        public void Start_Application(String device_name) //PC에서 App 실행하기
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

        }

    }
}
