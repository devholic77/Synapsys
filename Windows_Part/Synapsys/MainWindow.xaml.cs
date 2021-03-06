﻿using System.Windows;
using System.Threading;

using System.Windows.Forms;
using System;
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

        //Form
        ADB_Form form;

        // Socket
        public static SynapsysSocket socketIMG1 = null;
        public static SynapsysSocket socketData1 = null;

        public static SynapsysSocket socketIMG2 = null;
        public static SynapsysSocket socketData2 = null;

        public static int WIDTH = 600;
        public static int HEIGHT = 960;
		public static int DEVICE1_MARGIN = 0;
		public static int DEVICE2_MARGIN = 0;
		public static int MONITOR_FIRST_WIDTH = 0;


        // Minhwan

        public MainWindow()
        {
            //System.Diagnostics.Process myProcess = System.Diagnostics.Process.GetCurrentProcess();
            //myProcess.PriorityClass = System.Diagnostics.ProcessPriorityClass.High;

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
            cs.Start();

            form = new ADB_Form();
            form.Execute += new ADB_Form.execute(Clap);
            form.Usb_Check();
            form.Visible = false;
            form.Show();


            Synapsys_Values.Monitor_Control.Synapsys_Check_Monitor(); // 설치된 드라이버 확인하기 


            //monitor

            new Thread(new ThreadStart(hz)).Start();

            //btn_d1_start.RaiseEvent(new RoutedEventArgs(B, btn_d1_start));
            //SOCKET INIT //
        }

		public void Window_Closing(object sender, CancelEventArgs e)
		{
			//this.Close();
            if (Synapsys_Values.FirstSubProgram != null) { 
				Synapsys_Values.FirstSubProgram.Kill();
                Synapsys_Values.FirstSubProgram = null;
            }
            if (Synapsys_Values.SecondSubProgram != null) { 
				Synapsys_Values.SecondSubProgram.Kill();
                Synapsys_Values.SecondSubProgram = null;
            }

		}

		private void hz()
		{
			while (true)
			{
				Thread.Sleep(200);
				HotkeyList.Clear();
				if (tempHotkeyList.Count > 0)
				{
					foreach (string s in tempHotkeyList)
					{
						HotkeyList.Add(s);
						//Console.WriteLine(s);
						collectedHotkey += s + "+";
					}

					collectedHotkey.Substring(0, collectedHotkey.Length - 1);
					//Console.WriteLine(collectedHotkey);
					//Console.WriteLine(Keyup_Collector_string1);
					if (!nowCollecting1 && collectedHotkey.Equals(Keyup_Collector_string1)
						&& checkbox1_flag)
					{
						Console.WriteLine("Device 1 changed");
						System.IO.File.WriteAllText(@"C:\change1.txt", "");
					}
					else if (!nowCollecting2 && collectedHotkey.Equals(Keyup_Collector_string2)
						&& checkbox2_flag)
					{
						Console.WriteLine("Device 2 changed");
						System.IO.File.WriteAllText(@"C:\change2.txt", "");
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
			try
			{
				socketIMG1.Disconnect();
				socketData1.Disconnect();
				socketIMG2.Disconnect();
				socketData2.Disconnect();

				cs.Stop();
				kb.Deactivate();
			}
			catch (Exception)
			{

			}
		}


        #region BUTTON EVENTS

        //1번과 2번이 동시에 켜져있을 때 1번만 stop 불가능 2번이 stop되야 1번이 stop 가능 //장대찬 처리해주세용~

        private void btn1_start(object sender, RoutedEventArgs e)
        {
            //Button_Function.Synapsys_Start_Monitor(Synapsys_Values.First_Device_Name); // sub program start

            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[0], Synapsys_Values.port[1], Synapsys_Values.port[2], Synapsys_Values.First_Device_Name);

            Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.First_Device_Name);
            btn_d1_start.IsEnabled = false;
            btn_d1_stop.IsEnabled = true;

            socketIMG1 = new SynapsysSocket("1234");
            socketData1 = new SynapsysSocket("1235");
            Synapsys_Values.First_Monitor_Stop_Enable = true;
            socketIMG1.DoInit();
            socketData1.DoInit();

        }

        private void btn1_stop(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("btn1_stop");
            Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.First_Device_Name);
            btn_d1_start.IsEnabled = true;
            btn_d1_stop.IsEnabled = false;
            Synapsys_Values.First_Monitor_Stop_Enable = false;
            socketIMG1.Disconnect();
            socketData1.Disconnect();
        }



        private void btn2_start(object sender, RoutedEventArgs e)
        {

            Synapsys_Values.ADB_Instruction.Port_Define(Synapsys_Values.port[3], Synapsys_Values.port[4], Synapsys_Values.port[5], Synapsys_Values.Second_Device_Name);


            Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.Second_Device_Name);
            Console.WriteLine("btn2_start");
            btn_d2_start.IsEnabled = false;
            btn_d2_stop.IsEnabled = true;
            btn_d1_stop.IsEnabled = false;

            socketIMG2 = new SynapsysSocket("1237");
            socketData2 = new SynapsysSocket("1238");

            Synapsys_Values.Second_Monitor_Stop_Enable = true;
            socketIMG2.DoInit();
            socketData2.DoInit();
        }

        private void btn2_stop(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("btn2_stop");
            Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.Second_Device_Name);

            Synapsys_Values.Second_Monitor_Stop_Enable = false;

            btn_d1_stop.IsEnabled = true;
            btn_d2_start.IsEnabled = true;
            btn_d2_stop.IsEnabled = false;

            socketIMG2.Disconnect();
            socketData2.Disconnect();
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

            if (e.Check_Deivce_Msg.Equals("Add"))
            {
                switch (e.Check_Device_Flag)
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
                switch (e.Check_Device_Flag) // 제거제거제거  장대찬
                {

                    case 1:
                        Console.WriteLine("삭제1");
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1e");
                        Synapsys_Values.Buttons_Function.Synapsys_Remove_Monitor(Synapsys_Values.First_Device_Name);

                        if(Synapsys_Values.First_Monitor_Stop_Enable  == true)
                        {
                            socketIMG1.Disconnect();
                            socketData1.Disconnect();
                        }

                        // 이부분 수정 없는데 끈어서 
                        //socketIMG1.Disconnect();
                        //socketData1.Disconnect();
                        // 1번 스탑버튼 누르기
                        break;
                    case 2:
                        Console.WriteLine("삭제2");
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1se");
                        btn_d2_stop.Dispatcher.Invoke(new update1Callback(this.update3), "2e");

                        Synapsys_Values.Buttons_Function.Synapsys_Remove_Monitor(Synapsys_Values.Second_Device_Name);

                        if (Synapsys_Values.Second_Monitor_Stop_Enable == true)
                        {
                            socketIMG2.Disconnect();
                            socketData2.Disconnect();
                        }
                        //socketIMG2.Disconnect();
                       // socketData2.Disconnect();

                        // 2번 스탑버튼 누르기
                        break;
                    case 3:
                        btn_d1_stop.Dispatcher.Invoke(new update1Callback(this.update3), "1e");
                        btn_d2_stop.Dispatcher.Invoke(new update1Callback(this.update3), "2e");
                        Synapsys_Values.Buttons_Function.Synapsys_Remove_Monitor(Synapsys_Values.First_Device_Name);
                        Synapsys_Values.Buttons_Function.Synapsys_Remove_Monitor(Synapsys_Values.Second_Device_Name);


                        if (Synapsys_Values.First_Monitor_Stop_Enable == true)
                        {
                            socketIMG1.Disconnect();
                            socketData1.Disconnect();
                        }


                        if (Synapsys_Values.Second_Monitor_Stop_Enable == true)
                        {
                            socketIMG2.Disconnect();
                            socketData2.Disconnect();
                        }
               
                        // 2번,1번 스탑버튼 누르기                   
                        break;
                    case 4:
                        // 2번스탑 1번스탑
                        // 1번 실행 
						//btn_d1_start.RaiseEvent(new RoutedEventArgs(Button., btn_d1_start));

                        Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.First_Device_Name);
                        Synapsys_Values.Buttons_Function.Synapsys_Stop_Monitor(Synapsys_Values.Second_Device_Name);

                        Synapsys_Values.Buttons_Function.Synapsys_Start_Monitor(Synapsys_Values.First_Device_Name);
                        
                        break;
                    default:
                        break;
                }
            }

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

		static bool checkbox1_flag = false;
		static bool checkbox2_flag = false;

		private void checkbox_handler(object sender, RoutedEventArgs e)
		{
			if (sender.Equals(checkbox1))
			{
				if (checkbox1.IsChecked.Value)
				{
					checkbox1_flag = true;
					nowCollecting1 = true;
					KeyboardMouse.m_KeyboardHookManager.KeyUp += HookManager_KeyUp1;
					Keyup_Collector_string1 = "";
					new Thread(Keyup_Collector1).Start();
				}
				else//
				{
					checkbox1_flag = false;
				}
			}

			if (sender.Equals(checkbox2))
			{
				if (checkbox2.IsChecked.Value)
				{
					checkbox2_flag = true;
					nowCollecting2 = true;
					KeyboardMouse.m_KeyboardHookManager.KeyUp += HookManager_KeyUp2;
					Keyup_Collector_string2 = "";
					new Thread(Keyup_Collector2).Start();
				}
				else
				{
					checkbox2_flag = false;
				}
			}
		}

		private void HookManager_KeyUp1(object sender, KeyEventArgs e)
		{
			if (nowCollecting1)
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
			//Keyup_Collector_string1 = "";
		}

		private void update1(string s)
		{
			if (s.Length > 1)
				checkbox1.Content = s.Substring(0, s.Length - 1);
			else
				checkbox1.Content = "No Input";
		}

		private void update2(string s)
		{
			if(s.Length > 1)
				checkbox2.Content = s.Substring(0, s.Length - 1);
			else
				checkbox1.Content = "No Input";
		}

		private void Keyup_Collector2()
		{
			Thread.Sleep(1500);
			nowCollecting2 = false;
			KeyboardMouse.m_KeyboardHookManager.KeyUp -= HookManager_KeyUp2;

			checkbox1.Dispatcher.Invoke(new update1Callback(this.update2), Keyup_Collector_string2);
			//Keyup_Collector_string2 = "";
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
