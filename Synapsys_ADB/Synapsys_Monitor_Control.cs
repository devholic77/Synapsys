using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.InteropServices;


namespace Synapsys_ADB
{
    public class Synapsys_Monitor_Control
    {
        [DllImport("user32.dll", CharSet = CharSet.Unicode)]
        private static extern long SetDisplayConfig(
            int numPathArrayElements,
            IntPtr pathArray,
            int numModeArrayElements,
            IntPtr modeArray,
            uint flags);

        UInt32 SDC_TOPOLOGY_INTERNAL = 0x00000001;
        UInt32 SDC_TOPOLOGY_CLONE = 0x00000002;
        UInt32 SDC_TOPOLOGY_EXTEND = 0x00000004;
        UInt32 SDC_TOPOLOGY_EXTERNAL = 0x00000008;
        UInt32 SDC_APPLY = 0x00000080;

        public void ExternalDisplays()
        {
            SetDisplayConfig(0, IntPtr.Zero, 0, IntPtr.Zero, (SDC_APPLY | SDC_TOPOLOGY_EXTERNAL));
        }

        public void ExtendDisplays()
        {
            SetDisplayConfig(0, IntPtr.Zero, 0, IntPtr.Zero, (SDC_APPLY | SDC_TOPOLOGY_EXTEND));

        }

        [DllImport("user32.dll")]
        static extern bool EnumDisplayMonitors(IntPtr hdc, IntPtr lprcClip, MonitorEnumDelegate lpfnEnum, IntPtr dwData);

        delegate bool MonitorEnumDelegate(IntPtr hMonitor, IntPtr hdcMonitor, ref RECT lprcMonitor, IntPtr dwData);


        public int Synapsys_Check_Monitor()
        {
            EnumDisplayMonitors(IntPtr.Zero, IntPtr.Zero, MonitorEnumProc, IntPtr.Zero);
            return Synapsys_Values.Monitor_Driver_Num;
        }
        static bool MonitorEnumProc(IntPtr hMonitor, IntPtr hdcMonitor, ref RECT lprcMonitor, IntPtr dwData)
        {
            MONITORINFOEX mi = new MONITORINFOEX();
            mi.Size = Marshal.SizeOf(typeof(MONITORINFOEX));
            if (GetMonitorInfo(hMonitor, ref mi))
            {
                //Console.WriteLine(mi.DeviceName);
                Synapsys_Values.Monitor_Driver_Num++;
            }
            return true;
        }
        [StructLayout(LayoutKind.Sequential)]

        struct RECT
        {
            public int Left;
            public int Top;
            public int Right;
            public int Bottom;
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        struct MONITORINFOEX
        {
            public int Size;
            public RECT Monitor;
            public RECT WorkArea;
            public uint Flags;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 32)]
            public string DeviceName;
        }
        [DllImport("user32.dll", CharSet = CharSet.Unicode)]
        static extern bool GetMonitorInfo(IntPtr hMonitor, ref MONITORINFOEX lpmi);



        public void Monitor_Delete()
        {
            if(Synapsys_Values.Monitor_Driver_Num == 2)
            {
                ExternalDisplays();
            }
            if(Synapsys_Values.Monitor_Driver_Num == 3)
            {

            }
        }
        public void Monitor_Start()
        {
            if (Synapsys_Values.Monitor_Driver_Num == 2)
            {
                ExtendDisplays();
            }
            if (Synapsys_Values.Monitor_Driver_Num == 3)
            {

            }
        }

    }
}
