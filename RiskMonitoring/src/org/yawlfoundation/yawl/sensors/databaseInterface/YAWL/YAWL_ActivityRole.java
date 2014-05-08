package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;

import org.yawlfoundation.yawl.sensors.jdbcImpl;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRole;
import org.yawlfoundation.yawl.sensors.databaseInterface.ActivityRoleTuple;

public class YAWL_ActivityRole extends ActivityRole {

//
//		select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event from rs_eventlog, logtaskinstance
//		where participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case' and rs_eventlog.caseid=logtaskinstance.engineinstanceid and rs_eventlog.event='offer'
//		and rs_eventlog.rowid not in
//		(select a1.rowid from rs_eventlog as a1, logtaskinstance as b1, rs_eventlog as a2, logtaskinstance as b2 
//		where a1.participantid!='' and a1.participantid!='system' and a1.event!='launch_case' and a1.event!='cancel_case' and a1.caseid=b1.engineinstanceid and a2.participantid!='' and a2.participantid!='system' and a2.event!='launch_case' and a2.event!='cancel_case' and a2.caseid=b2.engineinstanceid
//		and (b1.engineinstanceid=b2.engineinstanceid or b1.engineinstanceid like b2.engineinstanceid||'.%' or b2.engineinstanceid like b1.engineinstanceid||'.%')
//		and (a1.event='offer' and a2.event='offer')and a1.eventtime < a2.eventtime)
//		union
//		select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event from rs_eventlog, logtaskinstance
//		where participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case' and rs_eventlog.caseid=logtaskinstance.engineinstanceid and rs_eventlog.event='allocate'
//		and rs_eventlog.rowid not in
//		(select a1.rowid from rs_eventlog as a1, logtaskinstance as b1, rs_eventlog as a2, logtaskinstance as b2 
//		where a1.participantid!='' and a1.participantid!='system' and a1.event!='launch_case' and a1.event!='cancel_case' and a1.caseid=b1.engineinstanceid and a2.participantid!='' and a2.participantid!='system' and a2.event!='launch_case' and a2.event!='cancel_case' and a2.caseid=b2.engineinstanceid
//		and (b1.engineinstanceid=b2.engineinstanceid or b1.engineinstanceid like b2.engineinstanceid||'.%' or b2.engineinstanceid like b1.engineinstanceid||'.%')
//		and (a1.event='allocate' and (a2.event='offer' or a2.event='allocate')) and a1.eventtime < a2.eventtime)
//		union
//		select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event from rs_eventlog, logtaskinstance
//		where participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case' and rs_eventlog.caseid=logtaskinstance.engineinstanceid and rs_eventlog.event='start'
//		and rs_eventlog.rowid not in
//		(select a1.rowid from rs_eventlog as a1, logtaskinstance as b1, rs_eventlog as a2, logtaskinstance as b2 
//		where a1.participantid!='' and a1.participantid!='system' and a1.event!='launch_case' and a1.event!='cancel_case' and a1.caseid=b1.engineinstanceid and a2.participantid!='' and a2.participantid!='system' and a2.event!='launch_case' and a2.event!='cancel_case' and a2.caseid=b2.engineinstanceid
//		and (b1.engineinstanceid=b2.engineinstanceid or b1.engineinstanceid like b2.engineinstanceid||'.%' or b2.engineinstanceid like b1.engineinstanceid||'.%')
//		and (a1.event='start' and (a2.event='offer' or a2.event='allocate' or a2.event='start')) and a1.eventtime < a2.eventtime)
//		union
//		select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event from rs_eventlog, logtaskinstance 
//		where participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case' and rs_eventlog.caseid=logtaskinstance.engineinstanceid and rs_eventlog.event='complete'
//		and rs_eventlog.rowid not in	
//		(select a1.rowid from rs_eventlog as a1, logtaskinstance as b1, rs_eventlog as a2, logtaskinstance as b2 
//		where a1.participantid!='' and a1.participantid!='system' and a1.event!='launch_case' and a1.event!='cancel_case' and a1.caseid=b1.engineinstanceid and a2.participantid!='' and a2.participantid!='system' and a2.event!='launch_case' and a2.event!='cancel_case' and a2.caseid=b2.engineinstanceid
//		and (b1.engineinstanceid=b2.engineinstanceid or b1.engineinstanceid like b2.engineinstanceid||'.%' or b2.engineinstanceid like b1.engineinstanceid||'.%')
//		and (a1.event='complete' and (a2.event='offer' or a2.event='allocate' or a2.event='start' or a2.event='complete')) and a1.eventtime < a2.eventtime)

		
	private static jdbcImpl jdbc = new jdbcImpl();
	
