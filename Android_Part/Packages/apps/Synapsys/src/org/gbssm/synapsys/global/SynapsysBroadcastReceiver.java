package org.gbssm.synapsys.global;

import org.gbssm.synapsys.MainActivity;
import org.gbssm.synapsys.SynapsysManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.30
 *
 */
public class SynapsysBroadcastReceiver extends BroadcastReceiver {
	
	private Toast mToast;
	
	private boolean prevConnected;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (mToast == null)
			mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
		
		Bundle bundle = intent.getExtras();
		
		boolean isUSBready = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_USB_READY);
		boolean isPCready = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_PC_READY);
		boolean isConnected = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_CONNECTION);

		SynapsysApplication mApplication = (SynapsysApplication) context.getApplicationContext();
		if (isUSBready && isPCready) {
			mApplication.setControllerConnected(isConnected);
			
			if (!isConnected) {
				mApplication.startStreaming();
				
				Intent newIntent = new Intent(context, MainActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}
			
		} else if (!isUSBready || !isPCready) {
			mApplication.setControllerConnected(false);
			mApplication.stopStreaming();
		}
		
		String message = (isUSBready? "USB! " : "")  + (isPCready? "PC! " : "") + (isConnected? "All Connected!" : "");
		mToast.setText(message);
		mToast.show();
	}

}
