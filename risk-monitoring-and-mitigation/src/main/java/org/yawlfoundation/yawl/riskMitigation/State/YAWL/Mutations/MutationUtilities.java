package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Mutations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom2.Element;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.sensors.YSensorManagerImplLayer;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRoleTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.language.MathEvaluator;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;


public class MutationUtilities {

	private static YExpression yExpression = new YExpression();
	private static MathEvaluator mathEvaluator = new MathEvaluator();

	private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static YSensorManagerImplLayer ysmi = YSensorManagerImplLayer.getInstance();

	public static final String commaString = ",";
	public static final String commaSpaceString =	", ";
	
	public static final String offerString = "offer";
	public static final String allocateString = "allocate";
	public static final String startString = "start";
	public static final String completeString = "complete";
	
	public static final String xmlID = "id";
	
	public static Activity activityLayer = Activity.getActivity("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Activity");
	public static ActivityRole activityRoleLayer = ActivityRole.getActivityRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_ActivityRole");
	public static Role roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role");
	public static SubProcess subProcessLayer = SubProcess.getSubProcess("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_SubProcess");
	public static WorkflowDefinition workflowDefinitionLayer = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition");
	public static Variable variableLayer = Variable.getVariable("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Variable");
	
	public static void updateModified(LinkedList<String> keys, StateYAWLProcess neighbour) {
		for(Entry<String, String> entry : neighbour.getMappingName().entrySet()) {
    		if(keys.contains(entry.getValue())) {
    			neighbour.getModified().add(entry.getKey());
    		}
    	}
    	HashSet<String> newModified = new HashSet<String>();
    	
    	do{
    		if(newModified.size()>0) {
    			neighbour.getModified().addAll(newModified);
    		}
    		newModified = new HashSet<String>();
	    	for(Entry<String, String> entry : neighbour.getMappingName().entrySet()) {
	    		if(!neighbour.getModified().contains(entry.getKey())) {
		    		for(String modifiedVar : neighbour.getModified()) {
		    			if(entry.getValue().contains(neighbour.getMappingName().get(modifiedVar)) || entry.getValue().contains(modifiedVar) || entry.getKey().contains(modifiedVar)) {
		        			newModified.add(entry.getKey());
		        		}
		    		}
	    		}
	    	}
    	}while(newModified.size()>0);
	}
	
	public static boolean checkIfModified(StateYAWLProcess neighbour, String key) {
		for(String modifiedVar : neighbour.getModified()) {
			if(neighbour.getMappingName().get(key).contains(neighbour.getMappingName().get(modifiedVar)) || neighbour.getMappingName().get(key).contains(modifiedVar) || key.contains(modifiedVar)) {
    			return true;
    		}
		}
		return false;
	}
	
