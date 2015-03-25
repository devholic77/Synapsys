package org.gbssm.synapsys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.10
 *
 */
public class SynapsysControlThread extends Thread {

	private static final int TIMEOUT = 10000; 	// ms

	private final String TAG = "SynapsysMediaThread";
	
	private final SynapsysHandler mHandler;
	private final ConnectionBox mBox;
	
	private Socket mConnectedSocket;
	
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	private boolean isDestroyed;
	
	public SynapsysControlThread(SynapsysHandler handler, ConnectionBox box) {
		mHandler = handler;
		mBox = box;
	}
	
	@Override
	public void run() {
		final int port = mBox.port;

		Log.d(TAG, "ControlThread_Run()_Port : " + port);
		try {
			ServerSocket listenSocket = new ServerSocket(port);	
			listenSocket.setSoTimeout(TIMEOUT);
			
			do {
				try {
					mConnectedSocket = listenSocket.accept();
					
				} catch (SocketTimeoutException e) {
					
				}
				
				if (isDestroyed)
					return;
					
			} while (mConnectedSocket == null);

			Log.d(TAG, "ControlThread_Connected!");
			mDIS = new DataInputStream(mConnectedSocket.getInputStream());
			mDOS = new DataOutputStream(mConnectedSocket.getOutputStream());

			listenSocket.close();
			
			while(mDIS != null) {
				byte[] bytes = new byte[1024];
				Log.d(TAG, "ControlThread_Received! : " + mDIS.read(bytes));
				
				String send = "ControlThread_Write";
				mDOS.writeUTF(send);
				Log.d(TAG, "ControlThread_Send! : " + send.length());
			}
			
		} catch (IOException e) {
			
			
		} catch (Exception e) {
			
		} finally {
			Message message = Message.obtain(mHandler);
			message.what = SynapsysHandler.MSG_PROCEED_CONNECTION;
			message.obj = mBox;

			mHandler.sendMessageDelayed(message, 500);
			Log.d(TAG, "ControlThread_Destroyed!");
		}
	}
	
	public void send(MessageProtocol message) {
		try {
			if (mDOS != null)
				mDOS.write(message.encode());
			
		} catch (IOException e) {
			
		}
	}
	
	@Override
	public void destroy() {
		isDestroyed = true;
		
		try {
			if (mConnectedSocket != null) {
				mConnectedSocket.close();
				mConnectedSocket = null;
			}
			
		} catch (IOException e) {
			
		} finally {
			mDIS = null;
			mDOS = null;
		}
	}
}
