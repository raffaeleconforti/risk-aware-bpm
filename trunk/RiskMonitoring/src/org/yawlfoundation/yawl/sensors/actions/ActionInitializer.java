package org.yawlfoundation.yawl.sensors.actions;

import org.yawlfoundation.yawl.sensors.actions.netactions.abstractactions.NetIsXAbstract;
import org.yawlfoundation.yawl.sensors.actions.netactions.abstractactions.NetXTimeAbstract;
import org.yawlfoundation.yawl.sensors.actions.netactions.abstractactions.NetXTimeInMillisAbstract;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetEstimationTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetPassTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetVariable;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.ActivityIsXAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.ActivityXTimeAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.ActivityXTimeInMillisAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XDistributionAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XInitiatorAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions.XResourceAbstract;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityCount;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityEstimationTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityPassTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityVariable;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;

public class ActionInitializer {

	public static void inizializeActions(WorkflowDefinition workflowLayer, Activity activityLayer, ActivityRole activityRoleLayer, Role roleLayer, Variable variableLayer, SubProcess subprocessLayer, String timePredictionURI) {
		
		ActivityIsXAbstract.setLayers(activityLayer, workflowLayer);
		ActivityXTimeAbstract.setLayers(activityLayer);
		ActivityXTimeInMillisAbstract.setLayers(activityLayer);
		
		XResourceAbstract.setLayers(activityLayer, activityRoleLayer, roleLayer);
		
		ActivityPassTimeInMillis.setLayers(activityLayer);
		
		XDistributionAbstract.setLayers(activityLayer);
		
		XInitiatorAbstract.setLayers(activityLayer);
		
		ActivityCount.setLayers(activityLayer);
		
		ActivityVariable.setLayers(activityLayer, variableLayer);
		
		ActivityEstimationTimeInMillis.setLayers(activityLayer, timePredictionURI);
		
		NetIsXAbstract.setLayers(subprocessLayer);
		NetXTimeAbstract.setLayers(subprocessLayer);
		NetXTimeInMillisAbstract.setLayers(subprocessLayer);
		
		NetPassTimeInMillis.setLayers(subprocessLayer);
		
		NetVariable.setLayers(subprocessLayer, variableLayer);

		NetEstimationTimeInMillis.setLayers(activityLayer, timePredictionURI);
	}
	
}
