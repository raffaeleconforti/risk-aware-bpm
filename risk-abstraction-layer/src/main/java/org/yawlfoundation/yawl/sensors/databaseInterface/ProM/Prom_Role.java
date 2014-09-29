package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom2.Element;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.util.*;
import java.util.Map.Entry;

public class Prom_Role extends Role {
	
	XLog log = null;
	String resources = null;
	Vector<String> listIDs = null;
	HashMap<String, String> index = new HashMap<String, String>();
	
	
	public void setLog(XLog log, String resources) {
		
		this.log = log;
		this.resources = resources;
		indexLog();
		
	}

	public void updateLog(XLog log) {

		this.log = log;
		index.clear();
		indexLog();
		
	}
	
	public void indexLog() {

		//Correct one but does not work using logs
		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> participants = e1.getChild("participants").getChildren("participant");
		for(Element participant : participants) {
			
			String key = participant.getAttributeValue("id");
			
			index.put(key, JDOMUtil.elementToString(participant));

		}
		
	}
	
	@Override
	public Vector<String> getAllID() {
		
		if(listIDs == null) {
			Vector<String> result = new Vector<String>();
			Element e1 = JDOMUtil.stringToElement(resources);
	
			List<Element> participants = e1.getChild("participants").getChildren("participant");
			for(Element participant : participants) {
				
				result.add(participant.getAttributeValue("id"));
				
			}
			
			if(result.size() != 0) {
				listIDs = result;
				return result;
			}
		}else {
			return listIDs;
		}
		return null;
		
	}

	@Override
	public Vector<String> getIDs(String Name, boolean isName, String Surname, boolean isSurname) {
		
		Vector<String> result = new Vector<String>();
		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> participants = e1.getChild("participants").getChildren("participant");
		for(Element participant : participants) {
			
			String firstName = participant.getChildText("firstname");
			String lastName = participant.getChildText("lastName");
			
			if(Prom_DatabaseUtilities.checkXAND(isName, firstName.equals(Name)) && Prom_DatabaseUtilities.checkXAND(isSurname, lastName.equals(Surname))) {
				result.add(participant.getAttributeValue("id"));
			}
			
		}
		
		if(result.size() != 0) return result;
		return null;
		
	}

	@Override
	public Vector<String> getNames(String RoleID) {
		
		Vector<String> result = new Vector<String>();
		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> participants = e1.getChild("participants").getChildren("participant");
		for(Element participant : participants) {
			
			if(participant.getAttributeValue("id").equals(RoleID)) {

				result.add(participant.getChildText("firstname"));
				result.add(participant.getChildText("lastName"));
				
			}
			
		}
		
		if(result.size() != 0) return result;
		return null;
		
	}
	
	@Override
	public String getRoleRInfo(String RoleID) {

		//Correct one but does not work using logs
		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> participants = e1.getChild("roles").getChildren("role");
		for(Element participant : participants) {
			
			if(participant.getAttributeValue("id").equals(RoleID)) {

				return JDOMUtil.elementToString(participant);
				
			}
			
		}
		
//		return null;
		
		return RoleID;
		
	}

	@Override
	public String getRoleInfo(String RoleID) {

		//Correct one but does not work using logs
//		Element e1 = JDOMUtil.stringToElement(resources);
//
//		List<Element> participants = e1.getChild("participants").getChildren("participant");
//		for(Element participant : participants) {
//			
//			if(participant.getAttributeValue("id").equals(RoleID)) {
//
//				return JDOMUtil.elementToString(participant);
//				
//			}
//			
//		}
		
		return index.get(RoleID);
		
//		return null;
		
//		return RoleID;
		
	}
	
	public HashMap<String, LinkedList<String>> getRolesInfo() {

		//Correct one but does not work using logs
		Element e1 = JDOMUtil.stringToElement(resources);
		
		HashMap<String, LinkedList<String>> map = new HashMap<String, LinkedList<String>>();

		List<Element> participants = e1.getChild("participants").getChildren("participant");
		for(Element participant : participants) {
			
			List<Element> roles = participant.getChild("roles").getChildren("role"); 
			for(Element role : roles) {

				LinkedList<String> part = null;
				if((part = map.get(role.getValue())) == null) {
					part = new LinkedList<String>();
					map.put(role.getValue(), part);
				}
				part.add(participant.getAttributeValue("id"));
				
			}
			
		}
		
		return map;
		
	}
	
	public HashMap<String, String> getRoles() {

		//Correct one but does not work using logs
		Element e1 = JDOMUtil.stringToElement(resources);
		
		HashMap<String, String> map = new HashMap<String, String>();

		List<Element> roles = e1.getChild("roles").getChildren("role");
		for(Element role : roles) {
			
			map.put(role.getAttributeValue("id"), role.getChildText("name"));
				
		}
		
		return map;
		
	}

