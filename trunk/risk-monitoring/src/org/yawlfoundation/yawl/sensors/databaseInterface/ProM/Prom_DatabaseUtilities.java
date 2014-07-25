package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;

public class Prom_DatabaseUtilities {
	
	public static final String scheduled = "schedule";
	public static final String start = "start";
	public static final String complete = "complete";
	
	public static final int minor = 0;
	public static final int minorEqual = 1;
	public static final int equal = 2;
	public static final int greaterEqual = 3;
	public static final int greater = 4;
	public static final int notEqual = 5;

	public static boolean checkIfTime(long questionTime, long Time, int isTime) {
		if(Time!=0) {
			if(isTime==minor && questionTime<Time) return true;
			else if(isTime==minorEqual && questionTime<=Time) return true;
			else if(isTime==equal && questionTime==Time) return true;
			else if(isTime==greaterEqual && questionTime>=Time) return true;
			else if(isTime==greater && questionTime>Time) return true;
			else if(isTime==notEqual && questionTime!=Time) return true;
		}else return true;
		
		return false;
	}
	
	public static boolean[] checkTypeTime(int TypeTime, boolean isTypeTime) {
		
		boolean enabled = true;
		boolean executing = true;
		boolean completed = true;
		
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==Activity.Enabled) {
					executing = false;
					completed = false;
				} else if(TypeTime==Activity.Executing) {
					enabled = false;
					completed = false;
				} else if(TypeTime==Activity.Completed) {
					enabled = false;
					executing = false;
				}
			}else {
				if(TypeTime==Activity.Enabled) {
					enabled = false;
				} else if(TypeTime==Activity.Executing) {
					executing = false;
				} else if(TypeTime==Activity.Completed) {
					completed = false;
				}
			}
		}
		
		return new boolean[] {enabled, executing, completed};
		
	}
	
	public static boolean checkXAND(boolean a, boolean b) {
		return ((a && b) || (!a && !b));
	}
	
	public static Integer getEventType(String stringEventType) {
		
		Integer eventType = null;
		
		if(scheduled.equals(stringEventType)) eventType = VariableID.scheduled;
		else if(start.equals(stringEventType)) eventType = VariableID.executing;
		else if(complete.equals(stringEventType)) eventType = VariableID.completed;
//		else System.out.println(stringEventType);
		return eventType;
		
	}
	
	public static String getEventType(int intEventType) {
		String event = null;
		switch (intEventType){
			case VariableID.scheduled: 	event = "offer";//"Enabled";
										break;
			case VariableID.executing: 	event = "start";//"Execution";
										break;
			case VariableID.completed: 	event = "complete";//"Completed";
										break; 
		}
		return event;
	}
	
}
