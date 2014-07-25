package org.yawlfoundation.yawl.sensors.language.loops;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;

public class ForIdentifier {

	public static final int FORAND = 1;
	public static final int FOROR = 2;
	
	public static final int FORADD = 3;
	public static final int FORMUL = 4;
	
	public static final int FORMIN = 5;
	public static final int FORMAX = 6;
	
	public static final int FORAVG = 7;
	
	public static int getFunction(String input) {
		
		String typeString = input.substring(input.indexOf(YSensorUtilities.FOR));
		
		if(typeString.endsWith(YSensorUtilities.AND)) {
			return FORADD;
		}else if(typeString.endsWith(YSensorUtilities.OR)) {
			return FOROR;
		}else if(typeString.endsWith(YSensorUtilities.SUM)) {
			return FORADD;
		}else if(typeString.endsWith(YSensorUtilities.MUL)) {
			return FORMUL;
		}else if(typeString.endsWith(YSensorUtilities.MIN)) {
			return FORMIN;
		}else if(typeString.endsWith(YSensorUtilities.MAX)) {
			return FORMAX;
		}else if(typeString.endsWith(YSensorUtilities.AVG)) {
			return FORAVG;
		}else return 0;
	}
	
}
