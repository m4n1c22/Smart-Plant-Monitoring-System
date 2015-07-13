using System;
using Microsoft.SPOT;
using System;
using System.Collections;
using System.Threading;
using Microsoft.SPOT;

using Microsoft.SPOT.Presentation;
using Microsoft.SPOT.Presentation.Controls;
using Microsoft.SPOT.Presentation.Media;
using Microsoft.SPOT.Presentation.Shapes;
using Microsoft.SPOT.Touch;

using Gadgeteer.Networking;
using NetworkModule = Gadgeteer.Modules.Module.NetworkModule;
using Gadgeteer;
using GT = Gadgeteer;
using GTM = Gadgeteer.Modules;
using Gadgeteer.Modules.GHIElectronics;
using System;
using System.Net;
using System.Security;
//using System.Net;
//using System.Http;
using System.Text;
using System.IO;
using Microsoft.SPOT.Net.NetworkInformation;

namespace SmartGreenHouse
{
    class PowerManager
    {
        Status lightstatus=Status.OFF;

        public void setlighton()
        {
            try
            {
                string url = "http://192.168.1.3/energenie/lighton.php";

                HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
                request.Method = "POST";

                {
                    //host is active
                }
                var response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception e)
            {
                string x = e.Message;
            }    
        }
        public void setlightoff()
        {
            try
            {
                string url = "http://192.168.1.3/energenie/lightoff.php";

                HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
                request.Method = "POST";

                {
                    //host is active
                }
                var response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception e)
            {
                string x = e.Message;
            } 
        }
        public void setwateron()
        {
            try
            {
                string url = "http://192.168.1.3/energenie/wateron.php";

                HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
                request.Method = "POST";

                {
                    //host is active
                }
                var response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception e)
            {
                string x = e.Message;
            }
        }
        public void setwateroff()
        {
            try
            {
                string url = "http://192.168.1.3/energenie/wateroff.php";

                HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
                request.Method = "POST";

                {
                    //host is active
                }
                var response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception e)
            {
                string x = e.Message;
            }
        }
    }
}
