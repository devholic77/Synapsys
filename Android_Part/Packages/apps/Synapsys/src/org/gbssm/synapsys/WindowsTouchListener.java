package org.gbssm.synapsys;

import android.content.Context;
import android.hardware.input.IInputManager;
import android.os.ServiceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.hardware.input.InputManager;


/**
 * 
 * 
 * @author Dhuckil.Kim
 * @since 2015.03.05
 * 
 */
public class WindowsTouchListener implements OnGestureListener, OnDoubleTapListener {

	final static int LEFT_CLICK = 1;
	final static int LEFT_DRAG = 2;
	final static int LEFT_DOUBLE_CLICK = 3;
	final static int RIGHT_CLICK = 4;
	final static int SCROLL_UP_DOWN = 5;
	final static int SCROLL_LEFT_RIGHT = 6;
	final static int EVENT_END = 7;
	
	int Doubletap_flag = 0;
	int MultiTouch_flag = 0;
	int Scroll_flag = 0;
	int act = 0;
	
	private static final String DTAG = "TestServer";

	MotionEvent temp_e;

	private final Context mContextF;
	private GestureDetector mGestureDetector;
	
	// TestManager 객체 생성
	IInputManager om = IInputManager.Stub.asInterface(ServiceManager.getService("input"));
	InputManager im ;
	// SynapsysManager 객체 생성

    SynapsysManager synapsysManager;
   
  
    public WindowsTouchListener(Context context) {
		mContextF = context;
		mGestureDetector = new GestureDetector(mContextF, this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(this);		
	    synapsysManager = (SynapsysManager)context.getSystemService(Context.SYNAPSYS_SERVICE);	 	  
	    im = (InputManager)context.getSystemService(Context.INPUT_SERVICE);	   
	}

	public boolean onTouchEvent(MotionEvent e) {
		temp_e = e;
		act = temp_e.getAction();
		if (act == MotionEvent.ACTION_POINTER_2_DOWN) {		// 멀티 터치가 되었을 때 
			
			MultiTouch_flag = 1;
		}
		else if(Scroll_flag == 1 && act == MotionEvent.ACTION_UP){	// 스크롤이 되고 이벤트가 끝났을 때 		
			
			Scroll_flag = 0;
			Log.d("Touch listener","On Scroll End" + " AXIS_X :"	+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"+ e.getAxisValue(e.AXIS_Y));
			MouseEvent(EVENT_END, e.getAxisValue(e.AXIS_X),e.getAxisValue(e.AXIS_Y));		
			MultiTouch_flag = 0;
		}
	
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

		MultiTouch_flag = 0;
		return false;
	}

	@Override
	// 마우스 좌 클릭 드래그
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		// TODO Auto-generated method stub
		Scroll_flag = 1;
		
		if (MultiTouch_flag == 1) {
			Log.d("Touch listener","On Double Scroll" + " AXIS_X :"+ e2.getAxisValue(e2.AXIS_X) + " AXIS_Y :"+ e2.getAxisValue(e2.AXIS_Y));
			MouseEvent(SCROLL_UP_DOWN, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
		} else if (MultiTouch_flag == 0) {
			Log.d("Touch listener","On Drag" + " AXIS_X :" + e2.getAxisValue(e2.AXIS_X)+ " AXIS_Y :" + e2.getAxisValue(e2.AXIS_Y));
			MouseEvent(LEFT_DRAG, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
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
		Log.d("Touch listener","On single Tap confirmed" + "AXIS_X :"+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"+ e.getAxisValue(e.AXIS_Y));
		MouseEvent(LEFT_CLICK, e.getAxisValue(e.AXIS_X),	e.getAxisValue(e.AXIS_Y));
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
			Log.d("Touch listener","On double Tap event" + " AXIS_X :"+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"+ e.getAxisValue(e.AXIS_Y));
			MouseEvent(LEFT_DOUBLE_CLICK, e.getAxisValue(e.AXIS_X),	e.getAxisValue(e.AXIS_Y));
			Doubletap_flag = 0;
		}

		return false;
	}

	@Override
	// 마우스 우클릭
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("Touch listener","On Long press" + "AXIS_X :" + e.getAxisValue(e.AXIS_X)+ " AXIS_Y :" + e.getAxisValue(e.AXIS_Y));
		MouseEvent(RIGHT_CLICK, e.getAxisValue(e.AXIS_X),e.getAxisValue(e.AXIS_Y));
	}

	/*
	 * MouseEvent 처리 메소드
	 */
	public void MouseEvent(int event_id, float point_x, float point_y) {
		
		synapsysManager.invokeMouseEventFromTouch(event_id, point_x, point_y);
	
	}

}
