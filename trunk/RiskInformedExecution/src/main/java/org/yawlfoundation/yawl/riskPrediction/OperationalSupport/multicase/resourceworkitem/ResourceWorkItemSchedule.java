package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 22/07/14.
 */
public class ResourceWorkItemSchedule {

    Map<String, Map<WorkItemRecord, Long>> schedule = new HashMap<String, Map<WorkItemRecord, Long>>();

    public void addScheduleToResource(String resource, WorkItemRecord workItem, Long time) {
        Map<WorkItemRecord, Long> result = null;
        if((result = schedule.get(resource)) == null) {
            result = new HashMap<WorkItemRecord, Long>();
            schedule.put(resource, result);
        }

        result.put(workItem, time);

    }

    public Map<WorkItemRecord, Long> getResourceSchedule(String resource) {
        return schedule.get(resource);
    }

    public Map<String, Map<WorkItemRecord, Long>> getSchedule() {
        return schedule;
    }

    public void clear() {
        schedule.clear();
    }
}