	@Override
	public LinkedList<ActivityRoleTuple> getAllTaskID() {
		
		String Query = "select distinct logtaskinstance.taskid " +
					   "from rs_eventlog, logtaskinstance " +
					   "where participantid!='' and participantid!='system' and (event='offer' or event='allocate' or event='start' or event='complete') and rs_eventlog.caseid=logtaskinstance.engineinstanceid";
		ResultSet rs = jdbc.execSelect(Query);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		
		ActivityRoleTuple art = null;
		
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setTaskID(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getAllRoleID() {

		String Query = "select distinct participantid " +
		   			   "from rs_eventlog, logtaskinstance " +
		   			   "where participantid!='' and participantid!='system' and (event='offer' or event='allocate' or event='start' or event='complete') and rs_eventlog.caseid=logtaskinstance.engineinstanceid";
		ResultSet rs = jdbc.execSelect(Query);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		try {
			ActivityRoleTuple art = null;
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setRoleID(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getTaskIDs(String RoleID, boolean isRoleID, long TimeStamp,
			int isTimeStamp, int Status, boolean isStatus) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(RoleID!=null) {
			if(isRoleID) option.add("participantid='"+RoleID+"' and ");
			else option.add("participantid!='"+RoleID+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Status!=0) {
			if(isStatus) {
				if(Status==1) option.add("event='offer' and ");
				else if(Status==2) option.add("event='allocate' and ");
				else if(Status==3) option.add("event='start' and ");
				else if(Status==4) option.add("event='complete' and ");
			} else {
				if(Status==1) option.add("event!='offer' and ");
				else if(Status==2) option.add("event!='allocate' and ");
				else if(Status==3) option.add("event!='start' and ");
				else if(Status==4) option.add("event!='complete' and ");
			}
		}
		
		
		String Query1 = "select logtaskinstance.taskid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";
				
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "rs_eventlog.event='offer' " +
				"union " +
				"select logtaskinstance.taskid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = " rs_eventlog.event='allocate' " +
				"union " +
				"select logtaskinstance.taskid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = " rs_eventlog.event='start' " +
				"union " +
				"select logtaskinstance.taskid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query4 += opt; 
			}
		}
		
		String Query5 = "rs_eventlog.caseid=logtaskinstance.engineinstanceid"; 

		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4+Query5);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setTaskID(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getRoleIDs(String TaskID, boolean isTaskID, long TimeStamp,
			int isTimeStamp, int Status, boolean isStatus) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(TaskID!=null) {
			if(isTaskID) option.add("logtaskinstance.taskid='"+TaskID+"' and ");
			else option.add("logtaskinstance.taskid!='"+TaskID+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Status!=0) {
			if(isStatus) {
				if(Status==1) option.add("event='offer' and ");
				else if(Status==2) option.add("event='allocate' and ");
				else if(Status==3) option.add("event='start' and ");
				else if(Status==4) option.add("event='complete' and ");
			} else {
				if(Status==1) option.add("event!='offer' and ");
				else if(Status==2) option.add("event!='allocate' and ");
				else if(Status==3) option.add("event!='start' and ");
				else if(Status==4) option.add("event!='complete' and ");
			}
		}
		
		
		String Query1 = "select distinct participantid " +
			"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "rs_eventlog.event='offer' " +
				"union " +
				"select distinct participantid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = " rs_eventlog.event='allocate' " +
				"union " +
				"select distinct participantid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "rs_eventlog.event='start' " +
				"union " +
				"select distinct participantid " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query4 += opt; 
			}
		}
		
		String Query5 = "rs_eventlog.caseid=logtaskinstance.engineinstanceid"; 

		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4+Query5);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setRoleID(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;

	}

	@Override
	public LinkedList<ActivityRoleTuple> getTimeStamps(String TaskID, boolean isTaskID,
			String RoleID, boolean isRoleID, int Status, boolean isStatus) {

		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(TaskID!=null) {
			if(isTaskID) option.add("logtaskinstance.taskid='"+TaskID+"' and ");
			else option.add("logtaskinstance.taskid!='"+TaskID+"' and ");
		}
		if(RoleID!=null) {
			if(isRoleID) option.add("participantid='"+RoleID+"' and ");
			else option.add("participantid!='"+RoleID+"' and ");
		}
		if(Status!=0) {
			if(isStatus) {
				if(Status==1) option.add("event='offer' and ");
				else if(Status==2) option.add("event='allocate' and ");
				else if(Status==3) option.add("event='start' and ");
				else if(Status==4) option.add("event='complete' and ");
			} else {
				if(Status==1) option.add("event!='offer' and ");
				else if(Status==2) option.add("event!='allocate' and ");
				else if(Status==3) option.add("event!='start' and ");
				else if(Status==4) option.add("event!='complete' and ");
			}
		}
		
		
		String Query1 = "select distinct eventtime " +
			"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "rs_eventlog.event='offer' " +
				"union " +
				"select distinct eventtime " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "rs_eventlog.event='allocate' " +
				"union " +
				"select distinct eventtime " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "rs_eventlog.event='start' " +
				"union " +
				"select distinct eventtime " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query4 += opt; 
			}
		}
		
		String Query5 = "rs_eventlog.caseid=logtaskinstance.engineinstanceid"; 
		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4+Query5);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setTime(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public LinkedList<ActivityRoleTuple> getStatus(String TaskID, boolean isTaskID, String RoleID,
			boolean isRoleID, long TimeStamp, int isTimeStamp) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(TaskID!=null) {
			if(isTaskID) option.add("logtaskinstance.taskid='"+TaskID+"' and ");
			else option.add("logtaskinstance.taskid!='"+TaskID+"' and ");
		}
		if(RoleID!=null) {
			if(isRoleID) option.add("participantid='"+RoleID+"' and ");
			else option.add("participantid!='"+RoleID+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		
		
		String Query1 = "select distinct event " +
			"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "rs_eventlog.event='offer' " +
				"union " +
				"select distinct event " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "rs_eventlog.event='allocate' " +
				"union " +
				"select distinct event " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "rs_eventlog.event='start' " +
				"union " +
				"select distinct event " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query4 += opt; 
			}
		}
		
		String Query5 = "rs_eventlog.caseid=logtaskinstance.engineinstanceid"; 

		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4+Query5);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setStatus(""+rs.getObject(1));
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;

	}

	@Override
	public LinkedList<ActivityRoleTuple> getRows(String TaskID, boolean isTaskID, String RoleID,
			boolean isRoleID, long TimeStamp, int isTimeStamp, int Status,
			boolean isStatus) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(TaskID!=null) {
			if(isTaskID) option.add("logtaskinstance.taskid='"+TaskID+"' and ");
			else option.add("logtaskinstance.taskid!='"+TaskID+"' and ");
		}
		if(RoleID!=null) {
			if(isRoleID) option.add("participantid='"+RoleID+"' and ");
			else option.add("participantid!='"+RoleID+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Status!=0) {
			if(isStatus) {
				if(Status==1) option.add("event='offer' and ");
				else if(Status==2) option.add("event='allocate' and ");
				else if(Status==3) option.add("event='start' and ");
				else if(Status==4) option.add("event='complete' and ");
			} else {
				if(Status==1) option.add("event!='offer' and ");
				else if(Status==2) option.add("event!='allocate' and ");
				else if(Status==3) option.add("event!='start' and ");
				else if(Status==4) option.add("event!='complete' and ");
			}
		}
		
		
		String Query1 = "(select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event, b.taskname from rs_eventlog, logtaskinstance, logtask as b " +
			"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and b.taskid=logtaskinstance.taskid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "rs_eventlog.event='offer' " +
				"union " +
				"select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event, b.taskname from rs_eventlog, logtaskinstance, logtask as b " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and b.taskid=logtaskinstance.taskid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query2 += opt; 
			}
		}
		
		String Query3 = "rs_eventlog.event='allocate' " +
				"union " +
				"select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event, b.taskname from rs_eventlog, logtaskinstance, logtask as b " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and b.taskid=logtaskinstance.taskid and ";

		if(option.size()>0) {
			for (String opt : option) {
				Query3 += opt; 
			}
		}
		
		String Query4 = "rs_eventlog.event='start' " +
				"union " +
				"select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event, b.taskname from rs_eventlog, logtaskinstance, logtask as b " +
				"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and b.taskid=logtaskinstance.taskid and ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query4 += opt; 
			}
		}
		
		String Query5 = "rs_eventlog.caseid=logtaskinstance.engineinstanceid) " +
				"order by eventtime"; 

