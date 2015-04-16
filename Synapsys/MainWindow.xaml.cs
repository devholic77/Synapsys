using System.Windows;
using System.Threading;

using System.Windows.Forms;
using System;
using Synapsys_ADB;
using System.Collections;

using System.ComponentModel;

using Synapsys_SUB;
using Synapsys_Sub_Program;

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

        //Form
        ADB_Form form;

		// Socket
		public static SynapsysSocket socketIMG = null;
		public static SynapsysSocket socketData = null;
        
        
        // Minhwan

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

            form = new ADB_Form();
            form.Execute += new ADB_Form.execute(Clap);
            form.Usb_Check();            
            form.Visible = false;
            form.Show();


            Synapsys_Values.Monitor_Control.Synapsys_Check_Monitor(); // 설치된 드라이버 확인하기 
            

            //monitor

			//new Thread(new ThreadStart(hz)).Start();

        }

		private void socketTest()
		{
			while(true)
			{
				Thread.Sleep(1000);
				socketData.Send("1:0:123:123");
			}
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

		#region BUTTON EVENTS

        //1번과 2번이 동시에 켜져있을 때 1번만 stop 불가능 2번이 stop되야 1번이 stop 가능 //장대찬 처리해주세용~
        
		private void btn1_start(object sender, RoutedEventArgs e)
		{
            //Button_Function.Synapsys_Start_Monitor(Synapsys_Values.First_Device_Name); // sub program start
			//Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.First_Device_Name);
			//btn_d1_start.IsEnabled = false;
			//btn_d1_stop.IsEnabled = true;

			socketData = new SynapsysSocket("1235", "1");
			socketData.DoInit();
			new Thread(new ThreadStart(socketTest)).Start();
		}

		private void btn1_stop(object sender, RoutedEventArgs e)
		{
			Console.WriteLine("btn1_stop");
            Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.First_Device_Name);
            btn_d1_start.IsEnabled = true;
            btn_d1_stop.IsEnabled = false;
		}

		private void btn2_start(object sender, RoutedEventArgs e)
		{
            Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.Second_Device_Name);
			Console.WriteLine("btn2_start");
            btn_d2_start.IsEnabled = false;
            btn_d2_stop.IsEnabled = true;
            btn_d1_stop.IsEnabled = false;
		}

		private void btn2_stop(object sender, RoutedEventArgs e)
		{
            Console.WriteLine("btn2_stop");
            Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.Second_Device_Name);
            btn_d1_stop.IsEnabled = true;
            btn_d2_start.IsEnabled = true;
            btn_d2_stop.IsEnabled = false;			
		}
		#endregion

		#region ADB

		void Clap(object sender, ConnEvent e) // 이벤트 발생시 실행하고픈 함수. 델리게이트 선언의 파라미터를 따라갸아 한다.
		{
			//Console.WriteLine("Clap");
			//Console.WriteLine(e.Device);
			//Console.WriteLine(e.Message);
            Console.WriteLine(e.Check_Deivce_Msg + " : " + e.Check_Device_Flag);
            //Console.WriteLine(e.Check_Device_Flag);

            // public  String Check_Deivce_Msg { get; set; } //Add, Remove
            // public  int Check_Device_Flag { get; set; } // 1, 2, 3
            //zz
            //(Check_Deivce_Msg 에 따라서)
            // 1 - First Monitor가 추가 또는 제거됨 
            // 2 - Second Monitor가 추가 또는 제거됨 
            // 3 - 두개의 Monitor가 동시에 추가 또는 제거됨
            // 4 - remove인데 flag가 4인경우 1,2 모니터가 다 연결되있는 상태에서 1번모니터가 제거되서 2번 모니터가 1번모니터로 이동
            //     연결되는 포트가 port[0],port[1],port[2]로 변경되야됨
            //      Android에서도 서버를 다시 열어야됨 포트를 변경해서 <- 아마될거임

            if(e.Check_Deivce_Msg.Equals("Add"))
            {
                switch(e.Check_Device_Flag)
                {
                    case 1:
                        btn_d1_start.Dispatcher.Invoke(new update1Callback(this.update3), "1s");
                        break;
                    case 2:
                        btn_d2_start.Dispatcher.Invoke(new update1Callback(this.update3), "2s");
                        break;
                    case 3:
                        btn_d1_start.Dispatcher.Invoke(new update1Callback(this.update3), "1s");
                        btn_d2_start.Dispatcher.Invoke(new update1Callback(this.update3), "2s");
                        break;
                    default:
                        break;
                }
            }
            else // Remove
            {
                switch(e.Check_Device_Flag) // 제거제거제거  장대찬
                {
                    case 1:
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1e");
                           Synapsys_Values.FirstSubProgram.Kill();
                        break;
                    case 2:
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1se");
                        btn_d2_stop.Dispatcher.Invoke(new update1Callback(this.update3), "2e");
                        Synapsys_Values.SecondSubProgram.Kill();
                        break;
                    case 3:
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1e");
                        btn_d2_stop.Dispatcher.Invoke(new update1Callback(this.update3), "2e");
                        Synapsys_Values.FirstSubProgram.Kill();
                        Synapsys_Values.SecondSubProgram.Kill();
                        break;
                    default:
                        break;
                }
            }
            /*
            if (e.Check_Deivce_Msg.Equals("Add"))
            {
               for(int i=0; Synapsys_Values.Synapsys_Auto_Connect_List[i] != null; i++)
               {
                   if( Synapsys_Values.Synapsys_Auto_Connect_List[i].Equals(e.Message))
                   {
                       //auto ㄱㄱ 
                   }
               }
            }
             * */


			
		}
		private void update3(string s)
		{
			if (s.Equals("1s"))
			{
				btn_d1_start.IsEnabled = true;
				Show_Log("Device 1 Connected!");
			}
			else if (s.Equals("2s"))
			{
                
				btn_d2_start.IsEnabled = true;
				Show_Log("Device 2 Connected!");
			}
            else if (s.Equals("1e"))
            {
                btn_d1_stop.IsEnabled = false;
                btn_d1_start.IsEnabled = false;
                Show_Log("Device 2 Stop!");
            }
            else if (s.Equals("2e"))
            {
                btn_d2_stop.IsEnabled = false;
                btn_d2_start.IsEnabled = false;
                Show_Log("Device 2 Stop!");
            }
            else if (s.Equals("1se"))
            {
                if (btn_d1_stop.IsEnabled == false && btn_d2_stop.IsEnabled == true)
                    btn_d1_stop.IsEnabled = true;
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
