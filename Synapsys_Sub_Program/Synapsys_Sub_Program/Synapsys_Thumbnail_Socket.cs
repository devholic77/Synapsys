using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
using System.Drawing;
using System.Windows;
using System.Windows.Media.Imaging;




namespace Synapsys_Sub_Program
{
    public class Synapsys_Thumbnail_Socket
    {
        int sPort;
        String d_num;
        Thread ReadThread;
        byte[] buffers;
        byte[] buffers1;
        int index = 0;


        byte[] buffer = new byte[80000];
        byte[] read_header = new byte[20];


        public Synapsys_Thumbnail_Socket()
        {



        }
        ~Synapsys_Thumbnail_Socket()
        {
            if (ReadThread != null)
            {
                Console.WriteLine("Read Thread 종료");
                ReadThread.Interrupt();
            }
            if (socket != null)
                socket.Close();
        }

        public static Socket socket;
        public static byte[] getbyte = new byte[30000];
        public static byte[] setbyte = new byte[80000];


        public bool Synapsys_Socket_Init(String port, String device_num)
        {
            sPort = Convert.ToInt32(port);
            d_num = device_num;

            Console.WriteLine(">> Thumbnail_Socket_Init Start : " + sPort);
            IPAddress serverIP = IPAddress.Parse("127.0.0.1");
            IPEndPoint serverEndPoint = new IPEndPoint(serverIP, sPort);
            try
            {
                socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socket.Connect(serverEndPoint);


                if (socket.Connected)
                {
                    int getValueLength = 0;

                    Console.WriteLine(">> Thumbnail_Socket Start");
                    //Synaposys_Write("01:02:03:04\n");
                    ReadThread = new Thread(new ThreadStart(Synapsys_ReadThread));
                    ReadThread.Start();

                }

            }
            catch (SocketException)
            {
                if (ReadThread != null)
                    ReadThread.Abort();// thread 종료
                Console.WriteLine("Thumbnail  Socket Error ");
                socket.Close();
                return false;
            }
            return true;
        }


        public bool Synapsys_1_State(int App_ID, int AppNameSize, int IconSize, int ThumbnailSize) //최초
        {
            
            byte[] str_name = new byte[AppNameSize]; // 장대찬 소켓 ㅁㄴㅇㅇㅁㄴㅇ
            if (!Synaposys_ReadFully(str_name))
            {
                ReadThread.Interrupt();
                return false;
            }
                
            String App_name = Encoding.UTF8.GetString(str_name);



            byte[] App_icon_byte = new byte[IconSize];
            Synaposys_ReadFully(App_icon_byte);

            Bitmap App_icon_bitmap = ConvertToBitmap(App_icon_byte);
            Icon App_Icon = Icon.FromHandle(App_icon_bitmap.GetHicon());

            Bitmap App_thumbnail;

            if(ThumbnailSize != 0)
            { 
                byte[] App_thumbnail_byte = new byte[ThumbnailSize];
                Synaposys_ReadFully(App_thumbnail_byte);
                App_thumbnail = ConvertToBitmap(App_thumbnail_byte);
            }
            else
            {
                App_thumbnail = global::Synapsys_Sub_Program.Properties.Resources.min_1;
            }


            for (int i = 0; i < Synapsys_Global_Value.Thumbnail_index; i++)
            {
                if (Synapsys_Global_Value.Android_Thumbnail[i].Synapsys_GetId().Equals(App_ID))
                {
                    return false;
                }
            }

            if (Execute != null) // 이벤트가 발생하면
            {
                Synapsys_Thumbnail_Event e = new Synapsys_Thumbnail_Event();
                e.App_Event_Num = 1;

                e.App_Icon = App_Icon;
                e.App_Thumbnail = App_thumbnail;
                e.App_Id = App_ID;
                e.App_Name = App_name;

                Execute(this, e);  // 이벤트 실행. this는 이 객체를 말하는것.
            }
            return true;
        }
        public delegate void execute(object sender, Synapsys_Thumbnail_Event e); // 델리게이트 선언. 이 형식에 맞춰 이벤트 적용 함수 만들어야함.
        public event execute Execute; // 이벤트 선언

