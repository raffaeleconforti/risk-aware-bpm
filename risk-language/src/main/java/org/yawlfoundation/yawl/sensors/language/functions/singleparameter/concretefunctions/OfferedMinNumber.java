package org.yawlfoundation.yawl.sensors.language.functions.singleparameter.concretefunctions;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XMinNumber;

import java.util.HashMap;

public class OfferedMinNumber extends XMinNumber{
	
	private static int queueType = WorkQueue.OFFERED;
	
	public static String getFunctionResult(int type, String name, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, mappingName, variables, queueType);
	}
	
}
