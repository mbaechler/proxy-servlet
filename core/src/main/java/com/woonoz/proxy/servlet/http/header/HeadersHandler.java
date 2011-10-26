package com.woonoz.proxy.servlet.http.header;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public interface HeadersHandler {

	String handleHeader(final String headerName, final String headerValue)
			throws URISyntaxException, MalformedURLException;

}