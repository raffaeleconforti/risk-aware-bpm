package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;


public class MutationStartFromOtherProcess extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationStartFromOtherProcess();
		return me;
	}

	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		for(Task t: status.getTasks()) {
			if(status.getTaskRelevant().contains(t.taskName) && t.originalStartRes != null && t.startRes == null && t.allocateRes == null && !t.relres && !t.allocateMod && !t.startMod) {
				LinkedList<String> resources = t.originalOfferResDef;
				for(String resource : resources) {
					LinkedList<String> caseIDs = null;
					if((caseIDs = StateYAWLProcess.resourceRiskInvolvement.get(resource)) != null) {
						if(resources != null && !resource.equals(t.originalStartRes)) {
							LinkedList<String[]> allocated = MutationUtilities.getResourceAllocatedTask(resource);
							LinkedList<String[]> started = MutationUtilities.getResourceStartedTask(resource);
							if(allocated.size() == 1 && started.size() == 0) {
								if(caseIDs.contains(belongsTo(allocated.getFirst()))) return true;
							}else if(allocated.size() == 0 && started.size() == 1){
								if(caseIDs.contains(belongsTo(started.getFirst()))) return true;
							}
						}	
					}
				}
			}
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		Task t = null;
		LinkedList<String[]> allocated = null;
		LinkedList<String[]> started = null;
		
		while(!complete) {

			int j = YStateProcessUtilities.selectTask(neighbour);
			t = neighbour.getTasks()[j];
			
			if(t.originalStartRes != null && t.startRes == null && t.allocateRes == null && !t.relres && !t.allocateMod && !t.startMod) {
				
				LinkedList<String> resources = t.originalOfferResDef;
				for(String resource : resources) {
					LinkedList<String> caseIDs = null;
					if((caseIDs = StateYAWLProcess.resourceRiskInvolvement.get(resource)) != null) {
						if(resources != null && !resource.equals(t.originalStartRes)) {
							allocated = MutationUtilities.getResourceAllocatedTask(resource);
							started = MutationUtilities.getResourceStartedTask(resource);
							if(allocated.size() == 1 && started.size() == 0) {
								if(caseIDs.contains(belongsTo(allocated.getFirst()))) {
									//TODO de-associate task from previous case
									if(t.status == Task.Unoffered) {
										String task = t.taskName;
										
										t.offerRes.add(resource);
										neighbour.getResourcesMap().get(resource).addOffer(task);
										
										t.allocateRes = resource;
										neighbour.getResourcesMap().get(resource).addAllocate(task);
										
										t.startRes = resource;
										neighbour.getResourcesMap().get(resource).addStart(task);
										
										updateVariables(neighbour, currentStatus, j, 2, t.startRes);
										
										neighbour.modifications.add(startFromOtherProcessString+t.taskName+colomSpaceString+t.engineID+colomSpaceString+resource+": from allocated: "+allocated.getFirst()+colomSpaceString+belongsTo(allocated.getFirst()));
										
										t.relres = true;
										
										neighbour.changing+=7;
										
										complete = true;
									}else if(t.status == Task.Offered) {
										String task = t.taskName;
										
										t.allocateRes = resource;
										neighbour.getResourcesMap().get(resource).addAllocate(task);
										
										t.startRes = resource;
										neighbour.getResourcesMap().get(resource).addStart(task);
										
										updateVariables(neighbour, currentStatus, j, 2, t.startRes);
										
										neighbour.modifications.add(startFromOtherProcessString+t.taskName+colomSpaceString+t.engineID+colomSpaceString+resource+": from allocated: "+allocated.getFirst()+colomSpaceString+belongsTo(allocated.getFirst()));
										
										t.relres = true;
										
										neighbour.changing+=7;
										
										complete = true;
									}
								}
							}else if(allocated.size() == 1 && started.size() == 1){
								if(caseIDs.contains(belongsTo(started.getFirst()))) {
									//TODO de-associate task from previous case
									if(t.status == Task.Unoffered) {
										String task = t.taskName;
										
										t.offerRes.add(resource);
										neighbour.getResourcesMap().get(resource).addOffer(task);
										
										t.allocateRes = resource;
										neighbour.getResourcesMap().get(resource).addAllocate(task);
										
										t.startRes = resource;
										neighbour.getResourcesMap().get(resource).addStart(task);
										
										updateVariables(neighbour, currentStatus, j, 2, t.startRes);
										
										neighbour.modifications.add(startFromOtherProcessString+t.taskName+colomSpaceString+t.engineID+colomSpaceString+resource+": from started: "+started.getFirst()+colomSpaceString+belongsTo(started.getFirst()));
										
										t.relres = true;
										
										neighbour.changing+=7;
										
										complete = true;
									}else if(t.status == Task.Offered) {
										String task = t.taskName;
										
										t.allocateRes = resource;
										neighbour.getResourcesMap().get(resource).addAllocate(task);
										
										t.startRes = resource;
										neighbour.getResourcesMap().get(resource).addStart(task);
										
										updateVariables(neighbour, currentStatus, j, 2, t.startRes);
										
										neighbour.modifications.add(startFromOtherProcessString+t.taskName+colomSpaceString+t.engineID+colomSpaceString+resource+": from starghted: "+started.getFirst()+colomSpaceString+belongsTo(started.getFirst()));
										
										t.relres = true;
										
										neighbour.changing+=7;
										
										complete = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		
		MutationRemoveStartRes.getInstance().updateVariables(neighbour, currentState, task, type, resource);

	}
	
	public static String belongsTo(String[] task) {
		//TODO
		return task[0];
	}

}
