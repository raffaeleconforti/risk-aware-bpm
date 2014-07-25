package Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.processmining.framework.util.ArrayUtils;

import Test.HPCmessage.Constrain;
import gurobi.*;

public class GurobiSolver {
		
	public static void main(String[] args) throws Exception {
		
		Socket clientSocket = null;
		
		try {
		
			String address = args[0];
			int timeout = Integer.parseInt(args[1]);			
			int threads = Integer.parseInt(args[2]);
			
			clientSocket = new Socket(address, 12345);
//			System.out.println("HPC accepted");
			
			while(true) {
				
				ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
//				ObjectInputStream reader = new ObjectInputStream(new FileInputStream("HPC.message"));
				HPCmessage message = (HPCmessage) reader.readObject();
//				reader.close();
				
				double[] solution = execute(message, timeout, threads);
				message.setSolution(solution);
				
				ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
				writer.writeObject(message);
//				writer.close();

				System.gc();
				
			}
			
		}finally { 

			clientSocket.close();
			
		}
	}
	
	public static double[] execute(HPCmessage message, int timeout, int threads) {
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
			
			GRBEnv env = new GRBEnv("mip1.log");
			GRBModel model = new GRBModel(env);
			
			/* We will build the model row by row
	           So we start with creating a model with 0 rows and 2 columns */
	
			/* create space large enough for one row */
			double[] row = message.getRow();
	
			/* let us name our variables. Not required, but can be useful for debugging */
	
			GRBVar[] arrayRW = model.addVars(rw, GRB.CONTINUOUS);
			GRBVar[] arrayRWW = model.addVars(rww, GRB.BINARY);
			GRBVar[] arrayRW2S = model.addVars(rw2s, GRB.BINARY);
			GRBVar[] arrayX = model.addVars(x, GRB.BINARY);
			
			GRBVar[] d = null;
			if(rw > 0) {
				d = arrayRW;
			}
			if(rww > 0) {
				d = ArrayUtils.concatAll(d, arrayRWW);
			}
			if(rw2s > 0) {
				d = ArrayUtils.concatAll(d, arrayRW2S);
			}
			if(x > 0) {
				d = ArrayUtils.concatAll(d, arrayX);
			}
			
			model.update();

			GRBLinExpr ct = null;
			
			for(int i = 0; i < d.length ; i++) {
				ct = new GRBLinExpr();
				ct.addTerm(1.0, d[i]);
				model.addConstr(ct, GRB.LESS_EQUAL, max, "var_"+i);
			}
			
			ArrayList<Integer> con = null;
			ArrayList<Double> coe = null;
			
			System.out.println("set name variable");
			
			/* construct first constrain */
			System.out.println("first constrain");	
	
			ArrayList<Constrain> con1 = message.getConstrain1();
			
			if(con1 != null && con1.size() > 0) {
				for(int i = 0; i < con1.size(); i++) {
					
					con = con1.get(i).getConstrain();
					coe = con1.get(i).getCoefficient();
					
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.EQUAL, 1.0, "cons1_"+i);
					
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
					
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con2a.get(i).getMaxBoundaries(), "cons2a_"+i);
					
				}
			}
			
			if(con2b != null && con2b.size() > 0) {
				for(int i = 0; i < con2b.size(); i++) {
					
					con = con2b.get(i).getConstrain();
					coe = con2b.get(i).getCoefficient();
					
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con2b.get(i).getMaxBoundaries(), "cons2b_"+i);
					
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
	
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con3a.get(i).getMaxBoundaries(), "cons3a_"+i);
					
				}
			}
			
			if(con3b != null && con3b.size() > 0) {
				for(int i = 0; i < con3b.size(); i++) {
					
					con = con3b.get(i).getConstrain();
					coe = con3b.get(i).getCoefficient();
	
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con3b.get(i).getMaxBoundaries(), "cons3b_"+i);
					
				}
			}
			
			if(con3c != null && con3c.size() > 0) {
				for(int i = 0; i < con3c.size(); i++) {
					
					con = con3c.get(i).getConstrain();
					coe = con3c.get(i).getCoefficient();
	
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con3c.get(i).getMaxBoundaries(), "cons3c_"+i);
					
				}
			}
			
			if(con3d != null && con3d.size() > 0) {
				for(int i = 0; i < con3d.size(); i++) {
					
					con = con3d.get(i).getConstrain();
					coe = con3d.get(i).getCoefficient();
	
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.LESS_EQUAL, con3d.get(i).getMaxBoundaries(), "cons3d_"+i);
					
				}
			}
			
			/* construct fourth constrain */
			System.out.println("fourth constrain");
	
			ArrayList<Constrain> con4 = message.getConstrain4();
			
			if(con4 != null && con4.size() > 0) {
				for(int i = 0; i < con4.size(); i++) {
					
					con = con4.get(i).getConstrain();
					coe = con4.get(i).getCoefficient();
	
					ct = new GRBLinExpr();
					
					for(int j = 0; j < con.size() ; j++) {
						ct.addTerm(coe.get(j), d[con.get(j)]);
					}
					
					model.addConstr(ct, GRB.EQUAL, con4.get(i).getMaxBoundaries(), "cons4_"+i);
					
				}
			}
	
			/* construct objective function */
			System.out.println("Objective function");
			ct = new GRBLinExpr();
			for(int i = 0; i < d.length ; i++) {
				ct.addTerm(row[i], d[i]);
			}
			model.setObjective(ct);
			
			//TODO SEND d AND row
	
			model.getEnv().set(GRB.DoubleParam.TimeLimit, timeout);
			model.getEnv().set(GRB.IntParam.Threads, threads);
			model.getEnv().set(GRB.IntParam.ConcurrentMIP, threads);
			model.getEnv().set(GRB.DoubleParam.IntFeasTol, 1e-7);
			model.getEnv().set(GRB.DoubleParam.FeasibilityTol, 1e-7);
			
			//			solver.solve();
			
			/* set the objective in lpsolve */
				
			model.optimize();
			
			StringBuilder sb = new StringBuilder();
				
		    sb.append("Problem solved in " +model.get(GRB.DoubleAttr.Runtime)+ " seconds\n");
		    
			sb.append("Found " +model.get(GRB.IntAttr.Status)+ " solution with cost: " + model.get(GRB.DoubleAttr.ObjVal) + " and GAP " + model.get(GRB.DoubleAttr.MIPGap) + "\n");
			
			double[] solution = new double[d.length];
			
			for(int i = 0; i < rw ; i++) {
	
				solution[i] = d[i].get(GRB.DoubleAttr.X);
				
			}
			
			for(int i = (rw+rw2s+rww); i < d.length ; i++) {
				
				solution[i] = d[i].get(GRB.DoubleAttr.X);
				
			}
			
			FileWriter fw = new FileWriter("SimulationTime", true);
			fw.write(sb.toString());
			fw.close();
			
			model.dispose();
			env.dispose();
			
			return solution;
		
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
//			return resultStatus;
			
	}
	
}
