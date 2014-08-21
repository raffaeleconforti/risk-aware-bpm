package org.yawlfoundation.yawl.riskPrediction.Annotators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import net.sf.saxon.s9api.SaxonApiException;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.riskPrediction.DatabaseInterface.CacheKey;
import org.yawlfoundation.yawl.sensors.*;
import org.yawlfoundation.yawl.sensors.actions.ActionExecutor;
import org.yawlfoundation.yawl.sensors.actions.ActionIdentifier;
import org.yawlfoundation.yawl.sensors.actions.ActionInitializer;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.engineInterface.EngineInterface;
import org.yawlfoundation.yawl.sensors.language.MathEvaluator;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;




/**
 * Author: Raffaele Conforti
 * Date: 29-lug-2010
 * Time: 13.32.13
 */

public class YSensorPredictionUpdater {

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
	private static YSensorPredictionUpdater _me = null; 
	private WorkQueueGatewayClient _workQueueClient;
	private final String[] times = new String[]{"Seconds", "Minutes", "Hours", "Days", "Weeks", "Month", "Years", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	private final ConcurrentHashMap<String, LinkedList<YSensor>> tableSensor = new ConcurrentHashMap<String, LinkedList<YSensor>>();
	
	private final HashMap<String, HashMap<String, LinkedList<YSensor>[]>> tableEventSensorNotification = new HashMap<String, HashMap<String, LinkedList<YSensor>[]>>();
	private final HashMap<String, HashMap<String,HashMap<String, String>>> tableUploadDistribution = new HashMap<String, HashMap<String,HashMap<String,String>>>();
	
	private final ConcurrentHashMap<String, HashMap<String,Object>> tableLogResource = new ConcurrentHashMap<String, HashMap<String,Object>>();
	private final ConcurrentHashMap<String, HashMap<String,Object>> tableLogVariable = new ConcurrentHashMap<String, HashMap<String,Object>>();
	private final ConcurrentHashMap<String, HashMap<String,String>> tableDistribution = new ConcurrentHashMap<String, HashMap<String,String>>();
	
	private final ConcurrentHashMap<CacheKey, HashSet<String>> cacheMap = new ConcurrentHashMap<CacheKey, HashSet<String>>();
	
	private final HashSet<String> completeCase = new HashSet<String>();
	private final String _engineHandle = "";
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
    
    public static void main(String[] args) throws Exception {
    	XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Log.xes");//"/home/stormfire/Documents/Useless/log.xes"); //

		String specification = null;
		try {
			File f = new File("/home/stormfire/CarrierAppointment4.yawl");//"/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl"); //
			InputStream is = new FileInputStream(f);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			specification = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String resources = null;
		try {
			File f = new File("/home/stormfire/orderfulfillment.ybkp");
			InputStream is = new FileInputStream(f);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			resources = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("Xlog", log);
		parameters.put("specification", specification);
		parameters.put("resources", resources);
		
		InterfaceManager a = new InterfaceManager(InterfaceManager.PROM, parameters);
    	
    	YSensorPredictionUpdater ysmi = new YSensorPredictionUpdater();
    	ysmi.activityLayer = a.getActivityLayer();
    	ysmi.activityRoleLayer = a.getActivityRoleLayer();
    	ysmi.roleLayer = a.getRoleLayer();
    	ysmi.subProcessLayer = a.getSubProcessLayer();
    	ysmi.variableLayer = a.getVariableLayer();
    	ysmi.workflowDefinitionLayer = a.getWorkflowDefinitionLayer();
    	ysmi._workQueueClient = null;
    	String sensor = "<sensor name=\"Approval Fraud\"><vars><var name=\"sfo1\" mapping=\"case(current).Prepare_Transportation_Quote_390(startResource)\" type=\"\" /><var name=\"ASPOa\" mapping=\"case(current).Prepare_Transportation_Quote_390(OfferTimeInMillis)\" type=\"\" /></vars><risk><riskProbability>IF[(ASPOn&gt;3)]THEN[1]ELSE[(ASPOn/4)]</riskProbability><riskThreshold>0.5</riskThreshold><riskMessage>Violation 4-eye principle detected</riskMessage></risk><consequence>10</consequence></sensor>";
    	YSensor s = new YSensor("450", JDOMUtil.stringToElement(sensor), null);
//    	LinkedList<String> distribution = new LinkedList<String>();
//    	distribution.add("Prova_3(offerInitiator)");
//    	ysmi.tableDistribution.put("355", new HashMap<String, String>());
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	map.put("case(current).Prepare_Transportation_Quote_390(startResource)", "<start><participant>PA-a97f2fc8-2fca-44a8-b415-b29ec6acc07e</participant></start>");
    	map.put("case(current).Prepare_Transportation_Quote_390(OfferTimeInMillis)", "13640280000");
    	ysmi.tableLogVariable.put("450", map);
    	LinkedList sl = new LinkedList<YSensor>();
    	sl.add(s);
    	ysmi.tableSensor.put("450", sl);
    	ysmi.updateSensor(s, "450");
//    	ysmi.updateLogVariable(s.getLogVariables(), "450");
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
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1 AND null_389.Company=c AND Prepare_Transportation_Quote_390.ShipmentCost<=1000 AND Prepare_Transportation_Quote_390(CompleteTime)>(ASPOa-(5*24*60*60*1000)) AND (ID)!=[IDCurr]).Prepare_Transportation_Quote_390(CountElements)", "2000"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Pickup_Appointment_397(Count)>=\"2\").Modify_Pickup_Appointment_397(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)>=\"1\" AND Produce_Shipment_Notice_401(isOffered)=\"true\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogResourceTaskItem("case(current).Modify_Delivery_Appointment_398(completeResource)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)>=\"3\" OR Modify_Pickup_Appointment_397(Count)>=\"3\" AND Modify_Delivery_Appointment_398(Count)=\"3\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)>=\"3\" AND Modify_Delivery_Appointment_398(Count)=\"3\" OR Modify_Pickup_Appointment_397(Count)>=\"3\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)=\"3\" AND Modify_Pickup_Appointment_397(Count)>=\"3\" OR Modify_Delivery_Appointment_398(Count)>=\"3\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)=\"2\" AND Modify_Pickup_Appointment_397(Count))>=\"2\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Modify_Delivery_Appointment_398(Count)>=\"2\" AND Modify_Pickup_Appointment_397(Count))=\"2\").Modify_Delivery_Appointment_398(CountElements)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1 AND null_389.Company=c AND Prepare_Transportation_Quote_390.ShipmentCost>=1000 AND Prepare_Transportation_Quote_390(CompleteTimeInMillis)>(ASPOa-(5*24*60*60*1000)) AND (ID)<[IDCurr]).Prepare_Transportation_Quote_390(CountElements)", "5"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(completeResource)", "5"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=\"<complete><participant>PA-a97f2fc8-2fca-44a8-b415-b29ec6acc07e</participant><complete>\" AND null_389.Company=\"customer15\" AND Prepare_Transportation_Quote_390.ShipmentCost>=\"1000\" AND Prepare_Transportation_Quote_390(CompleteTimeInMillis)>\"-10000\" AND (ID)<[IDCurr]).Prepare_Transportation_Quote_390(CountElements)", "5"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(CompleteTimeInMillis)", "5"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(isAllocated)", "1"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(startResource)", "1"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).null_389.Company", "1"));
//    	System.out.println(ysmi.getLogVariableTaskItem2("case(current).Carrier_Appointment(PassTimeInMillis)", "1"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Carrier_Appointment(PassTimeInMillis)", "2"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(OfferTimeInMillis)", "1"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1 AND null_389.Company=\"customer15\" AND Prepare_Transportation_Quote_390.ShipmentCost<=\"1000\" AND Prepare_Transportation_Quote_390(CompleteTimeInMillis)>\"-10000\" AND (ID)<[IDCurr]).Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Modify_Delivery_Appointment_398(Count)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Produce_Shipment_Notice_401(isOffered)=\"true\").Modify_Pickup_Appointment_397(Count)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1).Prepare_Transportation_Quote_390(Count)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390.ShipmentCost<=\"10000\").Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(null_389.Company=\"customer11\").Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1 AND null_389.Company=c AND Prepare_Transportation_Quote_390(CompleteTimeInMillis)>\"-10000\").Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1).Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(startResource)", "13"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(current).null_389.Company", "13"));
    	System.out.println(ysmi.getLogVariableTaskItem("case(current).Prepare_Transportation_Quote_390(completeResource)", "875"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(null_389.Company=c).null_389(CountElements)", "450"));-1.026102E10
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1 AND Prepare_Transportation_Quote_390.ShipmentCost<\"5000\" AND Prepare_Transportation_Quote_390.NumberPackages>\"3\" AND Prepare_Transportation_Quote_390(CompleteTimeInMillis)>=(ASPOa-(30*24*60*60*1000)) AND Prepare_Transportation_Quote_390(CompleteTimeInMillis<=ASPOa).Prepare_Transportation_Quote_390(CountElements)", "450"));
//    	System.out.println(ysmi.getLogVariableTaskItem("case(Prepare_Transportation_Quote_390(completeResource)=sfo1).Prepare_Transportation_Quote_390(completeResource)", "450"));
    	
