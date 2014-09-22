package Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.Map.Entry;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;
import org.yawlfoundation.yawl.util.JDOMUtil;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 14/06/2013
 *
 */

public class fixer {

	public static void main(String[] args) throws Exception {
//		keepLastAuthPayGenPayClose("/media/Data/SharedFolder/Commercial/FilteredCommercial5.xes", "FilteredCommercial6.xes");
//		removeSameEventSameTime("/media/Data/SharedFolder/Commercial/FilteredCommercial6.xes", "FilteredCommercial7.xes");
//		removeSameEvent("/media/Data/SharedFolder/Commercial/FilteredCommercial7.xes", "FilteredCommercial8.xes");
//		cleanData("/media/Data/SharedFolder/Commercial/FilteredCommercial8.xes", "FilteredCommercial9.xes");
//		utiliseIncoming("/media/Data/SharedFolder/Commercial/FilteredCommercial9.xes", "FilteredCommercial10.xes");
//		utiliseConductFile("/media/Data/SharedFolder/Commercial/FilteredCommercial10.xes", "FilteredCommercial11.xes");
//		removeIncAmt("/media/Data/SharedFolder/Commercial/FilteredCommercial11.xes", "FilteredCommercial12.xes");
//		removeCreated("/media/Data/SharedFolder/Commercial/FilteredCommercial13.xes", "FilteredCommercial14.xes");
//		treeGenerator("/media/Data/SharedFolder/Commercial/FilteredCommercial14.xes");
//		discoverResourceUse("/media/Data/SharedFolder/Commercial/FilteredCommercial14.xes");
		
//		removeSameEventSameTimeDiffLife("/media/Data/SharedFolder/Commercial/IPIStartEndOpenCloseReady.xes", "IPINoSameTimeDiffLife.xes");
//		removeSameEventSameTime("/media/Data/SharedFolder/Commercial/IPINoSameTimeDiffLife.xes", "IPINoSameTime.xes");
//		removeSameEvent("/media/Data/SharedFolder/Commercial/IPINoSameTime.xes", "IPINoSameEvent.xes");
////		keepLastAuthPayGenPayClose("/media/Data/SharedFolder/Commercial/IPINoSameEvent.xes", "IPISingleGenAuthClose.xes");
//		removeSameEventSameTime("/media/Data/SharedFolder/Commercial/IPINoSameEvent.xes", "IPINoSameTime2.xes");
////		removeSameEventSameTime("/media/Data/SharedFolder/Commercial/IPISingleGenAuthClose.xes", "IPINoSameTime2.xes");
//		removeSameEvent("/media/Data/SharedFolder/Commercial/IPINoSameTime2.xes", "IPINoSameEvent2.xes");
//		discoverDuration("/media/Data/SharedFolder/Commercial/IPINoSameEvent2.xes");
		
//		treeGenerator("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		discoverTeamName("/media/Data/SharedFolder/Commercial/FilteredCommercial14.xes");
		
//		generateResources2();

        count();
//		mergeFiles();
//		uniteFiles();
//		averageDur();
//		averageAct();
		
//		discoverFrequences("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		discoverFrequences2("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		discoverFrequences3("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		discoverFrequencesResources("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		discoverFrequences("/media/stormfire/2795-0BA2/Log/testLog.xes");
//		discoverFrequences2("/media/stormfire/2795-0BA2/Log/testLog.xes");
		
//		calculateUtilization("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		calculateUtilization("/media/stormfire/2795-0BA2/Exp0/testLog.xes");
//		calculateUtilization("/media/stormfire/2795-0BA2/Exp025/testLog.xes");
//		calculateUtilization("/media/stormfire/2795-0BA2/Exp05/testLog.xes");
//		calculateUtilization("/media/stormfire/2795-0BA2/Exp075/testLog.xes");
//		calculateUtilization("/media/stormfire/2795-0BA2/ExpRandom/testLog.xes");
		
//		convertGuard();
//		generateResources();
		
//		activeCaseDiscovery("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
//		dataGenerator("/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes");
		
		
	}

    private static void count() throws Exception {

        XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), "/home/user/DSS/FilteredCommercial15.xes");

        HashMap<Long, ArrayList<String>> startTime = new HashMap<Long, ArrayList<String>>();
        HashMap<Long, ArrayList<String>> endTime = new HashMap<Long, ArrayList<String>>();

        XConceptExtension xce = XConceptExtension.instance();
        XTimeExtension xte = XTimeExtension.instance();

        for(XTrace trace : log) {
            String name = xce.extractName(trace);
            Long start = xte.extractTimestamp(trace.get(0)).getTime();
            Long end = xte.extractTimestamp(trace.get(trace.size()-1)).getTime();

            ArrayList<String> list = null;
            if((list = startTime.get(start)) == null) {
                list = new ArrayList<String>();
            }
            list.add(name);

            startTime.put(start, list);

            list = null;
            if((list = endTime.get(start)) == null) {
                list = new ArrayList<String>();
            }
            list.add(name);

            endTime.put(end, list);
        }

        Long[] start = startTime.keySet().toArray(new Long[0]);
        Long[] end = endTime.keySet().toArray(new Long[0]);

        Couple[] couples = new Couple[start.length+end.length];
        int i = 0;
        for(Long l : start) {
            couples[i] = new Couple(l, true);
            i++;
        }

        for(Long l : end) {
            couples [i] = new Couple(l, false);
            i++;
        }

        Arrays.sort(couples);

        int open = 0;
        int total = 0;
        ArrayList<Integer> list = new ArrayList<Integer>();

        int count = 0;
        for(int j = 0; j < couples.length; j++) {
            long l = couples[j].l;

            if(startTime.get(l) != null) {
                open += startTime.get(l).size();
            }else {
                open -= endTime.get(l).size();
            }

            total += open;

            list.add(open);
            count++;

            System.out.println(open);
        }
        System.out.println("Count "+count);

        int[] array = new int[list.size()];
        i = 0;
        for(int r : list) {
            array[i] = r;
            i++;
        }

        Arrays.sort(array);
        System.out.println(Arrays.toString(array));

        if(array.length % 2 == 0) {
            int a = array.length / 2;
            int b = array[a];
            int c = array[a + 1];
            System.out.println("Median " + (b+c)/2);
        }else {
            System.out.println("Median " + (array[array.length / 2]));
        }

        System.out.println("Final "+total/couples.length);

    }

    static class Couple implements Comparable<Couple> {
        Long l;
        boolean start;

        public Couple(Long l, boolean start) {
            this.l = l;
            this.start = start;
        }

        @Override
        public int compareTo(Couple o) {
            return this.l.compareTo(o.l);
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Couple) {
                return l.equals(((Couple) o).l);
            }
            return false;
        }
    }
	
	private static void averageAct() throws Exception {
		activities( 
				new String[]{
//				"/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes",
//				"/media/Data/SharedFolder/Commercial/Log/testLog.xes",		
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Log/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/new0.25/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75-30/testLog.xes",
////				"/media/Data/SharedFolder/Commercial/Exp/Exp1/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1(2)/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1-30/testLog.xes"
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/new05-33/testLog.xes"
				"/media/Data/SharedFolder/Commercial/Exp/new075-33/testLog.xes"
				},
				
				new String[]{
//				"Suncorp",
//				"Sim",		
//				"Zero",
//				"Zero-Trenta",
//				"ZeroVentiCinque", 
//				"ZeroVentiCinque-Trenta",
				"ZeroCinque"
//				"ZeroCinque-Trenta",
//				"ZeroSettantaCinque", 
//				"ZeroSettantaCinque-Trenta",
//				"Uno", 
//				"UnoDue", 
//				"Uno-Trenta"
				});
	}
	
	private static void averageDur() throws Exception {
		dur( 
				new String[]{
//				"/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes",
//				"/media/Data/SharedFolder/Commercial/Log/testLog.xes",		
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Log/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/new0.25/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75-30/testLog.xes",
////				"/media/Data/SharedFolder/Commercial/Exp/Exp1/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1(2)/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1-30/testLog.xes"
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1-33/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/new05-33/testLog.xes"
				"/media/Data/SharedFolder/Commercial/Exp/new075-33/testLog.xes"
				},
				
				new String[]{
//				"Suncorp",
//				"Sim",		
//				"Zero",
//				"Zero-Trenta",
//				"ZeroVentiCinque", 
//				"ZeroVentiCinque-Trenta",
				"ZeroCinque"
//				"ZeroCinque-Trenta",
//				"ZeroSettantaCinque", 
//				"ZeroSettantaCinque-Trenta",
//				"Uno", 
//				"UnoDue", 
//				"Uno-Trenta"
				});
	}
	
	private static void activities(String[] logs, String[] names) throws Exception {
		
		XConceptExtension xce = XConceptExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		
		for(int i = 0; i < logs.length; i++) {
			String path = logs[i];
			XLog tmpLog = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), path);
			
			long dur = 0L;
			long minDur = Long.MAX_VALUE;
			long maxDur = Long.MIN_VALUE;
			double stDev = 0.0;
			int num = 0;
			
			for(XTrace trace : tmpLog) {

				long act = 0L;
				
				if(i == 0 && xce.extractName(trace).equals("84")) {	
					
				}else {		
					act = trace.size();
					dur += act;

					minDur = Math.min(minDur, act);
					maxDur = Math.max(maxDur, act);
					
					num++;
				}
			}

			double ave = ((double) dur) / ((double) num);
			
			for(XTrace trace : tmpLog) {

				long act = 0L;
				
				if(i == 0 && xce.extractName(trace).equals("84")) {	
					
				}else {
					act = trace.size();

					stDev += Math.pow((act - ave), 2);
				}
			}
			
			stDev = Math.sqrt(stDev / num);
			
			System.out.println(names[i] + " " + ave + " " + stDev + " " + minDur + " " + maxDur);
			
		}
		
	}
	
	private static void dur(String[] logs, String[] names) throws Exception {
		
		XConceptExtension xce = XConceptExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		
		for(int i = 0; i < logs.length; i++) {
			String path = logs[i];
			XLog tmpLog = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), path);
			
			double dur = 0L;
			double minDur = Long.MAX_VALUE;
			double maxDur = Long.MIN_VALUE;
			double stDev = 0.0;
			int num = 0;
			
			for(XTrace trace : tmpLog) {

				long min = Long.MAX_VALUE;
				long max = Long.MIN_VALUE;

				if(i == 0 && xce.extractName(trace).equals("84")) {	
					
				}else {		
					for(XEvent event : trace) {
						long time = xte.extractTimestamp(event).getTime();
						min = Math.min(min, time);
						max = Math.max(max, time);
					}
					
					double d = (max - min) / 3600000.0; 
					dur += d;

					minDur = Math.min(minDur, d);
					maxDur = Math.max(maxDur, d);
					
					num++;
				}
			}

			double ave = (dur) / (num);
			
			for(XTrace trace : tmpLog) {

				long min = Long.MAX_VALUE;
				long max = Long.MIN_VALUE;

				if(i == 0 && xce.extractName(trace).equals("84")) {	
					
				}else {
					for(XEvent event : trace) {
						long time = xte.extractTimestamp(event).getTime();
						min = Math.min(min, time);
						max = Math.max(max, time);
					}
					dur = (max - min) / 3600000.0; 

					stDev += Math.pow((dur - ave), 2);
				}
			}
			
			stDev = Math.sqrt(stDev / num);
			
			System.out.println(names[i] + " " + ave + " " + stDev + " " + minDur + " " + maxDur);
			
		}
		
	}
	
	private static void uniteFiles() throws Exception {
		unite("/media/Data/SharedFolder/AllLogs.xes", 
				new String[]{
				"/media/Data/SharedFolder/Commercial/Exp/Exp0/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Log/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp0.25-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp0.5-30/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp0.75-30/testLog.xes",
//				"/media/Data/SharedFolder/Commercial/Exp/Exp1/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp1(2)/testLog.xes",
				"/media/Data/SharedFolder/Commercial/Exp/Exp1-30/testLog.xes"
				},
				
				new String[]{
				"Zero",
				"Zero-Trenta",
				"ZeroVentiCinque", 
				"ZeroVentiCinque-Trenta",
//				"ZeroCinque", 
				"ZeroCinque-Trenta",
				"ZeroSettantaCinque", 
				"ZeroSettantaCinque-Trenta",
//				"Uno", 
				"UnoDue", 
				"Uno-Trenta"
				});
	}
	
	private static void unite(String first, String[] logs, String[] names) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), first);
		
		XConceptExtension xce = XConceptExtension.instance();
		
		for(int i = 0; i < logs.length; i++) {
			String path = logs[i];
			XLog tmpLog = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), path);
			
			for(XTrace trace : tmpLog) {
				xce.assignName(trace, names[i]+xce.extractName(trace));
				log.add(trace);
			}
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/", "AllLogs2.xes", log);
		
	}

	private static void mergeFiles() {
//		fixLine("/media/stormfire/2795-0BA2/Exp0/testLog.xes", "/media/stormfire/2795-0BA2/Exp0/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/new0.25/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/new0.25/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0-33/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0-33/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.5/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.5/");
        String test = "5";
        File f = new File("/home/user/DSS/"+test+"/");
        f.mkdirs();
		fixLine("/home/user/DSS/" + test + "/testLog.xes", "/media/user/MAL/Gurobi" + test + "/Commercial/Log/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.5-30/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.5-30/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.5-33/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.5-33/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.25/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.25/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.25-30/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.25-30/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.25-33/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.25-33/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.75/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.75/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.75-30/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.75-30/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp0.75-33/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp0.75-33/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp1/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp1/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp1-30/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp1-30/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp1-33/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp1-33/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Exp1(2)/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Exp1(2)/");
//		fixLine("/media/Data/SharedFolder/Commercial/Exp/Log/testLog.xes", "/media/Data/SharedFolder/Commercial/Exp/Log/");
	}
	
	public static HashMap<String, HashMap<String, Integer>> discoverFrequencesResources(String input) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		
		int total = 0;
		int pos = 0;

		HashMap<String, HashMap<String, Integer>> frequences = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> freq = new HashMap<String, Integer>();
		String last = null;
		
		for(XTrace trace : log) {
			
			pos++;
			
			for(XEvent event : trace) {
				
				total++;
				String name = xce.extractName(event);
				String res = xoe.extractResource(event);

				HashMap<String, Integer> frequence = null;
				if((frequence = frequences.get(name)) == null) {
					frequence = new HashMap<String, Integer>();
					frequences.put(name, frequence);
				}
				Integer count = null;
				if((count = frequence.get(res)) == null) {
					count = 0;
				}
				count++;
				frequence.put(res, count);
				
			}
			
		}
		