	public static LinkedList<String> generateCase2(String param, StateYAWLProcess neighbour, StateYAWLProcess currentState) {
		String caseID = neighbour.getCaseID();
		
    	LinkedList<String> activitySubProcess = new LinkedList<String>();
		LinkedList<String> action = new LinkedList<String>();
		LinkedList<String> operator = new LinkedList<String>();
		LinkedList<HashSet<String>> cases = new LinkedList<HashSet<String>>();
		
		param = YSensorUtilities.buildParam(param);

		YSensorUtilities.populateActivitySubProcessActionAndOperator(param, activitySubProcess, action, operator);
		
		for(int i=0; i<activitySubProcess.size(); i++) {
			boolean netVariable = false;
			boolean caseInfo = false;
			HashSet<String> casesPartial = null;
			String activity = activitySubProcess.get(i);
			String variableFull = action.get(i);
			
			if(activity.equals("") && variableFull.startsWith(YSensorUtilities.id)){
				caseInfo = true;
			}
			
			String[] preVarPostVarSuffix = YSensorUtilities.populatePreVarPostVarSuffix(variableFull);
			
			int oper = new Integer(preVarPostVarSuffix[0]);
			String preVar = preVarPostVarSuffix[1];
			String postVar = preVarPostVarSuffix[2];
			String suffix = preVarPostVarSuffix[3];
			
			if(postVar.contains(YExpression.MINOR) || postVar.contains(YExpression.GREATER) || postVar.contains(YExpression.SINGLEEQUAL) || postVar.contains(YExpression.NOT) || postVar.contains(YExpression.AND) || postVar.contains(YExpression.OR) || postVar.contains(YExpression.PARTA) || postVar.contains(YExpression.PARTC) || postVar.contains(YExpression.SUM) || postVar.contains(YExpression.SUB) || postVar.contains(YExpression.MULTIPLY) || postVar.contains(YExpression.DIVIDE) || postVar.contains(YExpression.EXP) || postVar.contains(YExpression.MOD)) {
				StringTokenizer st = new StringTokenizer(postVar, "<>=!&|()+*-/^%", true);
				StringBuffer sb = new StringBuffer();
				while(st.hasMoreTokens()) {
					String tmp = st.nextToken();
					if(!(tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.SINGLEEQUAL) || tmp.contains(YExpression.NOT) || tmp.contains(YExpression.AND) || tmp.contains(YExpression.OR) || tmp.contains(YExpression.PARTA) || tmp.contains(YExpression.PARTC) || tmp.contains(YExpression.SUM) || tmp.contains(YExpression.SUB) || tmp.contains(YExpression.MULTIPLY) || tmp.contains(YExpression.DIVIDE) || tmp.contains(YExpression.EXP) || tmp.contains(YExpression.MOD))) {
						if(!tmp.startsWith("\"")) {
							try {
								new Double(tmp);
							}catch(NumberFormatException nfe) {
								if(tmp.equals(YSensorUtilities.idCurr)){
									tmp = caseID;
								}else {
									if(neighbour.getModified().contains(tmp)) {
																				
										tmp = YSensorUtilities.preprocessResorce((String) currentState.getVariables().get(neighbour.getMappingName().get(tmp)), variableFull);
										
									}else {
										
										tmp = YSensorUtilities.preprocessResorce((String) currentState.getVariables().get(tmp), variableFull);
										
									}
								}
							}
						}else {
							tmp = tmp.substring(1, tmp.length()-1);
						}
					}
					sb.append(tmp);
				}
				postVar = sb.toString();
				if(postVar.contains(YExpression.MINOR) || postVar.contains(YExpression.GREATER) || postVar.contains(YExpression.SINGLEEQUAL) || postVar.contains(YExpression.NOT) || postVar.contains(YExpression.AND) || postVar.contains(YExpression.OR)) {
					postVar = ""+yExpression.booleanEvaluation(postVar, null, null, null);
				}else {
					mathEvaluator.setExpression(postVar);
					try {
						postVar = ""+mathEvaluator.getValue();
					}catch(Exception npe) {
						postVar = YSensorUtilities.nullString;
					}
				}
			}else {
				if(!postVar.startsWith("\"")) {
					if(postVar.equals(YSensorUtilities.idCurr)){
						postVar = caseID;
					}else {
						if(neighbour.getModified().contains(postVar)) {
							
							postVar = YSensorUtilities.preprocessResorce((String) neighbour.getVariables().get(neighbour.getMappingName().get(postVar)), variableFull);
							
						}else {
							
							postVar = YSensorUtilities.preprocessResorce((String) neighbour.getVariables().get(postVar), variableFull);
							
						}
					}
				}else {
					postVar = postVar.substring(1, postVar.length()-1);
				}
			}
			
			String op = "";
			switch(oper) {
				case 1: op = YExpression.MINOREQUAL;
						break;
				case 3: op = YExpression.GREATEREQUAL;
						break;
				case 5: op = YExpression.NOTEQUAL;
						break;
				case 2: op = YExpression.SINGLEEQUAL;
						break;
				case 0: op = YExpression.MINOR;
						break;
				case 4: op = YExpression.GREATER;
						break;
			}
			
			String k = activity+(preVar.startsWith(YExpression.PARTA)?preVar:YExpression.DOT+preVar)+(suffix!=null?YExpression.DOT+suffix:"")+op+postVar;
			if((casesPartial = StateYAWLProcess.cache.get(k)) == null) {
				casesPartial = new HashSet<String>();
				String Version = workflowDefinitionLayer.getVersion(caseID);
				String URI = workflowDefinitionLayer.getURI(caseID);
				Vector<String> workDefIDs = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, URI, Version);
				if(caseInfo) {
					for(String id : workDefIDs) {
						double a = new Double(id);
						double b = new Double(postVar);
						
						YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, id, suffix);
						
					}
				}else {
					LinkedList<String> workDefID = new LinkedList<String>();
					workDefID.addAll(workDefIDs);
					if(!netVariable) { // Analysing Task
						if(!preVar.contains(YExpression.PARTA)) { // Task Variable
							String innerK = activity+YExpression.DOT+preVar;
							HashSet<String> valueInnerK = new HashSet<String>();
							HashSet<String> principal = null;
							if((principal = StateYAWLProcess.cache.get(innerK)) == null) {
								
								if(casesPartial == null) {
									System.out.println("ERROR");
									casesPartial = new HashSet<String>();
								}
								getGenerateCaseIDActivityVariable(activity, oper, preVar, postVar, suffix, casesPartial, workDefID, innerK, valueInnerK);
								
							}else {
								for(String key : principal) {
									LinkedList<String> zzz = StateYAWLProcess.cacheValue.get(key);
									String ID = zzz.getFirst(); 
									String value = zzz.getLast();
									if(suffix!=null) {
										Object o = YSensorUtilities.getValueFromXML(YExpression.DOT+suffix, value);
										
										value = YSensorUtilities.checkIfString(o);

									}else {
										if(value.contains(YExpression.MINOR) && value.contains("</") && value.contains(YExpression.GREATER)) {
											Object o = YSensorUtilities.getValueFromXML("", value);
											
											value = YSensorUtilities.checkIfString(o);
											
										}
									}
									
									YSensorUtilities.populateCaseIDActivityVariable(value, postVar, suffix, oper, ID, casesPartial);
									
								}
							}
						} else { // Task Info
							
							int typeTime = YSensorUtilities.detectActivityType(preVar);

							if(preVar.equals(YSensorUtilities.count)) {
								String innerK = activity+preVar;
								HashSet<String> valueInnerK = new HashSet<String>();
								HashSet<String> principal = null;
								if((principal = StateYAWLProcess.cache.get(innerK)) == null) {
									
									getGenerateCaseIDActivityInfo(activity, oper, preVar, postVar, suffix, casesPartial, workDefIDs, URI, Version, innerK, valueInnerK);
									
								}else {
									for(String key : principal) {
										LinkedList<String> zzz = StateYAWLProcess.cacheValue.get(key);
										String ID = zzz.getFirst(); 
										if(postVar != null) {
											double b = new Double(postVar);
											try {
												double a = new Double(ID);
												
												YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, ID, suffix);
												
											}catch (NumberFormatException nfe) {}
										}else {
											casesPartial = null;
										}
									}
								}
							}else if(preVar.contains(YSensorUtilities.time)) {
								if(preVar.endsWith(YSensorUtilities.time)) {
									try {
										postVar = ""+originalDateFormat.parse(postVar).getTime();
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
								try {
									if(postVar != null) {
										LinkedList<ActivityTuple> time = activityLayer.getRows(null, false, activity, true, workDefID, false, (new Double(postVar)).longValue(), oper, typeTime, true, null, false, false);
										for(ActivityTuple activityID : time) {
											casesPartial.add(activityID.getWorkDefID());
										}
									}else {
										casesPartial = null;
									}
								}catch (NumberFormatException nfe) {}
							}else if(preVar.endsWith(YSensorUtilities.resource)) {
								if(preVar.endsWith(YSensorUtilities.offerResource)) {
																		
									generateResourceInfo(activity, preVar, workDefID, Activity.Enabled, YSensorUtilities.offerResource, offerString, postVar, oper, casesPartial, suffix);
									
								}else if(preVar.endsWith(YSensorUtilities.allocateResource)) {
									
									generateResourceInfo(activity, preVar, workDefID, Activity.Enabled, YSensorUtilities.allocateResource, allocateString, postVar, oper, casesPartial, suffix);
									
								}else if(preVar.endsWith(YSensorUtilities.startResource)) {
									
									generateResourceInfo(activity, preVar, workDefID, Activity.Executing, YSensorUtilities.startResource, startString, postVar, oper, casesPartial, suffix);
									
								} else if(preVar.endsWith(YSensorUtilities.completeResource)) {
									
									generateResourceInfo(activity, preVar, workDefID, Activity.Completed, YSensorUtilities.completeResource, completeString, postVar, oper, casesPartial, suffix);
									
								} 						
							}else {
								Boolean a = null;
								Boolean b = null;
								if((YSensorUtilities.trueString).equalsIgnoreCase(postVar)) {
									b = true;
								}else if((YSensorUtilities.falseString).equalsIgnoreCase(postVar)) {
									b = false;
								}
								if(oper!=2 && oper!=5) b = null; 
								if(suffix!=null) b = null;
								if(b!=null) {
									String innerK = activity+preVar;
									HashSet<String> valueInnerK = new HashSet<String>();
									HashSet<String> principal = null;
									if((principal = StateYAWLProcess.cache.get(innerK)) == null) {
										LinkedList<ActivityTuple> time = activityLayer.getRows(null, false, activity, true, workDefID, false, 0, 0, typeTime, true, null, false, false);
										if(time!=null) {
											for(ActivityTuple activityID : time) {
												String interest = activityID.getWorkDefID()+activity+preVar;
												if(time.size()!=0) {
													a = true;
												}else {
													a = false;
												}
												
												YSensorUtilities.generateCaseCompareBoolean(a, b, oper, casesPartial, activityID.getWorkDefID());

												LinkedList<String> zzz = new LinkedList<String>();
												zzz.add(activityID.getWorkDefID());
												zzz.add(""+a);
												StateYAWLProcess.cacheValue.put(interest, zzz);
												valueInnerK.add(interest);
											}
										}
										StateYAWLProcess.cache.put(innerK, valueInnerK);
									}else {
										for(String key : principal) {
											LinkedList<String> zzz = StateYAWLProcess.cacheValue.get(key);
											String ID = zzz.getFirst(); 
											a = new Boolean(zzz.getLast());
											
											YSensorUtilities.generateCaseCompareBoolean(a, b, oper, casesPartial, ID);
										}
									}
								}else {
									casesPartial = null;
								}
							}
						}
					}
				}
				StateYAWLProcess.cache.put(k, casesPartial);
			}
			if(casesPartial!=null) {
				cases.add(casesPartial);
			}else {
				return null;
			}
		}
		
		return YSensorUtilities.getConsolidatedCases(cases, operator, caseID, workflowDefinitionLayer);
    }
	
	public static void updateQueryVariable2(LinkedList<String> keys, StateYAWLProcess neighbour, StateYAWLProcess currentState) {
		
    	LinkedList<String> funtions = new LinkedList<String>();
    	
    	updateModified(keys, neighbour);
    	
    	updateVariablesValue(neighbour, currentState, funtions);
    	
    	recomputeFunctions(neighbour, currentState, funtions, neighbour.getCaseID());
    	
	}
	
	private static void generateResourceInfo(String activity, String preVar, LinkedList<String> workDefID, int activityType, String action, String eventType, String postVar, int oper, HashSet<String> casesPartial, String suffix) {
		String innerK = activity+preVar;
		HashSet<String> valueInnerK = new HashSet<String>();
		HashSet<String> principal = null;
		if((principal = StateYAWLProcess.cache.get(innerK)) == null) {
			LinkedList<ActivityTuple> taskids = activityLayer.getRows(null, false, activity, true, workDefID, false, 0, 0, activityType, true, null, false, false);
			for(ActivityTuple taskid : taskids){
				String interest = taskid.getWorkDefID()+activity+preVar;
				
				String a = getResource(activity, action, eventType, taskid);
				String b = postVar;
				
				Boolean check = YSensorUtilities.generateCaseCompareResource(a, b, oper, casesPartial, taskid.getWorkDefID(), suffix);
				
				if(check != null) {
					LinkedList<String> zzz = new LinkedList<String>();
					zzz.add(taskid.getWorkDefID());
					zzz.add(a);
					StateYAWLProcess.cacheValue.put(interest, zzz);
					valueInnerK.add(interest);
				}else {
					casesPartial = null;
				}
			}
			StateYAWLProcess.cache.put(innerK, valueInnerK);
		}else {
			for(String key : principal) {
				LinkedList<String> zzz = StateYAWLProcess.cacheValue.get(key);
				String ID = zzz.getFirst(); 
				String a = zzz.getLast();
				Element resEle = JDOMUtil.stringToElement(a);
				a = JDOMUtil.elementToString(resEle.getChild(eventType));
				String b = postVar;
				
				Boolean check = YSensorUtilities.generateCaseCompareResource(a, b, oper, casesPartial, ID, suffix);
				
				if(check == null) {
					casesPartial = null;
				}
			}
		}
	}

	private static String getResource(String activity, String action, String type, ActivityTuple taskid) {
		String a = (String) ysmi.getLogVariableTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+activity+action, taskid.getWorkDefID());
		Element resEle = JDOMUtil.stringToElement(a);
		a = JDOMUtil.elementToString(resEle.getChild(type));
		
		return a;
	}
	
	private static void getGenerateCaseIDActivityVariable(String activity, int oper, String preVar, String postVar, String suffix, HashSet<String> casesPartial, LinkedList<String> workDefID, String innerK, HashSet<String> valueInnerK) {
		LinkedList<ActivityTuple> activityIDs = activityLayer.getRows(null, false, activity, true, workDefID, false, 0, 0, 0, false, null, false, false);
		
		for(ActivityTuple activityID : activityIDs) {
			if(!activityID.getVariableID().equals("")) {
				Vector<String> values = variableLayer.getValues(null, false, preVar, true, 0, 0, 0, null, false, activityID.getVariableID(), true, true);
				if(values!=null) {
					String interest = activityID.getWorkDefID()+activity+values.firstElement();
					
					String value = YSensorUtilities.getVariableValue(values.firstElement(), suffix);
					
					YSensorUtilities.populateCaseIDActivityVariable(value, postVar, suffix, oper, activityID.getWorkDefID(), casesPartial);
					
					LinkedList<String> zzz = new LinkedList<String>();
					zzz.add(activityID.getWorkDefID());
					zzz.add(values.firstElement());
					StateYAWLProcess.cacheValue.put(interest, zzz);
					valueInnerK.add(interest);
				}else {
					casesPartial.clear();
				}
			}
		}
		StateYAWLProcess.cache.put(innerK, valueInnerK);
	}
	
	private static void getGenerateCaseIDActivityInfo(String activity, int oper, String preVar, String postVar, String suffix, HashSet<String> casesPartial, Vector<String> workDefIDs, String URI, String Version, String innerK, HashSet<String> valueInnerK) {
		LinkedList<ActivityTuple> count = activityLayer.getCounts(activity, true, null, false);
		try {
			if(postVar == null) {
				Vector<String> tst = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, URI, Version);
				for(String id : tst) {
					String interest = id+activity+preVar;
					double a = new Double(id);
					
					YSensorUtilities.generateCaseCompareDouble(a, 0.0, oper, casesPartial, id, suffix);
					
					LinkedList<String> zzz = new LinkedList<String>();
					zzz.add(id);
					StateYAWLProcess.cacheValue.put(interest, zzz);
					valueInnerK.add(interest);
				}
			}else if(postVar != null) {										
				double b = new Double(postVar);
				for(ActivityTuple activityID : count) {
					if(workDefIDs.contains(activityID.getWorkDefID())) {
						String interest = activityID.getWorkDefID()+activity+preVar;
						try {										
							double a = new Double(activityID.getCount());
							
							YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.getWorkDefID(), suffix);
							
						}catch (NumberFormatException nfe) {}
						LinkedList<String> zzz = new LinkedList<String>();
						zzz.add(activityID.getWorkDefID());
						StateYAWLProcess.cacheValue.put(interest, zzz);
						valueInnerK.add(interest);
					}
				}
			}
			StateYAWLProcess.cache.put(innerK, valueInnerK);
		}catch (NumberFormatException nfe) {}
	}
	
	private static void updateVariablesValue(StateYAWLProcess neighbour, StateYAWLProcess currentState, LinkedList<String> funtions) {
		for(Entry<String, String> entry : neighbour.getMappingName().entrySet()) {
    		
    		boolean change = checkIfModified(neighbour, entry.getKey());

    		if(change) {
	    		String logTaskItem = entry.getValue();
				String param = null;
				Boolean expression = false;
				if(logTaskItem.contains(YExpression.PARTA) && !logTaskItem.contains(YSensorUtilities.caseCurrent) && !logTaskItem.contains(YSensorUtilities.caseCurrentM)){
										
					param = YStateProcessUtilities.processParam(logTaskItem)[0];
					
					expression = !YSensorUtilities.checkIfInteger(param);
					
				}
				if(logTaskItem.contains(YSensorUtilities.fraudProb)){
					
					funtions.add(entry.getKey());
					
				}else if(expression) {
					LinkedList<String> cases = generateCase2(param, neighbour, currentState);
					if(cases != null) {
						if(logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1).endsWith(YSensorUtilities.countElements)) {
							
							neighbour.getVariables().put(entry.getValue(), ""+cases.size());
							
						}else {
							LinkedList<String> result = new LinkedList<String>();
							if(!logTaskItem.endsWith(YExpression.PARTC)) { //Variable is the value of a task/net variable
								
								String taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.length());
								String oldTaskName = taskName;
						    	LinkedList<ActivityTuple> variablesIDs = activityLayer.getVariablesIDs(null, false, taskName, true, cases, true, 0, false, false);
						    	LinkedList<ActivityTuple> tmp = null;
						    	for(String caseID : cases) {
						    		
						    		tmp = new LinkedList<ActivityTuple>();
						    		
							    	for(ActivityTuple variablesID : variablesIDs) {
							    		if(variablesID.equals(caseID)) {
							    			tmp.add(variablesID);
							    		}
							    	}

						    		HashMap<String, String> variable = new HashMap<String, String>();
							    		
						    		YSensorUtilities.generateVariables(variable, tmp, variableLayer);

						    		String value = YSensorUtilities.generateVariableXML(oldTaskName, variable);
						    		
						    		result.add( (value != null) ? value : YSensorUtilities.nullString);
							    		
						    	}
						    	neighbour.getVariables().put(entry.getValue(), result);
						    	
							}else if(logTaskItem.endsWith(YSensorUtilities.count)) { //Variable is the value of (Count)
								
								String taskName = logTaskItem.substring(logTaskItem.lastIndexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
								LinkedList<ActivityTuple> count = activityLayer.getCounts(taskName, true, cases, true); 
								for(ActivityTuple el : count) {
									result.add(el.getCount());
								}
								neighbour.getVariables().put(entry.getValue(), result);
								
							}else if(logTaskItem.endsWith(YSensorUtilities.time) || logTaskItem.endsWith(YSensorUtilities.timeInMillis) ||logTaskItem.endsWith("ted)")) { //Variable is the value of a time
								
								int typeTime = YSensorUtilities.detectActivityType(logTaskItem);
								
								String taskName = logTaskItem.substring(logTaskItem.lastIndexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
								LinkedList<ActivityTuple> time = activityLayer.getTimes(null, false, taskName, true, cases, true, typeTime, true, null, false, false);
								if(logTaskItem.endsWith(YSensorUtilities.time)) {
									for(ActivityTuple el : time) {
										result.add(originalDateFormat.format(new Date(new Long(el.getTime()))));
									}
								}else if(logTaskItem.endsWith(YSensorUtilities.timeInMillis)) {
									for(ActivityTuple el : time) {
										result.add(el.getTime());
									}
								}else {
									for(int i=0; i<time.size(); i++) {
										result.add(YSensorUtilities.trueString);
									}
								}
								
								neighbour.getVariables().put(entry.getValue(), result);
								
							}else if(!logTaskItem.endsWith(YSensorUtilities.distribution) && !logTaskItem.endsWith(YSensorUtilities.initiator)) { //Variable is the value of a Distributor or Initiator
								
								String taskName = logTaskItem.substring(logTaskItem.lastIndexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
								if(cases.size()>0) {
									LinkedList<ActivityTuple> taskIDs = activityLayer.getIDs(taskName, true, cases, true, 0, false, null, false, false);
									if(taskIDs!=null) {
										for(ActivityTuple taskID : taskIDs) {
											if(taskID!=null) {
												LinkedList<ActivityRoleTuple> resource = activityRoleLayer.getRows(taskID.getTaskID(), true, null, false, 0, 0, 0, false);
												int type = YSensorUtilities.getResourceType(logTaskItem);
												result.add(YSensorUtilities.createResource(roleLayer, taskName, resource, type));
											}else {
												result.add(YSensorUtilities.nullString);
											}
										}
									}
									neighbour.getVariables().put(entry.getValue(), result);
								}else {
									neighbour.getVariables().put(entry.getValue(), null);
								}
								
							}else { //Variable is a Resource 
								if(logTaskItem.contains(YSensorUtilities.resource)) {
									String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
									for(String caseID1 : cases) {
										result.add((String)ysmi.getLogVariableTaskItem(query, caseID1));
									}
									neighbour.getVariables().put(entry.getValue(), result);
								}else {
									String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
									for(String caseID1 : cases) {
										result.add((String) ysmi.getLogVariableTaskItem(query, caseID1));
									}
									neighbour.getVariables().put(entry.getValue(), result);		
								}
							}
						}
					}else {
						neighbour.getVariables().put(entry.getValue(), null);
					}
				}
	    	}	
    	}
	}
	
	private static void recomputeFunctions(StateYAWLProcess neighbour, StateYAWLProcess currentState, LinkedList<String> funtions, String caseID) {
		for(String key : funtions) {
    		String logTaskItem = neighbour.getMappingName().get(key);
    		String param = null;
			if(logTaskItem.contains(YExpression.PARTA) && !logTaskItem.contains(YSensorUtilities.caseCurrent) && !logTaskItem.contains(YSensorUtilities.caseCurrentM)){
				param = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTA), logTaskItem.indexOf(YExpression.PARTC)+1);
				String tmp = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTC)+1);
				while(tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.SINGLEEQUAL) || tmp.startsWith(YExpression.PARTC)) {
					param = param+tmp.substring(0, tmp.indexOf(YExpression.PARTC)+1);
					tmp = tmp.substring(tmp.indexOf(YExpression.PARTC)+1);
				}
			}
	    	if(logTaskItem.contains(YSensorUtilities.fraudProb)){ //Variable is the value of probabilityFunction
				String tmp = logTaskItem.substring(logTaskItem.indexOf(YSensorUtilities.fraudProb)+23);
				String paramCurrCount = tmp.substring(0, tmp.indexOf(commaString));
				tmp = tmp.substring(tmp.indexOf(commaSpaceString)+2);
				String paramMaxNumberExec = tmp.substring(0, tmp.indexOf(commaString));
				tmp = tmp.substring(tmp.indexOf(commaSpaceString)+2);
				String paramGroupingElement = tmp.substring(0, tmp.indexOf(commaString));
				tmp = tmp.substring(tmp.indexOf(commaSpaceString)+2);
				String paramWindowElement = tmp.substring(0, tmp.indexOf(commaString));
				tmp = tmp.substring(tmp.indexOf(commaSpaceString)+2);
				String paramWindow = tmp.substring(0, tmp.length()-1);
				boolean stop = false;
				if(converParam(caseID, paramWindow, neighbour)==null) {
					stop = true;
				}
				if(converParam(caseID, paramMaxNumberExec, neighbour)==null) {
					stop = true;
				}
				if(converParam(caseID, paramCurrCount, neighbour)==null) {
					stop = true;
				}
				if(!stop) {
					int window = new Integer(converParam(caseID, paramWindow, neighbour)); 
					int limit = new Integer(converParam(caseID, paramMaxNumberExec, neighbour));
					int currentCount = new Integer(converParam(caseID, paramCurrCount, neighbour));
					LinkedList<String> cases = generateCase2(param, neighbour, currentState);
					HashMap<String, String> time = new HashMap<String, String>();
					HashMap<String, HashSet<String>> customers = new HashMap<String, HashSet<String>>();
					String groupingElement = converParam(caseID, paramGroupingElement, neighbour);
					String pre = groupingElement.substring(0, groupingElement.indexOf(YExpression.DOT)+1);
					groupingElement = groupingElement.substring(groupingElement.indexOf(YExpression.DOT)+1);
					String mid = groupingElement.substring(0, groupingElement.indexOf(YExpression.DOT));
					String query = pre+mid;
					String query2 = converParam(caseID, paramWindowElement, neighbour);
					for(String caseInternalID : cases) {
						String s = (String) getLogVariableTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+query, caseInternalID);
						s = (String) YSensorUtilities.getValueFromXML(YExpression.DOT+groupingElement, s);
						HashSet<String> set = null;
						if((set = customers.get(s)) != null) {
							set.add(caseInternalID);
						}else {
							set = new HashSet<String>();
							set.add(caseInternalID);
							customers.put(s, set);
						}
						time.put(caseInternalID, (String) getLogVariableTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+query2, caseInternalID));
					}
					int sameExec = 0;
					int limExec = 0;
					int last = -1;
					for(String customer : customers.keySet()) {
						HashSet<String> caseIDs = customers.get(customer);
						long[] times = new long[caseIDs.size()];
						int j = 0;
						for(String caseInternalID : caseIDs) {
							try {
								if(time.get(caseInternalID)!=null) {
									times[j] = new Long(time.get(caseInternalID));
									j++;
								}else {
									j++;
								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
							
						}
						Arrays.sort(times);
						int count = 0;
						for(int i = 0; i<times.length; i++) {
							long t = times[i];
							if(t > 0) {
								if(count<=1) {
									count = 0;
								}else {
									count--;
								}
								for(j = i+count; j<times.length; j++) {
									if(times[j]<=(t+window)) {
										count++;
									}else {
										if(i == 0) {
											last = j;
											int tmpCount = count;
											while(last > 0 || (last == 0 && count == 1)) {
												tmpCount--;
												if(tmpCount>currentCount) {
													sameExec++;
												}
												if(tmpCount>limit) {
													limExec++;
												}
												last--;
											}
										}
										break;
									}
								}
								if(count>currentCount) {
									sameExec++;
								}
								if(count>limit) {
									limExec++;
								}
							}
						}
					}
			    	neighbour.getVariables().put(neighbour.getMappingName().get(key), ""+(double)limExec/sameExec);
				}else {
					neighbour.getVariables().put(neighbour.getMappingName().get(key), null);
				}
			}
    	}
	}
	
	private static String converParam(String WorkDefID, String tmp, StateYAWLProcess neighbour) {
    	if(!tmp.startsWith("\"")) {
			try {
				new Double(tmp);
			}catch(NumberFormatException nfe) {
				if(tmp.equals(YSensorUtilities.idCurr)){
					tmp = WorkDefID;
				}else {
					Object mapN = null;
					if((mapN = neighbour.getMappingName().get(tmp)) != null) {
						tmp = (String) neighbour.getVariables().get(mapN);
						if(tmp!=null && tmp.contains(YExpression.GREATER) && tmp.contains(YExpression.MINOR)) {
							String start = tmp.substring(0, tmp.indexOf(YExpression.GREATER)+1);
							String end = tmp.substring(tmp.length()-start.length()-1);
							if(start.substring(1).equals(end.substring(2))) {
								tmp = tmp.substring(start.length(), tmp.length()-end.length());
							}
						}
					}else {
						mathEvaluator.setExpression(tmp);
						try {
							tmp = ""+mathEvaluator.getValue().intValue();
						} catch (Exception e) {
							tmp = YSensorUtilities.nullString;
						}
					}
				}
			}
		}else {
			tmp = tmp.substring(1, tmp.length()-1);
		}
    	return tmp;
    }
	
	private static Object getLogVariableTaskItem(String logTaskItem, String WorkDefID) {
		Boolean expression = false;
		if(!expression) { //Specific Case
						
			String taskName = null;
			if(logTaskItem.endsWith(YExpression.PARTC)) {
				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
			}else {
				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.length());
			}
			if(logTaskItem.contains(workflowDefinitionLayer.getURI(WorkDefID))) {
				String Name = workflowDefinitionLayer.getNames(WorkDefID, true, 0, 0, 0, false, null, null).firstElement();
				if(logTaskItem.endsWith(YSensorUtilities.startTime)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, false, WorkDefID, true, SubProcess.Start, true, null, false, false);
	    			if(time!=null){
		    			return originalDateFormat.format(new Date(new Long(time.firstElement())));
	    			}else return null;
	    		} else if(logTaskItem.endsWith(YSensorUtilities.isStarted)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, false, WorkDefID, true, SubProcess.Start, true, null, false, false);
	    			if(time!=null){ return YSensorUtilities.trueString;
	    			}else return YSensorUtilities.falseString;
	    		} else if(logTaskItem.endsWith(YSensorUtilities.completeTime)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, false, WorkDefID, true, SubProcess.Complete, true, null, false, false);
	    			if(time!=null){
		    			return originalDateFormat.format(new Date(new Long(time.firstElement())));
	    			}else return null;
	    		} else if(logTaskItem.endsWith(YSensorUtilities.isCompleted)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, false, WorkDefID, true, SubProcess.Complete, true, null, false, false);
	    			if(time!=null){ return YSensorUtilities.trueString;
	    			}else return YSensorUtilities.falseString;
	    		}
	    	}

			boolean netVariable = false;
			
			String oldTaskName = taskName.replace(' ', '_');
			
			if(!netVariable) { //Analyse Task
				if(!logTaskItem.endsWith(YExpression.PARTC)) { //Activity Variable
					
					return YSensorUtilities.getLogVariableActivityVariable(activityLayer, variableLayer, taskName.substring(0, taskName.indexOf(YExpression.DOT)), oldTaskName, WorkDefID);
					
				}else { //Activity information (Status, Time...)
					
					return YSensorUtilities.getLogVariableActivityInformation(activityLayer, logTaskItem, taskName, WorkDefID);
					
				}
			}else { //Analyse Net
				if(!logTaskItem.endsWith(YExpression.PARTC)) { //SubProcess Variable
					
					return YSensorUtilities.getLogVariableNetVariable(subProcessLayer, variableLayer, taskName, WorkDefID);
					
				}else { //SubProcess Information (Status, Time...)
					
					return YSensorUtilities.getLogVariableNetInformation(subProcessLayer, logTaskItem, taskName, WorkDefID);
					
				}
			}
		}
		return null;
	}
	
	public static LinkedList<String[]> getResourceAllocatedTask(String resource){
		Element e = JDOMUtil.stringToDocument(resource).getRootElement();
		return roleLayer.getTasksAllocated(e.getAttributeValue(xmlID));
	}
	
	public static LinkedList<String[]> getResourceStartedTask(String resource){
		Element e = JDOMUtil.stringToDocument(resource).getRootElement();
		return roleLayer.getTasksStarted(e.getAttributeValue(xmlID));
	}
}
