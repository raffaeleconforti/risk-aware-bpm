package org.yawlfoundation.yawl.sensors.language.functions.singleparameter.concretefunctions;

import java.util.HashMap;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XNumber;

public class AllocatedNumber extends XNumber{

	private static int queueType = WorkQueue.ALLOCATED;
	
	public static String getFunctionResult(int type, String name, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, mappingName, variables, queueType);
	}
	
}
