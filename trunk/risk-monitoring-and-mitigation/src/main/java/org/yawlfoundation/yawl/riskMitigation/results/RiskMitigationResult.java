package org.yawlfoundation.yawl.riskMitigation.results;

import java.util.Arrays;
import java.util.List;

public class RiskMitigationResult {
	
	Double[] energy = null;
	List<String> mitigations = null;
	
	public RiskMitigationResult(Double[] energy, List<String> mitigations) {
		this.energy = energy;
		this.mitigations = mitigations;
	}

	public Double[] getEnergy() {
		return energy;
	}

	public List<String> getMitigations() {
		return mitigations;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof RiskMitigationResult) {
			
			RiskMitigationResult tmp = (RiskMitigationResult) o;
			if(this.mitigations.equals(tmp.mitigations) && this.energy.length == tmp.energy.length) {
				
				for(int i = 0; i < this.energy.length; i++) {
					
					if(this.energy[i].compareTo(tmp.energy[i]) != 0) {
						return false;
					}
				
				}
				return true;
			
			}
			return false;
			
		}
		
		return false;
	}

}
