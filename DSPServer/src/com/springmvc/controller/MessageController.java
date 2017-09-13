package com.springmvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springmvc.bean.ClientMap;
import com.springmvc.bean.Message;

/*
 * Services
 * */

@Controller
public class MessageController {
	
	/*
	 * Post method
	 * Called by post http, add new record to map
	 * */
	@RequestMapping(value = "/Server", method = {RequestMethod.POST})
	public void NewMessage(@RequestBody Message message) throws Exception{
		if(ClientMap.getMap().containsKey(message.getUrl())&&ClientMap.getMap().containsValue(message.getPort())){
			
		}else{
			ClientMap.add(message.getUrl(), message.getPort());
			System.out.println("New!");
		}
	} 
	
	/*
	 * Get method
	 * Called by get http, return all records of map
	 * */
	@RequestMapping(value = "/Server", method = {RequestMethod.GET})
	@ResponseBody
	public List<Message> getAllMessage(){
		System.out.println("get");
		List<Message> list = new ArrayList<Message>();
		for(Map.Entry<String, String> entry: ClientMap.getMap().entrySet()){
			Message msg = new Message();
			msg.setUrl(entry.getKey());
			msg.setPort(entry.getValue());
			list.add(msg);
		} 
		return list;
	}
	
	/*
	 * Delete method
	 * Called by delete http, remove one record from map
	 * */
	@RequestMapping(value = "/Server", method = {RequestMethod.DELETE})
	@ResponseBody
	public void deleteMessage(@RequestBody Message message){
		ClientMap.removeByName(message.getUrl());
	}
	
	
	
}
