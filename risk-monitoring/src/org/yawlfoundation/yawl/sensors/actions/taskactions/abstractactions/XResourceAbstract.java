package org.yawlfoundation.yawl.sensors.actions.taskactions.abstractactions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRoleTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.util.JDOMUtil;

public abstract class XResourceAbstract {

	private static Activity activityLayer = null;
	private static ActivityRole activityRoleLayer = null;
	private static Role roleLayer = null;
	
	public static void setLayers(Activity activityLayer, ActivityRole activityRoleLayer, Role roleLayer) {
		XResourceAbstract.activityLayer = activityLayer;
		XResourceAbstract.activityRoleLayer = activityRoleLayer;
		XResourceAbstract.roleLayer = roleLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName, int resourceType) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		
		LinkedList<ActivityTuple> taskIDs = activityLayer.getIDs(taskName, true, WorkDefIDs, true, resourceType, true, null, false, false);
		if(taskIDs!=null) {
			LinkedList<ActivityRoleTuple> resource = activityRoleLayer.getRows(taskIDs.get(0).getTaskID(), true, null, false, 0, 0, resourceType, true);
			return YSensorUtilities.createResource(roleLayer, taskName, resource, resourceType);
		}else {
			return null;
		}
	}
	
	public static Object getActionResultList(String WorkDefID, String taskName, int resourceType) {
		LinkedList<String> result = new LinkedList<String>();
		
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		
		LinkedList<ActivityTuple> taskIDs = activityLayer.getIDs(taskName, true, WorkDefIDs, true, resourceType, true, null, false, true);
		if(taskIDs!=null) {
			for(int i = 1; i < taskIDs.size(); i++) {
				LinkedList<ActivityRoleTuple> resource = activityRoleLayer.getRows(taskIDs.get(i).getTaskID(), true, null, false, 0, 0, resourceType, true);
				result.add(YSensorUtilities.createResource(roleLayer, taskName, resource, resourceType));
			}
			return result;
		}else {
			return null;
		}
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String taskName, int resourceType) {
		LinkedList<String> result = new LinkedList<String>();
		LinkedList<ActivityTuple> taskIDs = activityLayer.getIDs(taskName, true, workDefIDs, true, resourceType, true, null, false, false);
		if(taskIDs!=null) {
			for(ActivityTuple taskID : taskIDs) {
				if(taskID!=null) {
					LinkedList<ActivityRoleTuple> resource = activityRoleLayer.getRows(taskID.getTaskID(), true, null, false, 0, 0, resourceType, true);
					result.add(YSensorUtilities.createResource(roleLayer, taskName, resource, resourceType));
				}else {
					result.add("null");
				}
			}
		}
		return result;
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String taskName, int resourceType) {
		LinkedList<Object> result = new LinkedList<Object>();
		LinkedList<ActivityTuple> taskIDs = activityLayer.getIDs(taskName, true, workDefIDs, true, resourceType, true, null, false, true);
		if(taskIDs!=null) {
			for(String workDefID : workDefIDs) {
				LinkedList<String> tmpResult = new LinkedList<String>();
				for(ActivityTuple taskID : taskIDs) {
					if(taskID!=null) {
						if(taskID.getWorkDefID().equals(workDefID)) {
							LinkedList<ActivityRoleTuple> resource = activityRoleLayer.getRows(taskID.getTaskID(), true, null, false, 0, 0, resourceType, true);
							tmpResult.add(YSensorUtilities.createResource(roleLayer, taskName, resource, resourceType));
						}
						result.add(tmpResult);
					}else {
						result.add("null");
					}
				}
			}
		}
		return result;
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int activityType, int resourceType) {
		LinkedList<ActivityTuple> taskids = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, activityType, true, null, false, false);
		if(taskids != null) {
			for(ActivityTuple taskid : taskids) {
				String a = (String) getActionResult(taskid.getWorkDefID(), activity, resourceType);
				
				Element resEle = JDOMUtil.stringToElement(a);
				
				StringBuffer sb = new StringBuffer();
				for(Element el : (List<Element>) resEle.getChildren()) {
					sb.append(JDOMUtil.elementToString(el));
				}
				a = sb.toString();
				String b = postVar;
				
				YSensorUtilities.generateCaseCompareResource(a, b, oper, casesPartial, taskid.getWorkDefID(), suffix);
			}
		}
		
		return casesPartial;
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial, int activityType, int resourceType) {
		LinkedList<ActivityTuple> taskids = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, activityType, true, null, false, true);
		if(taskids != null) {
			for(ActivityTuple taskid : taskids) {
				String a = (String) getActionResult(taskid.getWorkDefID(), activity, resourceType);
				
				Element resEle = JDOMUtil.stringToElement(a);
				
				StringBuffer sb = new StringBuffer();
				for(Element el : (List<Element>) resEle.getChildren()) {
					sb.append(JDOMUtil.elementToString(el));
				}
				a = sb.toString();
				String b = postVar;
				
				YSensorUtilities.generateCaseCompareResource(a, b, oper, casesPartial, taskid.getWorkDefID(), suffix);
			}
		}
		
		return casesPartial;
	}
	
}
