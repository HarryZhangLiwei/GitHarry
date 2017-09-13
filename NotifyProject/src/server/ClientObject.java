package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.netty.channel.ChannelHandlerContext;


/*
 * This class used to describe the client
 * */
public class ClientObject {
	private String address;
	private ChannelHandlerContext sockeChannel;
	private Set<String> type = new HashSet<String>();
	
	public String getClientAddress() {
		return address;
	}
	public void setClientAddress(String address) {
		this.address = address;
	}
	public ChannelHandlerContext getSockeChannel() {
		return sockeChannel;
	}
	public void setSockeChannel(ChannelHandlerContext sockeChannel) {
		this.sockeChannel = sockeChannel;
	}
	public Set<String> getType() {
		return type;
	}
	public void setType(Set<String> type) {
		this.type = type;
	}
	
}
