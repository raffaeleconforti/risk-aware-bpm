package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;

public class ActivityCount {

	private static Activity activityLayer = null;
	
	public static void setLayers(Activity activityLayer) {
		ActivityCount.activityLayer = activityLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> count = activityLayer.getCounts(taskName, true, WorkDefIDs, true); 
		if(count!=null) {
			return count.getFirst().getCount();
		}
		return "0";
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {
		LinkedList<String> result = new LinkedList<String>();
		LinkedList<ActivityTuple> count = activityLayer.getCounts(taskName, true, workDefIDs, true);
		for(ActivityTuple el : count) {
			result.add(el.getCount());
		}
		return result;
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		if(preVar.equals(YSensorUtilities.count)) {
			LinkedList<ActivityTuple> count = activityLayer.getCounts(activity, true, null, false);
			try {
				if(postVar != null && !postVar.equals("null")) {
					double b = new Double(postVar);
					for(ActivityTuple activityID : count) {
						if(workDefIDs.contains(activityID.getWorkDefID())) {
							try {
								double a = new Double(activityID.getCount());
								
								YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.getWorkDefID(), suffix);
								
							}catch (NumberFormatException nfe) {
								if(!postVar.equals("null")) nfe.printStackTrace();
							}
						}
					}
				}
			}catch (NumberFormatException nfe) {
				if(!postVar.equals("null")) nfe.printStackTrace();
			}
		}
		
		return casesPartial;
	}
	
}
