package com.springmvc.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
/*
 * Test class, please ignore
 * */
@Controller
public class FirstController {
	
	@RequestMapping("/queryItems")
	public ModelAndView queryItems() throws Exception{
		
		ArrayList<String> itemList = new ArrayList<String>();
		
		itemList.add("1");
		itemList.add("2");
		itemList.add("3");
		
		//System.out.println("NewMessage");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("itemlist", itemList);
		
		modelAndView.setViewName("first");
		
		return modelAndView;
	} 
}
