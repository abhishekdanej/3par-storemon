package com.danej.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "3par")
public class ConfigProperties {

	private String host;
	private String port;
	private String username;
	private String password;
}
