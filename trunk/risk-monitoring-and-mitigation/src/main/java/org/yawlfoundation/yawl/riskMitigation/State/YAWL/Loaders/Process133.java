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

public class Process133 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process4/2 risks/Process133 - 4.yawl");
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

		variables.put("case(current).Issue Trackpoint Notice.AcceptanceCertificate", "5");
		variables.put("case(current).Initiate Shipment Status Inquiry.TrackpointNotice", "6");
		variables.put("case(current).Log Trackpoint Order Entry(allocateResource)", "<A><allocate><participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");
		variables.put("case(current).Create Acceptance Certificate(allocateResource)", "<A><allocate><participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");

		mappingName.put("a61", "case(current).Issue Trackpoint Notice.AcceptanceCertificate");
		mappingName.put("b61", "case(current).Initiate Shipment Status Inquiry.TrackpointNotice");
		mappingName.put("c61", "case(current).Log Trackpoint Order Entry(allocateResource)");
		mappingName.put("d61", "case(current).Create Acceptance Certificate(allocateResource)");
		
		taskWithToken.put("null_815", 5);
		taskWithToken.put("Initiate_Shipment_Status_Inquiry_929", 4);
		taskWithToken.put("Issue_Trackpoint_Notice_813", 4);
		
		StateYAWLProcess s = new StateYAWLProcess(null, specificationXML, mappingName, r);
		
		Resource ISPO = new Resource("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
		ISPO.addOffer("Initiate_Shipment_Status_Inquiry_929");
		ISPO.addAllocate("Initiate_Shipment_Status_Inquiry_929");
		ISPO.addStart("Initiate_Shipment_Status_Inquiry_929");
		ISPO.addOffer("Issue_Trackpoint_Notice_813");
		ISPO.addAllocate("Issue_Trackpoint_Notice_813");
		ISPO.addStart("Issue_Trackpoint_Notice_813");
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
		tasksLog.add("null_815");
		tasksLog.add("Issue_Trackpoint_Notice_813");
		tasksLog.add("Initiate_Shipment_Status_Inquiry_929");
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