	@Override
	public LinkedList<String> getParticipantWithRole(String role) {

		LinkedList<String> result = new LinkedList<String>();
		Element e1 = JDOMUtil.stringToElement(resources);
		
		List<Element> participants = e1.getChild("participants").getChildren("participant");
		for(Element participant : participants) {
			
			Element elementRoles = participant.getChild("roles");
			
			if(elementRoles != null) {
				
				List<Element> roles = elementRoles.getChildren("role");
				
				for(Element elementRole : roles) {
					
					if(elementRole.getValue().equals(role)) {
						
//						result.add(JDOMUtil.elementToString(participant)); //Correct one but does not work using logs
						result.add(participant.getAttributeValue("id"));
						
					}
					
				}
				
			}
			
		}
		
		if(result.size() != 0) return result;
		return null;
		
	}

	@Override
	public String getRoleName(String role) {

		Element e1 = JDOMUtil.stringToElement(resources);

		List<Element> roles = e1.getChild("roles").getChildren("role");
		for(Element elementRole : roles) {
			
			if(elementRole.getAttributeValue("id").equals(role)) {

				return elementRole.getChildText("name");
				
			}
			
		}
		
		return null;
		
	}
	
	@Override
	public LinkedList<String[]> getTasksOffered(String participantID) {

		LinkedList<String[]> result = new LinkedList<String[]>();
		
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
				
				String resource = null;
				Long timeStamp = null;
	
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				
				task = new TaskID(strTraceID, eventName);

				resource = xoeOrganizationalExt.extractResource(event);
				
				timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				if(timeStamp != null && resource != null && resource.equals(participantID)) {
					if((variables = idVariable.get(task)) == null) {
						variables = new LinkedList<VariableID>();
						idVariable.put(task, variables);
					}
					variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
				}
						
			}
				
		}
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
						
			boolean yes = false;	

			for(VariableID var : arrayTimes) {
								
				boolean add = false;
			
				add = (var.getEventType() == VariableID.scheduled);
				
				if(add) {
					
					yes = true;
					
				}

				add = (var.getEventType() == VariableID.executing);
					
				if(add) {
					
					yes = false;
					
				}
				
				add = (var.getEventType() == VariableID.completed);
					
				if(add) {

					yes = false;
					
				}
			}
			
			if(yes) {
				result.add(new String[] {id.getCaseID(), id.getEventName()});
				System.out.println(result);
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<String[]> getTasksAllocated(String participantID) {

		LinkedList<String[]> result = new LinkedList<String[]>();
		
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
				
				String resource = null;
				Long timeStamp = null;
	
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				
				task = new TaskID(strTraceID, eventName);

				resource = xoeOrganizationalExt.extractResource(event);
				
				timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				if(timeStamp != null && resource != null && resource.equals(participantID)) {
					if((variables = idVariable.get(task)) == null) {
						variables = new LinkedList<VariableID>();
						idVariable.put(task, variables);
					}
					variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
				}
						
			}
				
		}
		
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
						
			boolean yes = false;	

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				add = (var.getEventType() == VariableID.scheduled);
				
				if(add) {
					
					yes = true;
					
				}

				add = (var.getEventType() == VariableID.executing);
					
				if(add) {
					
					yes = false;
					
				}
				
				add = (var.getEventType() == VariableID.completed);
					
				if(add) {

					yes = false;
					
				}
			}
			
			if(yes) {
				result.add(new String[] {id.getCaseID(), id.getEventName()});
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<String[]> getTasksStarted(String participantID) {

		LinkedList<String[]> result = new LinkedList<String[]>();
		
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
				
				String resource = null;
				Long timeStamp = null;
	
				String eventName = xceConceptExt.extractName(event);
				Integer eventType = Prom_DatabaseUtilities.getEventType(xleLifecycletExt.extractTransition(event));
				
				task = new TaskID(strTraceID, eventName);
				
				resource = xoeOrganizationalExt.extractResource(event);
				
				timeStamp = xteTimeExt.extractTimestamp(event).getTime();
				
				if(timeStamp != null && resource != null && resource.equals(participantID)) {
					if((variables = idVariable.get(task)) == null) {
						variables = new LinkedList<VariableID>();
						idVariable.put(task, variables);
					}
					variables.add(new VariableID(strTraceID, eventName, eventType, timeStamp));
				}
						
			}
				
		}
		
		
		TaskID id = null;
		for(Entry<TaskID, LinkedList<VariableID>> entry : idVariable.entrySet()) {
			
			id = entry.getKey();
			
			LinkedList<VariableID> variables = entry.getValue();
			
			VariableID[] arrayTimes = variables.toArray(new VariableID[0]);
			Arrays.sort(arrayTimes);
						
			boolean yes = false;	

			for(VariableID var : arrayTimes) {
				
				boolean add = false;
			
				add = (var.getEventType() == VariableID.scheduled);
				
				if(add) {
					
					yes = false;
					
				}

				add = (var.getEventType() == VariableID.executing);
					
				if(add) {
					
					yes = true;
					
				}
				
				add = (var.getEventType() == VariableID.completed);
					
				if(add) {

					yes = false;
					
				}
			}
			
			if(yes) {
				result.add(new String[] {id.getCaseID(), id.getEventName()});
			}
			
		}
		
		if(result.size() > 0) return result;
		else return null;
		
	}

}
