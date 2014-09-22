package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.actions.netactions.abstractactions.NetXTimeInMillisAbstract;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;

public class NetStartTimeInMillis extends NetXTimeInMillisAbstract{
	
	private static int subprocessType = SubProcess.Start;
	
	public static Object getActionResult(String workDefID, String netName) {
		return getActionResult(workDefID, netName, subprocessType);
	}
	
	public static Object getActionResultList(String workDefID, String netName) {
		return getActionResultList(workDefID, netName, subprocessType);
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName) {
		return getActionResult(workDefIDs, netName, subprocessType);
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String netName) {
		return getActionResultList(workDefIDs, netName, subprocessType);
	}

	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, subprocessType);
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		return getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, subprocessType);
	}
    	
}