package org.yawlfoundation.yawl.riskPrediction.DecisionPoints;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class DecisionPoint implements Serializable{
	
	public static final boolean TASK = true;
	public static final boolean PLACE = false;
	
	private String name;
	private boolean type;
	private final HashSet<String> originator = new HashSet<String>();
	private final HashSet<String> directSuccessors = new HashSet<String>();
	private final HashSet<String> totalSuccessors = new HashSet<String>();
	private final HashSet<String> predecessors = new HashSet<String>();
	private final HashSet<String> all = new HashSet<String>();
	
	public DecisionPoint(String name, boolean type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getType() {
		return type;
	}
	
	public HashSet<String> getDirectSuccessors() {
		return directSuccessors;
//		return new LinkedList<String>(directSuccessors);
	}
	
	public HashSet<String> getTotalSuccessors() {
		return totalSuccessors;
//		return new LinkedList<String>(totalSuccessors);
	}
	
	public HashSet<String> getPredecessors() {
		return predecessors;
//		return new LinkedList<String>(predecessors);
	}
	
	public HashSet<String> getOriginator() {
		return originator;
	}
	
	public HashSet<String> getAll() {
		return all;
//		return new LinkedList<String>(all);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(boolean type) {
		this.type = type;
	}
	
	public void addOriginator(String originator) {
		this.originator.add(originator);
	}
	
	public void addAllOriginator(Collection<String> originator) {
		this.originator.addAll(originator);
	}
	
	public void addDirectSuccessor(String successor) {
		this.directSuccessors.add(successor);
		this.totalSuccessors.add(successor);
		this.all.add(successor);
	}
	
	public void addTotalSuccessor(String successor) {
		this.totalSuccessors.add(successor);
		this.all.add(successor);
	}
	
	public void addPredecessor(String predecessor) {
		this.predecessors.add(predecessor);
		this.all.add(predecessor);
	}
	
	public void addAllPredecessor(Collection<String> predecessors) {
		this.predecessors.addAll(predecessors);
		this.all.addAll(predecessors);
	}
	
	public void addTotalSuccessor(Collection<String> successors) {
		this.totalSuccessors.addAll(successors);
		this.all.addAll(successors);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nname: "+name+"\ndirectSuccessors: "+directSuccessors+"\ntotalSuccessors: "+totalSuccessors+"\npredecessors: "+predecessors);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DecisionPoint) {
			DecisionPoint oDP = (DecisionPoint) o;
			return (name.equals(oDP.name) && directSuccessors.equals(oDP.directSuccessors));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
