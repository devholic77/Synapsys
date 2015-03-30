package org.gbssm.synapsys;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * 
 * @author Yeonho.Kim
 * @since 2015.03.07
 *
 */
public abstract class MessageProtocol  {

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static MessageProtocol[] decode(byte[] bytes) {
		throw new UnsupportedOperationException("This method must be overrided.");
	}

	/**
	 * 
	 * @return
	 */
	public abstract byte[] encode();

}

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.28
 *
 */
class ControlProtocol<V1, V2, V3> extends MessageProtocol {

	public static final String END = "\n";
	
	public static final int MSG_SIZE = 128;
	
	// *** TYPE *** //
	/**
	 * 
	 */
	static final int TYPE_MOUSE_EVENT = 1;
	/**
	 * 
	 */
	static final int TYPE_APP_EVENT = 10;

	
	// *** CODE *** //
	/**
	 * 
	 */
	static final int CODE_MOUSE_MOVE = 0;
	/**
	 * 
	 */
	static final int CODE_MOUSE_CLICK_LEFT = 1;
	/**
	 * 
	 */
	static final int CODE_MOUSE_CLICK_RIGHT = 2;
	/**
	 * 
	 */
	static final int CODE_APP_START = 10;
	/**
	 * 
	 */
	static final int CODE_APP_STOP = 1;
	
	/**
	 * 
	 */
	int mType;
	/**
	 * 
	 */
	int mCode;
	/**
	 * 
	 */
	V1 mValue1;
	/**
	 * 
	 */
	V2 mValue2;
	/**
	 * 
	 */
	V3 mValue3;
	
	
	ControlProtocol(int type) {
		super();

		mType = type;
	}
	
	@Override
	public byte[] encode() {
		StringBuilder builder = new StringBuilder();
		builder.append(mType).append(":");
		builder.append(mCode).append(":");
		builder.append(mValue1).append(":");
		builder.append(mValue2).append(":");
		builder.append(mValue3).append(":");
		builder.append(END);
		
		ByteBuffer buffer = 
				ByteBuffer.allocate(MSG_SIZE)
					.put(builder.toString().getBytes());
		
		return buffer.array();
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static ControlProtocol<?, ?, ?>[] decode(byte[] bytes) {
		String received = new String(bytes, Charset.forName("UTF-8"));
		Log.i("Synapsys_Message", "ControlThread_Decoding : " + received);
		
		ArrayList<ControlProtocol<?, ?, ?>> results = new ArrayList<ControlProtocol<?, ?, ?>>();
		
		String[] messages = received.split(END);
		for(int itr = 0; itr < messages.length; itr++) {
			try {
				String[] values = messages[itr].split(":");
				
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
				if (END.equals(values[5])) {
					results.add(protocol);
					Log.i("Synapsys_Message", "ControlThread_Decoding : " + protocol.mType +" / " + protocol.mCode + " / " + protocol.mValue1 + " / " + protocol.mValue2 + " / " + protocol.mValue3);
				}
			} catch (Exception e) { ; }
		}
		
		return (ControlProtocol[]) results.toArray();
	}

	/**
	 * 
	 * @param service
	 */
	public void process(SynapsysManagerService service) {
		try {
			switch (mType) {
			case TYPE_MOUSE_EVENT: {
				service.interpolateMouseEvent(mCode, (Float)mValue1, (Float)mValue2);
				return;
			}
	
			case TYPE_APP_EVENT: {
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
}

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.28
 *
 */
class MediaProtocol extends MessageProtocol {
	
	public static final int MSG_SIZE = 128;
	
	MediaProtocol() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public byte[] encode() {
		
		return null;
	}
	
	public static MediaProtocol[] decode(byte[] bytes) {
		
		MediaProtocol[] protocols = new MediaProtocol[1];
		
		return protocols;
	}
}
