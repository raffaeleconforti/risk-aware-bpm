package org.yawlfoundation.yawl.riskPrediction.Annotations;

import java.io.Serializable;
import java.util.HashMap;

public class TimeBetweenTasksAnnotation implements Serializable {

	HashMap<String, HashMap<String, HashMap<String, Long>>> annotations;
	
	public TimeBetweenTasksAnnotation() {
		
		annotations = new HashMap<String, HashMap<String, HashMap<String, Long>>>();
		
	}
	
	public void addAnnotation(String caseid, String firstTaskName, String secondTaskName, Long annotation) {
		
		HashMap<String, HashMap<String, Long>> caseAnnotations = null;
		HashMap<String, Long> firstTaskNameAnnotations = null;
		
		if((caseAnnotations = annotations.get(caseid)) == null) {
			caseAnnotations = new HashMap<String, HashMap<String, Long>>();
			annotations.put(caseid, caseAnnotations);
		}
		
		if((firstTaskNameAnnotations = caseAnnotations.get(firstTaskName)) == null) {
			firstTaskNameAnnotations = new HashMap<String, Long>();
			caseAnnotations.put(firstTaskName, firstTaskNameAnnotations);
		}
		
		firstTaskNameAnnotations.put(secondTaskName, annotation);
	}
	
	public Long getTaskAnnotation(String caseid, String firstTaskName, String secondTaskName) {
		
		HashMap<String, HashMap<String, Long>> caseAnnotations = null;
		HashMap<String, Long> firstTaskNameAnnotations = null;
		
		if((caseAnnotations = annotations.get(caseid)) != null) {
			
			if((firstTaskNameAnnotations = caseAnnotations.get(firstTaskName)) != null) {
				
				return firstTaskNameAnnotations.get(secondTaskName);
				
			}
			
		} 
		
		return null;
		
	}
	
	public HashMap<String, Long> getFirstTaskAnnotations(String caseid, String firstTaskName) {
		
		HashMap<String, HashMap<String, Long>> caseAnnotations = null;
		
		if((caseAnnotations = annotations.get(caseid)) != null) {
			
			return caseAnnotations.get(firstTaskName);
				
		} 
		
		return null;
		
	}
	
	@Override
	public String toString() {
		return annotations.toString();
	}
	
}