//		for(String key : frequences.keySet()) {
//			System.out.println(key);
//			for(String name : frequences.get(key).keySet()) {
//				System.out.println(name + " : " + frequences.get(key).get(name));
//			}
//		}
//		System.out.println("Total : " + total);
		
		return frequences;
	}
	
	private static void discoverFrequences(String input) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);

		HashMap<String, Integer> frequences = new HashMap<String, Integer>();
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		
		int total = 0;

		for(XTrace trace : log) {
			
			for(XEvent event : trace) {
				
				total++;
				
				String name = xce.extractName(event);
				Integer count = null;
				if((count = frequences.get(name)) == null) {
					count = 0;
				}
				count++;
				frequences.put(name, count);
				
			}
			
		}
		
		for(String key : frequences.keySet()) {
			System.out.println(key + " : " + frequences.get(key));
		}
		System.out.println("Total : " + total);
	}
	
	private static void discoverFrequences2(String input) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);

		HashMap<Integer, HashMap<String, Integer>> frequences = new HashMap<Integer, HashMap<String, Integer>>();
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		
		int total = 0;
		int pos = 0;
		
		HashMap<String, Integer> freq = new HashMap<String, Integer>();

		for(XTrace trace : log) {
			
			pos++;
			HashMap<String, Integer> frequence = null;
			
			for(XEvent event : trace) {
				
				total++;
				
				String name = xce.extractName(event);
				Integer count = null;
				
				if((frequence = frequences.get(pos)) == null) {
					frequence = new HashMap<String, Integer>();
					frequences.put(pos, frequence);
				}
				if((count = frequence.get(name)) == null) {
					count = 0;
				}
				count++;
				frequence.put(name, count);
				freq.put(name, 0);
				
			}
			
		}
		
		for(String key : freq.keySet()) {
			int sum = 0;
			for(Integer p : frequences.keySet()) {
				sum += frequences.get(p).get(key)!=null?frequences.get(p).get(key):0;
			}
			freq.put(key, sum/frequences.size());
			System.out.println(key + " : " + (double)sum/frequences.size());
		}
		System.out.println("Total : " + total);
		
	}

