package client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;


/*
 * This class used to add operation class to the channel
 * */
public class ClientInitializer extends ChannelInitializer<SocketChannel>{ 

	/*
	 * Class Member
	 * @param list which used to store the webpage
	 * */
	private List<String> list = new ArrayList<String>();
	
	/*
	 * Structure 
	 * @describe the list used to send to client handler
	 * */
	public ClientInitializer(List<String> name){
		list  = name;
	}
	
	/*
	 * Method 
	 * This method will be called after initial 
	 * */
	@Override
	public void initChannel(SocketChannel ch) throws Exception {  
        ChannelPipeline pipeline = ch.pipeline();  
          
        pipeline.addLast(new IdleStateHandler(0,20,0,TimeUnit.SECONDS));
        pipeline.addLast("handler", new ClientHandler(list));  
          
    } 

}
