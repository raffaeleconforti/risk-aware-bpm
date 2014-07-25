package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

import java.util.Collections;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.timePrediction.TimePredictionInterface;
import org.yawlfoundation.yawl.sensors.timePrediction.YAWL.YAWL_TimePredictionInterface;

public class NetEstimationTimeInMillis {

	private static Activity activityLayer = null;
	private static TimePredictionInterface tpi = null;
	
	public static void setLayers(Activity activityLayer, String timePredictionURI) {
		NetEstimationTimeInMillis.activityLayer = activityLayer;
		tpi = YAWL_TimePredictionInterface.getInstance(timePredictionURI);
	}
	
	public static Object getActionResult(String workDefID, String netName) {
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
		
		return tpi.getPrediction(workDefID, executedTask, null, false);
		
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName) {

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
				predictions.add(tpi.getPrediction(currID, tmpRes, null, false));
				currID = tuple.getWorkDefID();
				tmpRes = new LinkedList<String>();
				executedTask.add(tmpRes);
				tmpRes.add(tuple.getName());
			}
		}
		if(currID != null) {
			predictions.add(tpi.getPrediction(currID, tmpRes, null, false));
		}

		return predictions;
		
	}
	
}
