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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractHeadersHandler {

	final Map<String, HeadersToSubstitute> headersToHandle; 
	final UrlRewriter urlRewriter;
	
	protected AbstractHeadersHandler(final UrlRewriter urlRewriter, HeadersToSubstitute[] headersToSubstitute) {
		this.urlRewriter = urlRewriter;
		headersToHandle = new HashMap<String, HeadersToSubstitute>();
		for (final HeadersToSubstitute h: headersToSubstitute) {
			headersToHandle.put(toLower(h.getHeader()), h);
		}
	}
	
	private String toLower(final String input) {
		return input.toLowerCase(Locale.ENGLISH);
	}

	public String handleHeader(final String headerName, final String headerValue) throws URISyntaxException, MalformedURLException {
		HeadersToSubstitute handler = headersToHandle.get(toLower(headerName));
		if (handler != null) {
			return handler.handleValue(headerValue, urlRewriter);
		} else {
			return headerValue;
		}
	}

}