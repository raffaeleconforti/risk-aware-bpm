package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

public class TaskID implements Comparable<TaskID>{
	
	private String caseID = null;
	private String eventName = null;
//	private Integer taskInstance = null;
	
	public TaskID(String taskID) {
		this.caseID = taskID.substring(0, taskID.indexOf(':'));
		this.eventName = taskID.substring(taskID.indexOf(':')+1);
	}
	
	public TaskID(String caseID, String eventName) {
		this.caseID = caseID;
		this.eventName = eventName;
//		this.taskInstance = taskInstance;
	}

	public String getCaseID() {
		return caseID;
	}

	public String getEventName() {
		return eventName;
	}
	
//	public Integer getTaskInstance() {
//		return taskInstance;
//	}
//
//	public void setTaskInstance(Integer taskInstance) {
//		this.taskInstance = taskInstance;
//	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(caseID);
		sb.append(":");
		sb.append(eventName);
		
		return sb.toString(); 
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TaskID) {
			return (this.compareTo((TaskID) o) == 0);
		}
		return false;
	}
	
	@Override
	public int compareTo(TaskID o) {
		
		int caseIDCompare = caseID.compareTo(o.caseID);
		if(caseIDCompare != 0) return caseIDCompare;
		
		return eventName.compareTo(o.eventName);
//		if(nameCompare != 0) return nameCompare;
		
//		return taskInstance.compareTo(o.taskInstance);
		
	}

}
