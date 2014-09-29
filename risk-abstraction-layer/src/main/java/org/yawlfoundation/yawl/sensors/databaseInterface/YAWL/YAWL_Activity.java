package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import org.jdom2.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.sensors.databaseInterface.Activity;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityTuple;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.jdbcImpl;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class YAWL_Activity extends Activity {
	
//	
//	select a.engineinstanceid, b.taskid, b.taskname, d.eventtime, d.descriptor, c.parenttaskinstanceid
//	from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d
//	where (d.descriptor='Enabled')
//	and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and a.netinstanceid not in 
//	(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2
//	where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid
//	and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid and (d1.descriptor='Enabled' and d2.descriptor='Enabled')
//	and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid )
//	union
//	select a.engineinstanceid, b.taskid, b.taskname, d.eventtime, d.descriptor, c.parenttaskinstanceid
//	from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d
//	where (d.descriptor='Executing')
//	and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid
//	and a.netinstanceid not in 
//	(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2
//	where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid
//	and (d1.descriptor='Executing' and (d2.descriptor='Enabled' or d2.descriptor='Executing')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)
//	union
//	select a.engineinstanceid, b.taskid, b.taskname, d.eventtime, d.descriptor, c.parenttaskinstanceid
//	from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d
//	where (d.descriptor='Complete')
//	and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and a.netinstanceid not in 
//	(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2
//	where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid
//	and (d1.descriptor='Complete' and (d2.descriptor='Enabled' or d2.descriptor='Executing' or d2.descriptor='Complete')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)
//	
	
	private static jdbcImpl jdbc = new jdbcImpl();
	private static InterfaceB_EnvironmentBasedClient _interfaceBClient = new InterfaceB_EnvironmentBasedClient("http://localhost:8080/yawl/ib");
	private static String _user = "sensorService";
	private static String _password = "ySensor";
	private static String session = null; 
	
	@Override
	public LinkedList<ActivityTuple> getAllID() {
		
		String Query = "select distinct logtask.taskid from logtask";
		ResultSet rs = jdbc.execSelect(Query);
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		
		ActivityTuple activityTuple = null;
		try {
			while(rs.next()) {
				activityTuple = new ActivityTuple();
				activityTuple.setTaskID(""+rs.getObject(1));
				result.add(activityTuple);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
	}
	
	@Override
	public boolean isActivity(String WorkDefID, String name) {
		String Query = "select distinct b.taskname " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c " +
				"where a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and b.taskname like '"+name+"' and (a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%')";
		
		ResultSet rs = jdbc.execSelect(Query);
		try {
			while(rs.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public LinkedList<ActivityTuple> getIDs(String Name, boolean isName, LinkedList<String> WorkDefIDs,
			boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		LinkedList<String> option = new LinkedList<String>();
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		
		//Create the option for the query
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(WorkDefIDs!=null) {
			if(WorkDefIDs.size()>1) {
				String s = "(";
				for(String WorkDefID : WorkDefIDs) {
					if(isWorkDefID) s+="(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') or ";
					else s+="(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') or ";
				}
				s = s.substring(0, s.length()-4);
				s+=") and ";
			}else {
				String WorkDefID = WorkDefIDs.getFirst();
				if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
				else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
			}
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor!='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor!='Complete' and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select descriptor, b.taskid, a.engineinstanceid, b.taskname from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
		"where (d.descriptor='Enabled' or d.descriptor='Executing' or d.descriptor='Complete') and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid order by eventtime";
				
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<ActivityTuple>> mapEnabled = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, LinkedList<ActivityTuple>> mapExecuting = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, LinkedList<ActivityTuple>> mapComplete = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, String> mapTMP = new HashMap<String, String>();
		
		ActivityTuple at = null;
		
		String id = "";
		try {
			while(rs.next()) {
				String caseid = rs.getString(3);
				if(caseid.contains("")) {
					caseid = caseid.substring(0, caseid.indexOf(""));
				}
				id = caseid+rs.getString(4);
				if(!mapTMP.containsKey(id)) {
					mapEnabled.put(id, new LinkedList<ActivityTuple>());
					mapExecuting.put(id, new LinkedList<ActivityTuple>());
					mapComplete.put(id, new LinkedList<ActivityTuple>());
					mapTMP.put(id, "Enabled");
				}
				String status = rs.getString(1);
				if(status.equals("Enabled")){
					if(!isList && mapTMP.get(id).equals("Enabled")) {
						mapEnabled.get(id).clear();
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					
					at = new ActivityTuple();
					at.setWorkDefID(id);
					at.setTaskID(""+rs.getObject(2));
					
					mapEnabled.get(id).add(at);
				}else if(status.equals("Executing")){
					if(!isList && mapTMP.get(id).equals("Executing")) {
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);

					at = new ActivityTuple();
					at.setWorkDefID(id);
					at.setTaskID(""+rs.getObject(2));
										
					mapExecuting.get(id).add(at);
				}else if(status.equals("Complete")){
					if(!isList && mapTMP.get(id).equals("Complete")) {
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);

					at = new ActivityTuple();
					at.setWorkDefID(id);
					at.setTaskID(""+rs.getObject(2));
					
					mapComplete.get(id).add(at);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		if(mapEnabled.size()>0 || mapExecuting.size()>0 || mapComplete.size()>0) {
			LinkedList<ActivityTuple> res = new LinkedList<ActivityTuple>();
			
			for(Entry<String, LinkedList<ActivityTuple>> entry : mapEnabled.entrySet()) {
				if(entry.getValue().size()>0) {
					res.addAll(entry.getValue());
				}
			}
			for(Entry<String, LinkedList<ActivityTuple>> entry : mapExecuting.entrySet()) {
				if(entry.getValue().size()>0) {
					res.addAll(entry.getValue());
				}
			}
			for(Entry<String, LinkedList<ActivityTuple>> entry : mapComplete.entrySet()) {
				if(entry.getValue().size()>0) {
					res.addAll(entry.getValue());
				}
			}
			
			if(res.size()>0) return res;
			else return null;
		}else return null;
		
	}

	@Override
	public LinkedList<ActivityTuple> getWorkDefIDs(String ID, boolean isID, String Name,
			boolean isName, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor!='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor!='Complete' and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select distinct a.engineinstanceid as WorkDefID " +
						"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
						"where (d.descriptor='Enabled') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid " +
				"and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid and (d1.descriptor='Enabled' and d2.descriptor='Enabled') " +
				"and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid ) " +
				"union " +
				"select distinct a.engineinstanceid as WorkDefID " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Executing') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and "; 

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}

		String Query3 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Executing' and (d2.descriptor='Enabled' or d2.descriptor='Executing')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid and (c1.engineinstanceid=c2.engineinstanceid or c1.engineinstanceid like c2.engineinstanceid||'.%' or c2.engineinstanceid like c1.engineinstanceid||'.%')) " +
				"union " +
				"select distinct a.engineinstanceid as WorkDefID " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Complete') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}

		String Query4 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Complete' and (d2.descriptor='Enabled' or d2.descriptor='Executing' or d2.descriptor='Complete')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)";
		

		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4);
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		try {
			ActivityTuple at = null;
			while(rs.next()) {
				at = new ActivityTuple();
				at.setWorkDefID(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
	}

	@Override
	public LinkedList<ActivityTuple> getNames(String ID, boolean isID, String WorkDefID,
			boolean isWorkDefID, int TypeTime, boolean isTypeTime, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor!='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor!='Complete' and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select distinct b.taskname as taskname " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid " +
				"and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid and (d1.descriptor='Enabled' and d2.descriptor='Enabled') " +
				"and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid ) " +
				"union " +
				"select distinct b.taskname as taskname " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Executing') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Executing' and (d2.descriptor='Enabled' or d2.descriptor='Executing')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid and (c1.engineinstanceid=c2.engineinstanceid or c1.engineinstanceid like c2.engineinstanceid||'.%' or c2.engineinstanceid like c1.engineinstanceid||'.%')) " +
				"union " +
				"select distinct b.taskname as taskname " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Complete') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Complete' and (d2.descriptor='Enabled' or d2.descriptor='Executing' or d2.descriptor='Complete')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)";

//		System.out.println(Query1+Query2+Query3+Query4);
		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4);
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		try {
			ActivityTuple at = null;
			while(rs.next()) {
				at = new ActivityTuple();
				at.setName(""+rs.getObject(1));
				result.add(at);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	
	@Override
	public LinkedList<ActivityTuple> getTimes(String ID, boolean isID, String Name,
			boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, 
			boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefIDs!=null) {
			if(WorkDefIDs.size()>1) {
				String s = "(";
				for(String WorkDefID : WorkDefIDs) {
					if(isWorkDefID) s+="(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') or ";
					else s+="(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') or ";
				}
				s = s.substring(0, s.length()-4);
				s+=") and ";
			}else {
				String WorkDefID = WorkDefIDs.getFirst();
				if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
				else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
			}
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid, b.taskname, descriptor, eventtime  from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled' or d.descriptor='Executing' or d.descriptor='Complete') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid order by eventtime";

//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<String>> mapEnabled = new HashMap<String, LinkedList<String>>();
		HashMap<String, LinkedList<String>> mapExecuting = new HashMap<String, LinkedList<String>>();
		HashMap<String, LinkedList<String>> mapComplete = new HashMap<String, LinkedList<String>>();
		HashMap<String, String> mapTMP = new HashMap<String, String>();
		String id = "";
		try {
			while(rs.next()) {
				String caseid = rs.getString(1);
				if(caseid.contains("")) {
					caseid = caseid.substring(0, caseid.indexOf(""));
				}
				id = caseid+rs.getString(2);
				if(!mapTMP.containsKey(id)) {
					mapEnabled.put(id, new LinkedList<String>());
					mapExecuting.put(id, new LinkedList<String>());
					mapComplete.put(id, new LinkedList<String>());
					mapTMP.put(id, "Enabled");
				}
				String status = rs.getString(3);
				if(status.equals("Enabled")){
					if(!isList && mapTMP.get(id).equals("Enabled")) {
						mapEnabled.get(id).clear();
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapEnabled.get(id).add(""+rs.getObject(4));
				}else if(status.equals("Executing")){
					if(!isList && mapTMP.get(id).equals("Executing")) {
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapExecuting.get(id).add(""+rs.getObject(4));
				}else if(status.equals("Complete")){
					if(!isList && mapTMP.get(id).equals("Complete")) {
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapComplete.get(id).add(""+rs.getObject(4));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
			if(entry.getValue().size() == 0) {
				mapEnabled.remove(entry.getKey());
			}
		}
		
		for(Entry<String, LinkedList<String>> entry : mapExecuting.entrySet()) {
			if(entry.getValue().size() == 0) {
				mapExecuting.remove(entry.getKey());
			}
		}
		
		for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
			if(entry.getValue().size() == 0) {
				mapComplete.remove(entry.getKey());
			}
		}
		
		LinkedList<ActivityTuple> res = new LinkedList<ActivityTuple>();
		ActivityTuple at = null;
		
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled && mapEnabled.size()>0) {
					for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime == Activity.Executing && mapExecuting.size()>0) {
					for(Entry<String, LinkedList<String>> entry : mapExecuting.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime == Activity.Completed && mapComplete.size()>0) {
					for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
			}else {
				if(TypeTime == Activity.Enabled && (mapExecuting.size()>0 || mapComplete.size()>0)) {
					for(Entry<String, LinkedList<String>> entry : mapExecuting.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime == Activity.Executing && (mapEnabled.size()>0 || mapComplete.size()>0)) {
					for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime == Activity.Completed && (mapEnabled.size()>0 || mapExecuting.size()>0)) {
					for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					for(Entry<String, LinkedList<String>> entry : mapExecuting.entrySet()) {
						at = new ActivityTuple();
						if(entry.getValue().size()>0) {
							for(String time : entry.getValue()) {
								at.setTime(time);
							}
						}
						res.add(at);
					}
					if(res.size()>0) return res;
					else return null;
				}
			}
		}
		return null;
	}
	
	/*
	@Override
	public Vector<String> getTimes(String ID, boolean isID, String Name,
			boolean isName, String WorkDefID, boolean isWorkDefID, int TypeTime, 
			boolean isTypeTime, String VariablesID, boolean isVariablesID) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime==2) option.add("d.descriptor='Executing' and ");
				else option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime==1) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime==2) option.add("d.descriptor!='Executing' and ");
				else option.add("d.descriptor!='Complete' and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select distinct d.eventtime as time " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid " +
				"and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid and (d1.descriptor='Enabled' and d2.descriptor='Enabled') " +
				"and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid ) ";
				
		String Query3 = "select distinct d.eventtime as time " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Executing') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query4 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Executing' and (d2.descriptor='Enabled' or d2.descriptor='Executing')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid and (c1.engineinstanceid=c2.engineinstanceid or c1.engineinstanceid like c2.engineinstanceid||'.%' or c2.engineinstanceid like c1.engineinstanceid||'.%')) ";

		String Query5 = "select distinct d.eventtime as time " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Complete') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query6 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Complete' and (d2.descriptor='Enabled' or d2.descriptor='Executing' or d2.descriptor='Complete')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)";

		String FinalQuery = null;
		if(isTypeTime) {
			if(TypeTime==0) {
				FinalQuery = Query1+Query2+"union "+Query3+Query4+"union "+Query5+Query6;
			}else if(TypeTime==1) {
				FinalQuery = Query1+Query2;
			}else if(TypeTime==2) {
				FinalQuery = Query3+Query4;
			}else if(TypeTime==3) {
				FinalQuery = Query5+Query6;
			}
		}else {
			FinalQuery = Query1+Query2+"union "+Query3+Query4+"union "+Query5+Query6;
		}

		System.out.println(FinalQuery);
		ResultSet rs = jdbc.execSelect(FinalQuery);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				if((""+rs.getObject(1)).contains(".")) result.add((""+rs.getObject(1)).substring((""+rs.getObject(1)).indexOf(".")));
				else result.add(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
	}*/

	@Override
	public LinkedList<ActivityTuple> getVariablesIDs(String ID, boolean isID, String Name,
			boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, int TypeTime, boolean isTypeTime, boolean isList) {
	
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefIDs!=null) {
			if(WorkDefIDs.size()>1) {
				String s = "(";
				for(String WorkDefID : WorkDefIDs) {
					if(isWorkDefID) s+="(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') or ";
					else s+="(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') or ";
				}
				s = s.substring(0, s.length()-4);
				s+=") and ";
			}else {
				String WorkDefID = WorkDefIDs.getFirst();
				if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
				else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
			}
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor!='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor!='Complete' and ");
			}
		}
		
		
		String Query1 = "select a.engineinstanceid, b.taskname, descriptor, c.taskinstanceid from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled' or d.descriptor='Executing' or d.descriptor='Complete') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid order by eventtime";

//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<String>> mapEnabled = new HashMap<String, LinkedList<String>>();
		HashMap<String, LinkedList<String>> mapExecuting = new HashMap<String, LinkedList<String>>();
		HashMap<String, LinkedList<String>> mapComplete = new HashMap<String, LinkedList<String>>();
		HashMap<String, String> mapTMP = new HashMap<String, String>();
		String id = "";
		try {
			while(rs.next()) {
				String caseid = rs.getString(1);
				if(caseid.contains("")) {
					caseid = caseid.substring(0, caseid.indexOf(""));
				}
				id = caseid+rs.getString(2);
				if(!mapTMP.containsKey(id)) {
					mapEnabled.put(id, new LinkedList<String>());
					mapExecuting.put(id, new LinkedList<String>());
					mapComplete.put(id, new LinkedList<String>());
					mapTMP.put(id, "Enabled");
				}
				String status = rs.getString(3);
				if(status.equals("Enabled")){
					if(!isList && mapTMP.get(id).equals("Enabled")) {
						mapEnabled.get(id).clear();
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapEnabled.get(id).add(""+rs.getObject(4));
				}else if(status.equals("Executing")){
					if(!isList && mapTMP.get(id).equals("Executing")) {
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapExecuting.get(id).add(""+rs.getObject(4));
				}else if(status.equals("Complete")){
					if(!isList && mapTMP.get(id).equals("Complete")) {
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapComplete.get(id).add(""+rs.getObject(4));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		if(mapEnabled.size()>0 || mapExecuting.size()>0 || mapComplete.size()>0) {
			LinkedList<ActivityTuple> res = new LinkedList<ActivityTuple>();
			ActivityTuple at = null;
			HashMap<String, Vector<String>> vecs = new HashMap<String, Vector<String>>();
			for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
				if(!vecs.containsKey(entry.getKey())) {
					Vector<String> vec = new Vector<String>();
					vecs.put(entry.getKey(), vec);
				}
				Vector<String> vec = vecs.get(entry.getKey());
				if(entry.getValue().size()>0) {
					vec.addAll(entry.getValue());
				}
			}
			for(Entry<String, LinkedList<String>> entry : mapExecuting.entrySet()) {
				if(!vecs.containsKey(entry.getKey())) {
					Vector<String> vec = new Vector<String>();
					vecs.put(entry.getKey(), vec);
				}
				Vector<String> vec = vecs.get(entry.getKey());
				if(entry.getValue().size()>0) {
					vec.addAll(entry.getValue());
				}
			}
			for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
				if(!vecs.containsKey(entry.getKey())) {
					Vector<String> vec = new Vector<String>();
					vecs.put(entry.getKey(), vec);
				}
				Vector<String> vec = vecs.get(entry.getKey());
				if(entry.getValue().size()>0) {
					vec.addAll(entry.getValue());
				}
			}
			for(Entry<String, Vector<String>> entry : vecs.entrySet()) {
				Vector<String> vec = entry.getValue();
				for(String var : vec) {
					at = new  ActivityTuple();
					at.setVariableID(var);
					at.setWorkDefID(entry.getKey());
					res.add(at);
				}
			}
			if(res.size()>0) return res;
			else return null;
		}else return null;
		
	}



	@Override
	public LinkedList<ActivityTuple> getCounts(String Name, boolean isName,
			LinkedList<String> WorkDefIDs, boolean isWorkDefID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(WorkDefIDs!=null) {
			if(WorkDefIDs.size()>1) {
				String s = "(";
				for(String WorkDefID : WorkDefIDs) {
					if(isWorkDefID) s+="(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') or ";
					else s+="(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') or ";
				}
				s = s.substring(0, s.length()-4);
				s+=") and ";
			}else {
				String WorkDefID = WorkDefIDs.getFirst();
				if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
				else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
			}
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}		
		
		String Query1 = "select a.engineinstanceid as WorkDefID, b.taskname as Taskname, count(*)/2 as Count, identifier " +
					  	"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d, logspecification, lognet " +
					  	"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and d.descriptor='Complete' " +
						"and a.netid=lognet.netid and lognet.speckey = logspecification.rowkey " +
						"group by a.engineinstanceid, taskname, identifier";

//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>();
		ActivityTuple at = null;
		
		if(WorkDefIDs==null) {
			HashSet<String> cases = new HashSet<String>();
			HashSet<String> identifier = new HashSet<String>();
			try {
				while(rs.next()) {
					at = new ActivityTuple();
					String id = null;
					if(rs.getString(1).contains("")) id = rs.getString(1).substring(0, rs.getString(1).indexOf(""));
					else id = ""+rs.getObject(1);
					at.setWorkDefID(id);
					at.setName(""+rs.getObject(2));
					at.setCount(""+rs.getObject(3));
					result.add(at);
					cases.add(id);
					identifier.add(""+rs.getObject(4));
				}
				
				String Query3 = "";

				if(identifier.size()>0) {
					Query3 += "and (";
					for (String opt : identifier) {
						Query3 += "identifier='"+opt+"' or "; 
					}
					Query3 = Query3.substring(0, Query3.length()-3);
					Query3 += ")";
				}
				
				rs = jdbc.execSelect("select a.engineinstanceid from lognetinstance as a, lognet as b, logspecification as c where parenttaskinstanceid='-1' and a.netid=b.netid and b.speckey = c.rowkey "+Query3);
				while(rs.next()) {
					at = new ActivityTuple();
					String id = null;
					if(rs.getString(1).contains("")) id = rs.getString(1).substring(0, rs.getString(1).indexOf(""));
					else id = ""+rs.getObject(1);
					if(!cases.contains(id)) {
						at.setWorkDefID(id);
						at.setName("");
						at.setCount("0");
						result.add(at);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
			if(result.size()>0) return result;
			else return null;
		}else {
			if(WorkDefIDs.size()>1) {
				HashSet<String> cases = new HashSet<String>(); 
				try {
					while(rs.next()) {
						at = new ActivityTuple();
						String id = null;
						if(rs.getString(1).contains("")) id = rs.getString(1).substring(0, rs.getString(1).indexOf(""));
						else id = ""+rs.getObject(1);
						at.setWorkDefID(id);
						at.setName(""+rs.getObject(2));
						at.setCount(""+rs.getObject(3));
						result.add(at);
						cases.add(id);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
				if(result.size()>0) return result;
				else {
					for(String workDefID : WorkDefIDs) {
						at = new ActivityTuple();
						at.setWorkDefID(workDefID);
						at.setName(Name);
						at.setCount("0");
						result.add(at);
					}
					return result;
				}
			}else {
				HashSet<String> cases = new HashSet<String>(); 
				try {
					while(rs.next()) {
						at = new ActivityTuple();
						String id = null;
						if(rs.getString(1).contains("")) id = rs.getString(1).substring(0, rs.getString(1).indexOf(""));
						else id = ""+rs.getObject(1);
						at.setWorkDefID(id);
						at.setName(""+rs.getObject(2));
						at.setCount(""+rs.getObject(3));
						result.add(at);
						cases.add(id);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
				if(result.size()>0) return result;
				else {
					at = new ActivityTuple();
					at.setWorkDefID(WorkDefIDs.getFirst());
					at.setName(Name);
					at.setCount("0");
					result.add(at);
					return result;
				}
			}
		}
		
	}
	
	@Override
	public LinkedList<ActivityTuple> getRows(String ID, boolean isID, String Name,
			boolean isName, LinkedList<String> WorkDefIDs, boolean isWorkDefID, long Time,
			int isTime, int TypeTime, boolean isTypeTime, String VariablesID,
			boolean isVariablesID, boolean isList) {
	
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
	
		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefIDs!=null) {
			if(WorkDefIDs.size()>1) {
				String s = "(";
				for(String WorkDefID : WorkDefIDs) {
					if(isWorkDefID) s+="(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') or ";
					else s+="(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') or ";
				}
				s = s.substring(0, s.length()-4);
				s+=") and ";
			}else {
				String WorkDefID = WorkDefIDs.getFirst();
				if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
				else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
			}
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(Time!=0) {
			if(isTime==0) option.add("d.eventtime<'"+Time+"' and ");
			else if(isTime==1) option.add("d.eventtime<='"+Time+"' and ");
			else if(isTime==2) option.add("d.eventtime='"+Time+"' and ");
			else if(isTime==3) option.add("d.eventtime>='"+Time+"' and ");
			else if(isTime==4) option.add("d.eventtime>'"+Time+"' and ");
			else if(isTime==5) option.add("d.eventtime!='"+Time+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime == Activity.Enabled) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime == Activity.Executing) option.add("d.descriptor!='Executing' and ");
				else if(TypeTime == Activity.Completed) option.add("d.descriptor!='Complete' and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid, b.taskname, b.taskid, descriptor, eventtime, c.taskinstanceid from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled' or d.descriptor='Executing' or d.descriptor='Complete') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid order by eventtime";
	
//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<ActivityTuple>> mapEnabled = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, LinkedList<ActivityTuple>> mapExecuting = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, LinkedList<ActivityTuple>> mapComplete = new HashMap<String, LinkedList<ActivityTuple>>();
		HashMap<String, String> mapTMP = new HashMap<String, String>();
		String id = "";
		
		LinkedList<ActivityTuple> result = new LinkedList<ActivityTuple>(); 
		
		try {
			while(rs.next()) {
				String caseid = rs.getString(1);
				if(caseid.contains("")) {
					caseid = caseid.substring(0, caseid.indexOf(""));
				}
				id = caseid+rs.getString(2);
				if(!mapTMP.containsKey(id)) {
					mapEnabled.put(id, new LinkedList<ActivityTuple>());
					mapExecuting.put(id, new LinkedList<ActivityTuple>());
					mapComplete.put(id, new LinkedList<ActivityTuple>());
					mapTMP.put(id, "Enabled");
				}
				String status = rs.getString(4);
				if(status.equals("Enabled")){
					if(!isList && mapTMP.get(id).equals("Enabled")) {
						mapEnabled.get(id).clear();
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					
					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(caseid);
					at.setName(""+rs.getObject(2));
					at.setTaskID(""+rs.getObject(3));
					at.setStatus(status);
					at.setTime(""+rs.getObject(5));
					at.setVariableID(""+rs.getObject(6));

					mapEnabled.get(id).add(at);
				}else if(status.equals("Executing")){
					if(!isList && mapTMP.get(id).equals("Executing")) {
						mapExecuting.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);

					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(caseid);
					at.setName(""+rs.getObject(2));
					at.setTaskID(""+rs.getObject(3));
					at.setStatus(status);
					at.setTime(""+rs.getObject(5));
					at.setVariableID(""+rs.getObject(6));

					mapExecuting.get(id).add(at);
				}else if(status.equals("Complete")){
					if(!isList && mapTMP.get(id).equals("Complete")) {
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);

					ActivityTuple at = new ActivityTuple();
					at.setWorkDefID(caseid);
					at.setName(""+rs.getObject(2));
					at.setTaskID(""+rs.getObject(3));
					at.setStatus(status);
					at.setTime(""+rs.getObject(5));
					at.setVariableID(""+rs.getObject(6));

					mapComplete.get(id).add(at);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime == Activity.Enabled && mapEnabled.size()>0) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
				else if(TypeTime == Activity.Executing && mapExecuting.size()>0) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapExecuting.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
				else if(TypeTime == Activity.Completed && mapComplete.size()>0) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
			}else {
				if(TypeTime == Activity.Enabled && (mapExecuting.size()>0 || mapComplete.size()>0)) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapExecuting.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
				else if(TypeTime == Activity.Executing && (mapEnabled.size()>0 || mapComplete.size()>0)) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
				else if(TypeTime == Activity.Completed && (mapEnabled.size()>0 || mapExecuting.size()>0)) {
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					for(Entry<String, LinkedList<ActivityTuple>> entry : mapExecuting.entrySet()) {
						if(entry.getValue().size()>0) {
							result.addAll(entry.getValue());
						}
					}
					if(result.size()>0) return result;
					else return null;
				}
			}
		}else {
			if(mapEnabled.size()>0 || mapExecuting.size()>0 || mapComplete.size()>0) {
				for(Entry<String, LinkedList<ActivityTuple>> entry : mapEnabled.entrySet()) {
					if(entry.getValue().size()>0) {
						result.addAll(entry.getValue());
					}
				}
				for(Entry<String, LinkedList<ActivityTuple>> entry : mapExecuting.entrySet()) {
					if(entry.getValue().size()>0) {
						result.addAll(entry.getValue());
					}
				}
				for(Entry<String, LinkedList<ActivityTuple>> entry : mapComplete.entrySet()) {
					if(entry.getValue().size()>0) {
						result.addAll(entry.getValue());
					}
				}
				if(result.size()>0) return result;
				else return null;
			}else {
				return null;
			}
		}
		return null;
	}
	
	@Override
	public LinkedList<ActivityTuple> getAllRows(String WorkDefID) {
	
				//Create the option for the query
		String Query1 = "select a.engineinstanceid, b.taskname, b.taskid, descriptor, eventtime, c.taskinstanceid from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
		"where (d.descriptor='Enabled') and ";// or d.descriptor='Executing' or d.descriptor='Complete') and ";
		
		if(WorkDefID!=null) {
			Query1 += "(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ";
		}
		
		String Query2 = "a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid order by eventtime";
	
//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		
		LinkedList<ActivityTuple> res = new LinkedList<ActivityTuple>();
		try {
			while(rs.next()) {				
				ActivityTuple at = new ActivityTuple();
				at.setWorkDefID(""+rs.getObject(1));
				at.setName(""+rs.getObject(2));
				at.setTaskID(""+rs.getObject(3));
				at.setStatus(""+rs.getObject(4));
				at.setTime(""+rs.getObject(5));
				at.setVariableID(""+rs.getObject(6));
				
				res.add(at);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}
	/*
	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name,
			boolean isName, String WorkDefID, boolean isWorkDefID, long Time,
			int isTime, int TypeTime, boolean isTypeTime, String VariablesID,
			boolean isVariablesID) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query

		if(ID!=null) {
			if(isID) option.add("b.taskid='"+ID+"' and ");
			else option.add("b.taskid!='"+ID+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.taskname like '"+Name+"' and ");
			else option.add("b.taskname not like '"+Name+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("d.descriptor='Enabled' and ");
				else if(TypeTime==2) option.add("d.descriptor='Executing' and ");
				else option.add("d.descriptor='Complete' and ");
			}else {
				if(TypeTime==1) option.add("d.descriptor!='Enabled' and ");
				else if(TypeTime==2) option.add("d.descriptor!='Executing' and ");
				else option.add("d.descriptor!='Complete' and ");
			}
		}
		if(Time!=0) {
			if(isTime==0) option.add("d.eventtime<'"+Time+"' and ");
			else if(isTime==1) option.add("d.eventtime<='"+Time+"' and ");
			else if(isTime==2) option.add("d.eventtime='"+Time+"' and ");
			else if(isTime==3) option.add("d.eventtime>='"+Time+"' and ");
			else if(isTime==4) option.add("d.eventtime>'"+Time+"' and ");
			else if(isTime==5) option.add("d.eventtime!='"+Time+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("c.taskinstanceid='"+VariablesID+"' and ");
			else option.add("c.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid as WorkDefID, b.taskiD as ID, b.taskname as taskname, d.eventtime as time, d.descriptor as typetime, c.taskinstanceid as VariablesID " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Enabled') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid " +
				"and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid and (d1.descriptor='Enabled' and d2.descriptor='Enabled') " +
				"and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid ) " +
				"union " +
				"select a.engineinstanceid as WorkDefID, b.taskiD as ID, b.taskname as taskname, d.eventtime as time, d.descriptor as typetime, c.taskinstanceid as VariablesID " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Executing') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Executing' and (d2.descriptor='Enabled' or d2.descriptor='Executing')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid and (c1.engineinstanceid=c2.engineinstanceid or c1.engineinstanceid like c2.engineinstanceid||'.%' or c2.engineinstanceid like c1.engineinstanceid||'.%')) " +
				"union " +
				"select a.engineinstanceid as WorkDefID, b.taskiD as ID, b.taskname as taskname, d.eventtime as time, d.descriptor as typetime, c.taskinstanceid as VariablesID " +
				"from lognetinstance as a, logtask as b, logtaskinstance as c, logevent as d " +
				"where (d.descriptor='Complete') and a.netinstanceid=c.parentnetinstanceid and b.taskid=c.taskid and c.taskinstanceid=d.instanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "a.netinstanceid not in " +
				"(select a1.netinstanceid from lognetinstance as a1, logtask as b1, logtaskinstance as c1, logevent as d1, lognetinstance as a2, logtask as b2, logtaskinstance as c2, logevent as d2 " +
				"where a1.netinstanceid=c1.parentnetinstanceid and b1.taskid=c1.taskid and c1.taskinstanceid=d1.instanceid and a2.netinstanceid=c2.parentnetinstanceid and b2.taskid=c2.taskid and c2.taskinstanceid=d2.instanceid " +
				"and (d1.descriptor='Complete' and (d2.descriptor='Enabled' or d2.descriptor='Executing' or d2.descriptor='Complete')) and b1.taskid=b2.taskid and d1.eventtime<d2.eventtime and b.taskid=b1.taskid and b1.taskid=c1.taskid)";

		System.out.println(Query1+Query2+Query3+Query4);
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4);
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		try {
			while(rs.next()) {
				Vector<String> tmp = new Vector<String>();
				if((""+rs.getObject(1)).contains(".")) tmp.add((""+rs.getObject(1)).substring((""+rs.getObject(1)).indexOf(".")));
				else tmp.add(""+rs.getObject(1));
				tmp.add(""+rs.getObject(2));
				tmp.add(""+rs.getObject(3));
				tmp.add(""+rs.getObject(4));
				tmp.add(""+rs.getObject(5));
				tmp.add(""+rs.getObject(6));
				result.add(tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}*/

	@Override
	public String getDistribution(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type) {

		String taskID = Name;
//		Name = Name.substring(0, Name.lastIndexOf("_"));
		Element resourcing = null;
		LinkedList<String> option = new LinkedList<String>();
		String type = "";
		if(Name!=null) {
			if(isName) option.add("logtask.taskname like '"+Name+"' and ");
			else option.add("logtask.taskname not like '"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(lognetinstance.engineinstanceid='"+WorkDefID+"' or lognetinstance.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(lognetinstance.engineinstanceid !='"+WorkDefID+"' or lognetinstance.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
		if(Type!=0) {
			if(Type==1) type="offer";
			else if(Type==2) type="allocate";
			else if(Type==3) type="start";
		}
		
		String Query1 = "select identifier, specversion, uri  " +
				"from logtask, lognet, lognetinstance, logspecification " +
				"where logtask.parentnetid=lognet.netid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
				
		String Query2 = "lognet.netid=lognetinstance.netid and lognet.speckey=logspecification.rowkey";

//		System.out.println(Query1+Query2);
		
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		String Identifier = null;
		String URI = null;
		String Version = null;
		try {
			while(rs.next()) {
				Identifier = ""+rs.getObject(1);
				Version = ""+rs.getObject(2);
				URI = ""+rs.getObject(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			YSpecificationID specID = new YSpecificationID(Identifier, Version, URI);
			List<SpecificationData> l = _interfaceBClient.getSpecificationList(getConnection());
			
			for(SpecificationData sp : l) {
				if(sp.getID().equals(specID)) {
					specID = sp.getID();
//					System.out.println("ok");
					break;
				}
			}

//			System.out.println(specID.getKey());
			
//			System.out.println(_interfaceBClient.getTaskInformationStr(specID, "Prepare_Route_Guide_387", getConnection()));
//			System.out.println(_interfaceBClient.getResourcingSpecs(specID, "Prepare Route Guide 387", getConnection()));
			
			resourcing = JDOMUtil.stringToDocument(_interfaceBClient.getResourcingSpecs(specID, taskID, getConnection())).getRootElement();

			for(Element e : (List<Element>) resourcing.getChildren()) {
				if(type.contains(e.getName())){
					if(e.getChildren().size()>0) {
						Element distributionSet = ((List<Element>)e.getChildren()).get(0);
						if(distributionSet.getChildren() != null) {
							Element initialSet = ((List<Element>)distributionSet.getChildren()).get(0);
							StringBuffer sb = new StringBuffer();
							for(Element child : (List<Element>) initialSet.getChildren()) {
								if(child.getName().equals("role")) {
//									sb.append(Role.getRole("org.yawlfoundation.yawl.sensors.layerDB.YAWL.YAWL_Role").getRoleRInfo(child.getValue()));
									
									Role roleLayer = Role.getRole("org.yawlfoundation.yawl.sensors.databaseInterface.YALW.YAWL_Role");
									
									for(String part : roleLayer.getParticipantWithRole(child.getValue())) {

										sb.append("<participant>");
										sb.append(part);
										sb.append("</participant>");
										
									}
								}else if(child.getName().equals("participant")) {
//									sb.append(Role.getRole("org.yawlfoundation.yawl.sensors.layerDB.YAWL.YAWL_Role").getRoleInfo(child.getValue()));

									sb.append("<participant>");
									sb.append(child.getValue());
									sb.append("</participant>");
									
	//							}else {
	//								List<Element> param = (List<Element>) child.getChildren();
	//								String name = param.get(0).getValue();
	//								String refers = param.get(1).getValue();
	//								String res = getParam(taskID, specID, caseID, name, s);
	//								if(res != null) {
	//									if(refers.equals("role")) {
	//										sb.append(_resClient.getRole(res, getResourceHandle()));
	//									}else if(refers.equals("participant")) {
	//										sb.append(_resClient.getParticipant(res, getResourceHandle()));
	//									}
	//								}
								}
							}
							initialSet.setText(sb.toString());
							String r = JDOMUtil.formatXMLString(JDOMUtil.elementToString(initialSet).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#xD;", ""));
							r = r.replace(" xmlns=\"http://www.yawlfoundation.org/yawlschema\"", "");
							return r;
						}
					}else {
						return null;
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public String getInitiator(String Name, boolean isName, String WorkDefID, boolean isWorkDefID, int Type) {

		String taskID = Name;
		if(Name.contains("_")) Name = Name.substring(0, Name.lastIndexOf("_"));
		Element resourcing = null;
		LinkedList<String> option = new LinkedList<String>();
		String type = "";
		if(Name!=null) {
			if(isName) option.add("logtask.taskname like '"+Name+"' and ");
			else option.add("logtask.taskname not like '"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(lognetinstance.engineinstanceid='"+WorkDefID+"' or lognetinstance.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(lognetinstance.engineinstanceid !='"+WorkDefID+"' or lognetinstance.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
		if(Type!=0) {
			if(Type==1) type="offer";
			else if(Type==2) type="allocate";
			else if(Type==3) type="start";
		}
		
		String Query1 = "select identifier, specversion, uri  " +
				"from logtask, lognet, lognetinstance, logspecification " +
				"where logtask.parentnetid=lognet.netid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
				
		String Query2 = "lognet.netid=lognetinstance.netid and lognet.speckey=logspecification.rowkey";

		ResultSet rs = jdbc.execSelect(Query1+Query2);
		String Identifier = null;
		String URI = null;
		String Version = null;
		try {
			while(rs.next()) {
				Identifier = ""+rs.getObject(1);
				Version = ""+rs.getObject(2);
				URI = ""+rs.getObject(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			resourcing = JDOMUtil.stringToDocument(_interfaceBClient.getResourcingSpecs(new YSpecificationID(Identifier, Version, URI), taskID, getConnection())).getRootElement();
			for(Element e : (List<Element>) resourcing.getChildren()) {
				if(type.contains(e.getName())){
					return e.getAttributeValue("initiator");
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
//	@Override
//	public HashSet<String> getActivitiesEnabled(String caseID) {
//		try {
//			String xml = _interfaceBClient.getWorkItemInstanceSummary(caseID, getConnection());
//			HashSet<String> taskName = new HashSet<String>();
//			Element e = JDOMUtil.stringToDocument(xml).getRootElement();
//			for(Element workitem : (List<Element>) e.getChildren()) {
//				if("statusExecuting".equals(workitem.getChild("status").getValue())) taskName.add(workitem.getChild("taskname").getValue());
//			}
//			return taskName;
//		} catch (IOException e1) {
//			return null;
//		}
//	}
	
	private String getConnection() {
		try {
			while(session == null || !_interfaceBClient.checkConnection(session).equals("<response><success/></response>")) {
				session = _interfaceBClient.connect(_user, _password);
			}
			return session;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
