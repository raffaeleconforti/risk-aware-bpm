package org.yawlfoundation.yawl.riskMitigation.State.YAWL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;


public class WorkQueueFacadeMap extends WorkQueueFacade {

	private HashSet<String> taskRelevant = null;
	private HashMap<String, Resource> resourcesMap = null;
	private ReentrantLock lock = new ReentrantLock(true);
	
	public WorkQueueFacadeMap(HashSet<String> taskRelevant, HashMap<String, Resource> resourcesMap) {
		super();
		this.taskRelevant = taskRelevant;
		this.resourcesMap = resourcesMap;
	}
	
	public void update(HashSet<String> taskRelevant, HashMap<String, Resource> resourcesMap) {
		lock.lock();
		this.taskRelevant = taskRelevant;
		this.resourcesMap = resourcesMap;
	}
	
	@Override
	public String getQueuedWorkItems(String id, int queueType) {
		while(!lock.isHeldByCurrentThread()) {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		LinkedList<String> list = null;
		
		if(queueType == 0) {
			list = resourcesMap.get(id).offeredList;
		}else if(queueType == 1) {
			list = resourcesMap.get(id).allocatedList;
		}else if(queueType == 2) {
			list = resourcesMap.get(id).startedList;
		}
		taskRelevant.addAll(list);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<workQueue>");
		for(String s : list) {
			sb.append("<workItemRecord><taskid>");
			sb.append(s);
			sb.append("</workItemRecord></taskid>");
		}
		sb.append("</workQueue>");
		
		lock.unlock();
		
		return sb.toString();
	}

}
