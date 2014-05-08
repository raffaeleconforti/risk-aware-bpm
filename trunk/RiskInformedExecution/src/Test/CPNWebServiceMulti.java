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
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.OperationalSupportApp;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.DurationSplitter;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.ILPBuilderSCIPNewMax;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.RiskFunction;
import org.yawlfoundation.yawl.riskPrediction.OperationalSupport.multicase.TupleClassifierName;
import org.yawlfoundation.yawl.sensors.databaseInterface.InterfaceManager;
import org.yawlfoundation.yawl.sensors.databaseInterface.ProM.ImportEventLog;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaEnumeration;

public class CPNWebServiceMulti {

	private static Random random = new Random(123456789);
	
	private static HashMap<String, HashMap<String, LinkedList<String>>> data = null;
	private static HashMap<String, String> dataValues = new HashMap<String, String>();
	
	private static HashMap<String, Object> parameters = new HashMap<String, Object>();
	private static HashMap<String, Object> parameters2 = new HashMap<String, Object>();
	private static HashMap<String, Object> parameters3 = new HashMap<String, Object>();
	private static InterfaceManager a = null;
	private static InterfaceManager b = null;
	private static InterfaceManager c = null;
	
	private static XFactory xfactory = null;
	
	private static FileWriter fw = null;
	
	private static String sunCorpLog = "/media/Data/SharedFolder/Commercial/FilteredCommercial15.xes";
	private static String originPath = "/media/Data/SharedFolder/Commercial/Log/Log.xes";
	
	private static String specID = "";
	
	private static boolean first = false;
	private static XLog originalLog = null;
	private static XLog log = null;

	private static OperationalSupportApp osa = null;
	
	public static String newClaim = "New_Claim_3";
	public static String incoming = "Incoming_Correspondence_5";
	public static String conduct = "Conduct_File_Review_4";
	public static String contact = "Contact_Customer_6";
	public static String process = "Process_Add_Info_7";
	public static String authorize = "Authorize_Payment_15";
	public static String generate = "Generate_Payment_14";
	public static String follow = "Follow_Up_Requested_20";
	public static String close = "Close_File_22";
	
