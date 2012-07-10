package com.woonoz.proxy.servlet.config;

public class ConfigurationException extends RuntimeException {

	public ConfigurationException(String message) {
		super(message);
	}
	
	public ConfigurationException(String message, Exception ex) {
		super(message, ex);
	}
}
