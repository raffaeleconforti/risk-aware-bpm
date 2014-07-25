package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.*;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Prom_SubProcess extends SubProcess {

	XLog log = null;
	DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
	
	public static void main(String[] args) {
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Log1.xes");//"/home/stormfire/Documents/Useless/log.xes"); //
	        
			String specification = null;
			try {
				File f = new File("/home/stormfire/CarrierAppointment.yawl");//"/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl"); //
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
			
			Prom_SubProcess ps = new Prom_SubProcess();
			ps.setLog(log);
			
			LinkedList<String> WorkDefIDs = new LinkedList<String>();
			WorkDefIDs.add("1");
//			Vector<Vector<String>> taskids = pa.getRows(null, false, "Prepare_Transportation_Quote_390", true, WorkDefIDs, false, 0, 0, 3, true, null, false);
//			System.out.println(taskids);
//			Vector<Vector<String>> taskids = pa.getIDs("Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 0, false, null, false);
			Vector<String> taskids = ps.getTimes(null, false, "Carrier_Appointment", true, "1", true, SubProcess.Complete, true, null, false, false);
			System.out.println(taskids);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLog(XLog log) {
		
		this.log = log;
		
	}
	
	public void updateLog(XLog log) {
		
		this.log = log;
		
	}

	
	@Override
	public Vector<String> getAllID() {
		
		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
				
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			result.add(strTraceID);
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public boolean isSubProcess(String WorkDefID, String Name) {
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
				
		String strTraceID = xceConceptExt.extractName(log);
		
		return (strTraceID.equals(Name) && getAllID().contains(WorkDefID));
		
	}

	@Override
	public Vector<String> getIDs(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = new LinkedList<VariableID>();
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID))) {
					
					for (XEvent event : trace) {
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
						Long timeStamp = null;
			
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						for (XAttribute attribute : cltAttributes) {
			
							if (attribute.getKey().startsWith("time:timestamp")) {
								
								timeStamp = ((XAttributeTimestamp) attribute).getValue().getTime();
								break;
								
							}
							
				    	}
						
						if(timeStamp != null) {
							variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
						}
					}
				
					idVariable.put(strTraceID, variables);
					
				}
				
			}
			
			String id = null;
			for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
				
				id = entry.getKey();
				LinkedList<VariableID> variables = entry.getValue();
				
				VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
				
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[0].toString().equals(VariablesID))) {
						
						result.add(id);
						
					}
				}
				
				if(end) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1].getTime(), Time, isTime);
				
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[arrayTimes.length-1].toString().equals(VariablesID))) {
						
						result.add(id);
						
					}
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getWorkDefIDs(String ID, boolean isID, String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {

		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = new LinkedList<VariableID>();
				
				if(Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
					
					for (XEvent event : trace) {
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
						Long timeStamp = null;
			
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						for (XAttribute attribute : cltAttributes) {
			
							if (attribute.getKey().startsWith("time:timestamp")) {
								
								timeStamp = ((XAttributeTimestamp) attribute).getValue().getTime();
								break;
								
							}
							
				    	}
						
						if(timeStamp != null) {
							variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
						}
					}
				
					idVariable.put(strTraceID, variables);
					
				}
				
			}
			
			String id = null;
			for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
				
				id = entry.getKey();
				LinkedList<VariableID> variables = entry.getValue();
				
				VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
				
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[0].toString().equals(VariablesID))) {
						
						result.add(id);
						
					}
				}
				
				if(end) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1].getTime(), Time, isTime);
				
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[arrayTimes.length-1].toString().equals(VariablesID))) {
						
						result.add(id);
						
					}
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getNames(String ID, boolean isID, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {
		
		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<VariableID> variables = new LinkedList<VariableID>();
			
			if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID)) && Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
				
				for (XEvent event : trace) {
					
					Collection<XAttribute> cltAttributes = event.getAttributes().values();
					Long timeStamp = null;
		
					String eventName = xceConceptExt.extractName(event);
					Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
					
					for (XAttribute attribute : cltAttributes) {
		
						if (attribute.getKey().startsWith("time:timestamp")) {
							
							timeStamp = ((XAttributeTimestamp) attribute).getValue().getTime();
							break;
							
						}
						
			    	}
					
					if(timeStamp != null) {
						variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
					}
				}
			
				idVariable.put(strTraceID, variables);
				
			}
			
		}
		
		for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			boolean start = checkTypeTime[0];
			boolean end = checkTypeTime[2];
			
			boolean add = false;
			if(start) {
				add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
			
				if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[0].toString().equals(VariablesID))) {
					
					result.add(name);
					
					return result;
					
				}
			}
			
			if(end) {
				add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1].getTime(), Time, isTime);
			
				if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[arrayTimes.length-1].toString().equals(VariablesID))) {
					
					result.add(name);
					
					return result;
					
				}
			}			
		}
		
		return null;
		
	}

	@Override
	public Vector<String> getTimes(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = new LinkedList<VariableID>();
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID)) && Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {

					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
																		
						if(timeStamp != null) {
							variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
						}
						
					}
				
					idVariable.put(strTraceID, variables);
					
				}
				
			}
			
			for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
				
				LinkedList<VariableID> variables = entry.getValue();
				
				VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
				Arrays.sort(arrayTimes);
			
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				if(start) {
				
					if(Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[0].toString().equals(VariablesID))) {
						
						result.add(arrayTimes[0].getTime().toString());
						
					}
					
				}
				
				if(end) {
				
					if(Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[arrayTimes.length-1].toString().equals(VariablesID))) {
						
						result.add(arrayTimes[arrayTimes.length-1].getTime().toString());
						
					}
					
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getVariablesIDs(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, boolean isList) {
		
		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = new LinkedList<VariableID>();
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID)) && Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
					
					for (XEvent event : trace) {
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
						Long timeStamp = null;
			
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						for (XAttribute attribute : cltAttributes) {
			
							if (attribute.getKey().startsWith("time:timestamp")) {
								
								timeStamp = ((XAttributeTimestamp) attribute).getValue().getTime();
								break;
								
							}
							
				    	}
						
						if(timeStamp != null) {
							variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
						}
					}
				
					idVariable.put(strTraceID, variables);
					
				}
				
			}
			
			String id = null;
			for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
				
				id = entry.getKey();
				LinkedList<VariableID> variables = entry.getValue();
				
				VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
				
					if(add) {
						
						result.add(arrayTimes[0].toString());
						
					}
				}
				
				if(end) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1].getTime(), Time, isTime);
				
					if(add) {
						
						result.add(arrayTimes[arrayTimes.length-1].toString());
						
					}
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, String WorkDefID, boolean isWorkDefID, long Time, int isTime, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<String> innerResult = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<VariableID>> idVariable = new HashMap<String, LinkedList<VariableID>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<VariableID> variables = new LinkedList<VariableID>();
				
				if(Prom_DatabaseUtilities.checkXAND(isWorkDefID, strTraceID.equals(WorkDefID)) && Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
					
					for (XEvent event : trace) {
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
						Long timeStamp = null;
			
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						
						for (XAttribute attribute : cltAttributes) {
			
							if (attribute.getKey().startsWith("time:timestamp")) {
								
								timeStamp = ((XAttributeTimestamp) attribute).getValue().getTime();
								break;
								
							}
							
				    	}
						
						if(timeStamp != null) {
							variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
						}
					}
				
					idVariable.put(strTraceID, variables);
					
				}
				
			}
			
			for(Entry<String, LinkedList<VariableID>> entry : idVariable.entrySet()) {
				LinkedList<VariableID> variables = entry.getValue();
				
				VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
					
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[0].toString().equals(VariablesID))) {
						
						innerResult = new Vector<String>();
						innerResult.add(arrayTimes[0].getCaseID());
						innerResult.add(arrayTimes[0].getCaseID());
						innerResult.add(name);
						innerResult.add(arrayTimes[0].getTime().toString());
						innerResult.add("CaseStart");
						innerResult.add(arrayTimes[0].toString());
						
						result.add(innerResult);
						
					}
					
				}
				
				if(end) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0].getTime(), Time, isTime);
				
					if(add && Prom_DatabaseUtilities.checkXAND(isVariablesID, arrayTimes[arrayTimes.length-1].toString().equals(VariablesID))) {
						
						innerResult = new Vector<String>();
						innerResult.add(arrayTimes[arrayTimes.length-1].getCaseID());
						innerResult.add(arrayTimes[arrayTimes.length-1].getCaseID());
						innerResult.add(name);
						innerResult.add(arrayTimes[arrayTimes.length-1].getTime().toString());
						innerResult.add("CaseStart");
						innerResult.add(arrayTimes[arrayTimes.length-1].toString());
						
						result.add(innerResult);
						
					}
					
				}				
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}
}
