package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.Map.Entry;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.OperationalSupportApp;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.OperationalSupportMultiCaseApp;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;

import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;


/**
 * 
 * @author vgehlot
 */

/* WSDL from http://www.webservicex.net/ws/WSDetails.aspx?CATID=2&WSID=10 */
public class JavCPNWebServiceExample {

	private static Random random = new Random(123456789);
	
	private static HashMap<String, Object> parameters = new HashMap<String, Object>();
	private static InterfaceManager a = null;
	
	private static XFactory xfactory = XFactoryRegistry.instance().currentDefault();
	
	private static File file = new File("/home/stormfire/ExperimentLog");
	private static FileWriter fw = null;
	
	private static String originPath = "/media/Data/SharedFolder/MXMLlogs/750/Log.xes";
	private static String newPath = "/media/Data/SharedFolder/MXMLlogs/750/tmp.xes";
	
	private static String specID = "";
	
//	private static XLog originalLog = null;
	
	public static void main(String[] args) throws Exception {
		
//		originalLog = ImportEventLog.importFromStream(xfactory, newPath);
		
		fw = new FileWriter(file, true);
		
		File file = new File("/media/Data/SharedFolder/MXMLlogs/750/LogOriginal.xes");
		File tmpFile = new File(originPath);
		copyFile(file, tmpFile);
		
		try {
		
			int port = 9000;
			JavaCPN conn = new JavaCPN();
			OperationalSupportMultiCaseApp oss = initializeMultiCase();
			
			String enabled = "enabled:";
			String started = "started:";
			String completed = "completed:";
			
			String request = "", responce = "";
			HashMap<String, Long> map1 = null;
			HashMap<WorkItemRecord, Long> map2 = null;
			
			HashMap<String, WorkItemRecord> mapNameWorkItem = new HashMap<String, WorkItemRecord>();
			
//			request = "resource:2001:Estimate_Trailer_Usage_386:Carrier_Appointment";
			// listen on port and accept connection
			System.out.println("service started");
			conn.accept(port);
			while (true) {
			
				request = EncodeDecode.decodeString(conn.receive());

				responce = "Done";
				
				System.out.println(request);
				updateParameters(originPath, newPath);
				
				if(request.startsWith(enabled)) {
					
					String taskString = request.substring(enabled.length());
					WorkItemRecord wir = generateWorkItem(request);
					
					mapNameWorkItem.put(taskString, wir);
											
					oss.receiveEvent(wir, true, false);
				
				}else if(request.startsWith(started)) {
										
					WorkItemRecord wir = mapNameWorkItem.get(request.substring(started.length()));
						
					oss.receiveEvent(wir, true, true);
				
				}else if(request.startsWith(completed)) {
					
					WorkItemRecord wir = mapNameWorkItem.get(request.substring(completed.length()));
					mapNameWorkItem.remove(request.substring(completed.length()));
						
					oss.receiveEvent(wir, false, false);
				
				}else {
					
					long currTime = Long.parseLong(request.substring(request.indexOf(":")+1));
					WorkItemRecord[] wirs = generateWorkItems(request.substring(request.indexOf(":")+1));
					
					Map<String, WorkItemRecord> schedule = oss.getOldSchedule(currTime);
					
					String resource = null;
					WorkItemRecord workItem = null;
					
					for(Entry<String, WorkItemRecord> entry : schedule.entrySet()) {
						boolean found = false;
						
						for(WorkItemRecord wir : wirs) {

							if(wir.equals(entry.getValue())) {
								found = true;
								break;
							}	
								
						}
						
						if(found) {
							resource = entry.getKey();
							workItem = entry.getValue();
							break;
						}
						
					}

					if(resource == null) {
						responce = "Wait";
					}else {
						responce = workItem.getTaskID()+":"+resource;
					}
					
				}
				
				conn.send(EncodeDecode.encode(responce));
				
			}
		
		}finally { 

			fw.close();
			
		}
	}
	
