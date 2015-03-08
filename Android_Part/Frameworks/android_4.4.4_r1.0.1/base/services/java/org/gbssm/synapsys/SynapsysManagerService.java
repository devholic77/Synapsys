package org.gbssm.synapsys;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;

import org.gbssm.synapsys.ISynapsysManager;

/**
 * 
 * SynapsysManager의 기능을 구현하는 시스템 서비스.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.06
 *
 */
public class SynapsysManagerService extends ISynapsysManager.Stub {
	
	private final int INIT_PORT = 12345;
	
	Context mContext;
	
	public SynapsysManagerService(Context context) {
		mContext = context;
	}
	
	public void requestDisplayConnection() throws RemoteException {
		Slog.i("SynapsysManagerService", "reqeustDisplayConnection()");
	}
	
	private void init() {
		Thread initThread = new Thread(initRunnable);
		
	}
	
	Runnable initRunnable = new Runnable() {
		@Override
		public void run() {
			
			try {
				ServerSocket initSocket = new ServerSocket(INIT_PORT);
				Socket connSocket = initSocket.accept();
				
				initSocket.close();
				
			} catch (IOException e) {
				
			}
		}
	};
	
	Runnable controlRunnable = new Runnable() {
		@Override
		public void run() {
			
		}
	};
}
