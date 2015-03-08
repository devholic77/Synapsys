using System.Windows;
using System.Threading;
using System.Drawing;
using System.Drawing.Imaging;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System;
using System.Text.RegularExpressions;

namespace Synapsys
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

		private void Button_Click(object sender, RoutedEventArgs e)
		{
			Popup popup = new Popup("KakaoTaaalk", "asdfasdf");

			//popup.Close();
			popup.Activate();
		}

		private void Button_Click2(object sender, RoutedEventArgs e)
		{

			//Console.WriteLine();

			new Thread(new ThreadStart(capture)).Start();
			//new Thread(new ThreadStart(trackMouse)).Start();
		}

		private string getCurrentMouse()
		{
			return Regex.Replace(Screen.FromPoint(new System.Drawing.Point(System.Windows.Forms.Cursor.Position.X,
					System.Windows.Forms.Cursor.Position.Y)).DeviceName,
					@"[^\d]", string.Empty);
		}

		private void capture()
		{

			int cnt = 1;
			int adder = 0;

			foreach(Screen scr in Screen.AllScreens)
			{
				Bitmap bmpScreenShot = new Bitmap(scr.Bounds.Width, scr.Bounds.Height, PixelFormat.Format32bppArgb);
				Graphics g = Graphics.FromImage(bmpScreenShot);
				g.CopyFromScreen(scr.Bounds.X, scr.Bounds.Y, 0, 0, scr.Bounds.Size, CopyPixelOperation.SourceCopy);



				CURSORINFO pci;
				pci.cbSize = System.Runtime.InteropServices.Marshal.SizeOf(typeof(CURSORINFO));

				if (GetCursorInfo(out pci))
				{
					if (pci.flags == CURSOR_SHOWING)
					{
						DrawIcon(g.GetHdc(), pci.ptScreenPos.x, pci.ptScreenPos.y, pci.hCursor);
						g.ReleaseHdc();
					}
				}





				bmpScreenShot.Save("c:\\" + cnt + "_" + adder + ".png", ImageFormat.Png);
				cnt++;
			}
		}


		[StructLayout(LayoutKind.Sequential)]
		struct CURSORINFO
		{
			public Int32 cbSize;
			public Int32 flags;
			public IntPtr hCursor;
			public POINTAPI ptScreenPos;
		}

		[StructLayout(LayoutKind.Sequential)]
		struct POINTAPI
		{
			public int x;
			public int y;
		}

		[DllImport("user32.dll")]
		static extern bool GetCursorInfo(out CURSORINFO pci);

		[DllImport("user32.dll")]
		static extern bool DrawIcon(IntPtr hDC, int X, int Y, IntPtr hIcon);

		const Int32 CURSOR_SHOWING = 0x00000001;
    }
	
}
