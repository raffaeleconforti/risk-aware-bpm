import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.yawlfoundation.yawl.util.JDOMUtil;

public class Test {
	
	public static void main(String[] args) {
		String process = "Process2";
		int pro = 2;
		int limit = 9;
		int start = 1;
		printSpec(process, limit, pro, start);
		start = printData(process, limit, pro, start);
		
		process = "Process4";
		pro = 4;
		limit = 6;
		printSpec(process, limit, pro, start);
		start = printData(process, limit, pro, start);
		
		process = "Process5";
		pro = 5;
		limit = 4;
		printSpec(process, limit, pro, start);
		start = printData(process, limit, pro, start);
		
		process = "Process6";
		pro = 6;
		limit = 7;
		printSpec(process, limit, pro, start);
		start = printData(process, limit, pro, start);
	}
	
	public static void printSpec(String process, int limit, int pro, int count) {
		String specificationXMLa = null;
		String specificationXMLb = null;
		
		try {
			File f = new File("Creator/"+process+"a.yawl");
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
			specificationXMLa = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			File f = new File("Creator/"+process+"b.yawl");
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
			specificationXMLb = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] sensor = null;
		String sensor1 = null;
		String sensor2 = null;
		String sensor3 = null;
		String sensor4 = null;
		String sensor5 = null;
		String sensor6 = null;
		String sensor7 = null;
		String sensor8 = null;
		
		switch(pro) {
			case 2:
				sensor1 = "<sensor name=\"risk1\"><vars><var name=\"a11\" mapping=\"case(current).Fill Out Continuity Daily Report(StartTimeInMillis)\" type=\"\" /><var name=\"a12\" mapping=\"case(current).Fill Out Continuity Daily Report(TimeEstimationInMillis)\" type=\"\" /><var name=\"b11\" mapping=\"case(current).Fill Out Sound Sheets(StartTimeInMillis)\" type=\"\" /><var name=\"b12\" mapping=\"case(current).Fill Out Sound Sheets(TimeEstimationInMillis)\" type=\"\" /><var name=\"c11\" mapping=\"case(current).Fill Out Camera Sheets(isStarted)\" type=\"\" /><var name=\"c12\" mapping=\"case(current).Fill Out Camera Sheets(isCompleted)\" type=\"\" /><var name=\"d11\" mapping=\"case(current).Fill Out AD Report(isStarted)\" type=\"\" /><var name=\"d12\" mapping=\"case(current).Fill Out AD Report(isCompleted)\" type=\"\" /></vars><riskProbability>IF[((a11+a12)&lt;(b11+b12))&amp;((c11&amp;!c12)|(d11&amp;!d12))]THEN[0.9]ELSE[0.2]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor2 = "<sensor name=\"risk2\"><vars><var name=\"a21\" mapping=\"case(current).Fill Out Continuity Daily Report(allocateResource)\" type=\"\" /><var name=\"b21\" mapping=\"case(current).Fill Out Sound Sheets(allocateResource)\" type=\"\" /></vars><riskProbability>IF[a21==b21]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor3 = "<sensor name=\"risk3\"><vars><var name=\"a31\" mapping=\"case(current).Fill Out Continuity Daily Report(isStarted)\" type=\"\" /><var name=\"a32\" mapping=\"case(current).Fill Out Continuity Daily Report(isCompleted)\" type=\"\" /><var name=\"b31\" mapping=\"case(current).Fill Out Sound Sheets(isCompleted)\" type=\"\" /></vars><riskProbability>IF[a31&amp;!a32&amp;!b31]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor4 = "<sensor name=\"risk4\"><vars><var name=\"a41\" mapping=\"case(current).Revise Shooting Schedule(Count)\" type=\"\" /><var name=\"b41\" mapping=\"case(current).Input Shooting Schedule(CompleteTimeInMillis)\" type=\"\" /><var name=\"a42\" mapping=\"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\" type=\"\" /><var name=\"a43\" mapping=\"case(current).Revise Shooting Schedule(isCompleted)\" type=\"\" /><var name=\"a44\" mapping=\"case(current).Revise Shooting Schedule(isStarted)\" type=\"\" /><var name=\"a45\" mapping=\"case(current).Revise Shooting Schedule(StartTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a41&gt;5)&amp;(!a43&amp;a44)&amp;(a45-b41&gt;a42)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor5 = "<sensor name=\"risk5\"><vars><var name=\"a51\" mapping=\"case(current).Revise Shooting Schedule(PassTimeInMillis)\" type=\"\" /><var name=\"a52\" mapping=\"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\" type=\"\" /></vars><riskProbability>IF[a51+a52&gt;5]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor6 = "<sensor name=\"risk6\"><vars><var name=\"a61\" mapping=\"case(current).Input Cast List.callSheet\" type=\"\" /><var name=\"b61\" mapping=\"case(current).Input Crew List.timeSheetInfo\" type=\"\" /><var name=\"c61\" mapping=\"case(current).Fill Out Camera Sheets(allocateResource)\" type=\"\" /><var name=\"d61\" mapping=\"case(current).Fill Out Continuity Daily Report(allocateResource)\" type=\"\" /></vars><riskProbability>IF[!(a61==b61)&amp;!(c61==d61)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor7 = "<sensor name=\"risk7\"><vars><var name=\"a71\" mapping=\"case(current).Input Crew List.production\" type=\"\" /><var name=\"b71\" mapping=\"case(current).Input Cast List(PassTimeInMillis)\" type=\"\" /><var name=\"c71\" mapping=\"case(current).Input Shooting Schedule(PassTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a71==5)&amp;((b71+c71)&lt;20)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor8 = "<sensor name=\"risk8\"><vars><var name=\"a81\" mapping=\"case(current).Fill Out Continuity Report.producer\" type=\"\" /><var name=\"b81\" mapping=\"case(current).Fill Out Sound Sheets.production\" type=\"\" /><var name=\"c81\" mapping=\"case(current).Fill Out Camera Sheets.production\" type=\"\" /><var name=\"d81\" mapping=\"case(current).Fill Out AD Report.production\" type=\"\" /></vars><riskProbability>IF[!(c81==b81)&amp;(!(b81==a81)|!(b81==d81))]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
				
			case 4:
				sensor1 = "<sensor name=\"risk2\"><vars><var name=\"a21\" mapping=\"case(current).Initiate Shipment Status Inquiry(allocateResource)\" type=\"\" /><var name=\"b21\" mapping=\"case(current).Issue Trackpoint Notice(allocateResource)\" type=\"\" /></vars><riskProbability>IF[a21==b21]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor2 = "<sensor name=\"risk3\"><vars><var name=\"a31\" mapping=\"case(current).Initiate Shipment Status Inquiry(isStarted)\" type=\"\" /><var name=\"a32\" mapping=\"case(current).Initiate Shipment Status Inquiry(isCompleted)\" type=\"\" /><var name=\"b31\" mapping=\"case(current).Issue Trackpoint Notice(isCompleted)\" type=\"\" /></vars><riskProbability>IF[a31&amp;!a32&amp;!b31]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor3 = "<sensor name=\"risk5\"><vars><var name=\"a51\" mapping=\"case(current).Issue Trackpoint Notice(PassTimeInMillis)\" type=\"\" /><var name=\"a52\" mapping=\"case(current).Issue Trackpoint Notice(TimeEstimationInMillis)\" type=\"\" /></vars><riskProbability>IF[a51+a52&gt;5]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor4 = "<sensor name=\"risk6\"><vars><var name=\"a61\" mapping=\"case(current).Issue Trackpoint Notice.AcceptanceCertificate\" type=\"\" /><var name=\"b61\" mapping=\"case(current).Initiate Shipment Status Inquiry.TrackpointNotice\" type=\"\" /><var name=\"c61\" mapping=\"case(current).Log Trackpoint Order Entry(allocateResource)\" type=\"\" /><var name=\"d61\" mapping=\"case(current).Create Acceptance Certificate(allocateResource)\" type=\"\" /></vars><riskProbability>IF[!(a61==b61)&amp;!(c61==d61)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor5 = "<sensor name=\"risk7\"><vars><var name=\"a71\" mapping=\"case(current).Initiate Shipment Status Inquiry.Report\" type=\"\" /><var name=\"b71\" mapping=\"case(current).Issue Trackpoint Notice(PassTimeInMillis)\" type=\"\" /><var name=\"c71\" mapping=\"case(current).Log Trackpoint Order Entry(PassTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a71==5)&amp;((b71+c71)&lt;20)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
				
			case 5:
				sensor1 = "<sensor name=\"risk4\"><vars><var name=\"a41\" mapping=\"case(current).Approve Purchase Order(Count)\" type=\"\" /><var name=\"b41\" mapping=\"case(current).Create Purchase Order(CompleteTime)\" type=\"\" /><var name=\"a42\" mapping=\"case(current).Approve Purchase Order(TimeEstimationInMillis)\" type=\"\" /><var name=\"a43\" mapping=\"case(current).Approve Purchase Order(isCompleted)\" type=\"\" /><var name=\"a44\" mapping=\"case(current).Approve Purchase Order(isStarted)\" type=\"\" /><var name=\"a45\" mapping=\"case(current).Approve Purchase Order(StartTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a41&gt;5)&amp;(!a43&amp;a44)&amp;(a45+b41&gt;a42)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor2 = "<sensor name=\"risk5\"><vars><var name=\"a51\" mapping=\"case(current).Modify Purchase Order(PassTimeInMillis)\" type=\"\" /><var name=\"a52\" mapping=\"case(current).Modify Purchase Order(TimeEstimationInMillis)\" type=\"\" /></vars><riskProbability>IF[a51+a52&gt;5]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor3 = "<sensor name=\"risk7\"><vars><var name=\"a71\" mapping=\"case(current).Modify Purchase Order.POApproval\" type=\"\" /><var name=\"b71\" mapping=\"case(current).Approve Purchase Order(PassTimeInMillis)\" type=\"\" /><var name=\"c71\" mapping=\"case(current).Confirm Puchase Order(PassTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a71==5)&amp;((b71+c71)&lt;20)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor4 = "";
				sensor5 = "";
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
				
			case 6:
				sensor1 = "<sensor name=\"risk2\"><vars><var name=\"a21\" mapping=\"case(current).Produce Freight Invoice(allocateResource)\" type=\"\" /><var name=\"b21\" mapping=\"case(current).Issue Shipment Payment Order(allocateResource)\" type=\"\" /></vars><riskProbability>IF[a21==b21]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor2 = "<sensor name=\"risk3\"><vars><var name=\"a31\" mapping=\"case(current).Produce Freight Invoice(isStarted)\" type=\"\" /><var name=\"a32\" mapping=\"case(current).Produce Freight Invoice(isCompleted)\" type=\"\" /><var name=\"b31\" mapping=\"case(current).Issue Shipment Invoice(isCompleted)\" type=\"\" /></vars><riskProbability>IF[a31&amp;!a32&amp;!b31]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor3 = "<sensor name=\"risk4\"><vars><var name=\"a41\" mapping=\"case(current).Approve Shipment Payment Order(Count)\" type=\"\" /><var name=\"b41\" mapping=\"case(current).Issue Shipment Payment Order(CompleteTime)\" type=\"\" /><var name=\"a42\" mapping=\"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\" type=\"\" /><var name=\"a43\" mapping=\"case(current).Approve Shipment Payment Order(isCompleted)\" type=\"\" /><var name=\"a44\" mapping=\"case(current).Approve Shipment Payment Order(isStarted)\" type=\"\" /><var name=\"a45\" mapping=\"case(current).Approve Shipment Payment Order(StartTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a41&gt;5)&amp;(!a43&amp;a44)&amp;(a45+b41&gt;a42)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor4 = "<sensor name=\"risk5\"><vars><var name=\"a51\" mapping=\"case(current).Approve Shipment Payment Order(PassTimeInMillis)\" type=\"\" /><var name=\"a52\" mapping=\"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\" type=\"\" /></vars><riskProbability>IF[a51+a52&gt;5]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor5 = "<sensor name=\"risk6\"><vars><var name=\"a61\" mapping=\"case(current).Produce Freight Invoice.ShipmentPaymentOrder\" type=\"\" /><var name=\"b61\" mapping=\"case(current).Approve Shipment Payment Order.ShipmentPaymentOrder\" type=\"\" /><var name=\"c61\" mapping=\"case(current).Issue Shipment Invoice(allocateResource)\" type=\"\" /><var name=\"d61\" mapping=\"case(current).Issue Shipment Payment Order(allocateResource)\" type=\"\" /></vars><riskProbability>IF[!(a61==b61)&amp;!(c61==d61)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor6 = "<sensor name=\"risk7\"><vars><var name=\"a71\" mapping=\"case(current).Produce Freight Invoice.ShipmentPaymentOrder\" type=\"\" /><var name=\"b71\" mapping=\"case(current).Issue Shipment Payment Order(PassTimeInMillis)\" type=\"\" /><var name=\"c71\" mapping=\"case(current).Approve Shipment Payment Order(PassTimeInMillis)\" type=\"\" /></vars><riskProbability>IF[(a71==5)&amp;((b71+c71)&lt;20)]THEN[0.7]ELSE[0.3]</riskProbability><riskThreshold>0.5</riskThreshold><consequence>10</consequence></sensor>";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
		}
		
		for(int i = 1; i<limit; i++) {
			String spec = specificationXMLa+sensor[i]+specificationXMLb;
			
			if(!spec.contains("risk3")) {
			
			File f = new File(""+process+"/1 risks/Process"+count+" - "+pro+".yawl");
			try {
				FileWriter fw = new FileWriter(f);
    			fw.write(JDOMUtil.formatXMLString(spec));
    			fw.flush();
    			fw.close();
			} catch (IOException e1) {
				System.out.println("error");
				e1.printStackTrace();
			}
			
			count++;
			
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				String spec = specificationXMLa+sensor[i]+sensor[j]+specificationXMLb;
				
				if(!spec.contains("risk3")) {
				
				File f = new File(""+process+"/2 risks/Process"+count+" - "+pro+".yawl");
				try {
					FileWriter fw = new FileWriter(f);
        			fw.write(JDOMUtil.formatXMLString(spec));
        			fw.flush();
        			fw.close();
				} catch (IOException e1) {
					System.out.println("error");
					e1.printStackTrace();
				}
				
				count++;
				
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+specificationXMLb;
					
					if(!spec.contains("risk3")) {
					File f = new File(""+process+"/3 risks/Process"+count+" - "+pro+".yawl");
					try {
						FileWriter fw = new FileWriter(f);
	        			fw.write(JDOMUtil.formatXMLString(spec));
	        			fw.flush();
	        			fw.close();
					} catch (IOException e1) {
						System.out.println("error");
						e1.printStackTrace();
					}
					
					count++;
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+specificationXMLb;
						
						if(!spec.contains("risk3")) {
						File f = new File(""+process+"/4 risks/Process"+count+" - "+pro+".yawl");
						try {
							FileWriter fw = new FileWriter(f);
		        			fw.write(JDOMUtil.formatXMLString(spec));
		        			fw.flush();
		        			fw.close();
						} catch (IOException e1) {
							System.out.println("error");
							e1.printStackTrace();
						}
						
						count++;
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+specificationXMLb;
							
							if(!spec.contains("risk3")) {
							File f = new File(""+process+"/5 risks/Process"+count+" - "+pro+".yawl");
							try {
								FileWriter fw = new FileWriter(f);
			        			fw.write(JDOMUtil.formatXMLString(spec));
			        			fw.flush();
			        			fw.close();
							} catch (IOException e1) {
								System.out.println("error");
								e1.printStackTrace();
							}
							
							count++;
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+specificationXMLb;
								
								if(!spec.contains("risk3")) {
								File f = new File(""+process+"/6 risks/Process"+count+" - "+pro+".yawl");
								try {
									FileWriter fw = new FileWriter(f);
				        			fw.write(JDOMUtil.formatXMLString(spec));
				        			fw.flush();
				        			fw.close();
								} catch (IOException e1) {
									System.out.println("error");
									e1.printStackTrace();
								}
								
								count++;
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]+specificationXMLb;

									if(!spec.contains("risk3")) {
									File f = new File(""+process+"/7 risks/Process"+count+" - "+pro+".yawl");
									try {
										FileWriter fw = new FileWriter(f);
					        			fw.write(JDOMUtil.formatXMLString(spec));
					        			fw.flush();
					        			fw.close();
									} catch (IOException e1) {
										System.out.println("error");
										e1.printStackTrace();
									}
									
									count++;
									}
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									for(int b = c+1; b<limit; b++) {
										String spec = specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]+sensor[b]+specificationXMLb;
										
										if(!spec.contains("risk3")) {
										File f = new File(""+process+"/8 risks/Process"+count+" - "+pro+".yawl");
										try {
											FileWriter fw = new FileWriter(f);
						        			fw.write(JDOMUtil.formatXMLString(spec));
						        			fw.flush();
						        			fw.close();
										} catch (IOException e1) {
											System.out.println("error");
											e1.printStackTrace();
										}
										
										count++;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static int printData(String process, int limit, int pro, int count) {
		String specificationXMLa = null;
		String specificationXMLb = null;
		
		try {
			File f = new File("Creator/"+process+"a.data");
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
			specificationXMLa = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			File f = new File("Creator/"+process+"b.data");
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
			specificationXMLb = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] sensor = null;
		String sensor1 = null;
		String sensor2 = null;
		String sensor3 = null;
		String sensor4 = null;
		String sensor5 = null;
		String sensor6 = null;
		String sensor7 = null;
		String sensor8 = null;
		
		switch(pro) {
			case 2:
				sensor1 = "\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(StartTimeInMillis)\", \"10\");\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(TimeEstimationInMillis)\", \"20\");\n" +
						"		variables.put(\"case(current).Fill Out Sound Sheets(StartTimeInMillis)\", \"15\");\n" +
						"		variables.put(\"case(current).Fill Out Sound Sheets(TimeEstimationInMillis)\", \"30\");\n" +
						"		variables.put(\"case(current).Fill Out Camera Sheets(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Fill Out Camera Sheets(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Fill Out AD Report(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Fill Out AD Report(isCompleted)\", \"false\");\n" +
						"\n		" +
						"		mappingName.put(\"a11\", \"case(current).Fill Out Continuity Daily Report(StartTimeInMillis)\");\n" +
						"		mappingName.put(\"a12\", \"case(current).Fill Out Continuity Daily Report(TimeEstimationInMillis)\");\n" +
						"		mappingName.put(\"b11\", \"case(current).Fill Out Sound Sheets(StartTimeInMillis)\");\n" +
						"		mappingName.put(\"b12\", \"case(current).Fill Out Sound Sheets(TimeEstimationInMillis)\");\n" +
						"		mappingName.put(\"c11\", \"case(current).Fill Out Camera Sheets(isStarted)\");\n" +
						"		mappingName.put(\"c12\", \"case(current).Fill Out Camera Sheets(isCompleted)\");\n" +
						"		mappingName.put(\"d11\", \"case(current).Fill Out AD Report(isStarted)\");\n" +
						"		mappingName.put(\"d12\", \"case(current).Fill Out AD Report(isCompleted)\");\n";
				
				sensor2 = "\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(allocateResource)\", \"<A><allocate><participant id=\\\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\\\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Fill Out Sound Sheets(allocateResource)\", \"<A><allocate><participant id=\\\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\\\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a21\", \"case(current).Fill Out Continuity Daily Report(allocateResource)\");\n" +
						"		mappingName.put(\"b21\", \"case(current).Fill Out Sound Sheets(allocateResource)\");\n";
				
				sensor3 = "\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Fill Out Sound Sheets(isCompleted)\", \"false\");\n" +
						"\n" +
						"		mappingName.put(\"a31\", \"case(current).Fill Out Continuity Daily Report(isStarted)\");\n" +
						"		mappingName.put(\"a32\", \"case(current).Fill Out Continuity Daily Report(isCompleted)\");\n" +
						"		mappingName.put(\"b31\", \"case(current).Fill Out Sound Sheets(isCompleted)\");\n";
				
				sensor4 = "\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(Count)\", \"6\");\n" +
						"		variables.put(\"case(current).Input Shooting Schedule(CompleteTimeInMillis)\", \"10\");\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\", \"10\");\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(StartTimeInMillis)\", \"30\");\n" +
						"\n" +
						"		mappingName.put(\"a41\", \"case(current).Revise Shooting Schedule(Count)\");\n" +
						"		mappingName.put(\"b41\", \"case(current).Input Shooting Schedule(CompleteTimeInMillis)\");\n" +
						"		mappingName.put(\"a42\", \"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\");\n" +
						"		mappingName.put(\"a43\", \"case(current).Revise Shooting Schedule(isCompleted)\");\n" +
						"		mappingName.put(\"a44\", \"case(current).Revise Shooting Schedule(isStarted)\");\n" +
						"		mappingName.put(\"a45\", \"case(current).Revise Shooting Schedule(StartTimeInMillis)\");\n";
				
				sensor5 = "\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(PassTimeInMillis)\", \"6\");\n" +
						"		variables.put(\"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\", \"10\");\n" +
						"\n" +
						"		mappingName.put(\"a51\", \"case(current).Revise Shooting Schedule(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"a52\", \"case(current).Revise Shooting Schedule(TimeEstimationInMillis)\");\n";
				
				sensor6 = "\n" +
						"		variables.put(\"case(current).Input Cast List.callSheet\", \"a\");\n" +
						"		variables.put(\"case(current).Input Crew List.timeSheetInfo\", \"b\");\n" +
						"		variables.put(\"case(current).Fill Out Camera Sheets(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Fill Out Continuity Daily Report(allocateResource)\", \"<A><allocate><participant id=\\\"PA-0b7f1d4c-f3b2-4fd4-8e77-c33f65bb2821\\\"><userid>fc</userid><firstname>Fredo</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a61\", \"case(current).Input Cast List.callSheet\");\n" +
						"		mappingName.put(\"b61\", \"case(current).Input Crew List.timeSheetInfo\");\n" +
						"		mappingName.put(\"c61\", \"case(current).Fill Out Camera Sheets(allocateResource)\");\n" +
						"		mappingName.put(\"d61\", \"case(current).Fill Out Continuity Daily Report(allocateResource)\");\n";
				
				sensor7 = "\n" +
						"		variables.put(\"case(current).Input Crew List.production\", \"5\");\n" +
						"		variables.put(\"case(current).Input Cast List(PassTimeInMillis)\", \"5\");\n" +
						"		variables.put(\"case(current).Input Shooting Schedule(PassTimeInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a71\", \"case(current).Input Crew List.production\");\n" +
						"		mappingName.put(\"b71\", \"case(current).Input Cast List(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"c71\", \"case(current).Input Shooting Schedule(PassTimeInMillis)\");\n";
				
				sensor8 = "\n" +
						"		variables.put(\"case(current).Fill Out Continuity Report.producer\", \"1\");\n" +
						"		variables.put(\"case(current).Fill Out Sound Sheets.production\", \"1\");\n" +
						"		variables.put(\"case(current).Fill Out Camera Sheets.production\", \"5\");\n" +
						"		variables.put(\"case(current).Fill Out AD Report.production\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a81\", \"case(current).Fill Out Continuity Report.producer\");\n" +
						"		mappingName.put(\"b81\", \"case(current).Fill Out Sound Sheets.production\");\n" +
						"		mappingName.put(\"c81\", \"case(current).Fill Out Camera Sheets.production\");\n" +
						"		mappingName.put(\"d81\", \"case(current).Fill Out AD Report.production\");\n";
				
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
			
			case 4:
				sensor1 = "\n" +
						"		variables.put(\"case(current).Initiate Shipment Status Inquiry(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a21\", \"case(current).Initiate Shipment Status Inquiry(allocateResource)\");\n" +
						"		mappingName.put(\"b21\", \"case(current).Issue Trackpoint Notice(allocateResource)\");\n";
				
				sensor2 = "\n" +
						"		variables.put(\"case(current).Initiate Shipment Status Inquiry(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Initiate Shipment Status Inquiry(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice(isCompleted)\", \"false\");\n" +
						"\n" +
						"		mappingName.put(\"a31\", \"case(current).Initiate Shipment Status Inquiry(isStarted)\");\n" +
						"		mappingName.put(\"a32\", \"case(current).Initiate Shipment Status Inquiry(isCompleted)\");\n" +
						"		mappingName.put(\"b31\", \"case(current).Issue Trackpoint Notice(isCompleted)\");\n";
				
				sensor3 = "\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice(PassTimeInMillis)\", \"1\");\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice(TimeEstimationInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a51\", \"case(current).Issue Trackpoint Notice(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"a52\", \"case(current).Issue Trackpoint Notice(TimeEstimationInMillis)\");\n";
				
				sensor4 = "\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice.AcceptanceCertificate\", \"5\");\n" +
						"		variables.put(\"case(current).Initiate Shipment Status Inquiry.TrackpointNotice\", \"6\");\n" +
						"		variables.put(\"case(current).Log Trackpoint Order Entry(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Create Acceptance Certificate(allocateResource)\", \"<A><allocate><participant id=\\\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\\\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a61\", \"case(current).Issue Trackpoint Notice.AcceptanceCertificate\");\n" +
						"		mappingName.put(\"b61\", \"case(current).Initiate Shipment Status Inquiry.TrackpointNotice\");\n" +
						"		mappingName.put(\"c61\", \"case(current).Log Trackpoint Order Entry(allocateResource)\");\n" +
						"		mappingName.put(\"d61\", \"case(current).Create Acceptance Certificate(allocateResource)\");\n";
				
				sensor5 = "\n" +
						"		variables.put(\"case(current).Initiate Shipment Status Inquiry.Report\", \"5\");\n" +
						"		variables.put(\"case(current).Issue Trackpoint Notice(PassTimeInMillis)\", \"5\");\n" +
						"		variables.put(\"case(current).Log Trackpoint Order Entry(PassTimeInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a71\", \"case(current).Initiate Shipment Status Inquiry.Report\");\n" +
						"		mappingName.put(\"b71\", \"case(current).Issue Trackpoint Notice(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"c71\", \"case(current).Log Trackpoint Order Entry(PassTimeInMillis)\");\n";
				
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
				
			case 5:
				sensor1 = "\n" +
						"		variables.put(\"case(current).Approve Purchase Order(Count)\", \"6\");\n" +
						"		variables.put(\"case(current).Create Purchase Order(CompleteTime)\", \"1\");\n" +
						"		variables.put(\"case(current).Approve Purchase Order(TimeEstimationInMillis)\", \"5\");\n" +
						"		variables.put(\"case(current).Approve Purchase Order(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Approve Purchase Order(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Approve Purchase Order(StartTimeInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a41\", \"case(current).Approve Purchase Order(Count)\");\n" +
						"        mappingName.put(\"b41\", \"case(current).Create Purchase Order(CompleteTime)\");\n" +
						"        mappingName.put(\"a42\", \"case(current).Approve Purchase Order(TimeEstimationInMillis)\");\n" +
						"        mappingName.put(\"a43\", \"case(current).Approve Purchase Order(isCompleted)\");\n" +
						"        mappingName.put(\"a44\", \"case(current).Approve Purchase Order(isStarted)\");\n" +
						"        mappingName.put(\"a45\", \"case(current).Approve Purchase Order(StartTimeInMillis)\");\n";
				
				sensor2 = "\n" +
						"		variables.put(\"case(current).Modify Purchase Order(PassTimeInMillis)\", \"1\");\n" +
						"		variables.put(\"case(current).Modify Purchase Order(TimeEstimationInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a51\", \"case(current).Modify Purchase Order(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"a52\", \"case(current).Modify Purchase Order(TimeEstimationInMillis)\");\n";
				
				sensor3 = "\n" +
						"		variables.put(\"case(current).Modify Purchase Order.POApproval\", \"5\");\n" +
						"		variables.put(\"case(current).Approve Purchase Order(PassTimeInMillis)\", \"5\");\n" +
						"		variables.put(\"case(current).Confirm Puchase Order(PassTimeInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a71\", \"case(current).Modify Purchase Order.POApproval\");\n" +
						"		mappingName.put(\"b71\", \"case(current).Approve Purchase Order(PassTimeInMillis)\");\n" +
						"		mappingName.put(\"c71\", \"case(current).Confirm Puchase Order(PassTimeInMillis)\");\n";
				
				sensor4 = "";
				sensor5 = "";
				sensor6 = "";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
				
			case 6:
				sensor1 = "\n" +
						"		variables.put(\"case(current).Produce Freight Invoice(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Issue Shipment Payment Order(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a21\", \"case(current).Produce Freight Invoice(allocateResource)\");\n" +
						"		mappingName.put(\"b21\", \"case(current).Issue Shipment Payment Order(allocateResource)\");\n";
				
				sensor2 = "\n" +
						"		variables.put(\"case(current).Produce Freight Invoice(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Produce Freight Invoice(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Issue Shipment Invoice(isCompleted)\", \"false\");\n" +
						"\n" +
						"		mappingName.put(\"a31\", \"case(current).Produce Freight Invoice(isStarted)\");\n" +
						"		mappingName.put(\"a32\", \"case(current).Produce Freight Invoice(isCompleted)\");\n" +
						"		mappingName.put(\"b31\", \"case(current).Issue Shipment Invoice(isCompleted)\");\n";
				
				sensor3 = "\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order(Count)\", \"6\");\n" +
						"		variables.put(\"case(current).Issue Shipment Payment Order(CompleteTime)\", \"1\");\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\", \"5\");\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order(isCompleted)\", \"false\");\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order(isStarted)\", \"true\");\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order(StartTimeInMillis)\", \"5\");\n" +
						"\n" +
						"		mappingName.put(\"a41\", \"case(current).Approve Shipment Payment Order(Count)\");\n" +
						"        mappingName.put(\"b41\", \"case(current).Issue Shipment Payment Order(CompleteTime)\");\n" +
						"        mappingName.put(\"a42\", \"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\");\n" +
						"        mappingName.put(\"a43\", \"case(current).Approve Shipment Payment Order(isCompleted)\");\n" +
						"        mappingName.put(\"a44\", \"case(current).Approve Shipment Payment Order(isStarted)\");\n" +
						"        mappingName.put(\"a45\", \"case(current).Approve Shipment Payment Order(StartTimeInMillis)\");\n";
				
				sensor4 = "\n" +
						"        variables.put(\"case(current).Approve Shipment Payment Order(PassTimeInMillis)\", \"5\");\n" +
						"        variables.put(\"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\", \"5\");\n" +
						"\n" +
						"        mappingName.put(\"a51\", \"case(current).Approve Shipment Payment Order(PassTimeInMillis)\");\n" +
						"        mappingName.put(\"a52\", \"case(current).Approve Shipment Payment Order(TimeEstimationInMillis)\");\n";
				
				sensor5 = "\n" +
						"		variables.put(\"case(current).Produce Freight Invoice.ShipmentPaymentOrder\", \"a\");\n" +
						"		variables.put(\"case(current).Approve Shipment Payment Order.ShipmentPaymentOrder\", \"b\");\n" +
						"		variables.put(\"case(current).Issue Shipment Invoice(allocateResource)\", \"<A><allocate><participant id=\\\"PA-223a865e-9b16-4c0b-a496-4e445eaad1ec\\\"><userid>mac</userid><firstname>Mama</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"		variables.put(\"case(current).Issue Shipment Payment Order(allocateResource)\", \"<A><allocate><participant id=\\\"PA-3dad7d51-ffbd-4e02-aeba-462deac95ef8\\\"><userid>mc</userid><firstname>Michael</firstname><lastname>Corleone</lastname><isAdministrator>true</isAdministrator><roles></roles><positions></positions><capabilities></capabilities></participant></allocate></A>\");\n" +
						"\n" +
						"		mappingName.put(\"a61\", \"case(current).Produce Freight Invoice.ShipmentPaymentOrder\");\n" +
						"		mappingName.put(\"b61\", \"case(current).Approve Shipment Payment Order.ShipmentPaymentOrder\");\n" +
						"		mappingName.put(\"c61\", \"case(current).Issue Shipment Invoice(allocateResource)\");\n" +
						"		mappingName.put(\"d61\", \"case(current).Issue Shipment Payment Order(allocateResource)\");\n";
				
				sensor6 = "\n" +
						"        variables.put(\"case(current).Produce Freight Invoice.ShipmentPaymentOrder\", \"5\");\n" +
						"        variables.put(\"case(current).Issue Shipment Payment Order(PassTimeInMillis)\", \"5\");\n" +
						"        variables.put(\"case(current).Approve Shipment Payment Order(PassTimeInMillis)\", \"5\");\n" +
						"\n" +
						"        mappingName.put(\"a71\", \"case(current).Produce Freight Invoice.ShipmentPaymentOrder\");\n" +
						"        mappingName.put(\"b71\", \"case(current).Issue Shipment Payment Order(PassTimeInMillis)\");\n" +
						"        mappingName.put(\"c71\", \"case(current).Approve Shipment Payment Order(PassTimeInMillis)\");\n";
				sensor7 = "";
				sensor8 = "";
				sensor = new String[]{"", sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8};
				break;
		}
		
		for(int i = 1; i<limit; i++) {
			String intro = "package State.YAWL.Loaders;\n" +
					"import java.io.BufferedReader;\n" +
					"import java.io.File;\n" +
					"import java.io.FileInputStream;\n" +
					"import java.io.FileNotFoundException;\n" +
					"import java.io.IOException;\n" +
					"import java.io.InputStream;\n" +
					"import java.io.InputStreamReader;\n" +
					"import java.io.Reader;\n" +
					"import java.io.StringWriter;\n" +
					"import java.io.UnsupportedEncodingException;\n" +
					"import java.io.Writer;\n" +
					"import java.util.HashMap;\n" +
					"import java.util.LinkedList;\n" +
					"import java.util.List;\n" +
					"import java.util.Random;\n" +
					"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
					"import State.YAWL.Loader;" +
					"import State.YAWL.StateYAWLProcess;" +
					"import State.YAWL.Resource;\n" +
					"\n" +
					"public class ";
			
			String postIntro = " implements Loader{\n" +
					"	public StateYAWLProcess generateStatus(Random r) {\n" +
					"		String specificationXML = null;\n" +
					"		try {\n";

			String a = "			File f = new File(\""+process+"/1 risks/Process"+count+" - "+pro+".yawl\");\n";
			
			String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+specificationXMLb;
			
			File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
			try {
				FileWriter fw = new FileWriter(f);
    			fw.write(spec);
    			fw.flush();
    			fw.close();
			} catch (IOException e1) {
				System.out.println("error");
				e1.printStackTrace();
			}
			
			if(!spec.contains("a31"))	count++;
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				String intro = "package State.YAWL.Loaders;\n" +
						"import java.io.BufferedReader;\n" +
						"import java.io.File;\n" +
						"import java.io.FileInputStream;\n" +
						"import java.io.FileNotFoundException;\n" +
						"import java.io.IOException;\n" +
						"import java.io.InputStream;\n" +
						"import java.io.InputStreamReader;\n" +
						"import java.io.Reader;\n" +
						"import java.io.StringWriter;\n" +
						"import java.io.UnsupportedEncodingException;\n" +
						"import java.io.Writer;\n" +
						"import java.util.HashMap;\n" +
						"import java.util.LinkedList;\n" +
						"import java.util.List;\n" +
						"import java.util.Random;\n" +
						"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
						"import State.YAWL.Loader;" +
						"import State.YAWL.StateYAWLProcess;" +
						"import State.YAWL.Resource;\n" +
						"\n" +
						"public class ";
				
				String postIntro = " implements Loader{\n" +
						"	public StateYAWLProcess generateStatus(Random r) {\n" +
						"		String specificationXML = null;\n" +
						"		try {\n";

				String a = "			File f = new File(\""+process+"/2 risks/Process"+count+" - "+pro+".yawl\");\n";
				
				String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+specificationXMLb;
				
				if(!spec.contains("a31")) {
				File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
				try {
					FileWriter fw = new FileWriter(f);
        			fw.write(spec);
        			fw.flush();
        			fw.close();
				} catch (IOException e1) {
					System.out.println("error");
					e1.printStackTrace();
				}
				
				count++;
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					String intro = "package State.YAWL.Loaders;\n" +
							"import java.io.BufferedReader;\n" +
							"import java.io.File;\n" +
							"import java.io.FileInputStream;\n" +
							"import java.io.FileNotFoundException;\n" +
							"import java.io.IOException;\n" +
							"import java.io.InputStream;\n" +
							"import java.io.InputStreamReader;\n" +
							"import java.io.Reader;\n" +
							"import java.io.StringWriter;\n" +
							"import java.io.UnsupportedEncodingException;\n" +
							"import java.io.Writer;\n" +
							"import java.util.HashMap;\n" +
							"import java.util.LinkedList;\n" +
							"import java.util.List;\n" +
							"import java.util.Random;\n" +
							"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
							"import State.YAWL.Loader;" +
							"import State.YAWL.StateYAWLProcess;" +
							"import State.YAWL.Resource;\n" +
							"\n" +
							"public class ";
					
					String postIntro = " implements Loader{\n" +
							"	public StateYAWLProcess generateStatus(Random r) {\n" +
							"		String specificationXML = null;\n" +
							"		try {\n";

					String a = "			File f = new File(\""+process+"/3 risks/Process"+count+" - "+pro+".yawl\");\n";
					
					String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+specificationXMLb;
					
					if(!spec.contains("a31")) {
					File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
					try {
						FileWriter fw = new FileWriter(f);
	        			fw.write(spec);
	        			fw.flush();
	        			fw.close();
					} catch (IOException e1) {
						System.out.println("error");
						e1.printStackTrace();
					}
					
					count++;
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						String intro = "package State.YAWL.Loaders;\n" +
								"import java.io.BufferedReader;\n" +
								"import java.io.File;\n" +
								"import java.io.FileInputStream;\n" +
								"import java.io.FileNotFoundException;\n" +
								"import java.io.IOException;\n" +
								"import java.io.InputStream;\n" +
								"import java.io.InputStreamReader;\n" +
								"import java.io.Reader;\n" +
								"import java.io.StringWriter;\n" +
								"import java.io.UnsupportedEncodingException;\n" +
								"import java.io.Writer;\n" +
								"import java.util.HashMap;\n" +
								"import java.util.LinkedList;\n" +
								"import java.util.List;\n" +
								"import java.util.Random;\n" +
								"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
								"import State.YAWL.Loader;" +
								"import State.YAWL.StateYAWLProcess;" +
								"import State.YAWL.Resource;\n" +
								"\n" +
								"public class ";
				
						String postIntro = " implements Loader{\n" +
								"	public StateYAWLProcess generateStatus(Random r) {\n" +
								"		String specificationXML = null;\n" +
								"		try {\n";
		
						String a = "			File f = new File(\""+process+"/4 risks/Process"+count+" - "+pro+".yawl\");\n";
						
						String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+specificationXMLb;
						
						if(!spec.contains("a31")) {
						File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
						try {
							FileWriter fw = new FileWriter(f);
		        			fw.write(spec);
		        			fw.flush();
		        			fw.close();
						} catch (IOException e1) {
							System.out.println("error");
							e1.printStackTrace();
						}
						
						count++;
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							String intro = "package State.YAWL.Loaders;\n" +
									"import java.io.BufferedReader;\n" +
									"import java.io.File;\n" +
									"import java.io.FileInputStream;\n" +
									"import java.io.FileNotFoundException;\n" +
									"import java.io.IOException;\n" +
									"import java.io.InputStream;\n" +
									"import java.io.InputStreamReader;\n" +
									"import java.io.Reader;\n" +
									"import java.io.StringWriter;\n" +
									"import java.io.UnsupportedEncodingException;\n" +
									"import java.io.Writer;\n" +
									"import java.util.HashMap;\n" +
									"import java.util.LinkedList;\n" +
									"import java.util.List;\n" +
									"import java.util.Random;\n" +
									"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
									"import State.YAWL.Loader;" +
									"import State.YAWL.StateYAWLProcess;" +
									"import State.YAWL.Resource;\n" +
									"\n" +
									"public class ";
					
							String postIntro = " implements Loader{\n" +
									"	public StateYAWLProcess generateStatus(Random r) {\n" +
									"		String specificationXML = null;\n" +
									"		try {\n";
		
							String a = "			File f = new File(\""+process+"/5 risks/Process"+count+" - "+pro+".yawl\");\n";
							
							String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+specificationXMLb;
							
							if(!spec.contains("a31")) {
							File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
							try {
								FileWriter fw = new FileWriter(f);
			        			fw.write(spec);
			        			fw.flush();
			        			fw.close();
							} catch (IOException e1) {
								System.out.println("error");
								e1.printStackTrace();
							}
							
							count++;
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								String intro = "package State.YAWL.Loaders;\n" +
										"import java.io.BufferedReader;\n" +
										"import java.io.File;\n" +
										"import java.io.FileInputStream;\n" +
										"import java.io.FileNotFoundException;\n" +
										"import java.io.IOException;\n" +
										"import java.io.InputStream;\n" +
										"import java.io.InputStreamReader;\n" +
										"import java.io.Reader;\n" +
										"import java.io.StringWriter;\n" +
										"import java.io.UnsupportedEncodingException;\n" +
										"import java.io.Writer;\n" +
										"import java.util.HashMap;\n" +
										"import java.util.LinkedList;\n" +
										"import java.util.List;\n" +
										"import java.util.Random;\n" +
										"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
										"import State.YAWL.Loader;" +
										"import State.YAWL.StateYAWLProcess;" +
										"import State.YAWL.Resource;\n" +
										"\n" +
										"public class ";
						
								String postIntro = " implements Loader{\n" +
										"	public StateYAWLProcess generateStatus(Random r) {\n" +
										"		String specificationXML = null;\n" +
										"		try {\n";
		
								String a = "			File f = new File(\""+process+"/6 risks/Process"+count+" - "+pro+".yawl\");\n";
								
								String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+specificationXMLb;
								
								if(!spec.contains("a31")) {
								File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
								try {
									FileWriter fw = new FileWriter(f);
				        			fw.write(spec);
				        			fw.flush();
				        			fw.close();
								} catch (IOException e1) {
									System.out.println("error");
									e1.printStackTrace();
								}
								
								count++;
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									String intro = "package State.YAWL.Loaders;\n" +
											"import java.io.BufferedReader;\n" +
											"import java.io.File;\n" +
											"import java.io.FileInputStream;\n" +
											"import java.io.FileNotFoundException;\n" +
											"import java.io.IOException;\n" +
											"import java.io.InputStream;\n" +
											"import java.io.InputStreamReader;\n" +
											"import java.io.Reader;\n" +
											"import java.io.StringWriter;\n" +
											"import java.io.UnsupportedEncodingException;\n" +
											"import java.io.Writer;\n" +
											"import java.util.HashMap;\n" +
											"import java.util.LinkedList;\n" +
											"import java.util.List;\n" +
											"import java.util.Random;\n" +
											"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
											"import State.YAWL.Loader;" +
											"import State.YAWL.StateYAWLProcess;" +
											"import State.YAWL.Resource;\n" +
											"\n" +
											"public class ";
							
									String postIntro = " implements Loader{\n" +
											"	public StateYAWLProcess generateStatus(Random r) {\n" +
											"		String specificationXML = null;\n" +
											"		try {\n";
		
									String a = "			File f = new File(\""+process+"/7 risks/Process"+count+" - "+pro+".yawl\");\n";
									
									String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]+specificationXMLb;
									
									if(!spec.contains("a31")) {
									File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
									try {
										FileWriter fw = new FileWriter(f);
					        			fw.write(spec);
					        			fw.flush();
					        			fw.close();
									} catch (IOException e1) {
										System.out.println("error");
										e1.printStackTrace();
									}
									
									count++;
									}
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 1; i<limit; i++) {
			for(int j = i+1; j<limit; j++) {
				for(int k = j+1; k<limit; k++) {
					for(int h = k+1; h<limit; h++) {
						for(int g = h+1; g<limit; g++) {
							for(int d = g+1; d<limit; d++) {
								for(int c = d+1; c<limit; c++) {
									for(int b = c+1; b<limit; b++) {
										String intro = "package State.YAWL.Loaders;\n" +
												"import java.io.BufferedReader;\n" +
												"import java.io.File;\n" +
												"import java.io.FileInputStream;\n" +
												"import java.io.FileNotFoundException;\n" +
												"import java.io.IOException;\n" +
												"import java.io.InputStream;\n" +
												"import java.io.InputStreamReader;\n" +
												"import java.io.Reader;\n" +
												"import java.io.StringWriter;\n" +
												"import java.io.UnsupportedEncodingException;\n" +
												"import java.io.Writer;\n" +
												"import java.util.HashMap;\n" +
												"import java.util.LinkedList;\n" +
												"import java.util.List;\n" +
												"import java.util.Random;\n" +
												"import org.yawlfoundation.yawl.util.JDOMUtil;\n" +
												"import State.YAWL.Loader;" +
												"import State.YAWL.StateYAWLProcess;" +
												"import State.YAWL.Resource;\n" +
												"\n" +
												"public class ";
								
										String postIntro = " implements Loader{\n" +
												"	public StateYAWLProcess generateStatus(Random r) {\n" +
												"		String specificationXML = null;\n" +
												"		try {\n";
		
										String a = "			File f = new File(\""+process+"/8 risks/Process"+count+" - "+pro+".yawl\");\n";
										
										String spec = intro+"Process"+count+postIntro+a+specificationXMLa+sensor[i]+sensor[j]+sensor[k]+sensor[h]+sensor[g]+sensor[d]+sensor[c]+sensor[b]+specificationXMLb;
										
										if(!spec.contains("a31")) {
										File f = new File("src/State/YAWL/Loaders/Process"+count+".java");
										try {
											FileWriter fw = new FileWriter(f);
						        			fw.write(spec);
						        			fw.flush();
						        			fw.close();
										} catch (IOException e1) {
											System.out.println("error");
											e1.printStackTrace();
										}
										
										count++;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return count;
	}
	
}
