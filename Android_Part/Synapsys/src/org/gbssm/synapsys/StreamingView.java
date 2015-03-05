package org.gbssm.synapsys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * Windows 확장 디스플레이 화면 스트리밍을 보여주는 View.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class StreamingView extends SurfaceView {

	public StreamingView(Context context) {
		this(context, null, 0);
	}

	public StreamingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StreamingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub

	}

}
