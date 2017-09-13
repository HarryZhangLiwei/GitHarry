package client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import server.ClientObject;
import util.Basehttp;
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
 * This class used to run the client
 * */
public class Client {
	/*
	    * Class Member
	    * @describe HOST and PORT is server address and port
	    * n_name is the Web page name
	    * 
	    * */
	public static String HOST = "zorn.hbg.psu.edu";
	public static int PORT=6666;
	private List<String> n_name = new ArrayList<String>();
   
	/*
	 * Structure
	 * @describe If you want to initial this class, you need set n_name
	 * */
	public Client(List<String> name) {
		n_name = name;
	}

	 /*
	    * Method
	    * @describe Call this method, you can connect to the server
	    * */
	public void run() {

//		Basehttp http = new Basehttp();
//		Map<String, String> map = http.getAll();
//		int num = map.size();
//		int random = (int) Math.random();
//		int res = random % num;
//		int i = 0;
//
//		for (Map.Entry<String, String> entry : map.entrySet()) {
//			if (i == res) {
//				System.out.println(entry.getKey());
//				HOST = entry.getKey();
//				PORT = Integer.parseInt(entry.getValue());
//			}
//			i++;
//		}

		if (HOST.equals("")) {
			System.out.println("no server found!");
		} else {
			EventLoopGroup group = new NioEventLoopGroup(4);
			try {
				Bootstrap bootstrap = new Bootstrap()
						.channel(NioSocketChannel.class).group(group)
						.handler(new ClientInitializer(n_name));

				Channel channel = bootstrap
						.connect(new InetSocketAddress(HOST, PORT)).sync()
						.channel();

				System.out.println("success>>>>>>>>>>>>>");

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// group.shutdownGracefully();
			}
		}

	}

	/*
	 * Main function
	 * @describe which used run the client
	 * */	
	public static void main(String[] args) throws Exception {
		List<String> nn = new ArrayList<String>();
		int flag = 1;
		Scanner scanner = new Scanner(System.in);
		while(flag!=2){
			
			
			System.out.println("1 input, 2 run：");
			flag = scanner.nextInt();
			if(flag==2) break;
			
			 scanner.nextLine();
			System.out.println("Class Name：");
			if(flag!=2){
			String name = scanner.nextLine();
			nn.add(name);}
		}
		if(nn.size()>0){
		Client client = new Client(nn);
		client.run();
		}else
		{
			System.out.println("illegal input!");
		}
	}
}
