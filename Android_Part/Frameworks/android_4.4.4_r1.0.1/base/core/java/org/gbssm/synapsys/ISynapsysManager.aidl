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
	 * Synapsys App의 Foreground 상태를 전달한다.
	 *
	 */
	boolean requestSynapsysForeground(boolean foreground);
	
	
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
	
	/**
	 * Android Device의 Keyboard Event를 PC의 Keyboard Event로 발생시킨다.
	 *
	 */
	boolean invokeKeyboardEvent(int event_id, int key_code);
	
	/**
	 * PC로부터 전달받은 Keyboard Event를 Android에 적용시킨다.
	 *
	 */
	boolean interpolateKeyboardEvent(int event_id, int key_code);
	
	/**
	 * PC로부터 전달받은 Task Event를 Android에 적용시킨다.
	 *
	 */
	boolean interpolateNotificationEvent(int state, int notificationId);
	
	/**
	 * Android Device의 Task Event를 PC의 Task Event로 발생시킨다.
	 *
	 */
	boolean invokeTaskInfoEvents(int state, int taskId, String packageName);
	
	/**
	 * PC로부터 전달받은 Task Event를 Android에 적용시킨다.
	 *
	 */
	boolean interpolateTaskInfoEvent(int state, int taskId);
}
