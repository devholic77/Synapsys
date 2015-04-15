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
	
	/**
	 * Android Device의 Keyboard Event를 PC의 Keyboard Event로 발생시킨다.
	 *
	 */
	boolean invokeKeyboardEvent(int event_id, int key_code);
	
<<<<<<< HEAD
	boolean invokeNotificationEvent(String PackageName , int id);
=======
	/**
	 * PC로부터 전달받은 Keyboard Event를 Android에 적용시킨다.
	 *
	 */
	boolean interpolateKeyboardEvent(int event_id, int key_code);
>>>>>>> refs/remotes/origin/yeonho
	
	/**
	 * Android Device의 Notification Event를 PC의 Notification Event로 발생시킨다.
	 *
	 */
	boolean invokeNotificationEvent(int notificationId, String packageName, String message);
	
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
