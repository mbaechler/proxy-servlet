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

import javax.servlet.http.Cookie;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.Assert;
import org.junit.Test;

import com.woonoz.proxy.servlet.CookieFormatter;
import com.woonoz.proxy.servlet.InvalidCookieException;


public class CookieFormatterTest {

	@Test
	public void testJSessionIdCookie() throws InvalidCookieException {
		String sessionId = "JJJ2234312421";
		Cookie cookie = new Cookie("JSESSIONID", sessionId);
		cookie.setPath("/");
		CookieFormatter formatter = CookieFormatter.createFromServletCookie(cookie);
		Assert.assertEquals("JSESSIONID=JJJ2234312421; path=/;", formatter.asString());
	}

	@Test
	public void testJSessionIdApacheCookie() throws InvalidCookieException {
		String sessionId = "JJJ2234312421";
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", sessionId);
		cookie.setPath("/");
		CookieFormatter formatter = CookieFormatter.createFromApacheCookie(cookie);
		Assert.assertEquals("JSESSIONID=JJJ2234312421; path=/;", formatter.asString());
	}

	
	@Test
	public void testCookieNoPath() throws InvalidCookieException {
		String sessionId = "JJJ2234312421";
		Cookie cookie = new Cookie("JSESSIONID", sessionId);
		CookieFormatter formatter = CookieFormatter.createFromServletCookie(cookie);
		Assert.assertEquals("JSESSIONID=JJJ2234312421;", formatter.asString());
	}

	@Test
	public void testCookieNoValue() throws InvalidCookieException {
		Cookie cookie = new Cookie("JSESSIONID", "");
		cookie.setPath("/");
		CookieFormatter formatter = CookieFormatter.createFromServletCookie(cookie);
		Assert.assertEquals("JSESSIONID=; path=/;", formatter.asString());	
	}

	@Test(expected=InvalidCookieException.class)
	public void testCookieNullValue() throws InvalidCookieException {
		Cookie cookie = new Cookie("JSESSIONID", null);
		cookie.setPath("/");
		CookieFormatter.createFromServletCookie(cookie);
	}

	
	@Test(expected=InvalidCookieException.class)
	public void testCookieNoName() throws InvalidCookieException {
		String sessionId = "JJJ2234312421";
		Cookie cookie = new Cookie("", sessionId);
		CookieFormatter.createFromServletCookie(cookie);
	}

	
}
