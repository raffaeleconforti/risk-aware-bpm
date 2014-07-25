package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;

public class Prom_Variable extends Variable {
	
	public final static String org = "org:";
	public final static String time = "time:";
	public final static String concept = "concept:";
	public final static String lifecycle = "lifecycle:";
	
	XLog log = null;
	String specification = null;
	HashMap<String, Collection<XAttribute>> index = new HashMap<String, Collection<XAttribute>>();
	HashMap<String, VariableID> indexVariableID = new HashMap<String, VariableID>();
	HashMap<String, LinkedList<String>> indexTaskID = new HashMap<String, LinkedList<String>>();
	
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
			
			Prom_Variable pv = new Prom_Variable();
			pv.setLog(log, specification);
			
			LinkedList<String> WorkDefIDs = new LinkedList<String>();
			WorkDefIDs.add("1");
//			Vector<Vector<String>> taskids = pa.getRows(null, false, "Prepare_Transportation_Quote_390", true, WorkDefIDs, false, 0, 0, 3, true, null, false);
//			System.out.println(taskids);
//			Vector<Vector<String>> taskids = pa.getIDs("Prepare_Transportation_Quote_390", true, WorkDefIDs, true, 0, false, null, false);
			
//			System.out.println(taskids);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setLog(XLog log, String specification) {
		
