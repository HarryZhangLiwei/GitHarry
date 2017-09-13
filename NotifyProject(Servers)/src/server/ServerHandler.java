package server; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import client.Client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import util.WebChecking;


/*
 * This class will used to handle the communication with the server
 * */
public class ServerHandler extends SimpleChannelInboundHandler<Object>{
	
	/*
	    * Class Member
	    * @params 
	    * ack was protocol which used to response to the client
	    * notify was protocol which used to identify the first byte of the notification
	    * connection was protocol which used to identify client login server
	    * heap was protocol identify from the client   
	    * check used to identified if it is a new notification
	    * notifyhead was protocol which used to send to client as first byte
	    * */
	private byte heap = 0x0F;
	private byte connection = 0x6F;
	private byte close = 0x7F;
	private byte notify = 0x1F;
	private byte[] ack = new byte[]{0x3F};
	static boolean check = false;
	private byte[] notifyhead = new byte[]{0x11};
	
	/*
	 * Method
	 * @describe Encode the notification
	 * @param name and time 
	 * */
	public byte[] getNotification(String name,String newTime){
		
		byte[] b_name = name.getBytes();
		int len0 = b_name.length;
		byte[] length0 = intToByteArray(len0);
		
		byte[] context = newTime.getBytes();
		int len = context.length;
		byte[] length = intToByteArray(len);
		
		byte[] send = new byte[5+len+4+len];
		send[0] = notifyhead[0];
		
		for(int i=1; i<5;i++){
			send[i] = length0[i-1];
		}
		for(int i=5, j=0; j<len0; j++, i++){
			send[i] = b_name[j];
		}
		
		for(int i=5+len0, j=0; j<4; j++, i++){
			send[i] = length[j];
		}
		
		for(int i=9+len0, j=0; j<len; j++, i++){
			send[i] = context[j];
		}
		return send;
	}
	
	/*
	 * Method
	 * @describe convert int to byte[]
	 * @param int
	 * @return byte[]
	 * */
	private byte[] intToByteArray(final int integer) {
		int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
		byte[] byteArray = new byte[4];
		for (int n = 0; n < byteNum; n++)
		byteArray[3 - n] = (byte) (integer>>> (n * 8));
		return (byteArray);
	}
	
	/*
	 * @Describe According the protocal to parse the message
	 * @param ChannelHandlerContext ctx, Object msg
	 */
	public void parseMessage(ChannelHandlerContext ctx, Object msg){

		if(heap==((ByteBuf)msg).getByte(0)){
			ByteBuf send = Unpooled.wrappedBuffer(ack);
			ClientMap.get(ctx.channel().remoteAddress().toString()).getSockeChannel().writeAndFlush(send);
		}
		Set<String> list = new HashSet<String>();
		if(connection == ((ByteBuf)msg).getByte(0)){
			int flag=0;
			byte[] len = new byte[4];
			((ByteBuf)msg).getBytes(1, len);
			int all = byte2int(len);
			int tmp = 5;
			while(all!=0){
				byte[] len1 = new byte[4];
				((ByteBuf)msg).getBytes(tmp, len1);
				tmp = tmp + 4;
				
				byte[]name = new byte[byte2int(len1)];
				((ByteBuf)msg).getBytes(tmp, name);
				String s_name = new String(name);
				
				tmp = tmp + byte2int(len1);
				byte[] len2 = new byte[4];
				((ByteBuf)msg).getBytes(tmp, len2);
				
				tmp = tmp+4;
				byte[]localtime = new byte[byte2int(len2)];
				((ByteBuf)msg).getBytes(tmp, localtime);
				String s_time = new String(localtime);
				if(ClientMap.getIndex().containsKey(s_name)){
					if(ClientMap.getIndex().get(s_name).equals(s_time)){
						list.add(s_name);
					}else{
						flag=1;
						ByteBuf send = Unpooled.wrappedBuffer(getNotification(s_name,ClientMap.getIndex().get(s_name)));
						ctx.writeAndFlush(send);
						ctx.disconnect();
						break;
					}
				}else{
					list.add(s_name);
				}
				
				tmp = tmp + byte2int(len2);
				
				all = all - 8 - byte2int(len1) - byte2int(len2);
			}
			
			if(flag==0){
				ClientObject obj = new ClientObject();
				obj.setClientAddress(ctx.channel().remoteAddress().toString());
				obj.setSockeChannel(ctx);
				obj.setType(list);
				ClientMap.add(ctx.channel().remoteAddress().toString(), obj);
			}
			
		}
		if(close == ((ByteBuf)msg).getByte(0)){
			ctx.disconnect();
			ClientMap.removeByAddress(ctx.channel().remoteAddress().toString());
		}
		if(notify == ((ByteBuf)msg).getByte(0)){
			byte[] len = new byte[4];
			((ByteBuf)msg).getBytes(1, len);
			byte[] b_name = new byte[byte2int(len)];
			((ByteBuf)msg).getBytes(5, b_name);
			String s_name = new String(b_name);
			
			int k =byte2int(len);
			byte[] len1 = new byte[4];
			((ByteBuf)msg).getBytes(5+k, len1);
			byte[] b_time = new byte[byte2int(len1)];
			((ByteBuf)msg).getBytes(5+k+4, b_time);
			String time = new String(b_time);
			if(ClientMap.getIndex().containsKey(s_name)){
				if(!ClientMap.getIndex().get(s_name).equals(time)){
					ClientMap.getIndex().remove(s_name);
					ClientMap.getIndex().put(s_name, time);
					ClientMap.pushNotification(s_name,getNotification(s_name,time));
					ctx.disconnect();
				}
			}else{
				ClientMap.getIndex().put(s_name, time);
			}
			
			
		}
		
		
	}
	
	/*
	 * Method
	 * @describe which used to convert byte[] to int
	 * @param byte[]
	 * @return int
	 * */
	public int byte2int(byte[] b) {   
		return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;     
	} 
	
	
	/*
	 * Method
	 * @describe 
	 * If client received messages from client, this method will be called
	 * */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		
		parseMessage(ctx,msg);	
	}
	
	/*
	 * Method
	 * @describe this method would be called when disconnect with the server
	 * */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		ClientMap.removeByAddress(ctx.channel().remoteAddress().toString());
	}
	
	/*
	 * Method 
	 * @describe this method would be called when there was a mistake between the communication
	 * */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		super.exceptionCaught(ctx, cause);
		System.out.println("exception.........");
	}
	/*
	 * Method
	 * @describe this method used to listen to the channel 
	 * */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
		if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state() == IdleState.READER_IDLE){
				
				ClientMap.removeByAddress(ctx.channel().remoteAddress().toString());
				ctx.disconnect();
			}
		}
	}

}
