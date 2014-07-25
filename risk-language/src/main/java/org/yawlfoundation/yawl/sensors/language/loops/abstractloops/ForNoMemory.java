package org.yawlfoundation.yawl.sensors.language.loops.abstractloops;

import org.jdom.Element;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.sensors.language.loops.ForExecutor;
import org.yawlfoundation.yawl.sensors.language.loops.ForInterpreter;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class ForNoMemory {
	
	public static String empty = "";

	public static Object getForResult(LinkedList<Object> input, HashMap<String, String> mappingName, HashMap<String, Object> variables, LinkedList<String> resource, ForInterpreter forInterpreter) {
		boolean fixed = false;
		int count = 0;
		int type = (Integer) input.get(0);
		
		Object res = ForExecutor.inizializeRes(type);
		
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
		LinkedList<String>[] var = (LinkedList<String>[]) new LinkedList[params.size()];
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
				
				result = forInterpreter.elaborate(sb.toString(), mappingName, variables, resource);
				res = ForExecutor.aggregate(res, result, null, type);

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
