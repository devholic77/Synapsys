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
import java.nio.ByteBuffer;
import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.MessageProtocol.ControlProtocol;
import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Message;
import android.util.Log;
import android.util.Slog;

/**
 * Data-Control SynapsysThread.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.10
 *
 */
public class SynapsysControlThread extends SynapsysThread {

	private final ConnectionBox mBox;
	
	private Socket mConnectedSocket;
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	public SynapsysControlThread(SynapsysHandler handler, ConnectionBox box) {
		super(handler);
		mBox = box;
		
		try {
			synchronized (this) {
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

		Log.d(TAG, "ControlThread_Destroyed!");
	}
	
	@Override
	public void destroy() {
		try {
			isDestroyed = true;
			interrupt();
			
			if (mConnectedSocket != null) {
				mConnectedSocket.close();
				mConnectedSocket = null;
			}
		} catch (IOException e) {
			
		} finally {
			mDIS = null;
			mDOS = null;
		}
		
	}
	
	
	void waitingConnection() throws Exception {
		if (waitingCount > 0 || runningCount > 0)
			throw new RejectedExecutionException("Another ControlThread is running.");
			
		waiting(true);
		
		Log.d(TAG, "ControlThread_Run()_Port : " + mBox.port);
		do {
			synchronized (this) {
				try {
					mConnectedSocket = mListenSocket.accept();

					mDIS = new DataInputStream(new BufferedInputStream(mConnectedSocket.getInputStream()));
					mDOS = new DataOutputStream(new BufferedOutputStream(mConnectedSocket.getOutputStream()));

				} catch (SocketTimeoutException e) { ; }
			}
		} while (mConnectedSocket == null && !isDestroyed);
		
		waiting(false);
	}
	
	void runningConnection() throws Exception {
		if (isDestroyed)
			return;

		Log.d(TAG, "ControlThread_Connected!");
		
		running(true);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_CONNECTED_CONTROL);
		
		try {
			while(!isDestroyed && mDIS != null && runningCount > 0) {
				try {
					byte[] bytes = new byte[ControlProtocol.MSG_SIZE];
					int read = mDIS.read(bytes);
					
					ControlProtocol<?, ?, ?>[] protocols = ControlProtocol.decode(
							ByteBuffer.wrap(bytes, 0, read).array());
					
					for (ControlProtocol<?, ?, ?> protocol : protocols) {
						protocol.process(mHandler.getService());
						protocol.destroy();
					}
					
				} catch (IOException e) {
					if (!isDestroyed) 
						Message.obtain(mHandler, SynapsysHandler.MSG_EXIT_CONTROL, mBox).sendToTarget();
					break;
					
				} finally {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		running(false);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_DESTROYED_CONTROL);
	}

	void send(ControlProtocol<?, ?, ?> message) {
		try {
			if (mDOS != null && message != null) {
				mDOS.write(message.encode());
				mDOS.flush();

				Log.i("Synapsys_Message", "ControlThread : " + message.mType +" / " + message.mCode + " / " + message.mValue1 + " / " + message.mValue2 + " / " + message.mValue3);
				message.destroy();
				message = null;
			}
			
		} catch (SocketException e) {
			if (!isDestroyed) 
				Message.obtain(mHandler, SynapsysHandler.MSG_EXIT_CONTROL, mBox).sendToTarget();
		
			throw new RejectedExecutionException("Connection Socket is closed.");
			
		} catch (IOException e) { 
			if (!isDestroyed) 
				Message.obtain(mHandler, SynapsysHandler.MSG_EXIT_CONTROL, mBox).sendToTarget();
			
			e.printStackTrace();
		}
	}
	
	

	// *** STATIC PART *** //
	/**
	 * LOCK Object.
	 */
	private static final Object LOCK = new Object();
	
	/**
	 * Control Connection 전역 서버소켓.
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
		Slog.v(TAG, "Control_isAbleToCreate : " + waitingCount + " / " + runningCount);
		
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
				waitingCount = waitingCount > 0? waitingCount-1 : 0;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static boolean isWaiting() {
		Slog.v(TAG, "Control_isWaiting : " + waitingCount);
		
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
		Slog.v(TAG, "Control_isRunning : " + runningCount);
		
		boolean result;
		synchronized(LOCK) {
			result = (runningCount > 0);
		}
		return result;
	}

	static void reset() {
		runningCount = 0;
	}
}
