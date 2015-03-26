package org.gbssm.synapsys;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.26
 *
 */
public abstract class SynapsysThread extends Thread {

	protected static final int TIMEOUT = 3000; 	// ms

	
	protected final String TAG = "SynapsysThread";

	protected final SynapsysHandler mHandler;
	
	/**
	 * 
	 */
	protected boolean isDestroyed;
	
	/**
	 * 
	 */
	protected boolean isExited;
	
	
	public SynapsysThread(SynapsysHandler handler) {
		mHandler = handler;
	}
	
	
	@Override
	public abstract void destroy();
	
	public abstract void exit();
	
}
