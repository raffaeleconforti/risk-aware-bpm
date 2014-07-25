package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.util.*;
import java.util.Map.Entry;

public class Prom_WorkFlowDefinition extends WorkflowDefinition {
	
	String specification = null;
	String xmlLog = null;
	XLog log = null;
	String URI = null;
	HashMap<String, LinkedList<Long>> index = new HashMap<String, LinkedList<Long>>(); 
	
//	Namespace ns

//	Vector<String> result = new Vector<String>();
//	
//	XConceptExtension xceConceptExt = XConceptExtension.instance();
//	
//	String strLogName = xceConceptExt.extractName(log);
//	
//	for (XTrace trace : log) {
//		for (XEvent event : trace) {
//			String strTraceID = xceConceptExt.extractName(trace);
//			Collection<XAttribute> cltAttributes = event.getAttributes().values();
//			
//			for (XAttribute attribute : cltAttributes) {
//				String strCostTypePrefix = "cost:type";
//				String strResourcePrefix = "org:resource";
//		     
//				//Get currency value for that event
//				if (attribute.getKey().startsWith("cost:currency"))
//					strLogCurrency = ((XAttributeLiteral) attribute).getValue();
//
//				//Get resource name for that event
//				if (attribute.getKey().startsWith(strResourcePrefix)) {
//					strResourceName = ((XAttributeLiteral) attribute).getValue();
//					blResource = true;
//				}
//				
//				//Get cost value for that event
//				if (attribute.getKey().startsWith(strCostTypePrefix)) {
//					alCost.add(((XAttributeContinuous) attribute).getValue());
//					blCost = true;
//				}
//	    	}
//		}
//	}
//	
//	if(result.size() > 0) return result;
//	else return null;

	public void setLog(String specification, XLog log) {
		
		this.specification = specification;
		this.xmlLog = null;
		this.log = log;
		indexLog();
		
	}

	public void updateLog(XLog log) {
		
		this.xmlLog = null;
		this.log = log;
		index.clear();
		indexLog();
		
	}
	
	private void indexLog() {
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		URI = xceConceptExt.extractName(log);
		
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<Long> times = new LinkedList<Long>();
			
			for (XEvent event : trace) {
				
				Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				if(timeStamp != null) {
					times.add(timeStamp);
				}
			}
			
			index.put(strTraceID, times);
			
		}	
		
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
	public Vector<String> getIDs(String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version) {		

		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		String name = xceConceptExt.extractName(log);
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
							
			String id = null;
			for(Entry<String, LinkedList<Long>> entry : index.entrySet()) {
				
				id = entry.getKey();
				LinkedList<Long> times = entry.getValue();
				Long[] arrayTimes = times.toArray(new Long[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean keepGoing = true;
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0], Time, isTime);
				
					if(add) {
						
						result.add(id);
						
						keepGoing = false;
						
					}
				}
				
				if(end && keepGoing) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1], Time, isTime);
				
					if(add) {
						
						result.add(id);
						
					}
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getNames(String ID, boolean isID, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version) {
		
		Vector<String> result = new Vector<String>();
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<Long>> idTime = new HashMap<String, LinkedList<Long>>();
			
		for (XTrace trace : log) {
			
			String strTraceID = xceConceptExt.extractName(trace);
			LinkedList<Long> times = new LinkedList<Long>();
			
			if(Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
				
				for (XEvent event : trace) {
					
					Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
					
					if(timeStamp != null) {
						times.add(timeStamp);
					}
				}
				
				idTime.put(strTraceID, times);
				
			}
			
		}
		
		for(Entry<String, LinkedList<Long>> entry : idTime.entrySet()) {
			LinkedList<Long> times = entry.getValue();
			Long[] arrayTimes = times.toArray(new Long[0]);
			Arrays.sort(arrayTimes);
			
			boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
			boolean start = checkTypeTime[0];
			boolean end = checkTypeTime[2];
			
			boolean add = false;
			if(start) {
				add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0], Time, isTime);
			
				if(add) {
					result.add(name);
					
					return result;
				}
			}
			
			if(end) {
				add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1], Time, isTime);
			
				if(add) {
					result.add(name);
					
					return result;
				}
			}			
		}
		
		return null;
		
	}

	@Override
	public String getURI(String ID) {
		
		return URI;
		
	}

	@Override
	public String getVersion(String ID) {
		
		return URI;
		
	}

	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name, boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime, String URI, String Version) {
		
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<String> innerResult = null;
		
		XConceptExtension xceConceptExt = XConceptExtension.instance();
		XTimeExtension xteTimeExt = XTimeExtension.instance();
		
		String name = xceConceptExt.extractName(log);
		
		HashMap<String, LinkedList<Long>> idTime = new HashMap<String, LinkedList<Long>>();
			
		if(Prom_DatabaseUtilities.checkXAND(isName, name.equals(Name))) {
			
			for (XTrace trace : log) {
				
				String strTraceID = xceConceptExt.extractName(trace);
				LinkedList<Long> times = new LinkedList<Long>();
				
				if(Prom_DatabaseUtilities.checkXAND(isID, strTraceID.equals(ID))) {
					
					for (XEvent event : trace) {
						
						Long timeStamp = xteTimeExt.extractTimestamp(event).getTime();
						
						if(timeStamp != null) {
							times.add(timeStamp);
						}
					}
					
					idTime.put(strTraceID, times);
					
				}
				
			}
		
			String id = null;
			for(Entry<String, LinkedList<Long>> entry : idTime.entrySet()) {
				
				id = entry.getKey();
				LinkedList<Long> times = entry.getValue();
				Long[] arrayTimes = times.toArray(new Long[0]);
				Arrays.sort(arrayTimes);
				
				boolean[] checkTypeTime = Prom_DatabaseUtilities.checkTypeTime(TypeTime, isTypeTime);
				boolean start = checkTypeTime[0];
				boolean end = checkTypeTime[2];
				
				boolean add = false;
				if(start) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[0], Time, isTime);
				
					if(add) {
						innerResult = new Vector<String>();
						innerResult.add(id);
						innerResult.add(name);
						innerResult.add(arrayTimes[0].toString());
						innerResult.add("CaseStart");
						
						result.add(innerResult);
					}
				}
				
				if(end) {
					add = Prom_DatabaseUtilities.checkIfTime(arrayTimes[arrayTimes.length-1], Time, isTime);
				
					if(add) {
						innerResult = new Vector<String>();
						innerResult.add(id);
						innerResult.add(name);
						innerResult.add(arrayTimes[arrayTimes.length-1].toString());
						innerResult.add("CaseComplete");
						
						result.add(innerResult);
					}
				}			
			}
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSensors(String ID, String URI, String Version) {

		Element e = JDOMUtil.stringToDocument(specification).getRootElement();
		for(Element spec: (List<Element>)e.getChildren()) {
			if("specification".equals(spec.getName())) {
				for(Element sensors : (List<Element>)spec.getChildren()) {
					if("sensors".equals(sensors.getName())) {
						return JDOMUtil.elementToString(sensors);
					}
				}
			}
		}
		return null;
		
	}

	@Override
	public String getSpecification(String ID, String URI, String Version) {

		return specification;
		
	}
	
	@Override
	public String getSpecification(String URI) {

		return specification;
		
	}

	@Override
	public String getOpenXESLog(String ID, String URI, String Version) {
		if(xmlLog == null) {
			xmlLog = LogConverter.getXMLlog(log);
		}
		return xmlLog;
		
	}

}
