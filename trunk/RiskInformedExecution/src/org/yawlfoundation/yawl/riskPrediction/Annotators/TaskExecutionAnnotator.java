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
import org.yawlfoundation.yawl.riskPrediction.Annotations.TaskExecutionAnnotation;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.LogConverter;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.Prom_Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.Prom_WorkFlowDefinition;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class TaskExecutionAnnotator {

	private final Activity activityLayer;
	private final WorkflowDefinition workFlowDefinitionLayer;
	
	private TaskExecutionAnnotation ta = null;
	
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
			
//			String resources = null;
//			try {
//				File f = new File("/home/stormfire/orderfulfillment.ybkp");
//				InputStream is = new FileInputStream(f);
//				Writer writer = new StringWriter();
//				char[] buffer = new char[1024];
//				try {
//					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					int n;
//					while ((n = reader.read(buffer)) != -1) {
//						writer.write(buffer, 0, n);
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				resources = writer.toString();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);

			InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);
			
			TaskExecutionAnnotator ta = new TaskExecutionAnnotator(im);
			
			ta.annotateLog(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public TaskExecutionAnnotator(InterfaceManager im) { //XLog log, String specification) {

		this.activityLayer = im.getActivityLayer();
		
		this.workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
	}
	
	public TaskExecutionAnnotation annotateLog(String specID) {
		
		ta = new TaskExecutionAnnotation();
		
		Vector<String> ids = workFlowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, specID, null);
		
		Set<String> names = new HashSet<String>();
		for(String id : ids) {
			LinkedList<ActivityTuple> name = activityLayer.getNames(null, false, id, true, 0, false, null, false);
			for(ActivityTuple at : name) {
				names.add(at.getName());
			}
		}
		
		LinkedList<String> idList = new LinkedList<String>();
		
		File taskExecutionAnnotationFile = new File("taskExecutionAnnotationFile");
		
		if(taskExecutionAnnotationFile.exists()) {				
			
			ObjectInputStream ois;
			
			try {
				
				ois = new ObjectInputStream(new FileInputStream(taskExecutionAnnotationFile));				
				ta = (TaskExecutionAnnotation) ois.readObject();
				
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
				
				for(String name : names) {
	
					LinkedList<ActivityTuple> v = activityLayer.getCounts(name, true, idList, true);
					String count = "0";
					if(v != null) count = v.getFirst().getCount();
					
					ta.addAnnotation(id, name, Long.parseLong(count));
					
				}
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(taskExecutionAnnotationFile));
				oos.writeObject(ta);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return ta;
		
	}
	
	public TaskExecutionAnnotation annotateLog(String specID, String caseID) {
		
		ta = new TaskExecutionAnnotation();
		
		LinkedList<ActivityTuple> names = activityLayer.getNames(null, false, caseID, true, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		
		idList.add(caseID);
		
		if(names != null) {
			for(ActivityTuple name : names) {
	
				LinkedList<ActivityTuple> v = activityLayer.getCounts(name.getName(), true, idList, true);
				String count = "0";
				if(v != null) count = v.getFirst().getCount();
				
				ta.addAnnotation(caseID, name.getName(), Long.parseLong(count));
				
			}
		}
		
		return ta;
		
	}
	
	public TaskExecutionAnnotation getTaskAnnotations() {
		return ta;
	}
	
}
