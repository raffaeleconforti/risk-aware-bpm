package org.yawlfoundation.yawl.riskMitigation.Temperature.Test;

import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;

public class TemperatureCalculatorTest implements TemperatureCalculator {
	
	Double beta = null;

	@Override
	public double updateTemperature(double initialTemperature, int iterations, int maxIterations) {
		if(beta == null) calculateBeta(initialTemperature, maxIterations);
		double betaK = Math.pow(beta, iterations);
		return betaK*initialTemperature;
//		return temperature/Math.log(iterations);
//		return temperature/iterations;
	}

	@Override
	public double calculateBeta(double initialTemperature, double maxIterations) {
		beta = Math.exp(Math.log(Math.pow(10,-5)/initialTemperature)*(3.0/(2.0*maxIterations)));
		return beta;
	}

}
