package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.gurobi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.google.ortools.constraintsolver.DecisionBuilder;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.OptimizeVar;
import com.google.ortools.constraintsolver.Solver;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import lpsolve.*;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.RiskFunction;

public class LPBuilderNew {
	
	private HashMap<String, Integer> cacheD = new HashMap<String, Integer>();
	
	static {
		System.loadLibrary("jniconstraintsolver");
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void execute(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItemsOffered, DurationFunction durationFunction, HashMap<String, WorkItemRecord> resourceUtilization, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, RiskFunction riskFunction, HashSet<HashSet<WorkItemRecord>> deferedChoices, HashMap<String, WorkItemRecord> result) throws LpSolveException {

		boolean optimal = false;
		
		WorkItemRecord[] workItems = Arrays.copyOf(workItemsOffered, workItemsOffered.length + workItemsExecuting.length);
		System.arraycopy(workItemsExecuting, 0, workItems, workItemsOffered.length, workItemsExecuting.length);
		
		long baseTime = System.currentTimeMillis();
		long maxDuration = findMaxDuration(resources, workItems, durationFunction);
		System.out.println("maxDuration "+maxDuration);
		long durationTimeFrame = findGCD(resources, workItems, durationFunction);
		
		long minDuration = findMinDuration(resources, workItems, durationFunction, durationTimeFrame);
		
		int missingPart = (int) minDuration;
		durationTimeFrame *= missingPart;
		
		System.out.println("durationTimeFrame "+durationTimeFrame);
		int horizon = (int) (maxDuration/durationTimeFrame);
		System.out.println("horizon "+horizon);
		
		int[] totalChunks = findBiggestChunks(resources, workItems, durationFunction, durationTimeFrame);
		int[] chunksSize = findChunks(resources, workItems, durationFunction, durationTimeFrame); 
		long lcm = findLCM(chunksSize);
		
		int totalNumberChunks = 0;
		
		for(int i = 0; i < totalChunks.length; i++) {
			totalNumberChunks += totalChunks[i];
		}
		int numberD = resources.length * workItems.length * horizon;
		System.out.println("numberD "+numberD);
		
		Solver solver = new Solver("Scheduling");
		int Ncol, j, ret = 0;

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */
		Ncol = numberD;

		/* create space large enough for one row */
		String[] colno = new String[Ncol];
		double[] row = new double[Ncol];

		/* let us name our variables. Not required, but can be useful for debugging */

		WorkItemRecord currWorkItem = null;
		String currResource = null;
		
		IntVar[] d = solver.makeIntVarArray(numberD, 0, 1, "x");
		
		System.out.println("set name d");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
					
				for(int t = 0; t < horizon; t++) {
			
					int i = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon);
					colno[i] = currResource+"_"+getStringWorkItem(currWorkItem)+"_Time_"+t;
				
				}
						
			}
			
		}
		
		ArrayList<IntVar> array = null;
		ArrayList<Double> arrayD = null;
		ArrayList<String> arrayVar = null;
		
		StringBuilder constrain1 = new StringBuilder();
		/* construct first constrain */
		System.out.println("first constrain");		
		for(int r = 0; r < resources.length; r++) {
			currResource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
				
				int chunks = getNumberChunks(currResource, currWorkItem, durationFunction, durationTimeFrame);
					
				for(int t = 0; t < horizon; t++) {
					
					array = new ArrayList<IntVar>();
					arrayD = new ArrayList<Double>();
					arrayVar = new ArrayList<String>();

					int dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon);
//					row[dPos] = -chunks;
											
					for(int c = -chunks+1; c < chunks; c++) {
//						System.out.println(chunks+" "+c);
						Integer xPos = findPositionD(currResource, currWorkItem, t+c, resources, workItems, durationFunction, horizon);

						if(xPos != null) {
							array.add(d[xPos]);

							arrayVar.add(colno[xPos]);
							
							if(xPos == dPos) {
								arrayD.add(1.0 - chunks);
							}else {
								arrayD.add(1.0);
							}
						}
						
					}

					/* add the row to lpsolve */
					constrain1.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", 0)+";\n");
					solver.addConstraint(solver.makeScalProdGreaterOrEqual(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), 0));
					
				}
				
			}
			
		}

		StringBuilder constrain2 = new StringBuilder();
		/* construct second constrain */
		System.out.println("second constrain");		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int t = 0; t < horizon; t++) {

				array = new ArrayList<IntVar>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				for(int w = 0; w < workItems.length; w++) {
				
					currWorkItem = workItems[w];
					
					int dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon);
					
					array.add(d[dPos]);
					arrayD.add(1.0);
					arrayVar.add(colno[dPos]);
					
				}
				
				/* add the row to lpsolve */
				constrain2.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "<=", 1)+";\n");
				solver.addConstraint(solver.makeScalProdLessOrEqual(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), 1));
				
			}
			
		}			

		StringBuilder constrain3 = new StringBuilder();
		/* costruct third constrain */
		System.out.println("third constrain");
		for(HashSet<WorkItemRecord> set : deferedChoices) {

			array = new ArrayList<IntVar>();
			arrayVar = new ArrayList<String>();
			arrayD = new ArrayList<Double>();
						
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(WorkItemRecord workItem : set) {
					
					int chunk = getNumberChunks(currResource, workItem, durationFunction, durationTimeFrame);
			
					for(int t = 0; t < horizon; t++) {
											
						int dPos = findPositionD(currResource, workItem, t, resources, workItems, durationFunction, horizon);

						array.add(d[dPos]);
						arrayVar.add(colno[dPos]);
						arrayD.add(((double) lcm)/ ((double) chunk));
														
					}
					
				}
				
			}

			/* add the row to lpsolve */
			constrain3.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "=", (int) lcm)+";\n");
			solver.addConstraint(solver.makeScalProdEquality(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), lcm));
				
		}

		StringBuilder constrain4 = new StringBuilder();
		/* construct fourth constrain */
		System.out.println("fourth constrain");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItems.length; w++) {

				currWorkItem = workItems[w];

				HashSet<WorkItemRecord> set = resourceAuthorization.get(currResource);
				int can = 0;
				if(set != null) {
					can = set.contains(currWorkItem)?1:0;
				}
				
				for(int t = 0; t < horizon; t++) {
					
					array = new ArrayList<IntVar>();
					arrayD = new ArrayList<Double>();
					arrayVar = new ArrayList<String>();
					
					int dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon);

					array.add(d[dPos]);
					arrayVar.add(colno[dPos]);
					arrayD.add(1.0);

					/* add the row to lpsolve */
					constrain4.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "<=", can)+";\n");
					solver.addConstraint(solver.makeScalProdLessOrEqual(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), can));		
				}
				
			}
			
		}

		StringBuilder constrain5 = new StringBuilder();
		/* construct fifth constrain */
		System.out.println("fifth constrain");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItemsExecuting.length; w++) {

				array = new ArrayList<IntVar>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				currWorkItem = workItemsExecuting[w];
					
				int dPos = findPositionD(currResource, currWorkItem, 0, resources, workItems, durationFunction, horizon);

				array.add(d[dPos]);
				arrayD.add(1.0);
				arrayVar.add(colno[dPos]);
				
				int use = currWorkItem.equals(resourceUtilization.get(currResource))?1:0;
					
				/* add the row to lpsolve */
				constrain5.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "=", use)+";\n");
				solver.addConstraint(solver.makeScalProdEquality(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), use));
										
			}
			
		}

