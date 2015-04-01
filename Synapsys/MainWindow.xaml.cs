using System.Windows;
using System.Threading;
using System.Drawing;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System;
using System.Text.RegularExpressions;
using System.Windows.Interop;
using Synapsys_ADB;

namespace Synapsys
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Elysium.Controls.Window
    {
        public MainWindow()
        {
            InitializeComponent();
			scrs = Screen.AllScreens;
			addItem_Listbox("Synapsys Started");
			addItem_Listbox("Please Connect devices..");

			btn_d1_start.IsEnabled = false;
			btn_d1_stop.IsEnabled = false;
			btn_d2_start.IsEnabled = false;
			btn_d2_stop.IsEnabled = false;

			//KEYBOARD, MOUSE HOOK
			KeyboardMouse kb = KeyboardMouse.getInstance();
			kb.Activate();

			Form1 form = new Form1();
			form.Visible = false;
			form.Execute += new Form1.execute(Clap);
			form.Show();
        }


		private void btn1_start(object sender, RoutedEventArgs e)
		{
			if (Synapsys_Values.Add_device[1].Equals(""))
			{
				Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.Add_device[0]);
			}
			else
			{
				Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.Add_device[0]);
				Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.Add_device[1]);
			}
			Synapsys_Values.Add_device[0] = "";
			Synapsys_Values.Add_device[1] = "";
		}

		#region ADB

		void Clap(object sender, ConnEvent e) // 이벤트 발생시 실행하고픈 함수. 델리게이트 선언의 파라미터를 따라갸아 한다.
		{
			Console.WriteLine("Clap");
			Console.WriteLine(e.Device);
			Console.WriteLine(e.Message);
			btn_d1_start.Dispatcher.Invoke(new update1Callback(this.update3), "1");
		}

		private void update3(string s)
		{
			if (s.Equals("1"))
			{
				btn_d1_start.IsEnabled = true;
			}
		}

		#endregion

		private void Setting_Click(object sender, RoutedEventArgs e)
		{
			Popup_settings.IsOpen = true;
		}

		private void addItem_Listbox(string s)
		{
			Listbox1.Items.Add(s);
			Listbox1.Items.MoveCurrentToLast();
			Listbox1.UpdateLayout();
		}

		private void Button_Click(object sender, RoutedEventArgs e)
		{
			
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

		public Popup popup = new Popup();

		private void Button_Click_1(object sender, RoutedEventArgs e)
		{

			string title = "KakaoTalk KakaoTalk KakaoTalk KakaoTalk KakaoTalk";
			string message = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리 나라 만세 무궁화 삼천리 화려강산 대한사람 대한으로";
			
			//Popup popup = new Popup();
			//popup.Set(title, message);
			//popup.AnimateWindow();
			////popup.Close();

			TaskbarPopup popup = new TaskbarPopup();
			popup.Gogo(title, message);

		}

		private void Button_Click_2(object sender, RoutedEventArgs e)
		{
			//KeyboardHook key = KeyboardHook.getInstance();
			//key.Run();

			//Hooker manager = new Hooker();
			//manager.Add();

			
		}

		private void btn_Close_Click(object sender, RoutedEventArgs e)
		{
			Popup_settings.IsOpen = false;
		}

		private void checkbox_handler(object sender, RoutedEventArgs e)
		{
			if(sender.Equals(checkbox1))
			{
				if (checkbox1.IsChecked.Value)
				{
					nowCollecting1 = true;
					KeyboardMouse.m_KeyboardHookManager.KeyUp += HookManager_KeyUp1;
					new Thread(Keyup_Collector1).Start();
				}
			}

			if (sender.Equals(checkbox2))
			{
				if (checkbox2.IsChecked.Value)
				{
					nowCollecting2 = true;
					KeyboardMouse.m_KeyboardHookManager.KeyUp += HookManager_KeyUp2;
					new Thread(Keyup_Collector2).Start();
				}
			}
		}

		private void HookManager_KeyUp1(object sender, KeyEventArgs e)
		{
			if(nowCollecting1)
			{
				Keyup_Collector_string1 += e.KeyCode + " + ";
			}
				
		}
		private void HookManager_KeyUp2(object sender, KeyEventArgs e)
		{
			if (nowCollecting2)
			{
				Keyup_Collector_string2 += e.KeyCode + " + ";
			}

		}

		private static string Keyup_Collector_string1;
		private static string Keyup_Collector_string2;
		private static bool nowCollecting1 = false;
		private static bool nowCollecting2 = false;

		public delegate void update1Callback(string s);

		private void Keyup_Collector1()
		{
			Thread.Sleep(1500);
			nowCollecting1 = false;
			KeyboardMouse.m_KeyboardHookManager.KeyUp -= HookManager_KeyUp1;
			
			checkbox1.Dispatcher.Invoke(new update1Callback(this.update1), Keyup_Collector_string1);
			Keyup_Collector_string1 = "";
		}

		private void update1(string s)
		{
			checkbox1.Content = s.Substring(0, s.Length - 3);
		}

		private void update2(string s)
		{
			checkbox2.Content = s.Substring(0, s.Length - 3);
		}

		private void Keyup_Collector2()
		{
			Thread.Sleep(1500);
			nowCollecting2 = false;
			KeyboardMouse.m_KeyboardHookManager.KeyUp -= HookManager_KeyUp2;

			checkbox1.Dispatcher.Invoke(new update1Callback(this.update2), Keyup_Collector_string2);
			Keyup_Collector_string2 = "";
		}


		
    }
	
}
