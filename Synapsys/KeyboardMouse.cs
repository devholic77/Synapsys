using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MouseKeyboardActivityMonitor;
using MouseKeyboardActivityMonitor.WinApi;
using System.Windows.Forms;

namespace Synapsys
{
	class KeyboardMouse
	{
		private static KeyboardHookListener m_KeyboardHookManager;
		private static MouseHookListener m_MouseHookManager;
		private static KeyboardMouse kb = null;

		// 싱글톤
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
			Console.WriteLine(string.Format("KeyUp - {0}\n", e.KeyValue));

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
			Console.WriteLine(string.Format("MouseClick - {0}\n", e.Button));

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




	}
}
