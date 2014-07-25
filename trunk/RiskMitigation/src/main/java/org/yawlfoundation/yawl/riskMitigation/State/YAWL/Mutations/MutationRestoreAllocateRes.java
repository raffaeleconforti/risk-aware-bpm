package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;


public class MutationRestoreAllocateRes extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRestoreAllocateRes();
		return me;
	}

	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		LinkedList<String> diff = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			diff = new LinkedList<String>(t.offerRes);
			diff.remove(t.originalAllocateRes);
			if(t.allocateRes == null && t.status == Task.Offered && t.originalStatus > Task.Offered && !diff.isEmpty()) return true;
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		Task t = null;
		LinkedList<String> diff = null;
		
		while(!complete) {

			int j = YStateProcessUtilities.selectTask(neighbour);
			t = neighbour.getTasks()[j];

			diff = new LinkedList<String>(t.offerRes);
			diff.remove(t.originalAllocateRes);
			
			if(t.allocateRes == null && t.status == Task.Offered && t.originalStatus > Task.Offered && !diff.isEmpty()) {
				
				int k = YStateProcessUtilities.selectRandom(diff.size(), neighbour.r);
				
				String res = diff.get(k);
				
				t.allocateRes = res;
				
				String task = t.taskName;
				neighbour.getResourcesMap().get(res).addAllocate(task);
				
				t.status = Task.Allocated;
				
				updateVariables(neighbour, currentStatus, j, 1, t.allocateRes);
				
				neighbour.changing--;
				
				neighbour.modifications.add(restoreAllocateResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				
				complete = true;
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		
		MutationRemoveAllocateRes.getInstance().updateVariables(neighbour, currentState, task, type, resource);

	}

}
