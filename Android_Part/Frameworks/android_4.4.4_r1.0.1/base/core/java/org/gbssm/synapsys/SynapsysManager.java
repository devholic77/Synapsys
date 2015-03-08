package org.gbssm.synapsys;

import java.net.Socket;
import java.security.InvalidParameterException;

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
//	
//	/**
//	 * 
//	 * @author Yeonho.Kim
//	 * @since 2015.03.07
//	 *
//	 */
//	private class IDisplayConnectionListener extends IDisplayConnectionListener.Stub {
//		
//		public void onConnected(Socket displaySock) throws RemoteException {
//			if (mOnDisplayConnectionListener != null)
//				mOnDisplayConnectionListener.onConnected(displaySock);
//		}
//		
//		public void onDisconnected() throws RemoteException {
//			if (mOnDisplayConnectionListener != null)
//				mOnDisplayConnectionListener.onDisconnected();
//		}
//	}
//	
//	private IDisplayConnectionListener mIDisplayConnectionListener;

	
	public void requestDisplayConnection() {
		//requestDisplayConnection(null);
	}
	
//	public void requestDisplayConnection(OnDisplayConnectionListener listener) {
//		mOnDisplayConnectionListener = listener;
//		
//		try {
//			mService.requestDisplayConnection(mIDisplayConnectionListener);
//			
//		} catch (RemoteException e) {
//		}
//	}
	
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
			
			throw new InvalidParameterException("SynapsysManager should be initiated by system.");
		}
	}
}
