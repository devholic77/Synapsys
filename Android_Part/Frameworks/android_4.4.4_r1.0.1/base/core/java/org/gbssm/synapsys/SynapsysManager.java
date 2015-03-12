package org.gbssm.synapsys;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.concurrent.TimeoutException;

import android.os.RemoteException;

import org.gbssm.synapsys.ISynapsysManager;


/**
 * SDK로 접근할 수 있는  SynapsysManager.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 *
 */
public class SynapsysManager {

	private final ISynapsysManager mService;
	
	/* package */ SynapsysManager(ISynapsysManager service) {
		mService = service;
		
	}
	
	/**
	 * 
	 * @author Yeonho.Kim
	 * @since 2015.03.07
	 *
	 */
	public interface OnDisplayConnectionListener {
		
		public void onConnected(Socket displaySock);
		
		public void onDisconnected();
	}

	private OnDisplayConnectionListener mOnDisplayConnectionListener;


	private Socket mDisplaySocket;
	
	/**
	 * Display 연결을 요청하고, 연결된 Socket을 전달받는다.
	 * Socket 연결 과정에서 Thread 대기가 발생할 수 있기 때문에, MainThread에서 호출하지 않는 것이 좋다.
	 * 
	 * @return
	 */
	public Socket requestDisplayConnection() {
		final int Timeout = 3000;
		
		try {
			int port = mService.requestDisplayConnection();
			
			final int portF = port;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						ServerSocket server = new ServerSocket(portF);
						server.setSoTimeout(Timeout);
						mDisplaySocket = server.accept();
						
					} catch (Exception e) {
						e.printStackTrace();
						mDisplaySocket = null;
					}
				}
			};
			thread.start();
			thread.join(Timeout);
			
			return mDisplaySocket;
			
		} catch (RemoteException e) {
			
		} catch (InterruptedException e) {
			// TODO : 제한된 연결시간을 초과.
		}
		
		return null;
	}
	
	
	/**
	 * Android Device의 Touch Event를 PC의 Mouse Event로 발생시킨다.
	 *
	 */
	public boolean invokeMouseEventFromTouch(int mouse_id, float mouse_x, float mouse_y) {
		try {
			return mService.invokeMouseEventFromTouch(mouse_id, mouse_x, mouse_y);
			
		} catch (RemoteException e) {	
		}
		return false;
	}
	
	/**
	 * 
	 * @author Yeonho.Kim
	 * @since 2015.03.08
	 *
	 */
	public static class Factory {
		
		public static SynapsysManager create(ISynapsysManager service) {
			if (service != null)
				return new SynapsysManager(service);
			
			throw new InvalidParameterException("SynapsysManager should be initiated by system itself.");
		}
	}
}
