namespace ClipboardHost
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.notifyIcon1 = new System.Windows.Forms.NotifyIcon(this.components);
            this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.modeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.silentToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.normalToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.devicesToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.settingsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toggleUSBToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.exitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.backgroundWorker1 = new System.ComponentModel.BackgroundWorker();
            this.onlineIcon = new System.Windows.Forms.NotifyIcon(this.components);
            this.offlineIcon = new System.Windows.Forms.NotifyIcon(this.components);
            this.contextMenuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // notifyIcon1
            // 
            this.notifyIcon1.BalloonTipText = "Clipboard v1.0";
            this.notifyIcon1.ContextMenuStrip = this.contextMenuStrip1;
            this.notifyIcon1.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon1.Icon")));
            this.notifyIcon1.Text = "Clipboard";
            this.notifyIcon1.Visible = true;
            // 
            // contextMenuStrip1
            // 
            this.contextMenuStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.modeToolStripMenuItem,
            this.devicesToolStripMenuItem,
            this.settingsToolStripMenuItem,
            this.exitToolStripMenuItem});
            this.contextMenuStrip1.Name = "contextMenuStrip1";
            this.contextMenuStrip1.Size = new System.Drawing.Size(270, 200);
            this.contextMenuStrip1.Opening += new System.ComponentModel.CancelEventHandler(this.contextMenuStrip1_Opening);
            // 
            // modeToolStripMenuItem
            // 
            this.modeToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.silentToolStripMenuItem,
            this.normalToolStripMenuItem});
            this.modeToolStripMenuItem.Name = "modeToolStripMenuItem";
            this.modeToolStripMenuItem.Size = new System.Drawing.Size(269, 38);
            this.modeToolStripMenuItem.Text = "Mode";
            // 
            // silentToolStripMenuItem
            // 
            this.silentToolStripMenuItem.CheckOnClick = true;
            this.silentToolStripMenuItem.Name = "silentToolStripMenuItem";
            this.silentToolStripMenuItem.Size = new System.Drawing.Size(194, 38);
            this.silentToolStripMenuItem.Text = "Silent";
            this.silentToolStripMenuItem.CheckedChanged += new System.EventHandler(this.silentToolStripMenuItem_CheckedChanged);
            // 
            // normalToolStripMenuItem
            // 
            this.normalToolStripMenuItem.CheckOnClick = true;
            this.normalToolStripMenuItem.Name = "normalToolStripMenuItem";
            this.normalToolStripMenuItem.Size = new System.Drawing.Size(194, 38);
            this.normalToolStripMenuItem.Text = "Normal";
            this.normalToolStripMenuItem.CheckedChanged += new System.EventHandler(this.normalToolStripMenuItem_CheckedChanged);
            // 
            // devicesToolStripMenuItem
            // 
            this.devicesToolStripMenuItem.Name = "devicesToolStripMenuItem";
            this.devicesToolStripMenuItem.Size = new System.Drawing.Size(269, 38);
            this.devicesToolStripMenuItem.Text = "Devices";
            // 
            // settingsToolStripMenuItem
            // 
            this.settingsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toggleUSBToolStripMenuItem});
            this.settingsToolStripMenuItem.Name = "settingsToolStripMenuItem";
            this.settingsToolStripMenuItem.Size = new System.Drawing.Size(269, 38);
            this.settingsToolStripMenuItem.Text = "Settings";
            // 
            // toggleUSBToolStripMenuItem
            // 
            this.toggleUSBToolStripMenuItem.Name = "toggleUSBToolStripMenuItem";
            this.toggleUSBToolStripMenuItem.Size = new System.Drawing.Size(269, 38);
            this.toggleUSBToolStripMenuItem.Text = "Enable USB";
            this.toggleUSBToolStripMenuItem.Click += new System.EventHandler(this.toggleUSBToolStripMenuItem_Click);
            // 
            // exitToolStripMenuItem
            // 
            this.exitToolStripMenuItem.Name = "exitToolStripMenuItem";
            this.exitToolStripMenuItem.Size = new System.Drawing.Size(269, 38);
            this.exitToolStripMenuItem.Text = "Exit";
            this.exitToolStripMenuItem.Click += new System.EventHandler(this.exitToolStripMenuItem_Click);
            // 
            // backgroundWorker1
            // 
            this.backgroundWorker1.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorker1_DoWork);
            // 
            // onlineIcon
            // 
            this.onlineIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("onlineIcon.Icon")));
            this.onlineIcon.Text = "Online";
            // 
            // offlineIcon
            // 
            this.offlineIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("offlineIcon.Icon")));
            this.offlineIcon.Text = "Online";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(12F, 25F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(568, 502);
            this.Margin = new System.Windows.Forms.Padding(6);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "Form1";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Form1";
            this.WindowState = System.Windows.Forms.FormWindowState.Minimized;
            this.Load += new System.EventHandler(this.Form1_Load);
            this.contextMenuStrip1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.NotifyIcon notifyIcon1;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
        private System.Windows.Forms.ToolStripMenuItem exitToolStripMenuItem;
        private System.ComponentModel.BackgroundWorker backgroundWorker1;
        private System.Windows.Forms.ToolStripMenuItem modeToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem silentToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem normalToolStripMenuItem;
        private System.Windows.Forms.NotifyIcon onlineIcon;
        private System.Windows.Forms.NotifyIcon offlineIcon;
        private System.Windows.Forms.ToolStripMenuItem settingsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem toggleUSBToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem devicesToolStripMenuItem;
    }
}

