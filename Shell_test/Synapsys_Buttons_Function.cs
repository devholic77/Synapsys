using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Shell_test
{
    class Synapsys_Buttons_Function
    {
        public void Synapsys_Search_Button()
        {
            Synapsys_Values.ADB_Instruction.Check_Device();
            Synapsys_Values.ADB_Instruction.Port_Define("33332", "0a1d99f6");
            Synapsys_Values.ADB_Instruction.Port_Define("33332", "0a1d99f6");
            Synapsys_Values.ADB_Instruction.Port_Forward("dfd");
            Synapsys_Values.ADB_Instruction.Port_Forward("dfd");

        }
        public void Synapsys_Start_Monitor(int num)
        {
         
        }
    }
}
