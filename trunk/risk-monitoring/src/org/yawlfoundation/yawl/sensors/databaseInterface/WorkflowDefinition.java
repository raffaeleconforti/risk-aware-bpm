package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.Vector;

public abstract class WorkflowDefinition implements Serializable {
	
	public static final int CaseStart = 1;
	public static final int CaseCancel = 2;
	public static final int CaseComplete = 3;
	private static WorkflowDefinition me = null;
	
	/**
	 * Create an Activity class
	 * @return
	 */
	public static WorkflowDefinition getWorkflowDefinition(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (WorkflowDefinition) Class.forName(ClassPathAndName).newInstance();
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
	 * Return the list of all ID order from the oldest to the earliest WorkflowDefinition
	 * @return
	 */
	abstract public Vector<String> getAllID();
	
	/**
	 * Return the list of ID, order from the oldest to the earliest WorkflowDefinition, where the value of Name, Time, TypeTime, URI and Version are/not are the passed values (if null or 0 the variable is ignored)
	 * @param Name
	 * @param isName
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime CaseStart, CaseCancel, CaseComplete
	 * @param isTypeTime
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public Vector<String> getIDs(String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version);
	
	/**
	 * Return the list of Name, order from the oldest to the earliest WorkflowDefinition, where the value of ID, Time, TypeTime, URI and Version are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime CaseStart, CaseCancel, CaseComplete
	 * @param isTypeTime
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public Vector<String> getNames(String ID, boolean isID, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version);
	
//	/**
//	 * Return the list of StartTime, order from the oldest to the earliest WorkflowDefinition, where the value of ID, Name, TypeTime, URI and Version are/not are the passed values (if null or 0 the variable is ignored)
//	 * @param ID
//	 * @param isID
//	 * @param Name
//	 * @param isName
//	 * @param TypeTime CaseStart, CaseCancel, CaseComplete
//	 * @param isTypeTime
//	 * @param URI
//	 * @param Version
//	 * @return
//	 */
//	abstract public Vector<String> getTimes(String ID, boolean isID, String Name, boolean isName, int TypeTime, boolean isTypeTime, String URI, String Version);
	
	/**
	 * Return the URI of a WorkflowDefinition
	 * @param ID
	 * @return
	 */
	abstract public String getURI(String ID);
	
	/**
	 * Return the Version of a WorkflowDefinition
	 * @param ID
	 * @return
	 */
	abstract public String getVersion(String ID);
	
	/**
	 * Return the entire row (or list of rows), order from the oldest to the earliest WorkflowDefinition, where the value of ID, Name, Time, TypeTime, URI and Version are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime CaseStart, CaseCancel, CaseComplete
	 * @param isTypeTime
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version);

	/**
	 * Return the Sensor specification, require the WorkflowDefinition ID or the WorkflowDefinition URI and Version
	 * @param ID
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public String getSensors(String ID, String URI, String Version);
	
	/**
	 * Return the specification of the WorkflowDefinition
	 * @param ID
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public String getSpecification(String ID, String URI, String Version);
	
	/**
	 * Return the log of all process instances in OpenXES format
	 * @param ID
	 * @param URI
	 * @param Version
	 * @return
	 */
	abstract public String getOpenXESLog(String ID, String URI, String Version);

	abstract public String getSpecification(String URI);
}
