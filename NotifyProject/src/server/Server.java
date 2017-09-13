package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import util.Basehttp;
import util.WebChecking;


/*
 * This class used to run server
 * */
public class Server {
	
	/*
	    * Class Member
	    * @describe HOST and PORT is server address and port
	    * 
	    * */
	private final int port;
	private final String url;
	
	/*
	 * Structure
	 * @describe set port and url
	 * */
	public Server(String url,int port){
		this.port=port;
		this.url = url;
	}
	
	 /*
	    * Method
	    * @describe Call this method, you can start the server
	    * */
	public void run() throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ServerInitializer())
			.option(ChannelOption.SO_BACKLOG, 2048);
			
			new ClientMap().init();
			
			ChannelFuture f = b.bind(new InetSocketAddress(url,port)).sync();
			
			if(f.isSuccess()){
				System.out.println("server start-----------");
			}
			
			
            f.channel().closeFuture().sync();
			
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	/*
	 * Main
	 * Start the server
	 * */
	public static void main(String[] args) throws Exception{
		int port = 6666;
		
		String url="zorn.hbg.psu.edu";
	//	Basehttp http = new Basehttp();
	//	http.postNew(url, port);
		new Server(url,port).run();
		
		
	}
	
}
