package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import java.util.*;

/**
 * Created by user on 22/07/14.
 */
public class ResourceWorkItemMapping {

    HashMap<String, HashSet<WorkItemRecord>> resourceAuthorization = new HashMap<String, HashSet<WorkItemRecord>>();

    public void clear() {
        resourceAuthorization.clear();
    }

    public HashSet<WorkItemRecord> getWorkItems(String resource) {
        return resourceAuthorization.get(resource);
    }

    public Collection<HashSet<WorkItemRecord>> getAllWorkItems() {
        return resourceAuthorization.values();
    }

    public void setWorkItems(String resource, HashSet<WorkItemRecord> workItems) {
        resourceAuthorization.put(resource, workItems);
    }

    public void removeWorkItemFromAll(WorkItemRecord workItem) {
        LinkedList<String> remove = new LinkedList<String>();
        for(Map.Entry<String, HashSet<WorkItemRecord>> entry : resourceAuthorization.entrySet()) {

            entry.getValue().remove(workItem);
            if(entry.getValue().size() == 0) remove.add(entry.getKey());

        }

        for(String res : remove) {
            resourceAuthorization.remove(res);
        }
    }

    public boolean removeWorkItem(String resource, WorkItemRecord workItem) {
        HashSet<WorkItemRecord> set = null;
        if((set = resourceAuthorization.get(resource)) == null) {
            return false;
        }else {
            set.remove(workItem);
            if(set.size() == 0) {
                resourceAuthorization.remove(resource);
            }
            return true;
        }
    }

    public void addWorkItem(String resource, WorkItemRecord workItem) {
        HashSet<WorkItemRecord> set = null;
        if((set = resourceAuthorization.get(resource)) == null) {
            set = new HashSet<WorkItemRecord>();
            resourceAuthorization.put(resource, set);
        }
        set.add(workItem);
    }

    public boolean hasResource(String resource) {return resourceAuthorization.containsKey(resource);}

    public Set<String> getResources() {return resourceAuthorization.keySet();}

    public boolean hasWorkItem(String resource, WorkItemRecord workItem) {
        HashSet<WorkItemRecord> set = null;
        if((set = resourceAuthorization.get(resource)) == null) {
            return false;
        }else {
            return set.contains(workItem);
        }
    }

    public boolean addResource(String resource) {
        if (resourceAuthorization.containsKey(resource)) {
            return false;
        }else {
            resourceAuthorization.put(resource, new HashSet<WorkItemRecord>());
            return true;
        }
    }

    public boolean removeResource(String resource) {
        return resourceAuthorization.remove(resource) != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry entry : resourceAuthorization.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
        }

        return sb.toString();
    }

}
