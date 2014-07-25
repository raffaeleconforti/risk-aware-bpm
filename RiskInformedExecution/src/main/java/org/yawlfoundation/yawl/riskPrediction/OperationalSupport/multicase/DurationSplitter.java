package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import weka.classifiers.Classifier;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 02/07/2013
 *
 */

public class DurationSplitter {
	
	private HashMap<WorkItemRecord, HashMap<String, HashSet<long[]>>> map = new HashMap<WorkItemRecord, HashMap<String, HashSet<long[]>>>();
	private HashMap<WorkItemRecord, HashMap<String, HashMap<Long, Long>>> riskMap = new HashMap<WorkItemRecord, HashMap<String, HashMap<Long, Long>>>();
	
	private String ls = "<";
	private String le = "<=";
	private String eq = "=";
	private String ge = ">=";
	private String gt = ">";
	private String neq = "!=";
	
	private String space = " ";
	private String colomn = ":";
	private String timeBetween = "TimeBetween_";
	
	private long approximation = 1;
	private long max = Long.MAX_VALUE;
	
	public DurationSplitter(long max) {
		this.max = max;
	}

	public DurationSplitter(List<TupleClassifierName> tupleClassifierNames, long max, long currTime, long approximation) {
		
		for(TupleClassifierName tupleClassifierName : tupleClassifierNames) {
			map.put(tupleClassifierName.getWorkItem(), generateSplits(tupleClassifierName.getClassifiers(), tupleClassifierName.getWorkItem(), tupleClassifierName.getResources(), tupleClassifierName, max, currTime));
		}
		
		this.approximation = approximation;
		
	}
	
	public HashMap<String, HashSet<long[]>> generateSplits(LinkedList<Classifier> classifiers,	WorkItemRecord workItem, 
			String[] resources, TupleClassifierName tuple, long max, long currTime) {
		
		HashMap<String, HashSet<long[]>> res = new HashMap<String, HashSet<long[]>>();
		HashSet<long[]> tmp = new HashSet<long[]>();
		LinkedList<Split> list = new LinkedList<Split>();
		HashSet<String> predecessors = new HashSet<String>();
		
		String token = null;
		String partial = null;
		String predecessor = null;
		String comparison = null;
		String compareOperator = null;
		String value = null;
		
		for(Classifier classifier : classifiers) {
			StringTokenizer st = new StringTokenizer(classifier.toString(), "\n");
			while(st.hasMoreTokens()) {
				token = st.nextToken();
				if(token.contains(timeBetween) && token.contains(workItem.getTaskID())) {
					partial = token.substring(token.indexOf(timeBetween), token.length());
					predecessor = partial.substring(12, partial.indexOf(workItem.getTaskID())-1);
					comparison = null;
					if(partial.contains(colomn)) {
						comparison = partial.substring(partial.indexOf(space)+1, partial.indexOf(colomn));
					}else {
						comparison = partial.substring(partial.indexOf(space)+1, partial.length());
					}
					compareOperator = comparison.substring(0, comparison.indexOf(space));
					value = comparison.substring(comparison.indexOf(space)+1, comparison.length());
					predecessors.add(predecessor);
					list.add(new Split(predecessor, compareOperator, Double.parseDouble(value)));
				}
			}
		}
		
		HashMap<String, Long> timePredecessors = new HashMap<String, Long>();
		for(String pred : predecessors) {
			Long t1 = tuple.getTime(pred);
			if(t1 != null) {
				timePredecessors.put(pred, t1);
			}else {
				timePredecessors.put(pred, 0L);
			}
		}
		
		HashSet<Long> times = new HashSet<Long>();
		for(Split split : list) {
			long val = -1;
			if(split.operator.equals(ls)) val = ((long) split.value)+timePredecessors.get(split.predecessor)-1;
			else if(split.operator.equals(le)) val = ((long) split.value)+timePredecessors.get(split.predecessor);
			else if(split.operator.equals(eq)) val = ((long) split.value)+timePredecessors.get(split.predecessor);
			else if(split.operator.equals(ge)) val = ((long) split.value)+timePredecessors.get(split.predecessor);
			else if(split.operator.equals(gt)) val = ((long) split.value)+timePredecessors.get(split.predecessor)+1;
			else if(split.operator.equals(neq)) val = ((long) split.value)+timePredecessors.get(split.predecessor);
			
			times.add(val);
		}
		
		times.add(max);

		long[] importantTimes = new long[times.size()];
		int pos = 0;
		for(long l : times) {
			importantTimes[pos] = l;
			pos++;
		}
		
		Arrays.sort(importantTimes);
		long current = 1L;
		
		for(long t : importantTimes) {

			long[] l = new long[] {current, t};
			tmp.add(l);
			current = t;
			
			if(current == max) break;
			
		}
		
		HashMap<String, HashMap<Long, Long>> riskRes = new HashMap<String,HashMap<Long,Long>>();
		HashMap<Long, Long> riskTime = null;
		HashSet<long[]> splits = null;
		for(String resource : resources) {
			riskTime = new HashMap<Long, Long>();
			long result = -1L;
			current = 1L;
			splits = new HashSet<long[]>();
			for(long[] lo : tmp) {
				Long pred = tuple.checkInstance(resource, lo[0]+currTime);
				if(pred != result) {
					riskTime.put(getDurationWithApproximation(lo[0]), pred);
					long[] l = new long[] {getDurationWithApproximation(current), getDurationWithApproximation(lo[1])};
					splits.add(l);
					current = lo[1] + approximation;
					result = pred;
				}
			}
			
			riskRes.put(resource, riskTime);
			res.put(resource, splits);
		}
		
		riskMap.put(workItem, riskRes);
		
		return res;
	}
	
