/*
 * Copyright 2010 Woonoz S.A.S.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.woonoz.proxy.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;

public class ProxyServlet extends HttpServlet {

	private static final int HTTP_DEFAULT_PORT = 80;
	private URL targetServer;
	private DefaultHttpClient client;

	public ProxyServlet() {
		super();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		try {
            ProxyServletConfig config = new ProxyServletConfig( servletConfig );
            init( config );
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	public void init( URL targetServer, int maxCnx ) {
        init( new ProxyServletConfig( targetServer, maxCnx ) );
    }

	public void init( ProxyServletConfig config ) {
		targetServer = config.getTargetUrl();
        if (targetServer != null) {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme(targetServer.getProtocol(), getPortOrDefault(targetServer.getPort()), PlainSocketFactory.getSocketFactory()));
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout( httpParams, config.getConnectionTimeout() );
            HttpConnectionParams.setSoTimeout( httpParams, config.getSocketTimeout() );
            PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
            cm.setDefaultMaxPerRoute( config.getMaxConnections() );
            cm.setMaxTotal( config.getMaxConnections() );
            client = new DefaultHttpClient(cm, httpParams);
            client.removeResponseInterceptorByClass(ResponseProcessCookies.class);
            client.removeRequestInterceptorByClass(RequestAddCookies.class);
            
            final String remoteUserHeader = config.getRemoteUserHeader(); 
            if (null != remoteUserHeader) {
                client.addRequestInterceptor(new HttpRequestInterceptor() {
                    
                    @Override
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        request.removeHeaders(remoteUserHeader);
                        HttpRequestHandler handler;
                        if (context != null && (handler = (HttpRequestHandler) context.getAttribute(HttpRequestHandler.class.getName())) != null) {
                            String remoteUser = handler.getRequest().getRemoteUser();
                            if (remoteUser != null) {
                                request.addHeader(remoteUserHeader, remoteUser);
                            }
                        }
                    }
                });
            }
        }
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpGetRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		new HttpDeleteRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		new HttpHeadRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		new HttpOptionsRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		new HttpTraceRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		new HttpPostRequestHandler(request, response, targetServer, client).execute();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new HttpPutRequestHandler(request, response, targetServer, client).execute();
	}

	private int getPortOrDefault(int port) {
		if (port == -1) {
			return HTTP_DEFAULT_PORT;
		} else {
			return port;
		}
	}
}
