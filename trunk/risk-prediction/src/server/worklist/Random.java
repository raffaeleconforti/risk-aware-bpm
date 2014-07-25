package server.worklist;

import server.worklist.MetricInterface;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 20/05/2013
 *
 */

public class Random implements MetricInterface {

	@Override
	public double getValue(String workItemId, String resourceId) {
		double res = Math.random(); 
		System.out.println(res);
		return res;
	}	
	
}
