package org.gbssm.synapsys.streaming;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.global.SynapsysApplication;

import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.util.Slog;

/**
 * 
 * @author Yeonho.Kim
 * @since  2015.03.30
 *
 */
public class StreamingThread extends Thread {
	
	private final static String TAG = "Synapsys_StreamingThread";
	private final static int TIMEOUT = 10000;	// ms
	
	private final SynapsysApplication mApplication;
	
	private Socket mStreamingSocket;
	private DataInputStream mInputStream;
	private DataOutputStream mOutputStream;
	
	private boolean isDestroyed;
	
	public StreamingThread(SynapsysApplication application) {
		mApplication = application;

		int port = mApplication.getSynapsysManager().requestDisplayConnection();
		if (port == -1)
			throw new RejectedExecutionException("Port Number isn't adequte.");
		
		try {
			synchronized (this) {
				// 서버 소켓이 활성화 되어있지만, 새로운 포트를 할당할 경우, 
				// 기존의 서버 소켓을 닫고 새로운 서버 소켓을 생성한다.
				if (mListenSocket != null && mListenSocket.getLocalPort() != port) {
					try {
						mListenSocket.close();
						mListenSocket = null;
						
					} catch (IOException e) { ; }
				}
				
				if (mListenSocket == null) {
					mListenSocket = new ServerSocket(port);
					mListenSocket.setSoTimeout(TIMEOUT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		Log.d(TAG, "StreamingThread is running.");
		try {
			waitingConnection();
			runningConnection();
			
		} catch (RejectedExecutionException e) {
			// Waiting Thread already exists.
			// Just Finish;
			return;
			
		} catch (Exception e) {
			waiting(false);
			e.printStackTrace();
		} 
		
		Log.d(TAG, "StreamingThread is dead.");
	}
	
	@Override
	public void destroy() {
		try {
			isDestroyed = true;
			interrupt();
			
			if (mStreamingSocket != null) {
				mStreamingSocket.close();
				mStreamingSocket = null;
			}
		} catch (IOException e) {
			
		} finally {
			mInputStream = null;
		}
		
	}
	
	void waitingConnection() throws Exception {
		if (waitingCount > 0 || runningCount > 0)
			throw new RejectedExecutionException("Another DisplayThread is running.");
			
		waiting(true);
		
		//Log.d(TAG, "StreamingThread_Run()_Port : " + mBox.port);
		do {
			synchronized (this) {
				try {
					mStreamingSocket = mListenSocket.accept();

					mInputStream = new DataInputStream(new BufferedInputStream(
									mStreamingSocket.getInputStream()));
					mOutputStream = new DataOutputStream(mStreamingSocket.getOutputStream());

				} catch (SocketTimeoutException e) { ; }
			}
		} while (mStreamingSocket == null && !isDestroyed);
		
		waiting(false);
	}
	
	void runningConnection() throws Exception {
		if (isDestroyed)
			return;
		
		running(true);
		Log.d(TAG, "DisplayThread_Connected!");
		Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_CONNECTED_DISPLAY).sendToTarget();
		
		try {
			while(!isDestroyed && mInputStream != null && runningCount > 0) {
				try {
					int size = mInputStream.readInt();
					if (size < 0)
						continue;

					byte[] bytes = new byte[size];
					mOutputStream.write("OK".getBytes());
					mInputStream.readFully(bytes);
					
					StreamingView view = mApplication.getStreamingView();
					if (view != null) {
						view.mStreamingImage = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
						view.switchSurfaceImage();
					}
					
				} catch (IOException e) {
					if (!isDestroyed) 
						Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_EXIT_DISPLAY).sendToTarget();
					break;
					
				} finally {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_DESTROYED_DISPLAY).sendToTarget();
		running(false);
	}

	
	
	// *** STATIC PART *** //
	/**
	 * LOCK Object.
	 */
	private static final Object LOCK = new Object();
	
	/**
	 * Display Connection 전역 서버소켓.
	 */
	static ServerSocket mListenSocket;

	/**
	  *  대기 중인 Thread의 전역 카운터.
	 */
	protected static int waitingCount = 0;

	/**
	  *  가동 중인 Thread의 전역 카운터.
	 */
	protected static int runningCount = 0;
	
	
	/**
	 * 
	 * @return
	 */
	public static boolean isAbleToCreate() {
		Slog.v(TAG, "Display_isAbleToCreate : " + waitingCount + " / " + runningCount);
		
		boolean result;
		synchronized (LOCK) {
			result = ((waitingCount <= 0) && (runningCount <= 0));
		}
		return result;
	}

	/**
	 * 대기 중인 Thread의 전역 카운터를 조절하는 메소드. (True-증가 / False-감소)
	 * @param enable
	 */
	protected static void waiting(boolean enable) {
		synchronized(LOCK) {
			if (enable)
				waitingCount++;
			else
				waitingCount = waitingCount>0? waitingCount-1 : 0;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static boolean isWaiting() {
		Slog.v(TAG, "Display_isWaiting : " + waitingCount);
		
		boolean result;
		synchronized (LOCK) {
			result = (waitingCount > 0);
		}
		return result;
	}
	
	/**
	 * 가동 중인 Thread의 전역 카운터를 조절하는 메소드. (True-증가 / False-감소)
	 * @param enable
	 */
	protected static void running(boolean enable) {
		synchronized(LOCK) {
			if (enable)
				runningCount++;
			else
				runningCount = runningCount > 0? runningCount-1 : 0;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static boolean isRunning() {
		Slog.v(TAG, "Display_isRunning : " + runningCount);
		
		boolean result;
		synchronized(LOCK) {
			result = (runningCount > 0);
		}
		return result;
	}
	
	/**
	 * 
	 */
	public static void reset() {
		runningCount = 0;
	}

	public static int getLocalPort() {
		if (mListenSocket != null)
			return mListenSocket.getLocalPort();
		
		return -1;
	}

}
