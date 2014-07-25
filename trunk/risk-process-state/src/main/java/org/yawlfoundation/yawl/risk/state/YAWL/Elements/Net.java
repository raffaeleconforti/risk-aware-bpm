package org.yawlfoundation.yawl.risk.state.YAWL.Elements;

import org.yawlfoundation.yawl.risk.state.Node;

public class Net implements Node {
	
	public String netName = null;
	boolean removed = false;
	
	public boolean startTime = false;
	public boolean completeTime = false;
	
	public Integer rootElement = null;
	
	public Net(String netName) {
		this.netName = netName;
	}
	
	public int isLinkedTo() {
		return 0;
	}
	
	public int isLinkedFrom() {
		return 0;
	}
	
	public Net clone() {
		Net clone = new Net(netName);
		clone.removed = removed;
		clone.startTime = startTime;
		clone.completeTime = completeTime;
		clone.rootElement = rootElement;
		return clone;
	}
}
