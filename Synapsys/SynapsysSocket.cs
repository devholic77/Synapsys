using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.IO;

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

		private Socket clientSock;  /* client Socket */
		private Socket cbSock;   /* client Async Callback Socket */
		private byte[] recvBuffer;

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
		string file;
		public void Send(string message)
		{
			try
			{
				/* 연결 성공시 */
				if (clientSock.Connected)
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
				if (clientSock.Connected)
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

		public void Synapsys_SendIMG(string file)
		{
			Console.WriteLine("SendIMG: " + file);
			this.file = file;
			filesize = (ulong)new FileInfo(file).Length;
			try
			{
				/* 연결 성공시 */
				if (clientSock.Connected)
				{
					byte[] buffer = BitConverter.GetBytes((int)filesize);
					Array.Reverse(buffer);

					Console.WriteLine(buffer);
					Console.WriteLine(buffer.Length);

					clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
										  new AsyncCallback(SendCallBack), filesize.ToString());
				}
			}
			catch (SocketException e)
			{
				Console.WriteLine("\r\n전송 에러 : " + e.Message);
			}
		}

		byte[] imgArray;

		public void SendFile(byte[] array)
		{
			try
			{
				/* 연결 성공시 */
				if (clientSock.Connected)
				{
					imgArray = array;

					if(array.Length > 0)
					{
						byte[] buffer = BitConverter.GetBytes(array.Length);
						Array.Reverse(buffer);
						Console.WriteLine("Send :" + buffer.Length);
						clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None,
											  new AsyncCallback(SendCallBack), filesize.ToString());
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
			Console.WriteLine("\r\n전송 완료 CallBack : " + message);
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
