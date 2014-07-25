package org.yawlfoundation.yawl.sensors.language.functions.singleparameter.abstractfunctions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.util.JDOMUtil;

public abstract class XNumber {
	
	private static WorkQueueFacade _workQueue;
	
	public static void setWorkQueueClient(WorkQueueFacade workQueue) {
		_workQueue = workQueue;
	}
	
	public static String getFunctionResult(int type, String name, HashMap<String, String> mappingName, HashMap<String, Object> variables, int queueType) {
		String s = name+"_"+type;
		
		LinkedList<String> resources = getListResource(variables.get(mappingName.get(name)));
		
		LinkedList<String> l = new LinkedList<String>();
		for(String id : resources) {
			Element xml = JDOMUtil.stringToElement(_workQueue.getQueuedWorkItems(id, queueType));
			LinkedList<String> list = new LinkedList<String>();
			for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
				list.addLast(workItemRecord.getChild("taskid").getValue());
			}

			l.addLast(""+list.size()); 
			
		}
		if(l.size()==0) {
			variables.put(s, null);
		}else if(l.size()>1) {
			variables.put(s, l);
		}else {
			variables.put(s, l.getFirst());
		}
		
		return s;
	}
	
    private static LinkedList<String> getListResource(Object o) {
    	LinkedList<String> res = null;
		if(o instanceof String) {
			res = new LinkedList<String>();
			res.add((String) o);
		}else if(o instanceof LinkedList) {
			res = (LinkedList<String>) o;
		}else throw new RuntimeException("not a resource");
		return res;
    }

}
