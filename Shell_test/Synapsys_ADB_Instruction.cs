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
   
    class Synapsys_ADB_Instruction
    {
        

        private Process process;
        private ProcessStartInfo startInfo;
        String str_display_socket = "adb forward tcp:1234 tcp:1234";
        String str_data_socket = "adb forward tcp:1235 tcp:1235";
        public Synapsys_ADB_Instruction(String Display_Socket, String Data_Socket)
        {
            Check_Device();
            Check_Device();
            Port_Forward(Display_Socket);
            Port_Forward(Data_Socket);
            Port_Define("33332", "0a1d99f6");

        }
        String cmd_string;
        String ret;

        public int Check_Device()
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

            int Device_num = 0;
            startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
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
                        Device_num = 0;
                    }
                    else if (result_msg.Length == 3)
                    {
                        Synapsys_Values.First_Device_Name = result_msg[1];
                        Synapsys_Values.First_Device_State = result_msg[2];
                        Synapsys_Values.Second_Device_Name = "";
                        Synapsys_Values.Second_Device_State = "";
                        Device_num = 1;
                    }
                    else if (result_msg.Length == 5)
                    {
                        Synapsys_Values.First_Device_Name = result_msg[1];
                        Synapsys_Values.First_Device_State = result_msg[2];
                        Synapsys_Values.Second_Device_Name = result_msg[3];
                        Synapsys_Values.Second_Device_State = result_msg[4];
                        Device_num = 2;
                    }
                    Console.Write(msg);
                }
                catch (Exception ex)
                {
                    startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
                    Console.WriteLine(ex.ToString());
                }
            }

            return Device_num;
        }
        public void Port_Forward(object str_inst)
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

            startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";

            cmd_string = (String)str_inst;

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
                    startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
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

        public void Port_Define(String msg, String id)
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

            String path = System.IO.Directory.GetCurrentDirectory();

            String filename = "\\portdefine.txt";

            String fullpath = path + filename;
            System.IO.File.WriteAllText(fullpath, msg, Encoding.Default);

            startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";

            String adb_msg = "adb -s " + id + " push " + fullpath + " /sdcard/portdefine.txt";

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
                    startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
                    Console.WriteLine(ex.ToString());
                }

            }


        }

    }
}
