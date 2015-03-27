package org.gbssm.synapsys;

import android.app.Activity;
import android.os.Bundle;
import android.os.ServiceManager;
import android.view.MotionEvent;

/**
 * 애플리케이션을 시작하는 Main Activity.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class MainActivity extends Activity {

	private WindowsTouchListener mTouchListener;

	private StreamingView mStreamingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mStreamingView = (StreamingView) findViewById(R.id.streamingView);
		mTouchListener = new WindowsTouchListener(this);		
	
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTouchListener != null && mStreamingView != null)
			if (mStreamingView.isConnected())
				return mTouchListener.onTouchEvent(event);
		
		return false;
	}

}
