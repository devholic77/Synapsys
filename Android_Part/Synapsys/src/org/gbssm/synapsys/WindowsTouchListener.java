package org.gbssm.synapsys;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * 작업 시작!!
 * 
 * @author dhuckil.kim
 * @since 2015.03.05
 *
 */
public class WindowsTouchListener implements OnTouchListener, OnGestureListener {

	private final Context mContextF;
	
	private GestureDetector gd;
	
	public WindowsTouchListener(Context context) {
		mContextF = context;
		
		// TODO Auto-generated constructor stub
		gd = new GestureDetector(mContextF, this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		
		return gd.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		
		Toast.makeText(mContextF, "onFling!! : " + e1.getX() +", " + e1.getY(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

		Toast.makeText(mContextF, "onLongPress!! : " + e.getX() +", " + e.getY(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
