package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;


public class MutationRemoveStartRes extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRemoveStartRes();
		return me;
	}
	
	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			if(t.status == Task.Started && !t.startMod && !t.relres) return true;
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
			
			if(t.status == Task.Started && !t.startMod && !t.relres) {
				
				String res = t.startRes; 
				t.startRes = null;
				t.startMod = true;

				String task = t.taskName;
				neighbour.getResourcesMap().get(res).removeStart(task);
				
				t.status = Task.Allocated;
				if(t.allocateRes == null) {
					t.allocateRes = res;
					neighbour.getResourcesMap().get(res).addAllocate(task);
				}
				
				updateVariables(neighbour, currentStatus, j, 2, t.startRes);
				
				neighbour.changing+=3;
				
				neighbour.modifications.add(removeStartResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				complete = true;
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		Task t = neighbour.getTasks()[task];
    	LinkedList<String> modified = new LinkedList<String>();

    	for(Entry<String, String> entry: neighbour.getCurrent().entrySet()) {
    		String value = entry.getValue();
    		if(value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.PARTA) && value.contains("tart")) { //Check Allocate and Check Start
	    			
	    		YStateProcessUtilities.updateVariableAllocateAndStart(neighbour, modified, resource, value);
	    			
	    	}
    	}

    	if(!modified.isEmpty()) {
    		MutationUtilities.updateQueryVariable2(modified, neighbour, currentState);
    	}
	}

}
