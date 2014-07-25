package org.yawlfoundation.yawl.sensors.actions.netactions.concreteactions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;

public class NetPassTime {
	
	private static SubProcess subProcessLayer = null;
    private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void setLayers(SubProcess subProcessLayer) {
		NetPassTime.subProcessLayer = subProcessLayer;
	}

	public static Object getActionResult(String WorkDefID, String netName) {
		Vector<String> timeComplete = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, SubProcess.Complete, true, null, false, false);
		Vector<String> timeStart = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, SubProcess.Start, true, null, false, false);
		if(timeStart != null) {
			if(timeComplete != null) {
				Long dif = Long.parseLong(timeComplete.firstElement())-Long.parseLong(timeStart.firstElement());
				return originalDateFormat.format(new Date(dif));
			}else {
				Long dif = System.currentTimeMillis()-Long.parseLong(timeStart.firstElement());
				return originalDateFormat.format(new Date(dif));
			}
		}else {
			return null;
		}
	}
	
	public static Object getActionResultList(String WorkDefID, String netName) {
		Vector<String> timeComplete = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, SubProcess.Complete, true, null, false, true);
		Vector<String> timeStart = subProcessLayer.getTimes(null, false, netName, true, WorkDefID, true, SubProcess.Start, true, null, false, true);
		if(timeStart != null) {
			return discoveryCouples(timeStart, timeComplete);
		}else {
			return null;
		}
	}
	
	private static List<String> discoveryCouples(Vector<String> timeStart, Vector<String> timeComplete) {
		LinkedList<String> result = new LinkedList<String>();
		
		long[] start = new long[timeStart.size()-1];
		for(int i = 0; i<start.length; i++) {
			start[i] = Long.parseLong(timeStart.firstElement());
		}
		
		long[] complete = null;
		if(timeComplete != null) {
			complete = new long[timeComplete.size()-1];
			for(int i = 0; i<complete.length; i++) {
				complete[i] = Long.parseLong(timeComplete.firstElement());
			}
		}
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		for(int i = 0; i<start.length; i++) {
			boolean inserted = false;
			if(complete != null) {
				for(int j = 0; j < complete.length; j++) {
					if(start[i] < complete[j]) {
						map.put(i, j);
						inserted = true;
						break;
					}
				}
			}
			if(!inserted) map.put(i, -1);
		}
		
		Integer i = null;
		Integer pos = null;
		for(Entry<Integer, Integer> entry : map.entrySet()) {
			i = entry.getKey();
			pos = entry.getValue();
			if(pos > -1) {
				result.add(originalDateFormat.format(new Date(complete[pos]-start[i])));
			}else {
				result.add(originalDateFormat.format(new Date(System.currentTimeMillis()-start[i])));
			}
		}
		
		return result;
	}

	public static Object getActionResult(LinkedList<String> workDefIDs, String netName) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String) getActionResult(caseID, netName));
		}
		return result;	
	}
	
	public static Object getActionResultList(LinkedList<String> workDefIDs, String netName) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String) getActionResultList(caseID, netName));
		}
		return result;	
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		try {
			long longTime = (originalDateFormat.parse(postVar)).getTime();
			for(String workDefID : workDefIDs) {
				Vector<String> timeComplete = subProcessLayer.getTimes(null, false, activity, true, workDefID, true, SubProcess.Complete, true, null, false, false);
				Vector<String> timeStart = subProcessLayer.getTimes(null, false, activity, true, workDefID, true, SubProcess.Start, true, null, false, false);
				
				long passTime = -1;
				
				if(timeStart != null) {
					if(timeComplete != null) {
						passTime = Long.parseLong(timeComplete.firstElement())- Long.parseLong(timeStart.firstElement());
					}else {
						passTime = System.currentTimeMillis()-Long.parseLong(timeStart.firstElement());
					}
					
					if(oper==0 && passTime < longTime) casesPartial.add(workDefID);
					else if(oper==1 && passTime <= longTime) casesPartial.add(workDefID);
					else if(oper==2 && passTime == longTime) casesPartial.add(workDefID);
					else if(oper==3 && passTime >= longTime) casesPartial.add(workDefID);
					else if(oper==4 && passTime > longTime) casesPartial.add(workDefID);
					else if(oper==5 && passTime != longTime) casesPartial.add(workDefID);
				}
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return casesPartial;
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		try {
			long longTime = (originalDateFormat.parse(postVar)).getTime();
			for(String workDefID : workDefIDs) {
				List<String> pass = (List<String>) getActionResultList(workDefID, activity);
				
				for(String s : pass) {
					long passTime = (originalDateFormat.parse(s)).getTime();
					
					if(oper==0 && passTime < longTime) casesPartial.add(workDefID);
					else if(oper==1 && passTime <= longTime) casesPartial.add(workDefID);
					else if(oper==2 && passTime == longTime) casesPartial.add(workDefID);
					else if(oper==3 && passTime >= longTime) casesPartial.add(workDefID);
					else if(oper==4 && passTime > longTime) casesPartial.add(workDefID);
					else if(oper==5 && passTime != longTime) casesPartial.add(workDefID);
				}
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return casesPartial;
	}

}
