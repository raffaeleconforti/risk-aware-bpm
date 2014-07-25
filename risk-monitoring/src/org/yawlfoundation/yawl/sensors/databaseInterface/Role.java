package org.yawlfoundation.yawl.sensors.databaseInterface;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;

public abstract class Role implements Serializable {
	
	public static final int offerResourceType = 1;
	public static final int allocateResourceType = 2;
	public static final int startResourceType = 3;
	public static final int completeResourceType = 4;
	
	private static Role me = null;
	
	/**
	 * Create an Role class
	 * @return
	 */
	public static Role getRole(String ClassPathAndName) {
		try {
			if(me==null) {
				me = (Role) Class.forName(ClassPathAndName).newInstance();
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
	 * Return the list of RoleID where the Name and Surname are/not are the passed values (if null the variable is ignored)
	 * @param Name
	 * @param isName
	 * @param Surname
	 * @param isSurname
	 * @return
	 */
	abstract public Vector<String> getIDs(String Name, boolean isName, String Surname, boolean isSurname);
	
	/**
	 * Return the list of Name and Surname where the RoleID is the passed value
	 * @param RoleID
	 * @return
	 */
	abstract public Vector<String> getNames(String RoleID);
	
	/**
	 * Return the Name and Surname where the RoleID is/isn't the passed value
	 * @param RoleID
	 * @return
	 */
	abstract public String getRoleInfo(String RoleID);

	/**
	 * Return a list of resources with that specific role
	 * @param role
	 * @return
	 */
	abstract public LinkedList<String> getParticipantWithRole(String role);
	
    /**
     * Gets the role with the specified id
     * @param role
     * @return
     */
	abstract public String getRoleName(String role);

	/**
     * Gets the tasks allocated to the participant with the specified id
     * @param participantID
     * @return 
     */
	abstract public LinkedList<String[]> getTasksAllocated(String participantID);
	
	/**
     * Gets the tasks started by the participant with the specified id
     * @param participantID
     * @return 
     */
	abstract public LinkedList<String[]> getTasksStarted(String participantID);

	
	abstract public String getRoleRInfo(String RoleID);

	public LinkedList<String[]> getTasksOffered(String participantID) {
		// TODO Auto-generated method stub
		return null;
	}
}
