package org.gbssm.synapsys;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;
/* by dhuck. added */
import android.hardware.input.InputManager;

/**
 * 
 * SynapsysManager의 기능을 구현하는 시스템 서비스.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.06
 *
 */
public class SynapsysManagerService extends ISynapsysManager.Stub {
	
	static final String TAG = "SynapsysManagerService";	
	static final int LISTEN_PORT = 30300;
	Context mContext;
	private boolean isServiceRunning;
	private Socket mControlSocket;
	
	/* by dhuck. added */
	InputManager im ;
	
	public SynapsysManagerService(Context context) {
		mContext = context;
		/* by dhuck. added */
		im = (InputManager)context.getSystemService(Context.INPUT_SERVICE);
	}
	
	public int requestDisplayConnection() throws RemoteException {
		Slog.i("SynapsysManagerService", "reqeustDisplayConnection()");
		
		return 0; // Port Number
	}
	
	public boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y) throws RemoteException {
		Slog.i("SynapsysManagerService","id : "+event_id +" x : "+event_x +" y : "+event_y);
		jnicall(20, event_x, event_y );
		return false;
	}
	
	public boolean interpolateMouseEvent(int event_id, float event_x, float event_y) throws RemoteException { 
		
		return false;
	}
/* by dhuck. added */	
	private void jnicall(int deviceId, float event_x, float event_y )
	{
		Slog.i("SynapsysManagerService","framework : JNI CALL test ");
		im.Event_Receive(deviceId,event_x,event_y);
	}
	

	void systemReady() {
		// TODO : USB 연결 탐지.  Listener 등록
		
	}
	
	void systemRunning() {
		// TODO : 프로그램 연결 탐지 후,  연결 성립 (소켓연결)
		// TODO : 통신 스레드 시작.

		if (isServiceRunning)
			return;
		
		setServiceRunning(true);
		
		do {
			ServerSocket listenSocket = null;
			try {
				listenSocket = new ServerSocket(LISTEN_PORT); 
				listenSocket.setSoTimeout(10000);
				Slog.v(TAG, "Synapsys Init Socket Open");
				
				mControlSocket = listenSocket.accept();
				InputStream mControlInputstream = mControlSocket.getInputStream();
				OutputStream mControlOutputstream = mControlSocket.getOutputStream();
				
				
				listenSocket.close();
						
			} catch (IOException e) {
				continue;
				
			} finally {
				// ListenSocket Close.
				if (listenSocket != null) {
					try {
						listenSocket.close();
					} catch (IOException e) { ; }
				}
			}
			
		} while (SystemProperties.getBoolean("config.disable_synapsys", false));

		setServiceRunning(false);
	}
	
	synchronized void setServiceRunning(boolean running) {
		isServiceRunning = running;
	}
	
	
	class SynaysysHandler extends Handler {
		
	}
}


