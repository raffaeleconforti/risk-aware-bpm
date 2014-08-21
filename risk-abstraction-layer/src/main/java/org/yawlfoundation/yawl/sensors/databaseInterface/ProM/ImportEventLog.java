package org.yawlfoundation.yawl.sensors.databaseInterface.ProM;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Import Event Log.
 * 
 * This plugin imports the Event Log.
 * 
 * @author Keith Low Wei Zhe (weizhe_1990@hotmail.com)
 */

public class ImportEventLog {

	public static XLog importFromStream(XFactory factory, String location) throws Exception {
		XParser parser = new XesXmlParser(factory);

		Collection<XLog> logs = null;
		try {
			logs = parser.parse(new File(location));
		} catch (Exception e) {
			e.printStackTrace();
			logs = null;
		}
		if (logs == null) {
			// try any other parser
			for (XParser p : XParserRegistry.instance().getAvailable()) {
				if (p == parser) {
					continue;
				}
				try {
					logs = p.parse(new File(location));
					if (logs.size() > 0) {
						break;
					}
				} catch (Exception e1) {
					// ignore and move on.
					logs = null;
				}
			}
		}

		// log sanity checks;
		// notify user if the log is awkward / does miss crucial information
		if (logs == null || logs.size() == 0) {
			throw new Exception("No processes contained in log!");
		}

		XLog log = logs.iterator().next();
		if (XConceptExtension.instance().extractName(log) == null) {
			XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
		}

		if (log.isEmpty()) {
			throw new Exception("No process instances contained in log!");
		}

		return log;

	}
	
	public static XLog importFromStream(XFactory factory, InputStream is) throws Exception {
		XParser parser = new XesXmlParser(factory);

		Collection<XLog> logs = null;
		try {
			logs = parser.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
			logs = null;
		}
		if (logs == null) {
			// try any other parser
			for (XParser p : XParserRegistry.instance().getAvailable()) {
				if (p == parser) {
					continue;
				}
				try {
					logs = p.parse(is);
					if (logs.size() > 0) {
						break;
					}
				} catch (Exception e1) {
					// ignore and move on.
					logs = null;
				}
			}
		}

		// log sanity checks;
		// notify user if the log is awkward / does miss crucial information
		if (logs == null || logs.size() == 0) {
			throw new Exception("No processes contained in log!");
		}

		XLog log = logs.iterator().next();
		if (XConceptExtension.instance().extractName(log) == null) {
			XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
		}

		if (log.isEmpty()) {
			throw new Exception("No process instances contained in log!");
		}

		return log;

	}
	
	private static String getXMLlog(XLog log) {
		XSerializer serializer = new XesXmlSerializer();
		
		ByteArrayOutputStream byte1=new ByteArrayOutputStream();  
		
		try {
			serializer.serialize(log, byte1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return byte1.toString();  
	}

	public InputStream getInputStream(File file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		if (file.getName().endsWith(".gz") || file.getName().endsWith(".xez")) {
			return new GZIPInputStream(stream);
		}
		if (file.getName().endsWith(".zip")) {
			ZipFile zip = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			ZipEntry zipEntry = entries.nextElement();
			if (entries.hasMoreElements()) {
				throw new InvalidParameterException("Zipped log files should not contain more than one entry.");
			}
			return zip.getInputStream(zipEntry);
		}
		return stream;
	}

	public static void exportLog(String path, String name, XLog log) {
		XesXmlSerializer serializer = new XesXmlSerializer();
		 
        FileOutputStream outputStream = null;
        try {
               outputStream = new FileOutputStream(new File(path + name));
               serializer.serialize(log, outputStream);
               outputStream.close();
//               GZIPOutputStream gz = new GZIPOutputStream(outputStream);
//               gz.
        } catch (Exception e) {
               System.out.println("Error");
        }
	}
	
}