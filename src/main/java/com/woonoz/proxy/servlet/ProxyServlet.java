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

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class ProxyServlet extends HttpServlet {

	private static final int HTTP_DEFAULT_PORT = 80;
	private URL targetServer;
	private DefaultHttpClient client;
	
	public ProxyServlet() {
		super();
	}
	
	public void init(final URL targetServer, int maxCnx) {
		this.targetServer = targetServer;
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(targetServer.getProtocol(), PlainSocketFactory.getSocketFactory(), getPortOrDefault(targetServer.getPort())));
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpParams connManagerParams = new BasicHttpParams();
		connManagerParams.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(maxCnx));
		connManagerParams.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, maxCnx);
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(connManagerParams, schemeRegistry);
		client = new DefaultHttpClient(cm, httpParams);
		client.removeResponseInterceptorByClass(ResponseProcessCookies.class);
		client.removeRequestInterceptorByClass(RequestAddCookies.class);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpGetRequestHandler(request, response, targetServer, client).execute();
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpDeleteRequestHandler(request, response, targetServer, client).execute();
	}
	
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpHeadRequestHandler(request, response, targetServer, client).execute();
	}
	
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpOptionsRequestHandler(request, response, targetServer, client).execute();
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		new HttpPostRequestHandler(request, response, targetServer, client).execute();
	}

	private int getPortOrDefault(int port) {
		if (port == -1) {
			return HTTP_DEFAULT_PORT;
		} else {
			return port;
		}
	}

}
