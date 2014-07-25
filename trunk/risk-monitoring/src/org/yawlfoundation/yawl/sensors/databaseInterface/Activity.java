package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;

public abstract class Activity implements Serializable {
	
	public static final int EnabledExecutingComplete = 0;
	
	public static final int Enabled = 1;
	public static final int Allocated = 1;
	public static final int Executing = 3;
	public static final int Completed = 4;
	
	public static final int OfferDis = 1;
	public static final int AllocateDis = 2;
	public static final int StartDis = 3;
	
	private static Activity me = null;
	
	/**
	 * Create an Activity class
	 * @return
	 */
	public static Activity getActivity(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (Activity) Class.forName(ClassPathAndName).newInstance();
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
	abstract public LinkedList<ActivityTuple> getAllID();
	
	/**
	 * Return true if the name passed belong to an Activity
	 * @param Name
	 * @return
	 */
	abstract public boolean isActivity(String WorkDefID, String Name);
	
	/**
	 * Return the list of ID, of the most recent task, where the value of WorkDefID, Name, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param Name
	 * @param isName
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public LinkedList<ActivityTuple> getIDs(String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);
	
	/**
	 * Return the list of WorkDefID, of the most recent task, where the value of ID, Name TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param TypeTime
	 * @param isTypeTime
	 * @param VariablesID
	 * @param isVariablesID
	 * @return
	 */
	abstract public LinkedList<ActivityTuple> getWorkDefIDs(String ID, boolean isID, String Name, boolean isName, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID);
	
	/**
	 * Return the list of Name, of the most recent task, where the value of ID, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
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
	abstract public LinkedList<ActivityTuple> getNames(String ID, boolean isID, String WorkDefID, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID);
	
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
	abstract public LinkedList<ActivityTuple> getTimes(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);
	
	/**
	 * Return the list of VariablesID, of the most recent task, where the values of ID, Name, TypeTime are/not are the passed values (if null or 0 the variable is ignored)
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @param TypeTime
	 * @param isTypeTime
	 * @return
	 */
	abstract public LinkedList<ActivityTuple> getVariablesIDs(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, boolean isList);
	
	/**
	 * Return the number of execution of the task, where the values of Name and WorkDefID are are the passed values (if null the variable is ignored)
	 * The information returned are WorkDefID, Taskname, Count
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @return
	 */
	abstract public LinkedList<ActivityTuple> getCounts(String Name, boolean isName, LinkedList<String> WorkDefID, boolean isWorkDefID);
	
	/**
	 * Return the entire row (or list of rows), of the most recent task, where the value of ID, Name, TypeTime and VariablesID are/not are the passed values (if null or 0 the variable is ignored)
	 * The order of the columns is WorkDefID, ID, TaskName, Time, TypeTime, VariablesID
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
	abstract public LinkedList<ActivityTuple> getRows(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList);

	/**
	 * Return the entire row (or list of rows), of all work items of the process instance with ID equals to WorkDefID
	 * @param WorkDefID
	 * @return
	 */
	abstract public LinkedList<ActivityTuple> getAllRows(String WorkDefID);
	
	/**
	 * Return the distribution of the Activity
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @return
	 */
	abstract public String getDistribution(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type);
	
	/**
	 * Return the initiator of the Activity
	 * @param ID
	 * @param isID
	 * @param Name
	 * @param isName
	 * @param WorkDefID
	 * @param isWorkDefID
	 * @return
	 */
	abstract public String getInitiator(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type);

	/**
	 * Return all the Activities with a token belonging to that process instance
	 * @param ID
	 * @return
	 */
//	abstract public HashSet<String> getActivitiesEnabled(String caseID);


}
