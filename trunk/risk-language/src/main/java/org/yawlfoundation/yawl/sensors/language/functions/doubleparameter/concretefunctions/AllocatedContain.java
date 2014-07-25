package org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.concretefunctions;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions.XContain;

import java.util.HashMap;

public class AllocatedContain extends XContain{

	private static int queueType = WorkQueue.ALLOCATED;
	
	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, term, mappingName, variables, queueType);
	}
	
}
