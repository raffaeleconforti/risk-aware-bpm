package org.yawlfoundation.yawl.sensors;

import java.util.LinkedList;

public class YSensorsEditor {
	
	private String generalTime = null;
	private LinkedList<String> names = new LinkedList<String>();
	
	private LinkedList<String> riskProbabilityList = new LinkedList<String>();
	private LinkedList<String> riskThresholdList = new LinkedList<String>();
	private LinkedList<String> riskMessageList = new LinkedList<String>();
	
	private LinkedList<String> faultConditionList = new LinkedList<String>();
	private LinkedList<String> faultMessageList = new LinkedList<String>();
	
	private LinkedList<String> consequenceList = new LinkedList<String>();
	private LinkedList<String> timeList = new LinkedList<String>();
	private LinkedList<LinkedList<String[]>> variablesList = new LinkedList<LinkedList<String[]>>(); 
	
	private LinkedList<String> namesFinal = new LinkedList<String>();
	
	private LinkedList<String> riskProbabilityListFinal = new LinkedList<String>();
	private LinkedList<String> riskThresholdListFinal = new LinkedList<String>();
	private LinkedList<String> riskMessageListFinal = new LinkedList<String>();
	
	private LinkedList<String> faultConditionListFinal = new LinkedList<String>();
	private LinkedList<String> faultMessageListFinal = new LinkedList<String>();
	
	private LinkedList<String> timeListFinal = new LinkedList<String>();
	private LinkedList<LinkedList<String[]>> variablesListFinal = new LinkedList<LinkedList<String[]>>(); 
	
	/**
	 * Add a sensor to the data-structure of the Editor 
	 * @param nameSensor Name of the Sensor
	 * @param trigger Trigger of the Sensor
	 * @param timeNotification SpecificTimeNotification of the Sensor
	 * @param messageSensor Message of the Sensor
	 * @param variable Variables of the Sensor
	 */
	public void add(String nameSensor, String riskProbability, String riskThreshold, String riskMessage, String faultCondition, String faultMessage, String consequence, String timeNotification, LinkedList<String[]> variable) {
		
		if(!(nameSensor != null && ((riskProbability != null && riskThreshold != null) || (faultCondition != null)))) return;
		
		names.addLast(nameSensor);
		
		riskProbabilityList.addLast(riskProbability);
		riskThresholdList.addLast(riskThreshold);
		riskMessageList.addLast(riskMessage);

		faultConditionList.addLast(faultCondition);
		faultMessageList.addLast(faultMessage);
		
		consequenceList.addLast(consequence);
		
		timeList.addLast(timeNotification);
		
		variablesList.addLast(variable);
	}
	
	/**
	 * Return the list of the names of all the Sensor
	 * @return
	 */
	public LinkedList<String> getNames(){
		return names;
	}
	
	/**
	 * Return the generalTimeNotification of the Sensors
	 * @return
	 */
	public String getGeneralTime() {
		return generalTime;
	}
	
