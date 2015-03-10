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
    class Synapsys_Data_Socket
    {
        int sPort;
        public Synapsys_Data_Socket(int port)
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
}
