package org.gbssm.synapsys;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 *
 */
public abstract class SynapsysManagerNative extends Binder implements ISynapsysManager {
	
    private static final Singleton<ISynapsysManager> gDefault = new Singleton<ISynapsysManager>() {
    	
        protected ISynapsysManager create() {
            IBinder binder = ServiceManager.getService("synapsys");
            if (false) {
                Log.v("SynapsysManager", "default service binder = " + binder);
            }
            
            ISynapsysManager sm = asInterface(binder);
            if (false) {
                Log.v("SynapsysManager", "default service = " + sm);
            }
            
            return sm;
        }
    };
    
	 /**
     * Cast a Binder object into an synapsys manager interface, generating
     * a proxy if needed.
     */
    static public ISynapsysManager asInterface(IBinder obj) {
        if (obj == null) 
            return null;
        
        ISynapsysManager in = (ISynapsysManager)obj.queryLocalInterface(Descriptor);
        if (in != null) 
            return in;
        
        return new SynapsysManagerProxy(obj);
    }

    
    public SynapsysManagerNative() {
        attachInterface(this, Descriptor);
    }

    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
    		throws RemoteException {
    	
    	// TODO Auto-generated method stub
    	return super.onTransact(code, data, reply, flags);
    }
}
