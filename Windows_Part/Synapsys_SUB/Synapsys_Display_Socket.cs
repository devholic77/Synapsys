using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;


namespace Synapsys_Sub_Program
{
    public class Synapsys_Display_Socket
    {
        int sPort;
        String d_num;
        Thread ReadThread;

        public Synapsys_Display_Socket(String port, String device_num)
        {
            sPort = Convert.ToInt32(port);
            d_num = device_num;
            Synapsys_Socket_Init();
        }
        ~Synapsys_Display_Socket()
        {
            if (ReadThread != null)
            {
                ReadThread.Abort();
            }
            socket.Close();
        }
        public static Socket socket;
        public static byte[] getbyte = new byte[1024];
        public static byte[] setbyte = new byte[1024];

        public bool Synapsys_Socket_Init()
        {
            IPAddress serverIP = IPAddress.Parse("127.0.0.1");
            IPEndPoint serverEndPoint = new IPEndPoint(serverIP, sPort);
            try
            {
                socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socket.Connect(serverEndPoint);


                if (socket.Connected)
                {
                    Console.WriteLine(">> Synapsys_Display_Socket Start");

                    ReadThread = new Thread(new ThreadStart(Synapsys_ReadThread));
                    ReadThread.Start();
                }

            }
            catch (SocketException)
            {
                if (ReadThread != null)
                    ReadThread.Abort();// thread 종료
                Console.WriteLine("Display Socket Error ");
                socket.Close();
                return false;
            }
            return true;
        }


        public void Synaposys_Write(String WriteMsg)
        {
            NetworkStream ns = new NetworkStream(socket);
            StreamWriter sw = new StreamWriter(ns);

            try
            {
                if (WriteMsg != String.Empty)
                {
                    //setbyte = Encoding.UTF8.GetBytes(WriteMsg);
                    sw.Write(WriteMsg);
                    sw.Flush();
                    //socket.Send(setbyte, 0, setbyte.Length, SocketFlags.None);
                }
            }
            catch (SocketException)
            {
                Console.WriteLine(">>Display Send ERROR : ");
            }
        }

        public void Synapsys_ReadThread() //data
        {
            int getValueLength = 0;
            string getstring = null;

            while (true)
            {
                try
                {
                    socket.Receive(getbyte, 0, getbyte.Length, SocketFlags.None);
                    getValueLength = Synapsys_ByteArrayDefrag(getbyte);
                    getstring = Encoding.UTF8.GetString(getbyte, 0, getValueLength + 1);
                    Console.WriteLine(">>Receive Display Socket : " + getstring);
                    //Synaposys_Write(getstring);
                }
                catch (SocketException)
                {
                    Console.WriteLine(">>Display Receive ERROR : ");
                }
            }
        }
        public static int Synapsys_ByteArrayDefrag(byte[] sData)
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
