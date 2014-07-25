package org.yawlfoundation.yawl.riskPrediction.OperationalSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;
import org.yawlfoundation.yawl.engine.interfce.interfaceX.InterfaceX_Service;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

public class OperationalSupportService extends InterfaceBWebsideController implements InterfaceX_Service {
		
	private static OperationalSupportService me = null;
	private static OperationalSupportApp oss = null;

	public OperationalSupportService(InterfaceManager im, String basePath, String classifierPathAndName, String classifierOptions) {
		
		me = this;
		
//		InterfaceManager im = new InterfaceManager(InterfaceManager.YAWL, null);
		im = new InterfaceManager(InterfaceManager.YAWL, null);
		
		oss = new OperationalSupportApp(im, im, classifierPathAndName, classifierOptions);
		oss.load(basePath);
		
	}
	
	public void initInterfaces(Map<String, String> urlMap) {
		
		int type = Integer.parseInt(urlMap.get("type"));
		String basePath = urlMap.get("basePath");
		
		Map<String, Object> parameters = null;
		
		if(urlMap.containsKey("Xlog")) {
			
			try {
				parameters = new HashMap<String, Object>();
				
				InputStream is = new ByteArrayInputStream(urlMap.get("Xlog").getBytes());
				
				parameters.put("Xlog", ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), is));
				parameters.put("specification", urlMap.get("specification"));
				parameters.put("resources", urlMap.get("resources"));
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		String classifierPathAndName = null;
		
		if(urlMap.containsKey("classifierPathAndName")) {
			classifierPathAndName = urlMap.get("classifierPathAndName");
		}
		
		String classifierOptions = null;
		
		if(urlMap.containsKey("classifierOptions")) {
			classifierOptions = urlMap.get("classifierOptions");
		}
		InterfaceManager im = new InterfaceManager(type, parameters);
		
		OperationalSupportService oss = new OperationalSupportService(im, basePath, classifierPathAndName, classifierOptions);
	}
	
	@Override
    public void handleEngineInitialisationCompletedEvent() {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCheckCaseConstraintEvent(YSpecificationID specID, String caseID, String data, boolean precheck) {
		
		oss.receiveEvent(specID, caseID, precheck);
		
	}

	@Override
	public void handleCaseCancellationEvent(String caseID) {

		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void handleWorkItemStatusChangeEvent(WorkItemRecord workItem, String oldStatus, String newStatus) {

		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleCheckWorkItemConstraintEvent(WorkItemRecord workItem, String data, boolean precheck) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
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

	public OperationalSupportService getOperationalSupportClientInstance(InterfaceManager im) {
    	
		return getInstance();
		
    }

    public static OperationalSupportService getInstance() {
    	
    	if (me == null) me = new OperationalSupportService(null, null, null, null);
    	return me;
    	
    }
    
    public HashMap<WorkItemRecord, Long> suggestDecisionPoints(WorkItemRecord[] workItems, String resource, long currTime) {
    	
    	return oss.suggestDecisionPoints(workItems, resource, currTime);
    	
    }
    
    public HashMap<WorkItemRecord, Long> suggestExecutionDecisionPoints(WorkItemRecord workItem, String resource, String data) {
    	
    	return oss.suggestExecutionDecisionPoints(workItem, resource, data);
    	
    }
    
    public HashMap<String, Long> suggestrResourceDecisionPoints(WorkItemRecord workItem) {
    	
    	return oss.suggestResourceDecisionPoints(workItem);
    	
    }

}