package org.yawlfoundation.yawl.sensors;

import net.sf.saxon.s9api.SaxonApiException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.yawlfoundation.yawl.sensors.databaseInterface.*;
import org.yawlfoundation.yawl.sensors.language.YExpression;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class YSensorUtilities {

    private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final String SUM = "ADD";
	public static final String MUL = "MUL";
	public static final String MIN = "MIN";
	public static final String MAX = "MAX";
	public static final String AVG = "AVG";
	public static final String FOR = "FOR";
	
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String ANDNOT = "ANDNOT";
	public static final String ORNOT = "ORNOT";
	public static final String NOT = "NOT";
	public static final String SPACE = " ";
	
	public static final String offerXMLopen = "<offer>";
	public static final String offerXMLclose = "</offer>";
	public static final String allocateXMLopen = "<allocate>";
	public static final String allocateXMLclose = "</allocate>";
	public static final String startXMLopen = "<start>";
	public static final String startXMLclose = "</start>";
	public static final String completeXMLopen = "<complete>";
	public static final String completeXMLclose = "</complete>";

	public static final String role = "role";
	public static final String roles = "roles";
	public static final String participant = "participant";
	
	public static final String id = "(ID)";
	public static final String idCurr = "[IDCurr]";
	
	public static final String caseB = "case(";
	public static final String current = "current";
	public static final String currentM = "current-";
	public static final String caseCurrent = "case(current)";
	public static final String caseCurrentM = "case(current-";
	public static final String caseTEIM = "case(current).(TimeEstimationInMillis)";

	public static final String resource = "Resource)";
	public static final String distribution = "Distribution)";
	public static final String initiator = "Initiator)";

	public static final String offerDistribution = "(offerDistribution)";
	public static final String allocateDistribution = "(allocateDistribution)";
	public static final String startDistribution = "(startDistribution)";
	
	public static final String offerInitiator = "(offerInitiator)";
	public static final String allocateInitiator = "(allocateInitiator)";
	public static final String startInitiator = "(startInitiator)";
	
	public static final String offeredList = ".offeredList";
	public static final String allocatedList = ".allocatedList";
	public static final String startedList = ".startedList";
	public static final String offeredNumber = ".offeredNumber";
	public static final String allocatedNumber = ".allocatedNumber";
	public static final String startedNumber = ".startedNumber";
	public static final String offeredMinNumber = ".offeredMinNumber";
	public static final String allocatedMinNumber = ".allocatedMinNumber";
	public static final String startedMinNumber = ".startedMinNumber";
	public static final String offeredMinNumberExcept = ".offeredMinNumberExcept";
	public static final String allocatedMinNumberExcept = ".allocatedMinNumberExcept";
	public static final String startedMinNumberExcept = ".startedMinNumberExcept";
	public static final String offeredContain = ".offeredContain";
	public static final String allocatedContain = ".allocatedContain";
	public static final String startedContain = ".startedContain";
	
	public static final String count = "(Count)";
	public static final String countElements = "(CountElements)";
	public static final String timeEstimationInMillis = "(TimeEstimationInMillis)";
	
	public static final String isOffered = "(isOffered)";
	public static final String isAllocated = "(isAllocated)";
	public static final String isStarted = "(isStarted)";
	public static final String isCompleted = "(isCompleted)";
	
	public static final String offerTime = "(OfferTime)";
	public static final String allocateTime = "(AllocateTime)";
	public static final String startTime = "(StartTime)";
	public static final String completeTime = "(CompleteTime)";
	public static final String time = "Time)";
	
	public static final String offerTimeInMillis = "(OfferTimeInMillis)";
	public static final String allocateTimeInMillis = "(AllocateTimeInMillis)";
	public static final String startTimeInMillis = "(StartTimeInMillis)";
	public static final String completeTimeInMillis = "(CompleteTimeInMillis)";
	public static final String timeInMillis = "TimeInMillis)";
	
	public static final String offerResource = "(offerResource)"; 
	public static final String allocateResource = "(allocateResource)";
	public static final String startResource = "(startResource)";
	public static final String completeResource = "(completeResource)";
		
	public static final String passTime = "(PassTime)";
	public static final String passTimeInMillis = "(PassTimeInMillis)";
	
	public static final String isOfferedList = "(isOfferedList)";
	public static final String isAllocatedList = "(isAllocatedList)";
	public static final String isStartedList = "(isStartedList)";
	public static final String isCompletedList = "(isCompletedList)";
	
	public static final String offerTimeList = "(OfferTimeList)";
	public static final String allocateTimeList = "(AllocateTimeList)";
	public static final String startTimeList = "(StartTimeList)";
	public static final String completeTimeList = "(CompleteTimeList)";
	public static final String timeList = "TimeList)";
	
	public static final String offerTimeInMillisList = "(OfferTimeInMillisList)";
	public static final String allocateTimeInMillisList = "(AllocateTimeInMillisList)";
	public static final String startTimeInMillisList = "(StartTimeInMillisList)";
	public static final String completeTimeInMillisList = "(CompleteTimeInMillisList)";
	public static final String timeInMillisList = "TimeInMillisList)";
	
	public static final String offerResourceList = "(offerResourceList)"; 
	public static final String allocateResourceList = "(allocateResourceList)";
	public static final String startResourceList = "(startResourceList)";
	public static final String completeResourceList = "(completeResourceList)";
		
	public static final String passTimeList = "(PassTimeList)";
	public static final String passTimeInMillisList = "(PassTimeInMillisList)";
	
	public static final String list = "(List)";
	
	public static final String fraudProb = "(FraudProbabilityFunc, ";
	
	public static final String trueString = "true";
	public static final String falseString = "false";
	public static final String nullString = "null";
	
	public static void generateCaseCompareDouble(double a, double b, int oper, HashSet<String> casesPartial, String ID, String suffix) {
//		if(suffix!=null) { //Jump
//		} else 
		if(oper==0 && a<b) {
			casesPartial.add(ID);
		}else if(oper==0) {
			casesPartial.remove(ID);
		}else if(oper==1 && a<=b) {
			casesPartial.add(ID);
		}else if(oper==1) {
			casesPartial.remove(ID);
		}else if(oper==2 && a==b) {
			casesPartial.add(ID);
		}else if(oper==2) {
			casesPartial.remove(ID);
		}else if(oper==3 && a>=b) {
			casesPartial.add(ID);
		}else if(oper==3) {
			casesPartial.remove(ID);
		}else if(oper==4 && a>b) {
			casesPartial.add(ID);
		}else if(oper==4) {
			casesPartial.remove(ID);
		}else if(oper==5 && a!=b) {
			casesPartial.add(ID);
		}else if(oper==5) {
			casesPartial.remove(ID);
		}
	}
	
	public static void generateCaseCompareDate(Date a, Date b, int oper, HashSet<String> casesPartial, String ID, String suffix) {
//		if(suffix!=null) { //Jump
//		} else 
		if(oper==0 && a.compareTo(b)<0) {
			casesPartial.add(ID);
		}else if(oper==0) {
			casesPartial.remove(ID);
		}else if(oper==1 && a.compareTo(b)<=0) {
			casesPartial.add(ID);
		}else if(oper==1) {
			casesPartial.remove(ID);
		}else if(oper==2 && a.compareTo(b)==0) {
			casesPartial.add(ID);
		}else if(oper==2) {
			casesPartial.remove(ID);
		}else if(oper==3 && a.compareTo(b)>=0) {
			casesPartial.add(ID);
		}else if(oper==3) {
			casesPartial.remove(ID);
		}else if(oper==4 && a.compareTo(b)>0) {
			casesPartial.add(ID);
		}else if(oper==4) {
			casesPartial.remove(ID);
		}else if(oper==5 && a.compareTo(b)!=0) {
			casesPartial.add(ID);
		}else if(oper==5) {
			casesPartial.remove(ID);
		}
	}
	
	public static void generateCaseCompareString(String a, String b, int oper, HashSet<String> casesPartial, String ID, String suffix) {
//		if(suffix!=null) { //Jump
//		}else 
		if(oper==0 && a.compareTo(b)<0) {
			casesPartial.add(ID);
		}else if(oper==0) {
			casesPartial.remove(ID);
		}else if(oper==1 && a.compareTo(b)<=0) {
			casesPartial.add(ID);
		}else if(oper==1) {
			casesPartial.remove(ID);
		}else if(oper==2 && a.compareTo(b)==0) {
			casesPartial.add(ID);
		}else if(oper==2) {
			casesPartial.remove(ID);
		}else if(oper==3 && a.compareTo(b)>=0) {
			casesPartial.add(ID);
		}else if(oper==3) {
			casesPartial.remove(ID);
		}else if(oper==4 && a.compareTo(b)>0) {
			casesPartial.add(ID);
		}else if(oper==4) {
			casesPartial.remove(ID);
		}else if(oper==5 && a.compareTo(b)!=0) {
			casesPartial.add(ID);
		}else if(oper==5) {
			casesPartial.remove(ID);
		}
	}
	
	public static Boolean generateCaseCompareResource(String a, String b, int oper, HashSet<String> casesPartial, String ID, String suffix) {
		Boolean check = solveResource(a, "=", b);
		if(suffix!=null || (oper!=2 && oper!=5)) { //Jump
		}else if(check == null){
			if(oper==2 && a == null && b == null) casesPartial.add(ID);
			else if(oper==5 && (a == null && b != null)) casesPartial.add(ID);
			else if(oper==5 && (a != null && b == null)) casesPartial.add(ID);
		}else if(oper==2 && check) {
			casesPartial.add(ID);
		}else if(oper==5 && !check) {
			casesPartial.add(ID);
		}
		return check;
	}
	
	public static void generateCaseCompareBoolean(Boolean a, Boolean b, int oper, HashSet<String> casesPartial, String ID) {
		boolean a1 = a;
		boolean b1 = b;
		if((oper==2 && a1==b1) || (oper==5 && a1!=b1)) {
			casesPartial.add(ID);
		}
	}
	
	public static LinkedList<String> getConsolidatedCases(LinkedList<HashSet<String>> cases, LinkedList<String> operator, String WorkDefID, WorkflowDefinition workflowDefinitionLayer) {
//		System.out.println(WorkDefID +" "+ cases);
		HashSet<String> finalCase = (HashSet<String>) cases.getFirst().clone();
		if(operator.size()>0) {
			for(int i=1; i<cases.size(); i++) {
				if(operator.get(i-1).equalsIgnoreCase(AND)) {
					HashSet<String> tmp = (HashSet<String>) finalCase.clone();
					tmp.removeAll(cases.get(i));
					finalCase.removeAll(tmp);
				} else if(operator.get(i-1).equalsIgnoreCase(OR)) {
					HashSet<String> tmp = (HashSet<String>) cases.get(i).clone();
					tmp.removeAll(finalCase);
					finalCase.addAll(tmp);
				} else if(operator.get(i-1).equalsIgnoreCase(ANDNOT)) {
					finalCase.removeAll(cases.get(i));
				} else if(operator.get(i-1).equalsIgnoreCase(ORNOT)) {
					String URI = workflowDefinitionLayer.getURI(WorkDefID);
					String Version = workflowDefinitionLayer.getVersion(WorkDefID);
					Vector<String> newCases = workflowDefinitionLayer.getIDs(null, false, 0, 0, 0, false, URI, Version);
					finalCase = new HashSet<String>();
					for(String ca : newCases) {
						finalCase.add(ca);
					}
					finalCase.removeAll(cases.get(i));
				}
			}
		}
		
		return new LinkedList<String>(finalCase);
	}
	
	public static String buildParam(String param) {
    	param = param.substring(1, param.length()-1);
		
		StringTokenizer st1 = new StringTokenizer(param);
		StringBuffer sb1 = new StringBuffer(st1.nextToken());
		String prevToken = null;
		String token = null;
		while(st1.hasMoreTokens()) {
			prevToken = token;
			token = st1.nextToken();
			if(token.equalsIgnoreCase(AND) || token.equalsIgnoreCase(OR) || token.equalsIgnoreCase(NOT)) {
				sb1.append(SPACE+token);
			}else if(prevToken != null && (prevToken.equalsIgnoreCase(AND) || prevToken.equalsIgnoreCase(OR) || prevToken.equalsIgnoreCase(NOT))) {
				sb1.append(SPACE+token);
			}else {
				sb1.append("_"+token);
			}
		}
		return sb1.toString();
	}
	
	public static Boolean solveResource(String resourceA, String operator, String resourceB) {
		if(resourceA == null || resourceA.equals("null") || resourceB == null || resourceB.equals("null")) return null;
		boolean res = false;
		boolean first = false;
		if(operator.contains("=")) {
			List<String> l = new LinkedList<String>();
			for(Element a1 : (List<Element>) JDOMUtil.stringToDocument(resourceA).getRootElement().getChildren()) {
				l.add(JDOMUtil.elementToString(a1));
			}
			Collections.sort(l);
			String a1 = l.toString();
			l.clear();
			for(Element b1 : (List<Element>) JDOMUtil.stringToDocument(resourceB).getRootElement().getChildren()) {
				l.add(JDOMUtil.elementToString(b1));
			}
			Collections.sort(l);
			String b1 = l.toString();
			return a1.equals(b1);
		}
		if(!res && !operator.contains("=")) {
			
			List<Element> listA = JDOMUtil.stringToDocument(resourceA).getRootElement().getChildren();
			List<Element> listB = JDOMUtil.stringToDocument(resourceB).getRootElement().getChildren();
			if(listA.size()==1) {
				if(listB.size()==1) {
					Element a = listA.get(0);
					Element b = listB.get(0);
					if(a.getName().equals(participant)) { //a participant
						res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
					}else { // a role
						if(b.getName().equals(participant)) { // b participant
							for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
								if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) res = true;
							}
							res = false;
						}else { // b role
							res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
						}
					}
				}else { // B size > 1
					Element a = listA.get(0);
					if(a.getName().equals(participant)) { // a participant
						res = false;
					}else { // a role
						boolean result = false;
						for(Element b : listB) {
							result = false;
							if(b.getName().equals(participant)) { // b participant
								for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
									if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) result = true;
								}
								if(!result) res = false;
							}else { // b role
								result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
							}
						}
						res = result;
					}
				}
			}else {
				if(listB.size()==1) {
					for(Element a : listA) {
						Element b = listB.get(0);
						if(a.getName().equals(participant)) { //a participant
							res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
						}else { // a role
							if(b.getName().equals(participant)) { // b participant
								for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
									if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) return true;
								}
								res = false;
							}else { // b role
								res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
							}
						}
					}
				}else { // B size > 1
					for(Element b : listB) {
						boolean result = false;
						for(Element a : listA) {
							if(a.getName().equals(participant)) { //a participant
								result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
							}else { // a role
								if(b.getName().equals(participant)) { // b participant
									for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
										if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) result = true;
									}
									result = false;
								}else { // b role
									result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
								}
							}
							if(result) break;
						}
						if(!result) res = false;
					}
					res = true;
				}
				res = false;
			}
			first = true;
		}
		if(!res && first) {
			res = solveResource(resourceB, YExpression.NOT, resourceA);
		}
		return res;
	}

