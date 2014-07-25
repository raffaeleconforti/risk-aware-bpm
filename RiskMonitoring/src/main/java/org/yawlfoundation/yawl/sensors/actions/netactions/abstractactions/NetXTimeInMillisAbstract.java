package org.yawlfoundation.yawl.sensors.actions.netactions.abstractactions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;

public abstract class NetXTimeInMillisAbstract {
	
	private static SubProcess subProcessLayer = null;
	
	public static void setLayers(SubProcess subProcessLayer) {
		NetXTimeInMillisAbstract.subProcessLayer = subProcessLayer;
	}

	public static Object getActionResult(String WorkDefID, String netName, int subprocessType) {
		Vector<String> time = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, subprocessType, true, null, false, false);
		if(time!=null) {
			return ""+(new Long(time.firstElement()));
		}else {
			return null;
		}
	}
	
	public static Object getActionResultList(String WorkDefID, String netName, int subprocessType) {
		Vector<String> time = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, subprocessType, true, null, false, true);
		ArrayList<Object> result = null;
		if(time!=null) {
			result = new ArrayList<Object>(time.size()-1);
			for(int i=1; i<time.size(); i++) {
				result.add(""+(new Long(time.get(i))));
			}
			return result;
		}else {
			result = new ArrayList<Object>(0);
			return result;
		}
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName, int subprocessType) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, netName, subprocessType));
		}
		return result;	
	}
	
	public static Object getActionResultList(List<String> workDefIDs, String netName, int subprocessType) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String caseID : workDefIDs) {
			result.add(getActionResultList(caseID, netName, subprocessType));
		}
		return result;	
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int subprocessType) {
		try {
			Vector<Vector<String>> time = subProcessLayer.getRows(null, false, activity, true, null, false, new Long(postVar), oper, subprocessType, true, null, false, false);
			for(Vector<String> activityID : time) {
				if(workDefIDs.contains(activityID.firstElement())) {
					casesPartial.add(activityID.firstElement());
				}
			}
		}catch (NumberFormatException nfe) {
			if(!postVar.equals("null")) nfe.printStackTrace();
		}
		
		return casesPartial;
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int subprocessType) {
		return getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial, subprocessType);
	}

}
