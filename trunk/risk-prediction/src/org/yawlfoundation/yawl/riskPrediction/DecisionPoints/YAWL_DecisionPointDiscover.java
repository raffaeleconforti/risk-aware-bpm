package org.yawlfoundation.yawl.riskPrediction.DecisionPoints;

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Place;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ModelReader;
import org.yawlfoundation.yawl.util.JDOMUtil;


public class YAWL_DecisionPointDiscover implements DecisionPointDiscover {

	private final LinkedList<DecisionPoint> decisionPoints = new LinkedList<DecisionPoint>();
	private final ModelReader mr = new ModelReader();
	
	public static final boolean TASK = true;
	public static final boolean PLACE = false;
	
	public static void main(String[] args) {
		String specification = null;
		try {
			File f = new File("/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl");
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
		
		YAWL_DecisionPointDiscover a = new YAWL_DecisionPointDiscover();
		a.discoverDecisionPoints(specification);
		
//		Object[] o = a.mr.getModel();

//		a.discoverPredecessors();
//		a.discoverSuccessors();
		
		System.out.println(a.getDecisionPoints());
		
//		System.out.println(Arrays.toString((Task[]) o[0]));
//		System.out.println(Arrays.toString((Place[]) o[1]));
	}
	
	@Override
	public void discoverDecisionPoints(String model) {
		
		mr.importModel(model);
		
		HashSet<String> originator = new HashSet<String>();
		
		Element e = JDOMUtil.stringToElement(model);
		Namespace ns = e.getNamespace();
		Namespace ns2 = e.getNamespace("xsi");
		List<Element> dec = e.getChild("specification", ns).getChildren("decomposition", ns);
		for(Element ele : dec) {
			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals("true")) {
								
				Element pce = ele.getChild("processControlElements", ns);
				
				for(Element place : (List<Element>) pce.getChildren("inputCondition", ns)) {
					
					String name = place.getAttributeValue("id");
					
					List<Element> flows = place.getChildren("flowsInto", ns);
						
					DecisionPoint decisionPoint = new DecisionPoint(name, DecisionPoint.PLACE);
					
					for(Element flow : flows) {
						addTaskSuccessor(decisionPoint, flow, ns, pce.getChildren("task", ns), pce.getChildren("place", ns), PLACE);
//							decisionPoint.addSuccessor(flow.getChild("nextElementRef", ns).getAttributeValue("id"));
					}
					
					decisionPoints.add(decisionPoint);
					
				}

				for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
					
					String name = place.getAttributeValue("id");
					
					List<Element> flows = place.getChildren("flowsInto", ns);
					if(flows.size() > 0) {
						
						DecisionPoint decisionPoint = new DecisionPoint(name, DecisionPoint.PLACE);
						
						for(Element flow : flows) {
							addTaskSuccessor(decisionPoint, flow, ns, pce.getChildren("task", ns), pce.getChildren("place", ns), PLACE);
//							decisionPoint.addSuccessor(flow.getChild("nextElementRef", ns).getAttributeValue("id"));
						}
						
						decisionPoints.add(decisionPoint);
					}
					
				}
				
				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
					
					String name = task.getAttributeValue("id");
//					if(task.getChild("name", ns)!=null) {
//						name = task.getChild("name", ns).getValue();
//					}
					
//					String split = task.getChild("split", ns).getAttributeValue("code");
					List<Element> flows = task.getChildren("flowsInto", ns);
//					
//					if(!"and".equals(split) && flows.size() > 1) {
						
						DecisionPoint decisionPoint = new DecisionPoint(name, DecisionPoint.TASK);
						
						for(Element flow : flows) {
							addTaskSuccessor(decisionPoint, flow, ns, pce.getChildren("task", ns), pce.getChildren("place", ns), TASK);
//							decisionPoint.addSuccessor(flow.getChild("nextElementRef", ns).getAttributeValue("id"));
						}
						
						decisionPoints.add(decisionPoint);
//					}
						
				}
			}
		}
		
		discoverPredecessors();
		discoverSuccessors();
		
		for(DecisionPoint dp : decisionPoints) {
			if(dp.getPredecessors().size() == 0) {
				if(dp.getType() == DecisionPoint.TASK) originator.add(dp.getName());
				else originator.addAll(dp.getDirectSuccessors());
				break;
			}
		}
		
		for(DecisionPoint dp : decisionPoints) {
			dp.addAllOriginator(originator);
		}
		
	}

	private void addTaskSuccessor(DecisionPoint decisionPoint, Element flow,
			Namespace ns, List<Element> tasks, List<Element> places, boolean type) {
		String next = flow.getChild("nextElementRef", ns).getAttributeValue("id");
		
		if(type = TASK) {
			for(Element task : tasks) {
				
				if(task.getAttributeValue("id").equals(next)) {
					if(task.getChild("decomposesTo", ns) == null) {
						
						List<Element> flows = task.getChildren("flowsInto", ns);
							
						for(Element flow2 : flows) {
							addTaskSuccessor(decisionPoint, flow2, ns, tasks, places, TASK);
						}
						
					}else {
						decisionPoint.addDirectSuccessor(next);
					}
					
					break;
				}
				
			}	
		}else {
			for(Element place : places) {
				
				if(place.getAttributeValue("id").equals(next)) {
						
					List<Element> flows = place.getChildren("flowsInto", ns);
						
					for(Element flow2 : flows) {
						addTaskSuccessor(decisionPoint, flow2, ns, tasks, places, PLACE);
					}
					
					break;
				}
				
			}
		}
	}

	private void discoverPredecessors() {
		Task[] tasks = (Task[]) mr.getModel()[0];
		Place[] places = (Place[]) mr.getModel()[1];
		Task t = null;
		Place p = null;
		
		for(DecisionPoint decisionPoint : getDecisionPoints()) {
			if(decisionPoint.getType() == DecisionPoint.TASK) {
				for(int i = 0; i<tasks.length; i++) {
					t = tasks[i];
					if(decisionPoint.getName().equals(t.engineID)) {
						HashSet<String> predecessors = new HashSet<String>();
						if(navigateToOrigin(predecessors, new HashSet<String>(), tasks, places, tasks.length, decisionPoint)) {
							finalizePredecessors(predecessors, tasks, places, tasks.length, decisionPoint);
							decisionPoint.addAllPredecessor(predecessors);
						}
					}
				}
			}else {
				for(int i = 0; i<places.length; i++) {
					p = places[i];
					if(decisionPoint.getName().equals(p.engineID)) {
						HashSet<String> predecessors = new HashSet<String>(); 
						if(navigateToOrigin(predecessors, new HashSet<String>(), tasks, places, tasks.length, decisionPoint)) {
							finalizePredecessors(predecessors, tasks, places, tasks.length, decisionPoint);
							decisionPoint.addAllPredecessor(predecessors);
						}
					}
				}
			}
		}
	}
	
	private void finalizePredecessors(HashSet<String> predecessors, Task[] tasks, Place[] places, int pos, DecisionPoint decisionPoint) {
		boolean inserted = false;
		int tasksLength = tasks.length;
		
		HashSet<String> newPredecessors = new HashSet<String>(); 
		
		pos = 0;
		
		while(pos < tasksLength+places.length) {
			if(pos < tasksLength) {
				if(!predecessors.contains(tasks[pos].engineID) && !decisionPoint.getName().equals(tasks[pos].engineID)) {
					for(int i : tasks[pos].linkedTo) {
						if(i < tasksLength) {
							if(predecessors.contains(tasks[i].engineID)) {
								newPredecessors.add(tasks[pos].engineID);
								inserted = true;
							}
						}else {
							if(predecessors.contains(places[i-tasksLength].engineID)) {
								newPredecessors.add(tasks[pos].engineID);
								inserted = true;
							}
						}
					}
				}
			}else {
				if(!predecessors.contains(places[pos-tasksLength].engineID) && !decisionPoint.getName().equals(places[pos-tasksLength].engineID)) {
					for(int i : places[pos-tasksLength].linkedTo) {
						if(i < tasksLength) {
							if(predecessors.contains(tasks[i].engineID)) {
								newPredecessors.add(places[pos-tasksLength].engineID);
								inserted = true;
							}
						}else {
							if(predecessors.contains(places[i-tasksLength].engineID)) {
								newPredecessors.add(places[pos-tasksLength].engineID);
								inserted = true;
							}
						}
					}
				}
			}
			
			if(inserted) {
				pos = 0;
				predecessors.addAll(newPredecessors);
				newPredecessors.clear();
				inserted = false;
			} else pos++;
		}
		predecessors.addAll(newPredecessors);
	}
	
	private void discoverSuccessors() {
		Task[] tasks = (Task[]) mr.getModel()[0];
		Place[] places = (Place[]) mr.getModel()[1];
		Task t = null;
		Place p = null;
		
		for(DecisionPoint decisionPoint : getDecisionPoints()) {
			if(decisionPoint.getType() == DecisionPoint.TASK) {
				for(int i = 0; i<tasks.length; i++) {
					t = tasks[i];
					if(decisionPoint.getName().equals(t.engineID)) {
						HashSet<String> successors = new HashSet<String>();
						if(navigateToEnd(successors, new HashSet<String>(), tasks, places, i, decisionPoint)) {
//							finalizeSuccessors(successors, tasks, places, i, decisionPoint);
							decisionPoint.addTotalSuccessor(successors);
						}
					}
				}
			}else {
				for(int i = 0; i<places.length; i++) {
					p = places[i];
					if(decisionPoint.getName().equals(p.engineID)) {
						HashSet<String> successors = new HashSet<String>(); 
						if(navigateToEnd(successors, new HashSet<String>(), tasks, places, tasks.length+i, decisionPoint)) {
//							finalizeSuccessors(successors, tasks, places, tasks.length+i, decisionPoint);
							decisionPoint.addTotalSuccessor(successors);
						}
					}
				}
			}
		}
	}
	
	private void finalizeSuccessors(HashSet<String> successors, Task[] tasks, Place[] places, int pos, DecisionPoint decisionPoint) {
		boolean inserted = false;
		int tasksLength = tasks.length;
		
		HashSet<String> newSuccessors = new HashSet<String>(); 
		
		pos = 0;
		
		while(pos < tasksLength) {
			if(pos < tasksLength) {
				if(!successors.contains(tasks[pos].engineID) && !decisionPoint.getName().equals(tasks[pos].engineID)) {
					for(int i : tasks[pos].linkedTo) {
						if(i < tasksLength) {
							if(successors.contains(tasks[i].engineID)) {
								newSuccessors.add(tasks[pos].engineID);
								inserted = true;
							}
						}else {
							if(successors.contains(places[i-tasksLength].engineID)) {
								newSuccessors.add(tasks[pos].engineID);
								inserted = true;
							}
						}
					}
				}
			}else {
				if(!successors.contains(places[pos-tasksLength].engineID) && !decisionPoint.getName().equals(places[pos-tasksLength].engineID)) {
					for(int i : places[pos-tasksLength].linkedTo) {
						if(i < tasksLength) {
							if(successors.contains(tasks[i].engineID)) {
								newSuccessors.add(places[pos-tasksLength].engineID);
								inserted = true;
							}
						}else {
							if(successors.contains(places[i-tasksLength].engineID)) {
								newSuccessors.add(places[pos-tasksLength].engineID);
								inserted = true;
							}
						}
					}
				}
			}
			
			if(inserted) {
				pos = 0;
				successors.addAll(newSuccessors);
				newSuccessors.clear();
				inserted = false;
			} else pos++;
		}
		successors.addAll(newSuccessors);
	}

	public boolean navigateToOrigin(HashSet<String> path, HashSet<String> visited, Task[] tasks, Place[] places, int pos, DecisionPoint decisionPoint) {
		int tasksLength = tasks.length;
		String curr = null;
		boolean found = false;
		
		if(pos < tasksLength) {
			curr = tasks[pos].engineID;
			
			if(visited.contains(curr)) {
				return false;
			}else {
				visited.add(curr);
			}
			
//			if(curr == null) curr = tasks[pos].engineID;
			
			if(decisionPoint.getType() == DecisionPoint.TASK && curr.equals(decisionPoint.getName())) {
				return true;
			}
			
			for(int i : tasks[pos].linkedTo) {
				if(i < tasksLength) {
					if(navigateToOrigin(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}else {
					if(navigateToOrigin(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}
			}
		}else {
			curr = places[pos-tasksLength].engineID;
			
			if(visited.contains(curr)) {
				return false;
			}else {
				visited.add(curr);
			}
			
			if(decisionPoint.getType() == DecisionPoint.PLACE && curr.equals(decisionPoint.getName())) {
				return true;
			}else if(places[pos-tasksLength].linkedTo.size() == 0) {
				return false;
			}
			
			for(int i : places[pos-tasksLength].linkedTo) {
				if(i < tasksLength) {
					if(navigateToOrigin(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}else {
					if(navigateToOrigin(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}
			}
		}
		
		return found;
	}
	
	public boolean navigateToEnd(HashSet<String> path, HashSet<String> visited, Task[] tasks, Place[] places, int pos, DecisionPoint decisionPoint) {
		int tasksLength = tasks.length;
		String curr = null;
		boolean found = false;
		
		if(pos < tasksLength) {
			curr = tasks[pos].engineID;
			
			if(visited.contains(curr)) {
				return false;
			}else {
				visited.add(curr);
			}
			
//			if(curr == null) curr = tasks[pos].engineID;
			
//			if(decisionPoint.getType() == DecisionPoint.TASK && curr.equals(decisionPoint.getName())) {
//				return true;
//			}
			
			for(int i : tasks[pos].linkedTo) {
				if(i < tasksLength) {
					if(navigateToEnd(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}else {
					if(navigateToEnd(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}
			}
		}else {
			curr = places[pos-tasksLength].engineID;
			
			if(visited.contains(curr)) {
				return false;
			}else {
				visited.add(curr);
			}
			
			if(places[pos-tasksLength].linkedTo.size() == 0) {
				return true;
			}
			
			for(int i : places[pos-tasksLength].linkedTo) {
				if(i < tasksLength) {
					if(navigateToEnd(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}else {
					if(navigateToEnd(path, visited, tasks, places, i, decisionPoint)) {
						path.add(curr);
						found = true;
					}
				}
			}
		}
		
		return found;
	}

	@Override
	public LinkedList<DecisionPoint> getDecisionPoints() {
		return decisionPoints;
	}

}
