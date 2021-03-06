package org.gbssm.synapsys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.FileObserver;
import android.os.Message;
import android.util.Slog;

/**
 * 연결 정보가 담긴 파일이 ADB를 통해 수정되는지 여부를 판단/감지하는 모듈이다. 
 *  파일이 수정되었음을 감지하면 {@link SynapsysHandler}를 통해 {@link SynapsysManagerService}로 알린다.
 * <br> {@link #CONNECTION_FILE_DIR} 파일을 감시한다. 
 * 
 * @author Yeonho.Kim
 * @since 2015.03.13
 *
 */
public class ConnectionDetector extends FileObserver {
	
	// *** CONSTANTS PART *** //
	/**
	 * Synapsys System Data 디렉토리
	 */
	public static final String SYNAPSYS_DIRECTORY = "/data/synapsys";
	
	/**
	 * ADB로 부터 삽입된 Connection 정보 파일에 대한 경로. 
	 */
	public static final String CONNECTION_FILE_DIR = SYNAPSYS_DIRECTORY + "/connection.dat";
	
	protected static final boolean DEBUG = false;
	
	private final String TAG = "Synapsys_ConnectionDetector";
	
	
	
	// *** STATIC PART *** //
	/**
	 * Singleton Instance
	 */
	private static ConnectionDetector sInstance = null;
	
	/**
	 * {@link ConnectionDetector} Singleton Instance를 반환한다. 
	 *  
	 * @param context
	 * @return
	 */
	public static ConnectionDetector getInstance(SynapsysHandler handler) {
		if (sInstance == null && handler != null)
			sInstance = new ConnectionDetector(handler);
		
		return sInstance;
	}
	
	

	// *** MEMBER PART *** //
	private final SynapsysHandler mHandler;

	private ConnectionBox mControlBox;
	private ConnectionBox mDisplayBox;
	private ConnectionBox mMediaBox;
	
	/**
	 * 
	 */
	private boolean isCreated;
	
	private ConnectionDetector(SynapsysHandler handler) {
		super(SYNAPSYS_DIRECTORY);
		
		mHandler = handler;
		
		// 초기화
		makeConnectionFile();
	}

	@Override
	public void onEvent(int event, String path) {
		switch (event) {
		case CREATE:
			if (CONNECTION_FILE_DIR.equals(SYNAPSYS_DIRECTORY + "/" + path)) {
				isCreated = true;
			}
			break;
			
		case CLOSE_WRITE:
			synchronized (this) {
				if (isCreated) {
					isCreated = false;
					readConnectionFile();
					
					// SynapsysManagerService_SynapsysHandler로 연결 결과 메시지를 보낸다.
					Message message = Message.obtain(mHandler);
					message.what = SynapsysHandler.MSG_PROCEED_CONTROL;
					message.obj = mControlBox;
					message.sendToTarget();
					
					message = Message.obtain(mHandler);
					message.what = SynapsysHandler.MSG_PROCEED_MEDIA;
					message.obj = mMediaBox;
					message.sendToTarget();

					message = Message.obtain(mHandler);
					message.what = SynapsysHandler.MSG_PROCEED_DISPLAY;
					message.obj = mDisplayBox;
					message.sendToTarget();
				}
			}
			break;
			
		case 32768:
			break;
		}
	}

	@Override
	@Deprecated
	public void startWatching() {
		throw new UnsupportedOperationException("This method is no longer used. Use start() instead.");
	}
	
	@Override
	@Deprecated
	public void stopWatching() {
		throw new UnsupportedOperationException("This method is no longer used. Use stop() instead.");
	}
	
	public void start() {
		File connFile = new File(CONNECTION_FILE_DIR);
		if (!connFile.exists())
			makeConnectionFile();
		
		super.startWatching();
	}
	
	public void stop() {
		super.stopWatching();

		mHandler.sendEmptyMessage(SynapsysHandler.MSG_DESTROY_CONTROL);
		mHandler.sendEmptyMessage(SynapsysHandler.MSG_DESTROY_MEDIA);
	}

	void makeConnectionFile() {
		try {
			File synapsysDir = new File(SYNAPSYS_DIRECTORY);
			if (!synapsysDir.exists());
				synapsysDir.mkdir();
				
			if (DEBUG)
				Slog.v(TAG, "Synapsys Directory is checked. > " + SYNAPSYS_DIRECTORY);
				
			File connectionFile = new File(CONNECTION_FILE_DIR);
			if (!connectionFile.exists())
				connectionFile.createNewFile();

			if (DEBUG)
				Slog.v(TAG, "Synapsys ConnectionFile is checked. > " + CONNECTION_FILE_DIR);
			
		} catch (IOException e) {
			Slog.e(TAG, e.getMessage());
		}
	}
	
	private void readConnectionFile() {
		mControlBox = new ConnectionBox(ConnectionBox.TYPE_CONTROL);
		mDisplayBox = new ConnectionBox(ConnectionBox.TYPE_DISPLAY);
		mMediaBox = new ConnectionBox(ConnectionBox.TYPE_MEDIA);
		
		// ConnectionFile로 부터 포트번호를 읽는다.
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(CONNECTION_FILE_DIR));
			
			mDisplayBox.port = Integer.parseInt(reader.readLine());
			mControlBox.port = Integer.parseInt(reader.readLine());
			mMediaBox.port = Integer.parseInt(reader.readLine());
			
			mMediaBox.deviceName = 
					mControlBox.deviceName = 
						mDisplayBox.deviceName = reader.readLine();
			
			if (!"Synapsys".equals(reader.readLine()))
				throw new IOException("ConnectionFile isn't edited properly.");
			
		} catch (IOException e) {
			if (DEBUG)
				Slog.e(TAG, e.getMessage());
			
		} catch (NumberFormatException e) {
			;
		} finally {
			try {
				if (reader != null)
					reader.close();
				
			} catch (IOException e) { ; }
		}
	}

}
