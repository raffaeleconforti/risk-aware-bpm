package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Place;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ModelReader;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.OperationalSupportApp;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationSplitter;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.TupleClassifierName;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class OperationalSupportMultiCaseApp {
	
	private OperationalSupportApp osa = null;
	
	private HashSet<WorkItemRecord> offered = new HashSet<WorkItemRecord>();
	private HashSet<WorkItemRecord> executing = new HashSet<WorkItemRecord>();
	
	private HashMap<String, WorkItemRecord> use = new HashMap<String, WorkItemRecord>();
	private HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
	private HashSet<HashSet<WorkItemRecord>> choice = new HashSet<HashSet<WorkItemRecord>>();
	private HashSet<HashSet<String>> choiceString = new HashSet<HashSet<String>>();
	
	private HashMap<String, Object[]> models = new HashMap<String, Object[]>();
	
	private HashMap<String, WorkItemRecord> schedule = new HashMap<String, WorkItemRecord>();

	private InterfaceManager imStarting = null;
	private InterfaceManager imUpdated = null;

	private AtomicBoolean changed = new AtomicBoolean(false);
	private Semaphore semaphore = new Semaphore(1);
	
	private long approximation = 0;
	
	public static void main(String[] args) {
		
		String base = null;
		File dir = new File("/home/stormfire");
		if(dir.exists()) {
			base = "stormfire";
		}else {
			base = "conforti";
		}
		
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Demo/Log.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/media/Data/SharedFolder/Demo/Test.xes");
//			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Documents/Useless/log.xes");
			
			String specification = null;
			try {
				File f = new File("/home/stormfire/InsuranceClaim.yawl");
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
				File f = new File("/home/"+base+"/Insurance.ybkp");
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
			
			OperationalSupportMultiCaseApp osmca = new OperationalSupportMultiCaseApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 2", 1);
			osmca.start("InsuranceClaim", "");
			
			String[] resourcesList = new String[] {"a", "b"};
			
			TupleClassifierName tcn = osmca.osa.buildTupleClassifierName(new WorkItemRecord("1", "Approve_Or_Decline_6", "InsuranceClaim", null, null), resourcesList);
			
			DurationSplitter ds = new DurationSplitter(1000);
			String[] res = new String[] {"a", "b"};
			HashMap<String, HashSet<long[]>> set = ds.generateSplits(tcn.getClassifiers(), 
					new WorkItemRecord("1", "Approve_Or_Decline_6", "InsuranceClaim", null, null), res, tcn, 10000L, System.currentTimeMillis());
			for(Entry<String, HashSet<long[]>> entry : set.entrySet()) {
				for(long[] split : entry.getValue()) {
					System.out.println(entry.getKey()+" "+split[0]+" "+split[1]);
				}
			}
			System.out.println(ds.getRiskMap());
			
			WorkItemRecord wir = new WorkItemRecord("1", "Approve_Or_Decline_6", a.getWorkflowDefinitionLayer().getSpecification("InsuranceClaim"), null, null);
			WorkItemRecord wir1 = new WorkItemRecord("1", "Request_Assessment_8", a.getWorkflowDefinitionLayer().getSpecification("InsuranceClaim"), null, null);
			osmca.addChoice(wir);
			osmca.addChoice(wir1);
//			System.out.println(osmca.choice);
			osmca.removeChoiceOtherOptions(wir);
//			System.out.println(osmca.choice);
			osmca.removeChoice(wir);
//			System.out.println(osmca.choice);
			

			DurationFunction df = new DurationFunction(a, a);
			System.out.println(df.getDuration(wir, 1));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		System.out.println("Terminate - " + (new Date(System.currentTimeMillis())).toString());
	}

	public OperationalSupportMultiCaseApp(InterfaceManager imStarting, InterfaceManager imUpdated, String classifierPathAndName, String classifierOptions, long approximation) {
		
		this.imStarting = imStarting;
		this.imUpdated = imUpdated;
		this.osa = new OperationalSupportApp(imStarting, imUpdated, classifierPathAndName, classifierOptions);
		this.approximation = approximation;
		
	}
	
	public void start(String specID, String basePath) {
		
		osa.start(specID, basePath);
		
	}
	
	public void updateDecisionTreeSpecification(String specID) {
		
		osa.insertNewSpecification(specID);
		
	}
	
	public void insertNewSpecification(String specID) {
		
		osa.insertNewSpecification(specID);
		
	}
	
	public void generateEntireListDecisionPoints() {
		
		osa.generateEntireListDecisionPoints();
		
	}
	
	public HashMap<WorkItemRecord, Long> suggestDecisionPoints(WorkItemRecord[] workItems, String resource, long currTime) {
		
		return osa.suggestDecisionPoints(workItems, resource, currTime);
		
	}
	
	public HashMap<WorkItemRecord, Long> suggestExecutionDecisionPoints(WorkItemRecord workItem, String resource, String data) {
		
		return osa.suggestExecutionDecisionPoints(workItem, resource, data);
		
	}

	public HashMap<String, Long> suggestResourceDecisionPoints(WorkItemRecord workItem) {
		
		return suggestResourceDecisionPoints(workItem);
		
	}
	
	public synchronized void receiveEvent(YSpecificationID specID, String caseID, boolean precheck) {
		
		osa.receiveEvent(specID, caseID, precheck);

	}
	
	public synchronized void receiveEvent(WorkItemRecord workItem, boolean precheck, boolean started) {
		
		try {
			semaphore.acquire();
		
			if(started) {
				
				offered.remove(workItem);
				executing.add(workItem);
				addUse(workItem);
				removeChoiceOtherOptions(workItem);
				
			}else if(precheck) { //enabled
				
				offered.add(workItem);
				addCan(workItem);
				addChoice(workItem);
				
			}else { //completed or canceled 
				
				offered.remove(workItem);
				executing.remove(workItem);
				removeUse(workItem);
				removeCan(workItem);
				removeChoice(workItem);
				
			}
			
			changed.set(true);
			semaphore.release();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addChoice(WorkItemRecord workItem) {
		
		Task[] tasks = null;
		Place[] places = null;
		Object[] model = null;
		if((model = models.get(workItem.getSpecURI())) == null) {
			ModelReader mr = new ModelReader();
			mr.importModel(imUpdated.getWorkflowDefinitionLayer().getSpecification(workItem.getSpecURI()));
			model = mr.getModel();
			models.put(workItem.getSpecURI(), model);
			
			tasks = (Task[]) model[0];
			places = (Place[]) model[1];
			
			int tasksLength = tasks.length;
			
			HashSet<String> choiceWorkItems = null;
			
			for(Task task : tasks) {
				choiceWorkItems = new HashSet<String>();
				choiceWorkItems.add(task.taskName);
				for(int i : task.linkedFrom) {
					if(i >= tasksLength) {
						if(places[i-tasksLength].linkedTo.size() > 1) {
							for(int j : places[i-tasksLength].linkedTo) {
								choiceWorkItems.add(tasks[j].taskName);
							}
						}
					}
				}
				choiceString.add(choiceWorkItems);
			}
		}

		for(HashSet<String> set : choiceString) {
			if(set.contains(workItem.getTaskID())) {
				boolean newChoice = true;
				for(HashSet<WorkItemRecord> innerChoice : choice) {
					WorkItemRecord wir = innerChoice.iterator().next();
					if(set.contains(wir.getTaskID())) {
						innerChoice.add(workItem);
						newChoice = false;
						break;
					}
				}
				if(newChoice) {
					HashSet<WorkItemRecord> innerChoice = new HashSet<WorkItemRecord>();
					innerChoice.add(workItem);
					choice.add(innerChoice);
				}
				break;
			}
		}
	}
	
	private void removeChoiceOtherOptions(WorkItemRecord workItem) {
		for(HashSet<WorkItemRecord> innerChoice : choice) {
			if(innerChoice.contains(workItem)) {
				innerChoice.clear();
				innerChoice.add(workItem);
				break;
			}
		}
	}

	private void removeChoice(WorkItemRecord workItem) {
		HashSet<WorkItemRecord> rem = null;
		for(HashSet<WorkItemRecord> innerChoice : choice) {
			if(innerChoice.contains(workItem)) {
				rem = innerChoice;
				break;
			}
		}
		choice.remove(rem);
	}
	
	private void addCan(WorkItemRecord workItem) {
		
		String dis = imUpdated.getActivityLayer().getDistribution(workItem.getTaskID(), true, workItem.getRootCaseID(), true, imUpdated.getActivityLayer().OfferDis);
		Element el = JDOMUtil.stringToElement(dis);
		
		System.out.println(dis);
		for(Element elem : (List<Element>) el.getChildren()) {
			String res = elem.getValue();
			HashSet<WorkItemRecord> set = null;
			if((set = can.get(res)) == null) {
				set = new HashSet<WorkItemRecord>();
				can.put(res, set);
			}
			set.add(workItem);
		}
		
	}
	
	private void removeCan(WorkItemRecord workItem) {
		
		LinkedList<String> remove = new LinkedList<String>();
		for(Entry<String, HashSet<WorkItemRecord>> entry : can.entrySet()) {
			
			entry.getValue().remove(workItem);
			if(entry.getValue().size() == 0) remove.add(entry.getKey());
			
		}
		
		for(String res : remove) {
			can.remove(res);
		}
		
	}

	private void addUse(WorkItemRecord workItem) {
		
		LinkedList<String> idList = new LinkedList<String>();
		idList.add(workItem.getRootCaseID());
		
		String taskID = imUpdated.getActivityLayer().getIDs(workItem.getTaskID(), true, idList, true, 0, false, null, false, false).getFirst().getTaskID();
		
		String res = imUpdated.getActivityRoleLayer().getRows(taskID, true, null, false, 0L, 0, imUpdated.getActivityRoleLayer().Start, true).getFirst().getRoleID();
		
		use.put(res, workItem);
		
	}
	
	private void removeUse(WorkItemRecord workItem) {
		
		String res = null;
		for(Entry<String, WorkItemRecord> entry : use.entrySet()) {
			
			if(entry.getValue().equals(workItem)) {
				res = entry.getKey();
				break;
			}
			
		}
		if(res != null) use.remove(res);
	}
	
	public WorkItemRecord getWorkItem(String resource, long currTime) {
		if(changed.get()) {
			computeSchedule(currTime);
		}
		return schedule.get(resource);
	}
	
	public HashMap<String, WorkItemRecord> getSchedule(long currTime) {
		if(changed.get()) {
			computeSchedule(currTime);
		}
		return schedule;
	}
	
	private void computeSchedule(long currTime) {
		
		try {
			semaphore.acquire();
		
			String[] resources = can.keySet().toArray(new String[0]);
			LinkedList<TupleClassifierName> listTCN = new LinkedList<TupleClassifierName>();
			
			WorkItemRecord[] totalWorkItem = new WorkItemRecord[offered.size()+executing.size()];
			
			int i = 0;
			for(WorkItemRecord wir : offered) {
				TupleClassifierName tcn = osa.buildTupleClassifierName(wir, resources);
				listTCN.add(tcn);
				totalWorkItem[i] = wir;
				i++;
			}
			
			for(WorkItemRecord wir : executing) {
				TupleClassifierName tcn = osa.buildTupleClassifierName(wir, resources);
				listTCN.add(tcn);
				totalWorkItem[i] = wir;
				i++;
			}
			
			DurationFunction df = new DurationFunction(imStarting, imUpdated);
			
			ILPBuilderSCIPNewMax ilp = new ILPBuilderSCIPNewMax();
			ilp.findMaxDuration(resources, totalWorkItem, df);
			
			DurationSplitter ds = new DurationSplitter(listTCN, 1000, currTime, approximation);
			ilp.execute(0.5, resources, executing.toArray(new WorkItemRecord[0]), 
					offered.toArray(new WorkItemRecord[0]), df, use, can, 
					new RiskFunction(ds), choice, schedule, ds);
			
			changed.set(false);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