		this.log = log;
		this.specification = specification;
		indexLog();
		
	}

	public void updateLog(XLog log) {

		this.log = log;
		index.clear();
		indexVariableID.clear();
		indexTaskID.clear();
		indexLog();
		
	}
	
	private void indexLog() {

		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		VariableID v = null;
		TaskID t = null;
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);

				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
					
				v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
				t = new TaskID(strTraceID, eventName);
				
				index.put(v.toString(), cltAttributes);
				indexVariableID.put(v.toString(), v);
				
				LinkedList<String> l = null;
				if((l = indexTaskID.get(t.toString())) == null) {
					l = new LinkedList<String>();
					indexTaskID.put(t.toString(), l);
				}
				
				l.add(v.toString());
				
			}
			
		}
		
	}
	
	@Override
	public Vector<String> getAllID() {

		HashSet<String> result = new HashSet<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
				
				boolean variable = false;
				
				for (XAttribute attribute : cltAttributes) {
			     
					if (attribute.getKey().startsWith(org) && 
							attribute.getKey().startsWith(time) && 
							attribute.getKey().startsWith(concept) && 
							attribute.getKey().startsWith(lifecycle)) {
						
						variable = true;
						break;
						
					}
					
		    	}
				
				if(variable) result.add((new VariableID(strTraceID, eventName, eventType, timeStamp)).toString());

			}
			
		}
		
		if(result.size() > 0) return new Vector<String>(result);
		else return null;
		
	}

	@Override
	public Vector<String> getIDs(String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID) {

		HashSet<String> result = new HashSet<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			TaskID task = null;
			VariableID v = null;
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
								
				boolean variable = false;
				
				task = new TaskID(strTraceID, eventName);
				
				if( ((TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
						&& Prom_DatabaseUtilities.checkIfTime(timeStamp, TimeStamp, isTimeStamp) 
						&& Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
					
					v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID))) {
						
						for (XAttribute attribute : cltAttributes) {
							
							if (!attribute.getKey().startsWith(org) && 
									!attribute.getKey().startsWith(time) && 
									!attribute.getKey().startsWith(concept) && 
									!attribute.getKey().startsWith(lifecycle)) {
						     
								if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
									
									String value = ((XAttributeLiteral) attribute).getValue();
									
									if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
									
										variable = true;
										break;
										
									}
									
								}
								
							}
							
				    	}
						
					}
					
				}
				
				if(variable) result.add(v.toString());

			}
			
		}
		
		if(result.size() > 0) return new Vector<String>(result);
		else return null;
		
	}

	@Override
	public Vector<String> getNames(String ID, boolean isID, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID) {

		HashSet<String> setResult = new HashSet<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			TaskID task = null;
			VariableID v = null;
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
								
				task = new TaskID(strTraceID, eventName);
				
				if( ((TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
						&& Prom_DatabaseUtilities.checkIfTime(timeStamp, TimeStamp, isTimeStamp) 
						&& Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
					
					v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
						
						for (XAttribute attribute : cltAttributes) {
						    
							if (!attribute.getKey().startsWith(org) && 
									!attribute.getKey().startsWith(time) && 
									!attribute.getKey().startsWith(concept) && 
									!attribute.getKey().startsWith(lifecycle)) {
							
								String value = ((XAttributeLiteral) attribute).getValue();
							
								if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
								
									setResult.add(attribute.getKey());
									
								}
								
							}
								
				    	}
						
					}
					
				}

			}
			
		}
		
		if(setResult.size() > 0) return new Vector<String>(setResult);
		else return null;
		
	}

	@Override
	public Vector<String> getTimeStamps(String ID, boolean isID, String Name, boolean isName, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID) {

		HashSet<String> setResult = new HashSet<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			TaskID task = null;
			VariableID v = null;
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
								
				task = new TaskID(strTraceID, eventName);
				
				if( ((TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
						&& Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
					
					v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
						
						boolean ok = false;
						
						for (XAttribute attribute : cltAttributes) {
						    
							if (!attribute.getKey().startsWith(org) && 
									!attribute.getKey().startsWith(time) && 
									!attribute.getKey().startsWith(concept) && 
									!attribute.getKey().startsWith(lifecycle)) {
								
								if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
							
									String value = ((XAttributeLiteral) attribute).getValue();
								
									if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
									
										ok = true;
										
									}
									
								}
								
							}
								
				    	}
						
						if(ok) {
							
							setResult.add(timeStamp.toString());
							
						}
						
					}
					
				}

			}
			
		}
		
		if(setResult.size() > 0) return new Vector<String>(setResult);
		else return null;
		
	}

	@Override
	public Vector<String> getValues(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID, boolean isActivity) {

		HashSet<String> setResult = new HashSet<String>();
		
		if(isActivity) {
			
			XConceptExtension xceConceptExt = XConceptExtension.instance();
			XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
			XTimeExtension xteTimeExt = XTimeExtension.instance();
			
			if(isVariablesID) {
				
				VariableID v = indexVariableID.get(VariablesID);
				
				Collection<XAttribute> cltAttributes = index.get(VariablesID);
					
				if(v.getTime() != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
					
					for (XAttribute attribute : cltAttributes) {
					    
						if (!attribute.getKey().startsWith(org) && 
								!attribute.getKey().startsWith(time) && 
								!attribute.getKey().startsWith(concept) && 
								!attribute.getKey().startsWith(lifecycle)) {
							
							if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
						
								if(attribute instanceof XAttributeLiteral) {
									setResult.add(((XAttributeLiteral) attribute).getValue());
								}else if(attribute instanceof XAttributeDiscrete) {
									setResult.add(""+((XAttributeDiscrete) attribute).getValue());
								}else if(attribute instanceof XAttributeContinuous) {
									setResult.add(""+((XAttributeContinuous) attribute).getValue());
								}else if(attribute instanceof XAttributeBoolean) {
									setResult.add(""+((XAttributeBoolean) attribute).getValue());
								}
							}
							
						}
							
			    	}
					
				}
				
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					TaskID task = null;
					VariableID v = null;
					
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
										
						task = new TaskID(strTraceID, eventName);
						
						if( ((TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
								&& Prom_DatabaseUtilities.checkIfTime(timeStamp, TimeStamp, isTimeStamp) 
								&& Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
							
							v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
							
							if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
								
								for (XAttribute attribute : cltAttributes) {
								    
									if (!attribute.getKey().startsWith(org) && 
											!attribute.getKey().startsWith(time) && 
											!attribute.getKey().startsWith(concept) && 
											!attribute.getKey().startsWith(lifecycle)) {
										
										if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
									
											setResult.add(((XAttributeLiteral) attribute).getValue());
											
										}
										
									}
										
						    	}
								
							}
							
						}
		
					}
					
				}
			
			}
				
		}else {
			
			
			
		}
		
		if(setResult.size() > 0) return new Vector<String>(setResult);
		else return null;
		
	}

	@Override
	public Vector<String> getTaskIDs(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String VariablesID, boolean isVariablesID) {

		HashSet<String> setResult = new HashSet<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			TaskID task = null;
			VariableID v = null;
			
			for (XEvent event : trace) {
				
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				Collection<XAttribute> cltAttributes = event.getAttributes().values();
								
				task = new TaskID(strTraceID, eventName);
				
				boolean ok = false;
				
				if( ((TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
						&& Prom_DatabaseUtilities.checkIfTime(timeStamp, TimeStamp, isTimeStamp)) {
					
					v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
					
					if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
						
						for (XAttribute attribute : cltAttributes) {
						    
							if (!attribute.getKey().startsWith(org) && 
									!attribute.getKey().startsWith(time) && 
									!attribute.getKey().startsWith(concept) && 
									!attribute.getKey().startsWith(lifecycle)) {
								
								if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
							
									String value = ((XAttributeLiteral) attribute).getValue();
									
									if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
									
										ok = true;
										
									}
									
								}
								
							}
								
				    	}
						
					}
					
				}
				
				if(ok) setResult.add(task.toString());

			}
			
		}
		
		if(setResult.size() > 0) return new Vector<String>(setResult);
		else return null;
		
	}

	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, long TimeStamp, int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID, String VariablesID, boolean isVariablesID, boolean isActivity) {

		
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<String> innerResult = null;
		
		if(isActivity) {
		
			XConceptExtension xceConceptExt = XConceptExtension.instance();
			XLifecycleExtension xleLifecycletExt = XLifecycleExtension.instance();
			XTimeExtension xteTimeExt = XTimeExtension.instance();
			
			if(isVariablesID) {
				
				VariableID v = indexVariableID.get(VariablesID);
				
				Collection<XAttribute> cltAttributes = index.get(VariablesID);
				
				if(v == null) System.out.println(VariablesID+" "+Name);
				if(v.getTime() != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
									
					for (XAttribute attribute : cltAttributes) {

						if (!attribute.getKey().startsWith(org) && 
								!attribute.getKey().startsWith(time) && 
								!attribute.getKey().startsWith(concept) && 
								!attribute.getKey().startsWith(lifecycle)) {

							if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
						
								String value = null;
								if(attribute instanceof XAttributeLiteral) value = ((XAttributeLiteral) attribute).getValue();
								else if(attribute instanceof XAttributeBoolean) value = Boolean.toString(((XAttributeBoolean) attribute).getValue());
								else if(attribute instanceof XAttributeContinuous) value = Double.toString(((XAttributeContinuous) attribute).getValue());
								else if(attribute instanceof XAttributeDiscrete) value = Long.toString(((XAttributeDiscrete) attribute).getValue());
								
								if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
									
									innerResult = new Vector<String>();
									
									innerResult.add(v.toString());
									innerResult.add(attribute.getKey());
									innerResult.add(value);
									
									TaskID t = new TaskID(v.getCaseID(), v.getEventName());
									
									innerResult.add(t.toString());
									innerResult.add(v.toString());	
									innerResult.add(v.getTime().toString());									
									
									result.add(innerResult);
									
								}
								
							}
							
						}
							
			    	}
					
				}
			}else if(isTaskID){
				
				for(String varID : indexTaskID.get(TaskID)) {
				
					VariableID v = indexVariableID.get(varID);
					
					Collection<XAttribute> cltAttributes = index.get(varID);
					
					if(v.getTime() != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
										
						for (XAttribute attribute : cltAttributes) {
	
							if (!attribute.getKey().startsWith(org) && 
									!attribute.getKey().startsWith(time) && 
									!attribute.getKey().startsWith(concept) && 
									!attribute.getKey().startsWith(lifecycle)) {
	
								if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
							
									String value = null;
									if(attribute instanceof XAttributeLiteral) value = ((XAttributeLiteral) attribute).getValue();
									else if(attribute instanceof XAttributeBoolean) value = Boolean.toString(((XAttributeBoolean) attribute).getValue());
									else if(attribute instanceof XAttributeContinuous) value = Double.toString(((XAttributeContinuous) attribute).getValue());
									else if(attribute instanceof XAttributeDiscrete) value = Long.toString(((XAttributeDiscrete) attribute).getValue());
									
									if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
										
										innerResult = new Vector<String>();
										
										innerResult.add(v.toString());
										innerResult.add(attribute.getKey());
										innerResult.add(value);
										
										TaskID t = new TaskID(v.getCaseID(), v.getEventName());
										
										innerResult.add(t.toString());
										innerResult.add(v.toString());	
										innerResult.add(v.getTime().toString());									
										
										result.add(innerResult);
										
									}
									
								}
								
							}
								
				    	}
						
					}
					
				}
			}else {
				for (XTrace trace : log) {
					
					String strTraceID = xceConceptExt.extractName(trace);
					TaskID task = null;
					VariableID v = null;
					
					for (XEvent event : trace) {
						
						String eventName = xceConceptExt.extractName(event);
						Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
						Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
						
						Collection<XAttribute> cltAttributes = event.getAttributes().values();
										
						task = new TaskID(strTraceID, eventName);
						
						if(eventType != null) {
							if(((TypeAssigment == 0) || (TypeAssigment == 1 && eventType == VariableID.scheduled) || (TypeAssigment == 2 && eventType == VariableID.completed))
									&& Prom_DatabaseUtilities.checkIfTime(timeStamp, TimeStamp, isTimeStamp) 
									&& Prom_DatabaseUtilities.checkXAND(isTaskID, task.toString().equals(TaskID))) {
								
								v = new VariableID(strTraceID, eventName, eventType, timeStamp); 
								
								if(timeStamp != null && Prom_DatabaseUtilities.checkXAND(isVariablesID, v.toString().equals(VariablesID)) && Prom_DatabaseUtilities.checkXAND(isID, v.toString().equals(ID))) {
									
									for (XAttribute attribute : cltAttributes) {
	
										if (!attribute.getKey().startsWith(org) && 
												!attribute.getKey().startsWith(time) && 
												!attribute.getKey().startsWith(concept) && 
												!attribute.getKey().startsWith(lifecycle)) {
	
											if (Prom_DatabaseUtilities.checkXAND(isName, (Name != null && attribute.getKey().startsWith(Name)))) {
										
												String value = null;
												if(attribute instanceof XAttributeLiteral) value = ((XAttributeLiteral) attribute).getValue();
												else if(attribute instanceof XAttributeBoolean) value = Boolean.toString(((XAttributeBoolean) attribute).getValue());
												else if(attribute instanceof XAttributeContinuous) value = Double.toString(((XAttributeContinuous) attribute).getValue());
												else if(attribute instanceof XAttributeDiscrete) value = Long.toString(((XAttributeDiscrete) attribute).getValue());
												
												if(Prom_DatabaseUtilities.checkXAND(isValue, value.equals(Value))) {
													
													innerResult = new Vector<String>();
													
													innerResult.add(v.toString());
													innerResult.add(attribute.getKey());
													innerResult.add(value);
													innerResult.add(task.toString());
													innerResult.add(v.toString());	
													innerResult.add(timeStamp.toString());									
													
													result.add(innerResult);
													
												}
												
											}
											
										}
											
							    	}
									
								}
								
							}
							
						}
		
					}
					
				}
			}
			
		}else {
			
			
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

}
