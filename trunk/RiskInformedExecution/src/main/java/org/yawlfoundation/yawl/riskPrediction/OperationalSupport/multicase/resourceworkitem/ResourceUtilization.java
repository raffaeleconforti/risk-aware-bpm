package org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.resourceworkitem;

import java.util.Map;

/**
 * Created by user on 22/07/14.
 */
public class ResourceUtilization extends  ResourceWorkItemMapping{

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry entry : resourceAuthorization.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" performs :");
            sb.append(entry.getValue());
        }

        return sb.toString();
    }
}
