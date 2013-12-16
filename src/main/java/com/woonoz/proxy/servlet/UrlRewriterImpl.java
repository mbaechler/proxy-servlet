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
package com.woonoz.proxy.servlet;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicHeader;

public class UrlRewriterImpl implements UrlRewriter {

	private final URL targetServer;
	private final HttpServletRequest servletRequest;
	private final String servletURI;

	public UrlRewriterImpl(HttpServletRequest servletRequest, final URL targetServer) {
		this.targetServer = targetServer;
		this.servletRequest = servletRequest;
		this.servletURI = getEncodedServletURI(servletRequest);
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
			return new URIBuilder()
				.setScheme(targetServer.getProtocol())
				.setHost(targetServer.getHost())
				.setPort(targetServer.getPort())
				.setPath(targetPath)
				.setQuery(servletRequest.getQueryString())
				.build();
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
			newCookie.setPath(servletURI);
			return newCookie;
		} else {
			return cookie;
		}
	}

	private String rewritePathIfNeeded(String requestedPath)
	{
		/*
		 * No need to call requestIsSubpathOfServlet(requestedPath) again, since this method is
		 * called only by rewriteUri() and rewriteUri() already calls requestIsSubpathOfServlet()
		 * via requestedUrlPointsToServlet() before calling this.
		 */
		if (!targetServer.getPath().isEmpty()) {
			return appendPathFragments(targetServer.getPath(), requestedPath.substring(servletURI.length()));
		} else {
			return requestedPath.substring(servletURI.length());
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
				/*
				 * requestIsSubpathOfServlet() requires URL in encoded form (getPath() would return path decoded)
				 */
		requestIsSubpathOfServlet(requestedUrl.getRawPath());
	}

	private boolean hostIsSameAsServletHost(final URI requestedUrl) throws MalformedURLException {
		return requestedUrl.getHost().equals(getServletHost()) &&
		getPortOrDefault(requestedUrl.toURL()) == getServletPort();
	}

	/**
	 * Check if a given URL path (encoded) is a subpath of the servlet URI (encoded).
	 * 
	 * @param requestedUrl
	 *            URL path to be checked (URL-encoded form)
	 * @return true if and only if requestedUrl starts with servlet context path
	 */
	private boolean requestIsSubpathOfServlet(final String requestedUrl)
	{
		return requestedUrl.startsWith(servletURI);
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

	/**
	 * Get request's servlet URI encoded, based on results from {@link HttpServletRequest#getContextPath()}
	 * and {@link HttpServletRequest#getRequestURI()} which are both encoded URI/path, and
	 * {@link HttpServletRequest#getServletPath()} which is DECODED path. We cannot just concatenate
	 * context path and servlet path to get the proper result because of these encoding/decoding
	 * differences, especially if the Servlet path contains encoded characters.
	 * @param request 
	 * @return servlet URI (meant to be equivalent to contextPath + URLEncoded(servletPath))
	 * 
	 */
	public static String getEncodedServletURI(HttpServletRequest request)
	{
		final String servletPath = request.getServletPath();
		final String contextPath = request.getContextPath();
		if (servletPath.length() <= 1)
		{
			// '' or '/'
			return contextPath + servletPath;
		}

		// servletPath has more than one character (first must be '/')
		final int contextPathLen = contextPath.length();
		final String requestURI = request.getRequestURI();
		final char[] requestUriChars = requestURI.toCharArray();
		final StringBuilder servletURI = new StringBuilder(contextPath);
		// start index just after context path for matching servletPath
		int requestUriCharIndex = contextPathLen;
		/*
		 * The servletPath is in decoded form, so we use the requestURI to get the corresponding
		 * encoding form
		 */
		for (char ch : servletPath.toCharArray())
		{
			if (ch == '/')
			{
				/*
				 * New path fragment found in servletPath Add all chars in corresponding (encoded)
				 * path fragment from requestURI
				 */
				requestUriCharIndex = copyPathFragment(requestUriChars, requestUriCharIndex, servletURI);
				servletURI.append('/');
				requestUriCharIndex += 1;
			}
		}

		// Add remaining characters
		requestUriCharIndex = copyPathFragment(requestUriChars, requestUriCharIndex, servletURI);
		return servletURI.toString();
	}

	/**
	 * Copy path fragment
	 * 
	 * @param input
	 *            input string
	 * @param beginIndex
	 *            character index from which we look for '/' in input
	 * @param output
	 *            where path fragments are appended
	 * @return last character in fragment + 1
	 */
	private static int copyPathFragment(char[] input, int beginIndex, StringBuilder output)
	{
		int inputCharIndex = beginIndex;
		while (inputCharIndex < input.length)
		{
			final char inputChar = input[inputCharIndex];
			if (inputChar == '/')
			{
				break;
			}

			output.append(inputChar);
			inputCharIndex += 1;
		}

		return inputCharIndex;
	}
}
