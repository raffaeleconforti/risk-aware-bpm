package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import lpsolve.*;

public class LPBuilderSCIP {
	
	private final HashMap<String, Integer> cacheD = new HashMap<String, Integer>();
	
	static {
		System.loadLibrary("jnilinearsolver");
	}
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		try {
			
			LPBuilderSCIP lp = new LPBuilderSCIP();
			
			Random r = new Random(100);
			int workItemsNum = 20;
			int offeredWorkItemsNum = 18;
			int startedworkItemsNum = workItemsNum - offeredWorkItemsNum;
			WorkItemRecord[] workItems = generateWorkItems(workItemsNum, r);
			
			WorkItemRecord[] offered = new WorkItemRecord[offeredWorkItemsNum];
			WorkItemRecord[] started = new WorkItemRecord[startedworkItemsNum];
			
			lp.generateOfferedAndStarted(workItems, offered, started, r);
			
			System.out.println("Offered");
			for(int i = 0; i < offered.length; i++) {
				System.out.println(lp.getStringWorkItem(offered[i]));
			}
			System.out.println("Started");
			for(int i = 0; i < started.length; i++) {
				System.out.println(lp.getStringWorkItem(started[i]));
			}
			
			String[] resources = lp.generateResources(10, r);
			
			HashMap<String, WorkItemRecord> use = lp.generateUse(started, resources, r);
			HashMap<String, HashSet<WorkItemRecord>> can = lp.generateCan(workItems, resources, use, r);
			
			HashSet<HashSet<WorkItemRecord>> choice = lp.generateChoice(offered, started, 10, r);
			
			HashMap<String, WorkItemRecord> result = new HashMap<String, WorkItemRecord>();
			
			for(HashSet<WorkItemRecord> set : choice) {
				System.out.println("Choice");
				for(WorkItemRecord workItem : set) {
					System.out.print(getStringWorkItem(workItem)+" ");				
				}

				System.out.println();
			}
			
			System.out.print("Feasible: ");
			HashSet<String> test = new HashSet<String>();
			for(HashSet<WorkItemRecord> set : choice) {
				for(WorkItemRecord workItem : set) {
					test.add(getStringWorkItem(workItem)+" ");				
				}
			}
			if(test.size() == workItems.length) System.out.println("true");
			else System.out.println("false");
			
			System.out.println("Use");
			for(String key : use.keySet()) {
				System.out.print(key+": ");
				WorkItemRecord workItem = use.get(key);
				System.out.print(getStringWorkItem(workItem)+" ");				
				
				System.out.println();
			}	
			
			System.out.println("Can");
			for(String key : can.keySet()) {
				System.out.print(key+": ");
				for(WorkItemRecord workItem : can.get(key)) {
					System.out.print(getStringWorkItem(workItem)+" ");				
				}
				
				System.out.println();
			}	

			new LPBuilderSCIP().execute(0.5, resources, started, offered, new DurationFunction(), use, can, new RiskFunction(), choice, result, 1000000);
			
			System.out.println(result);
			
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
	}

	public void execute(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItemsOffered, DurationFunction durationFunction, HashMap<String, WorkItemRecord> resourceUtilization, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, RiskFunction riskFunction, HashSet<HashSet<WorkItemRecord>> deferedChoices, HashMap<String, WorkItemRecord> result, long approximator) throws LpSolveException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

		
		WorkItemRecord[] workItems = Arrays.copyOf(workItemsOffered, workItemsOffered.length + workItemsExecuting.length);
		System.arraycopy(workItemsExecuting, 0, workItems, workItemsOffered.length, workItemsExecuting.length);
		
		long baseTime = System.currentTimeMillis();
		long maxDuration = findMaxDuration(resources, workItems, durationFunction, approximator);
		System.out.println("maxDuration "+maxDuration);
		long durationTimeFrame = findGCD(resources, workItems, durationFunction, approximator);
		
		long minDuration = findMinDuration(resources, workItems, durationFunction, durationTimeFrame, approximator);
		
		int missingPart = (int) minDuration;
		durationTimeFrame *= missingPart;
		
		System.out.println("durationTimeFrame "+durationTimeFrame);
		int horizon = (int) (maxDuration/durationTimeFrame);
		System.out.println("horizon "+horizon);
		
		int[] chunksSize = findChunks(resources, workItems, durationFunction, durationTimeFrame, approximator); 
		long lcm = findLCM(chunksSize);
		
		int numberD = discoverD(resources, workItems, horizon, resourceAuthorization); //resources.length * workItems.length * horizon;
//		System.out.println("numberD "+numberD);
		
