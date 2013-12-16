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

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.woonoz.proxy.servlet.UrlRewriter;
import com.woonoz.proxy.servlet.UrlRewriterImpl;


public class UrlRewriterTest {

	private HttpServletRequest buildGoogleDotComServletRequest() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		/*
		 *  The servlet used to process this request was matched using the "/*" pattern (see javadoc of HttpServletRequest#getServletPath())
		 */
		EasyMock.expect(request.getServletPath()).andReturn("").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/proxy").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("/proxy").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("www.google.com").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(80).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	private HttpServletRequest buildGoogleDotComOnPortHeightyServletRequest() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn("/toto").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/toto").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("").anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("www.google.com").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(80).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	private HttpServletRequest buildGoogleDotComOnPortFiftyFiveServletRequest() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServletPath()).andReturn("/proxy/").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/proxy/").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("").anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("www.google.com").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(55).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	private HttpServletRequest buildRequestBug1() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn("/com.woonoz.gwt.woonoz.Woonoz/proxy").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/wol-wnz-pro/com.woonoz.gwt.woonoz.Woonoz/proxy/gwt/AuthenticationServiceService").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("/wol-wnz-pro").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("matthieu-test.woonoz.dev").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(8180).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	private HttpServletRequest buildRequestBug2() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn("/com.woonoz.gwt.woonoz.Woonoz/proxy").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/wol-wnz-pro-2/com.woonoz.gwt.woonoz.Woonoz/proxy/gwt/AuthenticationServiceService").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("/wol-wnz-pro-2").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("online.woonoz-pro.com").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(443).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	private URL buildRedirectUrl() throws MalformedURLException {
		return new URL("http://localhost:8180/services/");
	}

	private URL buildRedirectUrlRoot() throws MalformedURLException {
		return new URL("http://localhost:8180");
	}

	private URL buildRedirectUrlWithoutPort() throws MalformedURLException {
		return new URL("http://localhost/services/");
	}

	@Test
	public void testRewriteHostNoRewrite() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		UrlRewriter rewriter = new UrlRewriterImpl(request, buildRedirectUrl());
		String perduDotComHost = "www.perdu.com";
		Assert.assertEquals(perduDotComHost, rewriter.rewriteHost(perduDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHost() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com";
		Assert.assertEquals("localhost:8180", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostTargetWithoutPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrlWithoutPort();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com";
		Assert.assertEquals("localhost", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostTargetWithPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComOnPortHeightyServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com";
		Assert.assertEquals("localhost:8180", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostRequestWithPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com:80";
		Assert.assertEquals("localhost:8180", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostWithRequestAndTargetWithPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComOnPortHeightyServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com:80";
		Assert.assertEquals("localhost:8180", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostWithRequestAndTargetWithDifferentPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComOnPortHeightyServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com:443";
		Assert.assertEquals("www.google.com:443", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteHostWithRequestAndTargetWithDifferentPort2() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComOnPortFiftyFiveServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String googleDotComHost = "www.google.com";
		Assert.assertEquals("www.google.com", rewriter.rewriteHost(googleDotComHost));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUri() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.google.com/proxy/docs/index.html");
		URI expectedUri = new URI("http://localhost:8180/services/docs/index.html");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriBug1() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildRequestBug1();
		URL redirectUrl = new URL("http://localhost:8180/services/");
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://matthieu-test.woonoz.dev:8180/wol-wnz-pro/com.woonoz.gwt.woonoz.Woonoz/proxy/gwt/AuthenticationServiceService");
		URI expectedUri = new URI("http://localhost:8180/services/gwt/AuthenticationServiceService");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriBug2() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildRequestBug2();
		URL redirectUrl = new URL("http://localhost:8180/services-wnz-pro-2/");
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI requestUri = new URI("https://online.woonoz-pro.com/wol-wnz-pro-2/com.woonoz.gwt.woonoz.Woonoz/proxy/gwt/AuthenticationServiceService");
		URI expectedUri = new URI("http://localhost:8180/services-wnz-pro-2/gwt/AuthenticationServiceService");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(requestUri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriRedirectToRoot() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrlRoot();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.google.com/proxy/doc/from/root/index.html");
		URI expectedUri = new URI("http://localhost:8180/doc/from/root/index.html");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriRequestContainsPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComOnPortFiftyFiveServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.google.com:55/proxy/docs/index.html");
		URI expectedUri = new URI("http://localhost:8180/services/docs/index.html");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriAnotherHost() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.perdu.com/proxy/docs/index.html");
		Assert.assertEquals(uri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriAnotherPath() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.google.com/application/docs/index.html");
		Assert.assertEquals(uri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteUriAnotherPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI uri = new URI("http://www.google.com:443/proxy/docs/index.html");
		Assert.assertEquals(uri, rewriter.rewriteUri(uri));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteCookieMatchingPath() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String cookie = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; Path=/services;";
		String expected = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; path=/proxy;";
		Assert.assertEquals(expected, rewriter.rewriteCookie(cookie));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteCookieMatchingPathNoPort() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrlWithoutPort();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String cookie = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; Path=/services;";
		String expected = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; path=/proxy;";
		Assert.assertEquals(expected, rewriter.rewriteCookie(cookie));
		EasyMock.verify(request);
	}


	@Test
	public void testRewriteCookieMatchingPathBug1() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildRequestBug1();
		URL redirectUrl = new URL("http://localhost:8180/services/");
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String cookie = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; Path=/services;";
		String expected = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; path=/wol-wnz-pro/com.woonoz.gwt.woonoz.Woonoz/proxy;";
		Assert.assertEquals(expected, rewriter.rewriteCookie(cookie));
		EasyMock.verify(request);
	}

	@Test
	public void testRewriteCookieNotMatchingPath() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildGoogleDotComServletRequest();
		URL redirectUrl = buildRedirectUrl();
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		String cookie = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; Path=/toto/tata;";
		String expected = "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; path=/toto/tata;";
		Assert.assertEquals(expected, rewriter.rewriteCookie(cookie));
		EasyMock.verify(request);
	}

	private HttpServletRequest buildRequestGithubIssue10() {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn("/").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/test-proxy/").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("/test-proxy").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("localhost").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(8080).anyTimes();
		EasyMock.replay(request);
		return request;
	}
	
	@Test
	public void testRewriteUriGithubIssue10() throws MalformedURLException, URISyntaxException {
		HttpServletRequest request = buildRequestGithubIssue10();
		URL redirectUrl = new URL("http://www.programme-tv.net");
		UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		URI requestUri = new URI("http://localhost:8080/test-proxy/");
		URI expectedUri = new URI("http://www.programme-tv.net");
		Assert.assertEquals(expectedUri, rewriter.rewriteUri(requestUri));
		EasyMock.verify(request);
	}

	private static HttpServletRequest builRequestWithUrlEncoding()
	{
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn("/").anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn("/test-proxy/OpenID%20Security%20Best%20Practices").anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn("/test-proxy").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("localhost").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(8080).anyTimes();
		EasyMock.replay(request);
		return request;
	}

	/**
	 * Test rewrite URI with encoded characters (RFC 1738)
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	 
	@Test
	public void testRewriteUriWithEncoding() throws MalformedURLException, URISyntaxException
	{
		 HttpServletRequest request = builRequestWithUrlEncoding();
		 URL redirectUrl = new URL("http://wiki.openid.net/w/page/12995200/");
		 UrlRewriter rewriter = new UrlRewriterImpl(request, redirectUrl);
		 URI requestUri = new
		 URI("http://localhost:8080/test-proxy/OpenID%20Security%20Best%20Practices");
		 URI expectedUri = new URI("http://wiki.openid.net/w/page/12995200/OpenID%20Security%20Best%20Practices");
		 Assert.assertEquals(expectedUri, rewriter.rewriteUri(requestUri));
		 EasyMock.verify(request);
	}
}
