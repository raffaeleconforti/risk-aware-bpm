package org.yawlfoundation.yawl.riskMitigation.Temperature;

public interface TemperatureCalculator {

	public double calculateBeta(double initialTemperature, double maxIterations);
	
	public double updateTemperature(double initialTemperature, int iterations, int maxIterations);

}