	public static void main2(String[] args) throws Exception {
		
//		originalLog = ImportEventLog.importFromStream(xfactory, newPath);
		
		fw = new FileWriter(file, true);
		
		File file = new File("/media/Data/SharedFolder/MXMLlogs/750/LogOriginal.xes");
		File tmpFile = new File(originPath);
		copyFile(file, tmpFile);
		
		try {
		
			int port = 9000;
			JavaCPN conn = new JavaCPN();
			OperationalSupportMultiCaseApp oss = initializeMultiCase();
			
			String enabled = "enabled:";
			String started = "started:";
			String completed = "completed:";
			
			String res = "resource:"; 
			String loop = "loop:";
			String request = "", responce = "";
			HashMap<String, Long> map1 = null;
			HashMap<WorkItemRecord, Long> map2 = null;
			
			HashMap<String, WorkItemRecord> mapNameWorkItem = new HashMap<String, WorkItemRecord>();
			
//			request = "resource:2001:Estimate_Trailer_Usage_386:Carrier_Appointment";
			// listen on port and accept connection
			System.out.println("service started");
			conn.accept(port);
			while (true) {
			
				request = EncodeDecode.decodeString(conn.receive());

				System.out.println(request);
				updateParameters(originPath, newPath);
				
				if(request.startsWith(res)) {
					
					
					String task = request.substring(res.length());
					String[] ws = identifyNextAndLastAndTime(";;"+task+";;");
					
					map1 = oss.suggestResourceDecisionPoints(generateWorkItem(ws[1]));
					System.out.println(map1);
	
					Long min = Long.MAX_VALUE;
					
					for(Entry<String, Long> e : map1.entrySet()) {
						
						if(e.getValue() < min) {
							min = e.getValue();
						}
						
					}

					Set<String> tmp = new HashSet<String>();
					for(Entry<String, Long> e : map1.entrySet()) {
						
						if(e.getValue().equals(min)) {
							tmp.add(e.getKey());
						}
						
					}

					boolean diff = (tmp.size() != map1.size());
					
//					if(map.keySet().size() > 1) {
						if(false && diff) {
							
							Set<String> set = map1.keySet();
							set.removeAll(tmp);
							String[] array = set.toArray(new String[0]);
							
							responce = array[random.nextInt(array.length)];
							
	//						System.out.println(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" notfollowed");
	//						System.out.println(map);
	//						System.out.println(r);
							fw.append(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" notfollowed\n");
							fw.flush();
							
						}else if(diff) {
							
							String[] array = tmp.toArray(new String[0]);
							
							responce = array[random.nextInt(array.length)];
							
	//						System.out.println(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" followed");
	//						System.out.println(map);
	//						System.out.println(r);
							fw.append(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" followed\n");
							fw.flush();
							
						}else {
							
							String[] array = tmp.toArray(new String[0]);
							
							responce = array[random.nextInt(array.length)];
						}
//					}
					
				}else if(request.startsWith(enabled)) {
					
					String workItemID = null;
					WorkItemRecord wir = null;
					
					String caseID = null;
					String taskID = null;
					
					if((wir = mapNameWorkItem.get(workItemID)) == null) {
						
						wir = new WorkItemRecord(caseID, taskID, specID, null, null);
						
					}
						
					oss.receiveEvent(wir, true, false);
				
				}else if(request.startsWith(started)) {
					
					String workItemID = null;
					WorkItemRecord wir = null;
					
					wir = mapNameWorkItem.get(workItemID);
						
					oss.receiveEvent(wir, true, true);
				
				}else if(request.startsWith(completed)) {
					
					String workItemID = null;
					WorkItemRecord wir = null;
					
					wir = mapNameWorkItem.get(workItemID);
					mapNameWorkItem.remove(workItemID);
						
					oss.receiveEvent(wir, false, false);
				
				}else {
					
					boolean keepLoop = false;
					if(request.startsWith(loop)) {
						keepLoop = true;
						request = request.substring(loop.length());
					}
					
					String[] ws = identifyNextAndLastAndTime(request);
					
					map2 = oss.suggestDecisionPoints(generateWorkItems(ws[0]), null, Long.parseLong(ws[2]));
					System.out.println(map2);
					
					Long min = Long.MAX_VALUE;
					
					for(Entry<WorkItemRecord, Long> e : map2.entrySet()) {
						
						if(e.getValue() < min) {
							min = e.getValue();
						}
						
					}

					Set<WorkItemRecord> tmp = new HashSet<WorkItemRecord>();
					for(Entry<WorkItemRecord, Long> e : map2.entrySet()) {
						
						if(e.getValue().equals(min)) {
							tmp.add(e.getKey());
						}
						
					}

					boolean diff = tmp.size() != map2.size();
					
//					if(map.keySet().size() > 1) {
						if(false && diff) {
							
							Set<WorkItemRecord> set = map2.keySet();
							set.removeAll(tmp);
							WorkItemRecord[] array = set.toArray(new WorkItemRecord[0]);
							
							responce = array[random.nextInt(array.length)].getTaskID();
							
	//						System.out.println(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" notfollowed");
	//						System.out.println(map);
	//						System.out.println(r);
							fw.append(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" notfollowed\n");
							fw.flush();
							
						}else if(diff) {

							WorkItemRecord[] array = tmp.toArray(new WorkItemRecord[0]);
							
							if(keepLoop && tmp.size()>1) {
								for(WorkItemRecord t : array) {
									if(!t.getTaskID().equals("Produce_Shipment_Notice_401")) {
										responce = t.getTaskID();
										break;
									}
								}
							}else {

								responce = array[random.nextInt(array.length)].getTaskID();
								
							}
							
	//						System.out.println(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" followed");
	//						System.out.println(map);
	//						System.out.println(r);
							fw.append(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" followed\n");
							fw.flush();
							
						}else {
							
							WorkItemRecord[] array = tmp.toArray(new WorkItemRecord[0]);
							
							if(keepLoop && tmp.size()>1) {
								for(WorkItemRecord t : array) {
									if(!t.getTaskID().equals("Produce_Shipment_Notice_401")) {
										responce = t.getTaskID();
										break;
									}
								}
							}else {

								responce = array[random.nextInt(array.length)].getTaskID();
								
							}
							
						}
//					}
				
				}
				
				conn.send(EncodeDecode.encode(responce));
				
			}
		
		}finally { 

			fw.close();
			
		}
	}
	
