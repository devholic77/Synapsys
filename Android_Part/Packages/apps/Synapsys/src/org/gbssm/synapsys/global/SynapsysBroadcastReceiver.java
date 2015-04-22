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
	
	/**
	 * 
	 */
	boolean isUSBready;
	/**
	 * 
	 */
	boolean isPCready;
	/**
	 * 
	 */
	boolean isConnected;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (mToast == null)
			mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
		
		Bundle bundle = intent.getExtras();
		
		boolean usbReady = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_USB_READY);
		boolean pcReady = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_PC_READY);
		boolean connected = bundle.getBoolean(SynapsysManager.BROADCAST_EXTRA_CONNECTION);

		SynapsysApplication mApplication = (SynapsysApplication) context.getApplicationContext();
		if (usbReady && pcReady) {
			mApplication.setControllerConnected(connected);
			
			if (!isConnected && !connected) {
				mApplication.startStreaming();
				
				Intent newIntent = new Intent(context, MainActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);

				String message = "PC와 연결되었습니다 !!";
				mToast.setText(message);
				mToast.show();
			}
			
			
		} else if (!usbReady || !pcReady) {
			mApplication.setControllerConnected(false);
			mApplication.stopStreaming();
			
			if (isUSBready || isPCready) {
				String message = "PC와 연결이 끊어졌습니다..";
				mToast.setText(message);
				mToast.show();
			}
		}
		
		isUSBready = usbReady;
		isPCready = pcReady;
		isConnected = connected;
	}

}
