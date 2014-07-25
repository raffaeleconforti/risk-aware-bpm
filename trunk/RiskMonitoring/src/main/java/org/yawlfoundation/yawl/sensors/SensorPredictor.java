package org.yawlfoundation.yawl.sensors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

public class SensorPredictor implements Serializable {
	
	private final ConcurrentHashMap<String, TimePredictor> tp = new ConcurrentHashMap<String, TimePredictor>();
	private WorkflowDefinition wd = null;
	
	public static void main(String[] args) {
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
				File f = new File("/home/stormfire/Insurance.ybkp");
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
			SensorPredictor sp = new SensorPredictor(new InterfaceManager(InterfaceManager.PROM, InterfaceManager.generateParameters(log, specification, resources)).getWorkflowDefinitionLayer());
				
			sp.initializePredictors();			
			
			LinkedList<String> a = new LinkedList<String>();
			a.add("Open_Claim_3");
			System.out.println(sp.getPrediction("394", a, "Open_Claim_3", true));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public SensorPredictor(WorkflowDefinition wd) {
		this.wd = wd;
	}
	
	public void initializePredictors() {
		tp.clear();
		for(String caseID : wd.getAllID()) {
			String URI = wd.getURI(caseID);
			if(!tp.containsKey(URI)) {
				XLog log = null;
				try {
					log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), new ByteArrayInputStream(wd.getOpenXESLog(caseID, URI, wd.getVersion(caseID)).getBytes()));

					tp.put(URI, new TimePredictor(log));
//					System.out.println(tp.get(URI));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getPrediction(String caseID, LinkedList<String> activities, String name, boolean task) {
		String URI = wd.getURI(caseID);
		
		if(task) {
			return tp.get(URI).getTaskDuration(activities, name);
		}else {
			return tp.get(URI).getNetDuration(activities);
		}
		
	}
	
	public void updatePredictions() {
		for(String caseID : wd.getAllID()) {
			String URI = wd.getURI(caseID);
			if(!tp.containsKey(URI)) {
				XLog log = null;
				try {
					log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), new ByteArrayInputStream(wd.getOpenXESLog(caseID, URI, wd.getVersion(caseID)).getBytes()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tp.put(URI, new TimePredictor(log));
			}
		}
	}
	
	public void updatePrediction(String URI, String version) {
		for(String caseID : wd.getAllID()) {
			String currURI = wd.getURI(caseID);
			if(currURI.equals(URI)) {
				XLog log = null;
				try {
					log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), new ByteArrayInputStream(wd.getOpenXESLog(caseID, URI, version).getBytes()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tp.put(URI, new TimePredictor(log));
				break;
			}
		}
	}
	
}
