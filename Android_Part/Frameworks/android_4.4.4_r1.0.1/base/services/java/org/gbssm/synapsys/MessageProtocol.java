package org.gbssm.synapsys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.gbssm.synapsys.SynapsysManagerService.SynapsysHandler;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Slog;

/**
 * Synapsys System에서 사용하는 Message에 관련된 Protocol을 구현한 클래스.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.07
 *
 */
public abstract class MessageProtocol {
	
	protected static final String TAG = "Synapsys_MessageProtocol";

	/**
	 * {@link SynapsysHandler} Message KeyCode.
	 * @author Yeonho.Kim
	 * @since 2015.03.07
	 *
	 */
	interface Handler {
		// *** Level C : Connection 관련 *** //
		/**
		 * Handler 메시지 : 데이터 소켓 오픈 및 진행.
		 * Handler obj : {@link ConnectionBox} 
		 */
		static final int MSG_PROCEED_CONTROL = 0xC100;
		/**
		 * Handler 메시지 : 데이터 소켓 연결 성립 알림.
		 */
		static final int MSG_CONNECTED_CONTROL = 0xC11C;
		/**
		 * Handler 메시지 : 미디어 연결 해제 및 재진행 명령.
		 * Handler obj : {@link ConnectionBox} 
		 */
		static final int MSG_EXIT_CONTROL = 0xC10E;
		/**
		 * Handler 메시지 : 데이터 연결 해제 명령.
		 */
		static final int MSG_DESTROY_CONTROL = 0xC10D;
		/**
		 * Handler 메시지 : 데이터 연결 해제 알림.
		 */
		static final int MSG_DESTROYED_CONTROL = 0xC11D;

		/**
		 * Handler 메시지 : 미디어 소켓 오픈 및 진행.
		 * Handler obj : {@link ConnectionBox} 
		 */
		static final int MSG_PROCEED_MEDIA = 0xC200;
		/**
		 * Handler 메시지 : 미디어 소켓 연결 성립 알림.
		 */
		static final int MSG_CONNECTED_MEDIA = 0xC21C;
		/**
		 * Handler 메시지 : 미디어 연결 해제 명령.
		 * Handler obj : {@link ConnectionBox} 
		 */
		static final int MSG_EXIT_MEDIA = 0xC20E;
		/**
		 * Handler 메시지 : 미디어 연결 해제 명령.
		 */
		static final int MSG_DESTROY_MEDIA = 0xC20D;
		/**
		 * Handler 메시지 : 미디어 연결 해제 알림.
		 */
		static final int MSG_DESTROYED_MEDIA = 0xC21D;
		
		/**
		 * Handler 메시지 : 디스플레이 소켓.
		 */
		static final int MSG_PROCEED_DISPLAY = 0xC300;
				
		
		// *** LEVEL E : Event 관련 *** //
		/**
		 * Handler 메시지 :
		 */
		static final int MSG_PUSH_NOTIFICATION = 0x700;
		/**
		 * Handler 메시지 :
		 */
		static final int MSG_PULL_NOTIFICATION = 0x707;
		/**
		 * Handler 메시지 :
		 */
		static final int MSG_PUSH_TASKINFO = 0x800;
		/**
		 * Handler 메시지 :
		 */
		static final int MSG_PULL_TASKINFO = 0x808;
				
		
		// *** LEVEL B : Broadcast 관련 *** //
		/**
		 * Handler 메시지 : 브로드캐스트 알림 메시지.
		 * Handler obj : String Message
		 */
		static final int MSG_BROADCAST_ALARM = 0xB0A;		
	}
	
	/**
	 * Protocol Byte배열을 {@link MessageProtocol} 배열로 디코딩한다.
	 * 
	 * @param bytes
	 * @return
	 */
	public static MessageProtocol[] decode(byte[] bytes) {
		throw new UnsupportedOperationException("This method must be overrided.");
	}

	/**
	 * 본 객체를 Byte배열로 인코딩한다.
	 * 
	 * @return
	 */
	public abstract byte[] encode();
}


/**
 * 
 * Data-Control 소켓 Connection에서 사용하는 Protocol.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.28
 *
 */
class ControlProtocol<V1, V2, V3> extends MessageProtocol {

