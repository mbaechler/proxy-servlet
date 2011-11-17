package com.woonoz.proxy.servlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class GuiceServletConfig extends GuiceServletContextListener {

	private URL targetUrl;
	private List<Class<? extends HeadersFilter>> filters;

	public GuiceServletConfig() throws MalformedURLException {
		targetUrl = new URL("http://www.google.fr");
		filters = Arrays.<Class<? extends HeadersFilter>>asList(UserAgentRemover.class);
	}
	
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
				new SimpleGuiceProxyServletModule(new ProxyServletConfig(targetUrl), filters),
				new ServletModule() {
			@Override
			protected void configureServlets() {
				super.configureServlets();
				serve("/happy/*").with(GuiceProxyServlet.class);
			}
		});
	}
	
}