        public bool Synapsys_2_State(int App_ID, int AppNameSize, int IconSize, int ThumbnailSize) //직전
        {

            byte[] str_name = new byte[AppNameSize];
             if (!Synaposys_ReadFully(str_name))
            {
                ReadThread.Interrupt();
                return false;
            }
            String App_name = Encoding.UTF8.GetString(str_name);
            byte[] App_icon_byte = new byte[IconSize];
            Synaposys_ReadFully(App_icon_byte);

            byte[] App_thumbnail_byte = new byte[ThumbnailSize];
            Synaposys_ReadFully(App_thumbnail_byte);

            Bitmap App_thumbnail = ConvertToBitmap(App_thumbnail_byte);

            if (Execute != null) // 이벤트가 발생하면
            {
                Synapsys_Thumbnail_Event e = new Synapsys_Thumbnail_Event();
                e.App_Event_Num = 2;

                e.App_Thumbnail = App_thumbnail;
                e.App_Id = App_ID;
                e.App_Name = App_name;

                Execute(this, e);  // 이벤트 실행. this는 이 객체를 말하는것.
            }
            return true;
        }
        public void Synapsys_3_State(int App_ID) //삭제
        {

            if (Execute != null) // 이벤트가 발생하면
            {
                Synapsys_Thumbnail_Event e = new Synapsys_Thumbnail_Event();
                e.App_Event_Num = 3;
                e.App_Id = App_ID;

                Execute(this, e);  // 이벤트 실행. this는 이 객체를 말하는것.
            }
        }
        public void Synapsys_4_State() //노티 장대찬
        {

        }
        public int[] Synapsys_Head_Analysis(byte[] read_header)
        {
            int[] b = new int[5];

            byte[] a = new byte[4];

            for (int i = 0; i < 4; i++)
                a[i] = read_header[i];

            if (BitConverter.IsLittleEndian)
                Array.Reverse(a);

            b[0] = BitConverter.ToInt32(a, 0);
            //Console.WriteLine("int: {1}", b[0]);
            // Output: int: 25



            for (int i = 0; i < 4; i++)
                a[i] = read_header[i + 4];


            if (BitConverter.IsLittleEndian)
                Array.Reverse(a);

            b[1] = BitConverter.ToInt32(a, 0);
            //Console.WriteLine("int: {2}", b[1]);
            // Output: int: 25

            for (int i = 0; i < 4; i++)
                a[i] = read_header[i + 8];


            if (BitConverter.IsLittleEndian)
                Array.Reverse(a);

            b[2] = BitConverter.ToInt32(a, 0);
            //Console.WriteLine("int: {3}", b[2]);
            // Output: int: 25

            for (int i = 0; i < 4; i++)
                a[i] = read_header[i + 12];


            if (BitConverter.IsLittleEndian)
                Array.Reverse(a);

            b[3] = BitConverter.ToInt32(a, 0);
            //Console.WriteLine("int: {4}", b[3]);

            for (int i = 0; i < 4; i++)
                a[i] = read_header[i + 16];


            if (BitConverter.IsLittleEndian)
                Array.Reverse(a);

            b[4] = BitConverter.ToInt32(a, 0);
            //Console.WriteLine("int: {4}", b[3]);



            return b;

        }
        public void Synapsys_Write(byte[] WriteMsg)
        {
            NetworkStream ns = new NetworkStream(socket);
            StreamWriter sw = new StreamWriter(ns);

            try
            {
                if (WriteMsg != null)
                {
                    //setbyte = Encoding.UTF8.GetBytes(WriteMsg);
                    //sw.Write(WriteMsg);

                    //sw.Flush();
                    socket.Send(WriteMsg, 0, WriteMsg.Length, SocketFlags.None);
                }
            }
            catch (SocketException)
            {
                Console.WriteLine(">>Thumbnail Send ERROR : ");
            }
        }

        public static bool Synaposys_ReadFully(byte[] buffer)
        {
            int offset = 0;
            int readBytes;

            do
            {
                // If you are using Socket directly instead of a Stream:
                readBytes = socket.Receive(buffer, offset, buffer.Length - offset, SocketFlags.None);

                //readBytes = stream.Read(buffer, offset, buffer.Length - offset);
                offset += readBytes;
            } while (readBytes > 0 && offset < buffer.Length);

            if (offset < buffer.Length)
            {
                Console.WriteLine("에러");
                return false;
                throw new EndOfStreamException();

            }
            Console.WriteLine("리드풀리 끝");
            return true;
        }
        //myNetworkStream.ReadFully(buffer);


        public void Synapsys_ReadThread() //data
        {
            int getValueLength = 0;
            string getstring = null;

            while (true)
            {
                try
                {

                    int[] head_msg;

                    socket.Receive(read_header, 0, read_header.Length, SocketFlags.None);

                    head_msg = Synapsys_Head_Analysis(read_header);


                    switch (head_msg[0])
                    {
                        case 1: // 최초
                            Console.WriteLine("최초");

                            if (!Synapsys_1_State(head_msg[1], head_msg[2], head_msg[3], head_msg[4]))
                                return;
                            break;
                        case 2: // 직전
                            Console.WriteLine("직전");
                            if (!Synapsys_2_State(head_msg[1], head_msg[2], head_msg[3], head_msg[4]))
                                return;
                            break;
                        case 3: // 삭제
                            Console.WriteLine("삭제");
                            Synapsys_3_State(head_msg[1]);
                            break;
                        case 4: // Noti
                            Console.WriteLine("노티");
                            Synapsys_4_State();
                            break;
                        default:
                            //Console.WriteLine("이상한 데이터가 넘어옴");
                            break;
                    }
                    //getstring = Encoding.UTF8.GetString(getbyte, 0, getValueLength + 1);
                    //Console.WriteLine(">>Receive Thumbnail_Socket : " + getstring);
                }
                catch (SocketException)
                {
                    Console.WriteLine(">>Thumbnail Receive ERROR : ");
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

        private static Bitmap ConvertToBitmap(byte[] imagesSource)
        {
            var imageConverter = new ImageConverter();
            var image = (Image)imageConverter.ConvertFrom(imagesSource);
            return new Bitmap(image);
        }
        


    }
}
