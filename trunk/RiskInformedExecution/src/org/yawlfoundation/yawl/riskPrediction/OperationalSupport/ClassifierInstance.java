package org.yawlfoundation.yawl.riskPrediction.OperationalSupport;

import weka.classifiers.Classifier;
import weka.core.Instance;

public class ClassifierInstance {
	
	private Classifier classifier = null;
	private Instance instance = null;
	
	public ClassifierInstance(Classifier classifier, Instance instance) {
		
		this.classifier = classifier;
		this.instance = instance;
		
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public Instance getInstance() {
		return instance;
	}

}
