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

import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class Process101 implements Loader{
	public StateYAWLProcess generateStatus(Random r) {
		String specificationXML = null;
		try {
			File f = new File("Process2/5 risks/Process101 - 2.yawl");
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

		variables.put("case(current).Fill Out Continuity Daily Report(StartTimeInMillis)", "10");
		variables.put("case(current).Fill Out Continuity Daily Report(TimeEstimationInMillis)", "20");
		variables.put("case(current).Fill Out Sound Sheets(StartTimeInMillis)", "15");
		variables.put("case(current).Fill Out Sound Sheets(TimeEstimationInMillis)", "30");
		variables.put("case(current).Fill Out Camera Sheets(isStarted)", "true");
		variables.put("case(current).Fill Out Camera Sheets(isCompleted)", "false");
		variables.put("case(current).Fill Out AD Report(isStarted)", "true");
		variables.put("case(current).Fill Out AD Report(isCompleted)", "false");

				mappingName.put("a11", "case(current).Fill Out Continuity Daily Report(StartTimeInMillis)");
		mappingName.put("a12", "case(current).Fill Out Continuity Daily Report(TimeEstimationInMillis)");
		mappingName.put("b11", "case(current).Fill Out Sound Sheets(StartTimeInMillis)");
		mappingName.put("b12", "case(current).Fill Out Sound Sheets(TimeEstimationInMillis)");
		mappingName.put("c11", "case(current).Fill Out Camera Sheets(isStarted)");
		mappingName.put("c12", "case(current).Fill Out Camera Sheets(isCompleted)");
		mappingName.put("d11", "case(current).Fill Out AD Report(isStarted)");
		mappingName.put("d12", "case(current).Fill Out AD Report(isCompleted)");

		variables.put("case(current).Fill Out Continuity Daily Report(allocateResource)", "<A><allocate><participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");
		variables.put("case(current).Fill Out Sound Sheets(allocateResource)", "<A><allocate><participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>");

		mappingName.put("a21", "case(current).Fill Out Continuity Daily Report(allocateResource)");
		mappingName.put("b21", "case(current).Fill Out Sound Sheets(allocateResource)");

		variables.put("case(current).Revise Shooting Schedule(Count)", "6");
		variables.put("case(current).Input Shooting Schedule(CompleteTimeInMillis)", "10");
		variables.put("case(current).Revise Shooting Schedule(TimeEstimationInMillis)", "10");
		variables.put("case(current).Revise Shooting Schedule(isCompleted)", "false");
		variables.put("case(current).Revise Shooting Schedule(isStarted)", "true");
		variables.put("case(current).Revise Shooting Schedule(StartTimeInMillis)", "30");

		mappingName.put("a41", "case(current).Revise Shooting Schedule(Count)");
		mappingName.put("b41", "case(current).Input Shooting Schedule(CompleteTimeInMillis)");
		mappingName.put("a42", "case(current).Revise Shooting Schedule(TimeEstimationInMillis)");
		mappingName.put("a43", "case(current).Revise Shooting Schedule(isCompleted)");
		mappingName.put("a44", "case(current).Revise Shooting Schedule(isStarted)");
		mappingName.put("a45", "case(current).Revise Shooting Schedule(StartTimeInMillis)");

		variables.put("case(current).Revise Shooting Schedule(PassTimeInMillis)", "6");
		variables.put("case(current).Revise Shooting Schedule(TimeEstimationInMillis)", "10");

		mappingName.put("a51", "case(current).Revise Shooting Schedule(PassTimeInMillis)");
		mappingName.put("a52", "case(current).Revise Shooting Schedule(TimeEstimationInMillis)");

		variables.put("case(current).Fill Out Continuity Report.producer", "1");
		variables.put("case(current).Fill Out Sound Sheets.production", "1");
		variables.put("case(current).Fill Out Camera Sheets.production", "5");
		variables.put("case(current).Fill Out AD Report.production", "5");

		mappingName.put("a81", "case(current).Fill Out Continuity Report.producer");
		mappingName.put("b81", "case(current).Fill Out Sound Sheets.production");
		mappingName.put("c81", "case(current).Fill Out Camera Sheets.production");
		mappingName.put("d81", "case(current).Fill Out AD Report.production");
		
		taskWithToken.put("Create_Call_Sheet_7902", 2);
		taskWithToken.put("Fill_Out_Sound_Sheets_39", 4);
		taskWithToken.put("Fill_Out_AD_Report_41", 4);
		taskWithToken.put("Fill_Out_Camera_Sheets_40", 4);
		taskWithToken.put("Fill_Out_Continuity_Daily_Report_3283", 4);
		
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
		tasksLog.add("Welcome_to_Start_Process_3258");
		tasksLog.add("Input_Location_Notes_3256");
		tasksLog.add("Input_Cast_List_5189");
		tasksLog.add("Input_Crew_List_3257");
		tasksLog.add("Input_Shooting_Schedule_3255");
		tasksLog.add("null_3253");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Revise_Shooting_Schedule_3282");
		tasksLog.add("Create_Call_Sheet_7902");
		tasksLog.add("Update_Call_Sheet_4740");
		tasksLog.add("Distribute_Call_Sheet_64");
		tasksLog.add("null_3279");
		tasksLog.add("null_3280");
		tasksLog.add("Start_Another_Shoot_Day_3268");
		tasksLog.add("Fill_Out_Continuity_Report_36");
		tasksLog.add("Fill_Out_Sound_Sheets_39");
		tasksLog.add("Fill_Out_AD_Report_41");
		tasksLog.add("Fill_Out_Camera_Sheets_40");
		tasksLog.add("Fill_Out_Continuity_Daily_Report_3283");
		tasksLog.add("null_3253");
		tasksLog.add("Create_Call_Sheet_7902");
		
		ImporterYState.importModel(s, variables, resourcesMap, taskWithToken, tasksLog);
		
		return s;
	}
}