		MPSolver solver = new MPSolver("Scheduling", MPSolver.getSolverEnum("SCIP_MIXED_INTEGER_PROGRAMMING"));
	    double infinity = solver.infinity();
		int Ncol = 0;

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */
		Ncol = numberD;

		/* create space large enough for one row */
		String[] colno = new String[Ncol];
		double[] row = new double[Ncol];

		/* let us name our variables. Not required, but can be useful for debugging */

		WorkItemRecord currWorkItem = null;
		String currResource = null;
		
		MPVariable[] d = solver.makeBoolVarArray(numberD);//keIntVarArray(numberD, 0, 1, "x");
		
		System.out.println("set name variable");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
//			for(int w = 0; w < workItems.length; w++) {
				
			for(WorkItemRecord work : resourceAuthorization.get(currResource)) {
				
//				currWorkItem = workItems[w];
				currWorkItem = work;
					
				for(int t = 0; t < horizon; t++) {
			
					int i = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
					colno[i] = currResource+"_"+getStringWorkItem(currWorkItem)+"_Time_"+t;
					
				}
						
			}
			
		}
		
		ArrayList<MPVariable> array = null;
		ArrayList<Double> arrayD = null;
		ArrayList<String> arrayVar = null;
		
//		StringBuilder constrain1 = new StringBuilder();
//		/* construct first constrain */
//		System.out.println("first constrain");		
//		for(int r = 0; r < resources.length; r++) {
//			currResource = resources[r];
//			
//			for(int w = 0; w < workItems.length; w++) {
//				
//				currWorkItem = workItems[w];
//				
//				int chunks = getNumberChunks(currResource, currWorkItem, durationFunction, durationTimeFrame, approximator);
//					
//				for(int t = 0; t < horizon; t++) {
//					
//					array = new ArrayList<MPVariable>();
//					arrayD = new ArrayList<Double>();
//					arrayVar = new ArrayList<String>();
//
//					Integer dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
////					row[dPos] = -chunks;
//					
//					if(dPos == null) continue;
//						
//					for(int c = -chunks+1; c < chunks; c++) {
////						System.out.println(chunks+" "+c);
//						Integer xPos = findPositionD(currResource, currWorkItem, t+c, resources, workItems, durationFunction, horizon, resourceAuthorization);
//
//						if(xPos != null) {
//							array.add(d[xPos]);
//
//							arrayVar.add(colno[xPos]);
//							
//							if(xPos == dPos) {
//								arrayD.add(1.0 - chunks);
//							}else {
//								arrayD.add(1.0);
//							}
//						}
//						
//					}
//
//					/* add the row to lpsolve */
//					if(array.size() > 1) {
//						constrain1.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", 0)+";\n");
//					    MPConstraint ct = solver.makeConstraint(0, infinity);
//						for(int i = 0; i < array.size() ; i++) {
//							ct.setCoefficient(array.get(i), arrayD.get(i));
//						}
//					}
//				}
//				
//			}
//			
//		}
		
		StringBuilder constrain1 = new StringBuilder();
		/* construct first constrain */
		System.out.println("first constrain");		
			
		for(int w = 0; w < workItems.length; w++) {
			
			currWorkItem = workItems[w];

			for(int t = 0; t < horizon; t++) {
				
				array = new ArrayList<MPVariable>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				int chunks = getNumberChunks(currResource, currWorkItem, durationFunction, durationTimeFrame, approximator);
				
				for(int r = 0; r < resources.length; r++) {
					currResource = resources[r];

					Integer dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
//					row[dPos] = -chunks;
					
					if(dPos == null) continue;
						
					for(int c = -chunks+1; c < chunks; c++) {
//						System.out.println(chunks+" "+c);
						Integer xPos = findPositionD(currResource, currWorkItem, t+c, resources, workItems, durationFunction, horizon, resourceAuthorization);

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
					
				}
				
				/* add the row to lpsolve */
				if(array.size() > 1) {
					constrain1.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", 0)+";\n");
				    MPConstraint ct = solver.makeConstraint(0, infinity);
					for(int i = 0; i < array.size() ; i++) {
						ct.setCoefficient(array.get(i), arrayD.get(i));
					}
				}
				
			}
			
		}
		
//		System.out.println(constrain1.toString());

		StringBuilder constrain2 = new StringBuilder();
		/* construct second constrain */
		System.out.println("second constrain");		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int t = 0; t < horizon; t++) {

				array = new ArrayList<MPVariable>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				for(int w = 0; w < workItems.length; w++) {
				
					currWorkItem = workItems[w];
					
					Integer dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
					if(dPos == null) continue;
					
					array.add(d[dPos]);
					arrayD.add(1.0);
					arrayVar.add(colno[dPos]);
					
				}
				
				/* add the row to lpsolve */
				constrain2.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "<=", 1)+";\n");
				MPConstraint ct = solver.makeConstraint(-infinity, 1);
				for(int i = 0; i < array.size() ; i++) {
					ct.setCoefficient(array.get(i), arrayD.get(i));
				}
			}
			
		}		
		
