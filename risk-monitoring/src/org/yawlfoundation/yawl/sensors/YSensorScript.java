package org.yawlfoundation.yawl.sensors;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.jdom.Element;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacadeGataway;
import org.yawlfoundation.yawl.sensors.language.loops.ForInterpreter;
import org.yawlfoundation.yawl.sensors.monitorInterface.YAWL.YAWL_MonitorInterface;
import org.yawlfoundation.yawl.util.JDOMUtil;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 12/09/2013
 *
 */

public class YSensorScript extends YSensor {
	
	String URI = null;
	
	LinkedList<String> nameVariables = new LinkedList<String>();

	public YSensorScript(String caseID, WorkQueueGatewayClient workQueueClient,
			String sensor, String time, String monitorSensorURI,
			String timePredictionURI, String specification, String URI) {
		this.caseID = caseID;
		mi = YAWL_MonitorInterface.getInstance(monitorSensorURI);
//		tpi = YAWL_TimePredictionInterface.getInstance(timePredictionURI);
		startTime = System.currentTimeMillis();
//		wd = WorkflowDefinition.getWorkflowDefinition("org.yawlfoundation.yawl.sensors.layerDB.YAWL.YAWL_WorkflowDefinition");
		_workQueueClient = workQueueClient;
		WorkQueueFacade wqf = new WorkQueueFacadeGataway(workQueueClient, _user, _password);
		itei = new ForInterpreter(wqf);
		createTime(time);
		this.specification = specification;
		this.URI = URI;
//		try {
//			s = YMarshal.unmarshalSpecifications(specification).get(0);
//		} catch (YSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (YSchemaBuildingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		initializeSensor(sensor);
	}

	/**
	 * Inizialize the sensor 
	 * @param string XML with the sensor information (from <sensor> to </sensors>)
	 */
    private void initializeSensor(String string) {
		Element sensor = JDOMUtil.stringToDocument(string).getRootElement();
		name = sensor.getAttributeValue("name");
		riskMessage = "Detect risk by sensor "+name;
		faultMessage = "Detect fault by sensor "+name;
		String tm = sensor.getAttributeValue("notifyTime");
		if(tm!=null) createTime(tm);
		for(Element child : (List<Element>) sensor.getChildren()) {
			if("vars".equals(child.getName())) {
				List<Element> varsChildren = child.getChildren();
				for(Element var : varsChildren) {
					String name = var.getAttributeValue("name");
					String type = var.getAttributeValue("type");
					String mapping = var.getAttributeValue("mapping");
					createVariable(name, type, mapping);
					nameVariables.add(name);
				}
			}else if("risk".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("riskProbability".equals(child1.getName())) {
						riskProbability = buildRiskProbabilityScript(child1.getValue());
					}else if("riskThreshold".equals(child1.getName())) {
						riskThreshold = buildRiskThresholdScript(child1.getValue());
					}else if("riskMessage".equals(child1.getName())) {
						riskMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("fault".equals(child.getName())) {
				for(Element child1 : (List<Element>) child.getChildren()) {
					if("faultCondition".equals(child1.getName())) {
						faultCondition = buildFaultConditionScript(child1.getValue());
					}else if("faultMessage".equals(child1.getName())) {
						faultMessage = YExpression.PARTA+child1.getValue()+YExpression.PARTC;
					}
				}
			}else if("consequence".equals(child.getName())) {
				consequence = buildConsequenceScript(child.getValue());
			}
		}
		orderList();
//		analyseCondition();
//		createFor();
	}
    
    private String buildConsequenceScript(String method) {
		
		String className = "Consequence_"+URI+"_"+name;
				
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			String importCode = "import java.util.*;import java.math.*;public class "+className+" {public static Double execute(Object[] o) {";
			
			StringBuffer vars = new StringBuffer();
			StringBuffer inputPass = new StringBuffer();
			
			String startCode = "private static Double execute2(";
			
			StringBuffer input = new StringBuffer();
			int count = 0;
			for(String name : nameVariables) {
				vars.append("Object "+name+" = o["+count+"];\n");
				inputPass.append("(Object) "+name);
				input.append("Object "+name);
				count++;
				if(count < nameVariables.size()) {
					input.append(", ");
					inputPass.append(", ");
				}
			}
			
			vars.append("return execute2("+inputPass+");}");
			
			String openMethod = ") {";
			
			String closeMethod = "}}";
			
			String sourceCode = importCode+vars+startCode+input+openMethod+method+closeMethod;
			
			compile(className, sourceCode);
		}

		return className;
	}

	private String buildFaultConditionScript(String method) {
		
		String className = "FaultCondition_"+URI+"_"+name;
				
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			String importCode = "import java.util.*;import java.math.*;public class "+className+" {public static Boolean execute(Object[] o) {";
			
			StringBuffer vars = new StringBuffer();
			StringBuffer inputPass = new StringBuffer();
			
			String startCode = "private static Boolean execute2(";
			
			StringBuffer input = new StringBuffer();
			int count = 0;
			for(String name : nameVariables) {
				vars.append("Object "+name+" = o["+count+"];\n");
				inputPass.append("(Object) "+name);
				input.append("Object "+name);
				count++;
				if(count < nameVariables.size()) {
					input.append(", ");
					inputPass.append(", ");
				}
			}
			
			vars.append("return execute2("+inputPass+");}");
			
			String openMethod = ") {";
			
			String closeMethod = "}}";
			
			String sourceCode = importCode+vars+startCode+input+openMethod+method+closeMethod;
			
			compile(className, sourceCode);
		}

		return className;
	}

