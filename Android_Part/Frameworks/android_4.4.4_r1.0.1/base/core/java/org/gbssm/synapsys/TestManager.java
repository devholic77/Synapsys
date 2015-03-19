package org.gbssm.synapsys;

import java.net.Socket;
import java.security.InvalidParameterException;
import android.os.RemoteException;

import org.gbssm.synapsys.ITestManager;


/**
 * 
 * @author dhuckil.Kim
 * @since 2015.03.09
 *
 */
public class TestManager {
	
	private final ITestManager mService;
	
	/* package */ TestManager(ITestManager service) {
		mService = service;
		
	}
	public static class Factory {
		
		public static TestManager create(ITestManager service) {
			if (service != null)
				return new TestManager(service);
			
			throw new InvalidParameterException("TestManager should be initiated by system.");
		}
	}
	
}