	// *** CONSTANTS PART *** //
	/**
	 * Protocol Message 마침표
	 */
	public static final String END = "\n";
	/**
	 * Protocol Message 분리자
	 */
	public static final String SPLITTER = ":";
	/**
	 * Protocol Message 크기 (byte)
	 */
	public static final int MSG_SIZE = 256;
	
	// === TYPE === //
	/**
	 * TYPE : 마우스 이벤트
	 */
	static final int TYPE_MOUSE_EVENT = 1;
	/**
	 * TYPE : 애플리케이션 이벤트
	 */
	static final int TYPE_APP_EVENT = 10;

	// === CODE === //
	/**
	 * CODE : Type::마우스이벤트_이동
	 */
	static final int CODE_MOUSE_MOVE = 0;
	/**
	 * CODE : Type::마우스이벤트_좌클릭
	 */
	static final int CODE_MOUSE_CLICK_LEFT = 1;
	/**
	 * CODE : Type::마우스이벤트_우클릭
	 */
	static final int CODE_MOUSE_CLICK_RIGHT = 2;
	/**
	 * CODE : Type::애플리케이션이벤트_시작
	 */
	static final int CODE_APP_START = 10;
	/**
	 * CODE : Type::애플리케이션이벤트_중지
	 */
	static final int CODE_APP_STOP = 1;
	
	

	// *** MEMBER PART *** //
	/**
	 *  Type Value
	 */
	int mType;
	/**
	 * Code Value
	 */
	int mCode;
	/**
	 * Value1
	 */
	V1 mValue1;
	/**
	 * Value2
	 */
	V2 mValue2;
	/**
	 * Value3
	 */
	V3 mValue3;
	
	
	ControlProtocol(int type) {
		mType = type;
	}

	/**
	 * 
	 * @param service
	 */
	void process(SynapsysManagerService service) {
		try {
			switch (mType) {
			case TYPE_MOUSE_EVENT: {
				Slog.i("Synapsys_MessageProtocol", "Process_Mouse_Event! : " + mCode);
				service.interpolateMouseEvent(mCode, (Float)mValue1, (Float)mValue2);
				return;
			}
	
			case TYPE_APP_EVENT: {
				Slog.i("Synapsys_MessageProtocol", "Process_App_Event! : " + mCode);
				switch (mCode) {
				case CODE_APP_START:
					break;
	
				case CODE_APP_STOP:
					break;
				}
				return;
			}
			
			default:
			}
		} catch (RemoteException e) {
			
		}
	}
	
	@Override
	public byte[] encode() {
		StringBuilder builder = new StringBuilder();
		builder.append(mType).append(SPLITTER);
		builder.append(mCode).append(SPLITTER);
		builder.append(mValue1).append(SPLITTER);
		builder.append(mValue2).append(SPLITTER);
		builder.append(mValue3).append(SPLITTER);
		builder.append(END);
		
		ByteBuffer buffer = 
				ByteBuffer.allocate(MSG_SIZE)
					.put(builder.toString().getBytes());
		
		return buffer.array();
	}

	
	
	// *** STATIC PART *** //
	public static ControlProtocol<?, ?, ?>[] decode(byte[] bytes) {
		ArrayList<ControlProtocol<?, ?, ?>> results = new ArrayList<ControlProtocol<?, ?, ?>>();

		try {
			String received = new String(bytes, "UTF-8");
			String[] messages = received.split(END);
			
			if (messages.length > 0) {
				for(int itr = 0; itr < messages.length; itr++) {
					try {
						String[] values = messages[itr].split(SPLITTER);
						
						int type = Integer.parseInt(values[0]);
						int code = Integer.parseInt(values[1]);
						
						ControlProtocol<?, ?, ?> protocol = null;
						switch (type) {
						case TYPE_MOUSE_EVENT: {
							ControlProtocol<Float, Float, Float> p = new ControlProtocol<Float, Float, Float>(type);
							try {
								p.mValue1 = Float.parseFloat(values[2]);
								p.mValue2 = Float.parseFloat(values[3]);
								p.mValue3 = Float.parseFloat(values[4]);
								
								protocol = p;
							} catch (NumberFormatException e) { ; }
						} break;
						
						case TYPE_APP_EVENT: {
							ControlProtocol<Integer, Integer, Integer> p = new ControlProtocol<Integer, Integer, Integer>(type);
							try {
								p.mValue1 = Integer.parseInt(values[2]);
								p.mValue2 = Integer.parseInt(values[3]);
								p.mValue3 = Integer.parseInt(values[4]);
								
								protocol = p;
							} catch (NumberFormatException e) { ; }
						} break;
						
						default:
							continue;
						}

						protocol.mCode = code;
						results.add(protocol);
						
					} catch (Exception e) { 
						Slog.w(TAG, e.getMessage());
					}
				}
			}
			
		} catch (Exception e) { ; 
			Slog.e(TAG, e.getMessage());
		}
		
		return results.toArray(new ControlProtocol<?, ?, ?>[results.size()]);
	}

}

