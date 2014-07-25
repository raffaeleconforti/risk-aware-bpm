package org.yawlfoundation.yawl.riskMitigation.State.YAWL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.jdom.Element;
import org.yawlfoundation.yawl.risk.state.State;
import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Net;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Place;
import org.yawlfoundation.yawl.risk.state.YAWL.Elements.Task;
import org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph.GraphCreator;
import org.yawlfoundation.yawl.risk.state.YAWL.ExecutionGraph.NodeGraph;
import org.yawlfoundation.yawl.sensors.YSensorManagerImplLayer;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.sensors.language.loops.ForInterpreter;
import org.yawlfoundation.yawl.util.JDOMUtil;



public class StateYAWLProcess extends YSensorManagerImplLayer implements State {
	
	public static final String colomSpaceString = ": ";
	public static final String commaString = ",";
	
	public static final String offeredString = "offered";
	public static final String allocatedString = "allocated";
	public static final String startedString = "started";
	
	public static final String offerString = "offer";
	public static final String allocateString = "allocate";
	public static final String startString = "start";
	public static final String completeString = "complete";
	
	public static final String xmlID = "id";

	public static HashMap<String, HashSet<String>> cache = new HashMap<String, HashSet<String>>();
	public static HashMap<String, LinkedList<String>> cacheValue = new HashMap<String, LinkedList<String>>();
	
	public static int idGeneral = 0;
	
	public static HashMap<String, LinkedList<String>> resourceRiskInvolvement = null;
	
	private static GraphCreator gc = GraphCreator.getInstance();
	
	public LinkedList<String> modifications = new LinkedList<String>();
	public int fathedID = 0;
	public int id = 0;
	public int changing = 0;
	
	private WorkQueueFacadeMap wqf = null;
	private ForInterpreter fi = null;
	private boolean skipneed = false;
	private String caseID = null;
	
	private String specificationXML = null;

	private HashMap<String, String> mappingName = null;
	private HashMap<String, Object> variablesOriginal = null;
	private HashMap<String, Object> variables = null;
	
	public String getCaseID() {
		return caseID;
	}

	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}

	public HashSet<String> getModified() {
		return modified;
	}

	public void setModified(HashSet<String> modified) {
		this.modified = modified;
	}

	public HashMap<String, String> getMappingName() {
		return mappingName;
	}

	public void setMappingName(HashMap<String, String> mappingName) {
		this.mappingName = mappingName;
	}

	public boolean isSkipneed() {
		return skipneed;
	}

	public void setSkipneed(boolean skipneed) {
		this.skipneed = skipneed;
	}

	public NodeGraph getNg() {
		return ng;
	}

	public void setNg(NodeGraph ng) {
		this.ng = ng;
	}

	public HashMap<String, String> getCurrent() {
		return current;
	}

	public void setCurrent(HashMap<String, String> current) {
		this.current = current;
	}

	private HashMap<String, Resource> resourcesMap = null;
	
	private Net rootNet = null;
	private String root = null;
	private Net[] nets = null;
	private Task[] tasks = null;
	private Place[] places = null;
	private boolean[] removed = null;

	private String[] riskProbability = null;
	private String[] faultCondition = null;
//	private String[] postRiskProbability = null;
	private String[] riskThreshold = null;
//	private final String[] postRiskThreshold = null;
	private String[] consequence = null;
