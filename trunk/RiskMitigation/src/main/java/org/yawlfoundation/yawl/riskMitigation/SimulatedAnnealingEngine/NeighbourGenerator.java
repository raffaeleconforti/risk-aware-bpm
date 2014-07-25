package org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine;
import java.util.Random;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessDiscover;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessNeighbourCreator;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;


public class NeighbourGenerator {
	
	public static StateYAWLProcess generateNeighbourState(StateYAWLProcess status) {
		
		StateYAWLProcess neighbour = status.clone();
		boolean[] operations = YStateProcessDiscover.discoverPossibleAtomicOperation(neighbour, neighbour.getOperations());
		neighbour.setOperations(operations);
		
		int i = selectAtomicOperation(operations, status.r);
		i = fixNumber(operations, i);
		if(i == -1) return neighbour;
		
		YStateProcessNeighbourCreator.createNeighbour(neighbour, status, i);
		
		return neighbour;
	}
	
	private static int fixNumber(boolean[] operations, int i) {
		int x = -1;
		for(int j=0; j<operations.length; j++) {
			if(operations[j]) {
				x++;
				if(x==i) return j;
			}
		}
		return -1;
	}

	private static int selectAtomicOperation(boolean[] operations, Random r) {
		int x = 0;
		for(boolean operation: operations) {
			if(operation) x++;
		}
		if(x == 0) return -1;
		return YStateProcessUtilities.selectRandom(x, r);
	}

}
