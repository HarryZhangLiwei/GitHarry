package check;

import java.net.InetSocketAddress;

import client.Client;
import client.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CheckPro {
	   
	   /*
	    * Class Member
	    * @describe HOST and PORT is server address and port
	    * n_name is the Web page name
	    * n_url is the web page address
	    * */
	   public  String HOST;  
	   public  int PORT;      
	   private String n_name;
	   private String n_url;

	   /*
		 * Structure
		 * @describe If you want to initial this class, you need set n_name, n_url, HOST and Port
		 * @param name, url, n_name and n_url
		 * */
	   public CheckPro(String name, String url,String host, int port){
		   n_name = name;
		   n_url = url;
		   HOST = host;
		   PORT = port;
	   }

	   /*
	    * Method
	    * @describe Call this method, you can connect to the server
	    * */
	   public void run() {    
	         
	       EventLoopGroup group = new NioEventLoopGroup(4);  
	       try {  
	           Bootstrap bootstrap  = new Bootstrap()   
	                   .channel(NioSocketChannel.class)
	                   .group(group)
	                   .handler(new ChannelInitializer<SocketChannel>() { // (4)
	                       @Override
	                       public void initChannel(SocketChannel ch) throws Exception {
	                           ch.pipeline().addLast(new CheckHandler(n_name, n_url));
	                       }
	                   });  
	           
	           Channel channel = bootstrap.connect(new InetSocketAddress(HOST, PORT)).sync().channel(); 
	           
	           
	           System.out.println("success>>>>>>>>>>>>>");   
	      
	       } catch (Exception e) {  
	           e.printStackTrace();  
	       } finally {  
	           group.shutdownGracefully();  
	       }  
	   }
}
