package org.yawlfoundation.yawl.riskMitigation.RunTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine.SimulatedAnnealingAlgorithm;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;



public class SecondTestRun {

	public static void main(String[] args) {
		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();
		
		StateYAWLProcess s = null;
		
		String path = "org.yawlfoundation.yawl.risk.state.YAWL.Loaders.";
		
		int innerCount = 1;
		while(innerCount < 4) {
			try {
				saa.reset();
				Loader l = (Loader) Class.forName(path+"Payment").newInstance();
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
			LinkedList<StateYAWLProcess> set = saa.simulate(s, (s.getTasks().length-s.getTaskRelevant().size()), 100, tc, 60000, 50);
			long nt = System.nanoTime();
			String stringOut = "Model: - Time: " +(nt-st)/1000000 +" -";
			
			StateYAWLProcess[] array = set.toArray(new StateYAWLProcess[0]);
			Arrays.sort(array, new Comparator<StateYAWLProcess>() {
	
				@Override
				public int compare(StateYAWLProcess o1, StateYAWLProcess o2) {
					StateYAWLProcess s1 = o1;
					StateYAWLProcess s2 = o2;
					Double som1 = 0.0;
					Double som2 = 0.0;
					Double[] eng1 = s1.calculateEnergy();
					Double[] eng2 = s2.calculateEnergy();
					for(int i=0; i<eng1.length-1; i+=2) {
						som1 += eng1[i];
						som2 += eng2[i];
					}
					if(som1.equals(som2)) {
						som1 = eng1[eng1.length-1];
						som2 = eng2[eng2.length-1];
					}
					return som1.compareTo(som2);
				}
			});
			
			for(int i = 0; i<array.length; i++) {
				StateYAWLProcess s1 = array[i];
				if(set.contains(s1)) {
					LinkedList<String> l1 = s1.modifications;
					for(int j = i+1; j<array.length; j++) {
						StateYAWLProcess s2 = array[j];
						if(s1!=s2) {
							if(set.contains(s2)) {
								int a = 0;
								int b = 0;
								boolean same = true;
								Double[] engA = s1.calculateEnergy();
								Double[] engB = s2.calculateEnergy();
								for(int h=0; h<engA.length; h++) {
									if(engA[h] > engB[h]) {
										b++;
										same = false; 
									}else if(engA[h] < engB[h]) {
										a++;
										same = false;
									}
								}
								if(same) {
									LinkedList<String> l2 = s2.modifications;
									if(l1.size()==l2.size()) {
										boolean equals = true;
										String str1 = null;
										String str2 = null;
										for(int z=0; z<l1.size(); z++) {
											str1 = l1.get(z);
											if(!l2.contains(str1)) {
												equals = false;
												break;
											}
										}
										for(int z=0; z<l2.size(); z++) {
											str2 = l2.get(z);
											if(!l1.contains(str2)) {
												equals = false;
												break;
											}
										}
										if(equals) {
											System.out.println(Arrays.toString(s2.calculateEnergy()));
											set.remove(s2);
										}
									}
								}else if(!same){
									if(a>0 && b==0) {
										System.out.println(Arrays.toString(s2.calculateEnergy()));
										set.remove(s2);	
									}else if(a==0 && b>0) {
										System.out.println(Arrays.toString(s1.calculateEnergy()));
										set.remove(s1);
									}
								}
							}
						}
					}
				}
			}
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
						som1 += eng1[i];
						som2 += eng2[i];
					}
					if(som1.equals(som2)) {
						som1 = eng1[eng1.length-1];
						som2 = eng2[eng2.length-1];
					}
					return som1.compareTo(som2);
				}
			});
			
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
//			try{
//				FileWriter fstream = new FileWriter("SecondTest.txt", true);
//				BufferedWriter out = new BufferedWriter(fstream);
//				out.write(stringOut);
//				out.close();
//			}catch (Exception e){//Catch exception if any
//				System.err.println("Error: " + e.getMessage());
//			}
		
			innerCount++;
		}
	}
	
}
