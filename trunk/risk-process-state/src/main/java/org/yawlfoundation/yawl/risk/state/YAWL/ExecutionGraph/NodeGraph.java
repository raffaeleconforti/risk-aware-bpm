package org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph;

import java.util.HashMap;
import java.util.LinkedList;

public class NodeGraph implements Cloneable {
	
	public LinkedList<NodeGraph> linkedTo = new LinkedList<NodeGraph>();
	LinkedList<NodeGraph> linkedFrom = new LinkedList<NodeGraph>();
	public String nodeName;
	int output = 0;
	int level = 0;
	static int count = 0;
	int specCount;
	
	public NodeGraph(String nodeName, int output) {
		this.nodeName = nodeName;
		this.output = output;
		count++;
		specCount = count;
	}
	
	public void add(NodeGraph next) {
		linkedTo.add(next);
		next.linkedFrom.add(this);
	}
	
	public void remove(NodeGraph next) {
		linkedTo.remove(next);
	}
	
	public void remove() {
		for(NodeGraph n : linkedFrom) {
			n.remove(this);
		}
		linkedFrom.clear();
	}
	
	@Override
	public String toString() {
		String str = "<"+nodeName+">"+specCount;
		str+= linkedTo+"</"+nodeName+">";
		return str;
	}
	
	@Override
	public Object clone() {
		NodeGraph clone = new NodeGraph(nodeName, output);
		clone.level = this.level;
		
		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
		
		HashMap<NodeGraph, NodeGraph> map = new HashMap<NodeGraph, NodeGraph>();
		
		map.put(this, clone);
		
		pile.add(this);
		
		while(pile.size() > 0) {
			NodeGraph ng = pile.removeFirst();
			if(!map.containsKey(ng)) {
				NodeGraph c = new NodeGraph(ng.nodeName, ng.output);
				c.level = ng.level;
				map.put(ng, c);
			}
			for(NodeGraph ng1 : ng.linkedTo) {
				if(!map.containsKey(ng1)) {
					NodeGraph c = new NodeGraph(ng1.nodeName, ng1.output);
					c.level = ng1.level;
					map.put(ng1, c);
				}
				map.get(ng).add(map.get(ng1));
				if(!pile.contains(ng1)) {
					pile.addLast(ng1);
				}
			}
		}
		
		return clone;
	}
	
}
