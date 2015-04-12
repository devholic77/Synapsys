package org.gbssm.synapsys;

import java.io.IOException;

import org.gbssm.synapsys.MessageProtocol.MediaProtocol;
import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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

	// *** CONSTANTS PART *** //
	public static final int EVENT_ADB_ENABLE = 1;
	public static final int EVENT_USB_CONNECT = 2;
	
	static final String TAG = "SynapsysManagerService";	
	
	static int SHADOW_TASK_STATE = -1;
	static int SHADOW_TASK_ID = -1;
	
	
	// *** MEMBER PART *** //
	final Context mContext;
	
	private boolean isServiceRunning;

	private ActivityManager mActivityManager;
	private PackageManager mPackageManager;
	private InputManager mInputManager;
	
	private ConnectionDetector mConnectionDetector;
	private SynapsysControlThread mControlThread;
	private SynapsysMediaThread mMediaThread;
	
	private ConnectionBox mConnectionBox;
	private ConnectionBox mMediaBox;
	private ConnectionBox mDisplayBox;
	
	
	public SynapsysManagerService(Context context) {
		mContext = context; 
	}
	
	public int requestDisplayConnection() throws RemoteException {
		Slog.v(TAG, "reqeustDisplayConnection()");
		if (mDisplayBox != null)
			return mDisplayBox.port;
		
		return -1; // Port Number
	}
	
	public boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y) throws RemoteException {
		// Windows PC로 Touch Event 전송.
		Slog.v(TAG, "invokeMouseEventFromTouch : event=" + event_id + " / x=" + event_x + " / y=" + event_y);
		jnicall(1, 0, event_x, event_y );
		return false;
	}
	
	public boolean invokeKeyboardEvent(int event_id, int key_code) throws RemoteException {
		Slog.v(TAG, "invokeKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		return false;
	}
	
	public boolean invokeNotificationEvent(int notificationId, String packageName, String message) throws RemoteException {
		// Windows PC로 Notification Event 전송.
		Slog.d(TAG, "invokeNotificationEvents :  Noti_ID = " + notificationId + " / Package = " + packageName + " / Message : " + message);
		
		if (mMediaThread != null) {
			try {
				ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
				
				MediaProtocol protocol = new MediaProtocol(MediaProtocol.SENDER_STATE_NOTI);
				protocol.id = notificationId;
				protocol.putName((String) mPackageManager.getApplicationLabel(info));
				protocol.putIcon(mPackageManager.getApplicationIcon(info));
				protocol.putContentMessage(message);
				
				mMediaThread.send(protocol);
				return true;
				
			} catch (Exception e) { ; }
		}
		
		return false;
	}
	
	public boolean invokeTaskInfoEvents(int state, int taskId, String packageName) throws RemoteException {
		// Windows PC로 Task-Info Event 전송.
		Slog.d(TAG, "invokeTaskInfoEvents : state = " + state + " / task = " + taskId + " / package = " + packageName);
		
		if (SHADOW_TASK_STATE == state && SHADOW_TASK_ID == taskId) {
			// 중복 메시지 전달 방지
			return false;
		}
		
		SHADOW_TASK_STATE = state;
		SHADOW_TASK_ID = taskId;
		
		if (mMediaThread != null) {
			try {
				ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
				
				MediaProtocol protocol = new MediaProtocol(state);
				protocol.id = taskId;
				protocol.putName((String) mPackageManager.getApplicationLabel(info));
				protocol.putIcon(mPackageManager.getApplicationIcon(info));
				protocol.putThumbnail(mActivityManager.getTaskTopThumbnail(taskId));
				 
				mMediaThread.send(protocol);
				return true;
				
			} catch (Exception e) {; }
		}
		
		return false;
	}
	
	public boolean interpolateMouseEvent(int event_id, float event_x, float event_y) throws RemoteException { 
		//  TODO : Windows PC로부터 Touch Event 받기.
		Slog.v(TAG, "interpolateMouseEvent : event=" + event_id + " / x=" + event_x + " / y=" + event_y);
		return false;
	}
	
	public boolean interpolateKeyboardEvent(int event_id, int key_code) throws RemoteException { 
		//  TODO : Windows PC로부터 Keyboard Event 받기.
		Slog.v(TAG, "interpolateKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		return false;
	}

	public boolean interpolateNotificationEvent(int state, int notificationId) throws RemoteException {
		//  Windows PC로부터 Notification Event 받기. 
		
		switch (state) {
		case MediaProtocol.RECEIVED_STATE_NOTI:
			
			return true;
		}
		return false;
	}
	
	public boolean interpolateTaskInfoEvent(int state, int taskId) throws RemoteException {
		// Windows PC로부터 Task-Info Event 받기.
		
		switch (state) {
		case MediaProtocol.RECEIVED_STATE_TASK_NEW:
			mActivityManager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
			return true;
			
		case MediaProtocol.RECEIVED_STATE_TASK_END:
			mActivityManager.removeTask(taskId, ActivityManager.REMOVE_TASK_KILL_PROCESS);
			return true;
		}
		
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
		Slog.v(TAG, "EventID : " + (event_id == 1 ? "ADB" : "CONN") + " / Event : " + event + " / Another : " + another);
		
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

	/* by dhuck. added */	
	private void jnicall(int event_type,int event_code, float value_1, float value_2 ) {
		Slog.i("SynapsysManagerService","framework : JNI CALL test ");
		mInputManager.Event_Receive(event_type,event_code,value_1,value_2);
	}
	
	void init() {
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		mPackageManager = mContext.getPackageManager();
		mInputManager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
	}
	
	/**
	 * System Phase-1 Ready : {@link ConnectionDetector} start.
	 */
	void systemReady() {
		init();
		
		isServiceRunning = true;
		mConnectionDetector = ConnectionDetector.getInstance(new SynapsysHandler());
		
		if (mConnectionDetector != null)
			mConnectionDetector.start();
	}
	
	/**
	 * System Phase-1 Stop : {@link ConnectionDetector} stop.
	 */
	void systemStop() {
		isServiceRunning = false;
		if (mConnectionDetector != null) {
			mConnectionDetector.stop();
			mConnectionDetector = null;
		}
		
		SynapsysControlThread.reset();
		if (SynapsysControlThread.mListenSocket != null) {
			try {
				SynapsysControlThread.mListenSocket.close();
				SynapsysControlThread.mListenSocket = null;
				
			} catch (IOException e) { ; }
		}

		SynapsysMediaThread.reset();
		if (SynapsysMediaThread.mListenSocket != null) {
			try {
				SynapsysMediaThread.mListenSocket.close();
				SynapsysMediaThread.mListenSocket = null;
				
			} catch (IOException e) { ; }
		}
		
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

        mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
	}
	
	
	/**
	 * SynapsysManagerService에서 기능의 일괄 처리를 위한 Handler. 
	 * 
	 * @author  Yeonho.Kim
	 * @since 2015.03.15
	 *
	 */
	class SynapsysHandler extends Handler implements MessageProtocol.Handler {
		
		@Override
		public void handleMessage(Message msg) {
			if (!isServiceRunning)
				return;
			
			switch (msg.what) {
			// *** Level C : Connection 관련 *** //
			case MSG_PROCEED_CONTROL:
				Slog.v(TAG, "Handler_MSG_PROCEED_CONTROL : " + msg.what);
				if (msg.obj != null) {
					ConnectionBox box = (ConnectionBox) msg.obj;
					
					if (SynapsysControlThread.isAbleToCreate()) {
						mControlThread = new SynapsysControlThread(this, mConnectionBox = box);
						mControlThread.start();
						
						broadcastSynapsysState(true, true, false);
						
					} else if (!box.equals(mConnectionBox))
						Message.obtain(this, MSG_EXIT_CONTROL, box).sendToTarget();
				}
				break;
				
			case MSG_CONNECTED_CONTROL:
				Slog.v(TAG, "Handler_MSG_CONNECTED_CONTROL : " + msg.what);
				broadcastSynapsysState(true, true, true);
				break;
				
			case MSG_EXIT_CONTROL:
				Slog.v(TAG, "Handler_MSG_EXIT_CONTROL : " + msg.what);
				sendMessageDelayed(Message.obtain(this, MSG_PROCEED_CONTROL, msg.obj), 250);
				
			case MSG_DESTROY_CONTROL:
				Slog.v(TAG, "Handler_MSG_DESTROY_CONTROL : " + msg.what);
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
				Slog.v(TAG, "Handler_MSG_DESTROYED_CONTROL : " + msg.what);
				broadcastSynapsysState(true, true, false);
				return;
				
				
			case MSG_PROCEED_MEDIA:
				Slog.v(TAG, "Handler_MSG_PROCEED_MEDIA : " + msg.what);
				if (msg.obj != null) {
					ConnectionBox box = (ConnectionBox) msg.obj;

					if (SynapsysMediaThread.isAbleToCreate()) {
						mMediaThread = new SynapsysMediaThread(this, mMediaBox = box);
						mMediaThread.start();
						
					} else if (!box.equals(mMediaBox)) 
						Message.obtain(this, MSG_EXIT_MEDIA, box).sendToTarget();
				}
				break;
				
			case MSG_CONNECTED_MEDIA:
				Slog.v(TAG, "Handler_MSG_CONNECTED_MEDIA : " + msg.what);
				//
				break;
				
			case MSG_EXIT_MEDIA:
				Slog.v(TAG, "Handler_MSG_EXIT_MEDIA : " + msg.what);
				sendMessageDelayed(Message.obtain(this, MSG_PROCEED_MEDIA, msg.obj), 250);
				
			case MSG_DESTROY_MEDIA:
				Slog.v(TAG, "Handler_MSG_DESTROY_MEDIA : " + msg.what);
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
				Slog.v(TAG, "Handler_MSG_DESTROYED_MEDIA : " + msg.what);
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
				
			default:
				Slog.v(TAG, "handleMessage : " + msg.what);
			}
		}
		
		public final SynapsysManagerService getService() {
			return SynapsysManagerService.this;
		}
	}
}

/**
 * Synapsys 통신을 위한 Thread 기본 틀을 정의한다.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.26
 *
 */
abstract class SynapsysThread extends Thread {

	// *** CONSTANTS PART *** //
	/**
	 * Socket Timeout 값
	 */
	protected static final int TIMEOUT = 5000; 	// ms
	
	protected static final String TAG = "SynapsysThread";

	

	// *** MEMBER PART *** //
	/**
	 * {@link SynapsysHanlder} 명령 전달 Handler.
	 */
	protected final SynapsysHandler mHandler;
	
	/**
	 * Thread 파괴(중) 상태 여부
	 */
	protected boolean isDestroyed;
	
	
	public SynapsysThread(SynapsysHandler handler) {
		mHandler = handler;
	}
	
	/**
	 * Thread를 종료한다. 
	 */
	@Override
	public abstract void destroy();
}


