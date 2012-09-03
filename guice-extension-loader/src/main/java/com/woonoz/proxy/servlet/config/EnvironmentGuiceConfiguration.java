package com.woonoz.proxy.servlet.config;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Strings;

public class EnvironmentGuiceConfiguration extends ProxyServletConfig {

	private static final String CONFIGURATION_TARGET_KEY = "targetServerUrl";

    public EnvironmentGuiceConfiguration(int maxConnections, int connectionTimeout, int socketTimeout ) {
    	super(envTargetUrl(), maxConnections, connectionTimeout, socketTimeout);
    }
    
	private static URL envTargetUrl() {
		String targetUrl = System.getProperty(CONFIGURATION_TARGET_KEY);
		if (!Strings.isNullOrEmpty(targetUrl)) {
			return createURL(targetUrl);
		}
		throw new ConfigurationException("A property isn't properly set: " + CONFIGURATION_TARGET_KEY);
	}

	private static URL createURL(String targetUrl) {
		try {
			return new URL(targetUrl);
		} catch (MalformedURLException e) {
			throw new ConfigurationException("A property isn't properly set: " + CONFIGURATION_TARGET_KEY, e);
		}
	}
	
}
