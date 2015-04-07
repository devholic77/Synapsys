package org.gbssm.synapsys;

import org.gbssm.synapsys.global.SynapsysApplication;
import org.gbssm.synapsys.streaming.StreamingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * 애플리케이션을 시작하는 Main Activity.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class MainActivity extends Activity {

	private SynapsysApplication mApplication;
	private StreamingView mStreamingView;

	private WindowsTouchListener mTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mApplication = (SynapsysApplication) getApplication();	
		mTouchListener = new WindowsTouchListener(this);	
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mStreamingView = (StreamingView) findViewById(R.id.streamingView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mApplication.notifyStreamingView(mStreamingView);
		mApplication.startStreaming();
	}

	@Override
	protected void onPause() {
		mApplication.notifyStreamingView(null);
		
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTouchListener != null && mStreamingView != null)
			if (mApplication.isControllerConnected())
				return mTouchListener.onTouchEvent(event);
		
		return false;
	}

	
}
