using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Synapsys_Sub_Program
{
    static class Program
    {
        /// <summary>
        /// 해당 응용 프로그램의 주 진입점입니다.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1());


            //new Synapsys_Display_Socket(Synapsys_Values.port[2], Synapsys_Values.Second_Device_Name);
            //new Synapsys_Data_Socket(Synapsys_Values.port[3], Synapsys_Values.Second_Device_Name);

        }
    }
}
