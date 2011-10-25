package com.woonoz.proxy.servlet.http;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.woonoz.proxy.servlet.base.ClientHeadersHandler;
import com.woonoz.proxy.servlet.base.ServerHeadersHandler;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public interface HttpRequestHandler {

	interface Factory {
		HttpRequestHandler create(HttpServletRequest request, URL targetServer);
	}
	
	ClientHeadersHandler getClientHeadersHandler();
	ClientHeadersHandler getHttpEntityEnclosingClientHeadersHandler();
	ServerHeadersHandler getServerHeadersHandler();
	UrlRewriter getUrlRewriter();
	
}
