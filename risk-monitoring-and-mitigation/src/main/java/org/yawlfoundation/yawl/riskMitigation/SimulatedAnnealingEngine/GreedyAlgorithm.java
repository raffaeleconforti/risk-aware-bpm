package org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessDiscover;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessNeighbourCreator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class GreedyAlgorithm {

	int needFound = 50;
	int lim = 50;
	StateYAWLProcess s = null;
	Double[] e = null;
	StateYAWLProcess sbest = null;
	Double[] ebest = null;
	int iterations = 1;
	static long seed = new Long("1317166250339");
	public static Random r = new Random(seed);
	AtomicInteger solutions = new AtomicInteger(1);
	boolean perfectSolutions = false;
	AtomicBoolean stop = new AtomicBoolean(false);
	AtomicInteger found = new AtomicInteger(0);
	Semaphore sm = new Semaphore(1);
	
	public void reset() {
		StateYAWLProcess.idGeneral = 0;
		lim = 50;
		s = null;
		e = null;
		sbest = null;
		ebest = null;
		iterations = 1;	
		seed = new Long("1317166250339");
		r = new Random(seed);
		stop.set(false);
		found.set(0);
		solutions.set(1);
	}
	
	
	public boolean dominates(StateYAWLProcess a, StateYAWLProcess b) {
		boolean res = false;
		
		Double[] energyA = a.calculateEnergy();
		Double[] energyB = b.calculateEnergy();
		
		for(int i=0; i<energyA.length; i++) {
			if(energyA[i] != null && energyB[i] != null) {
				if(energyA[i] > energyB[i]) return false;
				
				if(energyA[i] < energyB[i]) res = true;
			}
			
		}
		
		return res;
	}
	
	
	public int paretoDominance(StateYAWLProcess x, LinkedList<StateYAWLProcess> paretoFront) {
		int res = 0;
		
		for(StateYAWLProcess a : paretoFront) {
			if(dominates(a, x)) res ++;
		}
		
		return res;
	}

	public double energyDifference(StateYAWLProcess x, StateYAWLProcess y, LinkedList<StateYAWLProcess> paretoFront) {
		@SuppressWarnings("unchecked")
		LinkedList<StateYAWLProcess> paretoFrontNew = (LinkedList<StateYAWLProcess>) paretoFront.clone();
		
		if(!paretoFront.contains(x)) paretoFrontNew.add(x);
		if(!paretoFront.contains(y)) paretoFrontNew.add(y);
		
		double sizeFTilde = paretoFrontNew.size();
		
		double sizeFTildeX = paretoDominance(x, paretoFrontNew);
		
		double sizeFTildeY = paretoDominance(y, paretoFrontNew);
		
		return (sizeFTildeY - sizeFTildeX) / sizeFTilde;
	}

    public StateYAWLProcess simulate(StateYAWLProcess initialState, int maxIterations, double initialTemperature, TemperatureCalculator temperatureCalculator, long time, int sol) {
        YStateProcessDiscover.getInstance();
        YStateProcessNeighbourCreator.getInstance();
        s = initialState;
        e = s.calculateEnergy();
        sbest = initialState;
        ebest = e;
        Timer t = new Timer(time);
        t.start();
        needFound = sol;
        while(!stop.get() && found.get() < needFound) {
            int possibleOperation = NeighbourGenerator.numberAtomicOperation(s);
            boolean changed = false;
            for(int i = 0; i <= possibleOperation; i++) {
                StateYAWLProcess sNew = NeighbourGenerator.generateNeighbourStateNonRandom(s, i);
                Double[] eNew = sNew.calculateEnergy();
                double a = 0.0;
                double b = 0.0;
                for(int j = 0; j < eNew.length-1; j+=2) {
                    a += (eNew[j] != null)?eNew[j]:0.0;
                    b += (ebest[j] != null)?ebest[j]:0.0;
                }
                if(a < b || (a == b && eNew[eNew.length-1] < ebest[ebest.length-1])) {
                    sbest = sNew;
                    ebest = eNew;
                    changed = true;
                }
            }
            if(changed) {
                s = sbest;
                e = ebest;
            }else {
                break;
            }
        }
        t.wakeup();
        return s;
    }


	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		YStateProcessNeighbourCreator.getInstance();
		YStateProcessDiscover.getInstance();
		
		GreedyAlgorithm saa = new GreedyAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();
		
		StateYAWLProcess s = null;
		
//		int count = 128;
//		String path = "org.yawlfoundation.yawl.risk.state.YAWL.Loaders.Process";
		String path = "org.yawlfoundation.yawl.risk.state.YAWL.LoadingFile.";
		
//		String file = "Redo";
//		try{
//			FileInputStream fstream1 = new FileInputStream(file);
//			DataInputStream in1 = new DataInputStream(fstream1);
//			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
//			String val = null;
//			while ((val = br1.readLine()) != null){
//				count = new Integer(val);
			
//		while(count<181) {
			int innerCount = 1;
			while(innerCount < 2) {
				try {
					saa.reset();
//					Loader l = (Loader) Class.forName(path+count).newInstance();
					Loader l = (Loader) Class.forName(path+"Payment").newInstance();
					s = l.generateStatus(r);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		//		System.out.println("Start "+Arrays.toString(s.calculateEnergy()));
			
				long st = System.nanoTime();
				StateYAWLProcess set = saa.simulate(s, (s.getTasks().length-s.getTaskRelevant().size()), 100, tc, 60000, 50);
//				LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 50);
				long nt = System.nanoTime();
				String stringOut = "Model: - Time: " +(nt-st)/1000000 +" -";
//				String stringOut = "Model: " +count+ " - Time: " +(nt-st)/1 +" -";



				Double[] energy = set.calculateEnergy();
						
				stringOut += " "+Arrays.toString(energy);
						
				stringOut += set.modifications;

				stringOut += " - total model generated: "+s.idGeneral+"\n";
				System.out.println(stringOut);
//				try{
//					FileWriter fstream = new FileWriter("Test8.txt", true);
//					BufferedWriter out = new BufferedWriter(fstream);
//					out.write(stringOut);
//					out.close();
//				}catch (Exception e){//Catch exception if any
//					System.err.println("Error: " + e.getMessage());
//				}
			
				innerCount++;
		}
	}
	
	class Timer extends Thread{
		long time;
		public Timer(long time){
			super();
			this.time = time;
		}
		
		public void wakeup() {
			this.interrupt();
			stop.set(true);
		}

		@Override
		public void run() {
			try {
				sleep(time);
				stop.set(true);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
}