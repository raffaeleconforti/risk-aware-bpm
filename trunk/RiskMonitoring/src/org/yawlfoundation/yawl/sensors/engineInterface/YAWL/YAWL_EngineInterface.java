package org.yawlfoundation.yawl.sensors.engineInterface.YAWL;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;
import org.yawlfoundation.yawl.engine.interfce.interfaceX.InterfaceX_Service;
import org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine.SimulatedAnnealingAlgorithm;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Elements.Task;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.LogReader;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ResourceImporter;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;
import org.yawlfoundation.yawl.sensors.YSensor;
import org.yawlfoundation.yawl.sensors.YSensorManagerImplLayer;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.engineInterface.EngineInterface;

public class YAWL_EngineInterface extends InterfaceBWebsideController implements EngineInterface, InterfaceX_Service {
	
	private static YAWL_EngineInterface me = null;
	private static YSensorManagerImplLayer manager = null;
	private static HashMap<String, HashMap<String, boolean[]>> events = new HashMap<String, HashMap<String, boolean[]>>(); // boolean has 4 slot 0 to offered, 1 to allocated, 2 to started, 3 to completed 

//	@Override
//	public void registerManager(YSensorManagerImplLayer ysmi) {
//		manager = ysmi;		
//	}
	
	public static void main(String[] args) {
//		InterfaceManager im = new InterfaceManager(InterfaceManager.YAWL, null);
//		
//		HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
//		ResourceImporter.produceResourcesMap(im, resourceMap, "387");
//		System.out.println(resourceMap);
//		
//		HashMap<String, Integer> taskWithToken = new HashMap<String, Integer>();
//		LinkedList<String> tasksLog = new LinkedList<String>();
//		
//		System.out.println(2);
//		
//		LogReader.createTasks(im, taskWithToken, tasksLog, "387");
//		System.out.println(tasksLog);
//		System.out.println(taskWithToken);
		String m = "a : adf <participant sd><userid>sdsd</userid></participant>";
		String pre = m.substring(0, m.indexOf("<participant"));
		String post = m.substring(m.indexOf("<participant"), m.length());
		String userID = post.substring(post.indexOf("<userid>")+8, post.indexOf("</userid>"));
		System.out.println(pre);
		System.out.println(post);
		System.out.println(userID);
	}
	
	public YAWL_EngineInterface() {
		manager = YSensorManagerImplLayer.getInstance();
	}
	
	protected static void initInterfaces(Map<String, String> urlMap) {
		YSensorManagerImplLayer.initInterfaces(urlMap); 
	}

	@Override
	public void registerEvent(String workItemName, String caseID, int type) {
		HashMap <String, boolean[]> caseMap = null;
		if(events.containsKey(caseID)) {
			caseMap = events.get(caseID);
		}else {
			caseMap = new HashMap<String, boolean[]>();
		}
		boolean[] workItemMap = null;
		if(caseMap.containsKey(workItemName)) {
			workItemMap = caseMap.get(workItemName);
		}else {
			workItemMap = new boolean[4];
		}
		workItemMap[type] = true;
		caseMap.put(workItemName, workItemMap);
		events.put(caseID, caseMap);
		System.out.println("event registered");
		System.out.println(events);
	}
	
	@Override
    public void handleEngineInitialisationCompletedEvent() {
		manager.handleEngineInitialisationCompletedEvent(this);
	}

	@Override
	public void handleCheckCaseConstraintEvent(YSpecificationID specID,	String caseID, String data, boolean precheck) {
		manager.handleCheckCaseConstraintEvent(specID, caseID, data, precheck);
	}

	@Override
	public void handleCaseCancellationEvent(String caseID) {
		manager.handleCaseCancellationEvent(caseID);		
	}
	
