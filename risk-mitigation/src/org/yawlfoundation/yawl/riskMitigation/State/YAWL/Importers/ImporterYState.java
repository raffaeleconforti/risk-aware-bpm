package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Namespace;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessUtilities;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Net;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Place;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.ExecutionGraph.GraphCreator;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.ExecutionGraph.NodeGraph;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;


public class ImporterYState {

	public static final String xmlID = "id";

	public static final String offerString = "offer";
	public static final String allocateString = "allocate";
	public static final String startString = "start";
	public static final String completeString = "complete";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void importModel(InterfaceManager im, StateYAWLProcess status, HashMap<String, Object> inputVariables, HashMap<String, Resource> resourcesMap, HashMap<String, Integer> taskWithToken, List<String> tasksLog) {
		String root = null;
		String inputCondition = null;
		String outputCondition = null;
		LinkedList<Task> tmpTask = new LinkedList<Task>();
		LinkedList<Place> tmpPlace = new LinkedList<Place>();
		LinkedList<Net> tmpNet = new LinkedList<Net>();

		status.setResourcesMap(resourcesMap);
		
		Net[] nets = null;
		Task[] tasks = null;
		Place[] places = null;
		
		HashMap<String, LinkedList<Object[]>[]> dataFlows = new HashMap<String, LinkedList<Object[]>[]>();
		status.setDataFlows(dataFlows);
		
		HashSet<String> notSkippable = new HashSet<String>();
		status.setNotSkippable(notSkippable);
		
		status.setVariablesOriginal(inputVariables);
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		String variable = null;
		Object o = null;
		for(Entry<String, Object> entry : inputVariables.entrySet()) {
			variable = entry.getKey();
			o = entry.getValue();
			if(o instanceof LinkedList) {
				LinkedList l = (LinkedList) o;
				variables.put(variable, l.clone());
			}else {
				variables.put(variable, o);
			}
		}
		status.setVariables(variables);

		Element e = JDOMUtil.stringToElement(status.getSpecificationXML());
		Namespace ns = e.getNamespace();
		Namespace ns2 = e.getNamespace("xsi");
		String sensor = JDOMUtil.elementToString(e.getChild("specification", ns).getChild("sensors", ns));
		List<Element> dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals(YSensorUtilities.trueString)) {
				int net = tmpNet.size();
				root = ele.getAttributeValue(xmlID);		
				
				for(Element localVariable : (List<Element>) ele.getChildren("localVariable", ns)) {
					String name = localVariable.getChild("name", ns).getValue();
					LinkedList<Object[]>[] lists = new LinkedList[2];
					lists[0] = new LinkedList<Object[]>();
					lists[1] = new LinkedList<Object[]>();
					dataFlows.put(name, lists);
				}
				
				tmpNet.addLast(new Net(root));
				Element pce = ele.getChild("processControlElements", ns);
				
				inputCondition = pce.getChild("inputCondition", ns).getAttributeValue(xmlID);
				tmpPlace.add(new Place(inputCondition, true, false));
				
				for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
					tmpPlace.add(new Place(place.getAttributeValue(xmlID), false, true));
				}
				
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					boolean isComposite = false;
					boolean isMultiple = false;
					
					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
					
					if(task.getChild("decomposesTo", ns)!=null) {
						
						decomposeTo(im, status, task, ns, ns2, e, "", net, isComposite, tmpNet, tmpPlace, tmpTask);
						
					}else {
						notSkippable.add(task.getAttributeValue(xmlID));
					}
					if(!isComposite) {
						
						generateTaskVariables(im, status, task, ns, net, isComposite, isMultiple, tmpTask);
						
					}
					
				}
				
				outputCondition = pce.getChild("outputCondition", ns).getAttributeValue(xmlID);
				tmpPlace.add(new Place(outputCondition, false, true));
			}
		}
		
