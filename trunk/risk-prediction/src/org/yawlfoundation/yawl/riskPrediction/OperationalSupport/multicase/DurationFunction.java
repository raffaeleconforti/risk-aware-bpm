package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.sensors.actions.ActionInitializer;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityEstimationTimeInMillis;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.timePrediction.TimePredictionClient;

import com.google.ortools.constraintsolver.Action;

public class DurationFunction {
	
	InterfaceManager imStart = null;
	InterfaceManager imUpdated = null;
	HashMap<WorkItemRecord, Long> durationMap = new HashMap<WorkItemRecord, Long>();
	
	HashMap<String, TimePredictionClient> mapTPC = new HashMap<String, TimePredictionClient>();
	
	public DurationFunction() {
		
	}
	
	public DurationFunction(InterfaceManager imStart, InterfaceManager imUpdated) {
		
		this.imStart = imStart;
		this.imUpdated = imUpdated;
		
	}
	
	public long getDuration(String resource, WorkItemRecord workItem) {
		
		return getDuration(workItem);
		
	}
	
	public long getDuration1(WorkItemRecord workItem) {
		
		int a = Math.abs("resource".hashCode());
//		int a = Math.abs(resource.hashCode());
		int b = Math.abs((workItem.getRootCaseID()+" "+workItem.getTaskID()).hashCode());
		
		double res = 0;
		try{
			res = Integer.parseInt(Integer.toString(a+b).substring(0, 6));
		}catch(StringIndexOutOfBoundsException e) {
			res = a+b;
		}
		
		res /= 1000;
		res /= 60;
//		res /= 12;
		
//		res /= approximator;
		res = (int)res;
		
		double mod = res % 10;
		if(mod > 5) res += 10 - mod;
		else if (mod > 0) res -= mod;
		
		if(res == 0) res++;
		
//		System.out.println("Time "+res);
		return (int)res;
		
	}
	
	public long getDuration(WorkItemRecord workItem) {
		
		Long resultDuration = null;
		if((resultDuration = durationMap.get(workItem)) == null) {
			TimePredictionClient tpc = null;
			
			if((tpc = mapTPC.get(workItem.getSpecURI())) == null) {
				
				tpc = TimePredictionClient.getInstance(imStart);
				
				mapTPC.put(workItem.getSpecURI(), tpc);
				
			}
			
			LinkedList<String> workDefIDs = new LinkedList<String>();
			workDefIDs.add(workItem.getRootCaseID());
			LinkedList<ActivityTuple> result = imUpdated.getActivityLayer().getRows(null, false, null, false, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
	
//			LinkedList<ActivityTuple> isStarted = imUpdated.getActivityLayer().getRows(null, false, workItem.getTaskID(), true, workDefIDs, true, 0, 0, Activity.Executing, true, null, false, false);
//			long passTime = 0;
//			if(isStarted != null && isStarted.size() > 0) {
//				passTime = System.currentTimeMillis() - Long.parseLong(isStarted.getFirst().getTime());
//			}
			
			LinkedList<String> executedTask = new LinkedList<String>();
	
			if(result != null) {
				Collections.sort(result);
				
				for(ActivityTuple tuple : result) {
					executedTask.add(tuple.getName());
				}
			}
			
//			Map<String, String> params = new HashMap<String, String>();
//	        params.put("action", "Prediction");
//	        params.put("caseID", ""+workItem.getRootCaseID());
//	        params.put("task", ""+workItem.getTaskID());
//	        
//	        StringBuilder listActivities = new StringBuilder("<activities>");
//	        
//	        for(String activity : executedTask) {
//	        	listActivities.append("<activity>");
//	        	listActivities.append(activity);
//	        	listActivities.append("</activity>");
//	        }
//	        
//	        listActivities.append("</activities>");
//	        
//	        params.put("listActivities", listActivities.toString());
	        
	//        System.out.println(Long.parseLong(tpc.getPrediction(workItem.getRootCaseID(), executedTask, workItem.getTaskID(), true)) - passTime);
//	        resultDuration = (Long.parseLong(tpc.getPrediction(workItem.getRootCaseID(), executedTask, workItem.getTaskID(), true)) - passTime);
			resultDuration = (Long.parseLong(tpc.getPrediction(workItem.getRootCaseID(), executedTask, workItem.getTaskID(), true)));
	        
	        if(resultDuration < 1) {
	        	resultDuration = 1L;
	        }
	        	
	        durationMap.put(workItem, resultDuration);
		}
        return resultDuration;
	        
	}
	
	public void removeDuration(WorkItemRecord workItem) {
		durationMap.remove(workItem);
	}

}
