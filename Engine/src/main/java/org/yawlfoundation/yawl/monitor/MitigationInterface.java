package org.yawlfoundation.yawl.monitor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.yawlfoundation.yawl.engine.interfce.ServletUtils;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 24/05/2013
 *
 */

public class MitigationInterface {
	
	private static MitigationInterface _me = null;
	private String monitorSensorURI = null;
	
	private MitigationInterface(String monitorSensorURI) {
		this.monitorSensorURI = monitorSensorURI;
	}
	
	public static MitigationInterface getInstance(String monitorServiceURI) {
		if(_me == null) _me = new MitigationInterface(monitorServiceURI);
		return _me;
	}

	public String sendNotification(String workDefID) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "mitigate");
	        params.put("caseID", workDefID);
			return send(monitorSensorURI, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "No mitigation found";
	}
	
	/**
     * Sends data to the specified url via a HTTP POST, and returns the reply
     * @param urlStr the url to connect to
     * @param paramsMap a map of attribute=value pairs representing the data to send
     * @param post true if this was originally a POST request, false if a GET request
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
        String param = null;
        String value = null;
        for (Entry<String, String> entry : params.entrySet()) {
        	param = entry.getKey();
            value = entry.getValue();
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
