using System;
using System.Threading;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;

namespace Synapsys
{
	class CaptureScreen
	{

		private static CaptureScreen captureScreen = null;

		private static Thread thread = null;
		private static Thread gcThread = null;
		private static int FPS = 100;

		private static Bitmap[,] bitmapArray = new Bitmap[MAX_MONITOR, 2];
		private static int MAX_MONITOR = 3;
		private static Screen[] scrs;

		public static int currFPS = 0;
		public static int totFPS = 0;


		// 싱글톤
		public static CaptureScreen getInstance()
		{
			if (captureScreen == null)
			{
				captureScreen = new CaptureScreen();
			}
			return captureScreen;
		}

		// 디바이스 목록을 새로고침한다. 새로 기기가 연결된 경우 실행
		public static void Refresh()
		{
			totalMonitor = Screen.AllScreens.Length;
			if(beforeTotalMonitor != totalMonitor)
			{
				scrs = Screen.AllScreens;
				beforeTotalMonitor = totalMonitor;

				if(beforeTotalMonitor == 3)
				{
					MainWindow.DEVICE1_MARGIN = Screen.AllScreens[1].Bounds.Top;
					MainWindow.DEVICE2_MARGIN = Screen.AllScreens[2].Bounds.Top;
					Console.WriteLine(MainWindow.DEVICE1_MARGIN);
					Console.WriteLine(MainWindow.DEVICE2_MARGIN);
				} else if(beforeTotalMonitor == 2)
				{
					MainWindow.DEVICE1_MARGIN = Screen.AllScreens[1].Bounds.Top;
					Console.WriteLine(MainWindow.DEVICE1_MARGIN);
				}
			}
		}

		// 캡쳐를 시작한다.
		public void Start()
		{
			Stop();
			setFPS(25);

			thread = new Thread(new ThreadStart(captureMachine));
			thread.Start();
			gcThread = new Thread(new ThreadStart(gcMachine));
			gcThread.Start();

			//new Thread(new ThreadStart(gcMachine2)).Start();
		}

		public void Stop()
		{
			if (thread != null && thread.IsAlive)
			{
				try
				{
					thread.Interrupt();
					thread = null;

					gcThread.Interrupt();
					gcThread = null;
				}
				catch (Exception)
				{

				}
			}
		}

		// FPS를 정한다
		public static void setFPS(int a)
		{
			FPS = 1000 / a;
		}

		// 마우스 포인터가 위치한 모니터 번호를 리턴
		public static string getCurrentMonitor()
		{
			return Regex.Replace(Screen.FromPoint(new System.Drawing.Point(System.Windows.Forms.Cursor.Position.X,
					System.Windows.Forms.Cursor.Position.Y)).DeviceName,
					@"[^\d]", string.Empty);
		}

		private static void gcMachine()
		{
			while(true)
			{
				try
				{
					Console.WriteLine("Current FPS : " + currFPS);
					totFPS += currFPS;
					//Console.WriteLine("OK FPS : " + SynapsysSocket.currOK);
					currFPS = 0;
					//SynapsysSocket.currOK = 0;
					//GC.Collect();
					//Refresh();
					Thread.Sleep(1000);
				}
				catch (Exception)
				{

				}
				
			}
		}

		private static void captureMachine()
		{
			Refresh();
			while (true)
			{
				try
				{
					new Thread(new ThreadStart(capture)).Start();
					Thread.Sleep(FPS);
				}
				catch (Exception)
				{
					break;
				}
			}
		}

		// 화면을 캡쳐

		static int totalMonitor = 0;
		static int beforeTotalMonitor = 0;
		static int MONITOR_WIDTH = MainWindow.WIDTH;
		static int MONITOR_HEIGHT = MainWindow.HEIGHT;

		private static void capture()
		{
			currFPS++;
			try
			{
				int SUBSCREEN = (Screen.AllScreens.Length - 1);
				using (Bitmap bitmap = new Bitmap(MONITOR_WIDTH * SUBSCREEN, MONITOR_HEIGHT))
				{
					using (Graphics g = Graphics.FromImage(bitmap))
					{
						g.CopyFromScreen(new Point(-MONITOR_WIDTH * SUBSCREEN, 0), Point.Empty, new Size(MONITOR_WIDTH * SUBSCREEN, MONITOR_HEIGHT));
					}
					// #1
					Rectangle rect = new Rectangle(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);
					if(SUBSCREEN >= 1)
					{
						using (Bitmap firstHalf = bitmap.Clone(rect, bitmap.PixelFormat))
						{
							using (MemoryStream stream = new MemoryStream())
							{
								firstHalf.Save(stream, ImageFormat.Jpeg);
								MainWindow.socketIMG1.SendScreen(stream.ToArray());
								stream.Close();
							}
						}
					}

					// #2					
					if(SUBSCREEN >= 2)
					{
						rect = new Rectangle(MONITOR_WIDTH, 0, MONITOR_WIDTH, MONITOR_HEIGHT);
						using (Bitmap secondHalf = bitmap.Clone(rect, bitmap.PixelFormat))
						{
							using (MemoryStream stream = new MemoryStream())
							{
								secondHalf.Save(stream, ImageFormat.Jpeg);
								MainWindow.socketIMG2.SendScreen(stream.ToArray());
								stream.Close();
							}
						}
					}
					
					
				}
			}
			catch (Exception)
			{
			}
			
		}

		private static bool compare(Bitmap bmp1, Bitmap bmp2)
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
				int width = rect.Width * 2; // for 16bpp pixel data
				for (int y = 0; equals && y < rect.Height; y+=2)
				{
					for (int x = 0; x < width; x+=2)
					{
						if (*ptr1 != *ptr2)
						{
							equals = false;
							break;
						}
						ptr1+=2;
						ptr2+=2;
					}
					ptr1 += bmpData1.Stride - width;
					ptr2 += bmpData2.Stride - width;
				}
			}

			bmp1.UnlockBits(bmpData1);
			bmp2.UnlockBits(bmpData2);

			bmpData1 = null; bmpData2 = null;
			return equals;
		}

		//[StructLayout(LayoutKind.Sequential)]
		//struct CURSORINFO
		//{
		//	public Int32 cbSize;
		//	public Int32 flags;
		//	public IntPtr hCursor;
		//	public POINTAPI ptScreenPos;
		//}

		//[StructLayout(LayoutKind.Sequential)]
		//struct POINTAPI
		//{
		//	public int x;
		//	public int y;
		//}

		//[DllImport("user32.dll")]
		//static extern bool GetCursorInfo(out CURSORINFO pci);

		//[DllImport("user32.dll")]
		//static extern bool DrawIcon(IntPtr hDC, int X, int Y, IntPtr hIcon);

		//const Int32 CURSOR_SHOWING = 0x00000001;
	}
}
