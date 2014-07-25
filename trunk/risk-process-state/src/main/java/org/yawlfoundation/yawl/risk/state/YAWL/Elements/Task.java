package org.yawlfoundation.yawl.risk.state.YAWL.Elements;

import org.yawlfoundation.yawl.risk.state.Node;

import java.util.LinkedList;


public class Task implements Node {
	
	public static final int Unknown = 0;
	public static final int Unoffered = 1;
	public static final int Offered = 2;
	public static final int Allocated = 3;
	public static final int Started = 4;
	public static final int Completed = 5;
	
	public String taskName = null;
	public String engineID = null;
	public Integer refersTo = null;
	
	public boolean offerMod = false;
	public boolean allocateMod = false;
	public boolean startMod = false;
	
	public boolean isOffered = false;
	public boolean isAllocated = false;
	public boolean isStarted = false;
	public boolean isCompleted = false;
	
	public boolean rollback = false;
	public boolean relres = false;
	public boolean desta = false;
	public boolean deall = false;
	
	public int status = 0;
	public int originalStatus = 0;
	
	public boolean isComposite = false;
	public boolean isMultiple = false;
	
	public boolean andJoin = false;
	public boolean andSplit = false;
	public boolean orJoin = false;
	public boolean orSplit = false;
	public boolean xorJoin = false;
	public boolean xorSplit = false;
	
	public LinkedList<String> offerRes = new LinkedList<String>();
	public String allocateRes = null;
	public String startRes = null;
	
	public LinkedList<String> offerResDef = new LinkedList<String>(); //Convert all in participant
	
	public LinkedList<Integer> linkedTo = new LinkedList<Integer>();
	public int[] linkTo = null;
	public LinkedList<Integer> linkedFrom = new LinkedList<Integer>();
	public int[] linkFrom = null;
	
	public LinkedList<String> originalOfferRes = new LinkedList<String>();
	public String originalAllocateRes = null;
	public String originalStartRes = null;
	
	public LinkedList<String> originalOfferResDef = new LinkedList<String>(); //Convert all in participant
	
	public LinkedList<Integer> originalLinkedTo = new LinkedList<Integer>();
	public LinkedList<Integer> originalLinkedFrom = new LinkedList<Integer>();
	
	public Task(String taskName, String engineID, Integer refersTo, boolean isComposite, boolean isMultiple, String split, String join) {
		this.taskName = taskName;
		this.engineID = engineID;
		this.refersTo = refersTo;
		this.isComposite = isComposite;
		this.isMultiple = isMultiple;
		if(split.equals("and")) {
			andSplit = true;
			orSplit = false;
			xorSplit = false;
		}else if(split.equals("or")) {
			andSplit = false;
			orSplit = true;
			xorSplit = false;
		}else if(split.equals("xor")) {
			andSplit = false;
			orSplit = false;
			xorSplit = true;
		}
		
		if(join.equals("and")) {
			andJoin = true;
			orJoin = false;
			xorJoin = false;
		}else if(join.equals("or")) {
			andJoin = false;
			orJoin = true;
			xorJoin = false;
		}else if(join.equals("xor")) {
			andJoin = false;
			orJoin = false;
			xorJoin = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Task clone() {
		Task clone = new Task(taskName, engineID, refersTo, isComposite, isMultiple, "clone", "clone");
		
		clone.offerMod = offerMod;
		clone.allocateMod = allocateMod;
		clone.startMod = startMod;
		clone.relres = relres;
		clone.rollback = rollback;
		
		clone.andJoin = andJoin;
		clone.andSplit = andSplit;
		clone.orJoin = orJoin;
		clone.orSplit = orSplit;
		clone.xorJoin = xorJoin;
		clone.xorSplit = xorSplit;
		clone.offerRes = (LinkedList<String>) offerRes.clone();
		clone.allocateRes = allocateRes;
		clone.startRes = startRes;
		clone.offerResDef = offerResDef; //Convert all in participant
		
		clone.status = status;
		clone.originalStatus = originalStatus;
		
		clone.linkedTo = linkedTo;
		
		clone.linkedFrom = linkedFrom;
		
		clone.originalOfferRes = originalOfferRes;
		clone.originalAllocateRes = originalAllocateRes;
		clone.originalStartRes = originalStartRes;
		
		clone.originalOfferResDef = originalOfferResDef; //Convert all in participant
		
		clone.originalLinkedTo = originalLinkedTo;
		clone.originalLinkedFrom = originalLinkedFrom;
		return clone;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Task) {
			Task t = (Task) o;
			if(t.engineID.equals(engineID)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String s = "\nTaskID: "+ engineID;
		s += "\nlinkedTo: "+linkedTo;
		s += "\nlinkedFrom: "+linkedFrom;
		return s;
	}
}
