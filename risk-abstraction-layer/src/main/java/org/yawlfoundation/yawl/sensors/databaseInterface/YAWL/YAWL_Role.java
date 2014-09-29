package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import org.jdom2.Element;
import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.resourcing.rsInterface.ResourceGatewayClient;
import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;
import org.yawlfoundation.yawl.sensors.databaseInterface.Role;
import org.yawlfoundation.yawl.sensors.jdbcImpl;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class YAWL_Role extends Role {

	private static jdbcImpl jdbc = new jdbcImpl();
	private static ResourceGatewayClient _resClient = new ResourceGatewayClient("http://localhost:8080/resourceService/gateway");
	private static WorkQueueGatewayClient _workClient = new WorkQueueGatewayClient("http://localhost:8080/resourceService/workqueuegateway");
	private static String _user = "sensorService";
	private static String _password = "ySensor";
	private static String session = null;
	
	@Override
	public Vector<String> getAllID() {
		
		String Query = "select distinct participantid from rs_participant";
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
	public Vector<String> getIDs(String Name, boolean isName, String Surname,
			boolean isSurname) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(Name!=null) {
			if(isName) option.add("firstname='"+Name+"'");
			else option.add("firstname!='"+Name+"'");
		}
		if(Surname!=null) {
			if(isName) option.add("lastname='"+Surname+"'");
			else option.add("lastname!='"+Surname+"'");
		}
		
		String Query = "select distinct participantid from rs_participant";
		
		if(option.size()>0) {
			Query += " where ";
			for (String opt : option) {
				Query += opt;
				if(option.size()==2) Query += " and ";
			}
		}
		
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
	public Vector<String> getNames(String RoleID) {
		
		String option = null;
		
		//Create the option for the query
		if(RoleID!=null) {
			option = "participantid='"+RoleID+"'";
		}
		
		String Query = "select distinct firstname, lastname from rs_participant";
		
		if(option!=null) {
			Query += " where "+option;
		}
		
		ResultSet rs = jdbc.execSelect(Query);
		Vector<String> result = new Vector<String>();
		try {
			while(rs.next()) {
				result.add(""+rs.getObject(1));
				result.add(""+rs.getObject(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}
	
	public String getParticipantNameFromUserID(String userID) {
		
		String option = "userid='"+userID+"'";
		
		String Query = "select participantid from rs_participant";
		
		if(option!=null) {
			Query += " where "+option;
		}
		
		ResultSet rs = jdbc.execSelect(Query);
		String result = null;
		try {
			while(rs.next()) {
				result = ""+rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
		
	}

	@Override
	public String getRoleRInfo(String RoleID) {
		try {
			return _resClient.getRole(RoleID, getConnection());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getRoleInfo(String RoleID) {
		try {
			return _resClient.getParticipant(RoleID, getConnection());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public LinkedList<String> getParticipantWithRole(String role) {
		try {
			LinkedList<String> res = new LinkedList<String>();
			Element e = JDOMUtil.stringToDocument(_resClient.getParticipantsWithRole(role, getConnection())).getRootElement();
			for(Element child : (List<Element>) e.getChildren()) {
				res.add(JDOMUtil.elementToString(child));
			}
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    @Override
    public String getRoleName(String role) {
    	try {
    		Element e = JDOMUtil.stringToDocument(_resClient.getRole(role, getConnection())).getRootElement();
    		
    		return e.getChild("name").getValue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    @Override
    public LinkedList<String[]> getTasksOffered(String participantID){
    	try {
    		HashSet<String[]> res = new HashSet<String[]>();
			Element e = JDOMUtil.stringToDocument(_workClient.getQueuedWorkItems(participantID, WorkQueue.OFFERED, _workClient.connect("sensorService", "ySensor"))).getRootElement();
			for(Element child : (List<Element>) e.getChildren()) {
				String taskName = child.getChild("taskname").getValue();
				String caseID = child.getChild("caseid").getValue();
				if(caseID.contains("")) {
					caseID = caseID.substring(0, caseID.indexOf(""));
				}
				res.add(new String[] {caseID, taskName});
			}
			return new LinkedList<String[]>(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    @Override
    public LinkedList<String[]> getTasksAllocated(String participantID){
    	try {
    		HashSet<String[]> res = new HashSet<String[]>();
			Element e = JDOMUtil.stringToDocument(_workClient.getQueuedWorkItems(participantID, WorkQueue.ALLOCATED, _workClient.connect("sensorService", "ySensor"))).getRootElement();
			for(Element child : (List<Element>) e.getChildren()) {
				String taskName = child.getChild("taskname").getValue();
				String caseID = child.getChild("caseid").getValue();
				caseID = caseID.substring(0, caseID.indexOf(""));
				res.add(new String[] {caseID, taskName});
			}
			return new LinkedList<String[]>(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    @Override
    public LinkedList<String[]> getTasksStarted(String participantID){
    	try {
    		HashSet<String[]> res = new HashSet<String[]>();
			Element e = JDOMUtil.stringToDocument(_workClient.getQueuedWorkItems(participantID, WorkQueue.STARTED, _workClient.connect("sensorService", "ySensor"))).getRootElement();
			for(Element child : (List<Element>) e.getChildren()) {
				String taskName = child.getChild("taskname").getValue();
				String caseID = child.getChild("caseid").getValue();
				caseID = caseID.substring(0, caseID.indexOf(""));
				res.add(new String[] {caseID, taskName});
			}
			return new LinkedList<String[]>(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
	
	private String getConnection() {
		try {
			while(!_resClient.checkConnection(session).equalsIgnoreCase("true")) {
				session = _resClient.connect(_user, _password);
			}
			return session;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
