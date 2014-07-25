package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import Test.GurobiSolver;
import Test.HPCmessage;
import Test.HPCmessage.Constrain;


public class ILPBuilderSCIPNewMax {
	
	private static StringBuffer nameBuffer = new StringBuffer();
	
	private static String v = "V_";
	private static String m1 = "M1_";
	private static String m2 = "M2_";
	private static String x_ = "X_";
	
	private static String under = "_";
	
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private HashMap<WorkItemRecord, String> cacheWorkItems = new HashMap<WorkItemRecord, String>();
	private HashMap<String, Integer> cacheRW = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheRWW = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheRW2S = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheX = new HashMap<String, Integer>();
	
//	public static void main(String[] args) {
//	
//		int resultInt = -1;
//		int w = 2;
//		while(w == 2) {
//			ILPBuilderSCIPNewMax lp = new ILPBuilderSCIPNewMax();
//			
//			Random r = new Random(1234567890);
//			int workItemsNum = w;
//			int offeredWorkItemsNum = 2;
//			int startedworkItemsNum = workItemsNum - offeredWorkItemsNum;
//			WorkItemRecord[] workItems = generateWorkItems(workItemsNum, r);
//			
//			WorkItemRecord[] offered = new WorkItemRecord[offeredWorkItemsNum];
//			WorkItemRecord[] started = new WorkItemRecord[startedworkItemsNum];
//			
//			lp.generateOfferedAndStarted(workItems, offered, started, r);
//			
//			System.out.println("Offered");
//			for(int i = 0; i < offered.length; i++) {
//				System.out.println(lp.getStringWorkItem(offered[i]));
//			}
//			System.out.println("Started");
//			for(int i = 0; i < started.length; i++) {
//				System.out.println(lp.getStringWorkItem(started[i]));
//			}
//			
//			String[] resources = lp.generateResources(2, r);
//			
//			HashMap<String, HashSet<WorkItemRecord>> use = lp.generateUse(started, resources, r);
//			HashMap<String, HashSet<WorkItemRecord>> can = lp.generateCan(workItems, resources, use, r);
//			
//			HashSet<HashSet<WorkItemRecord>> choice = lp.generateChoice(offered, started, 2, r);
//			
//			HashMap<String, HashMap<WorkItemRecord, Long>> result = new HashMap<String, HashMap<WorkItemRecord, Long>>();
//			
//			for(HashSet<WorkItemRecord> set : choice) {
//				System.out.println("Choice");
//				for(WorkItemRecord workItem : set) {
//					System.out.print(getStringWorkItem(workItem)+" ");				
//				}
//	
//				System.out.println();
//			}
//			
//			System.out.print("Feasible: ");
//			HashSet<String> test = new HashSet<String>();
//			for(HashSet<WorkItemRecord> set : choice) {
//				for(WorkItemRecord workItem : set) {
//					test.add(getStringWorkItem(workItem)+" ");				
//				}
//			}
//			if(test.size() == workItems.length) System.out.println("true");
//			else System.out.println("false");
//			
//			System.out.println("Use");
//			for(String key : use.keySet()) {
//				System.out.print(key+": ");
//				for(WorkItemRecord workItem : use.get(key)) {
//					System.out.print(getStringWorkItem(workItem)+" ");				
//				}
//				
//				System.out.println();
//			}
//			
//			System.out.println("Can");
//			for(String key : can.keySet()) {
//				System.out.print(key+": ");
//				for(WorkItemRecord workItem : can.get(key)) {
//					System.out.print(getStringWorkItem(workItem)+" ");				
//				}
//				
//				System.out.println();
//			}	
//	
////			resultInt = new ILPBuilderSCIPNewMax().execute(1, resources, started, offered, new DurationFunction(), use, can, new RiskFunction(), choice, result, new DurationSplitter(w), new HashMap<WorkItemRecord, Long>(), 1000);
////			resultInt = new ILPBuilderSCIPNewMax().execute2(1, resources, started, offered, new DurationFunction(), use, can, new RiskFunction(), choice, result, new DurationSplitter(w), new HashMap<WorkItemRecord, Long>(), 1000);
//			w++;
//		}
//		System.out.println("Error with w = "+(w-1));
//		
//	}
	
	public long findMaxDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction) {
		
//		long max = 0;
//		int res = resources.length;
//		
//		int expected = (workItems.length / res);
//		if(expected < ((double) workItems.length) / ((double) res)) expected++;
//		
//		long[] shortest = new long[workItems.length];
//		
//		for(String resource : resources) {
//			
//			long duration = 0;
//			int pos = 0;
//			
//			for(WorkItemRecord workItem : workItems) {
//				duration += durationFunction.getDuration(resource, workItem);
//				shortest[pos] = durationFunction.getDuration(resource, workItem);
//				pos++;
//			}
//			
//			Arrays.sort(shortest);
//			
//			for(int i = 0; i < expected; i++) {
//				duration -= shortest[i];
//			}
//			
//			max = Math.max(max, duration);
//		}
//		
//		return max;
		
		long duration = 0L;
		
		for(WorkItemRecord workItem : workItems) {
			duration += durationFunction.getDuration(workItem);
		}

		return duration;
	}
	
	public LinkedList<HashSet<String>> identifyIndipendentResources(String[] resources, WorkItemRecord[] workItems, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization) {
		HashMap<WorkItemRecord, HashSet<String>> result = new HashMap<WorkItemRecord, HashSet<String>>();
		LinkedList<HashSet<String>> finalResult = new LinkedList<HashSet<String>>();
		
		HashSet<WorkItemRecord> set = null;
		HashSet<String> list = null;
		HashSet<String> list2 = null;
		
		for(String resource : resources) {
			set = resourceAuthorization.get(resource);
			for(WorkItemRecord workItem : set) {
				if((list = result.get(workItem)) == null) {
					list = new HashSet<String>();
					result.put(workItem, list);
				}
				list.add(resource);
			}
		}
		
		for(Entry<WorkItemRecord, HashSet<String>> entry : result.entrySet()) {
			finalResult.add(entry.getValue());
		}
		
		boolean modified = false;
		do {
			modified = false;
			for(int i = 0; i < finalResult.size(); i++) {
				list = finalResult.get(i);
				for(String res : list) {
					for(int j = i+1; j < finalResult.size(); j++) {
						list2 = finalResult.get(j);
						if(list2.contains(res)) {
							modified = true;
							break;
						}
					}
					if(modified) break;
				}
				if(modified) break;
			}
			if(modified) {
				list.addAll(list2);
				finalResult.remove(list2);
			}
		}while(modified);
		
		return finalResult;
	}
	
	public int execute2(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItemsOffered, WorkItemRecord[] workItems, 
			DurationFunction durationFunction, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, RiskFunction riskFunction, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, HashMap<String, HashMap<WorkItemRecord, Long>> result, 
			DurationSplitter ds, HashMap<WorkItemRecord, Long> preAllocated, long approximation, Socket hpc) {
		
		HPCmessage message = new HPCmessage();
		
		StringBuffer buffer = new StringBuffer();
		
		cacheWorkItems.clear();
		cacheRW.clear();
		cacheRWW.clear();
		cacheRW2S.clear();
		cacheX.clear();
		
		HashMap<Integer, CoupleWorkItemResource> map = new HashMap<Integer, ILPBuilderSCIPNewMax.CoupleWorkItemResource>();
//		WorkItemRecord[] workItems = Arrays.copyOf(workItemsOffered, workItemsOffered.length + workItemsExecuting.length);
//		System.arraycopy(workItemsExecuting, 0, workItems, workItemsOffered.length, workItemsExecuting.length);
		
		long max = (findMaxDuration(resources, workItems, durationFunction) / approximation);
		if(max == 0) max+=1001;
		System.out.println(max);
		
		int rw = discoverRW(resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
		int rww = discoverRWW(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, deferedChoices, rw);
		int rw2s = discoverRW2S(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, ds, rw, rww);
		int x = discoverX(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, ds, rw, rww, rw2s);
		
		int numberD = rw+rww+rw2s+x;
		
//		MPSolver solver = new MPSolver("Scheduling", MPSolver.SCIP_MIXED_INTEGER_PROGRAMMING);//MPSolver.getSolverEnum("SCIP_MIXED_INTEGER_PROGRAMMING"));

	    double infinity = Double.MAX_VALUE; //MPSolver.infinity();
	    double M = 2*max;
		int Ncol = 0;

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */
		Ncol = numberD;

		/* create space large enough for one row */
		String[] colno = new String[Ncol];
		double[] row = new double[Ncol];

		/* let us name our variables. Not required, but can be useful for debugging */

		WorkItemRecord currWorkItem = null;
		WorkItemRecord currWorkItem2 = null;
		String currResource = null;
		
//		MPVariable[] arrayRW = solver.makeNumVarArray(rw, 0, max);
//		MPVariable[] arrayRWW = solver.makeBoolVarArray(rww);
//		MPVariable[] arrayRW2S = solver.makeBoolVarArray(rw2s);//solver.makeNumVarArray(rw2s, 0, 1);
//		MPVariable[] arrayX = solver.makeBoolVarArray(x);
		
		message.setRW(rw);
		message.setRWW(rww);
		message.setRW2S(rw2s);
		message.setX(x);
		message.setMax(max);
		
//		MPVariable[] d = ArrayUtils.concatAll(arrayRW, arrayRWW, arrayRW2S, arrayX);
		
		System.out.println("set name variable");
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
				
				buffer.delete(0, buffer.length());
//				buffer.append(v);
				buffer.append(currResource);
				buffer.append(under);
				buffer.append(getStringWorkItem(currWorkItem));
				
				Integer varRW = findRW(buffer.toString());
//				Integer varRW = findRW(currResource, currWorkItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
				
				if(varRW != null) {
				
//					buffer.delete(0, buffer.length());
//					buffer.append(v);
//					buffer.append(currResource);
//					buffer.append(under);
//					buffer.append(getStringWorkItem(currWorkItem));
					
					buffer.insert(0, v);
					colno[varRW] = buffer.toString();
//					colno[varRW] = "V_"+currResource+"_"+getStringWorkItem(currWorkItem);
					
					map.put(varRW, new CoupleWorkItemResource(currWorkItem, currResource));
				
				}
				
				for(int w1 = 0; w1 < workItems.length; w1++) {
					
					currWorkItem2 = workItems[w1];
					
					buffer.delete(0, buffer.length());
//					buffer.append(m1);
					buffer.append(currResource);
					buffer.append(under);
					buffer.append(getStringWorkItem(currWorkItem));
					buffer.append(under);
					buffer.append(getStringWorkItem(currWorkItem2));
					
					Integer varRWW = findRWW(buffer.toString());
//					Integer varRWW = findRWW(currResource, currWorkItem, currWorkItem2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					
					if(varRWW != null) {
						
//						buffer.delete(0, buffer.length());
//						buffer.append(m1);
//						buffer.append(currResource);
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem));
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem2));
						
						buffer.insert(0, m1);
						colno[varRWW] = buffer.toString();
