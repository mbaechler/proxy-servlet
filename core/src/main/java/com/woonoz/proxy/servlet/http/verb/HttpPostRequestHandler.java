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
package com.woonoz.proxy.servlet.http.verb;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;

import com.woonoz.proxy.servlet.base.HttpEntityEnclosingRequestHandler;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;

public class HttpPostRequestHandler extends HttpEntityEnclosingRequestHandler {

	public HttpPostRequestHandler(HttpRequestHandler httpRequestHandler, HttpServletRequest request, HttpServletResponse response, HttpClient client) {
		super(httpRequestHandler, request, response, client);
	}

	@Override
	protected HttpEntityEnclosingRequestBase createHttpRequestBase(URI targetUri) {
		return new HttpPost(targetUri);
	}	
}
