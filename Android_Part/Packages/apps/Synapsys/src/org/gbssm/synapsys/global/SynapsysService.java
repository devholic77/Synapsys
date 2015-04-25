package org.gbssm.synapsys.global;

import org.gbssm.synapsys.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.04.25
 *
 */
public class SynapsysService extends Service {

	public static final int NOTIFICATION_ID = 20150304;
	
	private SynapsysApplication mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mApplication = (SynapsysApplication) getApplication();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int port = mApplication.getSynapsysManager().requestDisplayConnection();
		if (port == -1)
			return START_NOT_STICKY ;
		
		int deviceOrder = (port-1234)/3;
		int icon = (deviceOrder % 2 == 0)? R.drawable.icon1 : R.drawable.icon2;
		
		
		// 환경설정을 위한 Notification을 띄워준다.
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, 
											NOTIFICATION_ID,
											new Intent(this, SettingActivity.class),
											Intent.FLAG_ACTIVITY_SINGLE_TOP);
											
		
		Notification notification = new Notification.Builder(this)
									.setSmallIcon(icon)
									.setContentTitle(getText(R.string.app_name))
									.setContentText(getText(R.string.content_text))
									.setContentIntent(mPendingIntent)
									.build();
		
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
		 
		startForeground(NOTIFICATION_ID, notification);
	    
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
	}
}
