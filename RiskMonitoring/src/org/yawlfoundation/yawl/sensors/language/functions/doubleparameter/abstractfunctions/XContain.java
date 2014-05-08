package org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.util.JDOMUtil;

public abstract class XContain {
	
	private static WorkQueueFacade _workQueue;
	
	public static void setWorkQueueClient(WorkQueueFacade workQueue) {
		_workQueue = workQueue;
	}
	
	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables, int queueType) {
		
		String s = name+"_"+term+"_"+type;
		
		LinkedList<String> resources = getListResource(variables.get(mappingName.get(name)));
		
		LinkedList<Boolean> l = new LinkedList<Boolean>();
		for(String id : resources) {
			Element xml = JDOMUtil.stringToElement(_workQueue.getQueuedWorkItems(id, queueType));
			LinkedList<String> list = new LinkedList<String>();
			for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
				list.addLast(workItemRecord.getChild("taskid").getValue());
			}

			if(mappingName.containsKey(term)) {
				if(variables.get(mappingName.get(term))==null) {
					l.addLast(null);
				}else {
					l.addLast(list.contains(variables.get(mappingName.get(term))));
				}
			}else {
				l.addLast(list.contains(term)); 
			}
			
		}
		if(l.size()==0) {
			variables.put(s, null);
		}else if(l.size()>1) {
			variables.put(s, l);
		}else {
			variables.put(s, l.get(0));
		}
		
		return name+"_"+term+"_"+type;
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
