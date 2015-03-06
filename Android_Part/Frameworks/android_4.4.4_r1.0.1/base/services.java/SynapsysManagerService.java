package org.gbssm.synapsys;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.06
 *
 */
public class SynapsysManagerService extends ISynapsysManager.Stub {

	Context mContext;
	
	public SynapsysManagerService(Context context) {
		mContext = context;
	}
	
	public void requestDisplayConnection() throws RemoteException {
		Slog.i("SynapsysManagerService", "reqeustDisplayConnection()");
	}
}
