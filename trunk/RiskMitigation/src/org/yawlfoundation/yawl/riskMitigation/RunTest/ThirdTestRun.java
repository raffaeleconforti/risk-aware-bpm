package org.yawlfoundation.yawl.riskMitigation.RunTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom.Element;
import org.yawlfoundation.yawl.riskMitigation.SimulatedAnnealingEngine.SimulatedAnnealingAlgorithm;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Resource;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.StateYAWLProcess;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ImporterYState;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.LogReader;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Importers.ResourceImporter;
import org.yawlfoundation.yawl.riskMitigation.State.YAWL.Loaders.Loader;
import org.yawlfoundation.yawl.riskMitigation.Temperature.TemperatureCalculator;
import org.yawlfoundation.yawl.riskMitigation.Temperature.Test.TemperatureCalculatorTest;
import org.yawlfoundation.yawl.riskMitigation.results.RiskMitigationResult;
import org.yawlfoundation.yawl.riskPrediction.Annotators.YSensorPredictionUpdater;
import org.yawlfoundation.yawl.sensors.YSensor;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.util.JDOMUtil;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class ThirdTestRun {

	public static void main(String[] args) throws Exception {
		
//		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/TestMitigation.xes");
		
		String specification = null;
		try {
			File f = new File("/home/stormfire/InsuranceClaim.yawl");
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
			File f = new File("/home/stormfire/Insurance.ybkp");
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
		
		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();
		
		StateYAWLProcess s = null;
		
//		Map<String, Object> parameters = InterfaceManager.generateParameters(log, specification, resources);
				
		InterfaceManager im = new InterfaceManager(InterfaceManager.YAWL, null);
		
		HashMap<String, Integer> taskWithToken = new HashMap<String, Integer>();
		LinkedList<String> tasksLog = new LinkedList<String>();
		
		LogReader.createTasks(im, taskWithToken, tasksLog, "396");
		
		String sensorsString = null;
		if((sensorsString = im.getWorkflowDefinitionLayer().getSensors("396", "InsuranceClaim", "1.0")) != null) {
			
			Element sensorElements = JDOMUtil.stringToDocument(sensorsString).getRootElement();
			
			YSensor[] sensors = new YSensor[sensorElements.getChildren().size()];

			YSensorPredictionUpdater su = YSensorPredictionUpdater.getInstance(im);
			
			int pos = 0;
			for(Element sensorElement : (List<Element>) sensorElements.getChildren()) {
				sensors[pos] = new YSensor("396", sensorElement, null); 
				
				su.starterSensorSystem("396", "InsuranceClaim", "1.0");
				su.updateSensor(sensors[pos], "396");
				pos++;
			}
			
			su.updateSensorSystem("396");
			
			HashMap<String, String> mappingNames = sensors[0].getMappingName();
			mappingNames.putAll(sensors[1].getMappingName());
						
			s = new StateYAWLProcess("396", specification, mappingNames, new Random(10));
			
			HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
			ResourceImporter.produceResourcesMap(im, resourceMap, "396");
						
			HashMap<String, Object> variables = sensors[0].getVariables();
			variables.putAll(sensors[1].getVariables());

			System.out.println(variables);
			
			ImporterYState.importModel(im, s, variables, resourceMap, taskWithToken, tasksLog);
			
			System.out.println("ok");
			long st = System.nanoTime();
			LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 1);
			long nt = System.nanoTime();
			String stringOut = "Model: - Time: " +(nt-st)/1 +" -";
			
			StateYAWLProcess[] array = saa.bestSolutions.toArray(new StateYAWLProcess[0]);
			System.out.println(Arrays.toString(array));
			boolean reach = false;
			for(int i = 0; i<array.length; i++){
				if(array[i] != null) {
					StateYAWLProcess s1 = array[i];

					Double[] energy = s1.calculateEnergy();
					
					stringOut += " "+Arrays.toString(energy);
					
					stringOut += s1.modifications;
				}
			}
			stringOut += " - total model generated: "+s.idGeneral+"\n";

			try{
				FileWriter fstream = new FileWriter("FirstTest.txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(stringOut);
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
	
	public List<RiskMitigationResult> discoverMitigations(InterfaceManager im, String caseID, String version, String URI) {
		
		List<RiskMitigationResult> result = new LinkedList<RiskMitigationResult>(); 
		
		SimulatedAnnealingAlgorithm saa = new SimulatedAnnealingAlgorithm();
		TemperatureCalculator tc = new TemperatureCalculatorTest();
		
		StateYAWLProcess s = null;
		
		HashMap<String, Integer> taskWithToken = new HashMap<String, Integer>();
		LinkedList<String> tasksLog = new LinkedList<String>();
		
		LogReader.createTasks(im, taskWithToken, tasksLog, im.getWorkflowDefinitionLayer().getAllID().get(0));
		
		String sensorsString = null;
		
		if((sensorsString = im.getWorkflowDefinitionLayer().getSensors(caseID, URI, version)) != null) {
			
			Element sensorElements = JDOMUtil.stringToDocument(sensorsString).getRootElement();
			
			YSensor[] sensors = new YSensor[sensorElements.getChildren().size()];

			YSensorPredictionUpdater su = YSensorPredictionUpdater.getInstance(im);
			
			int pos = 0;
			for(Element sensorElement : (List<Element>) sensorElements.getChildren()) {
				sensors[pos] = new YSensor(caseID, sensorElement, null); 
				
				su.starterSensorSystem(caseID, URI, version);
				su.updateSensor(sensors[pos], caseID);
				pos++;
			}
			
			su.updateSensorSystem(caseID);
						
			s = new StateYAWLProcess("396", im.getWorkflowDefinitionLayer().getSpecification(URI), sensors[0].getMappingName(), new Random(10));
			
			HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
			ResourceImporter.produceResourcesMap(im, resourceMap, "1");
			
			InterfaceManager imY = new InterfaceManager(InterfaceManager.YAWL, null);
			
			ImporterYState.importModel(imY, s, sensors[0].getVariables(), resourceMap, taskWithToken, tasksLog);
			
//			long st = System.nanoTime();
			LinkedList<StateYAWLProcess> set = saa.simulate(s, 50, 100, tc, 60000, 1);
//			long nt = System.nanoTime();
//			String stringOut = "Model: - Time: " +(nt-st)/1 +" -";
			
			StateYAWLProcess[] array = saa.bestSolutions.toArray(new StateYAWLProcess[0]);
			
			boolean reach = false;
			for(int i = 0; i<array.length && result.size()<5; i++){
				if(array[i] != null) {
					StateYAWLProcess s1 = array[i];

					RiskMitigationResult rmr = new RiskMitigationResult(s1.calculateEnergy(), s1.modifications);

					if(!result.contains(rmr)) {
						result.add(rmr);
					}
				}
			}
			
		}
		
		return result;
	}
	
}
