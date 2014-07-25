package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LogConverter {

	public static String getXMLlog(XLog log) {
		XSerializer serializer = new XesXmlSerializer();
		
		ByteArrayOutputStream byte1=new ByteArrayOutputStream();  
		
		try {
			serializer.serialize(log, byte1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return byte1.toString();  
	}
	
}
