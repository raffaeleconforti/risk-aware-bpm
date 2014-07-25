package Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.processmining.framework.util.ArrayUtils;

import Test.HPCmessage.Constrain;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class SCIPSolver {
	
	static {
		System.loadLibrary("jnilinearsolver");
	}
	
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
		
		MPSolver solver = new MPSolver("Scheduling", MPSolver.SCIP_MIXED_INTEGER_PROGRAMMING);

		/* We will build the model row by row
           So we start with creating a model with 0 rows and 2 columns */

		/* create space large enough for one row */
		double[] row = message.getRow();

		/* let us name our variables. Not required, but can be useful for debugging */

		MPVariable[] arrayRW = solver.makeNumVarArray(rw, 0, max);
		MPVariable[] arrayRWW = solver.makeBoolVarArray(rww);
		MPVariable[] arrayRW2S = solver.makeBoolVarArray(rw2s);
		MPVariable[] arrayX = solver.makeBoolVarArray(x);
		
		MPVariable[] d = ArrayUtils.concatAll(arrayRW, arrayRWW, arrayRW2S, arrayX);
		
		MPConstraint ct = null;
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
				
				ct = solver.makeConstraint(con1.get(i).getMinBoundaries(), con1.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
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
				
				ct = solver.makeConstraint(con2a.get(i).getMinBoundaries(), con2a.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}
		
		if(con2b != null && con2b.size() > 0) {
			for(int i = 0; i < con2b.size(); i++) {
				
				con = con2b.get(i).getConstrain();
				coe = con2b.get(i).getCoefficient();
				
				ct = solver.makeConstraint(con2b.get(i).getMinBoundaries(), con2b.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
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
				
				ct = solver.makeConstraint(con3a.get(i).getMinBoundaries(), con3a.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}
		
		if(con3b != null && con3b.size() > 0) {
			for(int i = 0; i < con3b.size(); i++) {
				
				con = con3b.get(i).getConstrain();
				coe = con3b.get(i).getCoefficient();
				
				ct = solver.makeConstraint(con3b.get(i).getMinBoundaries(), con3b.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}
		
		if(con3c != null && con3c.size() > 0) {
			for(int i = 0; i < con3c.size(); i++) {
				
				con = con3c.get(i).getConstrain();
				coe = con3c.get(i).getCoefficient();
				
				ct = solver.makeConstraint(con3c.get(i).getMinBoundaries(), con3c.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}
		
		if(con3d != null && con3d.size() > 0) {
			for(int i = 0; i < con3d.size(); i++) {
				
				con = con3d.get(i).getConstrain();
				coe = con3d.get(i).getCoefficient();
				
				ct = solver.makeConstraint(con3d.get(i).getMinBoundaries(), con3d.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}
		
		/* construct fourth constrain */
		System.out.println("fourth constrain");

		ArrayList<Constrain> con4 = message.getConstrain4();
		
		if(con4 != null && con4.size() > 0) {
			for(int i = 0; i < con4.size(); i++) {
				
				con = con4.get(i).getConstrain();
				coe = con4.get(i).getCoefficient();
				
				ct = solver.makeConstraint(con4.get(i).getMinBoundaries(), con4.get(i).getMaxBoundaries());
				for(int j = 0; j < con.size() ; j++) {
					ct.setCoefficient(d[con.get(j)], coe.get(j));
				}
				
			}
		}

		/* construct objective function */
		System.out.println("Objective function");
		
		for(int i = 0; i < d.length ; i++) {
			solver.setObjectiveCoefficient(d[i], row[i]);
		}
		
		//TODO SEND d AND row

		int time = timeout;
		solver.setTimeLimit(time);
		
		/* set the objective in lpsolve */

		StringBuilder sb = new StringBuilder();
			
		int resultStatus = solver.solve();
			
	    sb.append("Problem solved in " + solver.wallTime() + " milliseconds\n");
	    String typeSolution = null;
	    if(resultStatus == MPSolver.OPTIMAL) typeSolution = "Optimal";
	    else if(resultStatus == 1) typeSolution = "Partial";
	    else if(resultStatus == MPSolver.ABNORMAL) typeSolution = "Abnormal";
	    else if(resultStatus == MPSolver.INFEASIBLE) typeSolution = "Infeasible";
	    else if(resultStatus == MPSolver.UNBOUNDED) typeSolution = "Unbounded";
	    
		sb.append("Found " + typeSolution + " solution with cost: " + solver.objectiveValue() + "\n");
		
		double[] solution = new double[d.length];
		
		for(int i = 0; i < d.length ; i++) {

			solution[i] = d[i].solutionValue();
			
		}
		
		System.out.println(sb);
				
		solver.reset();
		solver.clear();
		solver.delete();
		
		return solution;
		
	}	

}
