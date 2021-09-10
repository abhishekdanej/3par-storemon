package com.danej.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.danej.utils.CLIDataAccessObject;



@Service
public class DataService {

	
	@Autowired
	private CLIDataAccessObject dao;
	
	public String getPerformanceStats() {
		
		
		try {
			return dao.getStats();
		} catch (IOException e) {
			System.out.println("Error While Connecting");
			System.out.println(e.getMessage());
		}

		return null;
		
	}

}
