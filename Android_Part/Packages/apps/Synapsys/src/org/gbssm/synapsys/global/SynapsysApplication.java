package org.gbssm.synapsys.global;

import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.MainActivity;
import org.gbssm.synapsys.SynapsysManager;
import org.gbssm.synapsys.streaming.StreamingThread;
import org.gbssm.synapsys.streaming.StreamingView;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

	
	protected SynapsysManager mSynapsysManager;
	
	protected StreamingView mStreamingView;
	protected StreamingThread mStreamingThread;
	
	protected MainActivity mStreamingActivity;
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
			}
		}
	};
	
	private boolean isControllerConnected;
	private boolean isDisplayed;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mSynapsysManager = (SynapsysManager) getSystemService(SYNAPSYS_SERVICE);
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
	}
	
	public void notifyStreamingView(StreamingView view) {
		mStreamingView = view;
	}
	
	public void notifyStreamingActivity(MainActivity activity) {
		mStreamingActivity = activity;
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
}
