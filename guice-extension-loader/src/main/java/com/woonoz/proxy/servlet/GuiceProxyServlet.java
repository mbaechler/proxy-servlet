package com.woonoz.proxy.servlet;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;

@Singleton
public class GuiceProxyServlet extends AbstractProxyServlet {
	
	@Inject
	private GuiceProxyServlet(ProxyServletConfig config, HttpRequestHandler.Factory factory) {
		super();
		init(config, factory);
	}
	
	
}
