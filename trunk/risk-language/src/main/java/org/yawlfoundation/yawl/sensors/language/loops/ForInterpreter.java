package org.yawlfoundation.yawl.sensors.language.loops;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.YSensorUtilities;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.sensors.language.functions.FunctionInterpreter;
import org.yawlfoundation.yawl.sensors.language.functions.WorkQueueFacade;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ForInterpreter {
	
	public FunctionInterpreter funcInt = null;
	public static String empty = "";
	
	public static void main(String[] args) {
		ForInterpreter fi = new ForInterpreter(null);
		
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("ASPOn", "case(current).Carrier_Appointment(PassTimeInMillis)");
		mapping.put("cost", "case(current).Prepare_Transportation_Quote_390.ShipmentCost");
		mapping.put("OverTimeIsPaid", "null");
		mapping.put("OverTimeCompletePay", "null");
		mapping.put("OverTimeIsRejected", "null");
		mapping.put("OverTimeCompleteReject", "null");
		mapping.put("OverTimePassedTime", "e");
		mapping.put("OverTimeEstimatedTime", "f");
		
		mapping.put("g", "g");
		mapping.put("h", "h");
		mapping.put("i", "i");
		mapping.put("l", "l");
		
		LinkedList<String> a = new LinkedList<String>();
		a.add("1");
		a.add("2");
		a.add("3");
		
		LinkedList<String> b = new LinkedList<String>();
		b.add("4");
		b.add("5");
		b.add("6");
		
		LinkedList c = new LinkedList();
		c.add(a);
		c.add(b);

		HashMap<String, Object> value = new HashMap<String, Object>();
		value.put("case(current).Carrier_Appointment(PassTimeInMillis)", "3");
		value.put("case(current).Prepare_Transportation_Quote_390.ShipmentCost", "1000");
		value.put("a", "false");
		value.put("b", "2430000000");
		value.put("c", "true");
		value.put("d", "2430000000");
		value.put("e", "2430000000");
		value.put("f", "2430000000");
		
		value.put("g","0");
		value.put("h","0");
//		value.put("i","<Open_Claim_3><Customer>Customer25</Customer><Amount>5000</Amount><DamageType>Windscreen</DamageType><ClaimType>Comprehensive</ClaimType></Open_Claim_3>");
		value.put("l","<Open_Claim_3><Customer>Customer25</Customer><Amount>5000</Amount><DamageType>Windscreen</DamageType><ClaimType>Comprehensive</ClaimType></Open_Claim_3>");

//		System.out.println(fi.elaborate("IF[OverTimeCompleteReject>19800000]THEN[(OverTimeCompleteReject>19800000)]ELSE[0]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[OverTimeIsRejected]THEN[IF[OverTimeCompleteReject>19800000]THEN[(OverTimeCompleteReject>19800000)]ELSE[0]]ELSE[IF[(OverTimePassedTime+OverTimeEstimatedTime)>19800000]THEN[((OverTimePassedTime+OverTimeEstimatedTime)-19800000)]ELSE[0]]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[OverTimeIsPaid]THEN[IF[OverTimeCompletePay>19800000]THEN[(OverTimeCompletePay-19800000)]ELSE[0]]ELSE[IF[OverTimeIsRejected]THEN[IF[OverTimeCompleteReject>19800000]THEN[(OverTimeCompleteReject>19800000)]ELSE[0]]ELSE[IF[(OverTimePassedTime+OverTimeEstimatedTime)>19800000]THEN[((OverTimePassedTime+OverTimeEstimatedTime)-19800000)]ELSE[0]]]", mapping, value, new LinkedList<String>()));
		System.out.println(fi.elaborate("((((g*10)+(h*i))>(l-i))|((l>1000)&(h==0)))", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[OverTimeIsPaid]THEN[IF[(OverTimeCompletePay-OverTimeStartClaim)>19800000]THEN[true]ELSE[false]]ELSE[IF[OverTimeIsRejected]THEN[IF[(OverTimeCompleteReject-OverTimeStartClaim)>19800000]THEN[true]ELSE[false]]ELSE[false]]", mapping, value, new LinkedList<String>()));
		
//		value.put("TcEnd", "2");
//		value.put("TcStart", "3");
		
//		System.out.println(fi.calculateFor("(IF[4000>cost]THEN[0]ELSE[1])", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("(FORADD[c][c.startNumber]+a.startNumber)", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("(FORADD[a][a.startNumber])", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("(FORADD[a][IF[FOROR[b][IF[a+b>1]THEN[true]ELSE[false]]]THEN[1]ELSE[0]])", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("(FORAVG[a,b][a+b])", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[((TcEnd*100)+(TcStart*80))>100]THEN[true]ELSE[false]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[-(TcStart)>100]THEN[true]ELSE[false]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[((TcEnd-TcStart)>(64800000))]THEN[true]ELSE[false]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[(((TcEnd-TcStart)>(64800000))&((TcEnd-TcStart)<=(108000000)))]THEN[((TcEnd-TcStart)-64800000)]ELSE[IF[((TcEnd-TcStart)>(108000000))]THEN[43200000]ELSE[0]]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("IF[((TcEnd-TcStart)>(64800000))&((TcEnd-TcStart)<=(108000000))]THEN[((TcEnd-TcStart)-(64800000))]ELSE[0]", mapping, value, new LinkedList<String>()));
//		System.out.println(fi.elaborate("(TcEnd-(TcStart))-64800000", mapping, value, new LinkedList<String>()));
	}
	
	public ForInterpreter(WorkQueueFacade workQueue) {
		funcInt = new FunctionInterpreter(workQueue);
	}

	public Object elaborate(String input, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource) {
		
    	StringBuffer sb = new StringBuffer();
    	    	
		if(input.contains(YSensorUtilities.FOR)) {
			
			sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
			input = input.substring(input.indexOf(YSensorUtilities.FOR));
			
			while(input.contains(YSensorUtilities.FOR)) {
				if(!input.startsWith(YSensorUtilities.FOR)) {
					sb.append(input.substring(0, input.indexOf(YSensorUtilities.FOR)));
					input = input.substring(input.indexOf(YSensorUtilities.FOR));
				}
				
				String typeFor = input.substring(0, input.indexOf(YExpression.PARQA));
				
				input = input.substring(input.indexOf(YExpression.PARQA));
				
				int[] startEnd = YSensorUtilities.findClosure(input, "[", "]");
				
				String parameters = input.substring(startEnd[0], startEnd[1]+1);
				
				input = input.substring(startEnd[1]+1);
				
				startEnd = YSensorUtilities.findClosure(input, "[", "]");
				
				String loop = input.substring(startEnd[0], startEnd[1]+1);
				
				input = input.substring(startEnd[1]+1);
				
				String typeString = typeFor.substring(typeFor.indexOf(YSensorUtilities.FOR));
    			int type = ForIdentifier.getFunction(typeString);

    			if(type>0) {
	    			LinkedList<String> params = new LinkedList<String>();
	    			String tmp = parameters.substring(parameters.indexOf(YExpression.PARQA)+1,parameters.indexOf(YExpression.PARQC));
	    			if(tmp.startsWith("#")) {
	    				params.add(tmp);
	    			}else if(tmp.contains(",")) {
	    				while(tmp.contains(",")) {
	    					params.add(tmp.substring(0, tmp.indexOf(",")));
	        				tmp = tmp.substring(tmp.indexOf(",")+1);
	    				}
	    				params.add(tmp);
	    			}else {
	    				params.add(tmp);
	    			}
	    			String expression = loop.substring(loop.indexOf(YExpression.PARQA)+1, loop.lastIndexOf(YExpression.PARQC)); 
	    			LinkedList<Object> forInfo = new LinkedList<Object>();
	    			forInfo.add(type);
	    			forInfo.add(params);
	    			forInfo.add(expression);	    			
	    			
					sb.append(ForExecutor.getForResult(type, forInfo, mappingName, variables, resource, this));
    			}else {
    				sb.append(input+parameters+loop);
    			}
				
			}
			
			sb.append(input);
			
		}else {
			
			sb.append(input);
			
		}

		return funcInt.elaborate(sb.toString(), mappingName, variables, resource);
	}
	
	private static Object executeFor(LinkedList<Object> input, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource) {
//		System.out.println(input);
		boolean fixed = false;
		int count = 0;
		int type = (Integer) input.get(0);
		Object obj = input.get(1);
		LinkedList<String> params = new LinkedList<String>();
		if(obj instanceof LinkedList) {
			params = (LinkedList) ((LinkedList) obj).clone();
			if(params.getFirst().startsWith("#")) {
				String tmp = params.removeFirst();
				fixed = true;
				tmp = tmp.substring(1);
				while(tmp.contains(",")) {
					params.add(tmp.substring(0, tmp.indexOf(",")));
    				tmp = tmp.substring(tmp.indexOf(",")+1);
				}
				params.add(tmp);
			}
		}else if(obj instanceof String) {
			String tmp = (String) obj;
			if('<' == tmp.charAt(0) && '>' == tmp.charAt(tmp.length()-1)) {
				Element elem = JDOMUtil.stringToDocument(tmp).getRootElement();
				for(Element child: (List<Element>) elem.getChildren()) {
					params.add("<loop>"+JDOMUtil.elementToString(child)+"</loop>");
				}
			}else {
				params.add(tmp);
			}
		}
		String exp = (String) input.get(2);
		int[] counts = new int[params.size()];
		int[] countsLimit = new int[params.size()];
		LinkedList<String>[] var = new LinkedList[params.size()];
		for(int i=0; i<countsLimit.length; i++) {
			var[i] = (LinkedList<String>) variables.get(mappingName.get(params.get(i)));
			countsLimit[i] = var[i].size();
		}
		boolean endLoop = false;
		Object[] varString = new Object[params.size()];
		for(int i=0; i<varString.length; i++) {
			varString[i] = var[i].getFirst();
		}
		int pos = 0;
		Object res = null;
		if(type == 1) {
			res = new Boolean(true);
		}else if(type == 2) {
			res = new Boolean(false);
		}else if(type == 3) {
			res = new Double(0);
		}else if(type == 4) {
			res = new Double(1);
		}
		while(!endLoop) {
			boolean mod = false;
			if(!fixed) {
				while(!endLoop && counts[pos]>=countsLimit[pos] && pos<counts.length) {
					pos++;
					if(pos==counts.length) {
						endLoop = true;
						mod = false;
					}else {
						counts[pos]++;
						mod = true;
						if(counts[pos]<countsLimit[pos]) {
							varString[pos] = var[pos].get(counts[pos]);
						}
					}
				}
				while(pos>0 && mod) {
					pos--;
					counts[pos] = 0;
					varString[pos] = var[pos].getFirst();
				}
			}else {
				if(!endLoop && counts[0]<=countsLimit[0]) {
					if(counts[0]==countsLimit[0]) {
						endLoop = true;
					}
					for(int i = 0; i<counts.length; i++) {
						if(counts[0]<countsLimit[0]) {
							varString[i] = var[i].get(counts[0]);
						}else {
							endLoop = true;
						}
					}
					counts[0]++;
				}
			}
			if(!endLoop) {
				Object result = null;
				StringTokenizer stTMP = new StringTokenizer(exp,"(-+*/%^&|!<=>){}[]",true);
				StringBuffer sb = new StringBuffer();
				String token = null;
				String suffix = null;
				while(stTMP.hasMoreTokens()) {
					token = stTMP.nextToken();
					suffix = empty;
					if(token.contains(YExpression.DOT)) {
						try {
							double d = Double.parseDouble(token);
						}catch(Exception e) {
							suffix = token.substring(token.indexOf(YExpression.DOT));
							token = token.substring(0, token.indexOf(YExpression.DOT));							
						}
					}

					if(suffix.isEmpty()) {
						if(params.contains(token)) {
							sb.append(varString[params.indexOf(token)]);
						}else {
							sb.append(token);
						}
					}else {
						if(params.contains(token)) {
							mappingName.put(token+"funcVar"+count, token+"funcVar"+count);
							variables.put(token+"funcVar"+count, varString[params.indexOf(token)]);
							sb.append(token+"funcVar"+count+suffix);
							count++;
						}else {
							sb.append(token+suffix);
						}
					}
				}
				if(type == 1 || type == 2) {
//					result = (Boolean) funcInt.elaborate(sb.toString(), mappingName, variables, resource);
//					result = (Boolean) this.elaborate(sb.toString(), mappingName, variables, resource);
					if(type == 1) {
						res = (Boolean) res && (Boolean)result;
					}else {
						res = (Boolean) res || (Boolean)result;
					}
				}else if (type == 3 || type == 4) {
//					result = (Double) funcInt.elaborate(sb.toString(), mappingName, variables, resource);
//					result = (Double) this.elaborate(sb.toString(), mappingName, variables, resource);
					if(type == 3) {
						res = (Double) res + (Double)result;
					}else {
						res = (Double) res * (Double)result;
					}
				}
				if(!fixed) {
					counts[pos]++;
					if(counts[pos]<countsLimit[pos]) {
						varString[pos] = var[pos].get(counts[pos]);
					}	
				}
			}	
		}
		
		return res;
		
	}	

}
