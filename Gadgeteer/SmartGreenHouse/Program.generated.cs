//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.34209
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace SmartGreenHouse {
    using Gadgeteer;
    using GTM = Gadgeteer.Modules;
    
    
    public partial class Program : Gadgeteer.Program {
        
        /// <summary>The USB Client DP module using socket 1 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.USBClientDP usbClientDP;
        
        /// <summary>The Moisture module using socket 10 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.Moisture moisture;
        
        /// <summary>The LightSense module using socket 9 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.LightSense lightSense;
        
        /// <summary>The Display TE35 module using sockets 14, 13 and 12 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.DisplayTE35 display;
        
        /// <summary>The Ethernet J11D module using socket 7 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.EthernetJ11D ethernet;
        
        /// <summary>The GPS module using socket 11 of the mainboard.</summary>
        private Gadgeteer.Modules.GHIElectronics.GPS gps;
        
        /// <summary>This property provides access to the Mainboard API. This is normally not necessary for an end user program.</summary>
        protected new static GHIElectronics.Gadgeteer.FEZSpider Mainboard {
            get {
                return ((GHIElectronics.Gadgeteer.FEZSpider)(Gadgeteer.Program.Mainboard));
            }
            set {
                Gadgeteer.Program.Mainboard = value;
            }
        }
        
        /// <summary>This method runs automatically when the device is powered, and calls ProgramStarted.</summary>
        public static void Main() {
            // Important to initialize the Mainboard first
            Program.Mainboard = new GHIElectronics.Gadgeteer.FEZSpider();
            Program p = new Program();
            p.InitializeModules();
            p.ProgramStarted();
            // Starts Dispatcher
            p.Run();
        }
        
        private void InitializeModules() {
            this.usbClientDP = new GTM.GHIElectronics.USBClientDP(1);
            this.moisture = new GTM.GHIElectronics.Moisture(10);
            this.lightSense = new GTM.GHIElectronics.LightSense(9);
            this.display = new GTM.GHIElectronics.DisplayTE35(14, 13, 12, Socket.Unused);
            this.ethernet = new GTM.GHIElectronics.EthernetJ11D(7);
            this.gps = new GTM.GHIElectronics.GPS(11);
        }
    }
}
