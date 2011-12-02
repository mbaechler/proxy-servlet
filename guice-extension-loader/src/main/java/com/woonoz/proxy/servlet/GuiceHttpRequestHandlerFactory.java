package com.woonoz.proxy.servlet;

import java.net.URL;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.woonoz.proxy.servlet.base.ClientHeadersHandler;
import com.woonoz.proxy.servlet.base.HttpEntityEnclosingHeadersHandler;
import com.woonoz.proxy.servlet.base.HttpRequestHandlerImpl;
import com.woonoz.proxy.servlet.base.ServerHeadersHandler;
import com.woonoz.proxy.servlet.base.UrlRewriterImpl;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public class GuiceHttpRequestHandlerFactory implements HttpRequestHandler.Factory {

	private final Set<HeadersFilter> clientFilters;
	private final Set<HeadersFilter> serverFilters;

	@Inject
	private GuiceHttpRequestHandlerFactory(
			@Named("clientFilters")Set<HeadersFilter> clientFilters,
			@Named("serverFilters")Set<HeadersFilter> serverFilters) {
		this.clientFilters = clientFilters;
		this.serverFilters = serverFilters;
	}

	@Override
	public HttpRequestHandler create(HttpServletRequest request, URL targetServer) {
		UrlRewriter urlRewriter = new UrlRewriterImpl(request, targetServer);
		ClientHeadersHandler clientHeadersHandler = new ClientHeadersHandler(urlRewriter, clientFilters);
		ServerHeadersHandler serverHeadersHandler = new ServerHeadersHandler(urlRewriter, serverFilters);
		HttpEntityEnclosingHeadersHandler httpEntityEnclosingHeadersHandler = new HttpEntityEnclosingHeadersHandler(urlRewriter, clientFilters);
		return new HttpRequestHandlerImpl(urlRewriter, clientHeadersHandler,
				httpEntityEnclosingHeadersHandler, serverHeadersHandler);
	}
}
