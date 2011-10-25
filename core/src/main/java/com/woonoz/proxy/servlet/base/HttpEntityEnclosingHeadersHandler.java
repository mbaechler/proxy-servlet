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

import java.net.URISyntaxException;

import com.woonoz.proxy.servlet.http.header.HeadersFilter;
import com.woonoz.proxy.servlet.url.UrlRewriter;


public class HttpEntityEnclosingHeadersHandler extends ClientHeadersHandler {

	public HttpEntityEnclosingHeadersHandler(UrlRewriter urlRewriter) {
		super(urlRewriter, HeaderToSubstitute.values());
	}
	
	public HttpEntityEnclosingHeadersHandler(UrlRewriter urlRewriter, Iterable<HeadersFilter> filters) {
		super(urlRewriter, filters);
	}



	private enum HeaderToSubstitute implements HeadersFilter {
		ContentType {
			public String handleValue(String headerValue, UrlRewriter urlRewriter) throws URISyntaxException {
				if (headerValue.startsWith("multipart")) {
					return null;
				} else {
					return headerValue;
				}
				
			}
			
			public String getHeader() {
				return "content-type";
			}
		};
	}
}