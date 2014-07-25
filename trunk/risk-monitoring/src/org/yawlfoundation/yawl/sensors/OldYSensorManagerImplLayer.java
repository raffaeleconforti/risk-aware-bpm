package org.yawlfoundation.yawl.sensors;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import net.sf.saxon.s9api.SaxonApiException;

import org.jdom.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceX.InterfaceX_ServiceSideClient;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.engineInterface.EngineInterface;
import org.yawlfoundation.yawl.sensors.language.MathEvaluator;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;

/**
 * Author: Raffaele Conforti
 * Date: 29-lug-2010
 * Time: 13.32.13
 */

public class YSensorManagerImplLayer{ //extends InterfaceBWebsideController {//implements InterfaceX_Service {

	public Integer cvsize;
	public Integer bandSize;
	public Integer repeatOpt;
	private static String _engineURI;
    private static String _resClientURI;
    private static String _resLogClientURI;
    private static String _workQueueClientURI;
    private static String _ixClientURI;
    private static String _monitorSensorURI;
    private static String _timePredictionURI;
	private static YSensorManagerImplLayer _me = null; 
	private InterfaceX_ServiceSideClient _ixClient;
	private WorkQueueGatewayClient _workQueueClient;
	private final String[] times = new String[]{"Seconds", "Minutes", "Hours", "Days", "Weeks", "Month", "Years", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private final long[] timesMillis = new long[]{1000, 60000, 3600000, 86400000, 604800017, (86400000*30)+37800000, (86400000*365)+21600000 };
	private final Map<String, Semaphore> lockMap = new HashMap<String, Semaphore>();
	private final Map<String, List<YSensor>> tableSensor = new HashMap<String, List<YSensor>>();
	private final Map<String, Map<String, List<YSensor>[]>> tableEventSensorNotification = new HashMap<String, Map<String, List<YSensor>[]>>();
	private final Map<String, YSensorUpdater> tableSensorUpdater = new HashMap<String, YSensorUpdater>();
	private final Map<String, Map<String, Map<String, String>>> tableUploadDistribution = new HashMap<String, Map<String, Map<String,String>>>();
	private final Map<String, Map<String, Object>> tableLogResource = new HashMap<String, Map<String,Object>>();
	private final Map<String, Map<String, Object>> tableLogVariable = new HashMap<String, Map<String,Object>>();
	private final Map<String, Map<String, String>> tableDistribution = new HashMap<String, Map<String,String>>();
	private final Map<String, Boolean> tableLogResourceChanging = new HashMap<String, Boolean>();
	private final Map<String, Boolean> tableLogVariableChanging = new HashMap<String, Boolean>();
	private final Map<String, Boolean> tableDistributionChanging = new HashMap<String, Boolean>();
	private final Set<String> completeCase = new HashSet<String>();
	private String _engineHandle = "";
	private final String _engineUser = "sensorService";
    private final String _enginePassword = "ySensor";
    private final DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private DateFormat originalDateFormat2 = new SimpleDateFormat("MMM:dd, yyyy HH:mm:ss");
//    private StringBuffer message = new StringBuffer("Started");
    private Activity activityLayer;
    private ActivityRole activityRoleLayer;
    private Role roleLayer;
    private SubProcess subProcessLayer;
    private Variable variableLayer;
    private WorkflowDefinition workflowDefinitionLayer;
    private EngineInterface engineInterface;
    private final YExpression yExpression = new YExpression();
    private final MathEvaluator mathEvaluator = new MathEvaluator();
    private final Semaphore exprSemaphore = new Semaphore(1);
    private final Semaphore mathSemaphore = new Semaphore(1);
    
    public static void main(String[] args) throws IOException, SQLException, ParseException, InterruptedException {
    	YSensorManagerImplLayer ysmi = new YSensorManagerImplLayer();
    	ysmi.activityLayer = Activity.getActivity("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Activity");
    	ysmi.activityRoleLayer = ActivityRole.getActivityRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_ActivityRole");
    	ysmi.roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role");
    	ysmi.subProcessLayer = SubProcess.getSubProcess("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_SubProcess");
    	ysmi.variableLayer = Variable.getVariable("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Variable");
    	ysmi.workflowDefinitionLayer = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition");
    	ysmi._workQueueClient = new WorkQueueGatewayClient("http://localhost:8080/resourceService/workqueuegateway");
//    	String sensor = "<sensor name=\"a\"><vars><var name=\"aa\" mapping=\"case(current).Verifica_dati_227.datiEsatti\" type=\"\" /></vars><riskCondition>!aa</riskCondition><riskProbability /><riskThreshold /><faultCondition /><faultProbability>0</faultProbability><faultThreshold>100</faultThreshold></sensor>";
//    	YSensor s = new YSensor("355", new WorkQueueGatewayClient(_workQueueClientURI), sensor, "5_Seconds", "http://localhost:8080/monitorService/ms", "http://localhost:8080/timePredictionService/tps");
//    	LinkedList<String> distribution = new LinkedList<String>();
//    	distribution.add("Prova_3(offerInitiator)");
//    	ysmi.tableDistribution.put("355", new HashMap<String, String>());
//    	ysmi.tableLogVariable.put("355", new HashMap<String, Object>());
//    	LinkedList sl = new LinkedList<YSensor>();
//    	sl.add(s);
//    	ysmi.tableSensor.put("340", sl);    	
//    	ysmi.updateLogVariable(s.getLogVariables(), "340");
//    	System.out.println(ysmi.tableLogVariable);
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Test.b", "340"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Test(isStarted)", "340"));
    	long t = System.nanoTime();
    	
//    	System.out.println(s.getLogVariables());
//    	ysmi.updateLogVariable(s.getLogVariables(),"355");
//    	System.out.println(ysmi.tableLogVariable);
    	
    	/** Task isStarted */
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue Shipment Invoice(isStarted)", "210"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Verifica_dati_227(isStarted)=\"true\").New_Net_1(isStarted)", "355"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).New_Net_1(isStarted)", "355"));
//    	
//    	
//    	/** Task StartTime */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(StartTimeInMillis)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(StartTimeInMillis)", "210"));
//    	
//    	/** Task Variable */
////    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210"));
//    	
//    	/** Task Count */
////    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(Count)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(Count)", "210"));
//    	
//    	/** Task Resource */
////    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(startResource)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(startResource)", "210"));
    	
    	/** Task Distribution */
//    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(offerDistribution)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(offerDistribution)", "210"));
    	
    	/** Task Initiator */
//    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(offerInitiator)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(offerInitiator)", "210"));
    	
    	/** Net isStarted */
//    	ysmi.getLogVariableTaskItem("case(current).Payment(isStarted)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Payment(isStarted)", "210"));
    			
    	/** Net StartTime */
//    	ysmi.getLogVariableTaskItem("case(current).Payment(StartTime)", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Payment(StartTimeInMillis)", "210"));
    	
    	/** Net Variable */
//    	ysmi.getLogVariableTaskItem("case(current).Payment", "210");
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Payment.POApproval", "210"));
    	
    	/** NetVarNetVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").New_Net_1", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment.POApproval", "210"));
    	
    	/** NetVarNetTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").New_Net_1(StartTime)", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment(StartTimeInMillis)", "210"));
    	
    	/** NetVarNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").New_Net_1(isStarted)", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment(isStarted)", "210"));
    	
    	/** NetIsTimeNetVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment.POApproval", "317"));
    	
    	/** NetIsTimeNetTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1(StartTime)", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment(StartTimeInMillis)", "317"));
    	
    	/** NetIsTimeNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1(isStarted)", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment(isStarted)", "317"));
    	
    	/** TaskVarNetVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Payment(isStarted)=\"true\").Payment.", "317"));
    	
    	/** TaskVarNetTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1(StartTime)", "317");
    	System.out.println(ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1(StartTime)", "317"));
    	
    	/** TaskVarNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1(isStarted)", "317");
    	System.out.println();
    	
    	/** TaskIsTimeNetVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1", "317");
    	System.out.println();
    	
    	/** TaskIsTimeNetTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1(StartTime)", "317");
    	System.out.println();
    	
    	/** TaskIsTimeNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1(isStarted)", "317");
    	System.out.println();
    	
    	/** NetVarTaskVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").Prova_3", "317");
    	System.out.println();
    	
    	/** NetVarTaskTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").Prova_3(StartTime)", "317");
    	System.out.println();
    	
    	/** NetVarTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").Prova_3(isStarted)", "317");
    	System.out.println();
    	
    	/** NetIsTimeTaskVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3", "317");
    	System.out.println();
    	
    	/** NetIsTimeTaskTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3(StartTime)", "317");
    	System.out.println();
    	
    	/** NetIsTimeTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3(isStarted)", "317");
    	System.out.println();
    	
    	/** TaskVarTaskVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3", "317");
    	System.out.println();
    	
    	/** TaskVarTaskTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3(StartTime)", "317");
    	System.out.println();
    	
    	/** TaskVarTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3(isStarted)", "317");
    	System.out.println();
    	
    	/** TaskIsTimeTaskVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3", "317");
    	System.out.println();
    	
    	/** TaskIsTimeTaskTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3(StartTime)", "317");
    	System.out.println();
    	
    	/** TaskIsTimeTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3(isStarted)", "317");
    	System.out.println();
    	
    	/** MaximumNumberCycle */
//    	ysmi.getLogVariableTaskItem("case(current).Update_Shipment_Payment_Order_604(isStarted)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Update_Shipment_Payment_Order_604(Count)", "210");
//    	ysmi.getLogVariableTaskItem("case(Update_Shipment_Payment_Order_604(Count)>0 AND Update_Shipment_Payment_Order_604(isStarted)=\"true\").Update_Shipment_Payment_Order_604(CountElements)", "210");
//    	ysmi.getLogVariableTaskItem("case(Update_Shipment_Payment_Order_604(Count)>-1).Update_Shipment_Payment_Order_604(CountElements)", "210");
//    	int i = 0;
//    	for(String l : ll) {
//    		i++;
//    	}
//    	int j = 0;
//    	for(String l : ll1) {
//    		j++;
//    	}
////    	System.out.println(i);
////    	System.out.println(j);
////    	System.out.println((double)j/i);
    	
    	/** 4EyePrinciple */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Payment_Order_602(isCompleted)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Payment_Order_602(completeResource)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(isStarted)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(startResource)", "210");
    	
    	/** GoalType */
//    	ysmi.getLogVariableTaskItem("case(current).Payment(EnablementTime)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Payment(CompleteTime)", "210");
    	
    	/** 4EyePrincipleQueryOld */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(isStarted)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(startResource)", "210");
//    	ysmi.getLogVariableTaskItem("case(Approve_Shipment_Payment_Order_593(completeResource)=\"b\" AND Issue_Shipment_Invoice_594.ShipmentInvoice.Company=b AND (ID)!=[IDCurr]).Approve_Shipment_Payment_Order_593(Count)", "210");
    	
    	/** Approval Fraud */
