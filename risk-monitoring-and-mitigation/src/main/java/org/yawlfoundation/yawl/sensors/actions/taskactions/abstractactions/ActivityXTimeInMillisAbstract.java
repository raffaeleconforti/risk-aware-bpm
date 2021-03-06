package org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;

public abstract class ActivityXTimeInMillisAbstract {

	private static Activity activityLayer = null;
	
	public static void setLayers(Activity activityLayer) {
		ActivityXTimeInMillisAbstract.activityLayer = activityLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName, int type) {
        LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> time =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, type, true, null, false, false);
		if(time == null || time.getLast().getTime() == null) return null;
		return ""+Long.parseLong(time.getLast().getTime());
	}
	
	public static Object getActionResultList(String WorkDefID, String taskName, int type) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> time =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, type, true, null, false, true);
		ArrayList<Object> result = null;
		if(time != null && time.getLast().getTime() != null) {
			result = new ArrayList<Object>(time.size());
			for(int i=0; i<time.size(); i++) {
				result.add(""+Long.parseLong(time.get(i).getTime()));
			}
			return result;
		}else {
			result = new ArrayList<Object>(0);
			return result;
		}
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName, int type) {
		LinkedList<String> result = new LinkedList<String>();
		LinkedList<ActivityTuple> time = activityLayer.getTimes(null, false, taskName, true, workDefIDs, true, type, true, null, false, false);
		for(ActivityTuple el : time) {
			if(el.getTime() != null) {
				result.add(""+new Long(el.getTime()));
			}else {
				result.add(null);
			}
		}
		return result;
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String taskName, int type) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String workDefID : workDefIDs) {
			result.add(getActionResultList(workDefID, taskName, type));
		}
		return result;
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int type) {
		try {
			long longTime = (long) Double.parseDouble(postVar);
			LinkedList<ActivityTuple> time = activityLayer.getRows(null, false, activity, true, workDefIDs, true, longTime, oper, type, true, null, false, false);
			for(ActivityTuple activityID : time) {
				casesPartial.add(activityID.getWorkDefID());
			}
		}catch (NumberFormatException nfe) {
			if(!postVar.contains("null")) nfe.printStackTrace();
		}
		
		return casesPartial;
		
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int type) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, type);
	}
}
