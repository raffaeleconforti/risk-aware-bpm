package org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.concretefunctions;

import java.util.HashMap;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions.XMinNumberExcept;

public class AllocatedMinNumberExcept extends XMinNumberExcept{

	private static int queueType = WorkQueue.ALLOCATED;
	
	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, term, mappingName, variables, queueType);
	}
	
}
