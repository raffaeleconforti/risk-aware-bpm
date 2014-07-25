package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRoleTuple;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Prom_ActivityRole extends ActivityRole {
	
	XLog log = null;
	String resources = null;
	HashMap<String, LinkedList<XEvent>> index = new HashMap<String, LinkedList<XEvent>>();
	HashMap<String, TaskID> indexTaskID = new HashMap<String, TaskID>();
	
	public static void main(String[] args) {
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/MXMLlogs/750/tmp.xes"); ///home/stormfire/Log.xes
	        
			String specification = null;
			try {
				File f = new File("/home/stormfire/CarrierAppointment4.yawl");//"/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl"); //
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
			WorkDefIDs.add("1921");
			WorkDefIDs.add("2");
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
			
			Prom_ActivityRole par = new Prom_ActivityRole();
			par.setLog(log, resources);
			
			System.out.println(par.getRows("2001:Arrange_Pickup_Appointment_393", true, null, false, 0, 0, 0, false));
			
//			System.out.println(pa.getIDs("Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 0, false, null, false));
			
//			taskids = pa.getCounts("Modify_Delivery_Appointment_398", true, WorkDefIDs, true);
//			System.out.println(taskids);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setLog(XLog log, String resources) {
		
		this.log = log;
		this.resources = resources;
		indexLog();
		
	}

	public void updateLog(XLog log) {

		this.log = log;
		index.clear();
		indexTaskID.clear();
		indexLog();
		
	}
	
	private void indexLog() {
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);

				TaskID t = new TaskID(strTraceID, eventName);
				
				LinkedList<XEvent> l;
				if((l = index.get(t.toString())) == null) {
					l = new LinkedList<XEvent>();
					index.put(t.toString(), l);
					indexTaskID.put(t.toString(), t);
				}
				l.add(event);

			}
			
		}
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getAllTaskID() {
		
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		HashSet<TaskID> tasks = new HashSet<TaskID>();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				
				tasks.add(new TaskID(strTraceID, eventName));

			}
			
		}
		
		ActivityRoleTuple art = null;
		
		for(TaskID t : tasks) {
			art = new ActivityRoleTuple();
			art.setTaskID(t.toString());
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getAllRoleID() {
		
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> participants = e1.getChild("participants").getChildren("participant");
		
		ActivityRoleTuple art = null;
		for(Element participant : participants) {
			art = new ActivityRoleTuple();
			art.setRoleID(participant.getAttributeValue("id"));
			result.add(art);
			
		}
		
		if(result.size() != 0) return result;
		return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getTaskIDs(String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus) {

		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XOrganizationalExtension xoeOrganizationalExt = XOrganizationalExtension.instance();
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
				
				String resource = xoeOrganizationalExt.extractResource(event);
				
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isRoleID, resource != null && resource.equals(RoleID))) {
					if((variables = idVariable.get(task)) == null) {
						variables = new LinkedList<VariableID>();
						idVariable.put(task, variables);
					}
					variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
				}
						
			}
				
		}
		
		ActivityRoleTuple art = null;
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(Status, isStatus);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
						
			boolean yes = false;	

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
					
					if(add) {
						
						yes = true;
						
					}
				}

				if(executing) {
					add = (var.getEventType() == VariableID.executing) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {
						
						yes = true;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {
	
						yes = true;
						
					}
				}
			}
			
			if(yes) {
				art = new ActivityRoleTuple();
				art.setTaskID(id.toString());
				result.add(art);
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getRoleIDs(String TaskID, boolean isTaskID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus) {

		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XOrganizationalExtension xoeOrganizationalExt = XOrganizationalExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
		HashMap<VariableID, String> variableResource = new HashMap<VariableID, String>();
			
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<VariableID> variables = null;
			TaskID task = null;
				
			for (XEvent event : trace) {
	
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				
				task = new TaskID(strTraceID, eventName);
				
				if(Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
				
					String resource = xoeOrganizationalExt.extractResource(event);
					
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
					
					if(timeStamp != null && resource != null) {
						if((variables = idVariable.get(task)) == null) {
							variables = new LinkedList<VariableID>();
							idVariable.put(task, variables);
						}
						VariableID v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
						variables.add(v);
						variableResource.put(v, resource);
					}
				
				}
						
			}
				
		}
		
		ActivityRoleTuple art = null;
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(Status, isStatus);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
						
			boolean yes = false;	
			VariableID v = null;

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
					
					if(add) {
						
						yes = true;
						v = var;
						
					}
				}

				if(executing) {
					add = (var.getEventType() == VariableID.executing) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {
						
						yes = true;
						v = var;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {
	
						yes = true;
						v = var;
						
					}
				}
			}
			
			if(yes) {
				art = new ActivityRoleTuple();
				art.setRoleID(variableResource.get(v));
				result.add(art);
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getTimeStamps(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, int Status, boolean isStatus) {

		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XOrganizationalExtension xoeOrganizationalExt = XOrganizationalExtension.instance();
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
				
				if(Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
				
					String resource = xoeOrganizationalExt.extractResource(event);
					
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isRoleID, resource != null && resource.equals(RoleID))) {
						if((variables = idVariable.get(task)) == null) {
							variables = new LinkedList<VariableID>();
							idVariable.put(task, variables);
						}
						variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
					}
				
				}
						
			}
				
		}
		
		ActivityRoleTuple art = null;
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(Status, isStatus);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];
						
			boolean yes = false;	
			VariableID v = null;

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled);
					
					if(add) {
						
						yes = true;
						v = var;
						
					}
				}

				if(executing) {
					add = (var.getEventType() == VariableID.executing);
						
					if(add) {
						
						yes = true;
						v = var;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed);
						
					if(add) {
	
						yes = true;
						v = var;
						
					}
				}
			}
			
			if(yes) {
				art = new ActivityRoleTuple();
				art.setTime(v.getTime().toString());
				result.add(art);
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getStatus(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp) {

		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XOrganizationalExtension xoeOrganizationalExt = XOrganizationalExtension.instance();
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
				
				if(Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
				
					String resource = xoeOrganizationalExt.extractResource(event);
					
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isRoleID, resource != null && resource.equals(RoleID))) {
						if((variables = idVariable.get(task)) == null) {
							variables = new LinkedList<VariableID>();
							idVariable.put(task, variables);
						}
						variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
					}
				
				}
						
			}
				
		}
		ActivityRoleTuple art = null;
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
						
			boolean yes = false;	
			VariableID v = null;

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				add = Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
				
				if(add) {
					
					yes = true;
					v = var;
					
				}
			}
			
			if(yes) {
				art = null;
				art.setStatus(Prom_DatabaseUtilities.getEventType(v.getEventType()));
				result.add(art);
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getRows(String TaskID, boolean isTaskID, String RoleID, boolean isRoleID, long TimeStamp, int isTimeStamp, int Status, boolean isStatus) {

		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		LinkedList<ActivityRoleTuple> innerResult = new LinkedList<ActivityRoleTuple>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XOrganizationalExtension xoeOrganizationalExt = XOrganizationalExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		HashMap<TaskID, LinkedList<VariableID>> idVariable = new HashMap<TaskID, LinkedList<VariableID>>();
		HashMap<VariableID, String> variableResource = new HashMap<VariableID, String>();
			
		if(isTaskID) {
			
			TaskID task = indexTaskID.get(TaskID);
			
			LinkedList<VariableID> variables = null;
						
			LinkedList<XEvent> l = index.get(TaskID);
			
			if(l != null) {
			
				for (XEvent event : l) {
					
					String eventName = xceConceptExt.extractName(event);
					Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
					
					if(eventType != null) {
						
						if(Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {

							String resource = xoeOrganizationalExt.extractResource(event);
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
							
							if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isRoleID, resource != null && resource.equals(RoleID))) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								VariableID v = new VariableID(task.getCaseID(), eventName, eventType, timeStamp); 
								variables.add(v);
								variableResource.put(v, resource);
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
					
				for (XEvent event : trace) {
		
					String eventName = xceConceptExt.extractName(event);
					Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
					
					if(eventType != null) {
						task = new TaskID(strTraceID, eventName);
						
						if(Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
						
							String resource = xoeOrganizationalExt.extractResource(event);
							
							Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
							
							if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isRoleID, resource != null && resource.equals(RoleID))) {
								if((variables = idVariable.get(task)) == null) {
									variables = new LinkedList<VariableID>();
									idVariable.put(task, variables);
								}
								VariableID v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
								variables.add(v);
								variableResource.put(v, resource);
							}
						
						}
						
					}
							
				}
					
			}
		}
		
		HashMap<String, LinkedList<ActivityRoleTuple>> idTaskID = new HashMap<String, LinkedList<ActivityRoleTuple>>();
		
		ActivityRoleTuple art = null;
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			String workDefID = id.getCaseID();
			
			if((innerResult = idTaskID.get(workDefID)) == null) {
				innerResult = new LinkedList<ActivityRoleTuple>();
				idTaskID.put(workDefID, innerResult);
			}
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(Status, isStatus);
			
			boolean start = checkTypeTime[0];
			boolean executing = checkTypeTime[1];
			boolean end = checkTypeTime[2];

			VariableID vs = null;
			VariableID ve = null;
			VariableID vc = null;

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				if(start) {
					add = (var.getEventType() == VariableID.scheduled) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
					
					if(add) {
						
						vs = var;
						ve = null;
						vc = null;
						
					}
				}

				if(executing) {
					add = (var.getEventType() == VariableID.executing) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {

						ve = var;
						vc = null;
						
					}
				}
				
				if(end) {
					add = (var.getEventType() == VariableID.completed) && Prom_DatabaseUtilities.checkIfTime(var.getTime(), TimeStamp, isTimeStamp);
						
					if(add) {
	
						vc = var;
						
					}
				}
			}
			
			if(vs != null) {				
				art = new ActivityRoleTuple();
				art.setWorkDefID(vs.getCaseID());
				art.setRoleID(variableResource.get(vs));
				art.setStatus(Prom_DatabaseUtilities.getEventType(vs.getEventType()));
				art.setTaskID((new TaskID(vs.getCaseID(), vs.getEventName())).toString());
				art.setTaskName(vs.getEventName());
				art.setTime(vs.getTime().toString());

				innerResult.add(art);
			}
			if(ve != null) {
				art = new ActivityRoleTuple();
				art.setWorkDefID(ve.getCaseID());
				art.setRoleID(variableResource.get(ve));
				art.setStatus(Prom_DatabaseUtilities.getEventType(ve.getEventType()));
				art.setTaskID((new TaskID(ve.getCaseID(), ve.getEventName())).toString());
				art.setTaskName(ve.getEventName());
				art.setTime(ve.getTime().toString());

				innerResult.add(art);
			}
			if(vc != null) {
				art = new ActivityRoleTuple();
				art.setWorkDefID(vc.getCaseID());
				art.setRoleID(variableResource.get(vc));
				art.setStatus(Prom_DatabaseUtilities.getEventType(vc.getEventType()));
				art.setTaskID((new TaskID(vc.getCaseID(), vc.getEventName())).toString());
				art.setTaskName(vc.getEventName());
				art.setTime(vc.getTime().toString());

				innerResult.add(art);
			}
			
		}
		
		for(Entry<String, LinkedList<ActivityRoleTuple>> entry : idTaskID.entrySet()) {
			LinkedList<ActivityRoleTuple> vec = entry.getValue();
			if(vec.size()>0) {
				result.addAll(vec);
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getAllRows(String WorkDefID) {
		
		return getRows(null, false, null, false, 0, 0, 0, false);
		
	}

}
