package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XDistributionAbstract;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;

public class AllocateDistribution extends XDistributionAbstract{

	private static int distributionType = Activity.AllocateDis;

	public static Object getActionResult(String workDefID, String taskName) {
		return getActionResult(workDefID, taskName, distributionType);
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName) {
		return getActionResult(workDefIDs, taskName, distributionType);		
	}
	
}
