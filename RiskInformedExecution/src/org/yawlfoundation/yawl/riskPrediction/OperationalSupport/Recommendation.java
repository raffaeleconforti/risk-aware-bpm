package org.yawlfoundation.yawl.riskPrediction.OperationalSupport;

public class Recommendation {

	private String next = null;
	private Long risk = null;
	
	public Recommendation(String nextActivity, Long riskLevel) {
		next = nextActivity;
		risk = riskLevel;
	}
	

	public String getNextActivity() {
		return next;
	}

	public Long getRiskLevel() {
		return risk;
	}
	
}
