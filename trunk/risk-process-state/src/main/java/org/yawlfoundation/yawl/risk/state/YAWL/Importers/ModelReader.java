package org.yawlfoundation.yawl.risk.state.YAWL.Importers;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.yawlfoundation.yawl.risk.state.Node;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Net;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Place;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class ModelReader {
	
	public final int Unknown = 0;
	public final int Unoffered = 1;
	public final int Offered = 2;
	public final int Allocated = 3;
	public final int Started = 4;
	public final int Completed = 5;
	
	Net[] nets = null;
	Task[] tasks = null;
	Place[] places = null;
	
	LinkedHashSet<Node> toBeVisit = new LinkedHashSet<Node>();
	LinkedHashSet<Node> visited = new LinkedHashSet<Node>();
	LinkedList<String> newLog = new LinkedList<String>();
	
	HashSet<String> tasksIDs = new HashSet<String>();

	int logPos = 0;
	
	public static void main(String[] args) {
		
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
		
		ModelReader mr = new ModelReader();
		mr.importModel(specification);
		
	}

	public LinkedList<String> updateTaskLog(List<String> tasksLog) {		
		Place start = places[0];
		
		toBeVisit.addAll(getNextTask(start));
		
		while(!toBeVisit.isEmpty() && logPos<tasksLog.size()) {
			Iterator<Node> it = toBeVisit.iterator();
			Node n = it.next();
			it.remove();
			
			String taskID = ((Task)n).engineID;
			if(tasksLog.contains(taskID)) {
				newLog.add(taskID);
				tasksLog.remove(taskID);
			}else {
				if(!tasksIDs.contains(taskID)) {
					boolean ok = true;
					for(Task v : getPrevTask(n)) {
						if(!visited.contains(v.engineID)) ok = false;
					}
					if(ok) newLog.add(taskID);
				}
					
			}
			
			if(!visited.containsAll(getNextTask(n))) {
				toBeVisit.addAll(getNextTask(n));
				visited.addAll(getNextTask(n));
			}
		}
		for(String id : newLog) {
			tasksLog.add(id);
		}
		return newLog;
	}	


	private HashSet<Task> getNextTask(Node n) {
		if(n instanceof Task) {
			Task t = (Task) n;
			return getNextTask(t);
		}else if(n instanceof Place) {
			Place p = (Place) n;
			return getNextTask(p);
		}else return null;
	}

	private HashSet<Task> getNextTask(Task t) {
		HashSet<Task> next = new HashSet<Task>();
		
		for(Integer i : t.linkedTo) {
			if(i>=tasks.length) {
				Place p = places[i-tasks.length];
				next.addAll(getNextTask(p));
			}else {
				next.add(tasks[i]);
			}
		}
		
		return next;
	}
	
	private HashSet<Task> getNextTask(Place p) {
		HashSet<Task> next = new HashSet<Task>();
		
		for(Integer i : p.linkedTo) {
			next.add(tasks[i]);
		}
		
		return next;
	}
	
	private HashSet<Task> getPrevTask(Node n) {
		if(n instanceof Task) {
			Task t = (Task) n;
			return getPrevTask(t);
		}else if(n instanceof Place) {
			Place p = (Place) n;
			return getPrevTask(p);
		}else return null;
	}

	private HashSet<Task> getPrevTask(Task t) {
		HashSet<Task> next = new HashSet<Task>();
		
		for(Integer i : t.linkedFrom) {
			if(i>=tasks.length) {
				Place p = places[i-tasks.length];
				next.addAll(getPrevTask(p));
			}else {
				next.add(tasks[i]);
			}
		}
		
		return next;
	}
	
	private HashSet<Task> getPrevTask(Place p) {
		HashSet<Task> next = new HashSet<Task>();
		
		for(Integer i : p.linkedFrom) {
			next.add(tasks[i]);
		}
		
		return next;
	}

	public void importModelWithDataFlow(String specificationXML, HashMap<String, LinkedList<Object[]>[]> dataFlows) {
    	String root = null;
		String inputCondition = null;
		String outputCondition = null;
		LinkedList<Task> tmpTask = new LinkedList<Task>();
		LinkedList<Place> tmpPlace = new LinkedList<Place>();
		LinkedList<Net> tmpNet = new LinkedList<Net>();

		Element e = JDOMUtil.stringToElement(specificationXML);
		Namespace ns = e.getNamespace();
		Namespace ns2 = e.getNamespace("xsi");
		List<Element> dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals("true")) {
				int net = tmpNet.size();
				root = ele.getAttributeValue("id");		
				
				for(Element localVariable : (List<Element>) ele.getChildren("localVariable", ns)) {
					String name = localVariable.getChild("name", ns).getValue();
					LinkedList<Object[]>[] lists = new LinkedList[2];
					lists[0] = new LinkedList<Object[]>();
					lists[1] = new LinkedList<Object[]>();
					dataFlows.put(name, lists);
				}
				
				tmpNet.addLast(new Net(root));
				Element pce = ele.getChild("processControlElements", ns);
				
				inputCondition = pce.getChild("inputCondition", ns).getAttributeValue("id");
				tmpPlace.add(new Place(inputCondition, true, false));
				
				for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
					tmpPlace.add(new Place(place.getAttributeValue("id"), false, true));
				}
				
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					boolean isComposite = false;
					boolean isMultiple = false;
					
					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
					
					if(task.getChild("decomposesTo", ns)!=null) {
						String id = task.getChild("decomposesTo", ns).getAttributeValue("id");
						List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
						for(Element eleSubNet : decSubNet) {
							if(eleSubNet.getAttributeValue("id").equals(id)) {
								isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
								if(isComposite) {
									Object[] tmp = generateSubNetWithDataFlow(eleSubNet, e, ns, ns2, task.getAttributeValue("id"));
									tmpTask.add(new Task(null, task.getAttributeValue("id")+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code")));
									tmpNet.addAll((LinkedList) tmp[0]);
									tmpTask.addAll((LinkedList) tmp[1]);
									tmpPlace.addAll((LinkedList) tmp[2]);
									tmpTask.add(new Task(null, task.getAttributeValue("id")+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor"));
									
									if(task.getChild("startingMapping", ns)!=null) {
										for(Element mapping : (List<Element>) task.getChild("startingMapping", ns).getChildren("mapping", ns)) {
											String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
											String mapTo = mapping.getChild("mapTo", ns).getValue();
											dataInput(dataFlows, task.getAttributeValue("id")+"_open", query, mapTo);
										}
									}
									dataInputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, task.getAttributeValue("id")+"_open");
									if(task.getChild("completedMapping", ns)!=null) {
										for(Element mapping : (List<Element>) task.getChild("completedMapping", ns).getChildren("mapping", ns)) {
											String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
											String mapTo = mapping.getChild("mapTo", ns).getValue();
											dataOutput(dataFlows, task.getAttributeValue("id")+"_close", query, mapTo);
										}
									}
									dataOutputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, task.getAttributeValue("id")+"_close");
								}
								break;
							}
						}
					}
					if(!isComposite) {
						
						String name = null;
						if(task.getChild("name", ns)!=null) {
							name = task.getChild("name", ns).getValue();
						}
						
						if(task.getChild("startingMapping", ns)!=null) {
							for(Element mapping : (List<Element>) task.getChild("startingMapping", ns).getChildren("mapping", ns)) {
								String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
								String mapTo = mapping.getChild("mapTo", ns).getValue();
								dataInput(dataFlows, task.getAttributeValue("id"), query, mapTo);
							}
						}
						if(task.getChild("completedMapping", ns)!=null) {
							for(Element mapping : (List<Element>) task.getChild("completedMapping", ns).getChildren("mapping", ns)) {
								String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
								String mapTo = mapping.getChild("mapTo", ns).getValue();
								dataOutput(dataFlows, task.getAttributeValue("id"), query, mapTo);
							}
							
						}
						
						Task t = new Task(name, task.getAttributeValue("id"), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"));
												
						if(t.isCompleted) {
							t.status = Completed;
							t.originalStatus = Completed;
						}else if(t.isStarted) {
							t.status = Started;
							t.originalStatus = Started;
						}else if(t.isAllocated) {
							t.status = Allocated;
							t.originalStatus = Allocated;
						}else if(t.isOffered) {
							t.status = Offered;
							t.originalStatus = Offered;
						}else {
							t.status = Unoffered;
							t.originalStatus = Unoffered;
						}
						
						tmpTask.add(t);
					}
				}
				
				outputCondition = pce.getChild("outputCondition", ns).getAttributeValue("id");
				tmpPlace.add(new Place(outputCondition, false, true));
			}
		}
		
		int netPos = 0;
		int taskPos = 0;
		int placePos = 0;
		dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals("true")) {
				
				for(int i = 0; i<tmpNet.size(); i++) {
					Net n = tmpNet.get(i);
					if(n.netName.equals(ele.getAttributeValue("id"))) {
						netPos = i;
						break;
					}
				}
				
				Element pce = ele.getChild("processControlElements", ns);
				
				for(Element start : (List<Element>) pce.getChildren()){
					
					String startID = start.getAttributeValue("id");
					String startType = start.getName();
					if(startType.equals("condition") || startType.equals("inputCondition") || startType.equals("outputCondition")) {
						for(int i = 0; i<tmpPlace.size(); i++) {
							Place p = tmpPlace.get(i);
							if(p.engineID.equals(startID)) {
								placePos = i;
								LinkedList<Integer> linkedTo = p.linkedTo;
								
								for(Element flowsInto : (List<Element>) start.getChildren("flowsInto", ns)) {
									boolean isComposite = false;
									String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
									for(Element child : (List<Element>) pce.getChildren()) {
										if(child.getAttributeValue("id").equals(elementId)) {
											if(child.getChild("decomposesTo", ns)!=null) {
												String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
												List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
												for(Element eleSubNet : decSubNet) {
													if(eleSubNet.getAttributeValue("id").equals(id)) {
														isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
														if(isComposite) {
															
															String taskID = child.getAttributeValue("id")+"_open";
															for(int j = 0; j<tmpTask.size(); j++) {
																Task next = tmpTask.get(j);
																if(next.engineID.equals(taskID)) {
																	next.linkedFrom.add(placePos+tmpTask.size());
																	linkedTo.add(j);
																	break;
																}
															}
															
															generateSubNetMatrix(eleSubNet, e, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id"));
															
														}
														break;
													}
												}
											}
											if(!isComposite) {
												String type = child.getName();
												if(type.equals("condition") || type.equals("inputCondition") || type.equals("outputCondition")) {
													String conditionID = child.getAttributeValue("id");
													for(int j = 0; j<tmpPlace.size(); j++) {
														Place next = tmpPlace.get(j);
														if(next.engineID.equals(conditionID)) {
															next.linkedFrom.add(placePos+tmpTask.size());
															linkedTo.add(j+tmpTask.size());
															break;
														}
													}
												}else {
													String taskID = child.getAttributeValue("id");
													for(int j = 0; j<tmpTask.size(); j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(placePos+tmpTask.size());
															linkedTo.add(j);
															break;
														}
													}
												}
											}
											break;
										}
									}
								}
								break;
							}
						}
					}else {
						for(int i = 0; i<tmpTask.size(); i++) {
							Task t = tmpTask.get(i);
							if(t.engineID.equals(startID) || t.engineID.equals(startID+"_close")) {
								taskPos = i;
								LinkedList<Integer> linkedTo = t.linkedTo;
								
								for(Element flowsInto : (List<Element>) start.getChildren("flowsInto", ns)) {
									boolean isComposite = false;
									String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
									for(Element child : (List<Element>) pce.getChildren()) {
										if(child.getAttributeValue("id").equals(elementId)) {
											if(child.getChild("decomposesTo", ns)!=null) {
												String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
												List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
												for(Element eleSubNet : decSubNet) {
													if(eleSubNet.getAttributeValue("id").equals(id)) {
														isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
														if(isComposite) {
															
															String taskID = child.getAttributeValue("id")+"_open";
															for(int j = 0; j<tmpTask.size(); j++) {
																Task next = tmpTask.get(j);
																if(next.engineID.equals(taskID)) {
																	next.linkedFrom.add(taskPos);
																	linkedTo.add(j);
																	break;
																}
															}
															
															generateSubNetMatrix(eleSubNet, e, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id"));
															
														}
														break;
													}
												}
											}
											if(!isComposite) {
												String type = child.getName();
												if(type.equals("condition") || type.equals("inputCondition") || type.equals("outputCondition")) {
													String conditionID = child.getAttributeValue("id");
													for(int j = 0; j<tmpPlace.size(); j++) {
														Place next = tmpPlace.get(j);
														if(next.engineID.equals(conditionID)) {
															next.linkedFrom.add(taskPos);
															linkedTo.add(j+tmpTask.size());
															break;
														}
													}
												}else {
													String taskID = child.getAttributeValue("id");
													for(int j = 0; j<tmpTask.size(); j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(taskPos);
															linkedTo.add(j);
															break;
														}
													}
												}
											}
											break;
										}
									}
								}
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
		
		discoverDecomposedTask(specificationXML);
    }
	
	public void importModel(String specificationXML) {
    	String root = null;
		String inputCondition = null;
		String outputCondition = null;
		LinkedList<Task> tmpTask = new LinkedList<Task>();
		LinkedList<Place> tmpPlace = new LinkedList<Place>();
		LinkedList<Net> tmpNet = new LinkedList<Net>();

		Element e = JDOMUtil.stringToElement(specificationXML);
		Namespace ns = e.getNamespace();
		Namespace ns2 = e.getNamespace("xsi");
		List<Element> dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals("true")) {
				int net = tmpNet.size();
				root = ele.getAttributeValue("id");		
				
				tmpNet.addLast(new Net(root));
				Element pce = ele.getChild("processControlElements", ns);
				
				inputCondition = pce.getChild("inputCondition", ns).getAttributeValue("id");
				tmpPlace.add(new Place(inputCondition, true, false));
				
				for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
					tmpPlace.add(new Place(place.getAttributeValue("id"), false, true));
				}
				
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					boolean isComposite = false;
					boolean isMultiple = false;
					
					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
					
					if(task.getChild("decomposesTo", ns)!=null) {
						String id = task.getChild("decomposesTo", ns).getAttributeValue("id");
						List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
						for(Element eleSubNet : decSubNet) {
							if(eleSubNet.getAttributeValue("id").equals(id)) {
								isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
								if(isComposite) {
									Object[] tmp = generateSubNet(eleSubNet, e, ns, ns2, task.getAttributeValue("id"));
									tmpTask.add(new Task(null, task.getAttributeValue("id")+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code")));
									tmpNet.addAll((LinkedList) tmp[0]);
									tmpTask.addAll((LinkedList) tmp[1]);
									tmpPlace.addAll((LinkedList) tmp[2]);
									tmpTask.add(new Task(null, task.getAttributeValue("id")+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor"));
									
								}
								break;
							}
						}
					}
					if(!isComposite) {
						
						String name = null;
						if(task.getChild("name", ns)!=null) {
							name = task.getChild("name", ns).getValue();
						}
						
						Task t = new Task(name, task.getAttributeValue("id"), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"));
												
						if(t.isCompleted) {
							t.status = Completed;
							t.originalStatus = Completed;
						}else if(t.isStarted) {
							t.status = Started;
							t.originalStatus = Started;
						}else if(t.isAllocated) {
							t.status = Allocated;
							t.originalStatus = Allocated;
						}else if(t.isOffered) {
							t.status = Offered;
							t.originalStatus = Offered;
						}else {
							t.status = Unoffered;
							t.originalStatus = Unoffered;
						}
						
						tmpTask.add(t);
					}
				}
				
				outputCondition = pce.getChild("outputCondition", ns).getAttributeValue("id");
				tmpPlace.add(new Place(outputCondition, false, true));
			}
		}
		
		int netPos = 0;
		int taskPos = 0;
		int placePos = 0;
		dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals("true")) {
				
				for(int i = 0; i<tmpNet.size(); i++) {
					Net n = tmpNet.get(i);
					if(n.netName.equals(ele.getAttributeValue("id"))) {
						netPos = i;
						break;
					}
				}
				
				Element pce = ele.getChild("processControlElements", ns);
				
				for(Element start : (List<Element>) pce.getChildren()){
					
					String startID = start.getAttributeValue("id");
					String startType = start.getName();
					if(startType.equals("condition") || startType.equals("inputCondition") || startType.equals("outputCondition")) {
						for(int i = 0; i<tmpPlace.size(); i++) {
							Place p = tmpPlace.get(i);
							if(p.engineID.equals(startID)) {
								placePos = i;
								LinkedList<Integer> linkedTo = p.linkedTo;
								
								for(Element flowsInto : (List<Element>) start.getChildren("flowsInto", ns)) {
									boolean isComposite = false;
									String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
									for(Element child : (List<Element>) pce.getChildren()) {
										if(child.getAttributeValue("id").equals(elementId)) {
											if(child.getChild("decomposesTo", ns)!=null) {
												String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
												List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
												for(Element eleSubNet : decSubNet) {
													if(eleSubNet.getAttributeValue("id").equals(id)) {
														isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
														if(isComposite) {
															
															String taskID = child.getAttributeValue("id")+"_open";
															for(int j = 0; j<tmpTask.size(); j++) {
																Task next = tmpTask.get(j);
																if(next.engineID.equals(taskID)) {
																	next.linkedFrom.add(placePos+tmpTask.size());
																	linkedTo.add(j);
																	break;
																}
															}
															
															generateSubNetMatrix(eleSubNet, e, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id"));
															
														}
														break;
													}
												}
											}
											if(!isComposite) {
												String type = child.getName();
												if(type.equals("condition") || type.equals("inputCondition") || type.equals("outputCondition")) {
													String conditionID = child.getAttributeValue("id");
													for(int j = 0; j<tmpPlace.size(); j++) {
														Place next = tmpPlace.get(j);
														if(next.engineID.equals(conditionID)) {
															next.linkedFrom.add(placePos+tmpTask.size());
															linkedTo.add(j+tmpTask.size());
															break;
														}
													}
												}else {
													String taskID = child.getAttributeValue("id");
													for(int j = 0; j<tmpTask.size(); j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(placePos+tmpTask.size());
															linkedTo.add(j);
															break;
														}
													}
												}
											}
											break;
										}
									}
								}
								break;
							}
						}
					}else {
						for(int i = 0; i<tmpTask.size(); i++) {
							Task t = tmpTask.get(i);
							if(t.engineID.equals(startID) || t.engineID.equals(startID+"_close")) {
								taskPos = i;
								LinkedList<Integer> linkedTo = t.linkedTo;
								
								for(Element flowsInto : (List<Element>) start.getChildren("flowsInto", ns)) {
									boolean isComposite = false;
									String elementId = flowsInto.getChild("nextElementRef", ns).getAttributeValue("id");
									for(Element child : (List<Element>) pce.getChildren()) {
										if(child.getAttributeValue("id").equals(elementId)) {
											if(child.getChild("decomposesTo", ns)!=null) {
												String id = child.getChild("decomposesTo", ns).getAttributeValue("id");
												List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
												for(Element eleSubNet : decSubNet) {
													if(eleSubNet.getAttributeValue("id").equals(id)) {
														isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
														if(isComposite) {
															
															String taskID = child.getAttributeValue("id")+"_open";
															for(int j = 0; j<tmpTask.size(); j++) {
																Task next = tmpTask.get(j);
																if(next.engineID.equals(taskID)) {
																	next.linkedFrom.add(taskPos);
																	linkedTo.add(j);
																	break;
																}
															}
															
															generateSubNetMatrix(eleSubNet, e, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id"));
															
														}
														break;
													}
												}
											}
											if(!isComposite) {
												String type = child.getName();
												if(type.equals("condition") || type.equals("inputCondition") || type.equals("outputCondition")) {
													String conditionID = child.getAttributeValue("id");
													for(int j = 0; j<tmpPlace.size(); j++) {
														Place next = tmpPlace.get(j);
														if(next.engineID.equals(conditionID)) {
															next.linkedFrom.add(taskPos);
															linkedTo.add(j+tmpTask.size());
															break;
														}
													}
												}else {
													String taskID = child.getAttributeValue("id");
													for(int j = 0; j<tmpTask.size(); j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(taskPos);
															linkedTo.add(j);
															break;
														}
													}
												}
											}
											break;
										}
									}
								}
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
		
		discoverDecomposedTask(specificationXML);
    }
	
	private void discoverDecomposedTask(String specificationXML) {
		Element e = JDOMUtil.stringToElement(specificationXML);
		Namespace ns = e.getNamespace();
		Namespace ns2 = e.getNamespace("xsi");
		List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element eleSubNet : decSubNet) {
			if(eleSubNet.getAttributeValue("type", ns2).equals("WebServiceGatewayFactsType")) {
				List<Attribute> attributes = eleSubNet.getAttributes();
				String taskID = attributes.get(0).getValue();
				String newSpecificationXML = specificationXML;
				
				while(newSpecificationXML.contains("<decomposesTo id=\""+taskID+"\"")) {
					String decomposition = newSpecificationXML.substring(0, newSpecificationXML.indexOf("<decomposesTo id=\""+taskID+"\"")+19+taskID.length());
					
					newSpecificationXML = newSpecificationXML.substring(newSpecificationXML.indexOf(decomposition)+decomposition.length());
					
					decomposition = decomposition.substring(decomposition.lastIndexOf("<task id=\"")+10);
					
					tasksIDs.add(decomposition.substring(0, decomposition.indexOf("\">"))); 
				}
			}
		}
	}
	
	public Object[] getModel() {
		return new Object[] {tasks, places};
	}

	private Object[] generateSubNetWithDataFlow(Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, String compositeTaskID) {
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
		tmpNet.addLast(new Net(subNetElement.getAttributeValue("id")));
		
		Element pce = subNetElement.getChild("processControlElements", ns);
		
		String inputCondition = pce.getChild("inputCondition", ns).getAttributeValue("id");
		tmpPlace.add(new Place(inputCondition+"_"+compositeTaskID, true, false));
		
		for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
			tmpPlace.add(new Place(place.getAttributeValue("id"), false, true));
		}
		
		for(Element task : (List<Element>) pce.getChildren("task", ns)) {
			boolean isComposite = false;
			boolean isMultiple = false;
			
			if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
			
			if(task.getChild("decomposesTo", ns)!=null) {
				String id = task.getChild("decomposesTo", ns).getAttributeValue("id");
				List<Element> decSubNet = specificationSet.getChild("specification", ns).getChildren("decomposition", ns);
				for(Element eleSubNet : decSubNet) {
					if(eleSubNet.getAttributeValue("id").equals(id)) {
						isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
						if(isComposite) {
							Object[] tmp = generateSubNetWithDataFlow(eleSubNet, specificationSet, ns, ns2, task.getAttributeValue("id")+"_"+compositeTaskID);
							tmpTask.add(new Task(null, task.getAttributeValue("id")+"_"+compositeTaskID+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code")));
							tmpNet.addAll((LinkedList)tmp[0]);
							tmpTask.addAll((LinkedList)tmp[1]);
							tmpPlace.addAll((LinkedList)tmp[2]);
							tmpTask.add(new Task(null, task.getAttributeValue("id")+"_"+compositeTaskID+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor"));
							
							if(task.getChild("startingMapping", ns)!=null) {
								for(Element mapping : (List<Element>) task.getChild("startingMapping", ns).getChildren("mapping", ns)) {
									String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
									String mapTo = mapping.getChild("mapTo", ns).getValue();
									dataInput(dataFlows, task.getAttributeValue("id")+"_"+compositeTaskID+"_open", query, mapTo);
								}
							}
							dataInputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, task.getAttributeValue("id")+"_"+compositeTaskID+"_open");
							if(task.getChild("completedMapping", ns)!=null) {
								for(Element mapping : (List<Element>) task.getChild("completedMapping", ns).getChildren("mapping", ns)) {
									String query = mapping.getChild("expression", ns).getAttributeValue("query", ns);
									String mapTo = mapping.getChild("mapTo", ns).getValue();
									dataOutput(dataFlows, task.getAttributeValue("id")+"_"+compositeTaskID+"_close", query, mapTo);
								}
							}
							dataOutputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, task.getAttributeValue("id")+"_"+compositeTaskID+"_close");
						}
					}
				}
			}
			if(!isComposite) {
				String name = null;
				if(task.getChild("name", ns)!=null) {
					name = task.getChild("name", ns).getValue();
				}
				
				if(task.getChild("startingMapping", ns2)!=null) {
					for(Element mapping : (List<Element>) task.getChild("startingMapping", ns2).getChildren("mapping", ns2)) {
						String query = mapping.getChild("expression", ns2).getAttributeValue("query", ns2);
						String mapTo = mapping.getChild("mapTo", ns2).getValue();
						dataInput(dataFlows, task.getAttributeValue("id"), query, mapTo);
					}
				}
				if(task.getChild("completedMapping", ns2)!=null) {
					for(Element mapping : (List<Element>) task.getChild("completedMapping", ns2).getChildren("mapping", ns2)) {
						String query = mapping.getChild("expression", ns2).getAttributeValue("query", ns2);
						String mapTo = mapping.getChild("mapTo", ns2).getValue();
						dataOutput(dataFlows, task.getAttributeValue("id"), query, mapTo);
					}
					
				}
				
				Task t = new Task(name, task.getAttributeValue("id"), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"));
								
				if(t.isCompleted) {
					t.status = Completed;
					t.originalStatus = Completed;
				}else if(t.isStarted) {
					t.status = Started;
					t.originalStatus = Started;
				}else if(t.isAllocated) {
					t.status = Allocated;
					t.originalStatus = Allocated;
				}else if(t.isOffered) {
					t.status = Offered;
					t.originalStatus = Offered;
				}else {
					t.status = Unoffered;
					t.originalStatus = Unoffered;
				}
				
				tmpTask.add(t);
			}
		}
		
		String outputCondition = pce.getChild("outputCondition", ns).getAttributeValue("id");
		tmpPlace.add(new Place(outputCondition+"_"+compositeTaskID, false, true));
		
		return array;
    }
	
	private Object[] generateSubNet(Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, String compositeTaskID) {
    	Object[] array = new Object[4];
    	
    	LinkedList<Net> tmpNet = new LinkedList<Net>();
    	LinkedList<Task> tmpTask = new LinkedList<Task>();
    	LinkedList<Place> tmpPlace = new LinkedList<Place>();
    	
    	array[0] = tmpNet;
    	array[1] = tmpTask;
    	array[2] = tmpPlace;
    	
    	int net = tmpNet.size();
		tmpNet.addLast(new Net(subNetElement.getAttributeValue("id")));
		
		Element pce = subNetElement.getChild("processControlElements", ns);
		
		String inputCondition = pce.getChild("inputCondition", ns).getAttributeValue("id");
		tmpPlace.add(new Place(inputCondition+"_"+compositeTaskID, true, false));
		
		for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
			tmpPlace.add(new Place(place.getAttributeValue("id"), false, true));
		}
		
		for(Element task : (List<Element>) pce.getChildren("task", ns)) {
			boolean isComposite = false;
			boolean isMultiple = false;
			
			if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
			
			if(task.getChild("decomposesTo", ns)!=null) {
				String id = task.getChild("decomposesTo", ns).getAttributeValue("id");
				List<Element> decSubNet = specificationSet.getChild("specification", ns).getChildren("decomposition", ns);
				for(Element eleSubNet : decSubNet) {
					if(eleSubNet.getAttributeValue("id").equals(id)) {
						isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
						if(isComposite) {
							Object[] tmp = generateSubNet(eleSubNet, specificationSet, ns, ns2, task.getAttributeValue("id")+"_"+compositeTaskID);
							tmpTask.add(new Task(null, task.getAttributeValue("id")+"_"+compositeTaskID+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code")));
							tmpNet.addAll((LinkedList)tmp[0]);
							tmpTask.addAll((LinkedList)tmp[1]);
							tmpPlace.addAll((LinkedList)tmp[2]);
							tmpTask.add(new Task(null, task.getAttributeValue("id")+"_"+compositeTaskID+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor"));
							
						}
					}
				}
			}
			if(!isComposite) {
				String name = null;
				if(task.getChild("name", ns)!=null) {
					name = task.getChild("name", ns).getValue();
				}
				
				Task t = new Task(name, task.getAttributeValue("id"), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"));
								
				if(t.isCompleted) {
					t.status = Completed;
					t.originalStatus = Completed;
				}else if(t.isStarted) {
					t.status = Started;
					t.originalStatus = Started;
				}else if(t.isAllocated) {
					t.status = Allocated;
					t.originalStatus = Allocated;
				}else if(t.isOffered) {
					t.status = Offered;
					t.originalStatus = Offered;
				}else {
					t.status = Unoffered;
					t.originalStatus = Unoffered;
				}
				
				tmpTask.add(t);
			}
		}
		
		String outputCondition = pce.getChild("outputCondition", ns).getAttributeValue("id");
		tmpPlace.add(new Place(outputCondition+"_"+compositeTaskID, false, true));
		
		return array;
    }
	
	private void generateSubNetMatrix(Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, LinkedList<Net> tmpNet, LinkedList<Task> tmpTask, LinkedList<Place> tmpPlace, String compositeTaskID) {
    	
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
													
													String taskID = child.getAttributeValue("id")+"_"+compositeTaskID+"_open";
													for(int j = taskStartPos; j<=taskEndPos; j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(placePos+tmpTask.size());
															linkedTo.add(j);
															break;
														}
													}
													
													generateSubNetMatrix(eleSubNet, specificationSet, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id")+"_"+compositeTaskID);
													
												}
												break;
											}
										}
									}
									if(!isComposite) {
										String type = child.getName();
										if(type.equals("condition")) {
											String conditionID = child.getAttributeValue("id");
											for(int j = placeStartPos; j<=placeEndPos; j++) {
												Place next = tmpPlace.get(j);
												if(next.engineID.equals(conditionID)) {
													next.linkedFrom.add(placePos+tmpTask.size());
													linkedTo.add(j+tmpTask.size());
													break;
												}
											}
										}else if(type.equals("inputCondition") || type.equals("outputCondition")) {
											String conditionID = child.getAttributeValue("id")+"_"+compositeTaskID;
											for(int j = placeStartPos; j<=placeEndPos; j++) {
												Place next = tmpPlace.get(j);
												if(next.engineID.equals(conditionID)) {
													next.linkedFrom.add(placePos+tmpTask.size());
													linkedTo.add(j+tmpTask.size());
													break;
												}
											}
										}else {
											String taskID = child.getAttributeValue("id");
											for(int j = taskStartPos; j<=taskEndPos; j++) {
												Task next = tmpTask.get(j);
												if(next.engineID.equals(taskID)) {
													next.linkedFrom.add(placePos+tmpTask.size());
													linkedTo.add(j);
													break;
												}
											}
										}
									}
									break;
								}
							}
						}
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
													
													String taskID = child.getAttributeValue("id")+"_"+compositeTaskID+"_open";
													for(int j = taskStartPos; j<=taskEndPos; j++) {
														Task next = tmpTask.get(j);
														if(next.engineID.equals(taskID)) {
															next.linkedFrom.add(taskPos);
															linkedTo.add(j);
															break;
														}
													}
													
													generateSubNetMatrix(eleSubNet, specificationSet, ns, ns2, tmpNet, tmpTask, tmpPlace, child.getAttributeValue("id")+"_"+compositeTaskID);
													
												}
												break;
											}
										}
									}
									if(!isComposite) {
										String type = child.getName();
										if(type.equals("condition")) {
											String conditionID = child.getAttributeValue("id");
											for(int j = placeStartPos; j<=placeEndPos; j++) {
												Place next = tmpPlace.get(j);
												if(next.engineID.equals(conditionID)) {
													next.linkedFrom.add(taskPos);
													linkedTo.add(j+tmpTask.size());
													break;
												}
											}
										}else if(type.equals("inputCondition") || type.equals("outputCondition")) {
											String conditionID = child.getAttributeValue("id")+"_"+compositeTaskID;
											for(int j = placeStartPos; j<=placeEndPos; j++) {
												Place next = tmpPlace.get(j);
												if(next.engineID.equals(conditionID)) {
													next.linkedFrom.add(taskPos);
													linkedTo.add(j+tmpTask.size());
													break;
												}
											}
										}else {
											String taskID = child.getAttributeValue("id");
											for(int j = taskStartPos; j<=taskEndPos; j++) {
												Task next = tmpTask.get(j);
												if(next.engineID.equals(taskID)) {
													next.linkedFrom.add(taskPos);
													linkedTo.add(j);
													break;
												}
											}
										}
									}
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
    }
	
	private void dataInput(HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task, String query, String mapTo) {
		Object[] obj = null;
    	StringTokenizer st = new StringTokenizer(query, "{}");
    	HashSet<String> set = new HashSet<String>();
    	boolean open = false;
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
    	for(String var : set) {
    		obj = new Object[2];
        	obj[0] = Task;
        	obj[1] = new LinkedList<String>();
        	((LinkedList<String>) obj[1]).add(mapTo);
        	dataFlows.get(var)[0].add(obj);
    	}
	}
	
	private void dataInputNet(HashMap<String, LinkedList<Object[]>[]> netDataFlows, HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task) {
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
	
	 private void dataOutput(HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task, String query, String mapTo) {
    	Object[] obj = new Object[2];
    	obj[0] = Task;
    	obj[1] = new LinkedList<String>();
    	LinkedList<String> list = (LinkedList<String>) obj[1];
    	StringTokenizer st = new StringTokenizer(query, "{}");
    	HashSet<String> set = new HashSet<String>();
    	boolean open = false;
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
    	list.addAll(set);
		dataFlows.get(mapTo)[1].add(obj);		
	}
		
	private void dataOutputNet(HashMap<String, LinkedList<Object[]>[]> netDataFlows, HashMap<String, LinkedList<Object[]>[]> dataFlows, String Task) {
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
	
}