	@Override
    public void handleWorkItemStatusChangeEvent(WorkItemRecord workItem, String oldStatus, String newStatus) {
		System.out.println(workItem.getRootCaseID()+" "+workItem.getTaskName()+" "+oldStatus+" "+newStatus);
		String caseID = workItem.getRootCaseID();
		String workItemName = workItem.getTaskName();
		if(events.containsKey(caseID)) {
			HashMap <String, boolean[]> caseMap = events.get(caseID);
			if(caseMap.containsKey(workItemName)) {
				boolean[] workItemMap = caseMap.get(workItemName);
		    	if (workItemMap[0] & newStatus.equals(WorkItemRecord.statusResourceOffered)){
					manager.event(caseID, workItemName, 0);
				}else if (workItemMap[1] & newStatus.equals(WorkItemRecord.statusResourceAllocated)){
					manager.event(caseID, workItemName, 1);
				}else if (workItemMap[2] & newStatus.equals(WorkItemRecord.statusResourceStarted)){
					manager.event(caseID, workItemName, 2);
				}else if(workItemMap[2] & newStatus.equals(WorkItemRecord.statusExecuting)){
					manager.event(caseID, workItemName, 2);
				}
			}
	    }
    }
	
	@Override
	public void handleCheckWorkItemConstraintEvent(WorkItemRecord workItem, String data, boolean precheck) {
		String caseID = workItem.getRootCaseID();
		String workItemName = workItem.getTaskName();
		if(events.containsKey(caseID)) {
			HashMap <String, boolean[]> caseMap = events.get(caseID);
			if(caseMap.containsKey(workItemName)) {
				boolean[] workItemMap = caseMap.get(workItemName);
		    	if (workItemMap[0] & precheck){
		    		manager.event(caseID, workItemName, 0);
				}else if (workItemMap[3] & !precheck){
					manager.event(caseID, workItemName, 3);
				}
			}
	    }
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEnabledWorkItemEvent(WorkItemRecord enabledWorkItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleWorkItemAbortException(WorkItemRecord wir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTimeoutEvent(WorkItemRecord wir, String taskList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleResourceUnavailableException(WorkItemRecord wir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleConstraintViolationException(WorkItemRecord wir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String engineConnect(String user, String password) throws IOException {
		return connect(user, password);
	}

	@Override
	public boolean engineCheckConnection(String connection) throws IOException {
		return checkConnection(connection);
	}
	
	@Override
	public EngineInterface getEngineInstance() {
    	return getInstance();
    }

    public static EngineInterface getInstance() {
    	if (me == null) me = new YAWL_EngineInterface();
    	return me;
    }

	@Override
	public String getBackEndURI() {
		return _interfaceBClient.getBackEndURI(); 
	}
	
	@Override
	public String mitigate(String caseID) {
		try {
		List<YSensor> sensors = manager.getSensors(caseID);

		InterfaceManager im = manager.getInterfaceManager();
		HashMap<String, Integer> taskWithToken = new HashMap<String, Integer>();
		LinkedList<String> tasksLog = new LinkedList<String>();
		
		LogReader.createTasks(im, taskWithToken, tasksLog, caseID);
		
		HashMap<String, String> mappingNames = new HashMap<String, String>();
		HashMap<String, Object> variables = new HashMap<String, Object>();


		for(YSensor sensor : sensors) {
			mappingNames.putAll(sensor.getMappingName());
			variables.putAll(sensor.getVariables());
		}

		StateYAWLProcess s = new StateYAWLProcess(caseID, sensors.get(0).getSpecification(), mappingNames, new Random(10));

		HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
		ResourceImporter.produceResourcesMap(im, resourceMap, caseID);

		ImporterYState.importModel(im, s, variables, resourceMap, taskWithToken, tasksLog);

		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();

		LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 1);

		StateYAWLProcess[] array = set.toArray(new StateYAWLProcess[0]);
		
		StringBuilder sb = new StringBuilder();
		int count = 1;
		
		for(int i = 0; i<array.length && count<6; i++){
			if(array[i] != null) {
				StateYAWLProcess s1 = array[i];
				
				StringBuilder sb1 = new StringBuilder();
				for(String m : s1.modifications) {
					if(m.contains("<participant")) {
						String pre = m.substring(0, m.indexOf("<participant"));
						String post = m.substring(m.indexOf("<participant"), m.length());
						String userID = post.substring(post.indexOf("<userid>")+8, post.indexOf("</userid>"));
						sb1.append(pre);
						sb1.append(userID);
					}else {
						sb1.append(m);
					}
				}
				
				if(!sb.toString().contains(sb1.toString())) {
					sb.append("Solution" + count +"\n");
					sb.append(sb1.toString()+"\n"+"\n");
					count++;
				}
			}
		}
		if(sb.length() == 0) {
			sb.append("No Mitigation Found");
		}
		
		return sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