//    	ysmi.getLogResourceTaskItem("case(current).Approve_Shipment_Payment_Order_593(offerResource)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210");
//    	ysmi.getLogVariableTaskItem("case(Approve_Shipment_Payment_Order_593(completeResource)=a AND Issue_Shipment_Invoice_594.ShipmentInvoice.Company=b AND (ID)!=[IDCurr]).Approve_Shipment_Payment_Order_593(CountElements)", "210"); ;
//    	
//    	LinkedList<String> ll = (LinkedList<String>) ysmi.getLogResourceTaskItem("case(Approve_Shipment_Payment_Order_593(isCompleted)=\"true\" AND Issue_Shipment_Invoice_594.ShipmentInvoice.Company=b AND (ID)!=[IDCurr]).Approve_Shipment_Payment_Order_593(completeResource)", "210");
//    	String ss = (String) ysmi.getLogResourceTaskItem("case(current).Approve_Shipment_Payment_Order_593(offerDistribution)", "210");
//    	
//    	for(String var : ll) {
//			Element res = JDOMUtil.stringToElement(var);
//			res = res.getChild("start");
//			LinkedList l = new LinkedList();
//			for(Element child : (List<Element>) res.getChildren()) {
//				String id = child.getAttributeValue("id");
//				Element xml = JDOMUtil.stringToElement(ysmi._workQueueClient.getQueuedWorkItems(id, 2, ysmi._workQueueClient.connect("sensorService", "ySensor")));
//				LinkedList<String> list = new LinkedList<String>();
//				for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
//					list.addLast(workItemRecord.getChild("taskid").getValue());
//				}
//				l.addLast(list.size());
//			}
//			if(l.size()>1) {
//					int min = (Integer) l.get(0);
//					for(int i = 1; i<l.size(); i++) {
//						min = Math.min(min, (Integer)l.get(i));
//					}
////					System.out.println(min);
//			}else {
////					System.out.println(((Integer)l.get(0)).intValue());
//				
//			}
//		}
//    	
//    	for(String var : ll) {
//			var = JDOMUtil.elementToString(JDOMUtil.stringToElement(var).getChild("start"));
//			Element res = JDOMUtil.stringToElement(ss);
//			LinkedList l = new LinkedList();
//			for(Element child : (List<Element>) res.getChildren()) {
//				String id = child.getAttributeValue("id");
//				Element xml = JDOMUtil.stringToElement(ysmi._workQueueClient.getQueuedWorkItems(id, 2, ysmi._workQueueClient.connect("sensorService", "ySensor")));
//				LinkedList<String> list = new LinkedList<String>();
//				for(Element workItemRecord: (List<Element>) xml.getChildren("workItemRecord")) {
//					list.addLast(workItemRecord.getChild("taskid").getValue());
//				}
//				boolean check = false;
//				Element termRes = JDOMUtil.stringToElement(var);
//				for(Element childRes : (List<Element>) termRes.getChildren()) {
//					String c1 = JDOMUtil.elementToString(child);
//					String c2 = JDOMUtil.elementToString(childRes);
//					if(c1.equals(c2)) {
//						check = true;
//						break;
//					}
//				}
//				if(!check) l.addLast(list.size());
//			}
//			if(l.size()==0) {
////				System.out.println("null");
//			}else if(l.size()>1) {
//					int min = (Integer) l.get(0);
//					for(int i = 1; i<l.size(); i++) {
//						min = Math.min(min, (Integer)l.get(i));
//					}
////					System.out.println(min);
//			}else {
////					System.out.println(((Integer)l.get(0)).intValue());
//				
//			}
//    	}
		
    	/** Fraud */
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(isStarted)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Approve_Shipment_Payment_Order_593(StartTime)", "210");
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210");
//    	ysmi.getLogVariableTaskItem("case(Issue_Shipment_Invoice_594.ShipmentInvoice.Company=b AND Approve_Shipment_Payment_Order_593(Count)>0 AND Approve_Shipment_Payment_Order_593(CompleteTimeInMillis)>=(d-(5*24*60*60*1000))).Approve_Shipment_Payment_Order_593(CountElements)", "210");
//    	ysmi.getLogVariableTaskItem("case(Approve_Shipment_Payment_Order_593(Count)>0 AND (ID)!=[IDCurr]).Approve_Shipment_Payment_Order_593(Fraud)", "210");
    	
    	/** Specification */
//    	ysmi.workflowDefinitionLayer.getOpenXESLog("210", ysmi.workflowDefinitionLayer.getURI("210"), ysmi.workflowDefinitionLayer.getVersion("210"));
    	
//    	ysmi.getLogVariableTaskItem("case(Approve_Shipment_Payment_Order_593(Count)>0).Approve_Shipment_Payment_Order_593(CountElements)", "210");
//    	ysmi.getLogVariableTaskItem("case(Issue_Shipment_Invoice_594.ShipmentInvoice.Company=b).Issue_Shipment_Invoice_594(CountElements)", "210");
//    	System.out.println(ysmi.getLogVariableTaskItem("case((ID)!=[IDCurr]).Issue_Shipment_Invoice_594(StartTime)", "210"));
//    	ysmi.getLogVariableTaskItem("case((ID)!=[IDCurr]).Issue_Shipment_Invoice_594", "210");
    	System.out.println((System.nanoTime()-t)/1000000);
    	
		
    }
    
    private String converParam(String WorkDefID, String tmp) {
    	if('"' != tmp.charAt(0)) {
			try {
				new Double(tmp);
			}catch(NumberFormatException nfe) {
				if(tmp.equals(YSensorUtilities.idCurr)){
					tmp = WorkDefID;
				}else {
					Object[] map = isVariable(tmp, WorkDefID);
					if(map != null) {
						tmp = (String) getVariable(map);
						if(tmp!=null && tmp.contains(YExpression.GREATER) && tmp.contains(YExpression.MINOR)) {
							String start = tmp.substring(0, tmp.indexOf(YExpression.GREATER)+1);
							String end = tmp.substring(tmp.length()-start.length()-1);
							if(start.substring(1).equals(end.substring(2))) {
								tmp = tmp.substring(start.length(), tmp.length()-end.length());
							}
						}
					}else {
						mathEvaluator.setExpression(tmp);
						try {
							tmp = ""+mathEvaluator.getValue();
						} catch (Exception e) {
							tmp = "null";
						}
					}
				}
			}
		}else {
			tmp = tmp.substring(1, tmp.length()-1);
		}
    	return tmp;
    }
    
    
    public Object getLogVariableTaskItem(String logTaskItem, String WorkDefID) {//, boolean internal) {
		Integer diffCaseID = null;
		String param = null;
		Boolean current = true;
		Boolean expression = false;
		
		if(logTaskItem.contains(YSensorUtilities.caseCurrent) || logTaskItem.contains(YSensorUtilities.caseCurrentM)){
			if(logTaskItem.contains(YSensorUtilities.currentM)){
				diffCaseID = new Integer(logTaskItem.substring(logTaskItem.indexOf(YSensorUtilities.currentM)+8, logTaskItem.indexOf(YExpression.PARTC)));
			}else {
				diffCaseID = new Integer(0);
			}
		}else {
			param = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTA), logTaskItem.indexOf(YExpression.PARTC)+1);
			String tmp = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTC)+1);
			while(tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.SINGLEEQUAL) || ')' == tmp.charAt(0)) {
				param += tmp.substring(0, tmp.indexOf(YExpression.PARTC)+1);
				tmp = tmp.substring(tmp.indexOf(YExpression.PARTC)+1);
			}
			try {
				diffCaseID = new Integer(param);
			}catch(NumberFormatException nfe) {
				expression = true;
			}
			current = false;
		}
		
		
		if(logTaskItem.contains(YSensorUtilities.fraudProb)){
			return fraudProbabilityFunc(logTaskItem, param, WorkDefID);
		}else if(!expression) { //Specific Case
			
			WorkDefID = getWorkDefID(WorkDefID, current, diffCaseID);
			if(WorkDefID == null) return null;
			
			String taskName = null;
			if(')' == logTaskItem.charAt(logTaskItem.length()-1)) {
				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
			}else {
				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1);//, logTaskItem.lastIndexOf(YExpression.DOT)); //FIXME added for Fortino
//				if(taskName.contains(YExpression.DOT)) { //FIXME added for Fortino
//					taskName = taskName.substring(0, taskName.indexOf(YExpression.DOT)); //FIXME added for Fortino
//				}
			}
			
			String oldTaskName = taskName; //FIXME added for Fortino
			
			taskName = fixName(WorkDefID, taskName); //FIXME added for Fortino

			taskName = taskName.replace('_', ' '); //FIXME added for Fortino
			
			if(logTaskItem.contains(workflowDefinitionLayer.getURI(WorkDefID))) {
				String Name = workflowDefinitionLayer.getNames(WorkDefID, true, 0, 0, 0, false, null, null).firstElement();
				if(logTaskItem.endsWith(YSensorUtilities.startTimeInMillis)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Start, true, null, false);
	    			if(time!=null){
		    			return ""+(new Long(time.firstElement()));
	    			}else return null;
	    		} else if(logTaskItem.endsWith(YSensorUtilities.isStarted)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Start, true, null, false);
	    			if(time!=null){ return "true";
	    			}else return "false";
	    		} else if(logTaskItem.endsWith(YSensorUtilities.completeTimeInMillis)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Complete, true, null, false);
	    			if(time!=null){
		    			return ""+(new Long(time.firstElement()));
	    			}else return null;
	    		} else if(logTaskItem.endsWith(YSensorUtilities.isCompleted)) {
	    			Vector<String> time = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Complete, true, null, false);
	    			if(time!=null){ return "true";
	    			}else return "false";
	    		} else if(logTaskItem.endsWith(YSensorUtilities.passTimeInMillis)) {
	    			Vector<String> time1 = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Complete, true, null, false);
	    			Vector<String> time2 = subProcessLayer.getTimes(null, false, Name, true, WorkDefID, true, SubProcess.Start, true, null, false);
	    			if(time1 != null && time2 != null){
		    			return ""+((new Long(time1.firstElement())) - (new Long(time2.firstElement())));
	    			}else return null;
	    		}
	    	}else if(logTaskItem.endsWith(YSensorUtilities.offerResource) || logTaskItem.endsWith(YSensorUtilities.allocateResource) || logTaskItem.endsWith(YSensorUtilities.startResource) || logTaskItem.endsWith(YSensorUtilities.completeResource)) {
				LinkedList<String> WorkDefIDs = new LinkedList<String>();
				WorkDefIDs.add(WorkDefID);
				
				Vector<Vector<String>> taskIDs = activityLayer.getIDs(taskName, true, WorkDefIDs, true, 0, false, null, false);
				if(taskIDs!=null) {
					Vector<Vector<String>> resource = activityRoleLayer.getRows(taskIDs.firstElement().get(2), true, null, false, 0, 0, 0, false);
					int type = YSensorUtilities.getResourceType(logTaskItem);
					return YSensorUtilities.createResource(roleLayer, taskName, resource, type);
				}else {
					return null;
				}
			}else if(logTaskItem.endsWith(YSensorUtilities.offerDistribution)) {
				return activityLayer.getDistribution(oldTaskName, true, WorkDefID, true, Activity.OfferDis);
			}else if(logTaskItem.endsWith(YSensorUtilities.allocateDistribution)) {
				return activityLayer.getDistribution(taskName, true, WorkDefID, true, Activity.AllocateDis);
			}else if(logTaskItem.endsWith(YSensorUtilities.startDistribution)) {
				return activityLayer.getDistribution(taskName, true, WorkDefID, true, Activity.StartDis);
			}else if(logTaskItem.endsWith(YSensorUtilities.offerInitiator)) {
				return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.OfferDis);
			}else if(logTaskItem.endsWith(YSensorUtilities.allocateInitiator)) {
				return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.AllocateDis);
			}else if(logTaskItem.endsWith(YSensorUtilities.startInitiator)) {
				return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.StartDis);
			}

			boolean netVariable = false;
			
			if(subProcessLayer.isSubProcess(WorkDefID, taskName)) {
				netVariable = true;
			}
