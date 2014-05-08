package org.yawlfoundation.yawl.riskMitigation.State.YAWL.LoadingFile;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class Payment implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Payment.yawl");
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
			specificationXML = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		HashMap<String, String> mappingName = new HashMap<String, String>();
		HashMap<String, Integer> taskWithToken = new HashMap<String, Integer>();
		HashMap<String, Resource> resourcesMap = new HashMap<String, Resource>();
		
		//RISK1
		variables.put("case(current).Issue Shipment Invoice(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Issue Shipment Payment Order(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Issue Shipment Remittance Advice(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Produce Freight Invoice(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Approve Shipment Payment Order(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Update Shipment Payment Order(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Process Freight Payment(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Process Shipment Payment(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Issue Debit Adjustment(TimeEstimationInMillis)", "10000000");
		variables.put("case(current).Issue Debit Adjustment(TimeEstimationInMillis)", "10000000");
		
		mappingName.put("t1", "case(current).Issue Shipment Invoice(TimeEstimationInMillis)");
		mappingName.put("t2", "case(current).Issue Shipment Payment Order(TimeEstimationInMillis)");
		mappingName.put("t3", "case(current).Issue Shipment Remittance Advice(TimeEstimationInMillis)");
		mappingName.put("t4", "case(current).Produce Freight Invoice(TimeEstimationInMillis)");
		mappingName.put("t5", "case(current).Approve Shipment Payment Order(TimeEstimationInMillis)");
		mappingName.put("t6", "case(current).Update Shipment Payment Order(TimeEstimationInMillis)");
		mappingName.put("t7", "case(current).Process Freight Payment(TimeEstimationInMillis)");
		mappingName.put("t8", "case(current).Process Shipment Payment(TimeEstimationInMillis)");
		mappingName.put("t9", "case(current).Issue Debit Adjustment(TimeEstimationInMillis)");
		mappingName.put("t10", "case(current).Issue Debit Adjustment(TimeEstimationInMillis)");
		
		variables.put("d", "5");
		variables.put("case(current).Payment(PassTimeInMillis)", "100000000");
		variables.put("case(current).(TimeEstimationInMillis)", "1000000000");

		mappingName.put("d", "d");
		mappingName.put("Tc", "case(current).Payment(PassTimeInMillis)");
		mappingName.put("Te", "case(current).(TimeEstimationInMillis)");
		
		//RISK2
		variables.put("case(current).Approve Shipment Payment Order(isAllocated)", "true");
		variables.put("case(current).Approve Shipment Payment Order(allocateResource)", "<A><allocate><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");
		variables.put("case(current).Issue Shipment Invoice.ShipmentInvoice.Company", "a");
		variables.put("case(current).Approve Shipment Payment Order(OfferTimeInMillis)", "100");
		variables.put("case(Approve Shipment Payment Order(completeResource)=sfo1 AND Issue Shipment Invoice.ShipmentInvoice.Company=c AND Approve Shipment Payment Order(CompleteTimeInMillis)>(ASPOa-(5*24*60*60*1000)) AND (ID)!=[IDCurr]).Approve Shipment Payment Order(CountElements)", "5");
		variables.put("case(Issue Shipment Invoice.ShipmentInvoice.Company=c AND Approve Shipment Payment Order(isCompleted)=\"true\" AND Approve Shipment Payment Order(CompleteTimeInMillis)>(ASPOa-(5*24*60*60*1000)) AND (ID)!=[IDCurr]).Approve Shipment Payment Order(completeResource)", "<A><complete><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></complete></A>");
		variables.put("case(current).Approve Shipment Payment Order(offerDistribution)", "<A><offer><participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></offer></A>");
		
		variables.put("sfo2_13", "0");
		mappingName.put("sfo2_13", "sfo2_13");
		variables.put("sfo_sfo2_14", "2");
		mappingName.put("sfo_sfo2_14", "sfo_sfo2_14");

		mappingName.put("sfoA", "case(current).Approve Shipment Payment Order(isAllocated)");
		mappingName.put("sfo1", "case(current).Approve Shipment Payment Order(allocateResource)");
		mappingName.put("c", "case(current).Issue Shipment Invoice.ShipmentInvoice.Company");
		mappingName.put("ASPOa", "case(current).Approve Shipment Payment Order(OfferTimeInMillis)");
		mappingName.put("ASPOn", "case(Approve Shipment Payment Order(completeResource)=sfo1 AND Issue Shipment Invoice.ShipmentInvoice.Company=c AND Approve Shipment Payment Order(CompleteTimeInMillis)>(ASPOa-(5*24*60*60*1000)) AND (ID)!=[IDCurr]).Approve Shipment Payment Order(CountElements)");
		mappingName.put("sfo2", "case(Issue Shipment Invoice.ShipmentInvoice.Company=c AND Approve Shipment Payment Order(isCompleted)=\"true\" AND Approve Shipment Payment Order(CompleteTimeInMillis)>(ASPOa-(5*24*60*60*1000)) AND (ID)!=[IDCurr]).Approve Shipment Payment Order(completeResource)");
		mappingName.put("sfo", "case(current).Approve Shipment Payment Order(offerDistribution)");
		
		//RISK3
		variables.put("case(current).Update Shipment Payment Order(Count)", "3");
		variables.put("case(Update Shipment Payment Order(Count)>=5).Update Shipment Payment Order(CountElements)", "8");
		variables.put("case(Update Shipment Payment Order(Count)>=USPOuc AND Process Shipment Payment(isOffered)=\"true\").Update Shipment Payment Order(CountElements)", "10");
		
		mappingName.put("USPOuc", "case(current).Update Shipment Payment Order(Count)");
		mappingName.put("USPOu5", "case(Update Shipment Payment Order(Count)>=5).Update Shipment Payment Order(CountElements)");
		mappingName.put("USPOus", "case(Update Shipment Payment Order(Count)>=USPOuc AND Process Shipment Payment(isOffered)=\"true\").Update Shipment Payment Order(CountElements)");
		
		//RISK4
