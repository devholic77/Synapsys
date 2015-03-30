package org.gbssm.synapsys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
 * @since 2015.03.10
 *
 */
public class SynapsysControlThread extends SynapsysThread {
	
	private final ConnectionBox mBox;
	
	private ServerSocket mListenSocket;
	private Socket mConnectedSocket;
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	public SynapsysControlThread(SynapsysHandler handler, ConnectionBox box) {
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

		Log.d(TAG, "ControlThread_Destroyed!");
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
		try {
			final int port = mBox.port;
			mListenSocket = new ServerSocket(port);	
			mListenSocket.setSoTimeout(TIMEOUT);
			mListenSocket.setReuseAddress(true);

			Log.d(TAG, "ControlThread_Run()_Port : " + port);
			do {
				try {
					mConnectedSocket = mListenSocket.accept();
					mDIS = new DataInputStream(new BufferedInputStream(mConnectedSocket.getInputStream()));
					mDOS = new DataOutputStream(new BufferedOutputStream(mConnectedSocket.getOutputStream()));

				} catch (SocketTimeoutException e) { ; }
			} while (mConnectedSocket == null && !isDestroyed);
			
			mListenSocket.close();
			
		} catch (Exception e) { ; } 
		waiting(false);
	}
	
	void runningConnection() {
		if (isDestroyed)
			return;
		
		Log.d(TAG, "ControlThread_Connected!");
		
		running(true);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_CONNECTED_CONTROL);
		
		while(!isDestroyed && mDIS != null) {

			byte[] bytes = new byte[ControlProtocol.MSG_SIZE];
			try {
				Log.d(TAG, "ControlThread_Received!");
				mDIS.readFully(bytes);
				
			} catch (EOFException e) { 
			
			} catch (IOException e) {
				if (!isDestroyed)
					mHandler.sendMessageDelayed(Message.obtain(mHandler, SynapsysHandler.MSG_PROCEED_CONTROL, mBox), 100);
				break;
				
			} finally {
				ControlProtocol<?, ?, ?>[] protocols = ControlProtocol.decode(bytes);
				for (int itr = 0; itr < protocols.length; itr++)
					protocols[itr].process(mHandler.getService());
				
				// TEST_DUMMY
				ControlProtocol<Integer, Integer, Integer> protocol = new ControlProtocol<Integer, Integer, Integer>(0);
				protocol.mCode = 10;
				protocol.mValue1 = 100;
				protocol.mValue2 = 200;
				protocol.mValue3 = 300;
				
				Log.i("Synapsys_Message", "ControlThread_Encoding : " + 
						protocol.mType +" / " + protocol.mCode + " / " + protocol.mValue1 + " / " + protocol.mValue2 + " / " + protocol.mValue3);
				
				send(protocol);
			}
		}
		
		running(false);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_DESTROYED_CONTROL);
	}

	void send(ControlProtocol<?, ?, ?> message) {
		try {
			if (mDOS != null && message != null) {
				mDOS.write(message.encode());
				mDOS.flush();
				
				Log.d(TAG, "ControlThread_Send! : " + message.toString());
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
