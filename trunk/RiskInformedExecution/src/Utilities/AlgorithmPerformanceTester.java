package Utilities;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.PluginContextIDImpl;
import org.processmining.plugins.petrinet.mining.alphaminer.AlphaMiner;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

import weka.gui.beans.PluginManager;

public class AlgorithmPerformanceTester {
	
	@Plugin(name = "Algorithm Performance Tester",
			parameterLabels = {"Parameters"},
			returnLabels = {"int"},
			returnTypes = {Integer.class},
			userAccessible = true,
			help = "Measure Time Performance of an Algorithm")
			@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "R. Conforti", email = "raffaele.conforti@qut.edu.au")
	
	public static Object[] plugin(UIPluginContext context) throws Exception {
		int maxConcurrentThreads=Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor pool = new ThreadPoolExecutor(maxConcurrentThreads, maxConcurrentThreads, 60, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
		pool.allowCoreThreadTimeOut(true);
	
		
		
		HashMap<String, ArrayList<String>> attributeLiteralMap=new HashMap<String, ArrayList<String>>();
		
		Map<String, Class> classTypes=extractAttributeInformation(log, attributeLiteralMap);
		Map<Place, FunctionEstimator> estimators=new HashMap<Place, FunctionEstimator>();
		Map<Transition, Integer> numberOfExecutions=Collections.synchronizedMap(new HashMap<Transition, Integer>());
		Map<Transition, Map<String,Integer>> numberOfWritesPerTransition=
			Collections.synchronizedMap(new HashMap<Transition, Map<String,Integer>>());
		for(Transition trans : net.getTransitions())
		{
			numberOfExecutions.put(trans, 0);
			numberOfWritesPerTransition.put(trans, Collections.synchronizedMap(new HashMap<String, Integer>()));
		}
		
		for(Place place : net.getPlaces())
		{
			if (net.getOutEdges(place).size()<2)
				continue;
			Transition outputValues[]=new Transition[net.getOutEdges(place).size()];
			int index=0;
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc : net.getOutEdges(place))
			{
				outputValues[index++]=(Transition) arc.getTarget();
			}
			estimators.put(place, new FunctionEstimator(classTypes, outputValues, place.getLabel(), 1000));
		}
		
		Progress progress=context.getProgress();
		progress.setMaximum(log.size()+2*estimators.entrySet().size());
		progress.setValue(0);
		context.log("Processing the log traces...");
		
		for(SyncReplayResult alignment : input)
			for(Integer index : alignment.getTraceIndex())
				pool.execute(new TraceProcessor(net,log.get(index),estimators,alignment,numberOfExecutions,numberOfWritesPerTransition,progress));
		
		pool.shutdown();
		
		PetriNetWithDataFactory factory=new PetriNetWithDataFactory(net, net.getLabel());
		PetriNetWithData retValue=factory.getRetValue();
		for(Entry<String, Class> entry : classTypes.entrySet())
		{
			retValue.addVariable(entry.getKey(), entry.getValue(), null, null);
		}
		
		pool.awaitTermination(1, TimeUnit.DAYS);
		
		ProMPropertiesPanel panel=new ProMPropertiesPanel("");
		
		NiceIntegerSlider instances4Leaf=SlickerFactory.instance().createNiceIntegerSlider("(In Permil)", 1, 100, 10, Orientation.HORIZONTAL);
		panel.addProperty("",SlickerFactory.instance().createLabel("Minimal Percentage of Instances per leaf"));
		panel.addProperty("", instances4Leaf);
		NiceIntegerSlider percentageOfWrite=SlickerFactory.instance().createNiceIntegerSlider("(In Percent)", 50, 100, 66, Orientation.HORIZONTAL);
		panel.addProperty("",SlickerFactory.instance().createLabel("Minimal Number of Occurrence of Write Operation to be mined")); 
		panel.addProperty("", percentageOfWrite);		
		JCheckBox pruneBox=SlickerFactory.instance().createCheckBox("Prune tree", true);
		panel.addProperty("", pruneBox);
		InteractionResult result = context.showConfiguration("Configuration", panel);
		if (result == InteractionResult.CANCEL) {
			context.getFutureResult(0).cancel(true);
			return null;
		}		
		
		for(Entry<Place, FunctionEstimator> entry2 : estimators.entrySet())
		{
			context.log("Generating the condition for the decision point at place "+entry2.getKey().getLabel());	
			FunctionEstimator f=entry2.getValue();
			Map<Object,Expression> estimationTransitionExpression=f.getEstimation(instances4Leaf.getValue(),pruneBox.isSelected());
			for(Entry<Object, Expression> entry : estimationTransitionExpression.entrySet())
			{
				Transition transitionInPNWithoutData=(Transition) entry.getKey();
				Transition transitionInPNWithData = factory.getTransMapping().get(transitionInPNWithoutData);
				retValue.setGuard(transitionInPNWithData, entry.getValue());
			}
			progress.inc();
			progress.inc();
		}
		
		for(Transition transitionInPNWithoutData : net.getTransitions())
		{
			PNWDTransition transitionInPNWithData = (PNWDTransition) factory.getTransMapping().get(transitionInPNWithoutData);
			if (!transitionInPNWithData.isInvisible())
			{
				//Set the read operations
				if (transitionInPNWithData.getGuard()!=null)
				{
					Enumeration enumer=transitionInPNWithData.getGuard().findVariables();
					while(enumer.hasMoreElements())
					{
						String varName=(String) enumer.nextElement();
						retValue.assignReadOperation(transitionInPNWithData, retValue.getVariable(varName));
					}
				}


				//Set the write operations
				int numberOfExecution=numberOfExecutions.get(transitionInPNWithoutData);
				for(Entry<String,Integer> entry3 : numberOfWritesPerTransition.get(transitionInPNWithoutData).entrySet())
				{
					if (entry3.getValue() > (numberOfExecution * percentageOfWrite.getValue())/100)
					{
						DataElement dataElem=retValue.getVariable(entry3.getKey());
						if (dataElem!=null)
							retValue.assignWriteOperation(transitionInPNWithData, dataElem);
					}
				}
			}

			

		}
		
		Marking[] markings=factory.cloneInitialAndFinalConnection(context);
			
		return new Object[] { retValue, markings[0], markings[1]};	

	}
			


	private static Map<String, Class> extractAttributeInformation(XLog log, HashMap<String, ArrayList<String>> literalMap) {
		HashMap<String, Class> retValue=new HashMap<String, Class>();
		for (XTrace trace : log)
		{
			for (XEvent event : trace)
			{
				for(XAttribute attr : event.getAttributes().values())
				{
					Class classType=generateDataElement(attr,literalMap);
					if (classType!=null)
						retValue.put(attr.getKey(), classType);
				}
					
			}
		}
		return retValue;
	}
	
	private static Class generateDataElement(XAttribute xAttrib, Map<String, ArrayList<String>> attributeLiteralMap) {
		String name=xAttrib.getKey();
		if (name.contains(":"))
			return null;
		if (xAttrib instanceof XAttributeBoolean)
		{
			return Boolean.class;
		}
		else if (xAttrib instanceof XAttributeContinuous)
		{
			return Double.class;
		}
		else if (xAttrib instanceof XAttributeDiscrete)
		{
			return Long.class;
		}
		else if (xAttrib instanceof XAttributeTimestamp)
		{
			return java.util.Date.class;
		}
		else if (xAttrib instanceof XAttributeLiteral)
		{
			return String.class;
		}
		return null;	
	}

}
