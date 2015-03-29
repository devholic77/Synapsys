package org.gbssm.synapsys.global;

import org.gbssm.synapsys.SynapsysManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.30
 *
 */
public class SynapsysBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SynapsysBroadcastReceiver", intent.toString());
		
		Bundle bundle = intent.getExtras();
		
		boolean isReady = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_USB_READY);
		boolean isConnected = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_CONNECTION);
		
		SynapsysApplication mApplication = (SynapsysApplication) context.getApplicationContext();
		if (isConnected) {
			mApplication.startStreaming();
			
		} else {
			mApplication.stopStreaming();
		}
		
		Toast.makeText(context, "Ready : " + isReady + ", Connected : " + isConnected, Toast.LENGTH_SHORT).show();
	}

}
