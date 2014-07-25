package org.yawlfoundation.yawl.sensors.language.functions.doubleparameter.abstractfunctions;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class XMinNumberExcept {
	
	private static WorkQueueFacade _workQueue;
	
	public static void setWorkQueueClient(WorkQueueFacade workQueue) {
		_workQueue = workQueue;
	}
	
	public static String getFunctionResult(int type, String name, String term, HashMap<String, String> mappingName, HashMap<String, Object> variables, int queueType) {
		
		String s = name+"_"+term+"_"+type;
		
		LinkedList<String> resources = getListResource(variables.get(mappingName.get(name)));
		
		LinkedList<Integer> l = new LinkedList<Integer>();
		for(String id : resources) {
			Element xml = JDOMUtil.stringToElement(_workQueue.getQueuedWorkItems(id, queueType));
			LinkedList<String> list = new LinkedList<String>();
			for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
				list.addLast(workItemRecord.getChild("taskid").getValue());
			}
			
			if(mappingName.containsKey(term)) {
				if(variables.get(mappingName.get(term))==null) {
					l.addLast(list.size());
				}else {
					boolean check = false;
					LinkedList<String> resources2 = getListResource(variables.get(mappingName.get(term)));
					for(String id2 : resources2) {
						if(id.equals(id2)) {
							check = true;
							break;
						}
					}
					if(!check) l.addLast(list.size());
				}
			}else {
				if(!id.equals(term)) l.addLast(list.size()); 
			}
		}
		if(l.size()==0) {
			variables.put(s, null);
		}else if(l.size()>1) {
			int min = (Integer) l.get(0);
			for(int i = 1; i<l.size(); i++) {
				min = Math.min(min, (Integer)l.get(i));
			}
			variables.put(s, ""+min);
		}else {
			variables.put(s, ""+((Integer)l.get(0)));
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
