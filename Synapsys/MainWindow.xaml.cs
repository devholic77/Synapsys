using System.Windows;
using System.Threading;

using System.Runtime.InteropServices;
using System.Windows.Forms;
using System;
using System.Text.RegularExpressions;
using System.Windows.Interop;
using Synapsys_ADB;
using System.Collections;

using System.ComponentModel;

namespace Synapsys
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>\
	/// 
	/// Daechan!!!!
	/// 
    public partial class MainWindow : Elysium.Controls.Window
    {
		private static KeyboardMouse kb = null;
		private static CaptureScreen cs = null;

		//HOTKEY
		string collectedHotkey = "";
		ArrayList tempHotkeyList, HotkeyList;

        public MainWindow()
        {
            InitializeComponent();

			Closing += new CancelEventHandler(Exit);

			Show_Log("Synapsys Started");
			Show_Log("Please Connect devices..");

			// Deactivate Buttons
			btn_d1_start.IsEnabled = false;
			btn_d1_stop.IsEnabled = false;
			btn_d2_start.IsEnabled = false;
			btn_d2_stop.IsEnabled = false;

			//KEYBOARD, MOUSE HOOK
			kb = KeyboardMouse.getInstance();
			kb.Activate();

			HotkeyList = new ArrayList();
			tempHotkeyList = new ArrayList();
			KeyboardMouse.m_KeyboardHookManager.KeyUp += Hotkey;

			cs = CaptureScreen.getInstance();
			//cs.Start();

            ADB_Form form = new ADB_Form();
            form.Visible = false;
            form.Execute += new ADB_Form.execute(Clap);
            form.Show();

			new Thread(new ThreadStart(hz)).Start();

        }

		private void hz()
		{
			while(true)
			{
				Thread.Sleep(200);
				HotkeyList.Clear();
				if (tempHotkeyList.Count > 0)
				{
					foreach (string s in tempHotkeyList)
					{
						HotkeyList.Add(s);
						Console.WriteLine("Collected : " + s);
						collectedHotkey += s + "+";
					}

					collectedHotkey.Substring(0, collectedHotkey.Length - 1);
					if (collectedHotkey.Equals(Keyup_Collector_string1))
					{
						Console.WriteLine("Device 1 changed");
					}
					else if (collectedHotkey.Equals(Keyup_Collector_string2))
					{
						Console.WriteLine("Device 2 changed");
					}
					collectedHotkey = "";

					HotkeyList.Clear();
					tempHotkeyList.Clear();
				}
			}
		}


		private void Hotkey(object sender, KeyEventArgs e)
		{
			tempHotkeyList.Add(e.KeyCode + "");
		}

		void Exit(object sender, CancelEventArgs e)
		{
			cs.Stop();
			kb.Deactivate();
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
				Show_Log("Device 1 Connected!");
			}
			else if (s.Equals("2"))
			{
				btn_d2_start.IsEnabled = true;
				Show_Log("Device 2 Connected!");
			}
		}

		#endregion

		

		


		#region Popup
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
		#endregion





		#region Option

		// Option Popup - Open
		private void Setting_Click(object sender, RoutedEventArgs e)
		{
			Popup_settings.IsOpen = true;
		}

		// Option Popup - Close
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
				Keyup_Collector_string1 += e.KeyCode + "+";
			}
				
		}
		private void HookManager_KeyUp2(object sender, KeyEventArgs e)
		{
			if (nowCollecting2)
			{
				Keyup_Collector_string2 += e.KeyCode + "+";
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
			KeyboardMouse.m_KeyboardHookManager.KeyUp -= HookManager_KeyUp1;
			
			checkbox1.Dispatcher.Invoke(new update1Callback(this.update1), Keyup_Collector_string1);
			nowCollecting1 = false;
			Keyup_Collector_string1 = "";
		}

		private void update1(string s)
		{
			checkbox1.Content = s.Substring(0, s.Length - 1);
		}

		private void update2(string s)
		{
			checkbox2.Content = s.Substring(0, s.Length - 1);
		}

		private void Keyup_Collector2()
		{
			Thread.Sleep(1500);
			nowCollecting2 = false;
			KeyboardMouse.m_KeyboardHookManager.KeyUp -= HookManager_KeyUp2;

			checkbox1.Dispatcher.Invoke(new update1Callback(this.update2), Keyup_Collector_string2);
			Keyup_Collector_string2 = "";
		}

		#endregion



		// Input Log to Listbox
		private void Show_Log(string s)
		{
			Listbox1.Items.Add(s);
			Listbox1.Items.MoveCurrentToLast();
			Listbox1.UpdateLayout();
		}


	}
	
}
