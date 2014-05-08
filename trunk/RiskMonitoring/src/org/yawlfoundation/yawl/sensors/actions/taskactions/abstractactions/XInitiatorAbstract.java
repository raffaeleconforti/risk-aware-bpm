package org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions;

import java.util.LinkedList;
import java.util.List;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;

public abstract class XInitiatorAbstract {
	
	private static Activity activityLayer = null;
	
	public static void setLayers(Activity activityLayer) {
		XInitiatorAbstract.activityLayer = activityLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName, int distributionType) {
		return activityLayer.getInitiator(taskName, true, WorkDefID, true, distributionType);
	}

	public static Object getActionResult(List<String> workDefIDs, String taskName, int distributionType) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, taskName, distributionType));
		}
		return result;	
	}
	
}
