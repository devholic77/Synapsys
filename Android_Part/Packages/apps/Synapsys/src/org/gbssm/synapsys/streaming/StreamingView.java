package org.gbssm.synapsys.streaming;

import org.gbssm.synapsys.R;
import org.gbssm.synapsys.global.SynapsysApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Windows 확장 디스플레이 화면 스트리밍을 보여주는 View.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class StreamingView extends SurfaceView implements SurfaceHolder.Callback {

	private final static String TAG = "Synapsys_StreamingView";
	
	private final SynapsysApplication mApplication;
	
	private SurfaceThread mSurfaceThread;
	private SurfaceHolder mHolder;
	
	Bitmap mSurfaceImage;
	Bitmap mStreamingImage;
	
	public StreamingView(Context context) {
		this(context, null, 0);
	}

	public StreamingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StreamingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mApplication = (SynapsysApplication) context.getApplicationContext();
		mApplication.notifyStreamingView(this);
		
		init();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		init();
		if (mSurfaceThread != null)
			mSurfaceThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mSurfaceThread != null)
			mSurfaceThread.setDisplaySize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mSurfaceThread != null)
			mSurfaceThread.destroy();
	}

	private void init() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		if (mSurfaceThread != null)
			mSurfaceThread.destroy();
		mSurfaceThread = new SurfaceThread();	
		
		//mSurfaceImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher2);
	}

	synchronized void switchSurfaceImage() {
		if (mStreamingImage == null) {
			Log.d(TAG, "Streaming Image NULL!");
			return;
		}

		Log.d(TAG, "SurfaceImage is switched!");
		mSurfaceImage = mStreamingImage.copy(Bitmap.Config.ARGB_8888, false);
		mStreamingImage.recycle();
	}
	
	/**
	 * 
	 * @author Yeonho.Kim
	 * @since 2013.03.05
	 *
	 */
	private class SurfaceThread extends Thread {

		private boolean isRunning;

		private Paint mPaint;
		private Rect mRect;
		
		private SurfaceThread() {
			init();
		}

		private void init() {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager mWindowManager = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.getDefaultDisplay().getMetrics(dm);
			
			mRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
			mPaint = new Paint();
		}
		
		private void setDisplaySize(int width, int height) {
			if (mRect != null) {
				mRect.right = width;
				mRect.bottom = height;
			}
		}
		
		@Override
		public void run() {
			Log.d(TAG, "SurfaceThread is running.");
			
			try {
				while (mSurfaceThread.isRunning) {
					final Canvas canvas = mHolder.lockCanvas();
					try {
						synchronized (mHolder) {
							canvas.drawColor(Color.BLACK);
							canvas.drawBitmap(mSurfaceImage, null, mRect, mPaint);
						}
					} finally {
						mHolder.unlockCanvasAndPost(canvas);
					}
				}
			} catch (NullPointerException e) { 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Log.d(TAG, "SurfaceThread is dead.");
		}

		@Override
		public synchronized void start() {
			this.isRunning = true;
			super.start();
		}
		
		public synchronized void destroy() {
			this.isRunning = false;
			mSurfaceThread = null;
		}
	}
}