//	@SuppressWarnings("unchecked")
//	public static Boolean solveResource(String resourceA, String operator, String resourceB) {
//		if(resourceA == null || resourceA.equals("null") || resourceB == null || resourceB.equals("null")) return null;
//		boolean res = false;
//		boolean first = false;
//		if(operator.contains("=")) {
//			StringBuffer sb = new StringBuffer();
//			for(Element a1 : (List<Element>) JDOMUtil.stringToDocument(resourceA).getRootElement().getChildren()) {
//				sb.append(JDOMUtil.elementToString(a1));
//			}
//			String a1 = sb.toString();
//			sb.delete(0, sb.length());
//			for(Element b1 : (List<Element>) JDOMUtil.stringToDocument(resourceB).getRootElement().getChildren()) {
//				sb.append(JDOMUtil.elementToString(b1));
//			}
//			res = a1.equals(sb.toString());
//		}
//		if(!res && operator.contains("=")) {
//			
//			List<Element> listA = JDOMUtil.stringToDocument(resourceA).getRootElement().getChildren();
//			List<Element> listB = JDOMUtil.stringToDocument(resourceB).getRootElement().getChildren();
//			if(listA.size()==1) {
//				if(listB.size()==1) {
//					Element a = listA.get(0);
//					Element b = listB.get(0);
//					if(a.getName().equals(participant)) { //a participant
//						res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//					}else { // a role
//						if(b.getName().equals(participant)) { // b participant
//							for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
//								if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) res = true;
//							}
//							res = false;
//						}else { // b role
//							res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//						}
//					}
//				}else { // B size > 1
//					Element a = listA.get(0);
//					if(a.getName().equals(participant)) { // a participant
//						res = false;
//					}else { // a role
//						boolean result = false;
//						for(Element b : listB) {
//							result = false;
//							if(b.getName().equals(participant)) { // b participant
//								for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
//									if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) result = true;
//								}
//								if(!result) res = false;
//							}else { // b role
//								result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//							}
//						}
//						res = result;
//					}
//				}
//			}else {
//				if(listB.size()==1) {
//					for(Element a : listA) {
//						Element b = listB.get(0);
//						if(a.getName().equals(participant)) { //a participant
//							res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//						}else { // a role
//							if(b.getName().equals(participant)) { // b participant
//								for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
//									if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) return true;
//								}
//								res = false;
//							}else { // b role
//								res = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//							}
//						}
//					}
//				}else { // B size > 1
//					for(Element b : listB) {
//						boolean result = false;
//						for(Element a : listA) {
//							if(a.getName().equals(participant)) { //a participant
//								result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//							}else { // a role
//								if(b.getName().equals(participant)) { // b participant
//									for(Element role : (List<Element>) b.getChild(roles).getChildren()) {
//										if(JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(role))) result = true;
//									}
//									result = false;
//								}else { // b role
//									result = JDOMUtil.elementToString(a).equals(JDOMUtil.elementToString(b));
//								}
//							}
//							if(result) break;
//						}
//						if(!result) res = false;
//					}
//					res = true;
//				}
//				res = false;
//			}
//			first = true;
//		}
//		if(!res && first) {
//			res = solveResource(resourceB, YExpression.NOT, resourceA);
//		}
//		return res;
//	}
	
	public static String[] populatePreVarPostVarSuffix(String variableFull) {
    	String preVar = null;
		String postVar = null;
		String suffix = null;
		
    	int oper = -1;
		if(variableFull.contains(YExpression.MINOREQUAL)) {
			oper = 1;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.MINOREQUAL)+2);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.MINOREQUAL));
		}else if(variableFull.contains(YExpression.GREATEREQUAL)) {
			oper = 3;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.GREATEREQUAL)+2);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.GREATEREQUAL));
		}else if(variableFull.contains(YExpression.NOTEQUAL)) {
			oper = 5;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.NOTEQUAL)+2);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.NOTEQUAL));
		}else if(variableFull.contains(YExpression.SINGLEEQUAL)) {
			oper = 2;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.SINGLEEQUAL)+1);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.SINGLEEQUAL));
		}else if(variableFull.contains(YExpression.MINOR)) {
			oper = 0;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.MINOR)+1);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.MINOR));
		}else if(variableFull.contains(YExpression.GREATER)) {
			oper = 4;
			postVar = variableFull.substring(variableFull.indexOf(YExpression.GREATER)+1);
			variableFull = variableFull.substring(0, variableFull.indexOf(YExpression.GREATER));
		}else return null;
		if(variableFull.contains(YExpression.DOT)) {
			preVar = variableFull.substring(0, variableFull.indexOf(YExpression.DOT));
			suffix = variableFull.substring(variableFull.indexOf(YExpression.DOT)+1);
		}else {
			preVar = variableFull;
		}
		
		return new String[] {""+oper, preVar, postVar, suffix}; 
	}

	public static void populateActivitySubProcessActionAndOperator(String param, LinkedList<String> activitySubProcess, LinkedList<String> action, LinkedList<String> operator) {
    	if(param.contains(SPACE)) {
			String param2 = param.substring(0, param.indexOf(SPACE));
			param = param.substring(param.indexOf(SPACE)+1);
			if(param2.contains(YExpression.DOT)) {
				activitySubProcess.add(param2.substring(0, param2.indexOf(YExpression.DOT)));
				action.add(param2.substring(param2.indexOf(YExpression.DOT)+1));
			}else {
				activitySubProcess.add(param2.substring(0, param2.indexOf(YExpression.PARTA)));
				action.add(param2.substring(param2.indexOf(YExpression.PARTA)));
			}
			while(param.contains(SPACE)){
				String oper1 = param.substring(0, param.indexOf(SPACE));
				param = param.substring(param.indexOf(SPACE)+1);
				if((oper1.equalsIgnoreCase(AND) || oper1.equalsIgnoreCase(OR)) && param.contains(SPACE)) {
					String oper2 = param.substring(0, param.indexOf(SPACE));
					if(oper2.equalsIgnoreCase(NOT)) {
						oper1 = oper1+NOT;
						param = param.substring(param.indexOf(SPACE)+1);
					}
				}
				String param1 = null;
				if(param.contains(SPACE)) {
					param1 = param.substring(0, param.indexOf(SPACE));
					param = param.substring(param.indexOf(SPACE)+1);
				}else {
					param1 = param;
				}
				if(param1.contains(YExpression.DOT)) {
					activitySubProcess.add(param1.substring(0, param1.indexOf(YExpression.DOT)));
					action.add(param1.substring(param1.indexOf(YExpression.DOT)+1));
				}else {
					activitySubProcess.add(param1.substring(0, param1.indexOf(YExpression.PARTA)));
					action.add(param1.substring(param1.indexOf(YExpression.PARTA)));
				}
				operator.add(oper1);
			}
		} else {
			if(param.contains(YExpression.DOT)) {
				activitySubProcess.add(param.substring(0, param.indexOf(YExpression.DOT)));
				action.add(param.substring(param.indexOf(YExpression.DOT)+1));
			}else {
				activitySubProcess.add(param.substring(0, param.indexOf(YExpression.PARTA)));
				action.add(param.substring(param.indexOf(YExpression.PARTA)));
			}
		}
	}
	
	/**
     * Execute the pathQuery in the element
     * @param expression the query to execute
     * @param element the element on which execute the query
     * @return the result of the query
     */
    @SuppressWarnings("unchecked")
	public static Object getValueFromXML(String expression, String element){
    	LinkedList<String> list = new LinkedList<String>();
    	Document doc = JDOMUtil.stringToDocument(element);
    	String attribute = null;
    	Element e = doc.getRootElement();
    	expression = expression.replace('.', '/');
    	expression = "/"+e.getName()+expression;
    	if(expression.contains("@")) {
    		attribute = expression.substring(expression.indexOf("@")+1);
    		expression = expression.substring(0, expression.indexOf("/@"));
    	}
    	try {
			String result = SaxonUtil.evaluateQuery(expression, doc);
			Element e1 = JDOMUtil.stringToDocument("<Att>"+result+"</Att>").getRootElement();
			for(Element e2 : (List<Element>) e1.getChildren()) {
				if(attribute != null) {
					String att = e2.getAttributeValue(attribute);
					if(att != null)	list.addLast(att);
				}else {
					if(e2.getChildren().size()>0) {
						StringBuffer sb = new StringBuffer();
						for(Element e3 : (List<Element>) e2.getChildren()) {
							String elem = JDOMUtil.elementToString(e3);
							sb.append(elem);
						}
						list.addLast(sb.toString());
					}else {
						String text = e2.getText();
						if(text.length()>0) list.addLast(text);
					}
				}
			}
		} catch (SaxonApiException sae) { sae.printStackTrace();}
		if(list.size()==0) return null;
		if(list.size()==1) return list.getFirst();
		return list;
    }
    
    /**
	 * Convert the Resource from the logFormat to an easy one
	 * @return the Resource from the logFormat to an easy one
	 */
	public static String createResource(Role roleLayer, String taskname, LinkedList<ActivityRoleTuple> resources, int resourceType){
		if(resources != null) {
			boolean empty = true;
	    	String taskID = taskname;
	    	StringBuffer sb = new StringBuffer();
			HashSet<String> offer = new HashSet<String>();
	    	HashSet<String> allocate = new HashSet<String>();
	    	HashSet<String> start = new HashSet<String>();
	    	HashSet<String> complete = new HashSet<String>();
			for(ActivityRoleTuple vector : resources) {
				String role = vector.getRoleID();
				String event = vector.getStatus();
				
				if('o' == event.charAt(0)) {
	    			offer.add(role);
	    			allocate.clear();
	    			start.clear();
	    			complete.clear();
	    		}else if('a' == event.charAt(0)) {
	    			allocate.add(role);
	    			start.clear();
	    			complete.clear();
	    		}else if('s' == event.charAt(0)) {
	    			start.add(role);
	    			complete.clear();
	    		}else if('c' == event.charAt(0)) {
	    			complete.add(role);
	    		}
			}
//			sb.append("<"+taskID+">");
	    	if(resourceType == Role.offerResourceType && offer.size()>0) {
	    		empty = false;
		    	sb.append(offerXMLopen);
		    	for(String s: offer) {
//		    		sb.append(roleLayer.getRoleInfo(s));
		    		sb.append("<participant>");
		    		sb.append(s);
		    		sb.append("</participant>");
		    	}
		    	sb.append(offerXMLclose);
	    	}else if(resourceType == Role.allocateResourceType && allocate.size()>0) {
	    		empty = false;
		    	sb.append(allocateXMLopen);
		    	for(String s: offer) {
//		    		sb.append(roleLayer.getRoleInfo(s));
		    		sb.append("<participant>");
		    		sb.append(s);
		    		sb.append("</participant>");
		    	}
		    	sb.append(allocateXMLclose);
	    	}else if(resourceType == Role.startResourceType && start.size()>0) {
	    		empty = false;
		    	sb.append(startXMLopen);
		    	for(String s: start) {
//		    		sb.append(roleLayer.getRoleInfo(s));
		    		sb.append("<participant>");
		    		sb.append(s);
		    		sb.append("</participant>");
		    	}
		    	sb.append(startXMLclose);
	    	}else if(resourceType == Role.completeResourceType && complete.size()>0) {
	    		empty = false;
		    	sb.append(completeXMLopen);
		    	for(String s: complete) {
//		    		sb.append(roleLayer.getRoleInfo(s));
		    		sb.append("<participant>");
		    		sb.append(s);
		    		sb.append("</participant>");
		    	}
		    	sb.append(completeXMLclose);
	    	}
//	    	sb.append("</"+taskID+">");
	    	if(empty) return null;
	    	else return sb.toString();
		}else {
			return null;
		}
    }
	
    /**
     * Return true if the Specification use Sensors
     * @return Return true if the Specification use Sensors
     */
	public static boolean needSensors(String sensors) {
		if(sensors!=null) return true;
		else return false;
	}
	
	public static Object getLogVariableActivityVariable(Activity activityLayer, Variable variableLayer, String taskName, String oldTaskName, String WorkDefID) {
    	HashMap<String, String> variable = new HashMap<String, String>();
    	LinkedList<String> WorkDefIDs = new LinkedList<String>();
    	WorkDefIDs.add(WorkDefID);
    	    	
    	LinkedList<ActivityTuple> variablesIDs = activityLayer.getVariablesIDs(null, false, taskName, true, WorkDefIDs, true, 0, false, false);

//    	Vector<String> variablesID = variablesIDs.firstElement();
    	
    	generateVariables(variable, variablesIDs, variableLayer);
    	
    	return generateVariableXML(oldTaskName, variable);
	}
    
	public static Object getLogVariableActivityInformation(Activity activityLayer, String logTaskItem, String taskName, String WorkDefID) {
		String variable = null;
		if(logTaskItem.endsWith(YSensorUtilities.countElements)) return "1";
		
		int typeTime = YSensorUtilities.detectActivityType(logTaskItem);
		
		if(logTaskItem.endsWith(YSensorUtilities.count)) {
			LinkedList<String> WorkDefIDs = new LinkedList<String>();
			WorkDefIDs.add(WorkDefID);
			LinkedList<ActivityTuple> count = activityLayer.getCounts(taskName, true, WorkDefIDs, true); 
			if(count!=null) {
				return count.getFirst().getCount();
			}
			return "0";
		}else if(logTaskItem.endsWith(YSensorUtilities.timeInMillis) || logTaskItem.endsWith("ed)")) {
			LinkedList<ActivityTuple> time = YSensorUtilities.getTaskTime(WorkDefID, activityLayer, taskName, typeTime);
			if(time!=null) {
				if(logTaskItem.endsWith(YSensorUtilities.timeInMillis)) {
					variable = ""+(new Long(time.getFirst().getTime()));
				}else {
					variable = YSensorUtilities.trueString;
				}
			}else {
				if(logTaskItem.endsWith("ed)")) {
					variable = YSensorUtilities.falseString;
				}
			}
			return variable;
		}
		
		return null;
	}
	
    public static Object getLogVariableNetVariable(SubProcess subProcessLayer, Variable variableLayer, String taskName, String WorkDefID) {
    	Vector<String> VariableID = subProcessLayer.getVariablesIDs(null, false, taskName, true, WorkDefID, true, 0, 0, 0, false, false);
		Vector<Vector<String>> variable = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, VariableID.lastElement(), true, false);
		HashMap<String, String> var = new HashMap<String, String>();
		if(variable!=null) {
			for(Vector<String> vec : variable) {
				var.put(vec.get(1), vec.get(2));
			}
		}
		
		return generateVariableXML(taskName, var);
	}
    
    public static Object getLogVariableNetInformation(SubProcess subProcessLayer, String logTaskItem, String taskName, String WorkDefID) {
		int typeTime = 0;
		String variable = null;
		
		if(logTaskItem.endsWith(YSensorUtilities.completeTimeInMillis)) typeTime = SubProcess.Complete;
		else if(logTaskItem.endsWith(YSensorUtilities.startTimeInMillis)) typeTime = SubProcess.Start;
		else if(logTaskItem.endsWith(YSensorUtilities.isCompleted)) typeTime = SubProcess.Complete;
		else if(logTaskItem.endsWith(YSensorUtilities.isStarted)) typeTime = SubProcess.Start;
		
		if(logTaskItem.endsWith(YSensorUtilities.timeInMillis)) {
			Vector<String> time = subProcessLayer.getTimes(null, false, taskName, true, WorkDefID, true, typeTime, true, null, false, false);
			if(time!=null) {
				variable = ""+(new Long(time.firstElement()));
			}
			return variable;
		}else {
			Vector<String> time = subProcessLayer.getTimes(null, false, taskName, true, WorkDefID, true, typeTime, true, null, false, false);
			if(time!=null) {
				variable = YSensorUtilities.trueString;
			}else {
				variable = YSensorUtilities.falseString;
			}
			return variable;
		}
	}
    
    public static String checkIfXML(String res) {
    	if('<' == res.charAt(0) && '>' == res.charAt(res.length()-1)) {
    		if(res.contains("<?xml version=")) {
    			res = res.substring(res.indexOf(">")+1);
    		}
			res = JDOMUtil.formatXMLString(res);
		}
    	return res;
    }
    
    public static String preprocessResorce(String value, String mapping) {
    	if(value!=null && value.contains(">") && !mapping.contains(resource)) {
			String start = value.substring(0, value.indexOf(">")+1);
			String end = value.substring(value.length()-start.length()-1);
			if(start.substring(1).equals(end.substring(2))) {
				value = value.substring(start.length(), value.length()-end.length());
			}
		}
    	return value;
    }
    
    public static int detectActivityType(String preVar) {
    	int typeTime = 0;
		int checkType = 0;
		if(preVar.endsWith("me)")) checkType = 1;
		else if(preVar.endsWith("s)")) checkType = 2;
		else if(preVar.endsWith("d)")) checkType = 3;
		else if(preVar.endsWith("se)")) checkType = 4;

		if(checkType == 1) {
			if(preVar.endsWith(completeTime)) typeTime = Activity.Completed;
			else if(preVar.endsWith(startTime)) typeTime = Activity.Executing;
			else if(preVar.endsWith(offerTime)) typeTime = Activity.Enabled;
		}else if(checkType == 2) {
			if(preVar.endsWith(completeTimeInMillis)) typeTime = Activity.Completed;
			else if(preVar.endsWith(startTimeInMillis)) typeTime = Activity.Executing;
			else if(preVar.endsWith(offerTimeInMillis)) typeTime = Activity.Enabled;
		}else if(checkType == 3) {
			if(preVar.endsWith(isCompleted)) typeTime = Activity.Completed;
			else if(preVar.endsWith(isStarted)) typeTime = Activity.Executing;
			else if(preVar.endsWith(isOffered)) typeTime = Activity.Enabled;
		}else if(checkType == 4) {
			if(preVar.endsWith(completeResource)) typeTime = Activity.Completed;
			else if(preVar.endsWith(startResource)) typeTime = Activity.Executing;
			else if(preVar.endsWith(allocateResource)) typeTime = Activity.Allocated;
			else if(preVar.endsWith(offerResource)) typeTime = Activity.Enabled;
		}
		
		return typeTime;
    }
    
    public static LinkedList<ActivityTuple> getTaskTime(String WorkDefID, Activity activityLayer, String taskName, int typeTime) {
    	LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		
		return activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, typeTime, true, null, false, false);
    }
    
    public static void produceResourceVariable(String method, HashSet<String> resourceTask, HashMap<String, String> mappingName, StringBuffer sb, String var, String event, int check) {
    	int off = check*5;
    	if(method.endsWith("st")) {
    		resourceTask.add(var+"_"+(off-4));
    		mappingName.put(var+"_"+(off-4), var+"_"+(off-4));
    		sb.append(var+"_"+(off-4));
    	}else if(method.equals(event+"Number")) {
    		resourceTask.add(var+"_"+(off-3));
    		mappingName.put(var+"_"+(off-3), var+"_"+(off-3));
    		sb.append(var+"_"+(off-3));
    	}else if('r' == method.charAt(method.length()-1)) {
    		resourceTask.add(var+"_"+(off-2));
    		mappingName.put(var+"_"+(off-2), var+"_"+(off-2));
    		sb.append(var+"_"+(off-2));
    	}else if(method.contains(event+"MinNumberExcept")) {
    		String task = method.substring(method.indexOf("")+1);
    		resourceTask.add(var+"_"+task+"_"+(off-1));
    		mappingName.put(var+"_"+(off-1), var+"_"+(off-1));
    		sb.append(var+"_"+task+"_"+(off-1));
    	}else if(method.contains("Contain")) {
    		String task = method.substring(method.indexOf("")+1);
    		resourceTask.add(var+"_"+task+"_"+(off));
    		mappingName.put(var+"_"+(off), var+"_"+(off));
    		sb.append(var+"_"+task+"_"+(off));
    	}
    }
    
    public static void produceResourceVariable(String method, HashSet<String> resourceTask, HashMap<String, String> mappingName, String var, String event, int check) {
    	int off = check*5;
    	if(method.endsWith("st")) {
    		resourceTask.add(var+"_"+(off-4));
    		mappingName.put(var+"_"+(off-4), var+"_"+(off-4));
    	}else if(method.equals(event+"Number")) {
    		resourceTask.add(var+"_"+(off-3));
    		mappingName.put(var+"_"+(off-3), var+"_"+(off-3));
    	}else if('r' == method.charAt(method.length()-1)) {
    		resourceTask.add(var+"_"+(off-2));
    		mappingName.put(var+"_"+(off-2), var+"_"+(off-2));
    	}else if(method.contains(event+"MinNumberExcept")) {
    		String task = method.substring(method.indexOf("")+1);
    		resourceTask.add(var+"_"+task+"_"+(off-1));
    		mappingName.put(var+"_"+(off-1), var+"_"+(off-1));
    	}else if(method.contains("Contain")) {
    		String task = method.substring(method.indexOf("")+1);
    		resourceTask.add(var+"_"+task+"_"+(off));
    		mappingName.put(var+"_"+(off), var+"_"+(off));
    	}
    }
    
    public static boolean checkIfInteger(String number) {
    	try {
			new Integer(number);
			return true;
		}catch(NumberFormatException nfe) {
			return false;
		}
    }
    
    public static void generateVariables(HashMap<String, String> variable, LinkedList<ActivityTuple> variablesIDs, Variable variableLayer) {
//    	String WorkDefID = variablesID.firstElement();
    	for(ActivityTuple variableID : variablesIDs) {

    		if(!variableID.equals("-1")) {// && !variableID.equals(WorkDefID)) {
				
				generateMatrixVariable(variable, variableLayer, variableID.getVariableID());
				
			}
    	}
    }
    
    public static void generateMatrixVariable(HashMap<String, String> variable, Variable variableLayer, String variableID) {
    	Vector<Vector<String>> matrixVar = variableLayer.getRows(null, false, null, false, 0, 0, null, false, 0, null, false, variableID, true, true);
    	if(matrixVar!=null) {	
			for(Vector<String> vectorVar : matrixVar) {
    			String variableName = vectorVar.get(1);
    			String value = vectorVar.get(2);
    			if(!value.startsWith(YExpression.MINOR+variableName+YExpression.GREATER)) {
					value = YExpression.MINOR+variableName+YExpression.GREATER+value+"</"+variableName+YExpression.GREATER;
					variable.put(variableName, value);
				}else {
					variable.put(variableName, value);
				}
    		}
    	}
    }
    
    public static String generateVariableXML(String oldTaskName, HashMap<String, String> variable) {    	
    	StringBuffer sb = new StringBuffer();
    	if(variable.size()>0) {
    		sb.append(YExpression.MINOR+oldTaskName+YExpression.GREATER);
    		for(Entry<String, String> entry : variable.entrySet()) {
    			sb.append(entry.getValue());
    		}
    		sb.append("</"+oldTaskName+YExpression.GREATER);
    		return sb.toString();
    	}else {
    		return null;
    	}
    }
    
    public static String checkIfString(Object o) {
    	if(o!=null && o instanceof String) {
			return (String) o;
		}else {
			return null;
		}
    }
    
    public static String getVariableValue(String value, String suffix) {
		if(suffix!=null) {
			Object o = YSensorUtilities.getValueFromXML(YExpression.DOT+suffix, value);
			
			return YSensorUtilities.checkIfString(o);
			
		}else {
			if(value.contains(YExpression.MINOR) && value.contains("</") && value.contains(YExpression.GREATER)) {
				Object o = YSensorUtilities.getValueFromXML("", value);
				
				return YSensorUtilities.checkIfString(o);
			}
		}
		return value;
    }
    
    public static void populateCaseIDActivityVariable(String value, String postVar, String suffix, int oper, String ID, HashSet<String> casesPartial) {
    	try {
    		double a = new Double(value);
    		double b = new Double(postVar);
    		
    		YSensorUtilities.generateCaseCompareDouble(a, b, oper, casesPartial, ID, suffix);
    		
    	} catch (NumberFormatException nfe) {
    		try {
    			Date a = originalDateFormat.parse(value);
    			Date b = originalDateFormat.parse(postVar);
    			
    			YSensorUtilities.generateCaseCompareDate(a, b, oper, casesPartial, ID, suffix);

    		} catch (ParseException e) {
    			String a = value;
    			String b = postVar;
    			if(a==null) { //Jump
    				if(oper==2 && b==null) casesPartial.add(ID);
    				if(oper==5 && b!=null) casesPartial.add(ID);
    			}else if(b==null ) { //Jump
    				if(oper==5 && a!=null) casesPartial.add(ID);
    			}else {
    				YSensorUtilities.generateCaseCompareString(a, b, oper, casesPartial, ID, suffix);
    			}
    		}
    	}
    }
    
    public static int[] findClosure(String input, String openBracket, String closesBracket) {
		StringTokenizer st = new StringTokenizer(input, openBracket+closesBracket, true);
		int open = input.indexOf(openBracket);
		int count = 0;
		StringBuffer sb = new StringBuffer();
		
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			
			sb.append(token);
			
			if(token.equals(openBracket)) count++;
			else if(token.equals(closesBracket)) count--;
			
			if(count == 0) return new int[] {open, sb.length()-1};
		}
		return null;
	}

	public static int getResourceType(String logTaskItem) {
		if(logTaskItem.endsWith(offerResource)) return Role.offerResourceType;
		else if(logTaskItem.endsWith(allocateResource)) return Role.allocateResourceType;
		else if(logTaskItem.endsWith(startResource)) return Role.startResourceType;
		else if(logTaskItem.endsWith(completeResource)) return Role.completeResourceType;
		else return -1;
	}
}
