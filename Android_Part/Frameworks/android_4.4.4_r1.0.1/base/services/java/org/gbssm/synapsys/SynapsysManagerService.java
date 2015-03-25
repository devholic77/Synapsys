package org.gbssm.synapsys;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;

/**
 * 
 * SynapsysManager의 기능을 구현하는 시스템 서비스.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.06
 *
 */
public class SynapsysManagerService extends ISynapsysManager.Stub {
	
	public static final int EVENT_ADB_ENABLE = 1;
	public static final int EVENT_USB_CONNECT = 2;
	
	static final String TAG = "SynapsysManagerService";	
	
	static final int LISTEN_PORT = 30300;
	
	final Context mContext;
	
	
	private boolean isServiceRunning;
	
	private Socket mControlSocket;
	
	private ConnectionFileDetector mConnectionDetector;
	private SynapsysControlThread mControlThread;
	private SynapsysMediaThread mMediaThread;
	
	private ConnectionBox mConnectionBox;
	private ConnectionBox mMediaBox;
	
	
	public SynapsysManagerService(Context context) {
		mContext = context; 
	}
	
	public int requestDisplayConnection() throws RemoteException {
		Slog.i(TAG, "reqeustDisplayConnection()");
		if (mConnectionBox != null)
			return mConnectionBox.port + 1;
		
		return -1; // Port Number
	}
	
	public boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y) throws RemoteException {
		// TODO : Windows PC로 Touch Event 전송.
		Slog.i(TAG, "invokeMouseEventFromTouch : event=" + event_id + " / x=" + event_x + " / y=" + event_y);
		return false;
	}
	
	public boolean invokeKeyboardEvent(int event_id, int key_code) throws RemoteException {
		Slog.i(TAG, "invokeKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		return false;
	}
	
	public boolean invokeNotificationEvent() throws RemoteException {
		// TODO : Windows PC로 Notification Event 전송.
		return false;
	}
	
	public boolean invokeTaskInfoEvents() throws RemoteException {
		// TODO : WIndows PC로 Task-Info Event 전송.
		return false;
	}
	
	
	public boolean interpolateMouseEvent(int event_id, float event_x, float event_y) throws RemoteException { 
		//  TODO : Windows PC로부터 Touch Event 받기.
		Slog.i(TAG, "interpolateMouseEvent : event=" + event_id + " / x=" + event_x + " / y=" + event_y);
		return false;
	}
	
	public boolean interpolateKeyboardEvent(int event_id, int key_code) throws RemoteException { 
		//  TODO : Windows PC로부터 Keyboard Event 받기.
		Slog.i(TAG, "interpolateKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		return false;
	}
	
	public boolean interpolateNotificationEvent() throws RemoteException {
		//  TODO : Windows PC로부터 Notification Event 받기. 
		return false;
	}
	
	public boolean interpolateTaskInfoEvent() throws RemoteException {
		// TODO : Windows PC로부터 Task-Info Event 받기.
		return false;
	}
	
	
	/**
	 * USB 연결 상태를 탐지하여 이벤트를 발생시킨다.   
	 * 
	 * @param event_id 변화를 감지한 이벤트 타입
	 * @param event 이벤트 값
	 * @param another 변화하지 않은 다른 이벤트 값
	 */
	public void dispatchUsbConnectionEvent(int event_id, boolean event, boolean another) {
		Slog.d(TAG, "EventID : " + (event_id == 1 ? "ADB" : "CONN") + " / Event : " + event + " / Another : " + another);
		
		// 변화한 이벤트 타입에 따라 처리.
		switch (event_id) {
		case EVENT_ADB_ENABLE:
		case EVENT_USB_CONNECT:
			// ADB가 활성화된 상태에서 USB가 연결되었을 때, 
			// USB가 연결된 상태에서 ADB가 활성화되었을 때,
			// ConnectionFile의 변화를 감지하여, Synapsys 연결 상태를 확립한다.
			if (event && another) {
				systemReady();
				return;
			}
		}
		
		// 연결이 성립하지 않는 다른 모든  경우,
		systemStop();
	}
	
	/**
	 * 
	 */
	void systemReady() {
		mConnectionDetector = ConnectionFileDetector.getInstance(new SynapsysHandler());
		if (mConnectionDetector != null)
			mConnectionDetector.start();
	}
	
	/**
	 * 
	 */
	void systemStop() {
		if (mConnectionDetector != null)
			mConnectionDetector.stop();

	}
	
	/**
	 * SynapsysManagerService에서 기능의 일괄 처리를 위한 Handler. 
	 * 
	 * @author  Yeonho.Kim
	 * @since 2015.03.15
	 *
	 */
	class SynapsysHandler extends Handler {
		/**
		 * Handler 메시지 : 연결 성립 / 시작.
		 */
		static final int MSG_PROCEED_CONNECTION = 0x100;
		/**
		 * Handler 메시지 : 연결 해제 / 종료.
		 */
		static final int MSG_FINISHED_CONNECTION = 0xF00;
		/**
		 * 
		 */
		static final int MSG_PROCEED_MEDIA = 0x110;
		/**
		 * 
		 */
		static final int MSG_FINISHED_MEDIA = 0xFF0;
		/**
		 * 
		 */
		static final int MSG_PUSH_NOTIFICATION = 0x700;
		/**
		 * 
		 */
		static final int MSG_PULL_NOTIFICATION = 0x707;
		/**
		 * 
		 */
		static final int MSG_PUSH_TASKINFO = 0x800;
		/**
		 * 
		 */
		static final int MSG_PULL_TASKINFO = 0x808;
		
		
		@Override
		public void handleMessage(Message msg) {
			//Slog.d(TAG, "handleMessage : " + msg.what);
			
			switch (msg.what) {
			case MSG_PROCEED_CONNECTION:
				if (msg.obj != null) {
					removeMessages(MSG_PROCEED_CONNECTION);
					mConnectionBox = (ConnectionBox) msg.obj;
					
					mControlThread = new SynapsysControlThread(this, mConnectionBox);
					mControlThread.start();
				}
				break;
				
			case MSG_PROCEED_MEDIA:
				if (msg.obj != null) {
					removeMessages(MSG_PROCEED_MEDIA);
					mMediaBox = (ConnectionBox) msg.obj;
					
					mMediaThread = new SynapsysMediaThread(this, mMediaBox);
					mMediaThread.start();
				}
				break;
				
			case MSG_FINISHED_CONNECTION:
				if (mControlThread != null) {
					try {
						mControlThread.destroy();
						mControlThread.join(1000);
						
					} catch (InterruptedException e) {
					} finally {
						mControlThread = null;
					}
				}
				break;
				
			case MSG_FINISHED_MEDIA:
				if (mMediaThread != null) {
					try {
						mMediaThread.destroy();
						mMediaThread.join(1000);
						
					} catch (InterruptedException e) {
					} finally {
						mMediaThread = null;
					}
				}
				break;
				
			case MSG_PUSH_NOTIFICATION:
				
			case MSG_PUSH_TASKINFO:
				
			case MSG_PULL_NOTIFICATION:
				
			case MSG_PULL_TASKINFO:
				
			}
		}
		
	}
}

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.15
 *
 */
class ConnectionBox {
	public static final int TYPE_CONTROL = 1;
	public static final int TYPE_MEDIA = 2;

	final int type;
	
	public ConnectionBox(int type) {
		this.type = type;
	}
	
	String deviceName;
	int deviceId;
	int port;
	
	
}


