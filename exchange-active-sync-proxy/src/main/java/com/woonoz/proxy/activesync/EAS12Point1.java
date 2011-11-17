package com.woonoz.proxy.activesync;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.woonoz.proxy.servlet.http.header.HeadersFilter;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public class EAS12Point1 implements HeadersFilter {

	@Override
	public String getHeader() {
		return "MS-ASProtocolVersion";
	}
	
	@Override
	public String handleValue(String headerValue, UrlRewriter urlRewriter)
			throws URISyntaxException, MalformedURLException {
		return "12.1";
	}
	
}
