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
			scrs = Screen.AllScreens;
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

			new Thread(new ThreadStart(captureMachine)).Start();
			//new Thread(new ThreadStart(trackMouse)).Start();
		}

		// 마우스 포인터가 위치한 모니터 번호를 리턴
		private string getCurrentMonitor()
		{
			return Regex.Replace(Screen.FromPoint(new System.Drawing.Point(System.Windows.Forms.Cursor.Position.X,
					System.Windows.Forms.Cursor.Position.Y)).DeviceName,
					@"[^\d]", string.Empty);
		}

		private void captureMachine()
		{
			while(true)
			{
				capture();
				Thread.Sleep(33);
			}
		}

		// 화면을 캡쳐
		private Bitmap [,]bitmapArray = new Bitmap[MAX_MONITOR,2];
		private bool captSwitch = true;
		private const int MAX_MONITOR = 3;
		private static Bitmap bmpScreenShot;
		private static Graphics g;
		private static Screen []scrs;

		private void capture()
		{
			
			string currentMonitor;

			foreach(Screen scr in scrs)
			{
				if (scr == null)
					continue;

				currentMonitor = Regex.Replace(scr.DeviceName, @"[^\d]", string.Empty);

				try
				{
					bmpScreenShot = new Bitmap(scr.Bounds.Width, scr.Bounds.Height, PixelFormat.Format24bppRgb);

					g = Graphics.FromImage(bmpScreenShot);
					g.CopyFromScreen(scr.Bounds.X, scr.Bounds.Y, 0, 0, scr.Bounds.Size, CopyPixelOperation.SourceCopy);
				
					// 차영상
					if (captSwitch)
					{
						bitmapArray[Convert.ToInt32(currentMonitor), 0] = bmpScreenShot;
					}
					else
					{
						bitmapArray[Convert.ToInt32(currentMonitor), 1] = bmpScreenShot;
						if(compare(bitmapArray[Convert.ToInt32(currentMonitor), 0],bitmapArray[Convert.ToInt32(currentMonitor), 1]))
						{
							// 바뀐게 없다
							//Console.WriteLine(currentMonitor + ": 바뀐게 없다");
						}
						else
						{
							// 바뀌었다
							Console.WriteLine(currentMonitor + ": Changed");
						}
					}



					// 현재 모니터에 마우스 드로잉
					if(currentMonitor.Equals(getCurrentMonitor()))
					{
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
					}

					//bmpScreenShot.Save("c:\\" + currentMonitor + ".jpg", ImageFormat.Jpeg);
				}
				catch { }
			}

			captSwitch = !captSwitch;
			GC.Collect();
		}

		private bool compare(Bitmap bmp1, Bitmap bmp2)
		{
			// false : 바뀌었다
			// true : 바뀐게 없다.

			if (bmp1 == null || bmp2 == null)
				return false;

			bool equals = true;
			Rectangle rect = new Rectangle(0, 0, bmp1.Width, bmp1.Height);
			BitmapData bmpData1 = bmp1.LockBits(rect, ImageLockMode.ReadOnly, bmp1.PixelFormat);
			BitmapData bmpData2 = bmp2.LockBits(rect, ImageLockMode.ReadOnly, bmp2.PixelFormat);
			unsafe
			{
				byte* ptr1 = (byte*)bmpData1.Scan0.ToPointer();
				byte* ptr2 = (byte*)bmpData2.Scan0.ToPointer();
				int width = rect.Width * 3; // for 24bpp pixel data
				for (int y = 0; equals && y < rect.Height; y++)
				{
					for (int x = 0; x < width; x++)
					{
						if (*ptr1 != *ptr2)
						{
							equals = false;
							break;
						}
						ptr1++;
						ptr2++;
					}
					ptr1 += bmpData1.Stride - width;
					ptr2 += bmpData2.Stride - width;
				}
			}

			bmp1.UnlockBits(bmpData1);
			bmp2.UnlockBits(bmpData2);

			return equals;
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
