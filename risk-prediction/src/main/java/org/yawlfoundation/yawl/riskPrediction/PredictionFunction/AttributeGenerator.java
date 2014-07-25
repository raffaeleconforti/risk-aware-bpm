package org.yawlfoundation.yawl.riskPrediction.PredictionFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom.Element;
import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TaskExecutionAnnotation;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TimeBetweenTasksAnnotation;
import org.yawlfoundation.yawl.riskPrediction.Annotators.ProcessTimeExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TaskExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TaskTimeExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TimeBetweenTasksAnnotator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPoint;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.YAWL_DecisionPointDiscover;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRoleTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.util.JDOMUtil;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

public class AttributeGenerator {
		
	private HashMap<String, HashMap<String, Object>> variableValues = null;
	private HashMap<String, HashMap<String, Long>> variableValuesTimes = null;
	private HashMap<String, Class> variableClass = null;
	
	private HashMap<String, HashMap<String, Object>> variableValuesPostOffer = null;
	private HashMap<String, HashMap<String, Long>> variableValuesTimesPostOffer = null;
	private HashMap<String, Class> variableClassPostOffer = null;
	
	private HashMap<String, HashSet<String>> stringNominal = null;
	private HashMap<String, HashSet<String>> all = null;
	private HashMap<String, ArrayList<String>> stringNominalList = null;
	
	private final DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public AttributeGenerator() {
		
		variableValues = new HashMap<String, HashMap<String, Object>>();
		variableValuesTimes = new HashMap<String, HashMap<String, Long>>();
		variableClass = new HashMap<String, Class>();
		
		variableValuesPostOffer = new HashMap<String, HashMap<String, Object>>();
		variableValuesTimesPostOffer = new HashMap<String, HashMap<String, Long>>();
		variableClassPostOffer = new HashMap<String, Class>();
		
		stringNominal = new HashMap<String, HashSet<String>>();
		all = new HashMap<String, HashSet<String>>();
		stringNominalList = new HashMap<String, ArrayList<String>>();
		
	}
	
	public HashMap<String, ArrayList<String>> getStringNominal() {
		return stringNominalList;
	}

