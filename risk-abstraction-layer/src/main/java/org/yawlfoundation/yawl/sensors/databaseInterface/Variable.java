package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.Vector;

public abstract class Variable implements Serializable {
	
	public static final int InputVarAssignment = 1;
	public static final int OutputVarAssignment = 2;
	private static Variable me = null;
	
	/**
	 * Create an Variable class
	 * @return
	 */
	public static Variable getVariable(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (Variable) Class.forName(ClassPathAndName).newInstance();
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
	 * Return the list of all ID
	 * @return
	 */
	abstract public Vector<String> getAllID();
	
	/**
	 * Return the list of ID where the value of Name, TimeStamp, Value, TypeAssigment, TaskID and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param Name
	 * @param isName
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param Value
	 * @param isValue
	 * @param TypeAssigment
	 * @param TaskID
	 * @param isTaskID
	 * @param VariablesID
	 * @param isVariablesID 
	 * @return
	 */
	abstract public Vector<String> getIDs(String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of Name where the value of ID, TimeStamp, Value, TypeAssigment, TaskID and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param Value
	 * @param isValue
	 * @param TypeAssigment
	 * @param TaskID
	 * @param isTaskID
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getNames(String ID, boolean isID, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of TimeStamp where the value of ID, Name, Value, TypeAssigment, TaskID and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param Value
	 * @param isValue
	 * @param TypeAssigment
	 * @param TaskID
	 * @param isTaskID
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getTimeStamps(String ID, boolean isID, String Name, boolean isName, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of Value where the value of ID, Name, TimeStamp, TypeAssigment, TaskID, VariablesID and isActivity are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeAssigment
	 * @param TaskID
	 * @param isTaskID
	 * @param VariablesID
	 * @param isVariablesID
	 * @param isActivity
	 * @return
	 */
	abstract public Vector<String> getValues(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID, boolean isActivity);
	
	/**
	 * Return the list of TaskID where the value of ID, Name, TimeStamp, Value, TypeAssigment and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param Value
	 * @param isValue
	 * @param TypeAssigment
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getTaskIDs(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the entire row (or list of rows) where the value of ID, Name, TimeStamp, Value, TypeAssigment, TaskID, VariablesID and isActivity are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param TimeStamp
	 * @param isTimeStamp 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param Value
	 * @param isValue
	 * @param TypeAssigment
	 * @param VariablesID
	 * @param isVariablesID
	 * @param isActivity
	 * @return
	 */
	abstract public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID, boolean isActivity);
}
