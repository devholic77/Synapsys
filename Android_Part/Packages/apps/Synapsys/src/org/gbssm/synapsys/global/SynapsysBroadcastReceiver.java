package org.gbssm.synapsys.global;

import org.gbssm.synapsys.MainActivity;
import org.gbssm.synapsys.R;
import org.gbssm.synapsys.SynapsysManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.30
 *
 */
public class SynapsysBroadcastReceiver extends BroadcastReceiver {
	
	private Toast mToast;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (mToast == null)
			mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
		
		Bundle bundle = intent.getExtras();
		
		boolean usbReady = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_USB_READY);
		boolean pcReady = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_PC_READY);
		boolean connected = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_CONNECTION);
		boolean reaction = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_REACTION);

		SynapsysApplication mApplication = (SynapsysApplication) context.getApplicationContext();
		if (usbReady && pcReady) {
			mApplication.setControllerConnected(connected);
			
			if (!connected && reaction) {
				mApplication.startStreaming();
				
				String message = context.getString(R.string.pc_connected);
				mToast.setText(message);
				mToast.show();

				// Synapsys Application 자동 실행
				Intent newIntent = new Intent(context, MainActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}
			
		} else if (!usbReady || !pcReady) {
			mApplication.setControllerConnected(false);
			mApplication.stopStreaming();
			
			if ((usbReady && !pcReady && !reaction)) {
				String message = context.getString(R.string.pc_disconnected);
				mToast.setText(message);
				mToast.show();
			}
		}
		
	}
}
