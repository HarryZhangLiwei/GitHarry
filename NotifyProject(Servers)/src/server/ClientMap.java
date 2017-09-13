package server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.WebChecking;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;


/*
 * This class used to store the clients information and all web page time in the server
 * */
public class ClientMap {
	
	/*
	 * Member
	 * map used client address as key and clientobj as the value
	 * index used web page name as key and time as the value 
	 * */
	private static Map<String, ClientObject> map = new ConcurrentHashMap<String, ClientObject>();
	private static Map<String, String> index = new ConcurrentHashMap<String, String>();
	
	/*
	 * Method
	 * @describe
	 * add new record to map
	 * @param address and clientobj
	 * */
	public static void add(String clientId, ClientObject client){
		map.put(clientId, client);
		System.out.println(map.size());
	}
	
	/*
	 * Method
	 * @describe
	 * return the map
	 * @return map
	 * */
	public static Map<String, String> getIndex(){
		return index;
	}
	
	/*
	 * Method
	 * @describe
	 * get the map value by client address
	 * @return cleintobj
	 * */
	public static ClientObject get(String address){
		return map.get(address);
	}
	/*
	 * Method
	 * @describe
	 * remove the record from map
	 * @param address
	 * */
	public static void removeByAddress(String address){
				map.remove(address);
				System.out.println(map.size());
	}
	
	/*
	 * Method
	 * @describe
	 * send the notification to all clients which care the web page
	 * @parm name and newTime(byte[])
	 * */
	public static void pushNotification(String name, byte[] newTime){
		
		
		for(Map.Entry<String, ClientObject> entry: map.entrySet()){
			System.out.println("new notification"+entry.getValue().getSockeChannel().channel().remoteAddress());
			ByteBuf send = Unpooled.wrappedBuffer(newTime);
			if(entry.getValue().getType().contains(name)){
				entry.getValue().getSockeChannel().writeAndFlush(send);
				entry.getValue().getSockeChannel().disconnect();
				map.remove(entry.getKey());
			}
		} 
	}
	
	/*
	 * Method
	 * @describe
	 * used keep consistency, access the time form target web page
	 * */
	public static void init(){
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
                String time ="";
                for (int j = 0; j < clist.getLength(); j++) {  
                    Node c = clist.item(j);  
                    if (c instanceof Element) {  
                    	if("name".equals(c.getNodeName())){
                    		name = c.getTextContent();
                    	}
                    	if("url".equals(c.getNodeName())){
                    		WebChecking check = new WebChecking();
                    		check.setUrl(c.getTextContent());
                    		time= check.AnlysisHTMLByURL().toString();
                    	}
                      
                    }  
                }
                System.out.println(time);
                
                index.put(name, time);
            } 
            }
                                  
        } catch (ParserConfigurationException e) {  
            e.printStackTrace();  
        } catch (SAXException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
        	
            e.printStackTrace();  
        }
		System.out.println(index.size());
	}
	
}