//			netVariable = true;
			
			if(!netVariable) { //Analyse Task
								
				if(')' != logTaskItem.charAt(logTaskItem.length()-1)) { //Activity Variable

					String tmpTaskName = oldTaskName.contains(YExpression.DOT)?oldTaskName.substring(0, oldTaskName.indexOf(YExpression.DOT)):oldTaskName;
					return YSensorUtilities.getLogVariableActivityVariable(activityLayer, variableLayer, taskName, tmpTaskName, WorkDefID);
					
				}else { //Activity information (Status, Time...)

					return YSensorUtilities.getLogVariableActivityInformation(activityLayer, logTaskItem, taskName, WorkDefID);
					
				}
			}else { //Analyse Net
				if(')' != logTaskItem.charAt(logTaskItem.length()-1)) { //SubProcess Variable

					return YSensorUtilities.getLogVariableNetVariable(subProcessLayer, variableLayer, taskName, WorkDefID);
					
				}else { //SubProcess Information (Status, Time...)
//					System.out.println("EVVAI");
					return YSensorUtilities.getLogVariableNetInformation(subProcessLayer, logTaskItem, taskName, WorkDefID);

				}
			}
		}else { // Group of cases by query
		
			LinkedList<String> cases = generateCaseID(param, WorkDefID);
			logTaskItem = logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
			
			if(logTaskItem.endsWith(YSensorUtilities.countElements)) {
				return ""+cases.size();
			}
			
			String taskName = null;
			if(')' == logTaskItem.charAt(logTaskItem.length()-1)) {
				taskName = logTaskItem.substring(0, logTaskItem.lastIndexOf(YExpression.PARTA));
			}else {
				taskName = logTaskItem.substring(0, logTaskItem.indexOf(YExpression.DOT));
			}
			
			taskName = fixName(WorkDefID, taskName); //FIXME add for Fortino
						
			LinkedList<String> result = new LinkedList<String>();
			
//			if(!netVariable) {
//				if(')' != logTaskItem.charAt(logTaskItem.length()-1)) {
//					return getGroupActivityVariable(taskName, oldTaskName, WorkDefID, cases, result);
//				}else 
					if(logTaskItem.endsWith(YSensorUtilities.count)) {
					Vector<Vector<String>> count = activityLayer.getCounts(taskName, true, cases, true);
					for(Vector<String> el : count) {
						result.add(el.lastElement());
					}
					return result;
				}else if(logTaskItem.endsWith(YSensorUtilities.time)) {
					int typeTime = YSensorUtilities.detectActivityType(logTaskItem);
					
					Vector<Vector<String>> time = activityLayer.getTimes(null, false, taskName, true, cases, true, typeTime, true, null, false);
					for(Vector<String> el : time) {
						result.add(originalDateFormat.format(new Date(new Long(el.lastElement()))));
					}
					return result;
				}else if(logTaskItem.endsWith(YSensorUtilities.offerResource) || logTaskItem.endsWith(YSensorUtilities.allocateResource) || logTaskItem.endsWith(YSensorUtilities.startResource) || logTaskItem.endsWith(YSensorUtilities.completeResource)) {
					
					Vector<Vector<String>> taskIDs = activityLayer.getIDs(taskName, true, cases, true, 0, false, null, false);
					if(taskIDs!=null) {
						for(Vector<String> taskID : taskIDs) {
							if(taskID!=null) {
								Vector<Vector<String>> resource = activityRoleLayer.getRows(taskID.get(2), true, null, false, 0, 0, 0, false);
								int type = YSensorUtilities.getResourceType(logTaskItem);
								result.add(YSensorUtilities.createResource(roleLayer, taskName, resource, type));
							}else {
								result.add("null");
							}
						}
					}
					return new LinkedList<String>(result);
				}else if(!logTaskItem.endsWith(YSensorUtilities.distribution) && !logTaskItem.endsWith(YSensorUtilities.initiator)) {
	//				System.out.println(param);
	//				String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
					String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem;
					for(String caseID : cases) {
						result.add((String)getLogVariableTaskItem(query, caseID));
					}
					return new LinkedList<String>(result);				
				}else {
					String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem;
					for(String caseID : cases) {
						result.add((String)getLogVariableTaskItem(query, caseID));
					}
					return result;
				}
//			}else {
//				System.out.println("ok");
//				return null;
//			}
			
    	}
	}

	private String getWorkDefID(String WorkDefID, boolean current, Integer diffCaseID) {
    	Vector<String> listWorkDef = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, workflowDefinitionLayer.getURI(WorkDefID), workflowDefinitionLayer.getVersion(WorkDefID));
		if(current) {
			int i = 0;
			boolean found = false;
			while(i<listWorkDef.size() && !found) {
				if(listWorkDef.get(i).equals(WorkDefID)) {
					found = true;
				}else {
					i++;
				}
			}
			if(listWorkDef.size()>0) {
				if(found) {
					if(i>=diffCaseID) WorkDefID = listWorkDef.get(i-diffCaseID);
					else return null;
				}else {
					if(listWorkDef.size()>=diffCaseID) WorkDefID = listWorkDef.get(listWorkDef.size()-diffCaseID);
					else return null;
				}
			}else {
				return null;
			}
		}else {
			if(listWorkDef.size()>=diffCaseID) WorkDefID = listWorkDef.get(diffCaseID-1);
			else return null;
		}
		return WorkDefID;
	}

    private Object getGroupActivityVariable(String taskName, String oldTaskName, String WorkDefID, LinkedList<String> cases, LinkedList<String> result) {
		Vector<Vector<String>> variablesIDs = activityLayer.getVariablesIDs(null, false, taskName, true, cases, true, 0, false);
    	for(Vector<String> variablesID : variablesIDs) {
    		HashMap<String, String> variable = new HashMap<String, String>();
    		WorkDefID = variablesID.firstElement();
	    	for(String variableID : variablesID) {
	    		if(!variableID.equals("-1") && !variableID.equals(WorkDefID)) {
	    			Vector<Vector<String>> matrixVar = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, variableID, true, true);
			    	if(matrixVar!=null) {	
		    			for(Vector<String> vectorVar : matrixVar) {
			    			String variableName = vectorVar.get(1);
			    			String value = vectorVar.get(2);
			    			if(!value.startsWith(YExpression.MINOR+variableName+YExpression.GREATER)) {
								value = YExpression.MINOR+variableName+YExpression.GREATER+value+"</"+variableName+YExpression.GREATER;
								variable.put(variableName, value);
							}else {
								variable.put(variableName, value);
							}
			    		}
			    	}
	    		}
	    	}
	    	
	    	String value = YSensorUtilities.generateVariableXML(oldTaskName, variable);
	    	
	    	result.add( (value != null) ? value : YSensorUtilities.nullString);

		}
    	return result;
	}
    
    private void getGenerateCaseIDActivityVariable(String activity, int oper, String preVar, String postVar, String suffix, HashSet<String> casesPartial, LinkedList<String> workDefID) {
    	Vector<Vector<String>> activityIDs = activityLayer.getRows(null, false, activity, true, workDefID, true, 0, 0, 0, false, null, false);
    	if(activityIDs != null) {
			for(Vector<String> activityID : activityIDs) {
				if(!activityID.get(5).equals("")) {
					Vector<String> values = variableLayer.getValues(null, false, preVar, true, 0, 0, 0, null, false, activityID.get(5), true, true);
					if(values!=null) {
	
						String value = YSensorUtilities.getVariableValue(values.firstElement(), suffix);
	
						YSensorUtilities.populateCaseIDActivityVariable(value, postVar, suffix, oper, activityID.firstElement(), casesPartial);
					}
				}
			}
		}
	}

	private void getGenerateCaseIDActivityInfo(String activity, int typeTime, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefID, Vector<String> workDefIDs, HashSet<String> casesPartial) {
    	if(preVar.equals(YSensorUtilities.count)) {
			Vector<Vector<String>> count = activityLayer.getCounts(activity, true, null, false);
			try {
				if(postVar != null && !postVar.equals("null")) {
					double b = new Double(postVar);
					for(Vector<String> activityID : count) {
						if(workDefIDs.contains(activityID.firstElement())) {
							try {
								double a = new Double(activityID.lastElement());
								
								YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.firstElement(), suffix);
								
							}catch (NumberFormatException nfe) {
								if(!postVar.equals("null")) nfe.printStackTrace();
							}
						}
					}
				}
			}catch (NumberFormatException nfe) {
				if(!postVar.equals("null")) nfe.printStackTrace();
			}
		}else if(preVar.contains("Time")) {
			if(preVar.endsWith(YSensorUtilities.timeInMillis)) {
				try {
					postVar = ""+(long) Double.parseDouble(postVar);
				} catch (NumberFormatException nfe) {
					if(!postVar.contains("null")) nfe.printStackTrace();
				}
			}
			try {
				long longTime = (long) Double.parseDouble(postVar);
				Vector<Vector<String>> time = activityLayer.getRows(null, false, activity, true, workDefID, true, longTime, oper, typeTime, true, null, false);
				for(Vector<String> activityID : time) {
					casesPartial.add(activityID.firstElement());
				}
			}catch (NumberFormatException nfe) {
				if(!postVar.contains("null")) nfe.printStackTrace();
			}
		}else if(preVar.endsWith(YSensorUtilities.resource)) {
			if(preVar.endsWith(YSensorUtilities.offerResource)) {

				generateResourceInfo(activity, workDefID, Activity.Enabled, YSensorUtilities.offerResource, "offer", postVar, oper, casesPartial, suffix);
				
			}else if(preVar.endsWith(YSensorUtilities.allocateResource)) {

				generateResourceInfo(activity, workDefID, Activity.Enabled, YSensorUtilities.allocateResource, "allocate", postVar, oper, casesPartial, suffix);
				
			}else if(preVar.endsWith(YSensorUtilities.startResource)) {

				generateResourceInfo(activity, workDefID, Activity.Executing, YSensorUtilities.startResource, "start", postVar, oper, casesPartial, suffix);
				
			} else if(preVar.endsWith(YSensorUtilities.completeResource)) {
				
				generateResourceInfo(activity, workDefID, Activity.Completed, YSensorUtilities.completeResource, "complete", postVar, oper, casesPartial, suffix);
				
			} 						
		}else {
			Boolean a = null;
			Boolean b = null;
			if(("true").equalsIgnoreCase(postVar)) {
				b = true;
			}else if(("false").equalsIgnoreCase(postVar)) {
				b = false;
			}
			if(oper!=2 && oper!=5) b = null; 
			if(suffix!=null) b = null;
			if(b!=null) {
				Vector<Vector<String>> time = activityLayer.getRows(null, false, activity, true, null, false, 0, 0, typeTime, true, null, false);
				for(Vector<String> activityID : time) {
					if(time.size()!=0) {
						a = true;
					}else {
						a = false;
					}
					
					YSensorUtilities.generateCaseCompareBoolean(a, b, oper, casesPartial, activityID.firstElement());
					
				}
			}
		}
	}
	
	private void generateResourceInfo(String activity, LinkedList<String> workDefID, int activityType, String action, String eventType, String postVar, int oper, HashSet<String> casesPartial, String suffix) {
		Vector<Vector<String>> taskids = activityLayer.getRows(null, false, activity, true, workDefID, true, 0, 0, activityType, true, null, false);
		if(taskids != null) {
			for(Vector<String> taskid : taskids) {
				String a = (String) getLogVariableTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+activity+action, taskid.firstElement());
				
				Element resEle = JDOMUtil.stringToElement(a);
				
				StringBuffer sb = new StringBuffer();
				for(Element el : (List<Element>) resEle.getChildren()) {
					sb.append(JDOMUtil.elementToString(el));
				}
				a = sb.toString();
				String b = postVar;
				
				
				YSensorUtilities.generateCaseCompareResource(a, b, oper, casesPartial, taskid.firstElement(), suffix);
			}
		}
	}
	
	private void getGenerateIDNetVariable(String activity, int oper, String preVar, String postVar, String suffix, Vector<String> workDefIDs, HashSet<String> casesPartial) {
    	Vector<Vector<String>> activityIDs = subProcessLayer.getRows(null, false, activity, true, null, false, 0, 0, 0, false, null, false);
		for(Vector<String> activityID : activityIDs) {
			if(workDefIDs.contains(activityID.firstElement())) {
				Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, activityID.get(5), true, false);
				HashMap<String, String> var = new HashMap<String, String>();
				if(variable!=null) {
					if(variable.size()>0) {
						for(Vector<String> vec : variable) {
							var.put(vec.get(1), vec.get(2));
						}
					}
					String value = var.get(preVar);
					try {
						double a = new Double(value);
						double b = new Double(postVar);
						
						YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, activityID.firstElement(), suffix);
						
					} catch (NumberFormatException nfe) {
						try {
							Date a = originalDateFormat.parse(value);
							Date b = originalDateFormat.parse(postVar);
							
							YSensorUtilities.generateCaseCompareDate(a, b, oper, casesPartial, activityID.firstElement(), suffix);
							
						} catch (ParseException e) {
							String a = value;
							String b = postVar;
							if(suffix!=null) {
								Object o = YSensorUtilities.getValueFromXML(YExpression.DOT+suffix, value);
								
								a = YSensorUtilities.checkIfString(o);
								
							}else {
								Object o = YSensorUtilities.getValueFromXML("", value);
								
								a = YSensorUtilities.checkIfString(o);
							}
							
							YSensorUtilities.generateCaseCompareString(a, b, oper, casesPartial, activityID.firstElement(), suffix);

						}
					}
				}
			}
		}
	}
	
	private void getGenerateCaseIDNetInfo(String activity, int typeTime, int oper, String preVar, String postVar, String suffix, Vector<String> workDefIDs, HashSet<String> casesPartial) {
    	if(preVar.endsWith(YSensorUtilities.time)) {
			try {
				Vector<Vector<String>> time = subProcessLayer.getRows(null, false, activity, true, null, false, new Long(postVar), oper, typeTime, true, null, false);
				for(Vector<String> activityID : time) {
					if(workDefIDs.contains(activityID.firstElement())) {
						casesPartial.add(activityID.firstElement());
					}
				}
			}catch (NumberFormatException nfe) {
				if(!postVar.equals("null")) nfe.printStackTrace();
			}
		}else {
			Boolean a = null;
			Boolean b = null;
			if(postVar.equalsIgnoreCase("true")) {
				b = true;
			}else if(postVar.equalsIgnoreCase("false")) {
				b = false;
			}
			if(oper!=2 && oper!=5) b = null; 
			if(suffix!=null) b = null;
			if(b!=null) {
				Vector<Vector<String>> time = subProcessLayer.getRows(null, false, activity, true, null, false, 0, 0, typeTime, true, null, false);
				for(Vector<String> activityID : time) {
					if(workDefIDs.contains(activityID.firstElement())) {
						if(time.size()!=0) {
							a = true;
						}else {
							a = false;
						}
						
						YSensorUtilities.generateCaseCompareBoolean(a, b, oper, casesPartial, activityID.firstElement());
						
					}
				}
			}
		}
	}

	private Object fraudProbabilityFunc(String logTaskItem, String param, String WorkDefID) {
    	String tmp = logTaskItem.substring(logTaskItem.indexOf(YSensorUtilities.fraudProb)+23);
		String paramCurrCount = tmp.substring(0, tmp.indexOf(","));
		tmp = tmp.substring(tmp.indexOf(", ")+2);
		String paramMaxNumberExec = tmp.substring(0, tmp.indexOf(","));
		tmp = tmp.substring(tmp.indexOf(", ")+2);
		String paramGroupingElement = tmp.substring(0, tmp.indexOf(","));
		tmp = tmp.substring(tmp.indexOf(", ")+2);
		String paramWindowElement = tmp.substring(0, tmp.indexOf(","));
		tmp = tmp.substring(tmp.indexOf(", ")+2);
		String paramWindow = tmp.substring(0, tmp.length()-1);
		int window = new Integer(converParam(WorkDefID, paramWindow)); 
		int limit = new Integer(converParam(WorkDefID, paramMaxNumberExec));
		int currentCount = new Integer(converParam(WorkDefID, paramCurrCount));
		LinkedList<String> cases = generateCaseID(param, WorkDefID);
		HashMap<String, String> time = new HashMap<String, String>();
		HashMap<String, HashSet<String>> customers = new HashMap<String, HashSet<String>>();
		String groupingElement = converParam(WorkDefID, paramGroupingElement);
		String pre = groupingElement.substring(0, groupingElement.indexOf(YExpression.DOT)+1);
		groupingElement = groupingElement.substring(groupingElement.indexOf(YExpression.DOT)+1);
		String mid = groupingElement.substring(0, groupingElement.indexOf(YExpression.DOT)+1);
		groupingElement = groupingElement.substring(groupingElement.indexOf(YExpression.DOT)+1);
		String post = null;
		if(groupingElement.contains(YExpression.DOT)) {
			post = groupingElement.substring(0, groupingElement.indexOf(YExpression.DOT));
		}else {
			post = groupingElement;
		}
		String query = pre+mid+post;
		String query2 = converParam(WorkDefID, paramWindowElement);
		for(String caseID : cases) {
			String s = (String)getLogVariableTaskItem(query, caseID);
			s = (String) YSensorUtilities.getValueFromXML(YExpression.DOT+groupingElement, s);
			
			HashSet<String> set = null;
			if( (set = customers.get(s)) != null) {
				set.add(caseID);
			}else {
				set = new HashSet<String>();
				set.add(caseID);
				customers.put(s, set);
			}
			time.put(caseID, (String)getLogVariableTaskItem(query2, caseID));
		}
		int sameExec = 0;
		int limExec = 0;
		int last = -1;
		for(String customer : customers.keySet()) {
			HashSet<String> caseIDs = customers.get(customer);
			long[] times = new long[caseIDs.size()];
			int j = 0;
			for(String caseID : caseIDs) {
				try {
					times[j] = originalDateFormat.parse(time.get(caseID)).getTime();
					j++;
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			Arrays.sort(times);
			int count = 0;
			for(int i = 0; i<times.length; i++) {
				long t = times[i];
				if(count<=1) {
					count = 0;
				}else {
					count--;
				}
				for(j = i+count; j<times.length; j++) {
					if(times[j]<=(t+window)) {
						count++;
					}else {
						if(i == 0) {
							last = j;
							int tmpCount = count;
							while(last > 0 || (last == 0 && count == 1)) {
								tmpCount--;
								if(tmpCount>currentCount) {
									sameExec++;
								}
								if(tmpCount>limit) {
									limExec++;
								}
								last--;
							}
						}
						break;
					}
				}
				if(count>currentCount) {
					sameExec++;
				}
				if(count>limit) {
					limExec++;
				}
			}
		}
		return ""+(double)limExec/sameExec;
	}

	private String fixName(String WorkDefID, String taskName) {
		if(taskName.contains(YExpression.DOT)) { //FIXME added for Fortino
			taskName = taskName.substring(0, taskName.indexOf(YExpression.DOT)); //FIXME added for Fortino
		} //FIXME added for Fortino
		if(subProcessLayer.isSubProcess(WorkDefID, taskName)) {
			return taskName;
		}else {
	    	String number = taskName.substring(taskName.lastIndexOf("_")+1);
			try {
				new Integer(number);
				taskName = taskName.substring(0, taskName.lastIndexOf("_"));
			}catch (NumberFormatException nfe) {}
			return taskName;
		}
	}

	/**
     * Verify if the varName is the name of a sensor's variable 
     * @param varName
     * @param WorkDefID
     * @return
     */
    public Object[] isVariable(String varName, String WorkDefID) { /** TODO */
    	Object[] res = new Object[2];
    	for(YSensor s : tableSensor.get(WorkDefID)) {
    		
    		String tmp = s.isVariable(varName);
    		
    		if(tmp!=null) {
    			res[0] = tmp;
    			res[1] = s;
    			return res;
    		}
    	}
    	return null;
    }
    
    public Object getVariable(Object[] mapping) {
//    	String query = (String)mapping[0];
//    	if(query.endsWith(YSensorUtilities.resource) || query.endsWith(YSensorUtilities.initiator) || query.endsWith(YSensorUtilities.distribution)) {
//    		return this.getLogResourceTaskItem((String)mapping[0], ((YSensor)mapping[1]).getCaseID());
//    	}else {
//    		return this.getLogVariableTaskItem((String)mapping[0], ((YSensor)mapping[1]).getCaseID());
//    	}
    	
    	return ((YSensor)mapping[1]).getVariable((String)mapping[0]);
    }
    
    /**
     * Discover all the case ID that satisfy the condition described in expression
     * @param expression
     * @return
     */
    private LinkedList<String> generateCaseID(String param,  String WorkDefID) {
    	LinkedList<String> activitySubProcess = new LinkedList<String>();
		LinkedList<String> action = new LinkedList<String>();
		LinkedList<String> operator = new LinkedList<String>();
		LinkedList<HashSet<String>> cases = new LinkedList<HashSet<String>>();
		
		param = YSensorUtilities.buildParam(param);
		
		YSensorUtilities.populateActivitySubProcessActionAndOperator(param, activitySubProcess, action, operator);
		
		for(int i=0; i<activitySubProcess.size(); i++) {
			boolean netVariable = false;
			boolean caseInfo = false;
			HashSet<String> casesPartial = new HashSet<String>();
			String activity = activitySubProcess.get(i);
			activity = fixName(WorkDefID, activity);
			String variableFull = action.get(i);
			
			if(activity.equals("") && variableFull.startsWith(YSensorUtilities.id)){
				caseInfo = true;
			}else {
				if(subProcessLayer.isSubProcess(WorkDefID, activity)) {
					netVariable = true;
				}
			}
			
			String[] preVarPostVarSuffix = YSensorUtilities.populatePreVarPostVarSuffix(variableFull);
			
			int oper = new Integer(preVarPostVarSuffix[0]);
			String preVar = preVarPostVarSuffix[1];
			String postVar = preVarPostVarSuffix[2];
			String suffix = preVarPostVarSuffix[3];

			postVar = contextualixePostVar(postVar, WorkDefID, variableFull);
			
			String Version = workflowDefinitionLayer.getVersion(WorkDefID);
			String URI = workflowDefinitionLayer.getURI(WorkDefID);
			Vector<String> workDefIDs = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, URI, Version);
			
//			CacheKey cacheKey = new CacheKey(preVar, postVar, suffix);
////			
//			if(cacheMap.containsKey(cacheKey)) {
//				casesPartial.addAll(cacheMap.get(cacheKey));
//			}else 
			{
				if(caseInfo) {
					for(String id : workDefIDs) {
						double a = new Double(id);
						double b = new Double(postVar);
						
						YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, id, suffix);
					}
				}else {
					LinkedList<String> workDefID = new LinkedList<String>();
					workDefID.addAll(workDefIDs);
	
					if(!netVariable) { // Analysing Task
						if(!preVar.contains(YExpression.PARTA)) { // Task Variable
							
							getGenerateCaseIDActivityVariable(activity, oper, preVar, postVar, suffix, casesPartial, workDefID);
							
						} else { // Task Info
							
							int typeTime = YSensorUtilities.detectActivityType(preVar);	
	
							getGenerateCaseIDActivityInfo(activity, typeTime, oper, preVar, postVar, suffix, workDefID, workDefIDs, casesPartial);
						}
					} else { //Analysing Net
						if(!preVar.contains(YExpression.PARTA)) { // Net Variable 
							
							getGenerateIDNetVariable(activity, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
							
						} else { // Net Info
							
							int typeTime = 0;
							
							if(preVar.equals(YSensorUtilities.completeTime)) typeTime = SubProcess.Complete;
							else if(preVar.equals(YSensorUtilities.startTime)) typeTime = SubProcess.Start;
							else if(preVar.equals(YSensorUtilities.isCompleted)) typeTime = SubProcess.Complete;
							else if(preVar.equals(YSensorUtilities.isStarted)) typeTime = SubProcess.Start;
							
							getGenerateCaseIDNetInfo(activity, typeTime, oper, preVar, postVar, suffix, workDefIDs, casesPartial);
						}
					}
				}
//				cacheMap.put(cacheKey, casesPartial);
			}
			cases.add(casesPartial);
		}
		
		//Consolidating the cases
		return YSensorUtilities.getConsolidatedCases(cases, operator, WorkDefID, workflowDefinitionLayer);
    }

	private String contextualixePostVar(String postVar, String WorkDefID, String variableFull) {
    	if(postVar.contains(YExpression.MINOR) || postVar.contains(YExpression.GREATER) || postVar.contains(YExpression.SINGLEEQUAL) || postVar.contains(YExpression.NOT) || postVar.contains(YExpression.AND) || postVar.contains(YExpression.OR) || postVar.contains(YExpression.PARTA) || postVar.contains(YExpression.PARTC) || postVar.contains(YExpression.SUM) || postVar.contains(YExpression.SUB) || postVar.contains(YExpression.MULTIPLY) || postVar.contains(YExpression.DIVIDE) || postVar.contains(YExpression.EXP) || postVar.contains(YExpression.MOD)) {
			StringTokenizer st = new StringTokenizer(postVar, "<>=!&|()+*-/^%\"", true);
			StringBuffer sb = new StringBuffer();
			while(st.hasMoreTokens()) {
				String tmp = st.nextToken();
				if(!(tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.SINGLEEQUAL) || tmp.contains(YExpression.NOT) || tmp.contains(YExpression.AND) || tmp.contains(YExpression.OR) || tmp.contains(YExpression.PARTA) || tmp.contains(YExpression.PARTC) || tmp.contains(YExpression.SUM) || tmp.contains(YExpression.SUB) || tmp.contains(YExpression.MULTIPLY) || tmp.contains(YExpression.DIVIDE) || tmp.contains(YExpression.EXP) || tmp.contains(YExpression.MOD))) {
					if('"' != tmp.charAt(0)) {
						try {
							new Double(tmp);
						}catch(NumberFormatException nfe) {
							if(tmp.equals(YSensorUtilities.idCurr)){
								tmp = WorkDefID;
							}else {
								Object[] map = isVariable(tmp, WorkDefID);
								if(map != null) {
									
									tmp = YSensorUtilities.preprocessResorce((String) getVariable(map), variableFull);
									
								}
							}
						}
					}else {
						String tmp1 = st.nextToken();
						StringBuffer sb1 = new StringBuffer();
						while('"' != tmp1.charAt(0)) {
							sb1.append(tmp1);
							tmp1 = st.nextToken();
						}
						tmp = sb1.toString();
					}
				}
				sb.append(tmp);
			}
			postVar = sb.toString();
			if(postVar.contains(YExpression.MINOR) || postVar.contains(YExpression.GREATER) || postVar.contains(YExpression.SINGLEEQUAL) || postVar.contains(YExpression.NOT) || postVar.contains(YExpression.AND) || postVar.contains(YExpression.OR)) {
				try {
					exprSemaphore.acquire();
					postVar = ""+yExpression.booleanEvaluation(postVar, null, null, null);
					exprSemaphore.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				try {
					mathSemaphore.acquire();
					mathEvaluator.setExpression(postVar);
					postVar = ""+mathEvaluator.getValue();
					mathSemaphore.release();
				}catch(Exception npe) {
					if(!postVar.matches(".*[a-zA-Z]+.*")) postVar = "null";
					mathSemaphore.release();
				}
			}
		}else {
			if('"' != postVar.charAt(0)) {
				if(postVar.equals(YSensorUtilities.idCurr)){
					postVar = WorkDefID;
				}else {
					Object[] map = isVariable(postVar, WorkDefID);
					if(map != null) {
						
						postVar = YSensorUtilities.preprocessResorce((String) getVariable(map), variableFull);
						
					}
				}
			}else {
				postVar = postVar.substring(1, postVar.length()-1);
			}
		}
    	return postVar;
	}

    public YSensorManagerImplLayer() {
    	super();
    	_me = this;
    }

    public static YSensorManagerImplLayer getInstance() {
    	if (_me == null) {
    		_me = new YSensorManagerImplLayer();
	    	_me.activityLayer = Activity.getActivity("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Activity");
	    	_me.activityRoleLayer = ActivityRole.getActivityRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_ActivityRole");
	    	_me.roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role");
	    	_me.subProcessLayer = SubProcess.getSubProcess("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_SubProcess");
	    	_me.variableLayer = Variable.getVariable("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Variable");
	    	_me.workflowDefinitionLayer = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition");
	    	_me._workQueueClient = new WorkQueueGatewayClient("http://localhost:8080/resourceService/workqueuegateway");
    	}
        return _me;
    }
    
    public void event(String caseID, String taskName, int type) {
    	System.out.println("Manager "+caseID+" "+taskName+" "+type);
    	List<YSensor> sensors = tableEventSensorNotification.get(caseID).get(taskName)[type];
    	for(YSensor sensor : sensors) {
    		tableSensorUpdater.get(caseID).updateSensor(sensor);
    	}
    }
    	
    /**
     * Use getLogVariableTaskItem(String, YSpecificationID, String) to update the tableLogVariable
     * @param listLogVariablesName LinkedList<String> with the logTaskItem
     * @param specID YSpecificationID associated to the Task or Net
     * @param caseID CaseID of the Process
     */
    
    private void updateLogVariable(LinkedList<String> listLogVariablesName, String WorkDefID) {
 		String subLogCase = null;
 		Object log = null;
 		for(String logVariablesName : listLogVariablesName) {
 			if(logVariablesName.contains(YExpression.PARTC+YExpression.DOT+YSensorUtilities.offerTime)){
 				if(!tableLogVariable.get(WorkDefID).containsKey(logVariablesName)) { 
 					String ca = logVariablesName.substring(0, logVariablesName.indexOf(YExpression.PARTC)+1);
 					log = getLogVariableTaskItem((ca+YExpression.DOT+workflowDefinitionLayer.getURI(WorkDefID)+(YSensorUtilities.startTime)).replace('_', ' '), WorkDefID);
 					if(log != null) {
 						tableLogVariable.get(WorkDefID).put(logVariablesName, log);
 						tableLogVariableChanging.put(WorkDefID, true);
 					}
 				}
 			}else {//if(!tableLogVariable.get(WorkDefID).containsKey(logVariablesName)) { 
 				if(')' != logVariablesName.charAt(logVariablesName.length()-1)) {
 					String newSubLogCase = null;
 					String ca = logVariablesName.substring(0, logVariablesName.indexOf(YExpression.PARTC)+1);
 					newSubLogCase = logVariablesName.substring(logVariablesName.indexOf(YExpression.DOT)+1);
 					String last = newSubLogCase.substring(newSubLogCase.indexOf(YExpression.DOT)+1);
 					newSubLogCase = newSubLogCase.substring(0, newSubLogCase.indexOf(YExpression.DOT));
 					String variab = ca+YExpression.DOT+newSubLogCase;
 					if(subLogCase == null || !subLogCase.equals(newSubLogCase)) {
 						log = getLogVariableTaskItem(variab, WorkDefID);
 						subLogCase = newSubLogCase;
 					}
 					if(log != null) {
 						newSubLogCase = fixName(WorkDefID, newSubLogCase);
 						String expression = (newSubLogCase+YExpression.DOT+last).replace('.', '/').replace(' ','_');
 						try {
 							if(log instanceof String) {
 								String query = SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument((String)log));
 								if(query.contains("<?xml version=")) {
 					    			query = query.substring(query.indexOf(">")+1);
 					    			if(query.isEmpty()) query = null;
 					    		}
 								tableLogVariable.get(WorkDefID).put(logVariablesName, query);
 								tableLogVariableChanging.put(WorkDefID, true);
 							}else {
 								LinkedList<String> logs = (LinkedList<String>) log;
 								for(int i=0; i<logs.size(); i++) {
 									String query = SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument(logs.get(i)));
 									if(query.contains("<?xml version=")) {
 						    			query = query.substring(query.indexOf(">")+1);
 						    			if(query.isEmpty()) query = null;
 						    		}
 									logs.set(i, query);
 								}
 								tableLogVariable.get(WorkDefID).put(logVariablesName, logs);
 								tableLogVariableChanging.put(WorkDefID, true);
 							}
 						} catch (SaxonApiException sae) { sae.printStackTrace();} 
 					}
 				}else {
// 					System.out.println(logVariablesName);
// 					String ca = logVariablesName.substring(0, logVariablesName.indexOf(YExpression.PARTC)+1);
// 					String newSubLogCase = logVariablesName.substring(logVariablesName.indexOf(YExpression.DOT)+1);
// 					String last = newSubLogCase.substring(newSubLogCase.indexOf(YExpression.PARTA));
// 					newSubLogCase = newSubLogCase.substring(0, newSubLogCase.indexOf(YExpression.PARTA));
// 					newSubLogCase = ca+YExpression.DOT+newSubLogCase+last;
// 					newSubLogCase = newSubLogCase.replace('_', ' ');
 					String newSubLogCase = logVariablesName;
 					newSubLogCase = fixName(WorkDefID, newSubLogCase);
 					log = getLogVariableTaskItem(newSubLogCase, WorkDefID);
 					subLogCase = newSubLogCase;
 					if(log != null) {
 						tableLogVariable.get(WorkDefID).put(logVariablesName, log);
 						tableLogVariableChanging.put(WorkDefID, true);
 					}
 				}
 			}
 		}
 	}
     
     /**
      * Use the getLogResourceTaskItem(String, YSpecificationID, String, String) to upload the tableLogResource
      * @param listLogResourcesName List of the logTaskItem
      * @param specID YSpecificationID associated to the Task
      * @param caseID CaseID of the Process
      */
 	
 	private void updateLogResources(LinkedList<String> listLogResourcesName, String WorkDefID) {
 		String subLogCase = null;
 		Object log = null;
 		for(String logResourcesName : listLogResourcesName) {
 			if(!tableLogResource.get(WorkDefID).containsKey(logResourcesName)) { 
 				String preName = logResourcesName;
 				preName = preName.replace('_', ' ');
 				String postName = logResourcesName.substring(logResourcesName.lastIndexOf(YExpression.PARTA), logResourcesName.length());
 				if(subLogCase == null || !subLogCase.equals(preName)) {
 					log = getLogVariableTaskItem(preName, WorkDefID);
 					subLogCase = preName;
 				}
 				if(log != null) {
 					if(log instanceof String) {
 						String res = null;
 						Element resEle = JDOMUtil.stringToElement((String) log);
 						if(postName.equals(YSensorUtilities.offerResource)) {
 							res = JDOMUtil.elementToString(resEle.getChild("offer"));
 						}else if(postName.equals(YSensorUtilities.allocateResource)) {
 							res = JDOMUtil.elementToString(resEle.getChild("allocate"));
 						}else if(postName.equals(YSensorUtilities.startResource)) {
 							res = JDOMUtil.elementToString(resEle.getChild("start"));
 						}else {
 							res = JDOMUtil.elementToString(resEle.getChild("complete"));
 						}
 						tableLogResource.get(WorkDefID).put(logResourcesName, res);
 						tableLogResourceChanging.put(WorkDefID, true);
 					}else {
 						LinkedList<String> logs = (LinkedList<String>) log;
 						for(int i=0; i<logs.size(); i++) {
 							String res = null;
 							Element resEle = JDOMUtil.stringToElement(logs.get(i));
 							if(postName.equals(YSensorUtilities.offerResource)) {
 								res = JDOMUtil.elementToString(resEle.getChild("offer"));
 							}else if(postName.equals(YSensorUtilities.allocateResource)) {
 								res = JDOMUtil.elementToString(resEle.getChild("allocate"));
 							}else if(postName.equals(YSensorUtilities.startResource)) {
 								res = JDOMUtil.elementToString(resEle.getChild("start"));
 							}else {
 								res = JDOMUtil.elementToString(resEle.getChild("complete"));
 							}
 							logs.set(i, res);
 						}
 						tableLogResource.get(WorkDefID).put(logResourcesName, logs);
 						tableLogResourceChanging.put(WorkDefID, true);
 					}
 				}
 			}
 		}
 	}
	
    /**
     * 
     * @param caseID
     * @param subCase
     * @param specID
     * @param newElem
     */
    //TODO
    private void updateDistribution(String caseID, String subCase, YSpecificationID specID, String newElem) {
    	String s = tableUploadDistribution.get(caseID).get(subCase).get(subCase);
    	for(String name : tableUploadDistribution.get(caseID).get(subCase).keySet()) {
    		if(!name.equals(subCase)) {
    			Element globalElem = JDOMUtil.stringToElement(newElem);
				String netLabel = globalElem.getName();
				String expression = (netLabel+YExpression.DOT+name).replace('.', '/');
				try {
					String result = SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument(newElem));
					if(result != null) {
						result = YSensorUtilities.checkIfXML(result);
						tableUploadDistribution.get(caseID).get(subCase).put(name, result);
					}
				} catch (SaxonApiException sae) { sae.printStackTrace();} 
			}
    	}
		String information = s.substring(s.indexOf(YExpression.PARTA));
		String taskID = s.substring(0, s.indexOf(YExpression.PARTA));
		if(information.endsWith(YSensorUtilities.initiator)) {
			if(information.contains("offer")) {
				tableDistribution.get(caseID).put(s, activityLayer.getInitiator(taskID, true, caseID, true, Activity.OfferDis));
				tableDistributionChanging.put(caseID, true);
			}else if(information.contains("allocate")) {
				String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.AllocateDis);
				if(res!=null) {
					tableDistribution.get(caseID).put(s, res);
					tableDistributionChanging.put(caseID, true);
				}
			}else if(information.contains("start")) {
				String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.StartDis);
				if(res!=null) {
					tableDistribution.get(caseID).put(s, res);
					tableDistributionChanging.put(caseID, true);
				}
			}
		}else {
			if(information.contains("offer")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.OfferDis));
				tableDistributionChanging.put(caseID, true);
			}else if(information.contains("allocate")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.AllocateDis));
				tableDistributionChanging.put(caseID, true);
			}else if(information.contains("start")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.Completed));
				tableDistributionChanging.put(caseID, true);
			}
		}
    }
	
	/**
	 * Return the Resorce related to the Task
	 * @param logTaskItem type of resource (case(current).TaskID(offerDistribution))
	 * @param specID YSpecificationID associated to the Task
	 * @param caseID CaseID of the Process
	 * @param taskID TaskID of the Task of interest
	 * @return the String representing the resource
	 */

