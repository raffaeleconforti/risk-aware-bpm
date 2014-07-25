package org.yawlfoundation.yawl.riskPrediction.Annotators;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.LinkedList;
import java.util.Vector;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TaskTimeExecutionAnnotation;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class TaskTimeExecutionAnnotator {

	private final Activity activityLayer;
	private final WorkflowDefinition workFlowDefinitionLayer;
	
	private TaskTimeExecutionAnnotation tta = null;
	
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

			TaskTimeExecutionAnnotator ta = new TaskTimeExecutionAnnotator(im);
			
			System.out.println(ta.annotateLog());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public TaskTimeExecutionAnnotator(InterfaceManager im) { //XLog log, String specification) {

		this.activityLayer = im.getActivityLayer();
		
		this.workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
	}
	
	public TaskTimeExecutionAnnotation annotateLog() {
		
		tta = new TaskTimeExecutionAnnotation();
		
		LinkedList<ActivityTuple> names = activityLayer.getNames(null, false, null, false, 0, false, null, false);
		Vector<String> ids = workFlowDefinitionLayer.getAllID();
		LinkedList<String> idList = new LinkedList<String>();
		
		File taskTimeExecutionAnnotationFile = new File("taskTimeExecutionAnnotationFile");
		
		if(taskTimeExecutionAnnotationFile.exists()) {				
			
			ObjectInputStream ois;
			
			try {
				
				ois = new ObjectInputStream(new FileInputStream(taskTimeExecutionAnnotationFile));				
				tta = (TaskTimeExecutionAnnotation) ois.readObject();
				
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
				
				for(ActivityTuple name : names) {
	
	////				Vector<Vector<String>> v1 = activityLayer.getRows(null, false, name, true, idList, true, 0, 0, 0, true, null, false);
	//				Vector<Vector<String>> v1 = activityLayer.getTimes(null, false, name, true, idList, true, Activity.Enabled, true, null, false);
	//				
	//				if(v1 != null) {
	//
	//					Long t1 = Long.parseLong(v1.get(0).get(0));
	//					
	//					Vector<Vector<String>> v2 = activityLayer.getTimes(null, false, name, true, idList, true, Activity.Complete, true, null, false);
	//					
	//					if(v2 != null) {
	//				
	//						Long t2 = Long.parseLong(v2.get(0).get(0));
	//					
	//						tta.addAnnotation(id, name, t2-t1);
	//					
	//					}
	//				
	//				}
					
					LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, name.getName(), true, idList, true, Activity.EnabledExecutingComplete, true, null, false, false);
					
					if(v1 != null) {
	
						if(v1.size() > 1) {
							
							Long t1 = Long.parseLong(v1.getFirst().getTime());
						
							Long t2 = Long.parseLong(v1.getLast().getTime());
			
							if(t1 != null && t2 != null) {
								
								tta.addAnnotation(id, name.getName(), t2-t1);
								
							}
							
						}
					
					}
					
				}
				
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(taskTimeExecutionAnnotationFile));
				oos.writeObject(tta);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return tta;
		
	}
	
	public TaskTimeExecutionAnnotation annotateLog(String caseID) {
		
		tta = new TaskTimeExecutionAnnotation();
		
		LinkedList<ActivityTuple> names = activityLayer.getNames(null, false, null, false, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		
		idList.add(caseID);
		
		for(ActivityTuple name : names) {

////				Vector<Vector<String>> v1 = activityLayer.getRows(null, false, name, true, idList, true, 0, 0, 0, true, null, false);
//			Vector<Vector<String>> v1 = activityLayer.getTimes(null, false, name, true, idList, true, Activity.Enabled, true, null, false);
//			if(v1 == null) {
//				v1 = activityLayer.getTimes(null, false, name, true, idList, true, Activity.Executing, true, null, false);
//			}
//			
//			if(v1 != null) {
//
//				Long t1 = Long.parseLong(v1.get(0).get(0));
//				
//				Vector<Vector<String>> v2 = activityLayer.getTimes(null, false, name, true, idList, true, Activity.Complete, true, null, false);
//				
//				if(v2 != null) {
//			
//					Long t2 = Long.parseLong(v2.get(0).get(0));
//				
//					tta.addAnnotation(caseID, name, t2-t1);
//				
//				}
//			
//			}
			
			LinkedList<ActivityTuple> v1 = activityLayer.getTimes(null, false, name.getName(), true, idList, true, Activity.EnabledExecutingComplete, true, null, false, false);
			
			if(v1 != null) {

				if(v1.size() > 1) {
					
					Long t1 = Long.parseLong(v1.getFirst().getTime());
				
					Long t2 = Long.parseLong(v1.getLast().getTime());
	
					if(t1 != null && t2 != null) {
						
						tta.addAnnotation(caseID, name.getName(), t2-t1);
						
					}
					
				}
			
			}
			
		}
		
		return tta;
		
	}
	
	public TaskTimeExecutionAnnotation getTaskTimeAnnotations() {
		return tta;
	}
	
}
