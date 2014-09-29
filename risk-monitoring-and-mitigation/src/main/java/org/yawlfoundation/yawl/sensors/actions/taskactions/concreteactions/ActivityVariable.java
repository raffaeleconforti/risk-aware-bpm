package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.sf.saxon.s9api.SaxonApiException;

import org.jdom2.Element;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;

public class ActivityVariable {
	
    private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static Activity activityLayer = null;
	private static Variable variableLayer = null;
	
	public static void setLayers(Activity activityLayer, Variable variableLayer) {
		ActivityVariable.activityLayer = activityLayer;
		ActivityVariable.variableLayer = variableLayer;
	}

	public static Object getActionResult(String workDefID, String taskName, String variableName) {
		String tmpTaskName = taskName.contains(YExpression.DOT)?taskName.substring(0, taskName.indexOf(YExpression.DOT)):taskName;
		
		HashMap<String, String> variable = new HashMap<String, String>();
    	LinkedList<String> WorkDefIDs = new LinkedList<String>();
    	WorkDefIDs.add(workDefID);
    	    	
    	LinkedList<ActivityTuple> variablesIDs = activityLayer.getVariablesIDs(null, false, tmpTaskName, true, WorkDefIDs, true, 0, false, false);
    	
    	if(variablesIDs != null) {
//	    	Vector<String> variablesID = variablesIDs.firstElement();
	    	
	    	generateVariables(variable, variablesIDs);
	    	
	    	String var = YSensorUtilities.generateVariableXML(tmpTaskName, variable);
	    	
			try {
				if(var != null) {
					String query = SaxonUtil.evaluateQuery(taskName.replace('.', '/'), JDOMUtil.stringToDocument(var));
					if(query.contains("<?xml version=")) {
		    			query = query.substring(query.indexOf(">")+1);
		    			if(query.isEmpty()) query = null;
		    		}
					if(query != null && query instanceof String) {
						Element e = JDOMUtil.stringToElement(query);
						if(!e.getValue().isEmpty()) {
							String stringLog = query;
							stringLog = stringLog.substring(stringLog.indexOf(">")+1);
							stringLog = stringLog.substring(0, stringLog.lastIndexOf("<"));
							query = stringLog;							
						}
					}
					
					return query;
				}else {
					return null;
				}
			} catch (SaxonApiException e) {
//				e.printStackTrace();
				return null;
			}
    	}else {
    		return null;
    	}
		
	}
	
	public static Object getActionResultList(String workDefID, String taskName, String variableName) {
		LinkedList<String> result = new LinkedList<String>();
		
		String tmpTaskName = taskName.contains(YExpression.DOT)?taskName.substring(0, taskName.indexOf(YExpression.DOT)):taskName;
		
		HashMap<String, String> variable = new HashMap<String, String>();
    	LinkedList<String> WorkDefIDs = new LinkedList<String>();
    	WorkDefIDs.add(workDefID);
    	    	
    	LinkedList<ActivityTuple> variablesIDs = activityLayer.getVariablesIDs(null, false, tmpTaskName, true, WorkDefIDs, true, 0, false, true);
//    	for(Vector<String> variablesID : variablesIDs) {
    		generateVariables(variable, variablesIDs);
    		
    		String var = YSensorUtilities.generateVariableXML(tmpTaskName, variable);
    		String query = null;
    		try {
				query = SaxonUtil.evaluateQuery(taskName.replace('.', '/'), JDOMUtil.stringToDocument(var));
				if(query.contains("<?xml version=")) {
	    			query = query.substring(query.indexOf(">")+1);
	    			if(query.isEmpty()) query = null;
	    		}
				if(query != null && query instanceof String) {
					Element e = JDOMUtil.stringToElement(query);
					if(!e.getValue().isEmpty()) {
						String stringLog = query;
						stringLog = stringLog.substring(stringLog.indexOf(">")+1);
						stringLog = stringLog.substring(0, stringLog.lastIndexOf("<"));
						query = stringLog;							
					}
				}
			} catch (SaxonApiException e) {
				e.printStackTrace();
			}
    		result.add(query);
//    	}
    	
    	return result;
		
	}
	
