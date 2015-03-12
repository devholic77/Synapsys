package org.gbssm.synapsys;

import java.io.File;
import java.io.IOException;

import org.gbssm.synapsys.SynapsysManagerService.SynaysysHandler;

import android.os.FileObserver;

/**
 * 연결 정보가 담긴 파일이 ADB를 통해 수정되는지 여부를 판단/감지하는 모듈이다. <br>
 * {@link #CONNECTION_FILE_DIR} 파일을 감시한다.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.13
 *
 */
public class AdbConnectionDetector {

	/**
	 * ADB로 부터 삽입된 Connection 정보 파일에 대한 경로. 
	 */
	public static final String CONNECTION_FILE_DIR = "";
	
	/**
	 * Singleton Instance
	 */
	private static AdbConnectionDetector sInstance = null;
	
	/**
	 * {@link AdbConnectionDetector} Singleton Instance를 반환한다. 
	 *  
	 * @param context
	 * @return
	 */
	public static AdbConnectionDetector getInstance(SynaysysHandler handler) {
		if (sInstance == null)
			sInstance = new AdbConnectionDetector(handler);
		
		return sInstance;
	}
	
	private final SynaysysHandler mHandler;
	private final FileObserver mObserver;
	
	private AdbConnectionDetector(SynaysysHandler handler) {
		mHandler = handler;
		mObserver = new FileObserver(CONNECTION_FILE_DIR) {
			
			@Override
			public void onEvent(int event, String path) {
				
				switch (event) {
				case MODIFY:
					// TODO :
					break;
					
				case CLOSE_WRITE:
					// TODO : 파일이 수정됬음을 알리고, 해당 파일을 읽어 ServerSocket을 Open한다.
					break;
					
				case DELETE:
					// 파일이 삭제되었을 때, 해당 파일을 재생성한다.
					try {
						File file = new File(CONNECTION_FILE_DIR);
						file.createNewFile();
						
					} catch (IOException e) {
					} break;
					
				default:
					return;
				}
			}
		};
		
	}
	
	public void start() {
		mObserver.startWatching();
	}
	
	public void stop() {
		mObserver.stopWatching();
	}
}
