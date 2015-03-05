package org.gbssm.synapsys;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 작업 시작!!
 * 
 * @author Dhuckil.Kim
 * @since 2015.03.05
 * 
 */
public class WindowsTouchListener implements OnGestureListener, OnDoubleTapListener {

	private final Context mContextF;

	private GestureDetector mGestureDetector;

	public WindowsTouchListener(Context context) {
		mContextF = context;

		mGestureDetector = new GestureDetector(mContextF, this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(this);
	}

	public boolean onTouchEvent(MotionEvent e) {
		return mGestureDetector.onTouchEvent(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On Down");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On Fling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On Longpress");
		// Toast.makeText(mContextF, "onLongPress!! : " + e.getX() +", " +
		// e.getY(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On Scroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On showPress");

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On singletapup");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On singletapconfirmed");
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On doubletap");
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On doubletapevent");
		return false;
	}

}
