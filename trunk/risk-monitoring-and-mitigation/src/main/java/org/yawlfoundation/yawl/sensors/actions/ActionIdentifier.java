package org.yawlfoundation.yawl.sensors.actions;

import org.yawlfoundation.yawl.sensors.YSensorUtilities;

public class ActionIdentifier {	
	
	public static final int isOfferedNumber = 1;
	public static final int isAllocatedNumber = 2;
	public static final int isStartedNumber = 3;
	public static final int isCompletedNumber = 4;
	
	public static final int offerTimeNumber = 5;
	public static final int allocateTimeNumber = 6;
	public static final int startTimeNumber = 7;
	public static final int completeTimeNumber = 8;
	
	public static final int offerTimeInMillisNumber = 9;
	public static final int allocateTimeInMillisNumber = 10;
	public static final int startTimeInMillisNumber = 11;
	public static final int completeTimeInMillisNumber = 12;
	
	public static final int offerResourceNumber = 13; 
	public static final int allocateResourceNumber = 14;
	public static final int startResourceNumber = 15;
	public static final int completeResourceNumber = 16;
	
	public static final int passTimeNumber = 17;
	public static final int passTimeInMillisNumber = 18;
	
	public static final int offerDistributionNumber = 19;
	public static final int allocateDistributionNumber = 20;
	public static final int startDistributionNumber = 21;
	
	public static final int offerInitiatorNumber = 22;
	public static final int allocateInitiatorNumber = 23;
	public static final int startInitiatorNumber = 24;
	
	public static final int count = 25;
	public static final int countElements = 26;
	public static final int timeEstimationInMillis = 27;
	
	public static final int var = 28;
	
	public static final int isOfferedListNumber = 29;
	public static final int isAllocatedListNumber = 30;
	public static final int isStartedListNumber = 31;
	public static final int isCompletedListNumber = 32;

	public static final int offerTimeListNumber = 33;
	public static final int allocateTimeListNumber = 34;
	public static final int startTimeListNumber = 35;
	public static final int completeTimeListNumber = 36;
	
	public static final int offerTimeInMillisListNumber = 37;
	public static final int allocateTimeInMillisListNumber = 38;
	public static final int startTimeInMillisListNumber = 39;
	public static final int completeTimeInMillisListNumber = 40;
	
	public static final int offerResourceListNumber = 41; 
	public static final int allocateResourceListNumber = 42;
	public static final int startResourceListNumber = 43;
	public static final int completeResourceListNumber = 44;
	
	public static final int passTimeListNumber = 45;
	public static final int passTimeInMillisListNumber = 46;
	
	public static final int varList = 47;
	
	public static int getAction(String logTaskItem) {
		if(logTaskItem.endsWith(YSensorUtilities.isOffered)) {
			return isOfferedNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isAllocated)) {
			return isAllocatedNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isStarted)) {
			return isStartedNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isCompleted)) {
			return isCompletedNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.offerTime)) {
			return offerTimeNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.allocateTime)) {
			return allocateTimeNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.startTime)) {
			return startTimeNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.completeTime)) { 
			return completeTimeNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.offerTimeInMillis)) {
			return offerTimeInMillisNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.allocateTimeInMillis)) {
			return allocateTimeInMillisNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.startTimeInMillis)) {
			return startTimeInMillisNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.completeTimeInMillis)) {
			return completeTimeInMillisNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.offerResource)) {
			return offerResourceNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.allocateResource)) {
			return allocateResourceNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.startResource)) {
			return startResourceNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.completeResource)) {
			return completeResourceNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.passTime)) { 
			return passTimeNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.passTimeInMillis)) {
			return passTimeInMillisNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.offerDistribution)) {
			return offerDistributionNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.allocateDistribution)) {
			return allocateDistributionNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.startDistribution)) {
			return startDistributionNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.offerInitiator)) {
			return offerInitiatorNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.allocateInitiator)) {
			return allocateInitiatorNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.startInitiator)) {
			return startInitiatorNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.count)) {
			return count;
		}else if(logTaskItem.endsWith(YSensorUtilities.countElements)) {
			return countElements;
		}else if(logTaskItem.endsWith(YSensorUtilities.timeEstimationInMillis)) {
			return timeEstimationInMillis;
		}else if(logTaskItem.endsWith(YSensorUtilities.isOfferedList)) {
			return isOfferedListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isAllocatedList)) {
			return isAllocatedListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isStartedList)) {
			return isStartedListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.isCompletedList)) {
			return isCompletedListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.offerTimeList)) {
			return offerTimeListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.allocateTimeList)) {
			return allocateTimeListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.startTimeList)) {
			return startTimeListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.completeTimeList)) { 
			return completeTimeListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.offerTimeInMillisList)) {
			return offerTimeInMillisListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.allocateTimeInMillisList)) {
			return allocateTimeInMillisListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.startTimeInMillisList)) {
			return startTimeInMillisListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.completeTimeInMillisList)) {
			return completeTimeInMillisListNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.offerResourceList)) {
			return offerResourceListNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.allocateResourceList)) {
			return allocateResourceListNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.startResourceList)) {
			return startResourceListNumber;
		}else if(logTaskItem.endsWith(YSensorUtilities.completeResourceList)) {
			return completeResourceListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.passTimeList)) { 
			return passTimeListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.passTimeInMillisList)) {
			return passTimeInMillisListNumber;
		} else if(logTaskItem.endsWith(YSensorUtilities.list)){
			return varList;
		} else {
			return var;
		}
	}
}
