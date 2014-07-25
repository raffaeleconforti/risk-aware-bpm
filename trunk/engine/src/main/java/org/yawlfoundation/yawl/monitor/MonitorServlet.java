/*
 * Copyright (c) 2004-2010 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.monitor;

import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.sensors.YSensorNotification;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Hashtable;
import java.util.Map;

/**
 * Author: Michael Adams
 * Creation Date: 1/06/2010
 */
public class MonitorServlet extends HttpServlet {
	
	public static void main(String[] args) {
		 YSensorNotification ysn = new YSensorNotification("a", "a", 1, "a", "a", "a", "a", 1.0, 1.0, 1);
		 
	}

    /** Read settings from web.xml and use them to initialise the service */
    @Override
	public void init() {
        try {
            ServletContext context = getServletContext();

            // load the urls of the required interfaces
            Map<String, String> urlMap = new Hashtable<String, String>();
            String engineGateway = context.getInitParameter("EngineGateway");
            if (engineGateway != null) urlMap.put("engineGateway", engineGateway);
            String engineLogGateway = context.getInitParameter("EngineLogGateway");
            if (engineGateway != null) urlMap.put("engineLogGateway", engineLogGateway);
            String resourceGateway = context.getInitParameter("ResourceGateway");
            if (engineGateway != null) urlMap.put("resourceGateway", resourceGateway);
            String resourceLogGateway = context.getInitParameter("ResourceLogGateway");
            if (resourceLogGateway != null) urlMap.put("resourceLogGateway", resourceLogGateway);

            MonitorClient.getInstance().initInterfaces(urlMap); 
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass()).error("Monitor Service Initialisation Exception", e);
        }
    }
    
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	doPost(request, response);
    }

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
    	try {
	    	String action = request.getParameter("action");
	    	if("Notification".equals(action)){
	    		String caseID = request.getParameter("caseID");
		    	String nameSensor = request.getParameter("nameSensor");
		    	String status = request.getParameter("status");
		        String message = request.getParameter("message");
		        String trigger = request.getParameter("trigger");
		        String threshold = request.getParameter("threshold");
		        double probability = Double.parseDouble(request.getParameter("probability"));
		        double consequence = Double.parseDouble(request.getParameter("cons"));
		        int numberNotification = Integer.parseInt(request.getParameter("number"));
		        long timeStamp = Long.parseLong(request.getParameter("timestamp"));
		        
		        YSensorNotification ysn = new YSensorNotification(caseID, nameSensor, numberNotification, status, message, trigger, threshold, probability, consequence, timeStamp);
		        MonitorClient.getInstance().receiveSensorNotification(ysn); 
	    	}else if("cancelNotification".equals(action)){
	    		String caseID = request.getParameter("caseID");
		    	String nameSensor = request.getParameter("nameSensor");
		        int numberNotification = new Integer(request.getParameter("number"));
		        
		        YSensorNotification ysn = new YSensorNotification(caseID, nameSensor, numberNotification, null, null, null, null, 0.0, 0.0, 0);
		        MonitorClient.getInstance().removeSensorNotification(ysn); 
	    	}
	    	response.getOutputStream().print("ok");
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

}