    	/** Task StartTime */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(StartTime)", "210");
    	
    	/** Task Variable */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594.ShipmentInvoice.Company", "210");
    	
    	/** Task Count */
//    	ysmi.getLogVariableTaskItem("case(current).Issue_Shipment_Invoice_594(Count)", "210");
    	
    	/** Task Resource */
//    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(startResource)", "210");
    	
    	/** Task Distribution */
//    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(offerDistribution)", "210");
    	
    	/** Task Initiator */
//    	ysmi.getLogResourceTaskItem("case(current).Issue_Shipment_Invoice_594(offerInitiator)", "210");
    	
    	/** Net isStarted */
//    	ysmi.getLogVariableTaskItem("case(current).Payment(isStarted)", "210");
    	
    	/** Net StartTime */
//    	ysmi.getLogVariableTaskItem("case(current).Payment(StartTime)", "210");
    	
    	/** Net Variable */
//    	ysmi.getLogVariableTaskItem("case(current).Payment", "210");
    	
    	/** NetVarNetVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").New_Net_1", "317");
    	
    	/** NetVarNetTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").New_Net_1(StartTime)", "317");
    	
    	/** NetVarNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").New_Net_1(isStarted)", "317");
    	
    	/** NetIsTimeNetVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1", "317");
    	
    	/** NetIsTimeNetTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1(StartTime)", "317");
    	
    	/** NetIsTimeNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").New_Net_1(isStarted)", "317");
    	
    	/** TaskVarNetVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1", "317");
    	
    	/** TaskVarNetTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1(StartTime)", "317");
    	
    	/** TaskVarNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").New_Net_1(isStarted)", "317");
    	
    	/** TaskIsTimeNetVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1", "317");
    	
    	/** TaskIsTimeNetTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1(StartTime)", "317");
    	
    	/** TaskIsTimeNetIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").New_Net_1(isStarted)", "317");
    	
    	/** NetVarTaskVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").Prova_3", "317");
    	
    	/** NetVarTaskTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"b\").Prova_3(StartTime)", "317");
    	
    	/** NetVarTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1.a=\"a\").Prova_3(isStarted)", "317");
    	
    	/** NetIsTimeTaskVar */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3", "317");
    	
