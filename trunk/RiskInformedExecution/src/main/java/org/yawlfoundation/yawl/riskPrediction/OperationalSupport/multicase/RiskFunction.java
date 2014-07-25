package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.util.HashMap;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

public class RiskFunction {
	
	private HashMap<WorkItemRecord, HashMap<String, HashMap<Long, Long>>> riskMap = new HashMap<WorkItemRecord, HashMap<String, HashMap<Long, Long>>>();
	
	public RiskFunction() {}
	
	public RiskFunction(DurationSplitter ds) {
		this.riskMap = ds.getRiskMap();
	}
	
//	public long getRisk(String resource, WorkItemRecord workItem, long time) {
//		
//		int a = Math.abs(resource.hashCode());
//		int b = Math.abs((workItem.getRootCaseID()+" "+workItem.getTaskID()).hashCode());
//		
//		long tot = (a+b)*time;
//		
//		String c = Long.toString(tot);
//
//		int res = Integer.parseInt(c.substring(c.length()-3));
//
////		System.out.println(resource+" "+workItem.getCaseID()+" "+workItem.getTaskID()+" "+time+": "+res);
//		return res;
//		
//	}
	
//	public double getRisk(WorkItemRecord workItem, String resource, long time) {
//		
//		int a = Math.abs(resource.hashCode());
//		int b = Math.abs((workItem.getRootCaseID()+" "+workItem.getTaskID()).hashCode());
//		
//		long tot = (a+b)*time;
//		
//		String c = Long.toString(tot);
//
//		int res = Integer.parseInt(c.substring(c.length()-3));
//
////		System.out.println(resource+" "+workItem.getCaseID()+" "+workItem.getTaskID()+" "+time+": "+res);
//		return res;
//		
//	}
	
	public double getRisk(WorkItemRecord workItem, String resource, long time) {
		
		return (((double) riskMap.get(workItem).get(resource).get(time)) / 100.0);
		
	}
	
}
