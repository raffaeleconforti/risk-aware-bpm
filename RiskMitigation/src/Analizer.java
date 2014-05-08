import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class Analizer {
	
	static int models = 0;
	static int minModels = Integer.MAX_VALUE;
	static int maxModels = 0;
	static int some = 0;
	static int partial = 0;
	static int zero = 0;
	static int semiPerfect1 = 0;
	static int semiPerfect2 = 0;
	static int semiPerfect3 = 0;
	static int semiPerfect4 = 0;
	static int semiPerfect5 = 0;
	static int semiPerfect6 = 0;
	static int semiPerfect7 = 0;
	static int complete = 0;
	static String modelComplete = "";
	static String problematic = "";
	static HashSet<String> problematicSet = new HashSet<String>();
	
	static int risk1 = 0;
	static int risk2 = 0;
	static int risk3 = 0;
	static int risk4 = 0;
	static int risk5 = 0;
	static int risk6 = 0;
	static int risk7 = 0;
	static int risk8 = 0;
	
	static int[] riskArray1 = new int[4];
	static int[] riskArray2 = new int[4];
	static int[] riskArray3 = new int[4];
	static int[] riskArray4 = new int[4];
	static int[] riskArray5 = new int[4];
	static int[] riskArray6 = new int[4];
	static int[] riskArray7 = new int[4];
	static int[] riskArray8 = new int[4];

	public static void main(String[] args) {
		
		for(int i=1; i<181; i++) {
			System.out.println(task(i));
		}
		
//		riskProcess(8);
//		riskProcess(5);
//		riskProcess(4);
//		riskProcess(6);
//		int line = 0;
//		String output = "";
//		String file = "Test2.txt";
//		String[] s = new String[10];
//		try{
//			FileInputStream fstream = new FileInputStream(file);
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			int count = 0;
//			while ((s[count] = br.readLine()) != null){
//				count++;
//				line++;
//				while (count < 10) {
//					s[count] = br.readLine();
//					count++;
//					line++;
//				}
//				System.out.println(s[0]);
//				String x = analyze(s);
//				if(x != null) output += x+"\n";
////				if(!analyze(s).isEmpty()) {
////					output += analyze(s)+"\n";
////				}
//				count = 0;
//			}
//			in.close();
//		}catch (Exception e){//Catch exception if any
//			e.printStackTrace(); 
//		}
//		
//		System.out.println(output);
//		System.out.println(minModels +" - "+ maxModels +" - "+ models/line +" - "+ some +" - "+ partial +" - "+ zero +" - "+ semiPerfect1+" - "+ semiPerfect2+" - "+ semiPerfect3+" - "+ semiPerfect4+" - "+ semiPerfect5+" - "+ semiPerfect6+" - "+ semiPerfect7+" - "+ complete);
//		System.out.println(problematic);
//		System.out.println(problematicSet);
//		System.out.println(risk1 +" - "+ risk2 +" - "+ risk3 +" - "+ risk4 +" - "+ risk5 +" - "+ risk6 +" - "+ risk7 +" - "+ risk8);
//		System.out.println("risk1: "+Arrays.toString(riskArray1));
//		System.out.println("risk2: "+Arrays.toString(riskArray2));
//		System.out.println("risk3: "+Arrays.toString(riskArray3));
//		System.out.println("risk4: "+Arrays.toString(riskArray4));
//		System.out.println("risk5: "+Arrays.toString(riskArray5));
//		System.out.println("risk6: "+Arrays.toString(riskArray6));
//		System.out.println("risk7: "+Arrays.toString(riskArray7));
//		System.out.println("risk8: "+Arrays.toString(riskArray8));
//		System.out.println((riskArray1[0]+riskArray2[0]+riskArray3[0]+riskArray4[0]+riskArray5[0]+riskArray6[0]+riskArray7[0]+riskArray8[0])+ " - " +(riskArray1[1]+riskArray2[1]+riskArray3[1]+riskArray4[1]+riskArray5[1]+riskArray6[1]+riskArray7[1]+riskArray8[1])+ " - " +(riskArray1[2]+riskArray2[2]+riskArray3[2]+riskArray4[2]+riskArray5[2]+riskArray6[2]+riskArray7[2]+riskArray8[2])+ " - " +(riskArray1[3]+riskArray2[3]+riskArray3[3]+riskArray4[3]+riskArray5[3]+riskArray6[3]+riskArray7[3]+riskArray8[3]));
//		System.out.println(modelComplete);
	}

	private static String analyze(String[] s) {
		String model = s[0].substring(0, s[0].indexOf("-")-1);
		int[] VarNum = new int[1];
		int[] TaskNum = new int[1];
		String[] risk = risk(model, VarNum, TaskNum);
		long minTime = calculateMinTime(s);
		double everageTime = calculateTime(s);
		long maxTime = calculateMaxTime(s);
		int minModel = calculateMinModels(s);
		double everageModel = calculateModels(s);
		int maxModel = calculateMaxModels(s);
		int numberRisks = calculateRisks(s);
		HashMap<LinkedList<Integer>, Integer> mitigation = calculateMitigations(s, numberRisks, risk);
		int count255 = count255(mitigation);
		boolean solveSomeRisk = solveSomeRisk(mitigation);
		boolean solveRisk = solveRisk(mitigation);
		boolean zeroSolveRisk = zeroSolveRisk(mitigation);
		semiPerfectSolveRisk(model, mitigation);
		boolean perfectSolveRisk = perfectSolveRisk(mitigation);
		if(perfectSolveRisk) modelComplete += model +" - ";
//		return count255+ " -- " +model+" -- "+numberRisks+" - "+Arrays.toString(risk)+" -- "+minTime+" - "+everageTime+" - "+maxTime+" -- "+minModel+" - "+everageModel+" - "+maxModel+" -- "+perfectSolveRisk+" - "+solveRisk+" - "+zeroSolveRisk+" - "+solveSomeRisk+" -- "+mitigation;
		return model+" - "+VarNum[0]+" - "+TaskNum[0]+" - "+everageTime+" - "+everageModel;
		

//		if(checkFinishRollback(s)) return model;
//		else return "";
	}

	private static boolean checkFinishRollback(String[] s) {
		for(int i=0; i<s.length; i++) {
			String risks = s[i].substring(s[i].indexOf("["), s[i].lastIndexOf("]")+1);
			StringTokenizer st = new StringTokenizer(risks, "[]", true);
			boolean ok = false;
			boolean found = true;
			while(st.hasMoreTokens()) {
				String token = st.nextToken(); 
				if(token.equals("[")) {
					token = st.nextToken();
					if(!token.equals("]")) {
						if(ok) {
//							if(mitigations(token)) found = true;
//							if(!mitigations(token)) found = false;
							found = mitigations(token); 
						}
						ok = !ok;
						st.nextToken();
					}
				}
			}
			if(found) return true;
		}
		return false;
	}

	private static boolean mitigations(String risks) {
		StringTokenizer st = new StringTokenizer(risks, ",");
		String token = null;
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			if(token.startsWith(" ")) token = token.substring(1);
		}
		if(token.startsWith("Rollback")) return true;
		return false;
	}

	private static long calculateMinTime(String[] s) {
		Long res = null;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- Time: ")+8);
			String time = post.substring(0, post.indexOf(" -"));
			if(res == null || res > new Long(time)) res = new Long(time);
		}
		return res;
	}
	
	private static double calculateTime(String[] s) {
		double res = 0;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- Time: ")+8);
			String time = post.substring(0, post.indexOf(" -"));
			res += new Long(time);
		}
		return res/10;
	}
	
	private static long calculateMaxTime(String[] s) {
		Long res = null;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- Time: ")+8);
			String time = post.substring(0, post.indexOf(" -"));
			if(res == null || res < new Long(time)) res = new Long(time);
		}
		return res;
	}
	
	private static int calculateMinModels(String[] s) {
		Integer res = null;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- total model generated: ")+25);
			String models = post.substring(0, post.length());
			if(res == null || res > new Integer(models)) res = new Integer(models);
		}
		return res;
	}

	private static double calculateModels(String[] s) {
		double res = 0;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- total model generated: ")+25);
			String models = post.substring(0, post.length());
			res += new Long(models);
			if(minModels > new Integer(models)) {
				minModels = new Integer(models);
			}
			if(maxModels < new Integer(models)) {
				maxModels = new Integer(models);
			}
		}
		models += res;
		return res/10;
	}
	
	private static int calculateMaxModels(String[] s) {
		Integer res = null;
		for(int i=0; i<s.length; i++) {
			String post = s[i].substring(s[i].indexOf("- total model generated: ")+25);
			String models = post.substring(0, post.length());
			if(res == null || res < new Integer(models)) res = new Integer(models);
		}
		return res;
	}
	
	private static int calculateRisks(String[] s) {
		int res = 0;
		String risks = s[0].substring(s[0].indexOf("[")+1, s[0].indexOf("]"));
		StringTokenizer st = new StringTokenizer(risks, ",");
		while(st.hasMoreTokens()) {
			st.nextToken();
			res++;
		}
		switch((res-1)/2) {
		case 1: risk1++;
				break;
		case 2: risk2++;
				break;
		case 3: risk3++;
				break;
		case 4: risk4++;
				break;
		case 5: risk5++;
				break;
		case 6: risk6++;
				break;
		case 7: risk7++;
				break;
		case 8: risk8++;
				break;
		}
		return (res-1)/2;
	}
	
	private static HashMap<LinkedList<Integer>, Integer> calculateMitigations(String[] s, int numberRisks, String[] rr) {
		HashMap<LinkedList<Integer>, Integer> map = new HashMap<LinkedList<Integer>, Integer>();
		HashMap<LinkedList<Integer>, Integer> finalMap = new HashMap<LinkedList<Integer>, Integer>();
		for(int i=0; i<s.length; i++) {
			HashMap<LinkedList<Integer>, Integer> mitigations = new HashMap<LinkedList<Integer>, Integer>();
			String risks = s[i].substring(s[i].indexOf("["), s[i].lastIndexOf("]")+1);
			StringTokenizer st = new StringTokenizer(risks, "[]", true);
			while(st.hasMoreTokens()) {
				String token = st.nextToken(); 
				if(token.equals("[")) {
					token = st.nextToken();
					if(!token.equals("]")) {
						LinkedList<Integer> r = checkMitigation(token, numberRisks);
						for(int l=0; l<r.size(); l++) {
							System.out.println("Check "+r);
							String rs = rr[l];
							if(rs.equals("risk1")) {
								if(r.get(l) == 75) {
									riskArray1[0]++;
									riskArray1[1]++;
								}else if(r.get(l) == 25) {
									riskArray1[0]++;
									riskArray1[2]++;
								}else if(r.get(l) == 0) {
									riskArray1[0]++;
									riskArray1[3]++;
								}
							}else if(rs.equals("risk2")) {
								if(r.get(l) == 75) {
									riskArray2[0]++;
									riskArray2[1]++;
								}else if(r.get(l) == 25) {
									riskArray2[0]++;
									riskArray2[2]++;
								}else if(r.get(l) == 0) {
									riskArray2[0]++;
									riskArray2[3]++;
								}								
							}else if(rs.equals("risk3")) {
								if(r.get(l) == 75) {
									riskArray3[0]++;
									riskArray3[1]++;
								}else if(r.get(l) == 25) {
									riskArray3[0]++;
									riskArray3[2]++;
								}else if(r.get(l) == 0) {
									riskArray3[0]++;
									riskArray3[3]++;
								}
							}else if(rs.equals("risk4")) {
								if(r.get(l) == 75) {
									riskArray4[0]++;
									riskArray4[1]++;
								}else if(r.get(l) == 25) {
									riskArray4[0]++;
									riskArray4[2]++;
								}else if(r.get(l) == 0) {
									riskArray4[0]++;
									riskArray4[3]++;
								}
							}else if(rs.equals("risk5")) {
								if(r.get(l) == 75) {
									riskArray5[0]++;
									riskArray5[1]++;
								}else if(r.get(l) == 25) {
									riskArray5[0]++;
									riskArray5[2]++;
								}else if(r.get(l) == 0) {
									riskArray5[0]++;
									riskArray5[3]++;
								}
							}else if(rs.equals("risk6")) {
								if(r.get(l) == 75) {
									riskArray6[0]++;
									riskArray6[1]++;
								}else if(r.get(l) == 25) {
									riskArray6[0]++;
									riskArray6[2]++;
								}else if(r.get(l) == 0) {
									riskArray6[0]++;
									riskArray6[3]++;
								}
							}else if(rs.equals("risk7")) {
								if(r.get(l) == 75) {
									riskArray7[0]++;
									riskArray7[1]++;
								}else if(r.get(l) == 25) {
									riskArray7[0]++;
									riskArray7[2]++;
								}else if(r.get(l) == 0) {
									riskArray7[0]++;
									riskArray7[3]++;
								}
							}else if(rs.equals("risk8")) {
								if(r.get(l) == 75) {
									riskArray8[0]++;
									riskArray8[1]++;
								}else if(r.get(l) == 25) {
									riskArray8[0]++;
									riskArray8[2]++;
								}else if(r.get(l) == 0) {
									riskArray8[0]++;
									riskArray8[3]++;
								}
							}
						}
						if(!mitigations.containsKey(r)) {
							mitigations.put(r, 1);
						}else {
							mitigations.put(r, mitigations.get(r)+1);
						}
						st.nextToken();
					}
				}
			}
			for(Entry<LinkedList<Integer>, Integer> entry : mitigations.entrySet()) {
				if(!map.containsKey(entry.getKey())) {
					map.put(entry.getKey(), entry.getValue());
				}else {
					if(entry.getValue() > map.get(entry.getKey())) {
						map.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		for(Entry<LinkedList<Integer>, Integer> entry : map.entrySet()) {
			LinkedList<Integer> type = new LinkedList<Integer>();
			type.add(0);
			type.add(0);
			type.add(0);
			for(Integer i : entry.getKey()) {
				if(i == 75) {
					type.add(0, type.remove(0)+1);
				}else if(i == 25) {
					type.add(1, type.remove(1)+1);
				}else if(i == 0) {
					type.add(2, type.remove(2)+1);
				}
			}
			
			if(!finalMap.containsKey(type)) {
				finalMap.put(type, entry.getValue());
			}else {
				finalMap.put(type, finalMap.get(type) + entry.getValue());
			}
		}
		return finalMap;
	}

	private static LinkedList<Integer> checkMitigation(String token, int numberRisks) {
		LinkedList<Integer> res = new LinkedList<Integer>(); 
		StringTokenizer st = new StringTokenizer(token, ",");
		int count = 0;
		while(st.hasMoreTokens() && count < numberRisks) {
			double d = new Double(st.nextToken());
			res.add((int) d);
			st.nextToken();
			count++;
		}
		return res;
	}
	
	private static int count255(HashMap<LinkedList<Integer>, Integer> mitigation) {
		int res = 0;
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(0) == 0 && key.get(1) > 0 && key.get(2) == 0) {
				res++;
			}
		}
		return res;
	}
	
	private static boolean solveSomeRisk(HashMap<LinkedList<Integer>, Integer> mitigation) {
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(1) > 0 || key.get(2) > 0) {
				some++;
				return true;
			}
		}
		return false;
	}

	private static boolean solveRisk(HashMap<LinkedList<Integer>, Integer> mitigation) {
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(0) == 0) {
				partial++;
				return true;
			}
		}
		return false;
	}
	
	private static boolean zeroSolveRisk(HashMap<LinkedList<Integer>, Integer> mitigation) {
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(2) > 0) {
				zero++;
				return true;
			}
		}
		return false;
	}

	private static boolean semiPerfectSolveRisk(String model, HashMap<LinkedList<Integer>, Integer> mitigation) {
		boolean ok7 = false;
		boolean ok6 = false;
		boolean ok5 = false;
		boolean ok4 = false;
		boolean ok3 = false;
		boolean ok2 = false;
		boolean ok1 = false;
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(0) == 0 && key.get(2) > 6 && !ok7) {
				semiPerfect7++;
				ok7 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 5 && !ok6) {
				semiPerfect6++;
				ok6 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 4 && !ok5) {
				semiPerfect5++;
				ok5 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 3 && !ok4) {
				semiPerfect4++;
				ok4 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 2 && !ok3) {
				semiPerfect3++;
				ok3 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 1 && !ok2) {
				semiPerfect2++;
				ok2 = true;
			}
			if(key.get(0) == 0 && key.get(2) > 0 && !ok1) {
				semiPerfect1++;
				ok1 = true;
			}
		}
		if(ok1) return true;
		problematic += model +" "+ Arrays.toString(risk(model, null, null))+"\n";
		problematicSet.add(Arrays.toString(risk(model, null, null)));
		return false;
	}
	
	private static boolean perfectSolveRisk(HashMap<LinkedList<Integer>, Integer> mitigation) {
		for(LinkedList<Integer> key : mitigation.keySet()) {
			if(key.get(0) == 0 && key.get(1) == 0) {
				complete++;
				return true;
			}
		}
		return false;
	}
	
	public static void riskProcess(int limit) {
		for(int i = 1; i<limit; i++) {
			System.out.println(1);
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				System.out.println(2);
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					System.out.println(3);
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						System.out.println(4);
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							System.out.println(5);
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								System.out.println(6);
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									System.out.println(7);
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									for(int b = c+1; b<limit; b++) {
										System.out.println(8);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static String[] risk(String model, int[] varNum, int[] taskNum) {
		
		String number = model.substring(model.indexOf("Model: ")+7);
		int mod = new Integer(number);
		int proc = 0;
		int limit = 0;
		int count = 0;
		String[] risk = null;
		if(varNum == null) varNum = new int[1];
		if(taskNum == null) taskNum = new int[1];
		
		if(mod < 128) {
			proc = 2;
			limit = 8;
			count = 1;
		}else if(mod < 143) {
			proc = 4;
			limit = 5;
			count = 128;
		}else if(mod < 150) {
			proc = 5;
			limit = 4;
			count = 143;
		}else {
			proc = 6;
			limit = 6;
			count = 150;
		}

//		if(true) return new String[]{"risk1", "risk2", "risk3", "risk4"};
		
		String[] sensor = null;
		String sensor1 = null;
		String sensor2 = null;
		String sensor3 = null;
		String sensor4 = null;
		String sensor5 = null;
		String sensor6 = null;
		String sensor7 = null;
		String sensor8 = null;
		
		int[] varVarNum = null;
		int varVarNum1 = 0;
		int varVarNum2 = 0;
		int varVarNum3 = 0;
		int varVarNum4 = 0;
		int varVarNum5 = 0;
		int varVarNum6 = 0;
		int varVarNum7 = 0;
		int varVarNum8 = 0;
		
		int[] varTaskNum = null;
		int varTaskNum1 = 0;
		int varTaskNum2 = 0;
		int varTaskNum3 = 0;
		int varTaskNum4 = 0;
		int varTaskNum5 = 0;
		int varTaskNum6 = 0;
		int varTaskNum7 = 0;
		int varTaskNum8 = 0;
		
		switch(proc) {
			case 2:
				sensor1 = "risk1";
				sensor2 = "risk2";
				sensor3 = "risk3";
				sensor4 = "risk4";
				sensor5 = "risk5";
				sensor6 = "risk6";
				sensor7 = "risk7";
				sensor8 = "risk8";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};

				varVarNum1 = 8;
				varVarNum2 = 2;
				varVarNum3 = 3;
				varVarNum4 = 6;
				varVarNum5 = 2;
				varVarNum6 = 4;
				varVarNum7 = 3;
				varVarNum8 = 4;
				varVarNum = new int[]{0, varVarNum1, varVarNum2, varVarNum3, varVarNum4, varVarNum5, varVarNum6, varVarNum7, varVarNum8};

				varTaskNum1 = 4;
				varTaskNum2 = 2;
				varTaskNum3 = 2;
				varTaskNum4 = 2;
				varTaskNum5 = 1;
				varTaskNum6 = 4;
				varTaskNum7 = 3;
				varTaskNum8 = 4;
				varTaskNum = new int[]{0, varTaskNum1, varTaskNum2, varTaskNum3, varTaskNum4, varTaskNum5, varTaskNum6, varTaskNum7, varTaskNum8};
				break;
				
			case 4:
				sensor1 = "risk2";
				sensor2 = "risk3";
				sensor3 = "risk5";
				sensor4 = "risk6";
				sensor5 = "risk7";
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};

				varVarNum1 = 2;
				varVarNum2 = 3;
				varVarNum3 = 2;
				varVarNum4 = 4;
				varVarNum5 = 3;
				varVarNum6 = 0;
				varVarNum7 = 0;
				varVarNum8 = 0;
				varVarNum = new int[]{0, varVarNum1, varVarNum2, varVarNum3, varVarNum4, varVarNum5, varVarNum6, varVarNum7, varVarNum8};

				varTaskNum1 = 2;
				varTaskNum2 = 2;
				varTaskNum3 = 1;
				varTaskNum4 = 4;
				varTaskNum5 = 3;
				varTaskNum6 = 0;
				varTaskNum7 = 0;
				varTaskNum8 = 0;
				varTaskNum = new int[]{0, varTaskNum1, varTaskNum2, varTaskNum3, varTaskNum4, varTaskNum5, varTaskNum6, varTaskNum7, varTaskNum8};
				break;
				
			case 5:
				sensor1 = "risk4";
				sensor2 = "risk5";
				sensor3 = "risk7";
				sensor4 = "";
				sensor5 = "";
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};

				varVarNum1 = 6;
				varVarNum2 = 2;
				varVarNum3 = 3;
				varVarNum4 = 0;
				varVarNum5 = 0;
				varVarNum6 = 0;
				varVarNum7 = 0;
				varVarNum8 = 0;
				varVarNum = new int[]{0, varVarNum1, varVarNum2, varVarNum3, varVarNum4, varVarNum5, varVarNum6, varVarNum7, varVarNum8};

				varTaskNum1 = 2;
				varTaskNum2 = 1;
				varTaskNum3 = 3;
				varTaskNum4 = 0;
				varTaskNum5 = 0;
				varTaskNum6 = 0;
				varTaskNum7 = 0;
				varTaskNum8 = 0;
				varTaskNum = new int[]{0, varTaskNum1, varTaskNum2, varTaskNum3, varTaskNum4, varTaskNum5, varTaskNum6, varTaskNum7, varTaskNum8};
				break;
				
			case 6:
				sensor1 = "risk2";
				sensor2 = "risk3";
				sensor3 = "risk4";
				sensor4 = "risk5";
				sensor5 = "risk6";
				sensor6 = "risk7";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};

				varVarNum1 = 2;
				varVarNum2 = 3;
				varVarNum3 = 6;
				varVarNum4 = 2;
				varVarNum5 = 4;
				varVarNum6 = 3;
				varVarNum7 = 0;
				varVarNum8 = 0;
				varVarNum = new int[]{0, varVarNum1, varVarNum2, varVarNum3, varVarNum4, varVarNum5, varVarNum6, varVarNum7, varVarNum8};

				varTaskNum1 = 2;
				varTaskNum2 = 2;
				varTaskNum3 = 2;
				varTaskNum4 = 1;
				varTaskNum5 = 4;
				varTaskNum6 = 3;
				varTaskNum7 = 0;
				varTaskNum8 = 0;
				varTaskNum = new int[]{0, varTaskNum1, varTaskNum2, varTaskNum3, varTaskNum4, varTaskNum5, varTaskNum6, varTaskNum7, varTaskNum8};
				break;
		}
		
		for(int i = 1; i<limit; i++) {
			if(count == mod) {
				risk = new String[]{sensor[i]};
				varNum[0] = varVarNum[i];
				taskNum[0] = varTaskNum[i];
			}
			if(!sensor[i].contains("risk3")) count++;
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				if(count == mod) {
					risk = new String[]{sensor[i], sensor[j]};	
					varNum[0] = varVarNum[i] + varVarNum[j];
					taskNum[0] = varTaskNum[i] + varTaskNum[j];
				}
				if(!(sensor[i]+sensor[j]).contains("risk3")) count++;
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					if(count == mod) {
						risk = new String[]{sensor[i], sensor[j], sensor[k]};	
						varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k];
						taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k];
					}
					if(!(sensor[i]+sensor[j]+sensor[k]).contains("risk3")) count++;
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						if(count == mod) {
							risk = new String[]{sensor[i], sensor[j], sensor[k], sensor[h]};
							varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k] + varVarNum[h];
							taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k] + varTaskNum[h];
						}
						if(!(sensor[i]+sensor[j]+sensor[k]+sensor[h]).contains("risk3")) count++;
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							if(count == mod) {
								risk = new String[]{sensor[i], sensor[j], sensor[k], sensor[h], sensor[g]};
								varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k] + varVarNum[h] + varVarNum[g];
								taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k] + varTaskNum[h] + varTaskNum[g];
							}
							if(!(sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]).contains("risk3")) count++;
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								if(count == mod) {
									risk = new String[]{sensor[i], sensor[j], sensor[k], sensor[h], sensor[g], sensor[d]};
									varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k] + varVarNum[h] + varVarNum[g] + varVarNum[d];
									taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k] + varTaskNum[h] + varTaskNum[g] + varTaskNum[d];
								}
								if(!(sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]).contains("risk3")) count++;
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									if(count == mod) {
										risk = new String[]{sensor[i], sensor[j], sensor[k], sensor[h], sensor[g], sensor[d], sensor[c]};
										varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k] + varVarNum[h] + varVarNum[g] + varVarNum[d] + varVarNum[c];
										taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k] + varTaskNum[h] + varTaskNum[g] + varTaskNum[d] + varTaskNum[c];
									}
									if(!(sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]).contains("risk3")) count++;
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									for(int b = c+1; b<limit; b++) {
										if(count == mod) {
											risk = new String[]{sensor[i], sensor[j], sensor[k], sensor[h], sensor[g], sensor[d], sensor[c], sensor[b]};
											varNum[0] = varVarNum[i] + varVarNum[j] + varVarNum[k] + varVarNum[h] + varVarNum[g] + varVarNum[d] + varVarNum[c] + varVarNum[b];
											taskNum[0] = varTaskNum[i] + varTaskNum[j] + varTaskNum[k] + varTaskNum[h] + varTaskNum[g] + varTaskNum[d] + varTaskNum[c] + varTaskNum[b];
										}
										if(!(sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]+sensor[b]).contains("risk3")) count++;
									}
								}
							}
						}
					}
				}
			}
		}
		return risk;
	}
	
	public static int task(int model) {
		
		int mod = new Integer(model);
		int proc = 0;
		int limit = 0;
		int count = 0;
		
		if(mod < 128) {
			proc = 2;
			limit = 8;
			count = 1;
		}else if(mod < 143) {
			proc = 4;
			limit = 5;
			count = 128;
		}else if(mod < 150) {
			proc = 5;
			limit = 4;
			count = 143;
		}else {
			proc = 6;
			limit = 6;
			count = 150;
		}

//		if(true) return new String[]{"risk1", "risk2", "risk3", "risk4"};
		
		HashSet<Integer>[] sensor = null;
		HashSet<Integer> sensor1 = null;
		HashSet<Integer> sensor2 = null;
		HashSet<Integer> sensor3 = null;
		HashSet<Integer> sensor4 = null;
		HashSet<Integer> sensor5 = null;
		HashSet<Integer> sensor6 = null;
		HashSet<Integer> sensor7 = null;
		
		HashSet<Integer> res = new HashSet<Integer>();
		
		switch(proc) {
			case 2:
				sensor1 = new HashSet<Integer>();
				sensor1.add(15);
				sensor1.add(16);
				sensor1.add(17);
				sensor1.add(18);
				
				sensor2 = new HashSet<Integer>();
				sensor2.add(15);
				sensor2.add(16);
				
				sensor3 = new HashSet<Integer>();
				sensor3.add(5);
				sensor3.add(7);
				
				sensor4 = new HashSet<Integer>();
				sensor4.add(7);
				
				sensor5 = new HashSet<Integer>();
				sensor5.add(2);
				sensor5.add(3);
				sensor5.add(16);
				sensor5.add(18);
				
				sensor6 = new HashSet<Integer>();
				sensor6.add(2);
				sensor6.add(3);
				sensor6.add(5);
				
				sensor7 = new HashSet<Integer>();
				sensor7.add(15);
				sensor7.add(16);
				sensor7.add(17);
				sensor7.add(14);
				
				sensor = new HashSet[]{null, sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7};
				break;
				
			case 4:
				sensor1 = new HashSet<Integer>();
				sensor1.add(2);
				sensor1.add(3);
				
				sensor2 = new HashSet<Integer>();
				sensor2.add(2);
				
				sensor3 = new HashSet<Integer>();
				sensor3.add(2);
				sensor3.add(3);
				sensor3.add(4);
				sensor3.add(5);
				
				sensor4 = new HashSet<Integer>();
				sensor4.add(2);
				sensor4.add(3);
				sensor4.add(4);
				
				sensor5 = new HashSet<Integer>();
				
				sensor6 = new HashSet<Integer>();
				
				sensor7 = new HashSet<Integer>();
				
				sensor = new HashSet[]{null, sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7};
				break;
				
			case 5:
				sensor1 = new HashSet<Integer>();
				sensor1.add(1);
				sensor1.add(2);
				
				sensor2 = new HashSet<Integer>();
				sensor2.add(3);
				
				sensor3 = new HashSet<Integer>();
				sensor3.add(2);
				sensor3.add(3);
				sensor3.add(4);
				
				sensor4 = new HashSet<Integer>();
								
				sensor5 = new HashSet<Integer>();
				
				sensor6 = new HashSet<Integer>();
				
				sensor7 = new HashSet<Integer>();
				
				sensor = new HashSet[]{null, sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7};
				
			case 6:
				sensor1 = new HashSet<Integer>();
				sensor1.add(2);
				sensor1.add(4);
				
				sensor2 = new HashSet<Integer>();
				sensor2.add(5);
				sensor2.add(9);
				
				sensor3 = new HashSet<Integer>();
				sensor3.add(4);
				sensor3.add(5);
				sensor3.add(9);
				
				sensor4 = new HashSet<Integer>();
				sensor4.add(2);
				sensor4.add(4);
				sensor4.add(5);
				sensor4.add(9);
				
				sensor5 = new HashSet<Integer>();
				
				sensor6 = new HashSet<Integer>();
				
				sensor7 = new HashSet<Integer>();
				
				sensor = new HashSet[]{null, sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7};
				break;
		}
		
		for(int i = 1; i<limit; i++) {
			if(count == mod) {
				res.addAll(sensor[i]);
			}
			count++;
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				if(count == mod) {
					res.addAll(sensor[i]);
					res.addAll(sensor[j]);
				}
				count++;
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					if(count == mod) {
						res.addAll(sensor[i]);
						res.addAll(sensor[j]);
						res.addAll(sensor[k]);
					}
					count++;
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						if(count == mod) {
							res.addAll(sensor[i]);
							res.addAll(sensor[j]);
							res.addAll(sensor[k]);
							res.addAll(sensor[h]);
						}
						count++;
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							if(count == mod) {
								res.addAll(sensor[i]);
								res.addAll(sensor[j]);
								res.addAll(sensor[k]);
								res.addAll(sensor[h]);
								res.addAll(sensor[g]);
							}
							count++;
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								if(count == mod) {
									res.addAll(sensor[i]);
									res.addAll(sensor[j]);
									res.addAll(sensor[k]);
									res.addAll(sensor[h]);
									res.addAll(sensor[g]);
									res.addAll(sensor[d]);
								}
								count++;
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									if(count == mod) {
										res.addAll(sensor[i]);
										res.addAll(sensor[j]);
										res.addAll(sensor[k]);
										res.addAll(sensor[h]);
										res.addAll(sensor[g]);
										res.addAll(sensor[d]);
										res.addAll(sensor[c]);
									}
									count++;
								}
							}
						}
					}
				}
			}
		}
		
		return res.size();
	}
}
