package org.gbssm.synapsys;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.10
 *
 */
public class SynapsysControlThread extends Thread {

	private static final int TIMEOUT = 10000; 	// ms
	
	private final int mPort;
	
	private Socket mConnectedSocket;
	
	public SynapsysControlThread(int port) {
		mPort = port;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket listenSocket = new ServerSocket(mPort);	
			listenSocket.setSoTimeout(TIMEOUT);
			
			mConnectedSocket = listenSocket.accept();
			listenSocket.close();
			
			InputStream is = mConnectedSocket.getInputStream();
			OutputStream os = mConnectedSocket.getOutputStream();
			
			
		} catch (IOException e) {
			// ServerSocket Timeout
			
		} catch (Exception e) {
			
		}
	}
	
	
}