//		System.out.println(constrain2.toString());

		StringBuilder constrain3 = new StringBuilder();
		/* costruct third constrain */
		System.out.println("third constrain");
		for(HashSet<WorkItemRecord> set : deferedChoices) {

			array = new ArrayList<MPVariable>();
			arrayVar = new ArrayList<String>();
			arrayD = new ArrayList<Double>();
						
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(WorkItemRecord workItem : set) {
					
					int chunk = getNumberChunks(currResource, workItem, durationFunction, durationTimeFrame, approximator);
			
					for(int t = 0; t < horizon; t++) {
											
						Integer dPos = findPositionD(currResource, workItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
						if(dPos == null) continue;
						
						array.add(d[dPos]);
						arrayVar.add(colno[dPos]);
						arrayD.add(((double) lcm)/ ((double) chunk));
														
					}
					
				}
				
			}

			/* add the row to lpsolve */
			constrain3.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "=", (int) lcm)+";\n");
			MPConstraint ct = solver.makeConstraint(lcm, lcm);
			for(int i = 0; i < array.size() ; i++) {
				ct.setCoefficient(array.get(i), arrayD.get(i));
			}
				
		}

//		System.out.println(constrain3.toString());

//		StringBuilder constrain4 = new StringBuilder();
//		/* construct fourth constrain */
//		System.out.println("fourth constrain");
//		for(int r = 0; r < resources.length; r++) {
//			
//			currResource = resources[r];
//		
//			for(int w = 0; w < workItems.length; w++) {
//
//				currWorkItem = workItems[w];
//
//				HashSet<WorkItemRecord> set = resourceAuthorization.get(currResource);
//				int can = 0;
//				if(set != null) {
//					can = set.contains(currWorkItem)?1:0;
//				}
//				
//				for(int t = 0; t < horizon; t++) {
//					
//					array = new ArrayList<MPVariable>();
//					arrayD = new ArrayList<Double>();
//					arrayVar = new ArrayList<String>();
//					
//					Integer dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
//					if(dPos == null) continue;
//					
//					array.add(d[dPos]);
//					arrayVar.add(colno[dPos]);
//					arrayD.add(1.0);
//
//					/* add the row to lpsolve */
//					constrain4.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "<=", can)+";\n");
//					if(can == 0) {
//						MPConstraint ct = solver.makeConstraint(0, 0);
//						for(int i = 0; i < array.size() ; i++) {
//							ct.setCoefficient(array.get(i), arrayD.get(i));
//						}		
//					}
//				}
//				
//			}
//			
//		}

//		System.out.println(constrain4.toString());
		
		StringBuilder constrain5 = new StringBuilder();
		/* construct fifth constrain */
		System.out.println("fifth constrain");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItemsExecuting.length; w++) {

				array = new ArrayList<MPVariable>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				currWorkItem = workItemsExecuting[w];
					
				Integer dPos = findPositionD(currResource, currWorkItem, 0, resources, workItems, durationFunction, horizon, resourceAuthorization);
				if(dPos == null) continue;
				
				array.add(d[dPos]);
				arrayD.add(1.0);
				arrayVar.add(colno[dPos]);
				
				int use = currWorkItem.equals(resourceUtilization.get(currResource))?1:0;
					
				/* add the row to lpsolve */
				constrain5.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), "=", use)+";\n");
				MPConstraint ct = solver.makeConstraint(use, use);
				for(int i = 0; i < array.size() ; i++) {
					ct.setCoefficient(array.get(i), arrayD.get(i));
				}
										
			}
			
		}