	public static String incAmt = "incAmt";
	public static String originalEstimate = "originalEstimate";
	public static String manager = "manager";
	public static String syssubject = "SYSSUBJECT";
	public static String leader = "leader";
	public static String assignteamname = "ASSIGNEDTEAMNAME";
	public static String losspostcode = "lossPostcode";
	public static String team = "team";
	public static String losscause = "lossCause";
	public static String lossstate = "lossState";
	public static String productLine = "productLine6";
	public static String assignedusername = "ASSIGNEDUSERMNAME";
	public static double percentage = 1.0;
			
	
	public static String[] resources = new String[] {"Ryan Finn", "Anita Morrall", "Ashleigh McCourt", "Emmett O'Reilly", "Andrei Bennett", 
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
	
	public static String[] incomingCorrespondenResources = new String[] {resources[1], resources[60], resources[2], resources[61], 
			resources[3], resources[65], resources[4], resources[66], resources[6], resources[69], resources[72], resources[70], 
			resources[73], resources[74], resources[9], resources[76], resources[75], resources[77], resources[10], resources[11], 
			resources[12], resources[78], resources[79], resources[13], resources[14], resources[15], resources[81], resources[82], 
			resources[17], resources[83], resources[84], resources[85], resources[86], resources[21], resources[22], resources[87], 
			resources[88], resources[90], resources[92], resources[93], resources[94], resources[25], resources[96], resources[95], 
			resources[27], resources[97], resources[26], resources[28], resources[29], resources[30], resources[32], resources[98], 
			resources[100], resources[101], resources[34], resources[35], resources[102], resources[103], resources[36], resources[38], 
			resources[105], resources[39], resources[106], resources[40], resources[42], resources[44], resources[43], resources[109], 
			resources[108], resources[46], resources[47], resources[111], resources[112], resources[113], resources[52], resources[116], 
			resources[114], resources[115], resources[54], resources[55], resources[117], resources[118], resources[119]};
	
	public static String[] authorisePaymentIPResources = new String[] {resources[92], resources[93], resources[61], resources[3], 
			resources[64], resources[96], resources[29], resources[30], resources[32], resources[66], resources[98], resources[99], 
			resources[6], resources[69], resources[71], resources[34], resources[102], resources[103], resources[36], resources[74], 
			resources[38], resources[76], resources[39], resources[12], resources[78], resources[44], resources[43], resources[79], 
			resources[80], resources[46], resources[109], resources[15], resources[81], resources[113], resources[83], resources[52], 
			resources[85], resources[86], resources[116], resources[21], resources[54], resources[22], resources[118]};
	
	public static String[] generatePaymentResources = new String[] {resources[1], resources[90], resources[92], resources[91], 
			resources[93], resources[3], resources[96], resources[30], resources[32], resources[66], resources[98], resources[6], 
			resources[69], resources[72], resources[34], resources[102], resources[103], resources[36], resources[74], resources[38], 
			resources[76], resources[9], resources[105], resources[39], resources[12], resources[40], resources[78], resources[43], 
			resources[79], resources[109], resources[46], resources[15], resources[81], resources[113], resources[83], resources[85],
			resources[20], resources[54], resources[55], resources[22], resources[117], resources[118], resources[24]};
	
	public static String[] processAdditionalInResources = new String[] {resources[1], resources[2], resources[61], resources[3], 
			resources[64], resources[65], resources[5], resources[66], resources[6], resources[69], resources[72], resources[7], 
			resources[74], resources[76], resources[9], resources[77], resources[10], resources[11], resources[12], resources[78], 
			resources[80], resources[79], resources[13], resources[15], resources[81], resources[16], resources[18], resources[83], 
			resources[84], resources[86], resources[85], resources[21], resources[22], resources[87], resources[23], resources[89], 
			resources[90], resources[92], resources[93], resources[94], resources[96], resources[95], resources[25], resources[29], 
			resources[30], resources[32], resources[98], resources[101], resources[34], resources[33], resources[102], resources[103],
			resources[36], resources[38], resources[105], resources[39], resources[106], resources[40], resources[41], resources[42],
			resources[107], resources[44], resources[43], resources[45], resources[108], resources[46], resources[109],
			resources[110], resources[49], resources[112], resources[113], resources[50], resources[52], resources[116], 
			resources[53], resources[114], resources[115], resources[54], resources[55], resources[117], resources[118], 
			resources[58]};
	
	public static String[] closeFileResources = new String[] {resources[1], resources[90], resources[92], resources[2], resources[93], 
			resources[61], resources[3], resources[96], resources[29], resources[30], resources[32], resources[66], resources[6], 
			resources[69], resources[72], resources[34], resources[102], resources[103], resources[74], resources[38], resources[76],
			resources[9], resources[39], resources[78], resources[43], resources[79], resources[109], resources[48], resources[15],
			resources[81], resources[113], resources[83], resources[52], resources[86], resources[85], resources[21], resources[54],
			resources[22], resources[57], resources[118]};
	
	public static String[] contactCustomerResources = new String[] {resources[1], resources[90], resources[2], resources[93], 
		resources[61], resources[3], resources[25], resources[96], resources[95], resources[29], resources[30], resources[32],
		resources[66], resources[98], resources[6], resources[69], resources[72], resources[34], resources[102], resources[35],
		resources[103], resources[36], resources[74], resources[38], resources[9], resources[76], resources[77], resources[105],
		resources[39], resources[106], resources[12], resources[78], resources[43], resources[79], resources[109], resources[108],
		resources[46], resources[110], resources[15], resources[81], resources[113], resources[84], resources[116], resources[54],
		resources[22], resources[118]};
	
	public static String[] conductFileReviewResources = new String[] {resources[1], resources[59], resources[0], resources[2], 
		resources[61], resources[3], resources[63], resources[4], resources[66], resources[67], resources[6], resources[69], 
		resources[72], resources[74], resources[9], resources[76], resources[75], resources[77], resources[10], resources[11], 
		resources[12], resources[78], resources[79], resources[13], resources[14], resources[15], resources[81], resources[17], 
		resources[83], resources[84], resources[85], resources[86], resources[21], resources[22], resources[88], resources[24],
		resources[90], resources[92], resources[93], resources[94], resources[26], resources[29], resources[30], resources[32], 
		resources[31], resources[98], resources[100], resources[101], resources[34], resources[102], resources[35], resources[103], 
		resources[104], resources[36], resources[38], resources[105], resources[39], resources[106], resources[40], resources[42], 
		resources[44], resources[43], resources[108], resources[109], resources[46], resources[112], resources[113], resources[51], 
		resources[52], resources[114], resources[115], resources[116], resources[54], resources[55], resources[117], resources[118]};
	
	public static String[] newClaimIPIResources = new String[] {resources[61], resources[3], resources[62], resources[66], 
		resources[68], resources[6], resources[69], resources[72], resources[74], resources[76], resources[77], resources[12],
		resources[78], resources[79], resources[14], resources[15], resources[81], resources[17], resources[83], resources[84],
		resources[85], resources[86], resources[21], resources[22], resources[87], resources[88], resources[90], resources[92],
		resources[93], resources[94], resources[25], resources[96], resources[29], resources[30], resources[98], resources[101],
		resources[34], resources[102], resources[103], resources[36], resources[37], resources[38], resources[39], resources[106], 
		resources[40], resources[44], resources[109], resources[108], resources[46], resources[112], resources[113], resources[115],
		resources[116], resources[54], resources[55], resources[56], resources[117], resources[118]};
	
	public static String[] followUpRequestedResources = new String[] {resources[1], resources[2], resources[61], resources[3], 
		resources[63], resources[4], resources[66], resources[67], resources[68], resources[6], resources[69], resources[72], 
		resources[74], resources[8], resources[75], resources[76], resources[9], resources[77], resources[10], resources[11], 
		resources[12], resources[78], resources[80], resources[79], resources[13], resources[15], resources[81], resources[17], 
		resources[83], resources[19], resources[84], resources[85], resources[86], resources[21], resources[22], resources[88], 
		resources[24], resources[90], resources[92], resources[93], resources[96], resources[25], resources[97], resources[26], 
		resources[29], resources[28], resources[30], resources[32], resources[98], resources[100], resources[101], resources[34], 
		resources[102], resources[35], resources[103], resources[36], resources[38], resources[105], resources[39], resources[106], 
		resources[40], resources[42], resources[44], resources[43], resources[46], resources[109], resources[108], resources[112], 
		resources[113], resources[51], resources[52], resources[114], resources[115], resources[116], resources[54], resources[55], 
		resources[117], resources[118]};
	
//	public static int frequencyIncoming_Correspondence_5 = 3057;
//	public static int frequencyAuthorize_Payment_15 = 1065;
//	public static int frequencyClose_File_22 = 1065;
//	public static int frequencyContact_Customer_6 = 781;
//	public static int frequencyProcess_Add_Info_7 = 888;
//	public static int frequencyNew_Claim_3 = 1065;
//	public static int frequencyGenerate_Payment_14 = 1065;
//	public static int frequencyConduct_File_Review_4 = 4190;
//	public static int frequencyFollow_Up_Requested_20 = 3693;
	
//	public static int frequencyIncoming_Correspondence_5 = 3;
//	public static int frequencyAuthorize_Payment_15 = 1;
//	public static int frequencyClose_File_22 = 1;
//	public static int frequencyContact_Customer_6 = 1;
//	public static int frequencyProcess_Add_Info_7 = 1;
//	public static int frequencyNew_Claim_3 = 1;
//	public static int frequencyGenerate_Payment_14 = 1;
//	public static int frequencyConduct_File_Review_4 = 4;
//	public static int frequencyFollow_Up_Requested_20 = 4;
	
//	public static int frequencyIncoming_Correspondence_5 = 2870;
//	public static int frequencyAuthorize_Payment_15 = 1000;
//	public static int frequencyClose_File_22 = 1000;
//	public static int frequencyContact_Customer_6 = 733;
//	public static int frequencyProcess_Add_Info_7 = 833;
//	public static int frequencyNew_Claim_3 = 1000;
//	public static int frequencyGenerate_Payment_14 = 1000;
//	public static int frequencyConduct_File_Review_4 = 3934;
//	public static int frequencyFollow_Up_Requested_20 = 3467;
	
//	public static int frequencyIncoming_Correspondence_5 = 3000;
//	public static int frequencyAuthorize_Payment_15 = 1000;
//	public static int frequencyClose_File_22 = 1000;
//	public static int frequencyContact_Customer_6 = 1000;
//	public static int frequencyProcess_Add_Info_7 = 1000;
//	public static int frequencyNew_Claim_3 = 1000;
//	public static int frequencyGenerate_Payment_14 = 1000;
//	public static int frequencyConduct_File_Review_4 = 3500;
//	public static int frequencyFollow_Up_Requested_20 = 3000;
	
	public static HashMap<HashSet<String>, HashMap<String, Integer>> dur = new HashMap<HashSet<String>, HashMap<String, Integer>>();
	
	
//	public static void main(String[] args) {
//		HashMap<String, String> map = new HashMap<>();
//		for(int i=0; i<3;i++)
//		System.out.println(getNewValue(incoming, losscause, map, "/home/stormfire/Dropbox/workspace/RiskInformedExecution/", true));
//	}
	
	
	public static void main(String[] args) throws Exception {
		
		HashMap<String, HashMap<String, Integer>> resFreq = fixer.discoverFrequencesResources(args[0]);
		
		HashSet<String> a0 = new HashSet<String>();
		String[] a1 = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4", "Generate_Payment_14"};
		a0.addAll(Arrays.asList(a1));
		HashMap<String, Integer> a2 = new HashMap<String, Integer>();
		a2.put("Incoming_Correspondence_5", 1077); //optimal
//		a2.put("Incoming_Correspondence_5", 414);
//		a2.put("Incoming_Correspondence_5", 860); //good
		a2.put("Generate_Payment_14", 216); //optimal
//		a2.put("Generate_Payment_14", 146);
//		a2.put("Conduct_File_Review_4", 477);
		a2.put("Conduct_File_Review_4", 114); //optimal
//		a2.put("Conduct_File_Review_4", 877);
//		a2.put("Conduct_File_Review_4", 1077); //good
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Authorize_Payment_15"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
		a2.put("Authorize_Payment_15", 1);
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
		a2.put("Incoming_Correspondence_5", 654); //optimal
		a2.put("Conduct_File_Review_4", 56); //optimal
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Close_File_22"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
		a2.put("Close_File_22", 1);
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"New_Claim_3"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
		a2.put("New_Claim_3", 1);
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Follow_Up_Requested_20"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
		a2.put("Follow_Up_Requested_20", 1);
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Incoming_Correspondence_5", "Conduct_File_Review_4", "Process_Add_Info_7", "Contact_Customer_6", "Generate_Payment_14"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>();
//		a2.put("Incoming_Correspondence_5", 758); 
		a2.put("Incoming_Correspondence_5", 808); //optimal
//		a2.put("Incoming_Correspondence_5", 1008);
//		a2.put("Incoming_Correspondence_5", 1308);
//		a2.put("Incoming_Correspondence_5", 1508);
//		a2.put("Incoming_Correspondence_5", 1057);
//		a2.put("Incoming_Correspondence_5", 1357);
//		a2.put("Incoming_Correspondence_5", 1557);
//		a2.put("Incoming_Correspondence_5", 1957); //good
//		a2.put("Incoming_Correspondence_5", 2103);
//		a2.put("Contact_Customer_6", 583);
		a2.put("Contact_Customer_6", 483); //optimal
//		a2.put("Contact_Customer_6", 781);
//		a2.put("Process_Add_Info_7", 561);
//		a2.put("Process_Add_Info_7", 888);
//		a2.put("Process_Add_Info_7", 1950); 
		a2.put("Process_Add_Info_7", 2000); //optimal
//		a2.put("Generate_Payment_14", 709); //good
		a2.put("Generate_Payment_14", 309); //optimal
//		a2.put("Generate_Payment_14", 609);
//		a2.put("Generate_Payment_14", 809);
		a2.put("Conduct_File_Review_4", 157); //optimal
//		a2.put("Conduct_File_Review_4", 657);
//		a2.put("Conduct_File_Review_4", 857);
//		a2.put("Conduct_File_Review_4", 1708);
//		a2.put("Conduct_File_Review_4", 2008);
//		a2.put("Conduct_File_Review_4", 2708);//good
//		a2.put("Conduct_File_Review_4", 2837);
		dur.put(a0, a2);
		
		a0 = new HashSet<String>();
		a1 = new String[] {"Follow_Up_Requested_20", "Close_File_22"};
		a0.addAll(Arrays.asList(a1));
		a2 = new HashMap<String, Integer>(); 
//		a2.put("Follow_Up_Requested_20", 3693);
//		a2.put("Follow_Up_Requested_20", 2628);
//		a2.put("Follow_Up_Requested_20", 1628);
//		a2.put("Follow_Up_Requested_20", 1028);
//		a2.put("Follow_Up_Requested_20", 528);
//		a2.put("Follow_Up_Requested_20", 428);
		a2.put("Follow_Up_Requested_20", 228); //optimal
//		a2.put("Follow_Up_Requested_20", 168);
//		a2.put("Follow_Up_Requested_20", 128); //good
//		a2.put("Follow_Up_Requested_20", 0);
//		a2.put("Close_File_22", 1065);
		a2.put("Close_File_22", 160); //optimal
		dur.put(a0, a2);
		
		XFactoryRegistry.instance().setCurrentDefault(new XFactoryNaiveImpl());
		xfactory = XFactoryRegistry.instance().currentDefault();
		
		String val = null;
		String logString = args[0]; // /media/Data/SharedFolder/Commercial/Log/Log.xes
		String tmpString = args[1]; // /media/Data/SharedFolder/Commercial/Log/tmp.xes
		String logBaseString = args[2]; // /media/Data/SharedFolder/Commercial/Log/
		String logOriginalString = args[3]; // /media/Data/SharedFolder/Commercial/LogOriginal.xes
		String yawlString = args[4]; // /media/Data/SharedFolder/Commercial/Commercial.yawl
		String resourceString = args[5]; // /media/Data/SharedFolder/Commercial/Commercial.ybkp
		String decisionTreesString = args[6]; // /home/stormfire/Dropbox/workspace/RiskInformedExecution/
		String logInfoString = args[7]; // /media/Data/SharedFolder/Commercial/LogInfo
		double alpha = Double.parseDouble(args[8]);
		boolean randomize = Boolean.parseBoolean(args[9]);
		percentage = Integer.parseInt(args[10]);
		int timeout = Integer.parseInt(args[11]);
		
		double probFoll = Double.parseDouble(args[12]);
		
		String newPath = tmpString;
		
		HashMap<String, HashMap<String, WorkItemRecord>> workItemsMap = new HashMap<String, HashMap<String,WorkItemRecord>>();
		HashMap<WorkItemRecord, TupleClassifierName> tupleClassifierNameMap = new HashMap<WorkItemRecord, TupleClassifierName>();
		
		HashMap<String, WorkItemRecord> workItemsSubMap = null;
		HashMap<WorkItemRecord, Long> preAllocated = new HashMap<WorkItemRecord, Long>();
		HashMap<String, HashMap<WorkItemRecord, Long>> result = new HashMap<String, HashMap<WorkItemRecord, Long>>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> enabledTask = new ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>();
//		HashMap<String, HashMap<String, Object[]>> allocatedTask = new HashMap<String, HashMap<String, Object[]>>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Object[]>> startedTask = new ConcurrentHashMap<String, ConcurrentHashMap<String, Object[]>>();
//		HashMap<String, HashMap<String, Long>> completedTask = new HashMap<String, HashMap<String, Long>>();
		HashMap<String, LinkedList<HashSet<String>>> choice = new HashMap<String, LinkedList<HashSet<String>>>();
		boolean changed = false;
		
		File file = new File(logInfoString);
		fw = new FileWriter(file, true);
//		
		File f1 = new File(tmpString);
		f1.delete();
		f1.createNewFile();
		
		for(int i = 1; i < 1001; i++) {
			f1 = new File(logBaseString+i+".xes");
			f1.delete();
			f1.createNewFile();
		}
		
//		File file = new File("/media/Data/SharedFolder/Commercial/LogOriginal.xes");
//		File tmpFile = new File(originPath);
//		copyFile(file, tmpFile);

		XLog sunCorp = ImportEventLog.importFromStream(xfactory, logString);
		originalLog = ImportEventLog.importFromStream(xfactory, logOriginalString);
//		XLog sunCorp = ImportEventLog.importFromStream(xfactory, sunCorpLog);

		String specification = null;
		try {
			File f = new File(yawlString);
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
		
		String resourceSpec = null;
		try {
			File f = new File(resourceString);
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
			resourceSpec = writer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		parameters.put("Xlog", originalLog);
		parameters.put("specification", specification);
		parameters.put("resources", resourceSpec);
		
		XConceptExtension xce = XConceptExtension.instance();
		XTimeExtension xte = XTimeExtension.instance();
		XOrganizationalExtension xoe = XOrganizationalExtension.instance();
		
		parameters2.put("Xlog", originalLog);
		parameters2.put("specification", specification);
		parameters2.put("resources", resourceSpec);
		
		a = new InterfaceManager(InterfaceManager.PROM, parameters);
		b = new InterfaceManager(InterfaceManager.PROM, parameters2);

		parameters3.put("Xlog", sunCorp);
		parameters3.put("specification", specification);
		parameters3.put("resources", resourceSpec);
		
		c = new InterfaceManager(InterfaceManager.PROM, parameters3);
		
		DurationFunction df = new DurationFunction(c, b);
//		DurationFunction df = new DurationFunction(b, b);
//		DurationFunction df = new DurationFunction(a, a);
		
//		updateParameters(, newPath);
		
		osa = new OperationalSupportApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 5");
		osa.load(decisionTreesString);
		
		try {
			
			ServerSocket server = new ServerSocket(12345);
			System.out.println("HPC started");
//			Socket hpc = server.accept();
			Socket hpc = null;
		
			int port = 9000;
			JavaCPN conn = new JavaCPN();
//			OperationalSupportMultiCaseApp oss = initializeMultiCase();
					
			String event = "EVENT:";
			String sugg = "SUGG:";
			String enabled = "ENABLED:";
			String disabled = "DISABLED:";
			String started = "STARTED:";
			String completed = "COMPLETED:";
			String data = "DATA:";
			String routing1 = "ROUTE:";
			String routing2 = "ROUTE2:";
			String routing3 = "ROUTE3:";
			String routing4 = "ROUTE4:";
			String routing5 = "ROUTE5:";
			String routing6 = "ROUTE6:";
			String routing7 = "ROUTE7:";
			String routing8 = "ROUTE8:";
			String routing9 = "ROUTE9:";
			String routing10 = "ROUTE10:";
			
			String request = "", responce = "";
//			HashMap<String, Long> map1 = null;
//			HashMap<WorkItemRecord, Long> map2 = null;
			
//			HashMap<String, WorkItemRecord> mapNameWorkItem = new HashMap<String, WorkItemRecord>();
			
			System.out.println("CPNTool started");
			conn.accept(port);
			
			String[] task = null;
			String singleTask = null;
			String mess = null;
			String instance = null;
			String tasks = null;
			Long time = null;
			String info = null;
			HashMap<String, String> mapData = null;
			HashMap<String, String> newMapData = null;
			HashSet<WorkItemRecord> relevantWorkItems = new HashSet<WorkItemRecord>();
			HashMap<String, HashSet<WorkItemRecord>> use = new HashMap<String, HashSet<WorkItemRecord>>();
			HashSet<WorkItemRecord> set = null;
			LinkedList<WorkItemRecord> offeredWorkItemsList = new LinkedList<WorkItemRecord>();
			LinkedList<WorkItemRecord> startedWorkItemsList = new LinkedList<WorkItemRecord>();
			ConcurrentHashMap<String, Object[]> wirs = null;
			WorkItemRecord[] offeredWorkItems = null;
			WorkItemRecord[] startedWorkItems = null;
			HashMap<String, HashSet<WorkItemRecord>> can = null;
			HashSet<HashSet<WorkItemRecord>> choices = null;
			LinkedList<TupleClassifierName> listTCN = new LinkedList<TupleClassifierName>();
			WorkItemRecord[] totalWorkItem = null;
			TupleClassifierName tcn = null;
			ILPBuilderSCIPNewMax ilp = new ILPBuilderSCIPNewMax();			
			DurationSplitter ds = null;
			Long finalTime = null;
			String finalTask = null;
			String finalResurce = null;
			WorkItemRecord wir = null;
			WorkItemRecord toBeRemoved = null;
			ConcurrentHashMap<String, Object[]> map2 = null;
			
			while (true) {
				
				task = null;
				singleTask = null;
				mess = null;
				instance = null;
				tasks = null;
				time = null;
				info = null;
				mapData = null;
				newMapData = null;
				relevantWorkItems.clear();
				use.clear();
				set = null;
				offeredWorkItemsList.clear();
				startedWorkItemsList.clear();
				wirs = null;
				offeredWorkItems = null;
				startedWorkItems = null;
				can = null;
				choices = null;
				listTCN.clear();
				totalWorkItem = null;
				tcn = null;		
				ds = null;
				finalTime = null;
				finalTask = null;
				finalResurce = null;
				wir = null;
				toBeRemoved = null;
				map2 = null;
			
//				System.gc();
				
				request = EncodeDecode.decodeString(conn.receive());

				responce = "Done";
				
//				System.out.println(request);
				
				if(request.startsWith(sugg)) { //TODO SUGG
					
					mess = request.substring(request.indexOf(sugg)+sugg.length());
//					System.out.println(mess);
					
					instance = mess.substring(0, mess.indexOf(":"));
//					System.out.println(instance);
					
					mess = mess.substring(mess.indexOf(":")+1);
					tasks = mess.substring(0, mess.indexOf("::"));
//					System.out.println(tasks);
					task = convertStringToTasks(tasks);
					
					time = Long.parseLong(mess.substring(mess.lastIndexOf(":")+1));
//					System.out.println("time: "+time);
					
					if(!randomize) {
						if(probFoll < random.nextDouble()) {
							if(changed) {
		//						HashMap<WorkItemRecord, Long> newPreAllocated = new HashMap<WorkItemRecord, Long>();
								
								result.clear();
								
		//						preAllocated.clear();
								
								updateParameters(newPath, logBaseString);
		//						relevantWorkItems = new HashSet<WorkItemRecord>();
								relevantWorkItems.clear();
								
		//						removeOldComplete(enabledTask, completedTask, startedTask);
													
								for(Entry<String, ConcurrentHashMap<String, Long>> entry1 : enabledTask.entrySet()) {
									for(Entry<String, Long> entry2 : entry1.getValue().entrySet()) {
		//								if(entry2.getValue() <= time) {
		//									HashMap<String, Long> map = null;
		//									if((map = completedTask.get(entry1.getKey())) != null) {
		//										Long t2 = null;
		//										if((t2 = map.get(entry2.getKey())) != null) {
		////											System.out.println(t2);
		////											System.out.println(time);
		//											if(t2 > time) {
		//												wir = new WorkItemRecord(entry1.getKey(), entry2.getKey(), "Commercial", null, null);
		//												relevantWorkItems.add(wir);
		//											}
		//										}else {
		//											wir = new WorkItemRecord(entry1.getKey(), entry2.getKey(), "Commercial", null, null);
		//											relevantWorkItems.add(wir);	
		//										}
		//									}else {
												if((workItemsSubMap = workItemsMap.get(entry1.getKey())) == null) {
													workItemsSubMap = new HashMap<String, WorkItemRecord>();
													workItemsMap.put(entry1.getKey(), workItemsSubMap);
												}
												if((wir = workItemsSubMap.get(entry2.getKey())) == null) {
													wir = new WorkItemRecord(entry1.getKey(), entry2.getKey(), "Commercial", null, null);
													workItemsSubMap.put(entry2.getKey(), wir);
												}
													
		//										wir = new WorkItemRecord(entry1.getKey(), entry2.getKey(), "Commercial", null, null);
												relevantWorkItems.add(wir);
		//									}
		//								}
									}
								}
								
		//						use = new HashMap<String, HashSet<WorkItemRecord>>();
								use.clear();
								set = null;									
								
		//						offeredWorkItemsList = new LinkedList<WorkItemRecord>();
		//						startedWorkItemsList = new LinkedList<WorkItemRecord>();
								offeredWorkItemsList.clear();
								startedWorkItemsList.clear();
								
								for(WorkItemRecord wir1 : relevantWorkItems) {
									wirs = null;
									Object[] o = null;
									if((wirs = startedTask.get(wir1.getRootCaseID())) != null) {
										if((o = wirs.get(wir1.getTaskID())) != null) {
		//									long l = (Long) o[1];
		//									if(l <= time) {
												startedWorkItemsList.add(wir1);
												if((set = use.get(o[0])) == null){
													set = new HashSet<WorkItemRecord>();
													use.put((String) o[0], set);
												}
												set.add(wir1);
		
		//										boolean missing = true;
		//										Long oldTime = null;
		//										WorkItemRecord oldWir = null;
		//										for(Entry<WorkItemRecord, Long> entry : preAllocated.entrySet()) {
		//											if(entry.getKey().getRootCaseID().equals(wir.getRootCaseID()) &&
		//													entry.getKey().getTaskID().equals(wir.getTaskID())) {
		//												missing = false;
		//												oldTime = entry.getValue();
		//												oldWir = entry.getKey();
		//												break;
		//											}
		//										}
		//										if(missing) {
		//											long finalTime = l - time;
		//											if(finalTime == 0) {
		//												preAllocated.put(wir, finalTime);
		//											}else {
		//												preAllocated.put(wir, 1L);
		//											}
		//										}else {
		//											preAllocated.remove(oldWir);
		//											preAllocated.put(wir, oldTime);
		//										}
												
		//									}else {
		//										offeredWorkItemsList.add(wir1);
		//									}
										}else {
											offeredWorkItemsList.add(wir1);
										}
									}else {
										offeredWorkItemsList.add(wir1);
									}
								}
								
		//						LinkedList<WorkItemRecord> wirsRem = new LinkedList<WorkItemRecord>();
		//						for(WorkItemRecord wir : preAllocated.keySet()) {
		//							if(!startedWorkItemsList.contains(wir)) {
		//								wirsRem.add(wir);
		//							}
		//						}
		//						for(WorkItemRecord wir : wirsRem) {
		//							preAllocated.remove(wir);
		//						}
								
								offeredWorkItems = offeredWorkItemsList.toArray(new WorkItemRecord[0]);
								startedWorkItems = startedWorkItemsList.toArray(new WorkItemRecord[0]);
								
								can = generateCan(relevantWorkItems, use);
								choices = new HashSet<HashSet<WorkItemRecord>>();
								
								for(Entry<String, LinkedList<HashSet<String>>> entry : choice.entrySet()) {
									String caseID = entry.getKey();
									for(HashSet<String> set2 : entry.getValue()) {
										HashSet<WorkItemRecord> group = new HashSet<WorkItemRecord>();
										for(String name : set2) {
											for(WorkItemRecord wir1 : relevantWorkItems) {
												if(wir1.getCaseID().equals(caseID) && wir1.getTaskID().equals(name)) {
													group.add(wir1);
												}
											}
										}
										if(group.size() > 0) {
											choices.add(group);
										}
									}
								}
								
		//						listTCN = new LinkedList<TupleClassifierName>();
								listTCN.clear();
								
								totalWorkItem = new WorkItemRecord[offeredWorkItems.length+startedWorkItems.length];
								
								int i = 0;
								for(WorkItemRecord wir1 : offeredWorkItems) {
									if((tcn = tupleClassifierNameMap.get(wir1)) == null) {
										tcn = osa.buildTupleClassifierName(wir1, resources);
										tupleClassifierNameMap.put(wir1, tcn);
									}
		//							tcn = osa.buildTupleClassifierName(wir1, resources);
									listTCN.add(tcn);
									totalWorkItem[i] = wir1;
									i++;
								}
								
								for(WorkItemRecord wir1 : startedWorkItems) {
									if((tcn = tupleClassifierNameMap.get(wir1)) == null) {
										tcn = osa.buildTupleClassifierName(wir1, resources);
										tupleClassifierNameMap.put(wir1, tcn);
									}
		//							tcn = osa.buildTupleClassifierName(wir1, resources);
									listTCN.add(tcn);
									totalWorkItem[i] = wir1;
									i++;
								}
								
		//						ILPBuilderSCIPNewMax ilp = new ILPBuilderSCIPNewMax();
		//						ilp.clear();
								long max = ilp.findMaxDuration(resources, totalWorkItem, df)/60000;
								
								
								ds = new DurationSplitter(listTCN, max, time, 60000);
								ilp.execute(alpha, resources, startedWorkItems, offeredWorkItems, totalWorkItem, df, use, can, 
										new RiskFunction(ds), choices, result, ds, preAllocated, 60000, hpc, timeout);
								
		//						ds.clear();

								changed = false;
							}
							
							finalTime = null;
							finalTask = null;
							finalResurce = null;
							
							for(Entry<String, HashMap<WorkItemRecord, Long>> entry : result.entrySet()) {
								HashMap<WorkItemRecord, Long> result2 = entry.getValue();
								for(Entry<WorkItemRecord, Long> entry2 : result2.entrySet()) {
									String name = entry2.getKey().getTaskID();
									String caseID = entry2.getKey().getRootCaseID();
									for(String t : task) {
		//								System.out.println(t+" "+instance);
										if(t.equals(name) && instance.equals(caseID)) {
											finalTime = entry2.getValue();
											finalTask = t;
											finalResurce = entry.getKey();
		//									finalTime = getTime(result, entry2.getKey(), finalResurce);
											finalTime += time; 
		
		//									System.out.println("finalTask: "+finalTask);
		//									System.out.println("finalResurce: "+finalResurce);
											
											wir = entry2.getKey();
		//									toBeRemoved = null;
		//									for(Entry<WorkItemRecord, Long> entry3 : preAllocated.entrySet()) {
		//										if(entry3.getKey().getRootCaseID().equals(wir.getRootCaseID()) &&
		//												entry3.getKey().getTaskID().equals(wir.getTaskID())) {
		//											toBeRemoved = entry3.getKey();
		//											break;
		//										}
		//									}
		//									if(toBeRemoved != null) preAllocated.remove(toBeRemoved);
		//									preAllocated.put(entry2.getKey(), finalTime);
											break;
										}
									}
									if(finalTime != null && finalTask != null && finalResurce != null) {
										break;
									}
								}
								if(finalTime != null && finalTask != null && finalResurce != null) {
									break;
								}
							}
							
						}else {
							updateParameters(newPath, logBaseString);
							HashSet<String> gkeySet = null;
							
							for(HashSet<String> keySet : dur.keySet()) {
								if(keySet.size() == task.length) {
									boolean con = true;
									for(String t : task) {
										if(!keySet.contains(t)) {
											con = false;
											break;
										}
									}
									if(con) {
										gkeySet = keySet;
										break;
									}
								}
							}
							
							int[] freq = new int[task.length];
							int total = 0;
							
							for(int i = 0; i < task.length; i++) {
								String t = task[i];
								freq[i] = dur.get(gkeySet).get(t);
								total += freq[i];
							}
							
							int rand = random.nextInt(total);
							int cur = 0;
							for(int i = 0; i < task.length; i++) {
								cur += freq[i];
								if(rand < cur) {
									finalTask = task[i];
									break;
								}
							}
							
							String[] res = null;
							if(finalTask.equals(newClaim)) {
								res = newClaimIPIResources;
							}else if(finalTask.equals(incoming)) {
								res = incomingCorrespondenResources;
							}else if(finalTask.equals(conduct)) {
								res = conductFileReviewResources;
							}else if(finalTask.equals(contact)) {
								res = contactCustomerResources;
							}else if(finalTask.equals(process)) {
								res = processAdditionalInResources;
							}else if(finalTask.equals(authorize)) {
								res = authorisePaymentIPResources;
							}else if(finalTask.equals(generate)) {
								res = generatePaymentResources;
							}else if(finalTask.equals(follow)) {
								res = followUpRequestedResources;
							}else if(finalTask.equals(close)) {
								res = closeFileResources;
							}
							
							int countRes = 0;
							int[] ress = new int[res.length];
							for(int i = 0; i < res.length; i++) {
								ress[i] = resFreq.get(finalTask).get(res[i]);
								countRes += ress[i];
							}
							
							rand = random.nextInt(countRes);
							cur = 0;
							for(int i = 0; i < res.length; i++) {
								cur += ress[i];
								if(rand < cur) {
									finalResurce = res[i];
									break;
								}
							}
							
							finalTime = time+1;
						}
					}else {
						updateParameters(newPath, logBaseString);
						HashSet<String> gkeySet = null;
						
						for(HashSet<String> keySet : dur.keySet()) {
							if(keySet.size() == task.length) {
								boolean con = true;
								for(String t : task) {
									if(!keySet.contains(t)) {
										con = false;
										break;
									}
								}
								if(con) {
									gkeySet = keySet;
									break;
								}
							}
						}
						
						int[] freq = new int[task.length];
						int total = 0;
						
						for(int i = 0; i < task.length; i++) {
							String t = task[i];
							freq[i] = dur.get(gkeySet).get(t);
							total += freq[i];
						}
						
//						for(int i = 0; i < task.length; i++) {
//							String t = task[i];
//							if(t.equals(newClaim)) {
//								freq[i] = frequencyNew_Claim_3;
//							}else if(t.equals(incoming)) {
//								freq[i] = frequencyIncoming_Correspondence_5;
//							}else if(t.equals(conduct)) {
//								freq[i] = frequencyConduct_File_Review_4;
//							}else if(t.equals(contact)) {
//								freq[i] = frequencyContact_Customer_6;
//							}else if(t.equals(process)) {
//								freq[i] = frequencyProcess_Add_Info_7;
//							}else if(t.equals(authorize)) {
//								freq[i] = frequencyAuthorize_Payment_15;
//							}else if(t.equals(generate)) {
//								freq[i] = frequencyGenerate_Payment_14;
//							}else if(t.equals(follow)) {
//								freq[i] = frequencyFollow_Up_Requested_20;
//							}else if(t.equals(close)) {
//								freq[i] = frequencyClose_File_22;
//							}
//							total += freq[i];
//						}
						
						int rand = random.nextInt(total);
						int cur = 0;
						for(int i = 0; i < task.length; i++) {
							cur += freq[i];
							if(rand < cur) {
								finalTask = task[i];
								break;
							}
						}
						
//						finalTask = task[random.nextInt(task.length)];
						String[] res = null;
						if(finalTask.equals(newClaim)) {
							res = newClaimIPIResources;
						}else if(finalTask.equals(incoming)) {
							res = incomingCorrespondenResources;
						}else if(finalTask.equals(conduct)) {
							res = conductFileReviewResources;
						}else if(finalTask.equals(contact)) {
							res = contactCustomerResources;
						}else if(finalTask.equals(process)) {
							res = processAdditionalInResources;
						}else if(finalTask.equals(authorize)) {
							res = authorisePaymentIPResources;
						}else if(finalTask.equals(generate)) {
							res = generatePaymentResources;
						}else if(finalTask.equals(follow)) {
							res = followUpRequestedResources;
						}else if(finalTask.equals(close)) {
							res = closeFileResources;
						}
						
						int countRes = 0;
						int[] ress = new int[res.length];
						for(int i = 0; i < res.length; i++) {
							ress[i] = resFreq.get(finalTask).get(res[i]);
							countRes += ress[i];
						}
						
						rand = random.nextInt(countRes);
						cur = 0;
						for(int i = 0; i < res.length; i++) {
							cur += ress[i];
							if(rand < cur) {
								finalResurce = res[i];
								break;
							}
						}
						
//						finalResurce = res[random.nextInt((int) Math.floor(res.length / percentage))];
						finalTime = time+1;
					}
					
					responce = finalTask+":"+finalResurce+":"+(finalTime);
					
//					map2 = null;
//					if((map2 = allocatedTask.get(instance)) == null) {
//						map2 = new HashMap<String, Object[]>();
//						allocatedTask.put(instance, map2);
//					}
//					for(String t : task) {
//						map2.put(t, new Object[] {finalResurce, (finalTime)});
//					}
					System.out.println("SUGGESTED:"+instance+":"+responce);
				
				}else if(request.startsWith(enabled)) {
					
					System.out.println(request);
										
					mess = request.substring(request.indexOf(enabled)+enabled.length());
//					System.out.println("mess: "+mess);
					
					instance = mess.substring(0, mess.indexOf(":"));
//					System.out.println("caseID: "+caseID);
					
					mess = mess.substring(mess.indexOf(":")+1);
					tasks = mess.substring(0, mess.indexOf("::"));
					task = convertStringToTasks(tasks);
//					System.out.println("task: "+tasks);
					
					time = Long.parseLong(mess.substring(mess.lastIndexOf(":")+1));
//					System.out.println("time: "+time);
				
					ConcurrentHashMap<String, Long> map = null;
					if((map = enabledTask.get(instance)) == null) {
						map = new ConcurrentHashMap<String, Long>();
						enabledTask.put(instance, map);
					}
					
					HashSet<String> exclusive = null;
					LinkedList<HashSet<String>> listChoice = null;
					if((listChoice = choice.get(instance)) == null) {
						listChoice = new LinkedList<HashSet<String>>();
						choice.put(instance, listChoice);
					}
					
					for(String t : task) {
						if(t.equals(newClaim)) {
							singleTask = newClaim;
						}else if(t.equals(incoming)) {
							singleTask = incoming;
						}else if(t.equals(conduct)) {
							singleTask = conduct;
						}else if(t.equals(contact)) {
							singleTask = contact;
						}else if(t.equals(process)) {
							singleTask = process;
						}else if(t.equals(authorize)) {
							singleTask = authorize;
						}else if(t.equals(generate)) {
							singleTask = generate;
						}else if(t.equals(follow)) {
							singleTask = follow;
						}else if(t.equals(close)) {
							singleTask = close;
						}
						 
						map.put(singleTask, time);
						for(HashSet<String> x : listChoice) {
							if(x.contains(singleTask)) {
								exclusive = x;
								break;
							}
						}
						if(exclusive != null) break;
					}
					
					if(exclusive == null) {
						exclusive = new HashSet<String>();
						listChoice.add(exclusive);
					}
					
					for(String t : task) {
						if(t.equals(newClaim)) {
							singleTask = newClaim;
						}else if(t.equals(incoming)) {
							singleTask = incoming;
						}else if(t.equals(conduct)) {
							singleTask = conduct;
						}else if(t.equals(contact)) {
							singleTask = contact;
						}else if(t.equals(process)) {
							singleTask = process;
						}else if(t.equals(authorize)) {
							singleTask = authorize;
						}else if(t.equals(generate)) {
							singleTask = generate;
						}else if(t.equals(follow)) {
							singleTask = follow;
						}else if(t.equals(close)) {
							singleTask = close;
						}
						exclusive.add(singleTask);
					}
					
					changed = true;
					
				}else if(request.startsWith(disabled)) {
					
//					System.out.println(request);
					
					mess = request.substring(request.indexOf(disabled)+disabled.length());
//					System.out.println("mess: "+mess);
					
					instance = mess.substring(0, mess.indexOf(":"));
//					System.out.println("caseID: "+caseID);
					
					mess = mess.substring(mess.indexOf(":")+1);
					tasks = mess.substring(0, mess.indexOf("::"));
					task = convertStringToTasks(tasks);
//					System.out.println("task: "+tasks);
					
					time = Long.parseLong(mess.substring(mess.lastIndexOf(":")+1));
//					System.out.println("time: "+time);
					
					ConcurrentHashMap<String, Long> map = null;
					if((map = enabledTask.get(instance)) != null) {
						for(String t : task) {
							map.remove(t);
						}
						if(map.isEmpty()) enabledTask.remove(instance);
					}
					
					HashSet<String> exclusive = null;
					LinkedList<HashSet<String>> listChoice = choice.get(instance);
					
					for(String t : task) {
						if(listChoice != null) {
							for(HashSet<String> x : listChoice) {
								if(x.contains(t)) {
									exclusive = x;
									break;
								}
							}
						}
						if(exclusive != null) break;
					}
					
					if(exclusive != null) {
						for(String t : task) {
							exclusive.remove(t);
						}
						if(exclusive.isEmpty()) {
							listChoice.remove(exclusive);
						}
						if(listChoice.isEmpty()) {
							choice.remove(instance);
						}
					}
					
					if(!randomize) {
						if((workItemsSubMap = workItemsMap.get(instance)) != null) {
							for(String t : task) {
								if((wir = workItemsSubMap.remove(t)) != null) {
									tupleClassifierNameMap.remove(wir);
									df.removeDuration(wir);
								}
							}
							if(workItemsSubMap.isEmpty()) workItemsMap.remove(instance);
						}
					}
//					changed = true;
					
				}else if(request.startsWith(started)) {
					
//					System.out.println(request);
					
					mess = request.substring(request.indexOf(started)+started.length());
//					System.out.println("mess: "+mess);
					
					instance = mess.substring(0, mess.indexOf(":"));
//					System.out.println("caseID: "+caseID);
					
					mess = mess.substring(mess.indexOf(":")+1);
					String res = mess.substring(0, mess.indexOf(":"));
//					System.out.println("res: "+res);
					
					mess = mess.substring(mess.indexOf(":")+1);
					tasks = mess.substring(0, mess.indexOf("::"));
					task = convertStringToTasks(tasks);
//					System.out.println("task: "+tasks);
					
					time = Long.parseLong(mess.substring(mess.lastIndexOf(":")+1));
//					System.out.println("time: "+time);
				
//					HashMap<String, Long> map = null;
//					if((map = enabledTask.get(caseID)) != null) {
//						for(String t : task) {
//							map.remove(t);
//						}
//					}

					map2 = null;
					if((map2 = startedTask.get(instance)) == null) {
						map2 = new ConcurrentHashMap<String, Object[]>();
						startedTask.put(instance, map2);
					}
					String t = task[0];
					if(t.equals(newClaim)) {
						singleTask = newClaim;
					}else if(t.equals(incoming)) {
						singleTask = incoming;
					}else if(t.equals(conduct)) {
						singleTask = conduct;
					}else if(t.equals(contact)) {
						singleTask = contact;
					}else if(t.equals(process)) {
						singleTask = process;
					}else if(t.equals(authorize)) {
						singleTask = authorize;
					}else if(t.equals(generate)) {
						singleTask = generate;
					}else if(t.equals(follow)) {
						singleTask = follow;
					}else if(t.equals(close)) {
						singleTask = close;
					}
					map2.put(singleTask, new Object[] {res, time});
//					changed = true;
					
				}else if(request.startsWith(event)) { //TODO COMPLETE
					
//					System.out.println(request);
					mess = request.substring(request.indexOf(event)+event.length());
					
					instance = mess.substring(0, mess.indexOf(":"));
					mess = mess.substring(mess.indexOf(":")+1);
										
					singleTask = mess.substring(0, mess.indexOf(":"));
					mess = mess.substring(mess.indexOf(":")+1);
					
					if(singleTask.equals(newClaim)) {
						singleTask = newClaim;
					}else if(singleTask.equals(incoming)) {
						singleTask = incoming;
					}else if(singleTask.equals(conduct)) {
						singleTask = conduct;
					}else if(singleTask.equals(contact)) {
						singleTask = contact;
					}else if(singleTask.equals(process)) {
						singleTask = process;
					}else if(singleTask.equals(authorize)) {
						singleTask = authorize;
					}else if(singleTask.equals(generate)) {
						singleTask = generate;
					}else if(singleTask.equals(follow)) {
						singleTask = follow;
					}else if(singleTask.equals(close)) {
						singleTask = close;
					}
					
					responce = mess.substring(0, mess.indexOf(":"));
					for(String r : resources) {
						if(responce.equals(r)) {
							responce = r;
							break;
						}
					}
					mess = mess.substring(mess.indexOf(":")+1);
					
					tmpString = mess.substring(0, mess.indexOf(":"));
					info = mess.substring(mess.indexOf(":")+1);
					
//					System.out.println("info: "+info);
					
					mapData = convertStringToData(info);
//					System.out.println("caseID: "+caseID);
					
					boolean found = false;
					for(int i = 0; i < log.size(); i++) {
						XTrace t = log.get(i);
						if(xce.extractName(t).equals(instance)) {
							log.remove(i);
							XEvent e = xfactory.createEvent();
							xce.assignName(e, singleTask);
							xoe.assignResource(e, responce);
							xte.assignTimestamp(e, Long.parseLong(tmpString));
							for(Entry<String, String> entry : mapData.entrySet()) {
								val = dataValues.get(entry.getValue());
								if(entry.getValue().equals(incAmt)) {
									e.getAttributes().put(incAmt, xfactory.createAttributeDiscrete(incAmt, Long.parseLong(val), null));
								}else if(entry.getValue().equals(originalEstimate)) {
									e.getAttributes().put(originalEstimate, xfactory.createAttributeDiscrete(originalEstimate, Long.parseLong(val), null));
								}else if(entry.getValue().equals(manager)) {
									e.getAttributes().put(manager, xfactory.createAttributeLiteral(manager, val, null));
								}else if(entry.getValue().equals(syssubject)) {
									e.getAttributes().put(syssubject, xfactory.createAttributeLiteral(syssubject, val, null));
								}else if(entry.getValue().equals(leader)) {
									e.getAttributes().put(leader, xfactory.createAttributeLiteral(leader, val, null));
								}else if(entry.getValue().equals(assignteamname)) {
									e.getAttributes().put(assignteamname, xfactory.createAttributeLiteral(assignteamname, val, null));
								}else if(entry.getValue().equals(losspostcode)) {
									e.getAttributes().put(losspostcode, xfactory.createAttributeLiteral(losspostcode, val, null));
								}else if(entry.getValue().equals(team)) {
									e.getAttributes().put(team, xfactory.createAttributeLiteral(team, val, null));
								}else if(entry.getValue().equals(losscause)) {
									e.getAttributes().put(losscause, xfactory.createAttributeLiteral(losscause, val, null));
								}else if(entry.getValue().equals(lossstate)) {
									e.getAttributes().put(lossstate, xfactory.createAttributeLiteral(lossstate, val, null));
								}else if(entry.getValue().equals(productLine)) {
									e.getAttributes().put(productLine, xfactory.createAttributeLiteral(productLine, val, null));
								}else if(entry.getValue().equals(assignedusername)) {
									e.getAttributes().put(assignedusername, xfactory.createAttributeLiteral(assignedusername, val, null));
								}
							}
							t.add(e);
							log.add(i, t);
							found = true;
							break;
						}
					}
					if(!found) {
						XTrace t = xfactory.createTrace();
						xce.assignName(t, instance);
						XEvent e = xfactory.createEvent();
						xce.assignName(e, singleTask);
						xoe.assignResource(e, responce);
						xte.assignTimestamp(e, Long.parseLong(tmpString));
						for(Entry<String, String> entry : mapData.entrySet()) {
							val = dataValues.get(entry.getValue());
							if(entry.getValue().equals(incAmt)) {
								e.getAttributes().put(incAmt, xfactory.createAttributeDiscrete(incAmt, Long.parseLong(val), null));
							}else if(entry.getValue().equals(originalEstimate)) {
								e.getAttributes().put(originalEstimate, xfactory.createAttributeDiscrete(originalEstimate, Long.parseLong(val), null));
							}else if(entry.getValue().equals(manager)) {
								e.getAttributes().put(manager, xfactory.createAttributeLiteral(manager, val, null));
							}else if(entry.getValue().equals(syssubject)) {
								e.getAttributes().put(syssubject, xfactory.createAttributeLiteral(syssubject, val, null));
							}else if(entry.getValue().equals(leader)) {
								e.getAttributes().put(leader, xfactory.createAttributeLiteral(leader, val, null));
							}else if(entry.getValue().equals(assignteamname)) {
								e.getAttributes().put(assignteamname, xfactory.createAttributeLiteral(assignteamname, val, null));
							}else if(entry.getValue().equals(losspostcode)) {
								e.getAttributes().put(losspostcode, xfactory.createAttributeLiteral(losspostcode, val, null));
							}else if(entry.getValue().equals(team)) {
								e.getAttributes().put(team, xfactory.createAttributeLiteral(team, val, null));
							}else if(entry.getValue().equals(losscause)) {
								e.getAttributes().put(losscause, xfactory.createAttributeLiteral(losscause, val, null));
							}else if(entry.getValue().equals(lossstate)) {
								e.getAttributes().put(lossstate, xfactory.createAttributeLiteral(lossstate, val, null));
							}else if(entry.getValue().equals(productLine)) {
								e.getAttributes().put(productLine, xfactory.createAttributeLiteral(productLine, val, null));
							}else if(entry.getValue().equals(assignedusername)) {
								e.getAttributes().put(assignedusername, xfactory.createAttributeLiteral(assignedusername, val, null));
							}
						}
						t.add(e);
						log.add(t);
					}
					
				}else if(request.startsWith(completed)) { //TODO COMPLETE
					
					System.out.println(request);
					mess = request.substring(request.indexOf(completed)+completed.length());
					
					instance = mess.substring(0, mess.indexOf(":"));
//					System.out.println("caseID: "+caseID);
					
					mess = mess.substring(mess.indexOf(":")+1);
					tasks = mess.substring(0, mess.indexOf("::"));
					task = convertStringToTasks(tasks);
//					System.out.println("task: "+tasks);
					
					time = Long.parseLong(mess.substring(mess.lastIndexOf(":")+1));
//					System.out.println("time: "+time);
				
//					HashMap<String, Long> map = null;
//					if((map = completedTask.get(instance)) == null) {
//						map = new HashMap<String, Long>();
//						completedTask.put(instance, map);
//					}
//					for(String t : task) {
//						map.put(t, time);
//					}
					enabledTask.get(instance).remove(task[0]);
					if(enabledTask.get(instance).isEmpty()) enabledTask.remove(instance);
					startedTask.get(instance).remove(task[0]);
					if(startedTask.get(instance).isEmpty()) startedTask.remove(instance);
					
					HashSet<String> exclusive = null;
					LinkedList<HashSet<String>> listChoice = choice.get(instance);
					
					for(String t : task) {
						if(listChoice != null) {
							for(HashSet<String> x : listChoice) {
								if(x.contains(t)) {
									exclusive = x;
									break;
								}
							}
						}
						if(exclusive != null) break;
					}
					
					if(exclusive != null) {
						for(String t : task) {
							exclusive.remove(t);
						}
						if(exclusive.isEmpty()) {
							listChoice.remove(exclusive);
						}
						if(listChoice.isEmpty()) {
							choice.remove(instance);
						}
					}
					
					if(!randomize) {
						if((workItemsSubMap = workItemsMap.get(instance)) != null) {
							if((wir = workItemsSubMap.remove(task[0])) != null) {
								tupleClassifierNameMap.remove(wir);
								df.removeDuration(wir);
							}
							if(workItemsSubMap.isEmpty()) workItemsMap.remove(instance);
						}
					}
					
					if(task[0].equals(close)) {
						if(!randomize && workItemsSubMap != null && workItemsSubMap.isEmpty()) workItemsMap.remove(instance);
						for(int i = 0; i < log.size(); i++) {
							XTrace t = log.get(i);
							if(xce.extractName(t).equals(instance)) {
								log.remove(i);
								System.out.println("removed");
								break;
							}
						}
					}
					
//					changed = true;
					
				}else if(request.startsWith(data)) {
					
//					System.out.println("Data");
					
					mess = request.substring(request.indexOf(data)+data.length());
//					System.out.println("mess: "+mess);
					
					singleTask = mess.substring(0, mess.indexOf(":"));
//					System.out.println("task: "+task);
					
					info = mess.substring(mess.indexOf(":")+1);
//					System.out.println("info: "+info);
					
					mapData = convertStringToData(info);
					
					newMapData = (HashMap<String, String>) mapData.clone();
				
					int changes = 0;
				
					for(String name : mapData.keySet()) {
						if(name.equals(assignteamname)) {
							newMapData.put(name, "null");
						}else {
							String newValue = getNewValue(singleTask, name, mapData, decisionTreesString, false);
							if(!newValue.isEmpty() && !newValue.equals("null"))	{
								newMapData.put(name, newValue);
								if(!name.equals(syssubject) && !mapData.get(name).equals(newValue)) {
									changes++;
								}
							}
						}
					}
					
					LinkedList<String> names = new LinkedList<String>(mapData.keySet());
					
					if(changes == 0) {
						for(int i = 0; i < names.size(); i++) {
							String name = names.remove(random.nextInt(names.size()));
							if(name.equals(assignteamname)) {
								newMapData.put(name, "null");
							}else {
								if(changes == 0) {
									String newValue = getNewValue(singleTask, name, mapData, decisionTreesString, true);
									if(!newValue.isEmpty())	{
										newMapData.put(name, newValue);
										if(!name.equals(syssubject) && !mapData.get(name).equals(newValue)) {
											changes++;
										}
									}
								}
							}
						}
					}
					
//					System.out.println(map);
					responce = convertDataToString(newMapData);
//					System.out.println(responce);
					
				}else if(request.startsWith(routing1)) { //CLOSE FILE
					
//					info = request.substring(request.indexOf(routing1)+routing1.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((mapData.get("SYSSUBJECT").equals("Review and approve new payment")) || 
//									((!mapData.get("SYSSUBJECT").equals("Review and approve new payment")) && 
//											(Double.parseDouble(mapData.get("incAmt")) > 1.64)))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing2)) { //CONTACT CUSTOMER
					
//					info = request.substring(request.indexOf(routing2)+routing2.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							(((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(mapData.get("lossCause").equals("a"))) ||
//									(((((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(!mapData.get("lossPostcode").equals("5"))) &&
//									(!mapData.get("lossPostcode").equals("6"))) &&
//									(!mapData.get("lossCause").equals("null"))) &&
//									(!mapData.get("lossPostcode").equals("2"))) &&
//									(mapData.get("team").equals("IPI AMP Fulfilment - Cent"))))
//
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing3)) { //PROCESS ADD INFO
					
//					info = request.substring(request.indexOf(routing3)+routing3.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							(((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(mapData.get("SYSSUBJECT").equals("null"))) ||
//									((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//									(mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) > 1545.45)) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) > 3615.51)))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing4)) { //CONDUCT FILE REVIEW
					
//					info = request.substring(request.indexOf(routing4)+routing4.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((((((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(mapData.get("SYSSUBJECT").equals("Review correspondence"))) ||
//									((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job")))) ||
//									(((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number")))) ||
//									((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern")))) ||
//									(((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("VendorNote Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Central")))) ||
//									((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Central"))) &&
//									(mapData.get("ASSIGNEDTEAMNAME").equals("IPI Platinum Claims - Central")))) ||
//									(((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(mapData.get("lossCause").equals("null"))) &&
//									(mapData.get("leader").equals("Claire Matthews")))) ||
//									(((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(mapData.get("leader").equals("Claire Matthews"))) &&
//									(!mapData.get("lossCause").equals("null")))) ||
//									((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("leader").equals("Claire Matthews")))) || 
//									((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("null")))) ||
//									(((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(mapData.get("lossCause").equals("b")))) ||
//									((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(mapData.get("lossCause").equals("a")))) ||
//									((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern")))) ||
//									(((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(mapData.get("lossState").equals("QLD")))) ||
//									((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossState").equals("QLD"))) &&
//									(!mapData.get("lossCause").equals("null")))) ||
//									(((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing5)) { //INCOMING CURRE
					
//					info = request.substring(request.indexOf(routing5)+routing5.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((((((((((mapData.get("SYSSUBJECT").equals("Conduct file review")) ||
//									(((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(mapData.get("team").equals("IPI Vero - IBNA")))) ||
//									(((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(mapData.get("lossCause").equals("w")))) ||
//									(((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI")))) ||
//									((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Central"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI Platinum Claims - Central")))) ||
//									(((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(mapData.get("lossCause").equals("null"))) &&
//									(!mapData.get("leader").equals("Claire Matthews")))) ||
//									((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossCause").equals("null")))) ||
//									(((((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("team").equals("IPI Vero - IBNA"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("Quick Claims IPI"))) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(mapData.get("leader").equals("Claire Matthews"))) &&
//									(mapData.get("lossCause").equals("null")))) ||
//									((((((((((((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) <= 0.0)) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor Note Received on Job"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Vendor has submitted an invoice for Job Number"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("b"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(mapData.get("brand").equals("null"))) &&
//									(!mapData.get("ASSIGNEDTEAMNAME").equals("IPI AMP Fulfilment - Southern"))) &&
//									(!mapData.get("lossState").equals("QLD"))) &&
//									(mapData.get("lossCause").equals("null")))) ||
//									((!mapData.get("SYSSUBJECT").equals("Conduct file review")) &&
//									(Double.parseDouble(mapData.get("originalEstimate")) > 0.0)))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing6)) { //TR6
					
//					info = request.substring(request.indexOf(routing6)+routing6.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((!mapData.get("SYSSUBJECT").equals("Review and approve new payment")) &&
//									(Double.parseDouble(mapData.get("incAmt")) <= 1.64))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing7)) { //TR13
					
//					info = request.substring(request.indexOf(routing7)+routing7.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//							(mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//							(Double.parseDouble(mapData.get("originalEstimate")) <= 1545.45)) ||
//							((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//							(mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//							(Double.parseDouble(mapData.get("originalEstimate")) > 1545.45)) &&
//							(Double.parseDouble(mapData.get("originalEstimate")) <= 3615.51))) ||
//							(((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//							(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//							(!mapData.get("lossPostcode").equals("null"))) &&
//							(mapData.get("lossCause").equals("null"))) &&
//							(mapData.get("team").equals("Quick Claims IPI")))) ||
//							(((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//							(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//							(!mapData.get("lossPostcode").equals("null"))) &&
//							(!mapData.get("lossCause").equals("null"))) &&
//							(!mapData.get("lossState").equals("null"))))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing8)) { //TR23
					
//					info = request.substring(request.indexOf(routing8)+routing8.length());
//					mapData = convertStringToData(info);
//					
//					if(
//						(((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) ||
//						((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//						(mapData.get("lossState").equals("null")))) ||
//						(((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//						(!mapData.get("lossState").equals("null"))) &&
//						(mapData.get("lossCause").equals("null"))))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing9)) { //TR2
					
//					info = request.substring(request.indexOf(routing9)+routing9.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							(((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("null")))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}else if(request.startsWith(routing10)) { //TR24
					
//					info = request.substring(request.indexOf(routing10)+routing10.length());
//					mapData = convertStringToData(info);
//					
//					if(
//							((((((((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(mapData.get("leader").equals("Debbie Hunter"))) ||
//									(((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(mapData.get("lossPostcode").equals("null")))) ||
//									(((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(mapData.get("brand").equals("null")))) ||
//									((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(mapData.get("lossState").equals("null")))) ||
//									(((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(mapData.get("lossCause").equals("w")))) ||
//									((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(mapData.get("lossPostcode").equals("3")))) || 
//									(((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(mapData.get("lossPostcode").equals("5")))) ||
//									((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(!mapData.get("lossPostcode").equals("5"))) &&
//									(mapData.get("lossPostcode").equals("6")))) ||
//									(((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(!mapData.get("lossPostcode").equals("5"))) &&
//									(!mapData.get("lossPostcode").equals("6"))) &&
//									(mapData.get("lossCause").equals("null")))) ||
//									((((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(!mapData.get("lossPostcode").equals("5"))) &&
//									(!mapData.get("lossPostcode").equals("6"))) &&
//									(!mapData.get("lossCause").equals("null"))) &&
//									(mapData.get("lossPostcode").equals("2")))) ||
//									(((((((((((((((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(mapData.get("SYSSUBJECT").equals("Conduct file review"))) &&
//									(!mapData.get("leader").equals("Debbie Hunter"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("a"))) &&
//									(!mapData.get("brand").equals("null"))) &&
//									(!mapData.get("lossState").equals("null"))) &&
//									(!mapData.get("lossCause").equals("w"))) &&
//									(!mapData.get("lossPostcode").equals("3"))) &&
//									(!mapData.get("lossPostcode").equals("5"))) &&
//									(!mapData.get("lossPostcode").equals("6"))) &&
//									(!mapData.get("lossCause").equals("null"))) &&
//									(!mapData.get("lossPostcode").equals("2"))) &&
//									(!mapData.get("team").equals("IPI AMP Fulfilment - Cent")))) ||
//									(((Double.parseDouble(mapData.get("originalEstimate")) <= 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("null"))) &&
//									(!mapData.get("SYSSUBJECT").equals("Conduct file review")))) ||
//									(((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(mapData.get("lossPostcode").equals("null")))) ||
//									(((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(mapData.get("lossCause").equals("null"))) &&
//									(!mapData.get("team").equals("Quick Claims IPI")))) ||
//									(((((Double.parseDouble(mapData.get("originalEstimate")) > 0.0) &&
//									(!mapData.get("SYSSUBJECT").equals("Review correspondence"))) &&
//									(!mapData.get("lossPostcode").equals("null"))) &&
//									(!mapData.get("lossCause").equals("null"))) &&
//									(mapData.get("lossState").equals("null"))))
//					){
//						responce = "true"; 
//					}else {
//						responce = "false";
//					}
					responce = "false";
				}
				
//				System.out.println("responce: "+responce);
				
				conn.send(EncodeDecode.encode(responce));
				
			}
		
		}finally { 

//			fw.close();
			
		}
	}

	private static void removeOldComplete(HashMap<String, HashMap<String, Long>> enabledTask, HashMap<String, HashMap<String, Long>> completedTask, HashMap<String, HashMap<String, Object[]>> startedTask) {
		String oldCaseID = null;
		String oldTaskID = null;
		Long oldCompleteTime = null;
		boolean removed = true;
		
		do {
			oldCaseID = null;
			oldTaskID = null;
			oldCompleteTime = null;
			
			for(Entry<String, HashMap<String, Long>> entry1 : completedTask.entrySet()) {
				for(Entry<String, Long> entry2 : entry1.getValue().entrySet()) {
					if(oldCompleteTime == null || entry2.getValue() <= oldCompleteTime) {
						oldCaseID = entry1.getKey();
						oldTaskID = entry2.getKey();
						oldCompleteTime = entry2.getValue();
					}
				}
			}
			
			if(oldCompleteTime != null) {
				for(Entry<String, HashMap<String, Long>> entry1 : enabledTask.entrySet()) {
					for(Entry<String, Long> entry2 : entry1.getValue().entrySet()) {
						if(!entry1.getKey().equals(oldCaseID) &&
								!entry2.getKey().equals(oldTaskID) &&
								entry2.getValue() <= oldCompleteTime) {
							
							Long completeTime = completedTask.get(entry1.getKey()).get(entry2.getKey());
							if(completeTime == null) removed = false;
						}
					}
				}
				
				if(removed) {
					enabledTask.get(oldCaseID).remove(oldTaskID);
					startedTask.get(oldCaseID).remove(oldTaskID);
					completedTask.get(oldCaseID).remove(oldTaskID);
					System.out.println("removed");
				}
			}else {
				removed = false;
			}
		}while(removed);
	}


	private static Long getTime(HashMap<String, HashMap<WorkItemRecord, Long>> result, WorkItemRecord finalTask, String finalResurce) {
		HashMap<WorkItemRecord, Long> map = result.get(finalResurce);
		LinkedList<Entry<WorkItemRecord, Long>> list = new LinkedList<Entry<WorkItemRecord, Long>>();
		
		for(Entry<WorkItemRecord, Long> entry : map.entrySet()) {
			list.add(entry);
		}
		
		Comparator<Entry<WorkItemRecord, Long>> comparator = new Comparator<Map.Entry<WorkItemRecord,Long>>() {
			
			@Override
			public int compare(Entry<WorkItemRecord, Long> o1,
					Entry<WorkItemRecord, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		};
		
		Collections.sort(list, comparator);
		
		for(int i = 0; i<list.size(); i++) {
			if(list.get(i).getKey().equals(finalTask)) {
				return (long) (i+1);
			}
		}
		return null;
	}

	private static Long getOrigTime(HashMap<String, HashMap<WorkItemRecord, Long>> result, WorkItemRecord finalTask) {
		LinkedList<Entry<WorkItemRecord, Long>> list = new LinkedList<Entry<WorkItemRecord, Long>>();
		
		for(Entry<String, HashMap<WorkItemRecord, Long>> res : result.entrySet()) {
			HashMap<WorkItemRecord, Long> map = res.getValue();
			if(map.keySet().contains(finalTask)) {
				for(Entry<WorkItemRecord, Long> entry : map.entrySet()) {
					list.add(entry);
				}
				break;
			}
		}
		
		Comparator<Entry<WorkItemRecord, Long>> comparator = new Comparator<Map.Entry<WorkItemRecord,Long>>() {
			
			@Override
			public int compare(Entry<WorkItemRecord, Long> o1,
					Entry<WorkItemRecord, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		};
		
		Collections.sort(list, comparator);
		Long base = (list.get(0).getValue() - 1);
		for(int i = 0; i<list.size(); i++) {
			if(list.get(i).getKey().getRootCaseID().equals(finalTask.getRootCaseID())
					&& list.get(i).getKey().getTaskID().equals(finalTask.getTaskID())) {
				return list.get(i).getValue() - base;
			}
		}
		return null;
	}

	private static HashMap<String, HashSet<WorkItemRecord>> generateCan(HashSet<WorkItemRecord> relevantWorkItems,	HashMap<String, HashSet<WorkItemRecord>> use) {
		HashMap<String, HashSet<WorkItemRecord>> can = new HashMap<String, HashSet<WorkItemRecord>>();
		
		for(String res : resources) {
			can.put(res, new HashSet<WorkItemRecord>());
		}
		
		Collection<HashSet<WorkItemRecord>> used = use.values();
		
		for(Entry<String, HashSet<WorkItemRecord>> entry : use.entrySet()) {
			for(WorkItemRecord wir : entry.getValue()) {
				can.get(entry.getKey()).add(wir);
			}
		}
		
		for(WorkItemRecord wir : relevantWorkItems) {
			boolean contain = false;
			for(HashSet<WorkItemRecord> set2 : used) {
				if(set2.contains(wir)) contain = true;
			}
			if(!contain) {
				int count = 0;
				if(wir.getTaskID().equals(newClaim)) {
					for(String res : newClaimIPIResources) {
						if(count == ((int) (newClaimIPIResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(incoming)) {
					for(String res : incomingCorrespondenResources) {
						if(count == ((int) (incomingCorrespondenResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(conduct)) {
					for(String res : conductFileReviewResources) {
						if(count == ((int) (conductFileReviewResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(contact)) {
					for(String res : contactCustomerResources) {
						if(count == ((int) (contactCustomerResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(process)) {
					for(String res : processAdditionalInResources) {
						if(count == ((int) (processAdditionalInResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(authorize)) {
					for(String res : authorisePaymentIPResources) {
						if(count == ((int) (authorisePaymentIPResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(generate)) {
					for(String res : generatePaymentResources) {
						if(count == ((int) (generatePaymentResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(follow)) {
					for(String res : followUpRequestedResources) {
						if(count == ((int) (followUpRequestedResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}else if(wir.getTaskID().equals(close)) {
					for(String res : closeFileResources) {
						if(count == ((int) (closeFileResources.length / percentage))) break;
						can.get(res).add(wir);
						count++;
					}
				}
			}
		}
		
		return can;
	}

	public static void main1(String[] args) {
//		String data = "incAmt:0.0;originalEstimate:0.0;manager:;SYSSUBJECT:;leader:;ASSIGNEDTEAMNAME:;lossPostcode:;team:;lossCause:;lossState:;productLine6:;brand:";
//		getData(data);
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("lossCause", "t");
//		System.out.println(getNewValue("Close File", "manager", map));
		
		String routing1 = "ROUTE1";
		String request = "ROUTE1:incAmt:0.0;originalEstimate:0.0;manager:Donna Stewa;SYSSUBJECT:Follow up Insured;leader:CI Property Majo;ASSIGNEDTEAMNAME:IPI Platinum Claims - Southern;lossPostcode:null;team:CI Property Major Affect;lossCause:null;lossState:null;productLine6:Building;brand:null;ASSIGNEDUSERMNAME:Ryan Finn";
		String info = request.substring(request.indexOf(routing1)+routing1.length());
		HashMap<String, String> map = convertStringToData(info);
		
		if(
				((map.get("SYSSUBJECT") != "Review and approve new payment") && (Double.parseDouble(map.get("incAmt")) <= 1.64))
		){
			System.out.println("true"); 
		}else {
			System.out.println("false");
		}
	}
	
	private static String getNewValue2(String task, String name,
			HashMap<String, String> map) {
		
		J48 j48Tree = null;
		Instances instances = null;
		REPTree repTree = null;
		
		ObjectInputStream ois = null;
		
		String base = "/home/stormfire/Dropbox/workspace/RiskInformedExecution/Data/";
		
		try {
			File insta = new File(base+task+"_Instances_"+name);
			if(insta.exists()) {
				ois = new ObjectInputStream(new FileInputStream(base+task+"_Instances_"+name));
				instances = (Instances) ois. readObject();
			}else {
				return map.get(name);
			}
			
			File tree = new File(base+task+"_J48Tree_"+name);
			if(tree.exists()) {
				ois = new ObjectInputStream(new FileInputStream(base+task+"_J48Tree_"+name));
				j48Tree = (J48) ois.readObject();
			}else {
				ois = new ObjectInputStream(new FileInputStream(base+task+"_REPTree_"+name));
				repTree = (REPTree) ois.readObject();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Instance instance = new DenseInstance(instances.numAttributes());
		instance.setDataset(instances);
		
		WekaEnumeration atts = (WekaEnumeration) instance.enumerateAttributes();
		Object elem = null;
		
		while(atts.hasMoreElements()) {
			elem = atts.nextElement();
			Attribute att = (Attribute) elem;
			if(att.name().equals("ASSIGNEDTOUSERNAME")) {
				
			}else if(map.get(att.name()) != null && !map.get(att.name()).isEmpty()) {
				insertAttribute(instance, att, map.get(att.name()));
			}else {
				insertAttribute(instance, att, "null");
			}
		}

		try {
			if(j48Tree != null) {
				double res = j48Tree.classifyInstance(instance);
				return instance.classAttribute().value((int) res);
			}else {
				double res = repTree.classifyInstance(instance);
				return instance.classAttribute().value((int) res);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String getNewValue(String task, String name,
			HashMap<String, String> map, String base, boolean change) {
		
//		HashMap<String, HashMap<String, LinkedList<String>>> data = null;
		ObjectInputStream ois = null;
		
//		String base = "/home/stormfire/Dropbox/workspace/RiskInformedExecution/";
		
		if(data == null) {
			try {
				ois = new ObjectInputStream(new FileInputStream(base+"MAP"));
				data = (HashMap<String, HashMap<String, LinkedList<String>>>) ois. readObject();
				for(Entry<String, HashMap<String, LinkedList<String>>> entry : data.entrySet()) {
					for(Entry<String, LinkedList<String>> entry2 : entry.getValue().entrySet()) {
						for(String l : entry2.getValue()) {
							dataValues.put(l, l);
						}
					}
				}
				ois.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		LinkedList<String> list = null;
		String res = map.get(name);
		if(task.equals(conduct)){
			if(!change && !(res.equals("null") || res.equals("0.0")) && !name.equals(syssubject)) return res;
			if(name.equals(leader) || name.equals(manager) || name.equals(productLine) || name.equals(team) || name.equals(syssubject)) {
				list = data.get(task).get(name);
				res = list.get(random.nextInt(list.size()));
			}
		}else if(task.equals(incoming)){
			if(!change && !(res.equals("null") || res.equals("0.0")) && !name.equals(syssubject)) return res;
			if(name.equals(losscause) || name.equals(lossstate) || name.equals(losspostcode) || name.equals(syssubject)) {
				list = data.get(task).get(name);
				res = list.get(random.nextInt(list.size()));
			}
		}else if(task.equals(generate)){
			if(!change && !(res.equals("null") || res.equals("0.0")) && !name.equals(syssubject)) return res;
			if(name.equals(originalEstimate) || name.equals(syssubject)) {
				list = data.get(task).get(name);
				res = list.get(random.nextInt(list.size()));
			}
		}else if(task.equals(authorize)){
			if(!change && !(res.equals("null") || res.equals("0.0")) && !name.equals(syssubject)) return res;
			if(name.equals(originalEstimate) || name.equals(incAmt) || name.equals(syssubject)) {
				list = data.get(task).get(name);
				res = list.get(random.nextInt(list.size()));
			}
		}else if(task.equals(process) || task.equals(newClaim) || task.equals(follow) || task.equals(close) || task.equals(contact)) {
			if(name.equals(syssubject)) {
				list = data.get(task).get(name);
				res = list.get(random.nextInt(list.size()));
			}
		}

//		String res = list.get(random.nextInt(list.size()));
//		System.out.println(res);
		return res;
		
	}
	
	private static void insertAttribute(Instance instance, Attribute att, String value) {
		try {
			if(att.isNominal()) {
				if(!value.isEmpty()) {
					instance.setValue(att, value);
				}else {
					instance.setValue(att, "null");
				}
			}else if(att.isNumeric()) {
				instance.setValue(att, Double.parseDouble(value));
			}
		}catch(IllegalArgumentException e) {
			instance.setMissing(att);
		}
	}
	
	private static String getTeam(String user) {
		if(user.equals("Matthew Harvey")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Ryan Anderson-Smith")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Anthony Qiu")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Matthew Vincent")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Janet Mataia")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Ramkumar Hariram")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Michi Hollis")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Kavita Krishna")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Anita Morrall")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Treesa McInnes")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Mili Kapur")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Matthew Collett")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Helen Georgiou")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Angela Attard")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Shane Muddell")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Richard Roberts")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Emmett O'Reilly")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Daniel Cregan")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Rebecca Utro")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Lee Sithisakd")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Ayla Aydin")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Natalie Lock")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Koshi George")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Prashant Singh")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Angela Gray")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Katie MClay")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Mark Jeffrey")) return "IPI Vero Fulfilment";
		if(user.equals("Janelle Bantick")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Claire Matthews")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Andrea Gynn")) return "Quick Claims IPI";
		if(user.equals("Zachary Gibbs")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Justine Meniconi")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Kirsten Nuttall")) return "IPI Vero - Steadfast";
		if(user.equals("Aaron Thirukumar")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Kirralee Steward")) return "IPI Platinum Claims - Southern";
		if(user.equals("Glenda McTavish")) return "IPI Platinum Claims - Southern";
		if(user.equals("Leanne Brischetto")) return "IPI Platinum Claims - Southern";
		if(user.equals("David Wistrom")) return "Property Fulfilment and Operations";
		if(user.equals("Megan Creasey")) return "IPI Platinum Claims - Southern";
		if(user.equals("Avee Sharma")) return "IPI Platinum Claims - Southern";
		if(user.equals("Kahlee Robertson")) return "Commercial Insurance Home Claims Fulf";
		if(user.equals("Nathan de Pratt")) return "IPI Platinum Claims - Southern";
		if(user.equals("Sue Wharley")) return "IPI Platinum Claims - Southern";
		if(user.equals("Michelle Ebsworth")) return "IPI Vero Fulfilment";
		if(user.equals("Scott Davidson")) return "IPI Platinum Claims - Southern";
		if(user.equals("Scott Cook")) return "IPI Platinum Claims - Southern";
		if(user.equals("Sylvia Jedras")) return "IPI Platinum Claims - Southern";
		if(user.equals("Stephanie King")) return "IPI Platinum Claims - Southern";
		if(user.equals("Alicia Payne")) return "Property Fulfilment and Operations";
		if(user.equals("Sharon Kennedy")) return "Property Fulfilment and Operations";
		if(user.equals("Nana Ansuh Yeboah")) return "Corporate and Major Loss Claims";
		if(user.equals("Matthew Gray")) return "IPI Platinum Claims - Southern";
		if(user.equals("Rhiannon Merchant")) return "IPI Platinum Claims - Southern";
		if(user.equals("Karen Davis")) return "IPI Platinum Claims - Southern";
		if(user.equals("Peter Becvarovski")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("N")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Douglas Heim")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Leon Savelio")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Kirsty Azzopardi")) return "conSOL User Group";
		if(user.equals("Liesa Bruhn")) return "Quick Claims IPI";
		if(user.equals("Andrew Ejubs")) return "IPI Platinum Claims - Southern";
		if(user.equals("Sara Davis")) return "IPI Platinum Claims - Southern";
		if(user.equals("George Healy")) return "IPI Platinum Claims - Southern";
		if(user.equals("Ashleigh McCourt")) return "IPI Vero Fulfilment";
		if(user.equals("Aisling Fleming")) return "IPI Platinum Claims - Southern";
		if(user.equals("Kareena Assem")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Deborah Holl")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Simon Wardhaugh")) return "IPI Vero - Austbrokers";
		if(user.equals("Marie Martin")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Lee Hamilton")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Natalie Doyle")) return "Quick Claims IPI";
		if(user.equals("Ryan Finn")) return "IPI Platinum Claims - Southern";
		if(user.equals("Patricia Romero")) return "IPI Platinum Claims - Southern";
		if(user.equals("Jason Forbes")) return "IPI Platinum Claims - Southern";
		if(user.equals("Debbie Hunter")) return "IBNA IPI";
		if(user.equals("Tania Whitbread")) return "Quick Claims IPI";
		if(user.equals("Shantel Netzler")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Bill Burmester")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Clare Binns")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Lynette Braddock")) return "IPI Vero Fulfilment";
		if(user.equals("Susan Dunlop")) return "IPI Vero - IBNA";
		if(user.equals("Luke Patrick")) return "Quick Claims IPI";
		if(user.equals("Natalie Gilmour")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Allira Sandoval")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Abhishek Jain")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Samantha Whittemore")) return "OAMPS";
		if(user.equals("Carly Humphreys")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Matthew Davis")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Dwayne Tourell")) return "Corporate and Major Loss Claims";
		if(user.equals("Peter Henry")) return "Corporate and Major Loss Claims";
		if(user.equals("Luke Smith")) return "IPI Complex Claims";
		if(user.equals("Kristian O'Keefe")) return "IPI Vero Fulfilment";
		if(user.equals("Phil O'Donnell")) return "IPI Vero - IBNA";
		if(user.equals("Shauna Harley")) return "IPI Vero - IBNA";
		if(user.equals("Shirley Dunnigan")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Nikhil Todurkar")) return "IPI Vero - Steadfast";
		if(user.equals("Shehzad Sardar")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Vinoshi De Fonseka")) return "IPI Vero Fulfilment";
		if(user.equals("Michelle Talay")) return "IPI Vero Fulfilment";
		if(user.equals("Eloise McCarthy")) return "IPI Vero - Steadfast";
		if(user.equals("Kevin Murphy")) return "IPI Vero Fulfilment";
		if(user.equals("Jessica Borchard")) return "IPI Vero Fulfilment";
		if(user.equals("Daniel Aycock")) return "Quick Claims IPI";
		if(user.equals("Toni Emmett")) return "IPI Vero Fulfilment";
		if(user.equals("Nick Mann")) return "Property Assessing and Supplier Relat";
		if(user.equals("Michael Jessop")) return "IPI Vero - Steadfast";
		if(user.equals("Rebecca Conyngham")) return "Property Fulfilment and Operations";
		if(user.equals("Vashti Keyes")) return "Quick Claims IPI";
		if(user.equals("Patricia Perri")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Anne Nguyen")) return "Quick Claims IPI";
		if(user.equals("Vanessa Varley")) return "Commercial Insurance First Response U";
		if(user.equals("Bradley Warren")) return "IPI Vero - IBNA";
		if(user.equals("Norma Ballinger")) return "IPI Vero - Steadfast";
		if(user.equals("Andrei Bennett")) return "IPI AMP Fulfilment - Southern";
		if(user.equals("Lisa-Maree Baiada")) return "IPI AMP Fulfilment - Central";
		if(user.equals("Viviene Franco")) return "Property Assessing and Supplier Relat";
		if(user.equals("Cherie Searle")) return "IPI Vero - IBNA";
		if(user.equals("Mark Smeaton")) return "Quick Claims IPI";
		if(user.equals("Kathryn King")) return "Commercial Insurance Property";
		if(user.equals("Timothy Ferguson")) return "conSOL User Group";
		return null;
	}

	private static String[] convertStringToTasks(String tasks) {
		LinkedList<String> list = new LinkedList<String>();
		String singleTask = null;
	
		StringTokenizer st = new StringTokenizer(tasks, ":");

		while(st.hasMoreTokens()) {
			singleTask = st.nextToken();
			if(singleTask.equals(newClaim)) {
				singleTask = newClaim;
			}else if(singleTask.equals(incoming)) {
				singleTask = incoming;
			}else if(singleTask.equals(conduct)) {
				singleTask = conduct;
			}else if(singleTask.equals(contact)) {
				singleTask = contact;
			}else if(singleTask.equals(process)) {
				singleTask = process;
			}else if(singleTask.equals(authorize)) {
				singleTask = authorize;
			}else if(singleTask.equals(generate)) {
				singleTask = generate;
			}else if(singleTask.equals(follow)) {
				singleTask = follow;
			}else if(singleTask.equals(close)) {
				singleTask = close;
			}
			list.add(singleTask);
		}
		return list.toArray(new String[0]);
	}

	private static HashMap<String, String> convertStringToData(String data) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		StringTokenizer st = new StringTokenizer(data, ":;", true);
		boolean name = false;
		int delimitator = 0;
		
		String currName = null;
		String currValue = "";
		
		String token = null;
		
		while(st.hasMoreElements()) {
			token = st.nextToken();
			if(token.equals(":")) {
				delimitator++;
			}else if(token.equals(";")) {
				map.put(currName, currValue);
				delimitator = 0;
				currValue = "";
				name = false;
			}else if(!name) {
				currName = token;
				delimitator = 0;
				name = true;
			}else {
				currValue = token;
				delimitator = 0;
			}
		}
		map.put(currName, currValue);
		
		return map;
	}
	
	private static String convertDataToString(HashMap<String, String> map) {
		StringBuffer sb = new StringBuffer();
		
		String[] variables = new String[] {"incAmt", "originalEstimate", "manager", "SYSSUBJECT", "leader", 
				"ASSIGNEDTEAMNAME", "lossPostcode",	"team", "lossCause", "lossState", "productLine6", "brand"};
		for(int i = 0; i < variables.length; i++) {
			sb.append(variables[i]);
			sb.append(":");
			if(map.get(variables[i]) != null) {
				sb.append(map.get(variables[i]));
			}
			if(i < variables.length-1) sb.append(":");
		}
		
		return sb.toString();
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
		}catch(Exception e) {System.out.println("prob");}
	}
	
	private static void updateParameters(String newPath, String logBaseString) {
		
//		fixLine(newPath, logBaseString);
		
		try {
			if(!first) {
				log = (XLog) originalLog.clone();
				log.clear();
				parameters.put("Xlog", log);
				first = true;
			}else {
				parameters.put("Xlog", log);
			}
//			originalLog.addAll(log);
			
//			parameters.put("Xlog", log);
			
//			parameters.put("Xlog", originalLog);
			
			a.updateInterfaces(InterfaceManager.PROM, parameters);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void fixLine(String newPath, String logBaseString) {
		
		File file = new File(logBaseString+"Log.xes");
		File tmpFile = new File(newPath);
		copyFile(file, tmpFile, true);	
		
		for(int i = 1; i < 1001; i++) {
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

	
//	public static void main2(String[] args) throws Exception {
//		
////		originalLog = ImportEventLog.importFromStream(xfactory, newPath);
//		
//		fw = new FileWriter(file, true);
//		
//		File file = new File("/media/Data/SharedFolder/MXMLlogs/750/LogOriginal.xes");
//		File tmpFile = new File(originPath);
//		copyFile(file, tmpFile);
//		
//		try {
//		
//			int port = 9000;
//			JavaCPN conn = new JavaCPN();
//			OperationalSupportMultiCaseApp oss = initializeMultiCase();
//			
//			String enabled = "enabled:";
//			String started = "started:";
//			String completed = "completed:";
//			
//			String res = "resource:"; 
//			String loop = "loop:";
//			String request = "", responce = "";
//			HashMap<String, Long> map1 = null;
//			HashMap<WorkItemRecord, Long> map2 = null;
//			
//			HashMap<String, WorkItemRecord> mapNameWorkItem = new HashMap<String, WorkItemRecord>();
//			
////			request = "resource:2001:Estimate_Trailer_Usage_386:Carrier_Appointment";
//			// listen on port and accept connection
//			System.out.println("service started");
//			conn.accept(port);
//			while (true) {
//			
//				request = EncodeDecode.decodeString(conn.receive());
//
//				System.out.println(request);
//				updateParameters(originPath, newPath);
//				
//				if(request.startsWith(res)) {
//					
//					
//					String task = request.substring(res.length());
//					String[] ws = identifyNextAndLastAndTime(";;"+task+";;");
//					
//					map1 = oss.suggestResourceDecisionPoints(generateWorkItem(ws[1]));
//					System.out.println(map1);
//	
//					Long min = Long.MAX_VALUE;
//					
//					for(Entry<String, Long> e : map1.entrySet()) {
//						
//						if(e.getValue() < min) {
//							min = e.getValue();
//						}
//						
//					}
//
//					Set<String> tmp = new HashSet<String>();
//					for(Entry<String, Long> e : map1.entrySet()) {
//						
//						if(e.getValue().equals(min)) {
//							tmp.add(e.getKey());
//						}
//						
//					}
//
//					boolean diff = (tmp.size() != map1.size());
//					
////					if(map.keySet().size() > 1) {
//						if(false && diff) {
//							
//							Set<String> set = map1.keySet();
//							set.removeAll(tmp);
//							String[] array = set.toArray(new String[0]);
//							
//							responce = array[random.nextInt(array.length)];
//							
//	//						System.out.println(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" notfollowed");
//	//						System.out.println(map);
//	//						System.out.println(r);
//							fw.append(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" notfollowed\n");
//							fw.flush();
//							
//						}else if(diff) {
//							
//							String[] array = tmp.toArray(new String[0]);
//							
//							responce = array[random.nextInt(array.length)];
//							
//	//						System.out.println(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" followed");
//	//						System.out.println(map);
//	//						System.out.println(r);
//							fw.append(generateWorkItem(ws[1]).getCaseID()+" resource "+generateWorkItem(ws[1]).getTaskID()+" followed\n");
//							fw.flush();
//							
//						}else {
//							
//							String[] array = tmp.toArray(new String[0]);
//							
//							responce = array[random.nextInt(array.length)];
//						}
////					}
//					
//				}else if(request.startsWith(enabled)) {
//					
//					String workItemID = null;
//					WorkItemRecord wir = null;
//					
//					String caseID = null;
//					String taskID = null;
//					
//					if((wir = mapNameWorkItem.get(workItemID)) == null) {
//						
//						wir = new WorkItemRecord(caseID, taskID, specID, null, null);
//						
//					}
//						
//					oss.receiveEvent(wir, true, false);
//				
//				}else if(request.startsWith(started)) {
//					
//					String workItemID = null;
//					WorkItemRecord wir = null;
//					
//					wir = mapNameWorkItem.get(workItemID);
//						
//					oss.receiveEvent(wir, true, true);
//				
//				}else if(request.startsWith(completed)) {
//					
//					String workItemID = null;
//					WorkItemRecord wir = null;
//					
//					wir = mapNameWorkItem.get(workItemID);
//					mapNameWorkItem.remove(workItemID);
//						
//					oss.receiveEvent(wir, false, false);
//				
//				}else {
//					
//					boolean keepLoop = false;
//					if(request.startsWith(loop)) {
//						keepLoop = true;
//						request = request.substring(loop.length());
//					}
//					
//					String[] ws = identifyNextAndLastAndTime(request);
//					
//					map2 = oss.suggestDecisionPoints(generateWorkItems(ws[0]), null, Long.parseLong(ws[2]));
//					System.out.println(map2);
//					
//					Long min = Long.MAX_VALUE;
//					
//					for(Entry<WorkItemRecord, Long> e : map2.entrySet()) {
//						
//						if(e.getValue() < min) {
//							min = e.getValue();
//						}
//						
//					}
//
//					Set<WorkItemRecord> tmp = new HashSet<WorkItemRecord>();
//					for(Entry<WorkItemRecord, Long> e : map2.entrySet()) {
//						
//						if(e.getValue().equals(min)) {
//							tmp.add(e.getKey());
//						}
//						
//					}
//
//					boolean diff = tmp.size() != map2.size();
//					
////					if(map.keySet().size() > 1) {
//						if(false && diff) {
//							
//							Set<WorkItemRecord> set = map2.keySet();
//							set.removeAll(tmp);
//							WorkItemRecord[] array = set.toArray(new WorkItemRecord[0]);
//							
//							responce = array[random.nextInt(array.length)].getTaskID();
//							
//	//						System.out.println(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" notfollowed");
//	//						System.out.println(map);
//	//						System.out.println(r);
//							fw.append(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" notfollowed\n");
//							fw.flush();
//							
//						}else if(diff) {
//
//							WorkItemRecord[] array = tmp.toArray(new WorkItemRecord[0]);
//							
//							if(keepLoop && tmp.size()>1) {
//								for(WorkItemRecord t : array) {
//									if(!t.getTaskID().equals("Produce_Shipment_Notice_401")) {
//										responce = t.getTaskID();
//										break;
//									}
//								}
//							}else {
//
//								responce = array[random.nextInt(array.length)].getTaskID();
//								
//							}
//							
//	//						System.out.println(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" followed");
//	//						System.out.println(map);
//	//						System.out.println(r);
//							fw.append(generateWorkItems(ws[0])[0].getCaseID()+" route "+((generateWorkItem(ws[1]) != null)?generateWorkItem(ws[1]).getTaskID():"null")+" followed\n");
//							fw.flush();
//							
//						}else {
//							
//							WorkItemRecord[] array = tmp.toArray(new WorkItemRecord[0]);
//							
//							if(keepLoop && tmp.size()>1) {
//								for(WorkItemRecord t : array) {
//									if(!t.getTaskID().equals("Produce_Shipment_Notice_401")) {
//										responce = t.getTaskID();
//										break;
//									}
//								}
//							}else {
//
//								responce = array[random.nextInt(array.length)].getTaskID();
//								
//							}
//							
//						}
////					}
//				
//				}
//				
//				conn.send(EncodeDecode.encode(responce));
//				
//			}
//		
//		}finally { 
//
//			fw.close();
//			
//		}
//	}
//	
//	private static boolean diffProb(Collection<Long> collection) {
//		Long curr = null;
//		boolean first = true;
//		for(Long l : collection) {
//			if(first) {
//				curr = l;
//				first = false;
//			}
//			if(curr != null && l != null && !curr.equals(l)) return true;
//			if(curr == null && l != null) curr = l;
//		}
//		return false;
//	}
//
//	private static void fixLine(String originPath, String newPath) {
//		
//		File file = new File(originPath);
//		
//		File tmpFile = new File(newPath);
//		
//		copyFile(file, tmpFile);	
//		
//		String lastLine = line(newPath);
//			
//		try {
//			FileWriter fw1 = new FileWriter(tmpFile, true);
//			if(!"</trace>".equals(lastLine) ) {
//				fw1.append("</trace>");
//				fw1.flush();
//			}
//			fw1.append("</log>");
//			fw1.flush();
//			fw1.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	private static void copyFile(File sourceFile, File destFile) {
//		try {
//		    if(!destFile.exists()) {
//		        destFile.createNewFile();
//		    }else {
//		    	destFile.delete();
//		    	destFile.createNewFile();
//		    }
//	
//		    FileChannel source = null;
//		    FileChannel destination = null;
//	
//		    try {
//		        source = new FileInputStream(sourceFile).getChannel();
//		        destination = new FileOutputStream(destFile).getChannel();
//		        
//		        long count = 0;
//		        long size = source.size();              
//		        while((count += destination.transferFrom(source, count, size-count))<size);
//		    }
//		    finally {
//		        if(source != null) {
//		            source.close();
//		        }
//		        if(destination != null) {
//		            destination.close();
//		        }
//		    }
//		}catch(Exception e) {System.out.println("prob");}
//	}
//	
//	private static void updateParameters(String originPath, String newPath) {
//		
//		fixLine(originPath, newPath);
//		
//		try {
//			XLog log = ImportEventLog.importFromStream(xfactory, newPath);
//			
////			originalLog.addAll(log);
//			
//			parameters.put("Xlog", log);
////			parameters.put("Xlog", originalLog);
//			
//			a.updateInterfaces(InterfaceManager.PROM, parameters);
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private static String convertMap(HashMap<String, Long> map) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	private static String line(String path) {
//		File f = new File(path);
//		RandomAccessFile fileHandler = null;
//		
//		try {
//			try {
//				fileHandler = new RandomAccessFile(f, "r");
//				long fileLength = f.length() - 1;
//				StringBuilder sb = new StringBuilder();
//	
//		        for( long filePointer = fileLength; filePointer != -1; filePointer-- ) {
//		            fileHandler.seek( filePointer );
//		            int readByte = fileHandler.readByte();
//	
//		            if( readByte == 0xA ) {
//		                if( filePointer == fileLength ) {
//		                    continue;
//		                } else {
//		                    break;
//		                }
//		            } else if( readByte == 0xD ) {
//		                if( filePointer == fileLength - 1 ) {
//		                    continue;
//		                } else {
//		                    break;
//		                }
//		            }
//	
//		            sb.append( ( char ) readByte );
//		        }
//	
//		        String lastLine = sb.reverse().toString();
//		        return lastLine;
//		    } catch( java.io.FileNotFoundException e ) {
//		        e.printStackTrace();
//		        return null;
//		    } catch( java.io.IOException e ) {
//		        e.printStackTrace();
//		        return null;
//		    } 
//		}finally { try {
//			fileHandler.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} }
//	}
//
//	public static OperationalSupportApp initialize() {
//		try {
//			
//			XLog log = ImportEventLog.importFromStream(xfactory, "/media/Data/SharedFolder/MXMLlogs/750/TestLog.xes");
//
//			String specification = null;
//			try {
//				File f = new File("/home/stormfire/CarrierAppointment4.yawl");
//				InputStream is = new FileInputStream(f);
//				Writer writer = new StringWriter();
//				char[] buffer = new char[1024];
//				try {
//					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					int n;
//					while ((n = reader.read(buffer)) != -1) {
//						writer.write(buffer, 0, n);
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				specification = writer.toString();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			String resources = null;
//			try {
//				File f = new File("/home/stormfire/orderfulfillment.ybkp");
//				InputStream is = new FileInputStream(f);
//				Writer writer = new StringWriter();
//				char[] buffer = new char[1024];
//				try {
//					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					int n;
//					while ((n = reader.read(buffer)) != -1) {
//						writer.write(buffer, 0, n);
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				resources = writer.toString();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			parameters.put("Xlog", log);
//			parameters.put("specification", specification);
//			parameters.put("resources", resources);
//			
//			a = new InterfaceManager(InterfaceManager.PROM, parameters);
//			
//			OperationalSupportApp oss = new OperationalSupportApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 5");
//			
//			oss.start(specID, null);
//			
//			return oss;
//			
//		} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		
//		return null;
//			
//	}
//	
//	public static OperationalSupportMultiCaseApp initializeMultiCase() {
//		try {
//			
//			XLog log = ImportEventLog.importFromStream(xfactory, "/media/Data/SharedFolder/MXMLlogs/750/TestLog.xes");
//
//			String specification = null;
//			try {
//				File f = new File("/home/stormfire/CarrierAppointment4.yawl");
//				InputStream is = new FileInputStream(f);
//				Writer writer = new StringWriter();
//				char[] buffer = new char[1024];
//				try {
//					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					int n;
//					while ((n = reader.read(buffer)) != -1) {
//						writer.write(buffer, 0, n);
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				specification = writer.toString();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			String resources = null;
//			try {
//				File f = new File("/home/stormfire/orderfulfillment.ybkp");
//				InputStream is = new FileInputStream(f);
//				Writer writer = new StringWriter();
//				char[] buffer = new char[1024];
//				try {
//					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					int n;
//					while ((n = reader.read(buffer)) != -1) {
//						writer.write(buffer, 0, n);
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				resources = writer.toString();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			parameters.put("Xlog", log);
//			parameters.put("specification", specification);
//			parameters.put("resources", resources);
//			
//			a = new InterfaceManager(InterfaceManager.PROM, parameters);
//			
//			OperationalSupportMultiCaseApp oss = new OperationalSupportMultiCaseApp(a, a, "weka.classifiers.trees.J48", "-C 0.25 -B -M 5", 100);
//			
//			oss.start(specID, null);
//			
//			return oss;
//			
//		} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		
//		return null;
//			
//	}
//	
//	public static WorkItemRecord[] generateWorkItems(String request) {
//		
//		LinkedList<WorkItemRecord> listWorkItems = new LinkedList<WorkItemRecord>();
//		
//		StringTokenizer st = new StringTokenizer(request, ";");
//		
//		while(st.hasMoreTokens()) {
//			
//			String token = st.nextToken();
//						
//			listWorkItems.add(generateWorkItem(token));
//			
//		}
//		
//		return listWorkItems.toArray(new WorkItemRecord[0]);
//		
//	}
//	
//	public static WorkItemRecord generateWorkItem(String request) {
//					
//		StringTokenizer st1 = new StringTokenizer(request, ":");
//		
//		try {
//			String caseID = st1.nextToken();
////			System.out.println(caseID);
//			String taskID = st1.nextToken();
////			System.out.println(taskID);
//			String specURI = st1.nextToken();
////			System.out.println(specURI);
//			
//			return new WorkItemRecord(caseID, taskID, specURI, null, null);
//		}catch(NoSuchElementException nsee) {
//			return null;
//		}
//		
//	}
//	
//	public static String[] identifyNextAndLastAndTime(String request) {
//		
//		String[] res = new String[3];
//		
//		System.out.println("Request: "+request);
//		res[0] = request.substring(0, request.indexOf(";;"));
//		res[1] = request.substring(request.indexOf(";;")+2, request.lastIndexOf(";;"));
//		res[2] = request.substring(request.lastIndexOf(";;")+2);
//		
//		return res;
//		
//	}

}
