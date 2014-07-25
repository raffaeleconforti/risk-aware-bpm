package org.yawlfoundation.yawl.riskMitigation.State.YAWL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.Namespace;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Net;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Place;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.sensors.language.YExpression;;


public class YStateProcessUtilities {
	
	public static void main(String[] args) {
		
		String a = "(offerResource)";
		String b = "(offerResource)";
		String c = "Resource)";
		
		for(int i=0; i<10; i++) {
		long t1 = System.nanoTime();
		a.equals(b);
		long t2 = System.nanoTime();
		
		long t3 = System.nanoTime();
		a.endsWith(c);
		long t4 = System.nanoTime();
		
		System.out.println(t2-t1);
		System.out.println(t4-t3);
		System.out.println();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void importMapping(String stringMapping, Namespace ns, Element task, HashMap<String, LinkedList<Object[]>[]> dataFlows, String option, boolean inputOrOutput) {
    	if(task.getChild(stringMapping, ns)!=null) {
			for(Element mapping : (List<Element>) task.getChild(stringMapping, ns).getChildren("mapping", ns)) {
				String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
				String mapTo = mapping.getChild("mapTo", ns).getValue();
				if(inputOrOutput) dataInput(dataFlows, task.getAttributeValue("id")+option, query, mapTo);
				else dataOutput(dataFlows, task.getAttributeValue("id")+option, query, mapTo);
			}
		}
    }
    
	@SuppressWarnings("unchecked")
	private static void dataInput(HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task, String query, String mapTo) {
		Object[] obj = null;
    	StringTokenizer st = new StringTokenizer(query, "{}");
    	HashSet<String> set = new HashSet<String>();

    	data(set, st, false);
    	
    	for(String var : set) {
    		obj = new Object[2];
        	obj[0] = Task;
        	obj[1] = new LinkedList<String>();
        	((LinkedList<String>) obj[1]).add(mapTo);
        	dataFlows.get(var)[0].add(obj);
    	}
	}
    
    @SuppressWarnings("unchecked")
	private static void dataOutput(HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task, String query, String mapTo) {
    	Object[] obj = new Object[2];
    	obj[0] = Task;
    	obj[1] = new LinkedList<String>();
    	StringTokenizer st = new StringTokenizer(query, "{}");
    	HashSet<String> set = new HashSet<String>();
    	
    	data(set, st, false);
    	
    	((LinkedList<String>) obj[1]).addAll(set);
		dataFlows.get(mapTo)[1].add(obj);		
	}
    
    private static void data(HashSet<String> set, StringTokenizer st, boolean open) {
    	while(st.hasMoreTokens()) {
    		String s = st.nextToken();
    		if(!open & s.equals("{")) {
    			open = true;
    		}else if(open & s.equals("}")) {
    			open = false;
    		}else if(open) {
	    		s = s.substring(s.indexOf("/")+1);
	    		s = s.substring(s.indexOf("/")+1);
	    		if(s.contains("/")) s = s.substring(0, s.indexOf("/"));
	    		set.add(s);
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void dataInputNet(HashMap<String, LinkedList<Object[]>[]> netDataFlows, HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task) {
    	HashMap<String, LinkedList<String>> in = new HashMap<String, LinkedList<String>>();
    	for(Entry<String, LinkedList<Object[]>[]> entry : dataFlows.entrySet()) {
			LinkedList<Object[]>[] tmp = entry.getValue();
			LinkedList<Object[]> inputMapping = tmp[0];
			for(Object[] obj : inputMapping) {
				if(((String) obj[0]).equals(Task)) {
					in.put(entry.getKey(), (LinkedList<String>) obj[1]);
					break;
				}
			}
		}
		for(Entry<String, LinkedList<Object[]>[]> entry : netDataFlows.entrySet()) {
			for(Entry<String, LinkedList<String>> entry2 : in.entrySet()) {
				for(String subVar : entry2.getValue()) {
					if(entry.getKey().equals(subVar)) {
						LinkedList<Object[]> obj = entry.getValue()[0];
						dataFlows.get(entry2.getKey())[0].addAll(obj);
					}
				}
			}
		}
	}
    
    @SuppressWarnings("unchecked")
	public static void dataOutputNet(HashMap<String, LinkedList<Object[]>[]> netDataFlows, HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task) {
		HashMap<String, LinkedList<String>> out = new HashMap<String, LinkedList<String>>();
		for(Entry<String, LinkedList<Object[]>[]> entry : dataFlows.entrySet()) {
			LinkedList<Object[]>[] tmp = entry.getValue();
			LinkedList<Object[]> outputMapping = tmp[1];
			for(Object[] obj : outputMapping) {
				if(((String) obj[0]).equals(Task)) {
					out.put(entry.getKey(), (LinkedList<String>) obj[1]);
					break;
				}
			}
		}
		for(Entry<String, LinkedList<Object[]>[]> entry : netDataFlows.entrySet()) {
			for(Entry<String, LinkedList<String>> entry2 : out.entrySet()) {
				for(String subVar : entry2.getValue()) {
					if(entry.getKey().equals(subVar)) {
						LinkedList<Object[]> obj = entry.getValue()[1];
						dataFlows.get(entry2.getKey())[1].addAll(obj);
					}
				}
			}
		}
	}
    
    public static void generatePlaceAndTask(Element child, LinkedList<Task> tmpTask, LinkedList<Place> tmpPlace, int placeStartPos, int placeEndPos, int taskStartPos, int taskEndPos, int fromPos, String compositeTaskID, LinkedList<Integer> linkedTo) {
		String type = child.getName();
		if(type.equals("condition")) {
			
			String conditionID = child.getAttributeValue("id");
			
			generatePlaceLinks(tmpPlace, conditionID, placeStartPos, placeEndPos, fromPos, tmpTask.size(), linkedTo);
			
		}else if(type.equals("inputCondition") || type.equals("outputCondition")) {
			
			StringBuffer sb = new StringBuffer(child.getAttributeValue("id"));
			if(!compositeTaskID.isEmpty()) {
				sb.append("_");
				sb.append(compositeTaskID);
			}			
			String conditionID = sb.toString();

			generatePlaceLinks(tmpPlace, conditionID, placeStartPos, placeEndPos, fromPos, tmpTask.size(), linkedTo);
			
		}else {
			
			String taskID = child.getAttributeValue("id");
			
			generateTaskLinks(tmpTask, taskID, taskStartPos, taskEndPos, fromPos, 0, linkedTo);
			
		}
	}
    
    public static void generatePlaceLinks(LinkedList<Place> tmpPlace, String conditionID, int startPos, int endPos, int fromPos, int offSetToPos, LinkedList<Integer> linkedTo) {
    	for(int j = startPos; j<endPos; j++) {
			Place next = tmpPlace.get(j);
			if(next.engineID.equals(conditionID)) {
				next.linkedFrom.add(fromPos);
				linkedTo.add(j+offSetToPos);
				break;
			}
		}
    }
    
    public static void generateTaskLinks(LinkedList<Task> tmpTask, String taskID, int startPos, int endPos, int fromPos, int offSetToPos, LinkedList<Integer> linkedTo) {
    	for(int j = startPos; j<endPos; j++) {
			Task next = tmpTask.get(j);
			if(next.engineID.equals(taskID)) {
				next.linkedFrom.add(fromPos);
				linkedTo.add(j+offSetToPos);
				break;
			}
		}
    }
    
    public static void modifyVariable(StateYAWLProcess neighbour, LinkedList<String> modified, String key, Object value) {
    	neighbour.getVariables().put(key, value);
		modified.add(key);
    }
    
    public static void updateVariableAllocateAndStart(StateYAWLProcess neighbour, LinkedList<String> modified, Object resource, String value ) {
    	String res = (String) resource;
		if(value.endsWith("ce)")) {
			String r = null;
			if(res != null) {
				r = "<a>"+res+"</a>";
			}
			
			modifyVariable(neighbour, modified, value, r);
			
		}else if(value.endsWith("d)")) {
			if(res != null) {

				modifyVariable(neighbour, modified, value, "true");
				
			}else {
				
				modifyVariable(neighbour, modified, value, "false");
				
			}
		}else if(value.endsWith("me)") || value.endsWith("s)")) {
			if(res == null) {

				modifyVariable(neighbour, modified, value, null);
				
			}else {
				
				modifyVariable(neighbour, modified, value, neighbour.getVariablesOriginal().get(value));
				
			}
		}
    }
    
    public static void updateVariableResourceBuilder(StateYAWLProcess neighbour, LinkedList<String> modified, Object resource, String value) {
    	@SuppressWarnings("unchecked")
		LinkedList<String> res = (LinkedList<String>) resource;
    	
    	StringBuffer r = new StringBuffer();
		if(res != null && res.size()>0) {
			r.append("<a>");
			for(String tmp: res) {
				r.append(tmp);
			}
			r.append("</a>");
		}
		
		modifyVariable(neighbour, modified, value, (r.length()>0?r.toString():null));
    }
    
    @SuppressWarnings("unchecked")
	public static void generateTask(Element current, Namespace ns, Namespace ns2, Element pce, Element specificationSet, String compositeTaskID, LinkedList<Net> tmpNet, LinkedList<Place> tmpPlace, LinkedList<Task> tmpTask, int placeStartPos, int placeEndPos, int taskStartPos, int taskEndPos, int offSetToPos, LinkedList<Integer> linkedTo) {
    	for(Element flowsInto : (List<Element>) current.getChildren("flowsInto", ns)) {
			boolean isComposite = false;
			String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
			for(Element child : (List<Element>) pce.getChildren()) {
				if(child.getAttributeValue("id").equals(elementId)) {
					if(child.getChild("decomposesTo", ns)!=null) {
						String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
						List<Element> decSubNet = specificationSet.getChild("specification", ns).getChildren("decomposition", ns);
						for(Element eleSubNet : decSubNet) {
							if(eleSubNet.getAttributeValue("id").equals(id)) {
								isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
								if(isComposite) {
									
									StringBuffer sb = new StringBuffer(child.getAttributeValue("id"));
									if(!compositeTaskID.isEmpty()) {
										sb.append("_");
										sb.append(compositeTaskID);
									}
									sb.append("_open");
									String taskID = sb.toString();
									
									YStateProcessUtilities.generateTaskLinks(tmpTask, taskID, taskStartPos, taskEndPos, offSetToPos, 0, linkedTo);
									
									generateSubNetMatrix(eleSubNet, specificationSet, ns, ns2, tmpNet, tmpTask, tmpPlace, taskID);
									
								}
								break;
							}
						}
					}
					if(!isComposite) {
						
						YStateProcessUtilities.generatePlaceAndTask(child, tmpTask, tmpPlace, placeStartPos, placeEndPos, taskStartPos, taskEndPos, offSetToPos, compositeTaskID, linkedTo); 
						
					}
					break;
				}
			}
		}
    }
    
    @SuppressWarnings("unchecked")
	private static void generateSubNetMatrix(Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, LinkedList<Net> tmpNet, LinkedList<Task> tmpTask, LinkedList<Place> tmpPlace, String compositeTaskID) {
    	    	
    	Element pce = subNetElement.getChild("processControlElements", ns);
		
		Element start = pce.getChild("inputCondition", ns);
		Element end = pce.getChild("outputCondition", ns);
		int taskStartPos = 0;
		int taskEndPos = 0;
		int placeStartPos = 0;
		int placeEndPos = 0;
		
		for(int i=0; i<tmpTask.size(); i++) {
			Task t = tmpTask.get(i);
			if(t.engineID.equals(compositeTaskID+"_open")) {
				taskStartPos = i;
			}
			if(t.engineID.equals(compositeTaskID+"_close")) {
				taskEndPos = i;
			}
		}
		
		for(int i=0; i<tmpPlace.size(); i++) {
			Place p = tmpPlace.get(i);
			if(p.engineID.equals(start.getAttributeValue("id")+"_"+compositeTaskID)) {
				placeStartPos = i;
			}
			if(p.engineID.equals(end.getAttributeValue("id")+"_"+compositeTaskID)) {
				placeEndPos = i;
			}
		}
		
		int placePos = 0;
		int taskPos = 0;
		
		for(Element current : (List<Element>) pce.getChildren()) {
			String currentID = current.getAttributeValue("id");
			String currentType = current.getName();
			if(currentType.equals("condition") || currentType.equals("inputCondition") || currentType.equals("outputCondition")) {
				for(int i = placeStartPos; i<=placeEndPos; i++) {
					Place p = tmpPlace.get(i);
					if(p.engineID.equals(currentID) || p.engineID.equals(current.getAttributeValue("id")+"_"+compositeTaskID)) {
						placePos = i;
						LinkedList<Integer> linkedTo = p.linkedTo;
						
						if(p.engineID.equals(start.getAttributeValue("id")+"_"+compositeTaskID)) {
							p.linkedFrom.add(taskStartPos);
							tmpTask.get(taskStartPos).linkedTo.add(placePos+tmpTask.size());
						}
						
						if(p.engineID.equals(end.getAttributeValue("id")+"_"+compositeTaskID)) {
							tmpTask.get(taskEndPos).linkedFrom.add(placePos+tmpTask.size());
							p.linkedTo.add(taskEndPos);
						}
						
						generateTask(current, ns, ns2, pce, specificationSet, compositeTaskID, tmpNet, tmpPlace, tmpTask, placeStartPos, placeEndPos, taskStartPos, taskEndPos, placePos+tmpTask.size(), linkedTo);
						
						break;
					}
				}
			}else {
				for(int i = taskStartPos; i<=taskEndPos; i++) {
					Task t = tmpTask.get(i);
					if(t.engineID.equals(currentID) || t.engineID.equals(currentID+"_"+compositeTaskID+"_close")) {
						taskPos = i;
						LinkedList<Integer> linkedTo = t.linkedTo;
						
						for(Element flowsInto : (List<Element>) current.getChildren("flowsInto", ns)) {
							boolean isComposite = false;
							String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
							for(Element child : (List<Element>) pce.getChildren()) {
								if(child.getAttributeValue("id").equals(elementId)) {
									if(child.getChild("decomposesTo", ns)!=null) {
										String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
										List<Element> decSubNet = specificationSet.getChild("specification", ns).getChildren("decomposition", ns);
										for(Element eleSubNet : decSubNet) {
											if(eleSubNet.getAttributeValue("id").equals(id)) {
												isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
												if(isComposite) {
													
													StringBuffer sb = new StringBuffer(child.getAttributeValue("id"));
													if(!compositeTaskID.isEmpty()) {
														sb.append("_");
														sb.append(compositeTaskID);
													}
													sb.append("_open");
													String taskID = sb.toString();

													YStateProcessUtilities.generateTaskLinks(tmpTask, taskID, taskStartPos, taskEndPos, taskPos, 0, linkedTo);
													
													generateSubNetMatrix(eleSubNet, specificationSet, ns, ns2, tmpNet, tmpTask, tmpPlace, taskID);
													
												}
												break;
											}
										}
									}
									if(!isComposite) {
										
										YStateProcessUtilities.generatePlaceAndTask(child, tmpTask, tmpPlace, placeStartPos, placeEndPos, taskStartPos, taskEndPos, taskPos, compositeTaskID, linkedTo); 
										
									}
									break;
								}
							}
						}
						
						generateTask(current, ns, ns2, pce, specificationSet, compositeTaskID, tmpNet, tmpPlace, tmpTask, placeStartPos, placeEndPos, taskStartPos, taskEndPos, taskPos, linkedTo);
						
						break;
					}
				}
			}
		}
    }
    
    public static void produceResourceVariable(String method, HashSet<String> resourceTask, StringBuffer sb, String var, String event, int check) {
    	int off = 5*check;
    	if(method.endsWith("st")) {
    		resourceTask.add(var+"_"+(off-4));
    		sb.append(var+"_"+(off-4));
    	}else if(method.equals(event+"Number")) {
    		resourceTask.add(var+"_"+(off-3));
    		sb.append(var+"_"+(off-3));
    	}else if('r' == method.charAt(method.length()-1)) {
    		resourceTask.add(var+"_"+(off-2));
    		sb.append(var+"_"+(off-2));
    	}else if(method.contains(event+"MinNumberExcept")) {
    		String task = method.substring(method.indexOf(".")+1);
    		resourceTask.add(var+"_"+task+"_"+(off-1));
    		sb.append(var+"_"+task+"_"+(off-1));
    	}else if(method.contains("Contain")) {
    		String task = method.substring(method.indexOf(".")+1);
    		resourceTask.add(var+"_"+task+"_"+(off));
    		sb.append(var+"_"+task+"_"+(off));
    	}
    }
    
    public static void produceResourceVariable(String method, HashSet<String> resourceTask, String var, String event, int check) {
    	int off = 5*check;
    	if(method.endsWith("st")) {
    		resourceTask.add(var+"_"+(off-4));
    	}else if(method.equals(event+"Number")) {
    		resourceTask.add(var+"_"+(off-3));
    	}else if('r' == method.charAt(method.length()-1)) {
    		resourceTask.add(var+"_"+(off-2));
    	}else if(method.contains(event+"MinNumberExcept")) {
    		String task = method.substring(method.indexOf(".")+1);
    		resourceTask.add(var+"_"+task+"_"+(off-1));
    	}else if(method.contains("Contain")) {
    		String task = method.substring(method.indexOf(".")+1);
    		resourceTask.add(var+"_"+task+"_"+(off));
    	}
    }
	
	public static String[] processParam(String input) {
		String tmp = input.substring(input.indexOf(YExpression.PARTC)+1);
		input = input.substring(input.indexOf(YExpression.PARTA), input.indexOf(YExpression.PARTC)+1);
		while(tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.SINGLEEQUAL) || tmp.startsWith(YExpression.PARTC)) {
			input = input+tmp.substring(0, tmp.indexOf(YExpression.PARTC)+1);
			tmp = tmp.substring(tmp.indexOf(YExpression.PARTC)+1);
		}
		return new String[] {input, tmp};
	}
	
	public static String identifyTaskName(String taskName) {
		if(taskName.contains(YExpression.PARTA)) {
			taskName = taskName.substring(0, taskName.indexOf(YExpression.PARTA));
		}else {
			taskName = taskName.substring(0, taskName.indexOf(YExpression.DOT));
		}
		return taskName;
	}
	
	public static int selectTask(StateYAWLProcess neighbour) {
		if(neighbour.getNumberTaskRelevant().length < 1) return -1;
		int i = selectRandom(neighbour.getNumberTaskRelevant().length, neighbour.r);
		return neighbour.getNumberTaskRelevant()[i];
	}
	
	public static int selectRandom(int max, Random r) {
		int k = (int) (max*r.nextDouble());
		if(k==max) k--;
		return k;
	}
  
}
