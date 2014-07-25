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
import org.yawlfoundation.yawl.riskPrediction.Annotations.ProcessTimeExecutionAnnotation;
import org.yawlfoundation.yawl.riskPrediction.Annotations.TaskTimeExecutionAnnotation;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class ProcessTimeExecutionAnnotator {

	private final SubProcess subProcessLayer;
	private final Activity activityLayer;
	private final WorkflowDefinition workFlowDefinitionLayer;
	
	private ProcessTimeExecutionAnnotation ptea = null;
	
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

			ProcessTimeExecutionAnnotator ta = new ProcessTimeExecutionAnnotator(im);
			
			System.out.println(ta.annotateLog());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ProcessTimeExecutionAnnotator(InterfaceManager im) { //XLog log, String specification) {

		this.subProcessLayer = im.getSubProcessLayer();
		
		this.activityLayer = im.getActivityLayer();
		
		this.workFlowDefinitionLayer = im.getWorkflowDefinitionLayer();
		
	}
	
	public ProcessTimeExecutionAnnotation annotateLog() {
		
		ptea = new ProcessTimeExecutionAnnotation();
		
		Vector<String> names = subProcessLayer.getNames(null, false, null, false, 0, 0, 0, false, null, false);
		Vector<String> ids = workFlowDefinitionLayer.getAllID();
		
		LinkedList<ActivityTuple> activityNames = activityLayer.getNames(null, false, null, false, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		
		File processTimeExecutionAnnotationFile = new File("processTimeExecutionAnnotationFile");
		
		if(processTimeExecutionAnnotationFile.exists()) {				
			
			ObjectInputStream ois;
			
			try {
				
				ois = new ObjectInputStream(new FileInputStream(processTimeExecutionAnnotationFile));				
				ptea = (ProcessTimeExecutionAnnotation) ois.readObject();
				
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
					
					Vector<String> v1 = subProcessLayer.getTimes(null, false, name, true, id, true, SubProcess.Start, true, null, false, false);
					
					Long t1 = Long.parseLong(v1.firstElement());
					
					ptea.addAnnotation(id, id, t1);
					
					if(v1 != null) {							
							
						for(ActivityTuple activityName : activityNames) {
							
							LinkedList<ActivityTuple> activityV1 = activityLayer.getTimes(null, false, activityName.getName(), true, idList, true, Activity.Enabled, true, null, false, false);
							
							if(activityV1 != null) {
			
								if(activityV1.get(0).getTime() != null) {
									
									Long t2 = Long.parseLong(activityV1.get(0).getTime());
					
									if(t1 != null && t2 != null) {
										
										ptea.addAnnotation(id, name, t2-t1);
										
									}
									
								}
							
							}
							
						}
					
					}
					
				}
				
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(processTimeExecutionAnnotationFile));
				oos.writeObject(ptea);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return ptea;
		
	}
	
	public ProcessTimeExecutionAnnotation annotateLog(String caseID) {
		
		ptea = new ProcessTimeExecutionAnnotation();
		
		Vector<String> names = subProcessLayer.getNames(null, false, null, false, 0, 0, 0, false, null, false);
		
		LinkedList<ActivityTuple> activityNames = activityLayer.getNames(null, false, null, false, 0, false, null, false);
		LinkedList<String> idList = new LinkedList<String>();
		
		File processTimeExecutionAnnotationFile = new File("processTimeExecutionAnnotationFile");
		
		if(processTimeExecutionAnnotationFile.exists()) {				
			
			ObjectInputStream ois;
			
			try {
				
				ois = new ObjectInputStream(new FileInputStream(processTimeExecutionAnnotationFile));				
				ptea = (ProcessTimeExecutionAnnotation) ois.readObject();
				
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
						
			idList.clear();
			idList.add(caseID);
			
			for(String name : names) {
				
				Vector<String> v1 = subProcessLayer.getTimes(null, false, name, true, caseID, true, SubProcess.Start, true, null, false, false);
				
				Long t1 = Long.parseLong(v1.firstElement());
				
				ptea.addAnnotation(caseID, caseID, t1);
				
				if(v1 != null) {
						
					for(ActivityTuple activityName : activityNames) {
						
						LinkedList<ActivityTuple> activityV1 = activityLayer.getTimes(null, false, activityName.getName(), true, idList, true, Activity.Enabled, true, null, false, false);
						
						if(activityV1 != null) {
		
							if(activityV1.get(0).getTime() != null) {
								
								Long t2 = Long.parseLong(activityV1.get(0).getTime());
				
								if(t1 != null && t2 != null) {
									
									ptea.addAnnotation(caseID, name, t2-t1);
									
								}
								
							}
						
						}
						
					}
				
				}
					
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(processTimeExecutionAnnotationFile));
				oos.writeObject(ptea);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return ptea;
		
	}
	
	public ProcessTimeExecutionAnnotation getProcessTimeAnnotations() {
		return ptea;
	}
	
}
