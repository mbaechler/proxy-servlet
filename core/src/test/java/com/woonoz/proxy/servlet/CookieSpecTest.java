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

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;

public class CookieSpecTest {

	@Test
	public void testParseCookie() throws MalformedCookieException {
		BestMatchSpec parser = new BestMatchSpec();
		List<Cookie> cookies = parser.parse(new BasicHeader("Set-Cookie", "JSESSIONID=F39EC36E999C90604EAFF7A87F88DA58; Path=/"), 
				new CookieOrigin("localhost", 80, "/toto", false));
		Assert.assertEquals(cookies.size(), 1);
		Assert.assertEquals(cookies.get(0).getPath(), "/");
		Assert.assertEquals(cookies.get(0).getName(), "JSESSIONID");
		Assert.assertEquals(cookies.get(0).getValue(), "F39EC36E999C90604EAFF7A87F88DA58");
	}
	
}
