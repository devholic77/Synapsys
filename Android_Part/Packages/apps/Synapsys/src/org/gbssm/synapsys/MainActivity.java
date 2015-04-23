package org.gbssm.synapsys;

import org.gbssm.synapsys.global.SynapsysApplication;
import org.gbssm.synapsys.streaming.StreamingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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
	
	private View mStreamingBoard;

	private WindowsTouchListener mTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mApplication = (SynapsysApplication) getApplication();	
		mTouchListener = new WindowsTouchListener(this);	
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mStreamingView = (StreamingView) findViewById(R.id.streamingView);
		mStreamingBoard = findViewById(R.id.streaming_board);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mApplication.notifyStreamingView(mStreamingView);
		mApplication.notifyStreamingActivity(this);
		mApplication.startStreaming();
		
		notifyDisplaying(mApplication.isDisplaying());
	}

	@Override
	protected void onPause() {
		mApplication.notifyStreamingView(null);
		mApplication.notifyStreamingActivity(null);
		
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

	public void notifyDisplaying(boolean enable) {
		if (enable) {
			mStreamingBoard.setVisibility(View.GONE);
			
		} else {
			mStreamingBoard.setVisibility(View.VISIBLE);
			
		}
	}
	
}
