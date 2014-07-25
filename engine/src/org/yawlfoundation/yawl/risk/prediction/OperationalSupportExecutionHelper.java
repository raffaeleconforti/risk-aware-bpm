package org.yawlfoundation.yawl.risk.prediction;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.Marshaller;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.TaskInformation;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class OperationalSupportExecutionHelper extends InterfaceBWebsideController implements Serializable{

	private static OperationalSupportExecutionHelper _me = null;
	private String riskPredictionURI = null;
	private String _engineHandle = null; 
	
	public static void main(String[] args) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		
		String request = "<request><type>execution</type><workItems><workItem><caseID>392</caseID><taskID>Open_Claim_3</taskID><specificationURI>InsuranceClaim</specificationURI></workItem></workItems><resource>PA-84b0d53d-5a1d-41b3-9721-b7a05014be62</resource><data><Open_Claim_3><Customer>Customer1</Customer><ClaimType>Compulsory</ClaimType><DamageType>Fire</DamageType><Amount>1000</Amount></Open_Claim_3></data></request>";
		
		params.put("request", request);
		
		String responce = OperationalSupportExecutionHelper.getInstance().send("http://localhost:8080/riskPrevention/Prediction", params);
		
		Element res = JDOMUtil.stringToElement(responce);
		Element couples = res.getChild("coupleWorkItemsPredictions");
		
		int result = 0;
		
    	for(Element couple : (List<Element>) couples.getChildren()) {
    		
    		String pred = couple.getChildText("prediction");
    		result = (int) Double.parseDouble(pred);
    		
    	}
    	
    	System.out.println(result);
	}
	
	private OperationalSupportExecutionHelper(String riskPredictionURI) {
		this.riskPredictionURI = riskPredictionURI;
	}
	
	public static OperationalSupportExecutionHelper getInstance() {
		if(_me == null) {
			_me = new OperationalSupportExecutionHelper("http://localhost:8080/riskPrevention/Prediction");
			_me.setUpInterfaceBClient("http://localhost:8080/yawl/ib");
		}
		return _me;
	}
	
	public int getPrediction(WorkItemRecord wir, String resource, String data, SpecificationData specData) {
		try {
									
			String mergedlOutputData = Marshaller.getMergedOutputData(wir.getDataList(), JDOMUtil.stringToElement(data));
	        String filteredOutputData;

	        YSpecificationID specID = new YSpecificationID(wir.getSpecIdentifier(), wir.getSpecVersion(), wir.getSpecURI());
	        	       
	        // Now if this is beta4 or greater then remove all those input only bits of data
	        // by first preparing a list of output params to iterate over.
	        if (!(specData.usesSimpleRootData())) {
	            TaskInformation taskInfo = getTaskInformation(specID, wir.getTaskID(), getEngineHandle());
	            List<YParameter> outputParams = taskInfo.getParamSchema().getOutputParams();
	            filteredOutputData = Marshaller.filterDataAgainstOutputParams(mergedlOutputData, outputParams);
	        }
	        else {
	            filteredOutputData = mergedlOutputData;
	        }
			
			Map<String, String> params = new HashMap<String, String>();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("<request>");
				sb.append("<type>execution</type>");
				sb.append("<workItems>");
					sb.append("<workItem>");
						sb.append("<caseID>"+wir.getRootCaseID()+"</caseID>");
						sb.append("<taskID>"+wir.getTaskID()+"</taskID>");
						sb.append("<specificationURI>"+wir.getSpecURI()+"</specificationURI>");
					sb.append("</workItem>");
				sb.append("</workItems>");
				sb.append("<resource>"+resource+"</resource>");
				sb.append("<data>"+filteredOutputData+"</data>");
			sb.append("</request>");
			
			params.put("request", sb.toString());
			
			System.out.println(sb.toString());
			
			String responce = send(riskPredictionURI, params);
			
			Element res = JDOMUtil.stringToElement(responce);
			Element couples = res.getChild("coupleWorkItemsPredictions");
			
			int result = 0;
			
	    	for(Element couple : (List<Element>) couples.getChildren()) {
	    		
	    		String pred = couple.getChildText("prediction");
	    		result = (int) Double.parseDouble(pred);
	    		
	    	}
	    	
	    	return result;
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
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

	@Override
	public void handleEnabledWorkItemEvent(WorkItemRecord enabledWorkItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {
		// TODO Auto-generated method stub
		
	}
	
    private String getEngineHandle() {
        try {
        	while (!_me.successful(_engineHandle)) {
				_engineHandle = _me.connect(DEFAULT_ENGINE_USERNAME,DEFAULT_ENGINE_PASSWORD);
			}
		} catch (IOException e) {
			_engineHandle = "<failure>Problem connecting to engine.</failure>";
		}
        return _engineHandle;
    }

}
