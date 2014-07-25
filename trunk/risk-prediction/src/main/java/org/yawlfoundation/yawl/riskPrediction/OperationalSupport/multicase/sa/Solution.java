package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.sa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.RiskFunction;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 01/07/2013
 *
 */

public class Solution {

	Random ran = new Random(123456789);
	String[] resources = null;
	WorkItemRecord[] workItemsOffered = null;
	WorkItemRecord[] workItemsExecuting = null;
	
	DurationFunction durationFunction = null;
	RiskFunction riskFunction = null;
	
	HashMap<String, WorkItemRecord> resourceUtilization = null;
	HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization = null; 
	HashSet<HashSet<WorkItemRecord>> deferedChoices = null;
	
	long approximator = -1L;
	
	HashMap<String, LinkedList<WorkItemRecord>> solution = new HashMap<String, LinkedList<WorkItemRecord>>();
	
	public Solution(String[] resources, WorkItemRecord[] workItemsOffered, WorkItemRecord[] workItemsExecuting, 
			DurationFunction durationFunction, RiskFunction riskFunction, HashMap<String, WorkItemRecord> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, long approximator) {

		this.resources = resources;
		this.workItemsOffered = workItemsOffered;
		this.workItemsExecuting = workItemsExecuting;
		
		this.resourceUtilization = resourceUtilization;
		this.resourceAuthorization = resourceAuthorization;
		this.deferedChoices = deferedChoices;
		
		this.durationFunction = durationFunction;
		this.riskFunction = riskFunction;
		
		this.approximator = approximator;
	}
	
	public void generateInitialAssignment() {
		for(String resource : resources) {
			LinkedList<WorkItemRecord> value = new LinkedList<WorkItemRecord>();
			solution.put(resource, value);
		}
		
		for(Entry<String, WorkItemRecord> entry : resourceUtilization.entrySet()) {
			solution.get(entry.getKey()).add(entry.getValue());
		}
		
		boolean done = false;
		for(HashSet<WorkItemRecord> set : deferedChoices) {
			int w = ran.nextInt(set.size());
			
			int count = 0;
			for(WorkItemRecord workItem : set) {
				if(count == w) {
					done = false; 
					do {
						int r = ran.nextInt(resources.length);
						String res = resources[r];
						if(resourceAuthorization.get(res).contains(workItem)) {
							LinkedList<WorkItemRecord> value = solution.get(res);
							value.add(workItem);
							done = true;
						}
					} while(!done);
					break;
				};
				count++;
			}
		}
	}
	
	public Solution generateSolution() {
		Solution result = this.clone();
		int count = 0;
		WorkItemRecord wir = null; 
		
//		System.out.println("Start remotion");
		
		boolean done = false;
		do {
			int r1 = ran.nextInt(result.resources.length);
			String res = resources[r1];
			count = 0;
			LinkedList<WorkItemRecord> set = result.solution.get(res);
			if((set.size() > 0 && resources.length > (workItemsExecuting.length + workItemsOffered.length))
				|| (set.size() > 1 && resources.length <= (workItemsExecuting.length + workItemsOffered.length))) {
				boolean remove = true;
				int w = ran.nextInt(set.size());
				wir = set.get(w);
				
				if(w == 0) {
					for(WorkItemRecord wir1 : result.workItemsExecuting) {
						if(wir1.equals(wir)) {
							remove = false;
							break;
						}
					}
				}
				
				if(remove) {
//					System.out.println(res+" "+wir.getCaseID()+" "+wir.getTaskID());
					set.remove(w);
					done = true;
				}
				if(done) break;
			}
		} while(!done);
		
//		System.out.println("End remotion");
//		System.out.println("Start change");
		
		for(HashSet<WorkItemRecord> set : deferedChoices) {
			if(set.contains(wir)) {
				int w = ran.nextInt(set.size());
				count = 0;
				for(WorkItemRecord wir2 : set) {
					if(count == w) {
						wir = wir2;
						break;
					}
					count++;
				}
				break;
			}
		}
		
//		System.out.println("End change");
//		System.out.println("Start insertion");
		
		done = false;
		do {
			count = 0;
			int r2 = ran.nextInt(result.resources.length);
			String res = resources[r2];
			count = 0;
			LinkedList<WorkItemRecord> set = result.solution.get(res);
			if(resourceAuthorization.get(res).contains(wir)) {
//				do {
					int w = -1;
					if(set.size() == 0) w = 0; 
					else w = ran.nextInt(set.size());
					if(resourceUtilization.get(res) == null || w != 0) {
//						System.out.println(res+" "+wir.getCaseID()+" "+wir.getTaskID());
						set.add(w, wir);
						done = true;
					}
//				} while(!done);
//				break;
			}
		}while(!done);
		
//		System.out.println("End insertion");
		
		return result;
	}
	
	public Double[] evaluate() {
		Double risk = 0.0;
		Double time = 1.0;
		
		for(Entry<String, LinkedList<WorkItemRecord>> entry: solution.entrySet()) {
			long partTime = 1L;
			for(WorkItemRecord wir : entry.getValue()) {
				risk += riskFunction.getRisk(wir, entry.getKey(), partTime);
				partTime += durationFunction.getDuration(entry.getKey(), wir);
			}
			time = Math.max(partTime, time);
		}
		
		return new Double[] {risk, time};
	}
	
	@Override
	public Solution clone() {
		Solution result = new Solution(this.resources, this.workItemsOffered, this.workItemsExecuting, this.durationFunction,
				this.riskFunction, this.resourceUtilization, this.resourceAuthorization, this.deferedChoices, this.approximator);
		result.solution = (HashMap<String, LinkedList<WorkItemRecord>>) this.solution.clone();
		return result;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		Double[] e = evaluate();
		
		sb.append("Risk: "+e[0]+" Time: "+e[1]+"\n\n");
		
		for(Entry<String, LinkedList<WorkItemRecord>> entry: solution.entrySet()) {
			sb.append("Resource: "+entry.getKey()+"\nWorkItems:\n");
			for(WorkItemRecord wir : entry.getValue()) {
				sb.append(wir.getCaseID()+" "+wir.getTaskID()+"\n");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Solution) {
			Solution s1 = (Solution) o;
			for(Entry<String, LinkedList<WorkItemRecord>> entry : s1.solution.entrySet()) {
				if(!this.solution.get(entry.getKey()).equals(entry.getValue())) return false;
			}
			return true;
		}else {
			return false;
		}
	}

}
