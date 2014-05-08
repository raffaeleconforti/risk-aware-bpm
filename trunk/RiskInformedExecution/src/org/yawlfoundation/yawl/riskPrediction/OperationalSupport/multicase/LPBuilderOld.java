package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import lpsolve.*;

public class LPBuilderOld {
	
	private HashMap<String, Integer> cacheD = new HashMap<String, Integer>();
	private HashMap<String, Integer> cacheX = new HashMap<String, Integer>();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean execute(double alpha, String[] resources, WorkItemRecord[] workItemsExecuting, WorkItemRecord[] workItemsOffered, DurationFunction durationFunction, HashMap<String, WorkItemRecord> resourceUtilization, HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization, RiskFunction riskFunction, HashSet<HashSet<WorkItemRecord>> deferedChoices, HashMap<String, WorkItemRecord> result) throws LpSolveException {

		boolean optimal = false;
		
		WorkItemRecord[] workItems = Arrays.copyOf(workItemsOffered, workItemsOffered.length + workItemsExecuting.length);
		System.arraycopy(workItemsExecuting, 0, workItems, workItemsOffered.length, workItemsExecuting.length);
		
		long baseTime = System.currentTimeMillis();
		long maxDuration = findMaxDuration(resources, workItems, durationFunction);
		System.out.println("maxDuration "+maxDuration);
		long durationTimeFrame = findGCD(resources, workItems, durationFunction);
//		durationTimeFrame = 1000;
		System.out.println("durationTimeFrame "+durationTimeFrame);
		int horizon = (int) (maxDuration/durationTimeFrame);
		System.out.println("horizon "+horizon);

		int totalChunks[] = findBiggestChunks(resources, workItems, durationFunction, durationTimeFrame);
		
		int totalNumberChunks = 0;
		
		for(int i = 0; i < totalChunks.length; i++) {
			totalNumberChunks += totalChunks[i];
		}
		
		int numberX = resources.length * totalNumberChunks * (horizon);
		System.out.println("numberX "+numberX);
		int numberD = resources.length * workItems.length * (horizon);
		System.out.println("numberD "+numberD);
		
		LpSolve lp;
		int Ncol, j, ret = 0;

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */
		Ncol = numberX + numberD;

		/* create space large enough for one row */
		int[] colno = new int[Ncol];
		double[] row = new double[Ncol];

		lp = LpSolve.makeLp(0, Ncol);
		if(lp.getLp() == 0)
			ret = 1; /* couldn't construct a new model... */

		if(ret == 0) {
			/* let us name our variables. Not required, but can be useful for debugging */

			WorkItemRecord currWorkItem = null;
			String currResource = null;
			
			System.out.println("set name x");
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(int w = 0; w < workItems.length; w++) {
					
					currWorkItem = workItems[w];
					
					for(int t = 0; t < horizon; t++) {
						
						int i = findPositionX(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, totalChunks);

						for(int c = 0; c < totalChunks[w]; c++) {	
							colno[i+c] = i+c+1;
							lp.setColName(i+c+1, currResource+"_"+getStringWorkItem(currWorkItem)+"_"+c+"_Time_"+t);
						
						}
							
					}
					
				}
				
			}

			System.out.println("set name d");
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(int w = 0; w < workItems.length; w++) {
					
					currWorkItem = workItems[w];
						
					for(int t = 0; t < horizon; t++) {
				
						int i = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, numberX);
						colno[i] = i+1;
						lp.setColName(i+1, currResource+"_"+getStringWorkItem(currWorkItem)+"_Time_"+t);
					
					}
							
				}
				
			}

			lp.setAddRowmode(true);  /* makes building the model faster if it is done rows by row */

			/* construct first constrain */
			System.out.println("first constrain");
			
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
				
				for(int w = 0; w < workItems.length; w++) {
					
					currWorkItem = workItems[w];
					
					int chunks = getNumberChunks(currResource, currWorkItem, durationFunction, durationTimeFrame);
						
					for(int t = 0; t <= (horizon - chunks); t++) {
						
						row = new double[Ncol]; 

						int dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, numberX);
						row[dPos] = -chunks;
						
						for(int c = 0; c < chunks; c++) {
							
							int xPos = findPositionX(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, totalChunks);
							row[xPos+c] = 1.0;
							
						}

						/* add the row to lpsolve */
						lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);
						
					}
					
				}
				
			}
			
			/* construct second constrain */	
			System.out.println("second constrain");
			for(int w = 0; w < workItems.length; w++) {
				
				currWorkItem = workItems[w];
				int chunks = totalChunks[w];
				
				for(int c = 0; c < chunks; c++) {
				
					row = new double[Ncol];
					
					for(int r = 0; r < resources.length; r++) {
				
						currResource = resources[r];
				
						for(int t = 0; t < horizon; t++) {
							
							int xPos = findPositionX(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, totalChunks);
							row[xPos+c] = 1;
							
						}
						
					}

					/* add the row to lpsolve */
					lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 1);
					
				}
				
			}
			
			/* construct third constrain */
			System.out.println("third constrain");
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
			
				for(int t = 0; t < horizon; t++) {
				
					row = new double[Ncol];
					
					for(int w = 0; w < workItems.length; w++) {
						
						currWorkItem = workItems[w];
						int chunks = totalChunks[w];
						
						for(int c = 0; c < chunks; c++) {
							
							int xPos = findPositionX(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, totalChunks);
							row[xPos+c] = 1;
							
						}
						
					}

					/* add the row to lpsolve */
					lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 1);
					
				}
				
			}
			
			/* costruct fourth constrain */
			System.out.println("fourth constrain");
			for(HashSet<WorkItemRecord> set : deferedChoices) {

				row = new double[Ncol];
				
				for(int r = 0; r < resources.length; r++) {
					
					currResource = resources[r];
					
					for(WorkItemRecord workItem : set) {
						
						int chunks = getNumberChunks(currResource, workItem, durationFunction, durationTimeFrame);
					
						for(int t = 0; t <= (horizon - chunks); t++) {
							
							int dPos = findPositionD(currResource, workItem, t, resources, workItems, durationFunction, horizon, numberX);
							row[dPos] = 1;
							
						}
						
					}
				
				}
				
				/* add the row to lpsolve */
				lp.addConstraintex(row.length, row, colno, LpSolve.EQ, 1);
					
			}
			
			/* construct fifth constrain */
			System.out.println("fifth constrain");
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
			
				for(int w = 0; w < workItems.length; w++) {
				
					row = new double[Ncol];
					
					currWorkItem = workItems[w];
//					int chunks = totalChunks[w];

					HashSet<WorkItemRecord> set = resourceAuthorization.get(currResource);
					int can = 0;
					if(set != null) {
						can = set.contains(currWorkItem)?1:0;
					}
					
					for(int t = 0; t < horizon; t++) {
						
						int dPos = findPositionD(currResource, currWorkItem, t, resources, workItems, durationFunction, horizon, numberX);
						row[dPos] = 1;
						
					}
						
					/* add the row to lpsolve */
					lp.addConstraintex(Ncol, row, colno, LpSolve.LE, can);
						
//					}
					
				}
				
			}
			
			/* construct sixth constrain */
			System.out.println("sixth constrain");
			for(int r = 0; r < resources.length; r++) {
				
				currResource = resources[r];
			
				for(int w = 0; w < workItemsExecuting.length; w++) {
				
					row = new double[Ncol];
					
					currWorkItem = workItemsExecuting[w];
						
					int dPos = findPositionD(currResource, currWorkItem, 0, resources, workItems, durationFunction, horizon, numberX);
					row[dPos] = 1;
					
					int use = currWorkItem.equals(resourceUtilization.get(currResource))?1:0;
						
					/* add the row to lpsolve */
					lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, use);
											
				}
				
			}
