package org.yawlfoundation.yawl.risk.state.YAWL;

import java.util.LinkedList;

public class Resource implements Cloneable{
	public String id = null;
    public LinkedList<String> offeredList = new LinkedList<String>();
    public LinkedList<String> allocatedList = new LinkedList<String>();
    public LinkedList<String> startedList = new LinkedList<String>();
	
	public Resource(String resourceID) {
		id = resourceID;
		//populate lists
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return id;
	}
	
	public void removeOffer(String task) {
		offeredList.remove(task);
	}
	
	public void removeAllocate(String task) {
		allocatedList.remove(task);
	}

	public void removeStart(String task) {
		startedList.remove(task);
	}
	
	public void addOffer(String task) {
		offeredList.add(task);
	}
	
	public void addAllocate(String task) {
		allocatedList.add(task);
	}

	public void addStart(String task) {
		startedList.add(task);
	}

	public LinkedList<String> getOfferedList() {
		return offeredList;
	}

	public LinkedList<String> getAllocatedList() {
		return allocatedList;
	}

	public LinkedList<String> getStartedList() {
		return startedList;
	}
	
	@Override
	public String toString() {
		return id+" offered: "+offeredList+" allocated: "+allocatedList+" started: "+startedList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		Resource clone = new Resource(id);
		clone.offeredList = (LinkedList<String>) offeredList.clone();
		clone.allocatedList = (LinkedList<String>) allocatedList.clone();
		clone.startedList = (LinkedList<String>) startedList.clone();
		return clone;
	}
}