	private String buildRiskThresholdScript(String method) {
		
		String className = "RiskThreshold_"+URI+"_"+name;
				
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			String importCode = "import java.util.*;import java.math.*;public class "+className+" {public static Double execute(Object[] o) {";
			
			StringBuffer vars = new StringBuffer();
			StringBuffer inputPass = new StringBuffer();
			
			String startCode = "private static Double execute2(";
			
			StringBuffer input = new StringBuffer();
			int count = 0;
			for(String name : nameVariables) {
				vars.append("Object "+name+" = o["+count+"];\n");
				inputPass.append("(Object) "+name);
				input.append("Object "+name);
				count++;
				if(count < nameVariables.size()) {
					input.append(", ");
					inputPass.append(", ");
				}
			}
			
			vars.append("return execute2("+inputPass+");}");
			
			String openMethod = ") {";
			
			String closeMethod = "}}";
			
			String sourceCode = importCode+vars+startCode+input+openMethod+method+closeMethod;
			
			compile(className, sourceCode);
		}

		return className;
	}

	private String buildRiskProbabilityScript(String method) {
		
		String className = "RiskProbability_"+URI+"_"+name;
				
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			String importCode = "import java.util.*;import java.math.*;public class "+className+" {public static Double execute(Object[] o) {";
			
			StringBuffer vars = new StringBuffer();
			StringBuffer inputPass = new StringBuffer();
			
			String startCode = "private static Double execute2(";
			
			StringBuffer input = new StringBuffer();
			int count = 0;
			for(String name : nameVariables) {
				vars.append("Object "+name+" = o["+count+"];\n");
				inputPass.append("(Object) "+name);
				input.append("Object "+name);
				count++;
				if(count < nameVariables.size()) {
					input.append(", ");
					inputPass.append(", ");
				}
			}
			
			vars.append("return execute2("+inputPass+");}");
			
			String openMethod = ") {";
			
			String closeMethod = "}}";
			
			String sourceCode = importCode+vars+startCode+input+openMethod+method+closeMethod;
			
			compile(className, sourceCode);
		}

		return className;
	}
	
	private Double executeRiskProbabilityScript() {
		Object[] o = new Object[nameVariables.size()];
		
		int pos = 0;
		for(String name : nameVariables) {
			o[pos] = variables.get(mappingName.get(name));
			pos++;
		}
		
		try {
			Double res = (Double) Class.forName(riskProbability).getDeclaredMethod("execute", new Class[] { Object[].class}).invoke(null, (Object) o);
			return res;
		}
		catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e);
		}
		catch (NoSuchMethodException e) {
			System.err.println("No such method: " + e);
		}
		catch (IllegalAccessException e) {
			System.err.println("Illegal access: " + e);
		}
		catch (InvocationTargetException e) {
			System.err.println("Invocation target: " + e);
		}
		
		return null;
	}
	
	private Double executeRiskThresholdScript() {
		Object[] o = new Object[nameVariables.size()];
		
		int pos = 0;
		for(String name : nameVariables) {
			o[pos] = variables.get(mappingName.get(name));
			pos++;
		}
		
		try {
			Double res = (Double) Class.forName(riskThreshold).getDeclaredMethod("execute", new Class[] { Object[].class}).invoke(null, (Object) o);
			return res;
		}
		catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e);
		}
		catch (NoSuchMethodException e) {
			System.err.println("No such method: " + e);
		}
		catch (IllegalAccessException e) {
			System.err.println("Illegal access: " + e);
		}
		catch (InvocationTargetException e) {
			System.err.println("Invocation target: " + e);
		}
		
		return null;
	}
	
	private Boolean executeFaultConditionScript() {
		Object[] o = new Object[nameVariables.size()];
		
		int pos = 0;
		for(String name : nameVariables) {
			o[pos] = variables.get(mappingName.get(name));
			pos++;
		}
		
		try {
			Boolean res = (Boolean) Class.forName(faultCondition).getDeclaredMethod("execute", new Class[] { Object[].class}).invoke(null, (Object) o);
			return res;
		}
		catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e);
		}
		catch (NoSuchMethodException e) {
			System.err.println("No such method: " + e);
		}
		catch (IllegalAccessException e) {
			System.err.println("Illegal access: " + e);
		}
		catch (InvocationTargetException e) {
			System.err.println("Invocation target: " + e.getCause());
		}
		
