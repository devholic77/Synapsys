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
    class Program
    {
        static void Main(string[] args)
        {
            String str_display_socket = "adb forward tcp:1234 tcp:1234";
            String str_data_socket = "adb forward tcp:1235 tcp:1235";

            //new Synapsys_Display_Socket();
            new Synapsys_ADB_Instruction(str_display_socket, str_data_socket); // Port Forward 할 Port 2개를 입력 
            new Synapsys_Display_Socket(1235);
            new Synapsys_Data_Socket(1234);
        }
    }
    /*
    class Synapsys_Display_Socket // 화면 받는 socket
    {
        public Synapsys_Display_Socket()
        {
            new Thread(new ThreadStart(Synapsys_Socket)).Start();
        }

        public static Socket socket;
        public static byte[] getbyte = new byte[1024];
        public static byte[] setbyte = new byte[1024];

        public const int sPort = 1237;


        public void Synapsys_Socket()
        {
            string sendstring = null;
            string getstring = null;

            IPAddress serverIP = IPAddress.Parse("127.0.0.1");
            IPEndPoint serverEndPoint = new IPEndPoint(serverIP, sPort);

            socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            Console.WriteLine("-----------------------------------------------------");
            Console.WriteLine(" 서버로 접속을 시작합니다. [엔터를 입력하세요] ");
            Console.WriteLine("-----------------------------------------------------");
            Console.ReadLine();

            socket.Connect(serverEndPoint);

            if (socket.Connected)
            {
                Console.WriteLine(">> 정상적으로 연결 되었습니다.(전송한 데이터를 입력해주세요)");
            }

            while (true)
            {
                Console.Write(">>");
                sendstring = Console.ReadLine();

                if (sendstring != String.Empty)
                {
                    int getValueLength = 0;
                    setbyte = Encoding.UTF7.GetBytes(sendstring);
                    socket.Send(setbyte, 0, setbyte.Length, SocketFlags.None);
                    Console.WriteLine("송신 데이터 : {0} | 길이{1}", sendstring, setbyte.Length);
                    socket.Receive(getbyte, 0, getbyte.Length, SocketFlags.None);
                    getValueLength = byteArrayDefrag(getbyte);
                    getstring = Encoding.UTF7.GetString(getbyte, 0, getValueLength + 1);
                    Console.WriteLine(">>수신된 데이터 :{0} | 길이{1}", getstring, getValueLength + 1);
                }

                getbyte = new byte[1024];
            }

        }
        public static int byteArrayDefrag(byte[] sData)
        {
            int endLength = 0;

            for (int i = 0; i < sData.Length; i++)
            {
                if ((byte)sData[i] != (byte)0)
                {
                    endLength = i;
                }
            }

            return endLength;
        }

    }
     *
     * /
     * 
     * /*
    class Synapsys_Data_Socket // Data 주고받는 socket
    {
        public Synapsys_Data_Socket()
        {

        }


    }
    */
    /*
    class Synapsys_ADB_Instruction // PC에서 Port Forwarding 및 connection device check 
    {
        private Process process;
        private ProcessStartInfo startInfo;
        String str_display_socket = "adb forward tcp:1234 tcp:1234";
        String str_data_socket = "adb forward tcp:1235 tcp:1235";
        public Synapsys_ADB_Instruction()
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
            Port_Forward(str_display_socket); // 내일 파라미터 2개로 하나에서 내는 것으로 수정수정수정정수정 
            Port_Forward(str_data_socket);

         //   new Thread(new ThreadStart(Check_Device)).Start();
         //   new Thread(new ParameterizedThreadStart(Port_Forward)).Start(str_inst);

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
            Console.Write(startInfo.WorkingDirectory + ">");

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
     * */

   

}
