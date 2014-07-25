package org.yawlfoundation.yawl.sensors.language.loops.concreteloops;

import org.yawlfoundation.yawl.sensors.language.loops.abstractloops.ForNoMemory;

public class ForAND extends ForNoMemory {

	public static Object aggregate(Object firstElement, Object secondElement) {
		if(firstElement instanceof Boolean && secondElement instanceof Boolean) {
			return ((Boolean) firstElement && (Boolean) secondElement);
		}
		
		return null;
	}
	
	public static Object inizializeRes() {
		return true;
	}
	
}