//		StringBuilder constrain6 = new StringBuilder();
//		/* construct sixth constrain */
//		System.out.println("sixth constrain");
//		array = new ArrayList<IntVar>();
//		arrayD = new ArrayList<Double>();
//		for(int r = 0; r < resources.length; r++) {
//			
//			currResource = resources[r];
//			
//			for(int w = 0; w < workItems.length; w++) {
//				
//				currWorkItem = workItems[w];
//				
//				int dPos = findPositionD(currResource, currWorkItem, 0, resources, workItems, durationFunction, horizon);
//				
//				array.add(d[dPos]);
//				arrayD.add(1.0);
//				arrayVar.add(colno[dPos]);
//				
//			}
//			
//		}
//		
//		/* add the row to lpsolve */
//		constrain6.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", Math.min(workItems.length, resources.length))+";\n");
//		solver.addConstraint(solver.makeScalProdGreaterOrEqual(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), Math.min(workItems.length, resources.length)));
//
//		StringBuilder constrain7 = new StringBuilder();
//		/* construct seventh constrain */
//		System.out.println("seventh constrain");
//		for(int r = 0; r < resources.length; r++) {
//			
//			currResource = resources[r];
//
//			for(int t = 0; t < horizon; t++) {
//
//				array = new ArrayList<IntVar>();
//				arrayD = new ArrayList<Double>();
//				arrayVar = new ArrayList<String>();
//				
//				for(int w = 0; w < workItems.length; w++) {
//					
//					currWorkItem = workItems[w];
//					
//					int tPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon);
//					
//					array.add(d[tPos]);
//					arrayD.add(-1.0 * t);
//					arrayVar.add(colno[tPos]);
//	
//					for(int t1 = 0; t1 < t; t1++) {
//						
//						int dPos = findPositionD(currResource, currWorkItem, t1, resources, workItems, durationFunction, horizon);
//						
//						array.add(d[dPos]);
//						arrayD.add(1.0);
//						arrayVar.add(colno[dPos]);
//						
//					}
//					
//				}
//			
//				/* add the row to lpsolve */
//				constrain7.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", 0)+";\n");
//				solver.addConstraint(solver.makeScalProdGreaterOrEqual(array.toArray(new IntVar[0]), converDoubleToLong(arrayD.toArray(new Double[0]), false), 0));
//				
//			}
//			
//		}

		/* set X as binary */
