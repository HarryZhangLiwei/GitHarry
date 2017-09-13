package check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import client.Client;
import server.ClientObject;
import util.Basehttp;
import util.WebChecking;
/*
 * This class used to start the monitor program
 * */

public class CheckThread extends Thread{
	
	
	/*
	 * Class member
	 * @param
	 * n_name is the Web page name
	 * n_url is the web page address
	 * find used to identify if it is a new notification time
	 * ctime used to store all web page name and time
	 * currentLocalTime as a paramter sent to the notification server
	 * list used to store web page name
	 */
	static boolean find = false;
	private boolean open = false;
	private Map<String,String> ctime = new HashMap<String, String>();
	static String currentLocalTime;
	private String n_name;
	private String n_url;
	private Map<String, String> map;
	private List<String> list;
	
	/*
	 * structure 
	 * @describe you need put map and list as the params
	 * */
	public CheckThread(Map<String, String> map1, List<String> list1){
		map  = map1;
		list = list1;
	}
	

	/*
	    * Method
	    * @describe Call this method, you can monitor the web page and once there is a new notification
	    * this method will new CheckPro(n_name,n_url,host,port).run();
	    * */

	@Override
	public void run(){
		WebChecking check = new WebChecking();
		StringBuffer s = new StringBuffer();
		String tmp = null;
		int i = 0;
		while(true){
			try {
				Thread.sleep(10000);
				if(i>=map.size()){
					i=0;
				}
				n_name = list.get(i);
				n_url = map.get(list.get(i));
				System.out.print(n_name);
				i++;
				if(!open){
					File file = new File(n_name+".txt");
					if(!file.exists()){
						file.createNewFile();
					} 
					FileInputStream str = new FileInputStream(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(str));
					while((tmp=br.readLine())!=null){
						s.append(tmp);
					}
					open = true;
					System.out.println("1");
					ctime.put(n_name, s.toString());
				}else{
					check.setUrl(n_url);
					String newTime = check.AnlysisHTMLByURL().toString();
					
					if(newTime.equals(ctime.get(n_name))){
						System.out.println("fine");
					}else{
						File file = new File(n_name+".txt");
						FileOutputStream out = new FileOutputStream(file,false);
						out.write(newTime.getBytes("utf-8"));
						out.close();
						ctime.remove(n_name);
						ctime.put(n_name, newTime);
					
						find = true;
						String host="";
						int port=0;
						if(find){
						//	  Basehttp http = new Basehttp();
						//	  Map<String,String> map = http.getAll();
						//	  for(Map.Entry<String, String> entry: map.entrySet()){
						//			   System.out.println(entry.getKey());
						//			   host = entry.getKey();
						//			   port = Integer.parseInt(entry.getValue());
								       host = "zorn.hbg.psu.edu";
								       port=6666;
									   currentLocalTime = ctime.get(n_name);
									   new CheckPro(n_name,n_url,host,port).run();
						//	   }
							  find = false;
						  }
						System.out.println("call");
					}
				}
				
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Main function
	 * @describe which used run the monitor
	 * */
	
	public static void main(String[] args) throws Exception{
		
		Map<String, String> map = new HashMap<String, String>();
		List<String> list1 = new ArrayList<String>();
		 try {   
	            DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();  
	            
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	            
	            
	            File file = new File("web.xml");
				if(!file.exists()){
					file.createNewFile();
				} 
				else{
					
					
	            Document document = builder.parse(new File("web.xml"));  
	  
	                
	            Element root = document.getDocumentElement();  
	            NodeList list = root.getElementsByTagName("entry");  
	            
	            for (int i = 0; i < list.getLength(); i++) {  
	            	
	            	
	                Element lan =  (Element) list.item(i);    
	                NodeList clist = lan.getChildNodes();
	                String name ="";
	                String url ="";
	                
	                for (int j = 0; j < clist.getLength(); j++) {  
	                    Node c = clist.item(j);
	                    
	                    if (c instanceof Element) {  
	                    	if("name".equals(c.getNodeName())){
	                    		name = c.getTextContent();
	                    	}
	                    	if("url".equals(c.getNodeName())){
	                    		url = c.getTextContent();
	                    	}
	                    }  
	                }
	           	 	
	                map.put(name, url);
	                list1.add(name);
	            } 
	            }
	                                  
	        } catch (ParserConfigurationException e) {  
	            e.printStackTrace();  
	        } catch (SAXException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
		 		 
		 new CheckThread(map,list1).run();

	}
}
