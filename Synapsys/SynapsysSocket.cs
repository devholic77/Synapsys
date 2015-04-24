using System;
using System.Text;
using System.Net.Sockets;
using System.Threading;
using System.Collections.Concurrent;

namespace Synapsys
{

	public class SynapsysSocket
	{
		private int PORT;

		public SynapsysSocket(string port)
		{
			this.PORT = Convert.ToInt32(port);
		}

		private Socket clientSock = null;  /* client Socket */
		private Socket cbSock;   /* client Async Callback Socket */
		private byte[] recvBuffer;
		private volatile bool flag = true;


		private const int MAXSIZE = 4096;   /* 4096  */

		public void DoInit()
		{
			clientSock = new Socket(AddressFamily.InterNetwork,
										  SocketType.Stream, ProtocolType.Tcp);
			recvBuffer = new byte[4096];
			this.BeginConnect();
		}

		public void BeginConnect()
		{

			Console.WriteLine("서버 접속 대기 중");
			try
			{
				clientSock.BeginConnect("127.0.0.1", PORT, new AsyncCallback(ConnectCallBack), clientSock);
			}
			catch (SocketException se)
			{
				/*서버 접속 실패 */
				Console.WriteLine("\r\n서버접속 실패하였습니다. " + se.NativeErrorCode);
				this.DoInit();
			}

		}

		public void Disconnect()
		{
			clientSock.Disconnect(false);
			clientSock = null;
		}

		/*----------------------*
		 * ##### CallBack ##### *
		 *   Connection         *
		 *----------------------*/
		private void ConnectCallBack(IAsyncResult IAR)
		{
			try
			{
				// 보류중인 연결을 완성
				Socket tempSock = (Socket)IAR.AsyncState;
				Console.WriteLine("서버 접속 성공 " + PORT);

				tempSock.EndConnect(IAR);
				cbSock = tempSock;
				cbSock.BeginReceive(this.recvBuffer, 0, recvBuffer.Length, SocketFlags.None,
									new AsyncCallback(OnReceiveCallBack), cbSock);
			}
			catch (SocketException se)
			{
				if (se.SocketErrorCode == SocketError.NotConnected)
				{
					Console.WriteLine("\r\n서버 접속 실패 CallBack " + se.Message);
					this.BeginConnect();
				}
			}
		}

		/*----------------------*
		 *       Send           *
		 *----------------------*/

		public void SendString(string message)
		{
			try
			{
				/* 연결 성공시 */
				if (flag && clientSock != null && clientSock.Connected)
				{
					//Console.WriteLine(message);
					byte[] buffer = new UTF8Encoding().GetBytes(message);
					clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), "text");
				}
			}
			catch (SocketException e)
			{
				Console.WriteLine("\r\n전송 에러 : " + e.Message);
			}
		}

		public void SendByte(byte[] buffer)
		{
			try
			{
				/* 연결 성공시 */
				if (clientSock != null && clientSock.Connected)
				{
					clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), "byte");
				}
			}
			catch (SocketException e)
			{
				Console.WriteLine("\r\n전송 에러 : " + e.Message);
			}
		}

		byte[] imgArray;
		public void SendScreen(byte[] array)
		{
			try
			{
				/* 연결 성공시 */
				if (flag && clientSock != null && clientSock.Connected)
				{
					flag = false;
					//Console.WriteLine(array.Length);
					imgArray = array;

					if(array.Length > 0)
					{
						byte[] buffer = BitConverter.GetBytes(array.Length);
						Array.Reverse(buffer);
						clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
											  null, null);
						buffer = null;
					}
				}
			}
			catch (SocketException e)
			{
				Console.WriteLine("\r\n전송 에러 : " + e.Message);
			}
		}

		// Send Callback
		private void SendCallBack(IAsyncResult IAR)
		{
			try
			{
				string message = (string)IAR.AsyncState;
				if (message == "image" || message == "text")
				{
					flag = true;
				}
			}
			catch(Exception e){
				Console.WriteLine(e);
			}
		}


		// Receive
		public void Receive()
		{
			try
			{
				cbSock.BeginReceive(this.recvBuffer, 0, recvBuffer.Length, SocketFlags.None,
								 new AsyncCallback(OnReceiveCallBack), cbSock);
			}
			catch (Exception e)
			{
				Console.WriteLine(e);
				this.DoInit();
			}
		}

		// Receive Callback
		private void OnReceiveCallBack(IAsyncResult IAR)
		{
			try
			{
				Socket tempSock = (Socket)IAR.AsyncState;
				int nReadSize = tempSock.EndReceive(IAR);
				if (nReadSize != 0)
				{
					string message = new UTF8Encoding().GetString(recvBuffer, 0, nReadSize);
					//Console.WriteLine("\r\n서버로 데이터 수신 : " + message);
					message = message.Trim();
					if ("OK".Equals(message))
					{
						clientSock.BeginSend(imgArray, 0, imgArray.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), "image");
					} 
					else if(message.Length > 10)
					{
						if(message[1] == ':' && message[3] == ':')
						{
							string[] data = message.Split(':');
							int type = Int32.Parse(data[0]);
							int code = Int32.Parse(data[1]);
							int x = Int32.Parse(data[2]);
							int y = Int32.Parse(data[3]);
							if(0 == type)
							{
								if(0 == code)
								{
									KeyboardMouse.KeyDown(Convert.ToByte(x));
								}
								else if (1 == code)
								{
									KeyboardMouse.KeyUp(Convert.ToByte(x));
								}
							}
							else if (1 == type)
							{
								KeyboardMouse.MOVE_MOUSE(x, y, code, (PORT == 1235 ? 1 : 2));
							}
						}
					}

				}

				this.Receive();
			}
			catch (SocketException se)
			{
				Console.WriteLine("ERROR! - Receive Callback");
				Console.WriteLine(se);
				if (se.SocketErrorCode == SocketError.ConnectionReset)
				{
					this.BeginConnect();
				}
			}
		}


	}
}
