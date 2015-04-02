package org.gbssm.synapsys.global;

import java.net.Socket;

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
	
	static boolean DEBUG = true;

	protected SynapsysManager mSynapsysManager;
	
	protected StreamingView mStreamingView;
	
	protected StreamingThread mStreamingThread;
	
	
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
		if (mStreamingThread != null) {
			try {
				mStreamingThread.destroy();
				mStreamingThread.join(1000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		super.onTerminate();
	}

	public void startStreaming() {
		if (mStreamingThread == null)
			mStreamingThread = new StreamingThread(this);
			
		mStreamingThread.start();
	}
	
	public void stopStreaming() {
		if (mStreamingThread != null)
			mStreamingThread.destroy();
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
	
	public boolean isSynapsysConnected() {
		if (DEBUG)
			return true;
		
		if (mStreamingThread != null)
			return mStreamingThread.isConnected();
		
		return false;
	}
}
