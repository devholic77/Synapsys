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
    public class Synapsys_Data_Socket
    {
        int sPort;
        String d_num;
        public Synapsys_Data_Socket(String port, String device_num)
        {
            sPort = Convert.ToInt32(port);
            d_num = device_num;
            Thread Data_Socket = new Thread(new ThreadStart(Synapsys_Socket));
            Data_Socket.Start();
        }
        public static Socket socket;
        public static byte[] getbyte = new byte[1024];
        public static byte[] setbyte = new byte[1024];

        public void Synapsys_Socket()
        {
            string sendstring = null;
            string getstring = null;

            IPAddress serverIP = IPAddress.Parse("127.0.0.1");
            IPEndPoint serverEndPoint = new IPEndPoint(serverIP, sPort);

            while (true)
            {
                try// usb선이 빠졌을때 try 문
                {
                    socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                    socket.Connect(serverEndPoint);
                    Console.WriteLine("0 ");
                    if (socket.Connected)
                    {
                        Console.WriteLine(">> Synapsys_Data_Socket Start");
                    }
                    while (true) // 데이터 송수신부분 
                    {
                        Console.WriteLine("1 ");
                        try // 사용자가 어플리케이션을 강제종료 시켰을때 try문 
                        {
                            Console.WriteLine("2 ");
                            sendstring = "Synapsys_Data_Socket";
                            if (sendstring != String.Empty)
                            {
                                int getValueLength = 0;
                                setbyte = Encoding.UTF8.GetBytes(sendstring);
                                socket.Send(setbyte, 0, setbyte.Length, SocketFlags.None);
                                Console.WriteLine("송신 데이터 : {0} | 길이{1}", sendstring, setbyte.Length);
                                socket.Receive(getbyte, 0, getbyte.Length, SocketFlags.None);
                                getValueLength = byteArrayDefrag(getbyte);
                                getstring = Encoding.UTF8.GetString(getbyte, 0, getValueLength + 1);
                                Console.WriteLine(">>수신 Synapsys_Data_Socket : ", getstring, getValueLength + 1);
                            }

                            getbyte = new byte[1024];
                        }
                        catch (SocketException) // 사용자가 어플리케이션을 강제종료 시켰을때 try문 
                        {
                            // 메인 컨트롤 패널에서 "안드로이드 디바이스에서 앱이 강제로 종료되었습니다 다시 연결하시겠습니까? "라고 물어보고
                            // Synapsys_Values.ADB_Instruction.Start_Application(d_num); // 확인을 누르면 다시시작 
                            Console.WriteLine("Data try try try ");
                            Thread.Sleep(3000);
                            socket.Close();
                            break;
                        }
                        Console.WriteLine("3 ");
                    }
                }
                catch (SocketException)
                { // usb선이 빠졌을때 try 문 프로그램이 종료됬다는 말.
                    Console.WriteLine("222222 USB선이 빠짐 "); 
                    break;
                }
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
}
