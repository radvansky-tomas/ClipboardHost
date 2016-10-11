using System;
using System.Windows.Forms;
using System.Threading;

namespace ClipboardHost
{
    static class Program
    {
        static Mutex mutex = new Mutex(false, "clipboard-host");
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            // if you like to wait a few seconds in case that the instance is just 
            // shutting down
            if (!mutex.WaitOne(TimeSpan.FromSeconds(2), false))
            {
                MessageBox.Show("Application already started!", "", MessageBoxButtons.OK);
                return;
            }

            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new Form1());
            }
            finally { mutex.ReleaseMutex(); } // I find this more explicit
        }
    }
}
