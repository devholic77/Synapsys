using System;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.Threading;

namespace Synapsys
{
	public partial class Popup : Form
	{

		public string Title, Text;

		public Popup()
		{
			InitializeComponent();

			this.label1.Text = Title;
			this.label2.Text = Text;

			AnimateWindow();
			Thread.Sleep(2000);
			HideAnimateWindow();
		}

		private void Popup_Load(object sender, EventArgs e)
		{
		}

		private const int MarginX = 0; // x좌표 여백
		private const int MarginY = 0; // y좌표 여백
		private const int DeleayTime = 500; // 창이 나타나는 시간 (밀리초 단위)

		private void button1_Click(object sender, EventArgs e)
		{
			Close(); // 팝업창 닫기
		}

		private void AnimateWindow()
		{
			ResetStartPosition(); // 팝업창의 설정
			Animate.AnimateWindow(Handle, DeleayTime, Animate.DwFlagVerNegative); // WinApi 호출
		}

		private void HideAnimateWindow() // 사라지는 애니메이션
		{
			Animate.AnimateWindow(Handle, DeleayTime, Animate.DwFlagVerPositive); // WinApi 호출
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
			var width = wArea.Width - MarginX - Size.Width; // 작업영역의 넓이 에서 여백을 빼고 팝업창의 넓이를 뺌
			var height = wArea.Height - MarginY - Size.Height; // 작업영역 높이 에서 여백을 뺴고 팝업창의 높이를 뺌
			return new Point(width, height); // Point 로 전달
		}
	}

	public class Animate
	{
		[DllImport("User32.dll", EntryPoint = "AnimateWindow")]
		public static extern bool AnimateWindow(IntPtr hwnd, int dwTime, int dwFlags);

		public const int DwFlagHorPositive = 0x00000001; // 좌 -> 우
		public const int DwFlagHorNegative = 0x00000002; // 우 -> 좌
		public const int DwFlagVerPositive = 0x00000004; // 뒤 -> 아래
		public const int DwFlagVerNegative = 0x00000008; // 아래 -> 위
		public const int DwFlagCenter = 0x00000010; // 가운데부터
		public const int DwFlagHide = 0x00010000; // 숨기기
		public const int DwFlagActivate = 0x00020000; // 활성화(사용안함)
		public const int DwFlagSlide = 0x00040000; // 슬라이드
		public const int DwFlagBlend = 0x00080000; // Fading 효과
	}
}
