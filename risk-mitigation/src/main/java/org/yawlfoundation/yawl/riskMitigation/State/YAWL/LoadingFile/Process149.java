package org.yawlfoundation.yawl.riskMitigation.State.YAWL.LoadingFile;

import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Process149 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process5/3 risks/Process149 - 5.yawl");
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

		variables.put("case(current).Approve Purchase Order(Count)", "6");
		variables.put("case(current).Create Purchase Order(CompleteTime)", "1");
		variables.put("case(current).Approve Purchase Order(TimeEstimationInMillis)", "5");
		variables.put("case(current).Approve Purchase Order(isCompleted)", "false");
		variables.put("case(current).Approve Purchase Order(isStarted)", "true");
		variables.put("case(current).Approve Purchase Order(StartTimeInMillis)", "5");

		mappingName.put("a41", "case(current).Approve Purchase Order(Count)");
        mappingName.put("b41", "case(current).Create Purchase Order(CompleteTime)");
        mappingName.put("a42", "case(current).Approve Purchase Order(TimeEstimationInMillis)");
        mappingName.put("a43", "case(current).Approve Purchase Order(isCompleted)");
        mappingName.put("a44", "case(current).Approve Purchase Order(isStarted)");
        mappingName.put("a45", "case(current).Approve Purchase Order(StartTimeInMillis)");

		variables.put("case(current).Modify Purchase Order(PassTimeInMillis)", "1");
		variables.put("case(current).Modify Purchase Order(TimeEstimationInMillis)", "5");

		mappingName.put("a51", "case(current).Modify Purchase Order(PassTimeInMillis)");
		mappingName.put("a52", "case(current).Modify Purchase Order(TimeEstimationInMillis)");

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
		ISPO.addOffer("Fill Out Continuity Daily Report");
		ISPO.addAllocate("Fill Out Continuity Daily Report");
		ISPO.addStart("Fill Out Continuity Daily Report");
		ISPO.addOffer("Fill Out Sound Sheets");
		ISPO.addAllocate("Fill Out Sound Sheets");
		ISPO.addStart("Fill Out Sound Sheets");
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
