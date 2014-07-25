package Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import cern.colt.Arrays;
import Test.HPCmessage.Constrain;
import lpsolve.*;

public class LPSolveSolver {
	
	public static void main(String[] args) throws Exception {
		
		Socket clientSocket = null;
		try {

			String address = args[0];
			int timeout = Integer.parseInt(args[1]);		
			
			clientSocket = new Socket(address, 12345);
//			System.out.println("HPC accepted");
			
			while(true) {
				
				ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
//				ObjectInputStream reader = new ObjectInputStream(new FileInputStream("HPC.message"));
				HPCmessage message = (HPCmessage) reader.readObject();
				
				double[] solution = execute(message, timeout);
				message.setSolution(solution);
				
				ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
				writer.writeObject(message);

				System.gc();
				
			}
			
		}finally { 

			clientSocket.close();
			
		}
	}
	
	public static double[] execute(HPCmessage message, int timeout) {
			
		try {
			long max = message.getMax();
			if(max == 0) max+=1001;
			
			int rw = message.getRW();
			int rww = message.getRWW();
			int rw2s = message.getRW2S();
			int x = message.getX();
			
			System.out.println("rw: "+rw);
			System.out.println("rww: "+rww);
			System.out.println("rw2s: "+rw2s);
			System.out.println("x: "+x);
			
			int numberD = rw+rww+rw2s+x;
			
			LpSolve solver = LpSolve.makeLp(0, numberD);
			solver.setAddRowmode(true);  /* makes building the model faster if it is done rows by row */
	
			/* We will build the model row by row
	           So we start with creating a model with 0 rows and 2 columns */
	
			/* create space large enough for one row */
			String[] colno = message.getColno();
			double[] row = message.getRow();
	
			/* let us name our variables. Not required, but can be useful for debugging */
			ArrayList<Integer> con = null;
			ArrayList<Double> coe = null;
			
			System.out.println("set name variable");
			for(int i = rw; i < numberD; i++) {
				solver.setBinary(i, true);
			}
			
			/* construct first constrain */
			System.out.println("first constrain");	
	
			ArrayList<Constrain> con1 = message.getConstrain1();
			
	        double[] line = null;
			
			if(con1 != null && con1.size() > 0) {
				for(int i = 0; i < con1.size(); i++) {
									
					con = con1.get(i).getConstrain();
					coe = con1.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.EQ, con1.get(i).getMaxBoundaries());
					
				}
			}
			
			/* construct second constrain */
			System.out.println("second constrain");
	
			ArrayList<Constrain> con2a = message.getConstrain2a();
	
			ArrayList<Constrain> con2b = message.getConstrain2b();
			
			if(con2a != null && con2a.size() > 0) {
				for(int i = 0; i < con2a.size(); i++) {
					
					con = con2a.get(i).getConstrain();
					coe = con2a.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con2a.get(i).getMaxBoundaries());
												
				}
			}
			
			if(con2b != null && con2b.size() > 0) {
				for(int i = 0; i < con2b.size(); i++) {
					
					con = con2b.get(i).getConstrain();
					coe = con2b.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con2b.get(i).getMaxBoundaries());
					
				}
			}
			
			/* construct third constrain */
			System.out.println("third constrain");
			
			ArrayList<Constrain> con3a = message.getConstrain3a();
	
			ArrayList<Constrain> con3b = message.getConstrain3b();
			
			ArrayList<Constrain> con3c = message.getConstrain3c();
	
			ArrayList<Constrain> con3d = message.getConstrain3d();
			
			if(con3a != null && con3a.size() > 0) {
				for(int i = 0; i < con3a.size(); i++) {
					
					con = con3a.get(i).getConstrain();
					coe = con3a.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con3a.get(i).getMaxBoundaries());
					
				}
			}
			
			if(con3b != null && con3b.size() > 0) {
				for(int i = 0; i < con3b.size(); i++) {
	
					con = con3b.get(i).getConstrain();
					coe = con3b.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con3b.get(i).getMaxBoundaries());
					
				}
			}
			
			if(con3c != null && con3c.size() > 0) {
				for(int i = 0; i < con3c.size(); i++) {
	
					con = con3c.get(i).getConstrain();
					coe = con3c.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con3c.get(i).getMaxBoundaries());
					
				}
			}
			
			if(con3d != null && con3d.size() > 0) {
				for(int i = 0; i < con3d.size(); i++) {
	
					con = con3d.get(i).getConstrain();
					coe = con3d.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.LE, con3d.get(i).getMaxBoundaries());
					
				}
			}
			
			/* construct fourth constrain */
			System.out.println("fourth constrain");
	
			ArrayList<Constrain> con4 = message.getConstrain4();
			
			if(con4 != null && con4.size() > 0) {
				for(int i = 0; i < con4.size(); i++) {
	
					con = con4.get(i).getConstrain();
					coe = con4.get(i).getCoefficient();
	
			        line = new double[numberD];
					
					for(int j = 0; j < con.size() ; j++) {
						line[con.get(j)] = coe.get(j);
					}
					
					solver.addConstraint(line, LpSolve.EQ, con4.get(i).getMaxBoundaries());
					
				}
			}
			solver.setAddRowmode(false);
			
			/* construct objective function */
			System.out.println("Objective function");
			
			solver.setObjFn(row);
				
			int time = timeout;
			solver.setTimeout(time);
			
			/* set the objective in lpsolve */
	
			StringBuilder sb = new StringBuilder();
				
			int resultStatus = solver.solve();
				
		    sb.append("Problem solved in " + solver.timeElapsed() + " seconds\n");
		    String typeSolution = null;
		    if(resultStatus == LpSolve.OPTIMAL) typeSolution = "Optimal";
		    else if(resultStatus == LpSolve.SUBOPTIMAL) typeSolution = "Partial";
		    else if(resultStatus == LpSolve.DEGENERATE) typeSolution = "Abnormal";
		    else if(resultStatus == LpSolve.INFEASIBLE) typeSolution = "Infeasible";
		    else if(resultStatus == LpSolve.UNBOUNDED) typeSolution = "Unbounded";
		    
			sb.append("Found " + typeSolution + " solution with cost: " + solver.getObjective() + "\n");
			
			double[] solution = new double[row.length];
			solver.getVariables(solution);
			System.out.println(Arrays.toString(solution));
			
			System.out.println(sb);
					
			solver.deleteLp();
			
			return solution;
			
		}catch(LpSolveException e) {
			return null;
		}
			
	}	

}
