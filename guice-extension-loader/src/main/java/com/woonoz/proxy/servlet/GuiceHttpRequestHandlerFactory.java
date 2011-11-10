package com.woonoz.proxy.servlet;

import java.net.URL;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
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

	@Inject
	private GuiceHttpRequestHandlerFactory(Set<HeadersFilter> clientFilters) {
		this.clientFilters = clientFilters;
	}

	@Override
	public HttpRequestHandler create(HttpServletRequest request, URL targetServer) {
		UrlRewriter urlRewriter = new UrlRewriterImpl(request, targetServer);
		ClientHeadersHandler clientHeadersHandler = new ClientHeadersHandler(urlRewriter, clientFilters);
		HttpEntityEnclosingHeadersHandler httpEntityEnclosingHeadersHandler = new HttpEntityEnclosingHeadersHandler(urlRewriter, clientFilters);
		return new HttpRequestHandlerImpl(urlRewriter, clientHeadersHandler,
				httpEntityEnclosingHeadersHandler, new ServerHeadersHandler(urlRewriter));
	}
}