    	/** NetIsTimeTaskTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3(StartTime)", "317");
    	
    	/** NetIsTimeTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(New_Net_1(isStarted)=\"true\").Prova_3(isStarted)", "317");
    	
    	/** TaskVarTaskVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3", "317");
    	
    	/** TaskVarTaskTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3(StartTime)", "317");
    	
    	/** TaskVarTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3.a=\"a\").Prova_3(isStarted)", "317");
    	
    	/** TaskIsTimeTaskVar */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3", "317");
    	
    	/** TaskIsTimeTaskTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3(StartTime)", "317");
    	
    	/** TaskIsTimeTaskIsTime */
//    	ysmi.getLogVariableTaskItem("case(Prova_3(isStarted)=\"true\").Prova_3(isStarted)", "317");
    	
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
						try {
							mathSemaphore.acquire();
							mathEvaluator.setExpression(tmp);
							tmp = ""+mathEvaluator.getValue();
							mathSemaphore.release();
						} catch (Exception e) {
							tmp = "null";
							mathSemaphore.release();
						}
					}
				}
			}
		}else {
			tmp = tmp.substring(1, tmp.length()-1);
		}
    	return tmp;
    }
    
    public Object getLogVariableTaskItem2(String logTaskItem, String WorkDefID) {//, boolean internal) {
//    	System.out.println(logTaskItem);
    	Object o = getLogVariableTaskItem2(logTaskItem, WorkDefID);
//    	System.out.println(o);
    	return o;
    }
    
    public Object getLogVariableTaskItem(String logTaskItem, String WorkDefID) {//, boolean internal) {
    	Object[] parameters = calculate_DiffCaseID_Param_Current_Expression(logTaskItem);
    	
    	Integer diffCaseID = (Integer) parameters[0];
		String param = (String) parameters[1];
		Boolean current = (Boolean) parameters[2];
		Boolean expression = (Boolean) parameters[3];
		
		if(logTaskItem.contains(YSensorUtilities.fraudProb)){
			return fraudProbabilityFunc(logTaskItem, param, WorkDefID);
		}else if(!expression) { //Specific Case
			
			WorkDefID = getWorkDefID(WorkDefID, current, diffCaseID);
			if(WorkDefID == null) return null;
			
			String taskName = getTaskName(logTaskItem);
			
			String variableName = getVariableName(logTaskItem);
			
			boolean netVariable = false;
			
//			if(subProcessLayer.isSubProcess(WorkDefID, taskName.substring(0, taskName.indexOf(".")))) {
			if(logTaskItem.contains(").(TimeEstimationInMillis)") || subProcessLayer.isSubProcess(WorkDefID, taskName)) {
				netVariable = true;
			}
			
			int action = ActionIdentifier.getAction(logTaskItem);
			
			if(!netVariable) { //Analyse Task

				return ActionExecutor.getTaskActionResult(action, taskName, WorkDefID, variableName);
				
			}else { //Analyse Net

                return ActionExecutor.getNetActionResult(action, taskName, WorkDefID, variableName);
				
			}
			
		}else { // Group of cases by query
		
			LinkedList<String> cases = generateCaseID(param, WorkDefID);

			logTaskItem = logTaskItem.substring(logTaskItem.indexOf(param)+param.length()+1);
									
			String taskName = getTaskNameMulti(logTaskItem);
			
			String variableName = getVariableName(logTaskItem);
			
			boolean netVariable = false;
			
			if(subProcessLayer.isSubProcess(WorkDefID, taskName)) {
				netVariable = true;
			}
			
			int action = ActionIdentifier.getAction(logTaskItem);
			
			if(!netVariable) { //Analyse Task
				
				return ActionExecutor.getMultipleTaskActionResult(action, taskName, cases, variableName);
				
			}else { //Analyse Net
				
				return ActionExecutor.getMultipleNetActionResult(action, taskName, cases, variableName);
				
			}
			
    	}
	}
    
    private Object[] calculate_DiffCaseID_Param_Current_Expression(String logTaskItem) {
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
    	return new Object[] {diffCaseID, param, current, expression};
    }
    
    public String getTaskName(String logTaskItem) {
    	String taskName = null;
    	if(')' == logTaskItem.charAt(logTaskItem.length()-1)) {
    		if(logTaskItem.contains(YExpression.DOT)) {
    			taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.lastIndexOf(YExpression.PARTA));
			}else {
				if(logTaskItem.endsWith(YSensorUtilities.list)) { 
					taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1, logTaskItem.length()-6);
				}else {
					taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1);
				}
			}
		}else {
			taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1);
		}
        return taskName;
    }
    
    public String getVariableName(String logTaskItem) {
    	String taskName = null;
    	if(')' != logTaskItem.charAt(logTaskItem.length()-1)) {
			taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1);
			taskName = taskName.substring(taskName.indexOf(YExpression.DOT)+1);
		}else if (logTaskItem.endsWith(YSensorUtilities.list)){
			taskName = logTaskItem.substring(logTaskItem.indexOf(YExpression.DOT)+1);
			taskName = taskName.substring(taskName.indexOf(YExpression.DOT)+1);
			taskName = taskName.substring(0, taskName.length()-6);
		}
    	return taskName;
    }
    
    public String getTaskNameMulti(String logTaskItem) {
		String taskName = null;
		if(')' == logTaskItem.charAt(logTaskItem.length()-1)) {
//			if(!logTaskItem.contains(YExpression.DOT)) {
				taskName = logTaskItem.substring(0, logTaskItem.lastIndexOf(YExpression.PARTA));
//			}else {
//				taskName = logTaskItem.substring(0, logTaskItem.indexOf(YExpression.DOT));
//			}
		}else {
			taskName = logTaskItem;//.substring(0, logTaskItem.indexOf(YExpression.DOT));
		}
    	return taskName;
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
		}
