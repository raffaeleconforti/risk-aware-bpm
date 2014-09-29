package org.yawlfoundation.yawl.sensors.timePrediction;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Raffaele Conforti
 * Creation Date: 1/06/2010
 */

public class TimePredictionServlet  extends HttpServlet {

    /** Read settings from web.xml and use them to initialise the service */
	@Override
	public void init() {
        try {
            TimePredictionClient.getInstance(); 
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass()).error("TimePrediction Service Initialisation Exception", e);
        }
    }

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String action = request.getParameter("action");
    	if("Prediction".equals(action)){
    		
    		String caseID = request.getParameter("caseID");
    		String name = request.getParameter("name");
    		boolean task = new Boolean(request.getParameter("task"));
    		String listActivities = request.getParameter("listActivities");
    		
    		LinkedList<String> activities = new LinkedList<String>();
    		
    		Element elements = JDOMUtil.stringToElement(listActivities);
    		for(Element ele : (List<Element>) elements.getChildren()) {
    			activities.add(ele.getValue());
    		}
	        
    		String prediction = TimePredictionClient.getInstance().getPrediction(caseID, activities, name, task);
	        
	        response.getOutputStream().print(prediction);
	        
    	}else if("updatePrediction".equals(action)){

    		String URI = request.getParameter("URI");
    		String version = request.getParameter("version");
    		
    		TimePredictionClient.getInstance().updatePrediction(URI, version);
    		
    	}else if("updatePredictions".equals(action)){
    		
    		TimePredictionClient.getInstance().updatePredictions();
    		
    	}
    }

}

