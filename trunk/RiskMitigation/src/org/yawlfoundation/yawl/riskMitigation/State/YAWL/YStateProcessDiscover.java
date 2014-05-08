package org.yawlfoundation.yawl.riskMitigation.State.YAWL;

import java.util.Arrays;

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


public class YStateProcessDiscover {
	
	private static YStateProcessDiscover me = null;
	private static Mutation[] mutationOperations = null;
	
	public static YStateProcessDiscover getInstance() {
		if(me == null) {
			me = new YStateProcessDiscover();
			
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
	
	
	public static boolean[] discoverPossibleAtomicOperation(StateYAWLProcess neighbour, boolean[] operations) {
		if(operations == null) {
			operations = new boolean[mutationOperations.length];
		
			for(int i = 0; i<operations.length; i++) {
				operations[i] = mutationOperations[i].checkIfPossible(neighbour);
			}
		}
		return operations;
	}

}
