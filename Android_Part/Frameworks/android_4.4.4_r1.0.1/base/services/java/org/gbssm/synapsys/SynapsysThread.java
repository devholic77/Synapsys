package org.gbssm.synapsys;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.26
 *
 */
public abstract class SynapsysThread extends Thread {

	protected static final int TIMEOUT = 5000; 	// ms

	protected final String TAG = "SynapsysThread";

	/**
	 * {@link SynapsysHanlder} 명령 전달 Handler.
	 */
	protected final SynapsysHandler mHandler;
	
	/**
	 * Thread 파괴(중) 상태 여부
	 */
	protected boolean isDestroyed;
	
	
	public SynapsysThread(SynapsysHandler handler) {
		mHandler = handler;
	}
	
	/**
	 * Thread를 종료한다. 
	 */
	@Override
	public abstract void destroy();
}
