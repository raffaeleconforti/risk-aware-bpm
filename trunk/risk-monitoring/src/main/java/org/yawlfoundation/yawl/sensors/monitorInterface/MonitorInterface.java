package org.yawlfoundation.yawl.sensors.monitorInterface;

public interface MonitorInterface {

	public void sendNotification(int notificationID, String workDefID, String sensorName, String status, String message, String condition, String threshold, double probability, double cons, long timeStamp);
	
	public void cancelNotification(int notificationID, String workDefID, String sensorName);
	
}
