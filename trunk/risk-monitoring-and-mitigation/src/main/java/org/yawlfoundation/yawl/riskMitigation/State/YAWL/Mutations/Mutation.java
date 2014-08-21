package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;

public interface Mutation {
	
	public boolean checkIfPossible(StateYAWLProcess status);
	
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus);
	
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource);

}
