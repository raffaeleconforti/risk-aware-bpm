package org.yawlfoundation.yawl.sensors.actions;

import java.util.HashSet;
import java.util.LinkedList;

import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetCompleteTime;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetCompleteTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetEstimationTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetIsCompleted;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetIsStarted;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetPassTime;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetPassTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetStartTime;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetStartTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions.NetVariable;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityAllocateTime;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityAllocateTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityCompleteTime;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityCompleteTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityCount;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityCountElements;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityEstimationTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityIsAllocated;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityIsCompleted;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityIsOffered;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityIsStarted;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityOfferTime;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityOfferTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityPassTime;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityPassTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityStartTime;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityStartTimeInMillis;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.ActivityVariable;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.AllocateDistribution;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.AllocateInitiator;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.AllocateResource;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.CompleteResource;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.OfferDistribution;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.OfferInitiator;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.OfferResource;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.StartDistribution;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.StartInitiator;
import org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions.StartResource;

public class ActionExecutor {
	
	public static Object getTaskActionResult(int action, String taskName, String WorkDefID, String variableName) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: return ActivityIsOffered.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.isAllocatedNumber: return ActivityIsAllocated.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.isStartedNumber: return ActivityIsStarted.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.isCompletedNumber: return ActivityIsCompleted.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.offerTimeNumber: return ActivityOfferTime.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.allocateTimeNumber: return ActivityAllocateTime.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.startTimeNumber: return ActivityStartTime.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.completeTimeNumber: return ActivityCompleteTime.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.offerTimeInMillisNumber: return ActivityOfferTimeInMillis.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.allocateTimeInMillisNumber: return ActivityAllocateTimeInMillis.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.startTimeInMillisNumber: return ActivityStartTimeInMillis.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.completeTimeInMillisNumber: return ActivityCompleteTimeInMillis.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.offerResourceNumber: return OfferResource.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.allocateResourceNumber: return AllocateResource.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.startResourceNumber: return StartResource.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.completeResourceNumber: return CompleteResource.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.passTimeNumber: return ActivityPassTime.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.passTimeInMillisNumber: return ActivityPassTimeInMillis.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.offerDistributionNumber: return OfferDistribution.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.allocateDistributionNumber: return AllocateDistribution.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.startDistributionNumber: return StartDistribution.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.offerInitiatorNumber: return OfferInitiator.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.allocateInitiatorNumber: return AllocateInitiator.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.startInitiatorNumber: return StartInitiator.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.count: return ActivityCount.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.countElements: return ActivityCountElements.getActionResult(WorkDefID, taskName);
			case ActionIdentifier.timeEstimationInMillis: return ActivityEstimationTimeInMillis.getActionResult(WorkDefID, taskName);
			
			case ActionIdentifier.var: return ActivityVariable.getActionResult(WorkDefID, taskName, variableName);
			
			case ActionIdentifier.isOfferedListNumber: return ActivityIsOffered.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.isAllocatedListNumber: return ActivityIsAllocated.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.isStartedListNumber: return ActivityIsStarted.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.isCompletedListNumber: return ActivityIsCompleted.getActionResultList(WorkDefID, taskName);
			
			case ActionIdentifier.offerTimeListNumber: return ActivityOfferTime.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.allocateTimeListNumber: return ActivityAllocateTime.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.startTimeListNumber: return ActivityStartTime.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.completeTimeListNumber: return ActivityCompleteTime.getActionResultList(WorkDefID, taskName);
			
			case ActionIdentifier.offerTimeInMillisListNumber: return ActivityOfferTimeInMillis.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.allocateTimeInMillisListNumber: return ActivityAllocateTimeInMillis.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.startTimeInMillisListNumber: return ActivityStartTimeInMillis.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.completeTimeInMillisListNumber: return ActivityCompleteTimeInMillis.getActionResultList(WorkDefID, taskName);
				
			case ActionIdentifier.offerResourceListNumber: return OfferResource.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.allocateResourceListNumber: return AllocateResource.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.startResourceListNumber: return StartResource.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.completeResourceListNumber: return CompleteResource.getActionResultList(WorkDefID, taskName);
			
			case ActionIdentifier.passTimeListNumber: return ActivityPassTime.getActionResultList(WorkDefID, taskName);
			case ActionIdentifier.passTimeInMillisListNumber: return ActivityPassTimeInMillis.getActionResultList(WorkDefID, taskName);
			