//
//			/* set X as binary */
//			System.out.println("sixth and seventh constrain");
//			for(int i = 1; i <= Ncol; i++) {
//				
//				lp.setBinary(i, true);
//
//			}

		}

		if(ret == 0) {
			
			System.out.println("objective function");
			lp.setAddRowmode(false); /* row mode should be turned off again when done building the model */

			row = new double[Ncol];
			
			for(int r = 0; r < resources.length; r++) {
				
				String resource = resources[r];
				
				for(int w = 0; w < workItems.length; w++) {
					
					WorkItemRecord workItem = workItems[w];
						
					for(int t = 0; t < horizon; t++) {
						
						int dPos = findPositionD(resource, workItem, t, resources, workItems, durationFunction, horizon, numberX);
						double risk = riskFunction.getRisk(resource, workItem, (t*durationTimeFrame)+baseTime);
												
						row[dPos] = (1-alpha) * risk;
						
						for(int c = 0; c < totalChunks[w]; c++) {
							
							int xPos = findPositionX(resource, workItem, t, resources, workItems, durationFunction, horizon, totalChunks);
							
							row[xPos+c] = alpha * (t+1);
							
						}
						
					}
					
				}
				
			}

			/* set the objective in lpsolve */
			lp.setObjFnex(Ncol, row, colno);

		}

		if(ret == 0) {
			/* set the object direction to maximize */
			lp.setMinim();
			
//			lp.setPresolve(LpSolve.PRESOLVED, lp.getPresolveloops());

			/* just out of curioucity, now generate the model in lp format in file model.lp */
			lp.writeLp("model.lp");

			/* I only want to see important messages on screen while solving */
			lp.setVerbose(LpSolve.DETAILED);
//			lp.setVerbose(LpSolve.IMPORTANT);
			
			lp.setPresolve(LpSolve.PRESOLVE_BOUNDS | LpSolve.PRESOLVE_COLDOMINATE | LpSolve.PRESOLVE_COLFIXDUAL | LpSolve.PRESOLVE_COLS | LpSolve.PRESOLVE_DUALS | LpSolve.PRESOLVE_ELIMEQ2 | 
					LpSolve.PRESOLVE_IMPLIEDFREE | LpSolve.PRESOLVE_IMPLIEDSLK | LpSolve.PRESOLVE_KNAPSACK | LpSolve.PRESOLVE_LINDEP | LpSolve.PRESOLVE_MERGEROWS | LpSolve.PRESOLVE_PROBEFIX |
					LpSolve.PRESOLVE_PROBEREDUCE | LpSolve.PRESOLVE_REDUCEGCD | LpSolve.PRESOLVE_REDUCEMIP | LpSolve.PRESOLVE_ROWDOMINATE | LpSolve.PRESOLVE_ROWS | LpSolve.PRESOLVE_SENSDUALS |
					LpSolve.PRESOLVE_SOS, 0);
			
			lp.setScaling(LpSolve.SCALE_EXTREME);
			int maxloops = lp.getPresolveloops();
			System.out.println("Max Loops: " + maxloops);
			
			System.out.println("pre" +lp.getPresolve());
			
//			lp.setPresolve(LpSolve.PRESOLVE_BOUNDS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_COLDOMINATE, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_COLFIXDUAL, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_COLS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_DUALS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_ELIMEQ2, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_IMPLIEDFREE, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_IMPLIEDSLK, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_KNAPSACK, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_LINDEP, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_MERGEROWS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_PROBEFIX, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_PROBEREDUCE, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_REDUCEGCD, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_REDUCEMIP, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_ROWDOMINATE, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_ROWS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_SENSDUALS, 0);
//			lp.setPresolve(LpSolve.PRESOLVE_SOS, 0);

			/* Now let lpsolve calculate a solution */
			ret = lp.solve();

			if(ret == LpSolve.OPTIMAL) optimal = true;
		}

		if(ret == 0) {
			/* a solution is calculated, now lets get some results */

			/* objective value */
			System.out.println("Objective value: " + lp.getObjective());

			/* variable values */
			lp.getVariables(row);
			for(j = 0; j < Ncol; j++) {

//				String res = (String) x[j].get(0);
//				WorkItemRecord workItem = (WorkItemRecord) x[j].get(1);
//				
//				if(row[j] == 1) {
//					result.put(res, workItem);
//				}
				
				if(row[j] == 1) {
					System.out.println(lp.getColName(j + 1) + ": " + row[j]);
				}
				
			}

			/* we are done now */
		}

		/* clean up such that all used memory by lpsolve is freed */
		if(lp.getLp() != 0)
			lp.deleteLp();

		return(optimal);
	}
	
	private int findPositionX(String resource, WorkItemRecord workItem, int timeFrame, String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long horizon, int[] totalChunks) {
		
		if(cacheX.isEmpty()) {
		
			int pos = 0;
			
			for(int r = 0; r < resources.length; r++) {
				
				String currResource = resources[r];
								
				for(int w = 0; w < workItems.length; w++) {
					
					WorkItemRecord currWorkItem = workItems[w];
					
					for(int t = 0; t < horizon; t++) {
						
						cacheX.put(currResource+"_"+getStringWorkItem(currWorkItem)+"_"+t, pos);
						
						for(int c = 0; c < totalChunks[w]; c++) {							
							
							pos++;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		if(cacheX.get(resource+"_"+getStringWorkItem(workItem)+"_"+timeFrame) == null) System.out.println(resource+"_"+getStringWorkItem(workItem)+"_"+timeFrame);
		
		return cacheX.get(resource+"_"+getStringWorkItem(workItem)+"_"+timeFrame);
		
	}
	
	private int findPositionD(String resource, WorkItemRecord workItem, int timeFrame, String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction, long horizon, int totalChunks) {
		
		int pos = totalChunks;
		
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
				chunksSize[i] = Math.max(chunksSize[i], getNumberChunks(resource, workItem, durationFunction, sizeTimeFrame));
				
			}
			
		}
			
		return chunksSize;
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

	private long findMaxDuration(String[] resources, WorkItemRecord[] workItems, DurationFunction durationFunction) {
		
		long max = 0;
		
		for(String resource : resources) {
			
			long duration = 0;
			
			for(WorkItemRecord workItem : workItems) {
				duration += durationFunction.getDuration(resource, workItem); 
			}
			
			max = Math.max(max, duration);
		}
		
		return max;
		
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

	public static void main(String[] args) {
		try {
			WorkItemRecord wir1 = new WorkItemRecord("1", "a", "uri", null, null);
			WorkItemRecord wir2 = new WorkItemRecord("1", "b", "uri", null, null);
			WorkItemRecord wir3 = new WorkItemRecord("1", "c", "uri", null, null);
			WorkItemRecord wir4 = new WorkItemRecord("1", "d", "uri", null, null);

			WorkItemRecord wir5 = new WorkItemRecord("1", "e", "uri", null, null);
			WorkItemRecord wir6 = new WorkItemRecord("1", "f", "uri", null, null);
			
			WorkItemRecord[] offered = new WorkItemRecord[] {wir1, wir2, wir3, wir4};
			WorkItemRecord[] executing = new WorkItemRecord[] {wir5, wir6};
			
			String res1 = "res1";
			String res2 = "res2";
			String res3 = "res3";
			String[] resources = new String[] {res1, res2, res3};
			
			HashMap<String, WorkItemRecord> use = new HashMap<String, WorkItemRecord>();
			
			use.put(res1, wir5);
			use.put(res2, wir6);
			
			HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
			
			HashSet<WorkItemRecord> set1 = new HashSet<WorkItemRecord>();
			set1.add(wir1);
			set1.add(wir2);
			set1.add(wir3);
			set1.add(wir4);
			
			HashSet<WorkItemRecord> set2 = new HashSet<WorkItemRecord>();
			set2.add(wir1);
			set2.add(wir2);
//			set2.add(wir3);
			set2.add(wir5);
			
			HashSet<WorkItemRecord> set3 = new HashSet<WorkItemRecord>();
			set3.add(wir1);
			set3.add(wir2);
//			set3.add(wir3);
			set3.add(wir6);
			
			can.put(res3, set1);
			can.put(res1, set2);
			can.put(res2, set3);

			HashMap<String, WorkItemRecord> result = new HashMap<String, WorkItemRecord>();
			
			HashSet<HashSet<WorkItemRecord>> choice = new HashSet<HashSet<WorkItemRecord>>();
			
			HashSet<WorkItemRecord> set = new HashSet<WorkItemRecord>();
			set.add(wir1);
			set.add(wir2);
			
			choice.add(set);
			
			set = new HashSet<WorkItemRecord>();
			set.add(wir3);
			set.add(wir4);
			
			choice.add(set);
			
			set = new HashSet<WorkItemRecord>();
			set.add(wir5);
			
			choice.add(set);
			
			set = new HashSet<WorkItemRecord>();
			set.add(wir6);
			
			choice.add(set);
			
			new LPBuilderOld().execute(0.5, resources, executing, offered, new DurationFunction(), use, can, new RiskFunction(), choice, result);
			
			System.out.println(result);
			
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
	}

}
