package check;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CheckHandler extends  SimpleChannelInboundHandler<Object>{

	/*
	 * Class member
	 * n_name is the Web page name
	 * n_url is the web page address
	 * notify is the identify which used to call server notify function.
	 */
	private String n_name;
	private String n_url;
	public byte notify[] = new byte[]{0x1F};
	
	/*
	 * Structure
	 * @describe If you want to initial this class, you need set n_name and n_url
	 * @param name and url
	 * */
	public CheckHandler(String name, String url){
		n_name = name;
		n_url = url;
	}
	
	/*
	 * Method
	 * @describe this method would be called when connect server successfully
	 * */
	@Override
	public void channelActive(final ChannelHandlerContext ctx) { 
		
		String date = CheckThread.currentLocalTime;
		System.out.println(CheckThread.currentLocalTime);
		
		
		byte[] context = date.getBytes();
		int len = context.length;
		byte[] length = intToByteArray(len);
		
		int len0 = n_name.length();
		byte[] length0 = intToByteArray(len0);
		byte[] send = new byte[5+len+4+len0];
		byte[] b_name = n_name.getBytes();
		send[0] = notify[0];
		
		for(int i=1; i<5;i++){
			send[i] = length0[i-1];
		}
		for(int i=5, j=0; j<len0;j++, i++){
			send[i] = b_name[j];
		}
		
		for(int i=len0+5,j=0;j<4;j++,i++){
			send[i] = length[j];
		}
		for(int i=len0+9, j=0; j<len; j++,i++){
			send[i] = context[j];
		}
		
		ByteBuf res = Unpooled.wrappedBuffer(send);
		ctx.writeAndFlush(res);
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
	 * @describe 
	 * If client received messages from server, this method will be called
	 * */
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
