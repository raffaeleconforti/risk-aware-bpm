package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.language.YExpression;

public class NetVariable {

	private static SubProcess subProcessLayer = null;
	private static Variable variableLayer = null;
    private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void setLayers(SubProcess subProcessLayer, Variable variableLayer) {
		NetVariable.subProcessLayer = subProcessLayer;
		NetVariable.variableLayer = variableLayer;
	}

	public static Object getActionResult(String WorkDefID, String netName) {
		String tmpNetName = netName.contains(YExpression.DOT)?netName.substring(0, netName.indexOf(YExpression.DOT)):netName;
		
    	Vector<String> VariableID = subProcessLayer.getVariablesIDs(null, false, tmpNetName, true, WorkDefID, true, 0, 0, 0, false, false);
		Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, VariableID.lastElement(), true, false);
		HashMap<String, String> var = new HashMap<String, String>();
		if(variable!=null) {
			for(Vector<String> vec : variable) {
				var.put(vec.get(1), vec.get(2));
			}
		}
		
		return YSensorUtilities.generateVariableXML(netName, var);
	}
	
	public static Object getActionResultList(String WorkDefID, String netName) {
		LinkedList<String> result = new LinkedList<String>();
    	Vector<String> VariableID = subProcessLayer.getVariablesIDs(null, false, netName, true, WorkDefID, true, 0, 0, 0, false, true);
    	for(int i = 1; i < VariableID.size(); i++) {
			Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, VariableID.get(i), true, false);
			HashMap<String, String> var = new HashMap<String, String>();
			if(variable!=null) {
				for(Vector<String> vec : variable) {
					var.put(vec.get(1), vec.get(2));
				}
			}
			result.add(YSensorUtilities.generateVariableXML(netName, var));
    	}
		
		return result;
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, netName));
		}
		return result;	
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String netName) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String caseID : workDefIDs) {
			result.add(getActionResult(caseID, netName));
		}
		return result;	
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
    	Vector<Vector<String>> activityIDs = subProcessLayer.getRows(null, false, activity, true, null, false, 0, 0, 0, false, null, false, false);
		for(Vector<String> activityID : activityIDs) {
			if(workDefIDs.contains(activityID.firstElement())) {
				Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, activityID.get(5), true, false);
				HashMap<String, String> var = new HashMap<String, String>();
				if(variable!=null) {
					if(variable.size()>0) {
						for(Vector<String> vec : variable) {
							var.put(vec.get(1), vec.get(2));
						}
					}
					String value = var.get(preVar);
					try {
						double a = new Double(value);
						double b = new Double(postVar);
						
						YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.firstElement(), suffix);
						
					} catch (NumberFormatException nfe) {
						try {
							Date a = originalDateFormat.parse(value);
							Date b = originalDateFormat.parse(postVar);
							
							YSensorUtilities.generateCaseCompareDate(a, b, oper, casesPartial, activityID.firstElement(), suffix);
							
						} catch (ParseException e) {
							String a = value;
							String b = postVar;
							if(suffix!=null) {
								Object o = YSensorUtilities.getValueFromXML(YExpression.DOT+suffix, value);
								
								a = YSensorUtilities.checkIfString(o);
								
							}else {
								Object o = YSensorUtilities.getValueFromXML("", value);
								
								a = YSensorUtilities.checkIfString(o);
							}
							
							YSensorUtilities.generateCaseCompareString(a, b, oper, casesPartial, activityID.firstElement(), suffix);

						}
					}
				}
			}
		}
		
		return casesPartial;
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
    	Vector<Vector<String>> activityIDs = subProcessLayer.getRows(null, false, activity, true, null, false, 0, 0, 0, false, null, false, true);
		for(Vector<String> activityID : activityIDs) {
			if(workDefIDs.contains(activityID.firstElement())) {
				Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, activityID.get(5), true, false);
				HashMap<String, String> var = new HashMap<String, String>();
				if(variable!=null) {
					if(variable.size()>0) {
						for(Vector<String> vec : variable) {
							var.put(vec.get(1), vec.get(2));
						}
					}
					String value = var.get(preVar);
					try {
						double a = new Double(value);
						double b = new Double(postVar);
						
						YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.firstElement(), suffix);
						
					} catch (NumberFormatException nfe) {
						try {
							Date a = originalDateFormat.parse(value);
							Date b = originalDateFormat.parse(postVar);
							
							YSensorUtilities.generateCaseCompareDate(a, b, oper, casesPartial, activityID.firstElement(), suffix);
							
						} catch (ParseException e) {
							String a = value;
							String b = postVar;
							if(suffix!=null) {
								Object o = YSensorUtilities.getValueFromXML(YExpression.DOT+suffix, value);
								
								a = YSensorUtilities.checkIfString(o);
								
							}else {
								Object o = YSensorUtilities.getValueFromXML("", value);
								
								a = YSensorUtilities.checkIfString(o);
							}
							
							YSensorUtilities.generateCaseCompareString(a, b, oper, casesPartial, activityID.firstElement(), suffix);

						}
					}
				}
			}
		}
		
		return casesPartial;
	}
	
}