//		int netPos = 0;
		int taskPos = 0;
		int placePos = 0;
		dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals(YSensorUtilities.trueString)) {
				
				for(int i = 0; i<tmpNet.size(); i++) {
					Net n = tmpNet.get(i);
					if(n.netName.equals(ele.getAttributeValue(xmlID))) {
//						netPos = i;
						break;
					}
				}
				
				Element pce = ele.getChild("processControlElements", ns);
				
				for(Element start : (List<Element>) pce.getChildren()){
	//				Element start = pce.getChild("inputCondition", ns);
					
					String startID = start.getAttributeValue(xmlID);
					String startType = start.getName();
					if(startType.equals("condition") || startType.equals("inputCondition") || startType.equals("outputCondition")) {
						for(int i = 0; i<tmpPlace.size(); i++) {
							Place p = tmpPlace.get(i);
							if(p.engineID.equals(startID)) {
								placePos = i;
								LinkedList<Integer> linkedTo = p.linkedTo;
								
								YStateProcessUtilities.generateTask(start, ns, ns2, pce, e, "", tmpNet, tmpPlace, tmpTask, 0, tmpPlace.size(), 0, tmpTask.size(), placePos+tmpTask.size(), linkedTo);
								
								break;
							}
						}
					}else {
						for(int i = 0; i<tmpTask.size(); i++) {
							Task t = tmpTask.get(i);
							if(t.engineID.equals(startID) || t.engineID.equals(startID+"_close")) {
								taskPos = i;
								LinkedList<Integer> linkedTo = t.linkedTo;
								
								YStateProcessUtilities.generateTask(start, ns, ns2, pce, e, "", tmpNet, tmpPlace, tmpTask, 0, tmpPlace.size(), 0, tmpTask.size(), taskPos, linkedTo);
								
								break;
							}
						}
					}
				}
			}
		}
		
		nets = new Net[tmpNet.size()];
		
		for(int i = 0; i<nets.length; i++) {
			nets[i] = tmpNet.get(i);
		}
		
		tasks = new Task[tmpTask.size()];
		
		for(int i = 0; i<tasks.length; i++) {
			tasks[i] = tmpTask.get(i);
		}
		
		places = new Place[tmpPlace.size()];
		
		for(int i = 0; i<places.length; i++) {
			places[i] = tmpPlace.get(i);
		}

		status.setRoot(root);
		status.setTasksSize(tasks.length);
		status.setRemoved(new boolean[tmpTask.size()]);
		status.setNets(nets);
		status.setTasks(tasks);
		status.setPlaces(places);
		
		setTokens(status, tasksLog, taskWithToken, resourcesMap);
		
		status.initializeSensor(sensor);
		
		status.generateListTaskRelevant();
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void decomposeTo(InterfaceManager im, StateYAWLProcess status, Element task, Namespace ns, Namespace ns2, Element e, String compositeTaskID, int net, boolean isComposite, LinkedList<Net> tmpNet, LinkedList<Place> tmpPlace, LinkedList<Task> tmpTask) {
		StringBuffer sb = new StringBuffer(task.getAttributeValue(xmlID));
		if(!compositeTaskID.isEmpty()) {
			sb.append("_");
			sb.append(compositeTaskID);
		}
		String taskID = sb.toString();
		
		String id = task.getChild("decomposesTo", ns).getAttributeValue(xmlID);
		List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element eleSubNet : decSubNet) {
			if(eleSubNet.getAttributeValue(xmlID).equals(id)) {
				isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
				if(isComposite) {
					Object[] tmp = generateSubNet(im, status, eleSubNet, e, ns, ns2, taskID);
					tmpTask.add(new Task(null, taskID+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code")));
					tmpNet.addAll((LinkedList) tmp[0]);
					tmpTask.addAll((LinkedList) tmp[1]);
					tmpPlace.addAll((LinkedList) tmp[2]);
					tmpTask.add(new Task(null, taskID+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor"));
					
					YStateProcessUtilities.importMapping("startingMapping", ns, task, status.getDataFlows(), "_open", true);
					
					YStateProcessUtilities.dataInputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], status.getDataFlows(), taskID+"_open");
					
					YStateProcessUtilities.importMapping("completedMapping", ns, task, status.getDataFlows(), "_close", false);
					
					YStateProcessUtilities.dataOutputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], status.getDataFlows(), taskID+"_close");
				}
				if(compositeTaskID.isEmpty()) break;
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	private static void generateTaskVariables(InterfaceManager im, StateYAWLProcess status, Element task, Namespace ns, Integer net, boolean isComposite, boolean isMultiple, LinkedList<Task> tmpTask) {
		String name = null;
		if(task.getChild("name", ns)!=null) {
			name = task.getChild("name", ns).getValue();
		}
		
		YStateProcessUtilities.importMapping("startingMapping", ns, task, status.getDataFlows(), "", true);
		
		YStateProcessUtilities.importMapping("completedMapping", ns, task, status.getDataFlows(), "", false);
		
		Task t = new Task(name, task.getAttributeValue(xmlID), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"));
		createDefaultResource(im, t, task, ns);
		
		HashMap<String, Object> variablesOriginal = status.getVariablesOriginal();
		
		String mapping = null;
		Object o = null;
		for(Entry<String, Object> entry : variablesOriginal.entrySet()) {
			mapping = entry.getKey();
			o = entry.getValue();
			if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerResource)) {
				Element offer = JDOMUtil.stringToDocument((String) o).getRootElement().getChild(offerString);
				for(Element resource : (List<Element>) offer.getChildren()) {
					String xmlResource = JDOMUtil.elementToString(resource);
					t.isOffered = true;
					t.offerRes.add(xmlResource);
					t.originalOfferRes.add(xmlResource);
				}
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateResource)) {
				Element allocate = JDOMUtil.stringToDocument((String) o).getRootElement().getChild(allocateString);
				for(Element resource : (List<Element>) allocate.getChildren()) {
					String xmlResource = JDOMUtil.elementToString(resource);
					t.isAllocated = true;
					t.allocateRes = xmlResource;
					t.originalAllocateRes = xmlResource;
				}
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startResource)) {
				Element start = JDOMUtil.stringToDocument((String) o).getRootElement().getChild(startString);
				for(Element resource : (List<Element>) start.getChildren()) {
					String xmlResource = JDOMUtil.elementToString(resource);
					t.isStarted = true;
					t.startRes = xmlResource;
					t.originalStartRes = xmlResource;
				}
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isOffered)) {
				t.isOffered = new Boolean((String) o);
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isAllocated)) {
				t.isAllocated = new Boolean((String) o);
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isStarted)) {
				t.isStarted = new Boolean((String) o);
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isCompleted)) {
				t.isCompleted = new Boolean((String) o);
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerTime)) {
				if(o != null) t.isOffered = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateTime)) {
				if(o != null) t.isAllocated = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startTime)) {
				if(o != null) t.isStarted = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.completeTime)) {
				if(o != null) t.isCompleted = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.passTime)) {
				if(o != null) t.isOffered = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerTimeInMillis)) {
				if(o != null) t.isOffered = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateTimeInMillis)) {
				if(o != null) t.isAllocated = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startTimeInMillis)) {
				if(o != null) t.isStarted = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.completeTimeInMillis)) {
				if(o != null) t.isCompleted = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.passTimeInMillis)) {
				if(o != null) t.isOffered = true;
			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerDistribution)) {
				Element offer = JDOMUtil.stringToDocument((String) o).getRootElement().getChild(offerString);
				for(Element resource : (List<Element>) offer.getChildren()) {
					String xmlResource = JDOMUtil.elementToString(resource);
					t.offerResDef.add(xmlResource);
					t.originalOfferResDef.add(xmlResource);
				}
			}
		}
		
		if(t.isCompleted) {
			t.status = Task.Completed;
			t.originalStatus = Task.Completed;
		}else if(t.isStarted) {
			t.status = Task.Started;
			t.originalStatus = Task.Started;
		}else if(t.isAllocated) {
			t.status = Task.Allocated;
			t.originalStatus = Task.Allocated;
		}else if(t.isOffered) {
			t.status = Task.Offered;
			t.originalStatus = Task.Offered;
		}else {
			t.status = Task.Unoffered;
			t.originalStatus = Task.Unoffered;
		}
		
		tmpTask.add(t);
	}
	
	private static void setTokens(StateYAWLProcess status, List<String> tasksLog, HashMap<String, Integer> taskWithToken, HashMap<String, Resource> resourceMap) {
		
		GraphCreator gc = GraphCreator.getInstance();
		
		NodeGraph ng = gc.generateGraph(tasksLog, status.getTasks(), status.getPlaces());
		
		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
		
		LinkedList<NodeGraph> pending = gc.discoverLast(ng);
		
		String[] namePending = new String[pending.size()];
		NodeGraph ng1 = null;
		for(int i=0; i<pending.size(); i++) {
			ng1 = pending.get(i);
			namePending[i] = ng1.nodeName;
		}
		
		Arrays.sort(namePending);
		
		pile.add(ng);
		
		while(pile.size() > 0) {
			ng1 = pile.removeFirst();
			if(!pending.contains(ng1)) {
				for(Task t : status.getTasks()) {
					if(ng1.nodeName.equals(t.engineID)) {
						t.status = Task.Completed;
						t.originalStatus = Task.Completed;
					}
				}
			}
			for(NodeGraph ng2 : ng1.linkedTo) {
				if(!pile.contains(ng2)) {
					pile.addLast(ng2);
				}
			}
		}
		
		for(Task t : status.getTasks()) {
			if(Arrays.binarySearch(namePending, t.engineID) >= 0) {
				t.status = Task.Offered;
			}
			setResource(status, t, resourceMap);
		}
		
		String engineID = null;
		int val = 0;
		for(Task t : status.getTasks()) {
			for(Entry<String, Integer> entry : taskWithToken.entrySet()) {
				engineID = entry.getKey();
				if(engineID.equals(t.engineID)) {
					val = entry.getValue();
					t.status = val;
					t.originalStatus = val;
				setResource(status, t, resourceMap);
				}
			}
		}
		
		status.setPending(pending);
		status.setNg(ng);
	}

	@SuppressWarnings({ "unchecked"})
	private static Object[] generateSubNet(InterfaceManager im, StateYAWLProcess status, Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, String compositeTaskID) {
    	Object[] array = new Object[4];
    	
    	LinkedList<Net> tmpNet = new LinkedList<Net>();
    	LinkedList<Task> tmpTask = new LinkedList<Task>();
    	LinkedList<Place> tmpPlace = new LinkedList<Place>();
    	HashMap<String, LinkedList<Object[]>[]> dataFlows = new HashMap<String, LinkedList<Object[]>[]>(); 
    	
    	array[0] = tmpNet;
    	array[1] = tmpTask;
    	array[2] = tmpPlace;
    	array[3] = dataFlows;
    	
    	int net = tmpNet.size();
		tmpNet.addLast(new Net(subNetElement.getAttributeValue(xmlID)));
		
		Element pce = subNetElement.getChild("processControlElements", ns);
		
		String inputCondition = pce.getChild("inputCondition", ns).getAttributeValue(xmlID);
		tmpPlace.add(new Place(inputCondition+"_"+compositeTaskID, true, false));
		
		for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
			tmpPlace.add(new Place(place.getAttributeValue(xmlID), false, true));
		}
		
		for(Element task : (List<Element>) pce.getChildren("task", ns)) {
			boolean isComposite = false;
			boolean isMultiple = false;
			
			if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
			
			if(task.getChild("decomposesTo", ns)!=null) {
				
				decomposeTo(im, status, task, ns, ns2, specificationSet, compositeTaskID, net, isComposite, tmpNet, tmpPlace, tmpTask);
				
			}
			if(!isComposite) {
				
				generateTaskVariables(im, status, task, ns, net, isComposite, isMultiple, tmpTask);
				
			}
		}
		
		String outputCondition = pce.getChild("outputCondition", ns).getAttributeValue(xmlID);
		tmpPlace.add(new Place(outputCondition+"_"+compositeTaskID, false, true));
		
		return array;
    }
	
	@SuppressWarnings("unchecked")
	private static void createDefaultResource(InterfaceManager im, Task t, Element task, Namespace ns) {
		Element resourcing = task.getChild("resourcing", ns);
		if(resourcing != null) {
			if(resourcing.getChild(offerString, ns)!=null && resourcing.getChild(offerString, ns).getAttributeValue("initiator").equals("system")) {
				Element distributionSet = resourcing.getChild(offerString, ns).getChild("distributionSet", ns);
				if(distributionSet != null) {
					Element initialSet = distributionSet.getChild("initialSet", ns);
					if(initialSet != null) {
						for(Element set : (List<Element>) initialSet.getChildren()) {
							if(set.getName().equals(YSensorUtilities.participant)) {
								t.offerResDef.add(getParticipant(im, set.getValue()));
								t.originalOfferResDef.add(getParticipant(im, set.getValue()));
							}else if(set.getName().equals(YSensorUtilities.role)) {
								t.offerResDef.addAll(getResource(im, set.getValue()));
								t.originalOfferResDef.addAll(getResource(im, set.getValue()));
							}
						}
					}
				}
			}
		}
	}
	
	private static void setResource(StateYAWLProcess status, Task t, HashMap<String, Resource> resourceMap) {
		for(Entry<String, Resource> entry : resourceMap.entrySet()) {
			Resource resource = entry.getValue();
			if(resource.getOfferedList().contains(t.engineID) && t.status == Task.Offered) {
				t.offerRes.add(resource.getID());
				t.originalOfferRes.add(resource.getID());
			}else if(resource.getAllocatedList().contains(t.engineID) && t.status == Task.Allocated) {
				t.allocateRes = (resource.getID());
				t.originalAllocateRes = (resource.getID());
			}else if(resource.getStartedList().contains(t.engineID) && t.status == Task.Started) {
				t.startRes = (resource.getID());
				t.originalStartRes = (resource.getID());
			}
		}
//		if(t.status == Task.Offered && t.offerRes.isEmpty()) {
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			status.getResourcesMap().get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//			
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			status.getResourcesMap().get(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//			
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			status.getResourcesMap().get(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//		}
//		if(t.status == Task.Allocated && t.allocateRes == null) {
//			t.allocateRes  = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			t.originalAllocateRes  = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			status.getResourcesMap().get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addAllocate(t.taskName);
//		}
//		if(t.status == Task.Started && t.startRes == null) {
//			t.startRes = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			t.originalStartRes = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			status.getResourcesMap().get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addStart(t.taskName);
//		}
	}
	
	private static String getParticipant(InterfaceManager im, String participantID) {
		return im.getRoleLayer().getRoleInfo(participantID);
//		return JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
	}
	
	private static List<String> getResource(InterfaceManager im, String role) {
//		LinkedList<String> list = new LinkedList<String>();
		return im.getRoleLayer().getParticipantWithRole(role);
		
//		RoleInfo(participantID);
//		list.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//		list.add(JDOMUtil.formatXMLString("<participant id=\"PA-7933b9de-5aa6-4d9c-be12-d1a5cc8a8c67\"><userid>dvc</userid><firstname>Don Vito</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//		return list;
	}
}
