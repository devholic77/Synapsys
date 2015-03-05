package org.gbssm.synapsys;

import android.os.IBinder;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 *
 */
public class SynapsysManagerProxy implements ISynapsysManager {

    private IBinder mRemote;
    
	public SynapsysManagerProxy(IBinder remote) {
		mRemote = remote;
	}

	public IBinder asBinder() {
		return mRemote;
	}

}
