package org.yawlfoundation.yawl.riskMitigation.State.YAWL;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.Mutation;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRemoveAllocateRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRemoveOfferRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRemoveStartRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRestoreAllocateRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRestoreOfferRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRestoreStartRes;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationRollBack;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationStartFromOtherProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations.MutationTauize;

public class YStateProcessNeighbourCreator {

	private static YStateProcessNeighbourCreator me = null;
	private static Mutation[] mutationOperations = null;
	
	public static YStateProcessNeighbourCreator getInstance() {
		if(me == null) {
			me = new YStateProcessNeighbourCreator();
			
			mutationOperations = new Mutation[] {
					MutationRemoveOfferRes.getInstance(),
					MutationRemoveAllocateRes.getInstance(),
					MutationRemoveStartRes.getInstance(),
					
					MutationRestoreOfferRes.getInstance(),
					MutationRestoreAllocateRes.getInstance(),
					MutationRestoreStartRes.getInstance(),

					MutationTauize.getInstance(),
					MutationRollBack.getInstance(),
					MutationStartFromOtherProcess.getInstance(),	
			};
			
		}
		return me;		
	}
	
	
	public static void createNeighbour(StateYAWLProcess neighbour, StateYAWLProcess currentStatus, int mutation) {
		
		mutationOperations[mutation].applayMutation(neighbour, currentStatus);
		
	}
}
