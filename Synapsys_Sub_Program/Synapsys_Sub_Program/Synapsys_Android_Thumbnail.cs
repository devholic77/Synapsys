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
using Microsoft.WindowsAPICodePack;
using Microsoft.WindowsAPICodePack.Shell;
using System.Windows;


namespace Synapsys_Sub_Program
{

    public partial class Synapsys_Android_Thumbnail : Form
    {
        String App_name = "";
        int App_id = 0;
        Icon App_Icon;
        Bitmap App_Thumbnail;
        int num = 1;
        TabbedThumbnail thumb;

        public Synapsys_Android_Thumbnail(int App_id, String App_name, Icon App_Icon, Bitmap App_thumbnail)
        {
            InitializeComponent();
            this.App_name = App_name;
            this.App_id = App_id;
            this.App_Icon = App_Icon;
            this.App_Thumbnail = App_thumbnail;

        }

        public Synapsys_Android_Thumbnail(int num)
        {
            InitializeComponent();
            //this.Opacity = 0;
            this.num = num;
        }
        public Synapsys_Android_Thumbnail(Bitmap asdf)
        {
            InitializeComponent();
            this.App_Thumbnail = asdf;

            //this.Opacity = 0;
        }
        public Synapsys_Android_Thumbnail()
        {
            InitializeComponent();
            //this.Opacity = 0;
        }
        void Synapsys_AppContextChangeEvent(object sender, TabbedThumbnailEventArgs e)
        {
            //context 변경 메세지를 보내는 코드 
            Console.WriteLine("context 변경 클릭 이벤트");

            byte[] state = Synapsys_intToByte(1);
            byte[] id_byte = Synapsys_intToByte(this.App_id);
            byte[] result = new byte[8];

            for (int i = 0; i < 4; i++)
                result[i] = state[i];

            for (int i = 0; i < 4; i++)
                result[i + 4] = id_byte[i];

            for (int i = 0; i < 8; i++ )
                Console.WriteLine(result[i]);

            Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Write(result);
        }

        public byte[] Synapsys_intToByte(int id)
        {

            byte[] intBytes = BitConverter.GetBytes(id);
            if (BitConverter.IsLittleEndian)
                Array.Reverse(intBytes);
            byte[] result = intBytes;


            return result;
        }
        void Synapsys_AppDeleteEvent(object sender, TabbedThumbnailClosedEventArgs e)
        {
            //        this.thumbButtonNext.Dispose();
            //          this.thumbButtonPrev.Dispose();
            //            TaskbarManager.Instance.TabbedThumbnail.RemoveThumbnailPreview(thumb);
            Console.WriteLine("딜리트");

            byte[] state = Synapsys_intToByte(3);
            byte[] id_byte = Synapsys_intToByte(this.App_id);
            byte[] result = new byte[8];

            for (int i = 0; i < 4; i++)
                result[i] = state[i];

            for (int i = 0; i < 4; i++)
                result[i + 4] = id_byte[i];

            Synapsys_Global_Value.Thumbnail_Socket.Synapsys_Write(result);

            Synapsys_App_Delete();
        }
        public void Synapsys_App_Delete()
        {
            this.Dispose();
            this.Close();
            // 이 다음줄에 명령어 보내기 -- 끄라는 명령어
        }

        private void Synapsys_Thumbnail_Shown(object sender, EventArgs e)
        {

            //this.CancelButton.PerformClick();// += new EventHandler<CancelEventArgs>(asdf);
            thumb = new TabbedThumbnail(this.Handle, this.Handle);

            //thumb.SetImage(Properties.Resources.min_1); // app image
            thumb.SetWindowIcon(this.Icon); // app icon

            /*
            thumb.Title = App_name; // app title 
            thumb.SetImage(App_capture); // app image
            IntPtr Hicon = App_Icon.GetHicon();
            Icon myIcon = Icon.FromHandle(Hicon);
            thumb.SetWindowIcon(myIcon); // app icon
            */

            //thumb.Tooltip = "aaa";
            //thumb.Dispose();
            //thumb.TitleChanged(this.Icon,"ASDf");
            TaskbarManager.Instance.TabbedThumbnail.AddThumbnailPreview(thumb);
            thumb.TabbedThumbnailClosed += new EventHandler<TabbedThumbnailClosedEventArgs>(Synapsys_AppDeleteEvent); // 앱 삭제 이벤트
            thumb.TabbedThumbnailActivated += new EventHandler<TabbedThumbnailEventArgs>(Synapsys_AppContextChangeEvent);
            //TaskbarManager.Instance.TabbedThumbnail.RemoveThumbnailPreview(thumb);
            //TaskbarManager.Instance.ThumbnailToolBars.AddButtons(this.Handle, this.thumbButtonPrev, this.thumbButtonNext);

            App_thumbnail_picturebox.Image = App_Thumbnail;
            thumb.SetWindowIcon(App_Icon);
            App_thumbnail_picturebox.SizeMode = PictureBoxSizeMode.StretchImage;

            App_thumbnail_picturebox_SizeChanged(this.App_thumbnail_picturebox, null);
            thumb.Title = App_name; // app title 


        }

        private void App_thumbnail_picturebox_SizeChanged(object sender, EventArgs e)
        {
            //TaskbarManager.Instance.TabbedThumbnail.SetThumbnailClip(this.Handle, new Rectangle(this.pictureBox1.Location, this.pictureBox1.Size));

        }

        public int Synapsys_GetId()
        {
            return App_id;
        }
        public void Synapsys_SetThumbnail(Bitmap bmp)
        {
            App_thumbnail_picturebox.Image = bmp;
            App_thumbnail_picturebox.SizeMode = PictureBoxSizeMode.StretchImage;
        }
        public void Synspsys_Delete()
        {
            this.Dispose();
        }
    }
}
