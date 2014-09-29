import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.yawlfoundation.yawl.resourcing.rsInterface.ResourceGatewayClient;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class ResourceName{

	private static ResourceGatewayClient _resClient = new ResourceGatewayClient("http://localhost:8080/resourceService/gateway");
	
	public static void main(String[] args){
		Scanner console = new Scanner(System.in);
		boolean cicla = true;
		String risorsa = null;
		while(cicla) {
			System.out.println("Inserire ID risorsa");
			String ID = console.next();
			if(ID.equalsIgnoreCase("exit")) cicla=false;
			else {
				try {
					risorsa = _resClient.getParticipant(ID, _resClient.connect("admin", "YAWL"));
					org.jdom2.Element e = JDOMUtil.stringToElement(risorsa);
					List<org.jdom2.Element> l = e.getChildren();
					risorsa = l.get(1).getValue()+" "+l.get(2).getValue();
				} catch (IOException e) {
					risorsa = null;
				}
				System.out.println(risorsa);
			}
		}
	}
	
}