//						colno[varRWW] = "M1_"+currResource+"_"+getStringWorkItem(currWorkItem)+"_"+getStringWorkItem(currWorkItem2);

					}
					
				}
				
				for(long[] split : ds.getSplitts(currWorkItem, currResource)) {
					
					buffer.delete(0, buffer.length());
//					buffer.append(m2);
					buffer.append(currResource);
					buffer.append(under);
					buffer.append(getStringWorkItem(currWorkItem));
					buffer.append(under);
					buffer.append(split[0]);
					buffer.append(under);
					buffer.append(split[1]);
				
					Integer rw2sA = findRW2S(buffer.toString());
//					Integer rw2sA = findRW2S(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					
					if(rw2sA != null) {
						
//						buffer.delete(0, buffer.length());
//						buffer.append(m2);
//						buffer.append(currResource);
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem));
//						buffer.append(under);
//						buffer.append(split[0]);
//						buffer.append(under);
//						buffer.append(split[1]);

						buffer.insert(0, m2);
						colno[rw2sA] = buffer.toString();
//						colno[rw2sA] = "M2_"+currResource+"_"+getStringWorkItem(currWorkItem)+"_"+split[0]+"_"+split[1];
					
					}

					buffer.delete(0, buffer.length());
//					buffer.append(x_);
					buffer.append(currResource);
					buffer.append(under);
					buffer.append(getStringWorkItem(currWorkItem));
					buffer.append(under);
					buffer.append(split[0]);
					buffer.append(under);
					buffer.append(split[1]);
					
					Integer varX = findX(buffer.toString());
//					Integer varX = findX(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					
					if(varX != null) {
						
//						buffer.delete(0, buffer.length());
//						buffer.append(x_);
//						buffer.append(currResource);
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem));
//						buffer.append(under);
//						buffer.append(split[0]);
//						buffer.append(under);
//						buffer.append(split[1]);

						buffer.insert(0, x_);
						colno[varX] = buffer.toString();
//						colno[varX] = "X_"+currResource+"_"+getStringWorkItem(currWorkItem)+"_"+split[0]+"_"+split[1];
						
					}
				}
				
			}
			
		}
		
		//TODO SEND colno
		message.setColno(colno);
		
		System.out.println();
		
//		ArrayList<MPVariable> array = null;
//		ArrayList<Double> arrayD = null;
//		ArrayList<String> arrayVar = null;
//
//		ArrayList<MPVariable> array1 = null;
//		ArrayList<Double> arrayD1 = null;
//		ArrayList<String> arrayVar1 = null;
//		
//		ArrayList<MPVariable> array2 = null;
//		ArrayList<Double> arrayD2 = null;
//		ArrayList<String> arrayVar2 = null;
//
//		ArrayList<MPVariable> array3 = null;
//		ArrayList<Double> arrayD3 = null;
//		ArrayList<String> arrayVar3 = null;
		
		LinkedList<Constrain> con1 = new LinkedList<Constrain>();
		
		StringBuilder constrain1 = new StringBuilder();
		/* construct first constrain */
		System.out.println("first constrain");		
		for(HashSet<WorkItemRecord> set : deferedChoices) {
			
//			array = new ArrayList<MPVariable>();
//			arrayVar = new ArrayList<String>();
//			arrayD = new ArrayList<Double>();
			
			LinkedList<Integer> con = new LinkedList<Integer>();
			LinkedList<Double> coe = new LinkedList<Double>();
						
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(WorkItemRecord workItem : set) {
					for(long[] split : ds.getSplitts(workItem, currResource)) {
							
						Integer xPos = findX(currResource, workItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
						if(xPos == null) continue;

//						array.add(d[xPos]);
//						arrayVar.add(colno[xPos]);
//						arrayD.add(1.0);
						
						con.add(xPos);
						coe.add(1.0);
														
					}
					
				}
				
			}
			
			//TODO SEND array AND arrayD
			if(con.size() > 0) {
				
//				constrain1.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "=", 1)+";\n");
				
				Constrain c = message.new Constrain();
				c.setConstrain(con);
				c.setCoefficient(coe);
				
				c.setMinBoundaries(1.0);
				c.setMaxBoundaries(1.0);
				
				con1.add(c);
			}
				
		}

//		System.out.println(constrain1.toString());
		//TODO SEND array AND arrayD
		message.setConstrain1(con1);
		
		StringBuilder constrain2 = new StringBuilder();
		/* construct second constrain */
		System.out.println("second constrain");
		HashSet<WorkItemRecord> visitedW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> visitedW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashSet<Integer> visit = new HashSet<Integer>();
		
		LinkedList<Constrain> con2a = new LinkedList<Constrain>();

		LinkedList<Constrain> con2b = new LinkedList<Constrain>();
		
		for(String newResource : resources) {
			
			WorkItemRecord[] list = resourceAuthorization.get(newResource).toArray(new WorkItemRecord[0]);
			visitedW1.clear();
			visitedW2.clear();
			for(int i = 0; i < list.length-1; i++) {
				boolean found1 = false;
				toAddW1 = null;
				toAddW2 = null;
				
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(list[i])) {
						found1 = true;
						break;
					}
				}
				
				if(resourceUtilization.containsKey(newResource)) {
					boolean found3 = false;
					for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
						if(found1 && !list[i].equals(wir)) {
							found3 = true;
							break;
						}
					}
					if(found3) continue;
				}
				
				if(visitedW1.contains(list[i])) continue;
				else {
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i])) {
							toAddW1 = entry;
							visitedW1.addAll(entry);
							break;
						}
					}
				}
				visitedW2.clear();
				
				for(int j = i+1; j < list.length; j++) {
					toAddW2 = null;
					boolean found2 = false;
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i]) && entry.contains(list[j])) {
							found2 = true;
							break;
						}
					}
					if(found2) continue;
					
					for(WorkItemRecord w1 : workItemsExecuting) {
						if(w1.equals(list[j])) {
							found2 = true;
							break;
						}
					}
					
					if(resourceUtilization.containsKey(newResource)) {
						boolean found3 = false;
						for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
							if(found2 && !list[j].equals(wir)) {
								found3 = true;
								break;
							}
						}
						if(found3) continue;
					}
					
					if(visitedW2.contains(list[j])) continue;
					else {
						for(HashSet<WorkItemRecord> entry : deferedChoices) {
							if(entry.contains(list[j])) {
								toAddW2 = entry;
								visitedW2.addAll(entry);
								break;
							}
						}
					}
					