    private static void generateVariables(HashMap<String, String> variable, LinkedList<ActivityTuple> variablesIDs) {
    	for(ActivityTuple variableID : variablesIDs) {

    		if(!variableID.equals("-1")) {// && !variableID.equals(WorkDefID)) {
				
				generateMatrixVariable(variable, variableLayer, variableID.getVariableID());
				
			}
    	}
    }
    
    private static void generateMatrixVariable(HashMap<String, String> variable, Variable variableLayer, String variableID) {
    	Vector<Vector<String>> matrixVar = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, variableID, true, true);
    	if(matrixVar!=null) {	
			for(Vector<String> vectorVar : matrixVar) {
    			String variableName = vectorVar.get(1);
    			String value = vectorVar.get(2);
    			if(!value.startsWith(YExpression.MINOR+variableName+YExpression.GREATER)) {
					value = YExpression.MINOR+variableName+YExpression.GREATER+value+"</"+variableName+YExpression.GREATER;
					variable.put(variableName, value);
				}else {
					variable.put(variableName, value);
				}
    		}
    	}
    }

	public static Object getActionResult(List<String> workDefIDs, String taskName, String variableName) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, taskName, variableName));
		}
		return result;	
	}
	
	public static Object getActionResultList(List<String> workDefIDs, String taskName, String variableName) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String caseID : workDefIDs) {
			result.add(getActionResultList(caseID, taskName, variableName));
		}
		return result;	
	}
	
    public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
    	LinkedList<ActivityTuple> activityIDs = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, 0, false, null, false, false);
    	if(activityIDs != null) {
			for(ActivityTuple activityID : activityIDs) {
				if(!activityID.getVariableID().isEmpty()) {
					Vector<String> values = variableLayer.getValues(null, false, preVar, true, 0, 0, 0, null, false, activityID.getVariableID(), true, true);
					if(values!=null) {
	
						String value = YSensorUtilities.getVariableValue(values.firstElement(), suffix);
	
						populateCaseIDActivityVariable(value, postVar, suffix, oper, activityID.getWorkDefID(), casesPartial);
						
					}
				}
			}
		}
    	
    	return casesPartial;
	}
    
    public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
    	LinkedList<ActivityTuple> activityIDs = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, 0, false, null, false, true);
    	if(activityIDs != null) {
			for(ActivityTuple activityID : activityIDs) {
				if(!activityID.getVariableID().isEmpty()) {
					Vector<String> values = variableLayer.getValues(null, false, preVar, true, 0, 0, 0, null, false, activityID.getVariableID(), true, true);
					if(values!=null) {
	
						String value = YSensorUtilities.getVariableValue(values.firstElement(), suffix);
	
						populateCaseIDActivityVariable(value, postVar, suffix, oper, activityID.getWorkDefID(), casesPartial);
						
					}
				}
			}
		}
    	
    	return casesPartial;
	}
    
    private static void populateCaseIDActivityVariable(String value, String postVar, String suffix, int oper, String ID, HashSet<String> casesPartial) {
    	try {
    		double a = new Double(value);
    		double b = new Double(postVar);
    		
    		YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, ID, suffix);
    		
    	} catch (NumberFormatException nfe) {
    		try {
    			Date a = originalDateFormat.parse(value);
    			Date b = originalDateFormat.parse(postVar);
    			
    			YSensorUtilities.generateCaseCompareDate(a, b, oper, casesPartial, ID, suffix);

    		} catch (ParseException e) {
    			String a = value;
    			String b = postVar;
    			if(a==null) { //Jump
    				if(oper==2 && b==null) casesPartial.add(ID);
    				if(oper==5 && b!=null) casesPartial.add(ID);
    			}else if(b==null ) { //Jump
    				if(oper==5 && a!=null) casesPartial.add(ID);
    			}else {
    				YSensorUtilities.generateCaseCompareString(a, b, oper, casesPartial, ID, suffix);
    			}
    		}
    	}
    }
	
}
