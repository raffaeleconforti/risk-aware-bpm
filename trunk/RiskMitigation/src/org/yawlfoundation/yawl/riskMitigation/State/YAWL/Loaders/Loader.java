package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders;

import java.util.Random;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;


public interface Loader {
	
	public StateYAWLProcess generateStatus(Random r);

}
