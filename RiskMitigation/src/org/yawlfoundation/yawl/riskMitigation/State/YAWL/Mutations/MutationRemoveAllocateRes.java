package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;


public class MutationRemoveAllocateRes extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRemoveAllocateRes();
		return me;
	}
	
	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			if(t.status == Task.Allocated && !t.allocateMod && !t.relres) return true;
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		
		Task t = null;
		
		while(!complete) {		

			int j =  YStateProcessUtilities.selectTask(neighbour);
			t = neighbour.getTasks()[j];
			
			if(t.status == Task.Allocated && !t.allocateMod && !t.relres) {
				
				String res = t.allocateRes;
				t.allocateRes = null;

				String task = t.taskName;
				neighbour.getResourcesMap().get(res).removeAllocate(task);
				
				t.status = Task.Offered;
				t.allocateMod = true;
				if(t.offerRes.size()==0) {
					if(t.originalOfferResDef.size()==0) {
						t.offerRes.add(res);
						neighbour.getResourcesMap().get(res).addOffer(task);	
					}else {
						t.offerRes.addAll(t.originalOfferResDef);
						for(String re : t.offerRes) {
							neighbour.getResourcesMap().get(re).addOffer(task);	
						}
					}
				}
				
				updateVariables(neighbour, currentStatus, j, 1, t.allocateRes);
				
				neighbour.changing+=2;
				
				neighbour.modifications.add(removeAllocateResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				
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
    		if(value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.PARTA) && value.contains("llocate")) { //Check Allocate and Check Start
	    			
	    			YStateProcessUtilities.updateVariableAllocateAndStart(neighbour, modified, resource, value);
	    		
	    	}
    	}
    	
    	if(!modified.isEmpty()) {
    		MutationUtilities.updateQueryVariable2(modified, neighbour, currentState);
    	}
	}

}
