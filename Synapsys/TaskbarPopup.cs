using System;
using System.Drawing;
using System.Windows.Forms;

namespace Synapsys
{
	public partial class TaskbarPopup : Form
	{

		public int PopupMargin = 10;

		public TaskbarPopup()
		{
			InitializeComponent();
			ResetStartPosition();
		}

		public void Gogo(string title, string message)
		{
			Hide();

			label1.Text = title;
			label2.Text = message;

			pictureBox1.Image = System.Drawing.Image.FromFile(@"C:\Users\JDC\Documents\Visual Studio 2013\Projects\Synapsys\Synapsys\Resources\kakao.png");


			Show();
			timer_switch = 0;
			timer1.Start();
		}

		private void ResetStartPosition()
		{
			TopMost = true; // 창을 맨 앞에 나오게 하기 위해
			StartPosition = FormStartPosition.Manual; // 스타트 포지션을 수동으로 설정
			Location = GetLocation(); // 창이 나타날 위치 설정
		}

		private Point GetLocation() // 나타날 위치 설정을 위한 계산함수
		{
			Rectangle wArea = Screen.GetWorkingArea(this); // 작업표시줄을 제외한 1번 모니터의 작업영역을 가져옴
			var width = wArea.Width - PopupMargin - Size.Width; // 작업영역의 넓이 에서 여백을 빼고 팝업창의 넓이를 뺌
			var height = wArea.Height - PopupMargin - Size.Height; // 작업영역 높이 에서 여백을 뺴고 팝업창의 높이를 뺌
			return new Point(width, height); // Point 로 전달
		}

		private int timer_switch = 0;	// 0 - fadein, 1 - fadeout, else - wait

		private void timer1_Tick(object sender, EventArgs e)
		{
			switch(timer_switch)
			{
				case 0:
					this.Opacity += .05;
					break;
				case 2:
					this.Opacity -= .05;
					break;
				default:
					timer_switch--;
					break;
			}
			
			if(this.Opacity == 1)
			{
				timer_switch = 80;
				this.Opacity -= .05;
			}
			else if(this.Opacity == 0)
			{
				timer_switch = 0;
				timer1.Stop();
			}


		}

	}
}
