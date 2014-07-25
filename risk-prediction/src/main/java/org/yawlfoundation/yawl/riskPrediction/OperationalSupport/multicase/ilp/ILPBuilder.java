package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.ilp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import Test.GurobiSolver;
import Test.HPCmessage;
import Test.HPCmessage.Constrain;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationSplitter;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem.ResourceAuthorization;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem.ResourceUtilization;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.RiskFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem.ResourceWorkItemSchedule;


public class ILPBuilder {
	
	private static StringBuffer nameBuffer = new StringBuffer();
	
	private static String v = "V_";
	private static String x_ = "X_";
	
	private static String under = "_";
	
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private HashMap<WorkItemRecord, String> cacheWorkItems = new HashMap<WorkItemRecord, String>();
	private HashMap<String, Integer> cacheRW = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheRWW = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheRW2S = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheX = new HashMap<String, Integer>();

	public long findMaxDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction) {

		long duration = 0L;
		
		for(WorkItemRecord workItem : workItems) {
			duration += durationFunction.getDuration(workItem);
		}

		return duration;
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

				for(long[] split : ds.getSplitts(currWorkItem, currResource)) {

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
	
	private void generateFirstConstrain(String[] resources, DurationSplitter ds, HPCmessage message,
			HashSet<HashSet<WorkItemRecord>> deferedChoices, LinkedList<Constrain> con1) {
		
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
							
						Integer xPos = findX(currResource, workItem, split);
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

	private void generateSecondConstrain(String[] resources,
			DurationSplitter ds, ResourceUtilization resourceUtilization,
			ResourceAuthorization resourceAuthorization, HPCmessage message,
			HashSet<HashSet<WorkItemRecord>> deferedChoices,
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
			
			can = resourceAuthorization.getWorkItems(newResource);
			
			for(int i = 0; i < choices.length-1; i++) {
				
				choice1 = choices[i];
				
				toAddW1.clear();
				boolean exit = false;
				for(WorkItemRecord wir : choice1) {
					
					for(String otherRes : resources) {
						if(newResource != otherRes) {
							if(resourceUtilization.hasWorkItem(otherRes, wir)) {
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
                                    if(resourceUtilization.hasWorkItem(otherRes, wir)) {
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
								Integer rwPOS1 = findRW(newResource, w1);
		
								if(rwPOS1 != null) {
									conA.add(rwPOS1);
									coeA.add(1.0);
									conB.add(rwPOS1);
									coeB.add(-1.0);
								}
								
								for(long[] split : ds.getSplitts(w1, newResource)) {
									Integer xPOS1 = findX(newResource, w1, split);
		
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
								
								Integer rwPOS2 = findRW(newResource, w2);
								
								if(rwPOS2 != null) {
									conA.add(rwPOS2);
									coeA.add(-1.0);
									conB.add(rwPOS2);
									coeB.add(1.0);
								}
								
								for(long[] split : ds.getSplitts(w2, newResource)) {
									Integer xPOS2 = findX(newResource, w2, split);
									
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
							Integer rwwPOS = findRWW(newResource, currWorkItem, currWorkItem2);
							
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
			DurationSplitter ds, HPCmessage message, double M, double infinity,
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
					
					Integer rwPos = findRW(currResource, currWorkItem);
					if(rwPos == null) continue;
					
					conA.add(rwPos);
					coeA.add(-1.0);
					conB.add(rwPos);
					coeB.add(1.0);
					conC.add(rwPos);
					coeC.add(1.0);
					conD.add(rwPos);
					coeD.add(-1.0);
					
					Integer xPos = findX(currResource, currWorkItem, split);
					if(xPos == null) continue;

					conA.add(xPos);
					coeA.add(M);
					conB.add(xPos);
					coeB.add(M);
					conC.add(xPos);
					coeC.add(-M);
					conD.add(xPos);
					coeD.add(-M);
					
					Integer rw2sPos = findRW2S(currResource, currWorkItem, split);
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
	
	private void generateFourthConstrain(String[] resources, ResourceUtilization resourceUtilization,
			HPCmessage message, WorkItemRecord[] workItemsExecuting, LinkedList<Constrain> con4) {

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
					
				Integer rwPos = findRW(currResource, currWorkItem);
				if(rwPos == null) continue;

				con.add(rwPos);
				coe.add(1.0);
				
				long use = resourceUtilization.hasWorkItem(currResource, currWorkItem)?1:0;
				
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
	
	private int discoverRW(String[] resources, ResourceAuthorization resourceAuthorization,
			ResourceUtilization resourceUtilization, WorkItemRecord[] workItemsExecuting) {
		int res = 0;

		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.getWorkItems(newResource)) {
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
					for(WorkItemRecord wir : resourceUtilization.getWorkItems(newResource)) {
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
	
	private int discoverRWW(String[] resources, ResourceAuthorization resourceAuthorization,
			ResourceUtilization resourceUtilization, HashSet<HashSet<WorkItemRecord>> deferedChoices, int numberRW) {
		int res = 0;
		int res1 = numberRW;
		
		HashSet<WorkItemRecord> toAddW1 = new HashSet<WorkItemRecord>();
		HashSet<WorkItemRecord> toAddW2 = new HashSet<WorkItemRecord>();
		
		HashSet<WorkItemRecord>[] choices = deferedChoices.toArray(new HashSet[0]);
		HashSet<WorkItemRecord> can = null;
		HashSet<WorkItemRecord> choice1 = null;
		HashSet<WorkItemRecord> choice2 = null;
		
		for(String newResource : resources) {
		
			can = resourceAuthorization.getWorkItems(newResource);
			
			for(int i = 0; i < choices.length-1; i++) {
				
				choice1 = choices[i];
				
				toAddW1.clear();
				boolean exit = false;
				for(WorkItemRecord wir : choice1) {
					
					for(String otherRes : resources) {
						if(newResource != otherRes) {
                            if(resourceUtilization.hasWorkItem(otherRes, wir)) {
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
                                    if(resourceUtilization.hasWorkItem(otherRes, wir)) {
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

	private int discoverRW2S(String[] resources, ResourceAuthorization resourceAuthorization,
			ResourceUtilization resourceUtilization, WorkItemRecord[] workItemsExecuting, DurationSplitter ds,
			int numberRW, int numberRWW) {
		int res = 0;
		int res1 = numberRW + numberRWW;
		
		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.getWorkItems(newResource)) {
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
					if(resourceUtilization.hasResource(newResource)) {
						for(WorkItemRecord wir : resourceUtilization.getWorkItems(newResource)) {
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
	
	private int discoverX(String[] resources, ResourceAuthorization resourceAuthorization,
			ResourceUtilization resourceUtilization, WorkItemRecord[] workItemsExecuting, DurationSplitter ds,
			int numberRW, int numberRWW, int numberRW2S) {
		int res = 0;
		int res1 = numberRW + numberRWW + numberRW2S;
		
		for(String newResource : resources) {
			for(WorkItemRecord w : resourceAuthorization.getWorkItems(newResource)) {
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
					if(resourceUtilization.hasResource(newResource)) {
						for(WorkItemRecord wir : resourceUtilization.getWorkItems(newResource)) {
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
	
	private Integer findRW(String resource, WorkItemRecord workItem) {
		
		return cacheRW.get(resource+"_"+getStringWorkItem(workItem));
	}
	
	private Integer findRW(String key) {
		
		return cacheRW.get(key);
	}
	
	private Integer findRWW(String resource, WorkItemRecord workItem1, WorkItemRecord workItem2) {

		return cacheRWW.get(resource+"_"+getStringWorkItem(workItem1)+"_"+getStringWorkItem(workItem2));
	}

	private Integer findRW2S(String resource, WorkItemRecord workItem, long[] interval) {
		
		return cacheRW2S.get(resource+"_"+getStringWorkItem(workItem)+"_"+interval[0]+"_"+interval[1]);
	}

	private Integer findX(String resource, WorkItemRecord workItem, long[] interval) {
		
		return cacheX.get(resource+"_"+getStringWorkItem(workItem)+"_"+interval[0]+"_"+interval[1]);

	}
	
	private Integer findX(String key) {
		
		return cacheX.get(key);
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
	}

	public int execute(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItems,
			DurationFunction durationFunction, ResourceUtilization resourceUtilization,
			ResourceAuthorization resourceAuthorization, RiskFunction riskFunction,
			HashSet<HashSet<WorkItemRecord>> deferedChoices, ResourceWorkItemSchedule schedule,
			DurationSplitter ds, long approximation, Socket hpc, int timeout) {
		
		HPCmessage message = new HPCmessage();
		
//		StringBuffer buffer = new StringBuffer();
		
		cacheWorkItems.clear();
		cacheRW.clear();
		cacheRWW.clear();
		cacheRW2S.clear();
		cacheX.clear();
		
		HashMap<Integer, CoupleWorkItemResource> map = new HashMap<Integer, ILPBuilder.CoupleWorkItemResource>();
		
		long max = (findMaxDuration(resources, workItems, durationFunction) / approximation);
		if(max == 0) max+=1001;
		System.out.println(max);
		
		int rw = discoverRW(resources, resourceAuthorization, resourceUtilization, workItemsExecuting);
		int rww = discoverRWW(resources, resourceAuthorization, resourceUtilization, deferedChoices, rw);
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
		generateFirstConstrain(resources, ds, message, deferedChoices, con1);

		//TODO SEND array AND arrayD
		message.setConstrain1(con1);
		
		/* construct second constrain */
		
		LinkedList<Constrain> con2a = new LinkedList<Constrain>();
		LinkedList<Constrain> con2b = new LinkedList<Constrain>();
		
		//Populate con2a and con2b 
		generateSecondConstrain(resources, ds, resourceUtilization, resourceAuthorization,
				message, deferedChoices, durationFunction, approximation, M, infinity, con2a, con2b);
		
//		System.out.println(constrain2.toString());
		message.setConstrain2a(con2a);
		message.setConstrain2b(con2b);
		
		/* construct third constrain */
		
		LinkedList<Constrain> con3a = new LinkedList<Constrain>();
		LinkedList<Constrain> con3b = new LinkedList<Constrain>();
		LinkedList<Constrain> con3c = new LinkedList<Constrain>();
		LinkedList<Constrain> con3d = new LinkedList<Constrain>();
		
		//Populate con3a, con3b, con3c and con3d
		generateThirdConstrain(resources, workItems, ds, message, M, infinity, con3a, con3b, con3c, con3d);
		
//		System.out.println(constrain3.toString());

		message.setConstrain3a(con3a);
		message.setConstrain3b(con3b);
		message.setConstrain3c(con3c);
		message.setConstrain3d(con3d);
		
		/* construct fourth constrain */

		LinkedList<Constrain> con4 = new LinkedList<Constrain>();

		//Populate con4
		generateFourthConstrain(resources, resourceUtilization, message, workItemsExecuting, con4);
		
//		System.out.println(constrain4.toString());

		message.setConstrain4(con4);
		
		System.out.println("Objective function");
		row = new double[Ncol];
		
		for(int r = 0; r < resources.length; r++) {
			
			String resource = resources[r];
			
			for(int w = 0; w < workItems.length; w++) {
				
				WorkItemRecord workItem = workItems[w];
				
				Integer rwPos = findRW(resource, workItem);
				if(rwPos == null) continue;
				
				row[rwPos] = (alpha / max);
					
				for(long[] split : ds.getSplitts(workItem, resource)) {
					
					Integer dPos = findX(resource, workItem, split);
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

		/* set the objective in lpsolve */

		double[] solution = GurobiSolver.execute(message, timeout, 6); //message.getSolution();
		
		for(int i = 0; i < rw ; i++) {
			
			if(Math.round(solution[i]) > 0) {
				
				for(int j = (rw+rw2s+rww); j < solution.length ; j++) {
					if(Math.round(solution[j]) > 0 && colno[j].startsWith(x_+colno[i].substring(2))) {

                        schedule.addScheduleToResource(map.get(i).getResource(), map.get(i).getWorkItem(), Math.round(solution[i]));
						
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