//	private final String[] postConsequence = null;
	
	private HashMap<String, String> current = new HashMap<String, String>();
	private HashSet<String> resourceTask = new HashSet<String>();
	private HashMap<String, LinkedList<Object>> forNameVar = new HashMap<String, LinkedList<Object>>();
	private LinkedList<String> resource = new LinkedList<String>();
	private HashMap<String, LinkedList<Object[]>[]> dataFlows = new HashMap<String, LinkedList<Object[]>[]>(); 
	private HashMap<Integer, LinkedList<Integer>> dataFlow = new HashMap<Integer, LinkedList<Integer>>();
	private HashSet<String> taskRelevant = new HashSet<String>();
	private int[] numberTaskRelevant = null;
	
	private HashSet<String> modified = new HashSet<String>();
	private LinkedList<NodeGraph> pending = null;
	private String[] namePending = null;
	private HashSet<String> notSkippable = new HashSet<String>();
	private NodeGraph ng = null;
	private int tasksSize = 0;
	private boolean[] operations = null; 
	
	public Random r = null;
	
	public final int numOperation = 11;	
	
	public final int ExecuteLimit = Task.Unoffered;
	
	public StateYAWLProcess(String caseID, String specXML, HashMap<String, String> map, Random random) {
		this.caseID = caseID;
		
		this.wqf = new WorkQueueFacadeMap(taskRelevant, resourcesMap);
		
		this.fi = new ForInterpreter(wqf);
		
		idGeneral++;
		id = idGeneral;
		specificationXML = specXML;
		mappingName = map;
		r = random;

//		notSkippable.add("Approve_Or_Decline_6");
//		notSkippable.add("Pay_9");
//		notSkippable.add("Reject_10");
//		notSkippable.add("Conduct_File_Review_4");
//		notSkippable.add("Open_Claim_3");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public StateYAWLProcess clone() {
		StateYAWLProcess clone = new StateYAWLProcess(caseID, specificationXML, mappingName, r);
		clone.wqf = new WorkQueueFacadeMap(taskRelevant, resourcesMap);
		clone.skipneed = skipneed;
		clone.fathedID = id;
		clone.caseID = caseID;
		clone.modifications = (LinkedList<String>) modifications.clone();
		clone.variablesOriginal = variablesOriginal;
		clone.variables = new HashMap<String, Object>();
		for(Entry<String, Object> entry : variables.entrySet()) {
			Object value = entry.getValue();
			if(value instanceof LinkedList) {
				LinkedList<String> list = (LinkedList<String>) value;
				clone.variables.put(entry.getKey(), list.clone());
			}else {
				clone.variables.put(entry.getKey(), value);
			}
		}
		clone.notSkippable = notSkippable;
		clone.changing = changing;
		clone.rootNet = rootNet;
		clone.nets = nets;
		
		clone.tasks = new Task[tasks.length];
		for(int i=0; i<tasks.length; i++) {
			clone.tasks[i] = tasks[i].clone();
		}
		
		clone.places = places;

//		clone.modified = (HashSet<String>) modified.clone();
		clone.modified = new HashSet<String>();
		clone.removed = removed.clone();
		
		clone.tasksSize = tasksSize;
		
		clone.riskProbability = riskProbability;
		clone.riskThreshold = riskThreshold;
		clone.consequence = consequence;
		clone.faultCondition = faultCondition;
		
		clone.current = current;
		clone.resourceTask = resourceTask;
		clone.forNameVar = forNameVar;
		clone.resource = resource;
		clone.dataFlows = dataFlows; 
		clone.dataFlow = dataFlow;

		clone.taskRelevant = taskRelevant;
		clone.numberTaskRelevant = numberTaskRelevant;
		
		clone.resourcesMap = new HashMap<String, Resource>();
		for(Entry<String, Resource> entry : resourcesMap.entrySet()) {
			Object value = entry.getValue();
			if(value instanceof Resource) {
				Resource res = (Resource) value;
				clone.resourcesMap.put(entry.getKey(), (Resource) res.clone());
			}
		}
		
		clone.ng = (NodeGraph) ng.clone();
		clone.pending = gc.discoverLast(clone.ng);

		clone.namePending = new String[clone.pending.size()];
		NodeGraph ng1 = null;
		for(int i=0; i<clone.pending.size(); i++) {
			ng1 = clone.pending.get(i);
			clone.namePending[i] = ng1.nodeName;
		}
		Arrays.sort(clone.namePending);
		return clone;
	}	
	
	public void generateListTaskRelevant() {
		numberTaskRelevant = new int[taskRelevant.size()];
		int pos = 0;
		for(int i=0; i<tasks.length; i++) {
			Task t = tasks[i];
			if(taskRelevant.contains(t.taskName)) {
				numberTaskRelevant[pos] = i;
				pos++;
			}
		}
	}
	
	@Override
	public Double[] calculateEnergy() {
		Double[] energy = new Double[riskProbability.length*4+1];
		for(int i=0; i<riskProbability.length; i++) {
			
//			updateResourceTask();
//			updateForNameVar();
			
			generateListTaskRelevant();
			Double[] tmp = evaluate(i);
			
			/* Likelihood */
			energy[i*3] = (tmp[0] != null) ? tmp[0] : null;
			/* Difference Likelihood Threshold */
			energy[i*3+1] = (tmp[0] != null && tmp[1] != null) ? tmp[1]-tmp[0] : null;
			/* FaultCondition */
			energy[i*3+2] = (tmp[2] != null) ? tmp[2] : null;
			/* Consequence */
			energy[i*3+3] = (tmp[3] != null) ? tmp[3] : null;
			
		}
		energy[energy.length-1] = (double) changing;
		return energy;
	}
	
	@SuppressWarnings("unchecked")
	public void initializeSensor(String string) {
		if(string==null) return;
		
		Element sensors = JDOMUtil.stringToDocument(string).getRootElement();
		int size = sensors.getChildren().size();

		riskProbability = new String[size];
		riskThreshold = new String[size];
		faultCondition = new String[size];
		consequence = new String[size];
		
		int currentPos = 0;
		for(Element sensor : (List<Element>) sensors.getChildren()) {
			for(Element child : (List<Element>) sensor.getChildren()) {
				if("vars".equals(child.getName())) {
					List<Element> varsChildren = child.getChildren();
					for(Element var : varsChildren) {
						String nameVar = var.getAttributeValue("name");
						String type = var.getAttributeValue("type");
						String mapping = var.getAttributeValue("mapping");
						createVariable(nameVar, type, mapping);
					}	
				}else if("risk".equals(child.getName())) {
					List<Element> varsChildren = child.getChildren();
					for(Element child2 : varsChildren) {
						if("riskProbability".equals(child2.getName())) {
							if(!child2.getValue().isEmpty()) riskProbability[currentPos] = YExpression.PARTA+child2.getValue()+YExpression.PARTC;
							else riskProbability[currentPos] = ""+0;
						}else if("riskThreshold".equals(child2.getName())) {
							if(!child2.getValue().isEmpty()) riskThreshold[currentPos] = YExpression.PARTA+child2.getValue()+YExpression.PARTC;
							else riskThreshold[currentPos] = ""+1;
						}
					}
				}else if("fault".equals(child.getName())) {
					if(!child.getValue().isEmpty()) {
						List<Element> varsChildren2 = child.getChildren();
						for(Element child3 : varsChildren2) {
							if("faultCondition".equals(child3.getName())) {
								faultCondition[currentPos] = YExpression.PARTA+child3.getValue()+YExpression.PARTC;
							}
						}
					}
					else faultCondition[currentPos] = ""+0;
				}else if("consequence".equals(child.getName())) {
					if(!child.getValue().isEmpty()) consequence[currentPos] = YExpression.PARTA+child.getValue()+YExpression.PARTC;
					else consequence[currentPos] = ""+1;
				}
			}
			
			if(riskProbability[currentPos] == null) riskProbability[currentPos] = ""+1;
			if(faultCondition[currentPos] == null) faultCondition[currentPos] = ""+1;
			if(riskThreshold[currentPos] == null) riskThreshold[currentPos] = ""+1;
			if(consequence[currentPos] == null) consequence[currentPos] = ""+1;
			
//			analyseCondition(currentPos);
//			createFor(currentPos);
			
//			updateResourceTask();
//			updateForNameVar();
			
			currentPos++;
		}
	}
	
	private void createVariable(String name, String type, String mapping) {
		if(mapping.equals(YSensorUtilities.caseTEIM)) {
			skipneed = true;
		}
		if(YSensorUtilities.role.equals(type)) {
			//Removed
		}else if(YSensorUtilities.participant.equals(type)) {
			//Removed
		}else if(mapping.contains(YSensorUtilities.offeredList)					|| mapping.contains(YSensorUtilities.allocatedList) 			|| mapping.contains(YSensorUtilities.startedList)
				 || mapping.contains(YSensorUtilities.offeredNumber) 			|| mapping.contains(YSensorUtilities.allocatedNumber) 			|| mapping.contains(YSensorUtilities.startedNumber)
				 || mapping.contains(YSensorUtilities.offeredMinNumber) 		|| mapping.contains(YSensorUtilities.allocatedMinNumber) 		|| mapping.contains(YSensorUtilities.startedMinNumber)
				 || mapping.contains(YSensorUtilities.offeredMinNumberExcept)	|| mapping.contains(YSensorUtilities.allocatedMinNumberExcept) 	|| mapping.contains(YSensorUtilities.startedMinNumberExcept)
				 || mapping.contains(YSensorUtilities.offeredContain) 			|| mapping.contains(YSensorUtilities.allocatedContain) 			|| mapping.contains(YSensorUtilities.startedContain)){
//			analyseCondition(name, mapping);
		}else {
			if(mapping.contains(YSensorUtilities.caseCurrent)) {
				current.put(name, mapping);
			}else {
				if(mapping.endsWith(YSensorUtilities.resource)) {
					current.put(name, mapping);
				}else if(mapping.endsWith(YSensorUtilities.distribution)) {
					current.put(name, mapping);
				}
			}
		}
		String taskName = null;
		if(mapping.contains(YSensorUtilities.caseCurrent) || mapping.contains(YSensorUtilities.caseCurrentM)){
			
			taskName = YStateProcessUtilities.identifyTaskName(mapping.substring(mapping.indexOf(YExpression.PARTC)+2));
			
		}else if(mapping.startsWith(YSensorUtilities.caseB)){
			
			taskName = YStateProcessUtilities.identifyTaskName(YStateProcessUtilities.processParam(mapping)[1].substring(1));
			
		}
		if(taskName != null) {
			taskRelevant.add(taskName);
		}
	}
	
