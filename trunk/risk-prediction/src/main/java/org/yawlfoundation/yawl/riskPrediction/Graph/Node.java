package org.yawlfoundation.yawl.riskPrediction.Graph;

import java.util.LinkedList;

public class Node {
	
	String name;
	LinkedList<Node> next = new LinkedList<Node>();
	LinkedList<Node> prec = new LinkedList<Node>();
	
	public Node(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LinkedList<Node> getNext() {
		return next;
	}
	
	public void setNext(Node next) {
		this.next.add(next);
		next.setPrec(this);
	}
	
	public LinkedList<Node> getPrec() {
		return prec;
	}
	
	private void setPrec(Node prec) {
		this.prec.add(prec);
	}
	
	

}
