using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Synapsys
{
	class Protocol
	{

		// KEY
		const int KEY = 00;
		
		
		
		// MOUSE
		const int TYPE_MOUSE = 01;
		const int MOUSE_MOVE = 00;
		const int MOUSE_CLICK_L = 01;
		const int MOUSE_CLICK_R = 02;
		const int MOUSE_DBCLICK_L = 03;		// NOT USE
		const int MOUSE_UNCLICK_L = 04;
		const int MOUSE_UNCLICK_R = 05;
		const int MOUSE_WHEEL_UP = 06;
		const int MOUSE_WHEEL_DN = 07;

		public static void Decoder(string a)
		{

			char current = 'N';
			string temp = "";
			int count = 0;

			for (int i = 0; i < a.Length; i++ )
			{
				temp += a[i];
				count++;
				switch (current)
				{
					case 'N':		// START!
						temp += a[i];
						break;
					case 'T':		// TYPE
						if(count == 2)
						{
							count = 0;
							temp = "";

						}
						break;
					case 'C':		// CODE
						break;
					case 'V':		// VALUE
						break;
				}


			}
		}

		public static void Encoder()
		{

		}
	}
}
