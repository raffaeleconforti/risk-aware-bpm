package org.yawlfoundation.yawl.sensors.language.loops.concreteloops;

import org.yawlfoundation.yawl.sensors.language.loops.abstractloops.ForWithMemory;

public class ForAVG extends ForWithMemory{

	public static Object inizializeMemory() {
		return 0.0;
	}
	
	public static Object[] aggregate(Object firstElement, Object secondElement, Object memory) {
		if(firstElement instanceof Double & secondElement instanceof Double & memory instanceof Double) {
			double a = (Double) firstElement;
			double b = (Double) secondElement;
			double c = (Double) memory;
			c++;
			return new Object[] {(a + b), c}; 
		}
		return null;
	}
	
	public static Object finalize(Object firstElement, Object memory) {
		if(firstElement instanceof Double & memory instanceof Double) {
			double a = (Double) firstElement;
			double b = (Double) memory;
			return a/b;
		}		
		return null;
	}
	
	public static Object inizializeRes() {
		return 0.0;
	}
	
}
