package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XDistributionAbstract;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;

public class StartDistribution extends XDistributionAbstract{

	private static int distributionType = Activity.StartDis;

	public static Object getActionResult(String workDefID, String taskName) {
		return getActionResult(workDefID, taskName, distributionType);
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {
		return getActionResult(workDefIDs, taskName, distributionType);		
	}
	
}
