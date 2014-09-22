package org.yawlfoundation.yawl.sensors.timePrediction;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.sensors.SensorPredictor;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

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
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class TimePredictionClient {

    private static TimePredictionClient _me = null;
//	private SensorPredictor sp = new SensorPredictor(WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition"));
    private SensorPredictor sp = null;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReadLock readLock = lock.readLock();
    private final WriteLock writeLock = lock.writeLock();

    public static TimePredictionClient getInstanceForExperiments() {

        String logPath = "/home/user/DSS/InsuranceClaim.xes";
        String modelPath = "/home/user/DSS/InsuranceClaim.yawl";
        String resourcesPath = "/home/user/DSS/InsuranceClaim.ybkp";

        if (_me == null) {
        	_me = new TimePredictionClient();
			try {
				XLog xLog = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), logPath);
				
				String specification = null;
				try {
					File f = new File(modelPath);
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
					File f = new File(resourcesPath);
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
				
	        	InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, InterfaceManager.generateParameters(xLog, specification, resources));
	        	_me.sp = new SensorPredictor(im.getWorkflowDefinitionLayer());
	        	_me.sp.initializePredictors();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return _me;
    } 
    
    public static TimePredictionClient getInstance(InterfaceManager im) {
        if (_me == null) {
        	_me = new TimePredictionClient();
				
        	_me.sp = new SensorPredictor(im.getWorkflowDefinitionLayer());
        	_me.sp.initializePredictors();
        }
        return _me;
    }
    
    public static TimePredictionClient getInstance() {
        if (_me == null) {
        	_me = new TimePredictionClient();
        	_me.sp.initializePredictors();
        }
        return _me;
    }    
    
    public String getPrediction(String caseID, LinkedList<String> activities, String name, boolean task){
    	String res = "0";
    	readLock.lock();
    	res = sp.getPrediction(caseID, activities, name, task);
    	readLock.unlock();
    	return res;
    }
    
    public void updatePredictions(){
    	writeLock.lock();
		sp.updatePredictions();
    	writeLock.unlock();
    }
    
    public void updatePrediction(String URI, String version){
    	writeLock.lock();
		sp.updatePrediction(URI, version);
    	writeLock.unlock();
    }
    
}

