using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClipboardHost
{
    public partial class ProgressForm : Form
    {
        public ProgressForm()
        {
            InitializeComponent();
        }

        public void ReportProgress(int progress)
        {
            if (IsHandleCreated)
            {
                Invoke(new Action(() =>
                {
                    progressBar1.Value = progress;
                    this.Text = "Downloading... " + progress.ToString() + "%";
                }));
            }
        }

        public void Finish()
        {
            DialogResult = DialogResult.OK;
            this.Close();
        }

        private void cancelButton_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
