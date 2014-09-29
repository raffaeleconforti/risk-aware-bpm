package org.yawlfoundation.yawl.sensors;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sf.saxon.s9api.SaxonApiException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacadeGataway;
import org.yawlfoundation.yawl.sensors.language.loops.ForInterpreter;
import org.yawlfoundation.yawl.sensors.monitorInterface.MonitorInterface;
import org.yawlfoundation.yawl.sensors.monitorInterface.YAWL.YAWL_MonitorInterface;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;


/**
 * Author: Raffaele Conforti
 * Date: 28-lug-2010
 * Time: 14.15.04
 */

public class YSensor{
	
	protected String event = null;
	protected MonitorInterface mi = null;
//	private TimePredictionInterface tpi = null;
	protected double startTime;
	protected WorkQueueGatewayClient _workQueueClient;
	protected final String _user = "sensorService";
	protected final String _password ="ySensor";
	protected String _resourceHandle;
	protected boolean previousNotification = false;
	protected long[] time;
	protected int numberNotification = 0;
	
	protected String riskMessage;
	protected String riskProbability;
	protected String riskThreshold;
//	private String postRiskProbability;
//	private String postRiskThreshold;
	
	protected String faultMessage;
	protected String faultCondition;
//	private String postFaultCondition;

	protected String consequence;
//	private String postConsequence;
	
	protected String caseID;
	protected String name;