	/**
	 * Return the riskProbability of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getRiskProbability(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return riskProbabilityList.get(pos);
		else return null;
	}
	
	/**
	 * Return the riskThreshold of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getRiskThreshold(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return riskThresholdList.get(pos);
		else return null;
	}
	
	/**
	 * Return the riskMessage of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getRiskMessage(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return riskMessageList.get(pos);
		else return null;
	}
	
	/**
	 * Return the faultCondition of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getFaultCondition(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return faultConditionList.get(pos);
		else return null;
	}
	
	/**
	 * Return the faultMessage of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getFaultMessage(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return faultMessageList.get(pos);
		else return null;
	}
	
	/**
	 * Return the consequence of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getConsequence(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return consequenceList.get(pos);
		else return null;
	}
	
	/**
	 * Return the timeNotificaiton of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public String getTime(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return timeList.get(pos);
		else return null;
	}
	
	/**
	 * Return the Variables of the Sensor
	 * @param nameSensor Name of the Sensor
	 * @return null if the sensor doesn't exist
	 */
	public LinkedList<String[]> getVariable(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1)	return variablesList.get(pos);
		else return null;
	}
	
	/**
	 * Update the information about the Sensor
	 * @param nameSensor Name of the Sensor to update
	 * @param trigger Trigger of the Sensor
	 * @param timeNotification TimeNotification of the Sensor
	 * @param messageSensor Message of the Sensor
	 * @param variable Variables of the Sensor
	 */
	public void update(String nameSensor, String riskProbability, String riskThreshold, String riskMessage, String faultCondition, String faultMessage, String consequence, String timeNotification, LinkedList<String[]> variable) {
		
		if(!(nameSensor != null && ((riskProbability != null && riskThreshold != null) || (faultCondition != null)))) return;
		
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1) {
			riskProbabilityList.set(pos, riskProbability);
			riskThresholdList.set(pos, riskThreshold);
			riskMessageList.set(pos, riskMessage);
			
			faultConditionList.set(pos, faultCondition);
			faultMessageList.set(pos, faultMessage);
			
			consequenceList.set(pos, consequence);
			timeList.set(pos, timeNotification);
			variablesList.set(pos, variable);
		}
	}
	
	/**
	 * Remove from the data-structure the Sensor
	 * @param nameSensor Name of the Sensor
	 */
	public void remove(String nameSensor) {
		int pos = -1;
		for(int i=0; i<names.size(); i++) {
			if(nameSensor.equals(names.get(i))) pos=i;
		}
		if(pos!=-1) {
			names.remove(pos);
			
			riskProbabilityList.remove(pos);
			riskThresholdList.remove(pos);
			riskMessageList.remove(pos);
			
			faultConditionList.remove(pos);
			faultMessageList.remove(pos);
			
			consequenceList.remove(pos);
			timeList.remove(pos);
			variablesList.remove(pos);
		}
	}
	
	/**
	 * Check if the Sensor is present in the data-structure
	 * @param nameSensor Name of the Sensor
	 * @return
	 */
	public boolean contain(String nameSensor) {
		return names.contains(nameSensor);		
	}
	
	/**
	 * Save the modification
	 * @param generalTime generalTimeNotification
	 */
	public void conferm(String generalTime) {
		this.generalTime = generalTime;
		namesFinal = (LinkedList<String>)names.clone();
		
		riskProbabilityListFinal = (LinkedList<String>)riskProbabilityList.clone();
		riskThresholdListFinal = (LinkedList<String>)riskThresholdList.clone();
		riskMessageListFinal = (LinkedList<String>)riskMessageList.clone();
		
		faultConditionListFinal = (LinkedList<String>)faultConditionList.clone();
		faultMessageListFinal = (LinkedList<String>)faultMessageList.clone();
		
		timeListFinal = (LinkedList<String>)timeList.clone();
		variablesListFinal = new LinkedList<LinkedList<String[]>>();
		for(int i=0; i<variablesList.size(); i++) {
			LinkedList<String[]> l = new LinkedList<String[]>();
			for(int j=0; j<variablesList.get(i).size(); j++) {
				l.addLast(variablesList.get(i).get(j).clone());
			}
			variablesListFinal.addLast(l);
		}
		
	}
	
	/**
	 * Restore the status of the Sensors to the previous modification
	 */
	public void cancel() {		
		names = (LinkedList<String>)namesFinal.clone();
		
		riskProbabilityList = (LinkedList<String>)riskProbabilityListFinal.clone();
		riskThresholdList = (LinkedList<String>)riskThresholdListFinal.clone();
		riskMessageList = (LinkedList<String>)riskMessageListFinal.clone();
		
		faultConditionList = (LinkedList<String>)faultConditionListFinal.clone();
		faultMessageList = (LinkedList<String>)faultMessageListFinal.clone();
		
		timeList = (LinkedList<String>)timeListFinal.clone();
		variablesList = new LinkedList<LinkedList<String[]>>();
		for(int i=0; i<variablesListFinal.size(); i++) {
			LinkedList<String[]> l = new LinkedList<String[]>();
			for(int j=0; j<variablesListFinal.get(i).size(); j++) {
				l.addLast(variablesListFinal.get(i).get(j).clone());
			}
			variablesList.addLast(l);
		}
	}
	
}
