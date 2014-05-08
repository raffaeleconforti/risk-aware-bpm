package org.yawlfoundation.yawl.riskPrediction.DecisionPoints;

import java.util.LinkedList;

public interface DecisionPointDiscover {
	
	public void discoverDecisionPoints(String model);
	
	public LinkedList<DecisionPoint> getDecisionPoints();

}
