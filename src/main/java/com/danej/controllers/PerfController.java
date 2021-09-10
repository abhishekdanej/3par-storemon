package com.danej.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerfController {

	@GetMapping(value = "/data", produces = "text/plain")
//	@ResponseBody
	public String getData() {
		
		int var1 = (int) (Math.random()*101);
		int var2 = (int) (Math.random()*100);
		System.out.println(var1 + " - " + var2);
		
		String resp1 = 	"<?xml version=\"1.0\"?>\n"
				+ "<data>\n"
				+ "	<cpu>" + var1 + "</cpu>\n"
				+ "	<disk>" + var2 + "</disk>\n"
				+ "</data>"
				;
		
		String resp2 = "<data>"
				+ "<disk_util>" + var1 + "</disk_util>"
				+ "<cpu_util>" + var2 + "</cpu_util>"
				+ "</data>"
				;
		
		return resp2;
	}
}
