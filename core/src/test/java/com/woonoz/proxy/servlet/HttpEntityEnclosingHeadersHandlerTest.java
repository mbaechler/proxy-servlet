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
import java.net.URISyntaxException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.woonoz.proxy.servlet.AbstractHeadersHandler;
import com.woonoz.proxy.servlet.HttpEntityEnclosingHeadersHandler;
import com.woonoz.proxy.servlet.UrlRewriter;

public class HttpEntityEnclosingHeadersHandlerTest {

	@Test
	public void testReferer() throws URISyntaxException, MalformedURLException {
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		String fromGoogle = "http://www.google.com/";
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String actualValue = handler.handleHeader("Referer", fromGoogle);
		Assert.assertEquals(null, actualValue);
		EasyMock.verify(rewriter);
	}

	@Test
	public void testHost() throws URISyntaxException, MalformedURLException {
		String host = "matthieu-test.woonoz.dev";
		String expectedRewritedHost = "google.com";
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.expect(rewriter.rewriteHost(host)).andReturn(expectedRewritedHost);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String actualValue = handler.handleHeader("Host", host);
		Assert.assertEquals(expectedRewritedHost, actualValue);
		EasyMock.verify(rewriter);
	}
	
	@Test
	public void testContentType() throws URISyntaxException, MalformedURLException {
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String headerValue = "text/xml";
		String actualValue = handler.handleHeader("Content-Type", headerValue);
		Assert.assertEquals(headerValue, actualValue);
		EasyMock.verify(rewriter);
	}

	@Test
	public void testHostDifferentCase() throws URISyntaxException, MalformedURLException {
		String host = "matthieu-test.woonoz.dev";
		String expectedRewritedHost = "google.com";
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.expect(rewriter.rewriteHost(host)).andReturn(expectedRewritedHost);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String actualValue = handler.handleHeader("HoST", host);
		Assert.assertEquals(expectedRewritedHost, actualValue);
		EasyMock.verify(rewriter);
	}
	
	@Test
	public void testContentLenght() throws URISyntaxException, MalformedURLException {
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String headerValue = "46546";
		String actualValue = handler.handleHeader("Content-Length", headerValue);
		Assert.assertEquals(null, actualValue);
		EasyMock.verify(rewriter);
	}

	@Test
	public void testContentTypeMultipart() throws URISyntaxException, MalformedURLException {
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String headerValue = "multipart/form-data";
		String actualValue = handler.handleHeader("Content-type", headerValue);
		Assert.assertEquals(null, actualValue);
		EasyMock.verify(rewriter);
	}

	@Test
	public void testContentTypeNeutral() throws URISyntaxException, MalformedURLException {
		UrlRewriter rewriter = EasyMock.createMock(UrlRewriter.class);
		EasyMock.replay(rewriter);
		AbstractHeadersHandler handler = new HttpEntityEnclosingHeadersHandler(rewriter);
		String headerValue = "binary";
		String actualValue = handler.handleHeader("Content-type", headerValue);
		Assert.assertEquals(headerValue, actualValue);
		EasyMock.verify(rewriter);
	}
}
