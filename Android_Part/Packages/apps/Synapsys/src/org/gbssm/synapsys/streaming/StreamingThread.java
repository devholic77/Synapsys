package org.gbssm.synapsys.streaming;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.gbssm.synapsys.SynapsysManager;
import org.gbssm.synapsys.global.SynapsysApplication;

import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 * @since  2015.03.30
 *
 */
public class StreamingThread extends Thread {
	
	private final static int FRAME_MAX_LENGTH = 40000;
	
	private final SynapsysApplication mApplication;
	
	private Socket mStreamingSocket;
	private DataInputStream mInputStream;
	
	public StreamingThread(SynapsysApplication application) {
		mApplication = application;
	}
	
	
	@Override
	public void run() {
		Log.d("SynapsysApp_StreamingThread", "StreamingThread is running.");
		try {
			init();
		
			while (mStreamingSocket.isConnected()) {
				try {
					byte[] frameData = new byte[FRAME_MAX_LENGTH];
					
					
					// TODO : frameData 크기 조절.
					mInputStream.readFully(frameData);
					
					StreamingView view = mApplication.getStreamingView();
					view.mStreamingImage = BitmapFactory.decodeStream(
							new ByteArrayInputStream(frameData));
					view.switchSurfaceImage();
					
				} catch (IOException e) {
					
				}
			}
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		Log.d("SynapsysApp_StreamingThread", "StreamingThread is dead.");
	}
	
	private void init() throws IOException, Exception {
		SynapsysManager synapsysManager = mApplication.getSynapsysManager();
		
		mStreamingSocket = synapsysManager.requestDisplayConnection();
		if (mStreamingSocket == null)
			throw new Exception();

		mStreamingSocket.shutdownOutput();
		mInputStream = new DataInputStream(
				new BufferedInputStream(
						mStreamingSocket.getInputStream(), FRAME_MAX_LENGTH));
	}
	
	public void destroy() {
		try {
			if (mStreamingSocket != null)
				mStreamingSocket.close();
		} catch (IOException e) { ; }
		
		mStreamingSocket = null;
	}
	

	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (mStreamingSocket != null)
			return mStreamingSocket.isConnected();

		return true;
	}

}
