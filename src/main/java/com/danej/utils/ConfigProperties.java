package com.danej.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "3par")
public class ConfigProperties {

	private String host;
	private String port;
	private String username;
	private String password;
}
