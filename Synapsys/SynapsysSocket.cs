using System;
using System.Text;
using System.Net.Sockets;
using System.Threading;
using System.Collections.Concurrent;

namespace Synapsys
{

	public class SynapsysSocket
	{
		private int PORT, DEVICE_NUM;

		public SynapsysSocket(string port, string device_num)
		{
			this.PORT = Convert.ToInt32(port);
			this.DEVICE_NUM = Convert.ToInt32(device_num);
		}

		private Socket clientSock = null;  /* client Socket */
		private Socket cbSock;   /* client Async Callback Socket */
		private byte[] recvBuffer;
		private static bool flag = true;

		private const int MAXSIZE = 4096;   /* 4096  */

		public void DoInit()
		{
			clientSock = new Socket(AddressFamily.InterNetwork,
										  SocketType.Stream, ProtocolType.Tcp);
			recvBuffer = new byte[4096];
			queue = new ConcurrentQueue<byte[]>();
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
				Console.WriteLine("\r\n 서버로 접속 성공 : ");

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

		ulong filesize = 0L;
		public void Send(string message)
		{
			try
			{
				/* 연결 성공시 */
				if (clientSock != null && clientSock.Connected)
				{
					byte[] buffer = new UTF8Encoding().GetBytes(message);
					clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), message);
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
		ConcurrentQueue<byte[]> queue;

		public void addIMG(byte[] array)
		{
			queue.Enqueue(array);
		}
		byte[] array;
		public void Sender()
		{
			while (true)
			{
				if (queue.TryPeek(out array))
				{
					SendFile(array);
					System.Console.WriteLine("Send");
					new Thread(new ThreadStart(setEmpty)).Start();
				}
				array = null;
			}
		}

		public void setEmpty()
		{
			byte[] array;
			while (queue.TryDequeue(out array))
			{}
			array = null;
		}

		public void SendFile(byte[] array)
		{
			try
			{
				/* 연결 성공시 */
				if (flag && clientSock != null && clientSock.Connected)
				{
					flag = false;
					Console.WriteLine(array.Length);
					imgArray = array;

					if(array.Length > 0)
					{
						byte[] buffer = BitConverter.GetBytes(array.Length);
						Array.Reverse(buffer);
						clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
											  null, filesize.ToString());
					}
				}
			}
			catch (SocketException e)
			{
				Console.WriteLine("\r\n전송 에러 : " + e.Message);
			}
		}

		/*----------------------*
		 * ##### CallBack ##### *
		 *        Send          *
		 *----------------------*/
		private void SendCallBack(IAsyncResult IAR)
		{
			string message = (string)IAR.AsyncState;
			//Console.WriteLine("\r\n전송 완료 CallBack : " + message);
			if(message == "image")
			{
				flag = true;
			}
		}

		/*----------------------*
		 *  Receive             *
		 *----------------------*/
		public void Receive()
		{
			cbSock.BeginReceive(this.recvBuffer, 0, recvBuffer.Length, SocketFlags.None,
								 new AsyncCallback(OnReceiveCallBack), cbSock);
		}

		/*----------------------*
		 * ##### CallBack ##### *
		 *  Receive             *
		 *----------------------*/

		public static int currOK = 0;
		private void OnReceiveCallBack(IAsyncResult IAR)
		{
			try
			{
				Socket tempSock = (Socket)IAR.AsyncState;
				int nReadSize = tempSock.EndReceive(IAR);
				if (nReadSize != 0)
				{
					string message = new UTF8Encoding().GetString(recvBuffer, 0, nReadSize);
					Console.WriteLine("\r\n서버로 데이터 수신 : " + message);
					message = message.Trim();
					if ("OK".Equals(message))
					{
						currOK++;
						clientSock.BeginSend(imgArray, 0, imgArray.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), "image");
					}

				}

				this.Receive();
			}
			catch (SocketException se)
			{
				if (se.SocketErrorCode == SocketError.ConnectionReset)
				{
					this.BeginConnect();
				}
			}
		}


	}
}
