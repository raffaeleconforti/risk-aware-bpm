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

package org.yawlfoundation.yawl.riskPrediction.OperationalSupport;

import org.jdom.Element;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role;
import org.yawlfoundation.yawl.util.JDOMUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Author: Michael Adams
 * Creation Date: 1/06/2010
 */
public class OperationalSupportServlet extends HttpServlet {
	
	public static void main(String[] args) throws IOException {
		OperationalSupportServlet oss = new OperationalSupportServlet();
		ServletConfig sc = new ServletConfig() {
			
			@Override
			public String getServletName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ServletContext getServletContext() {
				ServletContext sc1 = new ServletContext() {
						
					@Override
					public void setAttribute(String arg0, Object arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void removeAttribute(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void log(String arg0, Throwable arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void log(Exception arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void log(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public Enumeration getServlets() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Enumeration getServletNames() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getServletContextName() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Servlet getServlet(String arg0) throws ServletException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getServerInfo() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Set getResourcePaths(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public InputStream getResourceAsStream(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public URL getResource(String arg0) throws MalformedURLException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public RequestDispatcher getRequestDispatcher(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getRealPath(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public RequestDispatcher getNamedDispatcher(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public int getMinorVersion() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public String getMimeType(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public int getMajorVersion() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public Enumeration getInitParameterNames() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getInitParameter(String arg0) {
						if(arg0.equals("type")) return "1";
						if(arg0.equals("basePath")) return "/home/stormfire/apache-tomcat-7.0.14/webapps/riskPrevention/";
						return null;
					}
					
					@Override
					public ServletContext getContext(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Enumeration getAttributeNames() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Object getAttribute(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}
				};
				return sc1;
			}
			
			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getInitParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		oss.init(sc);
		
		HttpServletRequest re = new HttpServletRequest() {
			
			@Override
			public void setCharacterEncoding(String arg0)
					throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public int getServerPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getServerName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getScheme() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRemotePort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getRemoteHost() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRemoteAddr() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRealPath(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public BufferedReader getReader() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getParameterValues(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map getParameterMap() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getParameter(String arg0) {
//				return "<request><type>execution</type><workItems><workItem><caseID>393</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-84b0d53d-5a1d-41b3-9721-b7a05014be62</resource><data><Open_Claim_3><Customer>Customer9</Customer><ClaimType>Compulsory</ClaimType><DamageType>Fire</DamageType><Amount>2000</Amount></Open_Claim_3></data></request>";
				return "<request><type>execution</type><workItems><workItem><caseID>393</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-c24ff639-2f45-4ffe-b9c5-ffebf11f7087</resource><data><Open_Claim_3>\n"+
				  "  <Customer>Customer9</Customer>\n"+
				  "  <ClaimType>Compulsory</ClaimType>\n"+
				  "  <DamageType>Fire</DamageType>\n"+
				  "  <Amount>2000</Amount>\n"+
				  "</Open_Claim_3></data></request>";
			}
			
			@Override
			public Enumeration getLocales() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getLocalPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getLocalAddr() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ServletInputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isUserInRole(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdValid() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromUrl() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromURL() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromCookie() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpSession getSession(boolean arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpSession getSession() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getServletPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRequestedSessionId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public StringBuffer getRequestURL() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRequestURI() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRemoteUser() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getQueryString() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPathTranslated() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPathInfo() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getMethod() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getIntHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Enumeration getHeaders(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getHeader(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getDateHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Cookie[] getCookies() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAuthType() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		oss.doPost(re, null);
	}
	
	/** Read settings from web.xml and use them to initialise the service */
    @Override
	public void init() {
        try {
            ServletContext context = getServletContext();

            // load the urls of the required interfaces
            Map<String, String> urlMap = new Hashtable<String, String>();
//            String engineGateway = context.getInitParameter("engineGateway");
//            if (engineGateway != null) urlMap.put("engineGateway", engineGateway);
//            String engineLogGateway = context.getInitParameter("engineLogGateway");
//            if (engineGateway != null) urlMap.put("engineLogGateway", engineLogGateway);
//            String resourceGateway = context.getInitParameter("resourceGateway");
//            if (engineGateway != null) urlMap.put("resourceGateway", resourceGateway);
//            String resourceLogGateway = context.getInitParameter("resourceLogGateway");
//            if (resourceLogGateway != null) urlMap.put("resourceLogGateway", resourceLogGateway);
//            
//            String type = context.getInitParameter("type");
//            if (type != null) urlMap.put("type", type);
//            String basePath = context.getInitParameter("basePath");
//            if (basePath != null) urlMap.put("basePath", basePath);
//            String Xlog = context.getInitParameter("Xlog");
//            if (Xlog != null) urlMap.put("Xlog", Xlog);
//            String specification = context.getInitParameter("specification");
//            if (specification != null) urlMap.put("specification", specification);
//            String resources = context.getInitParameter("resources");
//            if (resources != null) urlMap.put("resources", resources);
            
            @SuppressWarnings("rawtypes")
			Enumeration e = context.getInitParameterNames(); 
            
            while(e.hasMoreElements()) {
            	String key = (String) e.nextElement();
            	urlMap.put(key, context.getInitParameter(key));
            }

            OperationalSupportService.getInstance().initInterfaces(urlMap); 
            System.out.println(urlMap);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
	public void init(ServletConfig sc) {
        try {
            ServletContext context = sc.getServletContext();

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
            
            String type = context.getInitParameter("type");
            if (type != null) urlMap.put("type", type);
            String basePath = context.getInitParameter("basePath");
            if (basePath != null) urlMap.put("basePath", basePath);
            String Xlog = context.getInitParameter("Xlog");
            if (Xlog != null) urlMap.put("Xlog", Xlog);
            String specification = context.getInitParameter("specification");
            if (specification != null) urlMap.put("specification", specification);
            String resources = context.getInitParameter("resources");
            if (resources != null) urlMap.put("resources", resources);
            
            String classifierPathAndName = context.getInitParameter("classifierPathAndName");
            if (classifierPathAndName != null) urlMap.put("classifierPathAndName", classifierPathAndName);
            
            String classifierOptions = context.getInitParameter("classifierOptions");
            if (classifierOptions != null) urlMap.put("classifierOptions", classifierOptions);

//            @SuppressWarnings("rawtypes")
//			Enumeration e = context.getInitParameterNames(); 
//            
//            while(e.hasMoreElements()) {
//            	String key = (String) e.nextElement();
//            	urlMap.put(key, context.getInitParameter(key));
//            }
            
            OperationalSupportService.getInstance().initInterfaces(urlMap); 
            System.out.println(urlMap);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	doPost(request, response);
    }

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	String xml = request.getParameter("request");
//    	System.out.println(xml);
//    	String xml = "<request><type>routing</type><workItems><workItem><caseID>384</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-b39db4e2-2e39-4fef-ab78-810d58c12699</resource><data /></request>";
//    	String xml = "<request><type>execution</type><workItems><workItem><caseID>384</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-c24ff639-2f45-4ffe-b9c5-ffebf11f7087</resource><data><Open_Claim_3><Customer>Customer25</Customer><ClaimType>Comprehensive</ClaimType><DamageType>Windscreen</DamageType><Amount>5000</Amount></Open_Claim_3></data></request>";
//    	String xml = "<request><type>execution</type><workItems><workItem><caseID>392</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-84b0d53d-5a1d-41b3-9721-b7a05014be62</resource><data><Open_Claim_3><Customer>Customer1</Customer><ClaimType>Compulsory</ClaimType><DamageType>Fire</DamageType><Amount>100</Amount></Open_Claim_3></data></request>";
    	
    	Element e = JDOMUtil.stringToElement(xml);
    	
    	boolean execution = ("execution".equals(e.getChildText("type")))?true:false;
    	
    	Element workItems = e.getChild("workItems");
    	LinkedList<WorkItemRecord> wks = new LinkedList<WorkItemRecord>();
    	
    	for(Element workItem : (List<Element>) workItems.getChildren()) {
    		
    		String caseID = workItem.getChildText("caseID");
    		String taskID = workItem.getChildText("taskID");
    		String specURI = workItem.getChildText("specificationURI");
    		
    		wks.add(new WorkItemRecord(caseID, taskID, specURI, null, null));
    		
    	}
    	
    	String resource = e.getChildText("resource");
    	
    	String data = null;
    	Map<WorkItemRecord, Long> res = null;
    	
    	if(execution) {
    		
    		data = JDOMUtil.elementToString(e.getChild("data"));
    		res = OperationalSupportService.getInstance().suggestExecutionDecisionPoints(wks.getFirst(), resource, data);
    		
    	}else {

        	InterfaceManager im = new InterfaceManager(InterfaceManager.YAWL, null);
        	resource = ((YAWL_Role) im.getRoleLayer()).getParticipantNameFromUserID(resource);
    		
    		res = OperationalSupportService.getInstance().suggestDecisionPoints(wks.toArray(new WorkItemRecord[0]), resource, System.currentTimeMillis());
    		
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("<responce>");
    	sb.append("<coupleWorkItemsPredictions>");
    	
    	WorkItemRecord key = null;
    	for(Entry<WorkItemRecord, Long> entry : res.entrySet()) {
    		
    		key = entry.getKey();
    		
    		sb.append("<coupleWorkItemPrediction>");
    		
    		sb.append("<workItem>");
	    		sb.append("<caseID>"+key.getRootCaseID()+"</caseID>");
				sb.append("<taskID>"+key.getTaskID()+"</taskID>");
				sb.append("<specificationURI>"+key.getSpecURI()+"</specificationURI>");
    		sb.append("</workItem>");
    		
    		sb.append("<prediction>");
    		sb.append(entry.getValue());
    		sb.append("</prediction>");
    		
    		sb.append("</coupleWorkItemPrediction>");
    	}
    	
    	sb.append("</coupleWorkItemsPredictions>");
    	sb.append("</responce>");	
    	
    	System.out.println(sb.toString());
    	
    	response.setContentType("text/xml");
        OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        
        osw.write(sb.toString());
        osw.flush();
        osw.close();
//    	String xml = request.getParameter("request");
////    	String xml = "<request><type>execution</type><workItems><workItem><caseID>375</caseID><taskID>Arrange_Pickup_Appointment_454</taskID><specificationURI>Carrier_Appointment</specificationURI></workItem></workItems><resource>PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821</resource><data><Arrange_Pickup_Appointment><PickupInstructions><ShipmentNumber>2</ShipmentNumber><PickupDate>2013-01-07</PickupDate><PickupInstructions /><PickupSpot /></PickupInstructions></Arrange_Pickup_Appointment></data></request>";
//    	
//    	Element e = JDOMUtil.stringToElement(xml);
//    	
//    	boolean execution = ("execution".equals(e.getChildText("type")))?true:false;
//    	
//    	Element workItems = e.getChild("workItems");
//    	LinkedList<WorkItemRecord> wks = new LinkedList<WorkItemRecord>();
//    	
//    	for(Element workItem : (List<Element>) workItems.getChildren()) {
//    		
//    		String caseID = workItem.getChildText("caseID");
//    		String taskID = workItem.getChildText("taskID");
//    		String specURI = workItem.getChildText("specificationURI");
//    		
//    		wks.add(new WorkItemRecord(caseID, taskID, specURI, null, null));
//    		
//    	}
//    	
//    	String resource = e.getChildText("resource");
//    	
//    	String data = null;
//    	Map<WorkItemRecord, Long> res = new HashMap<WorkItemRecord, Long>();
//    	
//    	for(WorkItemRecord wir : wks) {
//    		res.put(wir, 20L);
//    	}
//    	
//    	StringBuilder sb = new StringBuilder();
//    	
//    	sb.append("<responce>");
//    	sb.append("<coupleWorkItemsPredictions>");
//    	
//    	WorkItemRecord key = null;
//    	for(Entry<WorkItemRecord, Long> entry : res.entrySet()) {
//    		
//    		key = entry.getKey();
//    		
//    		sb.append("<coupleWorkItemPrediction>");
//    		
//    		sb.append("<workItem>");
//	    		sb.append("<caseID>"+key.getCaseID()+"</caseID>");
//				sb.append("<taskID>"+key.getTaskID()+"</taskID>");
//				sb.append("<specificationURI>"+key.getSpecURI()+"</specificationURI>");
//    		sb.append("</workItem>");
//    		
//    		sb.append("<prediction>");
//    		double pred = (entry.getValue());
//    		sb.append(pred/20);
//    		sb.append("</prediction>");
//    		
//    		sb.append("</coupleWorkItemPrediction>");
//    	}
//    	
//    	sb.append("</coupleWorkItemsPredictions>");
//    	sb.append("</responce>");	
//    	
//    	System.out.println(sb.toString());
//    	response.setContentType("text/xml");
//        OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
//        
//        osw.write(sb.toString());
//        osw.flush();
//        osw.close();
    }

}
