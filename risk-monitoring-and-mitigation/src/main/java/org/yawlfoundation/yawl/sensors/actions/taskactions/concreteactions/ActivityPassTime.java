package org.yawlfoundation.yawl.sensors.actions.taskactions.concreteactions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;

public class ActivityPassTime {
	
	private static Activity activityLayer = null;
    private static DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void setLayers(Activity activityLayer) {
		ActivityPassTime.activityLayer = activityLayer;
	}

	public static Object getActionResult(String WorkDefID, String taskName) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> timeComplete =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, Activity.Completed, true, null, false, false);
		LinkedList<ActivityTuple> timeStart =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, Activity.Completed, true, null, false, false);
		if(timeStart != null) {
			if(timeComplete != null) {
				Long dif = Long.parseLong(timeComplete.getFirst().getTime())-Long.parseLong(timeStart.getFirst().getTime());
				return originalDateFormat.format(new Date(dif));
			}else {
				Long dif = System.currentTimeMillis()-Long.parseLong(timeStart.getFirst().getTime());
				return originalDateFormat.format(new Date(dif));
			}
		}else {
			return null;
		}
	}
	
	public static Object getActionResultList(String WorkDefID, String taskName) {
		LinkedList<String> WorkDefIDs = new LinkedList<String>();
		WorkDefIDs.add(WorkDefID);
		LinkedList<ActivityTuple> timeComplete =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, Activity.Completed, true, null, false, true);
		LinkedList<ActivityTuple> timeStart =  activityLayer.getTimes(null, false, taskName, true, WorkDefIDs, true, Activity.Completed, true, null, false, true);
		if(timeStart != null) {
			return discoveryCouples(timeStart, timeComplete);
		}else {
			return null;
		}
	}
	
	private static List<String> discoveryCouples(LinkedList<ActivityTuple> timeStart, LinkedList<ActivityTuple> timeComplete) {
		LinkedList<String> result = new LinkedList<String>();
		
		long[] start = new long[timeStart.size()];
		for(int i = 0; i<start.length; i++) {
			start[i] = Long.parseLong(timeStart.get(i).getTime());
		}
		
		long[] complete = null;
		if(timeComplete != null) {
			complete = new long[timeComplete.size()];
			for(int i = 0; i<complete.length; i++) {
				complete[i] = Long.parseLong(timeComplete.get(i).getTime());
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

	public static Object getActionResult(List<String> workDefIDs, String taskName) {
		LinkedList<String> result = new LinkedList<String>();
		for(String caseID : workDefIDs) {
			result.add((String)getActionResult(caseID, taskName));
		}
		return result;	
	}
	
	public static Object getActionResultList(List<String> workDefIDs, String taskName) {
		LinkedList<Object> result = new LinkedList<Object>();
		for(String caseID : workDefIDs) {
			result.add(getActionResultList(caseID, taskName));
		}
		return result;	
	}
	
	public static HashSet<String> getExpressionActionResult(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		try {
			long longTime = originalDateFormat.parse(postVar).getTime();
			long passTime = -1;
			
			LinkedList<ActivityTuple> timeStart = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, Activity.Executing, true, null, false, false);
			LinkedList<ActivityTuple> timeComplete = activityLayer.getRows(null, false, activity, true, workDefIDs, true, 0, 0, Activity.Completed, true, null, false, false);
			
			for(ActivityTuple activityID : timeStart) {
				long startTime = Long.parseLong(activityID.getTime());
				Long completeTime = null;
				String caseID = activityID.getWorkDefID();
				
				for(ActivityTuple activityID2 : timeComplete) {
					if(activityID2.getWorkDefID().equals(caseID)) {
						completeTime = Long.parseLong(activityID2.getTime());
						break;
					}
				}
				
				if(completeTime != null) {
					passTime = completeTime - startTime;
				}else {
					passTime = System.currentTimeMillis() - startTime;
				}
				
				if(oper==0 && passTime < longTime) casesPartial.add(activityID.getWorkDefID());
				else if(oper==1 && passTime <= longTime) casesPartial.add(activityID.getWorkDefID());
				else if(oper==2 && passTime == longTime) casesPartial.add(activityID.getWorkDefID());
				else if(oper==3 && passTime >= longTime) casesPartial.add(activityID.getWorkDefID());
				else if(oper==4 && passTime > longTime) casesPartial.add(activityID.getWorkDefID());
				else if(oper==5 && passTime != longTime) casesPartial.add(activityID.getWorkDefID());
				
			}
		}catch (NumberFormatException nfe) {
			if(!postVar.contains("null")) nfe.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return casesPartial;
		
	}
	
	public static HashSet<String> getExpressionActionResultList(String activity, int oper, String preVar, String postVar, String suffix, LinkedList<String> workDefIDs, HashSet<String> casesPartial) {
		try {
			long longTime = originalDateFormat.parse(postVar).getTime();
			long passTime = -1;
			
			for(String workDefID : workDefIDs) {
				List<String> pass = (List<String>) getActionResultList(workDefID, activity);
				
				for(String s : pass) {
					
					passTime = originalDateFormat.parse(s).getTime();
				
					if(oper==0 && passTime < longTime) casesPartial.add(workDefID);
					else if(oper==1 && passTime <= longTime) casesPartial.add(workDefID);
					else if(oper==2 && passTime == longTime) casesPartial.add(workDefID);
					else if(oper==3 && passTime >= longTime) casesPartial.add(workDefID);
					else if(oper==4 && passTime > longTime) casesPartial.add(workDefID);
					else if(oper==5 && passTime != longTime) casesPartial.add(workDefID);
					
				}
				
			}
		}catch (NumberFormatException nfe) {
			if(!postVar.contains("null")) nfe.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return casesPartial;
		
	}

}