//	private static void discoverFrequences3(String input) throws Exception {
//		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
//
//		String[] newClaimSuc = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4"};
//		String[] incomingSuc = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4", "Process_Add_Info_7", "Contact_Customer_6", "Generate_Payment_14"};
//		String[] conductSuc = incomingSuc;
//		String[] addInfoSuc = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4", "Generate_Payment_14"};
//		String[] contactSuc = addInfoSuc;
//		String[] generateSuc = new String[] {"Authorize_Payment_15"};
//		String[] authorizeSuc = new String[] {"Close_File_22"};
//		String[] followSuc = new String[] { "Follow_Up_Requested_20", "Close_File_22"};
//
//		String newClaim = "New_Claim_3";
//		String incoming = "Incoming_Correspondence_5";
//		String conduct = "Conduct_File_Review_4";
//		String contact = "Contact_Customer_6";
//		String process = "Process_Add_Info_7";
//		String authorize = "Authorize_Payment_15";
//		String generate = "Generate_Payment_14";
//		String follow = "Follow_Up_Requested_20";
//		String close = "Close_File_22";
//
//		XConceptExtension xce = XConceptExtension.instance();
//		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
//		XLifecycleExtension xle = XLifecycleExtension.instance();
//		XTimeExtension xte = XTimeExtension.instance();
//
//		int total = 0;
//		int pos = 0;
//
//		HashMap<String[], HashMap<String, Integer>> frequences = new HashMap<String[], HashMap<String, Integer>>();
//		HashMap<String, Integer> freq = new HashMap<String, Integer>();
//		String last = null;
//
//		for(XTrace trace : log) {
//
//			pos++;
//
//			for(XEvent event : trace) {
//
//				total++;
//				String name = xce.extractName(event);
//				String[] suc = null;
//
//				if(!name.equals(follow)) {
//					if(last != null) {
//						if(last.equals(newClaim)) {
//							suc = newClaimSuc;
//						}else if(last.equals(incoming)) {
//							suc = incomingSuc;
//						}else if(last.equals(conduct)) {
//							suc = conductSuc;
//						}else if(last.equals(contact)) {
//							suc = contactSuc;
//						}else if(last.equals(process)) {
//							suc = addInfoSuc;
//						}else if(last.equals(authorize)) {
//							suc = authorizeSuc;
//						}else if(last.equals(generate)) {
//							suc = generateSuc;
//						}
//					}
//				}else {
//					suc = followSuc;
//				}
//
//				if(suc != null) {
//					for(String s : suc) {
//						if(name.equals(s)) {
//							HashMap<String, Integer> frequence = null;
//							if((frequence = frequences.get(suc)) == null) {
//								frequence = new HashMap<String, Integer>();
//								frequences.put(suc, frequence);
//							}
//							Integer count = null;
//							if((count = frequence.get(name)) == null) {
//								count = 0;
//							}
//							count++;
//							frequence.put(name, count);
//						}
//					}
//				}
//
//				last = (!name.equals(follow))?name:last;
//
//			}
//
//		}
//
//		for(String[] key : frequences.keySet()) {
//			System.out.println(cern.colt.Arrays.toString(key));
//			for(String name : frequences.get(key).keySet()) {
//				System.out.println(name + " : " + frequences.get(key).get(name));
//			}
//		}
//		System.out.println("Total : " + total);
//
//	}
	
	private static void fixLine(String newPath, String logBaseString) {
		
		File file = new File(logBaseString+"Log.xes");
		File tmpFile = new File(newPath);
		tmpFile.delete();
		copyFile(file, tmpFile, true);	
		
		for(int i = 1; i < 300; i++) {
			file = new File(logBaseString+i+".xes");

			copyFile(file, tmpFile, false);	
			
			String lastLine = line(newPath);
				
			try {
				FileWriter fw1 = new FileWriter(tmpFile, true);
				if(!"</trace>".equals(lastLine) ) {
					fw1.append("</trace>");
					fw1.flush();
				}
				fw1.flush();
				fw1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		String lastLine = line(newPath);
			
		try {
			FileWriter fw1 = new FileWriter(tmpFile, true);
			if(!"</trace>".equals(lastLine) ) {
				fw1.append("</trace>");
				fw1.flush();
			}
			fw1.append("</log>");
			fw1.flush();
			fw1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
            System.out.println(newPath);
            XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), newPath);
            XConceptExtension xce = XConceptExtension.instance();

            XLog newLog = (XLog) log.clone();
            newLog.clear();

            int count = 1;
            for(XTrace t : log) {
                String end = "Close_File_22";
                boolean add = false;
                if(t.size() > 0 && end.equals(xce.extractName(t.get(t.size() - 1)))) {
                    add = true;
                }
                if(add) {
                    xce.assignName(t, Integer.toString(count));
                    newLog.add(t);
                    count++;
                }
                if(count > 213) {
                    break;
                }
            }

            File f = new File(newPath);
            System.out.println(f.getName());
            ImportEventLog.exportLog(f.getParent()+"/", f.getName()+"1", newLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	private static void copyFile(File sourceFile, File destFile, boolean delete) {
		try {
		    if(!destFile.exists()) {
		        destFile.createNewFile();
		    }else {
		    	if(delete) {
		    		destFile.delete();
			    	destFile.createNewFile();
		    	}
		    }
		
		    InputStream in = null;
		    OutputStream out = null; // appending output stream

		    try {
		        in = new FileInputStream(sourceFile);
		        out = new FileOutputStream(destFile, true);

		        byte[] buf = new byte[1024];
		        int len;
		        while ((len = in.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }
		    } finally {
		        if (out != null) {
		            out.close();
		        }
		        if (in != null) {
		            in.close();
		        }
		    }
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("prob");}
	}
	
	private static String line(String path) {
		File f = new File(path);
		RandomAccessFile fileHandler = null;
		
		try {
			try {
				fileHandler = new RandomAccessFile(f, "r");
				long fileLength = f.length() - 1;
				StringBuilder sb = new StringBuilder();
	
		        for( long filePointer = fileLength; filePointer != -1; filePointer-- ) {
		            fileHandler.seek( filePointer );
		            int readByte = fileHandler.readByte();
	
		            if( readByte == 0xA ) {
		                if( filePointer == fileLength ) {
		                    continue;
		                } else {
		                    break;
		                }
		            } else if( readByte == 0xD ) {
		                if( filePointer == fileLength - 1 ) {
		                    continue;
		                } else {
		                    break;
		                }
		            }
	
		            sb.append( ( char ) readByte );
		        }
	
		        String lastLine = sb.reverse().toString();
		        return lastLine;
		    } catch( java.io.FileNotFoundException e ) {
		        e.printStackTrace();
		        return null;
		    } catch( java.io.IOException e ) {
		        e.printStackTrace();
		        return null;
		    } 
		}finally { try {
			fileHandler.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
	}
	
	private static void generateResources2() {
		
		String[] resources = new String[] {"Ryan Finn", "Anita Morrall", "Ashleigh McCourt", "Emmett O'Reilly", "Andrei Bennett", 
			"Kahlee Robertson", "Treesa McInnes", "David Wistrom", "Jason Forbes", "Clare Binns", "Sara Davis", "Megan Creasey", 
			"Angela Gray", "Karen Davis", "Matthew Gray", "Koshi George", "Rebecca Conyngham", "Andrew Ejubs", "Shirley Dunnigan", 
			"Nick Mann", "Shehzad Sardar", "Phil O'Donnell", "Tania Whitbread", "Samantha Whittemore", "Eloise McCarthy", "N", 
			"Shantel Netzler", "Dwayne Tourell", "Toni Emmett", "Luke Patrick", "Kavita Krishna", "Stephanie King", 
			"Nana Ansuh Yeboah", "Scott Davidson", "Leon Savelio", "Marie Martin", "Prashant Singh", "Daniel Aycock", 
			"Matthew Collett", "Vashti Keyes", "Cherie Searle", "Alicia Payne", "Zachary Gibbs", "Richard Roberts", 
			"Michelle Talay", "Kirsty Azzopardi", "Shane Muddell", "Leanne Brischetto", "Lisa-Maree Baiada", "Lee Hamilton", 
			"Sylvia Jedras", "Anne Nguyen", "Kevin Murphy", "Vanessa Varley", "Michi Hollis", "Justine Meniconi", "Allira Sandoval", 
			"Natalie Lock", "Viviene Franco", "Sue Wharley", "Daniel Cregan", "Shauna Harley", "Abhishek Jain", "Patricia Perri", 
			"Kathryn King", "Scott Cook", "Simon Wardhaugh", "Bill Burmester", "Kirralee Steward", "Aaron Thirukumar", "Patricia Romero", 
			"Luke Smith", "Janelle Bantick", "Rebecca Utro", "Peter Becvarovski", "Rhiannon Merchant", "Ayla Aydin", "Ryan Anderson-Smith",
			"Kirsten Nuttall", "Ramkumar Hariram", "Debbie Hunter", "Natalie Doyle", "Avee Sharma", "Susan Dunlop", "Helen Georgiou", 
			"Kristian O'Keefe", "Katie MClay", "Matthew Harvey", "Andrea Gynn", "Vinoshi De Fonseka", "Kareena Assem", "Nikhil Todurkar",
			"Lynette Braddock", "Anthony Qiu", "Glenda McTavish", "Lee Sithisakd", "Claire Matthews", "Matthew Davis", "Carly Humphreys",
			"Timothy Ferguson", "Bradley Warren", "Aisling Fleming", "Liesa Bruhn", "Angela Attard", "Michael Jessop", "Douglas Heim",
			"Natalie Gilmour", "Sharon Kennedy", "Matthew Vincent", "Janet Mataia", "Mark Smeaton", "Norma Ballinger", "Nathan de Pratt",
			"Mili Kapur", "Michelle Ebsworth", "George Healy", "Deborah Holl", "Jessica Borchard", "Mark Jeffrey", "Peter Henry"};
	
	String[] incomingCorrespondenResources = new String[] {"Anita Morrall", "Daniel Cregan", "Ashleigh McCourt", "Shauna Harley", 
			"Emmett O'Reilly", "Scott Cook", "Andrei Bennett", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", 
			"Patricia Romero", "Rebecca Utro", "Peter Becvarovski", "Clare Binns", "Ayla Aydin", "Rhiannon Merchant", "Ryan Anderson-Smith",
			"Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Ramkumar Hariram", "Karen Davis", "Matthew Gray", 
			"Koshi George", "Natalie Doyle", "Avee Sharma", "Andrew Ejubs", "Susan Dunlop", "Helen Georgiou", "Kristian O'Keefe", 
			"Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Matthew Harvey", "Andrea Gynn", "Kareena Assem", "Lynette Braddock", 
			"Anthony Qiu", "Glenda McTavish", "N", "Claire Matthews", "Lee Sithisakd", "Dwayne Tourell", "Matthew Davis", "Shantel Netzler", 
			"Toni Emmett", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Carly Humphreys", "Bradley Warren", "Aisling Fleming", 
			"Leon Savelio", "Marie Martin", "Liesa Bruhn", "Angela Attard", "Prashant Singh", "Matthew Collett", "Douglas Heim", 
			"Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Janet Mataia", 
			"Matthew Vincent", "Shane Muddell", "Leanne Brischetto", "Norma Ballinger", "Nathan de Pratt", "Mili Kapur", "Kevin Murphy", 
			"Deborah Holl", "Michelle Ebsworth", "George Healy", "Michi Hollis", "Justine Meniconi", "Jessica Borchard", "Mark Jeffrey", 
			"Peter Henry"};
	
	String[] authorisePaymentIPResources = new String[] {"Lynette Braddock", "Anthony Qiu", "Shauna Harley", "Emmett O'Reilly", 
			"Kathryn King", "Claire Matthews", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh", "Carly Humphreys", 
			"Timothy Ferguson", "Treesa McInnes", "Aaron Thirukumar", "Luke Smith", "Leon Savelio", "Liesa Bruhn", "Angela Attard", 
			"Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Vashti Keyes", "Angela Gray", "Kirsten Nuttall", 
			"Michelle Talay", "Richard Roberts", "Ramkumar Hariram", "Debbie Hunter", "Shane Muddell", "Janet Mataia", "Koshi George", 
			"Natalie Doyle", "Mili Kapur", "Susan Dunlop", "Kevin Murphy", "Kristian O'Keefe", "Katie MClay", "Deborah Holl", 
			"Phil O'Donnell", "Michi Hollis", "Tania Whitbread", "Mark Jeffrey"};
	
	String[] generatePaymentResources = new String[] {"Anita Morrall", "Kareena Assem", "Lynette Braddock", "Nikhil Todurkar",
			"Anthony Qiu", "Emmett O'Reilly", "Claire Matthews", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh",
			"Carly Humphreys", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", "Liesa Bruhn", 
			"Angela Attard", "Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Clare Binns", 
			"Douglas Heim", "Vashti Keyes", "Angela Gray", "Cherie Searle", "Kirsten Nuttall", "Richard Roberts", 
			"Ramkumar Hariram", "Janet Mataia", "Shane Muddell", "Koshi George", "Natalie Doyle", "Mili Kapur", 
			"Susan Dunlop", "Kristian O'Keefe", "Shehzad Sardar", "Michi Hollis", "Justine Meniconi", "Tania Whitbread", 
			"Jessica Borchard", "Mark Jeffrey", "Eloise McCarthy"};
	
	String[] processAdditionalInResources = new String[] {"Anita Morrall", "Ashleigh McCourt", "Shauna Harley", "Emmett O'Reilly", 
			"Kathryn King", "Scott Cook", "Kahlee Robertson", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick",
			"David Wistrom", "Peter Becvarovski", "Ayla Aydin", "Clare Binns", "Ryan Anderson-Smith", "Sara Davis", "Megan Creasey",
			"Angela Gray", "Kirsten Nuttall", "Debbie Hunter", "Ramkumar Hariram", "Karen Davis", "Koshi George", "Natalie Doyle", 
			"Rebecca Conyngham", "Shirley Dunnigan", "Susan Dunlop", "Helen Georgiou", "Katie MClay", "Kristian O'Keefe", "Phil O'Donnell",
			"Tania Whitbread", "Matthew Harvey", "Samantha Whittemore", "Vinoshi De Fonseka", "Kareena Assem", "Lynette Braddock",
			"Anthony Qiu", "Glenda McTavish", "Claire Matthews", "Lee Sithisakd", "N", "Luke Patrick", "Kavita Krishna", 
			"Nana Ansuh Yeboah", "Carly Humphreys", "Aisling Fleming", "Leon Savelio", "Scott Davidson", "Liesa Bruhn", "Angela Attard",
			"Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Alicia Payne",
			"Zachary Gibbs", "Sharon Kennedy", "Michelle Talay", "Richard Roberts", "Kirsty Azzopardi", "Matthew Vincent", "Shane Muddell",
			"Janet Mataia", "Mark Smeaton", "Lee Hamilton", "Nathan de Pratt", "Mili Kapur", "Sylvia Jedras", "Kevin Murphy", 
			"Deborah Holl", "Vanessa Varley", "Michelle Ebsworth", "George Healy", "Michi Hollis", "Justine Meniconi", "Jessica Borchard", 
			"Mark Jeffrey", "Viviene Franco"};
	
	String[] closeFileResources = new String[] {"Anita Morrall", "Kareena Assem", "Lynette Braddock", "Ashleigh McCourt",
			"Anthony Qiu", "Shauna Harley", "Emmett O'Reilly", "Claire Matthews", "Luke Patrick", "Kavita Krishna", 
			"Nana Ansuh Yeboah", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", 
			"Liesa Bruhn", "Angela Attard", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Clare Binns", "Vashti Keyes",
			"Kirsten Nuttall", "Richard Roberts", "Ramkumar Hariram", "Janet Mataia", "Lisa-Maree Baiada", "Koshi George", 
			"Natalie Doyle", "Mili Kapur", "Susan Dunlop", "Kevin Murphy", "Katie MClay", "Kristian O'Keefe", "Phil O'Donnell", 
			"Michi Hollis", "Tania Whitbread", "Natalie Lock", "Mark Jeffrey"};
	
	String[] contactCustomerResources = new String[] {"Anita Morrall", "Kareena Assem", "Ashleigh McCourt", "Anthony Qiu", "Shauna Harley", 
			"Emmett O'Reilly", "N", "Claire Matthews", "Lee Sithisakd", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh",
			"Carly Humphreys", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", "Liesa Bruhn", "Marie Martin", 
			"Angela Attard", "Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Clare Binns", "Ayla Aydin", "Ryan Anderson-Smith",
			"Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Angela Gray", "Kirsten Nuttall", "Richard Roberts", "Ramkumar Hariram", 
			"Janet Mataia", "Matthew Vincent", "Shane Muddell", "Mark Smeaton", "Koshi George", "Natalie Doyle", "Mili Kapur", "Helen Georgiou", 
			"Deborah Holl", "Michi Hollis", "Tania Whitbread", "Mark Jeffrey"};
	
	String[] conductFileReviewResources = new String[] {"Anita Morrall", "Sue Wharley", "Ryan Finn", "Ashleigh McCourt", "Shauna Harley", 
			"Emmett O'Reilly", "Patricia Perri", "Andrei Bennett", "Simon Wardhaugh", "Bill Burmester", "Treesa McInnes", 
			"Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Clare Binns", "Ayla Aydin", "Rhiannon Merchant", 
			"Ryan Anderson-Smith", "Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Ramkumar Hariram", "Karen Davis", 
			"Matthew Gray", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", "Helen Georgiou", "Kristian O'Keefe",
			"Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Andrea Gynn", "Eloise McCarthy", "Kareena Assem", "Lynette Braddock",
			"Anthony Qiu", "Glenda McTavish", "Shantel Netzler", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Stephanie King",
			"Carly Humphreys", "Bradley Warren", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn", "Marie Martin", "Angela Attard", 
			"Michael Jessop", "Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle",
			"Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Matthew Vincent", "Janet Mataia", "Shane Muddell", "Nathan de Pratt", 
			"Mili Kapur", "Anne Nguyen", "Kevin Murphy", "Michelle Ebsworth", "George Healy", "Deborah Holl", "Michi Hollis", 
			"Justine Meniconi", "Jessica Borchard", "Mark Jeffrey"};
	
	String[] newClaimIPIResources = new String[] {"Shauna Harley", "Emmett O'Reilly", "Abhishek Jain", "Simon Wardhaugh", "Kirralee Steward", 
			"Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Ayla Aydin", "Ryan Anderson-Smith", "Angela Gray",
			"Kirsten Nuttall", "Ramkumar Hariram", "Matthew Gray", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", 
			"Helen Georgiou", "Kristian O'Keefe", "Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Matthew Harvey", "Andrea Gynn", 
			"Kareena Assem", "Lynette Braddock", "Anthony Qiu", "Glenda McTavish", "N", "Claire Matthews", "Luke Patrick", "Kavita Krishna",
			"Carly Humphreys", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn", "Angela Attard", "Prashant Singh", "Daniel Aycock", 
			"Matthew Collett", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Michelle Talay", "Janet Mataia", "Matthew Vincent", 
			"Shane Muddell", "Nathan de Pratt", "Mili Kapur", "George Healy", "Deborah Holl", "Michi Hollis", "Justine Meniconi",
			"Allira Sandoval", "Jessica Borchard", "Mark Jeffrey"};
	
	String[] followUpRequestedResources = new String[] {"Anita Morrall", "Ashleigh McCourt", "Shauna Harley", "Emmett O'Reilly", 
			"Patricia Perri", "Andrei Bennett", "Simon Wardhaugh", "Bill Burmester", "Kirralee Steward", "Treesa McInnes", 
			"Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Jason Forbes", "Rhiannon Merchant", "Ayla Aydin", "Clare Binns",
			"Ryan Anderson-Smith", "Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Debbie Hunter", "Ramkumar Hariram",
			"Karen Davis", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", "Nick Mann", "Helen Georgiou", 
			"Kristian O'Keefe", "Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Andrea Gynn", "Eloise McCarthy", "Kareena Assem", 
			"Lynette Braddock", "Anthony Qiu", "Claire Matthews", "N", "Matthew Davis", "Shantel Netzler", "Luke Patrick", "Toni Emmett",
			"Kavita Krishna", "Nana Ansuh Yeboah", "Carly Humphreys", "Bradley Warren", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn",
			"Marie Martin", "Angela Attard", "Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour",
			"Cherie Searle", "Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Shane Muddell", "Janet Mataia", "Matthew Vincent", 
			"Nathan de Pratt", "Mili Kapur", "Anne Nguyen", "Kevin Murphy", "Michelle Ebsworth", "George Healy", "Deborah Holl", 
			"Michi Hollis", "Justine Meniconi", "Jessica Borchard", "Mark Jeffrey"};
	
		System.out.print("String[] followUpRequestedResources = new String[] {");
		for(String r : followUpRequestedResources) {
			for(int i = 0; i < resources.length; i++) {
				if(r.equals(resources[i])) {
					System.out.print("resources["+i+"], ");
					break;
				}
			}
		}
		System.out.println("};");
		System.out.println();
		
	
	}
	
	private static void activeCaseDiscovery(String input) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		System.out.println("bla");
		XTimeExtension xte = XTimeExtension.instance();

		LinkedList<Integer> durations = new LinkedList<Integer>();

		Long start = Long.MAX_VALUE;
		Long max = 0L;
		for(XTrace trace : log) {
			
			Long startCase = xte.extractTimestamp(trace.get(0)).getTime();
			Long endCase = xte.extractTimestamp(trace.get(trace.size()-1)).getTime();
			
			if(startCase < start) {
				start = startCase;
			}
			
			if(endCase > max) {
				max = endCase;
			}
		}
		
		while(start < max) {
			int count = 0;
			for(XTrace trace : log) {
				
				Long startCase = xte.extractTimestamp(trace.get(0)).getTime();
				Long endCase = xte.extractTimestamp(trace.get(trace.size()-1)).getTime();
				
				if(startCase <= start && endCase >= start) {
					count++;
				}
			}
			durations.add(count);
			start += 24*60*60*1000;
		}
		
		double mean = 0.0;
		for(Integer i : durations) {
			mean += i;
		}
		System.out.println(durations);
		System.out.println(mean/durations.size());
		
	}

	private static void generateResources() {
		  String[] resources = new String[] {"Ryan Finn", "Anita Morrall", "Ashleigh McCourt", "Emmett O'Reilly", "Andrei Bennett", 
			"Kahlee Robertson", "Treesa McInnes", "David Wistrom", "Jason Forbes", "Clare Binns", "Sara Davis", "Megan Creasey", 
			"Angela Gray", "Karen Davis", "Matthew Gray", "Koshi George", "Rebecca Conyngham", "Andrew Ejubs", "Shirley Dunnigan", 
			"Nick Mann", "Shehzad Sardar", "Phil O'Donnell", "Tania Whitbread", "Samantha Whittemore", "Eloise McCarthy", "N", 
			"Shantel Netzler", "Dwayne Tourell", "Toni Emmett", "Luke Patrick", "Kavita Krishna", "Stephanie King", 
			"Nana Ansuh Yeboah", "Scott Davidson", "Leon Savelio", "Marie Martin", "Prashant Singh", "Daniel Aycock", 
			"Matthew Collett", "Vashti Keyes", "Cherie Searle", "Alicia Payne", "Zachary Gibbs", "Richard Roberts", 
			"Michelle Talay", "Kirsty Azzopardi", "Shane Muddell", "Leanne Brischetto", "Lisa-Maree Baiada", "Lee Hamilton", 
			"Sylvia Jedras", "Anne Nguyen", "Kevin Murphy", "Vanessa Varley", "Michi Hollis", "Justine Meniconi", "Allira Sandoval", 
			"Natalie Lock", "Viviene Franco", "Sue Wharley", "Daniel Cregan", "Shauna Harley", "Abhishek Jain", "Patricia Perri", 
			"Kathryn King", "Scott Cook", "Simon Wardhaugh", "Bill Burmester", "Kirralee Steward", "Aaron Thirukumar", "Patricia Romero", 
			"Luke Smith", "Janelle Bantick", "Rebecca Utro", "Peter Becvarovski", "Rhiannon Merchant", "Ayla Aydin", "Ryan Anderson-Smith",
			"Kirsten Nuttall", "Ramkumar Hariram", "Debbie Hunter", "Natalie Doyle", "Avee Sharma", "Susan Dunlop", "Helen Georgiou", 
			"Kristian O'Keefe", "Katie MClay", "Matthew Harvey", "Andrea Gynn", "Vinoshi De Fonseka", "Kareena Assem", "Nikhil Todurkar",
			"Lynette Braddock", "Anthony Qiu", "Glenda McTavish", "Lee Sithisakd", "Claire Matthews", "Matthew Davis", "Carly Humphreys",
			"Timothy Ferguson", "Bradley Warren", "Aisling Fleming", "Liesa Bruhn", "Angela Attard", "Michael Jessop", "Douglas Heim",
			"Natalie Gilmour", "Sharon Kennedy", "Matthew Vincent", "Janet Mataia", "Mark Smeaton", "Norma Ballinger", "Nathan de Pratt",
			"Mili Kapur", "Michelle Ebsworth", "George Healy", "Deborah Holl", "Jessica Borchard", "Mark Jeffrey", "Peter Henry"};
	
	  String[] incomingCorrespondenResources = new String[] {"Anita Morrall", "Daniel Cregan", "Ashleigh McCourt", "Shauna Harley", 
			"Emmett O'Reilly", "Scott Cook", "Andrei Bennett", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", 
			"Patricia Romero", "Rebecca Utro", "Peter Becvarovski", "Clare Binns", "Ayla Aydin", "Rhiannon Merchant", "Ryan Anderson-Smith",
			"Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Ramkumar Hariram", "Karen Davis", "Matthew Gray", 
			"Koshi George", "Natalie Doyle", "Avee Sharma", "Andrew Ejubs", "Susan Dunlop", "Helen Georgiou", "Kristian O'Keefe", 
			"Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Matthew Harvey", "Andrea Gynn", "Kareena Assem", "Lynette Braddock", 
			"Anthony Qiu", "Glenda McTavish", "N", "Claire Matthews", "Lee Sithisakd", "Dwayne Tourell", "Matthew Davis", "Shantel Netzler", 
			"Toni Emmett", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Carly Humphreys", "Bradley Warren", "Aisling Fleming", 
			"Leon Savelio", "Marie Martin", "Liesa Bruhn", "Angela Attard", "Prashant Singh", "Matthew Collett", "Douglas Heim", 
			"Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Janet Mataia", 
			"Matthew Vincent", "Shane Muddell", "Leanne Brischetto", "Norma Ballinger", "Nathan de Pratt", "Mili Kapur", "Kevin Murphy", 
			"Deborah Holl", "Michelle Ebsworth", "George Healy", "Michi Hollis", "Justine Meniconi", "Jessica Borchard", "Mark Jeffrey", 
			"Peter Henry"};
	
	  String[] authorisePaymentIPResources = new String[] {"Lynette Braddock", "Anthony Qiu", "Shauna Harley", "Emmett O'Reilly", 
			"Kathryn King", "Claire Matthews", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh", "Carly Humphreys", 
			"Timothy Ferguson", "Treesa McInnes", "Aaron Thirukumar", "Luke Smith", "Leon Savelio", "Liesa Bruhn", "Angela Attard", 
			"Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Vashti Keyes", "Angela Gray", "Kirsten Nuttall", 
			"Michelle Talay", "Richard Roberts", "Ramkumar Hariram", "Debbie Hunter", "Shane Muddell", "Janet Mataia", "Koshi George", 
			"Natalie Doyle", "Mili Kapur", "Susan Dunlop", "Kevin Murphy", "Kristian O'Keefe", "Katie MClay", "Deborah Holl", 
			"Phil O'Donnell", "Michi Hollis", "Tania Whitbread", "Mark Jeffrey"};
	
	  String[] generatePaymentResources = new String[] {"Anita Morrall", "Kareena Assem", "Lynette Braddock", "Nikhil Todurkar",
			"Anthony Qiu", "Emmett O'Reilly", "Claire Matthews", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh",
			"Carly Humphreys", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", "Liesa Bruhn", 
			"Angela Attard", "Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Clare Binns", 
			"Douglas Heim", "Vashti Keyes", "Angela Gray", "Cherie Searle", "Kirsten Nuttall", "Richard Roberts", 
			"Ramkumar Hariram", "Janet Mataia", "Shane Muddell", "Koshi George", "Natalie Doyle", "Mili Kapur", 
			"Susan Dunlop", "Kristian O'Keefe", "Shehzad Sardar", "Michi Hollis", "Justine Meniconi", "Tania Whitbread", 
			"Jessica Borchard", "Mark Jeffrey", "Eloise McCarthy"};
	
	  String[] processAdditionalInResources = new String[] {"Anita Morrall", "Ashleigh McCourt", "Shauna Harley", "Emmett O'Reilly", 
			"Kathryn King", "Scott Cook", "Kahlee Robertson", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick",
			"David Wistrom", "Peter Becvarovski", "Ayla Aydin", "Clare Binns", "Ryan Anderson-Smith", "Sara Davis", "Megan Creasey",
			"Angela Gray", "Kirsten Nuttall", "Debbie Hunter", "Ramkumar Hariram", "Karen Davis", "Koshi George", "Natalie Doyle", 
			"Rebecca Conyngham", "Shirley Dunnigan", "Susan Dunlop", "Helen Georgiou", "Katie MClay", "Kristian O'Keefe", "Phil O'Donnell",
			"Tania Whitbread", "Matthew Harvey", "Samantha Whittemore", "Vinoshi De Fonseka", "Kareena Assem", "Lynette Braddock",
			"Anthony Qiu", "Glenda McTavish", "Claire Matthews", "Lee Sithisakd", "N", "Luke Patrick", "Kavita Krishna", 
			"Nana Ansuh Yeboah", "Carly Humphreys", "Aisling Fleming", "Leon Savelio", "Scott Davidson", "Liesa Bruhn", "Angela Attard",
			"Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Alicia Payne",
			"Zachary Gibbs", "Sharon Kennedy", "Michelle Talay", "Richard Roberts", "Kirsty Azzopardi", "Matthew Vincent", "Shane Muddell",
			"Janet Mataia", "Mark Smeaton", "Lee Hamilton", "Nathan de Pratt", "Mili Kapur", "Sylvia Jedras", "Kevin Murphy", 
			"Deborah Holl", "Vanessa Varley", "Michelle Ebsworth", "George Healy", "Michi Hollis", "Justine Meniconi", "Jessica Borchard", 
			"Mark Jeffrey", "Viviene Franco"};
	
	  String[] closeFileResources = new String[] {"Anita Morrall", "Kareena Assem", "Lynette Braddock", "Ashleigh McCourt",
			"Anthony Qiu", "Shauna Harley", "Emmett O'Reilly", "Claire Matthews", "Luke Patrick", "Kavita Krishna", 
			"Nana Ansuh Yeboah", "Simon Wardhaugh", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", 
			"Liesa Bruhn", "Angela Attard", "Peter Becvarovski", "Matthew Collett", "Ayla Aydin", "Clare Binns", "Vashti Keyes",
			"Kirsten Nuttall", "Richard Roberts", "Ramkumar Hariram", "Janet Mataia", "Lisa-Maree Baiada", "Koshi George", 
			"Natalie Doyle", "Mili Kapur", "Susan Dunlop", "Kevin Murphy", "Katie MClay", "Kristian O'Keefe", "Phil O'Donnell", 
			"Michi Hollis", "Tania Whitbread", "Natalie Lock", "Mark Jeffrey"};
	
	  String[] contactCustomerResources = new String[] {"Anita Morrall", "Kareena Assem", "Ashleigh McCourt", "Anthony Qiu", "Shauna Harley", 
			"Emmett O'Reilly", "N", "Claire Matthews", "Lee Sithisakd", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Simon Wardhaugh",
			"Carly Humphreys", "Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Leon Savelio", "Liesa Bruhn", "Marie Martin", 
			"Angela Attard", "Prashant Singh", "Peter Becvarovski", "Matthew Collett", "Clare Binns", "Ayla Aydin", "Ryan Anderson-Smith",
			"Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Angela Gray", "Kirsten Nuttall", "Richard Roberts", "Ramkumar Hariram", 
			"Janet Mataia", "Matthew Vincent", "Shane Muddell", "Mark Smeaton", "Koshi George", "Natalie Doyle", "Mili Kapur", "Helen Georgiou", 
			"Deborah Holl", "Michi Hollis", "Tania Whitbread", "Mark Jeffrey"};
	
	  String[] conductFileReviewResources = new String[] {"Anita Morrall", "Sue Wharley", "Ryan Finn", "Ashleigh McCourt", "Shauna Harley", 
			"Emmett O'Reilly", "Patricia Perri", "Andrei Bennett", "Simon Wardhaugh", "Bill Burmester", "Treesa McInnes", 
			"Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Clare Binns", "Ayla Aydin", "Rhiannon Merchant", 
			"Ryan Anderson-Smith", "Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Ramkumar Hariram", "Karen Davis", 
			"Matthew Gray", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", "Helen Georgiou", "Kristian O'Keefe",
			"Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Andrea Gynn", "Eloise McCarthy", "Kareena Assem", "Lynette Braddock",
			"Anthony Qiu", "Glenda McTavish", "Shantel Netzler", "Luke Patrick", "Kavita Krishna", "Nana Ansuh Yeboah", "Stephanie King",
			"Carly Humphreys", "Bradley Warren", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn", "Marie Martin", "Angela Attard", 
			"Michael Jessop", "Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle",
			"Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Matthew Vincent", "Janet Mataia", "Shane Muddell", "Nathan de Pratt", 
			"Mili Kapur", "Anne Nguyen", "Kevin Murphy", "Michelle Ebsworth", "George Healy", "Deborah Holl", "Michi Hollis", 
			"Justine Meniconi", "Jessica Borchard", "Mark Jeffrey"};
	
	  String[] newClaimIPIResources = new String[] {"Shauna Harley", "Emmett O'Reilly", "Abhishek Jain", "Simon Wardhaugh", "Kirralee Steward", 
			"Treesa McInnes", "Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Ayla Aydin", "Ryan Anderson-Smith", "Angela Gray",
			"Kirsten Nuttall", "Ramkumar Hariram", "Matthew Gray", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", 
			"Helen Georgiou", "Kristian O'Keefe", "Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Matthew Harvey", "Andrea Gynn", 
			"Kareena Assem", "Lynette Braddock", "Anthony Qiu", "Glenda McTavish", "N", "Claire Matthews", "Luke Patrick", "Kavita Krishna",
			"Carly Humphreys", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn", "Angela Attard", "Prashant Singh", "Daniel Aycock", 
			"Matthew Collett", "Vashti Keyes", "Natalie Gilmour", "Cherie Searle", "Michelle Talay", "Janet Mataia", "Matthew Vincent", 
			"Shane Muddell", "Nathan de Pratt", "Mili Kapur", "George Healy", "Deborah Holl", "Michi Hollis", "Justine Meniconi",
			"Allira Sandoval", "Jessica Borchard", "Mark Jeffrey"};
	
	  String[] followUpRequestedResources = new String[] {"Anita Morrall", "Ashleigh McCourt", "Shauna Harley", "Emmett O'Reilly", 
			"Patricia Perri", "Andrei Bennett", "Simon Wardhaugh", "Bill Burmester", "Kirralee Steward", "Treesa McInnes", 
			"Aaron Thirukumar", "Janelle Bantick", "Peter Becvarovski", "Jason Forbes", "Rhiannon Merchant", "Ayla Aydin", "Clare Binns",
			"Ryan Anderson-Smith", "Sara Davis", "Megan Creasey", "Angela Gray", "Kirsten Nuttall", "Debbie Hunter", "Ramkumar Hariram",
			"Karen Davis", "Koshi George", "Natalie Doyle", "Andrew Ejubs", "Susan Dunlop", "Nick Mann", "Helen Georgiou", 
			"Kristian O'Keefe", "Katie MClay", "Phil O'Donnell", "Tania Whitbread", "Andrea Gynn", "Eloise McCarthy", "Kareena Assem", 
			"Lynette Braddock", "Anthony Qiu", "Claire Matthews", "N", "Matthew Davis", "Shantel Netzler", "Luke Patrick", "Toni Emmett",
			"Kavita Krishna", "Nana Ansuh Yeboah", "Carly Humphreys", "Bradley Warren", "Aisling Fleming", "Leon Savelio", "Liesa Bruhn",
			"Marie Martin", "Angela Attard", "Prashant Singh", "Matthew Collett", "Douglas Heim", "Vashti Keyes", "Natalie Gilmour",
			"Cherie Searle", "Zachary Gibbs", "Michelle Talay", "Richard Roberts", "Shane Muddell", "Janet Mataia", "Matthew Vincent", 
			"Nathan de Pratt", "Mili Kapur", "Anne Nguyen", "Kevin Murphy", "Michelle Ebsworth", "George Healy", "Deborah Holl", 
			"Michi Hollis", "Justine Meniconi", "Jessica Borchard", "Mark Jeffrey"};
	  
	  String rolesEntry =   "<roles>";
	  String rolesExit = "</roles>";
	  String roleEntry = "<role id=\"";
	  String roleName = "\"><name>";
	  String roleEndName = "</name>";
	  String roleExit = "<description /><notes /></role>";
	  
	  StringBuffer sb = new StringBuffer();
	  
	  String participantsEntry =   "<participants>";
	  String participantsExit = "</participants>";
	  String participantEntry = "<participant id=\"";
	  String useridEntry = "<userid>";
	  String useridExit = "</userid>";
	  String userMiddle = "<firstname>Clark1</firstname><lastname>Clark1</lastname><description /><notes /><isAdministrator>false</isAdministrator><isAvailable>true</isAvailable>";
	  String participantRole = "<role>";
	  String participantRoleExit = "</role>";
	  String participantExit = "<positions /><capabilities /><privileges>11100000</privileges></participant>";
	  
	  sb.append("<orgdata>");
	  sb.append(participantsEntry);
	  
	  for(String p : resources) {
		  sb.append(participantEntry);
		  sb.append(p.replace(" ", "-"));
		  sb.append("\">");
		  sb.append(useridEntry);
		  sb.append(p);
		  sb.append(useridExit);
		  sb.append(userMiddle);
		  LinkedList<String> roles = new LinkedList<String>();
		  
		  for(String role : incomingCorrespondenResources) {
			  if(role.equals(p)) {
				  roles.add("incomingCorrespondenResources");
				  break;
			  }
		  }
			  
		  for(String role : authorisePaymentIPResources) {
			  if(role.equals(p)) {
				  roles.add("authorisePaymentIPResources");
				  break;
			  }
		  }

		  for(String role : generatePaymentResources) {
			  if(role.equals(p)) {
				  roles.add("generatePaymentResources");
				  break;
			  }
		  }

		  for(String role : processAdditionalInResources) {
			  if(role.equals(p)) {
				  roles.add("processAdditionalInResources");
				  break;
			  }
		  }

		  for(String role : closeFileResources) {
			  if(role.equals(p)) {
				  roles.add("closeFileResources");
				  break;
			  }
		  }

		  for(String role : contactCustomerResources) {
			  if(role.equals(p)) {
				  roles.add("contactCustomerResources");
				  break;
			  }
		  }

		  for(String role : conductFileReviewResources) {
			  if(role.equals(p)) {
				  roles.add("conductFileReviewResources");
				  break;
			  }
		  }

		  for(String role : newClaimIPIResources) {
			  if(role.equals(p)) {
				  roles.add("newClaimIPIResources");
				  break;
			  }
		  }

		  for(String role : followUpRequestedResources) {
			  if(role.equals(p)) {
				  roles.add("followUpRequestedResources");
				  break;
			  }
		  }
		  
		  if(roles.isEmpty()) {
			  sb.append("<roles />");
		  }else {
			  sb.append("<roles>");
			  for(String role : roles) {
				  sb.append(participantRole);
				  sb.append(role);
				  sb.append(participantRoleExit);
			  }
			  sb.append("</roles>");
		  }
		  
		  sb.append(participantExit);
		  sb.append("\n");
	  }

	  sb.append(participantsExit);
	  
	  sb.append(rolesEntry);
	  
	  sb.append(roleEntry);
	  sb.append("incomingCorrespondenResources");
	  sb.append(roleName);
	  sb.append("incomingCorrespondenResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("authorisePaymentIPResources");
	  sb.append(roleName);
	  sb.append("authorisePaymentIPResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);

	  sb.append(roleEntry);
	  sb.append("generatePaymentResources");
	  sb.append(roleName);
	  sb.append("generatePaymentResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("processAdditionalInResources");
	  sb.append(roleName);
	  sb.append("processAdditionalInResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("closeFileResources");
	  sb.append(roleName);
	  sb.append("closeFileResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("contactCustomerResources");
	  sb.append(roleName);
	  sb.append("contactCustomerResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("conductFileReviewResources");
	  sb.append(roleName);
	  sb.append("conductFileReviewResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("newClaimIPIResources");
	  sb.append(roleName);
	  sb.append("newClaimIPIResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(roleEntry);
	  sb.append("followUpRequestedResources");
	  sb.append(roleName);
	  sb.append("followUpRequestedResources");
	  sb.append(roleEndName);
	  sb.append(roleExit);
	  
	  sb.append(rolesExit);
	  sb.append("</orgdata>");
	  
	  System.out.println(JDOMUtil.formatXMLString(sb.toString()));
	}

	private static void convertGuard1() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			String s = "";
			try {
				s = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//		s = s.replace("incAmt", "#1(data)");
	//		s = s.replace("originalEstimate", "#2(data)");
	//		s = s.replace("manager", "#3(data)");
	//		s = s.replace("SYSSUBJECT", "#4(data)");
	//		s = s.replace("leader", "#5(data)");
	//		s = s.replace("ASSIGNEDTEAMNAME", "#6(data)");
	//		s = s.replace("lossPostcode", "#7(data)");
	//		s = s.replace("team", "#8(data)");
	//		s = s.replace("lossCause", "#9(data)");
	//		s = s.replace("lossState", "#10(data)");
	//		s = s.replace("productLine", "#11(data)");
	//		s = s.replace("brand", "#12(data)");
	//		s = s.replace("ASSIGNEDUSERMNAME", "#13(data)");
	
			s = s.replace("incAmt", "Double.parseDouble(map.get(\"incAmt\"))");
			s = s.replace("originalEstimate", "Double.parseDouble(map.get(\"originalEstimate\"))");
			s = s.replace("manager", "map.get(\"manager\")");
			s = s.replace("SYSSUBJECT", "map.get(\"SYSSUBJECT\")");
			s = s.replace("leader", "map.get(\"leader\")");
			s = s.replace("ASSIGNEDTEAMNAME", "map.get(\"ASSIGNEDTEAMNAME\")");
			s = s.replace("lossPostcode", "map.get(\"lossPostcode\")");
			s = s.replace("team", "map.get(\"team\")");
			s = s.replace("lossCause", "map.get(\"lossCause\")");
			s = s.replace("lossState", "map.get(\"lossState\")");
			s = s.replace("productLine", "map.get(\"productLine\")");
			s = s.replace("brand", "map.get(\"brand\")");
			s = s.replace("ASSIGNEDUSERMNAME", "map.get(\"ASSIGNEDUSERMNAME\")");
			
	//		s = s.replace("!=", "<>");
			s = s.replace("&#34;", "\"");
			s = s.replace("&amp;&amp;", "&&");
	//		s = s.replace("||", "orelse");
	//		s = s.replace("==", "=");
			s = s.replace("&lt;", "<");
			s = s.replace("&gt;", ">");
			System.out.println(s);
		}
	}
	
	private static void convertGuard() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			String s = "";
			try {
				s = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//		s = s.replace("incAmt", "#1(data)");
	//		s = s.replace("originalEstimate", "#2(data)");
	//		s = s.replace("manager", "#3(data)");
	//		s = s.replace("SYSSUBJECT", "#4(data)");
	//		s = s.replace("leader", "#5(data)");
	//		s = s.replace("ASSIGNEDTEAMNAME", "#6(data)");
	//		s = s.replace("lossPostcode", "#7(data)");
	//		s = s.replace("team", "#8(data)");
	//		s = s.replace("lossCause", "#9(data)");
	//		s = s.replace("lossState", "#10(data)");
	//		s = s.replace("productLine", "#11(data)");
	//		s = s.replace("brand", "#12(data)");
	//		s = s.replace("ASSIGNEDUSERMNAME", "#13(data)");
	
			s = s.replace("mapData.get(\"manager\") == ", "mapData.get(\"manager\").equals(");
			s = s.replace("mapData.get(\"SYSSUBJECT\") == ", "mapData.get(\"SYSSUBJECT\").equals(");
			s = s.replace("mapData.get(\"leader\") == ", "mapData.get(\"leader\").equals(");
			s = s.replace("mapData.get(\"ASSIGNEDTEAMNAME\") == ", "mapData.get(\"ASSIGNEDTEAMNAME\").equals(");
			s = s.replace("mapData.get(\"lossPostcode\") == ", "mapData.get(\"lossPostcode\").equals(");
			s = s.replace("mapData.get(\"team\") == ", "mapData.get(\"team\").equals(");
			s = s.replace("mapData.get(\"lossCause\") == ", "mapData.get(\"lossCause\").equals(");
			s = s.replace("mapData.get(\"lossState\") == ", "mapData.get(\"lossState\").equals(");
			s = s.replace("mapData.get(\"productLine\") == ", "mapData.get(\"productLine\").equals(");
			s = s.replace("mapData.get(\"brand\") == ", "mapData.get(\"brand\").equals(");
			s = s.replace("mapData.get(\"ASSIGNEDUSERMNAME\") == ", "mapData.get(\"ASSIGNEDUSERMNAME\").equals(");
			
			s = s.replace("mapData.get(\"manager\") != ", "!mapData.get(\"manager\").equals(");
			s = s.replace("mapData.get(\"SYSSUBJECT\") != ", "!mapData.get(\"SYSSUBJECT\").equals(");
			s = s.replace("mapData.get(\"leader\") != ", "!mapData.get(\"leader\").equals(");
			s = s.replace("mapData.get(\"ASSIGNEDTEAMNAME\") != ", "!mapData.get(\"ASSIGNEDTEAMNAME\").equals(");
			s = s.replace("mapData.get(\"lossPostcode\") != ", "!mapData.get(\"lossPostcode\").equals(");
			s = s.replace("mapData.get(\"team\") != ", "!mapData.get(\"team\").equals(");
			s = s.replace("mapData.get(\"lossCause\") != ", "!mapData.get(\"lossCause\").equals(");
			s = s.replace("mapData.get(\"lossState\") != ", "!mapData.get(\"lossState\").equals(");
			s = s.replace("mapData.get(\"productLine\") != ", "!mapData.get(\"productLine\").equals(");
			s = s.replace("mapData.get(\"brand\") != ", "!mapData.get(\"brand\").equals(");
			s = s.replace("mapData.get(\"ASSIGNEDUSERMNAME\") != ", "!mapData.get(\"ASSIGNEDUSERMNAME\").equals(");
			
			s = s.replace(" || ", ") ||\n");
			s = s.replace(" && ", ") &&\n");

			System.out.println();
			System.out.println(s);
		}
	}

	private static void discoverTeamName(String input) throws Exception {
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();

			int b = 0;
			for(XEvent event : trace) {
				
				String team = ((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTEAMNAME")).getValue();
				String resource = ((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTOUSERNAME")).getValue();
				
				System.out.println("\""+resource+"\",\""+team+"\"");
				
			}

		}
		
	}

	public static void discoverDuration(String input) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		
		XLogInfo info = XLogInfoFactory.createLogInfo(log);
		
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		
		HashMap<String, LinkedList<Long>> duration = new HashMap<String, LinkedList<Long>>();
		for(XTrace trace : log) {

			HashSet<Integer> usedEnd = new HashSet<Integer>();
			
			for(int i = 0; i < trace.size(); i++) {
			
				XEvent start = trace.get(i);
				if(xle.extractStandardTransition(start).equals("complete")) continue;
				
				for(int j = i; j < trace.size(); j++) {
					
					XEvent end = trace.get(j);
					if(usedEnd.contains(j)) continue;
					if(xle.extractStandardTransition(start).equals("start")) continue;
					if(!xce.extractName(start).equals(xce.extractName(end))) continue;
						
					long time = xte.extractTimestamp(end).getTime() - xte.extractTimestamp(start).getTime();
					
					String name = xce.extractName(start);
					
					if(time > 60000) {
						if(duration.containsKey(name)) {
							duration.get(name).add(time);
						}else {
							LinkedList<Long> list = new LinkedList<Long>();
							list.add(time);
							duration.put(name, list);
						}
					}
				}
			}
		}
		
		long average = 0L;
		long min = Long.MAX_VALUE;
		long max = 0L;
		
		HashMap<String, Double> averages = new HashMap<String, Double>();
		
		for(Entry<String, LinkedList<Long>> entry : duration.entrySet()) {
//			System.out.print(entry.getKey()+",");
			int count = 1;
			for(Long time : entry.getValue()) {
//				System.out.print(time);
//				if(count < entry.getValue().size()) {
//					System.out.print(",");
//				}
//				min = Math.min(min, time);
//				max = Math.max(max, time);
				average += time;
				count++;
			}
//			System.out.println(average / count + ", " + max + ", " + min);
			averages.put(entry.getKey(), ((double) average) / count);
			average = 0L;
//			min = Long.MAX_VALUE;
//			max = 0L;
		}
		
		double std = 0.0;
		
		for(Entry<String, LinkedList<Long>> entry : duration.entrySet()) {
			System.out.print(entry.getKey()+", ");
			int count = 1;
			for(Long time : entry.getValue()) {
//				System.out.print(time);
//				if(count < entry.getValue().size()) {
//					System.out.print(",");
//				}
				
				min = Math.min(min, time);
				max = Math.max(max, time);
				average += time;
				std += Math.sqrt(Math.pow(averages.get(entry.getKey()) - time, 2));
				count++;
			}
			System.out.println(average / count  + ", " + std / averages.get(entry.getKey()) + ", " + max + ", " + min);
			min = Long.MAX_VALUE;
			average = 0L;
			max = 0L;
			std = 0.0;
		}
		
	}
	
	public static void discoverResourceUse(String input) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		
		XLogInfo info = XLogInfoFactory.createLogInfo(log);
		
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		
		HashMap<String, HashSet<String>> resourceUse = new HashMap<String, HashSet<String>>();
		for(XTrace trace : log) {

			for(XEvent event : trace) {
				
				String name = xce.extractName(event);
				String resource = xoe.extractResource(event);
				if(resourceUse.containsKey(name)) {
					resourceUse.get(name).add(resource);
				}else {
					HashSet<String> set = new HashSet<String>();
					set.add(resource);
					resourceUse.put(name, set);
				}
					
			}
		}
		
		System.out.print("colset RESOURCES = subset STRING with [");
		int count = 1;
		for(XEventClass res : info.getResourceClasses().getClasses()) {
			System.out.print("\""+res.getId()+"\"");
			if(count < info.getResourceClasses().getClasses().size()) {
				System.out.print(", ");
			}
			count++;
		}
		System.out.print("];");
		System.out.println();
		
		for(Entry<String, HashSet<String>> entry : resourceUse.entrySet()) {
			count = 1;
			System.out.print("colset "+entry.getKey().replace(" ", "")+"Resources = subset RESOURCES with [");
			for(String res : entry.getValue()) {
				System.out.print("\""+res+"\"");
				if(count < entry.getValue().size()) {
					System.out.print(", ");
				}
				count++;
			}
			System.out.print("];");
			System.out.println();
		}
		
	}
	
	public static void removeSameEvent(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			
			for(XEvent event : trace) {
				
				if(last == null) {
					traceNew.add(event);
					last = event;
				}else {
					if(xce.extractName(event).equals(xce.extractName(last)) && xle.extractStandardTransition(event).equals(xle.extractStandardTransition(last))) {
						
					}else {

						traceNew.add(event);
						last = event;
					}
				}
				
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void calculateUtilization(String input) throws Exception {
		int incomingTime = 96552;
		int authoriseTime = 53602;
		int generateTime = 55742;
		int addInfoTime = 124737;
		int closeTime = 44556;
		int contactTime = 44442;
		int conductTime = 90347;
		int newClaimTime = 573;
		int followTime = 85839;
		
		String newClaim = "New_Claim_3";
		String incoming = "Incoming_Correspondence_5";
		String conduct = "Conduct_File_Review_4";
		String contact = "Contact_Customer_6";
		String addInfo = "Process_Add_Info_7";
		String authorise = "Authorize_Payment_15";
		String generate = "Generate_Payment_14";
		String follow = "Follow_Up_Requested_20";
		String close = "Close_File_22";
		
		Long start = null;
		Long end = null;
		int activities = 0;
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<String, Integer> perfomedMap = new HashMap<String, Integer>();
		HashMap<String, Long> free = new HashMap<String, Long>();
		HashMap<String, Long> lastFree = new HashMap<String, Long>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		for(XTrace trace : log) {
			for(XEvent event : trace) {
				String act = xce.extractName(event);
				String res = xoe.extractResource(event);
				long ts = xte.extractTimestamp(event).getTime();
				
				if(start == null) start = ts;
				else if(start > ts) start = ts;
				if(end == null) end = ts;
				else if(end < ts) end = ts;
				activities++;

				Integer time = null;
				Integer perfomed = null;
				Long f = null;
				Long lf = null;
				
				if((time = map.get(res)) == null) {
					time = 0;
				}
				if((perfomed = perfomedMap.get(res)) == null) {
					perfomed = 0;
				}
				if((f = free.get(res)) == null) {
					f = 0L;
				}
				if((lf = lastFree.get(res)) == null) {
					lf = 0L;
				}
				
				perfomedMap.put(res, perfomed+1);
				long dur = ts - lf;
				
				lastFree.put(res, ts);
				
				int ref = 0;
				
				if(act.equals(newClaim)) {
					ref = newClaimTime;
				}else if(act.equals(incoming)) {
					ref = incomingTime;
				}else if(act.equals(conduct)) {
					ref = conductTime;
				}else if(act.equals(contact)) {
					ref = contactTime;
				}else if(act.equals(addInfo)) {
					ref = addInfoTime;
				}else if(act.equals(authorise)) {
					ref = authoriseTime;
				}else if(act.equals(generate)) {
					ref = generateTime;
				}else if(act.equals(follow)) {
					ref = followTime;
				}else if(act.equals(close)) {
					ref = closeTime;
				}
				
				time += ref;
				if(dur - ref > 0) f += (dur - ref);
				
				map.put(res, time);
				free.put(res, f);
			}
		}
		
		for(Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " : Busy : " + entry.getValue() + " : Free : " + free.get(entry.getKey())+ " : Performed : " + perfomedMap.get(entry.getKey()));
		}
		System.out.println("Duration " + (end-start));
		System.out.println("activities " + activities);
	}
	
	public static void removeCreated(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();

			int b = 0;
			for(XEvent event : trace) {
				
				event.getAttributes().remove("CREATEDT");
				traceNew.add(event);
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void removeSameEventSameTimeDiffLife(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		
		HashSet<XEvent> events = new HashSet<XEvent>(); 
		
		for(XTrace trace : log) {

			HashSet<Integer> usedEnd = new HashSet<Integer>();
			
			for(int i = 0; i < trace.size(); i++) {
			
				XEvent start = trace.get(i);
				if(xle.extractStandardTransition(start).equals("complete")) continue;
				
				for(int j = i; j < trace.size(); j++) {
					
					XEvent end = trace.get(j);
					if(usedEnd.contains(j)) continue;
					if(xle.extractStandardTransition(start).equals("start")) continue;
					if(!xce.extractName(start).equals(xce.extractName(end))) continue;
						
					long time = xte.extractTimestamp(end).getTime() - xte.extractTimestamp(start).getTime();
					
					String name = xce.extractName(start);
					
					if(time > 60000) {
						events.add(start);
						events.add(end);
					}
				}
			}
		}
		
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();

			int b = 0;
			for(XEvent event : trace) {
				
//				if(last == null) {
//					traceNew.add(event);
//					last = event;
//				}else {
//					if(xce.extractName(event).equals(xce.extractName(last)) && xte.extractTimestamp(event).equals(xte.extractTimestamp(last))) {
//						
//					}else if(xce.extractName(event).equals(xce.extractName(last)) && !xte.extractTimestamp(event).equals(xte.extractTimestamp(last))) {
//					
//					}else {
//
//						traceNew.add(event);
//						last = event;
//					}
//				}
				if(events.contains(event)) {
					traceNew.add(event);
				}
				
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void removeSameEventSameTime(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();

			int b = 0;
			for(XEvent event : trace) {
				
				if(last == null) {
					traceNew.add(event);
					last = event;
				}else {
					if(xce.extractName(event).equals(xce.extractName(last)) && xte.extractTimestamp(event).equals(xte.extractTimestamp(last))) {
						
					}else {

						traceNew.add(event);
						last = event;
					}
				}
				
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void treeGenerator(String input) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		
		XLogInfo info = XLogInfoFactory.createLogInfo(log);
		Collection<XEventClass> classes = info.getNameClasses().getClasses();
		
		for(XEventClass eventClass : classes) {
			String transition = eventClass.toString();
					
			if(transition.equals("New Claim (IPI)")) continue;
			
			LinkedList<String> orderVar = new LinkedList<String>();
			
			HashMap<String, String> mapNames = new HashMap<String, String>();
			
			HashMap<String, String> results = new HashMap<String, String>();
			HashMap<String, Boolean> resultsCheck = new HashMap<String, Boolean>();
	
			String title = "";
			
			for(XTrace trace : log) {
				traceNew = (XTrace) trace.clone();
				traceNew.clear();
				String s = null;
				
				mapNames.clear();
				for(XEvent event : trace) {
					Set<Entry<String, XAttribute>> entrySet = event.getAttributes().entrySet();
									
					if(orderVar.isEmpty()) {
						for(Entry<String, XAttribute> entry : entrySet) {
							if(!(entry.getKey().equals("lifecycle:transition") || entry.getKey().equals("org:resource") 
									|| entry.getKey().equals("concept:name") || entry.getKey().equals("time:timestamp"))) {
								orderVar.add(entry.getKey());
								title += entry.getKey()+",";
							}
						}
					}
					
					if(!xce.extractName(event).equals(transition)){
						for(Entry<String, XAttribute> entry : entrySet) {
							if(!(entry.getKey().equals("lifecycle:transition") || entry.getKey().equals("org:resource") 
									|| entry.getKey().equals("concept:name") || entry.getKey().equals("time:timestamp"))) {
								if(entry.getValue() instanceof XAttributeLiteral) {
									s = "\""+((XAttributeLiteral) entry.getValue()).getValue()+"\"";
								}else if(entry.getValue() instanceof XAttributeBoolean) {
									s = ""+((XAttributeBoolean) entry.getValue()).getValue();
								}else if(entry.getValue() instanceof XAttributeContinuous) {
									s = ""+((XAttributeContinuous) entry.getValue()).getValue();
								}else if(entry.getValue() instanceof XAttributeDiscrete) {
									s = ""+((XAttributeDiscrete) entry.getValue()).getValue();
								}
								mapNames.put(entry.getKey(), s);
							}
						}
					}else {
						String partial = "";
						for(String val : orderVar) {
							partial +=mapNames.get(val) + ",";
						}
						
						for(String guessVar : orderVar) {
							
							String r = results.get(guessVar);
							if(r == null) r = "";
							
							r += partial;
	
							XAttribute attribute = event.getAttributes().get(guessVar);
							
							if(attribute instanceof XAttributeLiteral) {
								s = "\""+((XAttributeLiteral) attribute).getValue()+"\"";
							}else if(attribute instanceof XAttributeBoolean) {
								s = ""+((XAttributeBoolean) attribute).getValue();
							}else if(attribute instanceof XAttributeContinuous) {
								s = ""+((XAttributeContinuous) attribute).getValue();
							}else if(attribute instanceof XAttributeDiscrete) {
								s = ""+((XAttributeDiscrete) attribute).getValue();
							}
							r += s +"\n";
							
							results.put(guessVar, r);
							if(resultsCheck.containsKey(guessVar)) {
								resultsCheck.put(guessVar, resultsCheck.get(guessVar) && mapNames.get(guessVar).equals(s));
							}else {
								if(mapNames.get(guessVar) != null) {
									resultsCheck.put(guessVar, mapNames.get(guessVar).equals(s));
								}else {
									resultsCheck.put(guessVar, s == null);
								}
							}
							
						}
					}
				}
			}
			
			J48 j48Tree = null;
			REPTree reptTree = null;
			CSVLoader loader = new CSVLoader();
		    Instances data = null;
		    InputStream stream = null;
		    ObjectOutputStream oos = null;
			
			Set<Entry<String, String>> entrySet = results.entrySet();
			for(Entry<String, String> entry : entrySet) {
				if(!resultsCheck.get(entry.getKey())) {
					String cvs = title+entry.getKey()+"_Class\n";
					cvs += entry.getValue();
					
					stream = new ByteArrayInputStream(cvs.getBytes("UTF-8"));
				    loader.setSource(stream);
				    data = loader.getDataSet();
				    data.setClassIndex(data.numAttributes() - 1);
				    
				    if(data.numDistinctValues(data.numAttributes() - 1) > 1) {
					    if(entry.getKey().equals("incAmt") || entry.getKey().equals("originalEstimate")) {
					    	reptTree = new REPTree();
					    	reptTree.buildClassifier(data);
					    	
					    	oos = new ObjectOutputStream(new FileOutputStream(transition+"_REPTree_"+entry.getKey()));
					    	oos.writeObject(reptTree);
					    	
					    	oos = new ObjectOutputStream(new FileOutputStream(transition+"_Instances_"+entry.getKey()));
					    	oos.writeObject(data);
					    	
					    }else {
					    	j48Tree = new J48();
					    	j48Tree.buildClassifier(data);
					    	
					    	oos = new ObjectOutputStream(new FileOutputStream(transition+"_J48Tree_"+entry.getKey()));
					    	oos.writeObject(j48Tree);
					    	
					    	oos = new ObjectOutputStream(new FileOutputStream(transition+"_Instances_"+entry.getKey()));
					    	oos.writeObject(data);
					    }
					}else {
						new File(transition+"_SingleValue_"+entry.getKey()+"_"+data.attribute(data.numAttributes() - 1).value(0));
					}
				}
			}
		}
	}
	
	public static void dataGenerator(String input) throws Exception {
		HashMap<String, HashMap<String, LinkedList<String>>> map = new HashMap<String, HashMap<String, LinkedList<String>>>
		();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		
		XLogInfo info = XLogInfoFactory.createLogInfo(log);
		Collection<XEventClass> classes = info.getNameClasses().getClasses();
		
		for(XTrace trace : log) {
			
			for(XEvent event : trace) {
				
				HashMap<String, LinkedList<String>> subMap = null;
				
				if((subMap = map.get(xce.extractName(event))) == null) {
					subMap = new HashMap<String, LinkedList<String>>();
					map.put(xce.extractName(event), subMap);
				}
				
				Set<Entry<String, XAttribute>> entrySet = event.getAttributes().entrySet();
				
				LinkedList<String> list = null;
				
				String s = null;
				for(Entry<String, XAttribute> entry : entrySet) {
					if(!(entry.getKey().equals("lifecycle:transition") || entry.getKey().equals("org:resource") 
							|| entry.getKey().equals("concept:name") || entry.getKey().equals("time:timestamp"))) {
						
						s = null;
						if(entry.getValue() instanceof XAttributeLiteral) {
							s = ((XAttributeLiteral) entry.getValue()).getValue();
						}else if(entry.getValue() instanceof XAttributeBoolean) {
							s = ""+((XAttributeBoolean) entry.getValue()).getValue();
						}else if(entry.getValue() instanceof XAttributeContinuous) {
							s = ""+((XAttributeContinuous) entry.getValue()).getValue();
						}else if(entry.getValue() instanceof XAttributeDiscrete) {
							s = ""+((XAttributeDiscrete) entry.getValue()).getValue();
						}
						
						if((list = subMap.get(entry.getKey())) == null) {
							 list = new LinkedList<String>();
							 subMap.put(entry.getKey(), list);
						}
						list.add(s);
						
					}
				}
				
			}
			
		}
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("MAP"));
    	oos.writeObject(map);
			
	}
	
	public static void keepLastAuthPayGenPayClose(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XLifecycleExtension xle = XLifecycleExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			String s = null;
			String old = null;
			String newS = null;
			int openStart = 0;
			int authPayStart = 0;
			int genPayStart = 0;
			int closeStart = 0;

			int openEnd = 0;
			int authPayEnd = 0;
			int genPayEnd = 0;
			int closeEnd = 0;
			
			String value = "";
			for(XEvent event : trace) {
				if(xce.extractName(event).equals("New Claim (IPI)")) {
					if(xle.extractStandardTransition(event).equals("start")) {
						openStart++;
					}else {
						openEnd++;
					}
				}else if(xce.extractName(event).equals("Authorise Payment (IP")) {
					if(xle.extractStandardTransition(event).equals("start")) {
						authPayStart++;
					}else {
						authPayEnd++;
					}
				}else if (xce.extractName(event).equals("Generate Payment")) {
					if(xle.extractStandardTransition(event).equals("start")) {
						genPayStart++;
					}else {
						genPayEnd++;
					}
				}else if (xce.extractName(event).equals("Close File")) {
					if(xle.extractStandardTransition(event).equals("start")) {
						closeStart++;
					}else {
						closeEnd++;
					}
				}
			}
				
			int b0start = 0;
			int b1start = 0;
			int b2start = 0;
			int b3start = 0;

			int b0end = 0;
			int b1end = 0;
			int b2end = 0;
			int b3end = 0;
			
			for(XEvent event : trace) {
				if(openStart > 0 || authPayStart > 0 || genPayStart > 0 || closeStart > 0 || openEnd > 0 || authPayEnd > 0 || genPayEnd > 0 || closeEnd > 0) {
					if(xce.extractName(event).equals("New Claim (IPI)")) {
						if(xle.extractStandardTransition(event).equals("start")) {
							b0start++;
							if(b0start == 1) {
								traceNew.add(event);
							}
						}else {
							b0end++;
							if(b0end == 1) {
								traceNew.add(event);
							}
						}
					}else if(xce.extractName(event).equals("Authorise Payment (IP")) {
						if(xle.extractStandardTransition(event).equals("start")) {
							b1start++;
							if(b1start == authPayStart) {
								traceNew.add(event);
							}
						}else {
							b1end++;
							if(b1end == authPayEnd) {
								traceNew.add(event);
							}
						}
					}else if(xce.extractName(event).equals("Generate Payment")) {
						if(xle.extractStandardTransition(event).equals("start")) {
							b2start++;
							if(b2start == genPayStart) {
								traceNew.add(event);
							}
						}else {
							b2end++;
							if(b2end == genPayEnd) {
								traceNew.add(event);
							}
						}
					}else if(xce.extractName(event).equals("Close File")) {
						if(xle.extractStandardTransition(event).equals("start")) {
							b3start++;
							if(b3start == closeStart) {
								traceNew.add(event);
							}
						}else {
							b3end++;
							if(b3end == closeEnd) {
								traceNew.add(event);
							}
						}
					}else {
						traceNew.add(event);
					}
										
				}else {
					traceNew.add(event);
				}
			}
			logNew.add(traceNew);
		}
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void removeIncAmt(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			String s = null;
			String old = null;
			String newS = null;
			int conductFile = 0;
			int incomingCor = 0;
			int incomingCor2 = 0;
			String value = "";
			for(XEvent event : trace) {
				if(xce.extractName(event).equals("Authorise Payment (IP")) {
					incomingCor++;
					value = ((XAttributeLiteral) event.getAttributes().get("incAmt")).getValue();
				}else if (xce.extractName(event).equals("Generate Payment")) {
					incomingCor2++;
					value = ((XAttributeLiteral) event.getAttributes().get("incAmt")).getValue();
				}
			}
				

			int b = 0;
			int b2 = 0;
			for(XEvent event : trace) {
				if(incomingCor > 0 || incomingCor2 > 0) {
					if(xce.extractName(event).equals("Authorise Payment (IP")) {
						b++;
					}else if(xce.extractName(event).equals("Generate Payment")) {
						b2++;
					}
					
					if(b < incomingCor || b2 < incomingCor2) {
						((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
					}else {
						((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue(value);
					}
				}
				traceNew.add(event);
			}
			logNew.add(traceNew);
		}
//		String[] names = map.keySet().toArray(new String[0]);
//		for(Entry<String, Integer> entry : map.entrySet()) {
//			System.out.println(entry.getKey() + " ^ " + entry.getValue());
//		}
//		
//		for(int i = 0; i < names.length; i++) {
//			for(int j = i+1; j < names.length; j++) {
//				if(i != j) {
//					System.out.println(LevenshteinDistance.returnDistance(names[i], names[j]) + " ^ " + map.get(names[i]) + " ^ " + map.get(names[j]));
//				}
//			}
//		}
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void utiliseConductFile(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			String s = null;
			String old = null;
			String newS = null;
			int conductFile = 0;
			int incomingCor = 0;
			for(XEvent event : trace) {
				if(xce.extractName(event).equals("Conduct File Review")) {
					incomingCor++;
				}
			}

			int b = 0;
			for(XEvent event : trace) {
				if(incomingCor > 0) {
					if(xce.extractName(event).equals("Conduct File Review")) {
						b++;
					}
					for(int i = b; i < incomingCor; i++) {
						((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
						((XAttributeLiteral) event.getAttributes().get("originalEstimate")).setValue("0.0");
					}
				}
				traceNew.add(event);
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void utiliseIncoming(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			int incomingCor = 0;
			for(XEvent event : trace) {
				if(xce.extractName(event).equals("Incoming Corresponden")) {
					incomingCor++;
				}
			}
			if(incomingCor > 0) {
				if(incomingCor > 4) incomingCor = 4;
				String[] elements = new String[incomingCor];
				HashSet<Integer> set = new HashSet<Integer>();
				for(int i = 0; i < incomingCor; i++) {
					int a = -1;
					while(a == -1 || set.contains(a)) {
						a = r.nextInt(incomingCor);
					}
					switch(a) {
						case 0: elements[i] = "lossCause";
								break;
						case 1: elements[i] = "lossState";
								break;
						case 2: elements[i] = "lossPostcode";
								break;
						case 3: elements[i] = "brand";
								break;
					}
					set.add(a);					
				}
				int b = 0;
				for(XEvent event : trace) {
					if(xce.extractName(event).equals("Incoming Corresponden")) {
						b++;
					}
					for(int i = b; i < incomingCor; i++) {
						((XAttributeLiteral) event.getAttributes().get(elements[i])).setValue("null");
					}
					traceNew.add(event);
				}
			}
			logNew.add(traceNew);
		}
		
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
	public static void cleanData(String input, String output) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		XLog log = ImportEventLog.importFromStream(XFactoryRegistry.instance().currentDefault(), input);
		XLog logNew = (XLog) log.clone();
		logNew.clear();
		XTrace traceNew = null;
		XConceptExtension xce = XConceptExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XEvent last = null;
		Random r = new Random(123456789);
		for(XTrace trace : log) {
			traceNew = (XTrace) trace.clone();
			traceNew.clear();
			String s = null;
			String old = null;
			String newS = null;
			for(XEvent event : trace) {
				if(xce.extractName(event).equals("New Claim (IPI)")) {
					((XAttributeLiteral) event.getAttributes().get("department")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("SYSSUBJECT")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
					((XAttributeLiteral) event.getAttributes().get("manager")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("leader")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("lossPostcode")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("team")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("lossCause")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("lossState")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("originalEstimate")).setValue("0.0");
					((XAttributeLiteral) event.getAttributes().get("productLine6")).setValue("null");
					((XAttributeLiteral) event.getAttributes().get("brand")).setValue("null");
//				}else if(xce.extractName(event).equals("Conduct File Review")) {
//					((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("manager")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("leader")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTEAMNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("lossPostcode")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTOUSERNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("team")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossCause")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossState")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("originalEstimate")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("productLine6")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("brand")).setValue("null");
//				}else if(xce.extractName(event).equals("Incoming Corrisponden")) {
////					((XAttributeLiteral) event.getAttributes().get("department")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("SYSSUBJECT")).setValue("null");
//					((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("manager")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("leader")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTEAMNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("lossPostcode")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTOUSERNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("team")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossCause")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossState")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("originalEstimate")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("productLine6")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("brand")).setValue("null");
//				}else if(xce.extractName(event).equals("Process Additional In")) {
////					((XAttributeLiteral) event.getAttributes().get("department")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("SYSSUBJECT")).setValue("null");
//					((XAttributeLiteral) event.getAttributes().get("incAmt")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("manager")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("leader")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTEAMNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("lossPostcode")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("ASSIGNEDTOUSERNAME")).setValue("");
////					((XAttributeLiteral) event.getAttributes().get("team")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossCause")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("lossState")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("originalEstimate")).setValue("0.0");
////					((XAttributeLiteral) event.getAttributes().get("productLine6")).setValue("null");
////					((XAttributeLiteral) event.getAttributes().get("brand")).setValue("null");
				}
				traceNew.add(event);
			}
			logNew.add(traceNew);
		}
//		String[] names = map.keySet().toArray(new String[0]);
//		for(Entry<String, Integer> entry : map.entrySet()) {
//			System.out.println(entry.getKey() + " ^ " + entry.getValue());
//		}
//		
//		for(int i = 0; i < names.length; i++) {
//			for(int j = i+1; j < names.length; j++) {
//				if(i != j) {
//					System.out.println(LevenshteinDistance.returnDistance(names[i], names[j]) + " ^ " + map.get(names[i]) + " ^ " + map.get(names[j]));
//				}
//			}
//		}
		ImportEventLog.exportLog("/media/Data/SharedFolder/Commercial/", output, logNew);
	}
	
}
