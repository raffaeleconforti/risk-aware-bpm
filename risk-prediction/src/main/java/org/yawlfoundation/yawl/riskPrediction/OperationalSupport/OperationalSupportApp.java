package org.yawlfoundation.yawl.riskPrediction.OperationalSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom2.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskPrediction.Annotators.RiskAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TaskExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TimeBetweenTasksAnnotator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPoint;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPointDiscover;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.InstanceGenerator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.YAWL_DecisionPointDiscover;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.TupleClassifierName;
import org.yawlfoundation.yawl.riskPrediction.PredictionFunction.AttributeGenerator;
import org.yawlfoundation.yawl.riskPrediction.PredictionFunction.DecisionPredictor;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
//import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

import org.yawlfoundation.yawl.util.JDOMUtil;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class OperationalSupportApp {
	
	public static final boolean OFFER = true;
	public static final boolean COMPLETE = false;
	
	public final int roundingFactor = 5;
	
	private String classifierPathAndName = null;
	private String classifierOptions = null;
	
	private Map<String, Map<String, DecisionPoint>> decisionPoints = new ConcurrentHashMap<String, Map<String, DecisionPoint>>();
	
	private Map<String, Map<String, Classifier>> decisionPointsPredictors = new ConcurrentHashMap<String, Map<String, Classifier>>();
	private Map<String, Map<String, Instances>> decisionPointsInstances = new ConcurrentHashMap<String, Map<String, Instances>>();

	private Map<String, Map<String, Classifier>> decisionPointsPredictorsPostOffer = new ConcurrentHashMap<String, Map<String, Classifier>>();
	private Map<String, Map<String, Instances>> decisionPointsInstancesPostOffer = new ConcurrentHashMap<String, Map<String, Instances>>();
	
	private Map<String, Map<String, HashSet<String>>> listDecisionPoints = new ConcurrentHashMap<String, Map<String, HashSet<String>>>();
	private Map<YSpecificationID, Long> numberOfInstances = new ConcurrentHashMap<YSpecificationID, Long>();
	private Map<String, double[]> weightMap = new ConcurrentHashMap<String, double[]>();
	private Map<YSpecificationID, Long> totalNumberOfInstances = new ConcurrentHashMap<YSpecificationID, Long>();
	
	private InterfaceManager imStarting = null;
	private InterfaceManager imUpdated = null;

	private final int cpu = 100;
	
	public static void main(String[] args) {

		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/user/DSS/FilteredCommercial15.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Demo/Log.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Demo/Test.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Documents/Useless/log.xes");
			
			String specification = null;
			try {
				File f = new File("/home/user/DSS/CommercialNew.yawl");
//				File f = new File("/home/stormfire/InsuranceClaim.yawl");
//				File f = new File("/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl");
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
				File f = new File("/home/user/DSS/Commercial.ybkp");
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
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);
			
			InterfaceManager a = new InterfaceManager(InterfaceManager.PROM, parameters);
//			InterfaceManager a = new InterfaceManager(InterfaceManager.YAWL, null);
//
			OperationalSupportApp oss = new OperationalSupportApp(a, a, "weka.classifiers.rules.JRip", "-F 3 -N 2.0 -O 2 -S 1");//"weka.classifiers.trees.J48", "-C 0.25 -B -M 2");//

			oss.start("Commercial", "/home/user/DSS/consequenceFile", "");
//			oss.load("/home/stormfire/Dropbox/workspace/RiskInformedExecution/");
//			for(Entry<String, Map<String, Classifier>> entry : oss.decisionPointsPredictorsPostOffer.entrySet()) {
//				for(Entry<String, Classifier> entry2 : entry.getValue().entrySet()) {
//					System.out.println(entry.getKey());
//					System.out.println(entry2.getKey());
//					System.out.println(entry2.getValue());
//					System.out.println();
//				}
//			}	
			for(Entry<String, Map<String, Classifier>> entry : oss.decisionPointsPredictors.entrySet()) {
				for(Entry<String, Classifier> entry2 : entry.getValue().entrySet()) {
					System.out.println(entry.getKey());
					System.out.println(entry2.getKey());
					System.out.println(entry2.getValue());
//					System.out.println(oss.decisionPointsInstances.get(entry.getKey()).get(entry2.getKey()));
					System.out.println();
				}
			}	
			
//			System.out.println(oss.decisionPoints);
//			System.out.println(oss.decisionPointsInstances);
//			System.out.println(oss.decisionPointsPredictorsPostOffer);
			
			String[] resourcesList = new String[] {"a", "b"};
			
//			TaskExecutionAnnotator teAnnotator = new TaskExecutionAnnotator(a);
//			teAnnotator.annotateLog("InsuranceClaim");
			
//			TupleClassifierName tcn = oss.buildTupleClassifierName(new WorkItemRecord("1", "Approve_Or_Decline_6", "InsuranceClaim", null, null), resourcesList);
//			
//			DurationSplitter ds = new DurationSplitter(1);
//			String[] res = new String[] {"a", "b"};
//			HashMap<String, HashSet<long[]>> set = ds.generateSplits(tcn.getClassifiers(), 
//					new WorkItemRecord("1", "Approve_Or_Decline_6", "InsuranceClaim", null, null), res, tcn, 10000L, System.currentTimeMillis());
//			for(Entry<String, HashSet<long[]>> entry : set.entrySet()) {
//				for(long[] split : entry.getValue()) {
//					System.out.println(entry.getKey()+" "+split[0]+" "+split[1]);
//				}
//			}
//			System.out.println(ds.getRiskMap());
			

//			WorkItemRecord wir = new WorkItemRecord("384", "Open_Claim_3","InsuranceClaim", "", "");
////			WorkItemRecord wir2 = new WorkItemRecord("942", "Approve_Or_Decline_6","InsuranceClaim", "", "");
////			WorkItemRecord wir = new WorkItemRecord("559", "Request_Assessment_8","InsuranceClaim", "", "");
////			WorkItemRecord wir2 = new WorkItemRecord("559", "Approve_Or_Decline_6","InsuranceClaim", "", "");
//			
////			System.out.println(oss.decisionPointsPredictorsPostOffer.get("null_5"));
////			
////			System.out.println(oss.decisionPointsPredictors.get("null_5"));
//			
//			HashMap<WorkItemRecord, Long> map2 = oss.suggestDecisionPoints(new WorkItemRecord[] {wir}, "PA-b39db4e2-2e39-4fef-ab78-810d58c12699", 12000);
//			System.out.println(map2.get(wir));
////			System.out.println(map2.get(wir2));
//			
////			System.out.println(oss.decisionPointsPredictors.get("InsuranceClaim"));
//			
////			String specID = oss.decisionPointsInstances.keySet().iterator().next();
////			Map<String, Instances> map = oss.decisionPointsInstances.get(specID);
////			
////			Iterator it = map.keySet().iterator(); 
////			it.next();
////			String key = (String) it.next();
////			System.out.println(key);
////			key = "Arrange_Pickup_Appointment_819";
////			Instances is = map.get(key);
////			
////			Instance i = is.get(0);
////			
////			Map<String, DecisionPoint> mapDp =  oss.decisionPoints.get(specID);
////			DecisionPoint dp = mapDp.get(key);
////			
////			AttributeGenerator ag = new AttributeGenerator();
////			TaskExecutionAnnotator tea = new TaskExecutionAnnotator(a);
////			tea.annotateLog("Carrier_Appointment");
////			
////			System.out.println(i);
////			
////			LinkedList<String> idList = new LinkedList<String>();
////			idList.add("1");
////			
//////			System.out.println("MOD "+dp.getName());
////			ag.updateAttribute(a, dp, tea, idList, "Arrange_Delivery_Appointment_820", "raffa", 1000);
////			System.out.println(ag.getAttributes().get("1").values());
////
////			Instance newInstance = InstanceGenerator.updateInstance(ag.getAttributes().get("1"), i);
////			
////			System.out.println(newInstance);
////			
////			LinkedList<String> list = new LinkedList<String>();
////			list.add("Issue_Credit_Adjustment_606");
////			list.add("Issue_Debit_Adjustment_605");
////			list.add("null_607");
////			
////			System.out.println(oss.suggestDecisionPoint(null, "155", "Issue_Credit_Adjustment_606", list, null));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//			
//		System.out.println("Terminate - " + (new Date(System.currentTimeMillis())).toString());
	}
	
	public static void main2(String[] args) {
		InterfaceManager a = new InterfaceManager(InterfaceManager.YAWL, null);
//		
		OperationalSupportApp oss = new OperationalSupportApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 2");
		
		oss.start("Commercial", "", "");
//		oss.load("/home/stormfire/Dropbox/workspace/RiskInformedExecution/");
//		for(Entry<String, Map<String, Classifier>> entry : oss.decisionPointsPredictorsPostOffer.entrySet()) {
//			for(Entry<String, Classifier> entry2 : entry.getValue().entrySet()) {
//				System.out.println(entry.getKey());
//				System.out.println(entry2.getKey());
//				System.out.println(entry2.getValue());
//				System.out.println();
//			}
//		}	
		for(Entry<String, Map<String, Classifier>> entry : oss.decisionPointsPredictors.entrySet()) {
			for(Entry<String, Classifier> entry2 : entry.getValue().entrySet()) {
				System.out.println(entry.getKey());
				System.out.println(entry2.getKey());
				System.out.println(entry2.getValue());
//				System.out.println(oss.decisionPointsInstances.get(entry.getKey()).get(entry2.getKey()));
				System.out.println(oss.decisionPoints.get(entry.getKey()).get(entry2.getKey()));
				System.out.println();
			}
		}
	}
	
	public void load(String basePath) {
		
		File test = new File(basePath+"Bla");
		try {
			test.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File decisionPointsFile = new File(basePath+"decisionPointsFile");
		File decisionPointsPredictorsFile = new File(basePath+"decisionPointsPredictorsFile");
		File decisionPointsInstancesFile = new File(basePath+"decisionPointsInstancesFile");
		File decisionPointsPredictorsPostOfferFile = new File(basePath+"decisionPointsPredictorsPostOfferFile");
		File decisionPointsInstancesPostOfferFile = new File(basePath+"decisionPointsInstancesPostOfferFile");
		File listDecisionPointsFile = new File(basePath+"listDecisionPointsFile");
		File numberOfInstancesFile = new File(basePath+"numberOfInstancesFile");
		File totalNumberOfInstancesFile = new File(basePath+"totalNumberOfInstancesFile");
		
		try {
			if(decisionPointsFile.exists()) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(decisionPointsFile));				
				decisionPoints = (Map<String, Map<String, DecisionPoint>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsPredictorsFile));	
				decisionPointsPredictors = (Map<String, Map<String, Classifier>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsInstancesFile));	
				decisionPointsInstances = (Map<String, Map<String, Instances>>) ois.readObject();
	
				ois = new ObjectInputStream(new FileInputStream(decisionPointsPredictorsPostOfferFile));	
				decisionPointsPredictorsPostOffer = (Map<String, Map<String, Classifier>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsInstancesPostOfferFile));	
				decisionPointsInstancesPostOffer = (Map<String, Map<String, Instances>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(listDecisionPointsFile));	
				listDecisionPoints = (Map<String, Map<String, HashSet<String>>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(numberOfInstancesFile));	
				numberOfInstances = (Map<YSpecificationID, Long>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(totalNumberOfInstancesFile));	
				totalNumberOfInstances = (Map<YSpecificationID, Long>) ois.readObject();
				
			}
			
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void start(String specID, String basePath, String dirPath) {
		
		File decisionPointsFile = new File(basePath+"decisionPointsFile");
		File decisionPointsPredictorsFile = new File(basePath+"decisionPointsPredictorsFile");
		File decisionPointsInstancesFile = new File(basePath+"decisionPointsInstancesFile");
		File decisionPointsPredictorsPostOfferFile = new File(basePath+"decisionPointsPredictorsPostOfferFile");
		File decisionPointsInstancesPostOfferFile = new File(basePath+"decisionPointsInstancesPostOfferFile");
		File listDecisionPointsFile = new File(basePath+"listDecisionPointsFile");
		File numberOfInstancesFile = new File(basePath+"numberOfInstancesFile");
		File totalNumberOfInstancesFile = new File(basePath+"totalNumberOfInstancesFile");
		
		try {
			if(!decisionPointsFile.exists()) {
				insertNewSpecification(specID, new double[] {0.333, 0.333, 0.333}, dirPath);
	            
	            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(decisionPointsFile));				
				oos.writeObject(decisionPoints);
				
				oos = new ObjectOutputStream(new FileOutputStream(decisionPointsPredictorsFile));	
				oos.writeObject(decisionPointsPredictors);
				
				oos = new ObjectOutputStream(new FileOutputStream(decisionPointsInstancesFile));	
				oos.writeObject(decisionPointsInstances);
	
				oos = new ObjectOutputStream(new FileOutputStream(decisionPointsPredictorsPostOfferFile));	
				oos.writeObject(decisionPointsPredictorsPostOffer);
				
				oos = new ObjectOutputStream(new FileOutputStream(decisionPointsInstancesPostOfferFile));	
				oos.writeObject(decisionPointsInstancesPostOffer);
				
				oos = new ObjectOutputStream(new FileOutputStream(listDecisionPointsFile));	
				oos.writeObject(listDecisionPoints);
				
				oos = new ObjectOutputStream(new FileOutputStream(numberOfInstancesFile));	
				oos.writeObject(numberOfInstances);
				
				oos = new ObjectOutputStream(new FileOutputStream(totalNumberOfInstancesFile));	
				oos.writeObject(totalNumberOfInstances);
	            
			}else {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(decisionPointsFile));				
				decisionPoints = (Map<String, Map<String, DecisionPoint>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsPredictorsFile));	
				decisionPointsPredictors = (Map<String, Map<String, Classifier>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsInstancesFile));	
				decisionPointsInstances = (Map<String, Map<String, Instances>>) ois.readObject();
	
				ois = new ObjectInputStream(new FileInputStream(decisionPointsPredictorsPostOfferFile));	
				decisionPointsPredictorsPostOffer = (Map<String, Map<String, Classifier>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(decisionPointsInstancesPostOfferFile));	
				decisionPointsInstancesPostOffer = (Map<String, Map<String, Instances>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(listDecisionPointsFile));	
				listDecisionPoints = (Map<String, Map<String, HashSet<String>>>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(numberOfInstancesFile));	
				numberOfInstances = (Map<YSpecificationID, Long>) ois.readObject();
				
				ois = new ObjectInputStream(new FileInputStream(totalNumberOfInstancesFile));	
				totalNumberOfInstances = (Map<YSpecificationID, Long>) ois.readObject();
				
				if(!decisionPointsPredictors.containsKey(specID)) {

					insertNewSpecification(specID, null, dirPath);
		            
		            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(decisionPointsFile));				
					oos.writeObject(decisionPoints);
					
					oos = new ObjectOutputStream(new FileOutputStream(decisionPointsPredictorsFile));	
					oos.writeObject(decisionPointsPredictors);
					
					oos = new ObjectOutputStream(new FileOutputStream(decisionPointsInstancesFile));	
					oos.writeObject(decisionPointsInstances);
		
					oos = new ObjectOutputStream(new FileOutputStream(decisionPointsPredictorsPostOfferFile));	
					oos.writeObject(decisionPointsPredictorsPostOffer);
					
					oos = new ObjectOutputStream(new FileOutputStream(decisionPointsInstancesPostOfferFile));	
					oos.writeObject(decisionPointsInstancesPostOffer);
					
					oos = new ObjectOutputStream(new FileOutputStream(listDecisionPointsFile));	
					oos.writeObject(listDecisionPoints);
					
					oos = new ObjectOutputStream(new FileOutputStream(numberOfInstancesFile));	
					oos.writeObject(numberOfInstances);
					
					oos = new ObjectOutputStream(new FileOutputStream(totalNumberOfInstancesFile));	
					oos.writeObject(totalNumberOfInstances);
				}
				
			}
			
		}catch(ClassNotFoundException e) {
			// TODO: handle exception
		}catch(FileNotFoundException e) {
			// TODO: handle exception
		}catch(IOException e) {
			// TODO: handle exception
		}
		
