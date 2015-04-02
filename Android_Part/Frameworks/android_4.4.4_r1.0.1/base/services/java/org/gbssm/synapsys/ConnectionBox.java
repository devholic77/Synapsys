package org.gbssm.synapsys;

/**
 * Android-Windows의 연결정보를 담는 객체.
 * {@link ConnectionDetector#CONNECTION_FILE_DIR}의 내용을 읽어 객체화한다.
 * 
 * @author Yeonho.Kim
 * @since 2015.03.15
 *
 */
class ConnectionBox {
	
	// *** CONSTANTS PART *** //
	/**
	 * Data-Control Connection.
	 */
	public static final int TYPE_CONTROL = 1;
	/**
	 * Media-Thumbnail Connection.
	 */
	public static final int TYPE_MEDIA = 2;
	/**
	 * Display-Streaming Connection.
	 */
	public static final int TYPE_DISPLAY = 3;

	

	// *** MEMBER PART *** //
	final int type;
	
	String deviceName;
	int deviceId;
	int port;
	
	public ConnectionBox(int type) {
		this.type = type;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		// 담겨져 있는 값으로만 일치여부를 검사하기 위해 hashCode()를 비교한다.
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		StringBuilder builder = new StringBuilder(type);
		builder.append(deviceName);
		builder.append(deviceId);
		builder.append(port);

		// 담겨져 있는 값들을 나열한 후, hashCode 값을 반환한다.
		return builder.toString().hashCode();
	}
}