//		System.out.println(constrain5.toString());
		
		StringBuilder constrain6 = new StringBuilder();
		/* construct sixth constrain */
		System.out.println("sixth constrain");
		array = new ArrayList<MPVariable>();
		arrayD = new ArrayList<Double>();
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
				
				Integer dPos = findPositionD(currResource, currWorkItem, 0, resources, workItems, durationFunction, horizon, resourceAuthorization);
				if(dPos == null) continue;
				
				array.add(d[dPos]);
				arrayD.add(1.0);
				arrayVar.add(colno[dPos]);
				
			}
			
		}
		
		/* add the row to lpsolve */
		constrain6.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", Math.min(workItems.length, resources.length))+";\n");
		MPConstraint ct = solver.makeConstraint(Math.min(workItems.length, resources.length), infinity);
		for(int i = 0; i < array.size() ; i++) {
			ct.setCoefficient(array.get(i), arrayD.get(i));
		}
		
//		System.out.println(constrain6.toString());

		StringBuilder constrain7 = new StringBuilder();
		/* construct seventh constrain */
		System.out.println("seventh constrain");
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];

			for(int t = 0; t < horizon; t++) {

				array = new ArrayList<MPVariable>();
				arrayD = new ArrayList<Double>();
				arrayVar = new ArrayList<String>();
				
				for(int w = 0; w < workItems.length; w++) {
					
					currWorkItem = workItems[w];
					
					Integer tPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
					if(tPos == null) continue;
					
					array.add(d[tPos]);
					arrayD.add(-1.0 * t);
					arrayVar.add(colno[tPos]);
	
					for(int t1 = 0; t1 < t; t1++) {
						
						Integer dPos = findPositionD(currResource, currWorkItem, t1, resources, workItems, durationFunction, horizon, resourceAuthorization);
						if(dPos == null) continue;
						
						array.add(d[dPos]);
						arrayD.add(1.0);
						arrayVar.add(colno[dPos]);
						
					}
					
				}
			
				/* add the row to lpsolve */
				constrain7.append(getLPformat(converDoubleToLong(arrayD.toArray(new Double[0]), false), arrayVar.toArray(new String[0]), ">=", 0)+";\n");
				MPConstraint ct2 = solver.makeConstraint(0, infinity);
				for(int i = 0; i < array.size() ; i++) {
					ct2.setCoefficient(array.get(i), arrayD.get(i));
				}
				
			}
			
		}
		
//		System.out.println(constrain7.toString());

		System.out.println("Constraints: "+solver.numConstraints());
		System.out.println("Variables: "+solver.numVariables());
			
		System.out.println("Objective function");
		row = new double[Ncol];
		
		for(int r = 0; r < resources.length; r++) {
			
			String resource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				WorkItemRecord workItem = workItems[w];
					
				for(int t = 0; t < horizon; t++) {
					
					Integer dPos = findPositionD(resource, workItem, t, resources, workItems, durationFunction, horizon, resourceAuthorization);
					if(dPos == null) continue;
					
					double risk = riskFunction.getRisk(resource, workItem, (t*durationTimeFrame)+baseTime);
											
					row[dPos] = (alpha * (t+1)) + ((1-alpha) / getNumberChunks(resource, workItem, durationFunction, durationTimeFrame, approximator) * risk);

				}
				
			}
			
		}
		
		for(int i = 0; i < d.length ; i++) {
			solver.setObjectiveCoefficient(d[i], row[i]);
		}

