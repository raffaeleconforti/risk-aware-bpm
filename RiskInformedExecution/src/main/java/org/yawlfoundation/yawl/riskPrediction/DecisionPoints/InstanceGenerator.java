package org.yawlfoundation.yawl.riskPrediction.DecisionPoints;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceGenerator {

	public static Instance generateInstance(Map<String,Object> variableAssignment, Object outputValue, Object[] outputValuesAsObjects, Instances instances) {
		
		Instance instance = new DenseInstance(instances.numAttributes());
		instance.setDataset(instances);
		
		if(variableAssignment != null) {
			for(Entry<String, Object> entry : variableAssignment.entrySet()) {
				
				Attribute attr = instances.attribute(entry.getKey());
				Object value = entry.getValue();
				
				if(attr != null) {
					if (value instanceof Double) {
		
						Double d = ((Double)value).doubleValue();
						if(d != null) {
	//						if(instance.classAttribute().isNominal()) {
	//
	//							String newValue = d.toString();
	//							instance.setValue(attr, newValue);
	//							
	//						}else {
								instance.setValue(attr, d);
	//						}
						}else {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof Integer) {
		
						Integer i = ((Integer)value).intValue();
						if(i != null) {
	//						if(instance.classAttribute().isNominal()) {
	//
	//							String newValue = i.toString();
	//							instance.setValue(attr, newValue);
	//							
	//						}else {
								instance.setValue(attr, i);
	//						}
						}else {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof Long) {
		
						Long l = ((Long)value).longValue();
						if(l != null) {
	//						if(instance.classAttribute().isNominal()) {
	//
	//							String newValue = l.toString();
	//							instance.setValue(attr, newValue);
	//							
	//						}else {
								instance.setValue(attr, l);
	//						}
						}else {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof Date) {
					
						Long l = ((Date) value).getTime();
						if(l != null) {
							instance.setValue(attr, l);
						}else {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof Boolean) {
						
						if (((Boolean)value).booleanValue()) {
							
							instance.setValue(attr,"T");
						
						}else {
						
							instance.setValue(attr,"F");
						
						}
					
					}else if (value instanceof Resource) {
					
						try {
							String newValue = ((Resource)value).getName();
							instance.setValue(attr,newValue);
						}catch (IllegalArgumentException iae) {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof String) {
					
		//				instance.setValue(attr, NumericValueConversion.convertToFloat((String) value));
						try {
							String newValue = ((String) value);
							instance.setValue(attr, newValue);
							
						}catch (IllegalArgumentException iae) {
							instance.setMissing(attr);
						}
					
					}	
					
					if(!instances.checkInstance(instance)) {
						System.out.println(attr);
						System.out.println(value);
						System.out.println(value.getClass());
						
						throw new RuntimeException();
					}
				}
				
			}
			
			if(outputValue != null) {// && getPositionValue(outputValue, outputValuesAsObjects) != null) {
			
				instance.setValue(instances.classAttribute(), getPositionValue(outputValue, outputValuesAsObjects));
			
			}
		}
		
		return instance;
		
	}
	
	private static String getPositionValue(Object outputValue, Object[] array) {
		
//		int pos = Arrays.binarySearch(array, outputValue);
//		
//		if(pos > -1) {
//			return String.valueOf(pos);
//		}
		
		for(int i = 0; i<array.length; i++) {
			
			if (array[i].equals(outputValue)) {
				
				return String.valueOf(i);
				
			}
			
		}
				
		return null;
		
	}
	
	public static Instance updateInstance(Map<String,Object> variableAssignment, Instance instance) {
		
		Instances instances = instance.dataset();
		Instance newInstance = (Instance) instance.copy();

		for(Entry<String, Object> entry : variableAssignment.entrySet()) {
			
//			System.out.println("entry "+entry);
			
			Attribute attr = instances.attribute(entry.getKey());
			Object value = entry.getValue();
			
//			System.out.println("attr "+attr);
//			System.out.println("value "+value+" "+value.getClass());
						
			if(attr != null) {
				if (value instanceof Double) {
	
					Double d = ((Double)value).doubleValue();
					if(d != null) {
						newInstance.setValue(attr, d);
					}else {
						newInstance.setMissing(attr);
					}
				
				}else if (value instanceof Long) {
	
					Long l = ((Long)value).longValue();
					if(l != null) {
						newInstance.setValue(attr, l);
					}else {
						newInstance.setMissing(attr);
					}
				
				}else if (value instanceof Date) {
				
					Long l = ((Date) value).getTime();
					if(l != null) {
						newInstance.setValue(attr, l);
					}else {
						newInstance.setMissing(attr);
					}
				
				}else if (value instanceof Boolean) {
					
					if (((Boolean)value).booleanValue()) {
						
						newInstance.setValue(attr,"T");
					
					}else {
					
						newInstance.setValue(attr,"F");
					
					}
				
				}else if (value instanceof Resource) {
				
					try {
						String newValue = ((Resource)value).getName();
						newInstance.setValue(attr,newValue);
					}catch (IllegalArgumentException iae) {
						newInstance.setMissing(attr);
					}
				
				}else if (value instanceof String) {
				
	//				instance.setValue(attr, NumericValueConversion.convertToFloat((String) value));
					try {
						String newValue = ((String) value);
						newInstance.setValue(attr, newValue);
						
					}catch (IllegalArgumentException iae) {
						newInstance.setMissing(attr);
					}
				
				}	
				
				if(!instances.checkInstance(newInstance)) {
					System.out.println(attr);
					System.out.println(value);
					
					throw new RuntimeException();
				}
			}
			
		}
		
		return newInstance;
		
	}
	
}
