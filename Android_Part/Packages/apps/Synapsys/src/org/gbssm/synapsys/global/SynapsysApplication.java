package org.gbssm.synapsys.global;

import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.MainActivity;
import org.gbssm.synapsys.R;
import org.gbssm.synapsys.SynapsysManager;
import org.gbssm.synapsys.streaming.StreamingThread;
import org.gbssm.synapsys.streaming.StreamingView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application {

	public static final int MSG_PROCEED_DISPLAY = 0xC300;
	
	public static final int MSG_EXIT_DISPLAY = 0xC30E;

	public static final int MSG_CONNECTED_DISPLAY = 0xC31C;

	public static final int MSG_DESTROYED_DISPLAY = 0xC31D;

	public static final int MSG_TOAST = 0x0;

	
	protected SynapsysManager mSynapsysManager;

	protected MainActivity mStreamingActivity;
	protected StreamingView mStreamingView;
	protected StreamingThread mStreamingThread;
	
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROCEED_DISPLAY:
				if (msg.obj != null) 
					startStreaming();
				break;
				
			case MSG_EXIT_DISPLAY:
				sendEmptyMessageDelayed(MSG_PROCEED_DISPLAY, 250);
				break;
				
			case MSG_CONNECTED_DISPLAY:
				isDisplayed = true;
				if (mStreamingActivity != null)
					mStreamingActivity.notifyDisplaying(true);
				break;
				
			case MSG_DESTROYED_DISPLAY:
				isDisplayed = false;
				if (mStreamingActivity != null)
					mStreamingActivity.notifyDisplaying(false);
				break;
				
			case MSG_TOAST:
				if (mToast != null) {
					mToast.setText((String)msg.obj);
					mToast.show();
				}
				break;
			}
		}
	};
	
	private Toast mToast;
	private Toast mSynapsysToast;
	
	private boolean isControllerConnected;
	private boolean isDisplayed;
	
	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();
		
		mSynapsysManager = (SynapsysManager) getSystemService(SYNAPSYS_SERVICE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	
	@Override
	public void onTerminate() {
		stopStreaming();
		super.onTerminate();
	}

	public void startStreaming() {
		if (StreamingThread.isAbleToCreate()) {
			try {
				mStreamingThread = new StreamingThread(this);
				mStreamingThread.start();
				
			} catch (RejectedExecutionException e) { ; }
		}
	}
	
	public void stopStreaming() {
		StreamingThread.reset();
		if (mStreamingThread != null) {
			try {
				mStreamingThread.destroy();
				mStreamingThread.join(1000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
				
			} finally {
				mStreamingThread = null;
			}
		}
	}
	
	public void setControllerConnected(boolean connected) {
		isControllerConnected = connected;
		
		if (connected)
			notifySynapsysDevice();
		else
			mSynapsysToast = null;
	}
	
	public void notifyStreamingView(StreamingView view) {
		mStreamingView = view;
	}
	
	public void notifyStreamingActivity(MainActivity activity) {
		mStreamingActivity = activity;
	}
	
	public void notifySynapsysDevice() {
		init();
		
		if (mSynapsysToast != null) 
			mSynapsysToast.show();
	}
	
	
	public SynapsysManager getSynapsysManager() {
		if (mSynapsysManager == null)
			mSynapsysManager = (SynapsysManager) getSystemService(SYNAPSYS_SERVICE);
			
		return mSynapsysManager;
	}
	
	public Handler getHandler() {
		return mHandler;
	}
	
	public StreamingView getStreamingView() {
		return mStreamingView;
	}
	
	public boolean isControllerConnected() {
		return isControllerConnected;
	}
	
	public boolean isDisplaying() {
		return isDisplayed;
	}
	

	@SuppressLint("ShowToast")
	private void init() {
		int port = mSynapsysManager.requestDisplayConnection();
		if (port == -1 || mSynapsysToast != null)
			return ;
		
		int deviceOrder = (port-1234)/3;
		int icon = (deviceOrder % 2 == 0)? R.drawable.icon1 : R.drawable.icon2;
		
		String deviceName = "Device" + (deviceOrder+1);
		Drawable drawable = getResources().getDrawable(icon);
		
		TextView mToastView = (TextView) LayoutInflater.from(this).inflate(R.layout.synapsys_toast, null, false);
		mToastView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
		mToastView.setText(deviceName);
		
		mSynapsysToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		mSynapsysToast.setView(mToastView);
	}
	
}
