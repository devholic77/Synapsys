using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Microsoft.WindowsAPICodePack.Taskbar;
using System.IO;
using Microsoft.WindowsAPICodePack.Shell;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;

using System.Windows;
using System.Threading;


namespace Synapsys_Sub_Program
{


    public delegate void event_Chatting(int App_event_num, int App_id, String App_name, Icon App_Icon, Bitmap App_capture);

    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            TabbedThumbnail thumb = new TabbedThumbnail(this.Handle, this.Handle);
            TaskbarManager.Instance.TabbedThumbnail.AddThumbnailPreview(thumb);
            thumb.TabbedThumbnailClosed += new EventHandler<TabbedThumbnailClosedEventArgs>(Synapsys_AppDeleteEvent); // 앱 삭제 이벤트
            thumb.TabbedThumbnailActivated += new EventHandler<TabbedThumbnailEventArgs>(Synapsys_AppContextChangeEvent);
            thumb.Title = "Home"; // app title 
            thumb.SetWindowIcon(Synapsys_Sub_Program.Properties.Resources.image_main_icon);
            Synapsys_FileRead();
        }
        public byte[] Synapsys_intToByte(int id)
        {

            byte[] intBytes = BitConverter.GetBytes(id);
            if (BitConverter.IsLittleEndian)
                Array.Reverse(intBytes);
            byte[] result = intBytes;

            return result;
        }

        private void hscrTaskbarProgress_Scroll(object sender, ScrollEventArgs e)
        {
        }

        public void Clap(object sender, Synapsys_Thumbnail_Event e)
        {
            this.Invoke(new event_Chatting(Synapsys_New_Thumbnail), e.App_Event_Num, e.App_Id, e.App_Name, e.App_Icon, e.App_Thumbnail);
        }
        public void Synapsys_New_Thumbnail(int App_event_num, int App_id, String App_name, Icon App_Icon, Bitmap App_thumbnail)
        {

            switch (App_event_num)
            {
                case 1: // 최초
                    if (App_name.Equals("Launcher"))
                    {
                        this.pictureBox1.Image = App_thumbnail;
                        pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;
                        
                    }
                    else
                    {
                        Synapsys_Global_Value.Android_Thumbnail[Synapsys_Global_Value.Thumbnail_index] = new Synapsys_Android_Thumbnail(App_id, App_name, App_Icon, App_thumbnail);
                        Synapsys_Global_Value.Android_Thumbnail[Synapsys_Global_Value.Thumbnail_index].Show();
                        Synapsys_Global_Value.Thumbnail_index++;
                    }
                    break;
                case 2: // 직전
                    for (int i = 0; i <= Synapsys_Global_Value.Thumbnail_index; i++)
                    {
                        if (Synapsys_Global_Value.Android_Thumbnail[i].Synapsys_GetId() == App_id)
                        {
                            Synapsys_Global_Value.Android_Thumbnail[i].Synapsys_SetThumbnail(App_thumbnail);
                            
                            break;
                        }
                    }
                    break;
                case 3: // 삭제
                    for (int i = 0; i <= Synapsys_Global_Value.Thumbnail_index; i++)
                    {
                        if (Synapsys_Global_Value.Android_Thumbnail[i].Synapsys_GetId() == App_id)
                        {
                            Synapsys_Global_Value.Android_Thumbnail[i].Synapsys_App_Delete();
                            for (int j = i; j <= Synapsys_Global_Value.Thumbnail_index; j++ )
                                Synapsys_Global_Value.Android_Thumbnail[j] = Synapsys_Global_Value.Android_Thumbnail[j + 1];

                            Synapsys_Global_Value.Android_Thumbnail[Synapsys_Global_Value.Thumbnail_index] = null;
                            Synapsys_Global_Value.Thumbnail_index--;

                            break;
                        }
                    }
                    break;
                case 4: // 노티 //장대찬
                    break;
                default:
                    break;
            }

        }

        private void Form1_Load(object sender, EventArgs e)
        {
            string[] strArg = Environment.GetCommandLineArgs();

            Synapsys_Global_Value.Thumbnail_Socket = new Synapsys_Thumbnail_Socket();
            Synapsys_Global_Value.Thumbnail_Socket.Execute += new Synapsys_Thumbnail_Socket.execute(Clap);
            Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Socket_Init(strArg[1], "123");

            TabbedThumbnail thumb = new TabbedThumbnail(this.Handle, this.Handle);
            TaskbarManager.Instance.TabbedThumbnail.AddThumbnailPreview(thumb);
            thumb.TabbedThumbnailClosed += new EventHandler<TabbedThumbnailClosedEventArgs>(Synapsys_AppDeleteEvent); // 앱 삭제 이벤트
            thumb.TabbedThumbnailActivated += new EventHandler<TabbedThumbnailEventArgs>(Synapsys_AppContextChangeEvent);

            Console.WriteLine(strArg[1]);

            var enums = Enum.GetNames(typeof(TaskbarProgressBarState));
            Console.WriteLine("form load");


        }
        void Synapsys_AppContextChangeEvent(object sender, TabbedThumbnailEventArgs e)
        {

            byte[] result = { 0, 0, 0, 1, 0, 0, 0, 1 };
            Console.WriteLine("main click");
            Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Write(result);

        }

        void Synapsys_AppDeleteEvent(object sender, TabbedThumbnailClosedEventArgs e)
        {
            //        this.thumbButtonNext.Dispose();
            //          this.thumbButtonPrev.Dispose();
            //            TaskbarManager.Instance.TabbedThumbnail.RemoveThumbnailPreview(thumb);

            Console.WriteLine("딜리트");

            byte[] result = { 0, 0, 0, 3, 0, 0, 0, 1 };

            Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Write(result);

            Synapsys_App_Delete();
        }
        public void Synapsys_App_Delete()
        {
            this.Dispose();
            this.Close();
            // 이 다음줄에 명령어 보내기 -- 끄라는 명령어
        }

        private void Form1_Closed(object sender, EventArgs e)
        {
            // 이벤트 소켓 날리기
            Console.WriteLine("Form close");
        }
        private void Form1_Shown(object sender, EventArgs e)
        {

            TaskbarManager.Instance.SetOverlayIcon(this.Icon, "Taskbar Demo Application");

            JumpList list = JumpList.CreateJumpList();//JumpList.CreateJumpListForIndividualWindow("TaskbarDemo.Form1", this.Handle);
            JumpListCustomCategory jcategory = new JumpListCustomCategory("My New Category");

            list.ClearAllUserTasks();
            string desktop = Environment.GetFolderPath(Environment.SpecialFolder.Desktop);
            //jcategory.AddJumpListItems(new JumpListItem(Path.Combine(desktop, "a.abc")));
            list.AddCustomCategories(jcategory);

            string systemFolder = Environment.GetFolderPath(Environment.SpecialFolder.System);
            //Add links to Tasks
            list.AddUserTasks(new JumpListLink(Path.Combine(systemFolder, "notepad.exe"), "Open Notepad")
            {
                IconReference = new IconReference(Path.Combine(systemFolder, "notepad.exe"), 0)
            });
            list.AddUserTasks(new JumpListLink(Path.Combine(systemFolder, "calc.exe"), "Open Calculator")
            {
                IconReference = new IconReference(Path.Combine(systemFolder, "calc.exe"), 0)
            });
            list.AddUserTasks(new JumpListSeparator());
            list.AddUserTasks(new JumpListLink(Path.Combine(systemFolder, "mspaint.exe"), "Open Paint")
            {
                IconReference = new IconReference(Path.Combine(systemFolder, "mspaint.exe"), 0)
            });
            //Adding links to RecentItems
            //list.AddToRecent(Path.Combine(systemFolder, "mspaint.exe"));
            list.Refresh();
        }

        private void button1_Click(object sender, EventArgs e)
        {

        }

        private void Synapsys_FileRead()
        {
            String path = System.IO.Directory.GetCurrentDirectory();
            path = path.Substring(0, path.IndexOf("Synapsys"));
            path = @path + "portdefine.txt";
            String text = System.IO.File.ReadAllText(path);
            Console.WriteLine(text);

            text = text.Replace("\r\n", " ");
            String[] result = text.Split(' ');

            //result[0] = display socket port
            //result[1] = data socket port
            //result[2] = thumbnail socket port 
            //result[3] = device name

            if (result[4].Equals("Synapsys"))
            {
                Synapsys_Global_Value.Thumbnail_Socket = new Synapsys_Thumbnail_Socket();
                Synapsys_Global_Value.Thumbnail_Socket.Execute += new Synapsys_Thumbnail_Socket.execute(Clap);
                Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Socket_Init("1236", "123");

                //Synapsys_Global_Value.Display_Socket = new Synapsys_Display_Socket(result[0], result[3]);
                //Synapsys_Global_Value.Data_Socket = new Synapsys_Data_Socket(result[1], result[3]);
                //  Synapsys_Global_Value.Thumbnail_Socket = new Synapsys_Thumbnail_Socket("1236", result[3]);


            }
        }
    }
}
