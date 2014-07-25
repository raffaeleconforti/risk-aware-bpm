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

package org.yawlfoundation.yawl.sensors.engineInterface.YAWL;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Author: Raffaele Conforti
 * Creation Date: 22/10/2010
 */
public class SensorServlet extends HttpServlet {

    /** Read settings from web.xml and use them to initialise the service */
    @Override
	public void init() {
        try {
            ServletContext context = getServletContext();

            // load the urls of the required interfaces
            Map<String, String> urlMap = new Hashtable<String, String>();
            String _resClientURI = context.getInitParameter("ResourceGateway");
            if (_resClientURI != null) urlMap.put("resourceGateway", _resClientURI);
            String _resLogClientURI = context.getInitParameter("ResourceLogGateway");
            if (_resLogClientURI != null) urlMap.put("resourceLogGateway", _resLogClientURI);
            String _workQueueClientURI = context.getInitParameter("WorkQueueGateway");
            if (_workQueueClientURI != null) urlMap.put("workQueueGateway", _workQueueClientURI);
            String _ixClientURI = context.getInitParameter("SensorGateway");
            if (_ixClientURI != null) urlMap.put("sensorGateway", _ixClientURI);
            String _monitorSensorService = context.getInitParameter("monitorSensorService");
            if (_monitorSensorService != null) urlMap.put("monitorSensorService", _monitorSensorService);
            String timePredictionService = context.getInitParameter("timePredictionService");
            if (timePredictionService != null) urlMap.put("timePredictionService", timePredictionService);

            YAWL_EngineInterface.getInstance();
            YAWL_EngineInterface.initInterfaces(urlMap);
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass()).error("Sensor Service Initialisation Exception", e);
        }
    }
    
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	doPost(request, response);
    }
    
    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String action = request.getParameter("action");
    	String result = null;
    	if("mitigate".equals(action)){
    		String caseID = request.getParameter("caseID");
    		result = YAWL_EngineInterface.getInstance().mitigate(caseID);
    		System.out.println(result);
    		response.getOutputStream().print(result);
    	}else {
    		response.getOutputStream().print("CIAO");
    	}
    	
    }
    
    public static void main(String[] args) {
		String result = YAWL_EngineInterface.getInstance().mitigate("380");
		System.out.println(result);
    }

}
