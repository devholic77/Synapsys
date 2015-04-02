package org.gbssm.synapsys;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.05
 * @hide
 */
interface ISynapsysManager {

	/**
	 * Display 연결 소켓에 대한 Port번호를 전달받는다.
	 *
	 */
	int requestDisplayConnection();
	
	/**
	 * Android Device의 Touch Event를 PC의 Mouse Event로 발생시킨다.
	 *
	 */
	boolean invokeMouseEventFromTouch(int event_id, float event_x, float event_y);
	
	/**
	 * PC로부터 전달받은 Mouse Event를 Android에 적용시킨다.
	 *
	 */
	boolean interpolateMouseEvent(int event_id, float event_x, float event_y);
	
	boolean invokeKeyboardEvent(int event_id, int key_code);
	
	boolean invokeNotificationEvent();
	
	boolean invokeTaskInfoEvents();
}
