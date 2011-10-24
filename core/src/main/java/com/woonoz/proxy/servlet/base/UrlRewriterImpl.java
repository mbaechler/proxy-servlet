/*
 * Copyright 2010 Woonoz S.A.S.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.woonoz.proxy.servlet.base;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicHeader;

import com.woonoz.proxy.servlet.http.cookie.CookieFormatter;
import com.woonoz.proxy.servlet.http.exception.InvalidCookieException;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public class UrlRewriterImpl implements UrlRewriter {

	private final URL targetServer;
	private final HttpServletRequest servletRequest;

	public UrlRewriterImpl(HttpServletRequest servletRequest, final URL targetServer) {
		this.targetServer = targetServer;
		this.servletRequest = servletRequest;
	}

	public String rewriteHost(final String host) throws URISyntaxException, MalformedURLException {
		URI hostAsUri = new URI("http://" + host);
		if (hostIsSameAsServletHost(hostAsUri)) {
			return getTargetHostString();
		} else {
			return host;
		}
	}

	public URI rewriteUri(URI url) throws URISyntaxException, MalformedURLException {
		if (requestedUrlPointsToServlet(url)) {
			final String targetPath = rewritePathIfNeeded(url.getPath());
			return URIUtils.createURI(targetServer.getProtocol(), targetServer.getHost(), 
					targetServer.getPort(), targetPath, servletRequest.getQueryString(), null);
		} else {
			return url;
		}
	}

	public String rewriteCookie(String headerValue) throws URISyntaxException, InvalidCookieException {
		BestMatchSpec parser = new BestMatchSpec();
		List<Cookie> cookies;
		try {
			cookies = parser.parse(new BasicHeader("Set-Cookie", headerValue), 
					new CookieOrigin(targetServer.getHost(), getPortOrDefault(targetServer), targetServer.getPath(), false));
		} catch (MalformedCookieException e) {
			throw new InvalidCookieException(e);
		}
		if (cookies.size() != 1) {
			throw new InvalidCookieException();
		}
		Cookie cookie = rewriteCookiePathIfNeeded(cookies.get(0));
		CookieFormatter cookieFormatter = CookieFormatter.createFromApacheCookie(cookie);
		return cookieFormatter.asString();
	}
	
	private Cookie rewriteCookiePathIfNeeded(Cookie cookie) {
		if (removeTrailingSlashes(cookie.getPath()).equals(removeTrailingSlashes(targetServer.getPath()))) {
			BasicClientCookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
			newCookie.setPath(servletRequest.getContextPath() + servletRequest.getServletPath());
			return newCookie;
		} else {
			return cookie;
		}
	}
	
	private String rewritePathIfNeeded(String requestedPath) {
		String servletURI = servletRequest.getContextPath() + servletRequest.getServletPath();
		if (!targetServer.getPath().isEmpty() && requestIsSubpathOfServlet(requestedPath)) {
			return appendPathFragments(targetServer.getPath(), requestedPath.substring(servletURI.length()));
		} else {
			return requestedPath;
		}
	}

	private static String appendPathFragments(final String firstPart, final String secondPart) {
		return removeTrailingSlashes(firstPart) + "/" + removeLeadingSlashes(secondPart);
	}
	
	private static String removeTrailingSlashes(final String text) {
		if (text.isEmpty()) {
			return text;
		}
		final CharacterIterator it = new StringCharacterIterator(text);
		Character c = it.last();
		while (c.equals('/')) {
			c = it.previous();
		}
		return text.substring(0, it.getIndex() + 1);
	}

	private static String removeLeadingSlashes(final String text) {
		if (text.isEmpty()) {
			return text;
		}
		final CharacterIterator it = new StringCharacterIterator(text);
		Character c = it.first();
		while (c.equals('/')) {
			c = it.next();
		}
		return text.substring(it.getIndex());
	}
	
	private boolean requestedUrlPointsToServlet(final URI requestedUrl) throws MalformedURLException {
		return hostIsSameAsServletHost(requestedUrl) &&
		requestIsSubpathOfServlet(requestedUrl.getPath());
	}

	private boolean hostIsSameAsServletHost(final URI requestedUrl) throws MalformedURLException {
		return requestedUrl.getHost().equals(getServletHost()) &&
		getPortOrDefault(requestedUrl.toURL()) == getServletPort();
	}

	private boolean requestIsSubpathOfServlet(final String requestedUrl) {
		return requestedUrl.startsWith(servletRequest.getRequestURI());
	}

	private String getTargetHostString() {
		int port = getTargetPort();
		if (port == -1) {
			return getTargetHost();
		} else {
			return getTargetHost() + ":" + getTargetPort();
		}
	}

	private String getServletHost() {
		return servletRequest.getServerName();
	}

	private int getServletPort() {
		return servletRequest.getServerPort();
	}
	
	private String getTargetHost() {
		return targetServer.getHost();
	}

	private int getTargetPort() {
		return targetServer.getPort();
	}

	private int getPortOrDefault(URL url) {
		final int port = url.getPort();
		if (port == -1) {
			return url.getDefaultPort();
		} else {
			return port;
		}
	}

}
