package org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders;
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
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;import org.yawlfoundation.yawl.risk.state.YAWL.Resource;

public class Process151 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process6/1 risks/Process151 - 6.yawl");
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

		variables.put("case(current).Approve Shipment Payment Order(Count)", "6");
		variables.put("case(current).Issue Shipment Payment Order(CompleteTime)", "1");
		variables.put("case(current).Approve Shipment Payment Order(TimeEstimationInMillis)", "5");
		variables.put("case(current).Approve Shipment Payment Order(isCompleted)", "false");
		variables.put("case(current).Approve Shipment Payment Order(isStarted)", "true");
		variables.put("case(current).Approve Shipment Payment Order(StartTimeInMillis)", "5");

		mappingName.put("a41", "case(current).Approve Shipment Payment Order(Count)");
        mappingName.put("b41", "case(current).Issue Shipment Payment Order(CompleteTime)");
        mappingName.put("a42", "case(current).Approve Shipment Payment Order(TimeEstimationInMillis)");
        mappingName.put("a43", "case(current).Approve Shipment Payment Order(isCompleted)");
        mappingName.put("a44", "case(current).Approve Shipment Payment Order(isStarted)");
        mappingName.put("a45", "case(current).Approve Shipment Payment Order(StartTimeInMillis)");
		
		taskWithToken.put("null_592", 5);
		taskWithToken.put("Produce_Freight_Invoice_595", 4);
		taskWithToken.put("Issue_Shipment_Invoice_594", 5);
		taskWithToken.put("Issue_Shipment_Payment_Order_602", 5);
		taskWithToken.put("Approve_Shipment_Payment_Order_593", 4);
		
		StateYAWLProcess s = new StateYAWLProcess(null, specificationXML, mappingName, r);
		
		Resource ISPO = new Resource("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		ISPO.addOffer("Produce_Freight_Invoice_595");
		ISPO.addAllocate("Produce_Freight_Invoice_595");
		ISPO.addStart("Produce_Freight_Invoice_595");
		ISPO.addOffer("Approve_Shipment_Payment_Order_593");
		ISPO.addAllocate("Approve_Shipment_Payment_Order_593");
		ISPO.addStart("Approve_Shipment_Payment_Order_593");
		resourcesMap.put(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"), ISPO);
		
		Resource Test1 = new Resource("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		Test1.addOffer("Fill_Out_Camera_Sheets_40");
		Test1.addAllocate("Fill_Out_Camera_Sheets_40");
		Test1.addStart("Fill_Out_Camera_Sheets_40");
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
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