//		System.out.println(Query1+Query2+Query3+Query4+Query5);
		
		ResultSet rs = jdbc.execSelect(Query1+Query2+Query3+Query4+Query5);
		LinkedList<ActivityRoleTuple> result = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				if(rs.getString(1).contains(".")) art.setWorkDefID(rs.getString(1).substring(0, rs.getString(1).indexOf(".")));
				else art.setWorkDefID(""+rs.getObject(1));
				art.setTaskName(""+rs.getObject(6));
				art.setTaskID(""+rs.getObject(2));
				art.setRoleID(""+rs.getObject(3));
				art.setTime(""+rs.getObject(4));
				art.setStatus(""+rs.getObject(5));
				
				result.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}
	
	@Override
	public LinkedList<ActivityRoleTuple> getAllRows(String WorkDefID){
		
		String Query1 = "(select logtaskinstance.engineinstanceid, logtaskinstance.taskid, participantid, eventtime, event, b.taskname from rs_eventlog, lognetinstance as a, logtask as b, logtaskinstance " +
			"where ((participantid!='' and participantid!='system' and event!='launch_case' and event!='cancel_case') or event='cancel') and rs_eventlog.caseid=logtaskinstance.engineinstanceid and a.netinstanceid=logtaskinstance.parentnetinstanceid " +
			"and b.taskid=logtaskinstance.taskid  and rs_eventlog.taskid=taskname and ";
		
		//Create the option for the query
		if(WorkDefID!=null) {
			Query1 += "(a.engineinstanceid='"+WorkDefID+"' or a.engineinstanceid like '"+WorkDefID+".%') and ";
		}

		String Query2 = "(rs_eventlog.event='offer' or rs_eventlog.event='allocate' or rs_eventlog.event='start' or rs_eventlog.event='complete' or rs_eventlog.event='cancel')) order by eventtime"; 

		ResultSet rs = jdbc.execSelect(Query1+Query2);
		
//		System.out.println(Query1+Query2);
		
		LinkedList<ActivityRoleTuple> res = new LinkedList<ActivityRoleTuple>();
		ActivityRoleTuple art = null;
		
		try {
			while(rs.next()) {
				art = new ActivityRoleTuple();
				art.setWorkDefID(""+rs.getObject(1));
				art.setRoleID(""+rs.getObject(3));
				art.setTaskID(""+rs.getObject(2));
				art.setTaskName(""+rs.getObject(6));
				art.setStatus(""+rs.getObject(5));
				art.setTime(""+rs.getObject(4));

				res.add(art);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return res;
		
	}

}
