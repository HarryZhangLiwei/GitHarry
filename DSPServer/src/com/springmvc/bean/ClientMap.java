package com.springmvc.bean;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
 * This map used to describe the address information
 * */
public class ClientMap {
	private static Map<String, String> map = new ConcurrentHashMap<String, String>();
		
	public static void add(String name, String ip){
		map.put(name, ip);
		System.out.println(map.size());
	}
	
	
	public static String get(String name){
		return map.get(name);
	}
	public static void removeByName(String name){
				map.remove(name);
				System.out.println(map.size());
	}
	
	public static Map<String,String> getMap(){
		return map;
	}
	
}