	private static boolean diffProb(Collection<Long> collection) {
		Long curr = null;
		boolean first = true;
		for(Long l : collection) {
			if(first) {
				curr = l;
				first = false;
			}
			if(curr != null && l != null && !curr.equals(l)) return true;
			if(curr == null && l != null) curr = l;
		}
		return false;
	}

	private static void fixLine(String originPath, String newPath) {
		
		File file = new File(originPath);
		
		File tmpFile = new File(newPath);
		
		copyFile(file, tmpFile);	
		
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
		
	}
	
	private static void copyFile(File sourceFile, File destFile) {
		try {
		    if(!destFile.exists()) {
		        destFile.createNewFile();
		    }else {
		    	destFile.delete();
		    	destFile.createNewFile();
		    }
	
		    FileChannel source = null;
		    FileChannel destination = null;
	
		    try {
		        source = new FileInputStream(sourceFile).getChannel();
		        destination = new FileOutputStream(destFile).getChannel();
		        
		        long count = 0;
		        long size = source.size();              
		        while((count += destination.transferFrom(source, count, size-count))<size);
		    }
		    finally {
		        if(source != null) {
		            source.close();
		        }
		        if(destination != null) {
		            destination.close();
		        }
		    }
		}catch(Exception e) {System.out.println("prob");}
	}
	
	private static void updateParameters(String originPath, String newPath) {
		
		fixLine(originPath, newPath);
		
		try {
			XLog log = ImportEventLog.importFromStream(xfactory, newPath);
			
//			originalLog.addAll(log);
			
			parameters.put("Xlog", log);
//			parameters.put("Xlog", originalLog);
			
			a.updateInterfaces(InterfaceManager.PROM, parameters);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String convertMap(HashMap<String, Long> map) {
		// TODO Auto-generated method stub
		return null;
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

	public static OperationalSupportApp initialize() {
		try {
			
			XLog log = ImportEventLog.importFromStream(xfactory, "/media/Data/SharedFolder/MXMLlogs/750/TestLog.xes");

			String specification = null;
			try {
				File f = new File("/home/stormfire/CarrierAppointment4.yawl");
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
			
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);
			
			a = new InterfaceManager(InterfaceManager.PROM, parameters);
			
			OperationalSupportApp oss = new OperationalSupportApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 5");
			
			oss.start(specID, null, null);
			
			return oss;
			
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		return null;
			
	}
	
	public static OperationalSupportMultiCaseApp initializeMultiCase() {
		try {
			
			XLog log = ImportEventLog.importFromStream(xfactory, "/media/Data/SharedFolder/MXMLlogs/750/TestLog.xes");

			String specification = null;
			try {
				File f = new File("/home/stormfire/CarrierAppointment4.yawl");
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
			
			parameters.put("Xlog", log);
			parameters.put("specification", specification);
			parameters.put("resources", resources);
			
			a = new InterfaceManager(InterfaceManager.PROM, parameters);
			
			OperationalSupportMultiCaseApp oss = new OperationalSupportMultiCaseApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 5", 100);
			
			oss.start(specID, null, null);
			
			return oss;
			
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		return null;
			
	}
	
	public static WorkItemRecord[] generateWorkItems(String request) {
		
		LinkedList<WorkItemRecord> listWorkItems = new LinkedList<WorkItemRecord>();
		
		StringTokenizer st = new StringTokenizer(request, ";");
		
		while(st.hasMoreTokens()) {
			
			String token = st.nextToken();
						
			listWorkItems.add(generateWorkItem(token));
			
		}
		
		return listWorkItems.toArray(new WorkItemRecord[0]);
		
	}
	
	public static WorkItemRecord generateWorkItem(String request) {
					
		StringTokenizer st1 = new StringTokenizer(request, ":");
		
		try {
			String caseID = st1.nextToken();
//			System.out.println(caseID);
			String taskID = st1.nextToken();
//			System.out.println(taskID);
			String specURI = st1.nextToken();
//			System.out.println(specURI);
			
			return new WorkItemRecord(caseID, taskID, specURI, null, null);
		}catch(NoSuchElementException nsee) {
			return null;
		}
		
	}
	
	public static String[] identifyNextAndLastAndTime(String request) {
		
		String[] res = new String[3];
		
		System.out.println("Request: "+request);
		res[0] = request.substring(0, request.indexOf(";;"));
		res[1] = request.substring(request.indexOf(";;")+2, request.lastIndexOf(";;"));
		res[2] = request.substring(request.lastIndexOf(";;")+2);
		
		return res;
		
	}

}
