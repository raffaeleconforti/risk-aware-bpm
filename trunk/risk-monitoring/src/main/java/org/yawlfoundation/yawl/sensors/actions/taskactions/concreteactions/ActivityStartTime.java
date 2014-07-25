package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.ActivityXTimeAbstract;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;

public class ActivityStartTime extends ActivityXTimeAbstract{
	
	private static int activityType = Activity.Executing;
	
	public static Object getActionResult(String workDefID, String taskName) {
		return getActionResult(workDefID, taskName, activityType);
	}
	
	public static Object getActionResultList(String workDefID, String taskName) {
		return getActionResultList(workDefID, taskName, activityType);
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {
		return getActionResult(workDefIDs, taskName, activityType);
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String taskName) {
		return getActionResultList(workDefIDs, taskName, activityType);
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, activityType);		
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, activityType);		
	}

}
