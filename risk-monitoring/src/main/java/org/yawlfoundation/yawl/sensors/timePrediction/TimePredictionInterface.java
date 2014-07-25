package org.yawlfoundation.yawl.sensors.timePrediction;

import java.util.LinkedList;

public interface TimePredictionInterface {

	public String getPrediction(String caseID, LinkedList<String> activities, String taskName, boolean task); 

}
