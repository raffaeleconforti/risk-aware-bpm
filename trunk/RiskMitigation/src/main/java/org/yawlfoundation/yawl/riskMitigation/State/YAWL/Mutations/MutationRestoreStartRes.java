package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;

public class MutationRestoreStartRes extends AbstractMutation{

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRestoreStartRes();
		return me;
	}

	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			if(t.startRes == null && t.status == Task.Allocated && t.originalStatus > Task.Allocated && !t.allocateRes.equals(t.originalStartRes)) return true;
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		Task t = null;
		
		while(!complete) {

			int j = YStateProcessUtilities.selectTask(neighbour);
			t = neighbour.getTasks()[j];			
			
			if(t.status == Task.Allocated && t.originalStatus>Task.Allocated && t.startRes == null && !t.allocateRes.equals(t.originalStartRes)) {
				
				String res = t.allocateRes;
				t.startRes = res;
									
				String task = t.taskName;

				neighbour.getResourcesMap().get(res).addStart(task);
				
				t.status = Task.Started;
				
				updateVariables(neighbour, currentStatus, j, 2, t.startRes);
				
				neighbour.changing-=2;
				
				neighbour.modifications.add(restoreStartResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				
				complete = true;
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		
		MutationRemoveStartRes.getInstance().updateVariables(neighbour, currentState, task, type, resource);
		
	}

}
