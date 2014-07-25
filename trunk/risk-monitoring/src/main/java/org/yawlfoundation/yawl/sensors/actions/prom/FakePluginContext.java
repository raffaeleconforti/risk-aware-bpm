package org.yawlfoundation.yawl.sensors.actions.prom;

import java.util.LinkedList;

import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.Progress;

public class FakePluginContext extends UIPluginContext {

	Progress me = null; 
	private static LinkedList<ProMFuture<?>> futures = new LinkedList<ProMFuture<?>>();
	
	private static UIContext MAIN_CONTEXT = new UIContext();
	private static UIPluginContext MAIN_PLUGINCONTEXT = MAIN_CONTEXT
			.getMainPluginContext().createChildContext("");

	public FakePluginContext() {
		this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
	}

	public FakePluginContext(UIPluginContext context, String label) {
		super(context, label);
	}

	public FakePluginContext(PluginContext context) {
		this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
		for (ConnectionID cid : context.getConnectionManager()
				.getConnectionIDs()) {
			try {
				Connection connection = context.getConnectionManager()
						.getConnection(cid);
				this.addConnection(connection);
			} catch (ConnectionCannotBeObtained e) {
			}
		}
	}

	@Override
	public Progress getProgress() {
		if(me == null) {
			me = new FakeProgress(); 
		}
		return me;
	}

	@Override
	public ProMFuture<?> getFutureResult(int i) {
		ProMFuture<?> future = null;
		if(futures.size() <= i) {
			future = new ProMFuture<Object>(String.class, "Fake Future") {
				@Override
				protected Object doInBackground() throws Exception {
					return new Object();
				}
			};
			futures.add(i, future);
			return future;
		}else {
			return futures.get(i); 
		}
	}

	@Override
	public void setFuture(PluginExecutionResult futureToBe) {

	}
}
