package org.yawlfoundation.yawl.riskPrediction.Annotations;

import java.io.Serializable;
import java.util.HashMap;

public class ProcessTimeExecutionAnnotation implements Serializable {

	HashMap<String, HashMap<String,Long>> annotations;
	
	public ProcessTimeExecutionAnnotation() {
		
		annotations = new HashMap<String, HashMap<String,Long>>();
		
	}
	
	public void addAnnotation(String caseid, String taskName, Long annotation) {
		
		HashMap<String, Long> caseAnnotations = null;
		
		if((caseAnnotations = annotations.get(caseid)) == null) {
			caseAnnotations = new HashMap<String, Long>();
			annotations.put(caseid, caseAnnotations);
		}
		
		caseAnnotations.put(taskName, annotation);
	}
	
	public Long getTaskAnnotation(String caseid, String taskName) {
		
		HashMap<String, Long> caseAnnotations = null;
		
		if((caseAnnotations = annotations.get(caseid)) != null) {
			
			return caseAnnotations.get(taskName);
			
		} 
		
		return null;
		
	}
	
	public HashMap<String,Long> getCaseAnnotations(String caseid) {
		
		return annotations.get(caseid);
		
	}
	
	@Override
	public String toString() {
		return annotations.toString();
	}
	
}