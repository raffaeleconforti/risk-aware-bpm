package org.yawlfoundation.yawl.sensors.language.functions;

import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.concretefunctions.*;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.concretefunctions.*;

import java.util.HashMap;

public class FunctionExecutor {

	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		
		switch(type) {
			
			case FunctionIdentifier.offeredList: return OfferedList.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.allocatedList: return AllocatedList.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.startedList: return StartedList.getFunctionResult(type, name, mappingName, variables);
			
			case FunctionIdentifier.offeredNumber: return OfferedNumber.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.allocatedNumber: return AllocatedNumber.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.startedNumber: return StartedNumber.getFunctionResult(type, name, mappingName, variables);
			
			case FunctionIdentifier.offeredMinNumber: return OfferedMinNumber.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.allocatedMinNumber: return AllocatedMinNumber.getFunctionResult(type, name, mappingName, variables);
			case FunctionIdentifier.startedMinNumber: return StartedMinNumber.getFunctionResult(type, name, mappingName, variables);
			
			case FunctionIdentifier.offeredMinNumberExcept: return OfferedMinNumberExcept.getFunctionResult(type, name, term, mappingName, variables);
			case FunctionIdentifier.allocatedMinNumberExcept: return AllocatedMinNumberExcept.getFunctionResult(type, name, term, mappingName, variables);
			case FunctionIdentifier.startedMinNumberExcept: return StartedMinNumberExcept.getFunctionResult(type, name, term, mappingName, variables);
			
			case FunctionIdentifier.offeredContain: return OfferedContain.getFunctionResult(type, name, term, mappingName, variables);
			case FunctionIdentifier.allocatedContain: return AllocatedContain.getFunctionResult(type, name, term, mappingName, variables);
			case FunctionIdentifier.startedContain: return StartedContain.getFunctionResult(type, name, term, mappingName, variables);
				
			case -1: 	mappingName.put(name+"_"+term, name+"_"+term);
						variables.put(name+"_"+term, 1);
						return name+"_"+term;
		}
		
		return null;
		
	}
	
}
