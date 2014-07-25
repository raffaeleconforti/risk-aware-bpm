package org.yawlfoundation.yawl.risk.state.YAWL.Importers;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.sensors.databaseInterface.*;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

import java.io.*;
import java.util.*;


public class LogReader {

	private static Activity activityLayer = null;
	private static ActivityRole activityRoleLayer = null;
	
	public static void main(String[] args) throws Exception {
		
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/repairExample.xes");
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		String specification = null;
		try {
			File f = new File("/home/stormfire/InsuranceClaim.yawl");
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
		
		parameters.put("Xlog", log);
		parameters.put("specification", specification);
		parameters.put("resources", resources);
		
		
		
		InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);
		
		System.out.println(im.getWorkflowDefinitionLayer().getAllID().get(100));
		
		createTasks(im, new HashMap<String, Integer>(), new LinkedList<String>(), im.getWorkflowDefinitionLayer().getAllID().get(100));
	}
	
	public static void createTasks(InterfaceManager im, HashMap<String, Integer> taskWithToken, LinkedList<String> tasksLog, String processID) {
		
		LogReader x = new LogReader();
		if(activityLayer == null) {
			activityLayer = im.getActivityLayer();
		}
		if(activityRoleLayer == null) {
			activityRoleLayer = im.getActivityRoleLayer();
		}
		
		LinkedList<ActivityTuple> res = activityLayer.getAllRows(processID);
		LinkedList<ActivityRoleTuple> res2 = activityRoleLayer.getAllRows(processID);
		LogNode[] array = new LogNode[res.size()+res2.size()];
		int i = 0;
		
		for(ActivityTuple r : res) {
			String key = r.getName();
			String status = r.getStatus();
			String time = r.getTime();
			
			array[i] = x.new LogNode(key, status, time);
			i++;			
		}
		
		for(ActivityRoleTuple r : res2) {
			String key = r.getTaskName();
			String status = r.getStatus();
			String time = r.getTime();
						
			array[i] = x.new LogNode(key, status, time);
			i++;			
		}
		Arrays.sort(array);
		
		ArrayList<LogNode> list = new ArrayList<LogNode>();
		HashSet<String> set = new HashSet<String>();
		for(LogNode ln : array) {
			
			int status = Task.Unknown;
			if(ln.status.equals("Enabled")) { status = Task.Unoffered;}
			else if(ln.status.equals("offer")) { status = Task.Offered;}
			else if(ln.status.equals("allocate")) { status = Task.Allocated;}
			else if(ln.status.equals("start")) { status = Task.Started;}
			else if(ln.status.equals("complete")) { status = Task.Completed;}		
			else if(ln.status.equals("cancelled_by_case")) { status = Task.Unoffered;}
			taskWithToken.put(ln.name, status);
			
			if(!set.contains(ln.name)) {
				set.add(ln.name);
				list.add(ln);
			}else {
				boolean found = false;
				for(i = 0; i<list.size(); i++) {
					LogNode ln1 = list.get(i);
					if(ln1.equals(ln)) {
						if(!ln1.status.equals("Complete")) {
							list.set(i, ln);
							found = true;
							break;
						}
					}
				}
				if(found = false) list.add(ln);
			}
		}
				
		LogNode[] array2 = list.toArray(new LogNode[0]);
		Arrays.sort(array2);
		
		for(LogNode ln : array2) {
			tasksLog.add(ln.name);
		}
		
		ModelReader mr = new ModelReader();
		HashMap<String, LinkedList<Object[]>[]> dataFlows = new HashMap<String, LinkedList<Object[]>[]>();
		
		
//		String specificationXML = null;
//		try {
//			File f = new File("/home/stormfire/Payment.yawl");
//			InputStream is = new FileInputStream(f);
//			Writer writer = new StringWriter();
//			char[] buffer = new char[1024];
//			try {
//				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//				int n;
//				while ((n = reader.read(buffer)) != -1) {
//					writer.write(buffer, 0, n);
//				}
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				is.close();
//			}
//			specificationXML = writer.toString();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		mr.importModelWithDataFlow(specificationXML, dataFlows);

//		System.out.println(specificationXML);
		
//		System.out.println(im.getWorkflowDefinitionLayer().getSpecification(processID, im.getWorkflowDefinitionLayer().getURI(processID), im.getWorkflowDefinitionLayer().getVersion(processID)));
//				
		mr.importModelWithDataFlow(im.getWorkflowDefinitionLayer().getSpecification(processID, im.getWorkflowDefinitionLayer().getURI(processID), im.getWorkflowDefinitionLayer().getVersion(processID)), dataFlows);
		
//		tasksLog = new LinkedList<String>();
//		
//		tasksLog.add("Issue_Shipment_Invoice_594");
//		tasksLog.add("Produce_Freight_Invoice_595");
//		tasksLog.add("Issue_Shipment_Payment_Order_602");
//		tasksLog.add("null_4515");
//		tasksLog.add("Approve_Shipment_Payment_Order_593");
//		tasksLog.add("Process_Shipment_Payment_603");
		
		tasksLog = mr.updateTaskLog(tasksLog);
		
	}	
	
	class LogNode implements Comparable<LogNode>{
		
		String name;
		String status;
		String time;
		
		public LogNode(String name, String status, String time) {
			this.name = name;
			this.status = status;
			this.time = time;
		}
		
		@Override
		public int compareTo(LogNode arg0) {
			Long a = new Long(time);
			Long b = new Long(arg0.time);
			return a.compareTo(b);
		}
		
		@Override
		public String toString() {
			return name+" "+status+" "+time;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o != null && o instanceof LogNode) {
				LogNode o1 = (LogNode) o;
				return this.name.equals(o1.name);
			}
			return false;
		}
	}

}
