import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.jdom2.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.interfaceE.YLogGatewayClient;
import org.yawlfoundation.yawl.resourcing.rsInterface.ResourceLogGatewayClient;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class Import{

	private static ResourceLogGatewayClient _resClient = new ResourceLogGatewayClient("http://localhost:8080/resourceService/logGateway");
	private static YLogGatewayClient _logClient = new YLogGatewayClient("http://localhost:8080/yawl/logGateway");
	
	public static void main(String[] args) throws IOException{
		Scanner console = new Scanner(System.in);
		System.out.println("Informazioni sulle risorse? (Si / No)");
		String tipo = console.next();
		System.out.println("Informazioni sui dati? (Si / No)");
		String dati = console.next();
		boolean data = false;
		if(dati.equalsIgnoreCase("Si")) data = true;		
		System.out.println("Digitare nome Specification");
		String specification = console.next();
		System.out.println("Digitare versione Specification");
		String v = console.next();
		String specXES = null;
		YSpecificationID specID = null;
		String s1 = _logClient.getAllSpecifications(_logClient.connect("admin", "YAWL"));
		Element el = JDOMUtil.stringToDocument(s1).getRootElement();
		for(Element e1: (List<Element>)el.getChildren()) {
			for(Element e2: (List<Element>)e1.getChildren()) {
				if("id".equals(e2.getName())) {	
					String identifier = ((Element) e2.getChildren().get(0)).getValue();
					String version = ((Element) e2.getChildren().get(1)).getValue();
					String uri = ((Element) e2.getChildren().get(2)).getValue();
					if(version.equals(v) && uri.equals(specification)) {
						specID = new YSpecificationID(identifier, version, uri);
					}
				}
			}
		}
		if(tipo.equalsIgnoreCase("No")) {
			specXES = _logClient.getSpecificationXESLog(specID, data, _logClient.connect("admin", "YAWL"));
		}else {
			specXES = _resClient.getMergedXESLog(specID.getIdentifier(), specID.getVersionAsString(), specID.getUri(), data, _resClient.connect("admin", "YAWL"));
		}
		File f = new File("log.xes");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(specXES.getBytes("UTF-8"));
		fos.flush();
		fos.close();
//		FileWriter fw = new FileWriter(f);
//		fw.write(specXES);
//		fw.flush();
//		fw.close();
	}
	
}
