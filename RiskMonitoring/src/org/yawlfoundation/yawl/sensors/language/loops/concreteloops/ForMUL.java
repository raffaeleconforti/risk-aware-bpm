package org.yawlfoundation.yawl.sensors.language.loops.concreteloops;

import org.yawlfoundation.yawl.sensors.language.loops.abstractloops.ForNoMemory;

public class ForMUL extends ForNoMemory {

	public static Object aggregate(Object firstElement, Object secondElement) {
		if(firstElement instanceof Double && secondElement instanceof Double) {
			return ((Double) firstElement * (Double) secondElement);
		}
		
		return null;
	}
	
	public static Object inizializeRes() {
		return 1.0;
	}
}
