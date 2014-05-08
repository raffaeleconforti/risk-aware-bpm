package org.yawlfoundation.yawl.sensors;

import org.jdom.Element;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class Resource {
	
	Element resource = null;
	
	public Resource(String resource) {
		this.resource = JDOMUtil.stringToElement(resource);
	}
	
	public String getID() {
		return resource.getAttributeValue("id");
	}
}
