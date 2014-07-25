package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

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
import java.util.Collections;
import java.util.LinkedList;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

/**
 *
 * @author Raffaele Conforti 26/04/2013
 *
 */

public class NetExecutedTasksList {
	
	public static void main(String[] args) throws FileNotFoundException, Exception {
		
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/repairExample.xes"); ///home/stormfire/Log.xes
        
		String specification = null;
		try {
			File f = new File("/home/stormfire/CarrierAppointment4.yawl");//"/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl"); //
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
			File f = new File("/home/stormfire/orderfulfillment.ybkp");
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
		
		InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, InterfaceManager.generateParameters(log, specification, resources));
		
		NetExecutedTasksList.setLayers(im.getActivityLayer());

		LinkedList<String> test = new LinkedList<String>();
		test.add("1");
		test.add("2");
		System.out.println(getActionResult(test, null));
	}

	private static Activity activityLayer = null;
	
	public static void setLayers(Activity activityLayer) {
		NetExecutedTasksList.activityLayer = activityLayer;
	}
	
	public static Object getActionResult(String workDefID, String netName) {
		LinkedList<String> workDefIDs = new LinkedList<String>();
		workDefIDs.add(workDefID);
		LinkedList<ActivityTuple> result = activityLayer.getRows(null, false, null, false, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
		
		LinkedList<String> executedTask = new LinkedList<String>();
		
		Collections.sort(result);
		
		for(ActivityTuple tuple : result) {
			executedTask.add(tuple.getName());
		}
		
		return executedTask;
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName) {
		
		LinkedList<ActivityTuple> result = activityLayer.getRows(null, false, null, false, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
		
		LinkedList<LinkedList<String>> executedTask = new LinkedList<LinkedList<String>>();
		
		Collections.sort(result);
		
		String currID = null;
		LinkedList<String> tmpRes = new LinkedList<String>();
		
		for(ActivityTuple tuple : result) {
			if(tuple.getWorkDefID().equals(currID)) {
				tmpRes.add(tuple.getName());
			}else {
				currID = tuple.getWorkDefID();
				tmpRes = new LinkedList<String>();
				executedTask.add(tmpRes);
				tmpRes.add(tuple.getName());
			}
		}
		
		return executedTask;
	}
	
}
