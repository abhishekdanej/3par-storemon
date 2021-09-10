package com.danej;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLIParserTest {

	public static void main(String[] args) throws IOException {

		String showsys = "sampledata\\showsys.txt";
		String statcpu = "sampledata\\statcpu.txt";
		
		List<String> fileLines = Files.readAllLines(Path.of(statcpu));
		fileLines.forEach(line -> {
			System.out.println(line);
		});
		
		/*
		00:02:22 09/11/2021
		node,cpu user sys idle intr/s ctxt/s
		 0,total    0   2   98  18867  19613
		 1,total    0   2   98  14103  17508
		 */
		System.out.println("CPU");
		String content = Files.readString(Path.of(statcpu));
		String patternString = "([0-1]),total"
				+ "\\s+"
				+ "([0-9]+)"	//user
				+ "\\s+"
				+ "([0-9]+)"	//sys
				+ "\\s+"
				+ "([0-9]+)"	//idle
				+ "\\s+"
				+ "([0-9]+)"	//intr
				+ "\\s+"
				+ "([0-9]+)"	//ctxt
//				+ "\\R"
				;
		Pattern pattern = Pattern.compile(patternString);
		Matcher m = pattern.matcher(content);
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
			
		}
		
		
		////////////
		/*
		 *                                                                          ----------------(MB)----------------
     	ID -----Name------ ----Model---- --Serial-- Nodes Master ClusterLED TotalCap AllocCap  FreeCap FailedCap
		0x1D67F 3par.mfdemo.net HPE_3PAR 8200 4C17121222     2      0 Off        40550400 28105728 12444672         0
		 */
		
		System.out.println("=============");
		System.out.println("showsys");
		content = Files.readString(Path.of(showsys));
		patternString = "[0-9A-Za-z]"
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
			;
		pattern = Pattern.compile(patternString);
		m = pattern.matcher(content);
		while(m.find()) {
			
			// all in GB
			double total = Double.parseDouble(m.group(1))/1024;
			double alloc = Double.parseDouble(m.group(2))/1024;
			double free = Double.parseDouble(m.group(3))/1024;
			double failed = Double.parseDouble(m.group(4))/1024;
			System.out.println("total: " + total + ", alloc: " + alloc + ", free: " + free + ", failed: " + failed);
			System.out.println("alloc %: " + Math.round(100*alloc/total) 
				+ ", free %: " + Math.round(100*free/total) 
				+ ", failed % : " + Math.round(100*failed/total));
		}
		
		
	}

}
