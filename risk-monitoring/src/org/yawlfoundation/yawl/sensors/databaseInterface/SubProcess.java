package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.Vector;

public abstract class SubProcess implements Serializable {
	
	public static final int Start = 1;
	public static final int Complete = 4;
	private static SubProcess me = null;
	
	/**
	 * Create an SubProcess class
	 * @return
	 */
	public static SubProcess getSubProcess(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (SubProcess) Class.forName(ClassPathAndName).newInstance();
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
	 * Return true if the name passed belong to an SubProcess
	 * @param Name
	 * @return
	 */
	abstract public boolean isSubProcess(String WorkDefID, String Name);
	
	/**
	 * Return the list of ID, of the most recent task, where the value of WorkDefID, Name, Time, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param Name
	 * @param isName
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getIDs(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);
	
	/**
	 * Return the list of WorkDefID, of the most recent task, where the value of ID, Name, Time, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getWorkDefIDs(String ID, boolean isID, String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of Name, of the most recent task, where the value of ID, Time, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getNames(String ID, boolean isID, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of Time, of the most recent task, where the value of ID, Name, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<String> getTimes(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);
	
	/**
	 * Return the list of VariablesID, of the most recent task, where the values of ID, Name, Time, TypeTime are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime
	 * @param isTypeTime
	 * @return
	 */
	abstract public Vector<String> getVariablesIDs(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, boolean isList);
	
	/**
	 * Return the entire row (or list of rows), of the most recent task, where the value of ID, Name, Time, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * The order of the columns is WorkDefID, ID, TaskName, Time, TypeTime, VariablesID
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param Time
	 * @param isTime 0 if <; 1 if <=; 2 if =; 3 if >=; 4 if >; 5 if !=  
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);
	
	

}
