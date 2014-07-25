package org.yawlfoundation.yawl.sensors.language.functions;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;

public class FunctionIdentifier {

	public static final int offeredList = 1;
	public static final int allocatedList = 2;
	public static final int startedList = 3;
	
	public static final int offeredNumber = 4;
	public static final int allocatedNumber = 5;
	public static final int startedNumber = 6;
	
	public static final int offeredMinNumber = 7;
	public static final int allocatedMinNumber = 8;
	public static final int startedMinNumber = 9;
	
	public static final int offeredMinNumberExcept = 10;
	public static final int allocatedMinNumberExcept = 11;
	public static final int startedMinNumberExcept = 12;
	
	public static final int offeredContain = 13; 
	public static final int allocatedContain = 14;
	public static final int startedContain = 15;
	
	public static int getFunction(String input) {
		if(input.endsWith(YSensorUtilities.offeredList)) {
			return offeredList;
		} else if(input.endsWith(YSensorUtilities.allocatedList)) {
			return allocatedList;
		} else if(input.endsWith(YSensorUtilities.startedList)) {
			return startedList;
		} else if(input.endsWith(YSensorUtilities.offeredNumber)) {
			return offeredNumber;
		} else if(input.endsWith(YSensorUtilities.allocatedNumber)) {
			return allocatedNumber;
		} else if(input.endsWith(YSensorUtilities.startedNumber)) {
			return startedNumber;
		} else if(input.endsWith(YSensorUtilities.offeredMinNumber)) {
			return offeredMinNumber;
		} else if(input.endsWith(YSensorUtilities.allocatedMinNumber)) { 
			return allocatedMinNumber;
		} else if(input.endsWith(YSensorUtilities.startedMinNumber)) {
			return startedMinNumber;
		} else if(input.contains(YSensorUtilities.offeredMinNumberExcept)) {
			return offeredMinNumberExcept;
		} else if(input.contains(YSensorUtilities.allocatedMinNumberExcept)) {
			return allocatedMinNumberExcept;
		} else if(input.contains(YSensorUtilities.startedMinNumberExcept)) {
			return startedMinNumberExcept;
		}else if(input.contains(YSensorUtilities.offeredContain)) {
			return offeredContain;
		}else if(input.contains(YSensorUtilities.allocatedContain)) {
			return allocatedContain;
		}else if(input.contains(YSensorUtilities.startedContain)) {
			return startedContain;
		}else return -1;
	}
	
	public static String getTerm(String input) {
		return input.substring(input.lastIndexOf(YExpression.DOT)+1);
	}
	
}