//	private void analyseCondition(String variable, String mapping) {
//		StringTokenizer st = new StringTokenizer(mapping,YExpression.DOT,true);
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.DOT)) {
//				String var = token.substring(0, token.indexOf(YExpression.DOT));
//    			String method = token.substring(token.indexOf(YExpression.DOT)+1);
//    			
//    			int check = 0;
//    			if('o' == method.charAt(0)) check = 1;
//    			else if('a' == method.charAt(0)) check = 2;
//    			else if('s' == method.charAt(0)) check = 3;
//    			
//    			if(check == 1) {
//	    			
//    				YStateProcessUtilities.produceResourceVariable(method, resourceTask, var, offeredString, check);
//	    			
//    			}else if(check == 2) {
//        			
//        			YStateProcessUtilities.produceResourceVariable(method, resourceTask, var, allocatedString, check);
//        			
//    			}else if(check == 3) {
//    				
//    				YStateProcessUtilities.produceResourceVariable(method, resourceTask, var, startedString, check);
//    				
//    			}
//    		}
//    	}
//    }
	
//	private String analyseCondition(String input) {
//		StringTokenizer st = new StringTokenizer(input,"(-*+/%^&|!<=>){}[]",true);
//    	StringBuffer sb = new StringBuffer();
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.DOT)) {
//    			try {
//    				new Double(token);
//        			sb.append(token);
//    			}catch(NumberFormatException nfe) {
//    				String var = token.substring(0, token.indexOf(YExpression.DOT));
//        			String method = token.substring(token.indexOf(YExpression.DOT)+1);
//        			
//        			int check = 0;
//        			if('o' == method.charAt(0)) check = 1;
//        			else if('a' == method.charAt(0)) check = 2;
//        			else if('s' == method.charAt(0)) check = 3;
//        			
//        			if(check == 1) {
//
//		    			YStateProcessUtilities.produceResourceVariable(method, resourceTask, sb, var, offeredString, check);
//		    			
//        			}else if(check == 2) {
//	        			
//	        			YStateProcessUtilities.produceResourceVariable(method, resourceTask, sb, var, allocatedString, check);
//	        			
//        			}else if(check == 3) {
//        				
//        				YStateProcessUtilities.produceResourceVariable(method, resourceTask, sb, var, startedString, check);
//        				
//        			}
//    			}
//    		}else {
//    			sb.append(token);
//    		}
//    	}
//    	return sb.toString();
//	}
	
//	private void analyseCondition(int currentPos) {
//    	postRiskProbability[currentPos] = analyseCondition(riskProbability[currentPos]);
//    	
//    	postRiskThreshold[currentPos] = analyseCondition(riskThreshold[currentPos]);
//    	
//    	postConsequence[currentPos] = analyseCondition(consequence[currentPos]);
//    }
	
//	private Object[] createFor(String input, int count) {
//	    	
//    	StringBuffer sb = new StringBuffer();
//    	
//		if(input.contains(YSensorUtilities.FOR)) {
//			
//			sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
//			input = input.substring(input.indexOf(YSensorUtilities.FOR));
//			
//			while(input.contains(YSensorUtilities.FOR)) {
//				
//				if(!input.startsWith(YSensorUtilities.FOR)) {
//					sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
//					input = input.substring(input.indexOf(YSensorUtilities.FOR));
//				}
//				
//				String typeFor = input.substring(0, input.indexOf(YExpression.PARQA));
//				
//				input = input.substring(input.indexOf(YExpression.PARQA));
//				
//				int[] startEnd = YSensorUtilities.findClosure(input, "[", "]");
//				
//				String parameters = input.substring(startEnd[0], startEnd[1]+1);
//				
//				input = input.substring(startEnd[1]+1);
//				
//				startEnd = YSensorUtilities.findClosure(input, "[", "]");
//				
//				String loop = input.substring(startEnd[0], startEnd[1]+1);
//				
//				input = input.substring(startEnd[1]+1);
//				
//				String typeString = typeFor.substring(typeFor.indexOf(YSensorUtilities.FOR));
//    			int type = 0;
//    			if(typeString.endsWith(YSensorUtilities.AND)) {
//    				type = 1;
//    			}else if(typeString.endsWith(YSensorUtilities.OR)) {
//    				type = 2;
//    			}else if(typeString.endsWith(YSensorUtilities.SUM)) {
//    				type = 3;
//    			}else if(typeString.endsWith(YSensorUtilities.MUL)) {
//    				type = 4;
//    			}
//    			
//    			if(type>0) {
//	    			LinkedList<String> params = new LinkedList<String>();
//	    			String tmp = parameters.substring(parameters.indexOf(YExpression.PARQA)+1,parameters.indexOf(YExpression.PARQC));
//	    			if(tmp.startsWith("#")) {
//	    				params.add(tmp);
//	    			}else if(tmp.contains(commaString)) {
//	    				while(tmp.contains(commaString)) {
//	    					params.add(tmp.substring(0, tmp.indexOf(commaString)));
//	        				tmp = tmp.substring(tmp.indexOf(commaString)+1);
//	    				}
//	    				params.add(tmp);
//	    			}else {
//	    				params.add(tmp);
//	    			}
//	    			
//	    			String expression = loop.substring(loop.lastIndexOf(YExpression.PARQA)+1, loop.lastIndexOf(YExpression.PARQC)); 
//	    			LinkedList<Object> forInfo = new LinkedList<Object>();
//	    			forInfo.add(type);
//	    			forInfo.add(params);
//	    			forInfo.add(expression);
//	    			forNameVar.put("for__"+count, forInfo);
//					sb.append("for__"+count);
//	    			count++;
//    			}else {
//    				sb.append(input+parameters+loop);
//    			}
//				
//			}
//			
//			sb.append(input);
//			
//		}else {
//			
//			sb.append(input);
//			
//		}
//		return new Object[] {sb.toString(), count};
//	}
    
//	private Object[] createFor(String input, int count) {
//		StringTokenizer st = new StringTokenizer(input,"(-*+/%^&|!<=>){}",true);
//    	StringBuffer sb = new StringBuffer();
//    	while(st.hasMoreTokens()) {
//    		String token = st.nextToken();
//    		if(token.contains(YExpression.PARQA)) {
//    			boolean found = false;
//    			while(st.hasMoreTokens() && !found) {
//    				String token2 = st.nextToken();
//    				token = token+token2;
//    				if(token2.contains(YExpression.PARQC)) found = true;
//    			}
//    			String typeString = token.substring(0, token.indexOf(YExpression.PARQA));
//    			int type = 0;
//    			if(typeString.endsWith(YSensorUtilities.AND)) {
//    				type = 1;
//    			}else if(typeString.endsWith(YSensorUtilities.OR)) {
//    				type = 2;
//    			}else if(typeString.endsWith(YSensorUtilities.SUM)) {
//    				type = 3;
//    			}else if(typeString.endsWith(YSensorUtilities.MUL)) {
//    				type = 4;
//    			}
//    			
//    			if(type>0) {
//	    			LinkedList<String> params = new LinkedList<String>();
//	    			String tmp = token.substring(token.indexOf(YExpression.PARQA)+1,token.indexOf(YExpression.PARQC));
//	    			if(tmp.contains(commaString)) {
//	    				while(tmp.contains(commaString)) {
//	    					params.add(tmp.substring(0, tmp.indexOf(commaString)));
//	        				tmp = tmp.substring(tmp.indexOf(commaString)+1);
//	    				}
//	    				params.add(tmp);
//	    			}else {
//	    				params.add(tmp);
//	    			}
//	    			String expression = token.substring(token.lastIndexOf(YExpression.PARQA)+1, token.lastIndexOf(YExpression.PARQC)); 
//	    			LinkedList<Object> forInfo = new LinkedList<Object>();
//	    			forInfo.add(type);
//	    			forInfo.add(params);
//	    			forInfo.add(expression);
//	    			forNameVar.put("for__"+count, forInfo);
//					sb.append("for__"+count);
//	    			count++;
//    			}else {
//    				sb.append(token);
//    			}
//    		}else {
//    			sb.append(token);
//    		}
//    	}
//    	return new Object[] {sb.toString(), count};
//	}
    
//    /**
//     * Create the information for the execution of the "for" construct
//     */
//    private void createFor(int currentPos) {
//    	int count = 0;
//    	
//    	Object[] res = createFor(postRiskProbability[currentPos], count);
//    	postRiskProbability[currentPos] = (String) res[0];
//    	count = (Integer) res[1];
//    	
//    	res = createFor(postRiskThreshold[currentPos], count);
//    	postRiskThreshold[currentPos] = (String) res[0];
//    	count = (Integer) res[1];
//    	
//    	res = createFor(postConsequence[currentPos], count);
//    	postConsequence[currentPos] = (String) res[0];
//    }
    
//    /**
//     * Update the value of the resources complex variable 
//     */
//	@SuppressWarnings("unchecked")
//	private void updateResourceTask() {
//		if(resourceTask.size()>0) {
//			for(String s : resourceTask) {
//				String var = s.substring(0, s.indexOf("_"));
//				String method = s.substring(s.indexOf("_")+1);
//				String term = null;
//				int intMethod = 0;
//				if(method.contains("_")) {
//					term = method.substring(0, method.indexOf("_"));
//					intMethod = new Integer(method.substring(method.indexOf("_")+1));
//				}else {
//					intMethod = new Integer(method);
//				}
//				int queue = -1;
//				switch(intMethod) {
//					case 1: queue = 0; break;
//					case 2: queue = 0; break;
//					case 3: queue = 0; break;
//					case 4: queue = 0; break;
//					case 5: queue = 0; break;
//					case 6: queue = 1; break;
//					case 7: queue = 1; break;
//					case 8: queue = 1; break;
//					case 9: queue = 1; break;
//					case 10: queue = 1; break;
//					case 11: queue = 2; break;
//					case 12: queue = 2; break;
//					case 13: queue = 2; break;
//					case 14: queue = 2; break;
//					case 15: queue = 2; break;
//				}
//				Element res = JDOMUtil.stringToElement((String)variables.get(mappingName.get(var)));
//				if((String)variables.get(mappingName.get(var))!=null) {
//					@SuppressWarnings("rawtypes")
//					LinkedList l = new LinkedList();
//					res = ((List<Element>)res.getChildren()).get(0);
//					for(Element child : (List<Element>) res.getChildren()) {
//						String id = JDOMUtil.elementToString(child);
//						LinkedList<String> list = null;
//						if(queue == 0) {
//							list = resourcesMap.get(id).offeredList;
//						}else if(queue == 1) {
//							list = resourcesMap.get(id).allocatedList;
//						}else if(queue == 2) {
//							list = resourcesMap.get(id).startedList;
//						}
//						taskRelevant.addAll(list);
//						String termV = null;
//						String varV = null;
//						Object objV = null;
//						switch(intMethod%5) {
//							case 1:
//								if(list.size()>0) l.addLast(list); 
//								break;
//							case 2: 
//								l.addLast(""+list.size()); 
//								break;
//							case 4: 
//								if((termV = mappingName.get(term)) != null) {
//									if((varV = (String)variables.get(termV)) == null) {
//										l.addLast(list.size());
//									}else {
//										boolean check = false;
//										Element termRes = JDOMUtil.stringToElement(varV);
//										termRes = ((List<Element>)termRes.getChildren()).get(0);
//										for(Element childRes : (List<Element>) termRes.getChildren()) {
//											String c1 = JDOMUtil.elementToString(child);
//											String c2 = JDOMUtil.elementToString(childRes);
//											if(c1.equals(c2)) {
//												check = true;
//												break;
//											}
//										}
//										if(!check) l.addLast(list.size());
//									}
//								}else {
//									if(!child.getChild("userid").getValue().equals(term)) l.addLast(list.contains(term)); 
//								}
//								break;
//							case 3: 
//								l.addLast(list.size());
//								break;
//							case 0: 
//								if((termV = mappingName.get(term)) != null) {
//									if((objV = variables.get(termV)) == null) {
//										l.addLast(null);
//									}else {
//										l.addLast(list.contains(objV));
//									}
//								}else {
//									l.addLast(list.contains(term)); 
//								}
//								break;
//						}
//					}
//					if(l.size()==0) {
//						variables.put(s, null);
//					}else if(l.size()>1) {
//						if(intMethod%5==3 || intMethod%5==4) {
//							int min = (Integer) l.get(0);
//							for(int i = 1; i<l.size(); i++) {
//								min = Math.min(min, (Integer)l.get(i));
//							}
//							variables.put(s, ""+min);
//						}else {
//							variables.put(s, l);
//						}
//					}else {
//						if(intMethod%5==3 || intMethod%5==4) {
//							variables.put(s, ""+((Integer)l.get(0)));
//						}else {
//							variables.put(s, l.get(0));
//						}
//					}
//				}else {
//					variables.put(s, null);
//				}
//			}
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	private void updateForNameVar() {
//		if(forNameVar.size()>0) {
//			for(String s : forNameVar.keySet()) {
//				int type = (Integer) forNameVar.get(s).get(1);
//				Object obj = forNameVar.get(s).get(2);
//				LinkedList<String> params = new LinkedList<String>();
//				if(obj instanceof LinkedList) {
//					params = (LinkedList<String>) obj;
//				}else if(obj instanceof String) {
//					String tmp = (String) obj;
//					if(tmp.startsWith(YExpression.MINOR) && tmp.endsWith(YExpression.GREATER)) {
//						Element elem = JDOMUtil.stringToDocument(tmp).getRootElement();
//						for(Element child: (List<Element>) elem.getChildren()) {
//							params.add("<loop>"+JDOMUtil.elementToString(child)+"</loop>");
//						}
//					}else {
//						params.add(tmp);
//					}
//				}
//				String exp = (String) forNameVar.get(s).get(3);
//				int[] counts = new int[params.size()];
//				int[] countsLimit = new int[params.size()];
//				LinkedList<String>[] var = (LinkedList<String>[]) new LinkedList[params.size()];
//				for(int i=0; i<countsLimit.length; i++) {
//					var[i] = (LinkedList<String>) variables.get(mappingName.get(params.get(i)));
//					countsLimit[i] = var[i].size();
//				}
//				boolean endLoop = false;
//				String[] varString = new String[params.size()];
//				for(int i=0; i<varString.length; i++) {
//					varString[i] = var[i].getFirst();
//				}
//				int pos = 0;
//				Object res = null;
//				if(type == 1) {
//					res = new Boolean(true);
//				}else if(type == 2) {
//					res = new Boolean(false);
//				}else if(type == 3) {
//					res = new Double(0);
//				}else if(type == 4) {
//					res = new Double(1);
//				}
//				while(!endLoop) {
//					boolean mod = false;
//					while(!endLoop && counts[pos]>=countsLimit[pos] && pos<counts.length) {
//						pos++;
//						if(pos==counts.length) {
//							endLoop = true;
//							mod = false;
//						}else {
//							counts[pos]++;
//							mod = true;
//							if(counts[pos]<countsLimit[pos]) {
//								varString[pos] = var[pos].get(counts[pos]);
//							}
//						}
//					}
//					while(pos>0 && mod) {
//						pos--;
//						counts[pos] = 0;
//						varString[pos] = var[pos].getFirst();
//					}
//					if(!endLoop) {
//						Object result = null;
//						if(type == 1 || type == 2) {
//							StringTokenizer stTMP = new StringTokenizer(exp,"(-+*/%^&|!<=>){}",true);
//							StringBuffer sb = new StringBuffer();
//							while(stTMP.hasMoreTokens()) {
//								String token = stTMP.nextToken();
//								int param = -1; 
//								if((param = params.indexOf(token)) != -1) {
//									sb.append(varString[param]);
//								}else {
//									sb.append(token);
//								}
//							}
//							result = (Boolean) itei.elaborate(sb.toString(), mappingName, variables, resource);
//							if(type == 1) {
//								res = (Boolean) res && (Boolean)result;
//							}else {
//								res = (Boolean) res || (Boolean)result;
//							}
//						}else if (type == 3 || type == 4) {
//							StringTokenizer stTMP = new StringTokenizer(exp,"(-+*/%^&|!<=>){}",true);
//							StringBuffer sb = new StringBuffer();
//							while(stTMP.hasMoreTokens()) {
//								String token = stTMP.nextToken();
//								String mapName = null;
//								int param = -1; 
//								if((param = params.indexOf(token)) != -1) {
//									sb.append(varString[param]);
//								}else if((mapName = mappingName.get(s)) != null) {
//									Object o = variables.get(mapName);
//									sb.append(o);
//								}else {
//									sb.append(token);
//								}
//							}
//							result = (Double) itei.elaborate(sb.toString(), mappingName, variables, resource);
//							if(type == 3) {
//								res = (Double) res + (Double)result;
//							}else {
//								res = (Double) res * (Double)result;
//							}
//						}
//						counts[pos]++;
//						if(counts[pos]<countsLimit[pos]) {
//							varString[pos] = var[pos].get(counts[pos]);
//						}
//					}	
//				}
//				variables.put(s, res);
//			}
//		}
//	}
	
	/**
	 * Evaluate the sensingCondition
	 * @return null if is not possible to evaluate
	 */
    private Double[] evaluate(int condition) {
    	
    	wqf.update(taskRelevant, resourcesMap);
    	Double riskProbabilityValue = (Double) fi.elaborate(riskProbability[condition], mappingName, variables, resource);
    	
    	wqf.update(taskRelevant, resourcesMap);
    	Double riskThresholdValue = (Double) fi.elaborate(riskThreshold[condition], mappingName, variables, resource);

    	wqf.update(taskRelevant, resourcesMap);
    	Double fault = (Double) fi.elaborate("IF["+faultCondition[condition]+"]THEN[1]ELSE[0]", mappingName, variables, resource);
    	
    	wqf.update(taskRelevant, resourcesMap);
    	Double consequenceValue = (Double) fi.elaborate(consequence[condition], mappingName, variables, resource);
    	
		return new Double[] {riskProbabilityValue, riskThresholdValue, fault, consequenceValue};
	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void importModel(HashMap<String, Object> variables, HashMap<String, Resource> resourcesMap, HashMap<String, Integer> taskWithToken, List<String> tasksLog) {
//    	root = null;
//		String inputCondition = null;
//		String outputCondition = null;
//		LinkedList<Task> tmpTask = new LinkedList<Task>();
//		LinkedList<Place> tmpPlace = new LinkedList<Place>();
//		LinkedList<Net> tmpNet = new LinkedList<Net>();
//		
//		this.resourcesMap = resourcesMap;
//		
//		variablesOriginal = variables;
//		this.variables = new HashMap<String, Object>();
//		for(String variable : variables.keySet()) {
//			Object o = variables.get(variable);
//			if(o instanceof LinkedList) {
//				LinkedList l = (LinkedList) o;
//				this.variables.put(variable, l.clone());
//			}else {
//				this.variables.put(variable, o);
//			}
//		}
//
//		Element e = JDOMUtil.stringToElement(specificationXML);
//		Namespace ns = e.getNamespace();
//		Namespace ns2 = e.getNamespace("xsi");
//		String sensor = JDOMUtil.elementToString(e.getChild("specification", ns).getChild("sensors", ns));
//		List<Element> dec = e.getChild("specification", ns).getChildren("decomposition", ns);
//		for(Element ele : dec) {
//			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals(YSensorUtilities.trueString)) {
//				int net = tmpNet.size();
//				root = ele.getAttributeValue(xmlID);		
//				
//				for(Element localVariable : (List<Element>) ele.getChildren("localVariable", ns)) {
//					String name = localVariable.getChild("name", ns).getValue();
//					LinkedList<Object[]>[] lists = new LinkedList[2];
//					lists[0] = new LinkedList<Object[]>();
//					lists[1] = new LinkedList<Object[]>();
//					dataFlows.put(name, lists);
//				}
//				
//				tmpNet.addLast(new Net(root));
//				Element pce = ele.getChild("processControlElements", ns);
//				
//				inputCondition = pce.getChild("inputCondition", ns).getAttributeValue(xmlID);
//				tmpPlace.add(new Place(inputCondition, true, false, removed));
//				
//				for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
//					tmpPlace.add(new Place(place.getAttributeValue(xmlID), false, true, removed));
//				}
//				
//				for(Element task : (List<Element>) pce.getChildren("task", ns)) {
//					boolean isComposite = false;
//					boolean isMultiple = false;
//					
//					if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
//					
//					if(task.getChild("decomposesTo", ns)!=null) {
//						
//						decomposeTo(task, ns, ns2, e, "", net, isComposite, tmpNet, tmpPlace, tmpTask);
//						
//					}else {
//						notSkippable.add(task.getAttributeValue(xmlID));
//					}
//					if(!isComposite) {
//						
//						generateTaskVariables(task, ns, net, isComposite, isMultiple, tmpTask);
//						
//					}
//					
//				}
//				
//				outputCondition = pce.getChild("outputCondition", ns).getAttributeValue(xmlID);
//				tmpPlace.add(new Place(outputCondition, false, true, removed));
//			}
//		}
//		
////		int netPos = 0;
//		int taskPos = 0;
//		int placePos = 0;
//		dec = e.getChild("specification", ns).getChildren("decomposition", ns);
//		for(Element ele : dec) {
//			if(ele.getAttribute("isRootNet")!=null && ele.getAttributeValue("isRootNet").equals(YSensorUtilities.trueString)) {
//				
//				for(int i = 0; i<tmpNet.size(); i++) {
//					Net n = tmpNet.get(i);
//					if(n.netName.equals(ele.getAttributeValue(xmlID))) {
////						netPos = i;
//						break;
//					}
//				}
//				
//				Element pce = ele.getChild("processControlElements", ns);
//				
//				for(Element start : (List<Element>) pce.getChildren()){
//	//				Element start = pce.getChild("inputCondition", ns);
//					
//					String startID = start.getAttributeValue(xmlID);
//					String startType = start.getName();
//					if(startType.equals("condition") || startType.equals("inputCondition") || startType.equals("outputCondition")) {
//						for(int i = 0; i<tmpPlace.size(); i++) {
//							Place p = tmpPlace.get(i);
//							if(p.engineID.equals(startID)) {
//								placePos = i;
//								LinkedList<Integer> linkedTo = p.linkedTo;
//								
//								YStateProcessUtilities.generateTask(start, ns, ns2, pce, e, "", tmpNet, tmpPlace, tmpTask, 0, tmpPlace.size(), 0, tmpTask.size(), placePos+tmpTask.size(), linkedTo);
//								
//								break;
//							}
//						}
//					}else {
//						for(int i = 0; i<tmpTask.size(); i++) {
//							Task t = tmpTask.get(i);
//							if(t.engineID.equals(startID) || t.engineID.equals(startID+"_close")) {
//								taskPos = i;
//								LinkedList<Integer> linkedTo = t.linkedTo;
//								
//								YStateProcessUtilities.generateTask(start, ns, ns2, pce, e, "", tmpNet, tmpPlace, tmpTask, 0, tmpPlace.size(), 0, tmpTask.size(), taskPos, linkedTo);
//								
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		removed = new boolean[tmpTask.size()];
//		
//		nets = new Net[tmpNet.size()];
//		
//		for(int i = 0; i<nets.length; i++) {
//			nets[i] = tmpNet.get(i);
//		}
//		
//		tasks = new Task[tmpTask.size()];
//		
//		for(int i = 0; i<tasks.length; i++) {
//			tasks[i] = tmpTask.get(i);
//		}
//		
//		places = new Place[tmpPlace.size()];
//		
//		for(int i = 0; i<places.length; i++) {
//			places[i] = tmpPlace.get(i);
//		}
//		
//		tasksSize = tasks.length;
//
//		setTokens(tasksLog, taskWithToken);
//		
//		initializeSensor(sensor);
//		
//		generateListTaskRelevant();
//		
//    }
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void decomposeTo(Element task, Namespace ns, Namespace ns2, Element e, String compositeTaskID, int net, boolean isComposite, LinkedList<Net> tmpNet, LinkedList<Place> tmpPlace, LinkedList<Task> tmpTask) {
//		StringBuffer sb = new StringBuffer(task.getAttributeValue(xmlID));
//		if(!compositeTaskID.isEmpty()) {
//			sb.append("_");
//			sb.append(compositeTaskID);
//		}
//		String taskID = sb.toString();
//		
//		String id = task.getChild("decomposesTo", ns).getAttributeValue(xmlID);
//		List<Element> decSubNet = e.getChild("specification", ns).getChildren("decomposition", ns);
//		for(Element eleSubNet : decSubNet) {
//			if(eleSubNet.getAttributeValue(xmlID).equals(id)) {
//				isComposite = eleSubNet.getAttributeValue("type", ns2).equals("NetFactsType");
//				if(isComposite) {
//					Object[] tmp = generateSubNet(eleSubNet, e, ns, ns2, taskID);
//					tmpTask.add(new Task(null, taskID+"_open", net, false, false, "xor", task.getChild("join", ns).getAttributeValue("code"), removed));
//					tmpNet.addAll((LinkedList) tmp[0]);
//					tmpTask.addAll((LinkedList) tmp[1]);
//					tmpPlace.addAll((LinkedList) tmp[2]);
//					tmpTask.add(new Task(null, taskID+"_close", net, false, false, task.getChild("split", ns).getAttributeValue("code"), "xor", removed));
//					
//					YStateProcessUtilities.importMapping("startingMapping", ns, task, dataFlows, "_open", true);
//					
//					YStateProcessUtilities.dataInputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, taskID+"_open");
//					
//					YStateProcessUtilities.importMapping("completedMapping", ns, task, dataFlows, "_close", false);
//					
//					YStateProcessUtilities.dataOutputNet((HashMap<String, LinkedList<Object[]>[]>) tmp[3], dataFlows, taskID+"_close");
//				}
//				if(compositeTaskID.isEmpty()) break;
//			}
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	private void generateTaskVariables(Element task, Namespace ns, Integer net, boolean isComposite, boolean isMultiple, LinkedList<Task> tmpTask) {
//		String name = null;
//		if(task.getChild("name", ns)!=null) {
//			name = task.getChild("name", ns).getValue();
//		}
//		
//		YStateProcessUtilities.importMapping("startingMapping", ns, task, dataFlows, "", true);
//		
//		YStateProcessUtilities.importMapping("completedMapping", ns, task, dataFlows, "", false);
//		
//		Task t = new Task(name, task.getAttributeValue(xmlID), net, isComposite, isMultiple, task.getChild("split", ns).getAttributeValue("code"), task.getChild("join", ns).getAttributeValue("code"), removed);
//		createDefaultResource(t, task, ns);
//		for(String mapping : variablesOriginal.keySet()) {
//			if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerResource)) {
//				Element offer = JDOMUtil.stringToDocument((String) variablesOriginal.get(mapping)).getRootElement().getChild(offerString);
//				for(Element resource : (List<Element>) offer.getChildren()) {
//					String xmlResource = JDOMUtil.elementToString(resource);
//					t.isOffered = true;
//					t.offerRes.add(xmlResource);
//					t.originalOfferRes.add(xmlResource);
//				}
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateResource)) {
//				Element allocate = JDOMUtil.stringToDocument((String) variablesOriginal.get(mapping)).getRootElement().getChild(allocateString);
//				for(Element resource : (List<Element>) allocate.getChildren()) {
//					String xmlResource = JDOMUtil.elementToString(resource);
//					t.isAllocated = true;
//					t.allocateRes = xmlResource;
//					t.originalAllocateRes = xmlResource;
//				}
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startResource)) {
//				Element start = JDOMUtil.stringToDocument((String) variablesOriginal.get(mapping)).getRootElement().getChild(startString);
//				for(Element resource : (List<Element>) start.getChildren()) {
//					String xmlResource = JDOMUtil.elementToString(resource);
//					t.isStarted = true;
//					t.startRes = xmlResource;
//					t.originalStartRes = xmlResource;
//				}
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isOffered)) {
//				t.isOffered = new Boolean((String) variablesOriginal.get(mapping));
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isAllocated)) {
//				t.isAllocated = new Boolean((String) variablesOriginal.get(mapping));
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isStarted)) {
//				t.isStarted = new Boolean((String) variablesOriginal.get(mapping));
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.isCompleted)) {
//				t.isCompleted = new Boolean((String) variablesOriginal.get(mapping));
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerTime)) {
//				if(variablesOriginal.get(mapping) != null) t.isOffered = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateTime)) {
//				if(variablesOriginal.get(mapping) != null) t.isAllocated = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startTime)) {
//				if(variablesOriginal.get(mapping) != null) t.isStarted = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.completeTime)) {
//				if(variablesOriginal.get(mapping) != null) t.isCompleted = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.passTime)) {
//				if(variablesOriginal.get(mapping) != null) t.isOffered = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerTimeInMillis)) {
//				if(variablesOriginal.get(mapping) != null) t.isOffered = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.allocateTimeInMillis)) {
//				if(variablesOriginal.get(mapping) != null) t.isAllocated = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.startTimeInMillis)) {
//				if(variablesOriginal.get(mapping) != null) t.isStarted = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.completeTimeInMillis)) {
//				if(variablesOriginal.get(mapping) != null) t.isCompleted = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.passTimeInMillis)) {
//				if(variablesOriginal.get(mapping) != null) t.isOffered = true;
//			}else if(mapping.contains(YSensorUtilities.caseCurrent+YExpression.DOT+name+YSensorUtilities.offerDistribution)) {
//				Element offer = JDOMUtil.stringToDocument((String) variablesOriginal.get(mapping)).getRootElement().getChild(offerString);
//				for(Element resource : (List<Element>) offer.getChildren()) {
//					String xmlResource = JDOMUtil.elementToString(resource);
//					t.offerResDef.add(xmlResource);
//					t.originalOfferResDef.add(xmlResource);
//				}
//			}
//		}
//		
//		if(t.isCompleted) {
//			t.status = Task.Completed;
//			t.originalStatus = Task.Completed;
//		}else if(t.isStarted) {
//			t.status = Task.Started;
//			t.originalStatus = Task.Started;
//		}else if(t.isAllocated) {
//			t.status = Task.Allocated;
//			t.originalStatus = Task.Allocated;
//		}else if(t.isOffered) {
//			t.status = Task.Offered;
//			t.originalStatus = Task.Offered;
//		}else {
//			t.status = Task.Unoffered;
//			t.originalStatus = Task.Unoffered;
//		}
//		
//		tmpTask.add(t);
//	}
//    
//    private void associateOfferResources(String activity) {
//    	String res = null;
//		for(Task t : tasks) {
//			if(activity.equals(t.taskName) && t.offerRes.isEmpty()) {
//				if(res == null)	res = (String) ysmi.getLogResourceTaskItem(YSensorUtilities.caseCurrent+YExpression.DOT+activity+YSensorUtilities.offerResource, caseID);
//				Element offer = JDOMUtil.stringToDocument((String) variablesOriginal.get(res)).getRootElement().getChild("offer");
//				for(Element resource : (List<Element>) offer.getChildren()) {
//					String xmlResource = JDOMUtil.elementToString(resource);
//					t.offerRes.add(xmlResource);
//					t.originalOfferRes.add(xmlResource);
//				}
//			}
//    	}
//    }
//
//	@SuppressWarnings("unchecked")
//	private void createDefaultResource(Task t, Element task, Namespace ns) {
//		Element resourcing = task.getChild("resourcing", ns);
//		if(resourcing != null) {
//			if(resourcing.getChild(offerString, ns)!=null && resourcing.getChild(offerString, ns).getAttributeValue("initiator").equals("system")) {
//				Element distributionSet = resourcing.getChild(offerString, ns).getChild("distributionSet", ns);
//				if(distributionSet != null) {
//					Element initialSet = distributionSet.getChild("initialSet", ns);
//					if(initialSet != null) {
//						for(Element set : (List<Element>) initialSet.getChildren()) {
//							if(set.getName().equals(YSensorUtilities.participant)) {
//								t.offerResDef.add(getParticipant(set.getValue()));
//								t.originalOfferResDef.add(getParticipant(set.getValue()));
//							}else if(set.getName().equals(YSensorUtilities.role)) {
//								t.offerResDef.addAll(getResource(set.getValue()));
//								t.originalOfferResDef.addAll(getResource(set.getValue()));
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//
//	private List<String> getResource(String role) {
//		LinkedList<String> list = new LinkedList<String>();
//		list.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//		list.add(JDOMUtil.formatXMLString("<participant id=\"PA-7933b9de-5aa6-4d9c-be12-d1a5cc8a8c67\"><userid>dvc</userid><firstname>Don Vito</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//		return list;
//	}
//
//	private String getParticipant(String participantID) {
//		return JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//	}
//
//	private void setTokens(List<String> tasksLog, HashMap<String, Integer> taskWithToken) {
//		
//		ng = gc.generateGraph(tasksLog, tasks, places);
//		
//		LinkedList<NodeGraph> pile = new LinkedList<NodeGraph>();
//		
//		LinkedList<NodeGraph> pending = gc.discoverLast(ng);
//		
//		namePending = new String[pending.size()];
//		NodeGraph ng1 = null;
//		for(int i=0; i<pending.size(); i++) {
//			ng1 = pending.get(i);
//			namePending[i] = ng1.nodeName;
//		}
//		
//		Arrays.sort(namePending);
//		
//		pile.add(ng);
//		
//		while(pile.size() > 0) {
//			ng1 = pile.removeFirst();
//			if(!pending.contains(ng1)) {
//				for(Task t : tasks) {
//					if(ng1.nodeName.equals(t.engineID)) {
//						t.status = Task.Completed;
//						t.originalStatus = Task.Completed;
//					}
//				}
//			}
//			for(NodeGraph ng2 : ng1.linkedTo) {
//				if(!pile.contains(ng2)) {
//					pile.addLast(ng2);
//				}
//			}
//		}
//		
//		for(Task t : tasks) {
//			if(Arrays.binarySearch(namePending, t.engineID) >= 0) {
//				t.status = Task.Offered;
//			}
//			setResource(t);
//		}
//		
//		for(Task t : tasks) {
//			for(String engineID : taskWithToken.keySet()) {
//				if(engineID.equals(t.engineID)) {
//					int val = taskWithToken.get(engineID);
//					t.status = val;
//					t.originalStatus = val;
//				setResource(t);
//				}
//			}
//		}
//	}
//
//	private void setResource(Task t) {
//		if(t.status == Task.Offered && t.offerRes.isEmpty()) {
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			resourcesMap.get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//			
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			resourcesMap.get(JDOMUtil.formatXMLString("<participant id=\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//			
//			t.offerRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			t.originalOfferRes.add(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>"));
//			resourcesMap.get(JDOMUtil.formatXMLString("<participant id=\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addOffer(t.taskName);
//		}
//		if(t.status == Task.Allocated && t.allocateRes == null) {
//			t.allocateRes  = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			t.originalAllocateRes  = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			resourcesMap.get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addAllocate(t.taskName);
//		}
//		if(t.status == Task.Started && t.startRes == null) {
//			t.startRes = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			t.originalStartRes = JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>");
//			resourcesMap.get(JDOMUtil.formatXMLString("<participant id=\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant>")).addStart(t.taskName);
//		}
//	}
//
//	@SuppressWarnings({ "unchecked"})
//	private Object[] generateSubNet(Element subNetElement, Element specificationSet, Namespace ns, Namespace ns2, String compositeTaskID) {
//    	Object[] array = new Object[4];
//    	
//    	LinkedList<Net> tmpNet = new LinkedList<Net>();
//    	LinkedList<Task> tmpTask = new LinkedList<Task>();
//    	LinkedList<Place> tmpPlace = new LinkedList<Place>();
//    	HashMap<String, LinkedList<Object[]>[]> dataFlows = new HashMap<String, LinkedList<Object[]>[]>(); 
//    	
//    	array[0] = tmpNet;
//    	array[1] = tmpTask;
//    	array[2] = tmpPlace;
//    	array[3] = dataFlows;
//    	
//    	int net = tmpNet.size();
//		tmpNet.addLast(new Net(subNetElement.getAttributeValue(xmlID)));
//		
//		Element pce = subNetElement.getChild("processControlElements", ns);
//		
//		String inputCondition = pce.getChild("inputCondition", ns).getAttributeValue(xmlID);
//		tmpPlace.add(new Place(inputCondition+"_"+compositeTaskID, true, false, removed));
//		
//		for(Element place : (List<Element>) pce.getChildren("condition", ns)) {
//			tmpPlace.add(new Place(place.getAttributeValue(xmlID), false, true, removed));
//		}
//		
//		for(Element task : (List<Element>) pce.getChildren("task", ns)) {
//			boolean isComposite = false;
//			boolean isMultiple = false;
//			
//			if(task.getAttributeValue("type", ns2)!=null && task.getAttributeValue("type", ns2).equals("MultipleInstanceExternalTaskFactsType")) isMultiple = true;						
//			
//			if(task.getChild("decomposesTo", ns)!=null) {
//				
//				decomposeTo(task, ns, ns2, specificationSet, compositeTaskID, net, isComposite, tmpNet, tmpPlace, tmpTask);
//				
//			}
//			if(!isComposite) {
//				
//				generateTaskVariables(task, ns, net, isComposite, isMultiple, tmpTask);
//				
//			}
//		}
//		
//		String outputCondition = pce.getChild("outputCondition", ns).getAttributeValue(xmlID);
//		tmpPlace.add(new Place(outputCondition+"_"+compositeTaskID, false, true, removed));
//		
//		return array;
//    }
    
//    private Object exportModel() {
//    	return null; //TODO
//    }
	
	public HashMap<String, HashMap<String, String>> getModificationsStructure() {
		HashMap<String, HashMap<String, String>> modificationStructure = new HashMap<String, HashMap<String, String>>();
		
		for(String s : modifications) {
			int pos = s.indexOf(colomSpaceString);
			String operation = s.substring(0, pos);
			String rest = s.substring(pos+2);
			
			String task = null;
			String other = null;
			
			if(rest.indexOf(YExpression.SUB) == rest.lastIndexOf(YExpression.SUB)) {
				task = rest;
			}else {
				pos = rest.indexOf(colomSpaceString);
				
				String taskName =  rest.substring(0, pos);
				rest = rest.substring(pos+2);
				pos = rest.indexOf(colomSpaceString);
				
				String taskID =  rest.substring(0, pos);
				other = rest.substring(pos+2);				
				
				task = taskName+colomSpaceString+taskID;
			}
			
			HashMap<String, String> mod = null;
			if((mod = modificationStructure.get(task)) == null) {
				mod = new HashMap<String, String>();
				modificationStructure.put(task, mod);
			}
			mod.put(operation, other);
		}
		
		return modificationStructure;
	}
	
	public HashMap<String, Resource> getResourcesMap() {
		return resourcesMap;
	}

	public void setResourcesMap(HashMap<String, Resource> resourcesMap) {
		this.resourcesMap = resourcesMap;
	}

	public HashMap<String, Object> getVariablesOriginal() {
		return variablesOriginal;
	}

	public void setVariablesOriginal(HashMap<String, Object> variablesOriginal) {
		this.variablesOriginal = variablesOriginal;
	}

	public HashMap<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, Object> variables) {
		this.variables = variables;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public Net[] getNets() {
		return nets;
	}

	public void setNets(Net[] nets) {
		this.nets = nets;
	}

	public Task[] getTasks() {
		return tasks;
	}

	public void setTasks(Task[] tasks) {
		this.tasks = tasks;
	}

	public Place[] getPlaces() {
		return places;
	}

	public void setPlaces(Place[] places) {
		this.places = places;
	}

	public boolean[] getRemoved() {
		return removed;
	}

	public void setRemoved(boolean[] removed) {
		this.removed = removed;
	}

	public HashMap<String, LinkedList<Object[]>[]> getDataFlows() {
		return dataFlows;
	}

	public void setDataFlows(HashMap<String, LinkedList<Object[]>[]> dataFlows) {
		this.dataFlows = dataFlows;
	}

	public HashSet<String> getTaskRelevant() {
		return taskRelevant;
	}

	public void setTaskRelevant(HashSet<String> taskRelevant) {
		this.taskRelevant = taskRelevant;
	}

	public int[] getNumberTaskRelevant() {
		return numberTaskRelevant;
	}

	public void setNumberTaskRelevant(int[] numberTaskRelevant) {
		this.numberTaskRelevant = numberTaskRelevant;
	}

	public LinkedList<NodeGraph> getPending() {
		return pending;
	}

	public void setPending(LinkedList<NodeGraph> pending) {
		this.pending = pending;
	}

	public String[] getNamePending() {
		return namePending;
	}

	public void setNamePending(String[] namePending) {
		this.namePending = namePending;
	}

	public HashSet<String> getNotSkippable() {
		return notSkippable;
	}

	public void setNotSkippable(HashSet<String> notSkippable) {
		this.notSkippable = notSkippable;
	}

	public int getTasksSize() {
		return tasksSize;
	}

	public void setTasksSize(int tasksSize) {
		this.tasksSize = tasksSize;
	}
	
	public String getSpecificationXML() {
		return specificationXML;
	}

	public void setSpecificationXML(String specificationXML) {
		this.specificationXML = specificationXML;
	}
	
	public boolean[] getOperations() {
		return operations;
	}

	public void setOperations(boolean[] operations) {
		this.operations = operations;
	}

	@Override
	public String toString() {
		String s = Arrays.toString(calculateEnergy()) + " " + modifications.size() + " " + id + " " + fathedID;
		
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof StateYAWLProcess){
			StateYAWLProcess a = (StateYAWLProcess) o;
			
			if(this.modifications.size() != a.modifications.size()) return false;
			else {
				return this.getModificationsStructure().equals(a.getModificationsStructure());
			}
			
		}
		
		return false;
	}

}