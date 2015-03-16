using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Shell_test
{
    class Synapsys_Manager
    {
        static void Main(string[] args)
        {            

            //initialize_port();
            //new Synapsys_Display_Socket();
            //new Synapsys_ADB_Instruction(str_display_socket, str_data_socket); // Port Forward 할 Port 2개를 입력 
            new Synapsys_ADB_Instruction(); // Port Forward 할 Port 2개를 입력 
            //  new Synapsys_Display_Socket(1234);
            //  new Synapsys_Data_Socket(1235);
        }

        static void initialize_port()
        {
            int j = 1234;

            Synapsys_Values.port[0] = "1234";
            Synapsys_Values.port[1] = "1235";
            Synapsys_Values.port[2] = "1236";
            Synapsys_Values.port[3] = "1237";




            /*
            for (int i = 0; i < 100; i++) {
                Synapsys_Values.port[i] = Convert.ToString(j);
                j++;
            }
             */

        }
    }
}
