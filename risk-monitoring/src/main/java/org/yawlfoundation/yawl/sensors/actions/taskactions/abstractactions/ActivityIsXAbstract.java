package org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;

public abstract class ActivityIsXAbstract {

	private static Activity activityLayer = null;
	private static WorkflowDefinition workflowLayer = null;
	
	public static void setLayers(Activity activityLayer, WorkflowDefinition workflowLayer) {
		ActivityIsXAbstract.activityLayer = activityLayer;
		ActivityIsXAbstract.workflowLayer = workflowLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName, int type) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> time =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, type, true, null, false, false);
		if(time != null) {
			return YSensorUtilities.trueString;
		}else {
			return YSensorUtilities.falseString;
		}
	}
	
	public static Object getActionResultList(String WorkDefID, String taskName, int type) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> times =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, type, true, null, false, true);
		LinkedList<Object> result = null;
		if(times!=null) {
			result = new LinkedList<Object>();
			for(int i=1; i<times.size(); i++) {
				result.add(YSensorUtilities.trueString);
			}
			return result;
		}else {
			result = new LinkedList<Object>();
			result.add(YSensorUtilities.falseString);
			return result;
		}
	}

	public static Object getActionResult(List<String> workDefIDs, String taskName, int type) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, taskName, type));
		}
		return result;	
	}
	
	public static Object getActionResultList(List<String> workDefIDs, String taskName, int type) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String caseID : workDefIDs) {
			result.add(getActionResultList(caseID, taskName, type));
		}
		return result;	
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int type) {
		Boolean a = null;
		Boolean b = null;
		if(("true").equalsIgnoreCase(postVar)) {
			b = true;
		}else if(("false").equalsIgnoreCase(postVar)) {
			b = false;
		}
		if(oper!=2 && oper!=5) b = null; 
		if(suffix!=null) b = null;
		if(b!=null) {
			Vector<String> time1 = workflowLayer.getIDs(null, false, 0, 0, 0, false, workflowLayer.getURI(workDefIDs.getFirst()), workflowLayer.getVersion(workDefIDs.getFirst()));
			LinkedList<ActivityTuple> time2 = activityLayer.getRows(null, false, activity, true, null, false, 0, 0, type, true, null, false, false);
			if(time2 != null) {
				for(ActivityTuple activityID : time2) {
					if(activityID.getStatus() != null && activityID.getStatus().equals("true")) {
						a = true;
					}else {
						a = false;
					}
					
					YSensorUtilities.generateCaseCompareBoolean(a, b, oper, casesPartial, activityID.getWorkDefID());
					
				}
			}else {
				LinkedList<ActivityTuple> time = new LinkedList<ActivityTuple>();
				for(String t : time1) {
					YSensorUtilities.generateCaseCompareBoolean(false, b, oper, casesPartial, t);
				}
			}
		}
		
		return casesPartial;
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int type) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, type);
	}	
	
}
