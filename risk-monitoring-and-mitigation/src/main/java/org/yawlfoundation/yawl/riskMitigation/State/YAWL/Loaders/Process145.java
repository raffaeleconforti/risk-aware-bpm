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

public class Process145 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process5/1 risks/Process145 - 5.yawl");
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

		variables.put("case(current).Modify Purchase Order.POApproval", "5");
		variables.put("case(current).Approve Purchase Order(PassTimeInMillis)", "5");
		variables.put("case(current).Confirm Puchase Order(PassTimeInMillis)", "5");

		mappingName.put("a71", "case(current).Modify Purchase Order.POApproval");
		mappingName.put("b71", "case(current).Approve Purchase Order(PassTimeInMillis)");
		mappingName.put("c71", "case(current).Confirm Puchase Order(PassTimeInMillis)");
		taskWithToken.put("Create_Purchase_Order_104", 5);
		taskWithToken.put("Approve_Purchase_Order_1901", 5);
		taskWithToken.put("Modify_Purchase_Order_2768", 5);
		taskWithToken.put("Confirm_Purchase_Order_2013", 3);
		
		StateYAWLProcess s = new StateYAWLProcess(null, specificationXML, mappingName, r);
		
		Resource ISPO = new Resource("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		ISPO.addOffer("Confirm_Purchase_Order_2013");
		ISPO.addAllocate("Confirm_Purchase_Order_2013");
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
		tasksLog.add("Create_Purchase_Order_104");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Modify_Purchase_Order_2768");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Modify_Purchase_Order_2768");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Modify_Purchase_Order_2768");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Modify_Purchase_Order_2768");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Modify_Purchase_Order_2768");
		tasksLog.add("Approve_Purchase_Order_1901");
		tasksLog.add("Confirm_Purchase_Order_2013");
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
