package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph.GraphCreator;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;

public abstract class AbstractMutation implements Mutation {
	
	public static final String removeOfferResString = "\nRemoveOfferRes: ";
	public static final String removeAllocateResString = "\nRemoveAllocateRes: ";
	public static final String removeStartResString = "\nRemoveStartRes: ";
	public static final String restoreOfferResString = "\nRestoreOfferRes: ";
	public static final String restoreAllocateResString = "\nRestoreAllocateRes: ";
	public static final String restoreStartResString = "\nRestoreStartRes: ";
	public static final String removeResDefOfferListString = "\nRemoveResDefOfferList: ";
	public static final String restoreResDefOfferListString = "\nRestoreResDefOfferList: ";
	public static final String tauizeString = "\nTauize: ";
	public static final String rollbackString = "\nRollback: ";
	public static final String startFromOtherProcessString = "\nStartFromOtherProcess: ";
	public static final String colomSpaceString = ": ";
	public static final String commaString = ",";
	public static final String commaSpaceString =	", ";
	
	public static final String offeredString = "offered";
	public static final String allocatedString = "allocated";
	public static final String startedString = "started";
	
	public static GraphCreator gc = GraphCreator.getInstance();
	
	@Override
	public abstract boolean checkIfPossible(StateYAWLProcess status);

	@Override
	public abstract void applayMutation(StateYAWLProcess neighbour, StateYAWLProcess currentStatus);
	
	@Override
	public abstract void updateVariables(StateYAWLProcess neighbour, StateYAWLProcess currentState, int task, int type, Object resource);

}
