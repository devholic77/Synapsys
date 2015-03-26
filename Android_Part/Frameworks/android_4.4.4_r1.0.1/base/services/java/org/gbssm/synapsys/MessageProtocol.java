package org.gbssm.synapsys;

/**
 * 
 * 
 * @author Yeonho.Kim
 * @since 2015.03.07
 *
 */
public class MessageProtocol  {
	
	public static final char END = '\n';
	
	
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
	 * @param bytes
	 * @return
	 */
	public static MessageProtocol decode(byte[] bytes) {
		MessageProtocol message = new MessageProtocol(0);
		
		return message; 
	}
	
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
	int mValue1;
	/**
	 * 
	 */
	int mValue2;
	/**
	 * 
	 */
	int mValue3;
	
	
	MessageProtocol(int type) {
		mType = type;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public byte[] encode() {
		
		return null;
	}
	
	/**
	 * 
	 * @param service
	 */
	public void process(SynapsysManagerService service) {
		
		switch (mType) {
		case TYPE_MOUSE_EVENT: {
			switch (mCode) {
			case CODE_MOUSE_MOVE:
				break;

			case CODE_MOUSE_CLICK_LEFT:
				break;

			case CODE_MOUSE_CLICK_RIGHT:
				break;
			}
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
	}
}
