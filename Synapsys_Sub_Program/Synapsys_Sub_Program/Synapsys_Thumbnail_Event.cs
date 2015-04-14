using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;
using System.Windows;
using System.Windows.Media.Imaging;

namespace Synapsys_Sub_Program
{
    //        public Synapsys_Android_Thumbnail(int App_id, String App_name, Icon App_Icon, Bitmap App_capture)

    public class Synapsys_Thumbnail_Event : EventArgs
    {
        public int App_Id { get; set; }
        public String App_Name { get; set; } //
        public Icon App_Icon { get; set; }
        public Bitmap App_Thumbnail { get; set; }
        public int App_Event_Num { get; set; }
    }
}