//		variables.put("case(current).Issue Debit Adjustment(StartTimeInMillis)", "10");
//		variables.put("case(Issue Shipment Invoice.ShipmentInvoice.Company=c AND Issue Debit Adjustment(Count)>0 AND Issue Debit Adjustment(CompleteTimeInMillis)>(IDAst-(5*24*60*60*1000))).Issue Debit Adjustment(CountElements)", "3");
//		variables.put("GroupingElement", "Issue Shipment Invoice.ShipmentInvoice.Company");
//		variables.put("WindowElement", "Issue Debit Adjustment(CompleteTimeInMillis)");
//		variables.put("Threshold", "0.6");
//		variables.put("case(Issue Debit Adjustment(Count)>0 AND (ID)!=[IDCurr]).Issue Debit Adjustment(FraudProbabilityFunc, IDAissue, 3, GroupingElement, WindowElement, (5*24*60*60*1000))", "0.9");
//		
//		mappingName.put("IDAst", "case(current).Issue Debit Adjustment(StartTimeInMillis)");
//		mappingName.put("IDAissue", "case(Issue Shipment Invoice.ShipmentInvoice.Company=c AND Issue Debit Adjustment(Count)>0 AND Issue Debit Adjustment(CompleteTimeInMillis)>(IDAst-(5*24*60*60*1000))).Issue Debit Adjustment(CountElements)");
//		mappingName.put("GroupingElement", "GroupingElement");
//		mappingName.put("WindowElement", "WindowElement");
//		mappingName.put("Threshold", "Threshold");
//		mappingName.put("Probability", "case(Issue Debit Adjustment(Count)>0 AND (ID)!=[IDCurr]).Issue Debit Adjustment(FraudProbabilityFunc, IDAissue, 3, GroupingElement, WindowElement, (5*24*60*60*1000))");
		
//		taskWithToken.put("null_592", 5);
//		taskWithToken.put("Produce_Freight_Invoice_595", 4);
		taskWithToken.put("Issue_Shipment_Invoice_594", 5);
		taskWithToken.put("Issue_Shipment_Payment_Order_602", 5);
		taskWithToken.put("Approve_Shipment_Payment_Order_593", 5);
		taskWithToken.put("Update_Shipment_Payment_Order_604", 5);
		taskWithToken.put("Approve_Shipment_Payment_Order_593", 5);
		taskWithToken.put("Update_Shipment_Payment_Order_604", 5);
		taskWithToken.put("Approve_Shipment_Payment_Order_593", 5);
		taskWithToken.put("Update_Shipment_Payment_Order_604", 5);
		taskWithToken.put("Approve_Shipment_Payment_Order_593", 3);
//		taskWithToken.put("Process_Shipment_Payment_603", 5);
//		taskWithToken.put("Issue_Debit_Adjustment_605", 5);
////		taskWithToken.put("Issue_Credit_Adjustment_606", 5);
//		taskWithToken.put("Process_Shipment_Payment_603", 3);
				
		StateYAWLProcess s = new StateYAWLProcess("100", specificationXML, mappingName, r);
		
		Resource ISPO = new Resource("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//		ISPO.addOffer("Fill Out Continuity Daily Report");
//		ISPO.addAllocate("Fill Out Continuity Daily Report");
//		ISPO.addStart("Fill Out Continuity Daily Report");
//		ISPO.addOffer("Fill Out Sound Sheets");
//		ISPO.addAllocate("Fill Out Sound Sheets");
//		ISPO.addStart("Fill Out Sound Sheets");
//		ISPO.addOffer("Produce Freight Invoice");
//		ISPO.addAllocate("Produce Freight Invoice");
//		ISPO.addStart("Produce Freight Invoice");
		resourcesMap.put(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"), ISPO);
		
		Resource Test1 = new Resource("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		ISPO.addOffer("Fill Out Camera Sheets");
		ISPO.addAllocate("Fill Out Camera Sheets");
		resourcesMap.put(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"), Test1);
		
		Resource Test2 = new Resource("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		resourcesMap.put(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"), Test2);
		
		Resource Test3 = new Resource("<participant id=\"PA-7933b9de-5aa6-4d9c-be12-d1a5cc8a8c67\"><userid>dvc</userid><firstname>Don Vito</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		resourcesMap.put(JDOMUtil.formatXMLString("<participant id=\"PA-7933b9de-5aa6-4d9c-be12-d1a5cc8a8c67\"><userid>dvc</userid><firstname>Don Vito</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"), Test3);
		
		List<String> tasksLog = new LinkedList<String>();
		tasksLog.add("null_592");
		tasksLog.add("Produce_Freight_Invoice_595");
		tasksLog.add("Issue_Shipment_Invoice_594");
		tasksLog.add("Issue_Shipment_Payment_Order_602");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
		tasksLog.add("Update_Shipment_Payment_Order_604");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
		tasksLog.add("Update_Shipment_Payment_Order_604");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
		tasksLog.add("Update_Shipment_Payment_Order_604");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
		tasksLog.add("Update_Shipment_Payment_Order_604");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
		tasksLog.add("Update_Shipment_Payment_Order_604");
		tasksLog.add("Approve_Shipment_Payment_Order_593");
//		tasksLog.add("Process_Shipment_Payment_603");
//		tasksLog.add("Issue_Debit_Adjustment_605");
////		tasksLog.add("Issue_Credit_Adjustment_606");
//		tasksLog.add("Process_Shipment_Payment_603");
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
