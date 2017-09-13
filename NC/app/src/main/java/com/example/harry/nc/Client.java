package com.example.harry.nc;

import android.app.Activity;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/*
*   Client
* */
public class Client{

   public static String HOST = "zorn.hbg.psu.edu";
   public static int PORT = 6666;
   public static SocketChannel socketChannel;
   private Activity act;

   public Client(Activity activity){
       act = activity;
   }
   public void run() {    
         
       EventLoopGroup group = new NioEventLoopGroup(4);  
       try {  
           Bootstrap bootstrap  = new Bootstrap()   
                   .channel(NioSocketChannel.class)
                   .group(group) 
                   .handler(new ClientInitializer(act));
           
           Channel channel = bootstrap.connect(new InetSocketAddress(HOST, PORT)).sync().channel(); 
           
           
           System.out.println("success>>>>>>>>>>>>>");   
      
       } catch (Exception e) {  
           e.printStackTrace();  
       } finally {  
           //group.shutdownGracefully();  
       }  
   }


}
