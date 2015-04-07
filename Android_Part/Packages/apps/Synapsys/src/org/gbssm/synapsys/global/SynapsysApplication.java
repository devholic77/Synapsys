package org.gbssm.synapsys.global;

import org.gbssm.synapsys.SynapsysManager;
import org.gbssm.synapsys.streaming.StreamingThread;
import org.gbssm.synapsys.streaming.StreamingView;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application {
	
	protected SynapsysManager mSynapsysManager;
	
	protected StreamingView mStreamingView;
	
	protected StreamingThread mStreamingThread;
	
	private boolean isControllerConnected;
	
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
		if (!isStreamerConnected()) {
			mStreamingThread = new StreamingThread(this);
			mStreamingThread.start();
		}
	}
	
	public void stopStreaming() {
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
	
	
	public SynapsysManager getSynapsysManager() {
		if (mSynapsysManager == null)
			mSynapsysManager = (SynapsysManager) getSystemService(SYNAPSYS_SERVICE);
			
		return mSynapsysManager;
	}
	
	public StreamingView getStreamingView() {
		return mStreamingView;
	}
	
	public boolean isStreamerConnected() {
		if (mStreamingThread != null)
			return mStreamingThread.isConnected();
		
		return false;
	}
	
	public boolean isControllerConnected() {
		return isControllerConnected;
	}
}
