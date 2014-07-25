package server.worklist;

import java.io.IOException;
import java.util.Map;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 28/05/2013
 *
 */

public class EngineConnector extends InterfaceBWebsideController {

	private static EngineConnector me = null;
	private static String _engineHandle = null;
	private static String _engineURI = "http://localhost:8080/yawl/ib";
		
    public static EngineConnector getInstance() {
    	if (me == null) me = new EngineConnector();
    	return me;
    }
	
	protected static void initInterfaces(Map<String, String> urlMap) {
		_engineURI = urlMap.get("InterfaceB_BackEnd");
        if (_engineURI == null) _engineHandle = "http://localhost:8080/yawl/ib";
        me.setUpInterfaceBClient(_engineURI);
	}

	@Override
	public void handleEnabledWorkItemEvent(WorkItemRecord enabledWorkItem) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {
		// TODO Auto-generated method stub
	}
	
	public String getEngineHandle() {
        try {
        	while (!this.successful(_engineHandle)) {
				_engineHandle = this.connect("sensorService", "ySensor");
			}
		} catch (IOException e) {
			_engineHandle = "<failure>Problem connecting to engine.</failure>";
		}
        return _engineHandle;
    }
	
}
