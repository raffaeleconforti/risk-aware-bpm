package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph.NodeGraph;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;


public class MutationRemoveOfferRes extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRemoveOfferRes();
		return me;
	}
	
	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		Task t = null;
		for(int i : status.getNumberTaskRelevant()) {
			t = status.getTasks()[i];
			if(t.status == Task.Offered) return true;
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
			
			if(t.status == Task.Offered){
				
				int k = YStateProcessUtilities.selectRandom(t.offerRes.size(), neighbour.r);
				
				String res = t.offerRes.remove(k);
				
				String task = t.taskName;
				neighbour.getResourcesMap().get(res).removeOffer(task);
				
				if(t.offerRes.size() == 0) {
					t.offerMod = true;
					t.status = Task.Completed;
					updateVariables(neighbour, currentStatus, j, 7, null);

					for(NodeGraph ng1 : neighbour.getPending()) {
						if(ng1.nodeName.equals(t.engineID)) {
							gc.removePending(neighbour.getNg(), ng1);
							break;
						}
					}
				}else {
					updateVariables(neighbour, currentStatus, j, 0, t.offerRes);
				}
				
				neighbour.changing++;
				
				neighbour.modifications.add(removeOfferResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
				
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
    		if(value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.PARTA)) {
	    		if(type == 0 && value.contains("ffer")) { //Check Offer
	    			@SuppressWarnings("unchecked")
	    			LinkedList<String> res = (LinkedList<String>) resource;
	    			if(value.endsWith("ce)")) {
	    				
	    				YStateProcessUtilities.updateVariableResourceBuilder(neighbour, modified, resource, value);
	    				
	    			}else if(value.endsWith("d)")) {
	    				if(res != null && res.size()>0) {

	    					YStateProcessUtilities.modifyVariable(neighbour, modified, value, YSensorUtilities.trueString);
	    					
	    				}else {
	    					
	    					YStateProcessUtilities.modifyVariable(neighbour, modified, value, YSensorUtilities.falseString);
	    					
	    				}
	    			}else if(value.endsWith("me)") || value.endsWith("s)")) {
	    				if(res == null || res.size()==0) {
	    					
	    					YStateProcessUtilities.modifyVariable(neighbour, modified, value, null);
	    					
	    				}else {
	    					
	    					YStateProcessUtilities.modifyVariable(neighbour, modified, value, neighbour.getVariablesOriginal().get(value));
	    					
	    				}
	    			}
	    		}else if(type == 7) {
	    			if(value.endsWith(YSensorUtilities.count)) {
	    				Object countValue = null;
	    				if((countValue = neighbour.getVariables().get(value)) != null) {
	    					Integer count = new Integer((String) countValue);
	    					
	    					YStateProcessUtilities.modifyVariable(neighbour, modified, value, ""+((count>1)?(count-1):0));
	    					
	    				}
	    			}else if(value.endsWith(YSensorUtilities.isOffered) || value.endsWith(YSensorUtilities.isAllocated) || value.endsWith(YSensorUtilities.isStarted) || value.endsWith(YSensorUtilities.isCompleted)) {
	    				
	    				YStateProcessUtilities.modifyVariable(neighbour, modified, value, YSensorUtilities.falseString);
	    				
	    			}else if(value.endsWith(YSensorUtilities.offerTime) || value.endsWith(YSensorUtilities.offerTimeInMillis) || value.endsWith(YSensorUtilities.allocateTime) || value.endsWith(YSensorUtilities.allocateTimeInMillis) || 
	    					value.endsWith(YSensorUtilities.startTime) || value.endsWith(YSensorUtilities.startTimeInMillis) || value.endsWith(YSensorUtilities.completeTime) || value.endsWith(YSensorUtilities.completeTimeInMillis) || 
	    					value.endsWith(YSensorUtilities.offerResource) || value.endsWith(YSensorUtilities.allocateResource) || value.endsWith(YSensorUtilities.startResource) || value.endsWith(YSensorUtilities.completeResource) || 
	    					value.endsWith(YSensorUtilities.passTime) || value.endsWith(YSensorUtilities.passTimeInMillis)) {
    					
	    				YStateProcessUtilities.modifyVariable(neighbour, modified, value, null);
	    				
	    			}
	    		}
	    	}else if(type == 7 && value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.DOT)) {

    			YStateProcessUtilities.modifyVariable(neighbour, modified, value, null);
	    			
	    	}
    	}
    	if(!modified.isEmpty()) {
    		MutationUtilities.updateQueryVariable2(modified, neighbour, currentState);
    	}
    	
    }

}
