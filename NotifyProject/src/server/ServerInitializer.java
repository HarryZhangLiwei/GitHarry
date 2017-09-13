package server;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;


/*
 * This class used to add operation class to the channel
 * */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	
	/*
	 * Method 
	 * This method will be called after initial 
	 * */
 
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new IdleStateHandler(120,0,0,TimeUnit.SECONDS));
		pipeline.addLast("handler", new ServerHandler()); 
	}
}
