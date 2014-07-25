package org.yawlfoundation.yawl.riskMitigation.RunTest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedList;

import org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine.SimulatedAnnealingAlgorithm;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;


public class FirstTestRun {

	public static void main(String[] args) {
		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();
		
		StateYAWLProcess s = null;
		
		int count = 1;
		String path = "org.yawlfoundation.yawl.risk.state.YAWL.Loaders.Process";
					
		while(count<181) {
			int innerCount = 1;
			while(innerCount < 11) {
				try {
					saa.reset();
					Loader l = (Loader) Class.forName(path+count).newInstance();
					s = l.generateStatus(saa.r);
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
				
			
				long st = System.nanoTime();
				LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 1);
				long nt = System.nanoTime();
				String stringOut = "Model: " +count+ " - Time: " +(nt-st)/1 +" -";
				
				StateYAWLProcess[] array = saa.bestSolutions.toArray(new StateYAWLProcess[0]);
				
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

				try{
					FileWriter fstream = new FileWriter("FirstTest.txt", true);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(stringOut);
					out.close();
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
			
				innerCount++;
			}
			count++;		
		}
	}
	
}
