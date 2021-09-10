package com.danej.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import hpe3par.base.DriveEnclosure;
//import hpe3par.base.PhysicalDrive;
//import utilities.FileWriter;

@Service
public class CLIDataAccessObject {
	
	@Autowired
	private Hpe3parSshAPIAccessor sshApi;
	
	private Map<String, String> cmdPatternMap = Map.of(
			"showpd","[^0-9:]([0-9]{1,2})"
					+ "\\s+"
					+ "([0-9]+):[0-9]+:[0-9]+"
					+ "\\s+"
					+ "([a-zA-Z]+)"
					+ "\\s+"
					+ "([0-9]+)"
					+ "\\s+"
					+ "([a-zA-Z]+)"	,
			"showcage", "[^0-9:]([0-9]{1,2})"
					+ "\\s+"
					+ "(.*)"
					+ "\\s+"
					+ "([0-9]+:[0-9]+:[0-9]+)"
					+ "\\s+"
					+ "\\d+"
					+ "\\s+"
					+ "([0-9]+:[0-9]+:[0-9]+)"
					+ ".*"
					+ "\\s+"
					+ "([A-Za-z0-9]+)"
					+ "\\s+"
					+ "[A-Za-z0-9]+"
					+ "\\s+"
					+ "\\R",
			"statcpu -iter 1 -t", "([0-1]),total"
					+ "\\s+"
					+ "([0-9]+)"	//user
					+ "\\s+"
					+ "([0-9]+)"	//sys
					+ "\\s+"
					+ "([0-9]+)"	//idle
					+ "\\s+"
					+ "([0-9]+)"	//intr
					+ "\\s+"
					+ "([0-9]+)",	//ctxt
			"showsys", "[0-9A-Za-z]"
					+ "\\s+"
					+ ".*"
					+ "\\s+"
					+ "([0-9]+)"	// total
					+ "\\s+"
					+ "([0-9]+)"	// alloc
					+ "\\s+"
					+ "([0-9]+)"	// free
					+ "\\s+"
					+ "([0-9]+)"	// failed
			);
	
	public CLIDataAccessObject() throws FileNotFoundException, IOException {
		sshApi = new Hpe3parSshAPIAccessor();
	}


	public String getStats() throws IOException {
		
		String xml = 
				"<data>"
				+ getCPU()
				+ getCapacity()
				+ "</data>";
		
		return xml;
	}
	
	private String getCPU() throws IOException {

		String cmd = "statcpu -iter 1 -t";
		String content = sshApi.exec(cmd);
		
		String xml = "";

		Pattern p = Pattern.compile(cmdPatternMap.get(cmd));
		Matcher m = p.matcher(content);
		int user_total = 0, sys_total =0, idle_total = 0;
		
		while(m.find()) {
			String node = m.group(1);
			int user = Integer.parseInt(m.group(2));
			int sys = Integer.parseInt(m.group(3));
			int idle = Integer.parseInt(m.group(4));
			String intr = m.group(5);
			String ctxt = m.group(6);
			System.out.println(
					"node: " + node
					+ ", user:" + user 
					+ ", sys: " + sys
					+ ", idle: " + idle
					+ ", intr: " + intr
					+ ", ctxt: " + ctxt);
			
			user_total = user_total + user;
			sys_total = sys_total + sys;
			idle_total = idle_total + idle;
			
		}
		
		user_total = user_total / 2;
		sys_total = sys_total / 2;
		idle_total = idle_total / 2;
		
		int cpu_util = 100 - idle_total;
		
		xml = "<cpu_util_pct>" + cpu_util + "</cpu_util_pct>";
		System.out.println("CPU XML: " + xml);
		
		return xml;
	}

