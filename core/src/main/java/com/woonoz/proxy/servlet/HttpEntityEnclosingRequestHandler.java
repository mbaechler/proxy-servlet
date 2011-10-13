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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

public abstract class HttpEntityEnclosingRequestHandler extends HttpRequestHandler {

	public HttpEntityEnclosingRequestHandler(HttpServletRequest request, HttpServletResponse response, URL targetServer, HttpClient client) {
		super(request, response, targetServer, client);
	}
	
	protected abstract HttpEntityEnclosingRequestBase createHttpRequestBase(URI targetUri);

	@Override
	protected HttpEntityEnclosingRequestBase createHttpCommand(URI targetUri, ClientHeadersHandler clientHeadersHandler) throws InvalidCookieException, URISyntaxException, FileUploadException, IOException {
		HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = createHttpRequestBase(targetUri);
		copyHeaders(getRequest(), httpEntityEnclosingRequestBase, clientHeadersHandler);
		copyData(getRequest(), httpEntityEnclosingRequestBase);
		return httpEntityEnclosingRequestBase;
	}


	protected ClientHeadersHandler createClientHeadersHandler(final UrlRewriter urlRewriter) {
		return new HttpEntityEnclosingHeadersHandler(urlRewriter);
	}
	
	private void copyData(HttpServletRequest request, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) throws FileUploadException, IOException {
		HttpEntity entity = createHttpEntity(request);
		httpEntityEnclosingRequestBase.setEntity(entity);
	}

	private HttpEntity createHttpEntity(HttpServletRequest request) throws FileUploadException, IOException {
		if (ServletFileUpload.isMultipartContent(request)) {
			return createMultipartEntity(request);
		} else {
			return new BufferedHttpEntity(new InputStreamEntity(request.getInputStream(), request.getContentLength()));
		}
	}

	private HttpEntity createMultipartEntity(HttpServletRequest request) throws FileUploadException, IOException {
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		MultipartEntity multipartEntity = new MultipartEntity();
		FileItemIterator iterator = servletFileUpload.getItemIterator(request);
		while (iterator.hasNext()) {
			FileItemStream fileItem = iterator.next();
			final String partName = fileItem.getFieldName();
			if (fileItem.isFormField()) {
				multipartEntity.addPart(partName, buildStringBody(fileItem));
			} else {
				multipartEntity.addPart(partName, buildContentBodyFromFileItem(fileItem));
			}
		}
		return multipartEntity;
	}

	private StringBody buildStringBody(FileItemStream fileItem)	throws UnsupportedEncodingException, IOException {
		return new StringBody(IOUtils.toString(fileItem.openStream()));
	}
	
	private ContentBody buildContentBodyFromFileItem(FileItemStream fileItem) throws IOException {
		final String partName = fileItem.getFieldName();
		final String contentType = getContentTypeForFileItem(fileItem);
		return new InputStreamBody(BufferOnCreateInputStream.create(fileItem.openStream()), contentType, partName);
	}

	private String getContentTypeForFileItem(FileItemStream fileItem) {
		final String contentType = fileItem.getContentType();
		if (contentType != null) {
			return contentType;
		} else {
			if (fileItem.isFormField()) {
				return "text/plain";
			} else {
				return "application/octet-stream";
			}
		}
	}
}
