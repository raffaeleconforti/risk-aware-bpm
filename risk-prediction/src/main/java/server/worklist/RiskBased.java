package server.worklist;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.util.JDOMUtil;

import server.worklist.MetricInterface;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 20/05/2013
 *
 */

public class RiskBased implements MetricInterface {

	private final String riskPredictionURI = "http://localhost:8080/riskPrevention/Prediction";
	private final String _engineURI = "http://localhost:8080/yawl/ib";
	
	public static void main(String[] args) {
		RiskBased rb = new RiskBased();
		Map<String, String> map = new HashMap<String, String>();
		map.put("InterfaceB_BackEnd", "http://localhost:8080/yawl/ib");
		EngineConnector.getInstance().initInterfaces(map);
		System.out.println(rb.getValue("384:Open_Claim_3", "Clark1"));
	}
	
	@Override
	public double getValue(String workItemId, String resourceId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("InterfaceB_BackEnd", "http://localhost:8080/yawl/ib");
		EngineConnector.getInstance().initInterfaces(map);
		try {
			
			WorkItemRecord wir = EngineConnector.getInstance().getEngineStoredWorkItem(workItemId, EngineConnector.getInstance().getEngineHandle());
						
			Map<String, String> params = new HashMap<String, String>();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("<request>");
				sb.append("<type>routing</type>");
				sb.append("<workItems>");
					sb.append("<workItem>");
						sb.append("<caseID>"+wir.getRootCaseID()+"</caseID>");
						sb.append("<taskID>"+wir.getTaskID()+"</taskID>");
						sb.append("<specificationURI>"+wir.getSpecURI()+"</specificationURI>");
					sb.append("</workItem>");
				sb.append("</workItems>");
				sb.append("<resource>"+resourceId+"</resource>");
				sb.append("<data />");
			sb.append("</request>");
			
			params.put("request", sb.toString());
			
			String responce = send(riskPredictionURI, params);
			
			Element res = JDOMUtil.stringToElement(responce);
			Element couples = res.getChild("coupleWorkItemsPredictions");
			
			double result = 0;
			
	    	for(Element couple : (List<Element>) couples.getChildren()) {
	    		
	    		String pred = couple.getChild("prediction").getValue();
	    		
	    		result = ((double) Long.parseLong(pred))/100;
	    		
	    	}
	    	
	    	return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}	
	
	/**
     * Sends data to the specified url via a HTTP POST, and returns the reply
     * @param urlStr the url to connect to
     * @param paramsMap a map of attribute=value pairs representing the data to send
     * @return the response from the url
     * @throws IOException when there's some kind of communication problem
     */
    private String send(String urlStr, Map<String, String> paramsMap) throws IOException {
    	
        HttpURLConnection connection = initPostConnection(urlStr);

        sendData(connection, encodeData(paramsMap)) ;
        String result = getReply(connection.getInputStream());
        
        connection.disconnect();
        
        return result;
    }
    
    /**
     * Initialises a HTTP POST connection
     * @param urlStr the url to connect to
     * @return an initialised POST connection
     * @throws IOException when there's some kind of communication problem
     */
    private HttpURLConnection initPostConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        return connection ;
    }
    
    /**
     * Encodes parameter values for HTTP transport
     * @param params a map of the data parameter values, of the form
     *        [param1=value1],[param2=value2]...
     * @return a formatted http data string with the data values encoded
     */
    private String encodeData(Map<String, String> params) {
        StringBuilder result = new StringBuilder("");
        for (String param : params.keySet()) {
            String value = params.get(param);
            if (value != null) {
                if (result.length() > 0) result.append("&");
                result.append(param)
                      .append("=")
                      .append(ServletUtils.urlEncode(value));
            }
        }
        return result.toString();
    }
    
    /**
     * Submits data on a HTTP connection
     * @param connection a valid, open HTTP connection
     * @param data the data to submit
     * @throws IOException when there's some kind of communication problem
     */
    private void sendData(HttpURLConnection connection, String data) throws IOException {    	
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        out.write(data);
        out.flush();
        out.close();
    }
    
    /**
     * Receives a reply from a HTTP submission
     * @param is the InputStream of a URL or Connection object
     * @return the stream's contents (ie. the HTTP reply)
     * @throws IOException when there's some kind of communication problem
     */
    private String getReply(InputStream is) throws IOException {   
        final int BUF_SIZE = 16384;
        
        // read reply into a buffered byte stream - to preserve UTF-8
        BufferedInputStream inStream = new BufferedInputStream(is);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(BUF_SIZE);
        byte[] buffer = new byte[BUF_SIZE];

        // read chunks from the input stream and write them out
        int bytesRead;
        while ((bytesRead = inStream.read(buffer, 0, BUF_SIZE)) > 0) {
            outStream.write(buffer, 0, bytesRead);
        }

        outStream.close();
        inStream.close();

        // convert the bytes to a UTF-8 string
        return outStream.toString("UTF-8");
    }
    
}
