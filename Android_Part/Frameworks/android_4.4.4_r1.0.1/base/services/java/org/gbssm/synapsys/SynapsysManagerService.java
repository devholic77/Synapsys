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
	
	Context mContext;
	
	
	private boolean isServiceRunning;
	
	private Socket mControlSocket;
	
	private ConnectionFileDetector mConnectionDetector;
	private SynapsysControlThread mControlThread;
	
	
	
	public SynapsysManagerService(Context context) {
		mContext = context; 
	}
	
	public int requestDisplayConnection() throws RemoteException {
		Slog.i("SynapsysManagerService", "reqeustDisplayConnection()");
		
		return 0; // Port Number
	}
	
	public boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y) throws RemoteException {
		// TODO : Windows PC로 Touch Event 전송.
		return false;
	}
	
	public boolean interpolateMouseEvent(int event_id, float event_x, float event_y) throws RemoteException { 
		//  TODO : Windows PC로부터 Touch Event 받기.
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
	
	void systemStop() {
		if (mConnectionDetector != null)
			mConnectionDetector.stop();
	}
	
	/**
	 * 
	 * 
	 * @author  Yeonho.Kim
	 *
	 */
	class SynapsysHandler extends Handler {
		
		static final int MSG_PROCEED_CONNECTION = 0x100;
		
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MSG_PROCEED_CONNECTION:
				MessageBox box;
				
				if (msg.obj != null) {
					box = (MessageBox) msg.obj;
					
					mControlThread = new SynapsysControlThread(box.port);
					mControlThread.start();
				}
				break;
				
			}
		}
		
	}
}

/**
 * 
 * @author Yeonho.Kim
 *
 */
class MessageBox {
	int port;
}


