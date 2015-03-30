package org.gbssm.synapsys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.25
 *
 */
public class SynapsysMediaThread extends SynapsysThread {

	private final ConnectionBox mBox;
	
	private ServerSocket mListenSocket;
	private Socket mMediaSocket;
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	public SynapsysMediaThread(SynapsysHandler handler, ConnectionBox box) {
		super(handler);
		mBox = box;
	}
	
	@Override
	public void run() {
		try {
			waitingConnection();
			runningConnection();
			
		} catch (RejectedExecutionException e) {
			// Waiting Thread already exists.
			// Just Finish;
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		Log.d(TAG, "MediaThread_Destroyed!");
	}
	
	@Override
	public void destroy() {
		try {
			isDestroyed = true;
			interrupt();
			
			if (mListenSocket != null) {
				mListenSocket.close();
				mListenSocket = null;
			}
			
			if (mMediaSocket != null) {
				mMediaSocket.close();
				mMediaSocket = null;
			}
		} catch (IOException e) {
			
		} finally {
			mDIS = null;
			mDOS = null;
		}
		
	}
	
	
	void waitingConnection() throws Exception {
		if (waitingCount > 0 || runningCount > 0)
			throw new RejectedExecutionException("Another MediaThread is running.");
			
		waiting(true);
		try {
			final int port = mBox.port;
			mListenSocket = new ServerSocket(port);	
			mListenSocket.setSoTimeout(TIMEOUT);
			mListenSocket.setReuseAddress(true);

			Log.d(TAG, "MediaThread_Run()_Port : " + port);
			do {
				try {
					mMediaSocket = mListenSocket.accept();
					mDIS = new DataInputStream(new BufferedInputStream(mMediaSocket.getInputStream()));
					mDOS = new DataOutputStream(new BufferedOutputStream(mMediaSocket.getOutputStream()));

				} catch (SocketTimeoutException e) { ; }
			} while (mMediaSocket == null && !isDestroyed);
			
			mListenSocket.close();
			
		} catch (Exception e) { ; } 
		waiting(false);
	}
	
	void runningConnection() {
		if (isDestroyed)
			return;
		
		Log.d(TAG, "MediaThread_Connected!");
		
		running(true);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_CONNECTED_MEDIA);
		
		while(!isDestroyed && mDIS != null) {

			byte[] bytes = new byte[MediaProtocol.MSG_SIZE];
			try {
				mDIS.readFully(bytes);
				Log.d(TAG, "MediaThread_Received!");
				
			
			} catch (IOException e) {
				// Reading Errors occur.
			}
		}
		
		running(false);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_DESTROYED_MEDIA);
	}

	void send(MediaProtocol message) {
		try {
			if (mDOS != null && message != null) {
				mDOS.write(message.encode());
				mDOS.flush();
				
				Log.d(TAG, "MediaThread_Send! : " + message.toString());
			}
			
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	

	/**
	 * LOCK Object.
	 */
	private static final Object LOCK = new Object();
	
	/**
	 * 
	 * @return
	 */
	static boolean isAbleToCreate() {
		synchronized (LOCK) {
			return ((waitingCount <= 0) && (runningCount <= 0));
		}
	}

	/**
	  *  대기 중인 Thread의 전역 카운터.
	 */
	protected static int waitingCount = 0;

	/**
	 * 대기 중인 Thread의 전역 카운터를 조절하는 메소드. (True-증가 / False-감소)
	 * @param enable
	 */
	protected static void waiting(boolean enable) {
		synchronized(LOCK) {
			if (enable)
				waitingCount++;
			else
				waitingCount--;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static boolean isWaiting() {
		synchronized (LOCK) {
			return (waitingCount > 0);
		}
	}
	
	/**
	  *  가동 중인 Thread의 전역 카운터.
	 */
	protected static int runningCount = 0;
	
	/**
	 * 가동 중인 Thread의 전역 카운터를 조절하는 메소드. (True-증가 / False-감소)
	 * @param enable
	 */
	protected static void running(boolean enable) {
		synchronized(LOCK) {
			if (enable)
				runningCount++;
			else
				runningCount--;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static boolean isRunning() {
		synchronized(LOCK) {
			return (runningCount > 0);
		}
	}
}