//		if(subProcessLayer.isSubProcess(WorkDefID, taskName)) {
//			return taskName;
//		}else {
//	    	String number = taskName.substring(taskName.lastIndexOf("_")+1);
//			try {
//				new Integer(number);
//				taskName = taskName.substring(0, taskName.lastIndexOf("_"));
//			}catch (NumberFormatException nfe) {}
//			return taskName;
//		}
		return taskName;
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
//			activity = fixName(WorkDefID, activity);
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
			
			int actionType = ActionIdentifier.getAction(preVar);
			
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
	
					if(!netVariable) { //Analyse Task
						
						casesPartial = ActionExecutor.getExpressionTaskActionResult(actionType, activity, oper, preVar, postVar, suffix, workDefID, casesPartial);
						
					}else { //Analyse Net
						
						casesPartial = ActionExecutor.getExpressionNetActionResult(actionType, activity, oper, preVar, postVar, suffix, workDefID, casesPartial);
						
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

    public YSensorPredictionUpdater() {
    	super();
    	_me = this;
    }

    public static YSensorPredictionUpdater getInstance(InterfaceManager im) {
    	if (_me == null) {
    		_me = new YSensorPredictionUpdater();
	    	_me.activityLayer = im.getActivityLayer();
	    	_me.activityRoleLayer = im.getActivityRoleLayer();
	    	_me.roleLayer = im.getRoleLayer();
	    	_me.subProcessLayer = im.getSubProcessLayer();
	    	_me.variableLayer = im.getVariableLayer();
	    	_me.workflowDefinitionLayer = im.getWorkflowDefinitionLayer();
//	    	_me._workQueueClient = new WorkQueueGatewayClient("http://localhost:8080/resourceService/workqueuegateway");
	    	ActionInitializer.inizializeActions(_me.workflowDefinitionLayer, _me.activityLayer, _me.activityRoleLayer, _me.roleLayer, _me.variableLayer, _me.subProcessLayer, "http://localhost:8080/predictionService");
    	}
        return _me;
    }
    
    /**
     * Use getLogVariableTaskItem(String, YSpecificationID, String) to update the tableLogVariable
     * @param listLogVariablesName LinkedList<String> with the logTaskItem
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
//						newSubLogCase = fixName(WorkDefID, newSubLogCase);
						String expression = (newSubLogCase+YExpression.DOT+last).replace('.', '/').replace(' ','_');
						try {
							if(log instanceof String) {
								String query = SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument((String)log));
								if(query.contains("<?xml version=")) {
					    			query = query.substring(query.indexOf(">")+1);
					    			if(query.isEmpty()) query = null;
					    		}
								tableLogVariable.get(WorkDefID).put(logVariablesName, query);
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
							}
						} catch (SaxonApiException sae) { sae.printStackTrace();} 
					}
				}else {
//					System.out.println(logVariablesName);
//					String ca = logVariablesName.substring(0, logVariablesName.indexOf(YExpression.PARTC)+1);
//					String newSubLogCase = logVariablesName.substring(logVariablesName.indexOf(YExpression.DOT)+1);
//					String last = newSubLogCase.substring(newSubLogCase.indexOf(YExpression.PARTA));
//					newSubLogCase = newSubLogCase.substring(0, newSubLogCase.indexOf(YExpression.PARTA));
//					newSubLogCase = ca+YExpression.DOT+newSubLogCase+last;
//					newSubLogCase = newSubLogCase.replace('_', ' ');
					String newSubLogCase = logVariablesName;
//					newSubLogCase = fixName(WorkDefID, newSubLogCase);
					log = getLogVariableTaskItem(newSubLogCase, WorkDefID);
                    subLogCase = newSubLogCase;
					if(log != null) {
						tableLogVariable.get(WorkDefID).put(logVariablesName, log);
					}
				}
			}
		}
    }
    
    /**
     * Use the getLogResourceTaskItem(String, YSpecificationID, String, String) to upload the tableLogResource
     * @param listLogResourcesName List of the logTaskItem
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
					if(result.contains("<?xml version=")) {
		    			result = result.substring(result.indexOf(">")+1);
		    			if(result.isEmpty()) result = null;
		    		}
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
			}else if(information.contains("allocate")) {
				String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.AllocateDis);
				if(res!=null) {
					tableDistribution.get(caseID).put(s, res);
				}
			}else if(information.contains("start")) {
				String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.StartDis);
				if(res!=null) {
					tableDistribution.get(caseID).put(s, res);
				}
			}
		}else {
			if(information.contains("offer")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.OfferDis));
			}else if(information.contains("allocate")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.AllocateDis));
			}else if(information.contains("start")) {
				tableDistribution.get(caseID).put(s, activityLayer.getDistribution(taskID, true, caseID, true, Activity.Completed));
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
//		
//		if(logTaskItem.contains(YSensorUtilities.caseCurrent) || logTaskItem.contains(YSensorUtilities.caseCurrentM)){
//			if(logTaskItem.contains(YSensorUtilities.currentM)){
//				diffCaseID = new Integer(logTaskItem.substring(logTaskItem.indexOf(YSensorUtilities.currentM)+8, logTaskItem.indexOf(YExpression.PARTC)));
//			}else {
//				diffCaseID = new Integer(0);
//			}
//		}else {
//			param = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTA), logTaskItem.indexOf(YExpression.PARTC)+1);
//			String tmp = logTaskItem.substring(logTaskItem.indexOf(YExpression.PARTC)+1);
//			while(tmp.contains(YExpression.GREATER) || tmp.contains(YExpression.MINOR) || tmp.contains(YExpression.SINGLEEQUAL) || ')' == tmp.charAt(0)) {
//				param += tmp.substring(0, tmp.indexOf(YExpression.PARTC)+1);
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
//			
//			WorkDefID = getWorkDefID(WorkDefID, current, diffCaseID);
//			if(WorkDefID == null) return null;
//			
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
//				
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
	
    /**
	 * Update the tableDistribution
	 * @param distribution List of the distribution interested (case(current).TaskID(offerDistribution)
	 * @param caseID CaseID of the Process
	 */
	
	private void getDistributionInformation(LinkedList<String> distribution, String caseID) {
		for(String s : distribution) {
			String information = s.substring(s.indexOf(YExpression.PARTA));
			String taskID = s.substring(0, s.indexOf(YExpression.PARTA));
			if(information.endsWith(YSensorUtilities.initiator)) {
				if(information.contains("offer")) {
					tableDistribution.get(caseID).put(s, activityLayer.getInitiator(taskID, true, caseID, true, Activity.OfferDis));
				}else if(information.contains("allocate")) {
					String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.AllocateDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
					}
				}else if(information.contains("start")) {
					String res = activityLayer.getInitiator(taskID, true, caseID, true, Activity.StartDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
					}
				}
				break;
			}else {
				if(information.contains("offer")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.OfferDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
					}
				}else if(information.contains("allocate")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.AllocateDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
					}
				}else if(information.contains("start")) {
					String res = activityLayer.getDistribution(taskID, true, caseID, true, Activity.StartDis);
					if(res!=null) {
						tableDistribution.get(caseID).put(s, res);
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
     * @param caseID CaseID of the process
     * @return the generic notification time (notifyTime inside the tag sensors)
     */

    private String createSensors(String caseID, String URI, String Version, String dirPath) {
		LinkedList<YSensor> listSensors = new LinkedList<YSensor>();
		String time = null;
		String sensorType = null;
		tableDistribution.put(caseID, new HashMap<String, String>());
    	tableLogVariable.put(caseID, new HashMap<String, Object>());
    	tableLogResource.put(caseID, new HashMap<String, Object>());
		String sensorsString = workflowDefinitionLayer.getSensors(caseID, URI, Version);
		Element sensors = JDOMUtil.stringToDocument(sensorsString).getRootElement();
		if("sensors".equals(sensors.getName())) {
			time = sensors.getAttributeValue("notifyTime");
			for(Element sensor : (List<Element>)sensors.getChildren()) {
				sensorType = sensor.getAttributeValue("type");
				if(sensorType != null && sensorType.equals("script")) {
					listSensors.add(new YSensorScript(caseID, _workQueueClient, JDOMUtil.elementToString(sensor), time, _monitorSensorURI, _timePredictionURI, null, URI, dirPath));
				}else {
					listSensors.add(new YSensor(caseID, _workQueueClient, JDOMUtil.elementToString(sensor), time, _monitorSensorURI, _timePredictionURI, null));
				}
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
				HashMap<String, LinkedList<YSensor>[]> mapTmp = null;
				if( (mapTmp = tableEventSensorNotification.get(caseID)) == null) {
					mapTmp = new HashMap<String, LinkedList<YSensor>[]>();
					tableEventSensorNotification.put(caseID, mapTmp);
				}
				LinkedList<YSensor>[] arraysTmp = null;
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
		HashMap<String, LinkedList<YSensor>[]> mapTmp = null;
		if((mapTmp = tableEventSensorNotification.get(caseID)) != null) {
//			for(String key : mapTmp.keySet()) {
			for(Entry<String, LinkedList<YSensor>[]> entry : mapTmp.entrySet()) {
				LinkedList<YSensor>[] listTmp = entry.getValue();
				if(listTmp[0]!=null && listTmp[0].size()>0) {
					engineInterface.registerEvent(entry.getKey(), caseID, 0);
				}
				if(listTmp[1]!=null && listTmp[1].size()>0) {
					engineInterface.registerEvent(entry.getKey(), caseID, 1);
				}
				if(listTmp[2]!=null && listTmp[2].size()>0) {
					engineInterface.registerEvent(entry.getKey(), caseID, 2);
				}
				if(listTmp[3]!=null && listTmp[3].size()>0) {
					engineInterface.registerEvent(entry.getKey(), caseID, 3);
				}
			}
		}
		return time;
	}
    
	public void handleCheckCaseConstraintEvent(YSpecificationID specID, String caseID, String data, boolean precheck, String dirPath) {
		starterSensorSystem(caseID, specID.getUri(), specID.getVersionAsString(), dirPath);
	}
	
	/**
	 * Thread in charged to inizialize a new Case (or to restore)
	 * @author Raffaele Conforti
	 *
	 */
		
	public YSensor[] starterSensorSystem(String caseID, String URI, String Version, String dirPath) {
		
		tableLogResource.put(caseID, new HashMap<String, Object>());
		tableLogVariable.put(caseID, new HashMap<String, Object>());
		
		String intervalTime = createSensors(caseID, URI, Version, dirPath);
				
		LinkedList<YSensor> listSensor = tableSensor.get(caseID);
		for(YSensor sensor : listSensor) {
			
			getDistributionInformation(sensor.getDistributions(), caseID);
			updateLogVariable(sensor.getLogVariables(), caseID);
			updateLogResources(sensor.getLogResources(), caseID);
			
		}
		return listSensor.toArray(new YSensor[0]);
		
	}
	
	public void updateSensorSystem(String caseID) {
						
		LinkedList<YSensor> listSensor = tableSensor.get(caseID);
		for(YSensor sensor : listSensor) {

            getDistributionInformation(sensor.getDistributions(), caseID);
			updateLogVariable(sensor.getLogVariables(), caseID);
			updateLogResources(sensor.getLogResources(), caseID);
			
		}
		
	}
	
	public void updateSensor(YSensor sensor, String caseID) {
		
		HashMap<String,String> tableDistributionLocal;
		HashMap<String,Object> tableLogResourceLocal;
		HashMap<String,Object> tableLogVariableLocal;
			
		tableDistributionLocal = tableDistribution.get(caseID);
		tableLogResourceLocal = tableLogResource.get(caseID);
		tableLogVariableLocal = tableLogVariable.get(caseID);
	 
		YSensorMessageUpdate variableUpdated = new YSensorMessageUpdate();
		
		for(String distribution: sensor.getDistributions()) {
			
			String dis = null;
			if((dis = tableDistributionLocal.get(distribution)) != null) {
				variableUpdated.addDistribution(distribution, dis);
			}
		}
		
		for(String logResource: sensor.getLogResources()) {
			
			Object res = null;
			if((res = tableLogResourceLocal.get(logResource)) != null) {
				variableUpdated.addLogResource(logResource, res);
			}
		}
		
		for(String logVariable: sensor.getLogVariables()) {
			Object result = null;
			if((result = tableLogVariableLocal.get(logVariable)) != null) {								
				if(result instanceof String) {
					String res = (String) result;
					res = YSensorUtilities.checkIfXML(res);
					variableUpdated.addLogVariable(logVariable, res);
				}else {
					List<String> res = (List<String>) result;
					for(int j=0; j<res.size(); j++) {
						String res2 = res.get(j);
						res2 = YSensorUtilities.checkIfXML(res2);
						res.set(j, res2);
					}
					variableUpdated.addLogVariable(logVariable, res);
				}
			}
		}
		
		sensor.updateWithoutNotification(variableUpdated);
		
//		for(YSensor internalSensor : tableSensor.get(sensor.getCaseID())) {
//			if(internalSensor.equals(sensor)) {
//				internalSensor.updateWithoutNotification(variableUpdated);
//			}
//		}
			
	}
	
}
