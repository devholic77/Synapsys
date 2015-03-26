package org.gbssm.synapsys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.25
 *
 */
public class SynapsysMediaThread extends SynapsysThread {

	private final ConnectionBox mBox;
	
	private Socket mMediaSocket;
	private DataInputStream mDIS;
	private DataOutputStream mDOS;

	
	public SynapsysMediaThread(SynapsysHandler handler, ConnectionBox box) {
		super(handler);
		mBox = box;
	}
	
	@Override
	public void run() {
		final int port = mBox.port;
		
		Log.d(TAG, "MediaThread_Run()_Port : " + port);
		try {
			ServerSocket listenSocket = new ServerSocket(port);	
			listenSocket.setSoTimeout(TIMEOUT);
			
			do {
				try {
					mMediaSocket = listenSocket.accept();
					
				} catch (SocketTimeoutException e) { }

				if (isDestroyed)
					return;
					
			} while (mMediaSocket == null);

			Log.d(TAG, "MediaThread_Connected!");
			mDIS = new DataInputStream(mMediaSocket.getInputStream());
			mDOS = new DataOutputStream(mMediaSocket.getOutputStream());
			
			listenSocket.close();

			while(mDIS != null) {
				byte[] bytes = new byte[1024];
				Log.d(TAG, "MediaThread_Received! : " + mDIS.read(bytes));
				
				String send = "MediaThread_Write";
				mDOS.writeUTF(send);
				Log.d(TAG, "MediaThread_Send! : " + send.length());
			}
			
		} catch (IOException e) {
			
		} catch (Exception e) {
			
		} finally {
			if (!isExited) {
				Message message = Message.obtain(mHandler);
				message.what = SynapsysHandler.MSG_PROCEED_MEDIA;
				message.obj = mBox;
	
				mHandler.sendMessageDelayed(message, 50);
			}
			Log.d(TAG, "MediaThread_Destroyed!");
		}
	}
	
	@Override
	public void destroy() {
		isDestroyed = true;
		
		try {
			if (mMediaSocket != null) {
				mMediaSocket.close();
				mMediaSocket = null;
			}
			
		} catch (IOException e) {
			
		} finally {
			mDIS = null;
			mDOS = null;
		}
		
		this.interrupt();
	}
	
	@Override
	public void exit() {
		isExited = true;
		destroy();
	}
	
	public void send(MessageProtocol message) {
		
	}
}