//		System.out.println("min: "+getLPformat(row, colno)+";");
//		System.out.println(constrain1.toString());
//		System.out.println(constrain2.toString());
//		System.out.println(constrain3.toString());
////		System.out.println(constrain4.toString());
//		System.out.println(constrain5.toString());
//		System.out.println(constrain6.toString());
//		System.out.println(constrain7.toString());
		

		/* set the objective in lpsolve */
		int resultStatus = solver.solve();
		
		StringBuilder sb = new StringBuilder();
	    System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
		sb.append("cost: " + resultStatus + "\n");
		for(int i = 0; i < d.length ; i++) {
			
			if(d[i].solutionValue() > 0) {
				
				sb.append(colno[i] + ": " + d[i].solutionValue() + " ");
				sb.append("\n");
				
			}			

		}
		System.out.println(sb.toString());

	}	
	
	private int discoverD(String[] resources, WorkItemRecord[] workItems, int horizon, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization) {
		int res = 0;
		
		for(String resource : resources) {
			res += resourceAuthorization.get(resource).size() * horizon;
		}
		System.out.println(res);
		return res;
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
	
	private Integer findPositionD(String resource, WorkItemRecord workItem, int timeFrame, String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long horizon, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization) {
		
		int pos = 0;

		if(cacheD.isEmpty()) {
			
			for(int r = 0; r < resources.length; r++) {
				
				String currResource = resources[r];
								
//				for(int w = 0; w < workItems.length; w++) {

				for(WorkItemRecord work : resourceAuthorization.get(currResource)) {
					
//					WorkItemRecord currWorkItem = workItems[w];
					WorkItemRecord currWorkItem = work;
					
					for(int t = 0; t < horizon; t++) {
						
						cacheD.put(currResource+"_"+getStringWorkItem(currWorkItem)+"_"+t, pos);

						pos++;
						
					}
					
				}
				
			}
			
		}
		return cacheD.get(resource+"_"+getStringWorkItem(workItem)+"_"+timeFrame);
	}

	private int[] findChunks(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long sizeTimeFrame, long approximator) {
		
		int[] chunks = new int[workItems.length * resources.length];
		
		int i = 0;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem: workItems) {

				int newSize = getNumberChunks(resource, workItem, durationFunction, sizeTimeFrame, approximator);
				chunks[i] = newSize;
				i++;
				
			}
			
		}
			
		return chunks;
	}
	
	private int getNumberChunks(String resource, WorkItemRecord workItem, DurationFunction durationFunction, long sizeTimeFrame, long approximator) {
		int res = Math.round(durationFunction.getDuration(resource, workItem, approximator)/sizeTimeFrame);
		if(res == 0) res = 1;
		return res;
	}
	
	private long findGCD(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long approximator) {
		
		long gcd = 0;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem : workItems) {
				
				long duration = durationFunction.getDuration(resource, workItem, approximator);
				
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

	private long findMaxDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long approximator) {
		
		long max = 0;
		int res = resources.length;
		
		int expected = (workItems.length / res);
		if(expected < ((double) workItems.length) / ((double) res)) expected++;
		
		long[] shortest = new long[workItems.length];
		
		for(String resource : resources) {
			
			long duration = 0;
			int pos = 0;
			
			for(WorkItemRecord workItem : workItems) {
				duration += durationFunction.getDuration(resource, workItem, approximator);
				shortest[pos] = durationFunction.getDuration(resource, workItem, approximator);
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
	
	private long findMinDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long sizeTimeFrame, long approximator) {
		
		long min = Long.MAX_VALUE;
		String res = null;
		WorkItemRecord wir = null;
		
		for(String resource : resources) {
			
			for(WorkItemRecord workItem : workItems) {
				
				long duration = durationFunction.getDuration(resource, workItem, approximator);
//				System.out.println(duration);
				if(duration < min) {
					min = duration;
					res = resource;
					wir = workItem;
				}
				
			}
			
		}
		
		return getNumberChunks(res, wir, durationFunction, sizeTimeFrame, approximator);
		
	}

	private static String getStringWorkItem(WorkItemRecord wir) {
		if(wir == null) return null;
		return wir.getRootCaseID() + "_" + wir.getTaskID();
	}
	
	public static WorkItemRecord[] generateWorkItems(int number, Random r) {
		WorkItemRecord[] res = new WorkItemRecord[number];
		
		for(int i = 0; i < number; i++) {
			res[i] = new WorkItemRecord("1", "T"+i, "uri", null, null);
		}
		
		return res;
	}
	
	public void generateOfferedAndStarted(WorkItemRecord[] workItems, WorkItemRecord[] offered, WorkItemRecord[] started, Random r) {
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
	
	public String[] generateResources(int number, Random r) {
		String[] res = new String[number];
		
		for(int i = 0; i < number; i++) {
			res[i] = "R"+i;
		}
		
		return res;
	}
	
	public HashMap<String, WorkItemRecord> generateUse(WorkItemRecord[] started, String[] resources, Random r) {
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
	
	public HashMap<String, HashSet<WorkItemRecord>> generateCan(WorkItemRecord[] workItems, String[] resources, HashMap<String, WorkItemRecord> use, Random r) {
		HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
		
		for(int i = 0; i < resources.length; i++) {
			
			String res = resources[i];
			
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			
			WorkItemRecord wir = use.get(res);
			if(wir != null) set.add(wir);
			
			for(int j = 0; j < workItems.length; j++) {
				if(r.nextDouble() > 0.4) {
					set.add(workItems[j]);
				}
			}
			
			can.put(res, set);
			
		}
		
		return can;
	}
	
	public HashSet<HashSet<WorkItemRecord>> generateChoice(WorkItemRecord[] workItems, WorkItemRecord[] started, int choices, Random r) {
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
		
		for(WorkItemRecord wir : started) {
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			set.add(wir);
						
			choice.add(set);
		}
		
		return choice;
	}

}
