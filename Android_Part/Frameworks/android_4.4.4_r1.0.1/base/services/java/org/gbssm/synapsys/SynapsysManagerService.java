package org.gbssm.synapsys;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.android.internal.telephony.MmiCode;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
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
	
	final Context mContext;
	
	
	private boolean isServiceRunning;
	
	private Socket mControlSocket;
	
	private ConnectionFileDetector mConnectionDetector;
	private SynapsysControlThread mControlThread;
	private SynapsysMediaThread mMediaThread;
	
	private ConnectionBox mConnectionBox;
	private ConnectionBox mMediaBox;
	private ConnectionBox mDisplayBox;
	
	
	public SynapsysManagerService(Context context) {
		mContext = context; 
	}
	
	public int requestDisplayConnection() throws RemoteException {
		Slog.i(TAG, "reqeustDisplayConnection()");
		if (mDisplayBox != null)
			return mDisplayBox.port + 1;
		
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
				broadcastSynapsysState(true, false, false);
				return;
			}
		}
		
		// 연결이 성립하지 않는 다른 모든  경우,
		systemStop();
		broadcastSynapsysState(false, false, false);
	}
	
	/**
	 * System Phase-1 Ready : {@link ConnectionFileDetector} start.
	 */
	void systemReady() {
		mConnectionDetector = ConnectionFileDetector.getInstance(new SynapsysHandler());
		
		if (mConnectionDetector != null)
			mConnectionDetector.start();
	}
	
	/**
	 * System Phase-1 Stop : {@link ConnectionFileDetector} stop.
	 */
	void systemStop() {
		if (mConnectionDetector != null)
			mConnectionDetector.stop();
		
		broadcastSynapsysState(true, false, false);
	}
	
	/**
	 * 
	 * @param usb
	 * @param pc
	 * @param connection
	 */
	void broadcastSynapsysState(boolean usb, boolean pc, boolean connection) {
        Intent intent = new Intent(SynapsysManager.BROADCAST_ACTION_SYNAPSYS);
        intent.putExtra(SynapsysManager.BROADCAST_EXTRA_USB_READY, usb);
        intent.putExtra(SynapsysManager.BROADCAST_EXTRA_PC_READY, pc);
        intent.putExtra(SynapsysManager.BROADCAST_EXTRA_CONNECTION, connection);

        mContext.sendStickyBroadcast(intent);
	}
	
	/**
	 * SynapsysManagerService에서 기능의 일괄 처리를 위한 Handler. 
	 * 
	 * @author  Yeonho.Kim
	 * @since 2015.03.15
	 *
	 */
	class SynapsysHandler extends Handler {
		// *** Level C : Connection 관련 *** //
		/**
		 * Handler 메시지 : 데이터 소켓 오픈 및 진행.
		 */
		static final int MSG_PROCEED_CONTROL = 0xC100;
		/**
		 * Handler 메시지 : 데이터 소켓 연결 성립 알림.
		 */
		static final int MSG_CONNECTED_CONTROL = 0xC11C;
		/**
		 * Handler 메시지 : 미디어 연결 해제 및 재진행 명령.
		 */
		static final int MSG_EXIT_CONTROL = 0xC10E;
		/**
		 * Handler 메시지 : 데이터 연결 해제 명령.
		 */
		static final int MSG_DESTROY_CONTROL = 0xC10D;
		/**
		 * Handler 메시지 : 데이터 연결 해제 알림.
		 */
		static final int MSG_DESTROYED_CONTROL = 0xC11D;
		
		/**
		 * Handler 메시지 : 미디어 소켓 오픈 및 진행.
		 */
		static final int MSG_PROCEED_MEDIA = 0xC200;
		/**
		 * Handler 메시지 : 미디어 소켓 연결 성립 알림.
		 */
		static final int MSG_CONNECTED_MEDIA = 0xC21C;
		/**
		 * Handler 메시지 : 미디어 연결 해제 명령.
		 */
		static final int MSG_EXIT_MEDIA = 0xC20E;
		/**
		 * Handler 메시지 : 미디어 연결 해제 명령.
		 */
		static final int MSG_DESTROY_MEDIA = 0xC20D;
		/**
		 * Handler 메시지 : 미디어 연결 해제 알림.
		 */
		static final int MSG_DESTROYED_MEDIA = 0xC21D;
		
		
		/**
		 * Handler 메시지 : 디스플레이 소켓.
		 */
		static final int MSG_PROCEED_DISPLAY = 0xC300;
		
		
		
		// *** LEVEL E : Event 관련 *** //
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
			Slog.i(TAG, "handleMessage : " + msg.what);
			
			switch (msg.what) {
			// *** Level C : Connection 관련 *** //
			case MSG_PROCEED_CONTROL:
				if (msg.obj != null) {
					ConnectionBox box = (ConnectionBox) msg.obj;
					
					if (SynapsysControlThread.isAbleToCreate()) {
						mControlThread = new SynapsysControlThread(this, mConnectionBox = box);
						mControlThread.start();
						
						broadcastSynapsysState(true, true, false);
						
					} else if (SynapsysControlThread.isRunning() || !box.equals(mConnectionBox))
						Message.obtain(this, MSG_EXIT_CONTROL, box).sendToTarget();
				}
				break;
				
			case MSG_CONNECTED_CONTROL:
				broadcastSynapsysState(true, true, true);
				break;
				
			case MSG_EXIT_CONTROL:
				sendMessageDelayed(Message.obtain(this, MSG_PROCEED_CONTROL, msg.obj), 250);
				
			case MSG_DESTROY_CONTROL:
				if (mControlThread != null) {
					try {
						mControlThread.destroy();
						mControlThread.join(100);
					} catch (InterruptedException e) {
					} finally {
						mControlThread = null;
					}
				}
				break;
				
			case MSG_DESTROYED_CONTROL:
				broadcastSynapsysState(true, true, false);
				return;
				
				
			case MSG_PROCEED_MEDIA:
				if (msg.obj != null) {
					ConnectionBox box = (ConnectionBox) msg.obj;

					if (SynapsysMediaThread.isAbleToCreate()) {
						mMediaThread = new SynapsysMediaThread(this, mMediaBox = box);
						mMediaThread.start();
						
					} else if (SynapsysControlThread.isRunning() || !box.equals(mMediaBox)) 
						Message.obtain(this, MSG_EXIT_MEDIA, box).sendToTarget();
				}
				break;
				
			case MSG_CONNECTED_MEDIA:
				//
				break;
				
			case MSG_EXIT_MEDIA:
				sendMessageDelayed(Message.obtain(this, MSG_PROCEED_MEDIA, msg.obj), 250);
				
			case MSG_DESTROY_MEDIA:
				if (mMediaThread != null) {
					try {
						mMediaThread.destroy();
						mMediaThread.join(100);
					} catch (InterruptedException e) { ; 
					} finally {
						mMediaThread = null;
					}
				}
				break;	
				
			case MSG_DESTROYED_MEDIA:
				//
				return;
				
				
			case MSG_PROCEED_DISPLAY:
				if (msg.obj != null) {
					ConnectionBox box = (ConnectionBox) msg.obj;
					
					if (!box.equals(mDisplayBox)) {
						mDisplayBox = box;
						Slog.d(TAG, "Display_Port : " + mDisplayBox.port);
					}
				}
				break;
				
				

			// *** LEVEL E : Event 관련 *** //
			case MSG_PUSH_NOTIFICATION:
				
			case MSG_PUSH_TASKINFO:
				
			case MSG_PULL_NOTIFICATION:
				
			case MSG_PULL_TASKINFO:
				
			}
		}
		
		public final SynapsysManagerService getService() {
			return SynapsysManagerService.this;
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
	public static final int TYPE_DISPLAY = 3;

	final int type;
	
	String deviceName;
	int deviceId;
	int port;
	
	public ConnectionBox(int type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		StringBuilder builder = new StringBuilder(type);
		builder.append(deviceName);
		builder.append(deviceId);
		builder.append(port);
		
		return builder.toString().hashCode();
	}
}


