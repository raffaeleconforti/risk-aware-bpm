package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.Arrays;
import java.util.LinkedList;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;


public class MutationRestoreOfferRes extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRestoreOfferRes();
		return me;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		LinkedList<String> diff = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			diff = (LinkedList<String>) t.originalOfferResDef.clone();
			diff.removeAll(t.offerRes);
			if(!diff.isEmpty() && ((t.status == Task.Unoffered && Arrays.binarySearch(status.getNamePending(), t.engineID)>=0) || t.status == Task.Offered) && t.originalStatus > Task.Unoffered) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		Task t = null;
		LinkedList<String> diff = null;
		while(!complete) {
			
			int j = YStateProcessUtilities.selectTask(neighbour);
			t = neighbour.getTasks()[j];
			
			diff = (LinkedList<String>) t.originalOfferResDef.clone();
			diff.removeAll(t.offerRes);

			if(!diff.isEmpty() && ((t.status == Task.Unoffered && Arrays.binarySearch(neighbour.getNamePending(), t.engineID) >= 0) || t.status == Task.Offered) && t.originalStatus>Task.Unoffered) {
				int k = YStateProcessUtilities.selectRandom(diff.size(), neighbour.r);

				String res = diff.get(k);
				
				t.offerRes.add(res);
				
				String task = t.taskName;
				neighbour.getResourcesMap().get(res).addOffer(task);
				
				if(t.offerRes.size() == 0) t.status = Task.Offered;
				
				updateVariables(neighbour, currentStatus, j, 0, t.offerRes);
				
				neighbour.changing++;
				
				neighbour.modifications.add(restoreOfferResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				
				complete = true;
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		
		MutationRemoveOfferRes.getInstance().updateVariables(neighbour, currentState, task, type, resource);
		
	}

}
