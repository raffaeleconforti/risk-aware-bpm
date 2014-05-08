package org.yawlfoundation.yawl.sensors.language.functions;

import java.io.IOException;

import org.yawlfoundation.yawl.resourcing.rsInterface.WorkQueueGatewayClient;

public class WorkQueueFacadeGataway extends WorkQueueFacade{

	private WorkQueueGatewayClient _workQueueClient;
	private String _resourceHandle;
	private String _user;
	private String _password;
	
	public WorkQueueFacadeGataway(WorkQueueGatewayClient workQueueClient, String user, String password) {
		super();
		_workQueueClient = workQueueClient;
		_user = user;
		_password = password;
	}
	
	@Override
	public String getQueuedWorkItems(String id, int queueType) {
		try {
			return _workQueueClient.getQueuedWorkItems(id, queueType, getResourceHandle());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    /**
     * Return a section handle with the resource
     * @return
     */
    
    private String getResourceHandle() {
        try {
        	while (_workQueueClient.checkConnection(_resourceHandle).equals("false")) {
				_resourceHandle = _workQueueClient.connect(_user,_password);
			}
		} catch (IOException e) {
			_resourceHandle = "<failure>Problem connecting to engine.</failure>";
		}
        return _resourceHandle;
    }

}