	public static void main(String[] args) {
		
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/MXMLlogs/750/tmp.xes"); //"/home/stormfire/Log.xes");//

			String specification = null;
			try {
				File f = new File("/home/stormfire/CarrierAppointment4.yawl");//
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				specification = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			String resources = null;
			try {
				File f = new File("/home/stormfire/orderfulfillment.ybkp");
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				resources = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);

			InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);
			
			YAWL_DecisionPointDiscover a = new YAWL_DecisionPointDiscover();
			a.discoverDecisionPoints(specification);
			
			TaskExecutionAnnotator tea = new TaskExecutionAnnotator(im);
			TaskTimeExecutionAnnotator ttea = new TaskTimeExecutionAnnotator(im);
			TimeBetweenTasksAnnotator tbta = new TimeBetweenTasksAnnotator(im, a.getDecisionPoints());
			ProcessTimeExecutionAnnotator ptea = new ProcessTimeExecutionAnnotator(im);
			
			tea.annotateLog(null);
			tbta.annotateLog(null);
			
			AttributeGenerator ag = new AttributeGenerator();
			ag.generateAttribute(im, a.getDecisionPoints().getLast(), tea, tbta, null, null, null, null, 0);
			
			long totalTime = 0;
			HashMap<String, Long> thm = new HashMap<String, Long>();
			
			String id = null;
			long idTime = 0;
			
			for(Entry<String, HashMap<String, Object>> entry : ag.getAttributes().entrySet()) {
				
				id = entry.getKey();
				
				idTime = 0;
				HashMap<String, Long> hm = ttea.getTaskTimeAnnotations().getCaseAnnotations(id);
				
				for(Entry<String, Long> entry2 : hm.entrySet()) {
					idTime += entry2.getValue();
				}
				
				thm.put(id, idTime);
				totalTime += idTime;
				
			}
			
			double avgTime = totalTime/ag.getAttributes().size(); 
			
//			System.out.println(avgTime);
			
//			System.out.println(ag.getAttributes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void generateAttribute(InterfaceManager im, DecisionPoint decisionPoint, TaskExecutionAnnotator teAnnotator, TimeBetweenTasksAnnotator tbtAnnotator, LinkedList<String> idList, String nextActivityName, String newResource, String data, long currTme) {
		
		Activity activityLayer = im.getActivityLayer();
		ActivityRole activityRoleLayer = im.getActivityRoleLayer();
		Variable variableLayer = im.getVariableLayer();
		
		if(idList == null) {
			idList = generateListID(activityLayer, decisionPoint);
		}
		
		TaskExecutionAnnotation tea = teAnnotator.getTaskAnnotations();
		
		TimeBetweenTasksAnnotation tbta = tbtAnnotator.getTimeBetweenTasksAnnotations();

		HashMap<String, Object> variableValue = null;
		HashMap<String, Long> variableValueTime = null;
		HashMap<String, Object> variableValuePostOffer = null;
		HashMap<String, Long> variableValueTimePostOffer = null;

		ConcurrentLinkedQueue<String> l = null;
		LinkedList<ActivityTuple> activityRow = null;
		HashSet<String> visitedTask = null;
		
		String workDefId = null;
		String task = null;
		String eventName = null;
		String eventType = null;
		
		Vector<Vector<String>> variableRow = null;
		
		for(String activityName : decisionPoint.getPredecessors()) {
			
			{
				
				l = new ConcurrentLinkedQueue<String>();
				activityRow = activityLayer.getRows(null, false, activityName, true, idList, true, 0, 0, 0, false, null, false, false);
				visitedTask = new HashSet<String>();
				
				if(activityRow != null) {
					
					for(ActivityTuple singleCase : activityRow) {
						workDefId = singleCase.getWorkDefID();
						task = singleCase.getTaskID();
						eventName = singleCase.getName();
						eventType = singleCase.getStatus();
						
						if(!variableValues.containsKey(workDefId)) {
							
							variableValue = new HashMap<String, Object>();
							variableValues.put(workDefId, variableValue);
							
							variableValueTime = new HashMap<String, Long>();
							variableValuesTimes.put(workDefId, variableValueTime);
							
						}else {
							
							variableValue = variableValues.get(workDefId);
							variableValueTime = variableValuesTimes.get(workDefId);
							
						}
						
						l.add("Event_"+eventName+eventType);
						
						if(!visitedTask.contains(task)) {
							
							//Insert OutputVariables
							variableRow = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 2, task, true, null, false, true);
							
							if(variableRow != null) {
								
								for(Vector<String> variable : variableRow) {
									
									String variableName = variable.get(1);
									String value = variable.get(2);
									long time = Long.parseLong(variable.get(5));
									
									Class c = getClass(value);
									
									if(c == String.class) {
										
//										convertComplexTypes("VariableOutput_"+eventName, variableName, variableValue, value, 1);
										convertComplexTypes("VariableOutput_", variableName, variableValue, variableValueTime, value, 1, time);
										
									}else {
									
//										insert("VariableOutput_"+eventName+variableName, variableValue, value, c);
										insert("VariableOutput_"+variableName, variableValue, variableValueTime, value, c, time);
										
									}
									
//									l.add("VariableOutput_"+eventName+variableName);
									l.add("VariableOutput_"+variableName);
									
								}
							}
							
							//Insert Resources		
							LinkedList<ActivityRoleTuple> resourceRow = activityRoleLayer.getRows(task, true, null, false, 0, 0, 0, false);
	
							if(resourceRow != null) {
								for(ActivityRoleTuple resource : resourceRow) {
									
									String resourceName = resource.getRoleID();
									String resourceEvent = resource.getStatus();
									
									if("complete".equals(resourceEvent)) {
										
										insert("Resource_"+eventName+resourceEvent, variableValue, new Resource(resourceName), Resource.class);
										
									}
	
									l.add("Resource_"+eventName+resourceEvent);
		
								}
							}
							
							
							
							visitedTask.add(task);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		for(String activityName : decisionPoint.getAll()) {
			
			activityRow = activityLayer.getRows(null, false, activityName, true, idList, true, 0, 0, 0, false, null, false, false);
			
			if(activityRow != null) {
				for(ActivityTuple singleCase : activityRow) {
					String id = singleCase.getWorkDefID();
					
					if(!variableValues.containsKey(id)) {
						variableValue = new HashMap<String, Object>();
						variableValues.put(id, variableValue);
					}
					
				}
				
			}
			
		}
		
		for(String activityName : decisionPoint.getPredecessors()) {
			
			for(Entry<String, HashMap<String, Object>> entry : variableValues.entrySet()) {
				
				variableValue = entry.getValue();
				
				//Insert number executions
				if(tea.getTaskAnnotation(entry.getKey(), activityName) != null) {
				
					insert("NumberExecutions_"+activityName, variableValue, tea.getTaskAnnotation(entry.getKey(), activityName), Long.class);
					
				}
				
			}
			
		}
		
		for(String activityName : decisionPoint.getDirectSuccessors()) {

			String id = null;
			
			for(Entry<String, HashMap<String, Object>> entry : variableValues.entrySet()) {
				
				variableValue = entry.getValue();
				id = entry.getKey();

				//Insert number executions
				if(tea.getTaskAnnotation(id, activityName) != null) {
									
					long numberExecution = tea.getTaskAnnotation(id, activityName);
					
					//Create instance to evaluate
					if(nextActivityName != null && activityName.equals(nextActivityName)) {
						numberExecution++;
						
						for(String firstActivityName : decisionPoint.getOriginator()) {//decisionPoint.getPredecessors()) {
							
							LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, firstActivityName, true, idList, true, Activity.Completed, true, null, false, false);
							
							if(v1 != null && v1.getFirst() != null && v1.getFirst().getTime() != null) {
								
								Long t1 = Long.parseLong(v1.get(0).getTime());
								
								insert("TimeBetween_" + firstActivityName + "_" + activityName, variableValue, (currTme-t1), Long.class);
								
							}
								
						}
						
						//Insert Resources			
						if(newResource != null) {
						
							System.out.println("test");
							//Create instance to evaluate
							insert("Resource_"+activityName+"complete", variableValue, new Resource(newResource), Resource.class);
							
							
						}
						
					}else if(nextActivityName == null) {
						
						HashMap<String, Long> hm = null;
							
						for(String firstActivityName : decisionPoint.getOriginator()) {//decisionPoint.getPredecessors()) {
							
							if((hm = tbta.getFirstTaskAnnotations(id, firstActivityName)) != null) {
							
								if(hm.containsKey(activityName)) {
									
									insert("TimeBetween_" + firstActivityName + "_" + activityName, variableValue, hm.get(activityName), Long.class);
								
								}
								
							}
							
						}
						
						//Insert Resources			
						if(newResource == null && tea.getTaskAnnotation(id, activityName) > 0) {
						
							String taskID = (id+":"+activityName);
							LinkedList<ActivityRoleTuple> resourceRow = activityRoleLayer.getRows(taskID, true, null, false, 0, 0, 0, false);
							
							for(ActivityRoleTuple resource : resourceRow) {
								
								String resourceName = resource.getRoleID();
								String resourceEvent = resource.getStatus();
								
								if(resourceName != null) {
									
									if("complete".equals(resourceEvent)) {
										insertPostOffer("Resource_"+activityName+resourceEvent, variableValue, new Resource(resourceName), Resource.class);
									}
									
								}
								
							}
							
						}
						
					}
					
					insert("NumberExecutions_"+activityName, variableValue, numberExecution, Long.class);
					
				}
				
			}
			
		}
		
		HashSet<String> parallel = (HashSet<String>) decisionPoint.getAll().clone();
		parallel.removeAll(decisionPoint.getTotalSuccessors());
		
		String id = null;
		
		for(String activityName : parallel) {
			
			for(Entry<String, HashMap<String, Object>> entry : variableValues.entrySet()) {
				
				variableValue = entry.getValue();
				id = entry.getKey();
				
				//Insert number executions
				if(tea.getTaskAnnotation(id, activityName) != null) {
				
					long numberExecution = tea.getTaskAnnotation(id, activityName);
										
					insert("NumberExecutions_"+activityName, variableValue, numberExecution, Long.class);
					
				}
				
			}
			
		}
		
		if(decisionPoint.getType() == DecisionPoint.TASK) {
			
			activityRow = activityLayer.getRows(null, false, decisionPoint.getName(), true, idList, true, 0, 0, 0, false, null, false, false);
			
			if(activityRow != null) {
				
				workDefId = null;
				task = null;
				eventName = null;
				
				for(ActivityTuple singleCase : activityRow) {
					workDefId = singleCase.getWorkDefID();
					task = singleCase.getTaskID();
					eventName = singleCase.getName();
					
					if(!variableValuesPostOffer.containsKey(workDefId)) {
						variableValuePostOffer = new HashMap<String, Object>();
						variableValuesPostOffer.put(workDefId, variableValuePostOffer);

						variableValueTimePostOffer = new HashMap<String, Long>();
						variableValuesTimesPostOffer.put(workDefId, variableValueTimePostOffer);
					}else {
						variableValuePostOffer = variableValuesPostOffer.get(workDefId);
						variableValueTimePostOffer = variableValuesTimesPostOffer.get(workDefId);
					}
					
					if(!variableValues.containsKey(workDefId)) {
						variableValue = new HashMap<String, Object>();
						variableValues.put(workDefId, variableValue);
						
						variableValueTime = new HashMap<String, Long>();
						variableValuesTimes.put(workDefId, variableValueTime);
					}else {
						variableValue = variableValues.get(workDefId);
						variableValueTime = variableValuesTimes.get(workDefId);
					}
					
					//Insert OutputVariables
					variableRow = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 2, task, true, null, false, true);
					
					if(variableRow != null) {
						
						for(Vector<String> variable : variableRow) {
							
							String variableName = variable.get(1);
							String value = variable.get(2);
							long time = Long.parseLong(variable.get(5));
							
							Class c = getClass(value);
							
							if(c == String.class) {
								
//								convertComplexTypes("VariableOutput_"+eventName, variableName, variableValuePostOffer, value, 1);
								convertComplexTypes("VariableOutput_", variableName, variableValuePostOffer, variableValueTimePostOffer, value, 1, time);
								
							}else {
							
//								insertPostOffer("VariableOutput_"+eventName+variableName, variableValuePostOffer, value, c);
								insertPostOffer("VariableOutput_"+variableName, variableValuePostOffer, variableValueTimePostOffer, value, c, time);
								
							}
							
						}
					}
					
					//Insert Resources			
					if(newResource == null) {
					
						LinkedList<ActivityRoleTuple> resourceRow = activityRoleLayer.getRows(task, true, null, false, 0, 0, 0, false);
						
						for(ActivityRoleTuple resource : resourceRow) {
							
							String resourceName = resource.getRoleID();
							String resourceEvent = resource.getStatus();
							
							if(resourceName != null) {
								
								if("complete".equals(resourceEvent)) {
									insertPostOffer("Resource_"+eventName+resourceEvent, variableValuePostOffer, new Resource(resourceName), Resource.class);
								}
								
							}
							
						}
						
					}else {
						
						//Create instance to evaluate
						insert("Resource_"+eventName+"complete", variableValue, new Resource(newResource), Resource.class); //TODO possible error
						insert("Resource_"+eventName+"complete", variableValuePostOffer, new Resource(newResource), Resource.class); //TODO possible error
						
						
					}
					
				}
				
			}
			
			for(int i = 0; i < idList.size(); i++) {
				
				id = idList.get(i);
				
				String activityName = decisionPoint.getName();
				
				if(!variableValuesPostOffer.containsKey(id)) {
					variableValuePostOffer = new HashMap<String, Object>();
					variableValuesPostOffer.put(id, variableValuePostOffer);
				}else {
					variableValuePostOffer = variableValuesPostOffer.get(id);
				}
				
				//Insert number executions
				if(tea.getTaskAnnotation(id, activityName) != null) {
				
					insertPostOffer("NumberExecutions_"+activityName, variableValuePostOffer, tea.getTaskAnnotation(id, activityName), Long.class);
					
				}
				
				//Insert time between tasks
				HashMap<String, Long> hm = null;
				
				for(String firstActivityName : decisionPoint.getOriginator()) {//decisionPoint.getPredecessors()) {
					
					if((hm = tbta.getFirstTaskAnnotations(id, firstActivityName)) != null) {
					
						if(hm.containsKey(activityName)) {
							
							insert("TimeBetween_" + firstActivityName + "_" + activityName, variableValue, hm.get(activityName), Long.class);
						
						}
						
					}
					
				}
				
			}
			
		}else {
			
		}
		
		if(data != null) {
			Element elData = JDOMUtil.stringToElement(data);
			if(elData != null) {
				for(Element subData : (List<Element>) elData.getChildren()) {
					String variableName = subData.getName();
					String value = JDOMUtil.elementToString(subData);
					long time = currTme;
					
					Class c = getClass(value);
					if(c == String.class) {
						convertComplexTypes("VariableOutput_", variableName, variableValuePostOffer, variableValueTimePostOffer, value, 1, time);
					}else {
						insertPostOffer("VariableOutput_"+variableName, variableValuePostOffer, variableValueTimePostOffer, value, c, time);
					}
				}
			}
		}
		
		convertSet();
		
	}
	
	public void updateAttribute(InterfaceManager im, DecisionPoint decisionPoint, TaskExecutionAnnotator teAnnotator, LinkedList<String> idList, String nextActivityName, String newResource, long currTme) {
		
		Activity activityLayer = im.getActivityLayer();
		
		if(idList == null) {
			idList = generateListID(activityLayer, decisionPoint);
		}
		
		TaskExecutionAnnotation tea = teAnnotator.getTaskAnnotations();

		HashMap<String, Object> variableValue = null;
		HashMap<String, Long> variableValueTime = null;
		HashMap<String, Object> variableValuePostOffer = null;
		HashMap<String, Long> variableValueTimePostOffer = null;
		
		for(String activityName : decisionPoint.getDirectSuccessors()) {

//			System.out.println(activityName);
			
			for(String id : idList) {
				
				if(!variableValues.containsKey(id)) {
					
					variableValue = new HashMap<String, Object>();
					variableValues.put(id, variableValue);
					
					variableValueTime = new HashMap<String, Long>();
					variableValuesTimes.put(id, variableValueTime);
					
				}else {
					
					variableValue = variableValues.get(id);
					variableValueTime = variableValuesTimes.get(id);
					
				}
//				
//				variableValue = variableValues.get(id);
				
				//Insert number executions
//				System.out.println(id);
				if(tea.getTaskAnnotation(id, activityName) != null) {
				
					long numberExecution = tea.getTaskAnnotation(id, activityName);
					
					//Create instance to evaluate
					if(nextActivityName != null && activityName.equals(nextActivityName)) {
						numberExecution++;
						
						for(String firstActivityName :  decisionPoint.getOriginator()) {//decisionPoint.getPredecessors()) {
							
							LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, firstActivityName, true, idList, true, Activity.Completed, true, null, false, false);
							
							if(v1 != null && v1.getFirst() != null && v1.getFirst().getTime() != null) {
								
								Long t1 = Long.parseLong(v1.get(0).getTime());
								
								insert("TimeBetween_" + firstActivityName + "_" + activityName, variableValue, (currTme-t1), Long.class);
								
							}
								
						}
						
						//Insert Resources			
						if(newResource != null) {
						
							//Create instance to evaluate
							insert("Resource_"+activityName+"complete", variableValue, new Resource(newResource), Resource.class);
							
						}
						
					}
					
					insert("NumberExecutions_"+activityName, variableValue, numberExecution, Long.class);
					
				}else if(activityName.equals(nextActivityName)){
										
					for(String firstActivityName :  decisionPoint.getOriginator()) {//decisionPoint.getPredecessors()) {
						
						LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, firstActivityName, true, idList, true, Activity.Completed, true, null, false, false);
						
						if(v1 != null) {
							
							Long t1 = Long.parseLong(v1.get(0).getTime());
							
							insert("TimeBetween_" + firstActivityName + "_" + nextActivityName, variableValue, (currTme-t1), Long.class);
							
						}
							
					}
					
					//Insert Resources			
					if(newResource != null) {
					
						//Create instance to evaluate
						insert("Resource_"+nextActivityName+"complete", variableValue, new Resource(newResource), Resource.class);
						
					}
				
					insert("NumberExecutions_"+nextActivityName, variableValue, 1L, Long.class);
				}
				
			}
			
		}
		
		if(decisionPoint.getType() == DecisionPoint.TASK) {
			
			LinkedList<ActivityTuple> activityRow = activityLayer.getRows(null, false, decisionPoint.getName(), true, idList, true, 0, 0, 0, false, null, false, false);
			
			if(activityRow != null) {
				
				for(ActivityTuple singleCase : activityRow) {
					String id = singleCase.getWorkDefID();
					String eventName = singleCase.getName();
					
					if(!variableValuesPostOffer.containsKey(id)) {
						variableValuePostOffer = new HashMap<String, Object>();
						variableValuesPostOffer.put(id, variableValuePostOffer);

						variableValueTimePostOffer = new HashMap<String, Long>();
						variableValuesTimesPostOffer.put(id, variableValueTimePostOffer);
					}else {
						variableValuePostOffer = variableValuesPostOffer.get(id);
						variableValueTimePostOffer = variableValuesTimesPostOffer.get(id);
					}
					
					if(!variableValues.containsKey(id)) {
						variableValue = new HashMap<String, Object>();
						variableValues.put(id, variableValue);
						
						variableValueTime = new HashMap<String, Long>();
						variableValuesTimes.put(id, variableValueTime);
					}else {
						variableValue = variableValues.get(id);
						variableValueTime = variableValuesTimes.get(id);
					}
					
					//Insert Resources			
					if(newResource != null) {
						
						//Create instance to evaluate
						insert("Resource_"+eventName+"complete", variableValue, new Resource(newResource), Resource.class);
						
					}
					
				}
				
			}
						
		}
		
		convertSet();
		
	}
	
