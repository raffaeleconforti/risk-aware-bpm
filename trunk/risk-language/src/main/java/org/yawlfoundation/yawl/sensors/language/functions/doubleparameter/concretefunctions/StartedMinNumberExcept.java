package org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.concretefunctions;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions.XMinNumberExcept;

import java.util.HashMap;

public class StartedMinNumberExcept extends XMinNumberExcept{

	private static int queueType = WorkQueue.STARTED;
	
	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, term, mappingName, variables, queueType);
	}
	
}