/**
 * Media-Thumbnail 소켓 Connection에서 사용하는 Protocol.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.28
 *
 */
class MediaProtocol extends MessageProtocol {

	// *** CONSTANTS PART *** //
	public static final int MSG_SIZE = 128;

	public static final String START = "#";
	public static final String SPLITTER = ":";
	public static final String COMMA = ",";
	public static final String END = "$";
	
	/**
	 * 
	 */
	public static final int STATE_FINISHED = 0;
	/**
	 * 
	 */
	public static final int STATE_TOP = 1;
	/**
	 * 
	 */
	public static final int STATE_PREVIOUS_TOP = 2;

	
	// *** MEMBER PART *** //
	/**
	 * Message Protocol State
	 */
	int state;
	/**
	 * Application ID
	 */
	int appID;
	/**
	 * Application 이름
	 */
	String appName;
	/**
	 * Icon Bitmap 객체
	 */
	private Bitmap icon;
	/**
	 * Icon 이미지 시작 포인트
	 */
	private final int iconFrom = 0;
	/**
	 * Icon 이미지 크기
	 */
	private int iconSize;
	/**
	 * Thumbnail Bitmap 객체
	 */
	private Bitmap thumbnail;
	/**
	 * Thumbnail 이미지 시작 포인트
	 */
	private int thumbnailFrom = 0;
	/**
	 * Thumbnail 이미지 크기
	 */
	private int thumbnailSize;
	
	
	MediaProtocol(int state) {
		this.state = state;
	}
	
	public void putIcon(Drawable drawable) {
		if (drawable == null)
			return;
		
		BitmapDrawable bd = (BitmapDrawable) drawable;
		putIcon(bd.getBitmap());
	}
	
	public void putIcon(Bitmap icon) {
		if (icon == null)
			return ;
		
		this.icon = icon;
	}
	
	public void putThumbnail(Bitmap thumbnail) {
		if (thumbnail == null)
			return;
		
		this.thumbnail = thumbnail;
	}
	
	@Override
	public byte[] encode() {
		// TEST
		File dir = new File("/data/synapsys/", appName);
		dir.mkdir();
		
		ByteArrayOutputStream iconByteStream = new ByteArrayOutputStream();
		if (icon != null) {
			icon.compress(CompressFormat.JPEG, 100, iconByteStream);
			
			// TEST
			try {
				File file = new File(dir, "icon.jpg");
				file.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(file);
				icon.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (IOException e) { ; }
		}
		iconSize = iconByteStream.size();

		ByteArrayOutputStream thumbnailByteStream = new ByteArrayOutputStream();
		if (thumbnail != null) {
			thumbnail.compress(CompressFormat.JPEG, 100, thumbnailByteStream);
			
			// TEST
			try {
				File file = new File(dir, "thumbnail.jpg");
				file.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(file);
				thumbnail.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (IOException e) { ; }
		}
		thumbnailFrom = iconSize;
		thumbnailSize = thumbnailByteStream.size();

		byte[] header = toString().getBytes();
		return ByteBuffer.allocate(header.length + iconSize + thumbnailSize)
					.put(header)
					.put(iconByteStream.toByteArray())
					.put(thumbnailByteStream.toByteArray())
					.array();
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(START);
		builder.append(state).append(SPLITTER);
		builder.append(appID).append(SPLITTER);
		builder.append(appName).append(SPLITTER);
		builder.append(iconFrom).append(COMMA).append(iconSize).append(SPLITTER);
		builder.append(thumbnailFrom).append(COMMA).append(thumbnailSize);
		builder.append(END);
		
		return builder.toString();
	}
	
	// *** STATIC PART *** //
	public static MediaProtocol[] decode(byte[] bytes) {
		
		MediaProtocol[] protocols = new MediaProtocol[1];
		
		return protocols;
	}
}
