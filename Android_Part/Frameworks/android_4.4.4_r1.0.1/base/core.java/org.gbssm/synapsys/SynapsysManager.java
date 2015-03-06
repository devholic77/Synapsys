package org.gbssm.synapsys;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 *
 */
public class SynapsysManager {

	private final ISynapsysManager mService;
	
	/* package */ SynapsysManager(ISynapsysManager service) {
		mService = service;
	}
	
	/**
	 * 
	 */
	public void requestDisplayConnection() {
		try {
			mService.requestDisplayConnection();
			
		} catch (RemoteException e) {
		}
	}
}