	private void convertSet() {
		for(Entry<String, HashSet<String>> entry : stringNominal.entrySet()) {
			stringNominalList.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
		
	}

	private void convertComplexTypes(String prefix, String variableName, HashMap<String, Object> variableValue, String value, int counter) {
		
		if('<' == value.charAt(0)) {
			
			Element e = JDOMUtil.stringToElement(value);
			
			if(e.getChildren().size() == 0) insert(prefix+e.getName()+counter, variableValue, e.getValue(), getClass(e.getValue()));
			
			int pos = 1;
			for(Element e1 : (List<Element>) e.getChildren()) {
				
				convertComplexTypes(prefix, e1.getName(), variableValue, JDOMUtil.elementToString(e1), pos);
				pos++;
				
			}
			
		}else {
			
			insert(prefix+variableName, variableValue, value, getClass(value));
			
		}
		
	}
	
	private void convertComplexTypes(String prefix, String variableName, HashMap<String, Object> variableValue, HashMap<String, Long> variableValueTime, String value, int counter, long time) {
		
		if('<' == value.charAt(0)) {
			
			Element e = JDOMUtil.stringToElement(value);
			
			if(e.getChildren().size() == 0) insert(prefix+e.getName()+counter, variableValue, e.getValue(), getClass(e.getValue()));
			
			int pos = 1;
			for(Element e1 : (List<Element>) e.getChildren()) {
				
				convertComplexTypes(prefix, e1.getName(), variableValue, variableValueTime, JDOMUtil.elementToString(e1), pos, time);
				pos++;
				
			}
			
		}else {
			
			insert(prefix+variableName, variableValue, variableValueTime, value, getClass(value), time);
			
		}
		
	}

	private LinkedList<String> generateListID(Activity activityLayer, DecisionPoint decisionPoint) {
		
		LinkedList<ActivityTuple> ids = activityLayer.getWorkDefIDs(null, false, decisionPoint.getName(), true, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		
		if(ids != null) {
			
			for(ActivityTuple at : ids) {
				idList.add(at.getWorkDefID());
			}
			
		}else {
			
			HashSet<String> idSet = new HashSet<String>();
			LinkedList<ActivityTuple> idtmp = null;
			
			for(String succ : decisionPoint.getDirectSuccessors()) {
				 
				idtmp = activityLayer.getWorkDefIDs(null, false, succ, true, 0, false, null, false);
				 
				if(idtmp != null) {
					
					for(ActivityTuple at : idtmp) {
						idSet.add(at.getWorkDefID());
					}
					
				}
				
			}
			
			idList.addAll(idSet);
			
		}

		return idList;
		
	}
	
	private Class getClass(String value) {
		if(value.equals("null")) return Integer.class;
		try {
			Long.valueOf(value);
			return Long.class;
		}catch (NumberFormatException nfe1) {
			try {
				Double.valueOf(value);
				return Double.class;
			}catch(NumberFormatException nfe) {
				if(value.length() != 28) {
					if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
						return Boolean.class;
					}else {
						return String.class;
					}
				}else {
					try {
						logDateFormat.parse(value);
						return Date.class;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
							return Boolean.class;
						}else {
							return String.class;
						}
					}
				}
			}
		}
	}
	