//		System.out.println("sixth and seventh constrain");
//		for(int i = 1; i <= Ncol; i++) {
//				
//			lp.setBinary(i, true);
//
//		}

		System.out.println("Constraints: "+solver.constraints());
			
		System.out.println("objective function");
		row = new double[Ncol];
		
		for(int r = 0; r < resources.length; r++) {
			
			String resource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				WorkItemRecord workItem = workItems[w];
					
				for(int t = 0; t < horizon; t++) {
					
					int dPos = findPositionD(resource, workItem, t, resources, workItems, durationFunction, horizon);
					double risk = riskFunction.getRisk(workItem, resource, (t*durationTimeFrame)+baseTime);
											
					row[dPos] = (alpha * (t+1)) + ((1-alpha) / getNumberChunks(resource, workItem, durationFunction, durationTimeFrame) * risk);

				}
				
			}
			
		}

//		System.out.println("min: "+getLPformat(row, colno)+";");
//		System.out.println(constrain1.toString());
//		System.out.println(constrain2.toString());
//		System.out.println(constrain3.toString());
//		System.out.println(constrain4.toString());
//		System.out.println(constrain5.toString());
//		System.out.println(constrain6.toString());
//		System.out.println(constrain7.toString());
		

		/* set the objective in lpsolve */
		IntVar cost = solver.makeScalProd(d, converDoubleToLong(row, true)).var();
		OptimizeVar obj = solver.makeMinimize(cost, 1);
		
		DecisionBuilder db = solver.makePhase(d, solver.CHOOSE_LOWEST_MIN, solver.ASSIGN_MIN_VALUE);
		solver.newSearch(db, obj);
		
		StringBuilder sb = null;
		while (solver.nextSolution()) {
			sb = new StringBuilder();
			
			sb.append("cost: " + cost.value() + " " + testSolution(d, row) + "\n");
			
			for(int i = 0; i < numberD; i++) {
				if(d[i].value() > 0) {
					
					sb.append(colno[i] + ": " + d[i].value() + " ");
				}
			}
			
			sb.append("\n");

			System.out.println(sb.toString());
		}
	    solver.endSearch();

	}
	
	public static void main(String[] args) {
		try {
			
			Random r = new Random(100);
			int workItemsNum = 40;
			int offeredWorkItemsNum = 38;
			int startedworkItemsNum = workItemsNum - offeredWorkItemsNum;
			WorkItemRecord[] workItems = generateWorkItems(workItemsNum, r);
			
			WorkItemRecord[] offered = new WorkItemRecord[offeredWorkItemsNum];
			WorkItemRecord[] started = new WorkItemRecord[startedworkItemsNum];
			
			generateOfferedAndStarted(workItems, offered, started, r);
			
			String[] resources = generateResources(10, r);
			
			HashMap<String, WorkItemRecord> use = generateUse(started, resources, r);
			HashMap<String, HashSet<WorkItemRecord>> can = generateCan(workItems, resources, use, r);
			
			HashSet<HashSet<WorkItemRecord>> choice = generateChoice(offered, 28, r);
			
			HashMap<String, WorkItemRecord> result = new HashMap<String, WorkItemRecord>();
			
//			for(HashSet<WorkItemRecord> set : deferedChoices) {
//				for(WorkItemRecord workItem : set) {
//					System.out.print(getStringWorkItem(workItem)+" ");				
//				}
//
//				System.out.println();
//			}
//			
//			for(String key : resourceAuthorization.keySet()) {
//				System.out.print(key+": ");
//				for(WorkItemRecord workItem : resourceAuthorization.get(key)) {
//					System.out.print(getStringWorkItem(workItem)+" ");				
//				}
//
//				System.out.println();
//			}	

			new LPBuilderNew().execute(0.5, resources, started, offered, new DurationFunction(), use, can, new RiskFunction(), choice, result);
			
			System.out.println(result);
			
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
	}
	
	private String getLPformat(double[] row, String[] colno, String operator, int value) {
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < row.length; j++) {
			if(row[j] >= 0) {
				sb.append("+");
			}
			sb.append(row[j]);
			sb.append(colno[j]);
			sb.append(" ");
		}
		
		sb.append(operator);
		sb.append(" ");
		sb.append(value);
		
		return sb.toString();
	}
	
	private String getLPformat(double[] row, String[] colno) {
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < row.length; j++) {
			if(row[j] >= 0) {
				sb.append("+");
			}
			sb.append(row[j]);
			sb.append(colno[j]);
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	private String getLPformat(long[] row, String[] colno, String operator, int value) {
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < row.length; j++) {
			if(row[j] >= 0) {
				sb.append("+");
			}
			sb.append(row[j]);
			sb.append(colno[j]);
			sb.append(" ");
		}
		
		sb.append(operator);
		sb.append(" ");
		sb.append(value);
		
		return sb.toString();
	}

	private int testSolution(IntVar[] values, double[] costs) {
		int res = 0;
		
		for(int i = 0; i < values.length; i++) {
			res += values[i].value()*costs[i];
		}
		
		return res;
	}

	private long[] converDoubleToLong(double[] d, boolean change) {
		long[] res = new long[d.length];
		
		for(int i = 0; i < d.length; i++) {
			if(change) {
				res[i] = Math.round(d[i]*100000);
			}else {
				res[i] = Math.round(d[i]);
			}
		}	
		
		return res;
	}
	
	private long[] converDoubleToLong(Double[] d, boolean change) {
		long[] res = new long[d.length];
		
		for(int i = 0; i < d.length; i++) {
			if(change) {
				res[i] = Math.round(d[i]*100000);
			}else {
				res[i] = Math.round(d[i]);
			}
		}	
		
		return res;
	}
	
	private double[] converDoubleTodouble(Double[] d) {
		double[] res = new double[d.length];
		
		for(int i = 0; i < d.length; i++) {
			res[i] = d[i];
		}	
		
		return res;
	}
	
	private Integer findPositionD(String resource, WorkItemRecord workItem, int timeFrame, String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long horizon) {
		
		int pos = 0;
		
		if(cacheD.isEmpty()) {
			
			for(int r = 0; r < resources.length; r++) {
				
				String currResource = resources[r];
								
				for(int w = 0; w < workItems.length; w++) {
					
					WorkItemRecord currWorkItem = workItems[w];
					
					for(int t = 0; t < horizon; t++) {
						
						cacheD.put(currResource+"_"+getStringWorkItem(currWorkItem)+"_"+t, pos);

						pos++;
						
					}
					
				}
				
			}
			
		}
		return cacheD.get(resource+"_"+getStringWorkItem(workItem)+"_"+timeFrame);
		
	}
	
	private int[] findBiggestChunks(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long sizeTimeFrame) {
		
		int[] chunksSize = new int[workItems.length];
		
		for(String resource : resources) {
			
			for(int i = 0; i < workItems.length; i++) {
				
				WorkItemRecord workItem = workItems[i];
				
				if(chunksSize[i] == 0) chunksSize[i] = 1;
				int newSize = getNumberChunks(resource, workItem, durationFunction, sizeTimeFrame);
				chunksSize[i] = Math.max(chunksSize[i], newSize);
				
			}
			
		}
			
		return chunksSize;
	}

	private int[] findChunks(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long sizeTimeFrame) {
		
		int[] chunks = new int[workItems.length * resources.length];
		
		int i = 0;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem: workItems) {

				int newSize = getNumberChunks(resource, workItem, durationFunction, sizeTimeFrame);
				chunks[i] = newSize;
				i++;
				
			}
			
		}
			
		return chunks;
	}
	
	private int getNumberChunks(String resource, WorkItemRecord workItem, DurationFunction durationFunction, long sizeTimeFrame) {
		int res = Math.round(durationFunction.getDuration(resource, workItem)/sizeTimeFrame);
		if(res == 0) res = 1;
		return res;
	}
	
	private long findGCD(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction) {
		
		long gcd = 0;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem : workItems) {
				
				long duration = durationFunction.getDuration(resource, workItem);
				
				if(gcd == 0 && duration != 0) gcd = duration;
				else if(gcd != 0 && duration != 0) gcd = BigInteger.valueOf(gcd).gcd(BigInteger.valueOf(duration)).longValue();
				
			}
		}
		
		return gcd;
		
	}
	
	private long findLCM(int[] chunks) {
		
		long lcm = chunks[0];
				
		for(int i = 1; i < chunks.length; i++) {
			
			lcm = lcm *(chunks[i] / BigInteger.valueOf(lcm).gcd(BigInteger.valueOf(chunks[i])).longValue());
				
		}
		
		return lcm;
		
	}

	private long findMaxDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction) {
		
		long max = 0;
		int res = resources.length;
		
		int expected = (workItems.length / res);
		if(expected < ((double) workItems.length) / ((double) res)) expected++;
		
		long[] shortest = new long[workItems.length];
		
		for(String resource : resources) {
			
			long duration = 0;
			int pos = 0;
			
			for(WorkItemRecord workItem : workItems) {
				duration += durationFunction.getDuration(resource, workItem);
				shortest[pos] = durationFunction.getDuration(resource, workItem);
				pos++;
			}
			
			Arrays.sort(shortest);
			
			for(int i = 0; i < expected; i++) {
				duration -= shortest[i];
			}
			
			max = Math.max(max, duration);
		}
		
		return max;
		
	}
	
	private long findMinDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long sizeTimeFrame) {
		
		long min = Long.MAX_VALUE;
		String res = null;
		WorkItemRecord wir = null;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem : workItems) {
				
				long duration = durationFunction.getDuration(resource, workItem);
//				System.out.println(duration);
				if(duration < min) {
					min = duration;
					res = resource;
					wir = workItem;
				}
				
			}
			
		}
		
		return getNumberChunks(res, wir, durationFunction, sizeTimeFrame);
		
	}

	private String getStringWorkItem(WorkItemRecord wir) {
		if(wir == null) return null;
		return wir.getRootCaseID() + "_" + wir.getTaskID();
	}
	
	private int getWorkItemPos(WorkItemRecord wir, WorkItemRecord[] wirs) {
		for(int i = 0; i<wirs.length; i++) {
			if(getStringWorkItem(wir).equals(getStringWorkItem(wirs[i]))) return i;
		}
		return -1;
	}
	
	public static WorkItemRecord[] generateWorkItems(int number, Random r) {
		WorkItemRecord[] res = new WorkItemRecord[number];
		
		for(int i = 0; i < number; i++) {
			res[i] = new WorkItemRecord("1", "T"+i, "uri", null, null);
		}
		
		return res;
	}
	
	public static void generateOfferedAndStarted(WorkItemRecord[] workItems, WorkItemRecord[] offered, WorkItemRecord[] started, Random r) {
		int offPos = 0;
		int staPos = 0;
		
		for(int i = 0; i < workItems.length; i++) {
			if(offPos < offered.length && staPos < started.length) {
				if(r.nextDouble() > 0.5) {
					offered[offPos] = workItems[i];
					offPos++;
				}else {
					started[staPos] = workItems[i];
					staPos++;
				}
			}else if(offPos < offered.length) {
				offered[offPos] = workItems[i];
				offPos++;
			}else {
				started[staPos] = workItems[i];
				staPos++;
			}
		}
	}
	
	public static String[] generateResources(int number, Random r) {
		String[] res = new String[number];
		
		for(int i = 0; i < number; i++) {
			res[i] = "R"+i;
		}
		
		return res;
	}
	
	public static HashMap<String, WorkItemRecord> generateUse(WorkItemRecord[] started, String[] resources, Random r) {
		HashMap<String, WorkItemRecord> use = new HashMap<String, WorkItemRecord>();
		
		LinkedList<String> free = new LinkedList<String>();
		for(int i = 0; i < resources.length; i++) {
			free.add(resources[i]);
		}
		
		for(WorkItemRecord workItem : started) {
			
			int a = (int) Math.round(r.nextDouble()*free.size());
			if(a >= free.size()) a--;
			else if(a < 0) a++;
			
			String res = free.remove(a);
			use.put(res, workItem);
		}
		
		return use;
	}
	
	public static HashMap<String, HashSet<WorkItemRecord>> generateCan(WorkItemRecord[] workItems, String[] resources, HashMap<String, WorkItemRecord> use, Random r) {
		HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
		
		for(int i = 0; i < resources.length; i++) {
			
			String res = resources[i];
			
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			
			WorkItemRecord wir = use.get(res);
			if(wir != null) set.add(wir);
			
			for(int j = 0; j < workItems.length; j++) {
				if(r.nextDouble() > 0.5) {
					set.add(workItems[j]);
				}
			}
			
			can.put(res, set);
			
		}
		
		return can;
	}
	
	public static HashSet<HashSet<WorkItemRecord>> generateChoice(WorkItemRecord[] workItems, int choices, Random r) {
		HashSet<HashSet<WorkItemRecord>> choice = new HashSet<HashSet<WorkItemRecord>>();
		
		LinkedList<WorkItemRecord> free = new LinkedList<WorkItemRecord>();
		for(int i = 0; i < workItems.length; i++) {
			free.add(workItems[i]);
		}
		
		for(int i = 0; i < choices; i++) {
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			
			int a = (int) Math.round(r.nextDouble()*free.size());
			if(a >= free.size()) a--;
			else if(a < 0) a++;
			
			WorkItemRecord wir = free.remove(a);
			set.add(wir);
						
			choice.add(set);
		}
		
		Iterator<WorkItemRecord> it = free.iterator();
		
		HashSet<WorkItemRecord>[] array = choice.toArray(new HashSet[0]);
		
		while(it.hasNext()) {
			
			int a = (int) Math.round(r.nextDouble()*array.length);
			if(a >= array.length) a--;
			else if(a < 0) a++;
			
			int pos = a;
			array[pos].add(it.next());
		}
		
		return choice;
	}

}
