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
public class SynapsysControlThread extends SynapsysThread {

	private final ConnectionBox mBox;
	
	private Socket mConnectedSocket;
	
	private DataInputStream mDIS;
	private DataOutputStream mDOS;
	
	private boolean isDestroyed;
	
	public SynapsysControlThread(SynapsysHandler handler, ConnectionBox box) {
		super(handler);
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
				byte[] bytes = new byte[ControlProtocol.MSG_SIZE];
				Log.d(TAG, "ControlThread_Received! : " + mDIS.read(bytes));
				
				ControlProtocol[] protocols = ControlProtocol.decode(bytes);
				for (int itr = 0; itr < protocols.length; itr++)
					protocols[itr].process(mHandler.getService());
				
				//
				ControlProtocol protocol = new ControlProtocol(0);
				protocol.mCode = 10;
				protocol.mValue1 = 100;
				protocol.mValue2 = 200;
				protocol.mValue3 = 300;
				
				Log.i("Synapsys_Message", "ControlThread_Encoding : " + 
						protocol.mType +" / " + protocol.mCode + " / " + protocol.mValue1 + " / " + protocol.mValue2 + " / " + protocol.mValue3);
				
				send(protocol);
			}
			
		} catch (IOException e) {
			
		} catch (Exception e) {
			
		} finally {
			if (!isExited) {
				Message message = Message.obtain(mHandler);
				message.what = SynapsysHandler.MSG_PROCEED_CONTROL;
				message.obj = mBox;
	
				mHandler.sendMessageDelayed(message, 100);
			}
			Log.d(TAG, "ControlThread_Destroyed!");
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
	
	@Override
	public void exit() {
		isExited = true;
		destroy();
	}

	public void send(ControlProtocol message) {
		try {
			if (mDOS != null && message != null) {
				mDOS.write(message.encode());
				mDOS.flush();
				
				Log.d(TAG, "ControlThread_Send! : " + message.toString());
			}
			
		} catch (IOException e) { }
	}
	
}
