package com.example.harry.nc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


public class ClientHandler  extends  SimpleChannelInboundHandler<Object> {  
    
	public static final int MIN_DELAY_TIME = 10;
	private long lastTime = 0;
	private byte ack = 0x3F;
	private byte notify = 0x11;
	private boolean ping = false;
	private boolean connect = false;
	private byte[] loginhead = new byte[]{0x6F};
	private byte[] heap = new byte[]{0x0F};

	private Activity act;

	public ClientHandler(Activity activity){
		act = activity;
	}
	
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



			Intent intent = new Intent();
			intent.setAction("NotifycationService");

			PackageManager pm = act.getApplicationContext().getPackageManager();
			List<ResolveInfo> resolveInfoList = pm.queryIntentServices(intent,0);
			ResolveInfo info = resolveInfoList.get(0);
			Intent expIntent = new Intent(intent);
			expIntent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
			act.startService(expIntent);


		}else{
			System.out.println("111");
		}
		
		}
	}
	
	public int byte2int(byte[] b) {   
		return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;     
	} 

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		parseMessage(ctx,msg);
	}



	@Override
    public void channelActive(final ChannelHandlerContext ctx) throws IOException {
		//System.out.print(readLocalTime());
		int all = 0;
		List<String> list = new ArrayList();
		list.add("COMP512");

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


	private void writeLocaltime(String content, String path){
		try
		{
			// 以追加模式打开文件输出流
			String filePath = act.getFilesDir().getPath().toString() +"/"+path+".txt";
			File file = new File(filePath);
			if(!file.exists()) {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(filePath);
				// 将FileOutputStream包装成PrintStream
				PrintStream ps = new PrintStream(fos);
				// 输出文件内容
				ps.println(content);
				ps.close();
			}
			else{
				FileOutputStream fos = new FileOutputStream(filePath);
				// 将FileOutputStream包装成PrintStream
				PrintStream ps = new PrintStream(fos);
				// 输出文件内容
				ps.println(content);
				ps.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String readLocalTime(String path){
		StringBuffer s = new StringBuffer();
		String tmp = null;
		String filePath = act.getFilesDir().getPath().toString()+ "/"+path+".txt";
		try
		{
			File file = new File(filePath);
			if(!file.exists()) {

				file.createNewFile();
				Date time = new Date(0000,00,00);
				FileOutputStream fos = act.openFileOutput(filePath, act.MODE_PRIVATE);
				// 将FileOutputStream包装成PrintStream
				PrintStream ps = new PrintStream(fos);
				// 输出文件内容
				ps.println(time.toString());

				ps.close();
				return time.toString();
			}else {

				FileInputStream str = new FileInputStream(filePath);
				BufferedReader br = new BufferedReader(new InputStreamReader(str));
				while ((tmp = br.readLine()) != null) {
					s.append(tmp);
				}
				return s.toString();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}



	
	private byte[] intToByteArray(final int integer) {
		int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
		byte[] byteArray = new byte[4];
		for (int n = 0; n < byteNum; n++)
		byteArray[3 - n] = (byte) (integer>>> (n * 8));
		return (byteArray);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		super.channelInactive(ctx);
		System.out.println("reconnect***********");
		TimeUnit.SECONDS.sleep(5);
		new Client(act).run();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		super.exceptionCaught(ctx, cause);
		System.out.println("expection^^^^^^^^^^^^");
	}
	
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
