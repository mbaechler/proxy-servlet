package com.woonoz.proxy.servlet.base;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.woonoz.proxy.servlet.http.HttpRequestHandler;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public class HttpRequestHandlerImpl implements HttpRequestHandler {

	private final UrlRewriter urlRewriter;
	private final ClientHeadersHandler clientHeadersHandler;
	private final ServerHeadersHandler serverHeadersHandler;
	private final ClientHeadersHandler httpEntityEnclosingHeadersHandler;

	public static class HttpRequestHandlerFactoryImpl implements HttpRequestHandler.Factory {
		@Override
		public HttpRequestHandler create(HttpServletRequest request, URL targetServer) {
			UrlRewriterImpl urlRewriter = new UrlRewriterImpl(request, targetServer);
			return new HttpRequestHandlerImpl(urlRewriter,
					new ClientHeadersHandler(urlRewriter),
					new HttpEntityEnclosingHeadersHandler(urlRewriter),
					new ServerHeadersHandler(urlRewriter));
		}
	}
	
	public HttpRequestHandlerImpl(UrlRewriter urlRewriter, 
			ClientHeadersHandler clientHeadersHandler, 
			ClientHeadersHandler httpEntityEnclosingHeadersHandler, 
			ServerHeadersHandler serverHeadersHandler) {
		
		this.urlRewriter = urlRewriter;
		this.clientHeadersHandler = clientHeadersHandler;
		this.httpEntityEnclosingHeadersHandler = httpEntityEnclosingHeadersHandler;
		this.serverHeadersHandler = serverHeadersHandler;
	}
	
	@Override
	public ClientHeadersHandler getClientHeadersHandler() {
		return clientHeadersHandler;
	}
	
	@Override
	public ClientHeadersHandler getHttpEntityEnclosingClientHeadersHandler() {
		return httpEntityEnclosingHeadersHandler;
	}
	
	@Override
	public ServerHeadersHandler getServerHeadersHandler() {
		return serverHeadersHandler;
	}
	
	@Override
	public UrlRewriter getUrlRewriter() {
		return urlRewriter;
	}
}
