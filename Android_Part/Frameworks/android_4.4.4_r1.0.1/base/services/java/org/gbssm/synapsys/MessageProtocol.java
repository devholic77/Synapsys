package org.gbssm.synapsys;

/**
 * 
 * 
 * @author Yeonho.Kim
 * @since 2015.03.07
 *
 */
public class MessageProtocol  {
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static MessageProtocol decode(byte[] bytes) {
		MessageProtocol message = new MessageProtocol();
		
		return message; 
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public static byte[] encode(MessageProtocol message) {
		
		return null;
	}
}
