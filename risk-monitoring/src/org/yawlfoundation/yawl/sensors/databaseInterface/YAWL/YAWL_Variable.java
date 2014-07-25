package org.yawlfoundation.yawl.sensors.databaseInterface.YAWL;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import net.sf.saxon.s9api.SaxonApiException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.Marshaller;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.TaskInformation;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.sensors.jdbcImpl;
import org.yawlfoundation.yawl.sensors.databaseInterface.Variable;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.SaxonUtil;

public class YAWL_Variable extends Variable {

//	select dataitemid as id, itemname as name, itemvalue as value, taskid as taskid, logevent.eventid as variablesid, eventtime as timestamp
//	from logevent, logtaskinstance, logdataitem
//	where logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid
	
	private static jdbcImpl jdbc= new jdbcImpl(); 
	private static InterfaceB_EnvironmentBasedClient _interfaceBClient = new InterfaceB_EnvironmentBasedClient("http://localhost:8080/yawl/ib");
	
	@Override
	public Vector<String> getAllID() {
		
		String Query = "select distinct dataitemid from logdataitem";
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
	public Vector<String> getIDs(String Name, boolean isName, long TimeStamp,
			int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID,
			boolean isTaskID, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(Name!=null) {
			if(isName) option.add("itemname='"+Name+"' and ");
			else option.add("itemname!='"+Name+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Value!=null) {
			if(isValue) option.add("itemvalue='"+Value+"' and ");
			else option.add("itemvalue!='"+Value+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(TaskID!=null) {
			if(isTaskID) option.add("taskid='"+TaskID+"' and ");
			else option.add("taskid!='"+TaskID+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		String Query1 = "select distinct dataitemid as id " +
						"from logevent, logtaskinstance, logdataitem " +
						"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";

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
	public Vector<String> getNames(String ID, boolean isID, long TimeStamp,
			int isTimeStamp, String Value, boolean isValue, int TypeAssigment, String TaskID,
			boolean isTaskID, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("dataitemid='"+ID+"' and ");
			else option.add("dataitemid!='"+ID+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Value!=null) {
			if(isValue) option.add("itemvalue='"+Value+"' and ");
			else option.add("itemvalue!='"+Value+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(TaskID!=null) {
			if(isTaskID) option.add("taskid='"+TaskID+"' and ");
			else option.add("taskid!='"+TaskID+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		String Query1 = "select distinct itemname as name " +
						"from logevent, logtaskinstance, logdataitem " +
						"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";

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
	public Vector<String> getTimeStamps(String ID, boolean isID, String Name,
			boolean isName, String Value, boolean isValue, int TypeAssigment, String TaskID,
			boolean isTaskID, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("dataitemid='"+ID+"' and ");
			else option.add("dataitemid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("itemname='"+Name+"' and ");
			else option.add("itemname!='"+Name+"' and ");
		}
		if(Value!=null) {
			if(isValue) option.add("itemvalue='"+Value+"' and ");
			else option.add("itemvalue!='"+Value+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(TaskID!=null) {
			if(isTaskID) option.add("taskid='"+TaskID+"' and ");
			else option.add("taskid!='"+TaskID+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		String Query1 = "select distinct eventtime as timestamp " +
						"from logevent, logtaskinstance, logdataitem " +
						"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";

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
	public Vector<String> getValues(String ID, boolean isID, String Name,
			boolean isName, long TimeStamp, int isTimeStamp, int TypeAssigment, String TaskID,
			boolean isTaskID, String VariablesID, boolean isVariablesID, boolean isActivity) {
		
		boolean activity = isActivity;
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("dataitemid='"+ID+"' and ");
			else option.add("dataitemid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("itemname='"+Name+"' and ");
			else option.add("itemname!='"+Name+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(TaskID!=null) {
			if(isTaskID) option.add("taskid='"+TaskID+"' and ");
			else option.add("taskid!='"+TaskID+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		if(activity) {
		
			String Query1 = "select distinct itemvalue as value " +
							"from logevent, logtaskinstance, logdataitem " +
							"where ";
			
			if(option.size()>0) {
				for (String opt : option) {
					Query1 += opt; 
				}
			}
	
			String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";
	
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
			
		}else {
			
			String identifier = null;
			String Version = null;
			String URI = null;
			String netName = null;
			String WorkDefID = null;
			
			String Query = "select identifier, version, uri, netname, engineinstanceid " +
			   "from logevent as a, lognetinstance as b, lognet as c, logspecification as d " +
			   "where a.eventid='"+VariablesID+"' and a.instanceid=b.netinstanceid and b.netid=c.netid and c.speckey=d.rowkey";

			ResultSet rs = jdbc.execSelect(Query);
			try {
				identifier=""+rs.getObject(1);
				Version=""+rs.getObject(2);
				URI=""+rs.getObject(3);
				netName=""+rs.getObject(4);
				WorkDefID=""+rs.getObject(5);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
			Element specificationSet = null;
			
			try {
				String specXML = _interfaceBClient.getSpecification(new YSpecificationID(identifier, Version, URI), _interfaceBClient.connect("sensorService", "ySensor"));
				specificationSet = JDOMUtil.stringToDocument(specXML).getRootElement();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			
			try {
				HashMap<String, String> var = new HashMap<String, String>();
				String netVarQuery = "select lognetinstance.engineinstanceid, lognet.netname, logtask.taskname, logevent.eventtime, logdataitem.itemname, logdataitem.itemvalue, logdataitem.descriptor " +
									"from logtask, lognet, lognetinstance, logtaskinstance, logevent, logdataitem " +
									"where logtask.taskID = logtaskinstance.taskID and logtask.parentNetID = lognet.netid and lognetinstance.netid = lognet.netid and lognetinstance.netinstanceid = logtaskinstance.parentnetinstanceid and logtaskinstance.taskinstanceid = logevent.instanceid and logevent.eventid = logdataitem.eventid and (logdataitem.descriptor = 'InputVarAssignment' or logdataitem.descriptor = 'OutputVarAssignment') " +
									"and lognet.netname = '"+netName+"' and lognetinstance.engineinstanceid = '"+WorkDefID+"' ORDER BY lognetinstance.engineinstanceid, logevent.eventtime";

				rs = jdbc.execSelect(netVarQuery);
				for(Element specification : getChild(specificationSet, "specification")) {
					for(Element decomposition : getChild(specification, "decomposition")) {
						if(decomposition.getAttributeValue("id").equals(netName)) {
							for(Element processControlElement : getChild(decomposition, "processControlElements")) {
								for(Element task : getChild(processControlElement, "task")) {
									rs.beforeFirst();
									String decomposeToID = null;
									for(Element decomposeTo : getChild(task, "decomposesTo")) {
										decomposeToID = decomposeTo.getAttributeValue("id");
									}
									for(Element decomposition2 : getChild(specification, "decomposition")) {
										if(decomposition2.getAttributeValue("id").equals(decomposeToID) && decomposition2.getAttributeValue("type", decomposition2.getNamespace("xsi")).equals("WebServiceGatewayFactsType")) {
											for(Element name : getChild(task, "name")) {
												decomposeToID = name.getValue();
											}
										}
									}
									List<Element> listCompletedMappings = getChild(task, "completedMappings");
									if(listCompletedMappings.size()>0) {
										String logVariable = null;
										HashMap<String, String> variable = new HashMap<String, String>();
										while(rs.next()) {
											if(rs.getString(3).equals(decomposeToID)) {
									    		String variableName = rs.getString(5);
									    		String definition = rs.getString(6);
									    		String type = rs.getString(7);
									    		if(type.equals("InputVarAssignment")) {
									    			variable.clear();
									    		}
												if(!definition.startsWith("<"+variableName+">")) {
													definition = "<"+variableName+">"+definition+"</"+variableName+">";
													variable.put(variableName, definition);
												}else {
													variable.put(variableName, definition);
												}
											}
								    	}
										StringBuffer sb = new StringBuffer();
										if(variable.size()>0) {
											sb.append("<"+decomposeToID.replace(" ", "_")+">");
											for(Entry<String, String> entry : variable.entrySet()) {
												sb.append(entry.getValue());
											}
											sb.append("</"+decomposeToID.replace(" ", "_")+">");
											logVariable = sb.toString();
										}
										if(logVariable != null) {
											for(Element mapping : getChild(listCompletedMappings.get(0), "mapping")) {
												String varMap = null;
												for(Element mapsTo : getChild(mapping, "mapsTo")) {
													varMap = mapsTo.getValue();
												}
												String expression = null;
												for(Element expressionEl : getChild(mapping, "expression")) {
													expression = expressionEl.getAttributeValue("query").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replace(' ', '_');
												}
												try {
											 	    var.put(varMap, SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument(logVariable)));
												} catch(SaxonApiException sae) { sae.printStackTrace();}
											}
										}
									}
								}
							}
						}
					}
				}
				if(var.size()>0) {
					Vector<String> res = new Vector<String>();
					if(isName) {
						res.add(var.get(Name));
					} else {
						for(Entry<String, String> entry : var.entrySet()) {
							if(!entry.getKey().equals(Name)) {
								res.add(entry.getValue());
							}
						}
					}
					return res;
				}else {
					return null;
				}
			}catch (SQLException sqle) {
				return null;
			}
			
		}
		
	}
	
    /**
     * Utility Method return the children of e with name nameChild
     * @param e Element father
     * @param nameChild tag name of the Element child
     * @return return the children of e with name nameChild
     */
    private List<Element> getChild(Element e, String nameChild) {
    	List<Element> list = new LinkedList<Element>();
    	List<Element> oldList = e.getChildren();
    	for(int i=0; i<oldList.size(); i++) {
    		Element child = oldList.get(i);
    		if(child.getName().equals(nameChild)) {
    			list.add(child);
    		}
    	}
    	return list;
    }

	@Override
	public Vector<String> getTaskIDs(String ID, boolean isID, String Name,
			boolean isName, long TimeStamp, int isTimeStamp, String Value,
			boolean isValue, int TypeAssigment, String VariablesID, boolean isVariablesID) {
		
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("dataitemid='"+ID+"' and ");
			else option.add("dataitemid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("itemname='"+Name+"' and ");
			else option.add("itemname!='"+Name+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Value!=null) {
			if(isValue) option.add("itemvalue='"+Value+"' and ");
			else option.add("itemvalue!='"+Value+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		String Query1 = "select distinct taskid " +
						"from logevent, logtaskinstance, logdataitem " +
						"where ";
		
		if(option.size()>0) {
			for (String opt : option) {
				Query1 += opt; 
			}
		}

		String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";

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
	public Vector<Vector<String>> getRows(String ID, boolean isID, String Name,
			boolean isName, long TimeStamp, int isTimeStamp, String Value,
			boolean isValue, int TypeAssigment, String TaskID, boolean isTaskID,
			String VariablesID, boolean isVariablesID, boolean isActivity) {
		
		boolean activity=isActivity;
		LinkedList<String> option = new LinkedList<String>();
		
		//Create the option for the query
		if(ID!=null) {
			if(isID) option.add("dataitemid='"+ID+"' and ");
			else option.add("dataitemid!='"+ID+"' and ");
		}
		if(Name!=null) {
			if(isName) option.add("itemname='"+Name+"' and ");
			else option.add("itemname!='"+Name+"' and ");
		}
		if(TimeStamp!=0) {
			if(isTimeStamp==0) option.add("eventtime<'"+TimeStamp+"' and ");
			else if(isTimeStamp==1) option.add("eventtime<='"+TimeStamp+"' and ");
			else if(isTimeStamp==2) option.add("eventtime='"+TimeStamp+"' and ");
			else if(isTimeStamp==3) option.add("eventtime>='"+TimeStamp+"' and ");
			else if(isTimeStamp==4) option.add("eventtime>'"+TimeStamp+"' and ");
			else if(isTimeStamp==5) option.add("eventtime!='"+TimeStamp+"' and ");
		}
		if(Value!=null) {
			if(isValue) option.add("itemvalue='"+Value+"' and ");
			else option.add("itemvalue!='"+Value+"' and ");
		}
		if(TypeAssigment!=0) {
			if(TypeAssigment==1) option.add("logdataitem.descriptor='InputVarAssignment' and ");
			else if(TypeAssigment==2) option.add("logdataitem.descriptor='OutputVarAssignment' and ");
		}
		if(TaskID!=null) {
			if(isTaskID) option.add("taskid='"+TaskID+"' and ");
			else option.add("taskid!='"+TaskID+"' and ");
		}
		if(VariablesID!=null) {
			if(isVariablesID) option.add("logtaskinstance.taskinstanceid='"+VariablesID+"' and ");
			else option.add("logtaskinstance.taskinstanceid!='"+VariablesID+"' and ");
		}
		
		if(activity) {
		
			String Query1 = "select dataitemid as id, itemname as name, itemvalue as value, taskid as taskid, logevent.eventid as variablesid, eventtime as timestamp " +
							"from logevent, logtaskinstance, logdataitem " +
							"where ";
			
			if(option.size()>0) {
				for (String opt : option) {
					Query1 += opt; 
				}
			}
	
			String Query2 = "logevent.descriptor='DataValueChange' and logevent.instanceid=logtaskinstance.taskinstanceid and logevent.eventid=logdataitem.eventid";
	
//			System.out.println(Query1+Query2);
			
			ResultSet rs = jdbc.execSelect(Query1+Query2);
			Vector<Vector<String>> result = new Vector<Vector<String>>();
			try {
				while(rs.next()) {
					Vector<String> tmp = new Vector<String>();
					tmp.add(""+rs.getObject(1));
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
			
		}else {
			
			String identifier = null;
			String Version = null;
			String URI = null;
			String netName = null;
			String WorkDefID = null;
			
			String Query = "select identifier, specversion, uri, netname, engineinstanceid " +
			   "from logevent as a, lognetinstance as b, lognet as c, logspecification as d " +
			   "where a.eventid='"+VariablesID+"' and a.instanceid=b.netinstanceid and b.netid=c.netid and c.speckey=d.rowkey";

			ResultSet rs = jdbc.execSelect(Query);
//			System.out.println(Query);
			try {
				rs.next();
				identifier=""+rs.getObject(1);
				Version=""+rs.getObject(2);
				URI=""+rs.getObject(3);
				netName=""+rs.getObject(4);
				WorkDefID=""+rs.getObject(5);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
			Element specificationSet = null;
			String specXML = null;
			
			try {
				specXML = _interfaceBClient.getSpecification(new YSpecificationID(identifier, Version, URI), _interfaceBClient.connect("sensorService", "ySensor"));
				specificationSet = JDOMUtil.stringToDocument(specXML).getRootElement();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			
			try {
				HashMap<String, String> var = new HashMap<String, String>();
				String netVarQuery = "select lognetinstance.engineinstanceid, lognet.netname, logtask.taskname, logevent.eventtime, logdataitem.itemname, logdataitem.itemvalue, logdataitem.descriptor " +
									"from logtask, lognet, lognetinstance, logtaskinstance, logevent, logdataitem " +
									"where logtask.taskID = logtaskinstance.taskID and logtask.parentNetID = lognet.netid and lognetinstance.netid = lognet.netid and lognetinstance.netinstanceid = logtaskinstance.parentnetinstanceid and logtaskinstance.taskinstanceid = logevent.instanceid and logevent.eventid = logdataitem.eventid and (logdataitem.descriptor = 'InputVarAssignment' or logdataitem.descriptor = 'OutputVarAssignment') " +
									"and lognet.netname = '"+netName+"' and lognetinstance.engineinstanceid = '"+WorkDefID+"' ORDER BY lognetinstance.engineinstanceid, logevent.eventtime";

				rs = jdbc.execSelect(netVarQuery);
//				System.out.println(netVarQuery);
				String filteredOutputData;
		        // Now if this is beta4 or greater then remove all those input only bits of data
		        // by first preparing a list of output params to iterate over.
				
				for(Element specification : getChild(specificationSet, "specification")) {
					for(Element decomposition : getChild(specification, "decomposition")) {
						if(decomposition.getAttributeValue("id").equals(netName)) {
							for(Element processControlElement : getChild(decomposition, "processControlElements")) {
								for(Element task : getChild(processControlElement, "task")) {
									rs.beforeFirst();
									String decomposeToID = null;
									for(Element decomposeTo : getChild(task, "decomposesTo")) {
										decomposeToID = decomposeTo.getAttributeValue("id");
									}
									for(Element decomposition2 : getChild(specification, "decomposition")) {
										if(decomposition2.getAttributeValue("id").equals(decomposeToID) && decomposition2.getAttributeValue("type", decomposition2.getNamespace("xsi")).equals("WebServiceGatewayFactsType")) {
											for(Element name : getChild(task, "name")) {
												decomposeToID = name.getValue();
											}
										}
									}
									List<Element> listCompletedMappings = getChild(task, "completedMappings");
									if(listCompletedMappings.size()>0) {
										String logVariable = null;
										HashMap<String, String> variable = new HashMap<String, String>();
										while(rs.next()) {
											if(rs.getString(3).equals(decomposeToID)) {
									    		String variableName = rs.getString(5);
									    		String definition = rs.getString(6);
									    		String type = rs.getString(7);
									    		if(type.equals("InputVarAssignment")) {
									    			variable.clear();
									    		}
												if(!definition.startsWith("<"+variableName+">")) {
													definition = "<"+variableName+">"+definition+"</"+variableName+">";
													variable.put(variableName, definition);
												}else {
													variable.put(variableName, definition);
												}
											}
								    	}
										StringBuffer sb = new StringBuffer();
										if(variable.size()>0) {
											String nameTask = decomposeToID.replace(" ", "_");
											nameTask = nameTask.substring(0, nameTask.lastIndexOf("_"));
											sb.append("<"+nameTask+">");
											for(Entry<String, String> entry : variable.entrySet()) {
												sb.append(entry.getValue());
											}
											sb.append("</"+nameTask+">");
											logVariable = sb.toString();
										}
										if(logVariable != null) {
											for(Element mapping : getChild(listCompletedMappings.get(0), "mapping")) {
												String varMap = null;
												for(Element mapsTo : getChild(mapping, "mapsTo")) {
													varMap = mapsTo.getValue();
												}
												String expression = null;
												for(Element expressionEl : getChild(mapping, "expression")) {
													expression = expressionEl.getAttributeValue("query").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replace(' ', '_');
												}
												try {
											 	    var.put(varMap, SaxonUtil.evaluateQuery(expression, JDOMUtil.stringToDocument(logVariable)));
												} catch(SaxonApiException sae) { sae.printStackTrace();}
											}
										}
									}
								}
							}
						}
					}
				}
				if(var.size()>0) {
					Vector<Vector<String>> result = new Vector<Vector<String>>();
					for(Entry<String, String> entry : var.entrySet()) {
						Vector<String> tmp = new Vector<String>();
						tmp.add("");
						tmp.add(""+entry.getKey());
						tmp.add(""+entry.getValue());
						tmp.add("");
						tmp.add("");
						tmp.add("");
						result.add(tmp);
					}
					if(result.size()>0) return result;
					else return null;
				}else {
					return null;
				}
			}catch (SQLException sqle) {
				return null;
			}
			
		}
		
	}

}
