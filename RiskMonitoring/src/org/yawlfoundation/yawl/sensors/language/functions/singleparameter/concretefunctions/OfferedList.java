package org.yawlfoundation.yawl.sensors.language.functions.singleparameter.concretefunctions;

import java.util.HashMap;

import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XList;

public class OfferedList extends XList{

	private static int queueType = WorkQueue.OFFERED;
	
	public static String getFunctionResult(int type, String name, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
		return getFunctionResult(type, name, mappingName, variables, queueType);
	}
	
}
