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

public class WindowsTouchListener implements OnGestureListener,
		OnDoubleTapListener {

	final static int LEFT_CLICK = 1;
	final static int LEFT_DRAG = 2;
	final static int LEFT_DOUBLE_CLICK = 3;
	final static int RIGHT_CLICK = 4;
	final static int SCROLL_UP_DOWN = 5;
	final static int SCROLL_LEFT_RIGHT = 6;
	int Doubletap_flag = 0;
	int Drag_flag = 0;

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
		Drag_flag = 1;		//드래그 상태 확인
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
	// 마우스 우클릭 
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener",
				"On Long press" + "AXIS_X :" + e.getAxisValue(e.AXIS_X)
						+ " AXIS_Y :" + e.getAxisValue(e.AXIS_Y));
		// Toast.makeText(mContextF, "onLongPress!! : " + e.getX() +", " +
		// e.getY(), Toast.LENGTH_SHORT).show();
		MouseEvent(RIGHT_CLICK, e.getAxisValue(e.AXIS_X),
				e.getAxisValue(e.AXIS_Y));
	}

	
	@Override
	//마우스 좌 클릭 드래그 
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		if(Drag_flag == 1)
		{
			Log.d("Touch listener", "On Scroll"+ " AXIS_X :" + e2.getAxisValue(e2.AXIS_X)
					+ " AXIS_Y :" + e2.getAxisValue(e2.AXIS_Y));
			MouseEvent(LEFT_DRAG, e2.getAxisValue(e2.AXIS_X),
					e2.getAxisValue(e2.AXIS_Y));
		}
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener", "On show Press");

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub

		Log.d("Touch listener", "On single Tap up");
		return false;
	}

	@Override
	// 마우스 좌 클릭 
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener",
				"On single Tap confirmed" + "AXIS_X :"
						+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"
						+ e.getAxisValue(e.AXIS_Y));
		MouseEvent(LEFT_CLICK, e.getAxisValue(e.AXIS_X),
				e.getAxisValue(e.AXIS_Y));
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub

		Log.d("Touch listener", "On double Tap");
		Doubletap_flag = 1;
		return false;
	}

	@Override
	// 마우스 좌 더블 클릭 
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		if (Doubletap_flag == 1) {
			Log.d("Touch listener",
					"On double Tap event" + " AXIS_X :"
							+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"
							+ e.getAxisValue(e.AXIS_Y));
			MouseEvent(LEFT_DOUBLE_CLICK, e.getAxisValue(e.AXIS_X),e.getAxisValue(e.AXIS_Y));
			Doubletap_flag = 0;
		}

		return false;
	}

	/*
	 * MouseEvent 처리 메소드
	 */
	public void MouseEvent(int event_id, float point_x, float point_y) {
		switch (event_id) {
		case LEFT_CLICK:
			// Log.d("Touch listener", "Send to event : left_click");
			break;

		case LEFT_DRAG:

			break;

		case LEFT_DOUBLE_CLICK:
			// Log.d("Touch listener", "Send to event : left_double_click");
			break;

		case RIGHT_CLICK:
			// Log.d("Touch listener", "Send to event : right_click");
			break;

		case SCROLL_LEFT_RIGHT:

			break;

		case SCROLL_UP_DOWN:

			break;

		default:

			break;
		}

	}

}
