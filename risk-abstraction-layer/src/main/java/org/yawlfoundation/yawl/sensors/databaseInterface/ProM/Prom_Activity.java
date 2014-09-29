package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Prom_Activity extends Activity {
	
	XLog log = null;
	String specification = null;
	String resources = null;
	HashMap<String, LinkedList<XEvent>> index = new HashMap<String, LinkedList<XEvent>>(); 
	
	public static void main(String[] args) {
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes"); ///home/stormfire/Log.xes
	        
			String specification = null;
			try {
				File f = new File("/media/Data/SharedFolder/Commercial/Commercial.yawl");//"/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl"); //
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
			
			Prom_Activity pa = new Prom_Activity();
			pa.setLog(log, resources, specification);
			
			LinkedList<String> WorkDefIDs = new LinkedList<String>();
			WorkDefIDs.add("H004996124");
//			WorkDefIDs.add("2");
//			Vector<Vector<String>> taskids = pa.getRows(null, false, "Prepare_Transportation_Quote_390", true, WorkDefIDs, false, 0, 0, 3, true, null, false);
//			System.out.println(taskids);
//			Vector<Vector<String>> taskids = pa.getIDs("Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 0, false, null, false);
//			Vector<Vector<String>> taskids = pa.getCounts("Modify_Pickup_Appointment_397", true, WorkDefIDs, true);
//			Vector<Vector<String>> taskids = pa.getVariablesIDs(null, false, "null_389", true, WorkDefIDs, true, 0, false);
//			Vector<Vector<String>> taskids = pa.getCounts("Modify_Pickup_Appointment_397", true, WorkDefIDs, true);
//			Vector<Vector<String>> taskids = pa.getRows(null, false, "Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 1, 4, 4, true, null, false);
			
//			String s = pa.getDistribution("Modify_Delivery_Appointment_398", true, "2001", true, 1);
//			Element initialSet = JDOMUtil.stringToElement(s);
//			
//			s = initialSet.getTextTrim();
//			System.out.println(s);
			
			Prom_Activity par = new Prom_Activity();
			par.setLog(log, resources, specification);
			
			System.out.println(par.getTimes(null, false, "Follow_up_requested_20", true, WorkDefIDs, true, 0, false, null, false, true));
			
//			System.out.println(pa.getIDs("Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 0, false, null, false));
			
//			taskids = pa.getCounts("Modify_Delivery_Appointment_398", true, WorkDefIDs, true);
//			System.out.println(taskids);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setLog(XLog log, String resources, String specification) {
		
		this.log = log;
		this.specification = specification;
		this.resources = resources;
		indexLog();
		
	}

	public void updateLog(XLog log) {

		this.log = log;
		index.clear();
		indexLog();
		
	}
	
	private void indexLog() {
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				
				LinkedList<XEvent> l;
				if((l = index.get(strTraceID+"&%$%&"+eventName)) == null) {
					l = new LinkedList<XEvent>();
					index.put(strTraceID+"&%$%&"+eventName, l);
				}
				l.add(event);

			}
			
		}
		
	}

	@Override
	public LinkedList<ActivityTuple> getAllID() {
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		HashSet<TaskID> tasks = new HashSet<TaskID>();
		
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		
		ActivityTuple activityTuple = null;
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				
				tasks.add(new TaskID(strTraceID, eventName));

			}
			
		}
		
		for(TaskID t : tasks) {
			activityTuple = new ActivityTuple();
			activityTuple.setWorkDefID(t.getCaseID());
			activityTuple.setTaskID(t.toString());
			activityTuple.setName(t.getEventName());
			result.add(activityTuple);
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public boolean isActivity(String WorkDefID, String Name) {
				
		XConceptExtension xceConceptExt = XConceptExtension.instance();
				
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			if(strTraceID.equals(WorkDefID)) {
				
				for (XEvent event : trace) {
					
					if(xceConceptExt.extractName(event).equals(Name)) return true;
	
				}
				
			}
			
		}
		
		return false;
		
	}

	@Override
	public LinkedList<ActivityTuple> getIDs(String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		LinkedList<ActivityTuple> innerResult = new LinkedList<ActivityTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
		
		if(isName) {
			
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(isWorkDefID) {
				for(String id : WorkDefIDs) {
					
					LinkedList<XEvent> l = index.get(id+"&%$%&"+Name);
					task = new TaskID(id, Name);
					
					if(l != null) {
					
						for (XEvent event : l) {
							
							Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
								
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
							
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) if(eventType != null) variables.add(new VariableID(id, Name, eventType, timeStamp));
							}
							
						}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					
					if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
		
						LinkedList<XEvent> l = index.get(strTraceID+"&%$%&"+Name);
						if(l != null) {
						
							for (XEvent event : l) {
							
								Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
								
								task = new TaskID(strTraceID, Name);
								
									
								Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
								
								if(timeStamp != null) {
									if((variables = idVariable.get(task)) == null) {
										variables = new LinkedList<VariableID>();
										idVariable.put(task, variables);
									}
									if(eventType != null) if(eventType != null) variables.add(new VariableID(strTraceID, Name, eventType, timeStamp));
								}
							
							}
							
						}
						
					}
					
				}
			}		
		}else {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = null;
				TaskID task = null;
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
					
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						task = new TaskID(strTraceID, eventName);
						
						if(Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
						
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
							
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		HashMap<String, LinkedList<ActivityTuple>> idTaskID = new HashMap<String, LinkedList<ActivityTuple>>();
		ActivityTuple at = null;
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new LinkedList<ActivityTuple>();
				idTaskID.put(workDefID, innerResult);
			}
			
			int lastEventType = -1;
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			LinkedList<ActivityTuple> varScheduled = new LinkedList<ActivityTuple>();
			LinkedList<ActivityTuple> varExecuting = new LinkedList<ActivityTuple>();
			LinkedList<ActivityTuple> varComplete = new LinkedList<ActivityTuple>();

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(!isList) {
							varScheduled.clear();
							varExecuting.clear();
							varComplete.clear();
						}
						
						at = new ActivityTuple();
						at.setTaskID(id.toString());
						
						varScheduled.add(at);
						lastEventType = VariableID.scheduled;
						
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete.clear();
						}
						
						if(!isList) {
							varExecuting.clear();
						}

						at = new ActivityTuple();
						at.setTaskID(id.toString());
						
						varExecuting.add(at);
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(!isList) {
							varComplete.clear();
						}
						
						at = new ActivityTuple();
						at.setTaskID(id.toString());
						
						varComplete.add(at);
						lastEventType = VariableID.completed;
						
					}
				}
			}
			
			if(varScheduled.size() > 0) innerResult.addAll(varScheduled);
			if(varExecuting.size() > 0) innerResult.addAll(varExecuting);
			if(varComplete.size() > 0) innerResult.addAll(varComplete);
		}
		
		for(Entry<String, LinkedList<ActivityTuple>> entry : idTaskID.entrySet()) {
			LinkedList<ActivityTuple> vec = entry.getValue();
			Collections.sort(vec);
			if(vec.size()>0) {
				result.addAll(vec);
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getWorkDefIDs(String ID, boolean isID, String Name, boolean isName, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
			
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<VariableID> variables = null;
			TaskID task = null;
							
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				
				task = new TaskID(strTraceID, eventName);
				
				if(Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
				
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
					
					if(timeStamp != null) {
						if((variables = idVariable.get(task)) == null) {
							variables = new LinkedList<VariableID>();
							idVariable.put(task, variables);
						}
						if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
					}
					
				}
				
			}
				
		}
		
		HashSet<String> workDefIDs = new HashSet<String>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
						
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			int lastEventType = -1;
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			String varScheduled = null;
			String varExecuting = null;
			String varComplete = null;

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						varScheduled = id.getCaseID();
						varExecuting = null;
						varComplete = null;
						
						lastEventType = VariableID.scheduled;
												
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete = null;
						}

						varExecuting = id.getCaseID();
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						varComplete = id.getCaseID();
						lastEventType = VariableID.completed;
						
					}
				}
			}

			if(varComplete != null) workDefIDs.add(varComplete);
			else if(varExecuting != null) workDefIDs.add(varExecuting);
			else if(varScheduled != null) workDefIDs.add(varScheduled);
			
		}
		
		LinkedList<String> list = new LinkedList<String>(workDefIDs);
		Collections.sort(list);
		
		ActivityTuple at = null;
		for(String wd : list) {
			at = new ActivityTuple();
			at.setWorkDefID(wd);
			result.add(at);
		}

		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getNames(String ID, boolean isID, String WorkDefID, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
			
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID))) {
				
				for (XEvent event : trace) {
					
					String eventName = xceConceptExt.extractName(event);
					Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
					
					task = new TaskID(strTraceID, eventName);
					
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
										
					if(timeStamp != null) {
						if((variables = idVariable.get(task)) == null) {
							variables = new LinkedList<VariableID>();
							idVariable.put(task, variables);
						}
						if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
					}
					
				}
				
			}
			
		}
		
		HashSet<String> names = new HashSet<String>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
						
			id = entry.getKey();
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			int lastEventType = -1;
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			String varScheduled = null;
			String varExecuting = null;
			String varComplete = null;	

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						varScheduled = id.getEventName();
						varExecuting = null;
						varComplete = null;
						
						lastEventType = VariableID.scheduled;
						
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete = null;
						}

						varExecuting = id.getEventName();
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						varComplete = id.getEventName();
						lastEventType = VariableID.completed;
						
					}
				}
			}

			if(varComplete != null) names.add(varComplete);
			else if(varExecuting != null) names.add(varExecuting);
			else if(varScheduled != null) names.add(varScheduled);
			
		}
			
		LinkedList<String> list = new LinkedList<String>(names);
		Collections.sort(list);
		
		ActivityTuple at = null;
		for(String name : list) {
			at = new ActivityTuple();
			at.setName(name);
			result.add(at);
		}

		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getTimes(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {
		
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		Vector<String> innerResult = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
					
		if(isName) {
			
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(isWorkDefID) {
				for(String id : WorkDefIDs) {
					
					LinkedList<XEvent> l = index.get(id+"&%$%&"+Name);
					task = new TaskID(id, Name);
					
					if(l != null) {
					
						for (XEvent event : l) {
							
							Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
														
							if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
								
								Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
														
								if(timeStamp != null) {
									if((variables = idVariable.get(task)) == null) {
										variables = new LinkedList<VariableID>();
										idVariable.put(task, variables);
									}
									if(eventType != null) variables.add(new VariableID(id, Name, eventType, timeStamp));
								}
									
							}
							
						}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					
					if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
		
						LinkedList<XEvent> l = index.get(strTraceID+"&%$%&"+Name);
						if(l != null) {
						
							for (XEvent event : l) {
								
								Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
																
								if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
									
									Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
															
									if(timeStamp != null) {
										if((variables = idVariable.get(task)) == null) {
											variables = new LinkedList<VariableID>();
											idVariable.put(task, variables);
										}
										if(eventType != null) variables.add(new VariableID(strTraceID, Name, eventType, timeStamp));
									}
										
								}
								
							}
							
						}
						
					}
					
				}
			}		
		}else {
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = null;
				TaskID task = null;
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
					
					for (XEvent event : trace) {
			
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						task = new TaskID(strTraceID, eventName);
	
						if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID)) && Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
													
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
							}
								
						}
						
					}
					
				}
				
			}
			
		}
		
		HashMap<String, Vector<String>> idTaskID = new HashMap<String, Vector<String>>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			id = entry.getKey();
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new Vector<String>();
				idTaskID.put(workDefID, innerResult);
			}
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			int lastEventType = -1;
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			LinkedList<String> varScheduled = new LinkedList<String>();
			LinkedList<String> varExecuting = new LinkedList<String>();
			LinkedList<String> varComplete = new LinkedList<String>();

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(!isList) {
							varScheduled.clear();
							varExecuting.clear();
							varComplete.clear();
						}
						
						varScheduled.add(var.getTime().toString());
						lastEventType = VariableID.scheduled;
						
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete.clear();
						}
						
						if(!isList) {
							varExecuting.clear();
						}

						varExecuting.add(var.getTime().toString());
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {

						if(!isList) {
							varComplete.clear();
						}
						
						varComplete.add(var.getTime().toString());
						lastEventType = VariableID.completed;
						
					}
				}
			}

			if(varScheduled.size() > 0) innerResult.addAll(varScheduled);
			if(varExecuting.size() > 0) innerResult.addAll(varExecuting);
			if(varComplete.size() > 0) innerResult.addAll(varComplete);

		}
		
		ActivityTuple at = null;
		
		for(Entry<String, Vector<String>> entry : idTaskID.entrySet()) {
			Vector<String> vec = entry.getValue();
			
			Collections.sort(vec);

			if(vec.size()>0) {
				for(String time : vec) {
					at = new ActivityTuple();
					at.setVariableID(entry.getKey());
					at.setTime(time);
					result.add(at);
				}
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getVariablesIDs(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, boolean isList) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		Vector<String> innerResult = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
			
		if(isName) {
			
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(isWorkDefID) {
				for(String id : WorkDefIDs) {
					
					LinkedList<XEvent> l = index.get(id+"&%$%&"+Name);
					task = new TaskID(id, Name);
					
					if(l != null) {
					
						for (XEvent event : l) {
							
							Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
							
							if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
								
								Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
														
								if(timeStamp != null) {
									if((variables = idVariable.get(task)) == null) {
										variables = new LinkedList<VariableID>();
										idVariable.put(task, variables);
									}
									if(eventType != null) variables.add(new VariableID(id, Name, eventType, timeStamp));
								}
									
							}
							
						}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					
					if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
		
						LinkedList<XEvent> l = index.get(strTraceID+"&%$%&"+Name);
						if(l != null) {
						
							for (XEvent event : l) {
							
								Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
								
//								if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
									
									Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
															
									if(timeStamp != null) {
										if((variables = idVariable.get(task)) == null) {
											variables = new LinkedList<VariableID>();
											idVariable.put(task, variables);
										}
										if(eventType != null) variables.add(new VariableID(strTraceID, Name, eventType, timeStamp));
									}
										
//								}
							
							}
							
						}
						
					}
					
				}
			}		
		}else {
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = null;
				TaskID task = null;
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
					
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						task = new TaskID(strTraceID, eventName);
						
						if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID)) && Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
													
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
							}
								
						}
						
					}
					
				}
				
			}
			
		}
		
		HashMap<String, Vector<String>> idTaskID = new HashMap<String, Vector<String>>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new Vector<String>();
				idTaskID.put(workDefID, innerResult);
			}
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			int lastEventType = -1;
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			LinkedList<String> varScheduled = new LinkedList<String>();
			LinkedList<String> varExecuting = new LinkedList<String>();
			LinkedList<String> varComplete = new LinkedList<String>();

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add) {
						
						if(!isList) {
							varScheduled.clear();
							varExecuting.clear();
							varComplete.clear();
						}
						
						varScheduled.add(var.toString());
						
						lastEventType = VariableID.scheduled;
						
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing);
					
					if(add) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete = null;
						}
						
						if(!isList) {
							varExecuting.clear();
						}

						varExecuting.add(var.toString());
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
					
					if(add) {

						if(!isList) {
							varComplete.clear();
						}
						
						varComplete.add(var.toString());
						lastEventType = VariableID.completed;
						
					}
				}
			}
			
			if(varScheduled.size() > 0) innerResult.addAll(varScheduled);
			if(varExecuting.size() > 0) innerResult.addAll(varExecuting);
			if(varComplete.size() > 0) innerResult.addAll(varComplete);
			
		}
		
		ActivityTuple at = null;
		
		for(Entry<String, Vector<String>> entry : idTaskID.entrySet()) {
			Vector<String> vec = entry.getValue();
			Collections.sort(vec);
			for(String variable : vec) {
				at = new ActivityTuple();
				VariableID v = VariableID.revertToString(variable);
				at.setWorkDefID(v.getCaseID());
				at.setVariableID(variable);
				at.setName(v.getEventName());
				result.add(at);
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getCounts(String Name, boolean isName, LinkedList<String> WorkDefID, boolean isWorkDefID) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		LinkedList<ActivityTuple> innerResult = new LinkedList<ActivityTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashSet<String> WorkDefID2 = new HashSet<String>();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
			
		if(isName) {
			
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(isWorkDefID) {
				
				for(String id : WorkDefID) {
					
					LinkedList<XEvent> l = index.get(id+"&%$%&"+Name);
					WorkDefID2.add(id);
					
					task = new TaskID(id, Name);
					
					if(l != null) {
					
						for (XEvent event : l) {
							
							Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
							
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) variables.add(new VariableID(id, Name, eventType, timeStamp));
							}
							
						}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
										
					if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefID != null && WorkDefID.contains(strTraceID)))) {
		
						WorkDefID2.add(strTraceID);
						LinkedList<XEvent> l = index.get(strTraceID+"&%$%&"+Name);
						task = new TaskID(strTraceID, Name);
						
						if(l != null) {
						
							for (XEvent event : l) {
								
								Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
								
								Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
										
								if(timeStamp != null) {
									if((variables = idVariable.get(task)) == null) {
										variables = new LinkedList<VariableID>();
										idVariable.put(task, variables);
									}
									if(eventType != null) variables.add(new VariableID(strTraceID, Name, eventType, timeStamp));
								}
								
							}
							
						}
						
					}
					
				}
			}		
		}else {
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = null;
				TaskID task = null;
	
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, WorkDefID.contains(strTraceID))) {
					
					WorkDefID2.add(strTraceID);
					
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						task = new TaskID(strTraceID, eventName);
	
						if(Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
						
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
	
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		HashMap<String, LinkedList<ActivityTuple>> idTaskID = new HashMap<String, LinkedList<ActivityTuple>>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new LinkedList<ActivityTuple>();
				idTaskID.put(workDefID, innerResult);
			}
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			int lastEventType = -1;
			int count = 0;
			
			String varScheduled = null;
			String varExecuting = null;
			String varComplete = null;	
			String varName = null;
			
			for(VariableID var : arrayTimes) {
				boolean add = false;
			
				add = (var.getEventType() == VariableID.scheduled);
				
				if(add) {
					
					if(lastEventType == -1 || lastEventType == VariableID.completed) count++;
					
					varScheduled = var.getCaseID();
					varExecuting = null;
					varComplete = null;
					
					varName = var.getEventName();
					
					lastEventType = VariableID.scheduled;
					
				}
			
				add = (var.getEventType() == VariableID.executing);
				
				if(add) {
					
					if(lastEventType == -1 || lastEventType == VariableID.completed) count++;
					
					if(lastEventType != VariableID.scheduled) {
						varComplete = null;
					}

					varExecuting = var.getCaseID();
					
					varName = var.getEventName();
					
					lastEventType = VariableID.executing;
					
				}
			
				add = (var.getEventType() == VariableID.completed);
				
				if(add) {
					
					if(lastEventType == -1 || lastEventType == VariableID.completed) count++;

					varComplete = var.getCaseID();
					
					varName = var.getEventName();
					
					lastEventType = VariableID.completed;
					
				}
			}

			if(varComplete != null) {
				ActivityTuple at = new ActivityTuple();
				at.setCount(Integer.toString(count));
				at.setName(varName);
				at.setWorkDefID(varComplete);
				innerResult.add(at);
			}else if(varExecuting != null) {
				ActivityTuple at = new ActivityTuple();
				at.setCount(Integer.toString(count));
				at.setName(varName);
				at.setWorkDefID(varExecuting);
				innerResult.add(at);
			}else if(varScheduled != null) {
				ActivityTuple at = new ActivityTuple();
				at.setCount(Integer.toString(count));
				at.setName(varName);
				at.setWorkDefID(varScheduled);
				innerResult.add(at);
			}
		}
		
		ActivityTuple at = null;
		for(String key : WorkDefID2) {
			if(idTaskID.keySet().contains(key)) {
				LinkedList<ActivityTuple> vec = idTaskID.get(key);
//				Collections.sort(vec);
				for(ActivityTuple count : vec) {
					at = new ActivityTuple();
					at.setCount(count.getCount());
					at.setName(Name);
					at.setWorkDefID(key);
					result.add(at);
				}
			}else {
				at = new ActivityTuple();
				at.setCount(""+0);
				at.setWorkDefID(key);
				at.setName(Name);
				result.add(at);
			}
		}

		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getRows(String ID, boolean isID, String Name, boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		LinkedList<ActivityTuple> innerResult = new LinkedList<ActivityTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
		
		if(isName) {
			
			LinkedList<VariableID> variables = null;
			TaskID task = null;
			
			if(isWorkDefID) {
				for(String id : WorkDefIDs) {
					
					LinkedList<XEvent> l = index.get(id+"&%$%&"+Name);
					task = new TaskID(id, Name);
					
					if(l != null) {
					
						for (XEvent event : l) {
							
							Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
							
							if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
								
								Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
															
								if(timeStamp != null) {
									if((variables = idVariable.get(task)) == null) {
										variables = new LinkedList<VariableID>();
										idVariable.put(task, variables);
									}
									if(eventType != null) variables.add(new VariableID(id, Name, eventType, timeStamp));
								}
									
							}
							
						}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					
					if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
		
						LinkedList<XEvent> l = index.get(strTraceID+"&%$%&"+Name);
						if(l != null) {
						
							for (XEvent event : l) {
							
								Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
								
								task = new TaskID(strTraceID, Name);
								
								if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID))) {
									
									Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
																
									if(timeStamp != null) {
										if((variables = idVariable.get(task)) == null) {
											variables = new LinkedList<VariableID>();
											idVariable.put(task, variables);
										}
										if(eventType != null) variables.add(new VariableID(strTraceID, Name, eventType, timeStamp));
									}
										
								}
							
							}
							
						}
						
					}
					
				}
			}		
		}else {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = null;
				TaskID task = null;
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, (WorkDefIDs != null && WorkDefIDs.contains(strTraceID)))) {
	
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						task = new TaskID(strTraceID, eventName);
						
						if(Prom_DatabaseUtilities.checkXAND(isID, task.toString().equals(ID)) && Prom_DatabaseUtilities.checkXAND(isName, eventName.equals(Name))) {
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
														
							if(timeStamp != null) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								if(eventType != null) variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
							}
								
						}
						
					}
					
				}
				
			}
			
		}
				
		HashMap<String, LinkedList<ActivityTuple>> idTaskID = new HashMap<String, LinkedList<ActivityTuple>>();
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new LinkedList<ActivityTuple>();
				idTaskID.put(workDefID, innerResult);
			}
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			int lastEventType = -1;
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
			
			LinkedList<VariableID> varScheduled = new LinkedList<VariableID>();
			LinkedList<VariableID> varExecuting = new LinkedList<VariableID>();
			LinkedList<VariableID> varComplete = new LinkedList<VariableID>();

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), Time, isTime);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(!isList) {
							varScheduled.clear();
							varExecuting.clear();
							varComplete.clear();
						}
						
						varScheduled.add(var);
						
						lastEventType = VariableID.scheduled;
						
					}
				}
				
				if(executing) {
					add = (var.getEventType() == VariableID.executing) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), Time, isTime);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {
						
						if(lastEventType != VariableID.scheduled && start) {
							varComplete.clear();
						}

						if(!isList) {
							varExecuting.clear();
						}
						
						varExecuting.add(var);
						lastEventType = VariableID.executing;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), Time, isTime);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, var.toString().equals(VariablesID))) {

						if(!isList) {
							varComplete.clear();
						}
						
						varComplete.add(var);
						lastEventType = VariableID.completed;
						
					}
				}
			}
			
			if(varScheduled.size() > 0) {
				for(VariableID var : varScheduled) {
					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(var.getCaseID());
					at.setTaskID((new TaskID(var.getCaseID(), var.getEventName())).toString());
					at.setName(var.getEventName());
					at.setStatus(Prom_DatabaseUtilities.getEventType(var.getEventType()));
					at.setTime(var.getTime().toString());
					at.setVariableID(var.toString());
					innerResult.add(at);
				}
			}
			if(varExecuting.size() > 0) {
				for(VariableID var : varExecuting) {
					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(var.getCaseID());
					at.setTaskID((new TaskID(var.getCaseID(), var.getEventName())).toString());
					at.setName(var.getEventName());
					at.setStatus(Prom_DatabaseUtilities.getEventType(var.getEventType()));
					at.setTime(var.getTime().toString());
					at.setVariableID(var.toString());
					innerResult.add(at);
				}
			}
			if(varComplete.size() > 0) {
				for(VariableID var : varComplete) {
					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(var.getCaseID());
					at.setTaskID((new TaskID(var.getCaseID(), var.getEventName())).toString());
					at.setName(var.getEventName());
					at.setStatus(Prom_DatabaseUtilities.getEventType(var.getEventType()));
					at.setTime(var.getTime().toString());
					at.setVariableID(var.toString());
					innerResult.add(at);
				}
			}
			
		}
		
		for(Entry<String, LinkedList<ActivityTuple>> entry : idTaskID.entrySet()) {
			LinkedList<ActivityTuple> vec = entry.getValue();
			if(vec.size()>0) {
				result.addAll(vec);
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getAllRows(String WorkDefID) {
		
		LinkedList<String> workDefIDs = new LinkedList<String>();
		workDefIDs.add(WorkDefID);
		return getRows(null, false, null, false, workDefIDs, true, 0, 0, 0, false, null, false, false);
		
	}

	@Override
	public String getDistribution(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type) {
		String type = null;
		if(Type!=0) {
			if(Type==1) type="offer";
			else if(Type==2) type="allocate";
			else if(Type==3) type="start";
		}
		
		Element e1 = JDOMUtil.stringToElement(specification);
		Namespace ns = e1.getNamespace();
		Namespace ns2 = e1.getNamespace("xsi");

		List<Element> dec = e1.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {

			Element pce = ele.getChild("processControlElements", ns);
			if(pce != null) {
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					boolean isComposite = false;
					boolean isMultiple = false;
					
					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
					
					String taskName = task.getAttributeValue("id");
					
					if(taskName != null && Prom_DatabaseUtilities.checkXAND(isName, Name.equals(taskName))) {
						Element resourcing = task.getChild("resourcing", ns);
						
						for(Element e : (List<Element>) resourcing.getChildren()) {
							if(type.contains(e.getName())){
								if(e.getChildren().size()>0) {
									Element distributionSet = ((List<Element>)e.getChildren()).get(0);
									if(distributionSet.getChildren() != null) {
										Element initialSet = ((List<Element>)distributionSet.getChildren()).get(0);
										StringBuffer sb = new StringBuffer();
										for(Element child : (List<Element>) initialSet.getChildren()) {
											if(child.getName().equals("role")) {
//												sb.append(Role.getRole("DatabaseInterface.Prom_Role").getRoleRInfo(child.getValue()));

												Prom_Role roleLayer = (Prom_Role) Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.ProM.Prom_Role");
												roleLayer.setLog(log, resources);
												
												for(String part : roleLayer.getParticipantWithRole(child.getValue())) {
													
													sb.append("<participant>");
													sb.append(part);
													sb.append("</participant>");
													
												}
												
											}else if(child.getName().equals("participant")) {
//												sb.append(Role.getRole("DatabaseInterface.Prom_Role").getRoleInfo(child.getValue())); //TODO fix role
												
												sb.append("<participant>");
												sb.append(child.getValue());
												sb.append("</participant>");
												
											}
										}
										initialSet.setText(sb.toString());
										String r = JDOMUtil.formatXMLString(JDOMUtil.elementToString(initialSet).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#xD;", ""));
										r = r.replace(" xmlns=\"http://www.yawlfoundation.org/yawlschema\"", "");
										return r;
									}
								}else {
									return null;
								}
							}
						}
						
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getInitiator(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type) {
		String type = null;
		if(Type!=0) {
			if(Type==1) type="offer";
			else if(Type==2) type="allocate";
			else if(Type==3) type="start";
		}
		
		Element e1 = JDOMUtil.stringToElement(specification);
		Namespace ns = e1.getNamespace();
		Namespace ns2 = e1.getNamespace("xsi");

		List<Element> dec = e1.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {

			Element pce = ele.getChild("processControlElements", ns);
			if(pce != null) {
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					boolean isComposite = false;
					boolean isMultiple = false;
					
					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
					
					Element taskName = null;
					if((taskName = task.getChild("name", ns)) != null && Prom_DatabaseUtilities.checkXAND(isName, Name.equals(taskName.getValue()))) {
						Element resourcing = task.getChild("resourcing", ns);
						
						for(Element e : (List<Element>) resourcing.getChildren()) {
							if(type.contains(e.getName())){
								return e.getAttributeValue("initiator");
							}
						}
						
					}
				}
			}
		}
		return null;
	}

}
