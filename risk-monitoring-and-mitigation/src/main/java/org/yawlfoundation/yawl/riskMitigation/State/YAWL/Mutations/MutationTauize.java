package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.util.LinkedList;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;


public class MutationTauize extends AbstractMutation {

	private static Mutation me = null;
	
	public static Mutation getInstance() {
		if(me == null) me = new MutationTauize();
		return me;
	}

	@Override
	public boolean checkIfPossible(StateYAWLProcess status) {
		for(int i=0; i<status.getTasksSize(); i++) {
			if(taskTauizable(status, i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus) {
		boolean complete = false;
		while(!complete) {
			
			int j = YStateProcessUtilities.selectRandom(neighbour.getTasksSize(), neighbour.r);
			
			if(taskTauizable(neighbour, j)) {
				
				Task t = neighbour.getTasks()[j];
				
				neighbour.getRemoved()[j] = true;
				neighbour.changing+=9;
				
				updateVariables(neighbour, currentStatus, j, 6, null);
				
				neighbour.modifications.add(tauizeString+t.taskName+colomSpaceString+t.engineID);
				
				complete = true;
			}
		}
	}

	@Override
	public void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource) {
		Task t = neighbour.getTasks()[task];
    	LinkedList<String> modified = new LinkedList<String>();
    	String timeKey = null;
    	String timeValue = null;
    	String timeSubtraction = null;
    	for(Entry<String, String> entry: neighbour.getCurrent().entrySet()) {
    		String value = entry.getValue();
    		if(value.equals(YSensorUtilities.caseTEIM)) {
    			timeKey = value;
    			timeValue = (String) neighbour.getVariables().get(value);
    		}else if(value.contains(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YExpression.PARTA)) {
    			if(t.status == Task.Completed && value.endsWith(YSensorUtilities.count)) {
	    				
    				YStateProcessUtilities.modifyVariable(neighbour, modified, value, null);
	    			
    			}
    			if(value.endsWith(YSensorUtilities.timeEstimationInMillis)) {
    				timeSubtraction = (String) neighbour.getVariables().get(value);
    				
    				YStateProcessUtilities.modifyVariable(neighbour, modified, value, "0");
    				
    			}
	    	}else {
//	    		timeSubtraction = (String) neighbour.getVariables().get(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YSensorUtilities.timeEstimationInMillis);
	    		timeSubtraction = (String) neighbour.getLogVariableTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+t.taskName+YSensorUtilities.timeEstimationInMillis, neighbour.getCaseID());
	    	}
    	}
    	if(timeKey!=null && timeValue!=null && timeSubtraction!=null) {
    		Long l1 = new Long(timeValue);
    		Long l2 = new Long(timeSubtraction);
    		long l = l1-l2;
    		
    		YStateProcessUtilities.modifyVariable(neighbour, modified, timeKey, ""+l);
			
    	}
    	if(!modified.isEmpty()) {
    		MutationUtilities.updateQueryVariable2(modified, neighbour, currentState);
    	}
	}
	
	private static boolean taskTauizable(StateYAWLProcess neighbour, int j) {
		Task t = neighbour.getTasks()[j];
		if(!neighbour.getTaskRelevant().contains(t.taskName) && !neighbour.isSkipneed()) return false;
		if(neighbour.getNotSkippable().contains(t.engineID)) return false;
		if(neighbour.getRemoved()[j]) return false;
		if(!(t.status == Task.Unoffered || t.status == Task.Unknown || (t.status == Task.Completed && t.rollback))) return false;
		if(!checkDataFlow(neighbour, j)) return false;
		return true;
	}
	
	private static boolean checkDataFlow(StateYAWLProcess neighbour, int j) {
		return true;
	}

}
