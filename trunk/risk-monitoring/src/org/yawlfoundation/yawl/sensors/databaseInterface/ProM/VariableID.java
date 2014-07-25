package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import java.util.StringTokenizer;

public class VariableID implements Comparable<VariableID>{
	
	public static final int scheduled = 0;
	public static final int executing = 1;
	public static final int completed = 2;
	
	private String caseID = null;
	private String eventName = null;
	private Integer eventType = null;
	private Long time = null;
	
	public VariableID(String caseID, String eventName, Integer eventType, Long time) {
		this.caseID = caseID;
		this.eventName = eventName;
		this.eventType = eventType;
		this.time = time;
	}

	public String getCaseID() {
		return caseID;
	}

	public String getEventName() {
		return eventName;
	}

	public Integer getEventType() {
		return eventType;
	}
	
	public Long getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(caseID);
		sb.append(":");
		sb.append(eventName);
		sb.append(":");
		sb.append(eventType);
		sb.append(":");
		sb.append(time);
		
		return sb.toString(); 
	}
	
	public static VariableID revertToString(String var) {
		StringTokenizer st = new StringTokenizer(var, ":");
		String caseID = st.nextToken();
		String eventName = st.nextToken();
		String eventType = st.nextToken();
		String time = st.nextToken();
		return new VariableID(caseID, eventName, Integer.getInteger(eventType), Long.getLong(time));
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof VariableID) {
			return (this.compareTo((VariableID) o) == 0);
		}
		return false;
	}
	
	@Override
	public int compareTo(VariableID o) {
		
		int caseIDCompare = caseID.compareTo(o.caseID);
		if(caseIDCompare != 0) return caseIDCompare;
		
		int timeCompare = time.compareTo(o.time);
		if(timeCompare != 0) return timeCompare;
		
		int eventCompare = eventName.compareTo(o.eventName);
		if(eventCompare != 0) return eventCompare;
		
		return eventType.compareTo(o.eventType);
		
	}

}
