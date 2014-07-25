package org.yawlfoundation.yawl.sensors.engineInterface;

import java.io.IOException;

public interface EngineInterface {
	
	final static int offered = 0;
	final static int allocated = 1;
	final static int started = 2;
	final static int completed = 3;	
	
	public EngineInterface getEngineInstance();
	
	public String getBackEndURI();
	
//	public void registerManager(YSensorManagerImplLayer ysmi);
	
	public void registerEvent(String workItemName, String caseID, int type);
	
	public String engineConnect(String user, String password) throws IOException;
	
	public boolean engineCheckConnection(String connection) throws IOException;
	
	public String mitigate(String caseID);
	
}
