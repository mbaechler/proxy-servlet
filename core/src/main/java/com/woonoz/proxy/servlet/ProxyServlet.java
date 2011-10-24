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


import com.woonoz.proxy.servlet.config.ProxyServletConfig;

public class ProxyServlet extends AbstractProxyServlet {

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
}
