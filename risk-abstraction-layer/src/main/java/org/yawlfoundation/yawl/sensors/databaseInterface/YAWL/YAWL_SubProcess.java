package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import org.yawlfoundation.yawl.sensors.databaseInterface.SubProcess;
import org.yawlfoundation.yawl.sensors.jdbcImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

public class YAWL_SubProcess extends SubProcess {

//	
//	select a.engineinstanceid, b.netid, b.netname, d.eventtime, d.descriptor, d.eventid
//	from lognetinstance as a, lognet as b, logevent as d
//	where (d.descriptor='NetStart' or d.descriptor='CaseStart') and 
//	a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in 
//	(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2
//	where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid
//	and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid 
//	and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart')
//	or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart'))
//	and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid 
//	and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%')
//	and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))
//	union
//	select a.engineinstanceid, b.netid, b.netname, d.eventtime, d.descriptor, d.eventid
//	from lognetinstance as a, lognet as b, logevent as d
//	where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and 
//	a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in 
//	(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2
//	where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid
//	and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid 
//	and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete')))
//	or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete'))))
//	and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid 
//	and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%')
//	and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))
//	
	
	private static jdbcImpl jdbc = new jdbcImpl();
	
	@Override
	public Vector<String> getAllID() {

		String Query = "select distinct lognet.netid from logtask";
		ResultSet rs = jdbc.execSelect(Query);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				result.add(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public boolean isSubProcess(String WorkDefID, String Name) {
		
		String Query = "select distinct b.netname " +
				"from lognetinstance as a, lognet as b " +
				"where a.netid=b.netid and b.netname='"+Name+"'";

		ResultSet rs = jdbc.execSelect(Query);
		try {
			while(rs.next()) {
				if(Name.equals(rs.getObject(1))){
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public Vector<String> getIDs(String Name, boolean isName, String WorkDefID,
			boolean isWorkDefID, long Time, int isTime, int TypeTime,
			boolean isTypeTime, String VariablesID, boolean isVariablesID, boolean isList) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
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
				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
			}else {
				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select b.netid as ID " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
				"union " +
				"select b.netid as ID " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
		

		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				result.add(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getWorkDefIDs(String ID, boolean isID, String Name,
			boolean isName, long Time, int isTime, int TypeTime,
			boolean isTypeTime, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
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
				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
			}else {
				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid as WorkDefID " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
				"union " +
				"select a.engineinstanceid as WorkDefID " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				result.add(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public Vector<String> getNames(String ID, boolean isID, String WorkDefID,
			boolean isWorkDefID, long Time, int isTime, int TypeTime,
			boolean isTypeTime, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
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
				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
			}else {
				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select b.netname as name " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
				"union " +
				"select b.netname as name " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				result.add(""+rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}
	
	@Override
	public Vector<String> getTimes(String ID, boolean isID, String Name,
			boolean isName, String WorkDefID, boolean isWorkDefID,
			int TypeTime, boolean isTypeTime, String VariablesID,
			boolean isVariablesID, boolean isList) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
//		if(TypeTime!=0) {
//			if(isTypeTime) {
//				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
//			}else {
//				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
//			}
//		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid, b.netname, descriptor, eventtime " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart' or d.descriptor='NetComplete' or d.descriptor='CaseComplete') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid order by eventtime";
		
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<String>> mapEnabled = new HashMap<String, LinkedList<String>>();
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
					mapComplete.put(id, new LinkedList<String>());
					mapTMP.put(id, "CaseStart");
				}
				String status = rs.getString(3);
				if(status.equals("CaseStart") || status.equals("NetStart")){
					if(!isList & (mapTMP.get(id).equals("CaseStart") || mapTMP.get(id).equals("NetStart"))) {
						mapEnabled.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapEnabled.get(id).add(""+rs.getObject(4));
				}else if(status.equals("CaseComplete") || status.equals("NetComplete")){
					if(!isList & (mapTMP.get(id).equals("CaseComplete") || mapTMP.get(id).equals("NetComplete"))) {
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

		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1 && mapEnabled.size()>0) {
					Vector<String> res = new Vector<String>();
					for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime==2 && mapComplete.size()>0) {
					Vector<String> res = new Vector<String>();
					for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
			}else {
				if(TypeTime==1 && mapComplete.size()>0) {
					Vector<String> res = new Vector<String>();
					for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime==2 && mapEnabled.size()>0) {
					Vector<String> res = new Vector<String>();
					for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
			}
		}
		return null;
		
	}

//	@Override
//	public Vector<String> getTimes(String ID, boolean isID, String Name,
//			boolean isName, String WorkDefID, boolean isWorkDefID,
//			int TypeTime, boolean isTypeTime, String VariablesID,
//			boolean isVariablesID) {
//		
//		LinkedList<String> option = new LinkedList<String>();
//		
//		//Create the option for the query
//		if(ID!=null) {
//			if(isID) option.add("b.netid='"+ID+" and ");
//			else option.add("b.netid!='"+ID+"' and ");
//		}
//		if(Name!=null) {
//			if(isName) option.add("b.netname='"+Name+"' and ");
//			else option.add("b.netname!='"+Name+"' and ");
//		}
//		if(WorkDefID!=null) {
//			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
//			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
//		}
//		if(TypeTime!=0) {
//			if(isTypeTime) {
//				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
//			}else {
//				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
//			}
//		}
//		if(VariablesID!=null) {
//			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
//			else option.add("d.eventid!='"+VariablesID+"' and ");
//		}
//		
//		
//		String Query1 = "select distinct d.eventtime as time " +
//				"from lognetinstance as a, lognet as b, logevent as d " +
//				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
//		
//		if(option.size()>0) {
//			for (String opt : option) {
//				Query1 += opt; 
//			}
//		}
//		
//		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
//				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
//				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
//				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
//				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
//				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
//				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
//				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
//				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
//				"union " +
//				"select distinct d.eventtime as time " +
//				"from lognetinstance as a, lognet as b, logevent as d " +
//				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
//		
//		if(option.size()>0) {
//			for (String opt : option) {
//				Query2 += opt; 
//			}
//		}
//		
//		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
//				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
//				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
//				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
//				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
//				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
//				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
//				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
//				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
//		
//		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
//		Vector<String> result = new Vector<String>();
//		try {
//			while(rs.next()) {
//				result.add(""+rs.getObject(1));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//		if(result.size()>0) return result;
//		else return null;
//		
//	}

	@Override
	public Vector<String> getVariablesIDs(String ID, boolean isID, String Name,
			boolean isName, String WorkDefID, boolean isWorkDefID, long Time,
			int isTime, int TypeTime, boolean isTypeTime, boolean isList) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
		}
//		if(Time!=0) {
//			if(isTime==0) option.add("d.eventtime<'"+Time+"' and ");
//			else if(isTime==1) option.add("d.eventtime<='"+Time+"' and ");
//			else if(isTime==2) option.add("d.eventtime='"+Time+"' and ");
//			else if(isTime==3) option.add("d.eventtime>='"+Time+"' and ");
//			else if(isTime==4) option.add("d.eventtime>'"+Time+"' and ");
//			else if(isTime==5) option.add("d.eventtime!='"+Time+"' and ");
//		}
//		if(TypeTime!=0) {
//			if(isTypeTime) {
//				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
//			}else {
//				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
//			}
//		}
		
		String Query1 = "select a.engineinstanceid, b.netname, descriptor, eventid " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart' or d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid order by eventtime";  
		
//		System.out.println(Query1+Query2);
		
		ResultSet rs = jdbc.execSelect(Query1+Query2);
		HashMap<String, LinkedList<String>> mapEnabled = new HashMap<String, LinkedList<String>>();
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
					mapComplete.put(id, new LinkedList<String>());
					mapTMP.put(id, "CaseStarted");
				}
				String status = rs.getString(3);
				if(status.equals("CaseStart") || status.equals("NetStart")){
					if(!isList & (mapTMP.get(id).equals("CaseStart") || mapTMP.get(id).equals("NetStart"))) {
						mapEnabled.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					mapEnabled.get(id).add(""+rs.getObject(4));
				}else if(status.equals("CaseComplete") || status.equals("NetComplete")){
					if(!isList & (mapTMP.get(id).equals("CaseComplete") || mapTMP.get(id).equals("CaseComplete"))) {
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

		if(mapEnabled.size()>0 || mapComplete.size()>0) {
			Vector<String> res = new Vector<String>();
			for(Entry<String, LinkedList<String>> entry : mapEnabled.entrySet()) {
				if(entry.getValue().size()>0) {
					res.addAll(entry.getValue());
				}
			}
			for(Entry<String, LinkedList<String>> entry : mapComplete.entrySet()) {
				if(entry.getValue().size()>0) {
					res.addAll(entry.getValue());
				}
			}
			if(res.size()>0) return res;
			else return null;
		}else return null;
		
	}
	
//	@Override
//	public Vector<String> getVariablesIDs(String ID, boolean isID, String Name,
//			boolean isName, String WorkDefID, boolean isWorkDefID, long Time,
//			int isTime, int TypeTime, boolean isTypeTime) {
//		
//		LinkedList<String> option = new LinkedList<String>();
//		
//		//Create the option for the query
//		if(ID!=null) {
//			if(isID) option.add("b.netid='"+ID+" and ");
//			else option.add("b.netid!='"+ID+"' and ");
//		}
//		if(Name!=null) {
//			if(isName) option.add("b.netname='"+Name+"' and ");
//			else option.add("b.netname!='"+Name+"' and ");
//		}
//		if(WorkDefID!=null) {
//			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
//			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
//		}
//		if(Time!=0) {
//			if(isTime==0) option.add("d.eventtime<'"+Time+"' and ");
//			else if(isTime==1) option.add("d.eventtime<='"+Time+"' and ");
//			else if(isTime==2) option.add("d.eventtime='"+Time+"' and ");
//			else if(isTime==3) option.add("d.eventtime>='"+Time+"' and ");
//			else if(isTime==4) option.add("d.eventtime>'"+Time+"' and ");
//			else if(isTime==5) option.add("d.eventtime!='"+Time+"' and ");
//		}
//		if(TypeTime!=0) {
//			if(isTypeTime) {
//				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
//			}else {
//				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
//				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
//			}
//		}
//		
//		String Query1 = "select distinct d.eventid as VariablesID " +
//				"from lognetinstance as a, lognet as b, logevent as d " +
//				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
//		
//		if(option.size()>0) {
//			for (String opt : option) {
//				Query1 += opt; 
//			}
//		}
//		
//		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
//				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
//				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
//				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
//				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
//				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
//				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
//				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
//				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
//				"union " +
//				"select distinct d.eventid as VariablesID " +
//				"from lognetinstance as a, lognet as b, logevent as d " +
//				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
//		
//		if(option.size()>0) {
//			for (String opt : option) {
//				Query2 += opt; 
//			}
//		}
//		
//		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
//				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
//				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
//				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
//				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
//				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
//				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
//				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
//				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
//		
////		System.out.println(Query1+Query2+Query3);
//		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
//		Vector<String> result = new Vector<String>();
//		try {
//			while(rs.next()) {
//				result.add(""+rs.getObject(1));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//		if(result.size()>0) return result;
//		else return null;
//		
//	}
	
	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name,
			boolean isName, String WorkDefID, boolean isWorkDefID, long Time,
			int isTime, int TypeTime, boolean isTypeTime, String VariablesID,
			boolean isVariablesID, boolean isList) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
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
				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
			}else {
				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid, b.netid, b.netname, d.eventtime, d.descriptor, d.eventid " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart' or d.descriptor='NetComplete' or d.descriptor='CaseComplete') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid order by eventtime"; 
		
//		System.out.println(Query1+Query2);
		ResultSet rs = jdbc.execSelect(Query1+Query2);

		HashMap<String, LinkedList<Vector<String>>> mapEnabled = new HashMap<String, LinkedList<Vector<String>>>();
		HashMap<String, LinkedList<Vector<String>>> mapComplete = new HashMap<String, LinkedList<Vector<String>>>();
		HashMap<String, String> mapTMP = new HashMap<String, String>();
		String id = "";
		try {
			while(rs.next()) {
				String caseid = rs.getString(1);
				if(caseid.contains("")) {
					caseid = caseid.substring(0, caseid.indexOf(""));
				}
				id = caseid+rs.getString(3);
				if(!mapTMP.containsKey(id)) {
					mapEnabled.put(id, new LinkedList<Vector<String>>());
					mapComplete.put(id, new LinkedList<Vector<String>>());
					mapTMP.put(id, "Enabled");
				}
				String status = rs.getString(5);
				if(status.equals("NetStart") || status.equals("CaseStart")){
					if(!isList & (mapTMP.get(id).equals("NetStart") || mapTMP.get(id).equals("CaseStart"))) {
						mapEnabled.get(id).clear();
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					Vector<String> vs = new Vector<String>();
					vs.add(caseid);
					vs.add(""+rs.getObject(2));
					vs.add(""+rs.getObject(3));
					vs.add(""+rs.getObject(4));
					vs.add(status);
					vs.add(""+rs.getObject(6));
					mapEnabled.get(id).add(vs);
				}else if(status.equals("NetComplete") || status.equals("CaseComplete")){
					if(!isList & (mapTMP.get(id).equals("NetComplete") || mapTMP.get(id).equals("CaseComplete"))) {
						mapComplete.get(id).clear();
					}
					mapTMP.put(id, status);
					Vector<String> vs = new Vector<String>();
					vs.add(caseid);
					vs.add(""+rs.getObject(2));
					vs.add(""+rs.getObject(3));
					vs.add(""+rs.getObject(4));
					vs.add(status);
					vs.add(""+rs.getObject(6));
					mapComplete.get(id).add(vs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		Vector<Vector<String>> res = new Vector<Vector<String>>();
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1 && mapEnabled.size()>0) {
					for(Entry<String, LinkedList<Vector<String>>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime==2 && mapComplete.size()>0) {
					for(Entry<String, LinkedList<Vector<String>>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
			}else {
				if(TypeTime==1 && mapComplete.size()>0) {
					for(Entry<String, LinkedList<Vector<String>>> entry : mapComplete.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
				else if(TypeTime==2 && mapEnabled.size()>0) {
					for(Entry<String, LinkedList<Vector<String>>> entry : mapEnabled.entrySet()) {
						if(entry.getValue().size()>0) {
							res.addAll(entry.getValue());
						}
					}
					if(res.size()>0) return res;
					else return null;
				}
			}
		}else {
			if(mapEnabled.size()>0 || mapComplete.size()>0) {
				for(Entry<String, LinkedList<Vector<String>>> entry : mapEnabled.entrySet()) {
					if(entry.getValue().size()>0) {
						res.addAll(entry.getValue());
					}
				}
				for(Entry<String, LinkedList<Vector<String>>> entry : mapComplete.entrySet()) {
					if(entry.getValue().size()>0) {
						res.addAll(entry.getValue());
					}
				}
				if(res.size()>0) return res;
				else return null;
			}else {
				return null;
			}
		}
		return null;
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
			if(isID) option.add("b.netid='"+ID+" and ");
			else option.add("b.netid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("b.netname='"+Name+"' and ");
			else option.add("b.netname!='"+Name+"' and ");
		}
		if(WorkDefID!=null) {
			if(isWorkDefID) option.add("(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ");
			else option.add("(a.engineinstanceid !='"+WorkDefID+"' or a.engineinstanceid not like '"+WorkDefID+".%') and ");
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
				if(TypeTime==1) option.add("(d.descriptor='CaseStart' or d.descriptor='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor='CaseComplete' or d.descriptor='NetComplete') and ");
			}else {
				if(TypeTime==1) option.add("(d.descriptor!='CaseStart' or d.descriptor!='NetStart') and ");
				else if(TypeTime==2) option.add("(d.descriptor!='CaseComplete' or d.descriptor!='NetComplete') and ");
			}
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("d.eventid='"+VariablesID+"' and ");
			else option.add("d.eventid!='"+VariablesID+"' and ");
		}
		
		
		String Query1 = "select a.engineinstanceid, b.netid, b.netname, d.eventtime, d.descriptor, d.eventid " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetStart' or d.descriptor='CaseStart') and "; 
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}
		
		String Query2 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetStart' and d1.descriptor='NetStart' and d2.descriptor='NetStart') " +
				"or (d.descriptor='CaseStart' and d1.descriptor='CaseStart' and d2.descriptor='CaseStart')) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%')) " +
				"union " +
				"select a.engineinstanceid, b.netid, b.netname, d.eventtime, d.descriptor, d.eventid " +
				"from lognetinstance as a, lognet as b, logevent as d " +
				"where (d.descriptor='NetComplete' or d.descriptor='CaseComplete') and ";  
		
		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "a.netid=b.netid and a.netinstanceid=d.instanceid and d.eventid not in " +
				"(select d1.eventid from lognetinstance as a1, lognet as b1, logevent as d1, lognetinstance as a2, lognet as b2, logevent as d2 " +
				"where a1.netinstanceid=d1.instanceid and a1.netid=b1.netid " +
				"and a2.netinstanceid=d2.instanceid and a2.netid=b2.netid " +
				"and ((d.descriptor='NetComplete' and ((d1.descriptor='NetStart' and d2.descriptor='NetStart') or (d1.descriptor='NetComplete' and d2.descriptor='NetComplete'))) " +
				"or (d.descriptor='CaseComplete' and ((d1.descriptor='CaseStart' and d2.descriptor='CaseStart') or (d1.descriptor='CaseComplete' and d2.descriptor='CaseComplete')))) " +
				"and b1.netid=b2.netid and d1.eventtime<d2.eventtime and b.netid=b1.netid " +
				"and (a1.engineinstanceid=a2.engineinstanceid or a1.engineinstanceid like a2.engineinstanceid||'.%' or a2.engineinstanceid like a1.engineinstanceid||'.%') " +
				"and (a.engineinstanceid=a1.engineinstanceid or a.engineinstanceid like a1.engineinstanceid||'.%' or a1.engineinstanceid like a.engineinstanceid||'.%'))";
		
		System.out.println(Query1+Query2+Query3);
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3);
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

}