	public HashMap<String, HashMap<String, Object>> getAttributes() {
		return variableValues;
	}
	
	public HashMap<String, HashMap<String, Object>> getAttributesPostOffer() {
		
		HashMap<String, HashMap<String, Object>> map = new HashMap<String, HashMap<String,Object>>();
		
		map.putAll(variableValues);
		map.putAll(variableValuesPostOffer);
		
		return map;
	}
	
	public HashMap<String, Class> getAttributesClasses() {
		return variableClass;
	}
	
	public HashMap<String, Class> getAttributesClassesPostOffer() {
		
		HashMap<String, Class> map = new HashMap<String, Class>();
		
		map.putAll(variableClass);
		map.putAll(variableClassPostOffer);
		
		return map;
	}
	
	private void insert(String key, HashMap<String, Object> variableValue, Object annotation, Class c) {
		
		Object o = annotation;
		
		if(annotation instanceof String) {
			if(c == Long.class) o = Long.valueOf((String)annotation);
			else if(c == Double.class) o = Double.valueOf((String)annotation);
			else if(c == Boolean.class) o = Boolean.valueOf((String)annotation);
			else if(c == Date.class) {
				try {
					o = logDateFormat.parse((String)annotation);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else o = annotation;		
		}
		
		if(annotation != null) {
		
			variableValue.put(key, o);
			
			if(c == String.class) {
				HashSet<String> set;
				if((set = stringNominal.get(key)) == null) {
					 set = new HashSet<String>();
					 stringNominal.put(key, set);
				}
			
				set.add((String) annotation);
			}
			
			HashSet<String> set;
			if((set = all.get(key)) == null) {
				 set = new HashSet<String>();
				 all.put(key, set);
			}
		
			set.add(annotation.toString());
			
			if(!variableClass.keySet().contains(key) || (variableClass.get(key) != c) && c != Integer.class) {
				variableClass.put(key, c);
//				staticVariableClass.put(key, c);
			}else {
				if(c == String.class) variableClass.put(key, c);
				stringNominal.put(key, all.get(key));
			}
			
		}else {
//			System.out.println(key);
			throw new RuntimeException();
		}
		
	}
	
	private void insert(String key, HashMap<String, Object> variableValue, HashMap<String, Long> variableValueTime, Object annotation, Class c, long time) {
		
		Long oldTime = null;
		if((oldTime = variableValueTime.get(key)) != null) {
			if(time < oldTime) return;
		}else {
			variableValueTime.put(key, time);
		}
		
		Object o = annotation;
		
		if(annotation instanceof String) {
			if(c == Long.class) o = Long.valueOf((String)annotation);
			else if(c == Double.class) o = Double.valueOf((String)annotation);
			else if(c == Boolean.class) o = Boolean.valueOf((String)annotation);
			else if(c == Date.class) {
				try {
					o = logDateFormat.parse((String)annotation);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else o = annotation;		
		}
		
		if(annotation != null) {
		
			variableValue.put(key, o);
			
			if(c == String.class) {
				HashSet<String> set;
				if((set = stringNominal.get(key)) == null) {
					 set = new HashSet<String>();
					 stringNominal.put(key, set);
				}
			
				set.add((String) annotation);
			}
			
			HashSet<String> set;
			if((set = all.get(key)) == null) {
				 set = new HashSet<String>();
				 all.put(key, set);
			}
		
			set.add(annotation.toString());
			
			if(!variableClass.keySet().contains(key) || (variableClass.get(key) != c) && c != Integer.class) {
				variableClass.put(key, c);
//				staticVariableClass.put(key, c);
			}else {
				if(c == String.class) variableClass.put(key, c);
				stringNominal.put(key, all.get(key));
			}
			
		}else {
//			System.out.println(key);
			throw new RuntimeException();
		}
		
	}
	
	private void insertPostOffer(String key, HashMap<String, Object> variableValue, Object annotation, Class c) {
		
		Object o = annotation;
		
		if(annotation instanceof String) {
			if(c == Long.class) o = Long.valueOf((String)annotation);
			else if(c == Double.class) o = Double.valueOf((String)annotation);
			else if(c == Boolean.class) o = Boolean.valueOf((String)annotation);
			else if(c == Date.class) {
				try {
					o = logDateFormat.parse((String)annotation);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else o = annotation;		
		}
		
		variableValue.put(key, o);
		
		if(c == String.class) {
			HashSet<String> set;
			if((set = stringNominal.get(key)) == null) {
				 set = new HashSet<String>();
				 stringNominal.put(key, set);
			}
		
			set.add((String) annotation);
		}
		
		HashSet<String> set;
		if((set = all.get(key)) == null) {
			 set = new HashSet<String>();
			 all.put(key, set);
		}
	
		set.add(annotation.toString());
		
		if(!variableClassPostOffer.keySet().contains(key) || (variableClassPostOffer.get(key) != c) && c != Integer.class) {
			variableClassPostOffer.put(key, c);
		}else {
			if(c == String.class) variableClassPostOffer.put(key, c);
			stringNominal.put(key, all.get(key));
		}
		
	}
	
	private void insertPostOffer(String key, HashMap<String, Object> variableValue, HashMap<String, Long> variableValueTime, Object annotation, Class c, long time) {
		
		Long oldTime = null;
		if((oldTime = variableValueTime.get(key)) != null) {
			if(time < oldTime) return;
		}else {
			variableValueTime.put(key, time);
		}
		
		Object o = annotation;
		
		if(annotation instanceof String) {
			if(c == Long.class) o = Long.valueOf((String)annotation);
			else if(c == Double.class) o = Double.valueOf((String)annotation);
			else if(c == Boolean.class) o = Boolean.valueOf((String)annotation);
			else if(c == Date.class) {
				try {
					o = logDateFormat.parse((String)annotation);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else o = annotation;		
		}
		
		variableValue.put(key, o);
		
		if(c == String.class) {
			HashSet<String> set;
			if((set = stringNominal.get(key)) == null) {
				 set = new HashSet<String>();
				 stringNominal.put(key, set);
			}
		
			set.add((String) annotation);
		}
		
		HashSet<String> set;
		if((set = all.get(key)) == null) {
			 set = new HashSet<String>();
			 all.put(key, set);
		}
	
		set.add(annotation.toString());
		
		if(!variableClassPostOffer.keySet().contains(key) || (variableClassPostOffer.get(key) != c) && c != Integer.class) {
			variableClassPostOffer.put(key, c);
		}else {
			if(c == String.class) variableClassPostOffer.put(key, c);
			stringNominal.put(key, all.get(key));
		}
		
	}

}
