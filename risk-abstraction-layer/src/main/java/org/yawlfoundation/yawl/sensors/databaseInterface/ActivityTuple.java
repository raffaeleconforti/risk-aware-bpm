package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;

public class ActivityTuple implements Comparable<ActivityTuple>, Serializable {
	
	String count = null;
	String taskID = null;
	String name = null;
	String time = null;
	String status = null;
	String variableID = null;
	String workDefID = null;

	public void setCount(String count) {
		this.count = count;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public void setVariableID(String variableID) {
		this.variableID = variableID;
	}

	public void setWorkDefID(String workDefID) {
		this.workDefID = workDefID;
	}

	public String getCount() {
		return count;
	}
	
	public String getTaskID() {
		return taskID;
	}
	
	public String getName() {
		return name;
	}
		
	public String getTime() {
		return time;
	}

	public String getStatus() {
		return status;
	}
	
	public String getVariableID() {
		return variableID;
	}
		
	public String getWorkDefID() {
		return workDefID;
	}
	
	@Override
	public int compareTo(ActivityTuple o) {
		if(this.workDefID != null && o.workDefID != null) {
			if(this.workDefID.equals(o.workDefID))	{
				if(this.time != null && o.time != null) {
					return this.time.compareTo(o.time);
				}return 0;
			}else return this.workDefID.compareTo(o.workDefID);
		}else
			return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("WorkDefID: ");
		sb.append(workDefID);
		sb.append(", ID: ");
		sb.append(taskID);
		sb.append(", Name: ");
		sb.append(name);
		sb.append(", Status: ");
		sb.append(status);
		sb.append(", TimeStamp: ");
		sb.append(time);
		sb.append(", VariableID: ");
		sb.append(variableID);
		sb.append(", Count: ");
		sb.append(count);
		
		return sb.toString();
	}

}
