package com.danej.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danej.service.DataService;

@RestController
public class PerfController {
	
	@Autowired
	private DataService dataService;

	@GetMapping(value = "/data", produces = "text/plain")
//	@ResponseBody
	public String getData() {
		
		int var1 = (int) (Math.random()*101);
		int var2 = (int) (Math.random()*100);
		System.out.println(var1 + " - " + var2);
		
		/*
		 * String resp1 = "<?xml version=\"1.0\"?>\n" + "<data>\n" + "	<cpu>" + var1 +
		 * "</cpu>\n" + "	<disk>" + var2 + "</disk>\n" + "</data>" ;
		 * 
		 * String resp2 = "<data>" + "<disk_util>" + var1 + "</disk_util>" +
		 * "<cpu_util>" + var2 + "</cpu_util>" + "</data>" ;
		 */
		
		String data = dataService.getPerformanceStats();
		System.out.println(data);
		
		return data;
	}
}
