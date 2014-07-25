package org.yawlfoundation.yawl.sensors.language;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class IfThenElseInterpreter {

	final int ifSize = 3;
	final int thenSize = 5;
	final int elseSize = 5;
	
	public static final String CONTAINS = "}";
	public static final String ISCONTAINED = "{";
	public static final String PARTA = "(";
	public static final String PARTC = ")";
	public static final String SUM = "+";
	public static final String SUB = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String EXP = "^";
	public static final String MOD = "%";
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String NOT = "!";
	public static final String MINOR = "<";
	public static final String MINOREQUAL = "<=";
	public static final String EQUAL = "==";
	public static final String NOTEQUAL = "!=";
	public static final String GREATEREQUAL = ">=";
	public static final String GREATER = ">";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String ifPart = "IF[";
	public static final String elsePart = "ELSE";
	
	private HashMap<String, String> mappingName = null;
	private HashMap<String, Object> variables = null;
	private LinkedList<String> resource = null;
	private final YExpression expression = new YExpression();
	private final MathEvaluator mathevaluator = new MathEvaluator();
	
	public static void main(String[] arg) {
		IfThenElseInterpreter itei = new IfThenElseInterpreter();
//		System.out.println(itei.elaborate("FORADD[a,b]IF[IF[false]THEN[true]ELSE[false]&IF[true]THEN[true]ELSE[false]]THEN[1]ELSE[2]", new HashMap<String, String>(), new HashMap<String, Object>(), new LinkedList<String>()));
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("claimStart", "case(current).New_Claim_3(CompleteTimeInMillis)");
		mapping.put("claimEnd", "case(current).Close_File_22(CompleteTimeInMillis)");
		HashMap<String, Object> value = new HashMap<String, Object>();
		value.put("case(current).New_Claim_3(CompleteTimeInMillis)", "-31260000");
		value.put("case(current).Close_File_22(CompleteTimeInMillis)", "1132980000");
		
//		System.out.println(itei.elaborate("(IF[(((4000-cost)-((4000-cost)%200))/200)&gt;0]THEN[(((4000-cost)-((4000-cost)%200))/200)]ELSE[1])", mapping, value, new LinkedList<String>()));
//		System.out.println(itei.elaborate("(IF[cost>=10000]THEN[1]ELSE[2])", mapping, value, new LinkedList<String>()));
//		itei.createFor("FORADD[a,b][IF[true]THEN[2]ELSE[1]]");
		System.out.println(itei.elaborate("IF[(claimEnd-claimStart)>2592000000]THEN[true]ELSE[false]", mapping, value, new LinkedList<String>()));
		
//		System.out.println(Arrays.toString(itei.createFor2("IF[FORSUM[a,b][IF[IF[false]THEN[true]ELSE[false]&IF[true]THEN[true]ELSE[false]]THEN[1]ELSE[2]]]ELSE[FORSUM[a,b][IF[IF[false]THEN[true]ELSE[false]&IF[true]THEN[true]ELSE[false]]THEN[1]ELSE[2]]]", 0)));
	}
	
	public Object elaborate(String condition, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource) {
		
		this.mappingName = mappingName;
		this.variables = variables;
		this.resource = resource;
		
		String result = "";
		String res = null;
		int currPos = 0;
		
		if(condition.contains(ifPart)) {
			
			res = condition.substring(0, condition.indexOf(ifPart));
			result += res;
			condition = condition.substring(res.length());
			String tmp = condition;
			
			int oldCurrPos = 0;
			
			while(!tmp.isEmpty() && (tmp.startsWith(ifPart) || (!tmp.startsWith(YExpression.PARQC) && tmp.contains(ifPart)))) {
				
				if(!tmp.startsWith(ifPart)) {
					String res1 = tmp.substring(0, tmp.indexOf(ifPart));
					result += res1;
					tmp = tmp.substring(res1.length());
					oldCurrPos += res1.length();
					currPos += res1.length();
				}
				
				//IF PART
				Object[] value = findIF(condition, currPos);
				currPos = (Integer) value[0];
				Boolean ifValue = (Boolean) value[1];
				oldCurrPos = currPos;

				//THEN PART
				value = findTHEN(condition, currPos, ifValue);
				currPos = (Integer) value[0];
				Object thenValue = value[1];
				oldCurrPos = currPos;

				//ELSE PART
				value = findELSE(condition, currPos, ifValue);
				currPos = (Integer) value[0];
				Object elseValue = value[1];
				oldCurrPos = currPos;
				
				if(currPos <= condition.length()) {
					tmp = condition.substring(currPos);
				}else {
					tmp = "";
				}

				if(ifValue == null) result += elseValue; //result += "null";
				else if(ifValue) result += thenValue;
				else if(!ifValue) result += elseValue;
			}
			
			if(!tmp.isEmpty()) {
				result += tmp;
			}
		}else {
			result = ""+evaluate(condition, false);
		}
				
		return evaluate(result, false);
	}
	
	private Object evaluate(String condition, boolean ifCondition) {
		if(condition.contains(AND) || condition.contains(OR) || condition.contains(NOT) || condition.contains(GREATER) || 
		   condition.contains(GREATEREQUAL) || condition.contains(EQUAL)|| condition.contains(MINOREQUAL) || 
		   condition.contains(MINOR) || condition.contains(CONTAINS) || condition.contains(ISCONTAINED) || 
		   condition.contains(TRUE) || condition.contains(FALSE)) {
			return expression.booleanEvaluation(condition, mappingName, variables, resource);
		}else if(!ifCondition || condition.contains(SUM) || condition.contains(SUB) || condition.contains(MULTIPLY) || 
		   condition.contains(DIVIDE) || condition.contains(MOD) || condition.contains(EXP)){
			StringTokenizer stTMP = new StringTokenizer(condition,"(-+*/%^)",true);
			StringBuffer sb = new StringBuffer();
			while(stTMP.hasMoreTokens()) {
				String token = stTMP.nextToken();
				if(mappingName.containsKey(token)) {
					String mapName = mappingName.get(token);
					Object o = variables.get(mapName);
					sb.append(o);
				}else {
					sb.append(token);
				}
			}
			condition = sb.toString();
			if(condition.contains(TRUE) || condition.contains(FALSE)) {
				return expression.booleanEvaluation(condition, mappingName, variables, resource);
			}else {
				condition = condition.replace("--", "+");
				condition = condition.replace("-+", "-");
				condition = condition.replace("+-", "-");
				mathevaluator.setExpression(condition);
				try {
					return mathevaluator.getValue();
				} catch (Exception e) {	
					return null;
				}
			}
		}else {
			return expression.booleanEvaluation(condition, mappingName, variables, resource);
		}
		
	}

	private Object[] elaborateInternal(String a) {
		String result = null;
		String res = null;
		int currPos = 0;
				
		if(a.contains(ifPart)) {
			
			res = a.substring(0, a.indexOf(ifPart));
			result = res;
			a = a.substring(res.length());
			String tmp = a;
			
			int oldCurrPos = 0;
			
			while(!tmp.isEmpty() && (tmp.startsWith(ifPart) || (!tmp.startsWith(YExpression.PARQC) && tmp.contains(ifPart)))) {
								
				if(!tmp.startsWith(ifPart)) {
					String res1 = tmp.substring(0, tmp.indexOf(ifPart));
					result += res1;
					tmp = tmp.substring(res1.length());
					oldCurrPos += res1.length();
					currPos += res1.length();
				}
				
				//If Part
				Object[] value = findIF(a, currPos);
				currPos = (Integer) value[0];
				Boolean ifValue = (Boolean) value[1];
				oldCurrPos = currPos;
				
				//Then Part
				value = findTHEN(a, currPos, ifValue);
				currPos = (Integer) value[0];
				Object thenValue = value[1];
				oldCurrPos = currPos;
				
				//Else Part
				value = findELSE(a, currPos, ifValue);
				currPos = (Integer) value[0];
				Object elseValue = value[1];
				oldCurrPos = currPos;
				
				tmp = a.substring(currPos);
				
				if(ifValue == null) result += elseValue;//result += "null";
				else if(ifValue) result += thenValue;
				else if(!ifValue) result += elseValue;
			}
			
			if(!tmp.isEmpty()) {
				int nextOpenBracket = tmp.indexOf(YExpression.PARQA);
				int nextCloseBracket = tmp.indexOf(YExpression.PARQC);
				
				if(nextCloseBracket != -1 && nextCloseBracket != -1 && nextCloseBracket < nextOpenBracket) {
					result += tmp.substring(0, nextCloseBracket);
					currPos += nextCloseBracket;
					tmp = "";
				}else if(nextOpenBracket == -1) {
					result += tmp;
					currPos += tmp.length();
					tmp = "";
				}
			}
			return new Object[] {currPos+res.length(), evaluate(result, false)};
		}else {
			return new Object[] {0, evaluate(a, false)};
		}
	}

	public Object[] findIF(String s, int currPos) {
		String tmp = s.substring(currPos+ifSize-1);
		String data = "";
		
		int brackets = 0;
		boolean start = true;
		int nextCloseBracket = tmp.indexOf(YExpression.PARQC);
		int nextColon = -2;//tmp.indexOf(":");
		int nextOpenBracket = tmp.indexOf(YExpression.PARQA);
				
		StringTokenizer st = new StringTokenizer(tmp, "[]", true);
		String token = null;
		StringBuffer build = new StringBuffer();
		
		while(st.hasMoreTokens() && (brackets != 0 || start)) {
			if(start) start = false;
			token = st.nextToken();
			if('[' == token.charAt(0)) brackets++;
			else if(']' == token.charAt(0)) brackets--;
			build.append(token);
		}
		
		tmp = build.toString();
				
		while (nextColon != -1) {
			if(nextColon < nextCloseBracket &&  nextOpenBracket < nextColon) {
				data += tmp.substring(nextOpenBracket, nextCloseBracket+1);
				tmp = tmp.substring(nextCloseBracket+1);
			}
			
			nextCloseBracket = tmp.indexOf(YExpression.PARQC);
			nextColon = tmp.indexOf(":");
			nextOpenBracket = tmp.indexOf(YExpression.PARQA);
		}
		currPos += data.length();
		
		if(!tmp.contains("IF[")) {
			String ifCondition = removeExternalBrackets(tmp);
			return new Object[] {ifCondition.length()+currPos+ifSize+1, evaluate(ifCondition, true)};
		}else {
			tmp = removeExternalBrackets(tmp);
			Object[] value = elaborateInternal(tmp);
			value[0] = tmp.length()+currPos+ifSize+1; 
			return value;
		}
	}
	
//	public Object[] findTHEN2(String s, int currPos, Boolean ifValue) {
//		String tmp = s.substring(currPos+thenSize);
//		String data = "";
//		
//		int nextCloseBracket = tmp.indexOf(YExpression.PARQC);
//		int nextColon = -2;//tmp.indexOf(":");
//		int nextOpenBracket = tmp.indexOf(YExpression.PARQA);
//						
//		while (nextColon != -1) {
//			if(nextColon < nextCloseBracket &&  nextOpenBracket < nextColon) {
//				data += tmp.substring(nextOpenBracket, nextCloseBracket+1);
//				tmp = tmp.substring(nextCloseBracket+1);
//			}
//			
//			nextCloseBracket = tmp.indexOf(YExpression.PARQC);
//			nextColon = tmp.indexOf(":");
//			nextOpenBracket = tmp.indexOf(YExpression.PARQA);
//		}
//		currPos += data.length();
//		
//		System.out.println(tmp);
//		
//		if(!tmp.contains("IF[")) {
//			String thenCondition = s.substring(currPos+thenSize, nextCloseBracket+currPos+thenSize);
//			if(ifValue != null && ifValue) return new Object[] {nextCloseBracket+currPos+thenSize+1, evaluate(thenCondition, false)};
//			else return new Object[] {nextCloseBracket+currPos+thenSize+1, ""};
//		}else {			
//			Object[] value = elaborateInternal(tmp);
//			System.out.println(tmp);
//			System.out.println(value[1]);
//			value[0] = ((Integer) value[0]) +currPos+thenSize+1; 
//			return value;
//		}
//	}
	
	public Object[] findTHEN(String s, int currPos, Boolean ifValue) {
		String tmp = s.substring(currPos+thenSize-1);
		String data = "";
		
		int brackets = 0;
		boolean start = true;
		int nextCloseBracket = tmp.indexOf(YExpression.PARQC);
		int nextColon = -2;//tmp.indexOf(":");
		int nextOpenBracket = tmp.indexOf(YExpression.PARQA);
				
		StringTokenizer st = new StringTokenizer(tmp, "[]", true);
		String token = null;
		StringBuffer build = new StringBuffer();
		
		while(st.hasMoreTokens() && (brackets != 0 || start)) {
			if(start) start = false;
			token = st.nextToken();
			if('[' == token.charAt(0)) brackets++;
			else if(']' == token.charAt(0)) brackets--;
			build.append(token);
		}
		
		tmp = build.toString();
		
		while (nextColon != -1) {
			if(nextColon < nextCloseBracket &&  nextOpenBracket < nextColon) {
				data += tmp.substring(nextOpenBracket, nextCloseBracket+1);
				tmp = tmp.substring(nextCloseBracket+1);
			}
			
			nextCloseBracket = tmp.indexOf(YExpression.PARQC);
			nextColon = tmp.indexOf(":");
			nextOpenBracket = tmp.indexOf(YExpression.PARQA);
		}
		currPos += data.length();
				
		if(!tmp.contains("IF[")) {
			String thenCondition = removeExternalBrackets(tmp);
			if(ifValue != null && ifValue) return new Object[] {thenCondition.length()+currPos+thenSize+1, evaluate(thenCondition, false)};
			else return new Object[] {thenCondition.length()+currPos+thenSize+1, ""};
		}else {			
			tmp = removeExternalBrackets(tmp);
			Object[] value = elaborateInternal(tmp);
			value[0] = tmp.length()+currPos+thenSize+1; 
			return value;
		}
	}
	
	public Object[] findELSE(String s, int currPos, Boolean ifValue) {
		if(s.substring(currPos).startsWith(elsePart)) {
			String tmp = s.substring(currPos+elseSize-1);
			String data = "";
			
			int brackets = 0;
			boolean start = true;
			int nextCloseBracket = tmp.indexOf(YExpression.PARQC);
			int nextColon = -2;//tmp.indexOf(":");
			int nextOpenBracket = tmp.indexOf(YExpression.PARQA);
					
			StringTokenizer st = new StringTokenizer(tmp, "[]", true);
			String token = null;
			StringBuffer build = new StringBuffer();
			
			while(st.hasMoreTokens() && (brackets != 0 || start)) {
				if(start) start = false;
				token = st.nextToken();
				if('[' == token.charAt(0)) brackets++;
				else if(']' == token.charAt(0)) brackets--;
				build.append(token);
			}
			
			tmp = build.toString();
					
			while (nextColon != -1) {
				if(nextColon < nextCloseBracket &&  nextOpenBracket < nextColon) {
					data += tmp.substring(nextOpenBracket, nextCloseBracket+1);
					tmp = tmp.substring(nextCloseBracket+1);
				}
				
				nextCloseBracket = tmp.indexOf(YExpression.PARQC);
				nextColon = tmp.indexOf(":");
				nextOpenBracket = tmp.indexOf(YExpression.PARQA);
			}
			currPos += data.length();
						
			if(!tmp.contains("IF[")) {
				String elseCondition = removeExternalBrackets(tmp);
				if(ifValue == null || !ifValue) return new Object[] {elseCondition.length()+currPos+elseSize+1, evaluate(elseCondition, false)};
				else return new Object[] {elseCondition.length()+currPos+elseSize+1, ""};
			}else {
				tmp = removeExternalBrackets(tmp);
				Object[] value = elaborateInternal(tmp);
				value[0] = tmp.length()+currPos+elseSize+1; 
				return value;
			}
		}else {
			return new Object[] {currPos, ""};
		}
	}
	
	private String removeExternalBrackets(String s) {
		return s.substring(1, s.length()-1);
	}
}
