package org.gbssm.synapsys;

import java.io.IOException;
import java.util.HashMap;

import org.gbssm.synapsys.MessageProtocol.ControlProtocol;
import org.gbssm.synapsys.MessageProtocol.MediaProtocol;
import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
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
	
	public static final int TYPE_KEYBOARD = 0;
	public static final int TYPE_MOUSE= 1;
	
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

	private HashMap<Integer, String> mCurrentTaskMap = new HashMap<Integer, String>();
	private int currentTopTaskID = -1;
	
	
	public SynapsysManagerService(Context context) {
		mContext = context; 

		// USB 혹은 Power 연결시, 화면이 꺼지지 않도록 설정.
		Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB));
	}
	
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public int requestDisplayConnection() throws RemoteException {
		Slog.v(TAG, "reqeustDisplayConnection()");
		if (mDisplayBox != null)
			return mDisplayBox.port;
		
		return -1; // Port Number
	}
	
	/**
	 * Windows PC로 Touch Event 전송.
	 * 
	 * @param event_id
	 * @param event_x
	 * @param event_y
	 * @return
	 * @throws RemoteException
	 */
	public boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y) throws RemoteException {
		Slog.v(TAG, "invokeMouseEventFromTouch : event=" + event_id + " / x=" + event_x + " / y=" + event_y);

		if (mControlThread != null) {
			try {

			} catch (Exception e) {
				;
			}
		}
		
		return false;
	}
	
	/**
	 * Windows PC로 Keyboard Event 전송.	
	 * 
	 * @param event_id
	 * @param key_code
	 * @return
	 * @throws RemoteException
	 */
	public boolean invokeKeyboardEvent(int event_id, int key_code) throws RemoteException {
		Slog.v(TAG, "invokeKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		
		if (mControlThread != null) {
			try {
				ControlProtocol<Integer, Integer, Integer> protocol = new ControlProtocol<Integer, Integer, Integer>(event_id);
				
				mControlThread.send(protocol);
				
			} catch (Exception e) { ; }
		}
		
		return false;
	}
	
	/**
	 * Windows PC로 Notification Event 전송.
	 * 
	 * @param notificationId
	 * @param packageName
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public boolean invokeNotificationEvent(int notificationId, String packageName, String message) throws RemoteException {
		Slog.d(TAG, "invokeNotificationEvents :  Noti_ID = " + notificationId + " / Package = " + packageName + " / Message : " + message);
		
		if (mMediaThread != null) {
			try {
				ApplicationInfo info = mPackageManager.getApplicationInfo(
						packageName, PackageManager.GET_META_DATA);
				
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
	
	/**
	 * Windows PC로 Task-Info Event 전송.
	 * 
	 * @param state
	 * @param taskId
	 * @param packageName
	 * @return
	 * @throws RemoteException
	 */
	public boolean invokeTaskInfoEvents(int state, int taskId, String packageName) throws RemoteException {
		
		// *** Task Filtering *** //
		if (SHADOW_TASK_STATE == state && SHADOW_TASK_ID == taskId) {
			// 중복 메시지 전달 방지
			return false;
			
		} else 
			if (packageName.startsWith("com.android.system")) {
				// System UI 전달 방지.
				return false;
			}
		Slog.v(TAG, "invokeTaskInfoEvents : state = " + state + " / task = " + taskId + " / package = " + packageName);
			
		SHADOW_TASK_STATE = state;
		SHADOW_TASK_ID = taskId;
		
		if (mMediaThread == null)
			return false;
		
		MediaProtocol newTaskEvent = new MediaProtocol(state);
		newTaskEvent.id = taskId;
		
		switch (state) {
		case MediaProtocol.SENDER_STATE_NEW:
			// 이전 Top-Task가 변경되었음을 알린다.
			try {
				ApplicationInfo info = mPackageManager.getApplicationInfo(
						mCurrentTaskMap.get(currentTopTaskID), PackageManager.GET_META_DATA);

				MediaProtocol protocol = new MediaProtocol(MediaProtocol.SENDER_STATE_RENEW);
				protocol.id = currentTopTaskID;
				protocol.putName((String) mPackageManager.getApplicationLabel(info));
				protocol.putIcon(mPackageManager.getApplicationIcon(info));
				protocol.putThumbnail(mActivityManager.getTaskTopThumbnail(currentTopTaskID));

				mMediaThread.send(protocol);

			} catch (Exception e) {
			}
		
			currentTopTaskID = taskId;
			
			if (!mCurrentTaskMap.containsKey(taskId)) {
				// 처음 실행되는 Task의 경우, 새 Top-Task를 추가한다.
				try {
					ApplicationInfo info = mPackageManager.getApplicationInfo(
							packageName, PackageManager.GET_META_DATA);
				
					newTaskEvent.putName((String) mPackageManager.getApplicationLabel(info));
					newTaskEvent.putIcon(mPackageManager.getApplicationIcon(info));
					newTaskEvent.putThumbnail(mActivityManager.getTaskTopThumbnail(taskId));
					
					mMediaThread.send(newTaskEvent);
					mCurrentTaskMap.put(taskId, packageName);
				} catch (Exception e) {
				}
			}
			break;
			
		case MediaProtocol.SENDER_STATE_END:
			mMediaThread.send(newTaskEvent);
			mCurrentTaskMap.remove(taskId);
			break;
		
		default:
			return false;
		}
		
		return true;
	}
	
	void invokeAllCurrentTasks() {
		if (mMediaThread == null) 
			return;
		
		//for(RecentTaskInfo rti : mActivityManager.getRecentTaskss(10, ActivityManager.RECENT_IGNORE_UNAVAILABLE)) {
		for(RunningTaskInfo rti : mActivityManager.getRunningTasks(10)) {
			try {
				//ComponentName baseComponent = rti.baseIntent.getComponent();
				ComponentName baseComponent = rti.baseActivity;
				if (baseComponent == null || rti.id <= 0)
					continue;
				
				String packageName = baseComponent.getPackageName();
				if (packageName.startsWith("com.android.system"))
					// System UI 전달 방지.
					continue;
				
				ApplicationInfo appInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
				mCurrentTaskMap.put(rti.id, packageName);
				
				MediaProtocol media = new MediaProtocol(MediaProtocol.SENDER_STATE_NEW);
				media.id = rti.id;
				media.putName((String) mPackageManager.getApplicationLabel(appInfo));
				media.putIcon(appInfo.loadIcon(mPackageManager));
				media.putThumbnail(mActivityManager.getTaskTopThumbnail(rti.id));
				
				Slog.i(TAG, "RecentTaskInfo : " + media.toString() + " / ComponentName : " + baseComponent.toShortString());
				mMediaThread.send(media);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Windows PC로부터 Touch Event 받기.
	 * 
	 * @param event_id
	 * @param event_x
	 * @param event_y
	 * @return
	 * @throws RemoteException
	 */
	public boolean interpolateMouseEvent(int event_id, float event_x, float event_y) throws RemoteException { 
		Slog.v(TAG, "interpolateMouseEvent : event=" + event_id + " / x=" + event_x + " / y=" + event_y);
				
		// 윈도우에서 받는 이벤트 전달 함수 호출 
		jnicall(TYPE_MOUSE, event_id, event_x, event_y );
		return true;
	}
	
	/**
	 * Windows PC로부터 Keyboard Event 받기.
	 * 
	 * @param event_id
	 * @param key_code
	 * @return
	 * @throws RemoteException
	 */
	public boolean interpolateKeyboardEvent(int event_id, int key_code) throws RemoteException { 
		Slog.v(TAG, "interpolateKeyboardEvent : event=" + event_id + " / keyCode=" + key_code);
		
		// 윈도우에서 받는 이벤트 전달 함수 호출 
		jnicall(TYPE_KEYBOARD, key_code, 0, 0);
		return true;
	}

	/**
	 * Windows PC로부터 Notification Event 받기. 
	 * 
	 * @param state
	 * @param notificationId
	 * @return
	 * @throws RemoteException
	 */
	public boolean interpolateNotificationEvent(int state, int notificationId) throws RemoteException {
		
		switch (state) {
		case MediaProtocol.RECEIVED_STATE_NOTI:
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Windows PC로부터 Task-Info Event 받기.
	 * 
	 * @param state
	 * @param taskId
	 * @return
	 * @throws RemoteException
	 */
	public boolean interpolateTaskInfoEvent(int state, int taskId) throws RemoteException {
		
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

	/**
	 * InputManager의 Event_Receive 함수를 통해 Native level로 이벤트 전달 
	 * 
	 * @param event_type
	 * @param event_code
	 * @param value_1
	 * @param value_2
	 * 
	 * @author Dhuckil.Kim
	 */
	private void jnicall(int event_type,int event_code, float value_1, float value_2 ) {
		Slog.i("SynapsysManagerService","framework : JNI CALL test ");
		
		// InputManager 이벤트 전달 함수 호출 
		mInputManager.Event_Receive(event_type,event_code,value_1,value_2);		
	}
	
	/**
	 * 
	 */
	void init() {
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		mPackageManager = mContext.getPackageManager();
		mInputManager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
		
		mCurrentTaskMap.clear();
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


