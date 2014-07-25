package org.yawlfoundation.yawl.riskPrediction.Annotations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class RiskAnnotations implements Serializable{

	HashMap<String, Long> annotations;
	
	public RiskAnnotations() {
		annotations = new HashMap<String, Long>();
	}
	
	public void addAnnotation(String caseid, Long annotation) {
		annotations.put(caseid, annotation);
	}
	
	public Long getAnnotation(String caseid) {
		return annotations.get(caseid);
	}
	
	@Override
	public String toString() {
		return annotations.toString();
	}
	
	public LinkedList<Long> getRisksAsList() {
		
		LinkedList<Long> risks = new LinkedList<Long>();
		
		for(Entry<String, Long> entry : annotations.entrySet()) {

			risks.add(entry.getValue());
		
		}
		
		return risks;
		
	}
	
	
}
