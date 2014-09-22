package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.util.List;

public class ActivityCountElements {
	
	private static ActivityCountElements me = null;
	
	public static ActivityCountElements getInstance() {
		if(me == null) me = new ActivityCountElements();
		return me;
	}

	public static Object getActionResult(String WorkDefID, String taskName) {
		return "1";
	}

	public static Object getActionResult(List<String> workDefIDs, String taskName) {
		return ""+workDefIDs.size();
	}
	
}
