package org.gbssm.synapsys.streaming;

import org.gbssm.synapsys.global.SynapsysApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Windows 확장 디스플레이 화면 스트리밍을 보여주는 View.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * 
 */
public class StreamingView extends SurfaceView implements SurfaceHolder.Callback {
	protected final static boolean DEBUG = false;
	private final static String TAG = "Synapsys_StreamingView";
	
	private final SynapsysApplication mApplication;
	
	private SurfaceHandler mSurfaceHandler;
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
		
		init();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		init();

		mSurfaceHandler.isRunning = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceHandler.setDisplaySize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHandler.isRunning = false;
		mApplication.notifyStreamingView(null);
		
		// Bitmap Memory Release!
		if (mSurfaceImage != null)
			mSurfaceImage.recycle();
		mSurfaceImage = null;
		
		if (mStreamingImage != null)
			mStreamingImage.recycle();
		mStreamingImage = null;
	}

	private void init() {
		mHolder = getHolder();
		mHolder.addCallback(this);

		mSurfaceHandler = new SurfaceHandler();
		mApplication.notifyStreamingView(this);
	}

	synchronized void switchSurfaceImage() {
		if (mStreamingImage == null) {
			if (DEBUG)
				Log.d(TAG, "Streaming Image NULL!");
			return;
		}

		Log.d(TAG, "SurfaceImage is switched!");
		synchronized (mHolder) {
			Bitmap temp = mSurfaceImage;
			mSurfaceImage = mStreamingImage;
			mStreamingImage = temp;
		}

		mSurfaceHandler.sendEmptyMessage(SurfaceHandler.SWITCH);
		
	}
	
	/**
	 * 
	 * @author Yeonho.Kim
	 * @since 2015.04.22
	 *
	 */
	private class SurfaceHandler extends Handler implements Runnable {
		static final int SWITCH = 0x0;

		boolean isRunning;
		
		private Paint mPaint;
		private Rect mRect;
		
		public SurfaceHandler() {
			init();
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SWITCH:
				new Thread(this).start();
				break;
			}
		}
		
		@Override
		public void run() {
			synchronized (mHolder) {
				if (!isRunning)
					return;
				
				Canvas canvas = mHolder.lockCanvas();
				try {
					canvas.drawColor(Color.BLACK);
					canvas.drawBitmap(mSurfaceImage, null, mRect, mPaint);

				} catch (Exception e) {
					;
				} finally {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}

		}
		
		private void init() {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager mWindowManager = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.getDefaultDisplay().getMetrics(dm);
			
			mRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
			mPaint = new Paint();
			
			isRunning = true;
		}

		void setDisplaySize(int width, int height) {
			if (mRect != null) {
				mRect.right = width;
				mRect.bottom = height;
			}
		}
		
	}
	
}
