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
import java.net.URISyntaxException;
import java.util.Arrays;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.woonoz.proxy.servlet.http.header.AbstractHeadersHandler;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;
import com.woonoz.proxy.servlet.url.UrlRewriter;


public class ClientHeadersHandler extends AbstractHeadersHandler {

	public ClientHeadersHandler(UrlRewriter urlRewriter) {
		this(urlRewriter, ImmutableList.<HeadersFilter>of());
	}
	
	protected ClientHeadersHandler(UrlRewriter urlRewriter, HeadersFilter[] filters) {
		this(urlRewriter, Arrays.asList(filters));
	}
	
	public ClientHeadersHandler(UrlRewriter urlRewriter, Iterable<HeadersFilter> filters) {
		super(urlRewriter, joinFilters(filters));
	}
	
	private static Iterable<HeadersFilter> joinFilters(final Iterable<HeadersFilter> filters) {
		Iterable<HeadersFilter> requiredFilters = Arrays.<HeadersFilter>asList(HeaderToSubstitute.values());
		return Iterables.concat(requiredFilters, filters);
	}
	
	private enum HeaderToSubstitute implements HeadersFilter {
		Host {
			public String handleValue(String headerValue, UrlRewriter urlRewriter) throws URISyntaxException, MalformedURLException {
				return urlRewriter.rewriteHost(headerValue);
			}
			
			public String getHeader() {
				return "host";
			}
		},
		Referer {
			public String handleValue(String headerValue, UrlRewriter urlRewriter) throws URISyntaxException {
				return null;
			}
			
			public String getHeader() {
				return "referer";
			}
		},
		ContentLenght {
			public String handleValue(String headerValue, UrlRewriter urlRewriter) throws URISyntaxException {
				return null;
			}
			
			public String getHeader() {
				return "content-length";
			}
		};
	}
}