package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers;

import java.util.HashMap;
import java.util.LinkedList;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Resource;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;


public class ResourceImporter {

	public static void produceResourcesMap(InterfaceManager im, HashMap<String, Resource> resourcesMap, String caseID) {
	
		Role roleLayer = im.getRoleLayer();
		
		for(String role : roleLayer.getAllID()) {
						
			String info = roleLayer.getRoleInfo(role);
			Resource r = new Resource(info);
			
			LinkedList<String[]> listTasks = roleLayer.getTasksOffered(role);
			if(listTasks != null) {
				for(String[] task : listTasks) {
					
					if(task[0].equals(caseID)) {
						r.addOffer(task[1]);
					}
					
				}
			}
			
			listTasks = roleLayer.getTasksAllocated(role);
			if(listTasks != null) {
				for(String[] task : listTasks) {
				
					if(task[0].equals(caseID)) {
						r.addAllocate(task[1]);
					}
					
				}
			}
			
			listTasks = roleLayer.getTasksStarted(role);
			if(listTasks != null) {
				for(String[] task : listTasks) {
				
					if(task[0].equals(caseID)) {
						r.addStart(task[1]);
					}
				
				}
			}
			
			resourcesMap.put(info, r);
			
		}
		
	}
	
}
