package org.yawlfoundation.yawl.riskPrediction.PredictionFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.jdom2.Element;
import org.yawlfoundation.yawl.risk.state.YAWL.Resource;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TaskExecutionAnnotator;
import org.yawlfoundation.yawl.riskPrediction.Annotators.TimeBetweenTasksAnnotator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.InstanceGenerator;
import org.yawlfoundation.yawl.riskPrediction.DecisionPoints.YAWL_DecisionPointDiscover;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;
import org.yawlfoundation.yawl.util.JDOMUtil;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class DecisionPredictor {

	private final Object[] outputValuesAsObjects;
	private final Instances instances;
	private static final DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private final ArrayList<String> booleanValues = new ArrayList<String>();
	{
		booleanValues.add("T");
		booleanValues.add("F");
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
				
		try {
			XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/stormfire/Documents/Useless/log.xes");

			String specification = null;
			try {
				File f = new File("/home/stormfire/Dropbox/workspace/Simulated Annealing/Payment.yawl");
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				specification = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			String resources = null;
			try {
				File f = new File("/home/stormfire/orderfulfillment.ybkp");
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				resources = writer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);

			InterfaceManager im = new InterfaceManager(InterfaceManager.PROM, parameters);

			YAWL_DecisionPointDiscover a = new YAWL_DecisionPointDiscover();
			a.discoverDecisionPoints(specification);
			
			TaskExecutionAnnotator tea = new TaskExecutionAnnotator(im);
			TimeBetweenTasksAnnotator tbta = new TimeBetweenTasksAnnotator(im, a.getDecisionPoints());

			AttributeGenerator ag = new AttributeGenerator();
			ag.generateAttribute(im, a.getDecisionPoints().get(3), tea, tbta, null, null, null, null, 0);
			
			LinkedList<Long> ob = new LinkedList<Long>();
			
//			Random r = new Random();
//			
//			for(String id : ag.getAttributes().keySet()) {
//				
//				Long x = (Long) ag.getAttributes().get(id).get("NumberExecutions_Update_Shipment_Payment_Order_604");
//				if(x > 0) {
//					ob.add(10);
//				}else {
//					ob.add(r.nextInt(2));
//				}
////				ob.add((Integer) r.nextInt(10));
//				
//			}
			
			HashSet<Long> obSet = new HashSet<Long>(ob);

			if(obSet.size() == 1) obSet.add(new Long(-1));
			
			Object[] obArr = obSet.toArray(new Object[0]);
			
			ArrayList<String> resourceValues = new ArrayList<String>();
			resourceValues.add("no-resource");
			resourceValues.addAll(im.getRoleLayer().getAllID());
			           
			DecisionPredictor dp = new DecisionPredictor(resourceValues, ag.getAttributesClasses(), ag.getStringNominal(), obArr, "Test", 1000);
			
			int i = 0;
			for(Entry<String, HashMap<String, Object>> entry : ag.getAttributes().entrySet()) {

				dp.addInstances(entry.getValue(), ob.get(i));
				i++;
			
			}
			
			System.out.println(dp.getEstimator("-C 0.25, -U, -M 2, 1000", "weka.classifiers.trees.J48"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public DecisionPredictor(ArrayList<String> resourceValues, Map<String,Class> variableType, Map<String, ArrayList<String>> value, Object[] outputValuesAsObjects, String name, int capacity) {
				
		this.outputValuesAsObjects = outputValuesAsObjects;
		
		ArrayList<Attribute> attributeList = new ArrayList<Attribute>(variableType.size()+1);
		
		for(Entry<String, Class> entry : variableType.entrySet()) {
						
			if (entry.getValue() == Boolean.class)
			
				attributeList.add(new Attribute(entry.getKey(), booleanValues));
			
			else if (entry.getValue() == Resource.class)
				
				attributeList.add(new Attribute(entry.getKey(), resourceValues));
			
			else if (entry.getValue() == Double.class) {
				
				attributeList.add(new Attribute(entry.getKey()));
//				attributeList.add(new Attribute(entry.getKey(), value.get(entry.getKey())));
			
			}else if (entry.getValue() == Long.class){
				
				attributeList.add(new Attribute(entry.getKey()));
//				attributeList.add(new Attribute(entry.getKey(), value.get(entry.getKey())));
			
			}else if (entry.getValue() == Integer.class){
				
				attributeList.add(new Attribute(entry.getKey()));
//				attributeList.add(new Attribute(entry.getKey(), value.get(entry.getKey())));
			
			}else if (entry.getValue() == Date.class)
			
				attributeList.add(new Attribute(entry.getKey()));//, "yyyy-MM-dd'T'HH:mm:ss"));
			
			else if (entry.getValue() == String.class)
			
				attributeList.add(new Attribute(entry.getKey(), value.get(entry.getKey())));
						
		}
		
		Attribute classAttribute = new Attribute("CLASS", createArray(outputValuesAsObjects.length));
		attributeList.add(classAttribute);
		instances = new Instances(name, attributeList, capacity);
		instances.setClass(classAttribute);
	}

	private ArrayList<String> createArray(int length) {
		
		ArrayList<String> retValue=new ArrayList<String>();
		
		for(int i=0;i<length;i++) {
			
			retValue.add(String.valueOf(i));
		
		}
		
		return retValue;
		
	}

	public void addInstances(Map<String,Object> variableAssignment, Object outputValue) {
		
		Instance instance = InstanceGenerator.generateInstance(variableAssignment, outputValue, outputValuesAsObjects, instances); 
		
		instances.add(instance);
				
	}
	
	public static void addData(Instance instance, String data) {
		System.out.println(data);
		System.out.println(instance);
		String prefix = "VariableOutput_";
		Element dataEl = JDOMUtil.stringToElement(data);
		Element workItemEl = (Element) dataEl.getChildren().get(0);
		for(Element item : (List<Element>) workItemEl.getChildren()) {
			System.out.println(item.getName());
			convertComplexTypes(prefix, item.getName(), item.getValue(), 1, instance);
		}
		System.out.println(instance);
	}
	
	private static void convertComplexTypes(String prefix, String variableName, String v, int counter, Instance instance) {
		
		Instances instances = instance.dataset();
		
		if('<' == v.charAt(0)) {
			
			Element e = JDOMUtil.stringToElement(v);
			
			if(e.getChildren().size() == 0) {

				Attribute attr = instances.attribute(prefix+e.getName()+counter);
				Object value = e.getValue();
				
				if(attr != null) {
					if (value instanceof Double) {
		
						Double d = ((Double)value).doubleValue();
						if(d != null) {
							instance.setValue(attr, d);
						}else {
							instance.setMissing(attr);
						}
					
					}else if (value instanceof Long) {
		
						Long l = ((Long)value).longValue();
						if(l != null) {
							instance.setValue(attr, l);
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
						
						throw new RuntimeException();
					}
				}
			}
			
			int pos = 1;
			for(Element e1 : (List<Element>) e.getChildren()) {
				
				convertComplexTypes(prefix, e1.getName(), JDOMUtil.elementToString(e1), pos, instance);
				pos++;
				
			}
			
		}else {
						
			System.out.println(prefix+variableName);
			Attribute attr = instances.attribute(prefix+variableName);
			System.out.println(attr);
			Class c = getClass(v);
			Object value = null;
			if(c == Integer.class) {
				value = Integer.parseInt(v);
			}else if(c == Double.class) {
				value = Double.parseDouble(v);
			}else if(c == Long.class) {
				value = Long.parseLong(v);
			}else if(c == Boolean.class) {
				value = Boolean.parseBoolean(v);
			}else {
				value = v;
			}
			
			if(attr != null) {
				if (value instanceof Double) {
	
					Double d = ((Double)value).doubleValue();
					if(d != null) {
						instance.setValue(attr, d);
					}else {
						instance.setMissing(attr);
					}
				
				}else if (value instanceof Long) {
	
					Long l = ((Long)value).longValue();
					if(l != null) {
						instance.setValue(attr, l);
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
					
					throw new RuntimeException();
				}
			}
			
		}
		
	}
	
	private static Class getClass(String value) {
		if(value.equals("null")) return Integer.class;
		try {
			Long.valueOf(value);
			return Long.class;
		}catch (NumberFormatException nfe1) {
			try {
				Double.valueOf(value);
				return Double.class;
			}catch(NumberFormatException nfe) {
				if(value.length() != 28) {
					if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
						return Boolean.class;
					}else {
						return String.class;
					}
				}else {
					try {
						logDateFormat.parse(value);
						return Date.class;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
							return Boolean.class;
						}else {
							return String.class;
						}
					}
				}
			}
		}
	}
	
	
	public static void fixMissingValues(Instance instance) {
		if(instance.hasMissingValue()) {
			for(int i = 0; i<instance.numAttributes(); i++) {
				if(instance.attribute(i).name().startsWith("Resource") && instance.isMissing(i)) {
					instance.setValue(i, "no-resource");
				}else if(instance.attribute(i).name().startsWith("Event") && instance.isMissing(i)) {
					instance.setValue(i, "F");
				}
			}
		}
	}
	
	public Classifier getEstimator(String options, String ClassPathAndName) {
		
		for(Instance instance : instances) {
			fixMissingValues(instance);
		}
		
		try {

			Classifier classifier = (Classifier) Utils.forName(Class.forName(ClassPathAndName), ClassPathAndName, weka.core.Utils.splitOptions(options)); // Class.forName(ClassPathAndName).newInstance();

            try {
                classifier.buildClassifier(this.instances);
                System.out.println("USO JRipps au");
            }catch (weka.core.WekaException ex) {
                System.out.println("USO J48");
                classifier = (Classifier) Utils.forName(Class.forName("weka.classifiers.trees.J48"), "weka.classifiers.trees.J48", weka.core.Utils.splitOptions("-C 0.25 -B -M 2"));
                classifier.buildClassifier(this.instances);
            }

			return classifier;
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void saveEstimator(Classifier c, String path) throws Exception {
		
		weka.core.SerializationHelper.write(path, c);
		
	}
	
	public Classifier loadEstimator(String path) throws Exception {
		
		return (Classifier) weka.core.SerializationHelper.read(path);
		
	}

	public Instances getInstances() {

		return instances;
		
	}
	
}
