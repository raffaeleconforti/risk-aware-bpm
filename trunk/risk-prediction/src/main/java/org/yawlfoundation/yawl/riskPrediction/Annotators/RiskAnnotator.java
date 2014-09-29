package org.yawlfoundation.yawl.riskPrediction.Annotators;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom2.Element;
import org.yawlfoundation.yawl.riskPrediction.Annotations.RiskAnnotations;
import org.yawlfoundation.yawl.sensors.SensorPredictor;
import org.yawlfoundation.yawl.sensors.YSensor;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;



public class RiskAnnotator {

	private final WorkflowDefinition workFlowDefinitionLayer;
	
	private final double roundingFactor = 20.0;
	
	private InterfaceManager im = null;
	
	private SensorPredictor sp = null;
	
	private RiskAnnotations ra = null;
	
	private final AtomicInteger ai = new AtomicInteger();
	
	private final int cpu = 4;

	private double[] weight;

    private static String filePath = "/home/user";
	
	public static PrintStream outptuFile(String name) throws FileNotFoundException {
	   return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)));
	}
	
	public static void main(String[] args) throws FileNotFoundException {
//		System.setOut(outptuFile("/media/Data/SharedFolder/risks"));
		try {
			File dir = new File(filePath);
			
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Demo/Log.xes");
			XFactoryRegistry.instance().setCurrentDefault(new XFactoryNaiveImpl());
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/stormfire/2795-0BA2/Log/filteredTestLog.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/testLog.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/AllLogs2.xes");
//			XLog log2 = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Commercial/OptimalLog/testLog.xes");
//			XLog log2 = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Commercial/Exp/new0.25/testLog.xes");
            String test = "5";
			XLog log2 = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/user/DSS/"+test+"testLog.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Commercial/testLog.xes");

			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/user/DSS/FilteredCommercial15.xes");
			
			XConceptExtension xce = XConceptExtension.instance();
			
			for(XTrace trace : log2) {
				log.add(trace);
			}

			String specification = null;
			try {
//				File f = new File("/home/stormfire/InsuranceClaim.yawl");
				File f = new File("/home/user/DSS/CommercialNew.yawl");
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				specification = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			String resources = null;
			try {
				File f = new File("/home/user/DSS/Insurance.ybkp");
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				resources = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);

			InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);
//			LinkedList<String> list = new LinkedList<String>();
//			list.add("1");
			
//			ActionInitializer.inizializeActions(im.getWorkflowDefinitionLayer(), im.getActivityLayer(), im.getActivityRoleLayer(), im.getRoleLayer(), im.getVariableLayer(), im.getSubProcessLayer(), null);
//			System.out.println(ActivityCompleteTimeInMillis.getActionResultList("1", "New_Claim_3"));
//			System.out.println(ActivityCompleteTimeInMillis.getActionResult("1", "New_Claim_3"));
//			System.out.println(ActivityCompleteTimeInMillis.getActionResult("1", "Close_File_22"));
			
			double[] weight = new double[] {0.333, 0.333, 0.333};
			RiskAnnotator ra = new RiskAnnotator(im, weight);
			
			ra.annotateLog(null, "/home/user/IdeaProjects/Risk-BPM/svn/trunk/risk-prediction/target/classes");
			
			System.out.println(ra.getRiskAnnotations());
			
			for(String id : im.getWorkflowDefinitionLayer().getAllID()) {
				if(!id.startsWith("H")) 
					System.out.println(id+","+ra.getRiskAnnotations().getAnnotation(id));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public RiskAnnotator(InterfaceManager im) {
		
		this.im = im;

		workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
		sp = new SensorPredictor(workFlowDefinitionLayer);
		
	}

	public RiskAnnotator(InterfaceManager im, double[] weight) {
		
		this.weight = weight;
		this.im = im;

		workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
		sp = new SensorPredictor(workFlowDefinitionLayer);
		
	}
	
	public RiskAnnotations annotateLog(String specID, String dirPath) {
		
		ra = new RiskAnnotations();
		YSensorPredictionUpdater yspu = YSensorPredictionUpdater.getInstance(im);
		
		String sensorsString = null;
		
		if((sensorsString = workFlowDefinitionLayer.getSensors(null, specID, null)) != null) {
			
			Element sensors = JDOMUtil.stringToDocument(sensorsString).getRootElement();
			int size = sensors.getChildren().size();
			
			Map<String, Double[]>[] risksConsequences = new Map[size];
			
			for(int i = 0; i<size; i++) {

				risksConsequences[i] = new ConcurrentHashMap<String, Double[]>();
				
			}
						
			Vector<String> caseIDs = workFlowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, null, null);
			
			Collections.sort(caseIDs, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
//					Integer a1 = Integer.parseInt(a);
//					Integer b1 = Integer.parseInt(b);
//					return a1.compareTo(b1);
					return a.compareTo(b);
				}
			});
			
			File consequenceFile = new File("consequenceFileTest");
			
			if(consequenceFile.exists() && false) {				
				
				ObjectInputStream ois;
				
				try {
					
					ois = new ObjectInputStream(new FileInputStream(consequenceFile));				
					risksConsequences = (Map<String, Double[]>[]) ois.readObject();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}else {
				
				int cores = Runtime.getRuntime().availableProcessors();
				
				for(String caseID : caseIDs)
//				for(int i = 2001; i<2201; i++)
				{
//				String caseID = ""+i;
						
//					System.out.println(caseID + " - " + System.currentTimeMillis());
									
					SensorUpdater su = new SensorUpdater(size, sensors, caseID, risksConsequences, yspu, dirPath);
					
					ai.incrementAndGet();
					
					su.start();
					
//					while(ai.get() >= (cores*3/4) || ai.get() >= cpu) {
					while(ai.get() >= cpu) {
						try {
							Thread.currentThread().sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
				while(ai.get() > 0) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(consequenceFile));
					oos.writeObject(risksConsequences);
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			normalizeConsequences(risksConsequences);
			
			Map<String, Double> tmpAnnotation = new HashMap<String, Double>();
			
			for(Entry<String, Double[]> entry : risksConsequences[0].entrySet()) {
				
				double outcome = 0;
								
				int riskpos = 1;
				for(Map<String, Double[]> riskConsequence : risksConsequences) {

					
					Double[] d = riskConsequence.get(entry.getKey());
					if(!entry.getKey().startsWith("H"))
						System.out.print(entry.getKey()+" "+riskpos+" "+Arrays.toString(d));
					if(weight != null) {
						outcome += (d[0]*d[1])*weight[riskpos-1];
					}else {
						outcome += (d[0]*d[1])*(1.0/risksConsequences.length);
					}
					riskpos++;
					
				}
				if(!entry.getKey().startsWith("H")) 
					System.out.println();
				
				tmpAnnotation.put(entry.getKey(), outcome);
				
			}
			
			annotateNormalizedRiskConsequence(ra, tmpAnnotation, size);
			
//			try {
//				
//				String base = null;
//				File dir = new File("/home/stormfire");
//				if(dir.exists()) base = "stormfire";
//				else base = "conforti";
//				
//				File output = new File("/home/"+base+"/risk.txt");
//				FileWriter fw = new FileWriter(output);
//				
//				for(int i = 2001; i< 2201; i++) {
//					String k = ""+i;
////					System.out.println(i+" "+ra.getAnnotation(k));
//					fw.append(i+" "+ra.getAnnotation(k)+"\n");
//				}
//				
//				fw.close();
//				
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
				
			
		}
		
		return ra;
		
	}
	
	private void annotateNormalizedRiskConsequence(RiskAnnotations ra, Map<String, Double> tmpAnnotation, double riskNumber) {
		
		for(Entry<String, Double> entry : tmpAnnotation.entrySet()) {
			
			ra.addAnnotation(entry.getKey(), (long) Math.ceil(entry.getValue()));
			
		}
					
	}

	private void annotateNormalizedRiskConsequence2(RiskAnnotations ra, Map<String, Double> tmpAnnotation, double riskNumber) {
		
		double max = 0;//20.0 * riskNumber;
		
		for(Entry<String, Double> entry : tmpAnnotation.entrySet()) {
			
			double tmp = entry.getValue();
			
			if(max < tmp) {
				
				max = tmp;
				
			}
			
		}
		if(max < 20) max = 20;
				
		double ratio = roundingFactor / max;
		
		for(Entry<String, Double> entry : tmpAnnotation.entrySet()) {
			
			ra.addAnnotation(entry.getKey(), (long) Math.ceil(entry.getValue() * ratio));
			
		}
		
//		for(String caseID : tmpAnnotation.keySet()) {
//			
//			ra.addAnnotation(caseID, Math.round(tmpAnnotation.get(caseID) / riskNumber));
//			
//		}
					
	}


	private void normalizeConsequences(Map<String, Double[]>[] risksConsequences) {
		
		for(Map<String, Double[]> riskConsequence : risksConsequences) {
			
			double maxConsequence = 0.0;
			
			for(Entry<String, Double[]> entry : riskConsequence.entrySet()) {
				
				double consequence = entry.getValue()[1];
				
				if(maxConsequence < consequence) {
					
					maxConsequence = consequence;
					
				}
				
			}
			if(maxConsequence < 20) maxConsequence = 20; 
			
			if(maxConsequence > roundingFactor) {
			
				double ratio = roundingFactor / maxConsequence;
				
				for(Entry<String, Double[]> entry : riskConsequence.entrySet()) {
					
					entry.getValue()[1] *= ratio;
					
				}
				
			}
			
		}
		
	}

	public RiskAnnotations getRiskAnnotations() {
		
		return ra;
		
	}
	
	public boolean load() {
		
		File save = new File("risksSave");
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(save));
			ra = (RiskAnnotations) ois.readObject();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void save() {
		
		File save = new File("risksSave");
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(save));
			oos.writeObject(ra);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class SensorUpdater extends Thread {
		
		YSensor[] ySensors = null;
		Element sensors = null;
		int count = 0;
		int size = -1;
		String caseID = null;
		Map<String, Double[]>[] risksConsequences = null;
		YSensorPredictionUpdater yspu = null;
        String dirPath = null;
		
		public SensorUpdater(int size, Element sensors, String caseID, Map<String, Double[]>[] risksConsequences, YSensorPredictionUpdater yspu, String dirPath) {
			this.ySensors = new YSensor[size];
			this.size = size;
			this.sensors = sensors;
			this.caseID = caseID;
			this.risksConsequences = risksConsequences;
			this.yspu = yspu;
            this.dirPath = dirPath;
		}
		
		@Override
		public void run() {
//			yspu.updateSensor(ySensor, caseID);
//			yspu.updateSensor(ySensor, caseID);
//			Double[] d = ySensor.evaluateForPrediction(false);
//			
//			risksConsequences[count].put(caseID, d);
//			ai.decrementAndGet();
			
//			for(Element sensor : (List<Element>) sensors.getChildren()) {
//				ySensors[count] = new YSensor(caseID, sensor, sp);
//				count++;
//			}
			
			YSensor ySensor = null;

			ySensors = yspu.starterSensorSystem(caseID, null, null, dirPath);

			for(count = 0; count<size; count++) {
				yspu.updateSensor(ySensors[count], caseID);
			}

			yspu.updateSensorSystem(caseID);

			try {

				File dir = new File(filePath+"/log/");
                if(!dir.exists()) dir.getAbsoluteFile().mkdirs();
                if(!dir.exists()) System.out.println("created");

                File output = new File(filePath+"/log/"+caseID+".txt");
				FileWriter fw = new FileWriter(output);
				
				for(count = 0; count<size; count++) {
					ySensor = ySensors[count];
					yspu.updateSensor(ySensor, caseID);
					Double[] d = ySensor.evaluateForPrediction();

					risksConsequences[count].put(caseID, d);
					
					fw.append(count+"_"+Arrays.toString(d)+"\n");
					
				}
				
				fw.close();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			ai.decrementAndGet();
			
		}
		
	}
	
}
