package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase;

import java.util.LinkedList;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TaskExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.DecisionPoint;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.InstanceGenerator;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.OperationalSupportApp;
import org.yawlfoundation.yawl.riskPrediction.PredictionFunction.AttributeGenerator;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 08/07/2013
 *
 */

public class TupleClassifierName {

	private OperationalSupportApp osa;
	private WorkItemRecord workItem;
	private String[] resources;
	private LinkedList<Classifier> classifiers;
	
	private LinkedList<String> idList = new LinkedList<String>();
	
	public TupleClassifierName(OperationalSupportApp osa, LinkedList<Classifier> classifiers, WorkItemRecord workItem, String[] resources) {
		
		this.osa = osa;
		this.classifiers = classifiers;
		this.workItem = workItem;
		this.resources = resources;
		idList.add(workItem.getRootCaseID());
		
	}
	
	public LinkedList<Classifier> getClassifiers() {
		return classifiers;
	}

	public WorkItemRecord getWorkItem() {
		return workItem;
	}
	
	public String[] getResources() {
		return resources;
	}
	
	public Long getTime(String workItem) {
		
		LinkedList<ActivityTuple> v1 = osa.getImUpdated().getActivityLayer().getTimes(null, false, workItem, true, idList, true, Activity.Completed, true, null, false, false);
		
		if(v1 != null) {
			return Long.parseLong(v1.get(0).getTime());
		}
		
		return null;
	}
	
	public Long checkInstance(String resource, long currTime) {
		
		return osa.classify(this, resource, currTime);
		
	}
	
}
