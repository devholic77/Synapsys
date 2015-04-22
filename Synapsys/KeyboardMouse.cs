using System;
using MouseKeyboardActivityMonitor;
using MouseKeyboardActivityMonitor.WinApi;
using System.Windows.Forms;

// Keycontrol
using System.Runtime.InteropServices;
using System.Drawing;
using System.Threading;

namespace Synapsys
{
	class KeyboardMouse
	{
		public static KeyboardHookListener m_KeyboardHookManager;
		private static MouseHookListener m_MouseHookManager;
		private static KeyboardMouse kb = null;

		// 싱글톤 123
		public static KeyboardMouse getInstance()
		{
			if(kb == null)
			{
				kb = new KeyboardMouse();

				m_KeyboardHookManager = new KeyboardHookListener(new GlobalHooker());
				m_MouseHookManager = new MouseHookListener(new GlobalHooker());
			}
			return kb;
		}

		public void Activate()
		{
			Console.WriteLine("Keyboard, Mouse Hooking Start");
			if(m_KeyboardHookManager.Enabled == false)
			{
				m_KeyboardHookManager.Enabled = true;
				m_MouseHookManager.Enabled = true;

				m_MouseHookManager.MouseMove += HookManager_MouseMove;
				m_MouseHookManager.MouseClickExt += HookManager_MouseClick;
				//m_MouseHookManager.MouseUp += HookManager_MouseUp;
				//m_MouseHookManager.MouseDown += HookManager_MouseDown;
				m_MouseHookManager.MouseDoubleClick += HookManager_MouseDoubleClick;
				m_MouseHookManager.MouseWheel += HookManager_MouseWheel;
				//m_MouseHookManager.MouseDownExt += HookManager_Supress;
				//m_KeyboardHookManager.KeyDown += HookManager_KeyDown;
				m_KeyboardHookManager.KeyUp += HookManager_KeyUp;
				//m_KeyboardHookManager.KeyPress += HookManager_KeyPress;
			}
		}

		public void Deactivate()
		{
			if (m_KeyboardHookManager.Enabled == true)
			{
				m_KeyboardHookManager.Enabled = true;
				m_MouseHookManager.Enabled = true;

				m_MouseHookManager.MouseMove -= HookManager_MouseMove;
				m_MouseHookManager.MouseClickExt -= HookManager_MouseClick;
				//m_MouseHookManager.MouseUp -= HookManager_MouseUp;
				//m_MouseHookManager.MouseDown -= HookManager_MouseDown;
				m_MouseHookManager.MouseDoubleClick -= HookManager_MouseDoubleClick;
				m_MouseHookManager.MouseWheel -= HookManager_MouseWheel;
				//m_MouseHookManager.MouseDownExt -= HookManager_Supress;
				//m_KeyboardHookManager.KeyDown -= HookManager_KeyDown;
				m_KeyboardHookManager.KeyUp -= HookManager_KeyUp;
				//m_KeyboardHookManager.KeyPress -= HookManager_KeyPress;
			}
		}



		#region Event Handler Start!

		//private void HookManager_KeyDown(object sender, KeyEventArgs e)
		//{
		//	Console.WriteLine(string.Format("KeyDown - {0}\n", e.KeyCode));

		//}

		private void HookManager_KeyUp(object sender, KeyEventArgs e)
		{
			//Console.WriteLine(string.Format("KeyUp - {0}\n", e.KeyValue));
			Console.WriteLine(string.Format("KeyUp - {0}\n", e.KeyCode));
		}


		//private void HookManager_KeyPress(object sender, KeyPressEventArgs e)
		//{
		//	Console.WriteLine(string.Format("KeyPress - {0}\n", e.KeyChar));
		//}


		private void HookManager_MouseMove(object sender, MouseEventArgs e)
		{
			Console.WriteLine(string.Format("x={0:0000}; y={1:0000}", e.X, e.Y));
		}

		private void HookManager_MouseClick(object sender, MouseEventArgs e)
		{
			//Console.WriteLine(string.Format("MouseClick - {0}\n", e.Button));

		}


		//private void HookManager_MouseUp(object sender, MouseEventArgs e)
		//{
		//	Console.WriteLine(string.Format("MouseUp - {0}\n", e.Button));

		//}


		//private void HookManager_MouseDown(object sender, MouseEventArgs e)
		//{
		//	Console.WriteLine(string.Format("MouseDown - {0}\n", e.Button));

		//}


		private void HookManager_MouseDoubleClick(object sender, MouseEventArgs e)
		{
			Console.WriteLine(string.Format("MouseDoubleClick - {0}\n", e.Button));

		}


		private void HookManager_MouseWheel(object sender, MouseEventArgs e)
		{
			Console.WriteLine(string.Format("Wheel={0:000}", e.Delta));
		}
		#endregion


		#region MOUSE EVENT WORKER

		[DllImport("user32.dll")]
		private static extern void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);
		private const int MOUSEEVENTF_LEFTDOWN = 0x0002;
		private const int MOUSEEVENTF_LEFTUP = 0x0004;
		private const int MOUSEEVENTF_RIGHTDOWN = 0x0008;
		private const int MOUSEEVENTF_RIGHTUP = 0x0010;
		private const int MOUSEEVENTF_MIDDLEUP = 0x0040;
		private const int MOUSEEVENTF_MIDDLEDOWN = 0x0020;

		public void MOVE_MOUSE(int x, int y)
		{
			Cursor.Position = new Point(
						//Cursor.Position.X + int.Parse((String)col["mouseX"].GetValue()),
						//Cursor.Position.Y + int.Parse((String)col["mouseY"].GetValue())
						x, y
					);
		}

		public void EVENT_MOUSE(int k)
		{
			switch(k)
			{
				case 0:		// MOVE
					break;
				case 1:		// CLICK_LEFT
					mouse_event(MOUSEEVENTF_LEFTDOWN, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 2:		// CLICK_RIGHT
					mouse_event(MOUSEEVENTF_RIGHTDOWN, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 3:		// DOUBLE_CLICK_LEFT
					mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					Thread.Sleep(150);
					mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 4:		// UNPRESS_LEFT
					mouse_event(MOUSEEVENTF_LEFTUP, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 5:		// UNPRESS_RIGHT
					mouse_event(MOUSEEVENTF_RIGHTUP, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 6:		// WHEEL_UP
					mouse_event(MOUSEEVENTF_MIDDLEUP, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				case 7:		// WHEEL_DOWN
					mouse_event(MOUSEEVENTF_MIDDLEDOWN, Control.MousePosition.X, Control.MousePosition.Y, 0, 0);
					break;
				default:	// DEFAULT
					break;
			}
		}


		#endregion

	}
}
