using System;
using System.Threading;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Drawing;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
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

		//private static System.Drawing.Imaging.Encoder myEncoder = System.Drawing.Imaging.Encoder.Quality;
		//private static EncoderParameter myEncoderParameter = new EncoderParameter(myEncoder, 50L);
		//private static EncoderParameters myEncoderParameters = new EncoderParameters(1);



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
			}
		}

		// 캡쳐를 시작한다.
		public void Start()
		{
			Stop();
			setFPS(25);

			Refresh();
			thread = new Thread(new ThreadStart(captureMachine));
			thread.Start();
			gcThread = new Thread(new ThreadStart(gcMachine));
			gcThread.Start();

			new Thread(new ThreadStart(gcMachine2)).Start();
		}

		public void Stop()
		{
			if (thread != null && thread.IsAlive)
			{
				thread.Interrupt();
				thread = null;

				gcThread.Interrupt();
				gcThread = null;
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
				Console.WriteLine("Current FPS : " + currFPS);
				//Console.WriteLine("OK FPS : " + SynapsysSocket.currOK);
				currFPS = 0; 
				//SynapsysSocket.currOK = 0;
				//GC.Collect();
				//Refresh();
				Thread.Sleep(1000);
			}
		}

		private static void gcMachine2()
		{
			while (true)
			{
				GC.Collect();
				Thread.Sleep(500);
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
					//capture();
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

		private static void capture()
		{
			try
			{
				for (int i = 1; i < beforeTotalMonitor; i++)
				{
					Screen scr = scrs[i];

					//Bitmap bmpScreenShot1 = new Bitmap(scr.Bounds.Width, scr.Bounds.Height, PixelFormat.Format16bppRgb555);
					Bitmap bmpScreenShot2 = new Bitmap(scr.Bounds.Width, scr.Bounds.Height, PixelFormat.Format16bppRgb555);

					//Graphics g1 = Graphics.FromImage(bmpScreenShot1);
					//g1.CopyFromScreen(scr.Bounds.X, scr.Bounds.Y, 0, 0, scr.Bounds.Size, CopyPixelOperation.SourceCopy);
					Graphics g2 = Graphics.FromImage(bmpScreenShot2);
					g2.CopyFromScreen(scr.Bounds.X, scr.Bounds.Y, 0, 0, scr.Bounds.Size, CopyPixelOperation.SourceCopy);
					currFPS++;

					scr = null; 
					//g1 = null; 
					g2 = null;

					//if(!compare(bmpScreenShot1, bmpScreenShot2))
					//{
						// Change to Byte Array
						MemoryStream stream = new MemoryStream();
						bmpScreenShot2.Save(stream, System.Drawing.Imaging.ImageFormat.Jpeg);

					if(i == 1)
						MainWindow.socketIMG1.SendScreen(stream.ToArray());
					else if (i == 2)
						MainWindow.socketIMG2.SendScreen(stream.ToArray());

						stream.Close();

						stream = null;
					//}
					
					//bmpScreenShot1 = null; 
					bmpScreenShot2 = null;
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
