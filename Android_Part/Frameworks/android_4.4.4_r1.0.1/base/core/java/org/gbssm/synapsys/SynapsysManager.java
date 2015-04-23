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
	/**
	 * Broadcast Intent Action
	 */
	public static final String BROADCAST_ACTION_SYNAPSYS = "org.gbssm.synapsys.system_broadcast.action";
	/**
	 * Extra Boolean Value 
	 */
	public static final String BROADCAST_EXTRA_USB_READY = "Synapsys_USB_Ready";
	/**
	 * Extra Boolean Value 
	 */
	public static final String BROADCAST_EXTRA_PC_READY = "Synapsys_PC_Ready";
	/**
	 * Extra Boolean Value 
	 */
	public static final String BROADCAST_EXTRA_CONNECTION = "Synpasys_Connection";

	
	private final ISynapsysManager mService;
	
	/* package */ SynapsysManager(ISynapsysManager service) {
		mService = service;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public int requestDisplayConnection() {
		try {
			return mService.requestDisplayConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	/**
	 * Android Device의 Touch Event를 PC의 Mouse Event로 발생시킨다.
	 *
	 */
	public boolean invokeMouseEventFromTouch(int mouse_id, float mouse_x, float mouse_y) {
		try {
			return mService.invokeMouseEventFromTouch(mouse_id, mouse_x, mouse_y);
			
		} catch (RemoteException e) {	
			;
		}
		return false;
	}
	
	/**
	 * Android Device의 Keyboard Event를 PC의 Keyboard Event로 발생시킨다.
	 *
	 */
	public boolean invokeKeyboardEvent(int event_id, int key_code) {
		try {
			return mService.invokeKeyboardEvent(event_id, key_code);
			
		} catch (RemoteException e) {
			;
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
