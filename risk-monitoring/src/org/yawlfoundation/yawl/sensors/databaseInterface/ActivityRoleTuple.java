package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;

public class ActivityRoleTuple implements Serializable {
	
	String workDefID = null;
	String taskID = null;
	String taskName = null;
	String roleID = null;
	String time = null;
	String status = null;

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setWorkDefID(String workDefID) {
		this.workDefID = workDefID;
	}

	public String getTaskID() {
		return taskID;
	}
	
	public String getRoleID() {
		return roleID;
	}
		
	public String getTime() {
		return time;
	}

	public String getStatus() {
		return status;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public String getWorkDefID() {
		return workDefID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("WorkDefID: ");
		sb.append(workDefID);
		sb.append(" TaskID: ");
		sb.append(taskID);
		sb.append(" TaskName: ");
		sb.append(taskName);
		sb.append(", RoleID: ");
		sb.append(roleID);
		sb.append(", Status: ");
		sb.append(status);
		sb.append(", TimeStamp: ");
		sb.append(time);
		
		return sb.toString();
	}

}
