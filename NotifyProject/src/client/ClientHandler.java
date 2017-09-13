package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/*
 * This class will used to handle the communication with the server
 * */
public class ClientHandler  extends  SimpleChannelInboundHandler<Object> {  
    
	/*
	    * Class Member
	    * @params MIN_DELAY_TIME used to set the delay time
	    * ack was protocol which used to compared with the server response message
	    * notify was protocol which used to identify the first byte of the notification
	    * ping used to identified the heart beat work well
	    * connect used to identify connected to server
	    * loginhead was protocol which used to send to the server
	    * heap was heart beat package
	    * list was used to store all web page name   
	    * */
	public static final int MIN_DELAY_TIME = 10;
	private byte ack = 0x3F;
	private byte notify = 0x11;
	private boolean ping = false;
	private boolean connect = false;
	private byte[] loginhead = new byte[]{0x6F};
	private byte[] heap = new byte[]{0x0F};
	private List<String> list = new ArrayList<String>();
	
	
	/*
	 * Structure
	 * @describe If you want to initial this class, you need set list
	 * list was the web page name
	 * */
	public ClientHandler(List<String> name){
		list = name;
	}
	
	/*
	 * Method
	 * which used to analysis the response form the server
	 * */
	public void parseMessage(ChannelHandlerContext ctx, Object msg) throws IOException{
		
		if(ack==((ByteBuf)msg).getByte(0)){
			connect = true;
			System.out.println("ack");
		}
		else{
		
		if(notify==((ByteBuf)msg).getByte(0)){
			byte[] len = new byte[4];
			((ByteBuf)msg).getBytes(1, len);
			byte[] b_name = new byte[byte2int(len)];
			((ByteBuf)msg).getBytes(5, b_name);
			String s_name = new String(b_name);
			System.out.println(s_name);
			
			byte[] len1 = new byte[4];
			((ByteBuf)msg).getBytes(5+byte2int(len), len1);
			byte[] b_time = new byte[byte2int(len1)];
			((ByteBuf)msg).getBytes(9+byte2int(len), b_time);
			String s_time = new String(b_time);
			writeLocaltime(s_time,s_name);
			Desktop tip = new Desktop();
	        tip.setToolTip(new ImageIcon("img.jpg"),
	                "Hi, you got a new notifycation! -- "+s_name);
		}else{
			System.out.println("111");
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
	 * If client received messages from server, this method will be called
	 * */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		parseMessage(ctx,msg);
	}  
	
	/*
	 * Method
	 * @describe this method would be called when connect server successfully
	 * */
	@Override
        public void channelActive(final ChannelHandlerContext ctx) throws IOException { 

		
		int all = 0;
		
		for(int k=0; k<list.size(); k++){
			byte[] context = readLocalTime(list.get(k)).getBytes();
			byte[] b_name = list.get(k).getBytes();
			
			int len = context.length;
			int len1 = b_name.length;
			
			byte[] length = intToByteArray(len);
			byte[] length1 = intToByteArray(len1);
			all = all + len + len1 + length.length + length1.length;
			
		}

		byte[] send = new byte[5+all];
		send[0] = loginhead[0];
		byte fl[] = intToByteArray(all);
		for(int i=1; i<5;i++){
			send[i] = fl[i-1];
		}
		int j = 5;
		for(int k=0; k<list.size(); k++){
			byte[] context = readLocalTime(list.get(k)).getBytes();
			byte[] b_name = list.get(k).getBytes();
			
			int len = context.length;
			int len1 = b_name.length;
			
			byte[] length = intToByteArray(len);
			byte[] length1 = intToByteArray(len1);
			
			for(int i=0; i<4; i++,j++){
				send[j] = length1[i];
			}
			for(int i=0; i<len1; i++,j++){
				send[j] = b_name[i];
			}
			for(int i=0; i<4; i++,j++){
				send[j] = length[i];
			}
			for(int i=0; i<len; i++,j++){
				send[j] = context[i];
			}
		}
		ByteBuf res = Unpooled.wrappedBuffer(send);
		ctx.writeAndFlush(res);
	}
	
	public void writeLocaltime(String time,String path) throws IOException{
		File file = new File(path + ".txt");
		if(!file.exists()){
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(time.getBytes("utf-8"));
			out.close();
		}else{
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(time.getBytes("utf-8"));
			out.close();
		}
	}
	
	
	/*
	 * Method
	 * @describe  read notification time from the local space
	 * @param path which is web page name
	 * @return time
	 * */
	public String readLocalTime(String path) throws IOException{
		StringBuffer s = new StringBuffer();
		String tmp = null;
		File file = new File(path+".txt");
		if(!file.exists()){
			file.createNewFile();
			Date time = new Date(0000,00,00);
			writeLocaltime(time.toString(), path);
			return time.toString();
		} 
		else{
			FileInputStream str = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(str));
			while((tmp=br.readLine())!=null){
				s.append(tmp);
			}
			str.close();
			br.close();
			return s.toString();
		}
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
	 * Method
	 * @describe this method would be called when disconnect with the server
	 * */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		super.channelInactive(ctx);
		System.out.println("reconnect***********");
		TimeUnit.SECONDS.sleep(5);
		new Client(list).run();
	}
	/*
	 * Method 
	 * @describe this method would be called when there was a mistake between the communication
	 * */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		super.exceptionCaught(ctx, cause);
		System.out.println("expection^^^^^^^^^^^^");
	}
	
	/*
	 * Method
	 * @describe this method used to listen to the channel 
	 * */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object msg){
		if(msg instanceof IdleStateEvent){
			IdleStateEvent e = (IdleStateEvent) msg;
			switch(e.state()){
				case WRITER_IDLE:
						ByteBuf send = Unpooled.wrappedBuffer(heap);
						ctx.writeAndFlush(send);
						System.out.println("send ping to server------------");
						ping = true;
					break;
				case READER_IDLE:
						ctx.disconnect();
				default:
					break;
			}
			
		}
	}
} 