			case ActionIdentifier.varList: return ActivityVariable.getActionResultList(WorkDefID, taskName, variableName);
			
		}
		
		return null;
		
	}
	
	public static Object getNetActionResult(int action, String netName, String WorkDefID, String variableName) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: throw new RuntimeException("Wrong Action: net isOffered");
			case ActionIdentifier.isAllocatedNumber: throw new RuntimeException("Wrong Action: net isAllocated");
			case ActionIdentifier.isStartedNumber: return NetIsStarted.getActionResult(WorkDefID, netName);
			case ActionIdentifier.isCompletedNumber: return NetIsCompleted.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.offerTimeNumber: throw new RuntimeException("Wrong Action: net offerTime");
			case ActionIdentifier.allocateTimeNumber: throw new RuntimeException("Wrong Action: net allocateTime");
			case ActionIdentifier.startTimeNumber: return NetStartTime.getActionResult(WorkDefID, netName);
			case ActionIdentifier.completeTimeNumber: return NetCompleteTime.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.offerTimeInMillisNumber: throw new RuntimeException("Wrong Action: net offerTimeInMillis");
			case ActionIdentifier.allocateTimeInMillisNumber: throw new RuntimeException("Wrong Action: net allocateTimeInMillis");
			case ActionIdentifier.startTimeInMillisNumber: return NetStartTimeInMillis.getActionResult(WorkDefID, netName);
			case ActionIdentifier.completeTimeInMillisNumber: return NetCompleteTimeInMillis.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.offerResourceNumber: throw new RuntimeException("Wrong Action: net offerResource");
			case ActionIdentifier.allocateResourceNumber: throw new RuntimeException("Wrong Action: net allocateResource");
			case ActionIdentifier.startResourceNumber: throw new RuntimeException("Wrong Action: net startResource");
			case ActionIdentifier.completeResourceNumber: throw new RuntimeException("Wrong Action: net completeResource");
			
			case ActionIdentifier.passTimeNumber: return NetPassTime.getActionResult(WorkDefID, netName);
			case ActionIdentifier.passTimeInMillisNumber: return NetPassTimeInMillis.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.offerDistributionNumber: throw new RuntimeException("Wrong Action: net offerDistribution");
			case ActionIdentifier.allocateDistributionNumber: throw new RuntimeException("Wrong Action: net allocateDistribution");
			case ActionIdentifier.startDistributionNumber: throw new RuntimeException("Wrong Action: net startDistribution");
			
			case ActionIdentifier.offerInitiatorNumber: throw new RuntimeException("Wrong Action: net offerInitiator");
			case ActionIdentifier.allocateInitiatorNumber: throw new RuntimeException("Wrong Action: net allocateInitiator");
			case ActionIdentifier.startInitiatorNumber: throw new RuntimeException("Wrong Action: net startInitiator");
			
			case ActionIdentifier.count: throw new RuntimeException("Wrong Action: net count");
			case ActionIdentifier.countElements: throw new RuntimeException("Wrong Action: net countElements");
			case ActionIdentifier.timeEstimationInMillis: return NetEstimationTimeInMillis.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.var: return NetVariable.getActionResult(WorkDefID, netName);
			
			case ActionIdentifier.isOfferedListNumber: throw new RuntimeException("Wrong Action: net isOfferedList");
			case ActionIdentifier.isAllocatedListNumber: throw new RuntimeException("Wrong Action: net isAllocatedList");
			case ActionIdentifier.isStartedListNumber: return NetIsStarted.getActionResultList(WorkDefID, netName);
			case ActionIdentifier.isCompletedListNumber: return NetIsCompleted.getActionResultList(WorkDefID, netName);
			
			case ActionIdentifier.offerTimeListNumber: throw new RuntimeException("Wrong Action: net offerTimeList");
			case ActionIdentifier.allocateTimeListNumber: throw new RuntimeException("Wrong Action: net allocateTimeList");
			case ActionIdentifier.startTimeListNumber: return NetStartTime.getActionResultList(WorkDefID, netName);
			case ActionIdentifier.completeTimeListNumber: return NetCompleteTime.getActionResultList(WorkDefID, netName);
			
			case ActionIdentifier.offerTimeInMillisListNumber: throw new RuntimeException("Wrong Action: net offerTimeInMillisList");
			case ActionIdentifier.allocateTimeInMillisListNumber: throw new RuntimeException("Wrong Action: net allocateTimeInMillisList");
			case ActionIdentifier.startTimeInMillisListNumber: return NetStartTimeInMillis.getActionResultList(WorkDefID, netName);
			case ActionIdentifier.completeTimeInMillisListNumber: return NetCompleteTimeInMillis.getActionResultList(WorkDefID, netName);
			
			case ActionIdentifier.offerResourceListNumber: throw new RuntimeException("Wrong Action: net offerResourceList");
			case ActionIdentifier.allocateResourceListNumber: throw new RuntimeException("Wrong Action: net allocateResourceList");
			case ActionIdentifier.startResourceListNumber: throw new RuntimeException("Wrong Action: net startResourceList");
			case ActionIdentifier.completeResourceListNumber: throw new RuntimeException("Wrong Action: net completeResourceList");
			
			case ActionIdentifier.passTimeListNumber: return NetPassTime.getActionResultList(WorkDefID, netName);
			case ActionIdentifier.passTimeInMillisListNumber: return NetPassTimeInMillis.getActionResultList(WorkDefID, netName);

			case ActionIdentifier.varList: return NetVariable.getActionResultList(WorkDefID, netName);
				
		}
		
		return null;
		
	}
	
	public static Object getMultipleTaskActionResult(int action, String taskName, LinkedList<String> WorkDefIDs, String variableName) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: return ActivityIsOffered.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.isAllocatedNumber: return ActivityIsAllocated.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.isStartedNumber: return ActivityIsStarted.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.isCompletedNumber: return ActivityIsCompleted.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerTimeNumber: return ActivityOfferTime.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.allocateTimeNumber: return ActivityAllocateTime.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.startTimeNumber: return ActivityStartTime.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.completeTimeNumber: return ActivityCompleteTime.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerTimeInMillisNumber: return ActivityOfferTimeInMillis.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.allocateTimeInMillisNumber: return ActivityAllocateTimeInMillis.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.startTimeInMillisNumber: return ActivityStartTimeInMillis.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.completeTimeInMillisNumber: return ActivityCompleteTimeInMillis.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerResourceNumber: return OfferResource.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.allocateResourceNumber: return AllocateResource.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.startResourceNumber: return StartResource.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.completeResourceNumber: return CompleteResource.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.passTimeNumber: return ActivityPassTime.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.passTimeInMillisNumber: return ActivityPassTimeInMillis.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerDistributionNumber: return OfferDistribution.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.allocateDistributionNumber: return AllocateDistribution.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.startDistributionNumber: return StartDistribution.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerInitiatorNumber: return OfferInitiator.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.allocateInitiatorNumber: return AllocateInitiator.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.startInitiatorNumber: return StartInitiator.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.count: return ActivityCount.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.countElements: return ActivityCountElements.getActionResult(WorkDefIDs, taskName);
			case ActionIdentifier.timeEstimationInMillis: ActivityEstimationTimeInMillis.getActionResult(WorkDefIDs, taskName);
			
			case ActionIdentifier.var: return ActivityVariable.getActionResult(WorkDefIDs, taskName, variableName);
			
			case ActionIdentifier.isOfferedListNumber: return ActivityIsOffered.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.isAllocatedListNumber: return ActivityIsAllocated.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.isStartedListNumber: return ActivityIsStarted.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.isCompletedListNumber: return ActivityIsCompleted.getActionResultList(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerTimeListNumber: return ActivityOfferTime.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.allocateTimeListNumber: return ActivityAllocateTime.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.startTimeListNumber: return ActivityStartTime.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.completeTimeListNumber: return ActivityCompleteTime.getActionResultList(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerTimeInMillisListNumber: return ActivityOfferTimeInMillis.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.allocateTimeInMillisListNumber: return ActivityAllocateTimeInMillis.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.startTimeInMillisListNumber: return ActivityStartTimeInMillis.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.completeTimeInMillisListNumber: return ActivityCompleteTimeInMillis.getActionResultList(WorkDefIDs, taskName);
			
			case ActionIdentifier.offerResourceListNumber: return OfferResource.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.allocateResourceListNumber: return AllocateResource.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.startResourceListNumber: return StartResource.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.completeResourceListNumber: return CompleteResource.getActionResultList(WorkDefIDs, taskName);
			
			case ActionIdentifier.passTimeListNumber: return ActivityPassTime.getActionResultList(WorkDefIDs, taskName);
			case ActionIdentifier.passTimeInMillisListNumber: return ActivityPassTimeInMillis.getActionResultList(WorkDefIDs, taskName);
		
			case ActionIdentifier.varList: return ActivityVariable.getActionResultList(WorkDefIDs, taskName, variableName);
			
		}
		
		return null;
		
	}
	
	public static Object getMultipleNetActionResult(int action, String netName, LinkedList<String> WorkDefIDs, String variableName) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: throw new RuntimeException("Wrong Action: net isOffered");
			case ActionIdentifier.isAllocatedNumber: throw new RuntimeException("Wrong Action: net isAllocated");
			case ActionIdentifier.isStartedNumber: return NetIsStarted.getActionResult(WorkDefIDs, netName);
			case ActionIdentifier.isCompletedNumber: return NetIsCompleted.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.offerTimeNumber: throw new RuntimeException("Wrong Action: net offerTime");
			case ActionIdentifier.allocateTimeNumber: throw new RuntimeException("Wrong Action: net allocateTime");
			case ActionIdentifier.startTimeNumber: return NetStartTimeInMillis.getActionResult(WorkDefIDs, netName);
			case ActionIdentifier.completeTimeNumber: return NetCompleteTime.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.offerTimeInMillisNumber: throw new RuntimeException("Wrong Action: net offerTimeInMillis");
			case ActionIdentifier.allocateTimeInMillisNumber: throw new RuntimeException("Wrong Action: net allocateTimeInMillis");
			case ActionIdentifier.startTimeInMillisNumber: return NetStartTimeInMillis.getActionResult(WorkDefIDs, netName);
			case ActionIdentifier.completeTimeInMillisNumber: return NetCompleteTimeInMillis.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.offerResourceNumber: throw new RuntimeException("Wrong Action: net offerResource");
			case ActionIdentifier.allocateResourceNumber: throw new RuntimeException("Wrong Action: net allocateResource");
			case ActionIdentifier.startResourceNumber: throw new RuntimeException("Wrong Action: net startResource");
			case ActionIdentifier.completeResourceNumber: throw new RuntimeException("Wrong Action: net completeResource");
			
			case ActionIdentifier.passTimeNumber: return NetPassTime.getActionResult(WorkDefIDs, netName);
			case ActionIdentifier.passTimeInMillisNumber: return NetPassTimeInMillis.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.offerDistributionNumber: throw new RuntimeException("Wrong Action: net offerDistribution");
			case ActionIdentifier.allocateDistributionNumber: throw new RuntimeException("Wrong Action: net allocateDistribution");
			case ActionIdentifier.startDistributionNumber: throw new RuntimeException("Wrong Action: net startDistribution");
			
			case ActionIdentifier.offerInitiatorNumber: throw new RuntimeException("Wrong Action: net offerInitiator");
			case ActionIdentifier.allocateInitiatorNumber: throw new RuntimeException("Wrong Action: net allocateInitiator");
			case ActionIdentifier.startInitiatorNumber: throw new RuntimeException("Wrong Action: net startInitiator");
			
			case ActionIdentifier.count: throw new RuntimeException("Wrong Action: net count");
			case ActionIdentifier.countElements: throw new RuntimeException("Wrong Action: net countElements");
			case ActionIdentifier.timeEstimationInMillis: NetEstimationTimeInMillis.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.var: return NetVariable.getActionResult(WorkDefIDs, netName);
			
			case ActionIdentifier.isOfferedListNumber: throw new RuntimeException("Wrong Action: net isOfferedList");
			case ActionIdentifier.isAllocatedListNumber: throw new RuntimeException("Wrong Action: net isAllocatedList");
			case ActionIdentifier.isStartedListNumber: return NetIsStarted.getActionResultList(WorkDefIDs, netName);
			case ActionIdentifier.isCompletedListNumber: return NetIsCompleted.getActionResultList(WorkDefIDs, netName);
			
			case ActionIdentifier.offerTimeListNumber: throw new RuntimeException("Wrong Action: net offerTimeList");
			case ActionIdentifier.allocateTimeListNumber: throw new RuntimeException("Wrong Action: net allocateTimeList");
			case ActionIdentifier.startTimeListNumber: return NetStartTimeInMillis.getActionResultList(WorkDefIDs, netName);
			case ActionIdentifier.completeTimeListNumber: return NetCompleteTime.getActionResultList(WorkDefIDs, netName);
			
			case ActionIdentifier.offerTimeInMillisListNumber: throw new RuntimeException("Wrong Action: net offerTimeInMillisList");
			case ActionIdentifier.allocateTimeInMillisListNumber: throw new RuntimeException("Wrong Action: net allocateTimeInMillisList");
			case ActionIdentifier.startTimeInMillisListNumber: return NetStartTimeInMillis.getActionResultList(WorkDefIDs, netName);
			case ActionIdentifier.completeTimeInMillisListNumber: return NetCompleteTimeInMillis.getActionResultList(WorkDefIDs, netName);
			
			case ActionIdentifier.offerResourceListNumber: throw new RuntimeException("Wrong Action: net offerResourceList");
			case ActionIdentifier.allocateResourceListNumber: throw new RuntimeException("Wrong Action: net allocateResourceList");
			case ActionIdentifier.startResourceListNumber: throw new RuntimeException("Wrong Action: net startResourceList");
			case ActionIdentifier.completeResourceListNumber: throw new RuntimeException("Wrong Action: net completeResourceList");
			
			case ActionIdentifier.passTimeListNumber: return NetPassTime.getActionResultList(WorkDefIDs, netName);
			case ActionIdentifier.passTimeInMillisListNumber: return NetPassTimeInMillis.getActionResultList(WorkDefIDs, netName);
				
			case ActionIdentifier.varList: return NetVariable.getActionResultList(WorkDefIDs, netName);
			
		}
		
		return null;
		
	}
	
	public static HashSet<String> getExpressionTaskActionResult(int action, String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: return ActivityIsOffered.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isAllocatedNumber: return ActivityIsAllocated.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isStartedNumber: return ActivityIsStarted.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isCompletedNumber: return ActivityIsCompleted.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeNumber: return ActivityOfferTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateTimeNumber: return ActivityAllocateTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startTimeNumber: return ActivityStartTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeNumber: return ActivityCompleteTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeInMillisNumber: return ActivityOfferTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateTimeInMillisNumber: return ActivityAllocateTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startTimeInMillisNumber: return ActivityStartTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeInMillisNumber: return ActivityCompleteTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerResourceNumber: return OfferResource.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateResourceNumber: return AllocateResource.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startResourceNumber: return StartResource.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeResourceNumber: return CompleteResource.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.passTimeNumber: return ActivityPassTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.passTimeInMillisNumber: return ActivityPassTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerDistributionNumber:  throw new RuntimeException("Wrong Action: expression task offerDistribution");
			case ActionIdentifier.allocateDistributionNumber:  throw new RuntimeException("Wrong Action: expression task allocateDistribution");
			case ActionIdentifier.startDistributionNumber:  throw new RuntimeException("Wrong Action: expression task startDistribution");
			
			case ActionIdentifier.offerInitiatorNumber:  throw new RuntimeException("Wrong Action: expression task offerInitiator");
			case ActionIdentifier.allocateInitiatorNumber:  throw new RuntimeException("Wrong Action: expression task allocateInitiator");
			case ActionIdentifier.startInitiatorNumber:  throw new RuntimeException("Wrong Action: expression task startInitiator");
			
			case ActionIdentifier.count: return ActivityCount.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.countElements:  throw new RuntimeException("Wrong Action: expression task countElements");
			case ActionIdentifier.timeEstimationInMillis: throw new RuntimeException("Wrong Action: expression task timeEstimationInMillis");
			
			case ActionIdentifier.var: return ActivityVariable.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.isOfferedListNumber: return ActivityIsOffered.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isAllocatedListNumber: return ActivityIsAllocated.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isStartedListNumber: return ActivityIsStarted.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isCompletedListNumber: return ActivityIsCompleted.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeListNumber: return ActivityOfferTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateTimeListNumber: return ActivityAllocateTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startTimeListNumber: return ActivityStartTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeListNumber: return ActivityCompleteTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeInMillisListNumber: return ActivityOfferTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateTimeInMillisListNumber: return ActivityAllocateTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startTimeInMillisListNumber: return ActivityStartTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeInMillisListNumber: return ActivityCompleteTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerResourceListNumber: return OfferResource.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.allocateResourceListNumber: return AllocateResource.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.startResourceListNumber: return StartResource.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeResourceListNumber: return CompleteResource.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.passTimeListNumber: return ActivityPassTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.passTimeInMillisListNumber: return ActivityPassTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.varList: return ActivityVariable.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
				
		}
		
		return null;
		
	}
	
	public static HashSet<String> getExpressionNetActionResult(int action, String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		
		switch(action) {
			
			case ActionIdentifier.isOfferedNumber: throw new RuntimeException("Wrong Action: expression net isOffered");
			case ActionIdentifier.isAllocatedNumber: throw new RuntimeException("Wrong Action: expression net isAllocated");
			case ActionIdentifier.isStartedNumber: return NetIsStarted.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isCompletedNumber: return NetIsCompleted.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeNumber: throw new RuntimeException("Wrong Action: expression net offerTime");
			case ActionIdentifier.allocateTimeNumber: throw new RuntimeException("Wrong Action: expression net allocateTime");
			case ActionIdentifier.startTimeNumber: return NetStartTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeNumber: return NetCompleteTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeInMillisNumber: throw new RuntimeException("Wrong Action: expression net offerTimeInMillis");
			case ActionIdentifier.allocateTimeInMillisNumber: throw new RuntimeException("Wrong Action: expression net allocateTimeInMillis");
			case ActionIdentifier.startTimeInMillisNumber: return NetStartTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeInMillisNumber: return NetCompleteTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);

			case ActionIdentifier.offerResourceNumber: throw new RuntimeException("Wrong Action: expression net offerResource");
			case ActionIdentifier.allocateResourceNumber: throw new RuntimeException("Wrong Action: expression net allocateResource");
			case ActionIdentifier.startResourceNumber: throw new RuntimeException("Wrong Action: expression net startResource");
			case ActionIdentifier.completeResourceNumber: throw new RuntimeException("Wrong Action: expression net completeResource");
			
			case ActionIdentifier.passTimeNumber: return NetPassTime.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.passTimeInMillisNumber: return NetPassTimeInMillis.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerDistributionNumber:  throw new RuntimeException("Wrong Action: expression net offerDistribution");
			case ActionIdentifier.allocateDistributionNumber:  throw new RuntimeException("Wrong Action: expression net allocateDistribution");
			case ActionIdentifier.startDistributionNumber:  throw new RuntimeException("Wrong Action: expression net startDistribution");
			
			case ActionIdentifier.offerInitiatorNumber:  throw new RuntimeException("Wrong Action: expression net offerInitiator");
			case ActionIdentifier.allocateInitiatorNumber:  throw new RuntimeException("Wrong Action: expression net allocateInitiator");
			case ActionIdentifier.startInitiatorNumber:  throw new RuntimeException("Wrong Action: expression net startInitiator");
			
			case ActionIdentifier.count: throw new RuntimeException("Wrong Action: expression net count");
			case ActionIdentifier.countElements:  throw new RuntimeException("Wrong Action: expression net countElements");
			case ActionIdentifier.timeEstimationInMillis: throw new RuntimeException("Wrong Action: expression net timeEstimationInMillis");
			
			case ActionIdentifier.var: return NetVariable.getExpressionActionResult(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.isOfferedListNumber: throw new RuntimeException("Wrong Action: expression net isOfferedList");
			case ActionIdentifier.isAllocatedListNumber: throw new RuntimeException("Wrong Action: expression net isAllocatedList");
			case ActionIdentifier.isStartedListNumber: return NetIsStarted.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.isCompletedListNumber: return NetIsCompleted.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeListNumber: throw new RuntimeException("Wrong Action: expression net offerTimeList");
			case ActionIdentifier.allocateTimeListNumber: throw new RuntimeException("Wrong Action: expression net allocateTimeList");
			case ActionIdentifier.startTimeListNumber: return NetStartTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeListNumber: return NetCompleteTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
			case ActionIdentifier.offerTimeInMillisListNumber: throw new RuntimeException("Wrong Action: expression net offerTimeInMillisList");
			case ActionIdentifier.allocateTimeInMillisListNumber: throw new RuntimeException("Wrong Action: expression net allocateTimeInMillisList");
			case ActionIdentifier.startTimeInMillisListNumber: return NetStartTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.completeTimeInMillisListNumber: return NetCompleteTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);

			case ActionIdentifier.offerResourceListNumber: throw new RuntimeException("Wrong Action: expression net offerResourceList");
			case ActionIdentifier.allocateResourceListNumber: throw new RuntimeException("Wrong Action: expression net allocateResourceList");
			case ActionIdentifier.startResourceListNumber: throw new RuntimeException("Wrong Action: expression net startResourceList");
			case ActionIdentifier.completeResourceListNumber: throw new RuntimeException("Wrong Action: expression net completeResourceList");
			
			case ActionIdentifier.passTimeListNumber: return NetPassTime.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			case ActionIdentifier.passTimeInMillisListNumber: return NetPassTimeInMillis.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
				
			case ActionIdentifier.varList: return NetVariable.getExpressionActionResultList(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
			
		}
		
		return null;
		
	}
	
}
