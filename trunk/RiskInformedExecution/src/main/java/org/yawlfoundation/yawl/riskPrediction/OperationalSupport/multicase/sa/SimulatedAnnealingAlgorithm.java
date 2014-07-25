package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.sa;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;



public class SimulatedAnnealingAlgorithm {

	int needFound = 50;
	int lim = 50;
	Solution s = null;
	Double[] e = null;
	Solution sbest = null;
	Double[] ebest = null;
	double temperature = 0.0;
	int iterations = 1;
	static long seed = new Long("1317166250339");
	public static Random r = new Random(seed);
	AtomicInteger solutions = new AtomicInteger(1);
	boolean perfectSolutions = false;
	AtomicBoolean stop = new AtomicBoolean(false);
	AtomicInteger found = new AtomicInteger(0);
	LinkedList<Solution> paretoFront = new LinkedList<Solution>();
	LinkedList<Solution> newParetoFront = new LinkedList<Solution>();
	Semaphore sm = new Semaphore(1);
	public static HashSet<Solution> bestSolutions = new HashSet<Solution>();
	
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
		paretoFront = new LinkedList<Solution>();
		newParetoFront = null;
//		bestSolutions = new HashSet<StateYAWLProcess>();
	}
	
	
	public boolean dominates(Solution a, Solution b) {
		boolean res = false;
		
		Double[] energyA = a.evaluate();
		Double[] energyB = b.evaluate();
		
		for(int i=0; i<energyA.length; i++) {
			if(energyA[i] != null && energyB[i] != null) {
				if(energyA[i] > energyB[i]) return false;
				
				if(energyA[i] < energyB[i]) res = true;
			}
			
		}
		
		return res;
	}
	
	
	public int paretoDominance(Solution x, LinkedList<Solution> paretoFront) {
		int res = 0;
		
		for(Solution a : paretoFront) {
			if(dominates(a, x)) res ++;
		}
		
		return res;
	}
	
	
	public double energyDifference(Solution x, Solution y, LinkedList<Solution> paretoFront) {
		@SuppressWarnings("unchecked")
		LinkedList<Solution> paretoFrontNew = (LinkedList<Solution>) paretoFront.clone();
		
		if(!paretoFront.contains(x)) paretoFrontNew.add(x);
		if(!paretoFront.contains(y)) paretoFrontNew.add(y);
		
		double sizeFTilde = paretoFrontNew.size();
		
		double sizeFTildeX = paretoDominance(x, paretoFrontNew);
		
		double sizeFTildeY = paretoDominance(y, paretoFrontNew);
		
		return (sizeFTildeY - sizeFTildeX) / sizeFTilde;
	}
	
	public LinkedList<Solution> simulate(Solution initialState, int maxIterations, double initialTemperature, TemperatureCalculator temperatureCalculator, long time, int sol) {
		s = initialState;
		e = s.evaluate();
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
				Solution sNew = s.generateSolution();
				double dE = energyDifference(s, sNew, paretoFront);
				double rand = r.nextDouble();
				temperature = temperatureCalculator.updateTemperature(initialTemperature, iterations, maxIterations);
				double value = Math.min(1, Math.exp(-dE/temperature));
				if(rand<value) {
					s = sNew;
					
					int numberDominantingElements = paretoDominance(s, paretoFront);
					
					if(numberDominantingElements == 0) {
						Iterator<Solution> it = paretoFront.iterator();
						while(it.hasNext()) {
							if(dominates(s, it.next())) it.remove();
						}
						
						if(!paretoFront.contains(s)) {
							paretoFront.add(s);
							System.out.println("Add");
						}
					}
				}
				iterations++;
			}
		}
//		paretoFront.remove(initialState);
		t.wakeup();
		return paretoFront;
	}

	@SuppressWarnings("static-access")
	
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