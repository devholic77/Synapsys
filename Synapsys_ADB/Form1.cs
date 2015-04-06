using System;
using System.Windows.Forms;
using System.Threading;

namespace Synapsys_ADB
{
	public partial class ADB_Form : Form
	{
		public ADB_Form()
		{
			InitializeComponent();
			Usb_Check();
		}
		static bool device_check_flag = true;

		protected override void WndProc(ref Message m)
		{
			UInt32 WM_DEVICECHANGE = 0x0219;
			UInt32 DBT_DEVICEARRIVAL = 0x8000;
			//Console.WriteLine(m);
			if ((m.Msg == WM_DEVICECHANGE))
			{
				Console.WriteLine(m);
				if (device_check_flag == true && (m.WParam.ToInt32() != DBT_DEVICEARRIVAL))
				{
					new Thread(new ThreadStart(Usb_Check)).Start();
					Console.WriteLine("Usb Checked");
				}
			}
			base.WndProc(ref m);
		}
		public void Usb_Check()
		{
			bool is_device = false;

			device_check_flag = false;

			Thread.Sleep(2000);
			is_device = Synapsys_Values.ADB_Instruction.Check_Device();
			if (is_device)
			{
				// "모니터 추가가 가능한 device가 감지되었습니다 추가하시겠습니까? 라는 팝업창
				// 확인 눌렀을 때  public void Start_Application(String device_name) 함수 ㄱㄱㄱㄱ
				//
				if (Execute != null) // 이벤트가 발생하면
				{
					ConnEvent e = new ConnEvent();
					e.Device = Synapsys_Values.Current_Device_Num;
					e.Message = Synapsys_Values.Current_Device_Name;
					Execute(this, e);  // 이벤트 실행. this는 이 객체를 말하는것.
				}


			}

			device_check_flag = true;
		}


		public delegate void execute(object sender, ConnEvent e); // 델리게이트 선언. 이 형식에 맞춰 이벤트 적용 함수 만들어야함.
		public event execute Execute; // 이벤트 선언
	}
}
