package org.yawlfoundation.yawl.sensors.language.loops;

import org.yawlfoundation.yawl.sensors.language.loops.concreteloops.*;

import java.util.HashMap;
import java.util.LinkedList;

public class ForExecutor {

	public static Object getForResult(int type, LinkedList<Object> input, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource, ForInterpreter forInterpreter) {
		
		switch(type) {
			
			case ForIdentifier.FORAND: return ForAND.getForResult(input, mappingName, variables, resource, forInterpreter);
			case ForIdentifier.FOROR: return ForOR.getForResult(input, mappingName, variables, resource, forInterpreter);
			
			case ForIdentifier.FORADD: return ForADD.getForResult(input, mappingName, variables, resource, forInterpreter);
			case ForIdentifier.FORMUL: return ForMUL.getForResult(input, mappingName, variables, resource, forInterpreter);
			
			case ForIdentifier.FORMIN: return ForMIN.getForResult(input, mappingName, variables, resource, forInterpreter);
			case ForIdentifier.FORMAX: return ForMAX.getForResult(input, mappingName, variables, resource, forInterpreter);
			
			case ForIdentifier.FORAVG: return ForAVG.getForResult(input, mappingName, variables, resource, forInterpreter);
				
		}
		
		return null;
		
	}

	public static Object inizializeRes(int type) {
		
		switch(type) {
			
			case ForIdentifier.FORAND: return ForAND.inizializeRes();
			case ForIdentifier.FOROR: return ForOR.inizializeRes();
			
			case ForIdentifier.FORADD: return ForADD.inizializeRes();
			case ForIdentifier.FORMUL: return ForMUL.inizializeRes();
			
			case ForIdentifier.FORMIN: return ForMIN.inizializeRes();
			case ForIdentifier.FORMAX: return ForMAX.inizializeRes();
			
			case ForIdentifier.FORAVG: return ForAVG.inizializeRes();
				
		}// TODO Auto-generated method stub
		return null;
	}

	public static Object aggregate(Object firstElement, Object secondElement, Object memory, int type) {
		
		switch(type) {
			
			case ForIdentifier.FORAND: return ForAND.aggregate(firstElement, secondElement);
			case ForIdentifier.FOROR: return ForOR.aggregate(firstElement, secondElement);
			
			case ForIdentifier.FORADD: return ForADD.aggregate(firstElement, secondElement);
			case ForIdentifier.FORMUL: return ForMUL.aggregate(firstElement, secondElement);
			
			case ForIdentifier.FORMIN: return ForMIN.aggregate(firstElement, secondElement);
			case ForIdentifier.FORMAX: return ForMAX.aggregate(firstElement, secondElement);
			
			case ForIdentifier.FORAVG: return ForAVG.aggregate(firstElement, secondElement, memory);
				
		}// TODO Auto-generated method stub
		return null;
	}

	public static Object finalize(Object firstElement, Object memory, int type) {
		switch(type) {
			
			case ForIdentifier.FORAVG: return ForAVG.finalize(firstElement, memory);
				
		}
		return null;
	}

	public static Object inizializeMemory(int type) {
		
		switch(type) {
			
			case ForIdentifier.FORAVG: return ForAVG.inizializeMemory();
				
		}
		return null;
		
	}
	
}
