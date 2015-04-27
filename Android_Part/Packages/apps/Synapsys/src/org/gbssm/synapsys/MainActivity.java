package org.gbssm.synapsys;

import org.gbssm.synapsys.global.SynapsysApplication;
import org.gbssm.synapsys.streaming.StreamingView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mApplication = (SynapsysApplication) getApplication();	
		mTouchListener = new WindowsTouchListener(this);
		
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
		
		mApplication.getSynapsysManager().requestSynapsysForeground(true);
		
		// Synapsys Streaming 준비
		mApplication.notifyStreamingActivity(this);
		mApplication.notifyStreamingView(mStreamingView);
		
		// Synapsys Toast를 띄운다.
		mApplication.notifySynapsysDeviceByToast();
		
		// Synapsys Streaming 시작.
		mApplication.startStreaming();
		
		// SynapsysBoard를 띄울지 말지 결정한다.
		notifyStreamingStateByBoard(mApplication.isDisplaying());
	}

	@Override
	protected void onPause() {
		mApplication.notifyStreamingView(null);
		mApplication.notifyStreamingActivity(null);
		mApplication.getSynapsysManager().requestSynapsysForeground(false);
		
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

	/**
	 * StreamingBoard를 통해 Streaming 상태를 나타낸다. 
	 * 
	 * @param isStreamingNow true: StreamingBoard OFF / false: StreamingBoard ON
	 */
	public synchronized void notifyStreamingStateByBoard(boolean isStreamingNow) {
		mStreamingBoard.setVisibility(isStreamingNow? View.GONE : View.VISIBLE);
	}
	
}
