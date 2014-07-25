package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;

public abstract class ActivityRole implements Serializable {
	
	public static final int Offer = 1;
	public static final int Allocate = 2;
	public static final int Start = 3;
	public static final int Complete = 4;
	private static ActivityRole me = null;
	
	/**
	 * Create an ActivityRole class
	 * @return
	 */
	public static ActivityRole getActivityRole(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (ActivityRole) Class.forName(ClassPathAndName).newInstance();
			}
			return me;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Return the list of all TaskID
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getAllTaskID();
	
	/**
	 * Return the list of all RoleID
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getAllRoleID();
	
	/**
	 * Return the list of TaskID where the value of RoleID, TimeStamp and Status are/not are the passed values (if null or 0 the variable is ignored)
	 * @param RoleID
	 * @param isRoleID
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if != 
	 * @param Status
	 * @param isStatus
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getTaskIDs(String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus); 
	
	/**
	 * Return the list of RoleID where the value of TaskID, TimeStamp and Status are/not are the passed values (if null or 0 the variable is ignored)
	 * @param RoleID
	 * @param isRoleID
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if != 
	 * @param Status
	 * @param isStatus
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getRoleIDs(String TaskID, boolean isTaskID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus);
	
	/**
	 * Return the list of TimeStamp where the value of TaskID, RoleID and Status are/not are the passed values (if null or 0 the variable is ignored)
	 * @param TaskID
	 * @param isTaskID
	 * @param RoleID
	 * @param isRoleID
	 * @param Status
	 * @param isStatus
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getTimeStamps(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, int Status, boolean isStatus);
	
	/**
	 * Return the list of Status where the value of TaskID, RoleID and TimeStamp are/not are the passed values (if null or 0 the variable is ignored)
	 * @param TaskID
	 * @param isTaskID
	 * @param RoleID
	 * @param isRoleID
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if != 
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getStatus(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp);
	
	/**
	 * Return the entire of row (or list of rows) where the value of TaskID, RoleID, TimeStamp and Status are/not are the passed values (if null or 0 the variable is ignored)
	 * @param TaskID
	 * @param isTaskID
	 * @param RoleID
	 * @param isRoleID
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if != 
	 * @param Status
	 * @param isStatus
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getRows(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus);
	
	/**
	 * Return the entire row (or list of rows), of all work items of the process instance with ID equals to WorkDefID
	 * @param WorkDefID
	 * @return
	 */
	abstract public LinkedList<ActivityRoleTuple> getAllRows(String WorkDefID);
}
