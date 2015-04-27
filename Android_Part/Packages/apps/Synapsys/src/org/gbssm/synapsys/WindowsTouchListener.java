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
	protected final static boolean DEBUG = true;

	final static int MOUSE_MOVE = 0;
	final static int LEFT_CLICK = 1;
	final static int RIGHT_CLICK = 2;
	final static int LEFT_DOUBLE_CLICK = 3;
	final static int LEFT_UNCLICK = 4;
	final static int RIGHT_UNCLICK = 5;
	final static int SCROLL_UP = 6;
	final static int SCROLL_DOWN = 7;
	final static int SCROLL_LEFT = 8;
	final static int SCROLL_RIGHT = 9;
	final static int EVENT_END = 10;
	final static int LEFT_DRAG = 11;	
	
	
	int Doubletap_flag = 0;
	int MultiTouch_flag = 0;
	int Scroll_flag = 0;
	boolean DragEnable = true;
	int act = 0;
	int pre_point_x;
	int pre_point_y;

	private static final String DTAG = "TestServer";

	MotionEvent temp_e;

	private final Context mContextF;
	private GestureDetector mGestureDetector;

	// TestManager 객체 생성
	IInputManager om = IInputManager.Stub.asInterface(ServiceManager.getService("input"));
	InputManager im;
	
	// SynapsysManager 객체 생성
	SynapsysManager synapsysManager;

	public WindowsTouchListener(Context context) {
		mContextF = context;
		mGestureDetector = new GestureDetector(mContextF, this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(this);
		synapsysManager = (SynapsysManager) context.getSystemService(Context.SYNAPSYS_SERVICE);
		im = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
	}

	public boolean onTouchEvent(MotionEvent e) {
		temp_e = e;
		act = temp_e.getAction();
		if (act == MotionEvent.ACTION_POINTER_2_DOWN) { // 멀티 터치가 되었을 때생성된 가상 이벤트는 processEventLocked함수로 실행가능한 형태의 Argument로 구성된 후 InputDispatcherThread의 이벤트 큐에 넣어 시스템에서 동작하도록 하였다.
			MultiTouch_flag = 1;
			
		} else if (Scroll_flag == 1 && act == MotionEvent.ACTION_UP) { // 스크롤이
																		// 되고
																		// 이벤트가
																		// 끝났을 때
			Scroll_flag = 0;			
			if (DEBUG)
				Log.d("Touch listener",	"On Scroll End" + " AXIS_X :"+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"+ e.getAxisValue(e.AXIS_Y));

			MouseEvent(EVENT_END, e.getAxisValue(e.AXIS_X), e.getAxisValue(e.AXIS_Y));
			MultiTouch_flag = 0;
		}

		return mGestureDetector.onTouchEvent(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (DEBUG)
			Log.d("Touch listener", "On Down");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		MultiTouch_flag = 0;
		return false;
	}

	@Override
	// 마우스 좌 클릭 드래그
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Scroll_flag = 1;

		if (MultiTouch_flag == 1) {
			if (DEBUG)
				Log.d("Touch listener","On Double Scroll" + " AXIS_X :"	+ e2.getAxisValue(e2.AXIS_X) + " AXIS_Y :"+ e2.getAxisValue(e2.AXIS_Y));
			
			if( distanceX > distanceY ) {				
				if( distanceX > 0 ) {
					MouseEvent(SCROLL_LEFT, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
				} else if( distanceX < 0 ) {				
					MouseEvent(SCROLL_DOWN, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
				}					
			} else if( distanceX < distanceY ) {
				if( distanceY > 0 ) {
					MouseEvent(SCROLL_UP, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
				} else if( distanceY < 0 ) {
					MouseEvent(SCROLL_RIGHT, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
				}				
			}
			
		} else if (MultiTouch_flag == 0) {
			if (DEBUG)
				Log.d("Touch listener",
						"On Drag" + " AXIS_X :" + e2.getAxisValue(e2.AXIS_X)+ " AXIS_Y :" + e2.getAxisValue(e2.AXIS_Y));

			MouseEvent(LEFT_DRAG, e2.getAxisValue(e2.AXIS_X),e2.getAxisValue(e2.AXIS_Y));
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if (DEBUG)
			Log.d("Touch listener", "On show Press");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (DEBUG)
			Log.d("Touch listener", "On single Tap up");
		return false;
	}

	@Override
	// 마우스 좌 클릭
	public boolean onSingleTapConfirmed(MotionEvent e) {
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
		if (DEBUG)
			Log.d("Touch listener", "On double Tap");

		Doubletap_flag = 1;
		return false;
	}

	@Override
	// 마우스 좌 더블 클릭
	public boolean onDoubleTapEvent(MotionEvent e) {
		if (Doubletap_flag == 1) {
			if (DEBUG)
				Log.d("Touch listener",
						"On double Tap event" + " AXIS_X :"
								+ e.getAxisValue(e.AXIS_X) + " AXIS_Y :"
								+ e.getAxisValue(e.AXIS_Y));

			MouseEvent(LEFT_DOUBLE_CLICK, e.getAxisValue(e.AXIS_X), e.getAxisValue(e.AXIS_Y));
			Doubletap_flag = 0;
		}

		return false;
	}

	@Override
	// 마우스 우클릭
	public void onLongPress(MotionEvent e) {
		if (DEBUG)
			Log.d("Touch listener",
					"On Long press" + "AXIS_X :" + e.getAxisValue(e.AXIS_X)
							+ " AXIS_Y :" + e.getAxisValue(e.AXIS_Y));

		MouseEvent(RIGHT_CLICK, e.getAxisValue(e.AXIS_X), e.getAxisValue(e.AXIS_Y));
	}

	/**
	 * MouseEvent 처리 메소드
	 */
	public void MouseEvent(int event_id, float point_x, float point_y) {
		// 해상도 조절을 위한 좌표값 조정.
		point_x /= 2;
		point_y /= 2;
		
		switch (event_id) {
		case LEFT_CLICK: // 왼 클릭 이벤트 다운/업
			synapsysManager.invokeMouseEventFromTouch(LEFT_CLICK, point_x,
					point_y);
			synapsysManager.invokeMouseEventFromTouch(LEFT_UNCLICK, point_x,
					point_y);
			break;

		case RIGHT_CLICK: // 우 클릭 이벤트 다운/업
			synapsysManager.invokeMouseEventFromTouch(RIGHT_CLICK, point_x,
					point_y);
			synapsysManager.invokeMouseEventFromTouch(RIGHT_UNCLICK, point_x,
					point_y);
			break;

		case LEFT_DOUBLE_CLICK: // 좌 더블 클릭 이벤트 다운/업 2회
			synapsysManager.invokeMouseEventFromTouch(LEFT_DOUBLE_CLICK, point_x, point_y);
			
			break;

		case LEFT_DRAG: // 좌 클릭 드래그
			if (DragEnable) // 초기에 좌 클릭 다운 이벤트만 전송
			{
				synapsysManager.invokeMouseEventFromTouch(LEFT_CLICK, point_x,
						point_y);
				DragEnable = false;
				break;
			}
			synapsysManager.invokeMouseEventFromTouch(MOUSE_MOVE, point_x,
					point_y); // 좌 클릭 무브 이벤트 전송
			break;			
			
		case SCROLL_UP:
			synapsysManager.invokeMouseEventFromTouch(SCROLL_UP, point_x,point_y);		
			break;
		
		case SCROLL_DOWN:
			synapsysManager.invokeMouseEventFromTouch(SCROLL_DOWN, point_x,point_y);			
			break;
			
		case SCROLL_LEFT:
			synapsysManager.invokeMouseEventFromTouch(SCROLL_LEFT, point_x,point_y);			
			break;
			
		case SCROLL_RIGHT:
			synapsysManager.invokeMouseEventFromTouch(SCROLL_RIGHT, point_x,point_y);			
			break;
			
		case EVENT_END:
			synapsysManager.invokeMouseEventFromTouch(LEFT_UNCLICK, point_x,point_y); // 좌 클릭 업 이벤트 전송
			DragEnable = true;
			break;

		
		default:
			// synapsysManager.invokeMouseEventFromTouch(event_id, point_x,
			// point_y);
		}

	}

}
