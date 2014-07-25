package org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph;

import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Place;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class GraphCreator {
	
	private static GraphCreator me = null;
	
	public static GraphCreator getInstance() {
		if(me == null) me = new GraphCreator();
		return me;
	}
	
	public NodeGraph generateGraph(List<String> tasksLog, Task[] tasks, Place[] places) {
		
		Task tStart = null;
		for(Task t1 : tasks) {
			if(t1.engineID.equals(tasksLog.get(0))) {
				tStart = t1;
			}
		}

		NodeGraph start = new NodeGraph(tStart.engineID, discoverOutput(tStart, tasks, places));
		
		HashSet<NodeGraph> current = new HashSet<NodeGraph>();
		
		current.add(start);
		
		NodeGraph tmp = null;
		
		for(int z=1; z<tasksLog.size(); z++) {

			String engineID = tasksLog.get(z);
			
//			System.out.println("A "+engineID);
			
			Task t = null;
			for(Task t1 : tasks) {
//				System.out.println(t1.engineID);
				if(t1.engineID.equals(engineID)) {
					t = t1;
				}
			}			

			tmp = new NodeGraph(t.engineID, discoverOutput(t, tasks, places));
			for(int i=0; i<t.linkedFrom.size(); i++) {
				if(t.linkedFrom.get(i) < tasks.length) {
					Task t1 = tasks[t.linkedFrom.get(i)];
					boolean remove = false;
					NodeGraph ng1 = null;
					for(NodeGraph ng : current) {
						if(ng.nodeName.equals(t1.engineID)) {
							ng.add(tmp);
							if(ng.output == ng.linkedTo.size()) {
								remove = true;
								ng1 = ng;
							}
							break;
						}
					}
					if(remove) {
						current.remove(ng1);
					}
				}else {
					Place p = places[t.linkedFrom.get(i)- tasks.length];
					for(int j=0; j<p.linkedFrom.size(); j++) {
						if(p.linkedFrom.get(j) < tasks.length) {
							Task t1 = tasks[p.linkedFrom.get(j)];
							boolean remove = false;
							NodeGraph ng1 = null;
							for(NodeGraph ng : current) {
								if(ng.nodeName.equals(t1.engineID)) {
									ng.add(tmp);
									if(ng.output == ng.linkedTo.size()) {
										remove = true;
										ng1 = ng;
									}
									break;
								}
							}
							if(remove) {
								current.remove(ng1);
							}
						}
					}
				}
			}	
			current.add(tmp);
		}
		
		return start;
	}
	
	public HashMap<String, LinkedList<NodeGraph>> createMap(NodeGraph start){
		HashMap<String, LinkedList<NodeGraph>> map = new HashMap<String, LinkedList<NodeGraph>>();
		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
		
		pile.add(start);
		
		while(pile.size() > 0) {
			NodeGraph ng = pile.removeFirst();
			if(map.containsKey(ng.nodeName)) {
				map.get(ng.nodeName).add(ng);
			}else {
				LinkedList<NodeGraph> l = new LinkedList<NodeGraph>();
				l.add(ng);
				map.put(ng.nodeName, l);
			}
			for(NodeGraph ng1 : ng.linkedTo) {
				if(!pile.contains(ng1)) {
					pile.addLast(ng1);
				}
			}
		}
		
		return map;
	}
	
	public LinkedList<NodeGraph> discoverLast(NodeGraph start){
		LinkedList<NodeGraph> last = new LinkedList<NodeGraph>();
		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
		
		pile.add(start);
		
		while(pile.size() > 0) {
			NodeGraph ng = pile.removeFirst();
			if(ng.linkedTo.size() == 0 && !last.contains(ng)) {
				last.add(ng);
			}
			for(NodeGraph ng1 : ng.linkedTo) {
				if(!pile.contains(ng1)) {
					pile.addLast(ng1);
				}
			}
		}
		
		return last;
	}
	
	public void removePending(NodeGraph start, NodeGraph pending){
		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
		
		pile.add(start);
		
//		while(pile.size() > 0) {
//			NodeGraph ng = pile.removeFirst();
//			if(ng.equals(pending)) {
//				start = null;
//			}
//			boolean remove = false;
//			NodeGraph tmp = null;
//			for(NodeGraph ng1 : ng.linkedTo) {
//				if(ng1.equals(pending)) {
//					tmp = ng1;
//					remove = true;
//				}else if(!pile.contains(ng1)) {
//					pile.addLast(ng1);
//				}
//			}
//			if(remove) ng.remove(tmp);
//		}
		
		pending.remove();
	}

	private int discoverOutput(Task t, Task[] tasks, Place[] places) {
		if(!t.andSplit) return 1;
		if(t.linkedTo.size() == 1) return 1;
		int output = 0;
		for(int i=0; i<t.linkedTo.size(); i++) {
			if(t.linkedTo.get(i) < tasks.length) {
				output++;
			}else {
				Place p = places[t.linkedTo.get(i)-tasks.length];
				for(int j=0; j<p.linkedTo.size(); j++) {
					if(p.linkedTo.get(j) < tasks.length) {
						output++;
					}
				}
			}
		}
		return output;
	}
	
}