	protected final String[] times = new String[]{"Seconds", "Minutes", "Hours", "Days", "Weeks", "Month", "Years", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	protected final long[] timesMillis = new long[]{1000, 60000, 3600000, 86400000, 604800017, (86400000*30)+37800000, (86400000*365)+21600000 };
//	private LinkedList<String> timePassed = new LinkedList<String>();
	protected final LinkedList<String> timeMillis = new LinkedList<String>();
	protected final LinkedList<String> timeStime = new LinkedList<String>();
	protected final HashSet<String> resourceTask = new HashSet<String>();
	protected final HashMap<String, LinkedList<Object>> forNameVar = new HashMap<String, LinkedList<Object>>();
	protected final HashMap<String, Object> variables = new HashMap<String, Object>();
	protected final HashMap<String, LinkedList<String>> variablesLevel = new HashMap<String, LinkedList<String>>();
	protected final HashMap<String, LinkedList<String>> variablesNames = new HashMap<String, LinkedList<String>>();
	protected final HashMap<String, LinkedList<String>> timesNames = new HashMap<String, LinkedList<String>>();
	protected final HashMap<String, LinkedList<String>> resourcesNames = new HashMap<String, LinkedList<String>>();
	protected final HashMap<String, String> mappingName = new HashMap<String, String>();
	protected final LinkedList<String> logVariables = new LinkedList<String>();
	protected final LinkedList<String> logResources = new LinkedList<String>();
	protected final LinkedList<String> distributionSet = new LinkedList<String>();
	protected final LinkedList<String> role = new LinkedList<String>();
	protected final LinkedList<String> participant = new LinkedList<String>();
	protected final LinkedList<String> resource = new LinkedList<String>();
	protected final DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private IfThenElseInterpreter itei = new IfThenElseInterpreter();
	protected ForInterpreter itei;
	
	protected SensorPredictor sp = null;
	
	protected String specification = null;
	
	/**
	 * 
	 * @param workQueueClient the WorkQueueGatewayClient associated to the YSensorManagerImpl
	 * @param caseID CaseID of the Process
	 * @param sensor XML with the sensor information (from <sensor> to </sensor>)
	 * @param time the genericTimeNotification
	 */
	
	public YSensor() {

	}
	
	public YSensor(String caseID, WorkQueueGatewayClient workQueueClient, String sensor, String time, String monitorSensorURI, String timePredictionURI, String specification){
		this.caseID = caseID;
		mi = YAWL_MonitorInterface.getInstance(monitorSensorURI);
//		tpi = YAWL_TimePredictionInterface.getInstance(timePredictionURI);
		startTime = System.currentTimeMillis();
//		wd = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.layerDB.YAWL.YAWL_WorkflowDefinition");
		_workQueueClient = workQueueClient;
		WorkQueueFacade wqf = new WorkQueueFacadeGataway(workQueueClient, _user, _password);
		itei = new ForInterpreter(wqf);
		createTime(time);
		this.specification = specification;
		initializeSensor(sensor);
		
		//
//		variables.put("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "<Company><Name>Oil Bros</Name><Address>Rome Street</Address><City>New York City</City><org.yawlfoundation.yawl.risk.state>New York</org.yawlfoundation.yawl.risk.state><PostCode>10000</PostCode><Phone>12345678</Phone><Fax>12345678</Fax><BusinessNumber>12345678</BusinessNumber></Company>");
//		String s = "<A><complete><participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></complete></A>";
//		s = JDOMUtil.elementToString(JDOMUtil.stringToElement(s).getChild("complete"));
//		variables.put("case(current).Approve_Shipment_Payment_Order_593(completeResource)", s);
//		String s1 = "<A><offer><participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></offer></A>";
//		s1 = JDOMUtil.elementToString(JDOMUtil.stringToElement(s1).getChild("offer"));
//		variables.put("case(current).Approve_Shipment_Payment_Order_593(offerResource)", s1);
//		try {
//			variables.put("case(current).Issue_Debit_Adjustment_605(StartTime)", ""+originalDateFormat.parse("2011-02-08 03:39:40").getTime());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//
	}
	
	public YSensor(String caseID, Element sensor, SensorPredictor sp){
		
		this.caseID = caseID;
		this.sp = sp;
		itei = new ForInterpreter(null);
		initializeSensor(sensor);
		
	}
	
	public String getCaseID() {
		return caseID;
	}
	
	public String getSpecification() {
		return specification;
	}

	/**
	 * Create from the String genericTime the start time and the update time
	 * @param time param genericTime
	 * @return
	 */
	protected void createTime(String time) {
		if(time.startsWith("Event_")) {
			event = time.substring(6);
		}else {
			this.time = new long[2];
			String start = time.substring(0, time.indexOf("_"));
			String update = time.substring(time.indexOf("_")+1);
			int index = 0;
			while(!update.equals(times[index]) && index<times.length) {
				index++;
			}
			if(index>6) {
				GregorianCalendar now = new GregorianCalendar();
				GregorianCalendar next = new GregorianCalendar();
				next.set(Calendar.DAY_OF_WEEK, index-7);
				if(now.compareTo(next)>0) {
					next.add(Calendar.WEEK_OF_YEAR, 1);
				}
				this.time[0] = next.getTimeInMillis()-now.getTimeInMillis();
				this.time[1] = timesMillis[4];
			}else {
				this.time[0] = (new Long(start))*timesMillis[index];
				this.time[1] = (new Long(start))*timesMillis[index];
			}
		}
	}
	
	public String getEvent() {
		return event;
	}

	/**
	 * Return the name of the Sensor
	 * @return name of the Sensor
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the notificationStartTime
	 * @return the genericStartTimeNotification if the specificTimeNotification is not defined
	 */
	public long getStartTime() {
		return time[0];
	}
	
	/**
	 * Return the notificationUpdateTime
	 * @return the genericUpdateTimeNotification if the specificTimeNotification is not defined
	 */
	public long getUpdateTime() {
		return time[1];
	}

	/**
	 * Inizialize the sensor 
	 * @param string XML with the sensor information (from <sensor> to </sensors>)
	 */
    private void initializeSensor(String string) {
		Element sensor = JDOMUtil.stringToDocument(string).getRootElement();
		name = sensor.getAttributeValue("name");
		riskMessage = "Detect risk by sensor "+name;
		faultMessage = "Detect fault by sensor "+name;
		String tm = sensor.getAttributeValue("notifyTime");
		if(tm!=null) createTime(tm);
		for(Element child : (List<Element>) sensor.getChildren()) {
			if("vars".equals(child.getName())) {
				List<Element> varsChildren = child.getChildren();
				for(Element var : varsChildren) {
					String name = var.getAttributeValue("name");
					String type = var.getAttributeValue("type");
					String mapping = var.getAttributeValue("mapping");
					createVariable(name, type, mapping);
				}
			}else if("risk".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("riskProbability".equals(child1.getName())) {
						riskProbability = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("riskThreshold".equals(child1.getName())) {
						riskThreshold = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("riskMessage".equals(child1.getName())) {
						riskMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("fault".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("faultCondition".equals(child1.getName())) {
						faultCondition = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("faultMessage".equals(child1.getName())) {
						faultMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("consequence".equals(child.getName())) {
				consequence = YExpression.PARTA+child.getValue()+YExpression.PARTC;
			}
		}
		orderList();
//		analyseCondition();
//		createFor();
	}
    
    /**
	 * Inizialize the sensor 
	 * @param string XML with the sensor information (from <sensor> to </sensors>)
	 */
    public void initializeSensor(Element sensor) {
		name = sensor.getAttributeValue("name");
		riskMessage = "Detect risk by sensor "+name;
		faultMessage = "Detect fault by sensor "+name;
		String tm = sensor.getAttributeValue("notifyTime");
		if(tm!=null) createTime(tm);
		for(Element child : (List<Element>) sensor.getChildren()) {
			if("vars".equals(child.getName())) {
				List<Element> varsChildren = child.getChildren();
				for(Element var : varsChildren) {
					String name = var.getAttributeValue("name");
					String type = var.getAttributeValue("type");
					String mapping = var.getAttributeValue("mapping");
					createVariable(name, type, mapping);
				}
			}else if("risk".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("riskProbability".equals(child1.getName())) {
						riskProbability = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("riskThreshold".equals(child1.getName())) {
						riskThreshold = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("riskMessage".equals(child1.getName())) {
						riskMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("fault".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("faultCondition".equals(child1.getName())) {
						faultCondition = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}else if("faultMessage".equals(child1.getName())) {
						faultMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("consequence".equals(child.getName())) {
				consequence = YExpression.PARTA+child.getValue()+YExpression.PARTC;
			}
		}
		orderList();
//		analyseCondition();
//		createFor();
	}
    
//    private void analyseCondition(String variable, String mapping) {
//    	StringTokenizer st = new StringTokenizer(mapping,YExpression.DOT,true);
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.DOT)) {
//				String var = token.substring(0, token.indexOf(YExpression.DOT));
//    			String method = token.substring(token.indexOf(YExpression.DOT)+1);
//    			
//    			int check = 0;
//    			if('o' == method.charAt(0)) check = 1;
//    			else if('a' == method.charAt(0)) check = 2;
//    			else if('s' == method.charAt(0)) check = 3;
//    			
//    			if(check == 1) {
//        			
//        			YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, var, "offered", check);
//        			
//    			}else if(check == 2) {
//        			
//    				YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, var, "allocated", check);
//    				
//    			}else if(check == 3) {
//        			
//    				YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, var, "started", check);
//    				
//    			}
//    		}
//    	}
//
//    }
//    
//    private String analyseCondition(String input) {
//    	if(input == null) return "0";
//    	StringTokenizer st = new StringTokenizer(input, "(-*+/%^&|!<=>){}[]", true);
//    	StringBuffer sb = new StringBuffer();
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.DOT)) {
//    			try {
//    				new Double(token);
//        			sb.append(token);
//    			}catch(NumberFormatException nfe) {
//    				String var = token.substring(0, token.indexOf(YExpression.DOT));
//        			String method = token.substring(token.indexOf(YExpression.DOT)+1);
//
//        			int check = 0;
//        			if('o' == method.charAt(0)) check = 1;
//        			else if('a' == method.charAt(0)) check = 2;
//        			else if('s' == method.charAt(0)) check = 3;
//        			
//        			if(check == 1) {
//	        			
//	        			YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, sb, var, "offered", check);
//	        			
//        			}else if(check == 2) {
//	        			
//        				YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, sb, var, "allocated", check);
//        				
//        			}else if(check == 3) {
//	        			
//        				YSensorUtilities.produceResourceVariable(method, resourceTask, mappingName, sb, var, "started", check);
//        				
//        			}
//    			}
//    		}else {
//    			sb.append(token);
//    		}
//    	}
//    	return sb.toString();
//    }
//    
//    /**
//     * Analyse the Sensing Condition looking for request of information about the resource
//     */
//    private void analyseCondition() {
//    	postRiskProbability = analyseCondition(riskProbability);
//    	
//    	postRiskThreshold = analyseCondition(riskThreshold);
//    	
//    	postFaultCondition = analyseCondition(faultCondition);
//    	
//    	postConsequence = analyseCondition(consequence);
//    }
    
    
//    private Object[] createFor(String input, int count) {
//    	StringTokenizer st = new StringTokenizer(input,"(-*+/%^&|!<=>){}",true);
//    	StringBuffer sb = new StringBuffer();
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.PARQA)) {
//    			int countBrackets = 1;
//    			boolean found = false;
//    			while(st.hasMoreTokens() && !found) {
//    				String token2 = st.nextToken();
//    				
//    				StringTokenizer st2 = new StringTokenizer(token2,"[]",true);
//    				while(st2.hasMoreTokens()) {
//    					String token3 = st2.nextToken();
//    					if(token3.equals("[")) countBrackets++;
//    					if(token3.equals("]")) countBrackets--;
//    				}
//    				
//    				token = token+token2;
////    				if(token2.contains(YExpression.PARQC)) found = true;
//    				if(countBrackets == 0) found = true;
//    			}
//    			String typeString = token.substring(0, token.indexOf(YExpression.PARQA));
//    			int type = 0;
//    			if(typeString.endsWith(YSensorUtilities.AND)) {
//    				type = 1;
//    			}else if(typeString.endsWith(YSensorUtilities.OR)) {
//    				type = 2;
//    			}else if(typeString.endsWith(YSensorUtilities.SUM)) {
//    				type = 3;
//    			}else if(typeString.endsWith(YSensorUtilities.MUL)) {
//    				type = 4;
//    			}
//    			
//    			if(type>0) {
//	    			LinkedList<String> params = new LinkedList<String>();
//	    			String tmp = token.substring(token.indexOf(YExpression.PARQA)+1,token.indexOf(YExpression.PARQC));
//	    			if(tmp.startsWith("#")) {
//	    				params.add(tmp);
//	    			}else if(tmp.contains(",")) {
//	    				while(tmp.contains(",")) {
//	    					params.add(tmp.substring(0, tmp.indexOf(",")));
//	        				tmp = tmp.substring(tmp.indexOf(",")+1);
//	    				}
//	    				params.add(tmp);
//	    			}else {
//	    				params.add(tmp);
//	    			}
//	    			String expression = token.substring(token.lastIndexOf(YExpression.PARQA)+1, token.lastIndexOf(YExpression.PARQC)); 
//	    			LinkedList<Object> forInfo = new LinkedList<Object>();
//	    			forInfo.add(type);
//	    			forInfo.add(params);
//	    			forInfo.add(expression);
//	    			forNameVar.put("for__"+count, forInfo);
//	    			mappingName.put("for__"+count, "for__"+count);
//					sb.append("for__"+count);
//	    			count++;
//    			}else {
//    				sb.append(token);
//    			}
//    		}else {
//    			sb.append(token);
//    		}
//    	}
//    	return new Object[] {sb.toString(), count};
//    }
    
//    private Object[] createFor(String input, int count) {
//    	
//    	StringBuffer sb = new StringBuffer();
//    	
//		if(input.contains(YSensorUtilities.FOR)) {
//			
//			sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
//			input = input.substring(input.indexOf(YSensorUtilities.FOR));
//			
//			while(input.contains(YSensorUtilities.FOR)) {
//				
//				if(!input.startsWith(YSensorUtilities.FOR)) {
//					sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
//					input = input.substring(input.indexOf(YSensorUtilities.FOR));
//				}
//				
//				String typeFor = input.substring(0, input.indexOf(YExpression.PARQA));
//				
//				input = input.substring(input.indexOf(YExpression.PARQA));
//				
//				int[] startEnd = YSensorUtilities.findClosure(input, "[", "]");
//				
//				String parameters = input.substring(startEnd[0], startEnd[1]+1);
//				
//				input = input.substring(startEnd[1]+1);
//				
//				startEnd = YSensorUtilities.findClosure(input, "[", "]");
//				
//				String loop = input.substring(startEnd[0], startEnd[1]+1);
//				
//				input = input.substring(startEnd[1]+1);
//				
//				String typeString = typeFor.substring(typeFor.indexOf(YSensorUtilities.FOR));
//    			int type = 0;
//    			if(typeString.endsWith(YSensorUtilities.AND)) {
//    				type = 1;
//    			}else if(typeString.endsWith(YSensorUtilities.OR)) {
//    				type = 2;
//    			}else if(typeString.endsWith(YSensorUtilities.SUM)) {
//    				type = 3;
//    			}else if(typeString.endsWith(YSensorUtilities.MUL)) {
//    				type = 4;
//    			}
//    			
//    			if(type>0) {
//	    			LinkedList<String> params = new LinkedList<String>();
//	    			String tmp = parameters.substring(parameters.indexOf(YExpression.PARQA)+1,parameters.indexOf(YExpression.PARQC));
//	    			if(tmp.startsWith("#")) {
//	    				params.add(tmp);
//	    			}else if(tmp.contains(",")) {
//	    				while(tmp.contains(",")) {
//	    					params.add(tmp.substring(0, tmp.indexOf(",")));
//	        				tmp = tmp.substring(tmp.indexOf(",")+1);
//	    				}
//	    				params.add(tmp);
//	    			}else {
//	    				params.add(tmp);
//	    			}
//	    			String expression = loop.substring(loop.indexOf(YExpression.PARQA)+1, loop.lastIndexOf(YExpression.PARQC)); 
//	    			LinkedList<Object> forInfo = new LinkedList<Object>();
//	    			forInfo.add(type);
//	    			forInfo.add(params);
//	    			forInfo.add(expression);
//	    			forNameVar.put("for__"+count, forInfo);
//	    			mappingName.put("for__"+count, "for__"+count);
//					sb.append("for__"+count);
//	    			count++;
//    			}else {
//    				sb.append(input+parameters+loop);
//    			}
//				
//			}
//			
//			sb.append(input);
//			
//		}else {
//			
//			sb.append(input);
//			
//		}
//		return new Object[] {sb.toString(), count};
//	}
    
//    /**
//     * Create the information for the execution of the "for" construct
//     */
//    private void createFor() {
//    	int count = 0;
//    	Object[] res = createFor(postRiskProbability, count);
//    	postRiskProbability = (String) res[0];
//    	count = (Integer) res[1];
//    	
//    	res = createFor(postRiskThreshold, count);
//    	postRiskThreshold = (String) res[0];
//    	count = (Integer) res[1];
//    	
//    	res = createFor(postFaultCondition, count);
//    	postFaultCondition = (String) res[0];
//    	count = (Integer) res[1];
//    	
//    	res = createFor(postConsequence, count);
//    	postConsequence = (String) res[0];
//    }

    /**
     * Order the list of logVariable, logResource, distributionSet
     */
	protected void orderList() {
		Collections.sort(logVariables);
		Collections.sort(logResources);
		Collections.sort(distributionSet);
	}

	/**
	 * Create the variable in the sensor
	 * @param name name of the Variable
	 * @param type type of the variable (role, participant or null)
	 * @param mapping the mapping of the variable with the engine variable
	 */
	protected void createVariable(String name, String type, String mapping) {
		if(YSensorUtilities.role.equals(type)) {
			if(!(mapping.contains("@") || mapping.contains(YExpression.DOT))) {
				mappingName.put(name, "role/"+mapping);
				role.addLast(mapping);
			}
		}else if(YSensorUtilities.participant.equals(type)) {
			if(!(mapping.contains("@") || mapping.contains(YExpression.DOT))) {
				mappingName.put(name, "participant/"+mapping);
				participant.addLast(mapping);
			}
		}else if(mapping.contains(YSensorUtilities.offeredList)					|| mapping.contains(YSensorUtilities.allocatedList) 			|| mapping.contains(YSensorUtilities.startedList)
				 || mapping.contains(YSensorUtilities.offeredNumber) 			|| mapping.contains(YSensorUtilities.allocatedNumber) 			|| mapping.contains(YSensorUtilities.startedNumber)
				 || mapping.contains(YSensorUtilities.offeredMinNumber) 		|| mapping.contains(YSensorUtilities.allocatedMinNumber) 		|| mapping.contains(YSensorUtilities.startedMinNumber)
				 || mapping.contains(YSensorUtilities.offeredMinNumberExcept)	|| mapping.contains(YSensorUtilities.allocatedMinNumberExcept) 	|| mapping.contains(YSensorUtilities.startedMinNumberExcept)
				 || mapping.contains(YSensorUtilities.offeredContain) 			|| mapping.contains(YSensorUtilities.allocatedContain) 			|| mapping.contains(YSensorUtilities.startedContain)){
//			analyseCondition(name, mapping);
		}else {
//			if(mapping.startsWith("case(current)")) {
//				mappingName.put(name, mapping.substring(mapping.indexOf(YExpression.DOT)+1));
//				mapping = mapping.substring(mapping.indexOf(YExpression.DOT)+1);
//				if(mapping.contains(YSensorUtilities.timeInMillis)) {
//					mappingName.put(name, mapping);
//					timeMillis.add(mapping);
//					mapping = mapping.replace("TimeInMillis", "Time");
//				}
//				if(mapping.contains(YSensorUtilities.initiator)) {
//					String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.initiator)+10);
//					String follow = mapping.substring(mapping.indexOf(YSensorUtilities.initiator)+10);
//					if(follow.length()>0) {
//						if(!variablesLevel.containsKey(nameVar)) {
//							variablesLevel.put(nameVar, new LinkedList<String>());
//						}
//						variablesLevel.get(nameVar).addLast(follow);
//					}
//					mappingName.put(name, mapping.substring(mapping.indexOf(YExpression.DOT)+1));
//					distributionSet.addLast(mapping.substring(mapping.indexOf(YExpression.DOT)+1, mapping.indexOf(YSensorUtilities.initiator)+10));
//				}else if(mapping.contains(YSensorUtilities.distribution)) {
//					String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.distribution)+13);
//					String follow = mapping.substring(mapping.indexOf(YSensorUtilities.distribution)+13);
//					if(follow.length()>0) {
//						if(!variablesLevel.containsKey(nameVar)) {
//							variablesLevel.put(nameVar, new LinkedList<String>());
//						}
//						variablesLevel.get(nameVar).addLast(follow);
//					}
//					mappingName.put(name, mapping.substring(mapping.indexOf(YExpression.DOT)+1));
//					distributionSet.addLast(mapping.substring(mapping.indexOf(YExpression.DOT)+1, mapping.indexOf(YSensorUtilities.distribution)+13));
//				}else if(mapping.contains(")")){
//					if(mapping.contains(YSensorUtilities.resource)) {
//						String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.resource)+9);
//						String follow = mapping.substring(mapping.indexOf(YSensorUtilities.resource)+9);
//						if(follow.length()>0) {
//							if(!variablesLevel.containsKey(nameVar)) {
//								variablesLevel.put(nameVar, new LinkedList<String>());
//							}
//							variablesLevel.get(nameVar).addLast(follow);
//						}
//						nameVar = nameVar.substring(0, nameVar.indexOf(YExpression.PARTA));
//						if(!resourcesNames.containsKey(nameVar)) {
//							resourcesNames.put(nameVar, new LinkedList<String>());
//						}
//						resourcesNames.get(nameVar).addLast(mapping);
//					}else if(mapping.contains("(Count)")){
//						String nameVar = mapping.substring(0, mapping.indexOf(")")+1);
//						nameVar = nameVar.substring(0, nameVar.indexOf(YExpression.PARTA));
//						if(!timesNames.containsKey(nameVar)) {
//							timesNames.put(nameVar, new LinkedList<String>());
//						}
//						timesNames.get(nameVar).addLast(mapping);
//					}else {
//						if(mapping.contains(YSensorUtilities.passTime)){
//							timePassed.add(mapping);
//							String nameVar = mapping.substring(0, mapping.indexOf(")")+1);
//							nameVar = nameVar.substring(0, nameVar.indexOf(YExpression.PARTA));
//							if(!timesNames.containsKey(nameVar)) {
//								timesNames.put(nameVar, new LinkedList<String>());
//							}
//							mapping = mapping.replace(YSensorUtilities.passTime, YSensorUtilities.offerTime);
//							timesNames.get(nameVar).addLast(mapping);
//							mapping = mapping.replace(YSensorUtilities.offerTime, YSensorUtilities.completeTime);
//							timesNames.get(nameVar).addLast(mapping);
//						}if(mapping.contains("(TimeEstimationInMillis")){
//							timeStime.addLast(mapping);
//							if(!mapping.startsWith(YExpression.PARTA)) {
//								String nameVar = mapping.substring(0, mapping.indexOf(")")+1);
//								nameVar = nameVar.substring(0, nameVar.indexOf(YExpression.PARTA));
//								if(!timesNames.containsKey(nameVar)) {
//									timesNames.put(nameVar, new LinkedList<String>());
//								}
//								mapping = mapping.replace("(TimeEstimationInMillis", YSensorUtilities.offerTime);
//								mapping = mapping.substring(0, mapping.indexOf(")")+1);
//								timesNames.get(nameVar).addLast(mapping);
//							}
//						}else {
//							String nameVar = mapping.substring(0, mapping.indexOf(")")+1);
//							nameVar = nameVar.substring(0, nameVar.indexOf(YExpression.PARTA));
//							if(!timesNames.containsKey(nameVar)) {
//								timesNames.put(nameVar, new LinkedList<String>());
//							}
//							timesNames.get(nameVar).addLast(mapping);
//						}
//					}
//				}else {
//					if(mapping.contains("@")) {
//						String nameVar = mapping.substring(0, mapping.indexOf("@"));
//						String preFollow = nameVar.substring(nameVar.lastIndexOf(YExpression.DOT));
//						nameVar = nameVar.substring(0, nameVar.lastIndexOf(YExpression.DOT));
//						String follow = preFollow+mapping.substring(mapping.indexOf("@"));
//						if(follow.length()>0) {
//							if(!variablesLevel.containsKey(nameVar)) {
//								variablesLevel.put(nameVar, new LinkedList<String>());
//							}
//							variablesLevel.get(nameVar).addLast(follow);
//						}
//						if(!variablesNames.containsKey(nameVar.substring(0, nameVar.indexOf(YExpression.DOT)))) {
//							variablesNames.put(nameVar.substring(0, nameVar.indexOf(YExpression.DOT)), new LinkedList<String>());
//						}
//						String variableTMP = nameVar.substring(nameVar.indexOf(YExpression.DOT)+1);
//						variablesNames.get(nameVar.substring(0, nameVar.indexOf(YExpression.DOT))).addLast(variableTMP);
//					}else {
//						if(!variablesNames.containsKey(mapping.substring(0, mapping.indexOf(YExpression.DOT)))) {
//							variablesNames.put(mapping.substring(0, mapping.indexOf(YExpression.DOT)), new LinkedList<String>());
//						}
//						String variableTMP = mapping.substring(mapping.indexOf(YExpression.DOT)+1);
//						variablesNames.get(mapping.substring(0, mapping.indexOf(YExpression.DOT))).addLast(variableTMP);
//					}
//				}
//			}else {
//				if(mapping.contains(YSensorUtilities.time) && !mapping.contains(YSensorUtilities.timeInMillis)) {
//					if(mapping.endsWith(YSensorUtilities.timeInMillis)) {
//						mappingName.put(name, mapping);
//						timeMillis.add(mapping);
//					}
//					mapping = mapping.replace("Time)", "TimeInMillis)");
//				}
//				if(mapping.contains("(varCount)")){
//					mappingName.put(name, mapping);
//					varCount.add(mapping);
//				}else
				if(mapping.endsWith(YSensorUtilities.initiator)) {
					String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.initiator)+10);
					String follow = mapping.substring(mapping.indexOf(YSensorUtilities.initiator)+10);
					if(follow.length()>0) {
						if(!variablesLevel.containsKey(nameVar)) {
							variablesLevel.put(nameVar, new LinkedList<String>());
						}
						variablesLevel.get(nameVar).addLast(follow);
					}
					mappingName.put(name, mapping.substring(mapping.indexOf(YExpression.DOT)+1));
					logVariables.addLast(mapping.substring(mapping.indexOf(YExpression.DOT)+1, mapping.indexOf(YSensorUtilities.initiator)+10));
				}else if(mapping.endsWith(YSensorUtilities.distribution)) {
					String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.distribution)+13);
					String follow = mapping.substring(mapping.indexOf(YSensorUtilities.distribution)+13);
					if(follow.length()>0) {
						if(!variablesLevel.containsKey(nameVar)) {
							variablesLevel.put(nameVar, new LinkedList<String>());
						}
						variablesLevel.get(nameVar).addLast(follow);
					}
					mappingName.put(name, mapping.substring(mapping.indexOf(YExpression.DOT)+1));
					logVariables.addLast(mapping.substring(mapping.indexOf(YExpression.DOT)+1, mapping.indexOf(YSensorUtilities.distribution)+13));
				}else {
					mappingName.put(name, mapping);
					if(mapping.endsWith(YSensorUtilities.resource)) {
//					if(mapping.contains(YSensorUtilities.resource)) {
//						String nameVar = mapping.substring(0, mapping.indexOf(YSensorUtilities.resource)+9);
//						String follow = mapping.substring(mapping.indexOf(YSensorUtilities.resource)+9);
//						if(follow.length()>0) {
//							if(!variablesLevel.containsKey(nameVar)) {
//								variablesLevel.put(nameVar, new LinkedList<String>());
//							}
//							variablesLevel.get(nameVar).addLast(follow);
//						}
//						logResources.addLast(nameVar);
						logVariables.addLast(mapping);
					}else {
						if(mapping.contains("@")) {
							String nameVar = mapping.substring(0, mapping.indexOf("@"));
							String preFollow = nameVar.substring(nameVar.lastIndexOf(YExpression.DOT));
							nameVar = nameVar.substring(0, nameVar.lastIndexOf(YExpression.DOT));
							String follow = preFollow+mapping.substring(mapping.indexOf("@"));
							if(follow.length()>0) {
								if(!variablesLevel.containsKey(nameVar)) {
									variablesLevel.put(nameVar, new LinkedList<String>());
								}
								variablesLevel.get(nameVar).addLast(follow);
							}
							String variableTMP = nameVar.substring(nameVar.indexOf(YExpression.DOT)+1);
							logVariables.addLast(variableTMP);
						}else {
//							if(mapping.contains("(TimeEstimationInMillis)")){
//								timeStime.addLast(mapping);
//								String enable = mapping.replace("(TimeEstimationInMillis", YSensorUtilities.offerTimeInMillis);
//								enable = enable.substring(0, enable.indexOf(")")+1);
//								logVariables.addLast(enable);
//							}else {
								logVariables.addLast(mapping);
//							}
						}
					}
				}
//			}
		}
	}
	
	/**
	 * Return the mapping name of the variable if varName is a sensor's variable
	 * @param varName
	 * @return
	 */
	public String isVariable(String varName) {
		return mappingName.get(varName);
	}
	
	/**
	 * Return the value of the variable with the mappingName passed
	 * @param mappingName
	 * @return
	 */
	public Object getVariable(String mappingName) {
		return variables.get(mappingName);
	}

	/**
	 * Update the information of the Sensor
	 * @param variableUpdated message that contain all the information
	 */
	public void update(YSensorMessageUpdate variableUpdated){
		updateWithoutNotification(variableUpdated);
		evaluate();
    }
	
	/**
	 * Update the information of the Sensor
	 * @param variableUpdated message that contain all the information
	 */
	public void updateWithoutNotification(YSensorMessageUpdate variableUpdated){
		if(variableUpdated.variablesSize()>0) {
			HashMap<String, Object> disTMP = variableUpdated.getDistribution();
			String variableName = null;
			Object o = null;
			for(Entry<String, Object> entry: disTMP.entrySet()){
				variableName = entry.getKey();
				o = entry.getValue();
				if(variablesLevel.containsKey(variableName)) {
					for(String s : variablesLevel.get(variableName)) {
//						Object tmp = disTMP.get(variableName);
						if(o instanceof String) {
							String value = (String) o;
							variables.put(variableName, getValueFromXML(s, value));
						}else {
							LinkedList<String> listValue = (LinkedList<String>) o;
							LinkedList<Object> value = new LinkedList<Object>();
							for(int i=0; i<listValue.size(); i++) {
								value.set(i, getValueFromXML(s, listValue.get(i)));
							}
							variables.put(variableName, value);
						}
					}
				}else {
					variables.put(variableName, o);
					resource.add(variableName);
				}
			}
			HashMap<String, Object> logTMP = variableUpdated.getLogVariables();
			for(Entry<String, Object> entry: logTMP.entrySet()){
				variableName = entry.getKey();
				o = entry.getValue();
				if(variablesLevel.containsKey(variableName)) {
					for(String s : variablesLevel.get(variableName)) {
//						Object tmp = logTMP.get(variableName);
						if(o instanceof String) {
							String value = (String) o;
							variables.put(variableName, getValueFromXML(s, value));
						}else {
							LinkedList<String> listValue = (LinkedList<String>) o;
							LinkedList<Object> value = new LinkedList<Object>();
							for(int i=0; i<listValue.size(); i++) {
								value.set(i, getValueFromXML(s, listValue.get(i)));
							}
							variables.put(variableName, value);
						}
					}
				}else {
//					Object tmp = logTMP.get(variableName);
					if(o instanceof String) {
						String value = (String) o;
						if(value.contains(YExpression.GREATER) || value.contains(YExpression.MINOR)) {
							try {
								Element e = JDOMUtil.stringToDocument(value).getRootElement();
								if(e.getChildren().size()==0) {
									variables.put(variableName, e.getTextNormalize());
								}else if(e.getChildren().size()==1) {
									variables.put(variableName, JDOMUtil.elementToString((Element)e.getChildren().get(0)));
								}else {
									variables.put(variableName, value);
								}
							}catch(Exception ex){
								variables.put(variableName, value);
							}
						}else {
							variables.put(variableName, value);
						}
					}else {
						variables.put(variableName, o);
					}
				}
			}
			HashMap<String, Object> netTMP = variableUpdated.getAllVariables();
			for(Entry<String, Object> entry: netTMP.entrySet()){
				variableName = entry.getKey();
				o = entry.getValue();
				if(variablesLevel.containsKey(variableName)) {
					for(String s : variablesLevel.get(variableName)) {
//						Object tmp = disTMP.get(variableName);
						if(o instanceof String) {
							String value = (String) o;
							variables.put(variableName, getValueFromXML(s, value));
						}else {
							LinkedList<String> listValue = (LinkedList<String>) o;
							LinkedList<Object> value = new LinkedList<Object>();
							for(int i=0; i<listValue.size(); i++) {
								value.set(i, getValueFromXML(s, listValue.get(i)));
							}
							variables.put(variableName, value);
						}
					}
				}else {
//					Object tmp = netTMP.get(variableName);
					if(o instanceof String) {
						String value = (String) o;
						variables.put(variableName, getValueFromXML("", value));
					}else {
						LinkedList<String> listValue = (LinkedList<String>) o;
						LinkedList<Object> value = new LinkedList<Object>();
						for(int i=0; i<listValue.size(); i++) {
							value.set(i, getValueFromXML("", listValue.get(i)));
						}
						variables.put(variableName, value);
					}
				}
			}
//			HashMap<String, Object> timeTMP = variableUpdated.getTimeVariables();
//			for(String variableName: timeTMP.keySet()){
//				variables.put(variableName, timeTMP.get(variableName));
//			}
//			HashMap<String, Object> logResTMP = variableUpdated.getLogResources();
//			for(String variableName: logResTMP.keySet()){
//				if(variablesLevel.containsKey(variableName)) {
//					for(String s : variablesLevel.get(variableName)) {
//						Object tmp = logResTMP.get(variableName);
//						if(tmp instanceof String) {
//							String value = (String) tmp;
//							variables.put(variableName, getValueFromXML(s, value));
//						}else {
//							LinkedList<String> listValue = (LinkedList<String>) tmp;
//							LinkedList<Object> value = new LinkedList<Object>();
//							for(int i=0; i<listValue.size(); i++) {
//								value.set(i, getValueFromXML(s, listValue.get(i)));
//							}
//							variables.put(variableName, value);
//						}
//					}
//				}else {
//					variables.put(variableName, logResTMP.get(variableName));
//					resource.add(variableName);
//				}
//			}
//			HashMap<String, Object> resTMP = variableUpdated.getResourceVariables();
//			for(String variableName: resTMP.keySet()){
//				if(variablesLevel.containsKey(variableName)) {
//					for(String s : variablesLevel.get(variableName)) {
//						Object tmp = resTMP.get(variableName);
//						if(tmp instanceof String) {
//							String value = (String) tmp;
//							variables.put(variableName, getValueFromXML(s, value));
//						}else {
//							LinkedList<String> listValue = (LinkedList<String>) tmp;
//							LinkedList<Object> value = new LinkedList<Object>();
//							for(int i=0; i<listValue.size(); i++) {
//								value.set(i, getValueFromXML(s, listValue.get(i)));
//							}
//							variables.put(variableName, value);
//						}
//					}
//				}else {
//					variables.put(variableName, resTMP.get(variableName));
//					resource.add(variableName);
//				}
//			}
//			HashMap<String, Object> roleTMP = variableUpdated.getRole();
//			for(String variableName: roleTMP.keySet()){
//				variables.put(variableName, roleTMP.get(variableName));
//				resource.add(variableName);
//			}
//			HashMap<String, Object> participantTMP = variableUpdated.getParticipant();
//			for(String variableName: participantTMP.keySet()){
//				variables.put(variableName, participantTMP.get(variableName));
//				resource.add(variableName);
//			}
//			for(String timeMil : timeMillis) {
//				String time = timeMil.replace("Time", "TimeInMillis");
//				String a = (String) variables.get(time);
//				if(a != null && !a.isEmpty()) variables.put(timeMil, originalDateFormat.format(new Date(new Long(a))));
//			}
		}
//		for(String timeStimed : timeStime) {
////			if(log == null) {
////				String URI = wd.getURI(caseID);
////				String Version = wd.getVersion(caseID);
////				log = wd.getOpenXESLog(caseID, URI, Version);
////			}
////			if(log != null) {
//			Double time;
//			if(timeStimed.startsWith("case(")) {
//				String task = timeStimed.replace("(TimeEstimationInMillis", YSensorUtilities.offerTimeInMillis);
//				task = task.substring(0, task.indexOf(")")+1);
//				String a = (String) variables.get(task);
//				if(a != null) time = Double.parseDouble(a);
//				else time = null;
//			}else {
//				time = startTime;
//			}
//			if(time != null) {
//				int cvsize = 0;
//				int bandSize = 0;
//				if(timeStimed.contains("))")){
//					String take = timeStimed.substring(timeStimed.indexOf("(TimeEstimationInMillis(")+24, timeStimed.indexOf("))"));
//					bandSize = new Integer(take.substring(0, take.indexOf(",")));
//					cvsize = new Integer(take.substring(take.indexOf(",")+1));
//				}
//				if(timeStimed.startsWith(YExpression.PARTA) || timeStimed.contains(").(")) {
//					if(sp != null) {
//						System.out.println("TEMPO");
//						System.out.println(sp.getPrediction(caseID, cvsize, bandSize,  0, null, System.currentTimeMillis()-time));
//						variables.put(timeStimed, ""+sp.getPrediction(caseID, cvsize, bandSize,  0, null, System.currentTimeMillis()-time));// TODO
//					}else {
//						variables.put(timeStimed, ""+tpi.getPrediction(caseID, System.currentTimeMillis()-time, cvsize, bandSize, false, null));// TODO
//					}
//				}else {
//					String task = timeStimed.substring(0, timeStimed.indexOf(YSensorUtilities.timeEstimationInMillis));
//					if(sp != null) {
//						variables.put(timeStimed, ""+sp.getPrediction(caseID, cvsize, bandSize, 1, task, System.currentTimeMillis()-time));// TODO
//					}else {
//						variables.put(timeStimed, ""+tpi.getPrediction(caseID, System.currentTimeMillis()-time, cvsize, bandSize, true, task));// TODO
//					}
//				}
//			}
//		}
//		updateVarCount();
//		updateResourceTask();
//		updateForNameVar();
//		System.out.println(variables);
    }
	
//	private void updateVarCount() {
//		for(String v : varCount) {
//			String varName = v.substring(0, v.indexOf(YExpression.DOT));
//			if(variables.containsKey(varName)) {
//				Object o = variables.get(varName);
//				if(o instanceof LinkedList) {
//					LinkedList<Object> l = (LinkedList<Object>) o;
//					variables.put(v, l.size());
//				}else {
//					variables.put(v, "null");
//				}
//			}else {
//				variables.put(v, "null");
//			}
//		}
//	}
	
//	private void updateForNameVar() {
//		if(forNameVar.size()>0) {
//			for(String s : forNameVar.keySet()) {	
//				boolean fixed = false;
//				int type = (Integer) forNameVar.get(s).get(0);
//				Object obj = forNameVar.get(s).get(1);
//				LinkedList<String> params = new LinkedList<String>();
//				if(obj instanceof LinkedList) {
//					params = (LinkedList<String>) ((LinkedList<String>) obj).clone();
//					if(params.getFirst().startsWith("#")) {
//						String tmp = params.removeFirst();
//						fixed = true;
//						tmp = tmp.substring(1);
//	    				while(tmp.contains(",")) {
//	    					params.add(tmp.substring(0, tmp.indexOf(",")));
//	        				tmp = tmp.substring(tmp.indexOf(",")+1);
//	    				}
//	    				params.add(tmp);
//					}
//				}else if(obj instanceof String) {
//					String tmp = (String) obj;
//					if('<' == tmp.charAt(0) && '>' == tmp.charAt(tmp.length()-1)) {
//						Element elem = JDOMUtil.stringToDocument(tmp).getRootElement();
//						for(Element child: (List<Element>) elem.getChildren()) {
//							params.add("<loop>"+JDOMUtil.elementToString(child)+"</loop>");
//						}
//					}else {
//						params.add(tmp);
//					}
//				}
//				String exp = (String) forNameVar.get(s).get(2);
//				int[] counts = new int[params.size()];
//				int[] countsLimit = new int[params.size()];
//				LinkedList<String>[] var = (LinkedList<String>[]) new LinkedList[params.size()];
//				for(int i=0; i<countsLimit.length; i++) {
//					var[i] = (LinkedList<String>) variables.get(mappingName.get(params.get(i)));
//					countsLimit[i] = var[i].size();
//				}
//				boolean endLoop = false;
//				String[] varString = new String[params.size()];
//				for(int i=0; i<varString.length; i++) {
//					varString[i] = var[i].getFirst();
//				}
//				int pos = 0;
//				Object res = null;
//				if(type == 1) {
//					res = new Boolean(true);
//				}else if(type == 2) {
//					res = new Boolean(false);
//				}else if(type == 3) {
//					res = new Double(0);
//				}else if(type == 4) {
//					res = new Double(1);
//				}
//				while(!endLoop) {
//					boolean mod = false;
//					if(!fixed) {
//						while(!endLoop && counts[pos]>=countsLimit[pos] && pos<counts.length) {
//							pos++;
//							if(pos==counts.length) {
//								endLoop = true;
//								mod = false;
//							}else {
//								counts[pos]++;
//								mod = true;
//								if(counts[pos]<countsLimit[pos]) {
//									varString[pos] = var[pos].get(counts[pos]);
//								}
//							}
//						}
//						while(pos>0 && mod) {
//							pos--;
//							counts[pos] = 0;
//							varString[pos] = var[pos].getFirst();
//						}
//					}else {
//						if(!endLoop && counts[0]<=countsLimit[0]) {
//							if(counts[0]==countsLimit[0]) {
//								endLoop = true;
//							}
//							for(int i = 0; i<counts.length; i++) {
//								if(counts[0]<countsLimit[0]) {
//									varString[i] = var[i].get(counts[0]);
//								}else {
//									endLoop = true;
//								}
//							}
//							counts[0]++;
//						}
//					}
//					if(!endLoop) {
//						Object result = null;
//						if(type == 1 || type == 2) {
//							StringTokenizer stTMP = new StringTokenizer(exp,"(-+*/%^&|!<=>){}",true);
//							StringBuffer sb = new StringBuffer();
//							while(stTMP.hasMoreTokens()) {
//								String token = stTMP.nextToken();
//								if(params.contains(token)) {
//									sb.append(varString[params.indexOf(token)]);
////								}else if(mappingName.containsKey(s)) {
////									String mapName = mappingName.get(s);
////									Object o = variables.get(mapName);
////									sb.append(o);
////								}else {
//									sb.append(token);
//								}
//							}
//							result = (Boolean) itei.elaborate(sb.toString(), mappingName, variables, resource);
////							result = executeIfThenElseBoolean(sb.toString());
////							result = expression.booleanEvaluation(sb.toString(), mappingName, variables, resource);
//							if(type == 1) {
//								res = (Boolean) res && (Boolean)result;
//							}else {
//								res = (Boolean) res || (Boolean)result;
//							}
//						}else if (type == 3 || type == 4) {
//							StringTokenizer stTMP = new StringTokenizer(exp,"(-+*/%^&|!<=>){}",true);
//							StringBuffer sb = new StringBuffer();
//							while(stTMP.hasMoreTokens()) {
//								String token = stTMP.nextToken();
//								if(params.contains(token)) {
//									sb.append(varString[params.indexOf(token)]);
////								}else if(mappingName.containsKey(s)) {
////									System.out.println("2" + s);
////									String mapName = mappingName.get(s);
////									Object o = variables.get(mapName);
////									sb.append(o);
//								}else {
//									sb.append(token);
//								}
//							}
//							result = (Double) itei.elaborate(sb.toString(), mappingName, variables, resource);
////							result = executeIfThenElseMath(sb.toString());
////							mathevaluator.setExpression(sb.toString());
////							result = mathevaluator.getValue();
//							if(type == 3) {
//								res = (Double) res + (Double)result;
//							}else {
//								res = (Double) res * (Double)result;
//							}
//						}
//						if(!fixed) {
//							counts[pos]++;
//							if(counts[pos]<countsLimit[pos]) {
//								varString[pos] = var[pos].get(counts[pos]);
//							}	
//						}
//					}	
//				}
//				variables.put(s, res);
//			}
//		}
//	}
	
//	private Boolean executeIfThenElse(String condition) throws ExpressionError {
//		String ifCondition = "";
//		String thenCondition = "";
//		String elseCondition = "";
////		String tmp = "";
//		System.out.println("Condition: "+condition);
//		if(condition.startsWith("IF(")) {
//			condition = condition.substring(3);
//			if(condition.contains("IF(")) {
//				String tmp = condition;
//				boolean loop = true;
//				while(tmp.contains("IF(") && loop) {
//					int a = tmp.indexOf(")THEN(");
//					int b = tmp.indexOf("IF(");
//					if(a<b) {
//						loop = false;
//					}else {
//						ifCondition += tmp.substring(0, tmp.indexOf(")]")+2);
//						tmp = tmp.substring(tmp.indexOf(")]")+2);
//					}
//				}
//				condition = tmp;
//			}else {
//				if(condition.contains(")THEN(")) {
//					ifCondition = condition.substring(0, condition.indexOf(")THEN("));
//					condition = condition.substring(condition.indexOf(")THEN(")); 
//				}else {
//					return null;
//				}
//			}
//			System.out.println("ifPart: "+ifCondition);
//			
//			if(condition.startsWith(")THEN(")){
//				condition = condition.substring(6);
//				if(condition.contains("IF(")) {
//					String tmp = condition;
//					boolean loop = true;
//					while(tmp.contains("IF(") && loop) {
//						int a = tmp.indexOf(")ELSE(");
//						int b = tmp.indexOf("IF(");
//						if(a<b || a<0) {
//							loop = false;
//							if(a<0) {
//								thenCondition = condition.substring(0, condition.length()-1);
//								tmp = "";
//							}
//						}else {
//							thenCondition += tmp.substring(0, tmp.indexOf(")]")+2);
//							tmp = tmp.substring(tmp.indexOf(")]")+2);
//						}
//					}
//					condition = tmp;
//				}else {
//					if(condition.contains(")ELSE(")) {
//						thenCondition = condition.substring(0, condition.indexOf(")ELSE("));
//						condition = condition.substring(condition.indexOf(")ELSE(")); 
//					}else {
//						thenCondition = condition.substring(0, condition.length()-1);
//						condition = "";
//					}
//				}
//			}else {
//				return null;
//			}
//			System.out.println("thenPart: "+thenCondition);
//			
//			if(condition.contains(")ELSE(")){
//				elseCondition = condition.substring(6, condition.length()-1);
//			}
//			System.out.println("elsePart: "+elseCondition);			
//			
//			Boolean ifPart = expression.booleanEvaluation(ifCondition, mappingName, variables, resource);
//			if(ifPart!=null) {
//				if(ifPart) {
//					return expression.booleanEvaluation(thenCondition, mappingName, variables, resource);
//				}else {
//					if(!elseCondition.isEmpty()) {
//						return expression.booleanEvaluation(elseCondition, mappingName, variables, resource);
//					}else {
//						return null;
//					}
//				}
//			}else {
//				return null;
//			}
//		}else {
//			return expression.booleanEvaluation(condition, mappingName, variables, resource);
//		}
//	}
//	
//    private Double executeIfThenElseMath(String condition) throws ExpressionError {
//    	if(condition.startsWith("IF(")) {
//			if(condition.contains(")THEN(")) {
//				if(condition.contains(")ELSE(")) {
//					String ifPart = condition.substring(condition.indexOf("IF(")+3, condition.indexOf(")THEN("));
//					String thenPart = condition.substring(condition.indexOf(")THEN(")+6, condition.indexOf(")ELSE("));
//					String elsePart = condition.substring(condition.indexOf(")ELSE(")+6, condition.length()-1);
//					Boolean ifResult = expression.booleanEvaluation(ifPart, mappingName, variables, resource);
//					if(ifResult!=null && ifResult) {
//						mathevaluator.setExpression(thenPart);
//						try {
//							return mathevaluator.getValue();
//						} catch (Exception e) {	
//							return null;
//						} 
//					}else if(ifResult!=null && !ifResult) {
//						mathevaluator.setExpression(elsePart);
//						try {
//							return mathevaluator.getValue();
//						} catch (Exception e) {
//							return null;
//						} 
//					}else return null;						
//				}else {
//					String ifPart = condition.substring(condition.indexOf("IF(")+3, condition.indexOf(")THEN("));
//					String thenPart = condition.substring(condition.indexOf(")THEN(")+6, condition.length()-1);
//					Boolean ifResult = expression.booleanEvaluation(ifPart, mappingName, variables, resource);
//					if(ifResult!=null && ifResult) {
//						mathevaluator.setExpression(thenPart);
//						try {
//							return mathevaluator.getValue();
//						} catch (Exception e) {
//							return null;
//						}  
//					}else return null;
//				}
//			}
//			return null;
//		}else {
//			mathevaluator.setExpression(condition);
//			try {
//				return mathevaluator.getValue();
//			} catch (Exception e) {
//				return null;
//			}
//		}
//	}
//
//	private Boolean executeIfThenElseBoolean(String condition) throws ExpressionError {
//		if(condition.startsWith("IF(")) {
//			if(condition.contains(")THEN(")) {
//				if(condition.contains(")ELSE(")) {
//					String ifPart = condition.substring(condition.indexOf("IF(")+3, condition.indexOf(")THEN("));
//					String thenPart = condition.substring(condition.indexOf(")THEN(")+6, condition.indexOf(")ELSE("));
//					String elsePart = condition.substring(condition.indexOf(")ELSE(")+6, condition.length()-1);
//					Boolean ifResult = expression.booleanEvaluation(ifPart, mappingName, variables, resource);
//					if(ifResult!=null && ifResult) {
//						return expression.booleanEvaluation(thenPart, mappingName, variables, resource); 
//					}else if(ifResult!=null && !ifResult) {
//						return expression.booleanEvaluation(elsePart, mappingName, variables, resource);
//					}else return null;						
//				}else {
//					String ifPart = condition.substring(condition.indexOf("IF(")+3, condition.indexOf(")THEN("));
//					String thenPart = condition.substring(condition.indexOf(")THEN(")+6, condition.length()-1);
//					Boolean ifResult = expression.booleanEvaluation(ifPart, mappingName, variables, resource);
//					if(ifResult!=null && ifResult) {
//						return expression.booleanEvaluation(thenPart, mappingName, variables, resource); 
//					}else return null;
//				}
//			}
//			return null;
//		}else {
//			return expression.booleanEvaluation(condition, mappingName, variables, resource);
//		}
//	}

//	/**
//     * Update the value of the resources complex variable 
//     */
//	private void updateResourceTask() {
//		if(resourceTask.size()>0) {
//			for(String s : resourceTask) {
//				String var = s.substring(0, s.indexOf("_"));
//				String method = s.substring(s.indexOf("_")+1);
//				String term = null;
//				int intMethod = 0;
//				if(method.contains("_")) {
//					term = method.substring(0, method.indexOf("_"));
//					intMethod = new Integer(method.substring(method.indexOf("_")+1));
//				}else {
//					intMethod = new Integer(method);
//				}
//				int queue = -1;
//				switch(intMethod) {
//					case 1: queue = 0; break;
//					case 2: queue = 0; break;
//					case 3: queue = 0; break;
//					case 4: queue = 0; break;
//					case 5: queue = 0; break;
//					case 6: queue = 1; break;
//					case 7: queue = 1; break;
//					case 8: queue = 1; break;
//					case 9: queue = 1; break;
//					case 10: queue = 1; break;
//					case 11: queue = 2; break;
//					case 12: queue = 2; break;
//					case 13: queue = 2; break;
//					case 14: queue = 2; break;
//					case 15: queue = 2; break;
//				}
//				try {
//					Element res = JDOMUtil.stringToElement((String)variables.get(mappingName.get(var)));
//					LinkedList l = new LinkedList();
//					for(Element child : (List<Element>) res.getChildren()) {
//						String id = child.getAttributeValue("id");
//						Element xml = JDOMUtil.stringToElement(_workQueueClient.getQueuedWorkItems(id, queue, getResourceHandle()));
//						LinkedList<String> list = new LinkedList<String>();
//						for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
//							list.addLast(workItemRecord.getChild("taskid").getValue());
//						}
//						switch(intMethod%5) {
//							case 1:
//								if(list.size()>0) l.addLast(list); 
//								break;
//							case 2: 
//								l.addLast(""+list.size()); 
//								break;
//							case 4: 
////								if(!JDOMUtil.elementToString(child).equals(term)) l.addLast(list.size());
//								if(mappingName.containsKey(term)) {
//									if(variables.get(mappingName.get(term))==null) {
//										l.addLast(list.size());
//									}else {
//										boolean check = false;
//										Element termRes = JDOMUtil.stringToElement((String)variables.get(mappingName.get(term)));
//										for(Element childRes : (List<Element>) termRes.getChildren()) {
//											String c1 = JDOMUtil.elementToString(child);
//											String c2 = JDOMUtil.elementToString(childRes);
//											if(c1.equals(c2)) {
//												check = true;
//												break;
//											}
//										}
//										if(!check) l.addLast(list.size());
//									}
//								}else {
//									if(!child.getChild("userid").getValue().equals(term)) l.addLast(list.contains(term)); 
//								}
//								break;
//							case 3: 
//								l.addLast(list.size()); 
//								break;
//							case 0: 
//								if(mappingName.containsKey(term)) {
//									if(variables.get(mappingName.get(term))==null) {
//										l.addLast(null);
//									}else {
//										l.addLast(list.contains(variables.get(mappingName.get(term))));
//									}
//								}else {
//									l.addLast(list.contains(term)); 
//								}
//								break;
//						}
//					}
//					if(l.size()==0) {
//						variables.put(s, null);
//					}else if(l.size()>1) {
//						if(intMethod%5==3 || intMethod%5==4) {
//							int min = (Integer) l.get(0);
//							for(int i = 1; i<l.size(); i++) {
//								min = Math.min(min, (Integer)l.get(i));
//							}
//							variables.put(s, ""+min);
//						}else {
//							variables.put(s, l);
//						}
//					}else {
//						if(intMethod%5==3 || intMethod%5==4) {
//							variables.put(s, ""+((Integer)l.get(0)));
//						}else {
//							variables.put(s, l.get(0));
//						}
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	
//    /**
//     * Return a section handle with the resource
//     * @return
//     */
//    
//    private String getResourceHandle() {
//        try {
//        	while (_workQueueClient.checkConnection(_resourceHandle).equals("false")) {
//				_resourceHandle = _workQueueClient.connect(_user,_password);
//			}
//		} catch (IOException e) {
//			_resourceHandle = "<failure>Problem connecting to engine.</failure>";
//		}
//        return _resourceHandle;
//    }
    
    /**
	 * Evaluate the sensingCondition
	 * @return null if is not possible to evaluate
	 */
	protected void evaluate() {
    	Double riskProbabilityValue = null;
    	Double riskThresholdValue = null;
    	
    	String riskCondition = null;
    	Boolean riskConditionValue = null;
    	
    	if(riskProbability != null) {
	    	riskProbabilityValue = (Double) itei.elaborate(riskProbability, mappingName, variables, logResources);
	    	riskThresholdValue = (Double) itei.elaborate(riskThreshold, mappingName, variables, logResources);
	    	
	    	riskCondition = riskProbabilityValue+">"+riskThresholdValue;
	    	riskConditionValue = (Boolean) itei.elaborate(riskCondition, mappingName, variables, logResources);
    	}
    	
    	Boolean faultConditionValue = null;
    	Double consequenceValue = null;
    			
    	if(faultCondition != null) {
	    	faultConditionValue = (Boolean) itei.elaborate(faultCondition, mappingName, variables, logResources);
	    	consequenceValue = (Double) itei.elaborate(consequence, mappingName, variables, logResources);
    	}

    	try {
    		
    		if(previousNotification) cancelNotification();
    		
			if(faultConditionValue != null && faultConditionValue) {
				notifyAdmin("fault", getMessage(faultMessage), convertCondition(faultCondition, mappingName, variables), null, 1, consequenceValue);//Fault
				previousNotification = true;
			} else if(riskConditionValue != null && riskConditionValue) {
				notifyAdmin("risk", getMessage(riskMessage), convertCondition(riskProbability, mappingName, variables), convertCondition(riskThreshold, mappingName, variables), riskProbabilityValue, consequenceValue);//Risk
				previousNotification = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public Double[] evaluateForPrediction() {
//    	System.out.println(mappingName);
//    	System.out.println(variables);
//    	Double riskProbabilityValue = (Double) itei.elaborate(postRiskProbability, mappingName, variables, logResources);
//    	Double riskThresholdValue = (Double) itei.elaborate(postRiskThreshold, mappingName, variables, logResources);
    	
    	Boolean faultConditionValue = (Boolean) itei.elaborate(faultCondition, mappingName, variables, logResources);

    	Double consequenceValue = (Double) itei.elaborate(consequence, mappingName, variables, logResources);
    	    	
//		String riskCondition = riskProbabilityValue+">"+riskThresholdValue;
	
//		Boolean riskConditionValue = (Boolean) itei.elaborate(riskCondition, mappingName, variables, logResources);
	
//    	if(riskConditionValue != null && riskConditionValue) {
//		if(riskProbabilityValue != null) {
//    		return new Double[] {riskProbabilityValue, consequenceValue};
//    	}
    	if(faultConditionValue != null) {
    		return new Double[] {(faultConditionValue?1.0:0.0), consequenceValue};
    	}
		
    	return new Double[] {0.0, 0.0};
    	
	}

//	/**
//	 * Evaluate the sensingCondition
//	 * @return null if is not possible to evaluate
//	 */
//    private Boolean evaluate() {
//    	Boolean riskConditionValue = null;
//    	Boolean faultConditionValue = null;
//    	boolean oldRiskNotification = previousRiskNotification;
//    	boolean oldFaultNotification = previousFaultNotification;
//    	try {
////    		riskConditionValue = expression.booleanEvaluation(postRiskCondition, mappingName, variables, resource);
//    		riskConditionValue = executeIfThenElse(postRiskCondition);
//    		if(riskConditionValue!=null && riskConditionValue){
////				faultConditionValue = expression.booleanEvaluation(postProbability, mappingName, variables, resource);
//				faultConditionValue = executeIfThenElse(postFaultCondition);
//				if(faultConditionValue!=null && faultConditionValue) {
//					if(!previousFaultNotification) {
//						previousFaultNotification = true;
//						try {
//							if(previousRiskNotification) cancelNotification();
//							notifyAdmin(getMessage(faultMessage), convertCondition(postFaultCondition, mappingName, variables));//Fault
//						} catch (IOException ioe) { ioe.printStackTrace(); }
//					}
//				} else {
//					if(!previousRiskNotification || previousFaultNotification) {
//						previousRiskNotification = true;
//						try {
//							if(previousFaultNotification) cancelNotification();
//							notifyAdmin(getMessage(riskMessage), convertCondition(postRiskCondition, mappingName, variables));//Risk
//						} catch (IOException ioe) { ioe.printStackTrace(); }
//					}
//					previousFaultNotification = false;
//				}
//			} else {
//				previousRiskNotification = false;
//				previousFaultNotification = false;
//			}
//		} catch (ExpressionError er) { 
//			previousRiskNotification = false;
//			previousFaultNotification = false; 
//			return null;
//		}
//		if((oldRiskNotification && !previousRiskNotification) || (!oldRiskNotification && !previousRiskNotification && oldFaultNotification && !previousFaultNotification)) {
//			try {
//				cancelNotification();
//			} catch (IOException ioe) { ioe.printStackTrace(); }
//		}
//		return faultConditionValue;
//	}
    
    /**
     * Return the message with the value of variable
     * @return String to notify
     */
    protected String getMessage(String message) {
    	StringBuffer sb = new StringBuffer();
    	StringTokenizer st = new StringTokenizer(message, "$", true);
    	boolean open = false;
    	while(st.hasMoreTokens()) {
    		String token = st.nextToken();
    		if("$".equals(token)) {
    			open = !open;
    		}else if(open) {
    			sb.append(variables.get(mappingName.get(token)));
    		}else {
    			sb.append(token);
    		}
    	}
    	return sb.toString();
    }

    /**
     * Notify to the monitorServlet that the sensing condition is active
     * @throws IOException
     */
    protected void notifyAdmin(String status, String message, String condition, String threshold, double probability, double cons) throws IOException {
    	numberNotification++;
//        params.put("trigger", convertCondition(postTrigger, mappingName, variables)+"=>"+convertCondition(postProbability, mappingName, variables));
        mi.sendNotification(numberNotification, caseID, name, status, message, condition, threshold, probability, cons, System.currentTimeMillis());
    }
    
    /**
     * Notify to the monitorServlet that the last notification is no more active
     * @throws IOException
     */
    protected void cancelNotification() throws IOException {
        mi.cancelNotification(numberNotification, caseID, name);
	}

    /**
     * Return all the variable under check
     * @return
     */
	public HashMap<String, LinkedList<String>> getVariableInterested(){
    	return variablesNames;
    }
    
	/**
	 * Return the list of the Task and Net that are under check for the Resource
	 * @return
	 */
    public LinkedList<String> getSubCaseResource(){
    	return new LinkedList<String>(resourcesNames.keySet());
    }
    
    /**
	 * Return the list of the Task and Net that are under check for the Time
	 * @return
	 */
    public LinkedList<String> getSubCaseTime(){
    	return new LinkedList<String>(timesNames.keySet());
    }
    
    /**
	 * Return the list of the Task and Net that are under check for the Variable
	 * @return
	 */
    public LinkedList<String> getSubCaseVariable(){
    	return new LinkedList<String>(variablesNames.keySet());
    }
    
    /**
	 * Return the list of the Task and Net that are under check for the DistributionSet
	 * @return
	 */
    public LinkedList<String> getDistributions(){
    	return distributionSet;
    }
    
    /**
	 * Return the list of the variable that are create with type Role
	 * @return
	 */
    public LinkedList<String> getRole(){
    	return role;
    }
    
    /**
	 * Return the list of the variable that are create with type Participant
	 * @return
	 */
    public LinkedList<String> getParticipant(){
    	return participant;
    }
        
    /**
	 * Return the list of the LogVariable that are under check
	 * @return
	 */
    public LinkedList<String> getLogVariables(){
    	return logVariables;
    }
    
    /**
	 * Return the list of the LogResource that are under check
	 * @return
	 */
    public LinkedList<String> getLogResources(){
    	return logResources;
    }
    
    /**
	 * Return the list of all the timeVariable that are under Check
	 * @return
	 */
    public LinkedList<String> getTimeInterested(){
    	LinkedList<String> list = new LinkedList<String>();
    	for(String task : timesNames.keySet()) {
    		list.addAll(timesNames.get(task));
    	}
    	return list;
    }   
    
    /**
	 * Return the list of all the resourceVariable that are under Check
	 * @return
	 */
    public LinkedList<String> getResourceInterested(){
    	LinkedList<String> list = new LinkedList<String>();
    	for(String task : resourcesNames.keySet()) {
    		list.addAll(resourcesNames.get(task));
    	}
    	return list;
    }
    
    /**
	 * Return the list of all the resourceVariable that are under Check for the task
	 * @param TaskID or NetLabel
	 * @return
	 */
    public LinkedList<String> getResourceInterested(String task){
    	return resourcesNames.get(task);
    }
    
    /**
     * Execute the pathQuery in the element
     * @param expression the query to execute
     * @param element the element on which execute the query
     * @return the result of the query
     */
    private Object getValueFromXML(String expression, String element){
    	LinkedList<String> list = new LinkedList<String>();
    	Document doc = JDOMUtil.stringToDocument(element);
    	String attribute = null;
    	Element e = doc.getRootElement();
    	expression = "/"+e.getName()+expression;
    	expression = expression.replace('.', '/');
    	if(expression.contains("@")) {
    		attribute = expression.substring(expression.indexOf("@")+1);
    		expression = expression.substring(0, expression.indexOf("/@"));
    	}
    	try {
			String result = SaxonUtil.evaluateQuery(expression, doc);
			Element e1 = JDOMUtil.stringToDocument("<Att>"+result+"</Att>").getRootElement();
			for(Element e2 : (List<Element>) e1.getChildren()) {
				if(attribute != null) {
					String att = e2.getAttributeValue(attribute);
					if(att != null)	list.addLast(att);
				}else {
					if(e2.getChildren().size()>0) {
						StringBuffer sb = new StringBuffer();
						for(Element e3 : (List<Element>) e2.getChildren()) {
							String elem = JDOMUtil.elementToString(e3);
							sb.append(elem);
						}
						list.addLast(sb.toString());
					}else {
						String text = e2.getText();
						if(text.length()>0) list.addLast(text);
					}
				}
			}
		} catch (SaxonApiException sae) {}
		if(list.size()==0) return null;
		if(list.size()==1) return list.getFirst();
		return list;
    }
    
    /**
     * Return the condition changing the variable with their value
     * @param expression the condition to change
     * @param mappingName a map with the mapping from variable name to variable path
     * @param value a map with mapping from variable path to variable value
     * @return the condition changed
     */
    public String convertCondition(String expression, HashMap<String, String> mappingName, HashMap<String, Object> value){
		StringTokenizer stTMP = new StringTokenizer(expression,"(-+*/%^&|!<=>){}[]",true);
		StringBuffer sb = new StringBuffer();
		while(stTMP.hasMoreTokens()) {
			String s = stTMP.nextToken();
			if(mappingName.containsKey(s)) {
				String mapName = mappingName.get(s);
				Object o = value.get(mapName);
				if(o!=null)	sb.append(o.toString());
				else sb.append("null");
			}else {
				sb.append(s.toString());
			}
		}
		return sb.toString();
	}
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof YSensor) {
    		YSensor s = (YSensor) o;
    		return (caseID.equals(s.caseID) && name.equals(s.name));
    	}
    	return false;
    }

	public HashMap<String, Object> getVariables() {
		return variables;
	}

	public HashMap<String, String> getMappingName() {
		return mappingName;
	}

	public LinkedList<String> getResource() {
		return resource;
	}
	
	public String getRiskProbability() {
		return riskProbability;
	}

	public String getRiskThreshold() {
		return riskThreshold;
	}

	public String getFaultCondition() {
		return faultCondition;
	}


}
