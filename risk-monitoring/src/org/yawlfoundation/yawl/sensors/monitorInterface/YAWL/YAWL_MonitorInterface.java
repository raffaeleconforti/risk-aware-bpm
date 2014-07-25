package org.yawlfoundation.yawl.sensors.monitorInterface.YAWL;

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
import org.yawlfoundation.yawl.sensors.monitorInterface.MonitorInterface;

public class YAWL_MonitorInterface implements MonitorInterface{
	
	private static MonitorInterface _me = null;
	private String monitorSensorURI = null;
	
	public static void main(String[] args) {
		MonitorInterface a = YAWL_MonitorInterface.getInstance("http://localhost:8080/monitorService/ms");
		a.sendNotification(1, "1", "1", "1", "1", "1", "1", 1.0, 10.0, 1L);
	}
	
	private YAWL_MonitorInterface(String monitorSensorURI) {
		this.monitorSensorURI = monitorSensorURI;
	}
	
	public static MonitorInterface getInstance(String monitorServiceURI) {
		if(_me == null) _me = new YAWL_MonitorInterface(monitorServiceURI);
		return _me;
	}

	@Override
	public void sendNotification(int notificationID, String workDefID, String sensorName, String status, String message, String condition, String threshold, double probability, double cons, long timeStamp) {
		try {
			Map<String, String> params = new HashMap<String, String>();;
	        params.put("action", "Notification");
	        params.put("number", ""+notificationID);
	        params.put("caseID", workDefID);
	        params.put("nameSensor", sensorName);
	        params.put("status", status);
	        params.put("message", message);
	        params.put("trigger", condition);
	        params.put("threshold", threshold);
	        params.put("probability", ""+probability);
	        params.put("cons", ""+cons);
	        params.put("timestamp", ""+timeStamp);    
			send(monitorSensorURI, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cancelNotification(int notificationID, String workDefID, String sensorName) {
		try {
			Map<String, String> params = new HashMap<String, String>();;
	        params.put("action", "cancelNotification");
	        params.put("number", ""+notificationID);
	        params.put("caseID", workDefID);
	        params.put("nameSensor", sensorName);
			send(monitorSensorURI, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Sends data to the specified url via a HTTP POST, and returns the reply
     * @param urlStr the url to connect to
     * @param paramsMap a map of attribute=value pairs representing the data to send
     * @param post true if this was originally a POST request, false if a GET request
     * @return the response from the url
     * @throws IOException when there's some kind of communication problem
     */
    private void send(String urlStr, Map<String, String> paramsMap) throws IOException {
    	
        HttpURLConnection connection = initPostConnection(urlStr);
        
        sendData(connection, encodeData(paramsMap)) ;
        String result = getReply(connection.getInputStream());
        
        connection.disconnect();
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
