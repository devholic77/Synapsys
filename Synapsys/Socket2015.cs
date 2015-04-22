using System;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.Collections.Concurrent;

namespace Synapsys
{
	public class Socket2015
	{
		private IPEndPoint ipEndPoint;
		private Socket client;

		public Socket2015(int port)
		{
			ipEndPoint = new IPEndPoint(IPAddress.Loopback, port);
			client = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		}

		public void Connect()
		{
			client.Connect(ipEndPoint);
			queue = new ConcurrentQueue<byte[]>();
			Sender();
			Console.WriteLine("Connected!!!!");
			Console.WriteLine("Connected!!!!");
		}

		public void SendIMG(byte[] array)
		{
			byte[] buffer = BitConverter.GetBytes(array.Length);
			Array.Reverse(buffer);
			client.Send(buffer);
			client.Send(array);
		}

		ConcurrentQueue<byte[]> queue;

		public void addIMG(byte[] array)
		{
			queue.Enqueue(array);
		}

		static byte[] array;
		public void Sender()
		{
			while(true)
			{
				if (queue.TryPeek(out array))
				{
					SendIMG(array);
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
			{ }
			array = null;
			System.Console.WriteLine("Queue is Empty!!");
		}
	}
}
