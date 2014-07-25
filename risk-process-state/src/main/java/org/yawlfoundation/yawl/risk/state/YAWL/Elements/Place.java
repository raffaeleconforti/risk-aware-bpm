package org.yawlfoundation.yawl.risk.state.YAWL.Elements;

import org.yawlfoundation.yawl.risk.state.Node;

import java.util.LinkedList;


public class Place implements Node {
	
	public String engineID = null;
	public Integer referTo = null;
	
	public boolean isStartPlace = false;
	public boolean isEndPlace = false;
	
	public LinkedList<Integer> linkedTo = new LinkedList<Integer>();
	
	public LinkedList<Integer> linkedFrom = new LinkedList<Integer>();
	
	public Place(String engineID, boolean isStartPlace, boolean isEndPlace) {
		this.engineID = engineID;
		this.isStartPlace = isStartPlace;
		this.isEndPlace = isEndPlace;
	}
	
	public void setLinkedTo(LinkedList<Integer> linkedTo) {
		this.linkedTo = linkedTo;
	}
	
	public void setLinkedFrom(LinkedList<Integer> linkedFrom) {
		this.linkedFrom = linkedFrom;
	}
	
	public Place clone() {
		Place clone = new Place(null, false, false);
		clone.engineID = engineID;
		
		clone.isStartPlace = isStartPlace;
		clone.isEndPlace = isEndPlace;
		
		clone.linkedTo = linkedTo;
		
		clone.linkedFrom = linkedFrom;
		
		return clone;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Place) {
			Place p = (Place) o;
			if(p.engineID.equals(engineID)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String s = "\nPlaceID: "+engineID;
		s += "\nlinkedTo: "+linkedTo;
		s += "\nlinkedFrom: "+linkedFrom;
		return s;
	}
}
