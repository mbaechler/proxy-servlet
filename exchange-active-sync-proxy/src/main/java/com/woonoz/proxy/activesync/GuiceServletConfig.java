package com.woonoz.proxy.activesync;


import java.util.Arrays;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.woonoz.proxy.servlet.GuiceProxyServlet;
import com.woonoz.proxy.servlet.SimpleGuiceProxyServletModule;
import com.woonoz.proxy.servlet.config.EnvironmentGuiceConfiguration;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class GuiceServletConfig extends GuiceServletContextListener {
	
	private List<Class<? extends HeadersFilter>> clientFilters;
	private List<Class<? extends HeadersFilter>> serverFilters;
	private ProxyServletConfig proxyServletConfig;

	public GuiceServletConfig() {
		proxyServletConfig = new EnvironmentGuiceConfiguration(10, 1000000, 1000000);
		clientFilters = Arrays.<Class<? extends HeadersFilter>>asList(EAS12Point1.class);
		serverFilters = Arrays.<Class<? extends HeadersFilter>>asList(ServerAS12Point1.class);
	}
	
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
				new SimpleGuiceProxyServletModule(proxyServletConfig, clientFilters, serverFilters),
				new ServletModule() {
			@Override
			protected void configureServlets() {
				super.configureServlets();
				serve("/*").with(GuiceProxyServlet.class);
			}
		});
	}
	
}
