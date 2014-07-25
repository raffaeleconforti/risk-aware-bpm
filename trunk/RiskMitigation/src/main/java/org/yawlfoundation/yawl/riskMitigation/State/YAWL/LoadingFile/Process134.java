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


public class Process134 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process4/2 risks/Process134 - 4.yawl");
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


		variables.put("case(current).Initiate Shipment Status Inquiry(allocateResource)", "<A><allocate><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");
		variables.put("case(current).Issue Trackpoint Notice(allocateResource)", "<A><allocate><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");

		mappingName.put("a21", "case(current).Initiate Shipment Status Inquiry(allocateResource)");
		mappingName.put("b21", "case(current).Issue Trackpoint Notice(allocateResource)");

		variables.put("case(current).Initiate Shipment Status Inquiry.Report", "5");
		variables.put("case(current).Issue Trackpoint Notice(PassTimeInMillis)", "5");
		variables.put("case(current).Log Trackpoint Order Entry(PassTimeInMillis)", "5");

		mappingName.put("a71", "case(current).Initiate Shipment Status Inquiry.Report");
		mappingName.put("b71", "case(current).Issue Trackpoint Notice(PassTimeInMillis)");
		mappingName.put("c71", "case(current).Log Trackpoint Order Entry(PassTimeInMillis)");
		
		taskWithToken.put("null_815", 5);
		taskWithToken.put("Initiate_Shipment_Status_Inquiry_929", 4);
		taskWithToken.put("Issue_Trackpoint_Notice_813", 4);
		
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
		tasksLog.add("null_815");
		tasksLog.add("Issue_Trackpoint_Notice_813");
		tasksLog.add("Initiate_Shipment_Status_Inquiry_929");
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
