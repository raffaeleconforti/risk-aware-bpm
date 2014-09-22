/*
 * Copyright (c) 2004-2010 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.sensors;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Raffaele Conforti
 * Creation Date: 13/09/2010
 */

public class YSensorNotification implements Comparable<YSensorNotification>, Serializable{
	
	private final String caseID;
	private final int numberNotification;
	private final String sensorName;
	private final String sensorStatus;
	private final String sensorMessage;
	private final double sensorProbability;
	private final double sensorConsequence;
	private final double sensorProbabilityXConsequence;
	private String sensorTrigger;
	private final long sensorTimeStamp;
    
	/**
	 * 
	 * @param caseID CaseID of the Process
	 * @param nameSensor Name of the Sensor
	 * @param numberNotification IDNumber of the Notification
	 * @param message Message to Show
	 * @param trigger Trigger of the Sensor
	 * @param timeStamp TimeStamp of the notification
	 */
	public YSensorNotification(String caseID, String nameSensor, int numberNotification, String status, String message, String trigger, String threshold, double probability, double cons, long timeStamp) {
    	this.caseID = caseID;
		this.sensorName = nameSensor;
		this.numberNotification = numberNotification;
		this.sensorStatus = status;
    	this.sensorMessage = message;
    	if(this.sensorStatus != null && !this.sensorStatus.equals("fault")) {
    		this.sensorTrigger = trigger+">"+threshold;
    	}else {
    		this.sensorTrigger = trigger;
    	}
    	this.sensorProbability = probability;
    	this.sensorConsequence = cons;
    	this.sensorProbabilityXConsequence = this.sensorProbability * this.sensorConsequence;
    	this.sensorTimeStamp = timeStamp;
    }
	
	public static void main(String[] args) {
		YSensorNotification ysn = new YSensorNotification("a", "b", 1, null, null, null, null, 0.0, 0.0, 0);
		System.out.println(ysn);
	}
	
	/**
	 * Return the caseID of the Process	
	 * @return
	 */
	public String getCaseID() {
		return caseID;
	}

	/**
	 * Return the name of the Sensor 
	 * @return
	 */
	public String getSensorName() {
		return sensorName;
	}
	
	/**
	 * Return the status of the Sensor 
	 * @return
	 */
	public String getSensorStatus() {
		return sensorStatus;
	}

	/**
	 * Return the message of the Sensor
	 * @return
	 */
	public String getSensorMessage() {
		return sensorMessage;
	}

	/**
	 * Return the Trigger of the Sensor
	 * @return
	 */
	public String getSensorTrigger() {
		return sensorTrigger;
	}
	
	/**
	 * Return the Probability of the Sensor
	 * @return
	 */
	public double getSensorProbability() {
		return sensorProbability;
	}
	
	/**
	 * Return the Consequence of the Sensor
	 * @return
	 */
	public double getSensorConsequence() {
		return sensorConsequence;
	}
	
	/**
	 * Return the ProbabilityXConsequence of the Sensor
	 * @return
	 */
	public double getSensorProbabilityXConsequence() {
		return sensorProbabilityXConsequence;
	}

	/**
	 * Return the timeStamp of the Notification
	 * @return
	 */
	public String getSensorTimeStamp() {
		return (new Date(sensorTimeStamp)).toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof YSensorNotification) {
			YSensorNotification ysn = (YSensorNotification) o;
			return (sensorName.equals(ysn.sensorName) && caseID.equals(ysn.caseID) && numberNotification==ysn.numberNotification);
		}
		return false;
	}

	@Override
	public int compareTo(YSensorNotification o) {
		if(caseID.compareTo(o.caseID) != 0) return caseID.compareTo(o.caseID);
		else if(sensorName.compareTo(o.sensorName) != 0) return sensorName.compareTo(o.sensorName);
		else {
			long res = sensorTimeStamp-o.sensorTimeStamp;
			if(res<0) return -1;
			else if(res == 0) return 0;
			else return 1;
		}
	}
	
}
