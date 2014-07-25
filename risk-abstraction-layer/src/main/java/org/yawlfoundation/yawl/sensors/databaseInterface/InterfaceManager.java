package org.yawlfoundation.yawl.sensors.databaseInterface;

import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InterfaceManager implements Serializable {

	public static int PROM = 0;
	public static int YAWL = 1;
	
	private Activity activityLayer = null;
	private ActivityRole activityRoleLayer = null;
	private Role roleLayer = null;
	private SubProcess subProcessLayer = null;
	private Variable variableLayer = null;
	private WorkflowDefinition workflowDefinitionLayer = null;
	
	public InterfaceManager(int type, Map<String, Object> parameters) {
		
		if(type == PROM) {
			
			generatePromInstance(parameters);
			
		}else if(type == YAWL){
			
			generateYAWLInstance(parameters);
			
		}
		
	}
	
	public void updateInterfaces(int type, HashMap<String, Object> parameters) {
		
		if(type == PROM) {
			
//			generatePromInstance(parameters);
			updatePromInstance(parameters);
			
		}else if(type == YAWL){
			
			generateYAWLInstance(parameters);
			
		}
		
	}
	
	private void generateYAWLInstance(Map<String, Object> parameters) {

		activityLayer = Activity.getActivity("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Activity");
    	activityRoleLayer = ActivityRole.getActivityRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_ActivityRole");
    	roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role");
    	subProcessLayer = SubProcess.getSubProcess("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_SubProcess");
    	variableLayer = Variable.getVariable("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Variable");
    	workflowDefinitionLayer = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition");
    	
	}
	
	public static Map<String, Object> generateParameters(XLog log, String specification, String resourceSpecification) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("Xlog", log);
		map.put("specification", specification);
		map.put("resources", resourceSpecification);
		
		return map;
		
	}

	private void generatePromInstance(Map<String, Object> parameters) {
		
		XLog log = (XLog) parameters.get("Xlog");
		String specification = (String) parameters.get("specification");
		String resources = (String) parameters.get("resources");
		
		if(log != null) {
			
			Prom_SubProcess subProcessLayer = new Prom_SubProcess();
			subProcessLayer.setLog(log);
			this.subProcessLayer = subProcessLayer;
		
			if(specification != null) {
				
				Prom_Variable variableLayer = new Prom_Variable();
				variableLayer.setLog(log, specification);
				this.variableLayer = variableLayer;
				
				Prom_WorkFlowDefinition workflowDefinitionLayer = new Prom_WorkFlowDefinition();
				workflowDefinitionLayer.setLog(specification, log);
				this.workflowDefinitionLayer = workflowDefinitionLayer;
			
			}
		
			if(resources != null) {
				
				Prom_ActivityRole activityRoleLayer = new Prom_ActivityRole();
				activityRoleLayer.setLog(log, resources);
				this.activityRoleLayer = activityRoleLayer;
				
				Prom_Role roleLayer = new Prom_Role();
				roleLayer.setLog(log, resources);
				this.roleLayer = roleLayer;
				
			}
			
			if(specification != null && resources != null) {
			
				Prom_Activity activityLayer = new Prom_Activity();
				activityLayer.setLog(log, resources, specification);
				this.activityLayer = activityLayer;
			
			}

		}
		
	}
	
    private void updatePromInstance(Map<String, Object> parameters) {
		
		XLog log = (XLog) parameters.get("Xlog");
					
		((Prom_SubProcess) this.subProcessLayer).updateLog(log);
		((Prom_Variable) this.variableLayer).updateLog(log);
		((Prom_WorkFlowDefinition) this.workflowDefinitionLayer).updateLog(log);
		((Prom_ActivityRole) this.activityRoleLayer).updateLog(log);
		((Prom_Role) this.roleLayer).updateLog(log);
		((Prom_Activity) this.activityLayer).updateLog(log);
		
	}

	public Activity getActivityLayer() {
		return activityLayer;
	}

	public ActivityRole getActivityRoleLayer() {
		return activityRoleLayer;
	}

	public Role getRoleLayer() {
		return roleLayer;
	}

	public SubProcess getSubProcessLayer() {
		return subProcessLayer;
	}

	public Variable getVariableLayer() {
		return variableLayer;
	}

	public WorkflowDefinition getWorkflowDefinitionLayer() {
		return workflowDefinitionLayer;
	}
	
}
