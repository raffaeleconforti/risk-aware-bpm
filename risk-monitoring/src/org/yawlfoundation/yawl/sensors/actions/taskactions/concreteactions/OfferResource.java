package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XResourceAbstract;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;

public class OfferResource extends XResourceAbstract{
	
	private static int activityType = Activity.Enabled;
	private static int resourceType = Role.offerResourceType;
	
	public static Object getActionResult(String workDefID, String taskName) {
		return getActionResult(workDefID, taskName, resourceType);
	}
	
	public static Object getActionResultList(String workDefID, String taskName) {
		return getActionResultList(workDefID, taskName, resourceType);
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {
		return getActionResult(workDefIDs, taskName, resourceType);
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String taskName) {
		return getActionResultList(workDefIDs, taskName, resourceType);
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, activityType, resourceType);
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, activityType, resourceType);
	}
	
}
