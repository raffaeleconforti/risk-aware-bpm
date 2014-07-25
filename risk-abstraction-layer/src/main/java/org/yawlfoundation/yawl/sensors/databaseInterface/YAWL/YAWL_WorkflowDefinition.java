package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import org.jdom.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceE.YLogGatewayClient;
import org.yawlfoundation.yawl.sensors.databaseInterface.WorkflowDefinition;
import org.yawlfoundation.yawl.sensors.jdbcImpl;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class YAWL_WorkflowDefinition extends WorkflowDefinition {

//	select lognetinstance.engineinstanceid, netname, eventtime, descriptor
//	from lognet, lognetinstance, logspecification, logevent
//	where lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1' and lognetinstance.netinstanceid=logevent.instanceid
	
	private static jdbcImpl jdbc = new jdbcImpl();
	private static InterfaceB_EnvironmentBasedClient _interfaceBClient = new InterfaceB_EnvironmentBasedClient("http://localhost:8080/yawl/ib");
	private static YLogGatewayClient _logClient = new YLogGatewayClient("http://localhost:8080/yawl/logGateway");
	String _user = "sensorService";
	String _password = "ySensor";
	
	public static void main(String[] args) {
		YAWL_WorkflowDefinition w = new YAWL_WorkflowDefinition();
		System.out.println(w.getSensors("379", "TestMitigation.yawl", "0.3"));
//		Vector<String> listWorkDef = w.getIDs(null, false, 0, 0, 0, false, w.getURI("321"), w.getVersion("321"));
//		int i = 0;
//		String WorkDefID = "321";
//		Integer diffCaseID = 0;
//		boolean found = false;
//		while(i<listWorkDef.size() && !found) {
//			System.out.println(listWorkDef.get(i)+" 321");
//			if(listWorkDef.get(i).equals(WorkDefID)) {
//				found = true;
//			}else {
//				i++;
//			}
//		}
//		if(listWorkDef.size()>0) {
//			if(found) {
//				if(i>=diffCaseID) WorkDefID = listWorkDef.get(i-diffCaseID);
//				else System.out.println("null");
//			}else {
//				if(listWorkDef.size()>=diffCaseID) WorkDefID = listWorkDef.get(listWorkDef.size()-diffCaseID);
//				else System.out.println("null");
//			}
//		}else {
//			System.out.println("null");
//		}
	}
	
	@Override
	public Vector<String> getAllID() {
		
		String Query = "select distinct cast(lognetinstance.engineinstanceid as BigInt) as id " +
					   "from lognet, lognetinstance, logspecification " +
					   "where lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'" +
					   "order by id";
	
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
	public Vector<String> getIDs(String Name, boolean isName, long Time,
			int isTime, int TypeTime, boolean isTypeTime, String URI, String Version) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(Name!=null) {
			if(isName) option.add("netname='"+Name+"' and ");
			else option.add("netname!='"+Name+"' and ");
		}
		if(Time!=0) {
			if(isTime==0) option.add("eventtime<'"+Time+"' and ");
			else if(isTime==1) option.add("eventtime<='"+Time+"' and ");
			else if(isTime==2) option.add("eventtime='"+Time+"' and ");
			else if(isTime==3) option.add("eventtime>='"+Time+"' and ");
			else if(isTime==4) option.add("eventtime>'"+Time+"' and ");
			else if(isTime==5) option.add("eventtime!='"+Time+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("descriptor='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor='CaseComplete' and ");
			}else {
				if(TypeTime==1) option.add("descriptor!='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor!='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor!='CaseComplete' and ");
			}
		}
		if(URI!=null) {
			option.add("uri='"+URI+"' and ");
		}
		if(Version!=null) {
			option.add("specversion='"+Version+"' and ");
		}
		
		String Query1 = "select distinct cast(lognetinstance.engineinstanceid as BigInt) as id " +
		   				"from lognet, lognetinstance, logspecification, logevent " +
		   				"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1' and lognetinstance.netinstanceid=logevent.instanceid " +
				"order by id";

		ResultSet rs = jdbc.execSelect(Query1+Query2);
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
	public Vector<String> getNames(String ID, boolean isID, long Time,
			int isTime, int TypeTime, boolean isTypeTime, String URI, String Version) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("lognetinstance.engineinstanceid='"+ID+"' and ");
			else option.add("lognetinstance.engineinstanceid!='"+ID+"' and ");
		}
		if(Time!=0) {
			if(isTime==0) option.add("eventtime<'"+Time+"' and ");
			else if(isTime==1) option.add("eventtime<='"+Time+"' and ");
			else if(isTime==2) option.add("eventtime='"+Time+"' and ");
			else if(isTime==3) option.add("eventtime>='"+Time+"' and ");
			else if(isTime==4) option.add("eventtime>'"+Time+"' and ");
			else if(isTime==5) option.add("eventtime!='"+Time+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("descriptor='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor='CaseComplete' and ");
			}else {
				if(TypeTime==1) option.add("descriptor!='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor!='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor!='CaseComplete' and ");
			}
		}
		if(URI!=null) {
			option.add("uri='"+URI+"' and ");
		}
		if(Version!=null) {
			option.add("specversion='"+Version+"' and ");
		}
		
		String Query1 = "select distinct netname " +
		   				"from lognet, lognetinstance, logspecification, logevent " +
		   				"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1' and lognetinstance.netinstanceid=logevent.instanceid";

		ResultSet rs = jdbc.execSelect(Query1+Query2);
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

//	@Override
	public Vector<String> getTimes(String ID, boolean isID, String Name,
			boolean isName, int TypeTime, boolean isTypeTime, String URI,
			String Version) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("lognetinstance.engineinstanceid='"+ID+"' and ");
			else option.add("lognetinstance.engineinstanceid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("netname='"+Name+"' and ");
			else option.add("netname!='"+Name+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("descriptor='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor='CaseComplete' and ");
			}else {
				if(TypeTime==1) option.add("descriptor!='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor!='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor!='CaseComplete' and ");
			}
		}
		if(URI!=null) {
			option.add("uri='"+URI+"' and ");
		}
		if(Version!=null) {
			option.add("specversion='"+Version+"' and ");
		}
		
		String Query1 = "select distinct eventtime " +
		   				"from lognet, lognetinstance, logspecification, logevent " +
		   				"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1' and lognetinstance.netinstanceid=logevent.instanceid";

		ResultSet rs = jdbc.execSelect(Query1+Query2);
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
	public String getURI(String ID) {
		
		String Query = "select distinct uri " +
		   "from lognet, lognetinstance, logspecification " +
		   "where lognetinstance.engineinstanceid = '"+ID+"' and lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'";
		
		ResultSet rs = jdbc.execSelect(Query);
		try {
			rs.next();
			return ""+rs.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
	}
	
	@Override
	public String getVersion(String ID) {
		
		String Query = "select distinct specversion " +
		   "from lognet, lognetinstance, logspecification " +
		   "where lognetinstance.engineinstanceid = '"+ID+"' and lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'";

		ResultSet rs = jdbc.execSelect(Query);
		try {
			rs.next();
			return ""+rs.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
	}
	
	@Override
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name,
			boolean isName, long Time, int isTime, int TypeTime, boolean isTypeTime,
			String URI, String Version) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("lognetinstance.engineinstanceid='"+ID+"' and ");
			else option.add("lognetinstance.engineinstanceid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("netname='"+Name+"' and ");
			else option.add("netname!='"+Name+"' and ");
		}
		if(Time!=0) {
			if(isTime==0) option.add("eventtime<'"+Time+"' and ");
			else if(isTime==1) option.add("eventtime<='"+Time+"' and ");
			else if(isTime==2) option.add("eventtime='"+Time+"' and ");
			else if(isTime==3) option.add("eventtime>='"+Time+"' and ");
			else if(isTime==4) option.add("eventtime>'"+Time+"' and ");
			else if(isTime==5) option.add("eventtime!='"+Time+"' and ");
		}
		if(TypeTime!=0) {
			if(isTypeTime) {
				if(TypeTime==1) option.add("descriptor='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor='CaseComplete' and ");
			}else {
				if(TypeTime==1) option.add("descriptor!='CaseStart' and ");
				else if(TypeTime==2) option.add("descriptor!='CaseCancel' and ");
				else if(TypeTime==3) option.add("descriptor!='CaseComplete' and ");
			}
		}
		if(URI!=null) {
			option.add("uri='"+URI+"' and ");
		}
		if(Version!=null) {
			option.add("specversion='"+Version+"' and ");
		}
		
		String Query1 = "select lognetinstance.engineinstanceid as ID, netname as Name, eventtime as Time, descriptor as TypeTime" +
		   				"from lognet, lognetinstance, logspecification, logevent " +
		   				"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1' and lognetinstance.netinstanceid=logevent.instanceid";

		ResultSet rs = jdbc.execSelect(Query1+Query2);
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		try {
			while(rs.next()) {
				Vector<String> tmp = new Vector<String>();
				tmp.add(""+rs.getObject(1));
				tmp.add(""+rs.getObject(2));
				tmp.add(""+rs.getObject(3));
				tmp.add(""+rs.getObject(4));
				result.add(tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(result.size()>0) return result;
		else return null;
		
	}

	@Override
	public String getSensors(String ID, String URI, String Version) {
		
		String identifier = null;
		String Query = "select distinct identifier " +
		   "from lognet, lognetinstance, logspecification " +
		   "where lognetinstance.engineinstanceid = '"+ID+"' and lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'";

		ResultSet rs = null;
		try {
			while(rs==null || !rs.next()) {
				rs = jdbc.execSelect(Query);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			identifier=""+rs.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
//			String specXML = _interfaceBClient.getSpecification(new YSpecificationID(URI), _interfaceBClient.connect("sensorService", "ySensor"));
			String specXML = _interfaceBClient.getSpecification(new YSpecificationID(identifier, Version, URI), _interfaceBClient.connect("sensorService", "ySensor"));
			Element e = JDOMUtil.stringToDocument(specXML).getRootElement();
			for(Element spec: (List<Element>)e.getChildren()) {
				if("specification".equals(spec.getName())) {
					for(Element sensors : (List<Element>)spec.getChildren()) {
						if("sensors".equals(sensors.getName())) {
							return JDOMUtil.elementToString(sensors);
						}
					}
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public String getSpecification(String ID, String URI, String Version) {

		String identifier = null;
		String Query = "select distinct identifier " +
		   "from lognet, lognetinstance, logspecification " +
		   "where lognetinstance.engineinstanceid = '"+ID+"' and lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'";

		ResultSet rs = null;
		try {
			while(rs==null || !rs.next()) {
				rs = jdbc.execSelect(Query);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			identifier=""+rs.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			String specXML = _interfaceBClient.getSpecification(new YSpecificationID(identifier, Version, URI), _interfaceBClient.connect(_user, _password));
			return specXML;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getSpecification(String URI) {
		
		try {
			String specXML = _interfaceBClient.getSpecification(new YSpecificationID(URI), _interfaceBClient.connect(_user, _password));
			return specXML;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public String getOpenXESLog(String ID, String URI, String Version) {
		
		String identifier = null;
		String Query = "select distinct identifier " +
		   "from lognet, lognetinstance, logspecification " +
		   "where lognetinstance.engineinstanceid = '"+ID+"' and lognetinstance.netid = lognet.netid and lognet.speckey=logspecification.rowkey and lognetinstance.parenttaskinstanceid='-1'";

		ResultSet rs = null;
		try {
			while(rs==null || !rs.next()) {
				rs = jdbc.execSelect(Query);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			identifier=""+rs.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			String log = _logClient.getSpecificationXESLog(new YSpecificationID(identifier, Version, URI), _logClient.connect(_user, _password));
			return log;
		} catch (IOException e) {
			return null;
		}
	}

}
