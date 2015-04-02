package org.gbssm.synapsys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Message;
import android.util.Log;
import android.util.Slog;

/**
 * Media-Thumbnail SynapsysThread.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.25
 *
 */
public class SynapsysMediaThread extends SynapsysThread {

	private final ConnectionBox mBox;

	private Socket mMediaSocket;
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	public SynapsysMediaThread(SynapsysHandler handler, ConnectionBox box) {
		super(handler);
		mBox = box;

		try {
			synchronized (LOCK) {
				// 서버 소켓이 활성화 되어있지만, 새로운 포트를 할당할 경우, 
				// 기존의 서버 소켓을 닫고 새로운 서버 소켓을 생성한다.
				if (mListenSocket != null && mListenSocket.getLocalPort() != box.port) {
					try {
						mListenSocket.close();
						mListenSocket = null;
						
					} catch (IOException e) { ; }
				}
				
				if (mListenSocket == null) {
					mListenSocket = new ServerSocket(box.port);
					mListenSocket.setSoTimeout(TIMEOUT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			waiting(false);
			e.printStackTrace();
		} 

		Log.d(TAG, "MediaThread_Destroyed!");
	}
	
	@Override
	public void destroy() {
		try {
			isDestroyed = true;
			interrupt();
			
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
		
		Log.d(TAG, "MediaThread_Run()_Port : " + mBox.port);
		do {
			synchronized (LOCK) {
				try {
					mMediaSocket = mListenSocket.accept();
						
					mDIS = new DataInputStream(new BufferedInputStream(mMediaSocket.getInputStream()));
					mDOS = new DataOutputStream(new BufferedOutputStream(mMediaSocket.getOutputStream()));
	
				} catch (SocketTimeoutException e) { ; }
			}
		} while (mMediaSocket == null && !isDestroyed);
			
		waiting(false);
	}
	
	void runningConnection() {
		if (isDestroyed)
			return;
		
		Log.d(TAG, "MediaThread_Connected!");
		
		running(true);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_CONNECTED_MEDIA);
		try {
			while(!isDestroyed && mDIS != null) {
				byte[] bytes = new byte[MediaProtocol.MSG_SIZE];
				try {
					mDIS.read(bytes);
					Log.d(TAG, "MediaThread_Received! : " + new String(bytes, "UTF-8"));
					
				} catch (SocketException e) {
					if (!isDestroyed) 
						mHandler.sendMessageDelayed(Message.obtain(mHandler, SynapsysHandler.MSG_EXIT_MEDIA, mBox), 1000);
					break;
				
				} catch (IOException e) {
					// Reading Errors occur.
				} 
			}
		} catch (Exception e) {
			
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
			
		} catch (SocketException e) {
			if (!isDestroyed) 
				Message.obtain(mHandler, SynapsysHandler.MSG_EXIT_CONTROL, mBox).sendToTarget();
		
			throw new RejectedExecutionException("Connection Socket is closed.");
			
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	

	
	// *** STATIC PART *** //
	/**
	 * LOCK Object.
	 */
	private static final Object LOCK = new Object();
	/**
	 * Media Connection 전역 서버소켓.
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
	static boolean isAbleToCreate() {
		Slog.v(TAG, "Media_isAbleToCreate : " + waitingCount + " / " + runningCount);
		synchronized (LOCK) {
			return ((waitingCount <= 0) && (runningCount <= 0));
		}
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
		Slog.v(TAG, "Media_isWaiting : " + waitingCount);
		synchronized (LOCK) {
			return (waitingCount > 0);
		}
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
		Slog.v(TAG, "Media_isRunning : " + runningCount);
		synchronized(LOCK) {
			return (runningCount > 0);
		}
	}
}