	private String getCapacity() throws IOException {
		
		String cmd = "showsys";
		String content = sshApi.exec(cmd);
		
		String xml = "";

		Pattern p = Pattern.compile(cmdPatternMap.get(cmd));
		Matcher m = p.matcher(content);
		
		double total=0, alloc=0, free=0, failed=0;
		double alloc_pct=0, free_pct=0, failed_pct=0;
		
		while(m.find()) {
			
			// all in GB
			total = Double.parseDouble(m.group(1))/1024;
			alloc = Double.parseDouble(m.group(2))/1024;
			free = Double.parseDouble(m.group(3))/1024;
			failed = Double.parseDouble(m.group(4))/1024;
			System.out.println("total: " + total + ", alloc: " + alloc + ", free: " + free + ", failed: " + failed);
			
			alloc_pct = (100*alloc/total);
			free_pct = (100*free/total);
			failed_pct = (100*failed/total);
			
			System.out.println("alloc %: " + alloc_pct
				+ ", free %: " + free_pct
				+ ", failed % : " + failed_pct);
		}
		
		xml = "<total_capacity_gb>" + String.format("%.0f", total) +  	"</total_capacity_gb>"
		+ "<allocated_capacity_gb>"	+ String.format("%.0f", alloc) + "</allocated_capacity_gb>"
		+ "<free_capacity_gb>" 		+ String.format("%.0f", free) + 	"</free_capacity_gb>"
		+ "<failed_capacity_gb>" 	+ String.format("%.0f", failed) + 	"</failed_capacity_gb>"
		+ "<allocated_capacity_pct>"+ String.format("%.2f", alloc_pct) + "</allocated_capacity_pct>"
		+ "<free_capacity_pct>" 	+ String.format("%.2f", free_pct) + 	"</free_capacity_pct>"
		+ "<failed_capacity_pct>" 	+ String.format("%.2f", failed_pct) + 	"</failed_capacity_pct>";
		
		System.out.println("Capacity XML: " + xml);
		
		return xml;
	}
	/*
	public ArrayList<PhysicalDrive> getPhysicalDrivesList() throws IOException {
		
		String cmd = "showpd";
		System.out.println("Executing getPhysicalDrives: " + cmd);
		
		ArrayList<PhysicalDrive> driveList = null;
		String content = sshApi.exec(cmd);
		appendToFile(cmd, content);
		Pattern p = Pattern.compile(cmdPatternMap.get(cmd));
		Matcher m = p.matcher(content);
		while(m.find()) {
			if(driveList==null) {
				driveList = new ArrayList<>();
			}
//			String id = m.group(1);
//			String driveEnclosureId = m.group(2);
//			String state = m.group(3);
			String id = m.group(1);
			String enclosureId = m.group(2);
			String type = m.group(3);
			String rpm = m.group(4);
			String state = m.group(5);
			System.out.println(
					"drive:" + id 
					+ ", state: " + state
					+ ", type: " + type
					+ ", rpm: " + rpm
					+ ", enclosure: " + enclosureId);
			
			PhysicalDrive drive = new PhysicalDrive();
			drive.setId(Integer.parseInt(id));
			drive.setDriveEnclosureId(Integer.parseInt(enclosureId));
			drive.setRpm(Integer.parseInt(rpm));
			drive.setType(type);
			drive.setState(state);
			driveList.add(drive);
		}
		
		return driveList;
	}
	
	private void appendToFile(String command, String content) throws IOException {
		FileWriter.getInstance().append("\nSSH " + command);
		FileWriter.getInstance().append(content);
	}


	public ArrayList<DriveEnclosure> getDriveEnclosuresList() throws IOException {
		String cmd = "showcage";
		System.out.println("Executing getDriveEnclosuresList: " + cmd);

		ArrayList<DriveEnclosure> deList = null; 

		String content = sshApi.exec(cmd);
		appendToFile(cmd, content);
		Pattern p= Pattern.compile(cmdPatternMap.get(cmd));
		Matcher m = p.matcher(content);
		while(m.find()) {
			if(deList==null) {
				deList = new ArrayList<>();
			}
			String id = m.group(1);
			String name = m.group(2);
			String aPortString = m.group(3);
			String bPortString = m.group(4);
			String model = m.group(5);
			System.out.println("cageId: " + id + ", cageName: " + name + ", model: " + model);
			DriveEnclosure enclosure = new DriveEnclosure();
			enclosure.setId(Integer.parseInt(id));
			enclosure.setName(name);
			enclosure.setModel(model);
			enclosure.setAPortString(aPortString);
			enclosure.setBPortString(bPortString);
			deList.add(enclosure);
		}
		
		return deList;
	}
	
	*/

}