	private long getDurationWithApproximation(long x) {
		return x / approximation;
	}

	public HashMap<WorkItemRecord, HashMap<String, HashMap<Long, Long>>> getRiskMap() {
		return riskMap;
	}

	public HashSet<long[]> getSplitts(WorkItemRecord wir, String resource) {
		HashSet<long[]> set = new HashSet<long[]>();
		
		return map.get(wir).get(resource);
		
//		int i = 1;
////		while(i*10 < max) {
////			long[] l1 = new long[]{i, i*10};
////			set.add(l1);
////			i*=10;
////		}
//		
//		if(i <= max) {
//			long[] l1 = new long[]{i, max};
//			set.add(l1);
//		}
//		
//		return set;
	}
	
	class Split implements Comparable<Split>{
		
		String predecessor;
		String operator;
		double value;
		
		public Split(String predecessor, String operator, double value) {
			this.predecessor = predecessor;
			this.operator = operator;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return predecessor+" "+operator+" "+value;
		}

		@Override
		public int compareTo(Split o) {
			if(o.value == value) {
				int cond1 = -1;
				int cond2 = -1;
				if(o.operator.equals(ls)) cond1 = 1;
				else if(o.operator.equals(le)) cond1 = 2;
				else if(o.operator.equals(eq)) cond1 = 3;
				else if(o.operator.equals(ge)) cond1 = 4;
				else if(o.operator.equals(gt)) cond1 = 5;
				else if(o.operator.equals(neq)) cond1 = 6;

				if(operator.equals(ls)) cond2 = 1;
				else if(operator.equals(le)) cond2 = 2;
				else if(operator.equals(eq)) cond2 = 3;
				else if(operator.equals(ge)) cond2 = 4;
				else if(operator.equals(gt)) cond2 = 5;
				else if(operator.equals(neq)) cond2 = 6;
				
				if(cond1 == cond2) return 0;
				else if(cond1 == 1 && cond2 == 4) return 1;
				else if(cond1 == 2 && cond2 == 5) return 1;
				else if(cond1 == 3 && cond2 == 6) return 0;
				else if(cond1 == 4 && cond2 == 1) return -1;
				else if(cond1 == 5 && cond2 == 2) return -1;
				else if(cond1 == 6 && cond2 == 3) return 0;
			}else {
				if(o.value < value) return 1;
				else return -1;
			}
			return 0;
		}
		
		
	}

	public void clear() {
		for(Entry<WorkItemRecord, HashMap<String, HashSet<long[]>>> entry : map.entrySet()) {
			entry.getValue().clear();
		}
		map.clear();
		
		for(Entry<WorkItemRecord, HashMap<String, HashMap<Long, Long>>> entry : riskMap.entrySet()) {
			for(Entry<String, HashMap<Long, Long>> entry2 : entry.getValue().entrySet()) {
				entry2.getValue().clear();
			}
			entry.getValue().clear();
		}
		riskMap.clear();
		
	}
	
}

