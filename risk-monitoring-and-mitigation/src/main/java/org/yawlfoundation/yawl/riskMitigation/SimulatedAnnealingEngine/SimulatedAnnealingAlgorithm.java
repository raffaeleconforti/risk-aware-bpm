package org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessDiscover;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.YStateProcessNeighbourCreator;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;



public class SimulatedAnnealingAlgorithm {

	int needFound = 50;
	int lim = 50;
	StateYAWLProcess s = null;
	Double[] e = null;
	StateYAWLProcess sbest = null;
	Double[] ebest = null;
	double temperature = 0.0;
	int iterations = 1;
	static long seed = new Long("1317166250339");
	public static Random r = new Random(seed);
	AtomicInteger solutions = new AtomicInteger(1);
	boolean perfectSolutions = false;
	AtomicBoolean stop = new AtomicBoolean(false);
	AtomicInteger found = new AtomicInteger(0);
	LinkedList<StateYAWLProcess> paretoFront = new LinkedList<StateYAWLProcess>();
	LinkedList<StateYAWLProcess> newParetoFront = new LinkedList<StateYAWLProcess>();
	Semaphore sm = new Semaphore(1);
	public static HashSet<StateYAWLProcess> bestSolutions = new HashSet<StateYAWLProcess>();
	
	public void reset() {
		StateYAWLProcess.idGeneral = 0;
		lim = 50;
		s = null;
		e = null;
		sbest = null;
		ebest = null;
		temperature = 0.0;
		iterations = 1;	
		seed = new Long("1317166250339");
		r = new Random(seed);
		stop.set(false);
		found.set(0);
		solutions.set(1);
		paretoFront = new LinkedList<StateYAWLProcess>();
		newParetoFront = null;
//		bestSolutions = new HashSet<StateYAWLProcess>();
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
	
	public LinkedList<StateYAWLProcess> simulate(StateYAWLProcess initialState, int maxIterations, double initialTemperature, TemperatureCalculator temperatureCalculator, long time, int sol) {
		YStateProcessDiscover.getInstance();
		YStateProcessNeighbourCreator.getInstance();
		s = initialState;
		e = s.calculateEnergy();
		sbest = initialState;
		ebest = e;
		temperature = initialTemperature;
		paretoFront.add(initialState);
		Timer t = new Timer(time);
		t.start();
		needFound = sol;
		while(!stop.get() && found.get() < needFound) {
			int i = (int) ((paretoFront.size())*r.nextDouble());
			if(i==paretoFront.size()) i--;
			s = paretoFront.get(i);
			iterations = 0;
			while(iterations<maxIterations && (!stop.get() && found.get() < needFound)) {
				StateYAWLProcess sNew = NeighbourGenerator.generateNeighbourState(s);
				double dE = energyDifference(s, sNew, paretoFront);
				double rand = r.nextDouble();
				temperature = temperatureCalculator.updateTemperature(initialTemperature, iterations, maxIterations);
				double value = Math.min(1, Math.exp(-dE/temperature));
				if(rand<value) {
					s = sNew;
					
					int numberDominantingElements = paretoDominance(s, paretoFront);
					
					if(numberDominantingElements == 0) {
						Iterator<StateYAWLProcess> it = paretoFront.iterator();
						while(it.hasNext()) {
							if(dominates(s, it.next())) it.remove();
						}
						
						if(!paretoFront.contains(s)) paretoFront.add(s);
					}
				}
				iterations++;
			}
		}
		paretoFront.remove(initialState);
		t.wakeup();
		return paretoFront;
	}


	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		YStateProcessNeighbourCreator.getInstance();
		YStateProcessDiscover.getInstance();
		
		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
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
				LinkedList<StateYAWLProcess> set = saa.simulate(s, (s.getTasks().length-s.getTaskRelevant().size()), 100, tc, 60000, 50);
//				LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 50);
				long nt = System.nanoTime();
				String stringOut = "Model: - Time: " +(nt-st)/1000000 +" -";
//				String stringOut = "Model: " +count+ " - Time: " +(nt-st)/1 +" -";
				
				StateYAWLProcess[] array = set.toArray(new StateYAWLProcess[0]);
				
				Arrays.sort(array, new Comparator<StateYAWLProcess>() {
		
					@Override
					public int compare(StateYAWLProcess o1, StateYAWLProcess o2) {
						StateYAWLProcess s1 =  o1;
						StateYAWLProcess s2 =  o2;
						Double som1 = 0.0;
						Double som2 = 0.0;
						Double[] eng1 = s1.calculateEnergy();
						Double[] eng2 = s2.calculateEnergy();
						for(int i=0; i<eng1.length-2; i+=2) {
							som1 += (eng1[i]!=null)?eng1[i]:0.0;
							som2 += (eng2[i]!=null)?eng2[i]:0.0;
						}
						if(som1.equals(som2)) {
							som1 = eng1[eng1.length-1];
							som2 = eng2[eng2.length-1];
						}
						return -som1.compareTo(som2);
					}
				});
				
				array = set.toArray(new StateYAWLProcess[0]);
				Arrays.sort(array, new Comparator<StateYAWLProcess>() {
		
					@Override
					public int compare(StateYAWLProcess o1, StateYAWLProcess o2) {
						StateYAWLProcess s1 =  o1;
						StateYAWLProcess s2 =  o2;
						Double som1 = 0.0;
						Double som2 = 0.0;
						Double[] eng1 = s1.calculateEnergy();
						Double[] eng2 = s2.calculateEnergy();
						for(int i=0; i<eng1.length-1; i+=2) {
							som1 += (eng1[i]!=null)?eng1[i]:0.0;
							som2 += (eng2[i]!=null)?eng2[i]:0.0;
						}
						if(som1.equals(som2)) {
							som1 = eng1[eng1.length-1];
							som2 = eng2[eng2.length-1];
						}
						return som1.compareTo(som2);
					}
				});
				
				boolean reach = false;
				for(int i = 0; i<array.length; i++){
					if(array[i] != null) {
						StateYAWLProcess s1 = array[i];

						Double[] energy = s1.calculateEnergy();
						
						stringOut += " "+Arrays.toString(energy);
						
						stringOut += s1.modifications;
					}
				}
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