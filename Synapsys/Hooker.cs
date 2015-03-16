//using System;
//using Gma.UserActivityMonitor;
//using System.Windows.Forms;
//using System.Runtime.InteropServices;

//namespace Synapsys
//{

//	class Hooker
//	{
		
//		public Hooker()
//		{
//		}

//		public void Add()
//		{
//			HookManager.MouseMove += HookManager_MouseMove;
//			HookManager.MouseClick += HookManager_MouseClick;
//			HookManager.MouseUp += HookManager_MouseUp;
//			HookManager.MouseDown += HookManager_MouseDown;
//			HookManager.MouseDoubleClick += HookManager_MouseDoubleClick;
//			HookManager.MouseWheel += HookManager_MouseWheel;
//			HookManager.KeyDown += HookManager_KeyDown;
//			HookManager.KeyUp += HookManager_KeyUp;
//			HookManager.KeyPress += HookManager_KeyPress;
//		}

//		public void Delete()
//		{
//			HookManager.MouseMove -= HookManager_MouseMove;
//			HookManager.MouseClick -= HookManager_MouseClick;
//			HookManager.MouseUp -= HookManager_MouseUp;
//			HookManager.MouseDown -= HookManager_MouseDown;
//			HookManager.MouseDoubleClick -= HookManager_MouseDoubleClick;
//			HookManager.MouseWheel -= HookManager_MouseWheel;
//			HookManager.KeyDown -= HookManager_KeyDown;
//			HookManager.KeyUp -= HookManager_KeyUp;
//			HookManager.KeyPress -= HookManager_KeyPress;
//		}

//		#region Event Handler Start!

//		private void HookManager_KeyDown(object sender, KeyEventArgs e)
//		{
//			Console.WriteLine(string.Format("KeyDown - {0}\n", e.KeyCode));

//		}

//		private void HookManager_KeyUp(object sender, KeyEventArgs e)
//		{
//			Console.WriteLine(string.Format("KeyUp - {0}\n", e.KeyCode));

//		}


//		private void HookManager_KeyPress(object sender, KeyPressEventArgs e)
//		{
//			Console.WriteLine(string.Format("KeyPress - {0}\n", e.KeyChar));

//		}


//		private void HookManager_MouseMove(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("x={0:0000}; y={1:0000}", e.X, e.Y));
//		}

//		private void HookManager_MouseClick(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("MouseClick - {0}\n", e.Button));

//		}


//		private void HookManager_MouseUp(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("MouseUp - {0}\n", e.Button));

//		}


//		private void HookManager_MouseDown(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("MouseDown - {0}\n", e.Button));

//		}


//		private void HookManager_MouseDoubleClick(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("MouseDoubleClick - {0}\n", e.Button));

//		}


//		private void HookManager_MouseWheel(object sender, MouseEventArgs e)
//		{
//			Console.WriteLine(string.Format("Wheel={0:000}", e.Delta));
//		}
//		#endregion

//	}
//}
