using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;
namespace Shell_test
{
    class Program
    {
        static void Main(string[] args)
        {
            new Program();
        }
        private Process process;
        private ProcessStartInfo startInfo;
        public Program()
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

            new Thread(new ThreadStart(cmd)).Start();
        }
        String cmd_string;
        String ret;
        
        public void cmd()
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
