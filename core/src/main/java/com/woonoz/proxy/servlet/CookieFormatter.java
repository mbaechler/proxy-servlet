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

class CookieFormatter {

	private final String name;
	private final String value;
	private final String path;

	public static CookieFormatter createFromApacheCookie(org.apache.http.cookie.Cookie cookie) {
		return new CookieFormatter(cookie.getName(), cookie.getValue(), cookie.getPath());
	}
	
	public static CookieFormatter createFromServletCookie(javax.servlet.http.Cookie cookie) {
		return new CookieFormatter(cookie.getName(), cookie.getValue(), cookie.getPath());
	}
	
	private CookieFormatter(String name, String value, String path) {
		this.name = name;
		this.value = value;
		this.path = path;
		if (!isCookieValueCorrect(name)) {
			throw new InvalidCookieException("name can't be empty");
		}
		if (value == null) {
			throw new InvalidCookieException("value can't be null");
		}
	}

	protected boolean isCookieValueCorrect(final String value) {
		return value != null && value.length() > 0;
	}
	
	public String asString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append("=").append(value).append(";");
		if (path != null) {
			builder.append(" path=").append(path).append(";");
		}
		return builder.toString();
	}
}