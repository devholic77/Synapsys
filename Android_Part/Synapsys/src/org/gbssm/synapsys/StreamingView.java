package org.gbssm.synapsys;

import java.net.Socket;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Windows 확장 디스플레이 화면 스트리밍을 보여주는 View.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class StreamingView extends SurfaceView implements SurfaceHolder.Callback {

	private final Context mContextF;
	
	public StreamingView(Context context) {
		this(context, null, 0);
	}

	public StreamingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StreamingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// Final 초기화.
		mContextF = context;
		
		init();
	}

	private void init() {
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), "Surface Changed!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		init();
		
		Toast.makeText(getContext(), "Surface Created!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), "Surface Destroyed!", Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * 
	 * @param socket
	 */
	void createConnection(final Socket socket) {
		
	}
	
	/**
	 * 
	 */
	void destroyConnection() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	boolean isConnected() {
		return true;
	}
	
	
	/**
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	private class StreamingThread extends Thread {
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		}
	}
	
	
}
