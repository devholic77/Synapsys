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
    class Synapsys_Display_Socket
    {  
        int sPort;
        public Synapsys_Display_Socket(int port)
        {
            sPort = port;
            new Thread(new ThreadStart(Synapsys_Socket)).Start();
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

            socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            socket.Connect(serverEndPoint);

            if (socket.Connected)
            {
                Console.WriteLine(">> Synapsys_Display_Socket Start");
            }

            while (true)
            {
               // Console.Write(">>");
               // sendstring = Console.ReadLine();
                sendstring = "Synapsys_Display_Socket";
                if (sendstring != String.Empty)
                {
                    int getValueLength = 0;
                    setbyte = Encoding.UTF8.GetBytes(sendstring); 
                    socket.Send(setbyte, 0, setbyte.Length, SocketFlags.None);
                    Console.WriteLine("송신 데이터 : {0} | 길이{1}", sendstring, setbyte.Length);
                    socket.Receive(getbyte, 0, getbyte.Length, SocketFlags.None);
                    getValueLength = byteArrayDefrag(getbyte);
                    getstring = Encoding.UTF8.GetString(getbyte, 0, getValueLength + 1);
                    Console.WriteLine(">>수신 Synapsys_Display_Socket : ", getstring, getValueLength + 1);

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
}
