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

            process = new Process();
            startInfo = new ProcessStartInfo();
            startInfo.FileName = "CMD.exe";
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardInput = true;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;

            process.EnableRaisingEvents = false;
            process.StartInfo = startInfo;

            Check_Device();
            Port_Forward(Display_Socket);
            Port_Forward(Data_Socket);
   

        }
        String cmd_string;
        String ret;

        public void Check_Device()
        {
            startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
            Console.Write(startInfo.WorkingDirectory + ">");

            cmd_string = "adb devices";

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
        public void Port_Forward(object str_inst)
        {
            startInfo.WorkingDirectory = @"C:\Users\Jomin\android-sdks\platform-tools";
            //Console.Write(startInfo.WorkingDirectory + ">");

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

    }
}
