package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.ExecutionGraph.NodeGraph;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;


public class MutationRollBack extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationRollBack();
		return me;
	}

	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		for(int i=0; i<status.getTasksSize(); i++) {
			if(taskRollbackable(status, i)) return true;
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		while(!complete) {
			
			int j = YStateProcessUtilities.selectRandom(neighbour.getTasksSize(), neighbour.r);
			
			if(taskRollbackable(neighbour, j)) {
				Task t = neighbour.getTasks()[j];

				updateVariables(neighbour, currentStatus, j, 7, null);
				
				if(t.status!=Task.Completed) {
					if(t.status == Task.Started) {
						t.status = Task.Allocated;
						neighbour.changing += 3;
						
						String res = t.startRes;
						String task = t.taskName;	
						
						neighbour.modifications.add(removeStartResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
						
						neighbour.getResourcesMap().get(res).removeStart(task);
						
						if(t.allocateRes == null) {
							t.allocateRes = res;
							neighbour.getResourcesMap().get(res).addAllocate(task);
						}
					}
					if(t.status == Task.Allocated) {
						t.status = Task.Offered;
						neighbour.changing += 2;
						
						String res = t.allocateRes;
						String task = t.taskName;

						neighbour.modifications.add(removeAllocateResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
						
						neighbour.getResourcesMap().get(res).removeAllocate(task);
						
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
					}
					while(t.status == Task.Offered) {
						String res = t.offerRes.removeFirst();
						neighbour.changing++;
						
						String task = t.taskName;
						
						neighbour.modifications.add(removeOfferResString+task+colomSpaceString+t.engineID+colomSpaceString+res);
						
						neighbour.getResourcesMap().get(res).removeOffer(task);
						
						if(t.offerRes.size() == 0) t.status = Task.Completed;
					}
				}else {
					t.rollback = true;
					neighbour.changing+=9;
					neighbour.modifications.add(rollbackString+t.taskName+colomSpaceString+t.engineID);
				}

				for(NodeGraph ng1 : neighbour.getPending()) {
					if(ng1.nodeName.equals(t.engineID)) {
						gc.removePending(neighbour.getNg(), ng1);
						break;
					}
				}
				
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
	    	}else if(value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.DOT)) {

    			YStateProcessUtilities.modifyVariable(neighbour, modified, value, null);
	    			
	    	}
    	}
    	
    	if(!modified.isEmpty()) {
    		MutationUtilities.updateQueryVariable2(modified, neighbour, currentState);
    	}
	}
	
	private static boolean taskRollbackable(StateYAWLProcess neighbour, int j) {
		
		Task t = neighbour.getTasks()[j];

		if(neighbour.getRemoved()[j]) return false;

		if(t.status <= Task.Unoffered) return false;

		if(Arrays.binarySearch(neighbour.getNamePending(), t.engineID) < 0) {
			return false;
		}
		
		if(t.allocateMod || t.startMod || t.relres || t.offerMod) return false;
		
		return true;
	}

}
