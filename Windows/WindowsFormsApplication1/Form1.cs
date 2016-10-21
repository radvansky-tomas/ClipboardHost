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
        private bool isADBEnabled = false;

        public Form1()
        {
            InitializeComponent();
            //Check mode
            ((ToolStripMenuItem)silentToolStripMenuItem).Checked = false;
            ((ToolStripMenuItem)normalToolStripMenuItem).Checked = false;
            switch (Properties.Settings.Default.Mode)
            {
                case 0:
                    {
                        ((ToolStripMenuItem)silentToolStripMenuItem).Checked = true;
                    }
                    break;
                case 1:
                    {
                        ((ToolStripMenuItem)normalToolStripMenuItem).Checked = true;
                    }
                    break;
            }

            if (isADBEnabled)
            {
                 AndroidDebugBridge.Initialize(true);
                 AndroidDebugBridge.CreateBridge("adb/adb.exe", true);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            backgroundWorker1.RunWorkerAsync();
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (isADBEnabled)
            {
                try
                {
                    AdbHelper.Instance.KillAdb(AndroidDebugBridge.SocketAddress);
                }
                catch
                {

                }
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
                if (isADBEnabled)
                {
                    var adbResult = AdbHelper.Instance.GetDevices(AndroidDebugBridge.SocketAddress)
                  .Where(d => d.IsOnline == true);

                    List<ClipboardHandler> newOnlineDevices = new List<ClipboardHandler>();

                    foreach (ClipboardHandler existingSockets in onlineDevices)
                    {
                        if (existingSockets.isAlive())
                        {
                            newOnlineDevices.Add(existingSockets);
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
                            Console.WriteLine("Connecting to adb device:" + device.SerialNumber);
                            ClipboardHandler handler = new ClipboardHandler(device, notifyIcon1);
                            onlineDevices.Add(handler);
                        }
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

                                Console.WriteLine("Connecting to new device: " + device.IPAddress + ":" + device.Services.First().Value.Port);
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

                if (onlineDevices.Count > 0)
                {
                    onlineIcon.Visible = true;
                    offlineIcon.Visible = false;
                }
                else
                {
                    onlineIcon.Visible = false;
                    offlineIcon.Visible = true;
                }
                    Thread.Sleep(2000);
            }
        }

        private void normalToolStripMenuItem_CheckedChanged(object sender, EventArgs e)
        {
            if (((ToolStripMenuItem)normalToolStripMenuItem).Checked)
            {
                Properties.Settings.Default.Mode = 1;
                Properties.Settings.Default.Save();
                ((ToolStripMenuItem)silentToolStripMenuItem).Checked = false;
            }
        }

        private void silentToolStripMenuItem_CheckedChanged(object sender, EventArgs e)
        {
            if (((ToolStripMenuItem)silentToolStripMenuItem).Checked)
            {
                Properties.Settings.Default.Mode = 0;
                Properties.Settings.Default.Save();
                ((ToolStripMenuItem)normalToolStripMenuItem).Checked = false;
            }
        }

        private void sendFileToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog dlg = new OpenFileDialog();
            dlg.Title = "File to send";
            if (String.IsNullOrEmpty(Properties.Settings.Default.Dir))
            {
                dlg.InitialDirectory = System.Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            }
            else
            {
                dlg.InitialDirectory = Properties.Settings.Default.Dir;
            }
            if (dlg.ShowDialog() == DialogResult.OK)
            {
                MessageBox.Show(dlg.FileName.ToString());
            }
        }

        private void changeDirToolStripMenuItem_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog dlg = new FolderBrowserDialog();
            if (String.IsNullOrEmpty(Properties.Settings.Default.Dir))
            {
                dlg.SelectedPath = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            }
            else
            {
                dlg.SelectedPath = Properties.Settings.Default.Dir;
            }
            if (dlg.ShowDialog() == DialogResult.OK)
            {
                MessageBox.Show(dlg.SelectedPath.ToString(),"New Default Directory");
                Properties.Settings.Default.Dir = dlg.SelectedPath;
                Properties.Settings.Default.Save();
            }
        }
    }
}
