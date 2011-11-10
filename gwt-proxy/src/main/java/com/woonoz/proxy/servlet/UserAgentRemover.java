package com.woonoz.proxy.servlet;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.inject.Singleton;

import com.woonoz.proxy.servlet.http.header.HeadersFilter;
import com.woonoz.proxy.servlet.url.UrlRewriter;

@Singleton
public class UserAgentRemover implements HeadersFilter {

	@Override
	public String getHeader() {
		return "User-Agent";
	}

	@Override
	public String handleValue(String headerValue, UrlRewriter urlRewriter)
			throws URISyntaxException, MalformedURLException {
		return null;
	}
	
}
