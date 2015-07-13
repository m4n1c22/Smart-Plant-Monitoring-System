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
    public partial class Program
    {
        PowerManager powermanager = new PowerManager();
        string latitude, longitude;
        bool lightstatus=false, waterstatus=false;
        bool isWaterOverridden = false; bool isLightOverridden = false;
        
        
        void ProgramStarted()
        {

            Debug.Print("Program Started");           
            EthernetSetup();
            
            register();
            
            //light sensor thread.
            Thread lightthread = new Thread(checklight);
            lightthread.Start();


            //moisture sensor thread
            Thread waterthread = new Thread(checkmoisture);
            waterthread.Start();

            //Polling the server for any updates 
            while (true)
            {
                polling();
            }
            //Thread poll = new Thread(polling);
            //poll.Start();
            ////register();
        }    

        private void checklight()
        {
            while(true)
            {
                if (lightSense.GetIlluminance() < 300 && !isLightOverridden)
                {
                    if (lightstatus != true)
                    {
                        lightstatus = true; 
                        powermanager.setlighton();
                        updateDeviceStatus(lightstatus, waterstatus);
                    }
                    lightstatus = true;
                }
                else 
                {
                    if(lightstatus!=false)
                        updateDeviceStatus(false, waterstatus);
                    lightstatus = false;
                    powermanager.setlightoff();
                    
                }

                Thread.Sleep(1000);
            }
        }
        public void waterplant()
        {
            waterstatus = true;
            updateDeviceStatus(lightstatus, waterstatus);
            powermanager.setwateron();
            Thread.Sleep(3000);
            powermanager.setwateroff();
            waterstatus = false;
            updateDeviceStatus(lightstatus, waterstatus);
        }
        private void checkmoisture()
        {
            while (true)
            {
                if (isWaterOverridden) { Thread.Sleep(10 * 1000); continue; }
                if (moisture.ReadMoisture() < 50)
                {
                    waterplant();
                }
                
                Thread.Sleep(300* 1000);
            }
        }
        private void EthernetSetup()
        {
            //ethernet.UseDHCP();
            //ethernet.NetworkInterface.EnableDhcp();
            string [] dns = {"192.168.1.1","192.168.1.1"};
            ethernet.UseStaticIP("192.168.1.20", "255.255.255.0", "192.168.1.1",dns);
            ethernet.UseThisNetworkInterface();
            //ethernet.UseDHCP();
            //ethernet.NetworkInterface.EnableDhcp();

            //ethernet.NetworkInterface.EnableDynamicDns();
            if(!ethernet.NetworkInterface.Opened)
            ethernet.NetworkInterface.Open();
        }
        // This function polls the server for any updates from the user.
        private void polling()
        {
            try
            {
                string UrlRequest = "https://api.parse.com/1/functions/pollForDevice";

                HttpWebRequest request = WebRequest.Create(UrlRequest) as HttpWebRequest;
                request.ContentType = "application/json";
                request.Headers.Add("X-Parse-Application-Id", "A1mhNBAj92MCA2vvLgWs9d1iGudjvTLt72PNWuo8");
                request.Headers.Add("X-Parse-REST-API-KEY", "YmFIVAFAQaS5OYx01VHaWFokiTRi3JKbCccw0XtU");
                request.Method = "POST";

                string parsedContent = "{\"DeviceID\":\"GADG3\"}}";
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] bytes = encoding.GetBytes(parsedContent);
                request.ContentLength = bytes.Length;

                Stream newStream = request.GetRequestStream();
                newStream.Write(bytes, 0, bytes.Length);
                newStream.Close();

                var response = (HttpWebResponse)request.GetResponse();

                using (var streamReader = new StreamReader(response.GetResponseStream()))
                {
                    var result = streamReader.ReadToEnd();
                    string resp = result;
                    int index = result.ToLower().IndexOf("isblocked");
                    if (index != -1)
                    {
                        string isBlocked = result.Substring(index + 11, 4);
                        if (isBlocked == "true") isWaterOverridden = true;
                        else isWaterOverridden = false;
                    }
                    index = result.ToLower().IndexOf("lightstatus");
                    if (index != -1)
                    {
                        string lightstatus = result.Substring(index + 13, 4);
                        if (lightstatus == "true") isLightOverridden = false;
                        else isLightOverridden = true;
                    }
                    index = result.ToLower().IndexOf("waterstatus");
                    if (index != -1)
                    {
                        string waterstatus = result.Substring(index + 13, 4);
                        if (waterstatus == "true") waterplant();
                    }

                    
                    //JObject o = JObject.Parse(json);
                }

            }
            catch (Exception e) { }
            Thread.Sleep(1000 * 2);
        }
        
        //The gadgeteer sends a status update to the server whenever there is an occurance of an event. light/water
        private void updateDeviceStatus(bool lightStatus, bool waterStatus)
        {
            try
            {
                string UrlRequest = "https://api.parse.com/1/functions/updateDeviceStatus";

                HttpWebRequest request = WebRequest.Create(UrlRequest) as HttpWebRequest;
                request.ContentType = "application/json";
                request.Headers.Add("X-Parse-Application-Id", "A1mhNBAj92MCA2vvLgWs9d1iGudjvTLt72PNWuo8");
                request.Headers.Add("X-Parse-REST-API-KEY", "YmFIVAFAQaS5OYx01VHaWFokiTRi3JKbCccw0XtU");
                request.Method = "POST";

                string parsedContent = "{\"DeviceID\":\"GADG3\",\"LightStatus\":" + lightStatus.ToString().ToLower() + ",\"WaterStatus\":" + waterStatus.ToString().ToLower() + "}}";
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] bytes = encoding.GetBytes(parsedContent);
                request.ContentLength = bytes.Length;

                Stream newStream = request.GetRequestStream();
                newStream.Write(bytes, 0, bytes.Length);
                newStream.Close();
                
                var response = (HttpWebResponse)request.GetResponse();

                using (var streamReader = new StreamReader(response.GetResponseStream()))
                {
                    var result = streamReader.ReadToEnd();
                    String x = result;
                }
            }
            catch (Exception e) { }
        }

        //The gadgeteer registers itself to the server.
        private void register()
        {
            try
            {
                string UrlRequest = "https://api.parse.com/1/functions/register";

                HttpWebRequest request = WebRequest.Create(UrlRequest) as HttpWebRequest;
                request.ContentType = "application/json";
                request.Headers.Add("X-Parse-Application-Id", "A1mhNBAj92MCA2vvLgWs9d1iGudjvTLt72PNWuo8");
                request.Headers.Add("X-Parse-REST-API-KEY", "YmFIVAFAQaS5OYx01VHaWFokiTRi3JKbCccw0XtU");
                request.Method = "POST";
                string parsedContent = "{\"DeviceID\":\"GADG1\",\"GeoLocation\":{\"latitude\":20.0,\"longitude\":30.0}}";
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] bytes = encoding.GetBytes(parsedContent);                
                request.ContentLength = bytes.Length;
                Stream newStream = request.GetRequestStream();
                newStream.Write(bytes, 0, bytes.Length);
                newStream.Close();
                
                var response = (HttpWebResponse)request.GetResponse();

                using (var streamReader = new StreamReader(response.GetResponseStream()))
                {
                    var result = streamReader.ReadToEnd();
                    String x = result;
                }
            }

            catch (Exception e) {
                string y = e.Message;            
            }
        }
        
    }
}
