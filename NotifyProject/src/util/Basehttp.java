package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
    


/*
 * This class provide method to access address restful web services
 * */
public class Basehttp {
    	
	/*
	 * Member
	 * @describe the target service address
	 * */
	private static String url =  "http://75.102.65.156:8080/DSPServer/Server.action";
		
	/*
	 * Get Method
	 * @describe Get all addresses from the service 
	 * */
    	public  Map<String, String> getAll(){
    		Map<String,String> map = new HashMap<String,String>();
    		try {
    		    
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(url);
                getRequest.addHeader("accept", "application/json");
        
                HttpResponse response = httpClient.execute(getRequest);
        
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                       + response.getStatusLine().getStatusCode());
                }
        
                BufferedReader br = new BufferedReader(
                                 new InputStreamReader((response.getEntity().getContent())));
        
                String output;
                String str = "";
     
                while ((output = br.readLine()) != null) {
                	str += output;
                }
                
                JSONArray arr = JSONArray.fromObject(str); 
            
                for (int i = 0; i < arr.toArray().length; i++) {      
                    JSONObject temp = (JSONObject) arr.get(i);      
                    String name = temp.getString("url");      
                    String ip = temp.getString("port");
                    map.put(name, ip);
                }   
                
                httpClient.getConnectionManager().shutdown();
        
              } catch (ClientProtocolException e) {
        
                e.printStackTrace();
        
              } catch (IOException e) {
        
                e.printStackTrace();
              }
    		return map;
    	}
    	/*
    	 * Post Method
    	 * @describe Add a new server information t o serveice 
    	 * */
    	public  void postNew(String url1, int port){
    		
    		try {
        	    
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);
        
                StringEntity input = new StringEntity("{\"url\":\""+url1+"\",\"port\":\""+port+"\"}");
                input.setContentType("application/json");
                postRequest.setEntity(input);
               
               HttpResponse response = httpClient.execute(postRequest);
        
        
                BufferedReader br = new BufferedReader(
                                new InputStreamReader((response.getEntity().getContent())));
        
              
                httpClient.getConnectionManager().shutdown();
        
              } catch (MalformedURLException e) {
        
                e.printStackTrace();
        
              } catch (IOException e) {
        
                e.printStackTrace();
        
              }
    	}
    	/*
    	 * Delete Method
    	 * @describe Delete a server from the address server
    	 * */
    	public  void deleteOne(String url1, int port){
    		try {
        	    
                DefaultHttpClient httpClient = new DefaultHttpClient();
                
                HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(url);
        
                StringEntity input = new StringEntity("{\"url\":\""+url1+"\",\"port\":\""+port+"\"}");
                
                input.setContentType("application/json");
                
                deleteRequest.setEntity(input);
             
               
                HttpResponse response = httpClient.execute(deleteRequest);
        
              
        
                BufferedReader br = new BufferedReader(
                                new InputStreamReader((response.getEntity().getContent())));
        
              
                httpClient.getConnectionManager().shutdown();
        
              } catch (MalformedURLException e) {
        
                e.printStackTrace();
        
              } catch (IOException e) {
        
                e.printStackTrace();
        
              }
    	}
    
    }
