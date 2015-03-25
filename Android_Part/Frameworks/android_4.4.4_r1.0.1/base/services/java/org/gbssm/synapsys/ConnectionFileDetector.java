package org.gbssm.synapsys;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.os.Environment;
import android.os.FileObserver;
import android.os.Message;
import android.util.Slog;

/**
 * 연결 정보가 담긴 파일이 ADB를 통해 수정되는지 여부를 판단/감지하는 모듈이다. <br>
 * {@link #CONNECTION_FILE_DIR} 파일을 감시한다.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.13
 *
 */
public class ConnectionFileDetector extends FileObserver {

	/**
	 * Synapsys System Data 디렉토리
	 */
	public static final String SYNAPSYS_DIRECTORY = "/data/synapsys";
	
	/**
	 * ADB로 부터 삽입된 Connection 정보 파일에 대한 경로. 
	 */
	public static final String CONNECTION_FILE_DIR = SYNAPSYS_DIRECTORY + "/connection.dat";
	
	/**
	 * Singleton Instance
	 */
	private static ConnectionFileDetector sInstance = null;
	
	/**
	 * {@link ConnectionFileDetector} Singleton Instance를 반환한다. 
	 *  
	 * @param context
	 * @return
	 */
	public static ConnectionFileDetector getInstance(SynapsysHandler handler) {
		if (sInstance == null && handler != null)
			sInstance = new ConnectionFileDetector(handler);
		
		return sInstance;
	}
	
	
	private final SynapsysHandler mHandler;
	private final String TAG = "ConnectionFileDetector";
	
	private ConnectionFileDetector(SynapsysHandler handler) {
		super(CONNECTION_FILE_DIR);
		
		mHandler = handler;
		
		// 초기화
		makeConnectionFile();
	}
	
	@Override
	public void onEvent(int event, String path) {
		Slog.d(TAG, event + " : " + path);
		
		switch (event) {
		case OPEN:
			break;
			
		case ACCESS:
			break;

		case CLOSE_NOWRITE:
			break;
			
		case MODIFY:
			break;
			
		case CLOSE_WRITE:
			ConnectionBox box = readConnectionFile();
			
			// SynapsysManagerService_SynapsysHandler로 연결 결과 메시지를 보낸다.
			Message message = Message.obtain(mHandler);
			message.what = SynapsysHandler.MSG_PROCEED_CONNECTION;
			message.obj = box;
			message.sendToTarget();
			break;
			
		case ATTRIB:
			break;
			
		case DELETE_SELF:
			makeConnectionFile();
			break;
			
		case 32768:
			break;
		}
	}
	
	public void start() {
		File connFile = new File(CONNECTION_FILE_DIR);
		if (!connFile.exists())
			makeConnectionFile();
		
		super.startWatching();
	}
	
	public void stop() {
		super.stopWatching();
	}

	public void makeConnectionFile() {
		try {
			File synapsysDir = new File(SYNAPSYS_DIRECTORY);
			if (!synapsysDir.exists());
				synapsysDir.mkdir();
			
			File connectionFile = new File(CONNECTION_FILE_DIR);
			if (!connectionFile.exists())
				connectionFile.createNewFile();
			
		} catch (IOException e) {
			Slog.e(TAG, e.getMessage());
		}
	}
	
	public ConnectionBox readConnectionFile() {

		ConnectionBox box = new ConnectionBox();
		// TODO: ConnectionFile로 부터 포트번호를 읽는다.
		try {
			FileReader reader = new FileReader(new File(CONNECTION_FILE_DIR));

			// Port 번호 읽기.
			CharBuffer buffer = CharBuffer.allocate(4);
			reader.read(buffer);
			box.port = Integer.parseInt(buffer.toString());
			
			reader.close();
			
		} catch (IOException e) {
			
		} catch (NumberFormatException e) {
			
		} finally {
			
		}
		
		return box;
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
}
