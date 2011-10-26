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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woonoz.proxy.servlet.http.HttpRequestHandler;
import com.woonoz.proxy.servlet.http.exception.InvalidCookieException;
import com.woonoz.proxy.servlet.http.header.HeadersHandler;
import com.woonoz.proxy.servlet.url.UrlRewriter;

public abstract class AbstractHttpRequestCommand {

	private static final Logger logger = LoggerFactory.getLogger("com.woonoz.proxy.servlet");
	protected final HttpRequestHandler httpRequestHandler;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final HttpClient client;
	
	public AbstractHttpRequestCommand(HttpRequestHandler httpRequestHandler, HttpServletRequest request, HttpServletResponse response, HttpClient client) {
		this.httpRequestHandler = httpRequestHandler;
		this.request = request;
		this.response = response;
		this.client = client;
	}
	
	protected abstract HttpRequestBase createHttpRequestBase(URI targetUri);
	
	protected HttpRequestBase createHttpCommand(final URI targetUri,
			ClientHeadersHandler clientHeadersHandler)
			throws URISyntaxException, InvalidCookieException,
			MalformedURLException, FileUploadException, IOException {
		HttpRequestBase httpRequestBase = createHttpRequestBase(targetUri);
		copyRequestHeaders(getRequest(), httpRequestBase, clientHeadersHandler);
		return httpRequestBase;
	}
	
	protected ClientHeadersHandler getClientHeadersHandler() {
		return httpRequestHandler.getClientHeadersHandler();
	}
	
	public void execute() {
		ClientHeadersHandler clientHeadersHandler = getClientHeadersHandler();
		ServerHeadersHandler serverHeadersHandler = httpRequestHandler.getServerHeadersHandler();
		UrlRewriter urlRewriter = httpRequestHandler.getUrlRewriter();
		HttpRequestBase httpCommand = null;
		try {
			logger.debug("Doing rewrite for uri: {}", request.getRequestURL());
			final URI targetUri = urlRewriter.rewriteUri(new URI(request.getRequestURL().toString()));
			logger.debug("Making request for rewritten uri: {}", targetUri);
			httpCommand = createHttpCommand(targetUri, clientHeadersHandler);
			logger.debug("Http client command: {}, headers: {}", httpCommand.getRequestLine(), Arrays.asList(httpCommand.getAllHeaders()));
			performHttpRequest(httpCommand, response, serverHeadersHandler);
		} catch (URISyntaxException e) {
			handleException(httpCommand, e);
		} catch (IOException e) {
			handleException(httpCommand, e);
		} catch (InvalidCookieException e) {
			handleException(httpCommand, e);
		} catch (FileUploadException e) {
			handleException(httpCommand, e);
		} catch (RuntimeException e ) {
			handleException(httpCommand, e);
		} finally {
			
			try {
				response.getOutputStream().flush();
			} catch (IOException e) {
				logger.error("Exception flushing OutputStream ", e);
			}
		}
	}

	private void handleException(HttpRequestBase httpCommand, Exception e) {
		logger.error("Exception handling httpCommand: {}", (httpCommand != null ? httpCommand.getURI() : "(missing)"), e);
		if (httpCommand != null) {
			httpCommand.abort();
		}
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	protected HttpServletResponse getResponse() {
		return response;
	}
	
	protected void copyResponseHeaders(final HttpResponse from, final HttpServletResponse to, ServerHeadersHandler serverHeadersHandler) throws URISyntaxException, MalformedURLException{
		for (final Header header: from.getAllHeaders()) {
			final String modifiedValue = filterHeaderValue(serverHeadersHandler, header.getName(), header.getValue());
			copyHeaderIntoForwardedResponse(to, header, modifiedValue);
		}
	}

	private void copyHeaderIntoForwardedResponse(final HttpServletResponse to,
			final Header header, final String modifiedValue) {
		if (modifiedValue != null) {
			to.addHeader(header.getName(), modifiedValue);
		}
	}
	
	protected void copyRequestHeaders(final HttpServletRequest from, final HttpRequestBase to, ClientHeadersHandler clientHeadersHandler) throws URISyntaxException, MalformedURLException {
		Enumeration<?> enumerationOfHeaderNames = from.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            final String headerName = (String)enumerationOfHeaderNames.nextElement();
            
            Enumeration<?> enumerationOfHeaderValues = from.getHeaders(headerName);
            while (enumerationOfHeaderValues.hasMoreElements()) {
            	final String headerValue = (String) enumerationOfHeaderValues.nextElement();
                final String filteredValue = filterHeaderValue(clientHeadersHandler, headerName, headerValue);
                copyHeaderInForwardedRequest(to, headerName, filteredValue);
            }
        }
	}

	private String filterHeaderValue(HeadersHandler filter, final String headerName,
			final String headerValue) throws URISyntaxException, MalformedURLException {
		
		final String filteredValue = filter.handleHeader(headerName, headerValue);
		return filteredValue;
	}

	private void copyHeaderInForwardedRequest(final HttpRequestBase to,
			final String headerName, final String modifiedValue) {
		if (modifiedValue != null) {
			to.addHeader(headerName, modifiedValue);
		}
	}

	private void performHttpRequest(HttpRequestBase requestToServer, HttpServletResponse responseToClient, ServerHeadersHandler serverHeadersHandler) throws IOException, URISyntaxException {
		HttpContext context = new BasicHttpContext();
		context.setAttribute(AbstractHttpRequestCommand.class.getName(), this);
		HttpResponse responseFromServer = client.execute(requestToServer, context);
		logger.debug("Performed request: {} --> {}", requestToServer.getRequestLine(), responseFromServer.getStatusLine());				
		responseToClient.setStatus(responseFromServer.getStatusLine().getStatusCode());
		copyResponseHeaders(responseFromServer, responseToClient, serverHeadersHandler);
		HttpEntity entity = responseFromServer.getEntity();
		if (entity != null) {
			try {
				entity.writeTo(responseToClient.getOutputStream());
			} finally {
				EntityUtils.consume(entity);
			}
		}
	}
}
