using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using System.Net;
using System.Net.Sockets;
using Zeroconf;
using Managed.Adb;
using System.Threading;

namespace ClipboardHost
{
    public partial class Form1 : Form
    {
        private AdbHelper adbHelper = AdbHelper.Instance;

        public Form1()
        {
            InitializeComponent();
            //Check mode
            if (Properties.Settings.Default.Mode == true)
            {
                //is silent
                ((ToolStripMenuItem)silentToolStripMenuItem).Checked = true;
                ((ToolStripMenuItem)normalToolStripMenuItem).Checked = false;
            }
            else
            {
                ((ToolStripMenuItem)silentToolStripMenuItem).Checked = false;
                ((ToolStripMenuItem)normalToolStripMenuItem).Checked = true;
            }
            AndroidDebugBridge.Initialize(true);
            AndroidDebugBridge.CreateBridge("adb/adb.exe", true);
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            backgroundWorker1.RunWorkerAsync();
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                //client.Disconnect(true);
                AdbHelper.Instance.KillAdb(AndroidDebugBridge.SocketAddress);
            }
            catch
            {

            }
            System.Diagnostics.Process.GetCurrentProcess().Kill();
        }

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            List<ClipboardHandler> onlineDevices = new List<ClipboardHandler>();
            while (true)
            {
                IReadOnlyList<IZeroconfHost> result = ZeroconfResolver.ResolveAsync("_jktest._tcp.local.").Result;
                //ADB
                var adbResult = AdbHelper.Instance.GetDevices(AndroidDebugBridge.SocketAddress)
                  .Where(d => d.IsOnline == true);

                List<ClipboardHandler> newOnlineDevices = new List<ClipboardHandler>();

                foreach (ClipboardHandler existingSockets in onlineDevices)
                {
                    IPEndPoint existingEndpont = existingSockets.getEndpoint();
                    if (existingEndpont != null)
                    {
                        //Is it still online?!
                        bool stillOnline = false;
                        foreach (IZeroconfHost device in result)
                        {
                            IPEndPoint iep = new IPEndPoint(IPAddress.Parse(device.IPAddress), device.Services.First().Value.Port);
                            if (ClipboardHandler.AreEqualIPE(existingEndpont, iep))
                            {
                                stillOnline = true;
                                break;
                            }
                        }

                        //ADB
                        foreach (Device device in adbResult)
                        {
                            if (existingSockets.device != null)
                            {
                                if (device.SerialNumber == existingSockets.device.SerialNumber)
                                {
                                    stillOnline = true;
                                    break;
                                }
                            }
                        }

                        if (stillOnline)
                        {
                            newOnlineDevices.Add(existingSockets);
                        }
                    }
                }

                onlineDevices = newOnlineDevices;

                //ADB
                foreach (Device device in adbResult)
                {
                    bool exists = false;

                    foreach (ClipboardHandler existingSockets in onlineDevices)
                    {
                        if (existingSockets.device != null)
                        {
                            if (existingSockets.device.SerialNumber == device.SerialNumber)
                            {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (!exists)
                    {
                        //So this is new device
                        Console.WriteLine("Connecting to device:" + device.SerialNumber);
                        ClipboardHandler handler = new ClipboardHandler(device,notifyIcon1);
                        onlineDevices.Add(handler);
                    }
                }

                //Check for newly added devices
                foreach (IZeroconfHost device in result)
                {
                    IPEndPoint iep = new IPEndPoint(IPAddress.Parse(device.IPAddress), device.Services.First().Value.Port);
                    bool exists = false;
                    foreach (ClipboardHandler existingSockets in onlineDevices)
                    {
                        IPEndPoint existingEndpont = existingSockets.getEndpoint();

                        if (ClipboardHandler.AreEqualIPE(existingEndpont, iep))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                    {
                        //So this is new device
                        try
                        {
                            IPAddress ip;
                            if (IPAddress.TryParse(device.IPAddress, out ip) == true)
                            {

                                Console.WriteLine("Connecting to device: " + device.IPAddress + ":" + device.Services.First().Value.Port);
                                ClipboardHandler handler = new ClipboardHandler(device.IPAddress, device.Services.First().Value.Port, notifyIcon1);
                                onlineDevices.Add(handler);
                            }

                        }

                        catch (Exception ex)
                        {
                            Console.WriteLine(ex.Message);
                        }
                    }
                }

                    Thread.Sleep(2000);
            }
        }

        private void normalToolStripMenuItem_CheckedChanged(object sender, EventArgs e)
        {
            if (((ToolStripMenuItem)normalToolStripMenuItem).Checked)
            {
                Properties.Settings.Default.Mode = false;
                Properties.Settings.Default.Save();
                ((ToolStripMenuItem)silentToolStripMenuItem).Checked = false;
            }
        }

        private void silentToolStripMenuItem_CheckedChanged(object sender, EventArgs e)
        {
            if (((ToolStripMenuItem)silentToolStripMenuItem).Checked)
            {
                Properties.Settings.Default.Mode = true;
                Properties.Settings.Default.Save();
                ((ToolStripMenuItem)normalToolStripMenuItem).Checked = false;
            }
        }
    }
}
