import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;


public class convert {
	
	public static void main(String[] args) throws IOException {
		Scanner console = new Scanner(System.in);
		String file = console.next();
		File f = new File(file+".mxml");
		InputStream is = new FileInputStream(f);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		String s = writer.toString();
		for(int i=0;i<1000;i++) {
			s = s.replace("."+i+"</Attribute>", ","+i+"</Attribute>");
			s = s.replace("."+i+"</attribute>", ","+i+"</attribute>");
		}
		File f1 = new File("logCorretto.mxml");
		FileOutputStream fos = new FileOutputStream(f1);
		fos.write(s.getBytes("UTF-8"));
		fos.flush();
		fos.close();
	}

}
