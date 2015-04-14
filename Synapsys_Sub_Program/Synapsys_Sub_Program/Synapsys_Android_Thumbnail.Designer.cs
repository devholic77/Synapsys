namespace Synapsys_Sub_Program
{
    partial class Synapsys_Android_Thumbnail
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Synapsys_Android_Thumbnail));
            this.App_thumbnail_picturebox = new System.Windows.Forms.PictureBox();
            ((System.ComponentModel.ISupportInitialize)(this.App_thumbnail_picturebox)).BeginInit();
            this.SuspendLayout();
            // 
            // App_thumbnail_picturebox
            // 
            this.App_thumbnail_picturebox.Location = new System.Drawing.Point(0, 0);
            this.App_thumbnail_picturebox.Name = "App_thumbnail_picturebox";
            this.App_thumbnail_picturebox.Size = new System.Drawing.Size(284, 262);
            this.App_thumbnail_picturebox.TabIndex = 0;
            this.App_thumbnail_picturebox.TabStop = false;
            // 
            // Synapsys_Android_Thumbnail
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 262);
            this.Controls.Add(this.App_thumbnail_picturebox);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Synapsys_Android_Thumbnail";
            this.Text = "Synapsys_Android_Thumbnail";
             this.Shown += new System.EventHandler(this.Synapsys_Thumbnail_Shown);
            ((System.ComponentModel.ISupportInitialize)(this.App_thumbnail_picturebox)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.PictureBox App_thumbnail_picturebox;
    }
}