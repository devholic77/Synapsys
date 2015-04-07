using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;

namespace Synapsys_SUB
{
	class SynapsysSocket
	{
		private int PORT, DEVICE_NUM;
		private Socket socket = null;

		public SynapsysSocket(string port, string device_num)
		{
			this.PORT = Convert.ToInt32(port);
			this.DEVICE_NUM = Convert.ToInt32(device_num);
		}

		private void init()
		{
			IPEndPoint ipEndPoint = new IPEndPoint(IPAddress.Parse("127.0.0.1"), PORT);
			socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			socket.Connect(ipEndPoint);
		}

		public void sendFile(string file)
		{
			socket.SendFile(file);
		}


	}
}
