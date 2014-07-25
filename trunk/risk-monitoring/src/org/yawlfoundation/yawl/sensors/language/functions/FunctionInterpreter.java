package org.yawlfoundation.yawl.sensors.language.functions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.yawlfoundation.yawl.sensors.language.IfThenElseInterpreter;
import org.yawlfoundation.yawl.sensors.language.YExpression;

public class FunctionInterpreter {

	private IfThenElseInterpreter itei = new IfThenElseInterpreter();
	
	public FunctionInterpreter(WorkQueueFacade workQueue) {
		FunctionInitializer.InizializeActions(workQueue);
	}
	
	public Object elaborate(String input, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource) {
		input = analyseCondition(input, mappingName, variables);
		return itei.elaborate(input, mappingName, variables, resource);
	}

	private String analyseCondition(String input, HashMap<String, String> mappingName, HashMap<String, Object> variables) {
    	if(input == null) return "0";
    	StringTokenizer st = new StringTokenizer(input, "(-*+/%^&|!<=>){}[]", true);
    	StringBuffer sb = new StringBuffer();
    	while(st.hasMoreTokens()) {
    		String token = st.nextToken();
    		if(token.contains(YExpression.DOT)) {
    			try {
    				new Double(token);
        			sb.append(token);
    			}catch(NumberFormatException nfe) {
    				
    				String var = token.substring(0, token.indexOf(YExpression.DOT));
    				
    				int functionType = FunctionIdentifier.getFunction(token);
    				String term = FunctionIdentifier.getTerm(token);
    				
    				String res = FunctionExecutor.getFunctionResult(functionType, var, term, mappingName, variables);
    				
    				sb.append(res);
    			}
    		}else {
    			sb.append(token);
    		}
    	}
    	return sb.toString();
    }
    
}
