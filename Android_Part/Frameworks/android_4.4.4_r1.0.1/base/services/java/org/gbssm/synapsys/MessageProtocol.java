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
	
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static MessageProtocol decode(byte[] bytes) {
		MessageProtocol message = new MessageProtocol();
		
		return message; 
	}
	
	public MessageProtocol() {
		// TODO Auto-generated constructor stub
	}
	
	public byte[] encode() {
		
		return null;
	}
	
}