//	public Object getLogResourceTaskItem(String logTaskItem, String WorkDefID) {
//		Integer diffCaseID = null;
//		String param = null;
//		Boolean current = true;
//		Boolean expression = false;
//		if(logTaskItem.contains(YSensorUtilities.current)){
//			if(logTaskItem.contains(YSensorUtilities.currentM)){
//				diffCaseID = new Integer(logTaskItem.substring(logTaskItem.indexOf(YSensorUtilities.currentM)+8, logTaskItem.indexOf(YExpression.PARTC)));
//			}else {
//				diffCaseID = new Integer(0);
//			}
//		}else {
//			param = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTA), logTaskItem.indexOf(YExpression.PARTC)+1);
//			String tmp = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTC)+1);
//			while(tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.SINGLEEQUAL) || ')' == tmp.charAt(0)) {
//				param = param+tmp.substring(0, tmp.indexOf(YExpression.PARTC)+1);
//				tmp = tmp.substring(tmp.indexOf(YExpression.PARTC)+1);
//			}
//			try {
//				diffCaseID = new Integer(param);
//			}catch(NumberFormatException nfe) {
//				expression = true;
//			}
//			current = false;
//		}
//		
//		if(!expression) {
//			Vector<String> listWorkDef = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, workflowDefinitionLayer.getURI(WorkDefID), workflowDefinitionLayer.getVersion(WorkDefID));
//			if(current) {
//				int i = 0;
//				boolean found = false;
//				while(i<listWorkDef.size() && !found) {
//					if(listWorkDef.get(i).equals(WorkDefID)) {
//						found = true;
//					}else {
//						i++;
//					}
//				}
//				if(listWorkDef.size()>0) {
//					if(found) {
//						if(i>=diffCaseID) WorkDefID = listWorkDef.get(i-diffCaseID);
//						else return null;
//					}else {
//						if(listWorkDef.size()>=diffCaseID) WorkDefID = listWorkDef.get(listWorkDef.size()-diffCaseID);
//						else return null;
//					}
//				}else {
//					return null;
//				}
//			}else {
//				if(listWorkDef.size()>=diffCaseID) WorkDefID = listWorkDef.get(diffCaseID-1);
//				else return null;
//			}
//			String taskName = null;
//			if(')' == logTaskItem.charAt(logTaskItem.length()-1)) {
//				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
//			}else {
//				taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.length());
//			}
//			
//			taskName = fixName(WorkDefID, taskName);
//			
//			if(!logTaskItem.endsWith(YSensorUtilities.distribution) && !logTaskItem.endsWith(YSensorUtilities.initiator)) {
//				LinkedList<String> WorkDefIDs = new LinkedList<String>();
//				WorkDefIDs.add(WorkDefID);
//				Vector<Vector<String>> taskIDs = activityLayer.getIDs(taskName, true, WorkDefIDs, true, 0, false, null, false);
//				if(taskIDs!=null) {
//					Vector<Vector<String>> resource = activityRoleLayer.getRows(taskIDs.firstElement().get(2), true, null, false, 0, 0, 0, false);
//					return YSensorUtilities.createResource(roleLayer, taskName, resource);
//				}else {
//					return null;
//				}
//			}else {
//				if(logTaskItem.endsWith(YSensorUtilities.offerDistribution)) {
//					return activityLayer.getDistribution(taskName, true, WorkDefID, true, Activity.OfferDis);
//				}else if(logTaskItem.endsWith(YSensorUtilities.allocateDistribution)) {
//					return activityLayer.getDistribution(taskName, true, WorkDefID, true, Activity.AllocateDis);
//				}else if(logTaskItem.endsWith(YSensorUtilities.startDistribution)) {
//					return activityLayer.getDistribution(taskName, true, WorkDefID, true, Activity.StartDis);
//				}else if(logTaskItem.endsWith(YSensorUtilities.offerInitiator)) {
//					return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.OfferDis);
//				}else if(logTaskItem.endsWith(YSensorUtilities.allocateInitiator)) {
//					return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.AllocateDis);
//				}else if(logTaskItem.endsWith(YSensorUtilities.startInitiator)) {
//					return activityLayer.getInitiator(taskName, true, WorkDefID, true, Activity.StartDis);
//				}else return null;
//			}
//		}else {
//			LinkedList<String> cases = generateCaseID(param, WorkDefID);
//			HashSet<String> result = new HashSet<String>();
//			if(!logTaskItem.endsWith(YSensorUtilities.distribution) && !logTaskItem.endsWith(YSensorUtilities.initiator)) {
//				String taskName = logTaskItem.substring(logTaskItem.lastIndexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
//
//				taskName = fixName(WorkDefID, taskName);
//				
//				Vector<Vector<String>> taskIDs = activityLayer.getIDs(taskName, true, cases, true, 0, false, null, false);
//				if(taskIDs!=null) {
//					for(Vector<String> taskID : taskIDs) {
//						if(taskID!=null) {
//							Vector<Vector<String>> resource = activityRoleLayer.getRows(taskIDs.firstElement().get(2), true, null, false, 0, 0, 0, false);
//							result.add(YSensorUtilities.createResource(roleLayer, taskName, resource));
//						}else {
//							result.add("null");
//						}
//					}
//				}
//				return new LinkedList<String>(result);
//			}
//			
//			String query = YSensorUtilities.caseCurrent+YExpression.DOT+logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
//			for(String caseID : cases) {
//				result.add((String)getLogResourceTaskItem(query, caseID));
//			}
//			return new LinkedList<String>(result);
//			
//		}
//	}
	
	public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) { }
    
	/**
	 * Return a section handle with the engine
	 * @return
	 */
    private String getEngineHandle() {
        try {
        	while (!engineInterface.engineCheckConnection(_engineHandle)) {
				_engineHandle = engineInterface.engineConnect(_engineUser,_enginePassword);
			}
		} catch (IOException e) {
			_engineHandle = "<failure>Problem connecting to engine.</failure>";
		}
        return _engineHandle;
    }
    
    /**
	 * Update the tableDistribution
	 * @param distribution List of the distribution interested (case(current).TaskID(offerDistribution)
	 * @param specID YSpecificationID associated to the Task
	 * @param caseID CaseID of the Process
	 */
	
	private void getDistributionInformation(LinkedList<String> distribution, String caseID) {
		for(String s : distribution) {
			String information = s.substring(s.indexOf(YExpression.PARTA));
			String taskID = s.substring(0, s.indexOf(YExpression.PARTA));
			if(information.endsWith(YSensorUtilities.initiator)) {
				if(information.contains("offer")) {
					tableDistribution.get(caseID).put(s, activityLayer.getInitiator(taskID, true, caseID, true, Activity.OfferDis));
					tableDistributionChanging.put(caseID, true);
				}else if(information.contains("allocate")) {
					String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.AllocateDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
						tableDistributionChanging.put(caseID, true);
					}
				}else if(information.contains("start")) {
					String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.StartDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
						tableDistributionChanging.put(caseID, true);
					}
				}
				break;
			}else {
				if(information.contains("offer")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.OfferDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
						tableDistributionChanging.put(caseID, true);
					}
				}else if(information.contains("allocate")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.AllocateDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
						tableDistributionChanging.put(caseID, true);
					}
				}else if(information.contains("start")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.StartDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
						tableDistributionChanging.put(caseID, true);
					}
				}
				break;
			}
		}
	}
    
    public static void initInterfaces(Map<String, String> urlMap) {
    	
    	_engineURI = urlMap.get("InterfaceB_BackEnd");
        if (_engineURI == null) _engineURI = "http://localhost:8080/yawl/ib";
    	
        _resClientURI = urlMap.get("resourceGateway");
        if (_resClientURI == null) _resClientURI = "http://localhost:8080/resourceService/gateway";
        
        _resLogClientURI = urlMap.get("resourceLogGateway");
        if (_resLogClientURI == null) _resLogClientURI = "http://localhost:8080/resourceService/logGateway";
        
        _workQueueClientURI = urlMap.get("workQueueGateway");
        if (_workQueueClientURI == null) _workQueueClientURI = "http://localhost:8080/resourceService/workqueuegateway";
        
        _ixClientURI = urlMap.get("sensorGateway");
        if (_ixClientURI == null) _ixClientURI = "http://localhost:8080/sensorService/ix";
        
        _monitorSensorURI = urlMap.get("monitorSensorService");
        if (_monitorSensorURI == null) _monitorSensorURI = "http://localhost:8080/monitorService/ms";
        
        _timePredictionURI = urlMap.get("timePredictionService");
        if (_timePredictionURI == null) _timePredictionURI = "http://localhost:8080/timePredictionService/tps";
    }
    
    /**
     * Create the sensor for the Case
     * @param specID YSpecification of the Case
     * @param caseID CaseID of the process
     * @return the generic notification time (notifyTime inside the tag sensors)
     */

    private String createSensors(String caseID, String URI, String Version) {
		LinkedList<YSensor> listSensors = new LinkedList<YSensor>();
		String time = null;
		tableDistribution.put(caseID, new HashMap<String, String>());
    	tableLogVariable.put(caseID, new HashMap<String, Object>());
    	tableLogResource.put(caseID, new HashMap<String, Object>());
		String sensorsString = workflowDefinitionLayer.getSensors(caseID, URI, Version);
		Element sensors = JDOMUtil.stringToDocument(sensorsString).getRootElement();
		if("sensors".equals(sensors.getName())) {
			time = sensors.getAttributeValue("notifyTime");
			for(Element sensor : (List<Element>)sensors.getChildren()) {
				listSensors.add(new YSensor(caseID, _workQueueClient, JDOMUtil.elementToString(sensor), time, _monitorSensorURI, _timePredictionURI));
			}
		}
		tableSensor.put(caseID,listSensors);
		for(YSensor sensor : listSensors) {
			if(sensor.getEvent()!=null) {
				String event = sensor.getEvent();
				String taskName = event.substring(0, event.lastIndexOf("_"));
				String eventType = event.substring(event.lastIndexOf("_")+1);
				int type = -1;
				if(eventType.equals("Offered")) {
					type = 0;
				}else if(eventType.equals("Allocated")) {
					type = 1;
				}else if(eventType.equals("Started")) {
					type = 2;
				}else if(eventType.equals("Completed")) {
					type = 3;
				}
				Map<String, List<YSensor>[]> mapTmp = null;
				if( (mapTmp = tableEventSensorNotification.get(caseID)) == null) {
					mapTmp = new HashMap<String, List<YSensor>[]>();
					tableEventSensorNotification.put(caseID, mapTmp);
				}
				List<YSensor>[] arraysTmp = null;
				if((arraysTmp = mapTmp.get(taskName)) == null) {
					arraysTmp = new LinkedList[4];
					mapTmp.put(taskName, arraysTmp);
				}
				if(arraysTmp[type]!=null) {
					arraysTmp[type].add(sensor);
				}else {
					arraysTmp[type] = new LinkedList<YSensor>();
					arraysTmp[type].add(sensor);
				}
			}
		}
		Map<String, List<YSensor>[]> mapTmp = null;
		if((mapTmp = tableEventSensorNotification.get(caseID)) != null) {
			for(String key : mapTmp.keySet()) {
				List<YSensor>[] listTmp = mapTmp.get(key);
				if(listTmp[0]!=null && listTmp[0].size()>0) {
					engineInterface.registerEvent(key, caseID, 0);
				}
				if(listTmp[1]!=null && listTmp[1].size()>0) {
					engineInterface.registerEvent(key, caseID, 1);
				}
				if(listTmp[2]!=null && listTmp[2].size()>0) {
					engineInterface.registerEvent(key, caseID, 2);
				}
				if(listTmp[3]!=null && listTmp[3].size()>0) {
					engineInterface.registerEvent(key, caseID, 3);
				}
			}
		}
		return time;
	}
    
    /**
     * Start the Thread interest to Update the Sensors
     * @param caseID CaseID of the Process
     * @param interval Time of updating
     */    
    private void startSensorUpdater(String caseID, String interval) {
    	YSensorUpdater ysu = new YSensorUpdater(caseID, interval);
    	tableSensorUpdater.put(caseID, ysu);
    	ysu.start();
    }
	
	public void handleCaseCancellationEvent(String caseID) {
		Semaphore sem = null;
		if((sem = lockMap.get(caseID)) != null) {
			try {
				sem.acquire();
				completeCase.remove(caseID);
				tableSensor.remove(caseID);
				tableLogVariable.remove(caseID);
				tableLogVariableChanging.remove(caseID);
				tableEventSensorNotification.remove(caseID);
				sem.release();
				lockMap.remove(caseID);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    public void handleEngineInitialisationCompletedEvent(EngineInterface engineInterface) {
		System.out.println("\nSystem Initialized");
		this.engineInterface = engineInterface;
		_engineURI = engineInterface.getBackEndURI();
		_ixClient = new InterfaceX_ServiceSideClient(_engineURI.replaceFirst("/ib", "/ix"));
		_workQueueClient = new WorkQueueGatewayClient(_workQueueClientURI);
		activityLayer = Activity.getActivity("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Activity");
		activityRoleLayer = ActivityRole.getActivityRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_ActivityRole");
		roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Role");
		subProcessLayer = SubProcess.getSubProcess("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_SubProcess");
		variableLayer = Variable.getVariable("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_Variable");
		workflowDefinitionLayer = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.databaseInterface.YAWL.YAWL_WorkflowDefinition");
    	try {
			_ixClient.addInterfaceXListener("http://localhost:8080/sensorService/ix");
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Vector<String> startedWorkflow = workflowDefinitionLayer.getIDs(null, false, 0, 0, WorkflowDefinition.CaseStart, true, null, null);
		Vector<String> stopedWorkflow = workflowDefinitionLayer.getIDs(null, false, 0, 0, WorkflowDefinition.CaseStart, false, null, null);
		startedWorkflow.removeAll(stopedWorkflow);
		
		for(String ID : startedWorkflow){
			try {
				String URI = workflowDefinitionLayer.getURI(ID);
				String Version = workflowDefinitionLayer.getVersion(ID);
				Semaphore semaphore = new Semaphore(1);
				semaphore.acquire();
				lockMap.put(ID, semaphore);
				if(YSensorUtilities.needSensors(workflowDefinitionLayer.getSensors(ID, URI, Version))){
					(new StarterSensorSystemThread(ID, URI, Version)).start();
    			}else {
    				lockMap.remove(ID);
    				semaphore.release();
    			}
			} catch (InterruptedException e) { }
		}
    }
	
	public void handleCheckCaseConstraintEvent(YSpecificationID specID, String caseID, String data, boolean precheck) {
		try {
			Semaphore semaphore = new Semaphore(1);
			semaphore.acquire();
			lockMap.put(caseID, semaphore);
			if(YSensorUtilities.needSensors(workflowDefinitionLayer.getSensors(caseID, specID.getUri(), specID.getVersionAsString()))){
				(new StarterSensorSystemThread(caseID, specID.getUri(), specID.getVersionAsString())).start();
			}else {
				lockMap.remove(caseID);
				semaphore.release();
			}
		} catch (InterruptedException e) { }
	}
	
	/**
	 * Thread in charged to inizialize a new Case (or to restore)
	 * @author Raffaele Conforti
	 *
	 */
	
	private class StarterSensorSystemThread extends Thread{
		
		private final String caseID;
		private final String URI;
		private final String Version;
		
		public StarterSensorSystemThread(String WorkDefID, String URI, String Version) {
			super();
			this.caseID = WorkDefID;
			this.URI = URI;
			this.Version = Version;
		}
		
		@Override
		public void run() {
			
			tableLogResource.put(caseID, new HashMap<String, Object>());
			tableLogResourceChanging.put(caseID, false);
			tableLogVariable.put(caseID, new HashMap<String, Object>());
			tableLogVariableChanging.put(caseID, false);
			
			String intervalTime = createSensors(caseID, URI, Version);
					
			List<YSensor> listSensor = tableSensor.get(caseID);
			for(YSensor sensor : listSensor) {
				
				getDistributionInformation(sensor.getDistributions(), caseID);
				updateLogVariable(sensor.getLogVariables(), caseID);
				updateLogResources(sensor.getLogResources(), caseID);
				
			}
			
			startSensorUpdater(caseID, intervalTime);
			
			lockMap.get(caseID).release();
		}
	}
	
	/**
	 * Thread in charged to update the Sensors
	 * @author Raffaele Conforti
	 *
	 */
	private class YSensorUpdater extends Thread{
		
		private boolean cycle = true;
		private boolean delete = false;
		private long time;
		private long timeMax;
		private long timeMaxPostStart;
		private long timePassed = 0;
		private final String caseID;
		private Map<String, String> tableDistributionLocal;
		private Map<String, Object> tableLogResourceLocal;
		private Map<String, Object> tableLogVariableLocal;
		private final boolean[] tableDistributionChangingLocal;
		private final boolean[] tableLogResourceChangingLocal;
		private final boolean[] tableLogVariableChangingLocal;
		private final long[] timeUpdate;
		private final long[] timeLeft;
		private final YSensor[] sensors;
		
		/**
		 * 
		 * @param caseID CaseID of the Process associated
		 * @param time Updating Time
		 */
		public YSensorUpdater(String caseID, String time){
			super();
			this.caseID = caseID;
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
				this.time = next.getTimeInMillis()-now.getTimeInMillis();
			}else {
				this.time = (new Long(start))*timesMillis[index];
			}
			if(this.time==0){
				cycle = false;
			}
			sensors = tableSensor.get(this.caseID).toArray(new YSensor[0]);
			timeUpdate = new long[sensors.length];
			timeLeft = new long[sensors.length];
			for(int i = 0; i<sensors.length; i++) {
				timeUpdate[i] = sensors[i].getUpdateTime();
				timeLeft[i] = sensors[i].getStartTime();
				if(sensors[i].getStartTime() <= this.time || i == 0) this.time = sensors[i].getStartTime();
				if(sensors[i].getStartTime() > timeMax || i == 0) timeMax = sensors[i].getStartTime();
				if(sensors[i].getUpdateTime() > timeMaxPostStart || i == 0) timeMaxPostStart = sensors[i].getUpdateTime();
			}
			tableDistributionChangingLocal = new boolean[sensors.length];
			tableLogResourceChangingLocal = new boolean[sensors.length];
			tableLogVariableChangingLocal = new boolean[sensors.length];
		}
		
		@Override
		public void run() {
			while(cycle){
				
				try {
					sleep(time);
					reduceTime();
				} catch (InterruptedException e) { e.printStackTrace();	}
				
				if(lockMap.get(caseID)==null) {
					break;
				}
				try {
					lockMap.get(caseID).acquire();
					delete = YSensorManagerImplLayer.this.completeCase.contains(caseID);
					lockMap.get(caseID).release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(cycle){
					try {
						lockMap.get(caseID).acquire();
						updateChanging();
						
						if(timePassed == 0) {
							for(YSensor sensor: sensors) {
								if(sensor.getEvent()==null) {
									updateLogVariable(sensor.getLogVariables(), caseID);
									updateLogResources(sensor.getLogResources(), caseID);
								}
							}
							if((tableDistributionLocal = YSensorManagerImplLayer.this.tableDistribution.get(caseID)) != null) tableDistributionLocal = (HashMap<String, String>) ((HashMap<String, String>) tableDistributionLocal).clone();
							if((tableLogResourceLocal = YSensorManagerImplLayer.this.tableLogResource.get(caseID)) != null) tableLogResourceLocal = (HashMap<String, Object>) ((HashMap<String, Object>) tableLogResourceLocal).clone();
							if((tableLogVariableLocal = YSensorManagerImplLayer.this.tableLogVariable.get(caseID)) != null) tableLogVariableLocal = (HashMap<String, Object>) ((HashMap<String, Object>) tableLogVariableLocal).clone();
						}else {
							
							if(tableDistributionLocal != null) {
								tableDistributionLocal.putAll((Map<String,String>) ((HashMap<String, String>) YSensorManagerImplLayer.this.tableDistribution.get(caseID)).clone());
							}else {
								tableDistributionLocal = (Map<String,String>) ((HashMap<String, String>) YSensorManagerImplLayer.this.tableDistribution.get(caseID)).clone();
							}
							
							if(tableLogResourceLocal != null) {
								tableLogResourceLocal.putAll((HashMap<String, Object>) ((HashMap<String, Object>) YSensorManagerImplLayer.this.tableLogResource.get(caseID)).clone());
							}else {
								tableLogResourceLocal = (HashMap<String, Object>) ((HashMap<String, Object>) YSensorManagerImplLayer.this.tableLogResource.get(caseID)).clone();
							}
							
							if(tableLogVariableLocal != null) {
								tableLogVariableLocal.putAll((HashMap<String, Object>) ((HashMap<String, Object>) YSensorManagerImplLayer.this.tableLogVariable.get(caseID)).clone());
							}else {
								tableLogVariableLocal = (HashMap<String, Object>) ((HashMap<String, Object>) YSensorManagerImplLayer.this.tableLogVariable.get(caseID)).clone();
							}
							
							
						}
						Map<String,String> tD = null;
						if((tD = YSensorManagerImplLayer.this.tableDistribution.get(caseID)) != null) tD.clear();
						if(YSensorManagerImplLayer.this.tableDistributionChanging.containsKey(caseID)) YSensorManagerImplLayer.this.tableDistributionChanging.put(caseID, false);
						Map<String,Object> tLV = null;
						if((tLV = YSensorManagerImplLayer.this.tableLogVariable.get(caseID)) != null) tLV.clear();
						if(YSensorManagerImplLayer.this.tableLogVariableChanging.containsKey(caseID)) YSensorManagerImplLayer.this.tableLogVariableChanging.put(caseID, false);
						Map<String,Object> tLR = null;
						if((tLR = YSensorManagerImplLayer.this.tableLogResource.get(caseID)) != null) tLR.clear();
						if(YSensorManagerImplLayer.this.tableLogResourceChanging.containsKey(caseID)) YSensorManagerImplLayer.this.tableLogResourceChanging.put(caseID, false);
						
						lockMap.get(caseID).release();
						
						for(int i = 0; i<sensors.length; i++) {
							YSensor sensor = sensors[i]; 
							if(sensor.getEvent()==null) {
								if(timeLeft[i] == 0){
									YSensorMessageUpdate variableUpdated = new YSensorMessageUpdate();
									
									if(tableDistributionChangingLocal[i]) {
										for(String distribution: sensor.getDistributions()) {
											
											String dis = null;
											if((dis = tableDistributionLocal.get(distribution)) != null) {
												variableUpdated.addDistribution(distribution, dis);
											}
										}
									}
									
									if(tableLogResourceChangingLocal[i]) {
										for(String logResource: sensor.getLogResources()) {
											if(tableLogResourceLocal.containsKey(logResource)) {
												variableUpdated.addLogResource(logResource, tableLogResourceLocal.get(logResource));
											}
										}
									}
									
									if(tableLogVariableChangingLocal[i]) {
										for(String logVariable: sensor.getLogVariables()) {
											
											Object result = null;
											if((result = tableLogVariableLocal.get(logVariable)) != null) {
												if(result instanceof String) {
													String res = (String) result;
													res = YSensorUtilities.checkIfXML(res);
													variableUpdated.addLogVariable(logVariable, res);
												}else {
													LinkedList<String> res = (LinkedList<String>) result;
													for(int j=0; j<res.size(); j++) {
														String res2 = res.get(j);
														res2 = YSensorUtilities.checkIfXML(res2);
														res.set(j, res2);
													}
													variableUpdated.addLogVariable(logVariable, res);
												}
											}
										}
									}
									sensor.update(variableUpdated);
								}
								resetChanging(i);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				updateTime();
				if(delete) { 
					YSensorManagerImplLayer.this.handleCaseCancellationEvent(caseID);
					cycle = false;
				}
			}
		}
		
		/**
		 * Reduce the time to the next update (for each sensor) of the passed 
		 */
		private void reduceTime() {
			timePassed += time;
			if(timePassed >= timeMax) {
				timePassed = 0;
				timeMax = timeMaxPostStart;
			}
			for(int i = 0; i<timeLeft.length; i++) {
				timeLeft[i] = timeLeft[i] - time;
			}
		}
		
		/**
		 * Update the time to the next update for the sensors that have reached 0
		 */
		private void updateTime() {
			long tmpTime = timeMax;
			for(int i = 0; i<timeLeft.length; i++) {
				if(timeLeft[i] == 0) timeLeft[i] = timeUpdate[i];
				if(timeLeft[i] <= tmpTime || i == 0) tmpTime = timeLeft[i];
			}
			time = tmpTime;
		}
		
		/**
		 * Reset the tableChanging associated to the sensor number "sensors"
		 * @param sensor
		 */
		private void resetChanging(int sensor) {
			tableLogResourceChangingLocal[sensor] = false;
			tableLogVariableChangingLocal[sensor] = false;
		}
		
		/**
		 * Update the changing table
		 */
		private void updateChanging() {
			for(int i = 0; i<sensors.length; i++) {
				
				if(YSensorManagerImplLayer.this.tableLogResourceChanging.get(caseID))
				tableLogResourceChangingLocal[i] = YSensorManagerImplLayer.this.tableLogResourceChanging.get(caseID);
				
				if(YSensorManagerImplLayer.this.tableLogVariableChanging.get(caseID))
				tableLogVariableChangingLocal[i] = YSensorManagerImplLayer.this.tableLogVariableChanging.get(caseID);

			}
		}
		
		public void updateSensor(YSensor sensor) {
			Semaphore sem = null;
			try {
				sleep(10000);
				sem = lockMap.get(caseID);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				sem.acquire();
				delete = YSensorManagerImplLayer.this.completeCase.contains(caseID);
				sem.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try {
				sem.acquire();
				
				updateLogVariable(sensor.getLogVariables(), caseID);
				updateLogResources(sensor.getLogResources(), caseID);
				if((tableDistributionLocal = tableDistribution.get(caseID)) != null) tableDistributionLocal = (HashMap<String, String>) ((HashMap<String, String>) tableDistributionLocal).clone();
				if((tableLogResourceLocal = tableLogResource.get(caseID)) != null) tableLogResourceLocal = (HashMap<String, Object>) ((HashMap<String, Object>) tableLogResourceLocal).clone();
				if((tableLogVariableLocal = tableLogVariable.get(caseID)) != null) tableLogVariableLocal = (HashMap<String, Object>) ((HashMap<String, Object>) tableLogVariableLocal).clone();
				
				Map<String, String> tD = null;
				if((tD = tableDistribution.get(caseID)) != null) tD.clear();
				if(tableDistributionChanging.containsKey(caseID)) tableDistributionChanging.put(caseID, false);
				
				Map<String, Object> tLV = null;
				if((tLV = tableLogVariable.get(caseID)) != null) tLV.clear();
				if(tableLogVariableChanging.containsKey(caseID)) tableLogVariableChanging.put(caseID, false);
				
				Map<String, Object> tLR = null;
				if((tLR = tableLogResource.get(caseID)) != null) tLR.clear();
				if(tableLogResourceChanging.containsKey(caseID)) tableLogResourceChanging.put(caseID, false);
				
				sem.release();
				
				for(int i = 0; i<sensors.length && sensor.equals(sensors[i]); i++) { 
					YSensorMessageUpdate variableUpdated = new YSensorMessageUpdate();
					
					if(tableDistributionChangingLocal[i]) {
						for(String distribution: sensor.getDistributions()) {
							
							String dis = null;
							if((dis = tableDistributionLocal.get(distribution)) != null) {
								variableUpdated.addDistribution(distribution, dis);
							}
						}
					}
					
					if(tableLogResourceChangingLocal[i]) {
						for(String logResource: sensor.getLogResources()) {
							
							Object res = null;
							if((res = tableLogResourceLocal.get(logResource)) != null) {
								variableUpdated.addLogResource(logResource, res);
							}
						}
					}
					
					if(tableLogVariableChangingLocal[i]) {
						for(String logVariable: sensor.getLogVariables()) {
//							System.out.println(logVariable);
//							System.out.println(tableLogVariableLocal.keySet());
//							System.out.println(tableLogVariableLocal.containsKey(logVariable));
							Object result = null;
							if((result = tableLogVariableLocal.get(logVariable)) != null) {								
								if(result instanceof String) {
									String res = (String) result;
									res = YSensorUtilities.checkIfXML(res);
									variableUpdated.addLogVariable(logVariable, res);
								}else {
									LinkedList<String> res = (LinkedList<String>) result;
									for(int j=0; j<res.size(); j++) {
										String res2 = res.get(j);
										res2 = YSensorUtilities.checkIfXML(res2);
										res.set(j, res2);
									}
									variableUpdated.addLogVariable(logVariable, res);
								}
							}
						}
					}
					sensor.update(variableUpdated);
						
					resetChanging(i);
				}
					
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			if(delete) { 
				YSensorManagerImplLayer.this.handleCaseCancellationEvent(caseID);
				cycle = false;
			}
		}
	}
	
}
