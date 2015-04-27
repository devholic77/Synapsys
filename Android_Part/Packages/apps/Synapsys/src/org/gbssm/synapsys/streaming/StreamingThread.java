package org.gbssm.synapsys.streaming;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.RejectedExecutionException;

import org.gbssm.synapsys.global.SynapsysApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.view.WindowManager;

/**
 * 
 * @author Yeonho.Kim
 * @since  2015.03.30
 *
 */
public class StreamingThread extends Thread {
	protected final static boolean DEBUG = false;
	private final static String TAG = "Synapsys_StreamingThread";
	
	private final static int TIMEOUT = 10000;	// ms
	private final static int MAX_SCREEN_SIZE = 1000000;	// bytes
	
	private final SynapsysApplication mApplication;
	
	private DisplayMetrics mScreenMetrics;
	private Socket mStreamingSocket;
	private DataInputStream mInputStream;
	private DataOutputStream mOutputStream;
	
	private boolean isDestroyed;
	
	public StreamingThread(SynapsysApplication application) {
		mApplication = application;
		mScreenMetrics = new DisplayMetrics();
		
		WindowManager mWindowManager = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(mScreenMetrics);
		
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
			if (DEBUG)
				e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		if (DEBUG)
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
			
			if (DEBUG)
				e.printStackTrace();
		} 

		if (DEBUG)
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
			;
		} finally {
			mInputStream = null;
		}
		
	}
	
	public void send(String str) {
		if (str == null)
			return;
		
		if (!isDestroyed && mOutputStream != null && runningCount > 0) {
			try {
				mOutputStream.write(str.getBytes());
				mOutputStream.flush();
				
			} catch (Exception e) { ; }
		}
	}
	
	void waitingConnection() throws Exception {
		if (waitingCount > 0 || runningCount > 0)
			throw new RejectedExecutionException("Another DisplayThread is running.");
			
		waiting(true);

		if (DEBUG)
			Log.d(TAG, "StreamingThread_Run()_Port : " + mListenSocket.getLocalPort());
		do {
			synchronized (this) {
				try {
					mStreamingSocket = mListenSocket.accept();

					mInputStream = new DataInputStream(new BufferedInputStream(mStreamingSocket.getInputStream()));
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
		
		if (DEBUG)
			Log.d(TAG, "DisplayThread_Connected!");
		Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_CONNECTED_DISPLAY).sendToTarget();
		
		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inPreferredConfig = Bitmap.Config.RGB_565;
			
			while(!isDestroyed && mInputStream != null && runningCount > 0) {
				try {
					int size = mInputStream.readInt();
					if (size < 0 || size > MAX_SCREEN_SIZE)
						continue;

					byte[] bytes = new byte[size];
					mOutputStream.write("OK".getBytes());
					mInputStream.readFully(bytes);
					
					StreamingView view = mApplication.getStreamingView();
					if (view != null) {
						
						// Bitmap 준비.
						option.inJustDecodeBounds = true;
						BitmapFactory.decodeByteArray(bytes, 0, size, option);
						
						option.inJustDecodeBounds = false;
						option.inPreferredConfig = Bitmap.Config.RGB_565;
						option.inSampleSize = Math.min(	option.outWidth/mScreenMetrics.widthPixels, 
														option.outHeight/mScreenMetrics.heightPixels );
						
						view.mStreamingImage = BitmapFactory.decodeByteArray(bytes, 0, size, option);	
						
						bytes = null;
						view.switchSurfaceImage();
						
						if (view.mStreamingImage != null)
							view.mStreamingImage.recycle();
						
						view.mStreamingImage = null;
					} 
					
				} catch (IOException e) {
					if (!isDestroyed) 
						Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_EXIT_DISPLAY).sendToTarget();
					break;
					
				} finally { ; }
			}
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		running(false);
		Message.obtain(mApplication.getHandler(), SynapsysApplication.MSG_DESTROYED_DISPLAY).sendToTarget();
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
		if (DEBUG)
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
		if (DEBUG)
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
		if (DEBUG)
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
