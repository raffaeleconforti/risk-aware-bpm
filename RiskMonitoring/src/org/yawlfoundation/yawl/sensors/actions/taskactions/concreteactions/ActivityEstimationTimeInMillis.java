package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.Collections;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.timePrediction.TimePredictionInterface;
import org.yawlfoundation.yawl.sensors.timePrediction.YAWL.YAWL_TimePredictionInterface;

public class ActivityEstimationTimeInMillis {

	private static Activity activityLayer = null;
	private static TimePredictionInterface tpi = null;
	
	public static void setLayers(Activity activityLayer, String timePredictionURI) {
		ActivityEstimationTimeInMillis.activityLayer = activityLayer;
		tpi = YAWL_TimePredictionInterface.getInstance(timePredictionURI);
	}
	
	public static Object getActionResult(String workDefID, String taskName) {
		LinkedList<String> workDefIDs = new LinkedList<String>();
		workDefIDs.add(workDefID);
		LinkedList<ActivityTuple> result = activityLayer.getRows(null, false, null, false, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
		
		LinkedList<String> executedTask = new LinkedList<String>();

		if(result != null) {
			Collections.sort(result);
			
			for(ActivityTuple tuple : result) {
				executedTask.add(tuple.getName());
			}
		}
		
		return tpi.getPrediction(workDefID, executedTask, taskName, true);
		
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {

		LinkedList<ActivityTuple> result = activityLayer.getRows(null, false, null, false, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
		
		LinkedList<LinkedList<String>> executedTask = new LinkedList<LinkedList<String>>();
		LinkedList<String> predictions = new LinkedList<String>();
		
		Collections.sort(result);
		
		String currID = null;
		LinkedList<String> tmpRes = new LinkedList<String>();
		
		for(ActivityTuple tuple : result) {
			if(tuple.getWorkDefID().equals(currID)) {
				tmpRes.add(tuple.getName());
			}else {
				predictions.add(tpi.getPrediction(currID, tmpRes, taskName, true));
				currID = tuple.getWorkDefID();
				tmpRes = new LinkedList<String>();
				executedTask.add(tmpRes);
				tmpRes.add(tuple.getName());
			}
		}
		if(currID != null) {
			predictions.add(tpi.getPrediction(currID, tmpRes, taskName, true));
		}
		
		return predictions;
		
	}
	
}
