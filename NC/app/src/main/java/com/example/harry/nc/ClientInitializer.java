package com.example.harry.nc;

import android.app.Activity;

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

public class ClientInitializer extends ChannelInitializer<SocketChannel>{
    private Activity act;

    public ClientInitializer(Activity activity){
        act = activity;
    }
	@Override
	public void initChannel(SocketChannel ch) throws Exception {  
        ChannelPipeline pipeline = ch.pipeline();  
          
        pipeline.addLast(new IdleStateHandler(0,10,0,TimeUnit.SECONDS));
        pipeline.addLast("handler", new ClientHandler(act));
          
    } 

}
