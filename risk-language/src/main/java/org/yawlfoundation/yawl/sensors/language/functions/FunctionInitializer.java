package org.yawlfoundation.yawl.sensors.language.functions;

import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions.XContain;
import org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions.XMinNumberExcept;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XList;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XMinNumber;
import org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions.XNumber;

public class FunctionInitializer {

	public static void InizializeActions(WorkQueueFacade workQueue) {
		
		XList.setWorkQueueClient(workQueue);
		XNumber.setWorkQueueClient(workQueue);
		XMinNumber.setWorkQueueClient(workQueue);
		XMinNumberExcept.setWorkQueueClient(workQueue);
		XContain.setWorkQueueClient(workQueue);
		
	}
	
}
