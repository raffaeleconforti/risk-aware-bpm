package org.yawlfoundation.yawl.sensors;

import java.util.HashMap;
import java.util.Set;

public class YSensorMessageUpdate {

	private HashMap<String, Object> variables = new HashMap<String, Object>();
	private HashMap<String, Object> distribution = new HashMap<String, Object>();
	private HashMap<String, Object> logVariables = new HashMap<String, Object>();
	private HashMap<String, Object> logResources = new HashMap<String, Object>();
	private HashMap<String, Object> timeVariables = new HashMap<String, Object>();
	private HashMap<String, Object> resourceVariables = new HashMap<String, Object>();
	private HashMap<String, Object> role = new HashMap<String, Object>();
	private HashMap<String, Object> participant = new HashMap<String, Object>();
	
	/**
	 * Return all the values of the Variables 
	 * @return
	 */
	public HashMap<String, Object> getAllVariables() {
		return variables;
	}
	
	/**
	 * Return all the value of the Distributions
	 * @return
	 */
	public HashMap<String, Object> getDistribution() {
		return distribution;
	}
	
	/**
	 * Return all the value of the LogVariables
	 * @return
	 */
	public HashMap<String, Object> getLogVariables() {
		return logVariables;
	}
	
	/**
	 * Return all the value of the LogResources
	 * @return
	 */
	public HashMap<String, Object> getLogResources() {
		return logResources;
	}
	
	/**
	 * Return all the value of the TimeVariables
	 * @return
	 */
	public HashMap<String, Object> getTimeVariables() {
		return timeVariables;
	}
	
	/**
	 * Return all the value of the Resource Variables
	 * @return
	 */
	public HashMap<String, Object> getResourceVariables() {
		return resourceVariables;
	}
	
	/**
	 * Return all the value of the Role
	 * @return
	 */
	public HashMap<String, Object> getRole() {
		return role;
	}
	
	/**
	 * Return all the value of the Parcitipant
	 * @return
	 */
	public HashMap<String, Object> getParticipant() {
		return participant;
	}
	
	/**
	 * Add a new Variable value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addVariable(String variableName, Object variableValue) {
		variables.put(variableName, variableValue);
	}
	
	/**
	 * Add a new distribution value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addDistribution(String variableName, Object variableValue) {
		distribution.put(variableName, variableValue);
	}
	
	/**
	 * Add a new LogVariable value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addLogVariable(String variableName, Object variableValue) {
		logVariables.put(variableName, variableValue);
	}
	
	/**
	 * Add a new LogResource value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addLogResource(String variableName, Object variableValue) {
		logResources.put(variableName, variableValue);
	}
		
	/**
	 * Add a Time value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addTime(String variableName, Object variableValue) {
		timeVariables.put(variableName, variableValue);
	}
	
	/**
	 * Add a new Resource value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addResource(String variableName, Object variableValue) {
		resourceVariables.put(variableName, variableValue);
	}
	
	/**
	 * Add a new Role value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addRole(String variableName, Object variableValue) {
		role.put(variableName, variableValue);
	}
	
	/**
	 * Add a new Participant value
	 * @param variableName Name of the Variable
	 * @param variableValue Value of the Variable
	 */
	public void addParticipant(String variableName, Object variableValue) {
		participant.put(variableName, variableValue);
	}
	
	/**
	 * Return the number of all the variable
	 * @return
	 */
	public int variablesSize(){
		return (variables.size()+logVariables.size()+resourceVariables.size()+logResources.size()+timeVariables.size()+distribution.size()+role.size()+participant.size());
	}
		
	/**
	 * Return the keySet of the Variables
	 * @return
	 */
	public Set<String> variableNames(){
		return variables.keySet();
	}
	
	/**
	 * Return the number of the LogVariables 
	 * @return
	 */
	public int logVariablesSize(){
		return logVariables.size();
	}
		
	/**
	 * Return the keySet of the LogVariables
	 * @return
	 */
	public Set<String> logVariableNames(){
		return logVariables.keySet();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(variables.size()>0)sb.append("Variables "+variables.toString());
		if(distribution.size()>0)sb.append("Distribution "+ distribution.toString());
		if(role.size()>0)sb.append("Role "+ role.toString());
		if(participant.size()>0)sb.append("Participant "+ participant.toString());
		if(logVariables.size()>0)sb.append("LogVariables "+logVariables.toString());
		if(logResources.size()>0)sb.append("LogResources "+logResources.toString());
		if(timeVariables.size()>0) {
			sb.append("\nTimeVariables");
			for(String s:timeVariables.keySet()){
				sb.append("\nTimeVariable "+s+" "+timeVariables.get(s));
			}
		}
		if(resourceVariables.size()>0) {
			sb.append("\nResourceVariables");
			for(String s:resourceVariables.keySet()){
				sb.append("\nResourceVariable "+s+" "+resourceVariables.get(s));
			}
		}
		return sb.toString();
	}
	
}
