package org.yawlfoundation.yawl.sensors;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.transitionsystem.converter.util.TSConversions;
import org.processmining.plugins.transitionsystem.miner.TSMiner;
import org.processmining.plugins.transitionsystem.miner.TSMinerInput;
import org.processmining.plugins.transitionsystem.miner.TSMinerOutput;
import org.processmining.plugins.transitionsystem.miner.TSMinerPayload;
import org.processmining.plugins.transitionsystem.miner.TSMinerPlugin;
import org.processmining.plugins.transitionsystem.miner.modir.TSMinerModirInput;
import org.processmining.plugins.transitionsystem.miner.util.TSAbstractions;
import org.processmining.plugins.transitionsystem.miner.util.TSDirections;
import org.processmining.plugins.tsanalyzer.TSAnalyzer;
import org.processmining.plugins.tsanalyzer.TimeTransitionSystemAnnotation;
import org.yawlfoundation.yawl.sensors.actions.prom.FakePluginContext;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


public class TimePredictor {
	
	private TimeTransitionSystemAnnotation annotation = null;
	private EventPayloadTransitionSystem ts = null;
	private State origin = null;
	
	public static void main(String[] args) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(),"/home/stormfire/repairExample.xes"); //"/media/Data/SharedFolder/Demo/LogOldGood.xes");
		TimePredictor t = new TimePredictor(log);
		System.out.println(t.annotation);
		System.out.println(t.origin);
		System.out.println(t.ts);
	}
	
	public TimePredictor(XLog log) {
		FakePluginContext context = new FakePluginContext();
				
		XEventClassifier transitionClassifier;
		XEventClassifier[] classifiers = new XEventClassifier[1];
		classifiers[0] = new XEventNameClassifier();
		transitionClassifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
		
		//Set Classiers
		TSMinerInput settings = new TSMinerInput(context, log, Arrays.asList(classifiers), transitionClassifier);
		settings.getModirSettings(TSDirections.BACKWARD, classifiers[0]).setUse(true);
		settings.getModirSettings(TSDirections.FORWARD, classifiers[0]).setUse(false);
		settings.setUseAttributes(false);
		
		//
		TSMinerModirInput modirSettings;
		TSAbstractions abstraction = TSAbstractions.SET;
		int horizon = -1;
		
		/**
		 * Set abstraction and (visible) horizon for all selected
		 * combinations of modes (model element, originator, event type) and
		 * directions (backward, forward).
		 */
		for (TSDirections direction : TSDirections.values()) {
			for (XEventClassifier classifier : settings.getClassifiers()) {
				modirSettings = settings.getModirSettings(direction, classifier);
				if (modirSettings.getUse()) {
					modirSettings.setAbstraction(abstraction);
					modirSettings.setFilteredHorizon(horizon);
					modirSettings.setHorizon(-1);
				}
			}
		}
		
		XLogInfo info = settings.getLogInfo();
		TreeSet<String> s = new TreeSet<String>();
		for (XEventClass eventClass : info.getEventClasses(classifiers[0]).getClasses()) {
			s.add(eventClass.toString());
		}
		
		for (TSDirections direction : TSDirections.values()) {
			if (settings.getModirSettings(direction, classifiers[0]).getUse()) {
				settings.getModirSettings(direction, classifiers[0]).setUse(true);
			}
			settings.getModirSettings(direction, classifiers[0]).getFilter().clear();
			for (String object : s) {
				if (settings.getModirSettings(direction, classifiers[0]).getUse()) {
					settings.getModirSettings(direction, classifiers[0]).getFilter().add(object);
				}
			}
		}
		
		settings.getVisibleFilter().clear();
		
		for (String object : s) {
			settings.getVisibleFilter().add(object);
		}
		
		settings.getConverterSettings().setUse(TSConversions.KILLSELFLOOPS, true);
		settings.getConverterSettings().setUse(TSConversions.EXTEND, true);

		TSMiner miner = new TSMiner(context);
		TSMinerOutput output = miner.mine(settings);
		TSMinerPlugin.setLabels(context, log);
		output.getTransitionSystem().getAttributeMap().put(AttributeMap.LABEL, context.getFutureResult(0).getLabel());
		
		ts = output.getTransitionSystem();
		
		context.getFutureResult(0).setLabel("Time Annotated " + ts.getLabel() + " from log " + XConceptExtension.instance().extractName(log));
		TSAnalyzer analyzer = new TSAnalyzer(context, ts, log);
		
		annotation = analyzer.annotate();
		
//		System.out.println("a "+annotation);
		
		TSMinerPayload start = new TSMinerPayload(settings);
		origin = null;
		
		for(State node : ts.getNodes()) {
			if(start.equals(node.getIdentifier())) {
				origin = node;
			}
			break;
		}
	}
	
	private State navigate(LinkedList<String> activities) {
		Iterator<String> it = activities.iterator();
		
		State curr = origin;
		String activity = null;
		
		LinkedList<String> reached = new LinkedList<String>();
		
		while(it.hasNext()) {
			activity = it.next();
			for(Transition edge : ts.getOutEdges(curr)) {
				if(edge.getTarget().getIdentifier().toString().contains(activity)) {
					boolean ok = true;
					for(String pre : reached) {
						if(!edge.getTarget().getIdentifier().toString().contains(pre)) {
							ok = false;
						}
						
					}
					if(ok) {
						reached.add(activity);
						curr = edge.getTarget();
//						System.out.println(curr+" "+annotation.getStateAnnotation(curr).getSoujourn().getAverage());
						break;
					}
				}
			}
		}
		
		return curr;
		
	}
	
	private String discover(String name) {
		Double avg = null;
		int total = 0;
		for(Transition edge : ts.getEdges()) {
			if(edge.getTarget().getIdentifier().toString().contains(name)) {
				if(avg == null) {
					avg = annotation.getStateAnnotation(edge.getTarget()).getSoujourn().getAverage();
				}else {
					avg += annotation.getStateAnnotation(edge.getTarget()).getSoujourn().getAverage();
				}
				total++;
			}
		}
		
		return Long.toString((long) (avg/total));
	}
	
	public String getTaskDuration(LinkedList<String> activities, String name) {
		if(activities.size() == 0) {
			return discover(name);
		}else {
			return Long.toString((long)annotation.getStateAnnotation(navigate(activities)).getSoujourn().getAverage());
		}
	}
	
	public String getNetDuration(LinkedList<String> activities) {
		return Long.toString((long)annotation.getStateAnnotation(navigate(activities)).getRemaining().getAverage());
	}
	
	@Override
	public String toString() {
		return annotation+" "+ts+" "+origin;
	}

}
