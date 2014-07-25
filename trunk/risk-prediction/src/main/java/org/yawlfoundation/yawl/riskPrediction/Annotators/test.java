package org.yawlfoundation.yawl.riskPrediction.Annotators;

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
import java.util.Vector;

public class test {

	public static void main(String[] args) {
		
//		String specification = null;
//		try {
//			File f = new File("/home/stormfire/Documents/Useless/log.xes");
//			InputStream is = new FileInputStream(f);
//			Writer writer = new StringWriter();
//			char[] buffer = new char[1024];
//			try {
//				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//				int n;
//				while ((n = reader.read(buffer)) != -1) {
//					writer.write(buffer, 0, n);
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			specification = writer.toString();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		for(int i = 0; i<25; i++)
//		specification = specification.replaceFirst("&lt;PostCode&gt;10000&lt;/PostCode&gt;", "&lt;PostCode&gt;11000&lt;/PostCode&gt;");
//		
//		System.out.println(specification);
		
//		String s = "colset CUSTOMER = subset STRING with [";
//		
//		for(int i = 1; i<101; i++) {
//			s += "\"customer"+i+"\"";
//			if(i < 100)
//				s += ", ";
//			
//		}
//		System.out.println(s+"];");
		
		Vector<String> v = new Vector<String>();
		v.add("bla");
		System.out.println(v.contains("bla"));
		System.out.println(v.indexOf("bla"));
		
		System.out.println(Long.valueOf("12345678"));
	}
	
}