//					array = new ArrayList<MPVariable>();
//					arrayVar = new ArrayList<String>();
//					arrayD = new ArrayList<Double>();
//					
//					array1 = new ArrayList<MPVariable>();
//					arrayVar1 = new ArrayList<String>();
//					arrayD1 = new ArrayList<Double>();
					
					LinkedList<Integer> conA = new LinkedList<Integer>();
					LinkedList<Double> coeA = new LinkedList<Double>();

					LinkedList<Integer> conB = new LinkedList<Integer>();
					LinkedList<Double> coeB = new LinkedList<Double>();			
					
					if(toAddW1 != null) {
						for(WorkItemRecord w1 : toAddW1) {
							Integer rwPOS1 = findRW(newResource, w1, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
	
							if(rwPOS1 != null) {
//								array.add(d[rwPOS1]);
//								arrayVar.add(colno[rwPOS1]);
//								arrayD.add(1.0);
//							
//								array1.add(d[rwPOS1]);
//								arrayVar1.add(colno[rwPOS1]);
//								arrayD1.add(-1.0);

								conA.add(rwPOS1);
								coeA.add(1.0);
								conB.add(rwPOS1);
								coeB.add(-1.0);
							}
							
							for(long[] split : ds.getSplitts(w1, newResource)) {
								Integer xPOS1 = findX(newResource, w1, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
	
								if(xPOS1 != null) {
//									array.add(d[xPOS1]);
//									arrayVar.add(colno[xPOS1]);
//									arrayD.add(((double) durationFunction.getDuration(newResource, w1) / approximation));

									conA.add(xPOS1);
									coeA.add(((double) durationFunction.getDuration(newResource, w1) / approximation));
								}
							}
							
							currWorkItem = w1;
							
						}
						
						if(toAddW2 != null) {
							for(WorkItemRecord w2 : toAddW2) {
								
								Integer rwPOS2 = findRW(newResource, w2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
								
								if(rwPOS2 != null) {
//									array.add(d[rwPOS2]);
//									arrayVar.add(colno[rwPOS2]);
//									arrayD.add(-1.0);
//								
//									array1.add(d[rwPOS2]);
//									arrayVar1.add(colno[rwPOS2]);
//									arrayD1.add(1.0);

									conA.add(rwPOS2);
									coeA.add(-1.0);
									conB.add(rwPOS2);
									coeB.add(1.0);
								}
								
								for(long[] split : ds.getSplitts(w2, newResource)) {
									Integer xPOS2 = findX(newResource, w2, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
									
									if(xPOS2 != null) {
//										array1.add(d[xPOS2]);
//										arrayVar1.add(colno[xPOS2]);
//										arrayD1.add(((double) durationFunction.getDuration(newResource, w2) / approximation));

										conB.add(xPOS2);
										coeB.add(((double) durationFunction.getDuration(newResource, w2) / approximation));
									}
								}
								
								currWorkItem2 = w2;
								
							}
							
							boolean add = true;
							Integer rwwPOS = findRWW(newResource, currWorkItem, currWorkItem2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
							
							if(visit.contains(rwwPOS)) {
								add = false;
							} else {
								visit.add(rwwPOS);
							}
							
//							array.add(d[rwwPOS]);
//							arrayVar.add(colno[rwwPOS]);
//							arrayD.add(-M);
//							
//							array1.add(d[rwwPOS]);
//							arrayVar1.add(colno[rwwPOS]);
//							arrayD1.add(M);

							conA.add(rwwPOS);
							coeA.add(-M);
							conB.add(rwwPOS);
							coeB.add(M);
							
							if(add) {								
								//TODO SEND array AND arrayD
								if(conA.size() > 0) {
									
//									constrain2.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "<=", 0)+";\n");
																		
									Constrain c = message.new Constrain();
									c.setConstrain(conA);
									c.setCoefficient(coeA);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(0.01);
									
									con2a.add(c);
								}
								if(conB.size() > 0) {
									
//									constrain2.append(getLPformat(converDoubleTodouble(arrayD1.toArray(new Double[0])), arrayVar1.toArray(new String[0]), "<=", M)+";\n");
									
									Constrain c = message.new Constrain();
									c.setConstrain(conB);
									c.setCoefficient(coeB);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(M);
									
									con2b.add(c);
								}
							}
						}
					}
				}
			}
		}
//		System.out.println(constrain2.toString());
		message.setConstrain2a(con2a);
		message.setConstrain2b(con2b);
		
		StringBuilder constrain3 = new StringBuilder();
		/* construct third constrain */
		System.out.println("third constrain");
		
		LinkedList<Constrain> con3a = new LinkedList<Constrain>();

		LinkedList<Constrain> con3b = new LinkedList<Constrain>();
		
		LinkedList<Constrain> con3c = new LinkedList<Constrain>();

		LinkedList<Constrain> con3d = new LinkedList<Constrain>();
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItems.length; w++) {

				currWorkItem = workItems[w];

				for(long[] split : ds.getSplitts(currWorkItem, currResource)) {
					
//					array = new ArrayList<MPVariable>();
//					arrayD = new ArrayList<Double>();
//					arrayVar = new ArrayList<String>();
//					
//					array1 = new ArrayList<MPVariable>();
//					arrayD1 = new ArrayList<Double>();
//					arrayVar1 = new ArrayList<String>();
//					
//					array2 = new ArrayList<MPVariable>();
//					arrayD2 = new ArrayList<Double>();
//					arrayVar2 = new ArrayList<String>();
//					
//					array3 = new ArrayList<MPVariable>();
//					arrayD3 = new ArrayList<Double>();
//					arrayVar3 = new ArrayList<String>();
					
					LinkedList<Integer> conA = new LinkedList<Integer>();
					LinkedList<Double> coeA = new LinkedList<Double>();

					LinkedList<Integer> conB = new LinkedList<Integer>();
					LinkedList<Double> coeB = new LinkedList<Double>();

					LinkedList<Integer> conC = new LinkedList<Integer>();
					LinkedList<Double> coeC = new LinkedList<Double>();

					LinkedList<Integer> conD = new LinkedList<Integer>();
					LinkedList<Double> coeD = new LinkedList<Double>();
					
					Integer rwPos = findRW(currResource, currWorkItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(rwPos == null) continue;
					
//					array.add(d[rwPos]);
//					arrayVar.add(colno[rwPos]);
//					arrayD.add(-1.0);
//					
//					array1.add(d[rwPos]);
//					arrayVar1.add(colno[rwPos]);
//					arrayD1.add(1.0);
//					
//					array2.add(d[rwPos]);
//					arrayVar2.add(colno[rwPos]);
//					arrayD2.add(1.0);
//
//					array3.add(d[rwPos]);
//					arrayVar3.add(colno[rwPos]);
//					arrayD3.add(-1.0);
					
					conA.add(rwPos);
					coeA.add(-1.0);
					conB.add(rwPos);
					coeB.add(1.0);
					conC.add(rwPos);
					coeC.add(1.0);
					conD.add(rwPos);
					coeD.add(-1.0);
					
					Integer xPos = findX(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(xPos == null) continue;

//					array.add(d[xPos]);
//					arrayVar.add(colno[xPos]);
//					arrayD.add(M);
//					
//					array1.add(d[xPos]);
//					arrayVar1.add(colno[xPos]);
//					arrayD1.add(M);
//					
//					array2.add(d[xPos]);
//					arrayVar2.add(colno[xPos]);
//					arrayD2.add(-M);
//
//					array3.add(d[xPos]);
//					arrayVar3.add(colno[xPos]);
//					arrayD3.add(-M);
					
					conA.add(xPos);
					coeA.add(M);
					conB.add(xPos);
					coeB.add(M);
					conC.add(xPos);
					coeC.add(-M);
					conD.add(xPos);
					coeD.add(-M);
					
					Integer rw2sPos = findRW2S(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(rw2sPos == null) continue;
					
//					array2.add(d[rw2sPos]);
//					arrayVar2.add(colno[rw2sPos]);
//					arrayD2.add(-M);
//
//					array3.add(d[rw2sPos]);
//					arrayVar3.add(colno[rw2sPos]);
//					arrayD3.add(M);

					conC.add(rw2sPos);
					coeC.add(-M);
					conD.add(rw2sPos);
					coeD.add(M);
						
					//TODO SEND array AND arrayD
					if(conA.size() > 0) {
						
//						constrain3.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "<=", (M-split[0]))+";\n");
						
						Constrain c = message.new Constrain();
						c.setConstrain(conA);
						c.setCoefficient(coeA);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(M-split[0]);
						
						con3a.add(c);
					}
					if(conB.size() > 0) {

//						constrain3.append(getLPformat(converDoubleTodouble(arrayD1.toArray(new Double[0])), arrayVar1.toArray(new String[0]), "<=", (M+split[1]-0.001))+";\n");
						
						Constrain c = message.new Constrain();
						c.setConstrain(conB);
						c.setCoefficient(coeB);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(M+split[1]-0.001);
						
						con3b.add(c);
					}
					if(conC.size() > 0) {

//						constrain3.append(getLPformat(converDoubleTodouble(arrayD2.toArray(new Double[0])), arrayVar2.toArray(new String[0]), "<=", (split[0]-0.001))+";\n");
						
						Constrain c = message.new Constrain();
						c.setConstrain(conC);
						c.setCoefficient(coeC);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(split[0]-0.001);
						
						con3c.add(c);
					}
					if(conD.size() > 0) {

//						constrain3.append(getLPformat(converDoubleTodouble(arrayD3.toArray(new Double[0])), arrayVar3.toArray(new String[0]), "<=", (M-split[1]))+";\n");
						
						Constrain c = message.new Constrain();
						c.setConstrain(conD);
						c.setCoefficient(coeD);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries((M-split[1]));
						
						con3d.add(c);
					}
				}
				
			}
			
		}
//		System.out.println(constrain3.toString());

		message.setConstrain3a(con3a);
		message.setConstrain3b(con3b);
		message.setConstrain3c(con3c);
		message.setConstrain3d(con3d);
		
		StringBuilder constrain4 = new StringBuilder();
		/* construct fourth constrain */
		System.out.println("fourth constrain");

		LinkedList<Constrain> con4 = new LinkedList<Constrain>();
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItemsExecuting.length; w++) {

//				array = new ArrayList<MPVariable>();
//				arrayD = new ArrayList<Double>();
//				arrayVar = new ArrayList<String>();

				LinkedList<Integer> con = new LinkedList<Integer>();
				LinkedList<Double> coe = new LinkedList<Double>();
				
				currWorkItem = workItemsExecuting[w];
					
				Integer rwPos = findRW(currResource, currWorkItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
				if(rwPos == null) continue;

//				array.add(d[rwPos]);
//				arrayD.add(1.0);
//				arrayVar.add(colno[rwPos]);
				
				con.add(rwPos);
				coe.add(1.0);
				
				long use = resourceUtilization.get(currResource).contains(currWorkItem)?1:0;
				
				//TODO SEND array AND arrayD
				if(con.size() > 0) {

//					constrain4.append(getLPformat(converDoubleTodouble(arrayD.toArray(new Double[0])), arrayVar.toArray(new String[0]), "=", use)+";\n");
					
					Constrain c = message.new Constrain();
					c.setConstrain(con);
					c.setCoefficient(coe);
														
					c.setMinBoundaries((double) use);
					c.setMaxBoundaries((double) use);
					
					con4.add(c);
				}					
			}
			
		}
//		System.out.println(constrain4.toString());

		message.setConstrain4(con4);
		
		System.out.println("Objective function");
		row = new double[Ncol];
		
		for(int r = 0; r < resources.length; r++) {
			
			String resource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				WorkItemRecord workItem = workItems[w];
				
				Integer rwPos = findRW(resource, workItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
				if(rwPos == null) continue;
				
				row[rwPos] = (alpha / max);
					
				for(long[] split : ds.getSplitts(workItem, resource)) {
					
					Integer dPos = findX(resource, workItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(dPos == null) continue;
					
					double risk = riskFunction.getRisk(workItem, resource, split[0]);
											
					row[dPos] = (1-alpha) * (risk / 100.0);

				}
				
			}
			
		}
		
		//TODO SEND d AND row
		message.setRow(row);
		
		
		try {
			System.out.println("send request");
			oos = new ObjectOutputStream(hpc.getOutputStream());
			oos.writeObject(message);
//			oos.close();
			
//			oos = new ObjectOutputStream(new FileOutputStream("HPC.message"));
//			oos.writeObject(message);
			
			ois = new ObjectInputStream(hpc.getInputStream());
			message = (HPCmessage) ois.readObject();
//			ois.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

////		System.out.println("Real variables: "+solver.numVariables());
////		System.out.println("Real constraints: "+solver.numConstraints());
////		
////		System.out.println("min: "+getLPformat(row, colno, "", 0)+";");
////		System.out.println("constrain1");
////		System.out.println(constrain1.toString());
////		System.out.println("constrain2");
////		System.out.println(constrain2.toString());
////		System.out.println("constrain3");
////		System.out.println(constrain3.toString());
////		System.out.println("constrain4");
////		System.out.println(constrain4.toString());
////		
//		solver.setWriteModelFilename("test.lp");
	
//		int time = 60000;
//		solver.setTimeLimit(time);
//		solver.solve();
		
		/* set the objective in lpsolve */

		StringBuilder sb = new StringBuilder();
			
//		int resultStatus = solver.solve();
			
//	    sb.append("Problem solved in " + solver.wallTime() + " milliseconds\n");
//	    String typeSolution = null;
//	    if(resultStatus == MPSolver.OPTIMAL) typeSolution = "Optimal";
//	    else if(resultStatus == 1) typeSolution = "Partial";
//	    else if(resultStatus == MPSolver.ABNORMAL) typeSolution = "Abnormal";
//	    else if(resultStatus == MPSolver.INFEASIBLE) typeSolution = "Infeasible";
//	    else if(resultStatus == MPSolver.UNBOUNDED) typeSolution = "Unbounded";
	    
//		sb.append("Found " + typeSolution + " solution with cost: " + solver.objectiveValue() + "\n");
		
		double[] solution = message.getSolution();
		
		for(int i = 0; i < solution.length ; i++) {
			
			if(Math.round(solution[i]) > 0 && colno[i].startsWith(v)) {
				
				sb.append(colno[i] + ": " + solution[i] + " " + i);
				sb.append("\n");

			}			
			
			if(Math.round(solution[i]) > 0 && colno[i].startsWith(v)) {
				
				for(int j = 0; j < solution.length ; j++) {
					if(Math.round(solution[j]) > 0 && colno[j].startsWith(x_+colno[i].substring(2))) {

						HashMap<WorkItemRecord, Long> result2 = null;
						if((result2 = result.get(map.get(i).getResource())) == null) {
							result2 = new HashMap<WorkItemRecord, Long>();
							result.put(map.get(i).getResource(), result2);
						}

						result2.put(map.get(i).getWorkItem(), Math.round(solution[i]));
						
					}
				}

			}
	
		}
		
		System.out.println(sb);
				
//		solver.reset();
//		solver.clear();
//		solver.delete();
		
//		return resultStatus;
		return 0;
	}
	
	private void generateNames(String[] resources, WorkItemRecord[] workItems,  
			DurationSplitter ds, HashMap<Integer, CoupleWorkItemResource> map, String[] colno) {
		
		System.out.println("set name variable");
		
		StringBuffer buffer = new StringBuffer();
		WorkItemRecord currWorkItem = null;
		WorkItemRecord currWorkItem2 = null;
		String currResource = null;
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
				
				buffer.delete(0, buffer.length());
				buffer.append(currResource);
				buffer.append(under);
				buffer.append(getStringWorkItem(currWorkItem));
				
				Integer varRW = findRW(buffer.toString());
				
				if(varRW != null) {
				
					buffer.insert(0, v);
					colno[varRW] = buffer.toString();
					
					map.put(varRW, new CoupleWorkItemResource(currWorkItem, currResource));
				
				}
				
//				for(int w1 = 0; w1 < workItems.length; w1++) {
//					
//					if(w1 != w) {
//						
//						currWorkItem2 = workItems[w1];
//						
//						buffer.delete(0, buffer.length());
//						buffer.append(currResource);
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem));
//						buffer.append(under);
//						buffer.append(getStringWorkItem(currWorkItem2));
//						
//						Integer varRWW = findRWW(buffer.toString());
//						
//						if(varRWW != null) {
//							
//							buffer.insert(0, m1);
//							colno[varRWW] = buffer.toString();
//	
//						}
//						
//					}
//					
//				}
				
				for(long[] split : ds.getSplitts(currWorkItem, currResource)) {
					
//					buffer.delete(0, buffer.length());
//					buffer.append(currResource);
//					buffer.append(under);
//					buffer.append(getStringWorkItem(currWorkItem));
//					buffer.append(under);
//					buffer.append(split[0]);
//					buffer.append(under);
//					buffer.append(split[1]);
//				
//					Integer rw2sA = findRW2S(buffer.toString());
//					
//					if(rw2sA != null) {
//						
//						buffer.insert(0, m2);
//						colno[rw2sA] = buffer.toString();
//					
//					}

					buffer.delete(0, buffer.length());
					buffer.append(currResource);
					buffer.append(under);
					buffer.append(getStringWorkItem(currWorkItem));
					buffer.append(under);
					buffer.append(split[0]);
					buffer.append(under);
					buffer.append(split[1]);
					
					Integer varX = findX(buffer.toString());
					
					if(varX != null) {
						
						buffer.insert(0, x_);
						colno[varX] = buffer.toString();
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private void generateFirstConstrain(String[] resources, WorkItemRecord[] workItems,
			DurationSplitter ds, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, HPCmessage message, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, WorkItemRecord[] workItemsExecuting,
			LinkedList<Constrain> con1) {
		
		System.out.println("first constrain");		
		String currResource = null;
		LinkedList<Integer> con = null;
		LinkedList<Double> coe = null;
		
		for(HashSet<WorkItemRecord> set : deferedChoices) {
			
			con = new LinkedList<Integer>();
			coe = new LinkedList<Double>();
						
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(WorkItemRecord workItem : set) {
					for(long[] split : ds.getSplitts(workItem, currResource)) {
							
						Integer xPos = findX(currResource, workItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
						if(xPos == null) continue;

						con.add(xPos);
						coe.add(1.0);
														
					}
					
				}
				
			}
			
			//TODO SEND array AND arrayD
			if(con.size() > 0) {
				
				Constrain c = message.new Constrain();
				c.setConstrain(con);
				c.setCoefficient(coe);
				
				c.setMinBoundaries(1.0);
				c.setMaxBoundaries(1.0);
				
				con1.add(c);
				
			}
				
		}
		
	}
	
	private void generateSecondConstrain2(String[] resources, WorkItemRecord[] workItems,
			DurationSplitter ds, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, HPCmessage message, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, WorkItemRecord[] workItemsExecuting,
			DurationFunction durationFunction, long approximation, double M, double infinity,
			LinkedList<Constrain> con2a, LinkedList<Constrain> con2b) {
		
		System.out.println("second constrain");
		HashSet<WorkItemRecord> visitedW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> visitedW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashSet<Integer> visit = new HashSet<Integer>();
		WorkItemRecord currWorkItem = null;
		WorkItemRecord currWorkItem2 = null;
		
		LinkedList<Integer> conA = null;
		LinkedList<Double> coeA = null;
		LinkedList<Integer> conB = null;
		LinkedList<Double> coeB = null;	
		
		WorkItemRecord[] list = null;
		double dur = 0.0;
		
		for(String newResource : resources) {
			
			list = resourceAuthorization.get(newResource).toArray(new WorkItemRecord[0]);
			visitedW1.clear();
			visitedW2.clear();
			for(int i = 0; i < list.length-1; i++) {
				boolean found1 = false;
				toAddW1 = null;
				toAddW2 = null;
				
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(list[i])) {
						found1 = true;
						break;
					}
				}
				
				if(found1 && resourceUtilization.containsKey(newResource)) {
					boolean found3 = false;
					for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
						 if(!list[i].equals(wir)) {
							found3 = true;
							break;
						}
					}
					if(found3) continue;
				}
				
				if(visitedW1.contains(list[i])) continue;
				else {
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i])) {
							toAddW1 = entry;
							visitedW1.addAll(entry);
							break;
						}
					}
				}
				visitedW2.clear();
				
				for(int j = i+1; j < list.length; j++) {
					toAddW2 = null;
					boolean found2 = false;
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i]) && entry.contains(list[j])) {
							found2 = true;
							break;
						}
					}
					if(found2) continue;
					
					for(WorkItemRecord w1 : workItemsExecuting) {
						if(w1.equals(list[j])) {
							found2 = true;
							break;
						}
					}
					
					if(found2 && resourceUtilization.containsKey(newResource)) {
						boolean found3 = false;
						for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
							if(!list[j].equals(wir)) {
								found3 = true;
								break;
							}
						}
						if(found3) continue;
					}
					
					if(visitedW2.contains(list[j])) continue;
					else {
						for(HashSet<WorkItemRecord> entry : deferedChoices) {
							if(entry.contains(list[j])) {
								toAddW2 = entry;
								visitedW2.addAll(entry);
								break;
							}
						}
					}
					
					conA = new LinkedList<Integer>();
					coeA = new LinkedList<Double>();

					conB = new LinkedList<Integer>();
					coeB = new LinkedList<Double>();			
					
					if(toAddW1 != null) {
						for(WorkItemRecord w1 : toAddW1) {
							Integer rwPOS1 = findRW(newResource, w1, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
	
							if(rwPOS1 != null) {
								conA.add(rwPOS1);
								coeA.add(1.0);
								conB.add(rwPOS1);
								coeB.add(-1.0);
							}
							
							for(long[] split : ds.getSplitts(w1, newResource)) {
								Integer xPOS1 = findX(newResource, w1, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
	
								if(xPOS1 != null) {;

									conA.add(xPOS1);

									dur = ((double) durationFunction.getDuration(newResource, w1) / approximation);
									if(dur < 1.0) {
										dur = 1.0;
									}
									
									coeA.add(dur);
								}
							}
							
							currWorkItem = w1;
							
						}
						
						if(toAddW2 != null) {
							for(WorkItemRecord w2 : toAddW2) {
								
								Integer rwPOS2 = findRW(newResource, w2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
								
								if(rwPOS2 != null) {
									conA.add(rwPOS2);
									coeA.add(-1.0);
									conB.add(rwPOS2);
									coeB.add(1.0);
								}
								
								for(long[] split : ds.getSplitts(w2, newResource)) {
									Integer xPOS2 = findX(newResource, w2, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
									
									if(xPOS2 != null) {
										conB.add(xPOS2);
										dur = ((double) durationFunction.getDuration(newResource, w2) / approximation);
										if(dur < 1.0) {
											dur = 1.0;
										}
										coeB.add(dur);
									}
								}
								
								currWorkItem2 = w2;
								
							}
							
							boolean add = true;
							Integer rwwPOS = findRWW(newResource, currWorkItem, currWorkItem2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
							
							if(visit.contains(rwwPOS)) {
								add = false;
							} else {
								visit.add(rwwPOS);
							}

							conA.add(rwwPOS);
							coeA.add(-M);
							conB.add(rwwPOS);
							coeB.add(M);
							
							if(add) {								
								//TODO SEND array AND arrayD
								if(conA.size() > 0) {
									
									Constrain c = message.new Constrain();
									c.setConstrain(conA);
									c.setCoefficient(coeA);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(0.01);
									
									con2a.add(c);
								}
								if(conB.size() > 0) {
									
//									constrain2.append(getLPformat(converDoubleTodouble(arrayD1.toArray(new Double[0])), arrayVar1.toArray(new String[0]), "<=", M)+";\n");
									
									Constrain c = message.new Constrain();
									c.setConstrain(conB);
									c.setCoefficient(coeB);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(M);
									
									con2b.add(c);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void generateSecondConstrain(String[] resources, WorkItemRecord[] workItems,
			DurationSplitter ds, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, HPCmessage message, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, WorkItemRecord[] workItemsExecuting,
			DurationFunction durationFunction, long approximation, double M, double infinity,
			LinkedList<Constrain> con2a, LinkedList<Constrain> con2b) {
		
		System.out.println("second constrain");
		HashSet<WorkItemRecord> visitedW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> visitedW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashSet<Integer> visit = new HashSet<Integer>();
		WorkItemRecord currWorkItem = null;
		WorkItemRecord currWorkItem2 = null;
		
		LinkedList<Integer> conA = null;
		LinkedList<Double> coeA = null;
		LinkedList<Integer> conB = null;
		LinkedList<Double> coeB = null;	
		
		double dur = 0.0;
		
		HashSet<WorkItemRecord>[] choices = deferedChoices.toArray(new HashSet[0]);
		HashSet<WorkItemRecord> can = null;
		HashSet<WorkItemRecord> choice1 = null;
		HashSet<WorkItemRecord> choice2 = null;
		
		for(String newResource : resources) {
			
			can = resourceAuthorization.get(newResource);
			
			for(int i = 0; i < choices.length-1; i++) {
				
				choice1 = choices[i];
				
				toAddW1.clear();
				boolean exit = false;
				for(WorkItemRecord wir : choice1) {
					
					for(String otherRes : resources) {
						if(newResource != otherRes) {
							if(resourceUtilization.containsKey(otherRes) && resourceUtilization.get(otherRes).contains(wir)) {
								exit = true;
								break;
							}
						}
					}
					
					if(!exit) {
						if(can.contains(wir)) {
							toAddW1.add(wir);
						}
					}else {
						toAddW1.clear();
						break;
					}
					
				}
				
				if(toAddW1.size() > 0) {
					for(int j = i+1; j < choices.length; j++) {
						
						choice2 = choices[j];
						
						toAddW2.clear();
						boolean exit2 = false;
						for(WorkItemRecord wir : choice2) {
							
							for(String otherRes : resources) {
								if(newResource != otherRes) {
									if(resourceUtilization.containsKey(otherRes) && resourceUtilization.get(otherRes).contains(wir)) {
										exit2 = true;
										break;
									}
								}
							}
							
							if(!exit2) {
								if(can.contains(wir)) {
									toAddW2.add(wir);
								}
							}else {
								toAddW2.clear();
								break;
							}
						}
						
						if(toAddW2.size() > 0) {
							conA = new LinkedList<Integer>();
							coeA = new LinkedList<Double>();
		
							conB = new LinkedList<Integer>();
							coeB = new LinkedList<Double>();			
							
							for(WorkItemRecord w1 : toAddW1) {
								Integer rwPOS1 = findRW(newResource, w1, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
		
								if(rwPOS1 != null) {
									conA.add(rwPOS1);
									coeA.add(1.0);
									conB.add(rwPOS1);
									coeB.add(-1.0);
								}
								
								for(long[] split : ds.getSplitts(w1, newResource)) {
									Integer xPOS1 = findX(newResource, w1, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
		
									if(xPOS1 != null) {;
	
										conA.add(xPOS1);
	
										dur = ((double) durationFunction.getDuration(newResource, w1) / approximation);
										if(dur < 1.0) {
											dur = 1.0;
										}
										
										coeA.add(dur);
									}
								}
								
								currWorkItem = w1;
								
							}
							
							for(WorkItemRecord w2 : toAddW2) {
								
								Integer rwPOS2 = findRW(newResource, w2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
								
								if(rwPOS2 != null) {
									conA.add(rwPOS2);
									coeA.add(-1.0);
									conB.add(rwPOS2);
									coeB.add(1.0);
								}
								
								for(long[] split : ds.getSplitts(w2, newResource)) {
									Integer xPOS2 = findX(newResource, w2, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
									
									if(xPOS2 != null) {
										conB.add(xPOS2);
										dur = ((double) durationFunction.getDuration(newResource, w2) / approximation);
										if(dur < 1.0) {
											dur = 1.0;
										}
										coeB.add(dur);
									}
								}
								
								currWorkItem2 = w2;
								
							}
							
							boolean add = true;
							Integer rwwPOS = findRWW(newResource, currWorkItem, currWorkItem2, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
							
							if(visit.contains(rwwPOS)) {
								add = false;
							} else {
								visit.add(rwwPOS);
							}
							
							if(rwwPOS == null) add = false;
							conA.add(rwwPOS);
							coeA.add(-M);
							conB.add(rwwPOS);
							coeB.add(M);
							
							if(add) {								
								//TODO SEND array AND arrayD
								if(conA.size() > 0) {
									
									Constrain c = message.new Constrain();
									c.setConstrain(conA);
									c.setCoefficient(coeA);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(0.01);
									
									con2a.add(c);
								}
								if(conB.size() > 0) {
									
//									constrain2.append(getLPformat(converDoubleTodouble(arrayD1.toArray(new Double[0])), arrayVar1.toArray(new String[0]), "<=", M)+";\n");
									
									Constrain c = message.new Constrain();
									c.setConstrain(conB);
									c.setCoefficient(coeB);
																		
									c.setMinBoundaries(-infinity);
									c.setMaxBoundaries(M);
									
									con2b.add(c);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void generateThirdConstrain(String[] resources, WorkItemRecord[] workItems,
			DurationSplitter ds, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, HPCmessage message, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, WorkItemRecord[] workItemsExecuting,
			DurationFunction durationFunction, long approximation, double M, double infinity,
			LinkedList<Constrain> con3a, LinkedList<Constrain> con3b, LinkedList<Constrain> con3c, LinkedList<Constrain> con3d) {
		
		System.out.println("third constrain");

		WorkItemRecord currWorkItem = null;
		String currResource = null;
		
		LinkedList<Integer> conA = null;
		LinkedList<Double> coeA = null;
		LinkedList<Integer> conB = null;
		LinkedList<Double> coeB = null;
		LinkedList<Integer> conC = null;
		LinkedList<Double> coeC = null;
		LinkedList<Integer> conD = null;
		LinkedList<Double> coeD = null;
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItems.length; w++) {

				currWorkItem = workItems[w];

				for(long[] split : ds.getSplitts(currWorkItem, currResource)) {
					
					conA = new LinkedList<Integer>();
					coeA = new LinkedList<Double>();

					conB = new LinkedList<Integer>();
					coeB = new LinkedList<Double>();

					conC = new LinkedList<Integer>();
					coeC = new LinkedList<Double>();

					conD = new LinkedList<Integer>();
					coeD = new LinkedList<Double>();
					
					Integer rwPos = findRW(currResource, currWorkItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(rwPos == null) continue;
					
					conA.add(rwPos);
					coeA.add(-1.0);
					conB.add(rwPos);
					coeB.add(1.0);
					conC.add(rwPos);
					coeC.add(1.0);
					conD.add(rwPos);
					coeD.add(-1.0);
					
					Integer xPos = findX(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(xPos == null) continue;

					conA.add(xPos);
					coeA.add(M);
					conB.add(xPos);
					coeB.add(M);
					conC.add(xPos);
					coeC.add(-M);
					conD.add(xPos);
					coeD.add(-M);
					
					Integer rw2sPos = findRW2S(currResource, currWorkItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(rw2sPos == null) continue;

					conC.add(rw2sPos);
					coeC.add(-M);
					conD.add(rw2sPos);
					coeD.add(M);
						
					//TODO SEND array AND arrayD
					if(conA.size() > 0) {
						
						Constrain c = message.new Constrain();
						c.setConstrain(conA);
						c.setCoefficient(coeA);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(M-split[0]);
						
						con3a.add(c);
					}
					if(conB.size() > 0) {

						Constrain c = message.new Constrain();
						c.setConstrain(conB);
						c.setCoefficient(coeB);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(M+split[1]-1.0);
						
						con3b.add(c);
					}
					if(conC.size() > 0) {

						Constrain c = message.new Constrain();
						c.setConstrain(conC);
						c.setCoefficient(coeC);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries(split[0]-1.0);
						
						con3c.add(c);
					}
					if(conD.size() > 0) {

						Constrain c = message.new Constrain();
						c.setConstrain(conD);
						c.setCoefficient(coeD);
															
						c.setMinBoundaries(-infinity);
						c.setMaxBoundaries((M-split[1]));
						
						con3d.add(c);
					}
				}
			}
		}
	}
	
	private void generateFourthConstrain(String[] resources, WorkItemRecord[] workItems,
			DurationSplitter ds, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, HPCmessage message, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, WorkItemRecord[] workItemsExecuting,
			DurationFunction durationFunction, long approximation, double M, double infinity,
			LinkedList<Constrain> con4) {

		System.out.println("fourth constrain");	
		WorkItemRecord currWorkItem = null;
		String currResource = null;
		
		LinkedList<Integer> con = null;
		LinkedList<Double> coe = null;
		
		for(int r = 0; r < resources.length; r++) {
			
			currResource = resources[r];
		
			for(int w = 0; w < workItemsExecuting.length; w++) {

				con = new LinkedList<Integer>();
				coe = new LinkedList<Double>();
				
				currWorkItem = workItemsExecuting[w];
					
				Integer rwPos = findRW(currResource, currWorkItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
				if(rwPos == null) continue;

				con.add(rwPos);
				coe.add(1.0);
				
				long use = resourceUtilization.get(currResource).contains(currWorkItem)?1:0;
				
				//TODO SEND array AND arrayD
				if(con.size() > 0) {

					Constrain c = message.new Constrain();
					c.setConstrain(con);
					c.setCoefficient(coe);
														
					c.setMinBoundaries((double) use);
					c.setMaxBoundaries((double) use);
					
					con4.add(c);
				}					
			}
			
		}
	}
	
	private int discoverRW(String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting) {
		int res = 0;

		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.get(newResource)) {
				boolean found = false;
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(w)) {
						found = true;
					}
				}
				if(!found) {
					res++;
					cacheRW.put(newResource+"_"+getStringWorkItem(w), res-1);
				}else {
					for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
						if(w.equals(wir)) {
							res++;
							cacheRW.put(newResource+"_"+getStringWorkItem(w), res-1);
							break;
						}
					}
				}
			}
		}
		return res;
	}
	
	private int discoverRWW(String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, int numberRW) {
		int res = 0;
		int res1 = numberRW;
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord>[] choices = deferedChoices.toArray(new HashSet[0]);
		HashSet<WorkItemRecord> can = null;
		HashSet<WorkItemRecord> choice1 = null;
		HashSet<WorkItemRecord> choice2 = null;
		
		for(String newResource : resources) {
		
			can = resourceAuthorization.get(newResource);
			
			for(int i = 0; i < choices.length-1; i++) {
				
				choice1 = choices[i];
				
				toAddW1.clear();
				boolean exit = false;
				for(WorkItemRecord wir : choice1) {
					
					for(String otherRes : resources) {
						if(newResource != otherRes) {
							if(resourceUtilization.containsKey(otherRes) && resourceUtilization.get(otherRes).contains(wir)) {
								exit = true;
								break;
							}
						}
					}
					
					if(!exit) {
						if(can.contains(wir)) {
							toAddW1.add(wir);
						}
					}else {
						toAddW1.clear();
						break;
					}
					
				}
				
				if(toAddW1.size() > 0) {
					for(int j = i+1; j < choices.length; j++) {
						
						choice2 = choices[j];
						
						toAddW2.clear();
						boolean exit2 = false;
						for(WorkItemRecord wir : choice2) {
							
							for(String otherRes : resources) {
								if(newResource != otherRes) {
									if(resourceUtilization.containsKey(otherRes) && resourceUtilization.get(otherRes).contains(wir)) {
										exit2 = true;
										break;
									}
								}
							}
							
							if(!exit2) {
								if(can.contains(wir)) {
									toAddW2.add(wir);
								}
							}else {
								toAddW2.clear();
								break;
							}
						}

						boolean add = false;
						res++;
						if(toAddW2.size() > 0) {
							for(WorkItemRecord w1 : toAddW1) {
								for(WorkItemRecord w2 : toAddW2) {
									if(!cacheRWW.containsKey(newResource+"_"+getStringWorkItem(w1)+"_"+getStringWorkItem(w2))) {
										cacheRWW.put(newResource+"_"+getStringWorkItem(w1)+"_"+getStringWorkItem(w2), (res1+(res-1)));
										cacheRWW.put(newResource+"_"+getStringWorkItem(w2)+"_"+getStringWorkItem(w1), (res1+(res-1)));
										add = true;
									}
								}
							}
						}
						if(!add) res--;
					}
				}
			}
		}
		return res;
	}
	
	private int discoverRWW2(String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, int numberRW) {
		int res = 0;
		int res1 = numberRW;
		
		HashSet<WorkItemRecord> visitedW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> visitedW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashMap<Integer, HashSet<WorkItemRecord>> map = new HashMap<Integer, HashSet<WorkItemRecord>>();
		
		for(String newResource : resources) {
			WorkItemRecord[] list = resourceAuthorization.get(newResource).toArray(new WorkItemRecord[0]);
			visitedW1.clear();
//			visitedW2 = new HashSet<WorkItemRecord>();
			visitedW2.clear();
			
			for(int i = 0; i < list.length-1; i++) {
				boolean found1 = false;
				toAddW1 = null;
				toAddW2 = null;
				
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(list[i])) {
						found1 = true;
						break;
					}
				}
				
				if(resourceUtilization.containsKey(newResource)) {
					boolean found = false;
					for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
						if(found1 && !list[i].equals(wir)) {
							found = true;
							break;
						}
					}
					if(found) continue;
				}
				
//				if(found1 && !list[i].equals(resourceUtilization.get(newResource))) {
////				System.out.println("Third exit");
//					continue;
//				}
				
				if(visitedW1.contains(list[i])) continue;
				else {
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i])) {
							toAddW1 = entry;
							visitedW1.addAll(entry);
							break;
						}
					}
				}
				visitedW2.clear(); //FIXME
				
				for(int j = i+1; j < list.length; j++) {
//					System.out.println("TEST "+newResource+"_"+getStringWorkItem(list[i])+"_"+getStringWorkItem(list[j]));
					toAddW2 = null;
					boolean found2 = false;
					for(HashSet<WorkItemRecord> entry : deferedChoices) {
						if(entry.contains(list[i]) && entry.contains(list[j])) {
							found2 = true;
							break;
						}
					}
					
					if(found2) {
//						System.out.println("Second exit");
						continue;
					}
					
					for(WorkItemRecord w1 : workItemsExecuting) {
						if(w1.equals(list[j])) {
							found2 = true;
							break;
						}
					}
					
					if(resourceUtilization.containsKey(newResource)) {
						boolean found3 = false;
						for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
							if(found2 && !list[j].equals(wir)) {
								found3 = true;
								break;
							}
						}
						if(found3) continue;
					}
//					if(found2 && !list[j].equals(resourceUtilization.get(newResource))) {
////						System.out.println("Third exit");
//						continue;
//					}
					
					if(visitedW2.contains(list[j])) {
//						System.out.println("Fourth exit");
						continue;
					}
					else {
						for(HashSet<WorkItemRecord> entry : deferedChoices) {
							if(entry.contains(list[j])) {
								toAddW2 = entry;
								visitedW2.addAll(entry);
								break;
							}
						}
					}

					boolean add = false;
					res++;
					if(toAddW1 != null) {
						for(WorkItemRecord w1 : toAddW1) {
							if(toAddW2 != null) {
								for(WorkItemRecord w2 : toAddW2) {
									if(!cacheRWW.containsKey(newResource+"_"+getStringWorkItem(w1)+"_"+getStringWorkItem(w2))) {
										cacheRWW.put(newResource+"_"+getStringWorkItem(w1)+"_"+getStringWorkItem(w2), (res1+(res-1)));
										cacheRWW.put(newResource+"_"+getStringWorkItem(w2)+"_"+getStringWorkItem(w1), (res1+(res-1)));
										add = true;
		//								System.out.println(newResource+"_"+getStringWorkItem(w1)+"_"+getStringWorkItem(w2)+" "+(res1+(res-1)));
									}
								}
							}
						}
					}
					if(!add) res--;
				}
			}
		}
		return res;
	}
	
	private int discoverRW2S(String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting, DurationSplitter ds,
			int numberRW, int numberRWW) {
		int res = 0;
		int res1 = numberRW + numberRWW;
		
		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.get(newResource)) {
				boolean found = false;
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(w)) {
						found = true;
					}
				}
				if(!found) {
					for(long[] inter : ds.getSplitts(w, newResource)) {
						res++;
						cacheRW2S.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1], res1+(res-1));
//						res += 1;
//						cacheRW2S.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1]+"_"+false, res1+res-1);
					}
				}else {
					if(resourceUtilization.containsKey(newResource)) {
						for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
							if(w.equals(wir)) {
								for(long[] inter : ds.getSplitts(w, newResource)) {
									res++;
									cacheRW2S.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1], res1+(res-1));
		//							res += 1;
		//							cacheRW2S.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1]+"_"+false, res1+res-1);
								}
								break;
							}
						}
					}
				}
			}
		}
		return res;
	}
	
	private int discoverX(String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting, DurationSplitter ds,
			int numberRW, int numberRWW, int numberRW2S) {
		int res = 0;
		int res1 = numberRW + numberRWW + numberRW2S;
		
		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.get(newResource)) {
				boolean found = false;
				for(WorkItemRecord w1 : workItemsExecuting) {
					if(w1.equals(w)) {
						found = true;
					}
				}
				if(!found) {
					for(long[] inter : ds.getSplitts(w, newResource)) {
						res++;
						cacheX.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1], res1+(res-1));
					}
				}else {
					if(resourceUtilization.containsKey(newResource)) {
						for(WorkItemRecord wir : resourceUtilization.get(newResource)) {
							if(w.equals(wir)) {
								for(long[] inter : ds.getSplitts(w, newResource)) {
									res++;
									cacheX.put(newResource+"_"+getStringWorkItem(w)+"_"+inter[0]+"_"+inter[1], res1+(res-1));
								}
							}
						}
					}
				}
			}
		}
		return res;
	}
	
	private Integer findRW(String resource, WorkItemRecord workItem, String[] resources, HashMap<String, 
			HashSet<WorkItemRecord>> resourceAuthorization,	HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			WorkItemRecord[] workItemsExecuting) {
		
		return cacheRW.get(resource+"_"+getStringWorkItem(workItem));
	}
	
	private Integer findRW(String key) {
		
		return cacheRW.get(key);
	}
	
	private Integer findRWW(String resource, WorkItemRecord workItem1, WorkItemRecord workItem2, 
			String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting) {

		return cacheRWW.get(resource+"_"+getStringWorkItem(workItem1)+"_"+getStringWorkItem(workItem2));
	}
	
	private Integer findRWW(String key) {

		return cacheRWW.get(key);
	}
	
	private Integer findRW2S(String resource, WorkItemRecord workItem, long[] interval, 
			String[] resources,	HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting) {
		
		return cacheRW2S.get(resource+"_"+getStringWorkItem(workItem)+"_"+interval[0]+"_"+interval[1]);
	}
	
	private Integer findRW2S(String key) {
		
		return cacheRW2S.get(key);
	}
	
	private Integer findX(String resource, WorkItemRecord workItem, long[] interval,
			String[] resources, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, WorkItemRecord[] workItemsExecuting) {
		
		return cacheX.get(resource+"_"+getStringWorkItem(workItem)+"_"+interval[0]+"_"+interval[1]);
	}
	
	private Integer findX(String key) {
		
		return cacheX.get(key);
	}

	private String getLPformat(long[] row, String[] colno, String operator, double value) {
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
	
	private String getLPformat(double[] row, String[] colno, String operator, double value) {
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
	
	private String getStringWorkItem(WorkItemRecord wir) {
		if(wir == null) return null;
		
		String name = null;
		if((name = cacheWorkItems.get(wir)) == null) {
			nameBuffer.delete(0, nameBuffer.length());
			nameBuffer.append(wir.getRootCaseID());
			nameBuffer.append(under);
			nameBuffer.append(wir.getTaskID());
			
			name = nameBuffer.toString();
			cacheWorkItems.put(wir, name);
		}
		return name;
//		return nameBuffer.toString();
//		return wir.getRootCaseID() + "_" + wir.getTaskID();
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
	
	public HashMap<String, HashSet<WorkItemRecord>> generateUse(WorkItemRecord[] started, String[] resources, Random r) {
		HashMap<String, HashSet<WorkItemRecord>> use = new HashMap<String, HashSet<WorkItemRecord>>();
		
		LinkedList<String> free = new LinkedList<String>();
		for(int i = 0; i < resources.length; i++) {
			free.add(resources[i]);
		}
		
		for(WorkItemRecord workItem : started) {
			
			int a = r.nextInt(free.size());
			if(a >= free.size()) a--;
			else if(a < 0) a++;
			
			String res = free.remove(a);
			HashSet<WorkItemRecord> set = null;
			if((set = use.get(res)) == null) {
				set = new HashSet<WorkItemRecord>();
				use.put(res, set);
			}
			set.add(workItem);
			
		}
		
		return use;
	}
	
	public HashMap<String, HashSet<WorkItemRecord>> generateCan(WorkItemRecord[] workItems, String[] resources, HashMap<String, HashSet<WorkItemRecord>> use, Random r) {
		HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
		
		for(int i = 0; i < resources.length; i++) {
			
			String res = resources[i];
			
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			
			HashSet<WorkItemRecord> set2 = use.get(res);
			if(set2 != null) {
				for(WorkItemRecord wir : set2) {
	//				WorkItemRecord wir = use.get(res);
					if(wir != null) set.add(wir);
				}
			}
			
			for(int j = 0; j < workItems.length; j++) {
//				if(res.equals("R19")) {
//					if(j == 5) {
//						set.add(workItems[j]);
//					}
//				}else {
//					if(j != 5 && r.nextDouble() > 0.4) {
//					if(r.nextDouble() > 0.4) {
						set.add(workItems[j]);
//					}
//				}
			}
			
			can.put(res, set);
			
		}
		
		return can;
	}
	
	public HashSet<HashSet<WorkItemRecord>> generateChoice(WorkItemRecord[] workItems, WorkItemRecord[] started, int choices, Random r) {
		LinkedList<HashSet<WorkItemRecord>> choice = new LinkedList<HashSet<WorkItemRecord>>();
		
		LinkedList<WorkItemRecord> free = new LinkedList<WorkItemRecord>();
		for(int i = 0; i < workItems.length; i++) {
			free.add(workItems[i]);
		}
		
		for(int i = 0; i < choices; i++) {
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			
			int a = r.nextInt(free.size());
			
			WorkItemRecord wir = free.remove(a);
			set.add(wir);
						
			choice.add(set);
		}
		
		Iterator<WorkItemRecord> it = free.iterator();
		
//		HashSet<WorkItemRecord>[] array = choice.toArray(new HashSet[0]);
		
		while(it.hasNext()) {
			
			int a = r.nextInt(choice.size());
			
			int pos = a;
			choice.get(pos).add(it.next());
		}
		
		for(WorkItemRecord wir : started) {
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			set.add(wir);
						
			choice.add(set);
		}
		
		return new HashSet<HashSet<WorkItemRecord>>(choice);
	}
	
	public int execute(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItemsOffered, WorkItemRecord[] workItems, 
			DurationFunction durationFunction, HashMap<String, HashSet<WorkItemRecord>> resourceUtilization, 
			HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, RiskFunction riskFunction, 
			HashSet<HashSet<WorkItemRecord>> deferedChoices, HashMap<String, HashMap<WorkItemRecord, Long>> result, 
			DurationSplitter ds, HashMap<WorkItemRecord, Long> preAllocated, long approximation, Socket hpc, int timeout) {
		
		HPCmessage message = new HPCmessage();
		
//		StringBuffer buffer = new StringBuffer();
		
		cacheWorkItems.clear();
		cacheRW.clear();
		cacheRWW.clear();
		cacheRW2S.clear();
		cacheX.clear();
		
		HashMap<Integer, CoupleWorkItemResource> map = new HashMap<Integer, ILPBuilderSCIPNewMax.CoupleWorkItemResource>();
		
		long max = (findMaxDuration(resources, workItems, durationFunction) / approximation);
		if(max == 0) max+=1001;
		System.out.println(max);
		
		int rw = discoverRW(resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
		int rww = discoverRWW(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, deferedChoices, rw);
		int rw2s = discoverRW2S(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, ds, rw, rww);
		int x = discoverX(resources, resourceAuthorization, resourceUtilization, workItemsExecuting, ds, rw, rww, rw2s);
		
		int numberD = rw+rww+rw2s+x;
		
	    double infinity = Double.MAX_VALUE; //MPSolver.infinity();
	    double M = 2*max;

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */
		int Ncol = numberD;

		/* create space large enough for one row */
		String[] colno = new String[Ncol];
		double[] row = new double[Ncol];

		/* let us name our variables. Not required, but can be useful for debugging */

		message.setRW(rw);
		message.setRWW(rww);
		message.setRW2S(rw2s);
		message.setX(x);
		message.setMax(max);
				
		//Populate colno
		generateNames(resources, workItems, ds, map, colno);
		
		//TODO SEND colno
		message.setColno(colno);
		
		LinkedList<Constrain> con1 = new LinkedList<Constrain>();
		
		/* construct first constrain */
		//Populate con1 
		generateFirstConstrain(resources, workItems, ds, resourceUtilization, resourceAuthorization, 
				message, deferedChoices, workItemsExecuting, con1);

		//TODO SEND array AND arrayD
		message.setConstrain1(con1);
		
		/* construct second constrain */
		
		LinkedList<Constrain> con2a = new LinkedList<Constrain>();
		LinkedList<Constrain> con2b = new LinkedList<Constrain>();
		
		//Populate con2a and con2b 
		generateSecondConstrain(resources, workItems, ds, resourceUtilization, resourceAuthorization, 
				message, deferedChoices, workItemsExecuting, durationFunction, approximation, M, infinity, con2a, con2b);
		
//		System.out.println(constrain2.toString());
		message.setConstrain2a(con2a);
		message.setConstrain2b(con2b);
		
		/* construct third constrain */
		
		LinkedList<Constrain> con3a = new LinkedList<Constrain>();
		LinkedList<Constrain> con3b = new LinkedList<Constrain>();
		LinkedList<Constrain> con3c = new LinkedList<Constrain>();
		LinkedList<Constrain> con3d = new LinkedList<Constrain>();
		
		//Populate con3a, con3b, con3c and con3d
		generateThirdConstrain(resources, workItems, ds, resourceUtilization, resourceAuthorization, 
				message, deferedChoices, workItemsExecuting, durationFunction, approximation, M, infinity, con3a, con3b, con3c, con3d);
		
//		System.out.println(constrain3.toString());

		message.setConstrain3a(con3a);
		message.setConstrain3b(con3b);
		message.setConstrain3c(con3c);
		message.setConstrain3d(con3d);
		
		/* construct fourth constrain */

		LinkedList<Constrain> con4 = new LinkedList<Constrain>();

		//Populate con4
		generateFourthConstrain(resources, workItems, ds, resourceUtilization, resourceAuthorization, 
				message, deferedChoices, workItemsExecuting, durationFunction, approximation, M, infinity, con4);
		
//		System.out.println(constrain4.toString());

		message.setConstrain4(con4);
		
		System.out.println("Objective function");
		row = new double[Ncol];
		
		for(int r = 0; r < resources.length; r++) {
			
			String resource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				WorkItemRecord workItem = workItems[w];
				
				Integer rwPos = findRW(resource, workItem, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
				if(rwPos == null) continue;
				
				row[rwPos] = (alpha / max);
					
				for(long[] split : ds.getSplitts(workItem, resource)) {
					
					Integer dPos = findX(resource, workItem, split, resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
					if(dPos == null) continue;
					
					double risk = riskFunction.getRisk(workItem, resource, split[0]);
											
					row[dPos] = (1-alpha) * (risk / 100.0);

				}
				
			}
			
		}
		
		//TODO SEND d AND row
		message.setRow(row);
		
		
//		try {
//			System.out.println("send request");
//			oos = new ObjectOutputStream(hpc.getOutputStream());
//			oos.writeObject(message);
////			oos.close();
//			
////			oos = new ObjectOutputStream(new FileOutputStream("HPC.message"));
////			oos.writeObject(message);
//			
//			ois = new ObjectInputStream(hpc.getInputStream());
//			message = (HPCmessage) ois.readObject();
////			ois.close();
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		/* set the objective in lpsolve */

		double[] solution = GurobiSolver.execute(message, timeout, 6); //message.getSolution();
		
		for(int i = 0; i < rw ; i++) {
			
			if(Math.round(solution[i]) > 0) {
				
				for(int j = (rw+rw2s+rww); j < solution.length ; j++) {
					if(Math.round(solution[j]) > 0 && colno[j].startsWith(x_+colno[i].substring(2))) {

						HashMap<WorkItemRecord, Long> result2 = null;
						if((result2 = result.get(map.get(i).getResource())) == null) {
							result2 = new HashMap<WorkItemRecord, Long>();
							result.put(map.get(i).getResource(), result2);
						}

						result2.put(map.get(i).getWorkItem(), Math.round(solution[i]));
						
					}
				}

			}
	
		}
		
//		return resultStatus;
		return 0;
	}	
	
	class CoupleWorkItemResource {
		
		private WorkItemRecord wir = null; 
		private String resource = null;

		public CoupleWorkItemResource(WorkItemRecord wir, String resource) {
			this.wir = wir;
			this.resource = resource;
		}

		public WorkItemRecord getWorkItem() {
			return wir;
		}

		public String getResource() {
			return resource;
		}
		
	}

	public void clear() {
		cacheRW.clear();
		cacheRWW.clear();
		cacheRW2S.clear();
		cacheX.clear();
	}

}
