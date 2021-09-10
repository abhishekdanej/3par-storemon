package com.danej.utils;

import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

//import utilities.LoadProperties;

@Service
public class Hpe3parSshAPIAccessor {
	
	@Autowired
	private ConfigProperties props;
	
	public Hpe3parSshAPIAccessor() throws FileNotFoundException, IOException {
//		p = LoadProperties.getInstance().props;
		
	}
	
	public String exec(String command) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			JSch jsh = new JSch();
			Session session = jsh.getSession(props.getUsername(), props.getHost());
//					p.getProperty("3par.username"), p.getProperty("3par.host"));
			session.setPassword(props.getPassword());
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection...");
			session.connect();
			System.out.println("Connection established.");

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// X Forwarding
			// channel.setXForwarding(true);

			// channel.setInputStream(System.in);
			channel.setInputStream(null);

			// channel.setOutputStream(System.out);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String s = new String(tmp, 0, i);
					System.out.print(s);
					sb.append(s);
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					System.out.println("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("Connection Closed.");

		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
}
