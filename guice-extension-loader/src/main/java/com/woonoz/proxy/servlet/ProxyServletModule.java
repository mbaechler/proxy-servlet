package com.woonoz.proxy.servlet;

import com.google.inject.AbstractModule;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;

public class ProxyServletModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HttpRequestHandler.Factory.class).to(GuiceHttpRequestHandlerFactory.class);
	}
	
}
