package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

import java.util.List;

public class NetCountElements {
	
	private static NetCountElements me = null;
	
	public static NetCountElements getInstance() {
		if(me == null) me = new NetCountElements();
		return me;
	}

	public static Object getActionResult(String WorkDefID, String netName) {
		return "1";
	}

	public static Object getActionResult(List<String> workDefIDs, String netName) {
		return ""+workDefIDs.size();
	}
	
}
