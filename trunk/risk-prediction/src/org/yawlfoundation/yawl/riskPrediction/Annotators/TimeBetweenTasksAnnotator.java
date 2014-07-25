package org.yawlfoundation.yawl.riskPrediction.Annotators;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TaskTimeExecutionAnnotation;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TimeBetweenTasksAnnotation;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPoint;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPointDiscover;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.YAWL_DecisionPointDiscover;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class TimeBetweenTasksAnnotator {

	private final Activity activityLayer;
	private final WorkflowDefinition workFlowDefinitionLayer;
	private LinkedList<DecisionPoint> decisionPoints = null;
	
	private TimeBetweenTasksAnnotation tbta = null;
	
	public static void main(String[] args) {
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Documents/Useless/log.xes");//"/home/stormfire/Documents/Useless/log.xes");

			String specification = null;
			try {
				File f = new File("/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl");
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
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);

			InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);

			DecisionPointDiscover dpd = new YAWL_DecisionPointDiscover();
			
			dpd.discoverDecisionPoints(specification);
			
			TimeBetweenTasksAnnotator ta = new TimeBetweenTasksAnnotator(im, dpd.getDecisionPoints());
			
			System.out.println(ta.annotateLog(null));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public TimeBetweenTasksAnnotator(InterfaceManager im, LinkedList<DecisionPoint> decisionPoints) { //XLog log, String specification) {

		this.activityLayer = im.getActivityLayer();
		
		this.workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
		this.decisionPoints = decisionPoints;
		
	}
	
	public TimeBetweenTasksAnnotation annotateLog(String specID) {
		
		tbta = new TimeBetweenTasksAnnotation();
		
		Vector<String> ids = workFlowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, specID, null);
		
		LinkedList<String> idList = new LinkedList<String>();
		HashSet<String> names = new HashSet<String>();
		
		File timeBetweenTasksAnnotationFile = new File("timeBetweenTasksAnnotationFile");
		
		for(DecisionPoint dp : decisionPoints) {
			names.addAll(dp.getPredecessors());
		}
		
		if(timeBetweenTasksAnnotationFile.exists()) {				
			
			ObjectInputStream ois;
			
			try {
				
				ois = new ObjectInputStream(new FileInputStream(timeBetweenTasksAnnotationFile));				
				tbta = (TimeBetweenTasksAnnotation) ois.readObject();
				
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
		
			for(String id : ids) {
				
				idList.clear();
				idList.add(id);
				
				for(String nameFirstActivity : names) {
	
					LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, nameFirstActivity, true, idList, true, Activity.Completed, true, null, false, false);
					
					if(v1 != null) {
						
						Long t1 = Long.parseLong(v1.get(0).getTime());
						
						for(String nameSecondActivity : names) {
						
							LinkedList<ActivityTuple> v2 = activityLayer.getTimes(null, false, nameSecondActivity, true, idList, true, Activity.Enabled, true, null, false, false);
							if(v2 == null) {
								v2 = activityLayer.getTimes(null, false, nameSecondActivity, true, idList, true, Activity.Executing, true, null, false, false);
							}
							
							if(v2 != null) {
						
								Long t2 = Long.parseLong(v2.get(0).getTime());
							
								if(t2-t1 > 0) {
									
									tbta.addAnnotation(id, nameFirstActivity, nameSecondActivity, t2-t1);
									
								}
							
							}
							
						}
					
					}
					
				}
					
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(timeBetweenTasksAnnotationFile));
				oos.writeObject(tbta);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return tbta;
		
	}
	
	public TimeBetweenTasksAnnotation annotateLog(String specID, String caseID) {
		
		tbta = new TimeBetweenTasksAnnotation();
		
//		Vector<String> namess = activityLayer.getNames(null, false, null, false, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		HashSet<String> names = new HashSet<String>();
		
		for(DecisionPoint dp : decisionPoints) {
			names.addAll(dp.getPredecessors());
		}
		
		idList.add(caseID);
		
		for(String nameFirstActivity : names) {

			LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, nameFirstActivity, true, idList, true, Activity.Completed, true, null, false, false);
			
			if(v1 != null) {
				
				Long t1 = Long.parseLong(v1.get(0).getTime());
				
				for(String nameSecondActivity : names) {
				
					LinkedList<ActivityTuple> v2 = activityLayer.getTimes(null, false, nameSecondActivity, true, idList, true, Activity.Enabled, true, null, false, false);
					
					if(v2 != null) {
				
						Long t2 = Long.parseLong(v2.get(0).getTime());
					
						if(t2-t1 > 0) {
							
							tbta.addAnnotation(caseID, nameFirstActivity, nameSecondActivity, t2-t1);
							
						}
					
					}
					
				}
			
			}
				
		}
		
		return tbta;
		
	}
	
	public TimeBetweenTasksAnnotation getTimeBetweenTasksAnnotations() {
		
		return tbta;
		
	}
	
}