//		System.out.println(decisionPointsPredictors.get("Carrier_Appointment").get("Arrange_Pickup_Appointment_819"));
//		System.out.println(decisionPointsInstances.get("Carrier_Appointment").get("Arrange_Pickup_Appointment_819"));
//		System.out.println(decisionPointsPredictorsPostOffer);
//		System.out.println(decisionPointsPredictors);
		
	}

	public OperationalSupportApp(InterfaceManager imStarting, InterfaceManager imUpdated, String classifierPathAndName, String classifierOptions) {
		
		this.imStarting = imStarting;
		this.imUpdated = imUpdated;
		this.classifierPathAndName = classifierPathAndName;
		this.classifierOptions = classifierOptions;
		
	}
	
	public void updateDecisionTreeSpecification(String specID, double[] weights, String dirPath) {
		
		insertNewSpecification(specID, weights, dirPath);
		
	}
	
	public void insertNewSpecification(String specID, double[] weights, String dirPath) {

        String specification = imStarting.getWorkflowDefinitionLayer().getSpecification(specID);
		weightMap.put(specID, weights);
		
		ConcurrentHashMap<String, Classifier> newDecisionPointsPredictors = new ConcurrentHashMap<String, Classifier>();
		ConcurrentHashMap<String, Instances> newDecisionPointsInstances = new ConcurrentHashMap<String, Instances>();
		ConcurrentHashMap<String, Classifier> newDecisionPointsPredictorsPostOffer = new ConcurrentHashMap<String, Classifier>();
		ConcurrentHashMap<String, Instances> newDecisionPointsInstancesPostOffer = new ConcurrentHashMap<String, Instances>();
		
		ConcurrentHashMap<String, DecisionPoint> newDecisionPoints = new ConcurrentHashMap<String, DecisionPoint>();
		
		DecisionPointDiscover dpd = new YAWL_DecisionPointDiscover();
		dpd.discoverDecisionPoints(specification);
		
		for(DecisionPoint dp : dpd.getDecisionPoints()) {
			
			newDecisionPoints.put(dp.getName(), dp);
			
		}
		
		decisionPoints.put(specID, newDecisionPoints);
		
		generateNewElementListDecisionPoints(specID);
		
		RiskAnnotator ra = new RiskAnnotator(imStarting);
		TaskExecutionAnnotator tea = new TaskExecutionAnnotator(imStarting);
		TimeBetweenTasksAnnotator tbta = new TimeBetweenTasksAnnotator(imStarting, dpd.getDecisionPoints());
		
		ra.annotateLog(specID, dirPath);
		tea.annotateLog(specID);
		tbta.annotateLog(specID);
		
		ArrayList<String> resourceValues = new ArrayList<String>();
		resourceValues.add("no-resource");
		resourceValues.addAll(imStarting.getRoleLayer().getAllID());
		
		AtomicInteger ai = new AtomicInteger();
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		for(DecisionPoint decisionPoint : dpd.getDecisionPoints()) {
			
			AttributeGeneratorThread dtg = new AttributeGeneratorThread(imStarting, decisionPoint, tea, tbta, ra, specID, resourceValues, ai, classifierPathAndName, classifierOptions);
			dtg.setNewPredictors(newDecisionPointsPredictors, newDecisionPointsInstances, newDecisionPointsPredictorsPostOffer, newDecisionPointsInstancesPostOffer);
			
			ai.incrementAndGet();
			
			dtg.start();
			
			while(ai.get() >= (cores*3/4) || ai.get() >= cpu) {
				try {
					Thread.currentThread().sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		while(ai.get() > 0) {
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		decisionPointsPredictors.put(specID, newDecisionPointsPredictors);
		decisionPointsInstances.put(specID, newDecisionPointsInstances);
		
		decisionPointsPredictorsPostOffer.put(specID, newDecisionPointsPredictorsPostOffer);
		decisionPointsInstancesPostOffer.put(specID, newDecisionPointsInstancesPostOffer);
		
	}
	
	private Object[] getRisksAsArray(LinkedList<Long> risks) {
		
//		HashSet<Long> obSet = new HashSet<Long>(risks);
//
//		if(obSet.size() == 1) obSet.add(new Long(-1));
//		
//		return obSet.toArray(new Object[0]);
		
		Object[] res = new Object[21];
		
		for(int i = 0; i<21; i++) {
			res[i] = Long.valueOf(i);
		}
		
		return res;
		           
	}

	public void generateEntireListDecisionPoints() {
		
		for(Entry<String, Map<String, DecisionPoint>> entry : decisionPoints.entrySet()) {
			
			generateNewElementListDecisionPoints(entry.getKey());
			
		}
		
	}
	
	private void generateNewElementListDecisionPoints(String specID) {
		
		Map<String, DecisionPoint> dps = decisionPoints.get(specID);
		Map<String, HashSet<String>> decisionPointEvent = null;
		
		if((decisionPointEvent = listDecisionPoints.get(specID)) == null){
			
			decisionPointEvent = new ConcurrentHashMap<String, HashSet<String>>();
			listDecisionPoints.put(specID, decisionPointEvent);
			
		}
		
		for(Entry<String, DecisionPoint> entry : dps.entrySet()) {
			
			DecisionPoint dp = entry.getValue();
			decisionPointEvent.put(dp.getName(), dp.getDirectSuccessors());
					
		}
		
	}

	private Long suggestDecisionPoint(String specID, String caseID, String nextActivityName, String resource, String decisionPointID, long currTime) {
		
		Map<String, HashSet<String>> decisionPointEvent = null;
		LinkedList<Long> result = new LinkedList<Long>();
		LinkedList<ActivityTuple> listResult = null;
		
		if((decisionPointEvent = listDecisionPoints.get(specID)) != null) {
			
			if(decisionPointID != null) {
				
				DecisionPoint dp = decisionPoints.get(specID).get(decisionPointID);
								
				classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);
				
			}else {
				
				for(Entry<String, HashSet<String>> entry : decisionPointEvent.entrySet()) {
					DecisionPoint dp = decisionPoints.get(specID).get(entry.getKey());

					if(dp.getDirectSuccessors().contains(nextActivityName)) { //&& dp.getSuccessors().containsAll(nextActivities)) {

						if(dp.getType() == DecisionPoint.PLACE) {
						
							classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);

						} else {
							
							LinkedList<String> list = new LinkedList<String>();
							list.add(caseID);
							
							if((listResult = imUpdated.getActivityLayer().getCounts(dp.getName(), true, list, true)) != null) {
								for(ActivityTuple at : listResult) {
									if(at.getCount() != null && Integer.parseInt(at.getCount()) > 0) {
										classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);
										break;
									}
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		if(result.isEmpty()) {
			if((decisionPointEvent = listDecisionPoints.get(specID)) != null) {
				
				if(decisionPointID != null) {
					
					DecisionPoint dp = decisionPoints.get(specID).get(decisionPointID);
									
					classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);
					
				}else {
					
					for(Entry<String, HashSet<String>> entry : decisionPointEvent.entrySet()) {
						DecisionPoint dp = decisionPoints.get(specID).get(entry.getKey());

						if(dp.getDirectSuccessors().contains(nextActivityName)) { //&& dp.getSuccessors().containsAll(nextActivities)) {

							if(dp.getType() == DecisionPoint.PLACE) {
							
								classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);

							} else {
								
								LinkedList<String> list = new LinkedList<String>();
								list.add(caseID);
								
								classifyDecisionPoint(dp, specID, caseID, nextActivityName, resource, result, currTime);
									
								
							}
							
						}
						
					}
					
				}
				
			}
		}
		
		if(result.size() < 1) return null;
		
//		long finalRes = 0;
//		
//		for(Long i : result) {
//			
//			finalRes += i;
//			
//		}
//		
//		finalRes = Math.round(finalRes / result.size());
//		
//		return finalRes;
		
		long max = Long.MIN_VALUE;
		
		for(Long i : result) {
			
			if(i > max) {
				
				max = i;
					
			}
			
		}
		
		return max;

	}
		
	private void classifyDecisionPoint(DecisionPoint dp, String specID, String caseID, String nextActivityName, String resource, LinkedList<Long> result, long currTime) {
		
		Instance instance = generateNewInstance(dp, specID, caseID, nextActivityName, true, resource, null, currTime);
		
		DecisionPredictor.fixMissingValues(instance);
		
//		Classifier classifier = decisionPointsPredictorsPostOffer.get(specID).get(dp.getName());
		Classifier classifier = decisionPointsPredictors.get(specID).get(dp.getName());
//		System.out.println(nextActivityName);
//		System.out.println(dp.getName());
//		System.out.println(classifier);
//		System.out.println(((DenseInstance) instance).dataset());
		
		try {
			
			boolean old = true;
			Long tmp = null;
			
			if(old) {
				
				double d = classifier.classifyInstance(instance);
				tmp = Long.parseLong(instance.classAttribute().value((int) d));

				tmp*=roundingFactor;
				
			}else {
				
				double d = classifier.classifyInstance(instance);
				double probability = classifier.distributionForInstance(instance)[(int) d];
				
				tmp = Long.parseLong(instance.classAttribute().value((int) d));
				tmp*=roundingFactor;
				
				tmp = Math.round(probability*tmp);
				
			}
			result.add(tmp);
		
		} catch (Exception e) { 
			e.printStackTrace();
		}	
		
	}
	
	public HashMap<WorkItemRecord, Long> suggestDecisionPoints(WorkItemRecord[] workItems, String resource, long currTime) {
		
		HashMap<WorkItemRecord, Long> result = new HashMap<WorkItemRecord, Long>();
		Long tmp = null;
		String decisionPointID = null;
		
//		LinkedList<String> nextActivities = new LinkedList<String>();
//		
//		for(WorkItemRecord workItem : workItems) {
//			
//			nextActivities.add(workItem.getTaskID());
//			
//		}
//		
//		if(lastWorkItem != null) {
//			decisionPointID = lastWorkItem.getTaskID();
//		}
		
		for(WorkItemRecord workItem : workItems) {
			
			tmp = suggestDecisionPoint(workItem.getSpecURI(), workItem.getRootCaseID(), workItem.getTaskID(), resource, decisionPointID, currTime);

			Long max = null;
			if((max = result.get(workItem.getTaskID())) != null) {
			
				if(tmp > max) {
				
					result.put(workItem, tmp);
				
				}
				
			}else {
				
				result.put(workItem, tmp);
				
			}
			
		}
		
		return result;
		
	}
	
	public HashMap<WorkItemRecord, Long> suggestExecutionDecisionPoints(WorkItemRecord workItem, String resource, String data) {
		
		String decisionPointID = null;
		
		decisionPointID = workItem.getTaskID();
		
		DecisionPoint dp = decisionPoints.get(workItem.getSpecURI()).get(decisionPointID);
		
		HashMap<WorkItemRecord, Long> result = new HashMap<WorkItemRecord, Long>();
		
		result.put(workItem, suggestExecutionDecisionPoint(dp, workItem.getSpecURI(), workItem.getRootCaseID(), resource, data));
		
		return result; 
		
	}

	private Long suggestExecutionDecisionPoint(DecisionPoint dp, String specID, String caseID, String resource, String data) {
		
		Instance instance = generateNewInstance(dp, specID, caseID, dp.getName(), false, resource, data, 0);
	
		DecisionPredictor.addData(instance, data);
		
		DecisionPredictor.fixMissingValues(instance);
				
		Classifier classifier = decisionPointsPredictorsPostOffer.get(specID).get(dp.getName());
		
//		System.out.println(instance);
//		System.out.println(instance.dataset());
//		System.out.println(classifier);
		
		Long tmp = null;
		
		try {
			double d = classifier.classifyInstance(instance);
			double probability = classifier.distributionForInstance(instance)[(int) d];
			tmp = Long.parseLong(instance.classAttribute().value((int) d));
			tmp*=roundingFactor;
			
			tmp = Math.round(probability*tmp);
			
//			System.out.println(tmp);
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return tmp;
		
	}
	
	public HashMap<String, Long> suggestResourceDecisionPoints(WorkItemRecord workItem) {
		
		String decisionPointID = null;
		
		decisionPointID = workItem.getTaskID();
		
		DecisionPoint dp = decisionPoints.get(workItem.getSpecURI()).get(decisionPointID);
		
		LinkedList<String> resources = new LinkedList<String>();
		String listResource = imUpdated.getActivityLayer().getDistribution(dp.getName(), true, workItem.getRootCaseID(), true, 1);
		Element initialSet = JDOMUtil.stringToElement(listResource);

		for(Element part : (List<Element>) initialSet.getChildren()) {

			resources.add(part.getValue());
			
		}
		
		return suggestResourceDecisionPoints(dp, workItem.getSpecURI(), workItem.getCaseID(), resources);
		
	}

	private HashMap<String, Long> suggestResourceDecisionPoints(DecisionPoint dp, String specID, String caseID, LinkedList<String> resources) {
		
		HashMap<String, Long> result = new HashMap<String, Long>();
		Long tmp = null;
		
		for(String resource : resources) {
		
			Instance instance = generateNewInstance(dp, specID, caseID, null, true, resource, null, 0);
		
			DecisionPredictor.fixMissingValues(instance);
								
			Classifier classifier = decisionPointsPredictors.get(specID).get(dp.getName());
		
			try {
				
				boolean old = false;
				
				if(old) {
				
					double d = classifier.classifyInstance(instance);
					tmp = Long.parseLong(instance.classAttribute().value((int) d));
				}else {

					double d = classifier.classifyInstance(instance);
					double probability = classifier.distributionForInstance(instance)[(int) d];
					
					tmp = Long.parseLong(instance.classAttribute().value((int) d));
					tmp*=roundingFactor;
					
					tmp = Math.round(tmp*probability);
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			result.put(resource, tmp);
			
		}	
		
		return result;
		
	}
	
	public synchronized void receiveEvent(YSpecificationID specID, String caseID, boolean precheck, String dirPath) {
		
		if(!precheck) {
			
			if(!numberOfInstances.containsKey(specID)) {

				numberOfInstances.put(specID, Long.valueOf(0));
				totalNumberOfInstances.put(specID, Long.valueOf(1));

				insertNewSpecification(specID.getUri(), weightMap.get(specID.getUri()), dirPath);
				
			}else {
			
				long n = numberOfInstances.get(specID);
				long nTotal = totalNumberOfInstances.get(specID);
				
				nTotal++;
				
				if(Math.log(nTotal) > n) {

					updateDecisionTreeSpecification(specID.getUri(), weightMap.get(specID.getUri()), dirPath);
					n++;
					
					if(Math.log(n) > 1) {
						
						n = Math.round(Math.log(n));
						nTotal = Math.round(Math.log(nTotal));
						
					}
					
				}
				
				numberOfInstances.put(specID, n);
				totalNumberOfInstances.put(specID, nTotal);
				
			}
			
		}

	}
	
	private Instance generateNewInstance(DecisionPoint dp, String specID, String caseID, String nextActivityName, boolean offer, String resource, String data, long currTime) {
		
		LinkedList<DecisionPoint> dps = new LinkedList<DecisionPoint>();
		dps.add(dp);
		
		TaskExecutionAnnotator tea = new TaskExecutionAnnotator(imUpdated);
		TimeBetweenTasksAnnotator tbta = new TimeBetweenTasksAnnotator(imUpdated, dps);
		
		tea.annotateLog(specID, caseID);
		tbta.annotateLog(specID, caseID);
		
		ArrayList<String> resourceValues = new ArrayList<String>();
		resourceValues.add("no-resource");
		resourceValues.addAll(imUpdated.getRoleLayer().getAllID());
		
		LinkedList<String> idList = new LinkedList<String>();
		idList.add(caseID);
		
		AttributeGenerator ag = new AttributeGenerator();
		ag.generateAttribute(imUpdated, dp, tea, tbta, idList, nextActivityName, resource, data, currTime);
		
		Instances instances = null;
		Instance instance = null;
		
		if(offer) {
			instances = decisionPointsInstances.get(specID).get(dp.getName());
			
			instance = InstanceGenerator.generateInstance(ag.getAttributes().get(caseID), null, null, instances);

			ag.updateAttribute(imUpdated, dp, tea, idList, nextActivityName, resource, currTime);
			instance = InstanceGenerator.updateInstance(ag.getAttributes().get(caseID), instance);
		
		}else {

			instances = decisionPointsInstancesPostOffer.get(specID).get(dp.getName());

			instance = InstanceGenerator.generateInstance(ag.getAttributesPostOffer().get(caseID), null, null, instances);
			
		}
			
//		instances.add(instance);
		instance.setDataset(instances);
		
		return instance;
//		return instances.lastInstance(); 
		
		
	}
	
	public InterfaceManager getImStarting() {
		return imStarting;
	}

	public InterfaceManager getImUpdated() {
		return imUpdated;
	}
	
	class AttributeGeneratorThread extends Thread {
		
		String classifierPathAndName = null;
		String classifierOptions = null;
		
		InterfaceManager imStarting = null;
		DecisionPoint decisionPoint = null;
		
		TaskExecutionAnnotator tea = null;
		TimeBetweenTasksAnnotator tbta = null;
		
		RiskAnnotator ra = null;
		String specID = null;
		ArrayList<String> resourceValues = null;
		AtomicInteger ai = null;
		
		Map<String, Classifier> newDecisionPointsPredictors = null;
		Map<String, Instances> newDecisionPointsInstances = null;
		
		Map<String, Classifier> newDecisionPointsPredictorsPostOffer = null;
		Map<String, Instances> newDecisionPointsInstancesPostOffer = null;
		
		
		private final HashMap<String, HashMap<String, LinkedList<String>>> listDecisionPoints = new HashMap<String, HashMap<String, LinkedList<String>>>();
		
		public AttributeGeneratorThread(InterfaceManager imStarting, DecisionPoint decisionPoint, TaskExecutionAnnotator tea,	TimeBetweenTasksAnnotator tbta,
				RiskAnnotator ra, String specID, ArrayList<String> resourceValues, AtomicInteger ai, String classifierPathAndName, String classifierOptions) {
//			System.out.println("Created AttributeGeneratorThread");
			this.imStarting = imStarting;
			this.decisionPoint = decisionPoint;
			this.tea = tea;
			this.tbta = tbta;
			this.ra = ra;
			this.specID = specID;
			this.resourceValues = resourceValues;
			this.ai = ai;
			this.classifierPathAndName = classifierPathAndName;
			this.classifierOptions = classifierOptions;
		}
		
		public void setNewPredictors(Map<String, Classifier> newDecisionPointsPredictors, Map<String, Instances> newDecisionPointsInstances, 
				Map<String, Classifier> newDecisionPointsPredictorsPostOffer, Map<String, Instances> newDecisionPointsInstancesPostOffer) {
			this.newDecisionPointsPredictors = newDecisionPointsPredictors;
			this.newDecisionPointsInstances = newDecisionPointsInstances;
			this.newDecisionPointsPredictorsPostOffer = newDecisionPointsPredictorsPostOffer;
			this.newDecisionPointsInstancesPostOffer = newDecisionPointsInstancesPostOffer;
			
		}
		
		@Override
		public void run() {
			AttributeGenerator ag = new AttributeGenerator();
			ag.generateAttribute(imStarting, decisionPoint, tea, tbta, null, null, null, null, 0);
			
			DecisionPredictor decisionPredictor = null;
			Classifier classifier = null;
			
			AtomicInteger aiInternal = new AtomicInteger();
			
			boolean incremented = false;
			
			if(decisionPoint.getType() == DecisionPoint.TASK) {
				//Pre-Offer
				
				DecisionTreeGeneratorThread dtgt1 = new DecisionTreeGeneratorThread(decisionPoint, ag, ra, specID, resourceValues, aiInternal, false, classifierPathAndName, classifierOptions);
				dtgt1.setNewPredictors(newDecisionPointsPredictors, newDecisionPointsInstances, newDecisionPointsPredictorsPostOffer, newDecisionPointsInstancesPostOffer);

				aiInternal.incrementAndGet();
				
				ai.incrementAndGet();
				incremented = true;
				
				dtgt1.start();
				
//				decisionPredictor = new DecisionPredictor(resourceValues, ag.getAttributesClasses(), ag.getStringNominal(), getRisksAsArray(ra.getRiskAnnotations().getRisksAsList()), specID+decisionPoint.getName(), 1000);
//				
//				for(String id : ag.getAttributes().keySet()) {
//					
//					decisionPredictor.addInstances(ag.getAttributes().get(id), ra.getRiskAnnotations().getAnnotation(id));
//				
//				}
//				
//				classifier = decisionPredictor.getEstimator(new String[] {"-C 0.25", "-U", "-M 2", ""+1000});
//				
//				newDecisionPointsPredictors.put(decisionPoint.getName(), classifier);
//				newDecisionPointsInstances.put(decisionPoint.getName(), decisionPredictor.getInstances());
								
			}
			
			DecisionTreeGeneratorThread dtgt2 = new DecisionTreeGeneratorThread(decisionPoint, ag, ra, specID, resourceValues, aiInternal, true, classifierPathAndName, classifierOptions);
			dtgt2.setNewPredictors(newDecisionPointsPredictors, newDecisionPointsInstances, newDecisionPointsPredictorsPostOffer, newDecisionPointsInstancesPostOffer);

			aiInternal.incrementAndGet();
			
			dtgt2.start();
			
//			//PostOffer
//			decisionPredictor = new DecisionPredictor(resourceValues, ag.getAttributesClassesPostOffer(), ag.getStringNominal(), getRisksAsArray(ra.getRiskAnnotations().getRisksAsList()), specID+decisionPoint.getName(), 1000);
//			
//			for(String id : ag.getAttributesPostOffer().keySet()) {
//
//				decisionPredictor.addInstances(ag.getAttributesPostOffer().get(id), ra.getRiskAnnotations().getAnnotation(id));
//			
//			}
//			
//			classifier = decisionPredictor.getEstimator(new String[] {"-C 0.25", "-U", "-M 2", ""+1000});
//			
//			newDecisionPointsPredictorsPostOffer.put(decisionPoint.getName(), classifier);
//			newDecisionPointsInstancesPostOffer.put(decisionPoint.getName(), decisionPredictor.getInstances());
			
			while(aiInternal.get() > 0) {
				if(incremented && aiInternal.get() < 2) {
					ai.decrementAndGet();
					incremented = false;
				}
				try {
					Thread.currentThread().sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(incremented) {
				ai.decrementAndGet();
			}
			
			ai.decrementAndGet();
			
//			System.out.println("AttributeGeneratorThread Terminated");
		}
		
	}
	
	class DecisionTreeGeneratorThread extends Thread {
		
		String classifierPathAndName = null;
		String classifierOptions = null;
		DecisionPoint decisionPoint = null;
		AttributeGenerator ag = null;
		RiskAnnotator ra = null;
		String specID = null;
		ArrayList<String> resourceValues = null;
		AtomicInteger ai = null;
		boolean preOffer = false;
		
		Map<String, Classifier> newDecisionPointsPredictors = null;
		Map<String, Instances> newDecisionPointsInstances = null;
		
		Map<String, Classifier> newDecisionPointsPredictorsPostOffer = null;
		Map<String, Instances> newDecisionPointsInstancesPostOffer = null;
		
		
		private final HashMap<String, HashMap<String, LinkedList<String>>> listDecisionPoints = new HashMap<String, HashMap<String, LinkedList<String>>>();
		
		public DecisionTreeGeneratorThread(DecisionPoint decisionPoint, AttributeGenerator ag, RiskAnnotator ra, String specID, ArrayList<String> resourceValues, AtomicInteger ai, boolean preOffer,
				String classifierPathAndName, String classifierOptions) {
//			System.out.println("Created DecisionTreeGeneratorThread");
			this.decisionPoint = decisionPoint;
			this.ag = ag;
			this.ra = ra;
			this.specID = specID;
			this.resourceValues = resourceValues;
			this.ai = ai;
			this.preOffer = preOffer;
			this.classifierPathAndName = classifierPathAndName;
			this.classifierOptions = classifierOptions;
		}

		public void setNewPredictors(Map<String, Classifier> newDecisionPointsPredictors, Map<String, Instances> newDecisionPointsInstances, 
				Map<String, Classifier> newDecisionPointsPredictorsPostOffer, Map<String, Instances> newDecisionPointsInstancesPostOffer) {
			this.newDecisionPointsPredictors = newDecisionPointsPredictors;
			this.newDecisionPointsInstances = newDecisionPointsInstances;
			this.newDecisionPointsPredictorsPostOffer = newDecisionPointsPredictorsPostOffer;
			this.newDecisionPointsInstancesPostOffer = newDecisionPointsInstancesPostOffer;
			
		}
		
		@Override
		public void run() {
			
			DecisionPredictor decisionPredictor = null;
			Classifier classifier = null;
			
			if(preOffer) {
				//Pre-Offer
				decisionPredictor = new DecisionPredictor(resourceValues, ag.getAttributesClasses(), ag.getStringNominal(), getRisksAsArray(ra.getRiskAnnotations().getRisksAsList()), specID+decisionPoint.getName(), 1000);

                for(Entry<String, HashMap<String, Object>> entry : ag.getAttributes().entrySet()) {
					
					decisionPredictor.addInstances(entry.getValue(), ra.getRiskAnnotations().getAnnotation(entry.getKey()));
				
				}
				
				classifier = decisionPredictor.getEstimator(classifierOptions, classifierPathAndName);
				
				newDecisionPointsPredictors.put(decisionPoint.getName(), classifier);
				newDecisionPointsInstances.put(decisionPoint.getName(), decisionPredictor.getInstances());
				
			}else {
				//PostOffer
				decisionPredictor = new DecisionPredictor(resourceValues, ag.getAttributesClassesPostOffer(), ag.getStringNominal(), getRisksAsArray(ra.getRiskAnnotations().getRisksAsList()), specID+decisionPoint.getName(), 1000);
				
				for(Entry<String, HashMap<String, Object>> entry : ag.getAttributesPostOffer().entrySet()) {
	
					decisionPredictor.addInstances(entry.getValue(), ra.getRiskAnnotations().getAnnotation(entry.getKey()));
				
				}
				
				classifier = decisionPredictor.getEstimator(classifierOptions, classifierPathAndName);
				
//				System.out.println(classifier);
				
				newDecisionPointsPredictorsPostOffer.put(decisionPoint.getName(), classifier);
				newDecisionPointsInstancesPostOffer.put(decisionPoint.getName(), decisionPredictor.getInstances());
			}
			
			ai.decrementAndGet();
			
//			System.out.println("DecisionTreeGeneratorThread Terminated");
		}
		
	}

	public Long classify(TupleClassifierName tupleClassifierName,
			String resource, long currTime) {
		
		WorkItemRecord[] wirs = new WorkItemRecord[] {tupleClassifierName.getWorkItem()};
		return suggestDecisionPoints(wirs, resource, currTime).get(tupleClassifierName.getWorkItem());
		
	}

	public TupleClassifierName buildTupleClassifierName(WorkItemRecord wir,
			String[] resources) {
		
		String specID = wir.getSpecURI();
		LinkedList<Classifier> classifiers = new LinkedList<Classifier>();
		LinkedList<ActivityTuple> listResult = null;
		
		Map<String, HashSet<String>> decisionPointEvent = null;
		
		if((decisionPointEvent = listDecisionPoints.get(specID)) != null) {
			
			for(Entry<String, HashSet<String>> entry : decisionPointEvent.entrySet()) {
				DecisionPoint dp = decisionPoints.get(specID).get(entry.getKey());

				if(dp.getDirectSuccessors().contains(wir.getTaskID())) {

					if(dp.getType() == DecisionPoint.PLACE ) {
					
						classifiers.add(decisionPointsPredictors.get(specID).get(dp.getName()));
//						if(decisionPointsPredictorsPostOffer.get(specID).get(dp.getName()) != null);
//						classifiers.add(decisionPointsPredictors.get(specID).get(dp.getName()));

					} else {
						
						LinkedList<String> list = new LinkedList<String>();
						list.add(wir.getRootCaseID());
						
						if((listResult = imUpdated.getActivityLayer().getCounts(dp.getName(), true, list, true)) != null) {
							for(ActivityTuple at : listResult) {
								if(at.getCount() != null && Integer.parseInt(at.getCount()) > 0) {
									classifiers.add(decisionPointsPredictors.get(specID).get(dp.getName()));
	//								if(decisionPointsPredictorsPostOffer.get(specID).get(dp.getName()) != null);
	//								classifiers.add(decisionPointsPredictorsPostOffer.get(specID).get(dp.getName()));
									break;
								}
								
							}
						}
						
					}
					
				}
				
			}
			
		}
		
		if(classifiers.isEmpty()) {
			if((decisionPointEvent = listDecisionPoints.get(specID)) != null) {
				
				for(Entry<String, HashSet<String>> entry : decisionPointEvent.entrySet()) {
					DecisionPoint dp = decisionPoints.get(specID).get(entry.getKey());

					if(dp.getDirectSuccessors().contains(wir.getTaskID())) {

						if(dp.getType() == DecisionPoint.PLACE ) {
						
							classifiers.add(decisionPointsPredictors.get(specID).get(dp.getName()));

						} else {
							
							LinkedList<String> list = new LinkedList<String>();
							list.add(wir.getRootCaseID());
							
							classifiers.add(decisionPointsPredictors.get(specID).get(dp.getName()));
							
						}
						
					}
					
				}
				
			}
		}
//		System.out.println("specID: "+specID);
//		System.out.println(classifiers.size());
//		for(Classifier c : classifiers) {
//			System.out.println(c);
//			System.out.println(c.toString());
//		}
		
		return new TupleClassifierName(this, classifiers, wir, resources);
	}

}
