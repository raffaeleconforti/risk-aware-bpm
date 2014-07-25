package org.yawlfoundation.yawl.sensors.language.loops.concreteloops;

import org.yawlfoundation.yawl.sensors.language.loops.abstractloops.ForNoMemory;

public class ForMIN extends ForNoMemory {

	public static Object aggregate(Object firstElement, Object secondElement) {
		if(firstElement instanceof Double && secondElement instanceof Double) {
			return Math.min((Double) firstElement, (Double) secondElement);
		}
		
		return null;
	}
	
	public static Object inizializeRes() {
		return Double.MAX_VALUE;
	}
	
}