//		return FaultCondition_null_FollowUp.execute(o);
		
		return null;
	}
	
	private Double executeConsequenceScript() {
		Object[] o = new Object[nameVariables.size()];
		
		int pos = 0;
		for(String name : nameVariables) {
			o[pos] = variables.get(mappingName.get(name));
			pos++;
		}
		
		try {
			Double res = (Double) Class.forName(consequence).getDeclaredMethod("execute", new Class[] { Object[].class}).invoke(null, (Object) o);
			return res;
		}
		catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e);
		}
		catch (NoSuchMethodException e) {
			System.err.println("No such method: " + e);
		}
		catch (IllegalAccessException e) {
			System.err.println("Illegal access: " + e);
		}
		catch (InvocationTargetException e) {
			System.err.println("Invocation target: " + e);
		}
		
		return null;
	}
	
	/**
	 * Evaluate the sensingCondition
	 * @return null if is not possible to evaluate
	 */
	@Override
    protected void evaluate() {
    	Double riskProbabilityValue = null;
    	Double riskThresholdValue = null;
    	
    	Boolean riskConditionValue = null;
    	
    	if(riskProbability != null) {
    		riskProbabilityValue = executeRiskProbabilityScript();
	    	riskThresholdValue = executeRiskThresholdScript();
	    	
	    	riskConditionValue = riskProbabilityValue > riskThresholdValue;
    	}
    	
    	Boolean faultConditionValue = null;
    	Double consequenceValue = null;
    			
    	if(faultCondition != null) {
	    	faultConditionValue = executeFaultConditionScript();
	    	consequenceValue = executeConsequenceScript();
    	}

    	try {
    		
    		if(previousNotification) cancelNotification();
    		
			if(faultConditionValue != null && faultConditionValue) {
				notifyAdmin("fault", getMessage(faultMessage), "", null, 1, consequenceValue);//Fault
				previousNotification = true;
			} else if(riskConditionValue != null && riskConditionValue) {
				notifyAdmin("risk", getMessage(riskMessage), "", "", riskProbabilityValue, consequenceValue);//Risk
				previousNotification = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @Override
	public Double[] evaluateForPrediction() {
    	    	
    	Boolean faultConditionValue = executeFaultConditionScript();

    	Double consequenceValue = executeConsequenceScript();
    	    	
    	if(faultConditionValue != null) {
    		return new Double[] {(faultConditionValue?1.0:0.0), consequenceValue};
    	}
		
    	return new Double[] {0.0, 0.0};
    	
	}

	private void compile(String className, String sourceCode) {
		
		System.out.println(sourceCode);
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(sourceCode);
		out.close();
		JavaFileObject file = new JavaSourceFromString(className, writer.toString());

		Iterable<String> options = Arrays.asList( new String[] { "-d", "/home/stormfire/Dropbox/workspace/RiskInformedExecution/bin/"} );
		Iterable <? extends JavaFileObject > compilationUnits = Arrays.asList(file);
		CompilationTask task = compiler.getTask(null, null, diagnostics, options, null, compilationUnits);
		
		boolean success = task.call();
		System.out.println(className + " " + success);

	}
	   
}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}